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
package org.kuali.kfs.module.ar.document.service;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;

public interface PaymentApplicationDocumentService {
    public CashControlDocument getCashControlDocumentForPaymentApplicationDocumentNumber(String paymentApplicationDocumentNumber);
    public CashControlDocument getCashControlDocumentForPaymentApplicationDocument(PaymentApplicationDocument document);
    public KualiDecimal getTotalUnappliedFundsForPaymentApplicationDocument(String paymentApplicationDocumentNumber);
    public KualiDecimal getTotalCashControlForPaymentApplicationDocument(String paymentApplicationDocumentNumber);
    public KualiDecimal getTotalUnappliedFundsToBeAppliedForPaymentApplicationDocument(String paymentApplicationDocumentNumber);
    public KualiDecimal getTotalToBeAppliedForPaymentApplicationDocument(String paymentApplicationDocumentNumber);
    public KualiDecimal getTotalAppliedAmountForPaymentApplicationDocument(String paymentApplicationDocumentNumber);
}
