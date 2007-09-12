package org.kuali.rice.ojb;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.accesslayer.JdbcAccess;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.apache.ojb.broker.util.sequence.SequenceManager;
import org.apache.ojb.broker.util.sequence.SequenceManagerException;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.rice.config.ConfigurationException;


/**
 * Overriding corresponding rice implementation that doesn't work for KFS.
 */
public class ConfigurableSequenceManager implements SequenceManager {
    private static final Logger LOG = Logger.getLogger(ConfigurableSequenceManager.class);
    private static final String SEQUENCE_MANAGER_CLASS_NAME_PROPERTY = "datasource.ojb.sequence.manager";
    private SequenceManager sequenceManager;

    public ConfigurableSequenceManager(PersistenceBroker broker) {
        this.sequenceManager = createSequenceManager(broker);
    }

    protected SequenceManager createSequenceManager(PersistenceBroker broker) {
        String sequenceManagerClassName = SpringContext.getStringConfigurationProperty(SEQUENCE_MANAGER_CLASS_NAME_PROPERTY);
        try {
            Object sequenceManagerObject = ConstructorUtils.invokeConstructor(Class.forName(sequenceManagerClassName), broker);
            if (!(sequenceManagerObject instanceof SequenceManager)) {
                throw new ConfigurationException("The configured sequence manager ('" + sequenceManagerClassName + "') is not an instance of '" + SequenceManager.class.getName() + "'");
            }
            return (SequenceManager) sequenceManagerObject;
        }
        catch (Exception e) {
            String message = "Unable to configure SequenceManager specified by " + SEQUENCE_MANAGER_CLASS_NAME_PROPERTY + " KualiConfigurationService property";
            LOG.fatal(message, e);
            throw new RuntimeException(message, e);
        }
    }

    protected SequenceManager getConfiguredSequenceManager() {
        return this.sequenceManager;
    }

    public void afterStore(JdbcAccess jdbcAccess, ClassDescriptor classDescriptor, Object object) throws SequenceManagerException {
        sequenceManager.afterStore(jdbcAccess, classDescriptor, object);
    }

    public Object getUniqueValue(FieldDescriptor fieldDescriptor) throws SequenceManagerException {
        return sequenceManager.getUniqueValue(fieldDescriptor);
    }
}
