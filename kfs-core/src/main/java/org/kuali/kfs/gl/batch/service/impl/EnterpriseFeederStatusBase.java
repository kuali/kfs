/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
