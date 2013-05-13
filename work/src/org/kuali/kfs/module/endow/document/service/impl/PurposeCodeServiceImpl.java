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
import org.kuali.kfs.module.endow.businessobject.PurposeCode;
import org.kuali.kfs.module.endow.document.service.PurposeCodeService;
import org.kuali.rice.krad.service.BusinessObjectService;

public class PurposeCodeServiceImpl implements PurposeCodeService {

    private BusinessObjectService businessObjectService;
    
    /**
     * @see org.kuali.kfs.module.endow.document.service.PurposeCodeService#getByPrimaryKey(java.lang.String)
     */
    public PurposeCode getByPrimaryKey(String code) {
        PurposeCode purposeCode = null;
        if (StringUtils.isNotBlank(code)) {
            Map criteria = new HashMap();
            criteria.put("code", code);
            purposeCode = (PurposeCode) businessObjectService.findByPrimaryKey(PurposeCode.class, criteria);

        }
        return purposeCode;
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
     * Sets the businessObjectService.
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
