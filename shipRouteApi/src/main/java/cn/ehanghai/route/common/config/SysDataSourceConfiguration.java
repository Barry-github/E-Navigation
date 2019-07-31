package cn.ehanghai.route.common.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import tk.mybatis.spring.annotation.MapperScan;

import javax.sql.DataSource;

/**
 * @author 胡恒博
 * @date 2018/05/21 10:05
 * @description 数据源配置
 **/
@Configuration
@Primary
@MapperScan(basePackages = "cn.ehanghai.route.sys.mapper", sqlSessionTemplateRef = "sysSqlSessionTemplate")
public class SysDataSourceConfiguration {

    @Bean(name = "sysDataSource")
    @ConfigurationProperties(prefix = "spring.sys-datasource")
    @Primary
    public DataSource sysDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "sysSqlSessionFactory")
    @Primary
    public SqlSessionFactory sysSqlSessionFactory(@Qualifier("sysDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/mapper/sys/*.xml"));
        return bean.getObject();
    }

    @Bean(name = "sysTransactionManager")
    @Primary
    public DataSourceTransactionManager sysTransactionManager(@Qualifier("sysDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "sysSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate sysSqlSessionTemplate(@Qualifier("sysSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
