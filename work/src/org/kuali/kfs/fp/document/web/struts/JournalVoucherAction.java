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
import org.kuali.core.authorization.DocumentAuthorizer;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.document.Document;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.financial.bo.JournalVoucherAccountingLineHelper;
import org.kuali.module.financial.document.JournalVoucherDocument;
import org.kuali.module.financial.web.struts.form.JournalVoucherForm;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class piggy backs on all of the functionality in the KualiTransactionalDocumentActionBase but is necessary for this document
 * type. The Journal Voucher is unique in that it defines several fields that aren't typically used by the other financial
 * transaction processing eDocs (i.e. external system fields, object type override, credit and debit amounts).
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class JournalVoucherAction extends KualiTransactionalDocumentActionBase {
    private static final String EXTERNAL_ENCUMBRANCE = "EX";

    // upload file format templates
    private static final String ACCOUNTING_LINE_UPLOAD_EXTERNAL_ENCUMBRANCE = "JV-Accounting-Line-Upload-External-Encumbrance-Balance-Type.csv";
    private static final String ACCOUNTING_LINE_UPLOAD_OFFSET_GENERATION = "JV-Accounting-Line-Upload-Offset-Generation-Balance-Type.csv";
    private static final String ACCOUNTING_LINE_UPLOAD_NON_OFFSET_GENERATION = "JV-Accounting-Line-Upload-Non-Offset-Generation-Balance-Type.csv";

    // used to determine which way the change balance type action is switching
    // these are local constants only used within this action class
    // these should not be used outside of this class
    private static final int CREDIT_DEBIT_TO_SINGLE_AMT_MODE = 0;
    private static final int SINGLE_AMT_TO_CREDIT_DEBIT_MODE = 1;
    private static final int EXT_ENCUMB_TO_NON_EXT_ENCUMB = 0;
    private static final int NON_EXT_ENCUMB_TO_EXT_ENCUMB = 1;
    private static final int NO_MODE_CHANGE = -1;
    private int balanceTypeAmountChangeMode = NO_MODE_CHANGE;
    private int balanceTypeExternalEncumbranceChangeMode = NO_MODE_CHANGE;

    /**
     * Overrides the parent and then calls the super method after building the array lists for valid accounting periods and balance
     * types.
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
     *      HttpServletResponse response)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        JournalVoucherForm journalVoucherForm = (JournalVoucherForm) form;

        // now check to see if the balance type was changed and if so, we want to
        // set the method to call so that the appropriate action can be invoked
        // had to do it this way b/c the changing of the drop down causes the page to re-submit
        // and couldn't use a hidden field called "methodToCall" b/c it screwed everything
        // up
        ActionForward returnForward;
        if (StringUtils.isNotBlank(journalVoucherForm.getOriginalBalanceType())
                && !journalVoucherForm.getSelectedBalanceType().getCode().equals(journalVoucherForm.getOriginalBalanceType())) {
            returnForward = super.dispatchMethod(mapping, form, request, response,
                    Constants.CHANGE_JOURNAL_VOUCHER_BALANCE_TYPE_METHOD);
            // must call this here, because execute in the super method will never have control for this particular action
            // this is called in the parent by super.execute()
            Document document = journalVoucherForm.getDocument();
            DocumentAuthorizer documentAuthorizer = SpringServiceLocator.getDocumentAuthorizationService().getDocumentAuthorizer(
                    document);
            journalVoucherForm.populateAuthorizationFields(documentAuthorizer);
        }
        else { // otherwise call the super
            returnForward = super.execute(mapping, journalVoucherForm, request, response);
        }
        return returnForward;
    }

    /**
     * We want to keep the bad data for the JV.
     */
    protected boolean revertAccountingLine(KualiTransactionalDocumentFormBase transForm, int revertIndex,
            AccountingLine originalLine, AccountingLine brokenLine) {
        boolean reverted = super.revertAccountingLine(transForm, revertIndex, originalLine, brokenLine);

        if (reverted) {
            JournalVoucherForm jvForm = (JournalVoucherForm) transForm;
            JournalVoucherAccountingLineHelper helper = jvForm.getJournalLineHelper(revertIndex);

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
     * Overrides parent to first populate the new source line with the correct debit or credit value, then it calls the parent's
     * implementation.
     * 
     * @see org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase#insertSourceLine(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward insertSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        // cast the form to the right pojo
        JournalVoucherForm journalVoucherForm = (JournalVoucherForm) form;

        // call the super's method
        ActionForward actionForward = super.insertSourceLine(mapping, form, request, response);

        if (GlobalVariables.getErrorMap().getErrorCount() == 0) {
            // since no exceptions were thrown, the add succeeded, so we have to re-init the new credit and debit
            // attributes, and add a new instance of a helperLine to the helperLines list
            JournalVoucherAccountingLineHelper helperLine = populateNewJournalVoucherAccountingLineHelper(journalVoucherForm);
            journalVoucherForm.getJournalLineHelpers().add(helperLine);

            // now reset the debit and credit fields for adds
            journalVoucherForm.setNewSourceLineDebit(new KualiDecimal(0));
            journalVoucherForm.setNewSourceLineCredit(new KualiDecimal(0));
        }

        return actionForward;
    }

    /**
     * Overrides parent to remove the associated helper line also, and then it call the parent's implementation.
     * 
     * @see org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase#deleteSourceLine(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward deleteSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        // cast the form to the right pojo
        JournalVoucherForm journalVoucherForm = (JournalVoucherForm) form;

        // call the super's method
        ActionForward actionForward = super.deleteSourceLine(mapping, form, request, response);

        // now remove the associated helper line
        int index = getLineToDelete(request);
        if (journalVoucherForm.getJournalLineHelpers() != null && journalVoucherForm.getJournalLineHelpers().size() > index) {
            journalVoucherForm.getJournalLineHelpers().remove(getLineToDelete(request));
        }

        return actionForward;
    }

    /**
     * Overrides the parent to iterate through all source accounting lines and re-populate the credit and debit code appropriately
     * in case the user flip-flopped any of the debit/credit amounts or changed the amounts.
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#route(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        JournalVoucherForm journalVoucherForm = (JournalVoucherForm) form;

        // process the question but we need to make sure there are lines and then check to see if it's not balanced
        JournalVoucherDocument jvDoc = journalVoucherForm.getJournalVoucherDocument();
        if (jvDoc.getSourceAccountingLines().size() > 0 && jvDoc.getTotal().compareTo(Constants.ZERO) != 0) {
            // it's not in "balance"
            ActionForward returnForward = processRouteOutOfBalanceDocumentConfirmationQuestion(mapping, form, request, response);

            // if not null, then the question component either has control of the flow and needs to ask its questions
            // or the person chose the "cancel" or "no" button
            // otherwise we have control
            if (returnForward != null) {
                return returnForward;
            }
        }
        // now call the route method
        return super.route(mapping, form, request, response);
    }

    /**
     * This method processes a change in the balance type for a Journal Voucher document - from either a offset generation balance
     * type to a non-offset generation balance type or visa-versa.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward changeBalanceType(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        JournalVoucherForm journalVoucherForm = (JournalVoucherForm) form;

        // figure out which way the balance type is changing
        determineBalanceTypeChangeModes(journalVoucherForm);

        // process the question
        if (balanceTypeAmountChangeMode != NO_MODE_CHANGE || balanceTypeExternalEncumbranceChangeMode != NO_MODE_CHANGE) {
            ActionForward returnForward = processChangeBalanceTypeConfirmationQuestion(mapping, form, request, response);

            // if not null, then the question component either has control of the flow and needs to ask its questions
            // or the person choose the "cancel" or "no" button
            // otherwise we have control
            if (returnForward != null) {
                return returnForward;
            }
            else {
                // deal with balance type changes
                // first amount change
                if (balanceTypeAmountChangeMode == CREDIT_DEBIT_TO_SINGLE_AMT_MODE) {
                    switchFromCreditDebitModeToSingleAmountMode(journalVoucherForm);
                }
                else if (balanceTypeAmountChangeMode == SINGLE_AMT_TO_CREDIT_DEBIT_MODE) {
                    switchFromSingleAmountModeToCreditDebitMode(journalVoucherForm);
                }

                // then look to see if the external encumbrance was involved
                if (balanceTypeExternalEncumbranceChangeMode == EXT_ENCUMB_TO_NON_EXT_ENCUMB) {
                    switchFromExternalEncumbranceModeToNonExternalEncumbrance(journalVoucherForm);
                }
            }
        }
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * This method will determine which balance type amount mode to switch to. A change in the balance type selection will
     * eventually invoke this mechanism, which looks at the old balance type value, and the new balance type value to determine what
     * the next mode is.
     * 
     * @param journalVoucherForm
     * @throws Exception
     */
    private void determineBalanceTypeChangeModes(JournalVoucherForm journalVoucherForm) throws Exception {
        // retrieve fully populated balance type instances
        BalanceTyp origBalType = getPopulatedBalanceTypeInstance(journalVoucherForm.getOriginalBalanceType());
        BalanceTyp newBalType = getPopulatedBalanceTypeInstance(journalVoucherForm.getSelectedBalanceType().getCode());

        // figure out which ways we are switching the modes
        // first deal with amount changes
        if (origBalType.isFinancialOffsetGenerationIndicator() && !newBalType.isFinancialOffsetGenerationIndicator()) { // credit/debit
            // mode -->
            // single
            // amount
            // mode
            balanceTypeAmountChangeMode = CREDIT_DEBIT_TO_SINGLE_AMT_MODE;
        }
        else if (!origBalType.isFinancialOffsetGenerationIndicator() && newBalType.isFinancialOffsetGenerationIndicator()) { // single
            // amount
            // mode
            // -->
            // credit/debit
            // mode
            balanceTypeAmountChangeMode = SINGLE_AMT_TO_CREDIT_DEBIT_MODE;
        }
        else {
            balanceTypeAmountChangeMode = NO_MODE_CHANGE;
        }

        // then deal with external encumbrance changes
        if (origBalType.getCode().equals(Constants.BALANCE_TYPE_EXTERNAL_ENCUMBRANCE)
                && !newBalType.getCode().equals(Constants.BALANCE_TYPE_EXTERNAL_ENCUMBRANCE)) {
            balanceTypeExternalEncumbranceChangeMode = EXT_ENCUMB_TO_NON_EXT_ENCUMB;
        }
        else if (!origBalType.getCode().equals(Constants.BALANCE_TYPE_EXTERNAL_ENCUMBRANCE)
                && newBalType.getCode().equals(Constants.BALANCE_TYPE_EXTERNAL_ENCUMBRANCE)) {
            balanceTypeExternalEncumbranceChangeMode = NON_EXT_ENCUMB_TO_EXT_ENCUMB;
        }
        else {
            balanceTypeExternalEncumbranceChangeMode = NO_MODE_CHANGE;
        }
    }

    /**
     * This method takes control from the changeBalanceType action method in order to present a question prompt to the user so that
     * they can confirm the change in balance type.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    private ActionForward processChangeBalanceTypeConfirmationQuestion(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        JournalVoucherForm jvForm = (JournalVoucherForm) form;
        JournalVoucherDocument jvDoc = jvForm.getJournalVoucherDocument();

        // only want to present the confirmation question to the user if there are any
        // accouting lines, because that is the only impact
        if (jvDoc.getSourceAccountingLines().size() != 0) {
            String question = request.getParameter(Constants.QUESTION_INST_ATTRIBUTE_NAME);
            KualiConfigurationService kualiConfiguration = SpringServiceLocator.getKualiConfigurationService();

            if (question == null) { // question hasn't been asked
                String message = buildBalanceTypeChangeConfirmationMessage(jvForm, kualiConfiguration);

                // now transfer control over to the question component
                return this.performQuestion(mapping, form, request, response,
                        Constants.JOURNAL_VOUCHER_CHANGE_BALANCE_TYPE_QUESTION, message, Constants.CONFIRMATION_QUESTION,
                        Constants.CHANGE_JOURNAL_VOUCHER_BALANCE_TYPE_METHOD);
            }
            else {
                String buttonClicked = request.getParameter(Constants.QUESTION_CLICKED_BUTTON);
                if ((Constants.JOURNAL_VOUCHER_CHANGE_BALANCE_TYPE_QUESTION.equals(question))
                        && ConfirmationQuestion.NO.equals(buttonClicked)) {
                    // if no button clicked keep the old value and reload doc
                    BalanceTyp origBalType = getPopulatedBalanceTypeInstance(jvForm.getOriginalBalanceType());
                    jvForm.setSelectedBalanceType(origBalType);
                    jvDoc.setBalanceType(origBalType);
                    jvDoc.setBalanceTypeCode(origBalType.getCode());
                    return mapping.findForward(Constants.MAPPING_BASIC);
                }
            }
        }
        return null;
    }

    /**
     * This method will setup the message that will get displayed to the user when they are asked to confirm the balance type
     * change. The message is tuned to the particular context, the value chosen, and also the previous value. It also combines with
     * the core part of the message which is part of the ApplicationResources.properties file.
     * 
     * @param jvForm
     * @param kualiConfiguration
     * @return The message to display to the user in the question prompt window.
     * @throws Exception
     */
    private String buildBalanceTypeChangeConfirmationMessage(JournalVoucherForm jvForm, KualiConfigurationService kualiConfiguration)
            throws Exception {
        String message = new String("");
        // grab the right message from the ApplicationResources.properties file depending upon the balance type switching mode
        if (balanceTypeAmountChangeMode == SINGLE_AMT_TO_CREDIT_DEBIT_MODE) {
            message = kualiConfiguration
                    .getPropertyString(KeyConstants.QUESTION_CHANGE_JV_BAL_TYPE_FROM_SINGLE_AMT_TO_CREDIT_DEBIT_MODE);
            // see if we need the extra bit about the external encumbrance
            String newMessage = new String("");
            if (balanceTypeExternalEncumbranceChangeMode == NON_EXT_ENCUMB_TO_EXT_ENCUMB) {
                newMessage = StringUtils
                        .replace(
                                message,
                                "{3}",
                                kualiConfiguration
                                        .getPropertyString(KeyConstants.QUESTION_CHANGE_JV_BAL_TYPE_FROM_SINGLE_AMT_TO_EXT_ENCUMB_CREDIT_DEBIT_MODE));
            }
            else {
                newMessage = StringUtils.replace(message, "{3}", "");
            }
            message = new String(newMessage);
        }
        else if (balanceTypeAmountChangeMode == CREDIT_DEBIT_TO_SINGLE_AMT_MODE) {
            message = kualiConfiguration
                    .getPropertyString(KeyConstants.QUESTION_CHANGE_JV_BAL_TYPE_FROM_CREDIT_DEBIT_TO_SINGLE_AMT_MODE);
            // see if we need the extra bit about the external encumbrance
            String newMessage = new String("");
            if (balanceTypeExternalEncumbranceChangeMode == EXT_ENCUMB_TO_NON_EXT_ENCUMB) {
                newMessage = StringUtils
                        .replace(
                                message,
                                "{3}",
                                kualiConfiguration
                                        .getPropertyString(KeyConstants.QUESTION_CHANGE_JV_BAL_TYPE_FROM_EXT_ENCUMB_CREDIT_DEBIT_TO_SINGLE_AMT_MODE));
            }
            else {
                newMessage = StringUtils.replace(message, "{3}", "");
            }
            message = new String(newMessage);
        }
        else if (balanceTypeExternalEncumbranceChangeMode == EXT_ENCUMB_TO_NON_EXT_ENCUMB) {
            message = kualiConfiguration
                    .getPropertyString(KeyConstants.QUESTION_CHANGE_JV_BAL_TYPE_FROM_EXT_ENCUMB_TO_NON_EXT_ENCUMB);
        }
        else if (balanceTypeExternalEncumbranceChangeMode == NON_EXT_ENCUMB_TO_EXT_ENCUMB) {
            message = kualiConfiguration
                    .getPropertyString(KeyConstants.QUESTION_CHANGE_JV_BAL_TYPE_FROM_NON_EXT_ENCUMB_TO_EXT_ENCUMB);
        }

        // retrieve fully populated balance type instances
        BalanceTyp origBalType = getPopulatedBalanceTypeInstance(jvForm.getOriginalBalanceType());
        BalanceTyp newBalType = getPopulatedBalanceTypeInstance(jvForm.getSelectedBalanceType().getCode());

        // now complete building of the message
        String replacement = "\"" + origBalType.getCode() + "-" + origBalType.getName() + "\"";
        String newMessage = StringUtils.replace(message, "{0}", replacement);

        replacement = "\"" + newBalType.getCode() + "-" + newBalType.getName() + "\"";
        String finalMessage = StringUtils.replace(newMessage, "{1}", replacement);

        return finalMessage;
    }

    /**
     * This method will fully populate a balance type given the passed in code, by calling the business object service that
     * retrieves the rest of the instances' information.
     * 
     * @param balanceTypeCode
     * @return BalanceTyp
     */
    private BalanceTyp getPopulatedBalanceTypeInstance(String balanceTypeCode) {
        // now we have to get the code and the name of the original and new balance types
        return SpringServiceLocator.getBalanceTypService().getBalanceTypByCode(balanceTypeCode);
    }

    /**
     * This method will clear out the source line values that aren't needed for the "Single Amount" mode.
     * 
     * @param journalVoucherForm
     */
    private void switchFromSingleAmountModeToCreditDebitMode(JournalVoucherForm journalVoucherForm) {
        // going from single amount to credit/debit view so we want to blank out the amount and the extra "reference" fields
        // that the single amount view uses
        JournalVoucherDocument jvDoc = (JournalVoucherDocument) journalVoucherForm.getTransactionalDocument();
        ArrayList sourceLines = (ArrayList) jvDoc.getSourceAccountingLines();
        ArrayList helperLines = (ArrayList) journalVoucherForm.getJournalLineHelpers();
        helperLines.clear(); // reset so we can add in fresh empty ones

        // make sure that there is enough space in the list
        helperLines.ensureCapacity(sourceLines.size());

        KualiDecimal ZERO = new KualiDecimal("0.00");
        for (int i = 0; i < sourceLines.size(); i++) {
            SourceAccountingLine sourceLine = (SourceAccountingLine) sourceLines.get(i);
            sourceLine.setAmount(ZERO);
            sourceLine.setDebitCreditCode(null); // will be needed, reset to make sure

            helperLines.add(new JournalVoucherAccountingLineHelper()); // populate with a fresh new empty object
        }
    }

    /**
     * This method will clear out the extra "reference" fields that the external encumbrance balance type uses, but will leave the
     * amounts since we aren't changing the offset generation code stuff.
     * 
     * @param journalVoucherForm
     */
    private void switchFromExternalEncumbranceModeToNonExternalEncumbrance(JournalVoucherForm journalVoucherForm) {
        // going from external encumbrance view to non external encumbrance view, so we want to blank out the extra "reference"
        // fields
        JournalVoucherDocument jvDoc = (JournalVoucherDocument) journalVoucherForm.getTransactionalDocument();
        ArrayList sourceLines = (ArrayList) jvDoc.getSourceAccountingLines();

        for (int i = 0; i < sourceLines.size(); i++) {
            SourceAccountingLine sourceLine = (SourceAccountingLine) sourceLines.get(i);
            sourceLine.setReferenceOriginCode(null); // won't be needed in this mode
            sourceLine.setReferenceNumber(null); // won't be needed in this mode
            sourceLine.setReferenceTypeCode(null); // won't be needed in this mode
        }
    }

    /**
     * This method will clear out the source line values that aren't needed for the "Credit/Debit" mode.
     * 
     * @param journalVoucherForm
     */
    private void switchFromCreditDebitModeToSingleAmountMode(JournalVoucherForm journalVoucherForm) {
        // going from credit/debit view to single amount view so we don't need the debit and credit
        // indicator set any more and we need to blank out the amount values to zero
        JournalVoucherDocument jvDoc = journalVoucherForm.getJournalVoucherDocument();
        ArrayList sourceLines = (ArrayList) jvDoc.getSourceAccountingLines();
        ArrayList helperLines = (ArrayList) journalVoucherForm.getJournalLineHelpers();

        KualiDecimal ZERO = new KualiDecimal("0.00");
        for (int i = 0; i < sourceLines.size(); i++) {
            JournalVoucherAccountingLineHelper helperLine = (JournalVoucherAccountingLineHelper) helperLines.get(i);
            SourceAccountingLine sourceLine = (SourceAccountingLine) sourceLines.get(i);
            sourceLine.setAmount(ZERO);
            sourceLine.setDebitCreditCode(null);

            helperLine.setCredit(null); // won't be needed in this mode
            helperLine.setDebit(null); // won't be needed in this mode
        }
    }

    /**
     * Overrides the parent to make sure that the JV specific accounting line helper forms are properly populated when the document
     * is first loaded. This first calls super, then populates the helper objects.
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);
        JournalVoucherForm journalVoucherForm = (JournalVoucherForm) kualiDocumentFormBase;

        // if the balance type is an offset generation balance type, then the user is able to enter the amount
        // as either a debit or a credit, otherwise, they only need to deal with the amount field
        JournalVoucherDocument journalVoucherDocument = (JournalVoucherDocument) journalVoucherForm.getTransactionalDocument();
        if (journalVoucherDocument.getBalanceType().isFinancialOffsetGenerationIndicator()) {
            populateAllJournalVoucherAccountingLineHelpers(journalVoucherForm);
            KualiDecimal ZERO = new KualiDecimal("0.00");
            journalVoucherForm.setNewSourceLineCredit(ZERO);
            journalVoucherForm.setNewSourceLineDebit(ZERO);
        }

        // always wipe out the new source line
        journalVoucherForm.setNewSourceLine(new SourceAccountingLine());

        // reload the balance type and accounting period selections since now we have data in the document bo
        populateSelectedJournalBalanceType(journalVoucherDocument, journalVoucherForm);
        populateSelectedJournalAccountingPeriod(journalVoucherDocument, journalVoucherForm);
    }

    /**
     * This method parses the accounting period value from the bo and builds the right string to pass to the form object as the
     * selected value.
     * 
     * @param journalVoucherDocument
     * @param journalVoucherForm
     */
    private void populateSelectedJournalAccountingPeriod(JournalVoucherDocument journalVoucherDocument,
            JournalVoucherForm journalVoucherForm) {
        if (StringUtils.isNotBlank(journalVoucherDocument.getPostingPeriodCode())) {
            String selectedAccountingPeriod = journalVoucherDocument.getPostingPeriodCode();
            if (null != journalVoucherDocument.getPostingYear()) {
                selectedAccountingPeriod += journalVoucherDocument.getPostingYear().toString();
            }
            else {
                selectedAccountingPeriod += SpringServiceLocator.getDateTimeService().getCurrentFiscalYear().toString();
            }
            journalVoucherForm.setSelectedAccountingPeriod(selectedAccountingPeriod);
        }
    }

    /**
     * This method grabs the value from the document bo and sets the selected balance type appropriately.
     * 
     * @param journalVoucherDocument
     * @param journalVoucherForm
     */
    private void populateSelectedJournalBalanceType(JournalVoucherDocument journalVoucherDocument,
            JournalVoucherForm journalVoucherForm) {
        journalVoucherForm.setSelectedBalanceType(journalVoucherDocument.getBalanceType());
        if (StringUtils.isNotBlank(journalVoucherDocument.getBalanceTypeCode())) {
            journalVoucherForm.setOriginalBalanceType(journalVoucherDocument.getBalanceTypeCode());
        }
    }

    /**
     * This populates a new helperLine instance with the one that was just added so that the new instance can be added to the
     * helperLines list.
     * 
     * @param journalVoucherForm
     * @return JournalVoucherAccountingLineHelper
     */
    private JournalVoucherAccountingLineHelper populateNewJournalVoucherAccountingLineHelper(JournalVoucherForm journalVoucherForm) {
        JournalVoucherAccountingLineHelper helperLine = new JournalVoucherAccountingLineHelper();

        KualiDecimal debitAmount = journalVoucherForm.getNewSourceLineDebit();
        if (debitAmount != null && StringUtils.isNotBlank(debitAmount.toString())) {
            helperLine.setDebit(debitAmount);
        }

        KualiDecimal creditAmount = journalVoucherForm.getNewSourceLineCredit();
        if (creditAmount != null && StringUtils.isNotBlank(creditAmount.toString())) {
            helperLine.setCredit(creditAmount);
        }

        return helperLine;
    }

    /**
     * This method builds the corresponding list of JV acounting line helper objects so that a user can differentiate between credit
     * and debit fields. It does this by iterating over each source accounting line (what the JV uses) looking at the debit/credit
     * code and then populateingLineHelpers a corresponding helper form instance with the amount in the appropriate amount field -
     * credit or debit.
     * 
     * @param journalVoucherForm
     */
    private void populateAllJournalVoucherAccountingLineHelpers(JournalVoucherForm journalVoucherForm) {
        // make sure the journal voucher accounting line helper form list is populated properly
        ArrayList journalLineHelpers = (ArrayList) journalVoucherForm.getJournalLineHelpers();

        // make sure the helper list is the right size
        JournalVoucherDocument jvDoc = (JournalVoucherDocument) journalVoucherForm.getTransactionalDocument();
        int size = jvDoc.getSourceAccountingLines().size();
        journalLineHelpers.ensureCapacity(size);

        // iterate through each source accounting line and initialize the helper form lines appropriately
        for (int i = 0; i < size; i++) {
            // get the bo's accounting line at the right index
            SourceAccountingLine sourceAccountingLine = jvDoc.getSourceAccountingLine(i);

            // instantiate a new helper form to use for populating the helper form list
            JournalVoucherAccountingLineHelper jvAcctLineHelperForm = journalVoucherForm.getJournalLineHelper(i);

            // figure whether we need to set the credit amount or the debit amount
            if (StringUtils.isNotBlank(sourceAccountingLine.getDebitCreditCode())) {
                if (sourceAccountingLine.getDebitCreditCode().equals(Constants.GL_DEBIT_CODE)) {
                    jvAcctLineHelperForm.setDebit(sourceAccountingLine.getAmount());
                }
                else if (sourceAccountingLine.getDebitCreditCode().equals(Constants.GL_CREDIT_CODE)) {
                    jvAcctLineHelperForm.setCredit(sourceAccountingLine.getAmount());
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
    private ActionForward processRouteOutOfBalanceDocumentConfirmationQuestion(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        JournalVoucherForm jvForm = (JournalVoucherForm) form;
        JournalVoucherDocument jvDoc = jvForm.getJournalVoucherDocument();

        String question = request.getParameter(Constants.QUESTION_INST_ATTRIBUTE_NAME);
        KualiConfigurationService kualiConfiguration = SpringServiceLocator.getKualiConfigurationService();

        if (question == null) { // question hasn't been asked
            String currencyFormattedDebitTotal = (String) new CurrencyFormatter().format(jvDoc.getDebitTotal());
            String currencyFormattedCreditTotal = (String) new CurrencyFormatter().format(jvDoc.getCreditTotal());
            String currencyFormattedTotal = (String) new CurrencyFormatter().format(jvDoc.getTotal());
            String message = "";
            jvDoc.refreshReferenceObject("balanceType");
            if (jvDoc.getBalanceType().isFinancialOffsetGenerationIndicator()) {
                message = StringUtils.replace(kualiConfiguration
                        .getPropertyString(KeyConstants.QUESTION_ROUTE_OUT_OF_BALANCE_JV_DOC), "{0}", currencyFormattedDebitTotal);
                message = StringUtils.replace(message, "{1}", currencyFormattedCreditTotal);
            }
            else {
                message = StringUtils.replace(kualiConfiguration
                        .getPropertyString(KeyConstants.QUESTION_ROUTE_OUT_OF_BALANCE_JV_DOC_SINGLE_AMT_MODE), "{0}",
                        currencyFormattedTotal);
            }

            // now transfer control over to the question component
            return this.performQuestion(mapping, form, request, response,
                    Constants.JOURNAL_VOUCHER_ROUTE_OUT_OF_BALANCE_DOCUMENT_QUESTION, message, Constants.CONFIRMATION_QUESTION,
                    Constants.ROUTE_METHOD);
        }
        else {
            String buttonClicked = request.getParameter(Constants.QUESTION_CLICKED_BUTTON);
            if ((Constants.JOURNAL_VOUCHER_ROUTE_OUT_OF_BALANCE_DOCUMENT_QUESTION.equals(question))
                    && ConfirmationQuestion.NO.equals(buttonClicked)) {
                GlobalVariables.getMessageList().add(KeyConstants.MESSAGE_JV_CANCELLED_ROUTE);
                return mapping.findForward(Constants.MAPPING_BASIC);
            }
        }
        return null;
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
    public ActionForward uploadSourceLines(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws FileNotFoundException, IOException {
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
    protected void uploadAccountingLines(boolean isSource, ActionForm form) throws FileNotFoundException, IOException {
        super.uploadAccountingLines(isSource, form);


        JournalVoucherForm jvForm = (JournalVoucherForm) form;

        populateAllJournalVoucherAccountingLineHelpers(jvForm);

        if (GlobalVariables.getErrorMap().containsKey(Constants.ACCOUNTING_LINE_ERRORS)) {
            JournalVoucherDocument jvDocument = jvForm.getJournalVoucherDocument();

            GlobalVariables.getErrorMap().put(Constants.ACCOUNTING_LINE_ERRORS,
                    KeyConstants.ERROR_DOCUMENT_JV_INVALID_ACCOUNTING_LINE_TEMPLATE,
                    new String[] { jvDocument.getBalanceTypeCode(), getImportTemplateName(jvDocument) });
        }
    }

    /**
     * Determines based on <code>{@link BalanceTyp}</code> criteria what template should have been used. This method is usually
     * only called when criteria isn't met properly which is determined through the business rules.<br/>
     * 
     * <p>
     * The following table shows the constraints this rule is based on. The table is separated by conditions, assumptions, and the
     * template determined to be used. If a certain condition is met, than an assumption is made. If the assumption is not
     * consistent, then the incorrect template was used.
     * </p>
     * <table>
     * <tr>
     * <th>Condition</th>
     * <th>Assumption</th>
     * <th>Template</th>
     * </tr>
     * <tr>
     * <td>BalanceType.isOffsetGeneration()</td>
     * <td>DebitCreditCode is set and amount &gt; 0</td>
     * <td> JV-Accounting-Line-Upload-Offset-Generation-Balance-Type.csv </td>
     * </tr>
     * <tr>
     * <td>BalanceType == "EX" (ExternalEncumberence)</td>
     * <td>ReferenceOriginCode, ReferenceNumber, and ReferenceTypeCode are not null</td>
     * <td> JV-Accounting-Line-Upload-External-Encumbrance-Balance-Type.csv </td>
     * </tr>
     * <tr>
     * <td>None of the above</td>
     * <td>DebitCreditCode is null, amount &gt; 0, and ReferenceOriginCode, ReferenceNumber, and ReferenceTypeCode are null.</td>
     * <td> JV-Accounting-Line-Upload-Non-Offset-Generation-Balance-Type.csv </td>
     * </tr>
     * </table><br/>
     * 
     * <p>
     * These rules are tested in the business rules. Not in this method. This method just determines the template that should have
     * been used if there was an error.
     * </p>
     * 
     * @param jv <code>{@link JournalVoucherDocument}</code> used to get
     * @return String
     */
    private String getImportTemplateName(JournalVoucherDocument jv) {
        String retval = null;

        if (jv.getBalanceType().isFinancialOffsetGenerationIndicator()) {
            retval = ACCOUNTING_LINE_UPLOAD_EXTERNAL_ENCUMBRANCE;
        }
        else if (jv.getBalanceType().getCode().equals(EXTERNAL_ENCUMBRANCE)) {
            retval = ACCOUNTING_LINE_UPLOAD_OFFSET_GENERATION;
        }
        else {
            retval = ACCOUNTING_LINE_UPLOAD_NON_OFFSET_GENERATION;
        }

        return retval;
    }
}
