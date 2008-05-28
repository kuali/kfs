/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.Timer;
import org.kuali.core.util.WebUtils;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase;
import org.kuali.module.financial.bo.CashReceiptHeader;
import org.kuali.module.financial.bo.Check;
import org.kuali.module.financial.bo.CoinDetail;
import org.kuali.module.financial.bo.CurrencyDetail;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.financial.rule.event.AddCheckEvent;
import org.kuali.module.financial.rule.event.DeleteCheckEvent;
import org.kuali.module.financial.rule.event.UpdateCheckEvent;
import org.kuali.module.financial.service.CashReceiptCoverSheetService;
import org.kuali.module.financial.service.CashReceiptService;
import org.kuali.module.financial.web.struts.form.CashReceiptForm;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * 
 */
public class CashReceiptAction extends KualiAccountingDocumentActionBase {
    /**
     * Adds handling for check updates
     * 
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Timer t0 = new Timer("execute");
        CashReceiptForm cform = (CashReceiptForm) form;

        if (cform.hasDocumentId()) {
            CashReceiptDocument cdoc = cform.getCashReceiptDocument();

            // handle change of checkEntryMode
            processCheckEntryMode(cform, cdoc);

            // handle changes to checks (but only if current checkEntryMode is 'detail')
            if (CashReceiptDocument.CHECK_ENTRY_DETAIL.equals(cdoc.getCheckEntryMode())) {
                cdoc.setTotalCheckAmount(cdoc.calculateCheckTotal()); // recalc b/c changes to the amounts could have happened
                processChecks(cdoc, cform);
            }

            // generate errors for negative cash totals (especially for the recalculate button)
            SpringContext.getBean(CashReceiptService.class).areCashTotalsInvalid(cdoc);
        }

        // proceed as usual
        ActionForward result = super.execute(mapping, form, request, response);
        t0.log();
        return result;

    }

    /**
     * Prepares and streams CR PDF Cover Sheet
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward printCoverSheet(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        // get directory of tempate
        String directory = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.EXTERNALIZABLE_HELP_URL_KEY);

        // retrieve document
        String documentNumber = request.getParameter(KFSPropertyConstants.DOCUMENT_NUMBER);

        CashReceiptDocument document = (CashReceiptDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(documentNumber);

        // since this action isn't triggered by a post, we don't have the normal document data
        // so we have to set the document into the form manually so that later authz processing
        // has a document object instance to work with
        CashReceiptForm crForm = (CashReceiptForm) form;
        crForm.setDocument(document);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CashReceiptCoverSheetService coverSheetService = SpringContext.getBean(CashReceiptCoverSheetService.class);
        coverSheetService.generateCoverSheet(document, directory, baos);
        String fileName = documentNumber + "_cover_sheet.pdf";
        WebUtils.saveMimeOutputStreamAsFile(response, "application/pdf", baos, fileName);

        return null;
    }

    /**
     * This method processes the check entry mode to determine if the user is entering checks or if they are just entering the
     * total.
     * 
     * @param crForm
     * @param crDoc
     */
    private void processCheckEntryMode(CashReceiptForm crForm, CashReceiptDocument crDoc) {
        String formMode = crForm.getCheckEntryMode();
        String docMode = crDoc.getCheckEntryMode();

        if (CashReceiptDocument.CHECK_ENTRY_DETAIL.equals(formMode) || CashReceiptDocument.CHECK_ENTRY_TOTAL.equals(formMode)) {
            if (!formMode.equals(docMode)) {
                if (formMode.equals(CashReceiptDocument.CHECK_ENTRY_DETAIL)) {
                    // save current checkTotal, for future restoration
                    crForm.setCheckTotal(crDoc.getTotalCheckAmount());

                    // change mode
                    crDoc.setCheckEntryMode(formMode);
                    crDoc.setTotalCheckAmount(crDoc.calculateCheckTotal());

                    // notify user
                    GlobalVariables.getMessageList().add(KFSKeyConstants.CashReceipt.MSG_CHECK_ENTRY_INDIVIDUAL);
                }
                else {
                    // restore saved checkTotal
                    crDoc.setTotalCheckAmount(crForm.getCheckTotal());

                    // change mode
                    crDoc.setCheckEntryMode(formMode);

                    // notify user
                    GlobalVariables.getMessageList().add(KFSKeyConstants.CashReceipt.MSG_CHECK_ENTRY_TOTAL);
                }
            }
        }
    }

    /**
     * This method handles iterating over the check list and generating check events to apply rules to.
     * 
     * @param cdoc
     * @param cform
     */
    private void processChecks(CashReceiptDocument cdoc, CashReceiptForm cform) {
        List formChecks = cdoc.getChecks();

        int index = 0;
        Iterator i = formChecks.iterator();
        while (i.hasNext()) {
            Check formCheck = (Check) i.next();

            // only generate update events for specific action methods
            String methodToCall = cform.getMethodToCall();
            if (UPDATE_EVENT_ACTIONS.contains(methodToCall)) {
                SpringContext.getBean(KualiRuleService.class).applyRules(new UpdateCheckEvent(KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.CHECK + "[" + index + "]", cdoc, formCheck));
            }
            index++;
        }
    }

    /**
     * Adds Check instance created from the current "new check" line to the document
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward addCheck(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CashReceiptForm crForm = (CashReceiptForm) form;
        CashReceiptDocument crDoc = crForm.getCashReceiptDocument();

        Check newCheck = crForm.getNewCheck();
        newCheck.setDocumentNumber(crDoc.getDocumentNumber());

        // check business rules
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AddCheckEvent(KFSConstants.NEW_CHECK_PROPERTY_NAME, crDoc, newCheck));
        if (rulePassed) {
            // add check
            crDoc.addCheck(newCheck);

            // clear the used newCheck
            crForm.setNewCheck(crDoc.createNewCheck());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Deletes the selected check (line) from the document
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteCheck(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CashReceiptForm crForm = (CashReceiptForm) form;
        CashReceiptDocument crDoc = crForm.getCashReceiptDocument();

        int deleteIndex = getLineToDelete(request);
        Check oldCheck = crDoc.getCheck(deleteIndex);


        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new DeleteCheckEvent(KFSConstants.EXISTING_CHECK_PROPERTY_NAME, crDoc, oldCheck));

        if (rulePassed) {
            // delete check
            crDoc.removeCheck(deleteIndex);

            // delete baseline check, if any
            if (crForm.hasBaselineCheck(deleteIndex)) {
                crForm.getBaselineChecks().remove(deleteIndex);
            }
        }
        else {
            GlobalVariables.getErrorMap().putError("document.check[" + deleteIndex + "]", KFSKeyConstants.Check.ERROR_CHECK_DELETERULE, Integer.toString(deleteIndex));
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * Changes the current check-entry mode, if necessary
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward changeCheckEntryMode(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CashReceiptForm crForm = (CashReceiptForm) form;
        CashReceiptDocument crDoc = crForm.getCashReceiptDocument();

        String formMode = crForm.getCheckEntryMode();
        String docMode = crDoc.getCheckEntryMode();

        if (CashReceiptDocument.CHECK_ENTRY_DETAIL.equals(formMode) || CashReceiptDocument.CHECK_ENTRY_TOTAL.equals(formMode)) {
            if (!formMode.equals(docMode)) {

                if (formMode.equals(CashReceiptDocument.CHECK_ENTRY_DETAIL)) {
                    // save current checkTotal, for future restoration
                    crForm.setCheckTotal(crDoc.getTotalCheckAmount());

                    // change mode
                    crDoc.setCheckEntryMode(formMode);
                    crDoc.setTotalCheckAmount(crDoc.calculateCheckTotal());

                    // notify user
                    GlobalVariables.getMessageList().add(KFSKeyConstants.CashReceipt.MSG_CHECK_ENTRY_INDIVIDUAL);
                }
                else {
                    // restore saved checkTotal
                    crDoc.setTotalCheckAmount(crForm.getCheckTotal());

                    // change mode
                    crDoc.setCheckEntryMode(formMode);

                    // notify user
                    GlobalVariables.getMessageList().add(KFSKeyConstants.CashReceipt.MSG_CHECK_ENTRY_TOTAL);
                }
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * @see org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase#createDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);

        CashReceiptForm crForm = (CashReceiptForm) kualiDocumentFormBase;
        CashReceiptDocument crDoc = crForm.getCashReceiptDocument();

        CashReceiptService crs = SpringContext.getBean(CashReceiptService.class);
        String verificationUnit = crs.getCashReceiptVerificationUnitForUser(GlobalVariables.getUserSession().getUniversalUser());
        String campusCode = crs.getCampusCodeForCashReceiptVerificationUnit(verificationUnit);
        crDoc.setCampusLocationCode(campusCode);

        crDoc.setCashReceiptHeader(new CashReceiptHeader());
        crDoc.getCashReceiptHeader().setDocumentNumber(crDoc.getDocumentNumber());
        crDoc.getCashReceiptHeader().setWorkgroupName(verificationUnit);

        /* initialize currency and coin detail */
        CurrencyDetail currencyDetail = new CurrencyDetail();
        currencyDetail.setCashieringRecordSource(KFSConstants.CurrencyCoinSources.CASH_RECEIPTS);
        currencyDetail.setFinancialDocumentTypeCode(CashReceiptDocument.DOCUMENT_TYPE);
        currencyDetail.setDocumentNumber(crDoc.getDocumentNumber());
        crDoc.setCurrencyDetail(currencyDetail);

        CoinDetail coinDetail = new CoinDetail();
        coinDetail.setCashieringRecordSource(KFSConstants.CurrencyCoinSources.CASH_RECEIPTS);
        coinDetail.setFinancialDocumentTypeCode(CashReceiptDocument.DOCUMENT_TYPE);
        coinDetail.setDocumentNumber(crDoc.getDocumentNumber());
        crDoc.setCoinDetail(coinDetail);

        initDerivedCheckValues(crForm);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase#loadDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);

        initDerivedCheckValues((CashReceiptForm) kualiDocumentFormBase);
    }

    /**
     * Initializes form values which must be derived form document contents (i.e. those which aren't directly available from the
     * document)
     * 
     * @param cform
     */
    private void initDerivedCheckValues(CashReceiptForm cform) {
        CashReceiptDocument cdoc = cform.getCashReceiptDocument();

        cform.setCheckEntryMode(cdoc.getCheckEntryMode());
        cform.setCheckTotal(cdoc.getTotalCheckAmount());

        cform.getBaselineChecks().clear();
        cform.getBaselineChecks().addAll(cform.getCashReceiptDocument().getChecks());
    }
}
