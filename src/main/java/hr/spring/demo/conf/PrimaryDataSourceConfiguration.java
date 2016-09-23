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
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import bitronix.tm.resource.jdbc.PoolingDataSource;

@Configuration
@DependsOn("transactionManager")
@EnableJpaRepositories(
		entityManagerFactoryRef = "entityManagerFactory",
		transactionManagerRef = "transactionManager",
		basePackages = "hr.spring.demo.repository")
public class PrimaryDataSourceConfiguration {
	
	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
		hibernateJpaVendorAdapter.setShowSql(true);
		hibernateJpaVendorAdapter.setGenerateDdl(true);
		hibernateJpaVendorAdapter.setDatabase(Database.POSTGRESQL);
		return hibernateJpaVendorAdapter;
	}
	

	@Bean
    @ConfigurationProperties(prefix = "primary.datasource")
    public DataSource dataSource() {
		return new PoolingDataSource();
    }
	

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws Throwable {

		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("javax.persistence.transactionType", "JTA");
		properties.put("hibernate.transaction.manager_lookup_class", "org.hibernate.transaction.BTMTransactionManagerLookup");
		properties.put("hibernate.transaction.jta.platform", "org.hibernate.service.jta.platform.internal.BitronixJtaPlatform");

		LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
		entityManager.setJtaDataSource(dataSource());
		entityManager.setJpaVendorAdapter(jpaVendorAdapter());
		entityManager.setPackagesToScan("hr.spring.demo.domain");
		entityManager.setPersistenceUnitName("primaryPersistenceUnit");
		entityManager.setJpaPropertyMap(properties);
		return entityManager;
	}
	
}
