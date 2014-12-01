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

