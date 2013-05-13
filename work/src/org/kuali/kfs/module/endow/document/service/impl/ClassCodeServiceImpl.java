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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.document.service.ClassCodeService;
import org.kuali.rice.krad.service.BusinessObjectService;

public class ClassCodeServiceImpl implements ClassCodeService {
    protected BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.endow.document.service.ClassCodeService#getByPrimaryKey(java.lang.String)
     */
    public ClassCode getByPrimaryKey(String code) {

        ClassCode classCode = null;
        if (StringUtils.isNotBlank(code)) {
            Map criteria = new HashMap();
            criteria.put("code", code);
            classCode = (ClassCode) businessObjectService.findByPrimaryKey(ClassCode.class, criteria);
        }

        return classCode;
    }


    /**
     * @see org.kuali.kfs.module.endow.document.service.ClassCodeService#getClassCodesForAccrualProcessing()
     */
    public Collection<ClassCode> getClassCodesForAccrualProcessing() {

        Collection<ClassCode> classCodes = null;

        String[] accrualMethodsForCriteria = new String[] { EndowConstants.AccrualMethod.AUTOMATED_CASH_MANAGEMENT, EndowConstants.AccrualMethod.TIME_DEPOSITS, EndowConstants.AccrualMethod.TREASURY_NOTES_AND_BONDS, EndowConstants.AccrualMethod.DIVIDENDS };
        Map<String, String> criteria = new HashMap<String, String>();

        for (int i = 0; i < accrualMethodsForCriteria.length; i++) {
            criteria.put(EndowPropertyConstants.CLASS_CODE_SEC_ACCRUAL_METHOD, accrualMethodsForCriteria[i]);
            Collection<ClassCode> tmpClassCodes = businessObjectService.findMatching(ClassCode.class, criteria);
            if (classCodes == null) {
                classCodes = tmpClassCodes;
            }
            else {
                classCodes.addAll(tmpClassCodes);
            }
            criteria.clear();
        }

        return classCodes;
    }

    /**
     * Gets the businessObjectService.
     * 
     * @return businessObjectService
     */
    protected BusinessObjectService getBusinessObjectService() {
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
