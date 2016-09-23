package hr.spring.demo;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;

import hr.spring.demo.domain.Applicant;
import hr.spring.demo.repository.ApplicantRepository;

@SpringBootApplication
@EnableAutoConfiguration(
		exclude = {
				DataSourceAutoConfiguration.class, 
				DataSourceTransactionManagerAutoConfiguration.class, 
				HibernateJpaAutoConfiguration.class})
public class SpringBootActivitiJtaApplication {

//	@Autowired
//	private ApplicantRepository applicantRepository;
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootActivitiJtaApplication.class, args);
	}

//    @Bean
//    public CommandLineRunner init(final RepositoryService repositoryService,
//                                  final RuntimeService runtimeService,
//                                  final TaskService taskService) {
//
//        return new CommandLineRunner() {
//            @Override
//            public void run(String... strings) throws Exception {
//            	Applicant applicant = new Applicant();
//            	applicant.setName("John Doe");
//            	applicant.setPhoneNumber("123456789");
//            	applicant.setEmail("john.doe@example.com");
//            	applicantRepository.save(applicant);
//                Map<String, Object> variables = new HashMap<String, Object>();
//                runtimeService.startProcessInstanceByKey("hireProcessWithJpa", variables);
//            }
//        };
//
//    }

//    @Bean
//    InitializingBean usersAndGroupsInitializer(final IdentityService identityService) {
//
//        return new InitializingBean() {
//            public void afterPropertiesSet() throws Exception {
//
//                Group group = identityService.newGroup("user");
//                group.setName("users");
//                group.setType("security-role");
//                identityService.saveGroup(group);
//
//                User admin = identityService.newUser("admin");
//                admin.setPassword("admin");
//                identityService.saveUser(admin);
//
//            }
//        };
//    }
}
