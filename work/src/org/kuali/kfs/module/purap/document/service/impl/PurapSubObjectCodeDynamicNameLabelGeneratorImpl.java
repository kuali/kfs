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
package org.kuali.kfs.module.purap.document.service.impl;

import org.kuali.kfs.module.purap.businessobject.PurApAccountingLineBase;
import org.kuali.kfs.module.purap.businessobject.PurApItemBase;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.service.impl.SubObjectCodeDynamicNameLabelGeneratorImpl;

public class PurapSubObjectCodeDynamicNameLabelGeneratorImpl extends SubObjectCodeDynamicNameLabelGeneratorImpl {
    /**
     * Overrides the method in SubObjectCodeDynamicNameLabelGeneratorImpl so that we could control whether
     * to display or hide the dynamic name label in certain conditions in purap documents.
     * 
     * @see org.kuali.kfs.sys.document.service.DynamicNameLabelGenerator#getDynamicNameLabelFieldName(org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String)
     */
    @Override
    public String getDynamicNameLabelValue(AccountingLine line, String accountingLineProperty) {
        PurApAccountingLineBase purapLine = (PurApAccountingLineBase)line;
        PurApItemBase purapItem = purapLine.getPurapItem();
        PurchasingAccountsPayableDocumentBase purapDocument = null;
        if (purapItem != null) {
            purapDocument = purapItem.getPurapDocument();
        }
        if (purapItem == null || purapDocument == null || purapDocument.isInquiryRendered()) {
            return super.getDynamicNameLabelValue(line, accountingLineProperty);       
        }
        else {
            return null;
        }
    }

}
