/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.gl.batch.service.impl;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;

/**
 * Base implementation for the enterprise feeder status
 */
public abstract class EnterpriseFeederStatusBase implements EnterpriseFeederStatus {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EnterpriseFeederStatusBase.class);

    /**
     * Retrieves the description in ApplicationResources.properties
     * 
     * @return the description for this class
     * @see org.kuali.kfs.gl.batch.service.impl.EnterpriseFeederStatus#getStatusDescription()
     */
    public String getStatusDescription() {
        try {
            String description = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.ENTERPRISE_FEEDER_STATUS_DESCRIPTION_PREFIX + getClass().getName());
            if (description == null) {
                return getDefaultStatusDescription();
            }
            return description;
        }
        catch (RuntimeException e) {
            LOG.error("Error occured trying to retrieve status description for class: " + getClass().getName(), e);
            return getDefaultStatusDescription();
        }
    }

    /**
     * In case there's no entry for this class in ApplicationResources.properties (or an exception occurs), then just return a
     * default class.
     * 
     * @return the default description
     */
    protected String getDefaultStatusDescription() {
        return "Status description unavailable for class name: " + getClass().getName();
    }
}
