/*
 * Copyright 2010 The Kuali Foundation.
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
import org.kuali.kfs.module.endow.businessobject.UseCriteriaCode;
import org.kuali.kfs.module.endow.document.service.UseCriteriaCodeService;
import org.kuali.rice.krad.service.BusinessObjectService;

public class UseCriteriaCodeServiceImpl implements UseCriteriaCodeService {

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.endow.document.service.UseCriteriaCodeService#getByPrimaryKey(java.lang.String)
     */
    public UseCriteriaCode getByPrimaryKey(String code) {

        UseCriteriaCode useCriteriaCode = null;
        if (StringUtils.isNotBlank(code)) {
            Map<String, String> criteria = new HashMap<String, String>();
            criteria.put(EndowPropertyConstants.KUALICODEBASE_CODE, code);
            useCriteriaCode = (UseCriteriaCode) businessObjectService.findByPrimaryKey(UseCriteriaCode.class, criteria);
        }
        return useCriteriaCode;
    }

    /**
     * Gets the businessObjectService attribute. 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
}
