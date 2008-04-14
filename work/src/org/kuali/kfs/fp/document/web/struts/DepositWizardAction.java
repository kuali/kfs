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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.authorization.DocumentAuthorizer;
import org.kuali.core.exceptions.InfrastructureException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DocumentAuthorizationService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSConstants.CashDrawerConstants;
import org.kuali.kfs.KFSConstants.DocumentStatusCodes.CashReceipt;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.bo.Bank;
import org.kuali.module.financial.bo.BankAccount;
import org.kuali.module.financial.bo.CashDrawer;
import org.kuali.module.financial.bo.CashieringTransaction;
import org.kuali.module.financial.bo.Check;
import org.kuali.module.financial.bo.CoinDetail;
import org.kuali.module.financial.bo.CurrencyDetail;
import org.kuali.module.financial.bo.Deposit;
import org.kuali.module.financial.bo.DepositWizardCashieringCheckHelper;
import org.kuali.module.financial.bo.DepositWizardHelper;
import org.kuali.module.financial.document.CashManagementDocument;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.financial.exceptions.CashDrawerStateException;
import org.kuali.module.financial.service.CashDrawerService;
import org.kuali.module.financial.service.CashManagementService;
import org.kuali.module.financial.service.CashReceiptService;
import org.kuali.module.financial.web.struts.form.CashDrawerStatusCodeFormatter;
import org.kuali.module.financial.web.struts.form.DepositWizardForm;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class handles actions for the deposit wizard, which is used to create deposits that bundle groupings of Cash Receipt
 * documents.
 */
public class DepositWizardAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DepositWizardAction.class);

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
        String cmDocTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(CashManagementDocument.class);
        DocumentAuthorizer cmDocAuthorizer = SpringContext.getBean(DocumentAuthorizationService.class).getDocumentAuthorizer(cmDocTypeName);
        UniversalUser luser = GlobalVariables.getUserSession().getUniversalUser();
        cmDocAuthorizer.canInitiate(cmDocTypeName, luser);

        // populate the outgoing form used by the JSP if it seems empty
        String cmDocId = dwForm.getCashManagementDocId();
        if (StringUtils.isBlank(cmDocId)) {
            cmDocId = request.getParameter("cmDocId");
            String depositTypeCode = request.getParameter("depositTypeCode");

            CashManagementDocument cmDoc = (CashManagementDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(cmDocId);

            initializeForm(dwForm, cmDoc, depositTypeCode);
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
        String verificationUnit = cmDoc.getWorkgroupName();

        CashDrawer cd = SpringContext.getBean(CashDrawerService.class).getByWorkgroupName(verificationUnit, true);
        if (!cd.isOpen()) {
            CashDrawerStatusCodeFormatter f = new CashDrawerStatusCodeFormatter();

            String cmDocId = cmDoc.getDocumentNumber();
            String currentState = cd.getStatusCode();

            throw new CashDrawerStateException(verificationUnit, cmDocId, (String) f.format(CashDrawerConstants.STATUS_OPEN), (String) f.format(cd.getStatusCode()));
        }

        dform.setCashManagementDocId(cmDoc.getDocumentNumber());
        dform.setCashDrawerVerificationUnit(verificationUnit);

        dform.setDepositTypeCode(depositTypeCode);

        if (depositTypeCode.equals(KFSConstants.DocumentStatusCodes.CashReceipt.FINAL)) {
            // hey, we're the magical final deposit. We get currency and coin details!
            CurrencyDetail currencyDetail = new CurrencyDetail();
            currencyDetail.setDocumentNumber(cmDoc.getDocumentNumber());
            currencyDetail.setCashieringRecordSource(KFSConstants.CurrencyCoinSources.DEPOSITS);
            currencyDetail.setFinancialDocumentTypeCode(CashieringTransaction.DETAIL_DOCUMENT_TYPE);
            dform.setCurrencyDetail(currencyDetail);

            CoinDetail coinDetail = new CoinDetail();
            coinDetail.setDocumentNumber(cmDoc.getDocumentNumber());
            coinDetail.setCashieringRecordSource(KFSConstants.CurrencyCoinSources.DEPOSITS);
            coinDetail.setFinancialDocumentTypeCode(CashieringTransaction.DETAIL_DOCUMENT_TYPE);
            dform.setCoinDetail(coinDetail);
        }

        loadCashReceipts(dform);
        loadUndepositedCashieringChecks(dform);
    }

    /**
     * Loads the CashReceipt information, re/setting the related form fields
     * 
     * @param dform
     */
    private void loadCashReceipts(DepositWizardForm dform) {
        List verifiedReceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(dform.getCashDrawerVerificationUnit(), KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED);
        dform.setDepositableCashReceipts(new ArrayList());
        dform.setCheckFreeCashReceipts(new ArrayList<CashReceiptDocument>());

        // prepopulate DepositWizardHelpers
        int index = 0;
        for (Iterator i = verifiedReceipts.iterator(); i.hasNext();) {
            CashReceiptDocument receipt = (CashReceiptDocument) i.next();
            if (receipt.getCheckCount() == 0 && receipt.getTotalCheckAmount().equals(KualiDecimal.ZERO)) {
                dform.getCheckFreeCashReceipts().add(receipt);
            }
            else {
                dform.getDepositableCashReceipts().add(receipt);
                DepositWizardHelper d = dform.getDepositWizardHelper(index++);
                d.setCashReceiptCreateDate(receipt.getDocumentHeader().getWorkflowDocument().getCreateDate());
            }
        }
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


    /**
     * Reloads the CashReceipts, leaving everything else unchanged
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        loadCashReceipts((DepositWizardForm) form);
        loadUndepositedCashieringChecks((DepositWizardForm) form);

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
        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);

        // validate Bank and BankAccount
        boolean hasBankAccountNumber = false;
        String bankAccountNumber = dform.getBankAccountNumber();
        if (StringUtils.isBlank(bankAccountNumber)) {
            GlobalVariables.getErrorMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_MISSING_BANKACCOUNT);
        }
        else {
            hasBankAccountNumber = true;
        }

        String bankCode = dform.getBankCode();
        if (StringUtils.isBlank(bankCode)) {
            GlobalVariables.getErrorMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_MISSING_BANK);
        }
        else {
            Map keyMap = new HashMap();
            keyMap.put("financialDocumentBankCode", bankCode);

            Bank bank = (Bank) boService.findByPrimaryKey(Bank.class, keyMap);
            if (bank == null) {
                GlobalVariables.getErrorMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_UNKNOWN_BANK, bankCode);
            }
            else {
                dform.setBank(bank);

                if (hasBankAccountNumber) {
                    keyMap.put("finDocumentBankAccountNumber", bankAccountNumber);

                    BankAccount bankAccount = (BankAccount) boService.findByPrimaryKey(BankAccount.class, keyMap);
                    if (bankAccount == null) {
                        String[] msgParams = { bankAccountNumber, bankCode };
                        GlobalVariables.getErrorMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_UNKNOWN_BANKACCOUNT, msgParams);
                    }
                    else {
                        dform.setBankAccount(bankAccount);
                    }
                }
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
            for (Object crDocObj : SpringContext.getBean(CashReceiptService.class).getCashReceipts(dform.getCashDrawerVerificationUnit(), KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED)) {
                CashReceiptDocument crDoc = (CashReceiptDocument) crDocObj;
                if (crDoc.getCheckCount() == 0) {
                    // it's check free; it is automatically deposited as part of the final deposit
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
            GlobalVariables.getErrorMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_CASHRECEIPT_ERROR, KFSKeyConstants.Deposit.ERROR_NO_CASH_RECEIPTS_SELECTED);
        }

        //
        // proceed, if possible
        if (GlobalVariables.getErrorMap().isEmpty()) {
            try {
                // retrieve selected receipts
                List selectedReceipts = new ArrayList();
                if (selectedIds != null && !selectedIds.isEmpty()) {
                    selectedReceipts = SpringContext.getBean(DocumentService.class).getDocumentsByListOfDocumentHeaderIds(CashReceiptDocument.class, selectedIds);
                }

                if (depositIsFinal) {
                    // have all verified CRs been deposited? If not, that's an error
                    List verifiedReceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(dform.getCashDrawerVerificationUnit(), KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED);
                    for (Object o : verifiedReceipts) {
                        CashReceiptDocument crDoc = (CashReceiptDocument) o;
                        if (!selectedReceipts.contains(crDoc)) {
                            GlobalVariables.getErrorMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NON_DEPOSITED_VERIFIED_CASH_RECEIPT, new String[] { crDoc.getDocumentNumber() });
                        }
                    }
                    KualiDecimal toBeDepositedChecksTotal = KualiDecimal.ZERO;
                    // have we selected the rest of the undeposited checks?
                    for (Check check : SpringContext.getBean(CashManagementService.class).selectUndepositedCashieringChecks(dform.getCashManagementDocId())) {
                        if (!selectedCashieringChecks.contains(check.getSequenceId())) {
                            GlobalVariables.getErrorMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_CASHIERING_CHECK_MUST_BE_DEPOSITED, new String[] { check.getCheckNumber() });
                        }
                        else {
                            toBeDepositedChecksTotal = toBeDepositedChecksTotal.add(check.getAmount());
                        }
                    }

                    // does the cash drawer have enough currency and coin to fulfill the requested deposit?
                    checkEnoughCurrencyForDeposit(dform);
                    checkEnoughCoinForDeposit(dform);

                    // does this deposit have currency and coin to match all currency and coin from CRs?
                    List<CashReceiptDocument> interestingReceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(dform.getCashDrawerVerificationUnit(), new String[] { CashReceipt.VERIFIED, CashReceipt.INTERIM, CashReceipt.FINAL });
                    CurrencyDetail currencyTotal = new CurrencyDetail();
                    CoinDetail coinTotal = new CoinDetail();
                    for (CashReceiptDocument receipt : interestingReceipts) {
                        receipt.refreshCashDetails();
                        if (receipt.getCurrencyDetail() != null) {
                            currencyTotal.add(receipt.getCurrencyDetail());
                        }
                        if (receipt.getCoinDetail() != null) {
                            coinTotal.add(receipt.getCoinDetail());
                        }
                    }

                    KualiDecimal cashReceiptCashTotal = currencyTotal.getTotalAmount().add(coinTotal.getTotalAmount());
                    KualiDecimal depositedCashieringChecksTotal = SpringContext.getBean(CashManagementService.class).calculateDepositedCheckTotal(dform.getCashManagementDocId());
                    // remove the cashiering checks amounts from the cash receipts total; cashiering checks act as if they were CR
                    // currency/coin that gets deposited
                    cashReceiptCashTotal = cashReceiptCashTotal.subtract(depositedCashieringChecksTotal).subtract(toBeDepositedChecksTotal);
                    KualiDecimal depositedCashTotal = dform.getCurrencyDetail().getTotalAmount().add(dform.getCoinDetail().getTotalAmount());
                    if (!cashReceiptCashTotal.equals(depositedCashTotal)) {
                        GlobalVariables.getErrorMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_CASH_DEPOSIT_DID_NOT_BALANCE, new String[] { depositedCashTotal.toString(), cashReceiptCashTotal.toString() });
                    }
                }

                // proceed again...if possible
                if (GlobalVariables.getErrorMap().isEmpty()) {
                    // retrieve CashManagementDocument
                    String cashManagementDocId = dform.getCashManagementDocId();
                    CashManagementDocument cashManagementDoc = null;
                    try {
                        cashManagementDoc = (CashManagementDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(cashManagementDocId);
                        if (cashManagementDoc == null) {
                            throw new IllegalStateException("unable to find cashManagementDocument with id " + cashManagementDocId);
                        }
                    }
                    catch (WorkflowException e) {
                        throw new IllegalStateException("unable to retrieve cashManagementDocument with id " + cashManagementDocId, e);
                    }

                    // create deposit
                    String cmDocId = dform.getCashManagementDocId();

                    CashManagementService cms = SpringContext.getBean(CashManagementService.class);
                    cms.addDeposit(cashManagementDoc, dform.getDepositTicketNumber(), dform.getBankAccount(), selectedReceipts, selectedCashieringChecks, depositIsFinal);

                    if (depositIsFinal) {
                        // find the final deposit
                        Deposit finalDeposit = findFinalDeposit(cashManagementDoc);
                        // if the currency and coin details aren't empty, save them and remove them from the cash drawer
                        if (dform.getCurrencyDetail() != null) {
                            // do we have enough currency to allow the deposit to leave the drawer?
                            SpringContext.getBean(BusinessObjectService.class).save(dform.getCurrencyDetail());
                            cashManagementDoc.getCashDrawer().removeCurrency(dform.getCurrencyDetail());
                            finalDeposit.setDepositAmount(finalDeposit.getDepositAmount().add(dform.getCurrencyDetail().getTotalAmount()));
                        }
                        if (dform.getCoinDetail() != null) {
                            // do we have enough coin to allow the deposit to leave the drawer?
                            SpringContext.getBean(BusinessObjectService.class).save(dform.getCoinDetail());
                            cashManagementDoc.getCashDrawer().removeCoin(dform.getCoinDetail());
                            finalDeposit.setDepositAmount(finalDeposit.getDepositAmount().add(dform.getCoinDetail().getTotalAmount()));
                        }
                        SpringContext.getBean(BusinessObjectService.class).save(cashManagementDoc.getCashDrawer());
                        SpringContext.getBean(BusinessObjectService.class).save(finalDeposit);
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
            CashDrawer drawer = SpringContext.getBean(CashDrawerService.class).getByWorkgroupName(depositForm.getCashDrawerVerificationUnit(), false);
            // assumptions at this point:
            // 1. a cash drawer does exist for the unit
            // 2. we can ignore negative amounts, because if we have negative amounts, we're actually gaining money (and that will
            // happen with cashiering checks)
            if (detail.getFinancialDocumentHundredDollarAmount() != null && detail.getFinancialDocumentHundredDollarAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentHundredDollarAmount() == null || drawer.getFinancialDocumentHundredDollarAmount().isLessThan(detail.getFinancialDocumentHundredDollarAmount())) {
                    GlobalVariables.getErrorMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "hundred dollar amount", detail.getFinancialDocumentHundredDollarAmount().toString(), drawer.getFinancialDocumentHundredDollarAmount().toString() });
                    success = false;
                }
            }
            if (detail.getFinancialDocumentFiftyDollarAmount() != null && detail.getFinancialDocumentFiftyDollarAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentFiftyDollarAmount() == null || drawer.getFinancialDocumentFiftyDollarAmount().isLessThan(detail.getFinancialDocumentFiftyDollarAmount())) {
                    GlobalVariables.getErrorMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "fifty dollar amount", detail.getFinancialDocumentFiftyDollarAmount().toString(), drawer.getFinancialDocumentFiftyDollarAmount().toString() });
                    success = false;
                }
            }
            if (detail.getFinancialDocumentTwentyDollarAmount() != null && detail.getFinancialDocumentTwentyDollarAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentTwentyDollarAmount() == null || drawer.getFinancialDocumentTwentyDollarAmount().isLessThan(detail.getFinancialDocumentTwentyDollarAmount())) {
                    GlobalVariables.getErrorMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "twenty dollar amount", detail.getFinancialDocumentTwentyDollarAmount().toString(), drawer.getFinancialDocumentTwentyDollarAmount().toString() });
                    success = false;
                }
            }
            if (detail.getFinancialDocumentTenDollarAmount() != null && detail.getFinancialDocumentTenDollarAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentTenDollarAmount() == null || drawer.getFinancialDocumentTenDollarAmount().isLessThan(detail.getFinancialDocumentTenDollarAmount())) {
                    GlobalVariables.getErrorMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "ten dollar amount", detail.getFinancialDocumentTenDollarAmount().toString(), drawer.getFinancialDocumentTenDollarAmount().toString() });
                    success = false;
                }
            }
            if (detail.getFinancialDocumentFiveDollarAmount() != null && detail.getFinancialDocumentFiveDollarAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentFiveDollarAmount() == null || drawer.getFinancialDocumentFiveDollarAmount().isLessThan(detail.getFinancialDocumentFiveDollarAmount())) {
                    GlobalVariables.getErrorMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "five dollar amount", detail.getFinancialDocumentFiveDollarAmount().toString(), drawer.getFinancialDocumentFiveDollarAmount().toString() });
                    success = false;
                }
            }
            if (detail.getFinancialDocumentTwoDollarAmount() != null && detail.getFinancialDocumentTwoDollarAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentTwoDollarAmount() == null || drawer.getFinancialDocumentTwoDollarAmount().isLessThan(detail.getFinancialDocumentTwoDollarAmount())) {
                    GlobalVariables.getErrorMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "two dollar amount", detail.getFinancialDocumentTwoDollarAmount().toString(), drawer.getFinancialDocumentTwoDollarAmount().toString() });
                    success = false;
                }
            }
            if (detail.getFinancialDocumentOneDollarAmount() != null && detail.getFinancialDocumentOneDollarAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentOneDollarAmount() == null || drawer.getFinancialDocumentOneDollarAmount().isLessThan(detail.getFinancialDocumentOneDollarAmount())) {
                    GlobalVariables.getErrorMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "one dollar amount", detail.getFinancialDocumentOneDollarAmount().toString(), drawer.getFinancialDocumentOneDollarAmount().toString() });
                    success = false;
                }
            }
            if (detail.getFinancialDocumentOtherDollarAmount() != null && detail.getFinancialDocumentOtherDollarAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentOtherDollarAmount() == null || drawer.getFinancialDocumentOtherDollarAmount().isLessThan(detail.getFinancialDocumentOtherDollarAmount())) {
                    GlobalVariables.getErrorMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "other dollar amount", detail.getFinancialDocumentOtherDollarAmount().toString(), drawer.getFinancialDocumentOtherDollarAmount().toString() });
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
            CashDrawer drawer = SpringContext.getBean(CashDrawerService.class).getByWorkgroupName(depositForm.getCashDrawerVerificationUnit(), false);
            // assumptions at this point:
            // 1. a cash drawer does exist for the unit
            // 2. we can ignore negative amounts, because if we have negative amounts, we're actually gaining money (and that will
            // happen with cashiering checks)
            if (detail.getFinancialDocumentHundredCentAmount() != null && detail.getFinancialDocumentHundredCentAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentHundredCentAmount() == null || drawer.getFinancialDocumentHundredCentAmount().isLessThan(detail.getFinancialDocumentHundredCentAmount())) {
                    GlobalVariables.getErrorMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "hundred cent amount", detail.getFinancialDocumentHundredCentAmount().toString(), drawer.getFinancialDocumentHundredCentAmount().toString() });
                    success = false;
                }
            }
            if (detail.getFinancialDocumentFiftyCentAmount() != null && detail.getFinancialDocumentFiftyCentAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentFiftyCentAmount() == null || drawer.getFinancialDocumentFiftyCentAmount().isLessThan(detail.getFinancialDocumentFiftyCentAmount())) {
                    GlobalVariables.getErrorMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "fifty cent amount", detail.getFinancialDocumentFiftyCentAmount().toString(), drawer.getFinancialDocumentFiftyCentAmount().toString() });
                    success = false;
                }
            }
            if (detail.getFinancialDocumentTwentyFiveCentAmount() != null && detail.getFinancialDocumentTwentyFiveCentAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentTwentyFiveCentAmount() == null || drawer.getFinancialDocumentTwentyFiveCentAmount().isLessThan(detail.getFinancialDocumentTwentyFiveCentAmount())) {
                    GlobalVariables.getErrorMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "twenty five cent amount", detail.getFinancialDocumentTwentyFiveCentAmount().toString(), drawer.getFinancialDocumentTwentyFiveCentAmount().toString() });
                    success = false;
                }
            }
            if (detail.getFinancialDocumentTenCentAmount() != null && detail.getFinancialDocumentTenCentAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentTenCentAmount() == null || drawer.getFinancialDocumentTenCentAmount().isLessThan(detail.getFinancialDocumentTenCentAmount())) {
                    GlobalVariables.getErrorMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "ten cent amount", detail.getFinancialDocumentTenCentAmount().toString(), drawer.getFinancialDocumentTenCentAmount().toString() });
                    success = false;
                }
            }
            if (detail.getFinancialDocumentFiveCentAmount() != null && detail.getFinancialDocumentFiveCentAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentFiveCentAmount() == null || drawer.getFinancialDocumentFiveCentAmount().isLessThan(detail.getFinancialDocumentFiveCentAmount())) {
                    GlobalVariables.getErrorMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "five cent amount", detail.getFinancialDocumentFiveCentAmount().toString(), drawer.getFinancialDocumentFiveCentAmount().toString() });
                    success = false;
                }
            }
            if (detail.getFinancialDocumentOneCentAmount() != null && detail.getFinancialDocumentOneCentAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentOneCentAmount() == null || drawer.getFinancialDocumentOneCentAmount().isLessThan(detail.getFinancialDocumentOneCentAmount())) {
                    GlobalVariables.getErrorMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "one cent amount", detail.getFinancialDocumentOneCentAmount().toString(), drawer.getFinancialDocumentOneCentAmount().toString() });
                    success = false;
                }
            }
            if (detail.getFinancialDocumentOtherCentAmount() != null && detail.getFinancialDocumentOtherCentAmount().isGreaterThan(KualiDecimal.ZERO)) {
                if (drawer.getFinancialDocumentOtherCentAmount() == null || drawer.getFinancialDocumentOtherCentAmount().isLessThan(detail.getFinancialDocumentOtherCentAmount())) {
                    GlobalVariables.getErrorMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_DEPOSITHEADER_ERROR, KFSKeyConstants.Deposit.ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT, new String[] { "other cent amount", detail.getFinancialDocumentOtherCentAmount().toString(), drawer.getFinancialDocumentOtherCentAmount().toString() });
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
        String cmDocTypeName = SpringContext.getBean(DocumentTypeService.class).getDocumentTypeNameByClass(CashManagementDocument.class);

        Properties params = new Properties();
        params.setProperty("methodToCall", "docHandler");
        params.setProperty("command", "displayDocSearchView");
        params.setProperty("docId", cmDocId);

        String cmActionUrl = UrlFactory.parameterizeUrl(KFSConstants.CASH_MANAGEMENT_DOCUMENT_ACTION, params);

        return new ActionForward(cmActionUrl, true);
    }
}