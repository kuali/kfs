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
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSConstants.CashDrawerConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.financial.bo.Bank;
import org.kuali.module.financial.bo.BankAccount;
import org.kuali.module.financial.bo.CashDrawer;
import org.kuali.module.financial.bo.DepositWizardHelper;
import org.kuali.module.financial.document.CashManagementDocument;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.financial.exceptions.CashDrawerStateException;
import org.kuali.module.financial.service.CashManagementService;
import org.kuali.module.financial.web.struts.form.CashDrawerStatusCodeFormatter;
import org.kuali.module.financial.web.struts.form.DepositWizardForm;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class handles actions for the deposit wizard, which is used to create deposits that bundle groupings of Cash Receipt
 * documents.
 * 
 * 
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
        String cmDocTypeName = SpringServiceLocator.getDataDictionaryService().getDocumentTypeNameByClass(CashManagementDocument.class);
        DocumentAuthorizer cmDocAuthorizer = SpringServiceLocator.getDocumentAuthorizationService().getDocumentAuthorizer(cmDocTypeName);
        UniversalUser luser = GlobalVariables.getUserSession().getUniversalUser();
        cmDocAuthorizer.canInitiate(cmDocTypeName, luser);

        // populate the outgoing form used by the JSP if it seems empty
        String cmDocId = dwForm.getCashManagementDocId();
        if (StringUtils.isBlank(cmDocId)) {
            cmDocId = request.getParameter("cmDocId");
            String depositTypeCode = request.getParameter("depositTypeCode");

            CashManagementDocument cmDoc = (CashManagementDocument) SpringServiceLocator.getDocumentService().getByDocumentHeaderId(cmDocId);

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

        CashDrawer cd = SpringServiceLocator.getCashDrawerService().getByWorkgroupName(verificationUnit, true);
        if (!cd.isOpen()) {
            CashDrawerStatusCodeFormatter f = new CashDrawerStatusCodeFormatter();

            String cmDocId = cmDoc.getDocumentNumber();
            String currentState = cd.getStatusCode();

            throw new CashDrawerStateException(verificationUnit, cmDocId, (String) f.format(CashDrawerConstants.STATUS_OPEN), (String) f.format(cd.getStatusCode()));
        }

        dform.setCashManagementDocId(cmDoc.getDocumentNumber());
        dform.setCashDrawerVerificationUnit(verificationUnit);

        dform.setDepositTypeCode(depositTypeCode);

        loadCashReceipts(dform);
    }


    /**
     * Loads the CashReceipt information, re/setting the related form fields
     * 
     * @param dform
     */
    private void loadCashReceipts(DepositWizardForm dform) {
        List verifiedReceipts = SpringServiceLocator.getCashReceiptService().getCashReceipts(dform.getCashDrawerVerificationUnit(), KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED);
        dform.setDepositableCashReceipts(verifiedReceipts);

        // prepopulate DepositWizardHelpers
        int index = 0;
        for (Iterator i = verifiedReceipts.iterator(); i.hasNext();) {
            CashReceiptDocument receipt = (CashReceiptDocument) i.next();

            DepositWizardHelper d = dform.getDepositWizardHelper(index++);
            d.setCashReceiptCreateDate(receipt.getDocumentHeader().getWorkflowDocument().getCreateDate());
        }
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
        BusinessObjectService boService = SpringServiceLocator.getBusinessObjectService();

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

        // validate cashReceipt selection
        List selectedIds = new ArrayList();
        for (Iterator i = dform.getDepositWizardHelpers().iterator(); i.hasNext();) {
            String checkValue = ((DepositWizardHelper) i.next()).getSelectedValue();

            if (StringUtils.isNotBlank(checkValue) && !checkValue.equals(KFSConstants.ParameterValues.NO)) {
                // removed apparently-unnecessary test for !checkValue.equals(KFSConstants.ParameterValues.YES)
                selectedIds.add(checkValue);
            }
        }

        if (selectedIds.isEmpty()) {
            GlobalVariables.getErrorMap().putError(KFSConstants.DepositConstants.DEPOSIT_WIZARD_CASHRECEIPT_ERROR, KFSKeyConstants.Deposit.ERROR_NO_CASH_RECEIPTS_SELECTED);
        }

        //
        // proceed, if possible
        if (GlobalVariables.getErrorMap().isEmpty()) {
            try {
                // retrieve selected receipts
                List selectedReceipts = SpringServiceLocator.getDocumentService().getDocumentsByListOfDocumentHeaderIds(CashReceiptDocument.class, selectedIds);

                // retrieve CashManagementDocument
                String cashManagementDocId = dform.getCashManagementDocId();
                CashManagementDocument cashManagementDoc = null;
                try {
                    cashManagementDoc = (CashManagementDocument) SpringServiceLocator.getDocumentService().getByDocumentHeaderId(cashManagementDocId);
                    if (cashManagementDoc == null) {
                        throw new IllegalStateException("unable to find cashManagementDocument with id " + cashManagementDocId);
                    }
                }
                catch (WorkflowException e) {
                    throw new IllegalStateException("unable to retrieve cashManagementDocument with id " + cashManagementDocId, e);
                }


                // create deposit
                String cmDocId = dform.getCashManagementDocId();

                boolean depositIsFinal = (StringUtils.equals(dform.getDepositTypeCode(), KFSConstants.DepositConstants.DEPOSIT_TYPE_FINAL));
                CashManagementService cms = SpringServiceLocator.getCashManagementService();
                cms.addDeposit(cashManagementDoc, dform.getDepositTicketNumber(), dform.getBankAccount(), selectedReceipts, depositIsFinal);

                // redirect to controlling CashManagementDocument
                dest = returnToSender(cashManagementDocId);
            }
            catch (WorkflowException e) {
                throw new InfrastructureException("unable to retrieve cashReceipts by documentId", e);
            }
        }

        return dest;
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
        String cmDocTypeName = SpringServiceLocator.getDocumentTypeService().getDocumentTypeNameByClass(CashManagementDocument.class);

        Properties params = new Properties();
        params.setProperty("methodToCall", "docHandler");
        params.setProperty("command", "displayDocSearchView");
        params.setProperty("docId", cmDocId);

        String cmActionUrl = UrlFactory.buildDocumentActionUrl(cmDocTypeName, params);

        return new ActionForward(cmActionUrl, true);
    }
}