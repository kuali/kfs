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

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.BeanPropertyComparator;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.ui.Field;
import org.kuali.core.web.ui.Row;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.service.BalanceService;
import org.kuali.module.gl.web.Constant;
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
     * @see org.kuali.core.lookup.Lookupable#getSearchResults(java.util.Map)
     */
    @Override
    public List getSearchResults(Map fieldValues) {

        boolean unbounded = false;
        Long actualCountIfTruncated = new Long(0);

        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        // get the pending entry option. This method must be prior to the get search results
        String pendingEntryOption = getSelectedPendingEntryOption(fieldValues);
        System.out.println("pendingEntryOption:" + pendingEntryOption);

        // get the consolidation option
        boolean isConsolidated = isConsolidationSelected(fieldValues);
        System.out.println("isConsolidated:" + isConsolidated);

        // update search results according to the selected pending entry option
        // updateByPendingLedgerEntry(searchResultsCollection, fieldValues, pendingEntryOption, isConsolidated, false);

        if (((fieldValues.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE) != null) && (fieldValues.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE).toString().length() > 0))) {

            // Check for a valid labor object code for this inquiry
            if (StringUtils.indexOfAny(fieldValues.get(KFSPropertyConstants.FINANCIAL_OBJECT_CODE).toString(), LaborConstants.BalanceInquiries.VALID_LABOR_OBJECT_CODES) != 0)
                GlobalVariables.getErrorMap().putError(LaborConstants.BalanceInquiries.ERROR_INVALID_LABOR_OBJECT_CODE, LaborConstants.BalanceInquiries.ERROR_INVALID_LABOR_OBJECT_CODE, "2");
            List searchResults = new ArrayList();

            return new CollectionIncomplete(searchResults, actualCountIfTruncated);
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

        return new CollectionIncomplete(searchResults, actualCountIfTruncated);
    }

    /**
     * @see org.kuali.core.lookup.Lookupable#getInquiryUrl(org.kuali.core.bo.BusinessObject, java.lang.String)
     */
    @Override
    public String getInquiryUrl(BusinessObject bo, String propertyName) {
        return (new CurrentFundsInquirableImpl()).getInquiryUrl(bo, propertyName);
    }


    /**
     * This method tests if the user selects to see the general ledager pending entries
     * 
     * @param fieldValues the map containing the search fields and values
     * @return the value of pending entry option
     */
    protected String getSelectedPendingEntryOption(Map fieldValues) {
        // truncate the non-property filed
        String pendingEntryOption = (String) fieldValues.get(Constant.PENDING_ENTRY_OPTION);
        fieldValues.remove(Constant.PENDING_ENTRY_OPTION);

        return pendingEntryOption;
    }

    /**
     * This method tests if the user selects to see the details or consolidated results
     * 
     * @param fieldValues the map containing the search fields and values
     * @return true if consolidation is selected and subaccount is not specified
     */
    protected boolean isConsolidationSelected(Map fieldValues) {
        // truncate the non-property filed
        String consolidationOption = (String) fieldValues.get(Constant.CONSOLIDATION_OPTION);
        fieldValues.remove(Constant.CONSOLIDATION_OPTION);

        // detail option would be used
        if (Constant.DETAIL.equals(consolidationOption)) {
            return false;
        }

        // if the subAccountNumber is specified, detail option could be used
        String subAccountNumber = (String) fieldValues.get(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        if (!StringUtils.isBlank(subAccountNumber)) {
            this.changeFieldValue(Constant.CONSOLIDATION_OPTION, Constant.DETAIL);
            return false;
        }

        // if the subObjectCode is specified, detail option could be used
        String subObjectCode = (String) fieldValues.get(KFSPropertyConstants.SUB_OBJECT_CODE);
        if (!StringUtils.isBlank(subObjectCode)) {
            this.changeFieldValue(Constant.CONSOLIDATION_OPTION, Constant.DETAIL);
            return false;
        }

        // if the objectTypeCode is specified, detail option could be used
        String objectTypeCode = (String) fieldValues.get(KFSPropertyConstants.OBJECT_TYPE_CODE);
        if (!StringUtils.isBlank(objectTypeCode)) {
            this.changeFieldValue(Constant.CONSOLIDATION_OPTION, Constant.DETAIL);
            return false;
        }
        return true;
    }

    // change the value of the field with the given field name into the given field value
    protected void changeFieldValue(String fieldName, String fieldValue) {
        for (Iterator rowIterator = getRows().iterator(); rowIterator.hasNext();) {
            Row row = (Row) rowIterator.next();

            for (Iterator fieldIterator = row.getFields().iterator(); fieldIterator.hasNext();) {
                Field field = (Field) fieldIterator.next();

                if (field.getPropertyName().equals(fieldName)) {
                    field.setPropertyValue(fieldValue);
                }
            }
        }
    }
}
