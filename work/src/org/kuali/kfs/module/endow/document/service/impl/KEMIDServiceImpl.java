/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.document.service.KEMIDService;
import org.kuali.rice.kns.service.BusinessObjectService;

public class KEMIDServiceImpl implements KEMIDService {

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.endow.document.service.KEMIDService#getByPrimaryKey(java.lang.String)
     */
    public KEMID getByPrimaryKey(String kemid) {
        KEMID theKemidObj = null;
        if (StringUtils.isNotBlank(kemid)) {
            Map criteria = new HashMap();
            criteria.put(EndowPropertyConstants.KEMID, kemid);

            theKemidObj = (KEMID) businessObjectService.findByPrimaryKey(KEMID.class, criteria);
        }
        return theKemidObj;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.document.service.KEMIDService#isTrueEndowment(java.lang.String)
     */
    public boolean isTrueEndowment(String kemid){
        boolean isTrueEndowment = false;
        KEMID theKemidObj = getByPrimaryKey(kemid);
        if (theKemidObj.getTypeRestrictionCodeForPrincipalRestrictionCode().getPermanentIndicator()){
            isTrueEndowment = true;
        }
        return isTrueEndowment;
    }

    /**
     * This method gets the businessObjectService.
     * 
     * @return businessObjectService
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * This method sets the businessObjectService
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
