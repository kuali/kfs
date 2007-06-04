/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.financial.document.authorization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kuali.module.chart.bo.ChartUser;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.document.authorization.AccountingDocumentAuthorizerBase;

/**
 * JournalVoucher-specific DocumentAuthorizer
 * 
 * 
 */
public class JournalVoucherDocumentAuthorizer extends AccountingDocumentAuthorizerBase {
    private static Log LOG = LogFactory.getLog(JournalVoucherDocumentAuthorizer.class);

    /**
     * No accountingLines will ever be editable, since the only two possible editModes don't check editableAccounts
     * 
     * @see org.kuali.core.authorization.TransactionalDocumentAuthorizer#getEditableAccounts(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.user.KualiUser)
     */
    public Map getEditableAccounts(TransactionalDocument document, ChartUser user) {
        return new HashMap();
    }

    /**
     * No accountingLines will ever be editable, since the only two possible editModes don't check editableAccounts
     * 
     * @see org.kuali.kfs.document.authorization.AccountingDocumentAuthorizerBase#getEditableAccounts(java.util.List, org.kuali.module.chart.bo.ChartUser)
     */
    @Override
    public Map getEditableAccounts(List<AccountingLine> lines, ChartUser user) {
        return new HashMap();
    }

}
