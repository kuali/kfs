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
