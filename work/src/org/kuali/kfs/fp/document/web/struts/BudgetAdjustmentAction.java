/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.document.web.struts;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.fp.document.BudgetAdjustmentDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.AccountingLineOverride;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.AccountingLineOverride.COMPONENT;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.service.PersistenceService;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * This class handles specific Actions requests for the BudgetAdjustment.
 */
public class BudgetAdjustmentAction extends KualiAccountingDocumentActionBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetAdjustmentAction.class);

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ActionForward forward = super.execute(mapping, form, request, response);

        // after any accounting line overrides - refresh the total for any amount changes
        BudgetAdjustmentForm baForm = (BudgetAdjustmentForm) form;
        if (baForm.hasDocumentId() && baForm.getDocumentActions().containsKey(KRADConstants.KUALI_ACTION_CAN_EDIT)){
            BudgetAdjustmentDocument baDoc = (BudgetAdjustmentDocument) baForm.getDocument();
            ((FinancialSystemDocumentHeader) baDoc.getDocumentHeader()).setFinancialDocumentTotalAmount(((AmountTotaling) baDoc).getTotalDollarAmount());
        }

        return forward;
    }

    /**
     * Do initialization for a new budget adjustment
     *
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        ((BudgetAdjustmentDocument) kualiDocumentFormBase.getDocument()).initiateDocument();

    }

    /**
     * Add warning message about copying a document with generated labor benefits.
     *
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#copy(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward copy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KNSGlobalVariables.getMessageList().add(KFSKeyConstants.WARNING_DOCUMENT_BA_COPY_LABOR_BENEFITS);
        return super.copy(mapping, form, request, response);
    }

    @Override
    protected void processAccountingLineOverrides(AccountingDocument financialDocument, List accountingLines) {
        if (!accountingLines.isEmpty()) {
            SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(accountingLines, AccountingLineOverride.REFRESH_FIELDS);

            for (Iterator i = accountingLines.iterator(); i.hasNext();) {
                AccountingLine line = (AccountingLine) i.next();
                processForOutput(financialDocument,line);
            }
        }
    }

    protected void processForOutput(AccountingDocument financialDocument,AccountingLine line) {
        AccountingLineOverride fromCurrentCode = AccountingLineOverride.valueOf(line.getOverrideCode());
        AccountingLineOverride needed = AccountingLineOverride.determineNeededOverrides(financialDocument,line);
        line.setAccountExpiredOverride(fromCurrentCode.hasComponent(COMPONENT.EXPIRED_ACCOUNT));
        line.setAccountExpiredOverrideNeeded(needed.hasComponent(COMPONENT.EXPIRED_ACCOUNT));
        line.setObjectBudgetOverride(false);
        line.setObjectBudgetOverrideNeeded(false);

    }

    /**
     * BA documents should also update total when inserting target lines
     *
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#insertAccountingLine(boolean, org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase, org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    @Override
    protected void insertAccountingLine(boolean isSource, KualiAccountingDocumentFormBase financialDocumentForm, AccountingLine line) {

        super.insertAccountingLine(isSource, financialDocumentForm, line);

        BudgetAdjustmentDocument baDoc = (BudgetAdjustmentDocument) financialDocumentForm.getDocument();
        if (!isSource){
            ((FinancialSystemDocumentHeader) financialDocumentForm.getDocument().getDocumentHeader()).setFinancialDocumentTotalAmount(((AmountTotaling) baDoc).getTotalDollarAmount());
        }
    }

}
