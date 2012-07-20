/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.businessobject.CreditCardAgency;
import org.kuali.kfs.module.tem.service.CreditCardAgencyService;
import org.kuali.rice.kns.service.BusinessObjectService;

public class CreditCardAgencyServiceImpl implements CreditCardAgencyService {

    BusinessObjectService businessObjectSerivce;

    /**
     * @see org.kuali.kfs.module.tem.service.CreditCardAgencyService#getCorpCreditCardAgencyList()
     */
    @Override
    public List<CreditCardAgency> getCorpCreditCardAgencyList() {
        Map<String, String> key = new HashMap<String, String>();
        List<CreditCardAgency> resultList = (List<CreditCardAgency>)businessObjectSerivce.findMatching(CreditCardAgency.class, key);
        return resultList;
    }
    
    /**
     * @see org.kuali.kfs.module.tem.service.CreditCardAgencyService#getCorpCreditCardAgencyCodeList()
     */
    @Override
    public List<String> getCorpCreditCardAgencyCodeList() {
        //List<CreditCardAgency> resultList = getCorpCreditCardAgencyList();
        List<String> resultList = new ArrayList<String>();
        resultList.add("USBC");
        return resultList;
    }
    
    public void setBusinessObjectSerivce(BusinessObjectService businessObjectSerivce) {
        this.businessObjectSerivce = businessObjectSerivce;
    }

}
