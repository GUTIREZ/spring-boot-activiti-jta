package hr.spring.demo.conf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.impl.interceptor.CommandInterceptor;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ActivitiProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.transaction.jta.JtaTransactionManager;

@Configuration
@DependsOn({"activitiDataSource", "transactionManager", "activitiEntityManagerFactory"})
public class ActivitiConfiguration extends SpringProcessEngineConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(ActivitiConfiguration.class);
	
    @Autowired
    private DataSource activitiDataSource;
    
    @Autowired
    private EntityManagerFactory activitiEntityManagerFactory;
    
    @Autowired
    private JtaTransactionManager transactionManager;
    
    @Autowired
    private ResourcePatternResolver resourceLoader;
    
    @Autowired
    private ActivitiProperties activitiProperties;
    
    private ActivitiSpringTransactionInterceptor txInterceptor;
    
    // @see: http://blog.progs.be/727/transaction-spring-activiti
    @Override
    protected CommandInterceptor createTransactionInterceptor() {
        if (transactionManager == null) {
            throw new ActivitiException("transactionManager is required property for SpringProcessEngineConfiguration, use "
                    + StandaloneProcessEngineConfiguration.class.getName() + " otherwise");
        }
 
        txInterceptor = new ActivitiSpringTransactionInterceptor(transactionManager);
        return txInterceptor;
    }
 
    @Override
    public ProcessEngine buildProcessEngine() {
        if (null != txInterceptor) {
            txInterceptor.setConvertRequiredToMandatory(false);
        }
        ProcessEngine processEngine = super.buildProcessEngine();
        ProcessEngines.setInitialized(true);
        autoDeployResources(processEngine);
        if (null != txInterceptor) {
            txInterceptor.setConvertRequiredToMandatory(true);
        }
        return processEngine;
    }
    
    @Primary
    @Bean
    public SpringProcessEngineConfiguration processEngineConfiguration() throws IOException {
    
    	List<Resource> procDefResources = this.discoverProcessDefinitionResources(
    	        this.resourceLoader, this.activitiProperties.getProcessDefinitionLocationPrefix(),
    	        this.activitiProperties.getProcessDefinitionLocationSuffixes(),
    	        this.activitiProperties.isCheckProcessDefinitions());
    	 
      SpringProcessEngineConfiguration config = new SpringProcessEngineConfiguration();
      

      config.setDataSource(activitiDataSource);
      config.setTransactionManager(transactionManager);
      //config.setJpaEntityManagerFactory(activitiEntityManagerFactory);
      config.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_CREATE_DROP);
      
      config.setJpaCloseEntityManager(true);
      config.setTransactionsExternallyManaged(true);
      config.setJpaHandleTransaction(false);
      
      config.setJobExecutorActivate(false);
      config.setDeploymentResources(procDefResources.toArray(new Resource[procDefResources.size()]));
      
      config.setMailServerHost(activitiProperties.getMailServerHost());
      config.setMailServerPort(activitiProperties.getMailServerPort());
      config.setMailServerUsername(activitiProperties.getMailServerUserName());
      config.setMailServerPassword(activitiProperties.getMailServerPassword());
      config.setMailServerDefaultFrom(activitiProperties.getMailServerDefaultFrom());
      config.setMailServerUseSSL(activitiProperties.isMailServerUseSsl());
      config.setMailServerUseTLS(activitiProperties.isMailServerUseTls());

      config.setHistoryLevel(activitiProperties.getHistoryLevel());
      

      return config;
    }

    @Bean
    public ProcessEngineFactoryBean processEngine() throws Exception {
      ProcessEngineFactoryBean factoryBean = new ProcessEngineFactoryBean();
      factoryBean.setProcessEngineConfiguration(processEngineConfiguration());
      return factoryBean;
    }

    @Bean
    public RepositoryService repositoryService(ProcessEngine processEngine) {
      return processEngine.getRepositoryService();
    }

    @Bean
    public RuntimeService runtimeService(ProcessEngine processEngine) {
      return processEngine.getRuntimeService();
    }

    @Bean
    public TaskService taskService(ProcessEngine processEngine) {
      return processEngine.getTaskService();
    }

    @Bean
    public HistoryService historyService(ProcessEngine processEngine) {
      return processEngine.getHistoryService();
    }

    @Bean
    public ManagementService managementService(ProcessEngine processEngine) {
      return processEngine.getManagementService();
    }

    @Bean
    public FormService formService(ProcessEngine processEngine) {
      return processEngine.getFormService();
    }

    @Bean
    public IdentityService identityService(ProcessEngine processEngine) {
      return processEngine.getIdentityService();
    }
    
    public List<Resource> discoverProcessDefinitionResources(ResourcePatternResolver applicationContext, String prefix, List<String> suffixes, boolean checkPDs) throws IOException {
        if (checkPDs) {

        	List<Resource> result = new ArrayList<Resource>();
        	for (String suffix : suffixes) {
        		String path = prefix + suffix;
        		Resource[] resources = applicationContext.getResources(path);
        		if (resources != null && resources.length > 0) {
        			for (Resource resource : resources) {
        				result.add(resource);
        			}
        		}
        	}
        	
        	if (result.isEmpty()) {
        		logger.info(String.format("No process definitions were found for autodeployment"));
        	}
        	
          return result;
        }
        return new ArrayList<Resource>();
      }
    
}