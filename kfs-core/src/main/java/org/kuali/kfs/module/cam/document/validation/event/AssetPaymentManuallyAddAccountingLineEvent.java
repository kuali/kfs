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
package org.kuali.kfs.module.cam.document.validation.event;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.rice.krad.document.Document;

public class AssetPaymentManuallyAddAccountingLineEvent extends AttributedDocumentEventBase {
    private final AccountingLine accountingLine;

    /**
     * Constructs an AssetPaymentManuallyAddAccountingLineEvent with the given errorPathPrefix, document, and accountingLine.
     * 
     * @param errorPathPrefix
     * @param document
     * @param accountingLine
     */
    public AssetPaymentManuallyAddAccountingLineEvent(String errorPathPrefix, Document document, AccountingLine accountingLine) {
        super("adding asset payment asset detail to asset payment document " + getDocumentId(document), errorPathPrefix, document);
        this.accountingLine = accountingLine;
    }


    public AccountingLine getAccountingLine() {
        return accountingLine;
    }
}
