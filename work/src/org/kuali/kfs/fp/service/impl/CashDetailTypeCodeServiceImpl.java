/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
