# 字段加密组件

适用于 **mybatis框架**  **Mysql数据库** 的 *非入侵式* 字段加密组件。

## 特性

- [ ] release版本(1.0.0)
- [ ] 加解密效率优化（0.8.0）
- [ ] 全SQL支持（0.6.0）
- [ ] 复杂SQL支持（0.4.0）
- [ ] 别名支持，简单连表查询支持（0.2.0）
- [x] 模糊查询支持
- [x] 字段加解密

## 版本

> 暂无release版本，当前0.0.8版本开发中

## 注意

- 为了支持模糊搜索功能，会牺牲一定的数据空间，每个字符占用空间为4个字符长度
- 禁止使用 select * 查询必须指明所查询的字段

## 使用

- 添加依赖

```xml
<dependency>
    <groupId>com.seele2.pbdd</groupId>
    <artifactId>encrypt-spring-boot-starter</artifactId>
    <version>${encrypt.version}</version>
</dependency>
```

- 启用
  在任意SpringBean添加`@EnableEncrypt`注解即可启用生效，建议使用在启动类

- 添加配置

```yml
jz:
  kr:
    encrypt-tables:
      - name: ry_jbxx            #需要加解密的表
        fields:                  #需要加解密的字段
          - sfzh
          - sjhm
    flush-type: empty            #清洗器，encrypt, decrypt, empty（默认）
                                 # encrypt：启动时运行清洗器加密
                                 # decrypt：启动时运行清洗器解密
                                 # empty： 不执行
```

- 清洗器的实现

配置清洗器为启用时，需要自行实现清洗器 `com.jiuzhou.myjw.encrypt.EncryptFlusher`

```java
@Component
public class Flusher implements EncryptFlusher {
    
    @Override
	public void encrypt() {
        // TODO 
        //  启动时对已有数据加密，
        //  用于已运行产生数据的项目对旧数据进行加密
    }

    @Override
	public void decrypt() {
       // TODO 
       //  启动时对已有数据解密，
       //  用于加密弃用时对加密数据解密
    }
}
```

## 其他

对有需要使用自己的特殊加密的可以实现`EncryptCipher`和`EncryptJude`接口，分表实现加解密和数据是否需要加解密判定


## 变更日志

- 0.0.7 过渡版本
- 0.0.6 bug修复，基础beta版本
- 0.0.5 适用于市纪委前台项目（测试调整中）
- 0.0.4 部分bug修复与扩展功能实现
- 0.0.3 支持模糊查询
- 0.0.2 实现单一查询字段加密与非复杂实体返回解密
- 0.0.1 基础构建版本