/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.document.service.EndowmentTransactionCodeService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class is the service implementation for the EndowmentTransactionCodeService. This is the default, Kuali provided implementation.
 */
public class EndowmentTransactionCodeServiceImpl implements EndowmentTransactionCodeService {
    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.endow.document.service.EndowmentTransactionCodeService#getByPrimaryKey(java.lang.String)
     */
    public EndowmentTransactionCode getByPrimaryKey(String eTranCode) {
        EndowmentTransactionCode endowmentTransactionCode = null;
        
        if (StringUtils.isNotBlank(eTranCode)) {
            Map criteria = new HashMap();

            if (SpringContext.getBean(DataDictionaryService.class).getAttributeForceUppercase(EndowmentTransactionCode.class, EndowPropertyConstants.KUALICODEBASE_CODE)) {
                eTranCode = eTranCode.toUpperCase();
            }
            
            criteria.put("code", eTranCode);

            endowmentTransactionCode = (EndowmentTransactionCode) businessObjectService.findByPrimaryKey(EndowmentTransactionCode.class, criteria);
        }
        return endowmentTransactionCode;
    }

    /**
     * Gets the businessObjectService.
     * 
     * @return businessObjectService
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
