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
package org.kuali.kfs.fp.document.web.struts;

import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonEmployeeExpense;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonEmployeeTravel;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPreConferenceRegistrant;
import org.kuali.kfs.fp.businessobject.WireCharge;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.service.DisbursementVoucherCoverSheetService;
import org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService;
import org.kuali.kfs.fp.document.service.DisbursementVoucherTaxService;
import org.kuali.kfs.fp.document.service.DisbursementVoucherTravelService;
import org.kuali.kfs.fp.document.service.impl.DisbursementVoucherCoverSheetServiceImpl;
import org.kuali.kfs.fp.document.validation.impl.DisbursementVoucherRuleConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;

/**
 * This class handles Actions for the DisbursementVoucher.
 */
public class DisbursementVoucherAction extends KualiAccountingDocumentActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherAction.class);


    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#execute(org.apache.struts.action.ActionMapping,
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
                DisbursementVoucherPayeeDetail dvPayeeDetail = dvDoc.getDvPayeeDetail();
                if(dvPayeeDetail == null || StringUtils.isBlank(dvPayeeDetail.getDisbVchrPayeeIdNumber())) {
                    String payeeIdNumber = (String)request.getParameter(KFSPropertyConstants.PAYEE_ID_NUMBER);
                    String payeeAddressIdentifier = (String) request.getParameter(KFSPropertyConstants.PAYEE_ADDRESS_IDENTIFIER);
                    String payeeTypeCode = (String)request.getParameter(KFSPropertyConstants.PAYEE_TYPE_CODE);
                    dvPayeeDetail.setDisbursementVoucherPayeeTypeCode(payeeTypeCode);
                    
                    // Determine what type of payee we're dealing with and setup disbursement voucher accordingly
                    if(StringUtils.equals(payeeTypeCode, DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_VENDOR)) {
                        setupPayeeAsVendor(dvForm, payeeIdNumber, payeeAddressIdentifier);
                    } else if(StringUtils.equals(payeeTypeCode, DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_EMPLOYEE)) {
                        setupPayeeAsEmployee(dvForm, payeeIdNumber);
                    }
                }
            }
        }

        return dest;
    }

    /**
     * 
     * This method...
     * @param dvForm
     * @param payeeIdNumber
     */
    private void setupPayeeAsEmployee(DisbursementVoucherForm dvForm, String payeeIdNumber) {
        Person person = (Person) SpringContext.getBean(PersonService.class).getPerson(payeeIdNumber);
        if (person != null) {
            ((DisbursementVoucherDocument) dvForm.getDocument()).templateEmployee(person);
        } else {
            LOG.error("Exception while attempting to retrieve universal user by universal user id "+payeeIdNumber);
        }
    }

    /**
     * 
     * This method...
     * @param dvForm
     * @param payeeIdNumber
     * @param payeeAddressIdentifier
     */
    private void setupPayeeAsVendor(DisbursementVoucherForm dvForm, String payeeIdNumber, String payeeAddressIdentifier) {
        VendorDetail vendorDetail = new VendorDetail();
        vendorDetail.setVendorNumber(payeeIdNumber);
        vendorDetail = (VendorDetail) SpringContext.getBean(BusinessObjectService.class).retrieve(vendorDetail);
        VendorAddress vendorAddress = new VendorAddress();
        if(StringUtils.isNotBlank(payeeAddressIdentifier)) {
            try {
                vendorAddress.setVendorAddressGeneratedIdentifier(new Integer(payeeAddressIdentifier));
                vendorAddress = (VendorAddress) SpringContext.getBean(BusinessObjectService.class).retrieve(vendorAddress);
            } catch(Exception x) {
                LOG.error("Exception while attempting to retrieve vendor address for vendor address id "+payeeAddressIdentifier+": "+x);
            }
        }
        ((DisbursementVoucherDocument) dvForm.getDocument()).templateVendor(vendorDetail, vendorAddress);
    }
    
    /**
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#approve(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward approve(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        SpringContext.getBean(DisbursementVoucherPayeeService.class).checkPayeeAddressForChanges((DisbursementVoucherDocument)dvForm.getDocument());
        
        return super.approve(mapping, form, request, response);
    }
        
    /**
     * Do initialization for a new disbursement voucher
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        ((DisbursementVoucherDocument) kualiDocumentFormBase.getDocument()).initiateDocument();

        // set wire charge message in form
        ((DisbursementVoucherForm) kualiDocumentFormBase).setWireChargeMessage(retrieveWireChargeMessage());
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
        // get directory of template
        String directory = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.EXTERNALIZABLE_HELP_URL_KEY);

        DisbursementVoucherDocument document = (DisbursementVoucherDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(request.getParameter(KFSPropertyConstants.DOCUMENT_NUMBER));

        // set workflow document back into form to prevent document authorizer "invalid (null)
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

    /**
     * 
     * This method...
     * @param dvNet
     */
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

    /**
     * 
     * This method...
     * @param dvNet
     */
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
    }

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
    }

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
        if (!GlobalVariables.getUserSession().getPerson().isMember(SpringContext.getBean(ParameterService.class).getParameterValue(DisbursementVoucherDocument.class, KFSConstants.FinancialApcParms.DV_TAX_WORKGROUP))) {
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
        if (!GlobalVariables.getUserSession().getPerson().isMember(SpringContext.getBean(ParameterService.class).getParameterValue(DisbursementVoucherDocument.class, KFSConstants.FinancialApcParms.DV_TAX_WORKGROUP))) {
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
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#performLookup(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward performLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // substitute bo class and mapping if the type is Employee, lookup already setup for Vendor
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument document = (DisbursementVoucherDocument) dvForm.getDocument();

        String fullParameter = (String) request.getAttribute(KFSConstants.METHOD_TO_CALL_ATTRIBUTE);
        String boClassName = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_BOPARM_LEFT_DEL, KFSConstants.METHOD_TO_CALL_BOPARM_RIGHT_DEL);

        if (VendorDetail.class.getName().equals(boClassName) && document.getDvPayeeDetail().isEmployee()) {
            String conversionFields = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM1_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM1_RIGHT_DEL);

            fullParameter = StringUtils.replace(fullParameter, boClassName, Person.class.getName());
            fullParameter = StringUtils.replace(fullParameter, conversionFields, "principalId:document.dvPayeeDetail.disbVchrPayeeIdNumber");
            request.setAttribute(KFSConstants.METHOD_TO_CALL_ATTRIBUTE, fullParameter);
        }

        return super.performLookup(mapping, form, request, response);
    }

    /*
     * Builds the wire charge message for the current fiscal year.
     * 
     * @return String
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

