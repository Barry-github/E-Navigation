 package cn.ehanghai.routecalc.common.config;

 import javax.sql.DataSource;
 import org.apache.ibatis.session.SqlSessionFactory;
 import org.mybatis.spring.SqlSessionFactoryBean;
 import org.mybatis.spring.SqlSessionTemplate;
 import org.springframework.beans.factory.annotation.Qualifier;
 import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
 import org.springframework.boot.context.properties.ConfigurationProperties;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;
 import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
 import org.springframework.jdbc.datasource.DataSourceTransactionManager;
 import tk.mybatis.spring.annotation.MapperScan;

 @Configuration
 @MapperScan(basePackages = "cn.ehanghai.routecalc.nav.mapper", sqlSessionTemplateRef = "navSqlSessionTemplate")
 public class NavDataSourceConfiguration {

     @Bean(name = "navDataSource")
     @ConfigurationProperties(prefix = "spring.nav-datasource")
     public DataSource navDataSource() {
         return DataSourceBuilder.create().build();
     }

     @Bean(name = "navSqlSessionFactory")
     public SqlSessionFactory navSqlSessionFactory(@Qualifier("navDataSource") DataSource dataSource) throws Exception {
         SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
         bean.setDataSource(dataSource);
         bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/mapper/nav/*.xml"));
         return bean.getObject();
     }

     @Bean(name = "navTransactionManager")
     public DataSourceTransactionManager aisTransactionManager(@Qualifier("navDataSource") DataSource dataSource) {
         return new DataSourceTransactionManager(dataSource);
     }

     @Bean(name = "navSqlSessionTemplate")
     public SqlSessionTemplate navSqlSessionTemplate(@Qualifier("navSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
         return new SqlSessionTemplate(sqlSessionFactory);
     }
 }

