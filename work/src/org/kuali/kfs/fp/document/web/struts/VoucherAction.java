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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.document.AmountTotaling;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase;
import org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase;
import org.kuali.module.financial.bo.VoucherAccountingLineHelper;
import org.kuali.module.financial.bo.VoucherAccountingLineHelperBase;
import org.kuali.module.financial.document.VoucherDocument;
import org.kuali.module.financial.web.struts.form.VoucherForm;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class piggy backs on all of the functionality in the KualiTransactionalDocumentActionBase but is necessary for this document
 * type. Vouchers are unique in that they define several fields that aren't typically used by the other financial transaction
 * processing eDocs (i.e. external system fields, object type override, credit and debit amounts).
 */
public class VoucherAction extends KualiAccountingDocumentActionBase {
    // used to determine which way the change balance type action is switching
    // these are local constants only used within this action class
    // these should not be used outside of this class

    /**
     *  We want to keep the bad data for the voucher.
     */
    @Override
    protected boolean revertAccountingLine(KualiAccountingDocumentFormBase transForm, int revertIndex, AccountingLine originalLine, AccountingLine brokenLine) {
        boolean reverted = super.revertAccountingLine(transForm, revertIndex, originalLine, brokenLine);

        if (reverted) {
            VoucherForm vForm = (VoucherForm) transForm;
            VoucherAccountingLineHelper helper = vForm.getVoucherLineHelper(revertIndex);

            String debitCreditCode = originalLine.getDebitCreditCode();
            if (StringUtils.equals(debitCreditCode, KFSConstants.GL_DEBIT_CODE)) {
                helper.setDebit(originalLine.getAmount());
                helper.setCredit(KFSConstants.ZERO);
            }
            else if (StringUtils.equals(debitCreditCode, KFSConstants.GL_CREDIT_CODE)) {
                helper.setDebit(KFSConstants.ZERO);
                helper.setCredit(originalLine.getAmount());
            }
            // intentionally ignoring the case where debitCreditCode is neither debit nor credir
        }

        return reverted;
    }

    /**
     * Overrides to call super, and then to repopulate the credit/debit amounts b/c the credit/debit code might change during a
     * voucher error correction.
     * @see org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase#correct(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward correct(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward actionForward = super.correct(mapping, form, request, response);

        VoucherForm vForm = (VoucherForm) form;

        // now make sure to repopulate credit/debit amounts
        populateAllVoucherAccountingLineHelpers(vForm);

        return actionForward;
    }

    /**
     * Overrides parent to first populate the new source line with the correct debit or credit value, then it calls the parent's
     * implementation.
     * @see org.kuali.module.financial.web.struts.action.KualiFinancialDocumentActionBase#insertSourceLine(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward insertSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // cast the form to the right pojo
        VoucherForm voucherForm = (VoucherForm) form;

        // call the super's method
        ActionForward actionForward = super.insertSourceLine(mapping, form, request, response);

        if (GlobalVariables.getErrorMap().getErrorCount() == 0) {
            // since no exceptions were thrown, the add succeeded, so we have to re-init the new credit and debit
            // attributes, and add a new instance of a helperLine to the helperLines list
            VoucherAccountingLineHelper helperLine = populateNewVoucherAccountingLineHelper(voucherForm);
            voucherForm.getVoucherLineHelpers().add(helperLine);

            // now reset the debit and credit fields for adds
            voucherForm.setNewSourceLineDebit(KualiDecimal.ZERO);
            voucherForm.setNewSourceLineCredit(KualiDecimal.ZERO);
        }

        return actionForward;
    }

    /**
     * Overrides parent to remove the associated helper line also, and then it call the parent's implementation.
     * @see org.kuali.module.financial.web.struts.action.KualiFinancialDocumentActionBase#deleteSourceLine(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward deleteSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // cast the form to the right pojo
        VoucherForm voucherForm = (VoucherForm) form;

        // call the super's method
        ActionForward actionForward = super.deleteSourceLine(mapping, voucherForm, request, response);

        // now remove the associated helper line
        int index = getLineToDelete(request);
        if (voucherForm.getVoucherLineHelpers() != null && voucherForm.getVoucherLineHelpers().size() > index) {
            voucherForm.getVoucherLineHelpers().remove(getLineToDelete(request));
        }

        return actionForward;
    }

    /**
     * Overrides the parent to make sure that the AV specific accounting line helper forms are properly populated when the document
     * is first loaded. This first calls super, then populates the helper objects.
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);
        VoucherForm voucherForm = (VoucherForm) kualiDocumentFormBase;

        populateAllVoucherAccountingLineHelpers(voucherForm);
        voucherForm.setNewSourceLineCredit(KualiDecimal.ZERO);
        voucherForm.setNewSourceLineDebit(KualiDecimal.ZERO);

        // always wipe out the new source line
        voucherForm.setNewSourceLine(null);

        // reload the accounting period selections since now we have data in the document bo
        populateSelectedAccountingPeriod(voucherForm.getVoucherDocument(), voucherForm);
    }

    /**
     * This method parses the accounting period value from the bo and builds the right string to pass to the form object as the
     * selected value.
     * 
     * @param voucherDocument
     * @param voucherForm
     */
    protected void populateSelectedAccountingPeriod(VoucherDocument voucherDocument, VoucherForm voucherForm) {
        if (StringUtils.isNotBlank(voucherDocument.getPostingPeriodCode())) {
            String selectedAccountingPeriod = voucherDocument.getPostingPeriodCode();
            if (null != voucherDocument.getPostingYear()) {
                selectedAccountingPeriod += voucherDocument.getPostingYear().toString();
            }
            else {
                selectedAccountingPeriod += SpringServiceLocator.getUniversityDateService().getCurrentFiscalYear().toString();
            }
            voucherForm.setSelectedAccountingPeriod(selectedAccountingPeriod);
        }
    }

    /**
     * This populates a new helperLine instance with the one that was just added so that the new instance can be added to the
     * helperLines list.
     * 
     * @param voucherForm
     * @return VoucherAccountingLineHelper
     */
    protected VoucherAccountingLineHelper populateVoucherAccountingLineHelper(VoucherForm voucherForm) {
        VoucherAccountingLineHelper helperLine = new VoucherAccountingLineHelperBase();

        KualiDecimal debitAmount = voucherForm.getNewSourceLineDebit();
        if (debitAmount != null && StringUtils.isNotBlank(debitAmount.toString())) {
            helperLine.setDebit(debitAmount);
        }

        KualiDecimal creditAmount = voucherForm.getNewSourceLineCredit();
        if (creditAmount != null && StringUtils.isNotBlank(creditAmount.toString())) {
            helperLine.setCredit(creditAmount);
        }

        return helperLine;
    }

    /**
     * This method builds the corresponding list of voucher acounting line helper objects so that a user can differentiate between
     * credit and debit fields. It does this by iterating over each source accounting line (what the voucher uses) looking at the
     * debit/credit code and then populateingLineHelpers a corresponding helper form instance with the amount in the appropriate
     * amount field - credit or debit.
     * 
     * @param voucherForm
     */
    protected void populateAllVoucherAccountingLineHelpers(VoucherForm voucherForm) {
        // make sure the journal voucher accounting line helper form list is populated properly
        ArrayList voucherLineHelpers = (ArrayList) voucherForm.getVoucherLineHelpers();

        // make sure the helper list is the right size
        VoucherDocument vDoc = (VoucherDocument) voucherForm.getTransactionalDocument();
        int size = vDoc.getSourceAccountingLines().size();
        voucherLineHelpers.ensureCapacity(size);

        // iterate through each source accounting line and initialize the helper form lines appropriately
        for (int i = 0; i < size; i++) {
            // get the bo's accounting line at the right index
            SourceAccountingLine sourceAccountingLine = vDoc.getSourceAccountingLine(i);

            // instantiate a new helper form to use for populating the helper form list
            VoucherAccountingLineHelper avAcctLineHelperForm = voucherForm.getVoucherLineHelper(i);

            // figure whether we need to set the credit amount or the debit amount
            if (StringUtils.isNotBlank(sourceAccountingLine.getDebitCreditCode())) {
                if (sourceAccountingLine.getDebitCreditCode().equals(KFSConstants.GL_DEBIT_CODE)) {
                    avAcctLineHelperForm.setDebit(sourceAccountingLine.getAmount());
                    avAcctLineHelperForm.setCredit(KFSConstants.ZERO);
                }
                else if (sourceAccountingLine.getDebitCreditCode().equals(KFSConstants.GL_CREDIT_CODE)) {
                    avAcctLineHelperForm.setCredit(sourceAccountingLine.getAmount());
                    avAcctLineHelperForm.setDebit(KFSConstants.ZERO);
                }
            }
        }
    }


    /**
     * This helper method determines from the request object instance whether or not the user has been prompted about the journal
     * being out of balance. If they haven't, then the method will build the appropriate message given the state of the document and
     * return control to the question component so that the user receives the "yes"/"no" prompt. If the question has been asked, the
     * we evaluate the user's answer and direct the flow appropriately. If they answer with a "No", then we build out a message
     * stating that they chose that value and return an ActionForward of a MAPPING_BASIC which keeps them at the same page that they
     * were on. If they choose "Yes", then we return a null ActionForward, which the calling action method recognizes as a "Yes" and
     * continues on processing the "Route."
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    protected ActionForward processRouteOutOfBalanceDocumentConfirmationQuestion(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        VoucherForm vForm = (VoucherForm) form;
        VoucherDocument avDoc = vForm.getVoucherDocument();

        String question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        KualiConfigurationService kualiConfiguration = SpringServiceLocator.getKualiConfigurationService();

        if (question == null) { // question hasn't been asked
            String currencyFormattedDebitTotal = (String) new CurrencyFormatter().format(avDoc.getDebitTotal());
            String currencyFormattedCreditTotal = (String) new CurrencyFormatter().format(avDoc.getCreditTotal());
            String currencyFormattedTotal = (String) new CurrencyFormatter().format(((AmountTotaling) avDoc).getTotalDollarAmount());
            String message = "";
            message = StringUtils.replace(kualiConfiguration.getPropertyString(KFSKeyConstants.QUESTION_ROUTE_OUT_OF_BALANCE_JV_DOC), "{0}", currencyFormattedDebitTotal);
            message = StringUtils.replace(message, "{1}", currencyFormattedCreditTotal);

            // now transfer control over to the question component
            return this.performQuestionWithoutInput(mapping, form, request, response, KFSConstants.JOURNAL_VOUCHER_ROUTE_OUT_OF_BALANCE_DOCUMENT_QUESTION, message, KFSConstants.CONFIRMATION_QUESTION, KFSConstants.ROUTE_METHOD, "");
        }
        else {
            String buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if ((KFSConstants.JOURNAL_VOUCHER_ROUTE_OUT_OF_BALANCE_DOCUMENT_QUESTION.equals(question)) && ConfirmationQuestion.NO.equals(buttonClicked)) {
                GlobalVariables.getMessageList().add(KFSKeyConstants.MESSAGE_JV_CANCELLED_ROUTE);
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }
        return null;
    }

    /**
     * This populates a new helperLine instance with the one that was just added so that the new instance can be added to the
     * helperLines list.
     * 
     * @param voucherForm
     * @return voucherAccountingLineHelper
     */
    protected VoucherAccountingLineHelper populateNewVoucherAccountingLineHelper(VoucherForm voucherForm) {
        VoucherAccountingLineHelper helperLine = new VoucherAccountingLineHelperBase();

        KualiDecimal debitAmount = voucherForm.getNewSourceLineDebit();
        if (debitAmount != null && StringUtils.isNotBlank(debitAmount.toString())) {
            helperLine.setDebit(debitAmount);
        }

        KualiDecimal creditAmount = voucherForm.getNewSourceLineCredit();
        if (creditAmount != null && StringUtils.isNotBlank(creditAmount.toString())) {
            helperLine.setCredit(creditAmount);
        }

        return helperLine;
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
    @Override
    public ActionForward uploadSourceLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException, IOException {
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
    @Override
    protected void uploadAccountingLines(boolean isSource, ActionForm form) throws FileNotFoundException, IOException {
        super.uploadAccountingLines(isSource, form);

        populateAllVoucherAccountingLineHelpers((VoucherForm) form);
    }

}
