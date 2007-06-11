/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.web.lookupable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.service.BalanceService;
import org.kuali.module.labor.bo.EmployeeFunding;
import org.kuali.module.labor.dao.LaborDao;
import org.kuali.module.labor.web.inquirable.EmployeeFundingInquirableImpl;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class EmployeeFundingLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private BalanceService balanceService;
    private Map fieldValues;
    private LaborDao laborDao;
    private KualiConfigurationService kualiConfigurationService;

    /**
     * @see org.kuali.core.lookup.Lookupable#gfetSearchResults(java.util.Map)
     */
    @Override
    public List getSearchResults(Map fieldValues) {
        
        this.setLaborDao(SpringServiceLocator.getLaborDao());
        boolean unbounded = false;

        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));
        
        // Parse the map and call the DAO to process the inquiry
        Iterator searchResultsCollection = laborDao.getEmployeeFunding(fieldValues);

        List balanceInquiryCollection = new ArrayList();
        while (searchResultsCollection.hasNext()) {
            Object collectionEntry = searchResultsCollection.next();

            if (collectionEntry.getClass().isArray()) {
                int i = 0;
                Object[] array = (Object[]) collectionEntry;
                EmployeeFunding employeeFunding = new EmployeeFunding();
                employeeFunding.setAccountLineAnnualBalanceAmount(new KualiDecimal(array[i++].toString()));
                employeeFunding.setUniversityFiscalYear(new Integer(array[i++].toString()));
                employeeFunding.setChartOfAccountsCode(array[i++].toString());
                employeeFunding.setAccountNumber(array[i++].toString());
                employeeFunding.setSubAccountNumber(array[i++].toString());
                employeeFunding.setFinancialObjectCode(array[i++].toString());
                employeeFunding.setFinancialSubObjectCode(array[i++].toString());
                employeeFunding.setPositionNumber(array[i++].toString());
                employeeFunding.setEmplid(array[i++].toString());
                balanceInquiryCollection.add(employeeFunding);
            }
        }
        return new CollectionIncomplete(balanceInquiryCollection, new Long(0));
    }

    /**
     * @see org.kuali.core.lookup.Lookupable#getInquiryUrl(org.kuali.core.bo.BusinessObject, java.lang.String)
     */
    @Override
    public String getInquiryUrl(BusinessObject bo, String propertyName) {
        return (new EmployeeFundingInquirableImpl()).getInquiryUrl(bo, propertyName);
    }
    
    public void setLaborDao(LaborDao laborDao) {
        this.laborDao = laborDao;
    }
}