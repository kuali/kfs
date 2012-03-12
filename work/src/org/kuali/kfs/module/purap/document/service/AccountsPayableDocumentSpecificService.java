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
package org.kuali.kfs.module.purap.document.service;

import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.rice.kim.api.identity.Person;

/**
 * This interface is a non spring managed interface that is implemented by both PaymentRequestService and CreditMemoService
 */
public interface AccountsPayableDocumentSpecificService {
    
    public boolean shouldPurchaseOrderBeReversed(AccountsPayableDocument apDoc);

    public void takePurchaseOrderCancelAction(AccountsPayableDocument apDoc);

    public Person getPersonForCancel(AccountsPayableDocument apDoc);

    public String updateStatusByNode(String currentNodeName, AccountsPayableDocument apDoc);

    public boolean poItemEligibleForAp(AccountsPayableDocument apDoc, PurchaseOrderItem poi);
    
    /**
     * Generates the general ledger entries that need to be created by an AccountsPayableDocument
     * of the specific type of the given AP document.
     * 
     * @param apDoc     An AccountsPayableDocument
     */
    public void generateGLEntriesCreateAccountsPayableDocument(AccountsPayableDocument apDoc);
}

