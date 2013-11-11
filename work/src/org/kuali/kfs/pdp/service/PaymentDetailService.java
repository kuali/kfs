/*
 * Copyright 2007 The Kuali Foundation
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
