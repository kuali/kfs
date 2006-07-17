/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
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
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.module.financial.bo.VoucherAccountingLineHelper;
import org.kuali.module.financial.bo.VoucherAccountingLineHelperBase;
import org.kuali.module.financial.document.VoucherDocument;
import org.kuali.module.financial.web.struts.form.VoucherForm;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class piggy backs on all of the functionality in the KualiTransactionalDocumentActionBase but is necessary for this document
 * type. Vouchers are unique in that they define several fields that aren't typically used by the other financial transaction
 * processing eDocs (i.e. external system fields, object type override, credit and debit amounts).
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class VoucherAction extends KualiTransactionalDocumentActionBase {
    // used to determine which way the change balance type action is switching
    // these are local constants only used within this action class
    // these should not be used outside of this class

    /**
     * We want to keep the bad data for the voucher.
     * 
     * @see org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase#revertAccountingLine(org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase,
     *      int, org.kuali.core.bo.AccountingLine, org.kuali.core.bo.AccountingLine)
     */
    protected boolean revertAccountingLine(KualiTransactionalDocumentFormBase transForm, int revertIndex, AccountingLine originalLine, AccountingLine brokenLine) {
        boolean reverted = super.revertAccountingLine(transForm, revertIndex, originalLine, brokenLine);

        if (reverted) {
            VoucherForm vForm = (VoucherForm) transForm;
            VoucherAccountingLineHelper helper = vForm.getVoucherLineHelper(revertIndex);

            String debitCreditCode = originalLine.getDebitCreditCode();
            if (StringUtils.equals(debitCreditCode, Constants.GL_DEBIT_CODE)) {
                helper.setDebit(originalLine.getAmount());
                helper.setCredit(Constants.ZERO);
            }
            else if (StringUtils.equals(debitCreditCode, Constants.GL_CREDIT_CODE)) {
                helper.setDebit(Constants.ZERO);
                helper.setCredit(originalLine.getAmount());
            }
            // intentionally ignoring the case where debitCreditCode is neither debit nor credir
        }

        return reverted;
    }

    /**
     * Overrides to call super, and then to repopulate the credit/debit amounts b/c the credit/debit code might change during a
     * voucher error correction.
     * 
     * @see org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase#correct(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
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
     * 
     * @see org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase#insertSourceLine(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
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
     * 
     * @see org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase#deleteSourceLine(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
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
                selectedAccountingPeriod += SpringServiceLocator.getDateTimeService().getCurrentFiscalYear().toString();
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
                if (sourceAccountingLine.getDebitCreditCode().equals(Constants.GL_DEBIT_CODE)) {
                    avAcctLineHelperForm.setDebit(sourceAccountingLine.getAmount());
                    avAcctLineHelperForm.setCredit(Constants.ZERO);
                }
                else if (sourceAccountingLine.getDebitCreditCode().equals(Constants.GL_CREDIT_CODE)) {
                    avAcctLineHelperForm.setCredit(sourceAccountingLine.getAmount());
                    avAcctLineHelperForm.setDebit(Constants.ZERO);
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

        String question = request.getParameter(Constants.QUESTION_INST_ATTRIBUTE_NAME);
        KualiConfigurationService kualiConfiguration = SpringServiceLocator.getKualiConfigurationService();

        if (question == null) { // question hasn't been asked
            String currencyFormattedDebitTotal = (String) new CurrencyFormatter().format(avDoc.getDebitTotal());
            String currencyFormattedCreditTotal = (String) new CurrencyFormatter().format(avDoc.getCreditTotal());
            String currencyFormattedTotal = (String) new CurrencyFormatter().format(avDoc.getTotal());
            String message = "";
            message = StringUtils.replace(kualiConfiguration.getPropertyString(KeyConstants.QUESTION_ROUTE_OUT_OF_BALANCE_JV_DOC), "{0}", currencyFormattedDebitTotal);
            message = StringUtils.replace(message, "{1}", currencyFormattedCreditTotal);

            // now transfer control over to the question component
            return this.performQuestionWithoutInput(mapping, form, request, response, Constants.JOURNAL_VOUCHER_ROUTE_OUT_OF_BALANCE_DOCUMENT_QUESTION, message, Constants.CONFIRMATION_QUESTION, Constants.ROUTE_METHOD, "");
        }
        else {
            String buttonClicked = request.getParameter(Constants.QUESTION_CLICKED_BUTTON);
            if ((Constants.JOURNAL_VOUCHER_ROUTE_OUT_OF_BALANCE_DOCUMENT_QUESTION.equals(question)) && ConfirmationQuestion.NO.equals(buttonClicked)) {
                GlobalVariables.getMessageList().add(KeyConstants.MESSAGE_JV_CANCELLED_ROUTE);
                return mapping.findForward(Constants.MAPPING_BASIC);
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

        return mapping.findForward(Constants.MAPPING_BASIC);
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
