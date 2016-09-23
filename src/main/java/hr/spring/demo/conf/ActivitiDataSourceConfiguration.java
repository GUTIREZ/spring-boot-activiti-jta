package hr.spring.demo.conf;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import bitronix.tm.resource.jdbc.PoolingDataSource;

@Configuration
@DependsOn("transactionManager")
public class ActivitiDataSourceConfiguration {
	
	@Bean
    @ConfigurationProperties(prefix = "activiti.datasource")
    public DataSource activitiDataSource() {
		return new PoolingDataSource();
    }
}
