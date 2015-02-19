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
package org.kuali.kfs.fp.document.service;

import java.util.List;

import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * 
 * This service interface defines the methods that a DisbursementVoucherTaxService implementation must provide.
 * 
 * Handles queries and validation on tax id numbers.
 * 
 */
public interface DisbursementVoucherTaxService {

    /**
     * Returns the vendor id number whose tax number matches the number passed in, or null if no vendor is found.
     * 
     * @param taxIDNumber A vendor tax id number.
     * @param taxpayerTypeCode A vendor tax payer type code.
     * @return The id of the vendor found with a matching tax id number and payer type code, or null if no vendor is found.
     */
    public String getVendorId(String taxIDNumber, String taxpayerTypeCode);

    /**
     * Returns the employee id number whose tax number matches the number passed in, or null if no employee is found.
     * 
     * @param taxIDNumber A vendor tax id number.
     * @param taxpayerTypeCode A vendor tax payer type code.
     * @return The universal id of the employee found with a matching tax id number and payer type code, or null if no employee is found.
     */
    public String getUniversalId(String taxIDNumber, String taxpayerTypeCode);

    /**
     * Removes non-resident alien tax lines from the document's accounting lines and updates the check total.
     * 
     * @param document The disbursement voucher document being modified.
     */
    public void clearNRATaxLines(DisbursementVoucherDocument document);

    /**
     * Clears non-resident alien tax info.
     * 
     * @param document The disbursement voucher document being modified.
     */
    public void clearNRATaxInfo(DisbursementVoucherDocument document);
    
    /**
     * Generates new tax lines based on associated non-resident alien information, and debits the check total
     * 
     * @param document The disbursement voucher document being modified.
     */
    public void processNonResidentAlienTax(DisbursementVoucherDocument document);

    /**
     * Returns the non-resident alien accounting line tax amount (if any).
     * 
     * @param document The disbursement voucher being reviewed.
     * @return The total tax amount of the non-resident alien accounting lines for the given disbursement voucher document.
     */
    public KualiDecimal getNonResidentAlienTaxAmount(DisbursementVoucherDocument document);

    /**
     * Returns a list of Integers representing the non-resident alien tax line numbers parsed from the line string.
     * 
     * @param taxLineString The tax line representation as as string that will be parsed for the non-resident alien tax line numbers.
     * @return A collection of Integers representing the line numbers of non-resident alien tax lines.
     */
    public List getNRATaxLineNumbers(String taxLineString);
}
