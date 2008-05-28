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
package org.kuali.module.financial.service;

import java.util.List;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.module.financial.document.CashReceiptDocument;

/**
 * 
 * This service interface defines the methods that a CashReceiptService implementation must provide.
 */
public interface CashReceiptService {
    
    /**
     * This method retrieves a cash receipt verification unit associated with the given campus code.
     *
     * @param campusCode The campus code to use to look up the cash receipt verification unit.
     * @return Cash receipt verification unit workgroupName associated with the given campusCode.
     */
    public String getCashReceiptVerificationUnitForCampusCode(String campusCode);

    /**
     * This method retrieves a campus code associated with the given cash receipt verification unit.
     * 
     * @param unitName The cash receipt verification unit name to use to look up the campus code.
     * @return The campusCode associated with the given cash receipt verification unit.
     */
    public String getCampusCodeForCashReceiptVerificationUnit(String unitName);

    /**
     * This method retrieves the cash receipt verification unit for the given user.
     * 
     * TODO: change this to do something other than return null (which will require updating CashReceiptDocumentAuthorizer, since
     * that's the one place I'm sure that returning a null is interpreted to mean that a user is a member of no verificationUnit)
     * 
     * @param user The user to retrieve the cash receipt verification unit for.
     * @return Cash receipt verificationUnit workgroupName associated with the given user; null if the user is not a member of any verification unit workgroup.
     */
    public String getCashReceiptVerificationUnitForUser(UniversalUser user);

    /**
     * Returns a List of CashReceiptDocuments for the given verification unit whose status matches the given status code.
     * 
     * @param verificationUnit A verification unit for a cash receipt.
     * @param statusCode A cash receipt status code.
     * @return List of CashReceiptDocument instances.
     * @throws IllegalArgumentException Thrown if verificationUnit is blank
     * @throws IllegalArgumentException Thrown if statusCode is blank
     */
    public List getCashReceipts(String verificationUnit, String statusCode);

    /**
     * Returns a List of CashReceiptDocuments for the given verificationUnit whose status matches any of the status codes in the
     * given String[].
     * 
     * @param verificationUnit A verification unit for a cash receipt.
     * @param statii A collection of potential cash receipt document statuses.
     * @return List of CashReceiptDocument instances.
     * @throws IllegalArgumentException Thrown if verificationUnit is blank
     * @throws IllegalArgumentException Thrown if statii is null or empty or contains any blank statusCodes
     */
    public List getCashReceipts(String verificationUnit, String[] statii);
    
    /**
     * This adds the currency and coin details associated with this Cash Receipt document to the proper cash drawer and to the
     * cumulative Cash Receipt details for the document which opened the cash drawer.
     * 
     * @param crDoc The cash receipt document with cash details to add to the cash drawer.
     */
    public void addCashDetailsToCashDrawer(CashReceiptDocument crDoc);
    
    /**
     * Checks whether the CashReceiptDocument's cash totals are invalid, generating global errors if so.
     * 
     * @param cashReceiptDocument submitted cash receipt document
     * @return true if CashReceiptDocument's cash totals are valid
     */
    public abstract boolean areCashTotalsInvalid(CashReceiptDocument cashReceiptDocument);
}
