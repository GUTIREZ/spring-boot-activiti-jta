package hr.spring.demo.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import bitronix.tm.TransactionManagerServices;

@Configuration
@EnableTransactionManagement
public class JtaConfiguration {

	@Bean
	@Primary
	public JtaTransactionManager transactionManager() {
		JtaTransactionManager jtaTxMgr = new JtaTransactionManager(TransactionManagerServices.getTransactionManager(), TransactionManagerServices.getTransactionManager());
		jtaTxMgr.setAllowCustomIsolationLevels(true);
		return jtaTxMgr;
	}
	
}
