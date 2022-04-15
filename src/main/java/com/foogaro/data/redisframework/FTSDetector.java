package com.foogaro.data.redisframework;

import com.foogaro.data.redis.resp.Redis;
import com.foogaro.data.redis.resp.exceptions.RedisError;
import com.foogaro.data.redis.resp.exceptions.RedisException;
import com.foogaro.data.redisframework.annotations.Key;
import com.foogaro.data.redisframework.annotations.hash.RedisHash;
import com.foogaro.data.redisframework.annotations.json.RedisJSON;
import com.foogaro.data.redisframework.annotations.search.Numeric;
import com.foogaro.data.redisframework.annotations.search.Tag;
import com.foogaro.data.redisframework.annotations.search.Text;
import com.foogaro.data.redisframework.handlers.DataStoreOperation;
import com.foogaro.data.redisframework.model.*;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.foogaro.data.redisframework.model.FTSCommand.AS;
import static com.foogaro.data.redisframework.model.FTSCommand.DROPINDEX;
import static com.foogaro.data.redisframework.model.FTSConst.*;

public class FTSDetector {

    private final Logger logger = LoggerFactory.getLogger(FTSDetector.class);

    private List<FTSIndex> indexes = new ArrayList<>();
    private String status;

    private ConcurrentHashMap<String, DataStoreInfo> dataStoreMap = DataStoreMap.getDataStoreMap();

    public FTSDetector() {
        this("*");
    }

    public FTSDetector(String packageName) {
        detectDataStoreOperations(packageName);
        buildIndexes();
        try {
            execute();
        } catch (RedisException e) {
            e.printStackTrace();
            this.status = e.getMessage();
        }
    }

    private List<List<Object>> commandsRESP() {
        List<List<Object>> commands = new ArrayList<>();
        for (FTSIndex index : indexes) {
            List<Object> command;
            FTSIndexStrategy indexStrategy = index.getIndexStrategy();

            if (indexStrategy != null && (indexStrategy == FTSIndexStrategy.NONE || indexStrategy == FTSIndexStrategy.UPDATE)) continue;

            if (indexStrategy != null && (indexStrategy == FTSIndexStrategy.DROP || indexStrategy == FTSIndexStrategy.DROP_CREATE)) {
                command = new ArrayList<>();
                command.add(FULL_TEXT_SEARCH + DOT + DROPINDEX);
                command.add(index.getName());
                commands.add(command);
            }
            if (indexStrategy != null && (indexStrategy == FTSIndexStrategy.CREATE || indexStrategy == FTSIndexStrategy.DROP_CREATE)) {
                command = new ArrayList<>();
                command.add(FULL_TEXT_SEARCH + DOT + FTSCommand.CREATE);
                command.add(index.getName());
                command.add(FTSCommand.ON.toString());
                command.add(index.getOn());
                //if (index.getOn().equalsIgnoreCase(HASH)) {
                    command.add(FTSCommand.PREFIX.toString());
                    command.add(index.getPrefixCount());
                    command.add(index.getPrefixKeys());
                //}
                command.add(FTSCommand.SCHEMA.toString());
                List<FTSField> fields = index.getFtsFields();
                for (FTSField field : fields) {
                    if (index.getOn().equalsIgnoreCase(DataType.JSON.toString())) {
                        command.add(DOLLAR + DOT + field.getName());
                    } else if (index.getOn().equalsIgnoreCase(DataType.HASH.toString())) {
                        command.add(field.getName());
                    }
                    if (field.getAsName() != null && !field.getAsName().equals("")) {
                        command.add(AS.toString());
                        command.add(field.getAsName());
                    }
                    command.add(field.getType().toString());
                    if (field.isSortable()) command.add(FTSCommand.SORTABLE.toString());
                    if (field.isCaseSensitive()) command.add(FTSCommand.CASESENSITIVE.toString());
                }
                commands.add(command);
            }
        }
        if (logger.isDebugEnabled()) commands.stream().forEach(objects -> objects.stream().forEach(o -> logger.debug("Command: {}", o)));
        return commands;
    }

    private void execute() throws RedisException {
        for (List<Object> command : commandsRESP()) {
            try {
                Redis.run(redis -> {
                    redis.call(command.stream().toArray(Object[]::new));
                }, "localhost", 6379);
            } catch (IOException | RedisError e) {
                if (logger.isTraceEnabled()) e.printStackTrace();
                if (logger.isDebugEnabled()) command.stream().forEach(o -> logger.debug("Command: {}", o));
                logger.error("Error: {}", e.getMessage());
                //throw new RedisException(e);
            }
        }
    }

    private void detectDataStoreOperations(String packageName) {
        try (ScanResult scanResult = new ClassGraph()
                .acceptPackages(packageName)
                .enableAllInfo()
                .scan()) {
            ClassInfoList classInfoList = scanResult.getClassesImplementing(DataStoreOperation.class);
            classInfoList.stream().forEach(classInfo -> mapDataStoreOperation(classInfo.getName()));
        }
    }

    private void mapDataStoreOperation(String dataStoreOperationClassName) {
        try {
            DataStoreInfo.Builder dataStoreInfoBuilder = new DataStoreInfo.Builder();
            dataStoreInfoBuilder.dataStore(dataStoreOperationClassName);
            String dataModel = dataModel(dataStoreOperationClassName);
            dataStoreInfoBuilder.dataModel(dataModel);
            Class<?> cl = Class.forName(dataModel);
            Annotation an = cl.getDeclaredAnnotations()[0];
            DataType dataType = dataType(an);
            dataStoreInfoBuilder.annotationName(dataType.toString());
            String indexName = indexName(an);
            dataStoreInfoBuilder.indexName(indexName);
            String[] prefixKeys = prefixKeys(an);
            dataStoreInfoBuilder.prefix(prefixKeys[0]);
            FTSIndexStrategy indexStrategy = indexStrategy(an);
            dataStoreInfoBuilder.indexStrategy(indexStrategy.toString());
            dataStoreMap.put(dataStoreOperationClassName, dataStoreInfoBuilder.build());
            dataStoreMap.put(dataModel, dataStoreInfoBuilder.build());
            if (logger.isDebugEnabled()) logger.debug("DataStoreInfo: {}", dataStoreInfoBuilder.build());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    private String dataModel(String dataStore) {
        try {
            Class<?> cl = Class.forName(dataStore);
//            if (logger.isDebugEnabled()) {
//                logger.debug("dataStore: {}", dataStore);
//                logger.debug("dataStore class: {}", cl);
//                logger.debug("dataStore class generic interface type: {}", cl.getGenericInterfaces()[0]);
//                logger.debug("dataStore class generic interface type class: {}", cl.getGenericInterfaces()[0].getClass());
//                logger.debug("dataStore class generic interface type class generic interface type: {}", cl.getGenericInterfaces()[0].getClass().getGenericInterfaces()[0]);
//                logger.debug("dataStore class generic interface type class generic interface type cast: {}", ((ParameterizedType)cl.getGenericInterfaces()[0].getClass().getGenericInterfaces()[0]));
//                logger.debug("dataStore class generic interface type class generic interface type cast actual type argument: {}", ((ParameterizedType)cl.getGenericInterfaces()[0].getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0]);
//            }
//            Type runtimeType = ((ParameterizedType)cl.getGenericInterfaces()[0].getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
//            String modelClassName = runtimeType.getTypeName();
            //FIXME: Mamma mia!!!
            String superInterfaceType = cl.getGenericInterfaces()[0].getTypeName();
            superInterfaceType = superInterfaceType.substring(superInterfaceType.indexOf('<'));
            superInterfaceType = superInterfaceType.substring(1, superInterfaceType.indexOf('>'));
            return superInterfaceType;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    private String indexName(Annotation annotation) {
        if (annotation != null) {
            if (annotation instanceof RedisJSON) {
                return  ((RedisJSON)annotation).index().name();
            } else if (annotation instanceof RedisHash) {
                return  ((RedisHash)annotation).index().name();
            }
        }
        throw new IllegalArgumentException("Invalid DataModel Annotation");
    }

    private DataType dataType(Annotation annotation) {
        if (annotation != null) {
            if (annotation instanceof RedisJSON) {
                return DataType.JSON;
            } else if (annotation instanceof RedisHash) {
                return DataType.HASH;
            }
        }
        throw new IllegalArgumentException("Invalid DataModel Annotation");
    }

    private String[] prefixKeys(Annotation annotation) {
        if (annotation != null) {
            String[] prefixes = null;
            if (annotation instanceof RedisJSON) {
                prefixes = ((RedisJSON)annotation).index().prefix();
            } else if (annotation instanceof RedisHash) {
                prefixes = ((RedisHash)annotation).index().prefix();
            }
            if (prefixes != null && prefixes.length > 0) return prefixes;
        }
        throw new IllegalArgumentException("Invalid DataModel Annotation");
    }

    private FTSIndexStrategy indexStrategy(Annotation annotation) {
        if (annotation != null) {
            if (annotation instanceof RedisJSON) {
                return ((RedisJSON)annotation).index().indexStrategy();
            } else if (annotation instanceof RedisHash) {
                return ((RedisHash)annotation).index().indexStrategy();
            }
        }
        throw new IllegalArgumentException("Invalid DataModel Annotation");
    }

    private void buildIndexes() {
        Enumeration<String> keys = dataStoreMap.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            if (logger.isDebugEnabled()) logger.debug("DataStoreMap[{}]", key);
            DataStoreInfo dataStoreInfo = dataStoreMap.get(key);
            if (logger.isDebugEnabled()) logger.debug("DataStoreInfo: {}", dataStoreInfo);
            buildIndex(dataStoreInfo);
        }
    }

    private void buildIndex(DataStoreInfo dataStoreInfo) {
        try {
            FTSIndex.Builder indexBuilder = new FTSIndex.Builder()
                    .name(dataStoreInfo.getIndexName())
                    .on(dataStoreInfo.getAnnotationName())
                    .prefixCount(1)
                    .prefixKeys(dataStoreInfo.getPrefix())
                    .indexStrategy(FTSIndexStrategy.valueOf(dataStoreInfo.getIndexStrategy()));

            Class<?> cl = Class.forName(dataStoreInfo.getDataModel());
            Field[] fields = cl.getDeclaredFields();
            for (Field field : fields) {
                FTSField.Builder fieldBuilder = new FTSField.Builder().name(field.getName());
                Annotation[] annotations = field.getDeclaredAnnotations();
                if (annotations != null && annotations.length > 0) {
                    boolean foundFTSAnnotations = false;
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof Text) {
                            foundFTSAnnotations = true;
                            Text at = (Text) annotation;
                            fieldBuilder.type(FTSTypes.TEXT)
                                    .asName(at.as())
                                    .sortable(at.sortable());
                            //.caseSensitive(at.caseSensitive());
                        } else if (annotation instanceof Tag) {
                            foundFTSAnnotations = true;
                            Tag at = (Tag) annotation;
                            fieldBuilder.type(FTSTypes.TAG)
                                    .asName(at.as())
                                    .sortable(at.sortable())
                                    .caseSensitive(at.caseSensitive())
                                    .separator(at.separator());
                        } else if (annotation instanceof Numeric) {
                            foundFTSAnnotations = true;
                            Numeric at = (Numeric) annotation;
                            fieldBuilder.type(FTSTypes.NUMERIC)
                                    .asName(at.as())
                                    .sortable(at.sortable());
                        } else if (annotation instanceof Key) {
                        } else {
                            if (logger.isWarnEnabled()) logger.warn("SKIPPING - Unknown field type: {}", annotation);
                        }
                    }
                    if (foundFTSAnnotations) indexBuilder.addFTSField(fieldBuilder.build());
                }
            }
            indexes.add(indexBuilder.build());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}

