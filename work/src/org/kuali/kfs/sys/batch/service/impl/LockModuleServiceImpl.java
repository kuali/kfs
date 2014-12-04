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
package org.kuali.kfs.sys.batch.service.impl;

import org.kuali.kfs.sys.batch.service.LockModuleService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

public class LockModuleServiceImpl implements LockModuleService {
    private BusinessObjectService businessObjectService;
    private ParameterService parameterService;
    
    public void lockModule(String namespaceCode , boolean lockModule) {
        Parameter parameter  = parameterService.getParameter(namespaceCode, KfsParameterConstants.PARAMETER_ALL_DETAIL_TYPE, KRADConstants.SystemGroupParameterNames.OLTP_LOCKOUT_ACTIVE_IND);
        Parameter.Builder updatedParameter = Parameter.Builder.create(parameter);
        if (lockModule) {
            updatedParameter.setValue("Y");
        }
        else {
            updatedParameter.setValue("N");
        }
        parameterService.updateParameter(updatedParameter.build());
        
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
}
