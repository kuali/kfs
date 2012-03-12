/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.Constant;
import org.kuali.kfs.gl.service.impl.StringHelper;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.service.DebitDeterminerService;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.BeanPropertyComparator;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * The abstract parent class for GL Lookupables, providing base implementations of methods
 * to make adding new lookupable reports easier
 */
public abstract class AbstractGeneralLedgerLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AbstractGeneralLedgerLookupableHelperServiceImpl.class);

    protected GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    protected DebitDeterminerService debitDeterminerService;


    /**
     * This method overides that in parent class so that the maintainance actions are surpressed
     *
     * @returns links to edit and copy maintenance action for the current maintenance record. For GL balance inquire, there are no
     *          maintenance links.
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject bo, List pkNames) {
        return super.getEmptyActionUrls();
    }

    /**
     * This method tests if the user selects to see the general ledger pending entries
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
     * This method tests if the user selects to see the Debit/Credit entries
     *
     * @param fieldValues the map containing the search fields and values
     * @return the value of pending entry option
     */
    protected String getDebitCreditOption(Map fieldValues) {
        // truncate the non-property filed
        String debitCreditOption = (String) fieldValues.get(Constant.DEBIT_CREDIT_OPTION);
        fieldValues.remove(Constant.DEBIT_CREDIT_OPTION);

        return debitCreditOption;
    }

    /**
     * This method tests if the user selects to see the reports by monthly or accumulated
     *
     * @param fieldValues the map containing the search fields and values
     * @return the value of amount view option
     */
    protected String getSelectedAmountViewOption(Map fieldValues) {

        String amountViewOption = Constant.EMPTY_STRING;
        if (fieldValues.containsKey(Constant.AMOUNT_VIEW_OPTION)) {
            amountViewOption = (String) fieldValues.get(Constant.AMOUNT_VIEW_OPTION);

            // truncate the non-property filed
            fieldValues.remove(Constant.AMOUNT_VIEW_OPTION);
        }
        return amountViewOption;
    }

    /**
     * This method tests if the user selects to see the details or consolidated results
     *
     * @param fieldValues the map containing the search fields and values
     * @return true if consolidation is selected and subaccount is not specified
     *
     * KRAD Conversion: Lookupable performs checking for a particular attribute and return true or false.
     * This method is called from AccountBalanceLookupableHelperServiceImpl.java, BalanceLookupableHelperServiceImpl.java,
     * CashBalanceLookupableHelperServiceImpl.java in gl module.
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

    /**
     * This method tests if the user selects to see the results with cost share subaccount
     *
     * @param fieldValues the map containing the search fields and values
     * @return true if inclusive option is selected
     */
    protected boolean isCostShareInclusive(Map fieldValues) {
        // TODO: is this method being called?
        // truncate the non-property filed
        String costShareOption = (String) fieldValues.get(Constant.COST_SHARE_OPTION);
        fieldValues.remove(Constant.COST_SHARE_OPTION);

        if (costShareOption.equals(Constant.COST_SHARE_INCLUDE)) {
            return true;
        }
        return false;
    }


    /**
     * build the serach result list from the given collection and the number of all qualified search results
     *
     * @param searchResultsCollection the given search results, which may be a subset of the qualified search results
     * @param actualSize the number of all qualified search results
     * @return the serach result list with the given results and actual size
     */
    protected List buildSearchResultList(Collection searchResultsCollection, Long actualSize) {
        CollectionIncomplete results = new CollectionIncomplete(searchResultsCollection, actualSize);

        // sort list if default sort column given
        List searchResults = results;
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(results, new BeanPropertyComparator(defaultSortColumns, true));
        }
        return searchResults;
    }

    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        Map fieldsForLookup = new HashMap(lookupForm.getFieldsForLookup());
        String debitCreditOption = getDebitCreditOption(fieldsForLookup);

        Collection displayList = super.performLookup(lookupForm, resultTable, bounded);
        updateByDebitCreditOption(resultTable, debitCreditOption);
        return displayList;

    }



    protected void updateByDebitCreditOption(Collection resultTable , String debitCreditOption) {

        if (Constant.DEBIT_CREDIT_EXCLUDE.equals(debitCreditOption)){
            for(Object table : resultTable) {
                ResultRow  row = (ResultRow)table;
                List<Column> columns = row.getColumns();
                ArrayList<Column> newColumnList = new ArrayList<Column>();
                String debitCreditCode = null;
                String objectType = null;
                Column amountCol = null;
                boolean setAmount = false ;
                for(Column col: columns) {

                    String propertyName = col.getPropertyName();
                    if (propertyName.equals(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE)) {
                        debitCreditCode = col.getPropertyValue();
                    }
                    else if (!propertyName.equals(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT)){
                        newColumnList.add(col);
                    }

                    if(propertyName.equals(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE)) {
                        objectType = col.getPropertyValue();
                    }

                    if (propertyName.equals(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT)) {
                        amountCol = col;
                    }

                    // determine the amount sign
                    if (!newColumnList.contains(amountCol)) {
                        if ((!StringHelper.isNullOrEmpty(objectType)) && (!StringHelper.isNullOrEmpty(debitCreditCode))
                                && ObjectUtils.isNotNull(amountCol)) {
                            String amount = debitDeterminerService.getConvertedAmount(objectType, debitCreditCode, amountCol.getPropertyValue());
                            amountCol.setPropertyValue(amount);
                            newColumnList.add(amountCol);

                        }
                    }

                }

                row.setColumns(newColumnList);
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
    protected void updateByPendingLedgerEntry(Collection entryCollection, Map fieldValues, String pendingEntryOption, boolean isConsolidated, boolean isCostShareInclusive) {

        // determine if search results need to be updated by pending ledger entries
        if (Constant.ALL_PENDING_ENTRY.equals(pendingEntryOption)) {
            updateEntryCollection(entryCollection, fieldValues, false, isConsolidated, isCostShareInclusive);
        }
        else if (Constant.APPROVED_PENDING_ENTRY.equals(pendingEntryOption)) {
            updateEntryCollection(entryCollection, fieldValues, true, isConsolidated, isCostShareInclusive);
        }
    }

    /**
     * This method is an abstract method and implemented to update the given entry collection by the children classes. It is called
     * by updateByPendingLedgerEntry method.
     *
     * @param entryCollection a collection of balance entries
     * @param fieldValues the map containing the search fields and values
     * @param isApproved flag whether the approved entries or all entries will be processed
     * @param isCostShareInclusive flag whether the user selects to see the results with cost share subaccount
     * @param isConsolidated flag whether the results are consolidated or not
     */
    protected abstract void updateEntryCollection(Collection entryCollection, Map fieldValues, boolean isApproved, boolean isConsolidated, boolean isCostShareInclusive);

    // change the value of the field with the given field name into the given field value
    private void changeFieldValue(String fieldName, String fieldValue) {
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

    protected GeneralLedgerPendingEntryService getGeneralLedgerPendingEntryService() {
        return generalLedgerPendingEntryService;
    }

    public void setGeneralLedgerPendingEntryService(GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }

    protected DebitDeterminerService getDebitDeterminerService() {
        return debitDeterminerService;
    }

    public void setDebitDeterminerService(DebitDeterminerService debitDeterminerService) {
        this.debitDeterminerService = debitDeterminerService;
    }



}
