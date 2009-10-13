/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.web;

import org.kuali.kfs.module.purap.businessobject.PurApAccountingLineBase;
import org.kuali.kfs.module.purap.businessobject.PurApItemBase;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.web.AccountingLineViewField;

/**
 * Represents a field (plus, optionally, a dynamic name field) to be rendered as part of an accounting line.
 */
public class PurapAccountingLineViewField extends AccountingLineViewField {

    /**
     * Overrides the method in AccountingLineViewField so that we could control when the inquiry link 
     * should be rendered and when it should be hidden.
     * 
     * @see org.kuali.kfs.sys.document.web.AccountingLineViewField#isRenderingInquiry(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    @Override
    protected boolean isRenderingInquiry(AccountingDocument document, AccountingLine line) {
        if (!((PurchasingAccountsPayableDocument)document).isInquiryRendered()) {
            return false;
        }
        return super.isRenderingInquiry(document, line);
    }
    
    /**
     * Overrides the method in AccountingLineViewField so that we could control when the
     * dynamic name label should be displayed and when it shouldn't be displayed.
     * 
     * @see org.kuali.kfs.sys.document.web.AccountingLineViewField#getDynamicNameLabelDisplayedValue(org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    @Override
    protected String getDynamicNameLabelDisplayedValue(AccountingLine accountingLine) {
        PurApAccountingLineBase purapLine = (PurApAccountingLineBase)accountingLine;
        PurApItemBase purapItem = purapLine.getPurapItem();
        PurchasingAccountsPayableDocumentBase purapDocument = null;
        
        if (purapItem != null) {
            purapDocument = purapItem.getPurapDocument();
        }
        if (purapItem == null || purapDocument == null || purapDocument.isInquiryRendered()) {
            return super.getDynamicNameLabelDisplayedValue(accountingLine);
        }
        else {
            return null;
        }
    }
}
