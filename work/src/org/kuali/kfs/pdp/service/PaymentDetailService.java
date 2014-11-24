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
package org.kuali.kfs.pdp.service;

import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.pdp.businessobject.ExtractionUnit;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;

public interface PaymentDetailService {

    /**
     * Return an iterator of all the payment details for a specific disbursement number
     *
     * @param disbursementNumber
     * @return
     */
    public Iterator getByDisbursementNumber(Integer disbursementNumber);

    /**
     * This will return an iterator of all the cancelled payment details that haven't already been processed
     *
     * @param extractionUnits a List of ExtractionUnit objects to represent each of the unit/sub-unit combinations to get PaymentDetails for
     * @return
     */
    public Iterator getUnprocessedCancelledDetails(List<ExtractionUnit> extractionUnits);

    /**
     * This will return an iterator of all the paid payment details that haven't already been processed
     *
     * @param extractionUnits a List of ExtractionUnit objects to represent each of the unit/sub-unit combinations to get PaymentDetails for
     * @return
     */
    public Iterator getUnprocessedPaidDetails(List<ExtractionUnit> extractionUnits);

    /**
     * Returns all PaymentDetail records with the given disbursement number and a group with the given process id, disbursement type, and bank code
     * @param disbursementNumber the disbursement number of the payment details to find
     * @param processId the process id of the payment group of payment details to find
     * @param disbursementType the disbursement type of the payment group of payment details to find
     * @param bankCode the bank code of the payment group of payment details to find
     * @return an iterator of PaymentDetail records matching the given criteria
     */
    public abstract Iterator<PaymentDetail> getByDisbursementNumber(Integer disbursementNumber, Integer processId, String disbursementType, String bankCode);
}
