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
import org.kuali.module.gl.service.BalanceService;
import org.kuali.module.gl.web.Constant;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.dao.LaborDao;
import org.kuali.module.labor.service.LaborInquiryOptionsService;
import org.kuali.module.labor.service.impl.LaborInquiryOptionsServiceImpl;
import org.kuali.module.labor.web.inquirable.CurrentFundsInquirableImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * The July1PositionFundingLookupableHelperServiceImpl class is the front-end for all July 1 funds balance inquiry processing.
 */

@Transactional
public class July1PositionFundingLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private BalanceService balanceService;   
    private LaborDao laborDao;
    private KualiConfigurationService kualiConfigurationService;    
    private LaborInquiryOptionsService laborInquiryOptionsService;    
    
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(July1PositionFundingLookupableHelperServiceImpl.class);
    
    /**
     * @see org.kuali.core.lookup.Lookupable#getSearchResults(java.util.Map)
     */
    @Override
    public List getSearchResults(Map fieldValues) {

        boolean unbounded = false;
        Long actualCountIfTruncated = new Long(0);
        
        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        // get the pending entry option. This method must be prior to the get search results
        String pendingEntryOption = getLaborInquiryOptionsService().getSelectedPendingEntryOption(fieldValues);

        // get the consolidation option
        boolean isConsolidated = getLaborInquiryOptionsService().isConsolidationSelected(fieldValues);
       
        if (((fieldValues.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE) != null) && (fieldValues.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE).toString().length() > 0))) {
            List emptySearchResults = new ArrayList();

            // Check for a valid labor object code for this inquiry
            if (StringUtils.indexOfAny(fieldValues.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE).toString(), LaborConstants.BalanceInquiries.VALID_LABOR_OBJECT_CODES) != 0)
                GlobalVariables.getErrorMap().putError(LaborConstants.BalanceInquiries.ERROR_INVALID_LABOR_OBJECT_CODE, LaborConstants.BalanceInquiries.ERROR_INVALID_LABOR_OBJECT_CODE, "2");

            return new CollectionIncomplete(emptySearchResults, actualCountIfTruncated);
        }        
        
        // Parse the map and call the DAO to process the inquiry
        Collection searchResultsCollection = getLaborDao().getCurrentFunds(fieldValues);

        // update search results according to the selected pending entry option
        getLaborInquiryOptionsService().updateByPendingLedgerEntry(searchResultsCollection, fieldValues, pendingEntryOption, isConsolidated, false);

        // sort list if default sort column given
        List searchResults = (List) searchResultsCollection;
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(searchResults, new BeanPropertyComparator(defaultSortColumns, true));
        }

        return new CollectionIncomplete(searchResults, actualCountIfTruncated);
    }

    /**
     * @see org.kuali.core.lookup.Lookupable#getInquiryUrl(org.kuali.core.bo.BusinessObject, java.lang.String)
     */
    @Override
    public String getInquiryUrl(BusinessObject bo, String propertyName) {
        return (new CurrentFundsInquirableImpl()).getInquiryUrl(bo, propertyName);
    }

    public BalanceService getBalanceService() {
        return balanceService;
    }

    public void setBalanceService(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    public KualiConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public LaborDao getLaborDao() {
        return laborDao;
    }

    public void setLaborDao(LaborDao laborDao) {
        this.laborDao = laborDao;
    }

    public LaborInquiryOptionsService getLaborInquiryOptionsService() {
        return laborInquiryOptionsService;
    }

    public void setLaborInquiryOptionsService(LaborInquiryOptionsService laborInquiryOptionsService) {
        this.laborInquiryOptionsService = laborInquiryOptionsService;
    }
}