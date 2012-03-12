/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.coa.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.InactivationBlockingMetadata;
import org.kuali.rice.krad.service.PersistenceService;
import org.kuali.rice.krad.service.impl.InactivationBlockingDetectionServiceImpl;

public class IndirectCostRecoveryRateDetailInactivationBlockingDetectionServiceImpl extends InactivationBlockingDetectionServiceImpl {
    protected PersistenceService persistenceService;
    
    @Override
    protected Map<String, String> buildInactivationBlockerQueryMap(BusinessObject blockedBo, InactivationBlockingMetadata inactivationBlockingMetadata) {
        Class<? extends BusinessObject> boClass = blockedBo.getClass();
        if (!(Account.class.isAssignableFrom(boClass) || SubAccount.class.isAssignableFrom(boClass) || ObjectCode.class.isAssignableFrom(boClass) || SubObjectCode.class.isAssignableFrom(boClass))) {
            throw new IllegalArgumentException("BO must be either an Account, SubAccount, ObjectCode, or SubObjectCode (was: " + boClass + ")");
        }
        
        // this code assumes that the PK field names in the BO are identical to the field names in the ICR Rate Detail BO
        Map<String, Object> fieldValues = persistenceService.getPrimaryKeyFieldValues(blockedBo);
        if (Account.class.isAssignableFrom(boClass)) {
            fieldValues.put(KFSPropertyConstants.ACCOUNT_ACTIVE_INDICATOR, KFSConstants.ParameterValues.NO);
        }
        else {
            fieldValues.put(KFSPropertyConstants.ACTIVE, KFSConstants.ParameterValues.YES);
        }
        return convertFieldValuesToStrings(fieldValues);
    }
    
    /**
     * Converts the map of PKs - which is a Map of <String, Object> to a Map of <String, String> by turning any objects inside into Strings...
     * @param fieldValues field values to convert
     * @return the Map of fieldValues converted to a Map of <String, String>
     */
    protected Map<String, String> convertFieldValuesToStrings(Map<String, Object> fieldValues) {
        Map<String, String> newFieldValues = new HashMap<String, String>();
        for (String key : fieldValues.keySet()) {
            final Object value = fieldValues.get(key);
            if (value != null) {
                newFieldValues.put(key, value.toString());
            }
        }
        return newFieldValues;
    }

    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }
}
