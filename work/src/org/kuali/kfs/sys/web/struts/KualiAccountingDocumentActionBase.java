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
package org.kuali.kfs.web.struts.action;

import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINE_SALES_TAX_INVALID_ACCOUNT;
import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINE_SALES_TAX_REQUIRED;
import static org.kuali.kfs.KFSKeyConstants.ERROR_REQUIRED;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.kuali.core.document.AmountTotaling;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.Timer;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.AccountingLineOverride;
import org.kuali.kfs.bo.AccountingLineParser;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.bo.TargetAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.exceptions.AccountingLineParserException;
import org.kuali.kfs.rule.event.AddAccountingLineEvent;
import org.kuali.kfs.rule.event.DeleteAccountingLineEvent;
import org.kuali.kfs.rule.event.UpdateAccountingLineEvent;
import org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.APPLICATION_PARAMETER;
import org.kuali.kfs.service.ParameterEvaluator;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase;
import org.kuali.kfs.web.ui.AccountingLineDecorator;
import org.kuali.module.financial.bo.SalesTax;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class handles UI actions for all shared methods of financial documents.
 */
public class KualiAccountingDocumentActionBase extends KualiTransactionalDocumentActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiAccountingDocumentActionBase.class);

    /**
     * Adds check for accountingLine updates, generates and dispatches any events caused by such updates
     * 
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Timer t0 = new Timer("KualiFinancialDocumentFormBase.execute");
        KualiAccountingDocumentFormBase transForm = (KualiAccountingDocumentFormBase) form;

        // handle changes to accountingLines
        if (transForm.hasDocumentId()) {
            AccountingDocument financialDocument = (AccountingDocument) transForm.getDocument();

            processAccountingLines(financialDocument, transForm, KFSConstants.SOURCE);
            processAccountingLines(financialDocument, transForm, KFSConstants.TARGET);
        }

        // This is after a potential handleUpdate(), to display automatically cleared overrides following a route or save.
        processAccountingLineOverrides(transForm);

        // this refershes if the accounting lines within the form are editable or not
        if (ObjectUtils.isNotNull(transForm.getDocument()) && ObjectUtils.isNotNull(transForm.getDocument().getDocumentHeader()) && ObjectUtils.isNotNull(transForm.getDocument().getDocumentNumber()) && SpringContext.getBean(WorkflowDocumentService.class).workflowDocumentExists(transForm.getDocument().getDocumentNumber())) {
            transForm.refreshEditableAccounts();
        }

        // proceed as usual
        ActionForward result = super.execute(mapping, form, request, response);
        t0.log();
        return result;
    }

    /**
     * All document-creation gets routed through here
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);

        KualiAccountingDocumentFormBase tform = (KualiAccountingDocumentFormBase) kualiDocumentFormBase;

        // reset baseline accountingLine lists
        tform.getBaselineSourceAccountingLines().clear();
        tform.getBaselineTargetAccountingLines().clear();

        // reset decorator lists
        tform.getSourceLineDecorators().clear();
        tform.getTargetLineDecorators().clear();
    }

    /**
     * All document-load operations get routed through here
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);


        KualiAccountingDocumentFormBase tform = (KualiAccountingDocumentFormBase) kualiDocumentFormBase;

        // populate baseline accountingLine lists
        List baselineSourceLines = tform.getBaselineSourceAccountingLines();
        baselineSourceLines.clear();
        baselineSourceLines.addAll(tform.getFinancialDocument().getSourceAccountingLines());
        // sales tax handling
        handleSalesTaxRequiredAllLines(kualiDocumentFormBase, baselineSourceLines);

        List baselineTargetLines = tform.getBaselineTargetAccountingLines();
        baselineTargetLines.clear();
        baselineTargetLines.addAll(tform.getFinancialDocument().getTargetAccountingLines());
        // sales tax handling
        handleSalesTaxRequiredAllLines(kualiDocumentFormBase, baselineTargetLines);

        // populate decorator lists
        tform.resetSourceLineDecorators(baselineSourceLines.size());
        tform.resetTargetLineDecorators(baselineTargetLines.size());

        // clear out the new accounting line holders
        tform.setNewSourceLine(null);
        tform.setNewTargetLine(null);

        processAccountingLineOverrides(tform);
    }

    /**
     * Needed to override this to keep from losing Sales Tax information
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.refresh(mapping, form, request, response);
        refreshSalesTaxInfo(form);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Needed to override this to keep from losing Sales Tax information
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#toggleTab(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward toggleTab(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.toggleTab(mapping, form, request, response);
        refreshSalesTaxInfo(form);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    // Set of actions for which updateEvents should be generated
    protected static final Set UPDATE_EVENT_ACTIONS;
    static {
        String[] updateEventActions = { KFSConstants.SAVE_METHOD, KFSConstants.ROUTE_METHOD, KFSConstants.APPROVE_METHOD, KFSConstants.BLANKET_APPROVE_METHOD };
        UPDATE_EVENT_ACTIONS = new HashSet();
        for (int i = 0; i < updateEventActions.length; ++i) {
            UPDATE_EVENT_ACTIONS.add(updateEventActions[i]);
        }
    }

    /**
     * @param transForm
     */
    protected void processAccountingLineOverrides(KualiAccountingDocumentFormBase transForm) {
        processAccountingLineOverrides(transForm.getNewSourceLine());
        processAccountingLineOverrides(transForm.getNewTargetLine());
        if (transForm.hasDocumentId()) {
            AccountingDocument financialDocument = (AccountingDocument) transForm.getDocument();

            processAccountingLineOverrides(financialDocument.getSourceAccountingLines());
            processAccountingLineOverrides(financialDocument.getTargetAccountingLines());
        }
    }

    /**
     * @param line
     */
    protected void processAccountingLineOverrides(AccountingLine line) {
        processAccountingLineOverrides(Arrays.asList(new AccountingLine[] { line }));
    }

    /**
     * @param accountingLines
     */
    protected void processAccountingLineOverrides(List accountingLines) {
        if (!accountingLines.isEmpty()) {
            SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(accountingLines, AccountingLineOverride.REFRESH_FIELDS);

            for (Iterator i = accountingLines.iterator(); i.hasNext();) {
                AccountingLine line = (AccountingLine) i.next();
                AccountingLineOverride.processForOutput(line);
            }
        }
    }

    /**
     * @param transDoc
     * @param transForm
     * @param lineSet
     */
    private void processAccountingLines(AccountingDocument transDoc, KualiAccountingDocumentFormBase transForm, String lineSet) {
        // figure out which set of lines we're looking at
        List baseLines;
        List formLines;
        List<AccountingLineDecorator> decorators;
        String pathPrefix;
        boolean source;
        if (lineSet.equals(KFSConstants.SOURCE)) {
            baseLines = transForm.getBaselineSourceAccountingLines();
            formLines = transDoc.getSourceAccountingLines();
            decorators = transForm.getSourceLineDecorators(formLines.size());
            pathPrefix = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.EXISTING_SOURCE_ACCT_LINE_PROPERTY_NAME;
            source = true;
        }
        else {
            baseLines = transForm.getBaselineTargetAccountingLines();
            formLines = transDoc.getTargetAccountingLines();
            decorators = transForm.getTargetLineDecorators(formLines.size());
            pathPrefix = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.EXISTING_TARGET_ACCT_LINE_PROPERTY_NAME;
            source = false;
        }

        Map baseLineMap = new HashMap();
        for (Iterator i = baseLines.iterator(); i.hasNext();) {
            AccountingLine baseLine = (AccountingLine) i.next();
            baseLineMap.put(baseLine.getSequenceNumber(), baseLine);
        }

        // find and process corresponding form and baselines
        int index = 0;
        for (Iterator i = formLines.iterator(); i.hasNext(); index++) {
            AccountingLine formLine = (AccountingLine) i.next();
            AccountingLine baseLine = (AccountingLine) baseLineMap.get(formLine.getSequenceNumber());
            AccountingLineDecorator decorator = decorators.get(index);

            // always update decorator
            handleDecorator(formLine, baseLine, decorator);

            // update sales tax required attribute for view
            // handleSalesTaxRequired(transDoc, formLine, source, false, index);
            checkSalesTax(transDoc, formLine, source, false, index);
            if (baseLine != null) {
                handleSalesTaxRequired(transDoc, baseLine, source, false, index);
                // checkSalesTax(transDoc, baseLine, source, false, index);
            }

            // only generate update events for specific action methods
            String methodToCall = transForm.getMethodToCall();
            if (UPDATE_EVENT_ACTIONS.contains(methodToCall)) {
                handleUpdate(transDoc, pathPrefix + "[" + index + "]", formLine, baseLine);
            }
        }
    }

    /**
     * @param formLine
     * @param baseLine
     * @param decorator
     */
    private void handleDecorator(AccountingLine formLine, AccountingLine baseLine, AccountingLineDecorator decorator) {
        // if line is new, or line hasn't changed, make non-revertible;
        // otherwise, revertible
        if ((baseLine == null) || formLine.isLike(baseLine)) {
            decorator.setRevertible(false);
        }
        else {
            decorator.setRevertible(true);
        }
    }

    /**
     * @param transDoc
     * @param errorPathPrefix
     * @param formLine
     * @param baseLine
     */
    private void handleUpdate(AccountingDocument transDoc, String errorPathPrefix, AccountingLine formLine, AccountingLine baseLine) {
        if ((baseLine != null) && !formLine.isLike(baseLine)) {
            // reluctantly refresh BOs for clearOverridesThatBecameUnneeded()
            formLine.refreshNonUpdateableReferences();
            clearOverridesThatBecameUnneeded(formLine);
            // the rule itself is responsible for adding error messages to the global ErrorMap
            SpringContext.getBean(KualiRuleService.class).applyRules(new UpdateAccountingLineEvent(errorPathPrefix, transDoc, baseLine, formLine));
        }
    }

    /**
     * Automatically clears any overrides that have become unneeded. This is for accounting lines that were changed right before
     * final actions like route. Normally the unneeded overrides are cleared in accountingLineOverrideField.tag instead, but that
     * requires another form submit. This method shouldn't be called on lines that haven't changed, to avoid automatically changing
     * read-only lines. This cannot be done in the Rule because Rules cannot change the AccountingLines; they only get a deepCopy.
     * 
     * @param formLine
     */
    protected void clearOverridesThatBecameUnneeded(AccountingLine formLine) {
        AccountingLineOverride currentlyNeeded = AccountingLineOverride.determineNeededOverrides(formLine);
        AccountingLineOverride currentOverride = AccountingLineOverride.valueOf(formLine.getOverrideCode());
        if (!currentOverride.isValidMask(currentlyNeeded)) {
            // todo: handle unsupported combinations of overrides (not a problem until we allow certain multiple overrides)
        }
        formLine.setOverrideCode(currentOverride.mask(currentlyNeeded).getCode());
    }

    /**
     * This method will revert a TargetAccountingLine by overwriting its current values with the values in the corresponding
     * baseline accountingLine. This assumes that the user presses the revert button for a specific accounting line on the document
     * and that the document is represented by a FinancialDocumentFormBase.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward revertTargetLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiAccountingDocumentFormBase financialDocumentForm = (KualiAccountingDocumentFormBase) form;
        AccountingDocument financialDocument = (AccountingDocument) financialDocumentForm.getDocument();

        int revertIndex = getSelectedLine(request);

        TargetAccountingLine originalLine = financialDocumentForm.getBaselineTargetAccountingLine(revertIndex);
        TargetAccountingLine brokenLine = financialDocument.getTargetAccountingLine(revertIndex);

        if (revertAccountingLine(financialDocumentForm, revertIndex, originalLine, brokenLine)) {
            financialDocumentForm.getTargetLineDecorator(revertIndex).setRevertible(false);
        }

        // no business rules to check, no events to create

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * This method will revert a SourceAccountingLine by overwriting its current values with the values in the corresponding
     * baseline accountingLine. This assumes that the user presses the revert button for a specific accounting line on the document
     * and that the document is represented by a FinancialDocumentFormBase.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward revertSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiAccountingDocumentFormBase financialDocumentForm = (KualiAccountingDocumentFormBase) form;
        AccountingDocument financialDocument = (AccountingDocument) financialDocumentForm.getDocument();

        int revertIndex = getSelectedLine(request);

        SourceAccountingLine originalLine = financialDocumentForm.getBaselineSourceAccountingLine(revertIndex);
        SourceAccountingLine brokenLine = financialDocument.getSourceAccountingLine(revertIndex);

        if (revertAccountingLine(financialDocumentForm, revertIndex, originalLine, brokenLine)) {
            financialDocumentForm.getSourceLineDecorator(revertIndex).setRevertible(false);
        }

        // no business rules to check, no events to create

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Overwrites field values in the given brokenLine with those in the given originalLine, if the two accountingLines differ.
     * 
     * @param transForm
     * @param revertIndex
     * @param originalLine
     * @param newerLine
     * @return true if and only if the brokenLine was actually changed
     */
    protected boolean revertAccountingLine(KualiAccountingDocumentFormBase transForm, int revertIndex, AccountingLine originalLine, AccountingLine newerLine) {
        boolean reverted = false;

        SpringContext.getBean(PersistenceService.class).refreshAllNonUpdatingReferences(originalLine);

        // *always* revert (so that if someone manually changes the line to its original values, then hits revert, they won't get an
        // error message saying "couldn't revert")
        newerLine.copyFrom(originalLine);
        if (isSalesTaxRequired((AccountingDocument) transForm.getDocument(), newerLine)) {
            newerLine.setSalesTaxRequired(true);
        }
        reverted = true;
        GlobalVariables.getMessageList().add(KFSKeyConstants.MESSAGE_REVERT_SUCCESSFUL);

        return reverted;
    }


    /**
     * This method will remove a TargetAccountingLine from a FinancialDocument. This assumes that the user presses the delete button
     * for a specific accounting line on the document and that the document is represented by a FinancialDocumentFormBase.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteTargetLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiAccountingDocumentFormBase financialDocumentForm = (KualiAccountingDocumentFormBase) form;

        boolean rulePassed = false;
        int deleteIndex = getLineToDelete(request);
        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.EXISTING_TARGET_ACCT_LINE_PROPERTY_NAME + "[" + deleteIndex + "]";

        // check business rule, if there is a baseline copy
        // (accountingLines without baselines haven't been persisted yet, so they can safely be deleted)
        if (financialDocumentForm.hasBaselineTargetAccountingLine(deleteIndex)) {
            TargetAccountingLine baseline = financialDocumentForm.getBaselineTargetAccountingLine(deleteIndex);
            SpringContext.getBean(PersistenceService.class).refreshAllNonUpdatingReferences(baseline);

            rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new DeleteAccountingLineEvent(errorPath, financialDocumentForm.getDocument(), baseline, false));
        }
        else {
            rulePassed = true;
        }

        // if the rule evaluation passed, let's delete it
        if (rulePassed) {
            deleteAccountingLine(false, financialDocumentForm, deleteIndex);
        }
        else {
            financialDocumentForm.getTargetLineDecorator(deleteIndex).setRevertible(true);

            String[] errorParams = new String[] { "target", Integer.toString(deleteIndex + 1) };
            GlobalVariables.getErrorMap().putError(errorPath, KFSKeyConstants.ERROR_ACCOUNTINGLINE_DELETERULE_INVALIDACCOUNT, errorParams);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method will remove a SourceAccountingLine from a FinancialDocument. This assumes that the user presses the delete button
     * for a specific accounting line on the document and that the document is represented by a FinancialDocumentFormBase.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiAccountingDocumentFormBase financialDocumentForm = (KualiAccountingDocumentFormBase) form;

        boolean rulePassed = false;
        int deleteIndex = getLineToDelete(request);
        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.EXISTING_SOURCE_ACCT_LINE_PROPERTY_NAME + "[" + deleteIndex + "]";

        // check business rule, if there is a baseline copy
        // (accountingLines without baselines haven't been persisted yet, so they can safely be deleted)
        if (financialDocumentForm.hasBaselineSourceAccountingLine(deleteIndex)) {
            SourceAccountingLine baseline = financialDocumentForm.getBaselineSourceAccountingLine(deleteIndex);
            SpringContext.getBean(PersistenceService.class).refreshAllNonUpdatingReferences(baseline);

            rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new DeleteAccountingLineEvent(errorPath, financialDocumentForm.getDocument(), baseline, false));
        }
        else {
            rulePassed = true;
        }

        // if the rule evaluation passed, let's delete it
        if (rulePassed) {
            deleteAccountingLine(true, financialDocumentForm, deleteIndex);
        }
        else {
            financialDocumentForm.getSourceLineDecorator(deleteIndex).setRevertible(true);

            String[] errorParams = new String[] { "source", Integer.toString(deleteIndex + 1) };
            GlobalVariables.getErrorMap().putError(errorPath, KFSKeyConstants.ERROR_ACCOUNTINGLINE_DELETERULE_INVALIDACCOUNT, errorParams);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * Deletes the source or target accountingLine with the given index from the given form. Assumes that the rule- and form-
     * validation have already occurred.
     * 
     * @param isSource
     * @param financialDocumentForm
     * @param deleteIndex
     */
    protected void deleteAccountingLine(boolean isSource, KualiAccountingDocumentFormBase financialDocumentForm, int deleteIndex) {
        if (isSource) {
            // remove from document
            financialDocumentForm.getFinancialDocument().getSourceAccountingLines().remove(deleteIndex);

            // remove baseline duplicate and decorator
            if (deleteIndex < financialDocumentForm.getBaselineSourceAccountingLines().size()) {
                financialDocumentForm.getBaselineSourceAccountingLines().remove(deleteIndex);
            }
            financialDocumentForm.getSourceLineDecorators().remove(deleteIndex);

            // update the doc total
            AccountingDocument tdoc = (AccountingDocument) financialDocumentForm.getDocument();
            if (tdoc instanceof AmountTotaling)
                financialDocumentForm.getDocument().getDocumentHeader().setFinancialDocumentTotalAmount(((AmountTotaling) tdoc).getTotalDollarAmount());
        }
        else {
            // remove from document
            financialDocumentForm.getFinancialDocument().getTargetAccountingLines().remove(deleteIndex);

            // remove baseline duplicate and decorator
            if (deleteIndex < financialDocumentForm.getBaselineTargetAccountingLines().size()) {
                financialDocumentForm.getBaselineTargetAccountingLines().remove(deleteIndex);
            }
            financialDocumentForm.getTargetLineDecorators().remove(deleteIndex);
        }
    }


    /**
     * This action executes a call to upload CSV accounting line values as TargetAccountingLines for a given transactional document.
     * The "uploadAccountingLines()" method handles the multi-part request.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward uploadTargetLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        // call method that sourceform and destination list
        uploadAccountingLines(false, form);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * This action executes a call to upload CSV accounting line values as SourceAccountingLines for a given transactional document.
     * The "uploadAccountingLines()" method handles the multi-part request.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws FileNotFoundException
     * @throws IOException
     */
    public ActionForward uploadSourceLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException, IOException {
        LOG.info("Uploading source accounting lines");
        // call method that sourceform and destination list
        uploadAccountingLines(true, form);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method determines whether we are uploading source or target lines, and then calls uploadAccountingLines directly on the
     * document object. This method handles retrieving the actual upload file as an input stream into the document.
     * 
     * @param isSource
     * @param form
     * @throws FileNotFoundException
     * @throws IOException
     */
    protected void uploadAccountingLines(boolean isSource, ActionForm form) throws FileNotFoundException, IOException {
        KualiAccountingDocumentFormBase tmpForm = (KualiAccountingDocumentFormBase) form;

        List importedLines = null;

        AccountingDocument financialDocument = tmpForm.getFinancialDocument();
        AccountingLineParser accountingLineParser = financialDocument.getAccountingLineParser();

        // import the lines
        String errorPathPrefix = null;
        try {
            if (isSource) {
                errorPathPrefix = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.SOURCE_ACCOUNTING_LINE_ERRORS;
                FormFile sourceFile = tmpForm.getSourceFile();
                checkUploadFile(sourceFile);
                importedLines = accountingLineParser.importSourceAccountingLines(sourceFile.getFileName(), sourceFile.getInputStream(), financialDocument);
            }
            else {
                errorPathPrefix = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.TARGET_ACCOUNTING_LINE_ERRORS;
                FormFile targetFile = tmpForm.getTargetFile();
                checkUploadFile(targetFile);
                importedLines = accountingLineParser.importTargetAccountingLines(targetFile.getFileName(), targetFile.getInputStream(), financialDocument);
            }
        }
        catch (AccountingLineParserException e) {
            GlobalVariables.getErrorMap().putError(errorPathPrefix, e.getErrorKey(), e.getErrorParameters());
        }

        // add line to list for those lines which were successfully imported
        if (importedLines != null) {
            for (Iterator i = importedLines.iterator(); i.hasNext();) {
                AccountingLine importedLine = (AccountingLine) i.next();
                insertAccountingLine(isSource, tmpForm, importedLine);
            }
        }
    }

    private void checkUploadFile(FormFile file) {
        if (file == null) {
            throw new AccountingLineParserException("invalid (null) upload file", KFSKeyConstants.ERROR_UPLOADFILE_NULL);
        }
    }

    /**
     * This method will add a TargetAccountingLine to a FinancialDocument. This assumes that the user presses the add button for a
     * specific accounting line on the document and that the document is represented by a FinancialDocumentFormBase. It first
     * validates the line for data integrity and then checks appropriate business rules.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward insertTargetLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiAccountingDocumentFormBase financialDocumentForm = (KualiAccountingDocumentFormBase) form;

        TargetAccountingLine line = financialDocumentForm.getNewTargetLine();
        boolean rulePassed = true;
        // before we check the regular rules we need to check the sales tax rules
        // TODO: Refactor rules so we no longer have to call this before a copy of the
        // accountingLine
        rulePassed &= checkSalesTax((AccountingDocument) financialDocumentForm.getDocument(), line, false, true, 0);

        // check any business rules
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddAccountingLineEvent(KFSConstants.NEW_TARGET_ACCT_LINE_PROPERTY_NAME, financialDocumentForm.getDocument(), line));

        // if the rule evaluation passed, let's add it
        if (rulePassed) {
            // add accountingLine
            SpringContext.getBean(PersistenceService.class).refreshAllNonUpdatingReferences(line);
            insertAccountingLine(false, financialDocumentForm, line);

            // clear the used newTargetLine
            financialDocumentForm.setNewTargetLine(null);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * This action executes an insert of a SourceAccountingLine into a document only after validating the accounting line and
     * checking any appropriate business rules.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward insertSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiAccountingDocumentFormBase financialDocumentForm = (KualiAccountingDocumentFormBase) form;

        SourceAccountingLine line = financialDocumentForm.getNewSourceLine();
        boolean rulePassed = true;
        // before we check the regular rules we need to check the sales tax rules
        // TODO: Refactor rules so we no longer have to call this before a copy of the
        // accountingLine
        rulePassed &= checkSalesTax((AccountingDocument) financialDocumentForm.getDocument(), line, true, true, 0);
        // check any business rules
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddAccountingLineEvent(KFSConstants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME, financialDocumentForm.getDocument(), line));

        if (rulePassed) {
            // add accountingLine
            SpringContext.getBean(PersistenceService.class).refreshAllNonUpdatingReferences(line);
            insertAccountingLine(true, financialDocumentForm, line);

            // clear the used newTargetLine
            financialDocumentForm.setNewSourceLine(null);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Adds the given accountingLine to the appropriate form-related datastructures.
     * 
     * @param isSource
     * @param financialDocumentForm
     * @param line
     */
    protected void insertAccountingLine(boolean isSource, KualiAccountingDocumentFormBase financialDocumentForm, AccountingLine line) {
        // create and init a decorator
        AccountingLineDecorator decorator = new AccountingLineDecorator();
        decorator.setRevertible(false);

        AccountingDocument tdoc = financialDocumentForm.getFinancialDocument();
        if (isSource) {
            // add it to the document
            tdoc.addSourceAccountingLine((SourceAccountingLine) line);

            // add it to the baseline, to prevent generation of spurious update events
            financialDocumentForm.getBaselineSourceAccountingLines().add(line);

            // add the decorator
            financialDocumentForm.getSourceLineDecorators().add(decorator);

            // add PK fields to sales tax if needed
            if (line.isSalesTaxRequired()) {
                populateSalesTax(line);
            }

            // Update the doc total
            if (tdoc instanceof AmountTotaling)
                financialDocumentForm.getDocument().getDocumentHeader().setFinancialDocumentTotalAmount(((AmountTotaling) tdoc).getTotalDollarAmount());
        }
        else {
            // add it to the document
            tdoc.addTargetAccountingLine((TargetAccountingLine) line);

            // add it to the baseline, to prevent generation of spurious update events
            financialDocumentForm.getBaselineTargetAccountingLines().add(line);

            // add the decorator
            financialDocumentForm.getTargetLineDecorators().add(decorator);

            // add PK fields to sales tax if needed
            if (line.isSalesTaxRequired()) {
                populateSalesTax(line);
            }
        }
    }

    /**
     * Method that will take the current document, copy it, replace all references to doc header id with a new one, clear pending
     * entries, clear notes, and reset version numbers
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward copy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.copy(mapping, form, request, response);

        KualiAccountingDocumentFormBase tmpForm = (KualiAccountingDocumentFormBase) form;

        // KULEDOCS-1440: need to reset base accounting lines since when doc number changes on copy base lines would still reference
        // old doc number causing revert button to show up
        tmpForm.setBaselineSourceAccountingLines(tmpForm.getFinancialDocument().getSourceAccountingLines());
        tmpForm.setBaselineTargetAccountingLines(tmpForm.getFinancialDocument().getTargetAccountingLines());
        tmpForm.getSourceLineDecorators().clear();
        tmpForm.getTargetLineDecorators().clear();

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This action changes the value of the hide field in the user interface so that when the page is rendered, the UI knows to show
     * all of the labels for each of the accounting line values.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward showDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiAccountingDocumentFormBase tmpForm = (KualiAccountingDocumentFormBase) form;
        tmpForm.setHideDetails(false);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method is triggered when the user toggles the show/hide button to "hide" thus making the UI render without any of the
     * accounting line labels/descriptions showing up underneath the values in the UI.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward hideDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiAccountingDocumentFormBase tmpForm = (KualiAccountingDocumentFormBase) form;
        tmpForm.setHideDetails(true);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Takes care of storing the action form in the User session and forwarding to the balance inquiry report menu action for a
     * source accounting line.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward performBalanceInquiryForSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SourceAccountingLine line = (SourceAccountingLine) getSourceAccountingLine(form, request);
        return performBalanceInquiryForAccountingLine(mapping, form, request, line);
    }

    /**
     * Takes care of storing the action form in the User session and forwarding to the balance inquiry report menu action for a
     * target accounting line.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward performBalanceInquiryForTargetLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        int lineIndex = getSelectedLine(request);

        TargetAccountingLine line = (TargetAccountingLine)((KualiAccountingDocumentFormBase) form).getFinancialDocument().getTargetAccountingLine(lineIndex);

        return performBalanceInquiryForAccountingLine(mapping, form, request, line);
    }

    /**
     * This method is a helper method that will return a source accounting line. The reason we're making it protected in here is so
     * that we can override this method in some of the modules. PurchasingActionBase is one of the subclasses that will be
     * overriding this, because in PurchasingActionBase, we'll need to get the source accounting line using both an item index and
     * an account index.
     * 
     * @param form
     * @param request
     * @param isSource
     * @return
     */
    protected SourceAccountingLine getSourceAccountingLine(ActionForm form, HttpServletRequest request) {
        int lineIndex = getSelectedLine(request);
        SourceAccountingLine line = (SourceAccountingLine) ObjectUtils.deepCopy(((KualiAccountingDocumentFormBase) form).getFinancialDocument().getSourceAccountingLine(lineIndex));
        return line;
    }

    /**
     * This method handles preparing all of the accounting line data so that it can be pushed up to the balance inquiries for
     * populating the search criteria of each.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param line
     * @return ActionForward
     */
    protected ActionForward performBalanceInquiryForAccountingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, AccountingLine line) {
        // build out base path for return location
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

        // build out the actual form key that will be used to retrieve the form on refresh
        String callerDocFormKey = GlobalVariables.getUserSession().addObject(form);

        // now add required parameters
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
        // need this next param b/c the lookup's return back will overwrite
        // the original doc form key
        parameters.put(KFSConstants.BALANCE_INQUIRY_REPORT_MENU_CALLER_DOC_FORM_KEY, callerDocFormKey);
        parameters.put(KFSConstants.DOC_FORM_KEY, callerDocFormKey);
        parameters.put(KFSConstants.BACK_LOCATION, basePath + mapping.getPath() + ".do");

        if (StringUtils.isNotBlank(line.getReferenceOriginCode())) {
            parameters.put("referenceOriginCode", line.getReferenceOriginCode());
        }
        if (StringUtils.isNotBlank(line.getReferenceNumber())) {
            parameters.put("referenceNumber", line.getReferenceNumber());
        }
        if (StringUtils.isNotBlank(line.getReferenceTypeCode())) {
            parameters.put("referenceTypeCode", line.getReferenceTypeCode());
        }
        if (StringUtils.isNotBlank(line.getDebitCreditCode())) {
            parameters.put("debitCreditCode", line.getDebitCreditCode());
        }
        if (StringUtils.isNotBlank(line.getChartOfAccountsCode())) {
            parameters.put("chartOfAccountsCode", line.getChartOfAccountsCode());
        }
        if (StringUtils.isNotBlank(line.getAccountNumber())) {
            parameters.put("accountNumber", line.getAccountNumber());
        }
        if (StringUtils.isNotBlank(line.getFinancialObjectCode())) {
            parameters.put("financialObjectCode", line.getFinancialObjectCode());
        }
        if (StringUtils.isNotBlank(line.getSubAccountNumber())) {
            parameters.put("subAccountNumber", line.getSubAccountNumber());
        }
        if (StringUtils.isNotBlank(line.getFinancialSubObjectCode())) {
            parameters.put("financialSubObjectCode", line.getFinancialSubObjectCode());
        }
        if (StringUtils.isNotBlank(line.getProjectCode())) {
            parameters.put("projectCode", line.getProjectCode());
        }
        if (StringUtils.isNotBlank(line.getObjectTypeCode())) {
            parameters.put("objectTypeCode", line.getObjectTypeCode());
        }

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + KFSConstants.BALANCE_INQUIRY_REPORT_MENU_ACTION, parameters);

        return new ActionForward(lookupUrl, true);
    }

    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.save(mapping, form, request, response);

        // KULEDOCS-1443: For the revert button, set the new baseline accounting lines as the most recently saved lines
        KualiAccountingDocumentFormBase tmpForm = (KualiAccountingDocumentFormBase) form;
        tmpForm.setBaselineSourceAccountingLines(tmpForm.getFinancialDocument().getSourceAccountingLines());
        tmpForm.setBaselineTargetAccountingLines(tmpForm.getFinancialDocument().getTargetAccountingLines());
        // need to check on sales tax for all the accounting lines
        checkSalesTaxRequiredAllLines(tmpForm, tmpForm.getFinancialDocument().getSourceAccountingLines());
        checkSalesTaxRequiredAllLines(tmpForm, tmpForm.getFinancialDocument().getTargetAccountingLines());
        return forward;
    }


    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.route(mapping, form, request, response);
        KualiAccountingDocumentFormBase tmpForm = (KualiAccountingDocumentFormBase) form;
        checkSalesTaxRequiredAllLines(tmpForm, tmpForm.getFinancialDocument().getSourceAccountingLines());
        checkSalesTaxRequiredAllLines(tmpForm, tmpForm.getFinancialDocument().getTargetAccountingLines());

        return forward;
    }

    /**
     * Encapsulate the rule check so we can call it from multiple places
     * 
     * @param document
     * @param line
     * @return true if sales is either not required or it contains sales tax
     */
    private boolean checkSalesTax(AccountingDocument document, AccountingLine line, boolean source, boolean newLine, int index) {
        boolean passed = true;
        if (isSalesTaxRequired(document, line)) {
            // then set the salesTaxRequired on the accountingLine
            line.setSalesTaxRequired(true);
            populateSalesTax(line);
            // check to see if the sales tax info has been put in
            passed &= isValidSalesTaxEntered(line, source, newLine, index);
        }
        return passed;
    }

    /**
     * This method checks to see if this doctype needs sales tax If it does then it checks to see if the account and object code
     * require sales tax If it does then it returns true. Note - this is hackish as we shouldn't have to call rules directly from
     * the action class But we need to in this instance because we are copying the lines before calling rules and need a way to
     * modify them before they go on
     * 
     * @param accountingLine
     * @return true if sales tax check is needed, false otherwise
     */
    protected boolean isSalesTaxRequired(AccountingDocument financialDocument, AccountingLine accountingLine) {
        boolean required = false;
        DocumentTypeService docTypeService = SpringContext.getBean(DocumentTypeService.class);
        String docType = docTypeService.getDocumentTypeCodeByClass(financialDocument.getClass());
        // first we need to check just the doctype to see if it needs the sales tax check
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        // apply the rule, see if it fails
        ParameterEvaluator docTypeSalesTaxCheckEvaluator = SpringContext.getBean(ParameterService.class).getParameterEvaluator(ParameterConstants.FINANCIAL_PROCESSING_DOCUMENT.class, APPLICATION_PARAMETER.DOCTYPE_SALES_TAX_CHECK, docType);
        if (docTypeSalesTaxCheckEvaluator.evaluationSucceeds()) {
            required = true;
        }

        // second we need to check the account and object code combination to see if it needs sales tax
        if (required) {
            // get the object code and account
            String objCd = accountingLine.getFinancialObjectCode();
            String account = accountingLine.getAccountNumber();
            if (!StringUtils.isEmpty(objCd) && !StringUtils.isEmpty(account)) {
                String compare = account + ":" + objCd;
                ParameterEvaluator salesTaxApplicableAcctAndObjectEvaluator = SpringContext.getBean(ParameterService.class).getParameterEvaluator(ParameterConstants.FINANCIAL_PROCESSING_DOCUMENT.class, APPLICATION_PARAMETER.SALES_TAX_APPLICABLE_ACCOUNTS_AND_OBJECT_CODES, compare);
                if (!salesTaxApplicableAcctAndObjectEvaluator.evaluationSucceeds()) {
                    required = false;
                }
            }
            else {
                // the two fields are currently empty and we don't need to check yet
                required = false;
            }
        }
        return required;
    }

    /**
     * This method checks to see if the sales tax information was put into the accounting line
     * 
     * @param accountingLine
     * @return true if entered correctly, false otherwise
     */
    private boolean isValidSalesTaxEntered(AccountingLine accountingLine, boolean source, boolean newLine, int index) {
        boolean valid = true;
        DictionaryValidationService dictionaryValidationService = SpringContext.getBean(DictionaryValidationService.class);
        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        String objCd = accountingLine.getFinancialObjectCode();
        String account = accountingLine.getAccountNumber();
        SalesTax salesTax = accountingLine.getSalesTax();
        String pathPrefix = "";
        if (source && !newLine) {
            pathPrefix = "document." + KFSConstants.EXISTING_SOURCE_ACCT_LINE_PROPERTY_NAME + "[" + index + "]";
        }
        else if (!source && !newLine) {
            pathPrefix = "document." + KFSConstants.EXISTING_TARGET_ACCT_LINE_PROPERTY_NAME + "[" + index + "]";
        }
        else if (source && newLine) {
            pathPrefix = KFSConstants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME;
        }
        else if (!source && newLine) {
            pathPrefix = KFSConstants.NEW_TARGET_ACCT_LINE_PROPERTY_NAME;
        }
        GlobalVariables.getErrorMap().addToErrorPath(pathPrefix);
        if (ObjectUtils.isNull(salesTax)) {
            valid &= false;
            GlobalVariables.getErrorMap().putError("salesTax.chartOfAccountsCode", ERROR_DOCUMENT_ACCOUNTING_LINE_SALES_TAX_REQUIRED, account, objCd);
        }
        else {

            if (StringUtils.isBlank(salesTax.getChartOfAccountsCode())) {
                valid &= false;
                GlobalVariables.getErrorMap().putError("salesTax.chartOfAccountsCode", ERROR_REQUIRED, "Chart of Accounts");
            }
            if (StringUtils.isBlank(salesTax.getAccountNumber())) {
                valid &= false;
                GlobalVariables.getErrorMap().putError("salesTax.accountNumber", ERROR_REQUIRED, "Account Number");
            }
            if (salesTax.getFinancialDocumentGrossSalesAmount() == null) {
                valid &= false;
                GlobalVariables.getErrorMap().putError("salesTax.financialDocumentGrossSalesAmount", ERROR_REQUIRED, "Gross Sales Amount");
            }
            if (salesTax.getFinancialDocumentTaxableSalesAmount() == null) {
                valid &= false;
                GlobalVariables.getErrorMap().putError("salesTax.financialDocumentTaxableSalesAmount", ERROR_REQUIRED, "Taxable Sales Amount");
            }
            if (salesTax.getFinancialDocumentSaleDate() == null) {
                valid &= false;
                GlobalVariables.getErrorMap().putError("salesTax.financialDocumentSaleDate", ERROR_REQUIRED, "Sale Date");
            }
            if (StringUtils.isNotBlank(salesTax.getChartOfAccountsCode()) && StringUtils.isNotBlank(salesTax.getAccountNumber())) {

                if (boService.getReferenceIfExists(salesTax, "account") == null) {
                    valid &= false;
                    GlobalVariables.getErrorMap().putError("salesTax.accountNumber", ERROR_DOCUMENT_ACCOUNTING_LINE_SALES_TAX_INVALID_ACCOUNT, salesTax.getChartOfAccountsCode(), salesTax.getAccountNumber());

                }
            }
            if (!valid) {
                GlobalVariables.getErrorMap().putError("salesTax.chartOfAccountsCode", ERROR_DOCUMENT_ACCOUNTING_LINE_SALES_TAX_REQUIRED, account, objCd);
            }
        }
        GlobalVariables.getErrorMap().removeFromErrorPath(pathPrefix);
        return valid;
    }

    /**
     * This method removes the sales tax information from a line that no longer requires it
     * 
     * @param accountingLine
     */
    private void removeSalesTax(AccountingLine accountingLine) {
        SalesTax salesTax = accountingLine.getSalesTax();
        if (ObjectUtils.isNotNull(salesTax)) {
            accountingLine.setSalesTax(null);
        }
    }


    /**
     * This method checks to see if the given accounting needs sales tax and if it does it sets the salesTaxRequired variable on the
     * line If it doesn't and it has it then it removes the sales tax information from the line This method is called from the
     * execute() on all accounting lines that have been edited or lines that have already been added to the document, not on new
     * lines
     * 
     * @param transDoc
     * @param formLine
     * @param baseLine
     */
    private void handleSalesTaxRequired(AccountingDocument transDoc, AccountingLine formLine, boolean source, boolean newLine, int index) {
        boolean salesTaxRequired = isSalesTaxRequired(transDoc, formLine);
        if (salesTaxRequired) {
            formLine.setSalesTaxRequired(true);
            populateSalesTax(formLine);
        }
        else if (!salesTaxRequired && hasSalesTaxBeenEntered(formLine, source, newLine, index)) {
            // remove it if it has been added but is no longer required
            removeSalesTax(formLine);
        }
    }

    private boolean hasSalesTaxBeenEntered(AccountingLine accountingLine, boolean source, boolean newLine, int index) {
        boolean entered = true;
        String objCd = accountingLine.getFinancialObjectCode();
        String account = accountingLine.getAccountNumber();
        SalesTax salesTax = accountingLine.getSalesTax();
        if (ObjectUtils.isNull(salesTax)) {
            return false;
        }
        if (StringUtils.isBlank(salesTax.getChartOfAccountsCode())) {
            entered &= false;
        }
        if (StringUtils.isBlank(salesTax.getAccountNumber())) {
            entered &= false;
        }
        if (salesTax.getFinancialDocumentGrossSalesAmount() == null) {
            entered &= false;
        }
        if (salesTax.getFinancialDocumentTaxableSalesAmount() == null) {
            entered &= false;
        }
        if (salesTax.getFinancialDocumentSaleDate() == null) {
            entered &= false;
        }
        return entered;
    }

    /**
     * This method is called from the createDocument and processes through all the accouting lines and checks to see if they need
     * sales tax fields
     * 
     * @param kualiDocumentFormBase
     * @param baselineSourceLines
     */
    private void handleSalesTaxRequiredAllLines(KualiDocumentFormBase kualiDocumentFormBase, List<AccountingLine> baselineAcctingLines) {
        AccountingDocument accoutingDocument = (AccountingDocument) kualiDocumentFormBase.getDocument();
        int index = 0;
        for (AccountingLine accountingLine : baselineAcctingLines) {
            boolean source = false;
            if (accountingLine.isSourceAccountingLine()) {
                source = true;
            }
            handleSalesTaxRequired(accoutingDocument, accountingLine, source, false, index);
            index++;
        }

    }

    private boolean checkSalesTaxRequiredAllLines(KualiDocumentFormBase kualiDocumentFormBase, List<AccountingLine> baselineAcctingLines) {
        AccountingDocument accoutingDocument = (AccountingDocument) kualiDocumentFormBase.getDocument();
        boolean passed = true;
        int index = 0;
        for (AccountingLine accountingLine : baselineAcctingLines) {
            boolean source = false;
            if (accountingLine.isSourceAccountingLine()) {
                source = true;
            }
            passed &= checkSalesTax(accoutingDocument, accountingLine, source, false, index);
            index++;
        }
        return passed;
    }

    /**
     * This method refreshes the sales tax fields on a refresh or tab toggle so that all the information that was there before is
     * still there after a state change
     * 
     * @param form
     */
    private void refreshSalesTaxInfo(ActionForm form) {
        KualiAccountingDocumentFormBase accountingForm = (KualiAccountingDocumentFormBase) form;
        AccountingDocument document = (AccountingDocument) accountingForm.getDocument();
        List sourceLines = document.getSourceAccountingLines();
        List targetLines = document.getTargetAccountingLines();
        handleSalesTaxRequiredAllLines(accountingForm, sourceLines);
        handleSalesTaxRequiredAllLines(accountingForm, targetLines);

        AccountingLine newTargetLine = accountingForm.getNewTargetLine();
        AccountingLine newSourceLine = accountingForm.getNewSourceLine();
        if (newTargetLine != null) {
            handleSalesTaxRequired(document, newTargetLine, false, true, 0);
        }
        if (newSourceLine != null) {
            handleSalesTaxRequired(document, newSourceLine, true, true, 0);
        }
    }

    /**
     * This method populates the sales tax for a given accounting line with the appropriate primary key fields from the accounting
     * line since OJB won't do it automatically for us
     * 
     * @param line
     */
    private void populateSalesTax(AccountingLine line) {
        SalesTax salesTax = line.getSalesTax();

        if (ObjectUtils.isNotNull(salesTax)) {
            salesTax.setDocumentNumber(line.getDocumentNumber());
            salesTax.setFinancialDocumentLineTypeCode(line.getFinancialDocumentLineTypeCode());
            salesTax.setFinancialDocumentLineNumber(line.getSequenceNumber());
        }
    }

    @Override
    public ActionForward performLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // parse out the business object name from our methodToCall parameter
        String fullParameter = (String) request.getAttribute(KFSConstants.METHOD_TO_CALL_ATTRIBUTE);
        String boClassName = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_BOPARM_LEFT_DEL, KFSConstants.METHOD_TO_CALL_BOPARM_RIGHT_DEL);

        if (!StringUtils.equals(boClassName, GeneralLedgerPendingEntry.class.getName())) {
            return super.performLookup(mapping, form, request, response);
        }

        String path = super.performLookup(mapping, form, request, response).getPath();
        path = path.replaceFirst(KFSConstants.LOOKUP_ACTION, KFSConstants.GL_MODIFIED_INQUIRY_ACTION);

        return new ActionForward(path, true);
    }
}