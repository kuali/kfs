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
package org.kuali.kfs.module.cg.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.cg.CGPropertyConstants;
import org.kuali.kfs.module.cg.businessobject.LetterOfCreditFund;
import org.kuali.kfs.module.cg.service.LetterOfCreditFundService;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * Implementation of the LetterOfCreditFundService
 */
public class LetterOfCreditFundServiceImpl implements LetterOfCreditFundService {
    protected BusinessObjectService businessObjectService;


    /**
     * @see org.kuali.kfs.module.cg.service.LetterOfCreditFundService#getByPrimaryId(java.lang.String)
     */
    @Override
    public LetterOfCreditFund getByPrimaryId(String letterOfCreditFundCode) {
        return businessObjectService.findByPrimaryKey(LetterOfCreditFund.class, mapPrimaryKeys(letterOfCreditFundCode));
    }

    protected Map<String, Object> mapPrimaryKeys(String letterOfCreditFundCode) {
        Map<String, Object> primaryKeys = new HashMap();
        primaryKeys.put(CGPropertyConstants.LETTER_OF_CREDIT_FUND_CODE, letterOfCreditFundCode);
        return primaryKeys;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


}
