/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.service;

import java.util.List;

import org.kuali.core.bo.user.KualiUser;


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
     * Returns null if the user is not a member of any verification unit workgroup
     * 
     * TODO: change this to do something other than return null (which will require updating CashReceiptDocumentAuthorizer, since
     * that's the one place I'm sure that returning a null is interpreted to mean that a user is a member of no verificationUnit)
     * 
     * @param user
     * @return cash receipt verificationUnit workgroupName associated with the given user
     */
    public String getCashReceiptVerificationUnitForUser(KualiUser user);


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
}
