package com.app.onestepback.config;

import com.app.onestepback.customHandler.PostSortTypeHandler;
import com.app.onestepback.domain.type.post.PostSortType;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.IOException;


@Configuration
@RequiredArgsConstructor
public class MyBatisConfiguration {
    //    resources까지의 경로를 매핑하기 위해서 ApplicationContext객체 주입
    private final ApplicationContext applicationContext;

    //    yml파일의 connection정보를 가져오기 위한 작업
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    //    DataSource객체에 미리 설정해놓은 connection설정을 넣어준다.
    @Bean
    public DataSource dataSource() {
        return new HikariDataSource(hikariConfig());
    }

    //    SqlSession을 만들기 위한 SqlSessionFactory를 제작한다.
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws IOException {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath*:/mapper/*.xml"));
        sqlSessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:/config/config.xml"));
        try {
            SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBean.getObject();
            if (sqlSessionFactory != null) {
                org.apache.ibatis.session.Configuration configuration = sqlSessionFactory.getConfiguration();
                configuration.setMapUnderscoreToCamelCase(true);
                configuration.getTypeHandlerRegistry().register(PostSortType.class, new PostSortTypeHandler());
            }
            return sqlSessionFactory;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

