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

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.CreditCardAgency;
import org.kuali.kfs.module.tem.service.CreditCardAgencyService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.ObjectUtils;

public class CreditCardAgencyServiceImpl implements CreditCardAgencyService {

    BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.tem.service.CreditCardAgencyService#getCorpCreditCardAgencyList()
     */
    @Override
    public List<CreditCardAgency> getCorpCreditCardAgencyList() {
        Map<String, String> key = new HashMap<String, String>();
        key.put(TemPropertyConstants.TRAVEL_CARD_TYPE_CODE, TemConstants.TRAVEL_TYPE_CORP);
        List<CreditCardAgency> resultList = (List<CreditCardAgency>)businessObjectService.findMatching(CreditCardAgency.class, key);
        return resultList;
    }
    
    /**
     * @see org.kuali.kfs.module.tem.service.CreditCardAgencyService#getCorpCreditCardAgencyCodeList()
     */
    @Override
    public List<String> getCorpCreditCardAgencyCodeList() {
        List<String> resultList = new ArrayList<String>();
        for (CreditCardAgency agency : getCorpCreditCardAgencyList()){
            resultList.add(agency.getCreditCardOrAgencyCode());
        }
        return resultList;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.CreditCardAgencyService#getCreditCardAgencyByCode(java.lang.String)
     */
    @Override
    public CreditCardAgency getCreditCardAgencyByCode(String code) {
        Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put(TemPropertyConstants.CREDIT_CARD_AGENCY_CODE,code);
        List<CreditCardAgency> agencyList = (List<CreditCardAgency>)businessObjectService.findMatching(CreditCardAgency.class, criteria);
        
        return ObjectUtils.isNotNull(agencyList) && !agencyList.isEmpty() ? agencyList.get(0) : null; 
    }
    
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


}
