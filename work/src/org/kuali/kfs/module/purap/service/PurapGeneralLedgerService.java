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
package org.kuali.module.purap.service;

import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;


public interface PurapGeneralLedgerService {

    public void customizeGeneralLedgerPendingEntry(PurchasingAccountsPayableDocument purapDocument, AccountingLine accountingLine, 
            GeneralLedgerPendingEntry explicitEntry, Integer referenceDocumentNumber, String debitCreditCode,
            String docType, boolean isEncumbrance);

    public void generateEntriesCreatePreq(PaymentRequestDocument preq);

    public void generateEntriesModifyPreq(PaymentRequestDocument preq);

    public void generateEntriesCreditMemo(CreditMemoDocument cm, boolean isCancel);

    public void generateEntriesModifyCm(CreditMemoDocument cm);

    public void generateEntriesApproveAmendPo(PurchaseOrderDocument po);

}
