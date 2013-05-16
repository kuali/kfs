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
package org.kuali.kfs.sys.document;

import java.sql.Date;

import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.PaymentSourceWireTransfer;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Information needed by PDP to pay out from a given document
 */
public interface PaymentSource extends GeneralLedgerPostingDocument, GeneralLedgerPendingEntrySource {
    /**
     * @return the wire transfer associated with this payment source
     */
    public abstract PaymentSourceWireTransfer getWireTransfer();

    /**
     * @return a PDP PaymentGroup which would act to pay out this payment
     */
    public abstract PaymentGroup generatePaymentGroup(Date processRunDate);

    /**
     * Marks the payment source as extracted upon the extraction date
     * @param extractionDate the date when this payment source was extracted
     */
    public abstract void markAsExtracted(java.sql.Date extractionDate);

    /**
     * Marks the payment source as paid upon the processing date
     * @param processDate the date when this payment source was paid
     */
    public abstract void markAsPaid(java.sql.Date processDate);

    /**
     * Marks the payment source as canceled upon the passed-in canceled date
     * @param cancelDate the date when the payment source was canceled
     */
    public abstract void cancelPayment(java.sql.Date cancelDate);

    /**
     * Resets the given PaymentSource so that it seems as if it was not extracted according to values on the document
     */
    public abstract void resetFromExtraction();

    /**
     * @return the date when the PaymentSource was canceled, nor null if it is not canceled at all.
     */
    public abstract java.sql.Date getCancelDate();

    /**
     * @return true if this payment has an attachment with it (which would prevent it from being used as part of a wire transfer)
     */
    public abstract boolean hasAttachment();

    /**
     * @return the method to pay out this payment
     */
    public abstract String getPaymentMethodCode();

    /**
     * @return the amount of this payment
     */
    public abstract KualiDecimal getPaymentAmount();

    /**
     * @return the code identifier of the campus most associated with this campus
     */
    public abstract String getCampusCode();

    /**
     * @return the bank associated with this document
     */
    public abstract Bank getBank();

}
