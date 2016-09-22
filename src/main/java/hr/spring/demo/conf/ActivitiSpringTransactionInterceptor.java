package hr.spring.demo.conf;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandConfig;
import org.activiti.spring.SpringTransactionInterceptor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class ActivitiSpringTransactionInterceptor extends SpringTransactionInterceptor {
 
    private boolean convertRequiredToMandatory;

	ActivitiSpringTransactionInterceptor(PlatformTransactionManager transactionManager) {
        super(transactionManager);
    }
 
    @Override
    public <T> T execute(final CommandConfig config, final Command<T> command) {
 
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(getPropagation(config));
 
        return transactionTemplate.execute((status) -> next.execute(config, command));
    }
 
    private int getPropagation(CommandConfig config) {
        switch (config.getTransactionPropagation()) {
            case NOT_SUPPORTED:
                return TransactionTemplate.PROPAGATION_NOT_SUPPORTED;
            case REQUIRED:
                if (convertRequiredToMandatory) {
                    return TransactionTemplate.PROPAGATION_MANDATORY;
                } else {
                    return TransactionTemplate.PROPAGATION_REQUIRED;
                }
            case REQUIRES_NEW:
                return TransactionTemplate.PROPAGATION_REQUIRES_NEW;
            default:
                throw new ActivitiIllegalArgumentException("Unsupported transaction propagation: " + config.getTransactionPropagation());
        }
    }
    
    public boolean isConvertRequiredToMandatory() {
		return convertRequiredToMandatory;
	}

	public void setConvertRequiredToMandatory(boolean convertRequiredToMandatory) {
		this.convertRequiredToMandatory = convertRequiredToMandatory;
	}
 
}