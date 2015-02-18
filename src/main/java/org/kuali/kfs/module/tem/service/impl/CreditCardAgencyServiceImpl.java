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
package org.kuali.kfs.module.tem.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.CreditCardAgency;
import org.kuali.kfs.module.tem.service.CreditCardAgencyService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

public class CreditCardAgencyServiceImpl implements CreditCardAgencyService {

    protected BusinessObjectService businessObjectService;

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
            resultList.add(agency.getTravelCardTypeCode());
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
