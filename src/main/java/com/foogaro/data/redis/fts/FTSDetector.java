package com.foogaro.data.redis.fts;

import com.foogaro.data.redis.fts.annotations.Numeric;
import com.foogaro.data.redis.fts.annotations.Searchable;
import com.foogaro.data.redis.fts.annotations.Tag;
import com.foogaro.data.redis.fts.annotations.Text;
import com.foogaro.data.redis.fts.builders.FTSBuilder;
import com.foogaro.data.redis.fts.builders.FTSFieldBuilder;
import com.foogaro.data.redis.fts.builders.FTSIndexBuilder;
import com.foogaro.data.redis.fts.clients.melp.Redis;
import com.foogaro.data.redis.fts.model.FTSField;
import com.foogaro.data.redis.fts.model.FTSIndex;
import com.foogaro.data.redis.fts.model.FTSTypes;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.foogaro.data.redis.fts.model.FTSCommand.*;
import static com.foogaro.data.redis.fts.model.FTSConst.*;

public class FTSDetector {

    private final Class<Searchable> ftsClass = Searchable.class;

    private FTSBuilder builder;

    public FTSDetector() {
        this("*");
    }

    public FTSDetector(String packageName) {
        this.detect(packageName);
    }

    private List<String> commands() {
        List<FTSIndex> indices = builder.build();
        List<String> commands = new ArrayList<>();
        for (FTSIndex index : indices) {
            StringBuilder sb = new StringBuilder();
            sb.append(FULL_TEXT_SEARCH).append(DOT).append(CREATE)
                    .append(SPACE).append(index.getName())
                    .append(SPACE).append(ON)
                    .append(SPACE).append(HASH)
                    .append(SPACE).append(PREFIX)
                    .append(SPACE).append(1)
                    .append(SPACE).append(index.getPrefix().toLowerCase()).append(COLON)
                    .append(SPACE).append(SCHEMA);
            List<FTSField> fields = index.getFtsFields();
            for (FTSField field : fields) {
                sb.append(SPACE).append(field.getName())
                        .append(SPACE).append(field.getType())
                        .append(field.isSortable() ? SPACE + SORTABLE : BLANK)
                        .append(field.isCaseSensitive() ? SPACE + CASESENSITIVE : BLANK);
            }
            commands.add(sb.toString());
        }
        return commands;
    }

    private List<List<String>> commandsRESP() {
        List<List<String>> commands = new ArrayList<>();
        List<FTSIndex> indices = builder.build();
        for (FTSIndex index : indices) {
            List<String> command = new ArrayList<>();
            command.add(FULL_TEXT_SEARCH+DOT+CREATE);
            command.add(index.getName());
            command.add(ON.toString());
            command.add(HASH.toString());
            command.add(PREFIX.toString());
            command.add("1");
            command.add(index.getPrefix().toLowerCase()+COLON);
            command.add(SCHEMA.toString());
            List<FTSField> fields = index.getFtsFields();
            for (FTSField field : fields) {
                command.add(field.getName());
                command.add(field.getType().toString());
                if (field.isSortable()) command.add(SORTABLE.toString());
                if (field.isCaseSensitive()) command.add(CASESENSITIVE.toString());
            }
            commands.add(command);
        }
        return commands;
    }

    public void execute() {
        try {
//            Redis.run(redis -> redis.call("SET", "foo", "loo"), "redis", 6379);
            for (List<String> command : commandsRESP()) {
                Redis.run(redis -> redis.call(command.stream().toArray(String[]::new)), "redis", 6379);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void detect(String packageName) {
        builder = FTSBuilder.builder();
        this.detectEntities(packageName);
    }

    private void detectEntities(String packageName) {
        ClassPathScanningCandidateComponentProvider provider = createComponentScanner();
        for (BeanDefinition beanDef : provider.findCandidateComponents(packageName)) {
            detectEntityDetails(beanDef.getBeanClassName());
        }
    }

    private void detectEntityDetails(String className) {
        try {
            Class<?> cl = Class.forName(className);
            Searchable ats = cl.getAnnotation(ftsClass);
            String indexName = "".equals(ats.index()) ? "idx" + cl.getSimpleName().toLowerCase() : ats.index();
            String prefixName = "".equals(ats.keyPrefix()) ? cl.getSimpleName().toLowerCase() : ats.keyPrefix();

            FTSIndexBuilder indexBuilder = FTSIndexBuilder.builder()
                    .setName(indexName)
                    .setPrefix(prefixName)
                    .setDropIndex(ats.dropIndex());

            Field[] fields = cl.getDeclaredFields();
            for (Field field : fields) {
                FTSFieldBuilder fieldBuilder = FTSFieldBuilder.builder();
                fieldBuilder.setName(field.getName());
                Annotation[] annotations = field.getDeclaredAnnotations();
                if (annotations != null && annotations.length > 0) {
                    boolean foundFTSAnnotations = false;
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof Text) {
                            foundFTSAnnotations = true;
                            Text at = (Text) annotation;
                            fieldBuilder.setType(FTSTypes.TEXT)
                                    .setSortable(at.sortable())
                                    .setCaseSensitive(at.caseSensitive());
                        } else if (annotation instanceof Tag) {
                            foundFTSAnnotations = true;
                            Tag at = (Tag) annotation;
                            fieldBuilder.setType(FTSTypes.TAG)
                                    .setSortable(at.sortable())
                                    .setCaseSensitive(at.caseSensitive())
                                    .setSeparator(at.separator());
                        } else if (annotation instanceof Numeric) {
                            foundFTSAnnotations = true;
                            Numeric at = (Numeric) annotation;
                            fieldBuilder.setType(FTSTypes.NUMERIC)
                                    .setSortable(at.sortable());
                        }
                    }
                    if (foundFTSAnnotations) indexBuilder.addFTSField(fieldBuilder.build());
                }
            }
            builder.addFTSIndex(indexBuilder.build());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private ClassPathScanningCandidateComponentProvider createComponentScanner() {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(ftsClass));
        return provider;
    }
}

