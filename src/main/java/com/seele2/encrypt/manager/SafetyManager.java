package com.seele2.encrypt.manager;

import com.seele2.encrypt.annotation.Safety;
import com.seele2.encrypt.core.SafetyCipher;
import com.seele2.encrypt.tool.FieldTool;
import com.seele2.encrypt.tool.SimpleCache;
import com.seele2.encrypt.factory.CipherFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
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
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@Slf4j
@Component
public class SafetyManager implements ApplicationListener<ContextRefreshedEvent> {

    private static final String RESOURCE_PATTERN = "/**/*.class";

    private static final SimpleCache<String, Field> FIELD_POOL = new SimpleCache<>();

    private static final SimpleCache<String, SafetyCipher> CIPHER_POOL = new SimpleCache<>();

    /**
     * 名称 是否有对应字段存在校验
     *
     * @param key 字段名称
     * @return true / false
     */
    public static boolean isPresent(String key) {
        return FIELD_POOL.isPresent(key);
    }

    /**
     * 获取解密器
     *
     * @param key 字段名
     * @return 解密器
     */
    public static SafetyCipher getSafetyCipher(String key) {
        SafetyCipher safetyCipher = CIPHER_POOL.getValue(key);
        if (null == safetyCipher) {
            safetyCipher = CipherFactory.getSafetyCipher(FIELD_POOL.getValue(key).getAnnotation(Safety.class).cipher());
            CIPHER_POOL.put(key, safetyCipher);
        }
        return safetyCipher;
    }

    public static void addField(Field... fields) {
        Arrays.stream(fields).parallel().filter(field -> field.isAnnotationPresent(Safety.class)).forEach(field -> {
            Safety   annotation = field.getAnnotation(Safety.class);
            String[] alias      = annotation.alias();
            String[] basename   = FieldTool.getBasename(field);
            Stream.concat(Arrays.stream(alias), Stream.of(basename)).filter(StringUtils::isNotBlank).forEach(name -> FIELD_POOL.put(name, field));
        });
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        Map<String, Object> main = contextRefreshedEvent.getApplicationContext().getBeansWithAnnotation(SpringBootApplication.class);
        main.forEach((__, v) -> scanSafetyModel(v.getClass()));
    }

    /**
     * 扫描主类所在的整个包，将带有 Safety 注解的字段进行缓存
     *
     * @param mainClass 主类
     */
    private void scanSafetyModel(Class<?> mainClass) {
        String packageName = ClassUtils.getPackageName(mainClass);
        String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                + ClassUtils.convertClassNameToResourcePath(packageName) + RESOURCE_PATTERN;
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory   readerFactory           = new CachingMetadataReaderFactory(resourcePatternResolver);
        try {
            Resource[] resources = resourcePatternResolver.getResources(pattern);
            for (Resource resource : resources) {
                MetadataReader reader    = readerFactory.getMetadataReader(resource);
                String         classname = reader.getClassMetadata().getClassName();
                Class<?>       clazz     = Class.forName(classname);
                Set<Field>     fields    = FieldTool.getFields(clazz);
                addField(fields.parallelStream().filter(field -> field.isAnnotationPresent(Safety.class)).toArray(Field[]::new));
            }
        } catch (IOException | ClassNotFoundException ignored) {
        }
    }


}
