package hr.spring.demo.conf;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
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
	public bitronix.tm.Configuration btmConfig() {
		return TransactionManagerServices.getConfiguration();
	}
	
	@Bean(destroyMethod="shutdown")
	@DependsOn("btmConfig")
	public TransactionManager bitronixTransactionManager() {
		return TransactionManagerServices.getTransactionManager();
	}
	
	@Bean
	@DependsOn("bitronixTransactionManager")
	public JtaTransactionManager transactionManager() {
		JtaTransactionManager jtaTxMgr = new JtaTransactionManager();
		jtaTxMgr.setTransactionManager(bitronixTransactionManager());
		jtaTxMgr.setUserTransaction((UserTransaction) bitronixTransactionManager());
		jtaTxMgr.setAllowCustomIsolationLevels(true);
		return jtaTxMgr;
	}
	
	
}
