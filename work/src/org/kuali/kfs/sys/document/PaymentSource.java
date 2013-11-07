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

import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.PaymentSourceWireTransfer;

/**
 * Information needed by PDP to pay out from a given document
 */
public interface PaymentSource extends GeneralLedgerPostingDocument, GeneralLedgerPendingEntrySource {
    /**
     * @return the wire transfer associated with this payment source
     */
    public abstract PaymentSourceWireTransfer getWireTransfer();

    /**
     * @return true if this payment has an attachment with it (which would prevent it from being used as part of a wire transfer)
     */
    public abstract boolean hasAttachment();

    /**
     * @return the method to pay out this payment
     */
    public abstract String getPaymentMethodCode();

    /**
     * @return the code identifier of the campus most associated with this campus
     */
    public abstract String getCampusCode();

    /**
     * @return the bank associated with this document
     */
    public abstract Bank getBank();
}
