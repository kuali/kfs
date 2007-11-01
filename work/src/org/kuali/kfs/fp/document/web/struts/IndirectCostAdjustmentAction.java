/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase;
import org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase;
import org.kuali.kfs.web.ui.AccountingLineDecorator;

/**
 * This class handles Actions for <ocde>IndirectCostAdjustmentDocument</code>s
 */
public class IndirectCostAdjustmentAction extends KualiAccountingDocumentActionBase {

    /**
     * added target line baseline creation for lines created by source add
     * 
     * @see org.kuali.module.financial.web.struts.action.KualiFinancialDocumentActionBase#insertAccountingLine(boolean,
     *      org.kuali.module.financial.web.struts.form.KualiFinancialDocumentFormBase, org.kuali.core.bo.AccountingLine)
     */
    @Override
    protected void insertAccountingLine(boolean isSource, KualiAccountingDocumentFormBase financialDocumentForm, AccountingLine line) {
        super.insertAccountingLine(isSource, financialDocumentForm, line);
        if (isSource) {
            AccountingLineDecorator decorator = new AccountingLineDecorator();
            decorator.setRevertible(false);

            // add it to the baseline, to prevent generation of spurious update events
            AccountingDocument tDoc = (AccountingDocument) financialDocumentForm.getDocument();
            List targetLines = tDoc.getTargetAccountingLines();
            financialDocumentForm.getBaselineTargetAccountingLines().add(targetLines.get(targetLines.size() - 1));

            // add the decorator
            financialDocumentForm.getTargetLineDecorators().add(decorator);
        }
    }
}