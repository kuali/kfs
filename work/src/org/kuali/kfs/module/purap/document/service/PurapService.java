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
import java.util.Map;

import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.module.purap.bo.ItemType;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;

import edu.iu.uis.eden.exception.WorkflowException;

public interface PurapService {

    public boolean updateStatusAndStatusHistory( PurchasingAccountsPayableDocument document, String statusToSet);
        
    public boolean updateStatus( PurchasingAccountsPayableDocument document, String statusToSet);
    
    public boolean updateStatusHistory(PurchasingAccountsPayableDocument document, String statusToSet);
    
    public List getRelatedViews(Class clazz, Integer accountsPayablePurchasingDocumentLinkIdentifier);

    public void addBelowLineItems(PurchasingAccountsPayableDocument document);
    
    public String[] getBelowTheLineForDocument(PurchasingAccountsPayableDocument document);
    /**
     * 
     * This method gets the below the line item for a doc by item type (unknown result if multilple of same below the line item type)
     * @param document the document
     * @param iT the itemType
     * @return below the line item by item type 
     */
    public PurApItem getBelowTheLineByType(PurchasingAccountsPayableDocument document, ItemType iT);
    
    /**
     * A method to determine whether a given date is in the past.
     * 
     * @param compareDate   An SQL date (not a DateFormatter date, or a util Date)
     * @return  True if the given date is before today.
     */
    public boolean isDateInPast(Date compareDate);
    
    /**
     * A method to determine whether a given date is more than a given number of days
     * away from the current date.
     * 
     * @param compareDate   An SQL date (not a DateFormatter date, or a util Date)
     * @param daysAway      An int, positive for future days, negative for past days
     * @return   True if the given date is more than the given number of days away in either direction.
     */
    public boolean isDateMoreThanANumberOfDaysAway(Date compareDate, int daysAway);
    
    /**
     * We are obliged not to simply use a dateDiff and compare the result to 365, because we have
     * to worry about leap years.
     * 
     * @param compareDate   An SQL date (not a DateFormatter date, or a util Date)
     * @return  True if the date given for comparison is more than a year in the past, not including today.
     */
    public boolean isDateAYearBeforeToday(Date compareDate);
    
    /*
     *    PURCHASING DOCUMENT METHODS
     * 
     */

    /**
     * This method gets the Automatic Purchase Order Limit amount based first on the derived contract limit
     * (see {@link org.kuali.module.vendor.service.VendorService#getApoLimitFromContract(Integer, String, String)})
     * and if that is null then based on the {@link org.kuali.module.purap.bo.OrganizationParameter} associated
     * with the given 'chart' and 'org' values.
     * 
     * @param vendorContractGeneratedIdentifier
     * @param chart chart code to use when looking up limit amount on {@link org.kuali.module.vendor.bo.VendorContract} 
     * and {@link org.kuali.module.purap.bo.OrganizationParameter}
     * @param org organization code to use when looking up limit amount on {@link org.kuali.module.vendor.bo.VendorContract} 
     * and {@link org.kuali.module.purap.bo.OrganizationParameter}
     * @return a KualiDecimal if a valid limit amount is found or null if one is not found
     */
    public KualiDecimal getApoLimit(Integer vendorContractGeneratedIdentifier, String chart, String org);

    /**
     * This method returns a list of fiscal years that can be selected from on the document (built for Requisition and Purchase Order).  Typically
     * only the current year is returned.  However, if the current date falls within the allowed range to encumber in the next fiscal year, the 
     * current year and the next current year is returned.
     * 
     * @return List<Integer>
     */
    public List<Integer> getAllowedFiscalYears();
    
    /**
     * 
     * This method returns true if full entry mode has ended for this Payment Request
     * @param preqDocument
     * @return a boolean
     */
    public boolean isFullDocumentEntryCompleted(PurchasingAccountsPayableDocument purapDocument);

    /**
     * 
     * This method performs all the actions on an update document
     * @param purapDocument
     */
    public void performLogicForFullEntryCompleted(PurchasingAccountsPayableDocument purapDocument);
    
    public Object performLogicWithFakedUserSession(String requiredUniversalUserPersonUserId, LogicToRunAsFakeUser logicToRun, Object... objects) throws UserNotFoundException, WorkflowException, Exception;
    
    public abstract interface LogicToRunAsFakeUser { public abstract Object runLogic(Object[] objects) throws Exception; }
}
