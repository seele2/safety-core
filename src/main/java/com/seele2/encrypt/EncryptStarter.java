package com.seele2.encrypt;

import com.seele2.encrypt.annotation.Safety;
import com.seele2.encrypt.core.SafetyCipher;
import com.seele2.encrypt.manager.CipherManager;
import com.seele2.encrypt.manager.SafetyManager;
import com.seele2.encrypt.tool.FieldTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Set;

@Slf4j
@Configuration
public class EncryptStarter implements ApplicationListener<ApplicationReadyEvent> {

    private final ApplicationContext container;

    private static final String RESOURCE_PATTERN = "/**/*.class";

    public EncryptStarter(ApplicationContext container) {
        this.container = container;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationEvent) {
        ConfigurableApplicationContext applicationContext = applicationEvent.getApplicationContext();
        if (container.containsBean("encryptInterceptor")) {
            log.info("已启用字段加密功能, 正在进行初始化。");
            applicationContext.getBeansWithAnnotation(SpringBootApplication.class).forEach((__, v) -> scanSafetyModel(v.getClass()));
            applicationContext.getBeansOfType(SafetyCipher.class).forEach((__, v) -> CipherManager.registerCipher(v));
        }
        else {
            log.info("如需启用字段加密功能请开启 @EnableEncrypt ");
        }
    }


    /**
     * 扫描主类所在的整个包，将带有 Safety 注解的字段进行缓存
     *
     * @param mainClass 主类
     */
    private void scanSafetyModel(Class<?> mainClass) {
        String                  packageName             = ClassUtils.getPackageName(mainClass);
        String                  pattern                 = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(packageName) + RESOURCE_PATTERN;
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory   readerFactory           = new CachingMetadataReaderFactory(resourcePatternResolver);
        try {
            Resource[] resources = resourcePatternResolver.getResources(pattern);
            for (Resource resource : resources) {
                MetadataReader reader    = readerFactory.getMetadataReader(resource);
                String         classname = reader.getClassMetadata().getClassName();
                Class<?>       clazz     = Class.forName(classname);
                Set<Field>     fields    = FieldTool.getFields(clazz);
                SafetyManager.addField(fields.parallelStream().filter(field -> field.isAnnotationPresent(Safety.class)).toArray(Field[]::new));
            }
        } catch (IOException | ClassNotFoundException ignored) {
        }
    }


}
