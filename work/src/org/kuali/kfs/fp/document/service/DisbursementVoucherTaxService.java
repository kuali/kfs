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

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.financial.document.DisbursementVoucherDocument;

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
     * Returns the pending payee id number whose tax number matches the number passed in, or null if no payee is found.
     * 
     * @param taxIDNumber A pending payee tax id number.
     * @param taxpayerTypeCode A pending payee tax payer type code.
     * @return The id of the pending payee found with a matching tax id number and payer type code, or null if no payee is found.
     */
    public String getPendingPayeeId(String taxIDNumber, String taxpayerTypeCode);

    /**
     * Returns the payee id number whose tax number matches the number passed in, or null if no payee is found.
     * 
     * @param taxIDNumber A payee tax id number.
     * @param taxpayerTypeCode A payee tax payer type code.
     * @return The id of the payee found with a matching tax id number and payer type code, or null if no payee is found.
     */
    public String getPayeeId(String taxIDNumber, String taxpayerTypeCode);

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