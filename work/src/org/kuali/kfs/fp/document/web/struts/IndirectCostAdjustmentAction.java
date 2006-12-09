/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.module.financial.web.struts.action;

import java.util.List;

import org.kuali.core.bo.AccountingLine;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.core.web.uidraw.AccountingLineDecorator;

/**
 * This class handles Actions for <ocde>IndirectCostAdjustmentDocument</code>s
 * 
 * 
 */
public class IndirectCostAdjustmentAction extends KualiTransactionalDocumentActionBase {
    /**
     * added target line baseline creation for lines created by source add
     * 
     * @see org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase#insertAccountingLine(boolean,
     *      org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase, org.kuali.core.bo.AccountingLine)
     */
    @Override
    protected void insertAccountingLine(boolean isSource, KualiTransactionalDocumentFormBase transactionalDocumentForm, AccountingLine line) {
        super.insertAccountingLine(isSource, transactionalDocumentForm, line);
        if (isSource) {
            AccountingLineDecorator decorator = new AccountingLineDecorator();
            decorator.setRevertible(false);
            // add it to the baseline, to prevent generation of spurious update events
            TransactionalDocument tDoc = (TransactionalDocument) transactionalDocumentForm.getDocument();
            List targetLines = tDoc.getTargetAccountingLines();
            transactionalDocumentForm.getBaselineTargetAccountingLines().add(targetLines.get(targetLines.size() - 1));

            // add the decorator
            transactionalDocumentForm.getTargetLineDecorators().add(decorator);
        }
    }
}