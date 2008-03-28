/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.cams.rules;

import org.kuali.core.document.Document;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.kfs.rules.GeneralLedgerPostingDocumentRuleBase;

public class AssetTransferDocumentRule extends GeneralLedgerPostingDocumentRuleBase {
    public static final String DOCUMENT_NUMBER_PATH = "documentNumber";
    public static final String DOCUMENT_PATH = "document";

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        return true;
    }

    @Override
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        return true;
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        return true;
    }

}
