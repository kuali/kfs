/*
 * Copyright 2005 The Kuali Foundation
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
package org.kuali.kfs.fp.service.impl;

import org.kuali.kfs.fp.businessobject.CashDetailTypeCode;
import org.kuali.kfs.fp.service.CashDetailTypeCodeService;

/**
 * This is the default implementation for the CashDetailTypeCodeService interface. This implementation used the KualiCodeService,
 * which in turn uses the KualiCodeDao for retrieving values from the database.
 */
public class CashDetailTypeCodeServiceImpl implements CashDetailTypeCodeService {
    // KFSConstants for doing the actual lookups
    public final static String CASH_RECEIPT_CHECK = "CRCHK";
    public final static String CASH_RECEIPT_COIN = "CRCOIN";

    /**
     * Constructs a CashDetailTypeCodeServiceImpl instance.
     */
    public CashDetailTypeCodeServiceImpl() {
    }


    /**
     * Gets the associated check type code for a CashReceipt.
     * 
     * @return Returns the CashReceipt check type code.
     * @see org.kuali.rice.krad.service.CashDetailTypeCode#getCashReceiptCheckTypeCode()
     */
    public CashDetailTypeCode getCashReceiptCheckTypeCode() {
        return getCashDetailTypeCodeByCode(CASH_RECEIPT_CHECK);
    }

    /**
     * Gets the associated coin type code for a CashReceipt.
     * 
     * @return Returns the CashReceipt coin type code.
     * @see org.kuali.rice.krad.service.CashDetailTypeCode#getCashReceiptCoinTypeCode()
     */
    public CashDetailTypeCode getCashReceiptCoinTypeCode() {
        return getCashDetailTypeCodeByCode(CASH_RECEIPT_CHECK);
    }

    /**
     * Retrieves a populated instance corresponding to the code passed into this method. This is retrieved via the KualiCodeService
     * and in turn from the database. 
     * 
     * TODO - uncomment the commented out line and remove the others when the table is in place
     * 
     * @param cashDetailTypeCode The identifier used to retrieve the appropriate type code object.
     * @return A CashDetailTypeCode instance based on the code provided.
     * 
     * @see #CASH_RECEIPT_CHECK
     * @see #CASH_RECEIPT_COIN
     */
    protected CashDetailTypeCode getCashDetailTypeCodeByCode(String cashDetailTypeCode) {
        // return (CashDetailTypeCode) kualiCodeService.getByCode(CashDetailTypeCode.class, cashDetailTypeCode);
        return getDummyInstance(cashDetailTypeCode);
    }

    /**
     * This method is a temporary helper method. This should be removed when the lookup table for CashDetailTypeCode business
     * objects is put in place. Then we'll be retrieving the stuff from the database. 
     * 
     * TODO - remove this method after the table is in place; this is a temp helper method
     * 
     * @param cashDetailTypeCodeCode The code to populate the dummy instance with.
     * @return A CashDetailTypeCode instance based on the value provided.
     */
    protected CashDetailTypeCode getDummyInstance(String cashDetailTypeCodeCode) {
        CashDetailTypeCode cashDetailTypeCode = new CashDetailTypeCode();
        cashDetailTypeCode.setCode(cashDetailTypeCodeCode);
        return cashDetailTypeCode;
    }
}
