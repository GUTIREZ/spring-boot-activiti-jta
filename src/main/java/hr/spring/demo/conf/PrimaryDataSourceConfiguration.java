package hr.spring.demo.conf;

import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import bitronix.tm.resource.jdbc.PoolingDataSource;

@Configuration
@DependsOn("transactionManager")
@EnableJpaRepositories(
		entityManagerFactoryRef = "entityManagerFactory",
		transactionManagerRef = "transactionManager",
		basePackages = "hr.spring.demo.repository")
public class PrimaryDataSourceConfiguration {
	
	@Autowired
	private JpaVendorAdapter jpaVendorAdapter;
	
	@Primary
	@Bean(initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties(prefix = "primary.datasource")
    public DataSource dataSource() {
		return new PoolingDataSource();
    }
	
	@Primary
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws Throwable {

		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("javax.persistence.transactionType", "JTA");

		LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
		entityManager.setJtaDataSource(dataSource());
		entityManager.setJpaVendorAdapter(jpaVendorAdapter);
		entityManager.setPackagesToScan("hr.spring.demo.domain");
		entityManager.setPersistenceUnitName("primaryPersistenceUnit");
		entityManager.setJpaPropertyMap(properties);
		return entityManager;
	}
	
}
