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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.kuali.kfs.fp.businessobject.ProcurementCardTargetAccountingLine;
import org.kuali.kfs.fp.businessobject.ProcurementCardTransactionDetail;
import org.kuali.kfs.fp.document.ProcurementCardDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.AccountingLineParser;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.event.AddAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.DeleteAccountingLineEvent;
import org.kuali.kfs.sys.exception.AccountingLineParserException;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.service.PersistenceService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
/**
 * This class handles specific Actions requests for the ProcurementCard.
 */
public class ProcurementCardAction extends CapitalAccountingLinesActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardAction.class);

    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);
        ProcurementCardDocument procureCardDocument = (ProcurementCardDocument)kualiDocumentFormBase.getDocument();
        int transactionsCount = procureCardDocument.getTransactionEntries().size();
        ProcurementCardForm procurementCardForm = (ProcurementCardForm)kualiDocumentFormBase;
        procurementCardForm.buildNewTargetAccountingLines(transactionsCount);
    }

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
     *
     * @see org.kuali.module.financial.web.struts.action.KualiFinancialDocumentActionBase#insertTargetLine(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward insertTargetLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProcurementCardForm procurementCardForm = (ProcurementCardForm) form;
        ProcurementCardDocument procurementCardDocument = (ProcurementCardDocument) procurementCardForm.getDocument();

        int targetContainerIndex = this.getSelectedContainer(request);
        ProcurementCardTargetAccountingLine line = (ProcurementCardTargetAccountingLine)procurementCardForm.getNewTargetLines().get(targetContainerIndex);

        ProcurementCardTransactionDetail transactionDetail = (ProcurementCardTransactionDetail) procurementCardDocument.getTransactionEntries().get(targetContainerIndex);
        line.setFinancialDocumentTransactionLineNumber(transactionDetail.getFinancialDocumentTransactionLineNumber());

        // check any business rules
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AddAccountingLineEvent(KFSConstants.NEW_TARGET_ACCT_LINES_PROPERTY_NAME + "[" + Integer.toString(targetContainerIndex) + "]", procurementCardForm.getDocument(), line));

        if (rulePassed) {
            // add accountingLine
            SpringContext.getBean(PersistenceService.class).retrieveNonKeyFields(line);
            insertAccountingLine(false, procurementCardForm, line);

            ProcurementCardTargetAccountingLine newLine = new ProcurementCardTargetAccountingLine();
            newLine.setTransactionContainerIndex(targetContainerIndex);

            procurementCardForm.getNewTargetLines().set(targetContainerIndex, newLine);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#performBalanceInquiryForTargetLine(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward performBalanceInquiryForTargetLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProcurementCardForm procurementCardForm = (ProcurementCardForm) form;
        ProcurementCardDocument procurementCardDocument = (ProcurementCardDocument) procurementCardForm.getDocument();

        int targetContainerIndex = this.getSelectedContainer(request);
        ProcurementCardTransactionDetail ProcurementCardTransactionDetail = (ProcurementCardTransactionDetail) procurementCardDocument.getTransactionEntries().get(targetContainerIndex);

        int targetIndex = super.getSelectedLine(request);
        TargetAccountingLine targetLine = (ProcurementCardTargetAccountingLine) ProcurementCardTransactionDetail.getTargetAccountingLines().get(targetIndex);

        return performBalanceInquiryForAccountingLine(mapping, form, request, targetLine);
    }

    /**
     * Override to resync base accounting lines. New lines on the PCDO document can be inserted anywhere in the list, not necessary
     * at the end.
     *
     * @see org.kuali.module.financial.web.struts.action.KualiFinancialDocumentActionBase#insertAccountingLine(boolean,
     *      org.kuali.module.financial.web.struts.form.KualiFinancialDocumentFormBase, org.kuali.rice.krad.bo.AccountingLine)
     */
    @Override
    protected void insertAccountingLine(boolean isSource, KualiAccountingDocumentFormBase financialDocumentForm, AccountingLine line) {
        AccountingDocument tdoc = financialDocumentForm.getFinancialDocument();

        // add it to the document
        tdoc.addTargetAccountingLine((TargetAccountingLine) line);
    }

    /**
     * Override to get the correct container of the transaction and then delete the correct accounting line
     *
     * @see org.kuali.module.financial.web.struts.action.KualiFinancialDocumentActionBase#deleteTargetLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response
     */
    @Override
    public ActionForward deleteTargetLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        int targetContainerIndex = this.getSelectedContainer(request);
        int targetIndex = this.getSelectedLine(request);

        KualiAccountingDocumentFormBase financialDocumentForm = (KualiAccountingDocumentFormBase) form;

        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.EXISTING_TARGET_ACCT_LINE_PROPERTY_NAME + "[" + targetIndex + "]";
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new DeleteAccountingLineEvent(errorPath, financialDocumentForm.getDocument(), ((AccountingDocument) financialDocumentForm.getDocument()).getTargetAccountingLine(targetIndex), false));

        // if the rule evaluation passed, let's delete it
        if (rulePassed) {
            deleteAccountingLineFromTransactionContainer(financialDocumentForm, targetContainerIndex, targetIndex);
        }
        else {
            String[] errorParams = new String[] { "target", Integer.toString(targetIndex + 1) };
            GlobalVariables.getMessageMap().putError(errorPath, KFSKeyConstants.ERROR_ACCOUNTINGLINE_DELETERULE_INVALIDACCOUNT, errorParams);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Override to remove the accounting line from the correct transaction
     *
     * @see org.kuali.module.financial.web.struts.action.KualiFinancialDocumentActionBase#deleteAccountingLine(boolean,
     *      org.kuali.module.financial.web.struts.form.KualiFinancialDocumentFormBase, int)
     */
    @Override
    protected void deleteAccountingLine(boolean isSource, KualiAccountingDocumentFormBase financialDocumentForm, int deleteIndex) {
        ProcurementCardDocument procurementCardDocument = (ProcurementCardDocument) financialDocumentForm.getDocument();
        procurementCardDocument.removeTargetAccountingLine(deleteIndex);
    }

    /**
     * Ensures that ProcurementCardForm.newTargetLines is cleared. Otherwise works like super.reload.
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#reload(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward reload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProcurementCardForm procurementCardForm = (ProcurementCardForm) form;
        procurementCardForm.setNewTargetLines(new ArrayList<ProcurementCardTargetAccountingLine>());

        return super.reload(mapping, procurementCardForm, request, response);
    }

    // get the index of selected transaction entry
    protected int getSelectedContainer(HttpServletRequest request) {
        int selectedContainer = -1;
        String parameterName = (String) request.getAttribute(KRADConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isNotBlank(parameterName)) {
            String lineNumber = StringUtils.substringBetween(parameterName, ".transactionEntries[", "].");
            selectedContainer = Integer.parseInt(lineNumber);
        }

        return selectedContainer;
    }

    /**
     * Removes the target accounting line at the given index from the transaction container transaction entries.
     *
     * @param financialDocumentForm, targetContainerIndex, targetIndex
     */
    protected void deleteAccountingLineFromTransactionContainer(KualiAccountingDocumentFormBase financialDocumentForm, int targetContainerIndex, int targetIndex) {
        ProcurementCardDocument procurementCardDocument = (ProcurementCardDocument) financialDocumentForm.getDocument();
        ProcurementCardTransactionDetail procurementCardTransactionDetail = (ProcurementCardTransactionDetail) procurementCardDocument.getTransactionEntries().get(targetContainerIndex);
        procurementCardTransactionDetail.getTargetAccountingLines().remove(targetIndex);
    }

    /**
     *
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#uploadTargetLines(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward uploadTargetLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProcurementCardForm procurementCardForm = (ProcurementCardForm) form;
        ProcurementCardDocument procurementCardDocument = (ProcurementCardDocument) procurementCardForm.getDocument();

        // get index of transaction line
        int tranasactionIndex = getTransactionLineIndex(request);

        uploadTargetAccountingLines(form, tranasactionIndex);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method handles retrieving the actual upload file as an input stream into the document.
     *
     * @param isSource
     * @param form
     * @throws FileNotFoundException
     * @throws IOException
     */
    protected void uploadTargetAccountingLines(ActionForm form, int tranasactionIndex) throws FileNotFoundException, IOException {
        ProcurementCardForm pcardForm = (ProcurementCardForm) form;

        List importedLines = null;

        ProcurementCardDocument procurementCardDocument = (ProcurementCardDocument) pcardForm.getFinancialDocument();
        ProcurementCardTransactionDetail transactionDetail = (ProcurementCardTransactionDetail) procurementCardDocument.getTransactionEntries().get(tranasactionIndex);

        AccountingLineParser accountingLineParser = procurementCardDocument.getAccountingLineParser();

        // import the lines
        String errorPathPrefix = null;
        try {
            errorPathPrefix = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.TARGET_ACCOUNTING_LINE_ERRORS;
            // get the import file
            FormFile targetFile = ((ProcurementCardTransactionDetail) (procurementCardDocument.getTransactionEntries().get(tranasactionIndex))).getTargetFile();
            checkUploadFile(targetFile);
            importedLines = accountingLineParser.importTargetAccountingLines(targetFile.getFileName(), targetFile.getInputStream(), procurementCardDocument);
        }
        catch (AccountingLineParserException e) {
            GlobalVariables.getMessageMap().putError(errorPathPrefix, e.getErrorKey(), e.getErrorParameters());
        }

        // add line to list for those lines which were successfully imported
        if (importedLines != null) {
            for (Iterator i = importedLines.iterator(); i.hasNext();) {
                ProcurementCardTargetAccountingLine importedLine = (ProcurementCardTargetAccountingLine) i.next();
                importedLine.setFinancialDocumentTransactionLineNumber(transactionDetail.getFinancialDocumentTransactionLineNumber());
                super.insertAccountingLine(false, pcardForm, importedLine);
            }
        }
    }

    /**
     * Parses the method to call attribute to pick off the transaction line number which should have an action performed
     * on it.
     *
     * @param request
     * @return
     */
    protected int getTransactionLineIndex(HttpServletRequest request) {
        int selectedLine = -1;
        String parameterName = (String) request.getAttribute(KFSConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isNotBlank(parameterName)) {
            String lineNumber = StringUtils.substringBetween(parameterName, "document.transactionEntries[", "]");
            selectedLine = Integer.parseInt(lineNumber);
        }

        return selectedLine;
    }
}
