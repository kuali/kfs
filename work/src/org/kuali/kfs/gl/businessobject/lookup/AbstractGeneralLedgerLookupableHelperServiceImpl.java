/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.gl.web.lookupable;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.util.BeanPropertyComparator;
import org.kuali.core.web.ui.Field;
import org.kuali.core.web.ui.Row;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.module.gl.web.Constant;

public abstract class AbstractGLLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AbstractGLLookupableHelperServiceImpl.class);
    
    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;

    protected GeneralLedgerPendingEntryService getGeneralLedgerPendingEntryService() {
        return generalLedgerPendingEntryService;
    }

    public void setGeneralLedgerPendingEntryService(GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }
    
    /**
     * This method overides that in parent class so that the maintainance actions are surpressed
     * 
     * @returns links to edit and copy maintenance action for the current maintenance record. For GL balance inquire, there are no
     *          maintenance links.
     */
    @Override
    public String getActionUrls(BusinessObject bo) {
        return KFSConstants.EMPTY_STRING;
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
        List searchResults = (List) results;
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(results, new BeanPropertyComparator(defaultSortColumns, true));
        }
        return searchResults;
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
}
