/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.fp.document.service;

import org.kuali.kfs.fp.businessobject.DisbursementPayee;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;

/**
 * define a set of service methods related to disbursement payee
 */
public interface DisbursementVoucherPayeeService {

    /**
     * find the payee type description corresponding to the given payee type code
     * 
     * @param payeeTypeCode the given payee type code
     * @return the payee type description corresponding to the given payee type code
     */
    public String getPayeeTypeDescription(String payeeTypeCode);

    /**
     * determine whether the given payee is an employee
     * 
     * @param dvPayeeDetail the given payee
     * @return true if the given payee is an employee; otherwise, false
     */
    public boolean isEmployee(DisbursementVoucherPayeeDetail dvPayeeDetail);

    /**
     * determine whether the given payee is an employee
     * 
     * @param payee the given payee
     * @return true if the given payee is an employee; otherwise, false
     */
    public boolean isEmployee(DisbursementPayee payee);

    /**
     * determine whether the given payee is a vendor
     * 
     * @param dvPayeeDetail the given payee
     * @return true if the given payee is a vendor; otherwise, false
     */
    public boolean isVendor(DisbursementVoucherPayeeDetail dvPayeeDetail);

    /**
     * determine whether the given payee is a vendor
     * 
     * @param payee the given payee
     * @return true if the given payee is a vendor; otherwise, false
     */
    public boolean isVendor(DisbursementPayee payee);

    /**
     * determine whether the given payee is an individual vendor
     * 
     * @param dvPayeeDetail the given payee
     * @return true if the given payee is an individual vendor; otherwise, false
     */
    public boolean isPayeeIndividualVendor(DisbursementVoucherPayeeDetail dvPayeeDetail);
    
    /**
     * determine whether the given payee is required for tax review
     * 
     * @param String the given payee tax control code
     * @return true if the given payee is required for tax review; otherwise, false
     */
    public boolean isTaxReviewRequired(String payeeTaxControlCode);

    /**
     * determine whether the given payee is an individual vendor
     * 
     * @param payee the given payee
     * @return true if the given payee is an individual vendor; otherwise, false
     */
    public boolean isPayeeIndividualVendor(DisbursementPayee payee);

    public void checkPayeeAddressForChanges(DisbursementVoucherDocument dvDoc);
    
    /**
     * get the ownership type code if the given payee is a vendor
     * @param payee the given payee
     * @return the ownership type code if the given payee is a vendor; otherwise, return null
     */
    public String getVendorOwnershipTypeCode(DisbursementPayee payee);
}
