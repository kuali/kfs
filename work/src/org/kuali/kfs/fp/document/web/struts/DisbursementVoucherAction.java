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
import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.WebUtils;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase;
import org.kuali.module.financial.bo.DisbursementVoucherNonEmployeeExpense;
import org.kuali.module.financial.bo.DisbursementVoucherNonEmployeeTravel;
import org.kuali.module.financial.bo.DisbursementVoucherPreConferenceRegistrant;
import org.kuali.module.financial.bo.Payee;
import org.kuali.module.financial.bo.WireCharge;
import org.kuali.module.financial.document.DisbursementVoucherDocument;
import org.kuali.module.financial.service.DisbursementVoucherCoverSheetService;
import org.kuali.module.financial.service.DisbursementVoucherTaxService;
import org.kuali.module.financial.service.DisbursementVoucherTravelService;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.financial.service.impl.DisbursementVoucherCoverSheetServiceImpl;
import org.kuali.module.financial.web.struts.form.DisbursementVoucherForm;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class handles Actions for the DisbursementVoucher.
 */
public class DisbursementVoucherAction extends KualiAccountingDocumentActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherAction.class);


    /**
     * @see org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward dest = super.execute(mapping, form, request, response);

        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        if (form != null) {
            DisbursementVoucherDocument dvDoc = (DisbursementVoucherDocument) dvForm.getDocument();
            if (dvDoc != null) {
                DisbursementVoucherNonEmployeeTravel dvNet = dvDoc.getDvNonEmployeeTravel();
                if (dvNet != null) {
                    // clear values derived from travelMileageAmount if that amount has been (manually) cleared
                    Integer amount = dvNet.getDvPersonalCarMileageAmount();
                    if ((amount == null) || (amount.intValue() == 0)) {
                        clearTravelMileageAmount(dvNet);
                    }

                    // clear values derived from perDiemRate if that amount has been (manually) cleared
                    KualiDecimal rate = dvNet.getDisbVchrPerdiemRate();
                    if ((rate == null) || rate.isZero()) {
                        clearTravelPerDiem(dvNet);
                    }
                }
            }
        }

        return dest;
    }


    /**
     * Do initialization for a new disbursement voucher
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        ((DisbursementVoucherDocument) kualiDocumentFormBase.getDocument()).initiateDocument();

        // set wire charge message in form
        ((DisbursementVoucherForm) kualiDocumentFormBase).setWireChargeMessage(retrieveWireChargeMessage());
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument document = (DisbursementVoucherDocument) dvForm.getDocument();

        /* refresh from dv payee lookup */
        if ((KFSConstants.KUALI_LOOKUPABLE_IMPL.equals(dvForm.getRefreshCaller()) || KFSConstants.KUALI_USER_LOOKUPABLE_IMPL.equals(dvForm.getRefreshCaller()))
                && request.getParameter(KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.DV_PAYEE_DETAIL + "." + KFSPropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER) != null
                && document.getDvPayeeDetail().isPayee()) {
            String payeeIdNumber = ((DisbursementVoucherDocument) dvForm.getDocument()).getDvPayeeDetail().getDisbVchrPayeeIdNumber();
            Payee refreshPayee = new Payee();
            refreshPayee.setPayeeIdNumber(payeeIdNumber);
            refreshPayee = (Payee) SpringContext.getBean(BusinessObjectService.class).retrieve(refreshPayee);
            ((DisbursementVoucherDocument) dvForm.getDocument()).templatePayee(refreshPayee);
        }
        /* refresh from employee lookup */
        else if ((KFSConstants.KUALI_LOOKUPABLE_IMPL.equals(dvForm.getRefreshCaller()) || KFSConstants.KUALI_USER_LOOKUPABLE_IMPL.equals(dvForm.getRefreshCaller()))
                && request.getParameter(KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.DV_PAYEE_DETAIL + "." + KFSPropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER) != null
                && document.getDvPayeeDetail().isEmployee()) {
            String emplUuid = ((DisbursementVoucherDocument) dvForm.getDocument()).getDvPayeeDetail().getDisbVchrPayeeIdNumber();
            UniversalUser employee = new UniversalUser();
            employee.setPersonUniversalIdentifier(emplUuid);
            employee = (UniversalUser) SpringContext.getBean(BusinessObjectService.class).retrieve(employee);
            ((DisbursementVoucherDocument) dvForm.getDocument()).templateEmployee(employee);
        }


        return super.refresh(mapping, form, request, response);
    }

    /**
     * Calls service to generate the disbursement voucher cover sheet as a pdf.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward printDisbursementVoucherCoverSheet(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // get directory of tempate
        String directory = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.EXTERNALIZABLE_HELP_URL_KEY);

        DisbursementVoucherDocument document = (DisbursementVoucherDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(request.getParameter(KFSPropertyConstants.DOCUMENT_NUMBER));

        // set worflow document back into form to prevent document authorizer "invalid (null)
        // document.documentHeader.workflowDocument" since we are bypassing form submit and just linking directly to the action
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        dvForm.getDocument().getDocumentHeader().setWorkflowDocument(document.getDocumentHeader().getWorkflowDocument());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DisbursementVoucherCoverSheetService coverSheetService = SpringContext.getBean(DisbursementVoucherCoverSheetService.class);

        coverSheetService.generateDisbursementVoucherCoverSheet(directory, DisbursementVoucherCoverSheetServiceImpl.DV_COVERSHEET_TEMPLATE_NM, document, baos);
        String fileName = document.getDocumentNumber() + "_cover_sheet.pdf";
        WebUtils.saveMimeOutputStreamAsFile(response, "application/pdf", baos, fileName);
        return (null);

    }

    /**
     * Calculates the travel per diem amount.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward calculateTravelPerDiem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) dvForm.getDocument();

        try {
            // call service to calculate per diem
            KualiDecimal perDiemAmount = SpringContext.getBean(DisbursementVoucherTravelService.class).calculatePerDiemAmount(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDisbVchrPerdiemRate());

            dvDocument.getDvNonEmployeeTravel().setDisbVchrPerdiemCalculatedAmt(perDiemAmount);
            dvDocument.getDvNonEmployeeTravel().setDisbVchrPerdiemActualAmount(perDiemAmount);
        }
        catch (RuntimeException e) {
            String errorMessage = e.getMessage();

            if (StringUtils.isBlank(errorMessage)) {
                errorMessage = "The per diem amount could not be calculated.  Please ensure all required per diem fields are filled in before attempting to calculate the per diem amount.";
            }

            LOG.error("Error in calculating travel per diem: " + errorMessage);
            GlobalVariables.getErrorMap().putError("DVNonEmployeeTravelErrors", KFSKeyConstants.ERROR_CUSTOM, errorMessage);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);

    }

    /**
     * Clears the travel per diem amount
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward clearTravelPerDiem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) dvForm.getDocument();

        DisbursementVoucherNonEmployeeTravel dvNet = dvDocument.getDvNonEmployeeTravel();
        if (dvNet != null) {
            clearTravelPerDiem(dvNet);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);

    }

    private void clearTravelPerDiem(DisbursementVoucherNonEmployeeTravel dvNet) {
        dvNet.setDisbVchrPerdiemCalculatedAmt(null);
        dvNet.setDisbVchrPerdiemActualAmount(null);
    }

    /**
     * Calculates the travel mileage amount.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward calculateTravelMileageAmount(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) dvForm.getDocument();

        if (dvDocument.getDvNonEmployeeTravel().getDvPersonalCarMileageAmount() == null) {
            LOG.error("Total Mileage must be given");
            GlobalVariables.getErrorMap().putError("DVNonEmployeeTravelErrors", KFSKeyConstants.ERROR_REQUIRED, "Total Mileage");
        }

        if (dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp() == null) {
            LOG.error("Travel Start Date must be given");
            GlobalVariables.getErrorMap().putError("DVNonEmployeeTravelErrors", KFSKeyConstants.ERROR_REQUIRED, "Travel Start Date");
        }

        if (GlobalVariables.getErrorMap().isEmpty()) {
            // call service to calculate mileage amount
            KualiDecimal mileageAmount = SpringContext.getBean(DisbursementVoucherTravelService.class).calculateMileageAmount(dvDocument.getDvNonEmployeeTravel().getDvPersonalCarMileageAmount(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp());

            dvDocument.getDvNonEmployeeTravel().setDisbVchrMileageCalculatedAmt(mileageAmount);
            dvDocument.getDvNonEmployeeTravel().setDisbVchrPersonalCarAmount(mileageAmount);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Clears the travel mileage amount
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward clearTravelMileageAmount(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) dvForm.getDocument();

        DisbursementVoucherNonEmployeeTravel dvNet = dvDocument.getDvNonEmployeeTravel();
        if (dvNet != null) {
            clearTravelMileageAmount(dvNet);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);

    }

    private void clearTravelMileageAmount(DisbursementVoucherNonEmployeeTravel dvNet) {
        dvNet.setDisbVchrMileageCalculatedAmt(null);
        dvNet.setDisbVchrPersonalCarAmount(null);
    }


    /**
     * Adds a new employee travel expense line.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward addNonEmployeeExpenseLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) dvForm.getDocument();

        DisbursementVoucherNonEmployeeExpense newExpenseLine = dvForm.getNewNonEmployeeExpenseLine();

        // validate line
        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.NEW_NONEMPLOYEE_EXPENSE_LINE);
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(newExpenseLine);

        // Ensure all fields are filled in before attempting to add a new expense line
        if (StringUtils.isBlank(newExpenseLine.getDisbVchrPrePaidExpenseCode())) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.DISB_VCHR_EXPENSE_CODE, KFSKeyConstants.ERROR_DV_EXPENSE_CODE);
        }
        if (StringUtils.isBlank(newExpenseLine.getDisbVchrPrePaidExpenseCompanyName())) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.DISB_VCHR_EXPENSE_COMPANY_NAME, KFSKeyConstants.ERROR_DV_EXPENSE_COMPANY_NAME);
        }
        if (ObjectUtils.isNull(newExpenseLine.getDisbVchrExpenseAmount())) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.DISB_VCHR_EXPENSE_AMOUNT, KFSKeyConstants.ERROR_DV_EXPENSE_AMOUNT);
        }

        GlobalVariables.getErrorMap().removeFromErrorPath(KFSPropertyConstants.NEW_NONEMPLOYEE_EXPENSE_LINE);

        if (GlobalVariables.getErrorMap().isEmpty()) {
            dvDocument.getDvNonEmployeeTravel().addDvNonEmployeeExpenseLine(newExpenseLine);
            dvForm.setNewNonEmployeeExpenseLine(new DisbursementVoucherNonEmployeeExpense());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    };

    /**
     * Adds a new employee pre paid travel expense line.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward addPrePaidNonEmployeeExpenseLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) dvForm.getDocument();

        DisbursementVoucherNonEmployeeExpense newExpenseLine = dvForm.getNewPrePaidNonEmployeeExpenseLine();

        // validate line
        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.NEW_PREPAID_EXPENSE_LINE);
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(newExpenseLine);

        // Ensure all fields are filled in before attempting to add a new expense line
        if (StringUtils.isBlank(newExpenseLine.getDisbVchrPrePaidExpenseCode())) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.DISB_VCHR_PRE_PAID_EXPENSE_CODE, KFSKeyConstants.ERROR_DV_PREPAID_EXPENSE_CODE);
        }
        if (StringUtils.isBlank(newExpenseLine.getDisbVchrPrePaidExpenseCompanyName())) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.DISB_VCHR_PRE_PAID_EXPENSE_COMPANY_NAME, KFSKeyConstants.ERROR_DV_PREPAID_EXPENSE_COMPANY_NAME);
        }
        if (ObjectUtils.isNull(newExpenseLine.getDisbVchrExpenseAmount())) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.DISB_VCHR_EXPENSE_AMOUNT, KFSKeyConstants.ERROR_DV_PREPAID_EXPENSE_AMOUNT);
        }
        GlobalVariables.getErrorMap().removeFromErrorPath(KFSPropertyConstants.NEW_PREPAID_EXPENSE_LINE);

        if (GlobalVariables.getErrorMap().isEmpty()) {
            dvDocument.getDvNonEmployeeTravel().addDvPrePaidEmployeeExpenseLine(newExpenseLine);
            dvForm.setNewPrePaidNonEmployeeExpenseLine(new DisbursementVoucherNonEmployeeExpense());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    };

    /**
     * Deletes a non employee travel expense line.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteNonEmployeeExpenseLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) dvForm.getDocument();

        int deleteIndex = getLineToDelete(request);
        dvDocument.getDvNonEmployeeTravel().getDvNonEmployeeExpenses().remove(deleteIndex);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Deletes a pre paid travel expense line.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deletePrePaidEmployeeExpenseLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) dvForm.getDocument();

        int deleteIndex = getLineToDelete(request);
        dvDocument.getDvNonEmployeeTravel().getDvPrePaidEmployeeExpenses().remove(deleteIndex);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Adds a new pre conference registrant line.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward addPreConfRegistrantLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) dvForm.getDocument();

        DisbursementVoucherPreConferenceRegistrant newRegistrantLine = dvForm.getNewPreConferenceRegistrantLine();

        // validate line
        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.NEW_PRECONF_REGISTRANT_LINE);
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(newRegistrantLine);
        GlobalVariables.getErrorMap().removeFromErrorPath(KFSPropertyConstants.NEW_PRECONF_REGISTRANT_LINE);

        if (GlobalVariables.getErrorMap().isEmpty()) {
            dvDocument.addDvPrePaidRegistrantLine(newRegistrantLine);
            dvForm.setNewPreConferenceRegistrantLine(new DisbursementVoucherPreConferenceRegistrant());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);

    }

    /**
     * Deletes a pre conference registrant line.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deletePreConfRegistrantLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) dvForm.getDocument();

        int deleteIndex = getLineToDelete(request);
        dvDocument.getDvPreConferenceDetail().getDvPreConferenceRegistrants().remove(deleteIndex);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);

    }


    /**
     * Calls service to generate tax accounting lines and updates nra tax line string in action form.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward generateNonResidentAlienTaxLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument document = (DisbursementVoucherDocument) dvForm.getDocument();

        /* user should not have generate button if not in tax group, but check just to make sure */
        if (!GlobalVariables.getUserSession().getUniversalUser().isMember(SpringContext.getBean(ParameterService.class).getParameterValue(DisbursementVoucherDocument.class, KFSConstants.FinancialApcParms.DV_TAX_WORKGROUP))) {
            LOG.info("User requested generateNonResidentAlienTaxLines who is not in the kuali tax group.");
            GlobalVariables.getErrorMap().putError(KFSConstants.DV_NRATAX_TAB_ERRORS, KFSKeyConstants.ERROR_DV_NRA_PERMISSIONS_GENERATE);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        DisbursementVoucherTaxService taxService = SpringContext.getBean(DisbursementVoucherTaxService.class);

        /* call service to generate new tax lines */
        GlobalVariables.getErrorMap().addToErrorPath("document");
        taxService.processNonResidentAlienTax(document);
        GlobalVariables.getErrorMap().removeFromErrorPath("document");

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Calls service to clear tax accounting lines and updates nra tax line string in action form.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward clearNonResidentAlienTaxLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument document = (DisbursementVoucherDocument) dvForm.getDocument();

        /* user should not have generate button if not in tax group, but check just to make sure */
        if (!GlobalVariables.getUserSession().getUniversalUser().isMember(SpringContext.getBean(ParameterService.class).getParameterValue(DisbursementVoucherDocument.class, KFSConstants.FinancialApcParms.DV_TAX_WORKGROUP))) {
            LOG.info("User requested generateNonResidentAlienTaxLines who is not in the kuali tax group.");
            GlobalVariables.getErrorMap().putError(KFSConstants.DV_NRATAX_TAB_ERRORS, KFSKeyConstants.ERROR_DV_NRA_PERMISSIONS_GENERATE);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        DisbursementVoucherTaxService taxService = SpringContext.getBean(DisbursementVoucherTaxService.class);

        /* call service to clear previous lines */
        taxService.clearNRATaxLines(document);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Hook into performLookup to switch the payee lookup based on the payee type selected.
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#performLookup(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward performLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // substitute bo class and mapping if the type is Employee, lookup already setup for Payee
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument document = (DisbursementVoucherDocument) dvForm.getDocument();

        String fullParameter = (String) request.getAttribute(KFSConstants.METHOD_TO_CALL_ATTRIBUTE);
        String boClassName = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_BOPARM_LEFT_DEL, KFSConstants.METHOD_TO_CALL_BOPARM_RIGHT_DEL);

        if (Payee.class.getName().equals(boClassName) && document.getDvPayeeDetail().isEmployee()) {
            String conversionFields = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM1_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM1_RIGHT_DEL);

            fullParameter = StringUtils.replace(fullParameter, boClassName, UniversalUser.class.getName());
            fullParameter = StringUtils.replace(fullParameter, conversionFields, "personUniversalIdentifier:document.dvPayeeDetail.disbVchrPayeeIdNumber");
            request.setAttribute(KFSConstants.METHOD_TO_CALL_ATTRIBUTE, fullParameter);
        }

        return super.performLookup(mapping, form, request, response);
    }

    /*
     * Builds the wire charge message for the current fiscal year.
     */
    private String retrieveWireChargeMessage() {
        String message = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSKeyConstants.MESSAGE_DV_WIRE_CHARGE);
        WireCharge wireCharge = new WireCharge();
        wireCharge.setUniversityFiscalYear(SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear());

        wireCharge = (WireCharge) SpringContext.getBean(BusinessObjectService.class).retrieve(wireCharge);
        Object[] args = { wireCharge.getDomesticChargeAmt(), wireCharge.getForeignChargeAmt() };

        return MessageFormat.format(message, args);
    }

}
