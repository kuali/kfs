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
package org.kuali.module.purap.service;

import java.sql.Date;
import java.util.List;

import org.kuali.core.document.Document;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.bo.ItemType;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.document.PurapItemOperations;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Defines methods that must be implemented by classes providing a PurapService. 
 */
public interface PurapService {

    /**
     * Update the status for the given Purchasing/Accounts Payable document
     * 
     * @param document
     * @param statusToSet
     * @return
     */
    public boolean updateStatus(PurchasingAccountsPayableDocument document, String statusToSet);

    /**
     * Retrieve list of views for given identifier
     * 
     * @param clazz
     * @param accountsPayablePurchasingDocumentLinkIdentifier
     * @return List of views for given identifier
     */
    public List getRelatedViews(Class clazz, Integer accountsPayablePurchasingDocumentLinkIdentifier);

    /**
     * Add the allowed below the line items to the given document
     * 
     * @param document   PurchasingAccountsPayableDocument
     */
    public void addBelowLineItems(PurchasingAccountsPayableDocument document);

    /**
     * Retrieves the below the line items allowed from the parameter table for the given document
     * 
     * @param document  PurchasingAccountsPayableDocument
     * @return Array list of below the line items 
     */
    public String[] getBelowTheLineForDocument(PurchasingAccountsPayableDocument document);

    /**
     * Retrieve the below the line item for a doc by item type (unknown result if multiple of same below the line item
     * type)
     * 
     * @param document the document
     * @param iT the itemType
     * @return below the line item by item type
     */
    public PurApItem getBelowTheLineByType(PurchasingAccountsPayableDocument document, ItemType iT);
    
    /**
     * Return a Date object which is which is a given number of days away from today, in either direction. 
     * 
     * @param offsetDays
     * @return  A java.sql.Date
     */
    public Date getDateFromOffsetFromToday(int offsetDays);

    /**
     * Determine whether a given date is in the past.
     * 
     * @param compareDate An SQL date (not a DateFormatter date, or a util Date)
     * @return True if the given date is before today.
     */
    public boolean isDateInPast(Date compareDate);

    /**
     * Determine whether a given date is more than a given number of days away from the current date.
     * 
     * @param compareDate An SQL date (not a DateFormatter date, or a util Date)
     * @param daysAway An int, positive for future days, negative for past days
     * @return True if the given date is more than the given number of days away in either direction.
     */
    public boolean isDateMoreThanANumberOfDaysAway(Date compareDate, int daysAway);

    /**
     * We are obliged not to simply use a dateDiff and compare the result to 365, because we have to worry about leap years.
     * 
     * @param compareDate An SQL date (not a DateFormatter date, or a util Date)
     * @return True if the date given for comparison is more than a year in the past, not including today.
     */
    public boolean isDateAYearBeforeToday(Date compareDate);

    /**
     * Retrieve the Automatic Purchase Order Limit amount based first on the derived contract limit (see
     * {@link org.kuali.module.vendor.service.VendorService#getApoLimitFromContract(Integer, String, String)}) and if that is null
     * then based on the {@link org.kuali.module.purap.bo.OrganizationParameter} associated with the given 'chart' and 'org' values.
     * 
     * @param vendorContractGeneratedIdentifier
     * @param chart chart code to use when looking up limit amount on {@link org.kuali.module.vendor.bo.VendorContract} and
     *        {@link org.kuali.module.purap.bo.OrganizationParameter}
     * @param org organization code to use when looking up limit amount on {@link org.kuali.module.vendor.bo.VendorContract} and
     *        {@link org.kuali.module.purap.bo.OrganizationParameter}
     * @return a KualiDecimal if a valid limit amount is found or null if one is not found
     */
    public KualiDecimal getApoLimit(Integer vendorContractGeneratedIdentifier, String chart, String org);

    /**
     * Determines if full entry mode has ended for this Purchasing/Accounts Payable document.
     * 
     * @param purapDocument PurchasingAccountsPayableDocument
     * @return a boolean to indicate if document has completed full entry mode
     */
    public boolean isFullDocumentEntryCompleted(PurchasingAccountsPayableDocument purapDocument);

    /**
     * Create a close or reopen purchase order document.
     * 
     * @param purapDocument PurchasingAccountsPayableDocument
     */
    public void performLogicForCloseReopenPO(PurchasingAccountsPayableDocument purapDocument);

    /**
     * Performs the given logic with the given fake user id.  Need this to control the user.
     * 
     * @param requiredUniversalUserPersonUserId
     * @param logicToRun
     * @param objects
     * @return
     * @throws UserNotFoundException
     * @throws WorkflowException
     * @throws Exception
     */
    public Object performLogicWithFakedUserSession(String requiredUniversalUserPersonUserId, LogicContainer logicToRun, Object... objects) throws UserNotFoundException, WorkflowException, Exception;

    /**
     * Sort the below the line elements of the given document
     * 
     * @param document  PurchasingAccountsPayableDocument to be sorted
     */
    public void sortBelowTheLine(PurchasingAccountsPayableDocument document);
    
    /**
     * Remove items that have not been "entered" which means no data has been added to them so no more processing needs to continue
     * on these items.
     * 
     * @param apDocument  AccountsPayableDocument which contains list of items to be reviewed
     */
    public void deleteUnenteredItems(PurapItemOperations document);
    
    /**
     * Saves the document without doing validation by invoking the saveDocument method of documentService.
     * 
     * @param document The purchase order document to be saved.
     */
    public void saveDocumentNoValidation(Document document);
    
    public PurchaseOrderService getPurchaseOrderService();
}
