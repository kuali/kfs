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
package org.kuali.module.labor.service.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.core.web.ui.Field;
import org.kuali.core.web.ui.Row;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.gl.util.BusinessObjectFieldConverter;
import org.kuali.module.gl.web.Constant;
import org.kuali.module.labor.bo.PendingLedgerEntry;
import org.kuali.module.labor.service.LaborInquiryOptionsService;
import org.kuali.module.labor.service.LaborLedgerPendingEntryService;
import org.springframework.transaction.annotation.Transactional;

/**
 * The LaborInquiryOptionsService class is a service that will generate Pending Ledger and Consilidation options for balance inquiries.
 */
@Transactional
public class LaborInquiryOptionsServiceImpl extends AbstractLookupableHelperServiceImpl implements LaborInquiryOptionsService {

    protected LaborLedgerPendingEntryService laborLedgerPendingEntryService;

    
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborInquiryOptionsServiceImpl.class);


    public List getSearchResults(Map fieldValues) {
        List searchResults = null;
        return searchResults;
    }
    
    /**
     * This method tests if the user selects to see the details or consolidated results
     * 
     * @param fieldValues the map containing the search fields and values
     * @return true if consolidation is selected and subaccount is not specified
     */
    public boolean isConsolidationSelected(Map fieldValues) {
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
    public void changeFieldValue(String fieldName, String fieldValue) {
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
    
    /**
     * This method is used to update amounts of the given entries with the corresponding pending amounts. It is a factory that
     * executes the update methods of individual derived classes.
     * 
     * @param entryCollection a collection of balance entries
     * @param fieldValues the map containing the search fields and values
     * @param pendingEntryOption flag whether the approved entries or all entries will be processed
     * @param isCostShareInclusive flag whether the user selects to see the results with cost share subaccount
     * @param isConsolidated flag whether the results are consolidated or not
     */
    public void updateByPendingLedgerEntry(Collection entryCollection, Map fieldValues, String pendingEntryOption, boolean isConsolidated, boolean isCostShareInclusive) {

        // determine if search results need to be updated by pending ledger entries
        if (Constant.ALL_PENDING_ENTRY.equals(pendingEntryOption)) {
            updateEntryCollection(entryCollection, fieldValues, false, isConsolidated, isCostShareInclusive);
        }
        else if (Constant.APPROVED_PENDING_ENTRY.equals(pendingEntryOption)) {
            updateEntryCollection(entryCollection, fieldValues, true, isConsolidated, isCostShareInclusive);
        }
    }

    /**
     * This method tests if the user selects to see the labor ledager pending entries
     * 
     * @param fieldValues the map containing the search fields and values
     * @return the value of pending entry option
     */
    public String getSelectedPendingEntryOption(Map fieldValues) {
        // truncate the non-property filed
        String pendingEntryOption = (String) fieldValues.get(Constant.PENDING_ENTRY_OPTION);
        fieldValues.remove(Constant.PENDING_ENTRY_OPTION);

        return pendingEntryOption;
    }
    
    /**
     * @see org.kuali.module.gl.web.lookupable.AbstractGLLookupableImpl#updateEntryCollection(java.util.Collection, java.util.Map,
     *      boolean, boolean, boolean)
     */
    public void updateEntryCollection(Collection entryCollection, Map fieldValues, boolean isApproved, boolean isConsolidated, boolean isCostShareExcluded) {
        // convert the field names of balance object into corresponding ones of pending entry object
        Map pendingEntryFieldValues = BusinessObjectFieldConverter.convertToTransactionFieldValues(fieldValues);

        // go through the pending entries to update the balance collection       
        Iterator pendingEntryIterator = getLaborLedgerPendingEntryService().findPendingLedgerEntriesForAccountBalance(pendingEntryFieldValues, isApproved);

        while (pendingEntryIterator.hasNext()) {
            PendingLedgerEntry pendingEntry = (PendingLedgerEntry) pendingEntryIterator.next();

            if (isCostShareExcluded) {
                if ((pendingEntry.getSubAccount() != null) && (pendingEntry.getSubAccount().getA21SubAccount() != null)) {
                    if ("CS".equals(pendingEntry.getSubAccount().getA21SubAccount().getSubAccountTypeCode())) {
                        // Don't process this one
                        continue;
                    }
                }
            }

            // if consolidated, change the following fields into the default values for consolidation
            if (isConsolidated) {
                pendingEntry.setSubAccountNumber(Constant.CONSOLIDATED_SUB_ACCOUNT_NUMBER);
                pendingEntry.setFinancialSubObjectCode(Constant.CONSOLIDATED_SUB_OBJECT_CODE);
                pendingEntry.setFinancialObjectTypeCode(Constant.CONSOLIDATED_OBJECT_TYPE_CODE);
            }

//            AccountBalance accountBalance = postAccountBalance.findAccountBalance(entryCollection, pendingEntry);
  //          postAccountBalance.updateAccountBalance(pendingEntry, accountBalance);

            // recalculate the variance after pending entries are combined into account balances
      //      if (accountBalance.getDummyBusinessObject() == null) {
        //        accountBalance.setDummyBusinessObject(new TransientBalanceInquiryAttributes());
          //  }
    //        KualiDecimal variance = calculateVariance(accountBalance);
          //  accountBalance.getDummyBusinessObject().setGenericAmount(variance);
        }
    }

    public LaborLedgerPendingEntryService getLaborLedgerPendingEntryService() {
        return laborLedgerPendingEntryService;
    }

    public void setLaborLedgerPendingEntryService(LaborLedgerPendingEntryService laborLedgerPendingEntryService) {
        this.laborLedgerPendingEntryService = laborLedgerPendingEntryService;
    }

}
