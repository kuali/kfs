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
