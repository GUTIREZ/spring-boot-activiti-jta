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
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import bitronix.tm.resource.jdbc.PoolingDataSource;

@Configuration
@DependsOn("transactionManager")
@EnableJpaRepositories(
		entityManagerFactoryRef = "activitiEntityManagerFactory",
		transactionManagerRef = "transactionManager",
		basePackages = "org.activiti")
public class ActivitiDataSourceConfiguration {
	
	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
		hibernateJpaVendorAdapter.setShowSql(true);
		hibernateJpaVendorAdapter.setGenerateDdl(true);
		hibernateJpaVendorAdapter.setDatabase(Database.POSTGRESQL);
		return hibernateJpaVendorAdapter;
	}
	
	@Bean
    @ConfigurationProperties(prefix = "activiti.datasource")
    public DataSource activitiDataSource() {
		return new PoolingDataSource();
    }
	
	@Bean
	public LocalContainerEntityManagerFactoryBean activitiEntityManagerFactory() throws Throwable {

		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("javax.persistence.transactionType", "JTA");
		properties.put("hibernate.transaction.manager_lookup_class", "org.hibernate.transaction.BTMTransactionManagerLookup");
		properties.put("hibernate.transaction.jta.platform", "org.hibernate.service.jta.platform.internal.BitronixJtaPlatform");

		LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
		entityManager.setJtaDataSource(activitiDataSource());
		entityManager.setJpaVendorAdapter(jpaVendorAdapter());
		entityManager.setPackagesToScan("org.activiti");
		entityManager.setPersistenceUnitName("activitiPersistenceUnit");
		entityManager.setJpaPropertyMap(properties);
		return entityManager;
	}
}
