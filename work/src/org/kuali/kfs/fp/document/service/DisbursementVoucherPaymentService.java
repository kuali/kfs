/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.fp.document.service;

import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;

/**
 * Service methods which support PDP processing on DisbursementVoucher documents
 */
public interface DisbursementVoucherPaymentService {
    /**
     * Cancels a DisbursementVoucher document payment
     *
     * @param dv the disbursement voucher to cancel
     * @param cancelDate the date of cancellation for the document
     */
    public abstract void cancelDisbursementVoucher(DisbursementVoucherDocument dv, java.sql.Date cancelDate);

    /**
     * Creates a PDP PaymentGroup for a given DisbursementVoucher
     * @param dv the DisbursementVoucher to pay out
     * @param processRunDate the date when the payment was processed
     * @return a PDP payment group to pay out the given DisbursementVoucher
     */
    public abstract PaymentGroup createPaymentGroupForDisbursementVoucher(DisbursementVoucherDocument dv, java.sql.Date processRunDate);
}
