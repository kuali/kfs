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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.BeanPropertyComparator;
import org.kuali.core.util.TransactionalServiceUtils;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.web.ui.Row;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.gl.bo.TransientBalanceInquiryAttributes;
import org.kuali.module.gl.service.BalanceService;
import org.kuali.module.gl.web.Constant;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.AccountStatusBaseFunds;
import org.kuali.module.labor.dao.LaborDao;
import org.kuali.module.labor.service.LaborInquiryOptionsService;
import org.kuali.module.labor.web.inquirable.BaseFundsInquirableImpl;
import org.springframework.transaction.annotation.Transactional;


/**
 * The BaseFundsLookupableHelperServiceImpl class is the front-end for all Base Fund balance inquiry processing.
 */

@Transactional
public class BaseFundsLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BaseFundsLookupableHelperServiceImpl.class);
    private BalanceService balanceService;
    private Map fieldValues;
    private LaborDao laborDao;
    private KualiConfigurationService kualiConfigurationService;
    private LaborInquiryOptionsService laborInquiryOptionsService;    
    

    /**
     * @see org.kuali.core.lookup.Lookupable#getInquiryUrl(org.kuali.core.bo.BusinessObject, java.lang.String)
     */
    @Override
    public String getInquiryUrl(BusinessObject bo, String propertyName) {
        return (new BaseFundsInquirableImpl()).getInquiryUrl(bo, propertyName);
    }
    
    /**
     * @see org.kuali.core.lookup.Lookupable#gfetSearchResults(java.util.Map)
     */

    @Override
    public List getSearchResults(Map fieldValues) {

        boolean unbounded = false;

        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));
 
        // get the consolidation option
        boolean isConsolidated = getLaborInquiryOptionsService().isConsolidationSelected(fieldValues, (Collection<Row>) getRows());
               
        Collection searchResultsCollection = buildBaseFundsCollection(findBaseFunds(fieldValues, isConsolidated), fieldValues, isConsolidated);

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
     * Retrieve the Account Status
     */
    public Iterator findBaseFunds(Map fieldValues, boolean isConsolidated) {
        LOG.debug("findBaseFunds() started");
        return TransactionalServiceUtils.copyToExternallyUsuableIterator(laborDao.getBaseFunds(fieldValues, isConsolidated));
    }

    /**
     * @param iterator the iterator of search results of account status
     * @param isConsolidated determine if the consolidated result is desired
     * 
     * @return the base funds collection
     */
    private Collection buildBaseFundsCollection(Iterator iterator, Map fieldValues, boolean isConsolidated) {
        Collection retval = null;
        
        if (isConsolidated) {
            retval = buildCosolidatedBaseFundsCollection(iterator, fieldValues);
        }
        else {
            retval = buildDetailedBaseFundsCollection(iterator, fieldValues);
        }
        return retval;
    }

    /**
     * This method builds the base funds collection with consolidation option from an iterator
     * 
     * @param iterator
     * 
     * @return the consolidated base funds collection
     */
    private Collection buildCosolidatedBaseFundsCollection(Iterator iterator, Map fieldValues) {
        Collection retval = new ArrayList();
        
        while (iterator.hasNext()) {
            Object collectionEntry = iterator.next();

            if (collectionEntry.getClass().isArray()) {
                int i = 0;
                Object[] array = (Object[]) collectionEntry;
                AccountStatusBaseFunds bf = new AccountStatusBaseFunds();
                
                if (AccountStatusBaseFunds.class.isAssignableFrom(getBusinessObjectClass())) {
                    try {
                        bf = (AccountStatusBaseFunds) getBusinessObjectClass().newInstance();
                    } 
                    catch (Exception e) {
                        LOG.warn("Using " + AccountStatusBaseFunds.class + " for results because I couldn't instantiate the " + getBusinessObjectClass());
                    }
                }
                else {
                        LOG.warn("Using " + AccountStatusBaseFunds.class + " for results because I couldn't instantiate the " + getBusinessObjectClass());
                }
                    
                bf.setUniversityFiscalYear(new Integer(array[i++].toString()));
                bf.setChartOfAccountsCode(array[i++].toString());
                bf.setAccountNumber(array[i++].toString());

                String subAccountNumber = Constant.CONSOLIDATED_SUB_ACCOUNT_NUMBER;
                bf.setSubAccountNumber(subAccountNumber);

                bf.setBalanceTypeCode(array[i++].toString());
                bf.setObjectCode(array[i++].toString());

                bf.setObjectId(array[i++].toString());
                bf.setPositionNumber(array[i++].toString());
               
                bf.setSubObjectCode(Constant.CONSOLIDATED_SUB_OBJECT_CODE);
                bf.setObjectTypeCode(Constant.CONSOLIDATED_OBJECT_TYPE_CODE);

                bf.setAccountLineAnnualBalanceAmount(new KualiDecimal(array[i++].toString()));
                bf.setBeginningBalanceLineAmount(new KualiDecimal(array[i++].toString()));
                bf.setContractsGrantsBeginningBalanceAmount(new KualiDecimal(array[i++].toString()));

                bf.setCsfAmount(getCsfAmount(bf, fieldValues));
                bf.setDummyBusinessObject(new TransientBalanceInquiryAttributes());

                retval.add(bf);
            }
        }
        return retval;
    }


    /**
     * Assign to base funds bo a Csf Amount
     */
    private KualiDecimal getCsfAmount(AccountStatusBaseFunds bo, Map fieldValues) {
        fieldValues.put("emplid", bo.getEmplid());
        return (KualiDecimal) getLaborDao().getCSFTrackerTotal(fieldValues);
    }

    /**
     * This method builds the base funds collection with detail option from an iterator
     * 
     * @param iterator the current funds iterator
     * 
     * @return the detailed balance collection
     */
    private Collection buildDetailedBaseFundsCollection(Iterator iterator, Map fieldValues) {
        Collection retval = new ArrayList();
        
        while (iterator.hasNext()) {
            AccountStatusBaseFunds bf = (AccountStatusBaseFunds) (iterator.next());

            bf.setDummyBusinessObject(new TransientBalanceInquiryAttributes());
            bf.setCsfAmount(getCsfAmount(bf, fieldValues));

            retval.add(bf);
        }
        return retval;
    }

    public void updateEntryCollection(Collection entryCollection, Map fieldValues, boolean isApproved, boolean isConsolidated, boolean isCostShareInclusive) {
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
