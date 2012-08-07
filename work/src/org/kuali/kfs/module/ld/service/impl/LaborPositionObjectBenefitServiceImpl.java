/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.ld.businessobject.PositionObjectBenefit;
import org.kuali.kfs.module.ld.service.LaborPositionObjectBenefitService;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class provides its clients with access to labor position object benefit entries in the backend data store.
 * 
 * @see org.kuali.kfs.module.ld.businessobject.PositionObjectBenefit
 */
public class LaborPositionObjectBenefitServiceImpl implements LaborPositionObjectBenefitService {

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.ld.service.LaborPositionObjectBenefitService#getPositionObjectBenefits(java.lang.Integer,
     *      java.lang.String, java.lang.String)
     */
    public Collection<PositionObjectBenefit> getPositionObjectBenefits(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode) {

        Map fieldValues = new HashMap();
        fieldValues.put("universityFiscalYear", universityFiscalYear);
        fieldValues.put("chartOfAccountsCode", chartOfAccountsCode);
        fieldValues.put("financialObjectCode", financialObjectCode);

        return businessObjectService.findMatching(PositionObjectBenefit.class, fieldValues);
    }

    public Collection<PositionObjectBenefit> getActivePositionObjectBenefits(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode) {

        Map fieldValues = new HashMap();
        fieldValues.put("universityFiscalYear", universityFiscalYear);
        fieldValues.put("chartOfAccountsCode", chartOfAccountsCode);
        fieldValues.put("financialObjectCode", financialObjectCode);
        fieldValues.put("active", true);
        return businessObjectService.findMatching(PositionObjectBenefit.class, fieldValues);
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
