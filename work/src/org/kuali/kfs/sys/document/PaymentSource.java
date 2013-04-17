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

import org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail;
import org.kuali.kfs.sys.businessobject.PaymentSourceWireTransfer;

/**
 * Information needed by PDP to pay out from a given document
 */
public interface PaymentSource {
    /**
     * @return the wire transfer associated with this payment source
     */
    public abstract PaymentSourceWireTransfer getWireTransfer();

    public abstract boolean isDisbVchrAttachmentCode();

    public abstract String getDisbVchrPaymentMethodCode();

    public abstract DisbursementVoucherPayeeDetail getDvPayeeDetail();
}
