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
package org.kuali.kfs.coa.service.impl;

import java.util.Collection;
import java.util.Map;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.datadictionary.InactivationBlockingMetadata;
import org.kuali.core.service.InactivationBlockingDetectionService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.service.PersistenceStructureService;
import org.kuali.core.service.impl.InactivationBlockingDetectionServiceImpl;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjCd;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;

public class IndirectCostRecoveryRateDetailInactivationBlockingDetectionServiceImpl extends InactivationBlockingDetectionServiceImpl {
    protected PersistenceService persistenceService;
    
    @Override
    protected Map<String, Object> buildInactivationBlockerQueryMap(BusinessObject blockedBo, InactivationBlockingMetadata inactivationBlockingMetadata) {
        Class boClass = blockedBo.getClass();
        if (!(Account.class.isAssignableFrom(boClass) || SubAccount.class.isAssignableFrom(boClass) || ObjectCode.class.isAssignableFrom(boClass) || SubObjCd.class.isAssignableFrom(boClass))) {
            throw new IllegalArgumentException("BO must be either an Account, SubAccount, ObjectCode, or SubObjCd");
        }
        
        // this code assumes that the PK field names in the BO are identical to the field names in the ICR Rate Detail BO
        Map<String, Object> fieldValues = persistenceService.getPrimaryKeyFieldValues(blockedBo);
        if (Account.class.isAssignableFrom(boClass)) {
            fieldValues.put(KFSPropertyConstants.ACCOUNT_CLOSED_INDICATOR, KFSConstants.ParameterValues.NO);
        }
        else {
            fieldValues.put(KFSPropertyConstants.ACTIVE, KFSConstants.ParameterValues.YES);
        }
        return fieldValues;
    }

    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }
}
