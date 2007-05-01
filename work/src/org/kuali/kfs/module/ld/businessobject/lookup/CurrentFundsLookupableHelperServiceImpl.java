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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.BeanPropertyComparator;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.service.BalanceService;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.dao.LaborDao;
import org.kuali.module.labor.web.inquirable.CurrentFundsInquirableImpl;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CurrentFundsLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private BalanceService balanceService;
    private Map fieldValues;
    private LaborDao laborDao;
    private KualiConfigurationService kualiConfigurationService;
    
    /**
     * @see org.kuali.core.lookup.Lookupable#gfetSearchResults(java.util.Map)
     */
    @Override
    public List getSearchResults(Map fieldValues) {

        boolean unbounded = false;

        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        if (((fieldValues.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE) != null) && (fieldValues.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE).toString().length() > 0))) {

            // Check for a valid labor object code for this inquiry
            if (StringUtils.indexOfAny(fieldValues.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE).toString(), LaborConstants.BalanceInquiries.VALID_LABOR_OBJECT_CODES) != 0)
            GlobalVariables.getErrorMap().putError(LaborConstants.BalanceInquiries.ERROR_INVALID_LABOR_OBJECT_CODE, 
                    LaborConstants.BalanceInquiries.ERROR_INVALID_LABOR_OBJECT_CODE, "2");
        }
        // Parse the map and call the DAO to process the inquiry
        BeanFactory beanFactory = SpringServiceLocator.getBeanFactory();
        laborDao = (LaborDao) beanFactory.getBean("laborDao");
        Collection searchResultsCollection = laborDao.getCurrentFunds(fieldValues);

        // sort list if default sort column given
        List searchResults = (List) searchResultsCollection;
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(searchResults, new BeanPropertyComparator(defaultSortColumns, true));
        }
               
        Long actualCountIfTruncated = new Long(0);

        return new CollectionIncomplete(searchResults, actualCountIfTruncated);
    }
    
    /**
     * @see org.kuali.core.lookup.Lookupable#getInquiryUrl(org.kuali.core.bo.BusinessObject, java.lang.String)
     */
    @Override
    public String getInquiryUrl(BusinessObject bo, String propertyName) {
        return (new CurrentFundsInquirableImpl()).getInquiryUrl(bo, propertyName);
    }
}