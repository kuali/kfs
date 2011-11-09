/*
 * Copyright 2006 The Kuali Foundation
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

import org.kuali.kfs.coa.businessobject.ObjectConsolidation;
import org.kuali.kfs.coa.service.ObjectConsService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This service implementation is the default implementation of the BalanceTyp service that is delivered with Kuali.
 */

@NonTransactional
public class ObjectConsServiceImpl implements ObjectConsService {

    /**
     * @see org.kuali.kfs.coa.service.ObjectConsService#getByPrimaryId(java.lang.String, java.lang.String)
     */
    public ObjectConsolidation getByPrimaryId(String chartOfAccountsCode, String objectConsCode) {
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        keys.put(KFSPropertyConstants.FIN_CONSOLIDATION_OBJECT_CODE, objectConsCode);
        return (ObjectConsolidation)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(ObjectConsolidation.class, keys);
    }


}
