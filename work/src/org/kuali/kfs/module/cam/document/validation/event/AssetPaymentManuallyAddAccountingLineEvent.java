/*
 * Copyright 2008-2009 The Kuali Foundation
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
