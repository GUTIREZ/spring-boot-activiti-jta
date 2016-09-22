package hr.spring.demo.conf;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import bitronix.tm.TransactionManagerServices;

@Configuration
@EnableTransactionManagement
public class JtaConfiguration {

	@Bean
	public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
		hibernateJpaVendorAdapter.setShowSql(true);
		hibernateJpaVendorAdapter.setGenerateDdl(true);
		hibernateJpaVendorAdapter.setDatabase(Database.POSTGRESQL);
		return hibernateJpaVendorAdapter;
	}

	
	@Bean
	public bitronix.tm.Configuration btmConfig() {
		return TransactionManagerServices.getConfiguration();
	}
	
	@Bean(destroyMethod="shutdown")
	@DependsOn("btmConfig")
	public TransactionManager bitronixTransactionManager() {
		return TransactionManagerServices.getTransactionManager();
	}
	
	@Primary
	@Bean
	@DependsOn("bitronixTransactionManager")
	public JtaTransactionManager transactionManager() {
		JtaTransactionManager jtaTxMgr = new JtaTransactionManager();
		jtaTxMgr.setTransactionManager(bitronixTransactionManager());
		jtaTxMgr.setUserTransaction((UserTransaction) bitronixTransactionManager());
		return jtaTxMgr;
	}
	
	
}
