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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.kfs.fp.businessobject.CashieringTransaction;
import org.kuali.kfs.fp.businessobject.Check;
import org.kuali.kfs.fp.businessobject.CoinDetail;
import org.kuali.kfs.fp.businessobject.CurrencyDetail;
import org.kuali.kfs.fp.businessobject.Deposit;
import org.kuali.kfs.fp.businessobject.DepositWizardCashieringCheckHelper;
import org.kuali.kfs.fp.businessobject.DepositWizardHelper;
import org.kuali.kfs.fp.businessobject.format.CashDrawerStatusCodeFormatter;
import org.kuali.kfs.fp.document.CashManagementDocument;
import org.kuali.kfs.fp.document.CashReceiptDocument;
import org.kuali.kfs.fp.document.service.CashManagementService;
import org.kuali.kfs.fp.document.service.CashReceiptService;
import org.kuali.kfs.fp.exception.CashDrawerStateException;
import org.kuali.kfs.fp.service.CashDrawerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.CashDrawerConstants;
import org.kuali.kfs.sys.KFSConstants.DocumentStatusCodes.CashReceipt;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.datadictionary.FinancialSystemTransactionalDocumentEntry;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentPresentationController;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.exception.InfrastructureException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * This class handles actions for the deposit wizard, which is used to create deposits that bundle groupings of Cash Receipt
 * documents.
 */
public class DepositWizardAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DepositWizardAction.class);
    private static final String CASH_MANAGEMENT_STATUS_PAGE = "/cashManagementStatus.do";

    /**
     * Overrides the parent to validate the document state of the cashManagementDocument which will be updated and redisplayed after
     * the DepositWizard builds and attaches the new Deposit.
     *
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DepositWizardForm dwForm = (DepositWizardForm) form;

        ActionForward dest = super.execute(mapping, form, request, response);

        // check authorization manually, since the auth-check isn't inherited by this class
        DocumentAuthorizer cmDocAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(KFSConstants.FinancialDocumentTypeCodes.CASH_MANAGEMENT);
        Person luser = GlobalVariables.getUserSession().getPerson();
        cmDocAuthorizer.canInitiate(KFSConstants.FinancialDocumentTypeCodes.CASH_MANAGEMENT, luser);

        // populate the outgoing form used by the JSP if it seems empty
        String cmDocId = dwForm.getCashManagementDocId();
        if (StringUtils.isBlank(cmDocId)) {
            cmDocId = request.getParameter("cmDocId");
            String depositTypeCode = request.getParameter("depositTypeCode");

            CashManagementDocument cmDoc = (CashManagementDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(cmDocId);

            try {
                initializeForm(dwForm, cmDoc, depositTypeCode);
            }
            catch (CashDrawerStateException cdse) {
                dest = new ActionForward(UrlFactory.parameterizeUrl(CASH_MANAGEMENT_STATUS_PAGE, cdse.toProperties()), true);
            }
        } else { // for recalculation
            loadCashReceipts(dwForm);
            loadUndepositedCashieringChecks(dwForm);
            if (dwForm.getTargetDepositAmount() == null) {
                calculateTargetFinalDepositAmount(dwForm);
            }
            loadEditModesAndDocumentActions(dwForm);
        }

        return dest;
    }

    /**
     * Initializes the given form using the given values
     *
     * @param dform
     * @param cmDoc
     * @param depositTypeCode
     */
    private void initializeForm(DepositWizardForm dform, CashManagementDocument cmDoc, String depositTypeCode) {
        CashDrawer cd = SpringContext.getBean(CashDrawerService.class).getByCampusCode(cmDoc.getCampusCode());
        if (cd == null) {
            throw new RuntimeException("No cash drawer exists for campus code " + cmDoc.getCampusCode() + "; please create on via the Cash Drawer Maintenance Document before attemping to create a CashManagementDocument for campus " + cmDoc.getCampusCode());
        }
        if (!cd.isOpen()) {
            CashDrawerStatusCodeFormatter f = new CashDrawerStatusCodeFormatter();

            String cmDocId = cmDoc.getDocumentNumber();
            String currentState = cd.getStatusCode();

            throw new CashDrawerStateException(cmDoc.getCampusCode(), cmDocId, (String) f.format(CashDrawerConstants.STATUS_OPEN), (String) f.format(cd.getStatusCode()));
        }

        dform.setCashManagementDocId(cmDoc.getDocumentNumber());
        dform.setCashDrawerCampusCode(cmDoc.getCampusCode());

        dform.setDepositTypeCode(depositTypeCode);

        if (depositTypeCode.equals(KFSConstants.DocumentStatusCodes.CashReceipt.FINAL)) {
            // hey, we're the magical final deposit. We get currency and coin details!
            CurrencyDetail currencyDetail = new CurrencyDetail();
            currencyDetail.setDocumentNumber(cmDoc.getDocumentNumber());
            currencyDetail.setCashieringStatus(KFSConstants.CurrencyCoinSources.DEPOSITS);
            currencyDetail.setFinancialDocumentTypeCode(CashieringTransaction.DETAIL_DOCUMENT_TYPE);
            dform.setCurrencyDetail(currencyDetail);

            CoinDetail coinDetail = new CoinDetail();
            coinDetail.setDocumentNumber(cmDoc.getDocumentNumber());
            coinDetail.setCashieringStatus(KFSConstants.CurrencyCoinSources.DEPOSITS);
            coinDetail.setFinancialDocumentTypeCode(CashieringTransaction.DETAIL_DOCUMENT_TYPE);
            dform.setCoinDetail(coinDetail);
        }

        loadCashReceipts(dform);
        loadUndepositedCashieringChecks(dform);
        if (dform.isDepositFinal() && dform.getTargetDepositAmount() == null) {
            calculateTargetFinalDepositAmount(dform);
        }
        loadEditModesAndDocumentActions(dform);
    }

    /**
     * Loads the CashReceipt information, re/setting the related form fields
     *
     * @param dform
     */
    private void loadCashReceipts(DepositWizardForm dform) {
        List<CashReceiptDocument> verifiedReceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(dform.getCashDrawerCampusCode(), new String[] { CashReceipt.VERIFIED, CashReceipt.INTERIM });
        dform.setDepositableCashReceipts(new ArrayList());
        dform.setCheckFreeCashReceipts(new ArrayList<CashReceiptDocument>());

        // prepopulate DepositWizardHelpers
        int index = 0;
        for (Iterator i = verifiedReceipts.iterator(); i.hasNext();) {
            CashReceiptDocument receipt = (CashReceiptDocument) i.next();
            receipt.processAfterRetrieve(); // To populate Currency and Coin details
            String docStatus = receipt.getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode();
            if (docStatus.equalsIgnoreCase(CashReceipt.VERIFIED)) { // for interim or final deposit
                if (receipt.getCheckCount() == 0 && receipt.getTotalConfirmedCheckAmount().equals(KualiDecimal.ZERO)) {
                    dform.getCheckFreeCashReceipts().add(receipt);
                }
                else {
                    dform.getDepositableCashReceipts().add(receipt);
                    DepositWizardHelper d = dform.getDepositWizardHelper(index++);
                    // KFSMI-5232 Jira fix. Convert the time stamp to SQL date format
                    Timestamp ts = new Timestamp(receipt.getDocumentHeader().getWorkflowDocument().getDateCreated().getMillis());
                    try {
                        d.setCashReceiptCreateDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate(ts));
                    }
                    catch (Exception e) {

                    }
                }
            }
            else if (docStatus.equalsIgnoreCase(CashReceipt.INTERIM)) { // for final deposit
                // checks are already deposited but there are cash to be deposited
                if (receipt.getTotalConfirmedCashAmount().isGreaterThan(KualiDecimal.ZERO)) {
                    dform.getCheckFreeCashReceipts().add(receipt);
                }
            }
            dform.addCashReceiptToChecks(receipt);
        }
    }

    /**
     * Loads cashiering transactions for final deposit target amount
     * @param dwform
     */
    private void calculateTargetFinalDepositAmount(DepositWizardForm dwform) {
        final CashReceiptService cashReceiptService = SpringContext.getBean(CashReceiptService.class);
        final List<CashReceiptDocument> interestingReceipts =
                cashReceiptService.getCashReceipts(dwform.getCashDrawerCampusCode(), new String[] { CashReceipt.VERIFIED, CashReceipt.INTERIM, CashReceipt.FINAL });
        for (CashReceiptDocument crDoc : interestingReceipts) {
            crDoc.refreshCashDetails();
            dwform.addCashReceiptToTargetTotal(crDoc);
        }

        final CashManagementService cashManagementService = SpringContext.getBean(CashManagementService.class);
        KualiDecimal toBeDepositedChecksTotal = KualiDecimal.ZERO;
        for (Check check : cashManagementService.selectUndepositedCashieringChecks(dwform.getCashManagementDocId())) {
            toBeDepositedChecksTotal = toBeDepositedChecksTotal.add(check.getAmount());
        }
        // since final, include deposited checks as well (see KFSCNTRB-160)
        for (Check check : cashManagementService.selectDepositedCashieringChecks(dwform.getCashManagementDocId())) {
            toBeDepositedChecksTotal = toBeDepositedChecksTotal.add(check.getAmount());
        }
        dwform.addCashieringTransactionToTargetTotal(toBeDepositedChecksTotal);
    }

    /**
     * This loads any cashiering checks which have not yet been deposited into the DepositWizardForm
     *
     * @param dform a form to load undeposited checks into
     */
    private void loadUndepositedCashieringChecks(DepositWizardForm dform) {
        List<Check> cashieringChecks = SpringContext.getBean(CashManagementService.class).selectUndepositedCashieringChecks(dform.getCashManagementDocId());
        dform.setDepositableCashieringChecks(cashieringChecks);
    }

    private void loadEditModesAndDocumentActions(DepositWizardForm dform) {
        final FinancialSystemTransactionalDocumentEntry ddEntry = getCashManagementDataDictionaryEntry();
        final TransactionalDocumentPresentationController presentationController = getCashManagementPresentationController(ddEntry);
        final TransactionalDocumentAuthorizer docAuthorizer = getCashManagementDocumentAuthorizer(ddEntry);

        dform.setEditingMode(retrieveEditingModes(dform.getCashManagementDocId(), presentationController, docAuthorizer));
        dform.setDocumentActions(retrieveDocumentActions(dform.getCashManagementDocId(), presentationController, docAuthorizer));
    }

    /**
     * @return the class of the cash management document
     */
    protected String getCashManagementDocumentTypeName() {
        return "CMD";
    }

    /**
     * @return the data dictionary entry for the cash management class
     */
    private FinancialSystemTransactionalDocumentEntry getCashManagementDataDictionaryEntry() {
        final DataDictionaryService ddService = SpringContext.getBean(DataDictionaryService.class);
        return (FinancialSystemTransactionalDocumentEntry)ddService.getDataDictionary().getDocumentEntry(getCashManagementDocumentTypeName());
    }

    /**
     * Returns an instance of the document presentation controller for the cash management class
     * @param cashManagementEntry the data dictionary entry for the cash management document
     * @return an instance of the proper document presentation controller
     */
    private TransactionalDocumentPresentationController getCashManagementPresentationController(FinancialSystemTransactionalDocumentEntry cashManagementEntry) {
        final Class presentationControllerClass = cashManagementEntry.getDocumentPresentationControllerClass();
        TransactionalDocumentPresentationController presentationController = null;
        try {
            presentationController = (TransactionalDocumentPresentationController)presentationControllerClass.newInstance();
        }
        catch (InstantiationException ie) {
            throw new RuntimeException("Could not instantiate cash management presentation controller of class " + presentationControllerClass.getName(), ie);
        }
        catch (IllegalAccessException iae) {
            throw new RuntimeException("Could not instantiate cash management presentation controller of class " + presentationControllerClass.getName(), iae);
        }
        return presentationController;
    }

    /**
     * Returns an instance of the document authorizer for the cash management class
     * @param cashManagementEntry the data dictionary entry for the cash management document
     * @return an instance of the proper document authorizer
     */
    private TransactionalDocumentAuthorizer getCashManagementDocumentAuthorizer(FinancialSystemTransactionalDocumentEntry cashManagementEntry) {
        final Class docAuthorizerClass = cashManagementEntry.getDocumentAuthorizerClass();
        TransactionalDocumentAuthorizer docAuthorizer = null;
        try {
            docAuthorizer = (TransactionalDocumentAuthorizer)docAuthorizerClass.newInstance();
        }
        catch (InstantiationException ie) {
            throw new RuntimeException("Could not instantiate cash management document authorizer of class " + docAuthorizerClass.getName(), ie);
        }
        catch (IllegalAccessException iae) {
            throw new RuntimeException("Could not instantiate cash management document authorizer of class " + docAuthorizerClass.getName(), iae);
        }
        return docAuthorizer;
    }

    /**
     * Retrieves the edit modes for the given cash management document
     * @param cashManagementDocId the id of the cash management document to check
     * @param presentationController the presentation controller of the cash management document
     * @param docAuthorizer the cash management document authorizer
     * @return a Map of edit modes
     */
    private Map retrieveEditingModes(String cashManagementDocId, TransactionalDocumentPresentationController presentationController, TransactionalDocumentAuthorizer docAuthorizer) {
        Map editModeMap = null;
        try {
            final CashManagementDocument cmDoc = (CashManagementDocument)SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(cashManagementDocId);
            Set<String> editModes = presentationController.getEditModes(cmDoc);
            editModes = docAuthorizer.getEditModes(cmDoc, GlobalVariables.getUserSession().getPerson(), editModes);
            editModeMap = convertSetToMap(editModes);
        }
        catch (WorkflowException we) {
            throw new RuntimeException("Workflow exception while retrieving document " + cashManagementDocId, we);
        }
        return editModeMap;
    }

    /**
     * Retrieves the document actions for the given cash management document
     * @param cashManagementDocId the id of the cash management document to check
     * @param presentationController the presentation controller of the cash management document
     * @param docAuthorizer the cash management document authorizer
     * @return a Map of document actions
     */
    private Map retrieveDocumentActions(String cashManagementDocId, TransactionalDocumentPresentationController presentationController, TransactionalDocumentAuthorizer docAuthorizer) {
        Map documentActionsMap = null;
        try {
            final CashManagementDocument cmDoc = (CashManagementDocument)SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(cashManagementDocId);
            Set<String> documentActions = presentationController.getDocumentActions(cmDoc);
            documentActions = docAuthorizer.getEditModes(cmDoc, GlobalVariables.getUserSession().getPerson(), documentActions);
            documentActionsMap = convertSetToMap(documentActions);
        }
        catch (WorkflowException we) {
            throw new RuntimeException("Workflow exception while retrieving document " + cashManagementDocId, we);
        }
        return documentActionsMap;
    }

    /**
     * Converts a set into a map, where each value in the set becomes a key and each value becomes KNSConstants.KUALI_DEFAULT_TRUE_VALUE
     * @param s a set
     * @return a map
     */
    protected Map convertSetToMap(Set s){
        Map map = new HashMap();
        Iterator i = s.iterator();
        while(i.hasNext()) {
            Object key = i.next();
            map.put(key, KRADConstants.KUALI_DEFAULT_TRUE_VALUE);
        }
        return map;
    }

    /**
     * Reloads the CashReceipts, leaving everything else unchanged
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        loadCashReceipts((DepositWizardForm) form);
        loadUndepositedCashieringChecks((DepositWizardForm) form);
        if (((DepositWizardForm) form).isDepositFinal() && ((DepositWizardForm)form).getTargetDepositAmount() == null) {
            calculateTargetFinalDepositAmount((DepositWizardForm) form);
        }
        loadEditModesAndDocumentActions((DepositWizardForm) form);

        return super.refresh(mapping, form, request, response);
    }

    /**
     * This method is the starting point for the deposit document wizard.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward startWizard(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method is the action method for creating the new deposit document from the information chosen by the user in the UI.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     */
    public ActionForward createDeposit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        ActionForward dest = mapping.findForward(KFSConstants.MAPPING_BASIC);

        DepositWizardForm dform = (DepositWizardForm) form;
        final BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        final CashReceiptService cashReceiptService = SpringContext.getBean(CashReceiptService.class);
        final DocumentService documentService = SpringContext.getBean(DocumentService.class);
        final CashManagementService cashManagementService = SpringContext.getBean(CashManagementService.class);

        CurrencyFormatter formatter = new CurrencyFormatter();

        // reload edit modes and summary totals - just in case we have to return to the deposit wizard page
        loadCashReceipts((DepositWizardForm) form);
        if (((DepositWizardForm) form).isDepositFinal() && ((DepositWizardForm)form).getTargetDepositAmount() == null) {
            calculateTargetFinalDepositAmount((DepositWizardForm) form);
        }
        loadEditModesAndDocumentActions(dform);

        // validate Bank
        String bankCode = dform.getBankCode();
        if (StringUtils.isBlank(bankCode)) {
            GlobalVariables.getMessageMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_MISSING_BANK);
        }
        else {
            Map keyMap = new HashMap();
            keyMap.put(KFSPropertyConstants.BANK_CODE, bankCode);

            Bank bank = (Bank) boService.findByPrimaryKey(Bank.class, keyMap);
            if (bank == null) {
                GlobalVariables.getMessageMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_UNKNOWN_BANK, bankCode);
            }
            else {
                dform.setBank(bank);
            }
        }

        boolean depositIsFinal = (StringUtils.equals(dform.getDepositTypeCode(), KFSConstants.DepositConstants.DEPOSIT_TYPE_FINAL));

        // validate cashReceipt selection
        List selectedIds = new ArrayList();
        for (Iterator i = dform.getDepositWizardHelpers().iterator(); i.hasNext();) {
            String checkValue = ((DepositWizardHelper) i.next()).getSelectedValue();

            if (StringUtils.isNotBlank(checkValue) && !checkValue.equals(KFSConstants.ParameterValues.NO)) {
                // removed apparently-unnecessary test for !checkValue.equals(KFSConstants.ParameterValues.YES)
                selectedIds.add(checkValue);
            }
        }

        if (depositIsFinal) {
            // add check free cash receipts to the selected receipts so they are automatically deposited
            dform.setCheckFreeCashReceipts(new ArrayList<CashReceiptDocument>());
            List<CashReceiptDocument> cashReceipts = cashReceiptService.getCashReceipts(dform.getCashDrawerCampusCode(), new String[] {CashReceipt.VERIFIED, CashReceipt.INTERIM});
            for (Object crDocObj : cashReceipts) {
                CashReceiptDocument crDoc = (CashReceiptDocument) crDocObj;
                crDoc.refreshCashDetails();
                if (crDoc.getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode().equals(CashReceipt.VERIFIED) && crDoc.getCheckCount() == 0) {
                    // it's check free; it is automatically deposited as part of the final deposit
                    selectedIds.add(crDoc.getDocumentNumber());
                    dform.getCheckFreeCashReceipts().add(crDoc);
                }
                else if (crDoc.getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode().equals(CashReceipt.INTERIM) &&
                        crDoc.getGrandTotalConfirmedCashAmount().isGreaterThan(KualiDecimal.ZERO)) {
//                        crDoc.setChecks(null);
//                        crDoc.setConfirmedChecks(new ArrayList<Check>());
//                        crDoc.setTotalConfirmedCheckAmount(null);
                        selectedIds.add(crDoc.getDocumentNumber());
                        dform.getCheckFreeCashReceipts().add(crDoc);
                }
            }
        }

        // make a list of cashiering checks to deposit
        List<Integer> selectedCashieringChecks = new ArrayList<Integer>();
        for (DepositWizardCashieringCheckHelper helper : dform.getDepositWizardCashieringCheckHelpers()) {
            if (helper.getSequenceId() != null && !helper.getSequenceId().equals(new Integer(-1))) {
                selectedCashieringChecks.add(helper.getSequenceId());
            }
        }

        if (selectedIds.isEmpty() && selectedCashieringChecks.isEmpty()) {
            GlobalVariables.getMessageMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_CASHRECEIPT_ERROR, KFSKeyConstants.Deposit.ERROR_NO_CASH_RECEIPTS_SELECTED);
        }

        //
        // proceed, if possible
        if (GlobalVariables.getMessageMap().hasNoErrors()) {
            try {
                // retrieve selected receipts
                List selectedReceipts = new ArrayList();
                if (selectedIds != null && !selectedIds.isEmpty()) {
                    selectedReceipts = documentService.getDocumentsByListOfDocumentHeaderIds(CashReceiptDocument.class, selectedIds);
                }

                if (depositIsFinal) {
                    // have all verified CRs been deposited? If not, that's an error
//                    List verifiedReceipts = cashReceiptService.getCashReceipts(dform.getCashDrawerCampusCode(), new String[] {CashReceipt.VERIFIED, CashReceipt.INTERIM});
//                    for (Object o : verifiedReceipts) {
//                        CashReceiptDocument crDoc = (CashReceiptDocument) o;
//                        if (!selectedReceipts.contains(crDoc)) {
//                            GlobalVariables.getMessageMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NON_DEPOSITED_VERIFIED_CASH_RECEIPT, new String[] { crDoc.getDocumentNumber() });
//                        }
//                    }
                    KualiDecimal toBeDepositedChecksTotal = KualiDecimal.ZERO;
                    // have we selected the rest of the undeposited checks?
                    for (Check check : cashManagementService.selectUndepositedCashieringChecks(dform.getCashManagementDocId())) {
                        if (!selectedCashieringChecks.contains(check.getSequenceId())) {
                            GlobalVariables.getMessageMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_CASHIERING_CHECK_MUST_BE_DEPOSITED, new String[] { check.getCheckNumber() });
                        }
                        else {
                            toBeDepositedChecksTotal = toBeDepositedChecksTotal.add(check.getAmount());
                        }
                    }
                    // add the rest of the deposited cashiering checks to the toBeDepositedChecksTotal
                    for (Check check : cashManagementService.selectDepositedCashieringChecks(dform.getCashManagementDocId())) {
                        toBeDepositedChecksTotal = toBeDepositedChecksTotal.add(check.getAmount());
                    }

                    // does the cash drawer have enough currency and coin to fulfill the requested deposit?
                    checkEnoughCurrencyForDeposit(dform);
                    checkEnoughCoinForDeposit(dform);

                    // does this deposit have currency and coin to match all currency and coin from CRs?
                    List<CashReceiptDocument> interestingReceipts = cashReceiptService.getCashReceipts(dform.getCashDrawerCampusCode(), new String[] { CashReceipt.VERIFIED, CashReceipt.INTERIM, CashReceipt.FINAL });
                    CurrencyDetail currencyTotal = new CurrencyDetail();
                    CoinDetail coinTotal = new CoinDetail();
                    for (CashReceiptDocument receipt : interestingReceipts) {
                        receipt.refreshCashDetails();
                        if (receipt.getCurrencyDetail() != null) {
                            currencyTotal.add(receipt.getConfirmedCurrencyDetail());
                            currencyTotal.subtract(receipt.getChangeCurrencyDetail());
                        }
                        if (receipt.getCoinDetail() != null) {
                            coinTotal.add(receipt.getConfirmedCoinDetail());
                            coinTotal.subtract(receipt.getChangeCoinDetail());
                        }
                    }

                    KualiDecimal cashReceiptCashTotal = currencyTotal.getTotalAmount().add(coinTotal.getTotalAmount());
                    // remove the cashiering checks amounts from the cash receipts total; cashiering checks act as if they were CR
                    // currency/coin that gets deposited
                    cashReceiptCashTotal = cashReceiptCashTotal.subtract(toBeDepositedChecksTotal);
                    KualiDecimal depositedCashTotal = dform.getCurrencyDetail().getTotalAmount().add(dform.getCoinDetail().getTotalAmount());
                    if (!cashReceiptCashTotal.equals(depositedCashTotal)) {
                        GlobalVariables.getMessageMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_CASH_DEPOSIT_DID_NOT_BALANCE, new String[] { formatter.format(depositedCashTotal).toString(), formatter.format(cashReceiptCashTotal).toString() });
                    }
                }

                // proceed again...if possible
                if (GlobalVariables.getMessageMap().hasNoErrors()) {
                    // retrieve CashManagementDocument
                    String cashManagementDocId = dform.getCashManagementDocId();
                    CashManagementDocument cashManagementDoc = null;
                    try {
                        cashManagementDoc = (CashManagementDocument) documentService.getByDocumentHeaderId(cashManagementDocId);
                        if (cashManagementDoc == null) {
                            throw new IllegalStateException("unable to find cashManagementDocument with id " + cashManagementDocId);
                        }
                    }
                    catch (WorkflowException e) {
                        throw new IllegalStateException("unable to retrieve cashManagementDocument with id " + cashManagementDocId, e);
                    }

                    // create deposit
                    String cmDocId = dform.getCashManagementDocId();

                    cashManagementService.addDeposit(cashManagementDoc, dform.getDepositTicketNumber(), dform.getBank(), selectedReceipts, selectedCashieringChecks, depositIsFinal);

                    if (depositIsFinal) {
                        // find the final deposit
                        Deposit finalDeposit = findFinalDeposit(cashManagementDoc);
                        // if the currency and coin details aren't empty, save them and remove them from the cash drawer
                        if (dform.getCurrencyDetail() != null) {
                            // do we have enough currency to allow the deposit to leave the drawer?
                            boService.save(dform.getCurrencyDetail());
                            cashManagementDoc.getCashDrawer().removeCurrency(dform.getCurrencyDetail());
                            finalDeposit.setDepositAmount(finalDeposit.getDepositAmount().add(dform.getCurrencyDetail().getTotalAmount()));
                        }
                        if (dform.getCoinDetail() != null) {
                            // do we have enough coin to allow the deposit to leave the drawer?
                            boService.save(dform.getCoinDetail());
                            cashManagementDoc.getCashDrawer().removeCoin(dform.getCoinDetail());
                            finalDeposit.setDepositAmount(finalDeposit.getDepositAmount().add(dform.getCoinDetail().getTotalAmount()));
                        }
                        boService.save(cashManagementDoc.getCashDrawer());
                        boService.save(finalDeposit);
                    }

                    // redirect to controlling CashManagementDocument
                    dest = returnToSender(cashManagementDocId);
                }
            }
            catch (WorkflowException e) {
                throw new InfrastructureException("unable to retrieve cashReceipts by documentId", e);
            }
        }

        return dest;
    }

    private Deposit findFinalDeposit(CashManagementDocument cmDoc) {
        Deposit finalDeposit = null;
        for (Deposit deposit : cmDoc.getDeposits()) {
            if (deposit.getDepositTypeCode().equals(KFSConstants.DepositConstants.DEPOSIT_TYPE_FINAL)) {
                finalDeposit = deposit;
                break;
            }
        }
        return finalDeposit;
    }

    /**
     * Checks that the currency amount requested to be part of a deposit can be fulfilled by the amount of currency in the cash
     * drawer
     *
     * @param depositForm the deposit form we are checking against
     * @param detail the currency detail to check against the drawer
     * @return true if enough currency, false if otherwise
     */
    private boolean checkEnoughCurrencyForDeposit(DepositWizardForm depositForm) {
        boolean success = true;
        CurrencyDetail detail = depositForm.getCurrencyDetail();
        if (detail != null) {
            // 1. get the cash drawer
            CashDrawer drawer = SpringContext.getBean(CashDrawerService.class).getByCampusCode(depositForm.getCashDrawerCampusCode());
            // assumptions at this point:
            // 1. a cash drawer does exist for the unit
            // 2. we can ignore negative amounts, because if we have negative amounts, we're actually gaining money (and that will
            // happen with cashiering checks)
            CurrencyFormatter formatter = new CurrencyFormatter();
            if (detail.getFinancialDocumentHundredDollarAmount() != null && detail.getFinancialDocumentHundredDollarAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentHundredDollarAmount() == null || drawer.getFinancialDocumentHundredDollarAmount().isLessThan(detail.getFinancialDocumentHundredDollarAmount())) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "hundred dollar amount", formatter.format(detail.getFinancialDocumentHundredDollarAmount()).toString(), formatter.format(drawer.getFinancialDocumentHundredDollarAmount()).toString() });
                    success = false;
                }
            }
            if (detail.getFinancialDocumentFiftyDollarAmount() != null && detail.getFinancialDocumentFiftyDollarAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentFiftyDollarAmount() == null || drawer.getFinancialDocumentFiftyDollarAmount().isLessThan(detail.getFinancialDocumentFiftyDollarAmount())) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "fifty dollar amount", formatter.format(detail.getFinancialDocumentFiftyDollarAmount()).toString(), formatter.format(drawer.getFinancialDocumentFiftyDollarAmount()).toString() });
                    success = false;
                }
            }
            if (detail.getFinancialDocumentTwentyDollarAmount() != null && detail.getFinancialDocumentTwentyDollarAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentTwentyDollarAmount() == null || drawer.getFinancialDocumentTwentyDollarAmount().isLessThan(detail.getFinancialDocumentTwentyDollarAmount())) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "twenty dollar amount", formatter.format(detail.getFinancialDocumentTwentyDollarAmount()).toString(), formatter.format(drawer.getFinancialDocumentTwentyDollarAmount()).toString() });
                    success = false;
                }
            }
            if (detail.getFinancialDocumentTenDollarAmount() != null && detail.getFinancialDocumentTenDollarAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentTenDollarAmount() == null || drawer.getFinancialDocumentTenDollarAmount().isLessThan(detail.getFinancialDocumentTenDollarAmount())) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "ten dollar amount", formatter.format(detail.getFinancialDocumentTenDollarAmount()).toString(), formatter.format(drawer.getFinancialDocumentTenDollarAmount()).toString() });
                    success = false;
                }
            }
            if (detail.getFinancialDocumentFiveDollarAmount() != null && detail.getFinancialDocumentFiveDollarAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentFiveDollarAmount() == null || drawer.getFinancialDocumentFiveDollarAmount().isLessThan(detail.getFinancialDocumentFiveDollarAmount())) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "five dollar amount", formatter.format(detail.getFinancialDocumentFiveDollarAmount()).toString(), formatter.format(drawer.getFinancialDocumentFiveDollarAmount()).toString() });
                    success = false;
                }
            }
            if (detail.getFinancialDocumentTwoDollarAmount() != null && detail.getFinancialDocumentTwoDollarAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentTwoDollarAmount() == null || drawer.getFinancialDocumentTwoDollarAmount().isLessThan(detail.getFinancialDocumentTwoDollarAmount())) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "two dollar amount", formatter.format(detail.getFinancialDocumentTwoDollarAmount()).toString(), formatter.format(drawer.getFinancialDocumentTwoDollarAmount()).toString() });
                    success = false;
                }
            }
            if (detail.getFinancialDocumentOneDollarAmount() != null && detail.getFinancialDocumentOneDollarAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentOneDollarAmount() == null || drawer.getFinancialDocumentOneDollarAmount().isLessThan(detail.getFinancialDocumentOneDollarAmount())) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "one dollar amount", formatter.format(detail.getFinancialDocumentOneDollarAmount()).toString(), formatter.format(drawer.getFinancialDocumentOneDollarAmount()).toString() });
                    success = false;
                }
            }
            if (detail.getFinancialDocumentOtherDollarAmount() != null && detail.getFinancialDocumentOtherDollarAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentOtherDollarAmount() == null || drawer.getFinancialDocumentOtherDollarAmount().isLessThan(detail.getFinancialDocumentOtherDollarAmount())) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "other dollar amount", formatter.format(detail.getFinancialDocumentOtherDollarAmount()).toString(), formatter.format(drawer.getFinancialDocumentOtherDollarAmount()).toString() });
                    success = false;
                }
            }
        }
        return success;
    }

    /**
     * Checks that the coin amount requested by the deposit does not exceed the amount actually in the drawer
     *
     * @param depositForm the deposit form we are checking against
     * @param detail the coin detail to check against the drawer
     * @return true if there is enough coin, false if otherwise
     */
    public boolean checkEnoughCoinForDeposit(DepositWizardForm depositForm) {
        boolean success = true;
        CoinDetail detail = depositForm.getCoinDetail();
        if (detail != null) {
            // 1. get the cash drawer
            CashDrawer drawer = SpringContext.getBean(CashDrawerService.class).getByCampusCode(depositForm.getCashDrawerCampusCode());
            // assumptions at this point:
            // 1. a cash drawer does exist for the unit
            // 2. we can ignore negative amounts, because if we have negative amounts, we're actually gaining money (and that will
            // happen with cashiering checks)
            CurrencyFormatter formatter = new CurrencyFormatter();
            if (detail.getFinancialDocumentHundredCentAmount() != null && detail.getFinancialDocumentHundredCentAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentHundredCentAmount() == null || drawer.getFinancialDocumentHundredCentAmount().isLessThan(detail.getFinancialDocumentHundredCentAmount())) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "hundred cent amount", formatter.format(detail.getFinancialDocumentHundredCentAmount()).toString(), formatter.format(drawer.getFinancialDocumentHundredCentAmount()).toString() });
                    success = false;
                }
            }
            if (detail.getFinancialDocumentFiftyCentAmount() != null && detail.getFinancialDocumentFiftyCentAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentFiftyCentAmount() == null || drawer.getFinancialDocumentFiftyCentAmount().isLessThan(detail.getFinancialDocumentFiftyCentAmount())) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "fifty cent amount", formatter.format(detail.getFinancialDocumentFiftyCentAmount()).toString(), formatter.format(drawer.getFinancialDocumentFiftyCentAmount()).toString() });
                    success = false;
                }
            }
            if (detail.getFinancialDocumentTwentyFiveCentAmount() != null && detail.getFinancialDocumentTwentyFiveCentAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentTwentyFiveCentAmount() == null || drawer.getFinancialDocumentTwentyFiveCentAmount().isLessThan(detail.getFinancialDocumentTwentyFiveCentAmount())) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "twenty five cent amount", formatter.format(detail.getFinancialDocumentTwentyFiveCentAmount()).toString(), formatter.format(drawer.getFinancialDocumentTwentyFiveCentAmount()).toString() });
                    success = false;
                }
            }
            if (detail.getFinancialDocumentTenCentAmount() != null && detail.getFinancialDocumentTenCentAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentTenCentAmount() == null || drawer.getFinancialDocumentTenCentAmount().isLessThan(detail.getFinancialDocumentTenCentAmount())) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "ten cent amount", formatter.format(detail.getFinancialDocumentTenCentAmount()).toString(), formatter.format(drawer.getFinancialDocumentTenCentAmount()).toString() });
                    success = false;
                }
            }
            if (detail.getFinancialDocumentFiveCentAmount() != null && detail.getFinancialDocumentFiveCentAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentFiveCentAmount() == null || drawer.getFinancialDocumentFiveCentAmount().isLessThan(detail.getFinancialDocumentFiveCentAmount())) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "five cent amount", formatter.format(detail.getFinancialDocumentFiveCentAmount()).toString(), formatter.format(drawer.getFinancialDocumentFiveCentAmount()).toString() });
                    success = false;
                }
            }
            if (detail.getFinancialDocumentOneCentAmount() != null && detail.getFinancialDocumentOneCentAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentOneCentAmount() == null || drawer.getFinancialDocumentOneCentAmount().isLessThan(detail.getFinancialDocumentOneCentAmount())) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "one cent amount", formatter.format(detail.getFinancialDocumentOneCentAmount()).toString(), formatter.format(drawer.getFinancialDocumentOneCentAmount()).toString() });
                    success = false;
                }
            }
            if (detail.getFinancialDocumentOtherCentAmount() != null && detail.getFinancialDocumentOtherCentAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentOtherCentAmount() == null || drawer.getFinancialDocumentOtherCentAmount().isLessThan(detail.getFinancialDocumentOtherCentAmount())) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "other cent amount", formatter.format(detail.getFinancialDocumentOtherCentAmount()).toString(), formatter.format(drawer.getFinancialDocumentOtherCentAmount()).toString() });
                    success = false;
                }
            }
        }
        return success;
    }


    /**
     * This method handles canceling (closing) the deposit wizard.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DepositWizardForm dform = (DepositWizardForm) form;

        ActionForward dest = returnToSender(dform.getCashManagementDocId());
        return dest;
    }


    /**
     * @param cmDocId
     * @return ActionForward which will redirect the user to the docSearchDisplay for the CashManagementDocument with the given
     *         documentId
     */
    private ActionForward returnToSender(String cmDocId) {
        Properties params = new Properties();
        params.setProperty("methodToCall", "docHandler");
        params.setProperty("command", "displayDocSearchView");
        params.setProperty("docId", cmDocId);

        String cmActionUrl = UrlFactory.parameterizeUrl(KFSConstants.CASH_MANAGEMENT_DOCUMENT_ACTION, params);

        return new ActionForward(cmActionUrl, true);
    }
}

