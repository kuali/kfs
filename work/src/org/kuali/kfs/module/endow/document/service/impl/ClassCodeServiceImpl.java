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
