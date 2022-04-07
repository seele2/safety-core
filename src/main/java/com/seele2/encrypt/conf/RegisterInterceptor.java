package com.seele2.encrypt.conf;

import com.seele2.encrypt.core.ColumnInterceptor;
import com.seele2.encrypt.core.DecryptInterceptor;
import com.seele2.encrypt.core.EncryptInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 通过后置添加的方式来确保拦截器的顺序
 */
@Component
public class RegisterInterceptor {

    @Autowired
    private List<SqlSessionFactory> sqlSessionFactoryList;

    @PostConstruct
    public void registerInterceptor() {
        for (SqlSessionFactory factory : sqlSessionFactoryList) {
            org.apache.ibatis.session.Configuration configuration = factory.getConfiguration();
            configuration.addInterceptor(new EncryptInterceptor());
            configuration.addInterceptor(new DecryptInterceptor());
            configuration.addInterceptor(new ColumnInterceptor());
        }
    }

}
