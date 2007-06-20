/*
 * Created on Jan 19, 2005
 *
 */
package org.kuali.module.pdp.service.impl;

import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSConstants;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author local-jsissom
 *
 */
@Transactional
public class EnvironmentServiceImpl implements org.kuali.module.pdp.service.EnvironmentService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EnvironmentServiceImpl.class);

    private KualiConfigurationService kualiConfigurationService;

    public void setKualiConfigurationService(KualiConfigurationService kcs) {
        kualiConfigurationService = kcs;
    }

    public EnvironmentServiceImpl() {
        super();
    }

    /* (non-Javadoc)
     * @see edu.iu.uis.pdp.service.EnvironmentService#getEnvironment()
     */
    public String getEnvironment() {
        return kualiConfigurationService.getPropertyString(KFSConstants.ENVIRONMENT_KEY).toUpperCase();
    }

    public boolean isProduction() {
        return kualiConfigurationService.isProductionEnvironment();
    }
}
