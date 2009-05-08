/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.authorization;

import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Accounting line authorizer for Requisition document which allows adding accounting lines at specified nodes
 */
public class PurchaseOrderAccountingLineAuthorizer extends PurapAccountingLineAuthorizer {
    private static final String NEW_UNORDERED_ITEMS_NODE = "NewUnorderedItems";

    /**
     * Allow new lines to be rendered at NewUnorderedItems node
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#renderNewLine(org.kuali.kfs.sys.document.AccountingDocument, java.lang.String)
     */
    @Override
    public boolean renderNewLine(AccountingDocument accountingDocument, String accountingGroupProperty) {
        if (accountingDocument.getDocumentHeader().getWorkflowDocument().getCurrentRouteNodeNames().equals(PurchaseOrderAccountingLineAuthorizer.NEW_UNORDERED_ITEMS_NODE)) return true;
        return super.renderNewLine(accountingDocument, accountingGroupProperty);
    }
    
    @Override
    protected boolean allowAccountingLinesAreEditable(AccountingDocument accountingDocument,
            AccountingLine accountingLine){
        PurApAccountingLine purapAccount = (PurApAccountingLine)accountingLine;
        PurchaseOrderItem poItem = (PurchaseOrderItem)purapAccount.getPurapItem();
        if (poItem.getItemInvoicedTotalAmount().compareTo(new KualiDecimal(0)) != 0 ) {
            return false;
        }
        else {
            return super.allowAccountingLinesAreEditable(accountingDocument, accountingLine);
        }
    }
}
