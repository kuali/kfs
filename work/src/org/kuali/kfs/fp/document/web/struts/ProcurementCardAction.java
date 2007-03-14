/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.TypedArrayList;
import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.TargetAccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rule.event.AddAccountingLineEvent;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase;
import org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase;
import org.kuali.kfs.web.ui.AccountingLineDecorator;
import org.kuali.module.financial.bo.ProcurementCardTargetAccountingLine;
import org.kuali.module.financial.document.ProcurementCardDocument;
import org.kuali.module.financial.web.struts.form.ProcurementCardForm;

/**
 * This class handles specific Actions requests for the ProcurementCard.
 */
public class ProcurementCardAction extends KualiAccountingDocumentActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardAction.class);

    /**
     * Override to accomodate multiple target lines.
     * 
     * @param transForm
     */
    @Override
    protected void processAccountingLineOverrides(KualiAccountingDocumentFormBase transForm) {
        ProcurementCardForm procurementCardForm = (ProcurementCardForm) transForm;

        processAccountingLineOverrides(procurementCardForm.getNewSourceLine());
        processAccountingLineOverrides(procurementCardForm.getNewTargetLines());
        if (procurementCardForm.hasDocumentId()) {
            AccountingDocument financialDocument = (AccountingDocument) procurementCardForm.getDocument();

            processAccountingLineOverrides(financialDocument.getSourceAccountingLines());
            processAccountingLineOverrides(financialDocument.getTargetAccountingLines());
        }
    }

    /**
     * Override to add the new accounting line to the correct transaction
     * @see org.kuali.module.financial.web.struts.action.KualiFinancialDocumentActionBase#insertTargetLine(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward insertTargetLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProcurementCardForm procurementCardForm = (ProcurementCardForm) form;

        // get index of new target line
        int newTargetIndex = super.getSelectedLine(request);

        ProcurementCardTargetAccountingLine line = (ProcurementCardTargetAccountingLine) procurementCardForm.getNewTargetLines().get(newTargetIndex);

        // check any business rules
        boolean rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(new AddAccountingLineEvent(Constants.NEW_TARGET_ACCT_LINES_PROPERTY_NAME + "[" + Integer.toString(newTargetIndex) + "]", procurementCardForm.getDocument(), (AccountingLine) line));

        if (rulePassed) {
            // add accountingLine
            SpringServiceLocator.getPersistenceService().retrieveNonKeyFields(line);
            insertAccountingLine(false, procurementCardForm, line);

            // clear the used newTargetIndex
            procurementCardForm.getNewTargetLines().set(newTargetIndex, new ProcurementCardTargetAccountingLine());
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }


    /**
     * Override to resync base accounting lines. New lines on the PCDO document can be inserted anywhere in the list, not necessary
     * at the end.
     * @see org.kuali.module.financial.web.struts.action.KualiFinancialDocumentActionBase#insertAccountingLine(boolean, org.kuali.module.financial.web.struts.form.KualiFinancialDocumentFormBase, org.kuali.core.bo.AccountingLine)
     */
    @Override
    protected void insertAccountingLine(boolean isSource, KualiAccountingDocumentFormBase financialDocumentForm, AccountingLine line) {
        AccountingDocument tdoc = financialDocumentForm.getFinancialDocument();

        // create and init a decorator
        AccountingLineDecorator decorator = new AccountingLineDecorator();
        decorator.setRevertible(false);

        // add it to the document
        tdoc.addTargetAccountingLine((TargetAccountingLine) line);

        // get the index of the inserted line
        int newLineIndex = tdoc.getTargetAccountingLines().indexOf(line);

        // add it to the baseline, to prevent generation of spurious update events
        financialDocumentForm.getBaselineTargetAccountingLines().add(newLineIndex, line);

        // add the decorator
        financialDocumentForm.getTargetLineDecorators().add(newLineIndex, decorator);
    }

    /**
     * Override to remove the accounting line from the correct transaction
     * @see org.kuali.module.financial.web.struts.action.KualiFinancialDocumentActionBase#deleteAccountingLine(boolean, org.kuali.module.financial.web.struts.form.KualiFinancialDocumentFormBase, int)
     */
    @Override
    protected void deleteAccountingLine(boolean isSource, KualiAccountingDocumentFormBase financialDocumentForm, int deleteIndex) {
        ProcurementCardDocument procurementCardDocument = (ProcurementCardDocument) financialDocumentForm.getDocument();
        procurementCardDocument.removeTargetAccountingLine(deleteIndex);

        // remove baseline duplicate and decorator
        financialDocumentForm.getBaselineTargetAccountingLines().remove(deleteIndex);
        financialDocumentForm.getTargetLineDecorators().remove(deleteIndex);
    }

    /**
     * Ensures that ProcurementCardForm.newTargetLines is cleared. Otherwise works like super.reload.
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#reload(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward reload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProcurementCardForm procurementCardForm = (ProcurementCardForm) form;
        procurementCardForm.setNewTargetLines(new TypedArrayList(ProcurementCardTargetAccountingLine.class));

        return super.reload(mapping, procurementCardForm, request, response);
    }
}