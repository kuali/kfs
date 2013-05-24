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
package org.kuali.kfs.module.cg.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.cg.businessobject.LetterOfCreditFund;
import org.kuali.kfs.module.cg.service.LetterOfCreditFundService;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * Implementation of the LetterOfCreditFundService
 */
public class LetterOfCreditFundServiceImpl implements LetterOfCreditFundService {
    private BusinessObjectService businessObjectService;


    /**
     * @see org.kuali.kfs.module.cg.service.LetterOfCreditFundService#getByPrimaryId(java.lang.String)
     */
    @Override
    public LetterOfCreditFund getByPrimaryId(String letterOfCreditFundCode) {
        return businessObjectService.findByPrimaryKey(LetterOfCreditFund.class, mapPrimaryKeys(letterOfCreditFundCode));
    }

    protected Map<String, Object> mapPrimaryKeys(String letterOfCreditFundCode) {
        Map<String, Object> primaryKeys = new HashMap();
        primaryKeys.put("letterOfCreditFundCode", letterOfCreditFundCode);
        return primaryKeys;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


}