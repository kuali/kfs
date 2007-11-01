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


public interface CashReceiptService {
    /**
     * @param campusCode
     * @return cash receipt verificationUnit workgroupName associated with the given campusCode
     */
    public String getCashReceiptVerificationUnitForCampusCode(String campusCode);

    /**
     * @param unitName
     * @return campusCode associated with the given cashReceipt verificationUnit
     */
    public String getCampusCodeForCashReceiptVerificationUnit(String unitName);

    /**
     * Returns null if the user is not a member of any verification unit workgroup TODO: change this to do something other than
     * return null (which will require updating CashReceiptDocumentAuthorizer, since that's the one place I'm sure that returning a
     * null is interpreted to mean that a user is a member of no verificationUnit)
     * 
     * @param user
     * @return cash receipt verificationUnit workgroupName associated with the given user
     */
    public String getCashReceiptVerificationUnitForUser(UniversalUser user);


    /**
     * Returns a List of CashReceiptDocuments for the given verificationUnit whose status matches the given status code
     * 
     * @param verificationUnit
     * @param statusCode
     * @return List of CashReceiptDocument instance
     * @throws IllegalArgumentException if verificationUnit is blank
     * @throws IllegalArgumentException if statusCode is blank
     */
    public List getCashReceipts(String verificationUnit, String statusCode);

    /**
     * Returns a List of CashReceiptDocuments for the given verificationUnit whose status matches any of the status codes in the
     * given String[].
     * 
     * @param verificationUnit
     * @param statii
     * @return List of CashReceiptDocument instance
     * @throws IllegalArgumentException if verificationUnit is blank
     * @throws IllegalArgumentException if statii is null or empty or contains any blank statusCodes
     */
    public List getCashReceipts(String verificationUnit, String[] statii);

    /**
     * This adds the currency and coin details associated with this Cash Receipt document to the proper cash drawer and to the
     * cumulative Cash Receipt details for the document which opened the cash drawer.
     * 
     * @param crDoc the cash receipt document with cash details to add to the cash drawer
     */
    public void addCashDetailsToCashDrawer(CashReceiptDocument crDoc);
}
