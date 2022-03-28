package com.foogaro.data.redisframework;

import com.foogaro.data.redis.resp.Redis;
import com.foogaro.data.redis.resp.exceptions.RedisError;
import com.foogaro.data.redis.resp.exceptions.RedisException;
import com.foogaro.data.redisframework.annotations.hash.RedisHash;
import com.foogaro.data.redisframework.annotations.json.RedisJSON;
import com.foogaro.data.redisframework.annotations.search.Numeric;
import com.foogaro.data.redisframework.annotations.search.Tag;
import com.foogaro.data.redisframework.annotations.search.Text;
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
import java.util.List;

import static com.foogaro.data.redisframework.model.FTSCommand.AS;
import static com.foogaro.data.redisframework.model.FTSCommand.DROPINDEX;
import static com.foogaro.data.redisframework.model.FTSConst.*;

public class FTSDetector {

    private final Logger logger = LoggerFactory.getLogger(FTSDetector.class);

    private List<FTSIndex> indexes = new ArrayList<>();
    private String status;

    public FTSDetector() {
        this("*");
    }

    public FTSDetector(String packageName) {
        this.detect(packageName);
        try {
            this.execute();
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
                    if (index.getOn().equalsIgnoreCase(JSON)) {
                        command.add(DOLLAR + DOT + field.getName());
                    }
                    command.add(AS.toString());
                    if (field.getAsName() != null && !field.getAsName().equals("")) {
                        command.add(field.getAsName());
                    } else {
                        command.add(field.getName());
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
        try {
            for (List<Object> command : commandsRESP()) {
                Redis.run(redis -> {
                    redis.call(command.stream().toArray(Object[]::new));
                }, "localhost", 6379);
            }
        } catch (IOException | RedisError e) {
            e.printStackTrace();
            throw new RedisException(e);
        }
    }

    private void detect(String packageName) {
        this.detectEntities(packageName);
    }

    private void detectEntities(String packageName) {
        try (ScanResult scanResult = new ClassGraph()
                .acceptPackages(packageName)
                .enableAllInfo()
                .scan()) {
            ClassInfoList json = scanResult.getClassesWithAnnotation(RedisJSON.class);
            ClassInfoList hash = scanResult.getClassesWithAnnotation(RedisHash.class);
            json.stream().forEach(classInfo -> detectEntityDetails(classInfo.getName(), RedisJSON.class));
            hash.stream().forEach(classInfo -> detectEntityDetails(classInfo.getName(), RedisHash.class));
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

    private String dataType(Annotation annotation) {
        if (annotation != null) {
            if (annotation instanceof RedisJSON) {
                return JSON;
            } else if (annotation instanceof RedisHash) {
                return HASH;
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

    private void detectEntityDetails(String className, Class dataModel) {
        try {
            Class<?> cl = Class.forName(className);
            Annotation an = cl.getDeclaredAnnotation(dataModel);
            String dataType = dataType(an);
            String indexName = indexName(an);
            String[] prefixKeys = prefixKeys(an);
            FTSIndexStrategy indexStrategy = indexStrategy(an);

            FTSIndex.Builder indexBuilder = new FTSIndex.Builder()
                    .name(indexName)
                    .on(dataType)
                    .prefixCount((prefixKeys.length > 0) ? prefixKeys.length : 1)
                    .prefixKeys((prefixKeys.length > 0) ? String.join(" ", prefixKeys) : "*")
                    .indexStrategy(indexStrategy);

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
                                    .sortable(at.sortable());
                                    //.caseSensitive(at.caseSensitive());
                        } else if (annotation instanceof Tag) {
                            foundFTSAnnotations = true;
                            Tag at = (Tag) annotation;
                            fieldBuilder.type(FTSTypes.TAG)
                                    .sortable(at.sortable())
                                    .caseSensitive(at.caseSensitive())
                                    .separator(at.separator());
                        } else if (annotation instanceof Numeric) {
                            foundFTSAnnotations = true;
                            Numeric at = (Numeric) annotation;
                            fieldBuilder.type(FTSTypes.NUMERIC)
                                    .sortable(at.sortable());
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

