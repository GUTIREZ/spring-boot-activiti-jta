package hr.spring.demo.conf;

import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import bitronix.tm.resource.jdbc.PoolingDataSource;

@Configuration
@DependsOn("transactionManager")
@EnableJpaRepositories(
		entityManagerFactoryRef = "activitiEntityManagerFactory",
		transactionManagerRef = "transactionManager",
		basePackages = "org.activiti")
public class ActivitiDataSourceConfiguration {
	
	@Autowired
	private JpaVendorAdapter jpaVendorAdapter;
	
	@Bean(initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties(prefix = "activiti.datasource")
    public DataSource activitiDataSource() {
		return new PoolingDataSource();
    }
	
	@Bean
	public LocalContainerEntityManagerFactoryBean activitiEntityManagerFactory() throws Throwable {

		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("javax.persistence.transactionType", "JTA");

		LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
		entityManager.setJtaDataSource(activitiDataSource());
		entityManager.setJpaVendorAdapter(jpaVendorAdapter);
		entityManager.setPackagesToScan("org.activiti");
		entityManager.setPersistenceUnitName("activitiPersistenceUnit");
		entityManager.setJpaPropertyMap(properties);
		return entityManager;
	}
}
