package com.seele2.encrypt.conf;

import com.seele2.encrypt.enums.FlushTypeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jz.kr")
public class EncryptConf {

    /**
     * 启动时数据清洗处理，清洗方式 ENCRYPT / DECRYPT / EMPTY 分别代表 加密 / 解密 / 无需操作
     */
    private FlushTypeEnum flushType = FlushTypeEnum.EMPTY;

}
