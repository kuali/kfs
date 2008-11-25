/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ld.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.ld.businessobject.PositionObjectGroup;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportPosition;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.InactivationBlockingMetadata;
import org.kuali.rice.kns.service.PersistenceService;
import org.kuali.rice.kns.service.impl.InactivationBlockingDetectionServiceImpl;

public class EffortCertificationReportPositionInactivationBlockingDetectionServiceImpl extends InactivationBlockingDetectionServiceImpl {
    protected PersistenceService persistenceService;
    
    @Override
    protected Map<String, Object> buildInactivationBlockerQueryMap(BusinessObject blockedBo, InactivationBlockingMetadata inactivationBlockingMetadata) {
        Class boClass = blockedBo.getClass();
        
        EffortCertificationReportDefinition effortCertificationReportDefinition = new EffortCertificationReportDefinition();
        EffortCertificationReportPosition effortCertificationReportPosition = new EffortCertificationReportPosition(); 
        
        PositionObjectGroup positionObjectGroup = (PositionObjectGroup) blockedBo ;
        
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        
        if (effortCertificationReportDefinition.isActive()) {
            fieldValues.put(KFSPropertyConstants.ACTIVE, KFSConstants.ParameterValues.NO);
        }
        else if (effortCertificationReportPosition.isActive()) {
            fieldValues.put(KFSPropertyConstants.ACTIVE, KFSConstants.ParameterValues.NO);
        }
        else if (positionObjectGroup.isActive()) {
            fieldValues.put(KFSPropertyConstants.ACTIVE, KFSConstants.ParameterValues.NO);            
        }
        else 
            fieldValues.put(KFSPropertyConstants.ACTIVE, KFSConstants.ParameterValues.YES);
        
        return fieldValues;
    }

    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }
}
