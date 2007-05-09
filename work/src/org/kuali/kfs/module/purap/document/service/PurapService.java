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

import java.util.List;

import org.kuali.core.bo.Note;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.bo.CreditMemoView;
import org.kuali.module.purap.bo.PaymentRequestView;
import org.kuali.module.purap.bo.PurchaseOrderView;
import org.kuali.module.purap.bo.RequisitionView;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;

public interface PurapService {

    public boolean updateStatusAndStatusHistory( PurchasingAccountsPayableDocument document, String statusToSet);
    
    public boolean updateStatusAndStatusHistory( PurchasingAccountsPayableDocument document, 
            String statusToSet, Note statusHistoryNote );
    
    public boolean updateStatus( PurchasingAccountsPayableDocument document, String statusToSet);
    
    public boolean updateStatusHistory( PurchasingAccountsPayableDocument document, String statusToSet);
    
    public boolean updateStatusHistory( PurchasingAccountsPayableDocument document,
            String statusToSet, Note statusHistoryNote );
    
    public List getRelatedViews(Class clazz, Integer accountsPayablePurchasingDocumentLinkIdentifier);

    public void addBelowLineItems(PurchasingAccountsPayableDocument document);
    
    public String[] getBelowTheLineForDocument(PurchasingAccountsPayableDocument document);
}
