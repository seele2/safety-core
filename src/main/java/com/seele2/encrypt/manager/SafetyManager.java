package com.seele2.encrypt.manager;

import com.seele2.encrypt.annotation.Safety;
import com.seele2.encrypt.base.SafetyCipher;
import com.seele2.encrypt.base.SimpleCache;
import com.seele2.encrypt.factory.CipherFactory;
import com.seele2.encrypt.tool.CamelSnakeHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Stream;

@Slf4j
@Component
public class SafetyManager implements CommandLineRunner {

    private static final SimpleCache<String, Field> pool = new SimpleCache<>();

    public static boolean isPresent(String key) {
        return pool.isPresent(key);
    }

    public static SafetyCipher getDecryptCipher(String key) {
        Field  field      = pool.getValue(key);
        Safety annotation = field.getAnnotation(Safety.class);
        return CipherFactory.getEncryptCipher(annotation.cipher());
    }

    public static SafetyCipher getDecryptCipher(Field value) {
        Safety annotation = value.getAnnotation(Safety.class);
        return CipherFactory.getEncryptCipher(annotation.cipher());
    }

    public static void add(Field... fields) {
        Arrays.stream(fields).parallel().filter(field -> field.isAnnotationPresent(Safety.class)).forEach(field -> {
            Safety   annotation = field.getAnnotation(Safety.class);
            String[] alias      = annotation.alias();
            String   basename   = getBasename(field);
            Stream.concat(Arrays.stream(alias), Stream.of(basename))
                    .filter(StringUtils::isNotBlank)
                    .forEach(name -> pool.put(name, field));
        });
    }

    private static final String BASE_PACKAGE     = "com.example.encryptdemo.model";
    private static final String RESOURCE_PATTERN = "/**/*.class";

    @Override
    public void run(String... args) throws Exception {
        //spring工具类，可以获取指定路径下的全部类
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        try {
            String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    ClassUtils.convertClassNameToResourcePath(BASE_PACKAGE) + RESOURCE_PATTERN;
            Resource[] resources = resourcePatternResolver.getResources(pattern);
            //MetadataReader 的工厂类
            MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
            for (Resource resource : resources) {
                //用于读取类信息
                MetadataReader reader = readerFactory.getMetadataReader(resource);
                //扫描到的class
                String   classname = reader.getClassMetadata().getClassName();
                Class<?> clazz     = Class.forName(classname);
                Field[]  fields    = clazz.getDeclaredFields();
                add(Arrays.stream(fields).parallel().filter(field -> field.isAnnotationPresent(Safety.class)).toArray(Field[]::new));
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    private static String getBasename(Field field) {
        String name = field.getName();
        return CamelSnakeHelper.toSnakeCase(name);
    }
}
