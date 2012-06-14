/*
 * Copyright 2005 The Kuali Foundation
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

import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonEmployeeExpense;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonEmployeeTravel;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPreConferenceRegistrant;
import org.kuali.kfs.fp.businessobject.WireCharge;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants.TabByReasonCode;
import org.kuali.kfs.fp.document.service.DisbursementVoucherCoverSheetService;
import org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService;
import org.kuali.kfs.fp.document.service.DisbursementVoucherTaxService;
import org.kuali.kfs.fp.document.service.DisbursementVoucherTravelService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentPresentationController;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * This class handles Actions for the DisbursementVoucher.
 */
public class DisbursementVoucherAction extends KualiAccountingDocumentActionBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherAction.class);

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
            }
        }

        return dest;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#approve(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward approve(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        SpringContext.getBean(DisbursementVoucherPayeeService.class).checkPayeeAddressForChanges((DisbursementVoucherDocument) dvForm.getDocument());

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
     */
    public ActionForward printDisbursementVoucherCoverSheet(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;

        // get directory of template
        String directory = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.EXTERNALIZABLE_HELP_URL_KEY);

        DisbursementVoucherDocument document = (DisbursementVoucherDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(request.getParameter(KFSPropertyConstants.DOCUMENT_NUMBER));

        // set workflow document back into form to prevent document authorizer "invalid (null)
        // document.documentHeader.workflowDocument" since we are bypassing form submit and just linking directly to the action

        dvForm.getDocument().getDocumentHeader().setWorkflowDocument(document.getDocumentHeader().getWorkflowDocument());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DisbursementVoucherCoverSheetService coverSheetService = SpringContext.getBean(DisbursementVoucherCoverSheetService.class);

        coverSheetService.generateDisbursementVoucherCoverSheet(directory, DisbursementVoucherConstants.DV_COVER_SHEET_TEMPLATE_NM, document, baos);
        String fileName = document.getDocumentNumber() + "_cover_sheet.pdf";
        WebUtils.saveMimeOutputStreamAsFile(response, "application/pdf", baos, fileName);

        return (null);
    }

    /**
     * Calculates the travel per diem amount.
     */
    public ActionForward calculateTravelPerDiem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;

        try {
            // call service to calculate per diem
            DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) dvForm.getDocument();
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
            GlobalVariables.getMessageMap().putError("DVNonEmployeeTravelErrors", KFSKeyConstants.ERROR_CUSTOM, errorMessage);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Clears the travel per diem amount
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
     * clear travel perdiem amounts
     */
    protected void clearTravelPerDiem(DisbursementVoucherNonEmployeeTravel dvNet) {
        dvNet.setDisbVchrPerdiemCalculatedAmt(null);
        dvNet.setDisbVchrPerdiemActualAmount(null);
    }

    /**
     * Calculates the travel mileage amount.
     */
    public ActionForward calculateTravelMileageAmount(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) dvForm.getDocument();

        if (dvDocument.getDvNonEmployeeTravel().getDvPersonalCarMileageAmount() == null) {
            LOG.error("Total Mileage must be given");
            GlobalVariables.getMessageMap().putError("DVNonEmployeeTravelErrors", KFSKeyConstants.ERROR_REQUIRED, "Total Mileage");
        }

        if (dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp() == null) {
            LOG.error("Travel Start Date must be given");
            GlobalVariables.getMessageMap().putError("DVNonEmployeeTravelErrors", KFSKeyConstants.ERROR_REQUIRED, "Travel Start Date");
        }

        if (GlobalVariables.getMessageMap().hasErrors()) {
            // call service to calculate mileage amount
            KualiDecimal mileageAmount = SpringContext.getBean(DisbursementVoucherTravelService.class).calculateMileageAmount(dvDocument.getDvNonEmployeeTravel().getDvPersonalCarMileageAmount(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp());

            dvDocument.getDvNonEmployeeTravel().setDisbVchrMileageCalculatedAmt(mileageAmount);
            dvDocument.getDvNonEmployeeTravel().setDisbVchrPersonalCarAmount(mileageAmount);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Clears the travel mileage amount
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
     * reset the travel mileage amount as null
     */
    protected void clearTravelMileageAmount(DisbursementVoucherNonEmployeeTravel dvNet) {
        dvNet.setDisbVchrMileageCalculatedAmt(null);
        dvNet.setDisbVchrPersonalCarAmount(null);
    }

    /**
     * Adds a new employee travel expense line.
     */
    public ActionForward addNonEmployeeExpenseLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) dvForm.getDocument();

        DisbursementVoucherNonEmployeeExpense newExpenseLine = dvForm.getNewNonEmployeeExpenseLine();

        // validate line
        GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.NEW_NONEMPLOYEE_EXPENSE_LINE);
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(newExpenseLine);

        // Ensure all fields are filled in before attempting to add a new expense line
        if (StringUtils.isBlank(newExpenseLine.getDisbVchrPrePaidExpenseCode())) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.DISB_VCHR_EXPENSE_CODE, KFSKeyConstants.ERROR_DV_EXPENSE_CODE);
        }
        if (StringUtils.isBlank(newExpenseLine.getDisbVchrPrePaidExpenseCompanyName())) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.DISB_VCHR_EXPENSE_COMPANY_NAME, KFSKeyConstants.ERROR_DV_EXPENSE_COMPANY_NAME);
        }
        if (ObjectUtils.isNull(newExpenseLine.getDisbVchrExpenseAmount())) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.DISB_VCHR_EXPENSE_AMOUNT, KFSKeyConstants.ERROR_DV_EXPENSE_AMOUNT);
        }

        GlobalVariables.getMessageMap().removeFromErrorPath(KFSPropertyConstants.NEW_NONEMPLOYEE_EXPENSE_LINE);

        if (GlobalVariables.getMessageMap().hasErrors()) {
            dvDocument.getDvNonEmployeeTravel().addDvNonEmployeeExpenseLine(newExpenseLine);
            dvForm.setNewNonEmployeeExpenseLine(new DisbursementVoucherNonEmployeeExpense());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Adds a new employee pre paid travel expense line.
     */
    public ActionForward addPrePaidNonEmployeeExpenseLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) dvForm.getDocument();

        DisbursementVoucherNonEmployeeExpense newExpenseLine = dvForm.getNewPrePaidNonEmployeeExpenseLine();

        // validate line
        GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.NEW_PREPAID_EXPENSE_LINE);
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(newExpenseLine);

        // Ensure all fields are filled in before attempting to add a new expense line
        if (StringUtils.isBlank(newExpenseLine.getDisbVchrPrePaidExpenseCode())) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.DISB_VCHR_PRE_PAID_EXPENSE_CODE, KFSKeyConstants.ERROR_DV_PREPAID_EXPENSE_CODE);
        }
        if (StringUtils.isBlank(newExpenseLine.getDisbVchrPrePaidExpenseCompanyName())) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.DISB_VCHR_PRE_PAID_EXPENSE_COMPANY_NAME, KFSKeyConstants.ERROR_DV_PREPAID_EXPENSE_COMPANY_NAME);
        }
        if (ObjectUtils.isNull(newExpenseLine.getDisbVchrExpenseAmount())) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.DISB_VCHR_EXPENSE_AMOUNT, KFSKeyConstants.ERROR_DV_PREPAID_EXPENSE_AMOUNT);
        }
        GlobalVariables.getMessageMap().removeFromErrorPath(KFSPropertyConstants.NEW_PREPAID_EXPENSE_LINE);

        if (GlobalVariables.getMessageMap().hasErrors()) {
            dvDocument.getDvNonEmployeeTravel().addDvPrePaidEmployeeExpenseLine(newExpenseLine);
            dvForm.setNewPrePaidNonEmployeeExpenseLine(new DisbursementVoucherNonEmployeeExpense());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Deletes a non employee travel expense line.
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
     */
    public ActionForward addPreConfRegistrantLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) dvForm.getDocument();

        DisbursementVoucherPreConferenceRegistrant newRegistrantLine = dvForm.getNewPreConferenceRegistrantLine();

        // validate line
        GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.NEW_PRECONF_REGISTRANT_LINE);
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(newRegistrantLine);
        GlobalVariables.getMessageMap().removeFromErrorPath(KFSPropertyConstants.NEW_PRECONF_REGISTRANT_LINE);

        if (! GlobalVariables.getMessageMap().hasErrors()) {
            dvDocument.addDvPrePaidRegistrantLine(newRegistrantLine);
            dvForm.setNewPreConferenceRegistrantLine(new DisbursementVoucherPreConferenceRegistrant());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Deletes a pre conference registrant line.
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
     */
    public ActionForward generateNonResidentAlienTaxLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument document = (DisbursementVoucherDocument) dvForm.getDocument();

        DisbursementVoucherTaxService taxService = SpringContext.getBean(DisbursementVoucherTaxService.class);

        /* call service to generate new tax lines */
        GlobalVariables.getMessageMap().addToErrorPath("document");
        taxService.processNonResidentAlienTax(document);
        GlobalVariables.getMessageMap().removeFromErrorPath("document");

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Calls service to clear tax accounting lines and updates nra tax line string in action form.
     */
    public ActionForward clearNonResidentAlienTaxLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument document = (DisbursementVoucherDocument) dvForm.getDocument();

        DisbursementVoucherTaxService taxService = SpringContext.getBean(DisbursementVoucherTaxService.class);

        /* call service to clear previous lines */
        taxService.clearNRATaxLines(document);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Calls service to clear tax info.
     */
    public ActionForward clearNonResidentAlienTaxInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument document = (DisbursementVoucherDocument) dvForm.getDocument();

        DisbursementVoucherTaxService taxService = SpringContext.getBean(DisbursementVoucherTaxService.class);

        /* call service to clear previous lines */
        taxService.clearNRATaxInfo(document);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * Builds the wire charge message for the current fiscal year.
     * 
     * @return the wire charge message for the current fiscal year
     */
    protected String retrieveWireChargeMessage() {
        String message = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSKeyConstants.MESSAGE_DV_WIRE_CHARGE);
        WireCharge wireCharge = new WireCharge();
        wireCharge.setUniversityFiscalYear(SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear());

        wireCharge = (WireCharge) SpringContext.getBean(BusinessObjectService.class).retrieve(wireCharge);
        Object[] args = { wireCharge.getDomesticChargeAmt(), wireCharge.getForeignChargeAmt() };

        return MessageFormat.format(message, args);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;

        ActionForward actionAfterPayeeLookup = this.refreshAfterPayeeSelection(mapping, dvForm, request);
        if (actionAfterPayeeLookup != null) {
            return actionAfterPayeeLookup;
        }

        return super.refresh(mapping, form, request, response);
    }

    // do refresh after a payee is selected
    protected ActionForward refreshAfterPayeeSelection(ActionMapping mapping, DisbursementVoucherForm dvForm, HttpServletRequest request) {
        String refreshCaller = dvForm.getRefreshCaller();
        
        DisbursementVoucherDocument document = (DisbursementVoucherDocument) dvForm.getDocument();

        boolean isPayeeLookupable = KFSConstants.KUALI_DISBURSEMENT_PAYEE_LOOKUPABLE_IMPL.equals(refreshCaller);
        boolean isAddressLookupable = KFSConstants.KUALI_VENDOR_ADDRESS_LOOKUPABLE_IMPL.equals(refreshCaller);

        // if a cancel occurred on address lookup we need to reset the payee id and type, rest of fields will still have correct information
        if (refreshCaller == null && hasFullEdit(document)) {
            dvForm.setPayeeIdNumber(dvForm.getTempPayeeIdNumber());
            dvForm.setHasMultipleAddresses(false);
            document.getDvPayeeDetail().setDisbVchrPayeeIdNumber(dvForm.getTempPayeeIdNumber());
            document.getDvPayeeDetail().setDisbursementVoucherPayeeTypeCode(dvForm.getOldPayeeType());
            
            return null;
        }
        
        // do not execute the further refreshing logic if the refresh caller is not a lookupable
        if (!isPayeeLookupable && !isAddressLookupable) {
            return null;
        }

        // do not execute the further refreshing logic if a payee is not selected
        String payeeIdNumber = document.getDvPayeeDetail().getDisbVchrPayeeIdNumber();
        if (payeeIdNumber == null) {
            return null;
        }

        dvForm.setPayeeIdNumber(payeeIdNumber);
        dvForm.setHasMultipleAddresses(false);

        // determine whether the selected vendor has multiple addresses. If so, redirect to the address selection screen
        if (isPayeeLookupable && dvForm.isVendor()) {
            VendorDetail refreshVendorDetail = new VendorDetail();
            refreshVendorDetail.setVendorNumber(payeeIdNumber);
            refreshVendorDetail = (VendorDetail) SpringContext.getBean(BusinessObjectService.class).retrieve(refreshVendorDetail);

            VendorAddress defaultVendorAddress = null;
            if (refreshVendorDetail != null) {
                List<VendorAddress> vendorAddresses = refreshVendorDetail.getVendorAddresses();
                boolean hasMultipleAddresses = vendorAddresses != null && vendorAddresses.size() > 1;
                dvForm.setHasMultipleAddresses(hasMultipleAddresses);

                if (vendorAddresses != null) {
                    defaultVendorAddress = vendorAddresses.get(0);
                }
            }

            if (dvForm.hasMultipleAddresses()) {
                return renderVendorAddressSelection(mapping, request, dvForm);
            }
            else if (defaultVendorAddress != null) {
                setupPayeeAsVendor(dvForm, payeeIdNumber, defaultVendorAddress.getVendorAddressGeneratedIdentifier().toString());
            }

            return null;
        }

        if (isPayeeLookupable && dvForm.isEmployee()) {
            this.setupPayeeAsEmployee(dvForm, payeeIdNumber);
        }

        String payeeAddressIdentifier = request.getParameter(KFSPropertyConstants.VENDOR_ADDRESS_GENERATED_ID);
        if (isAddressLookupable && StringUtils.isNotBlank(payeeAddressIdentifier)) {
            setupPayeeAsVendor(dvForm, payeeIdNumber, payeeAddressIdentifier);
        }

        String paymentReasonCode = document.getDvPayeeDetail().getDisbVchrPaymentReasonCode();
        addPaymentCodeWarningMessage(dvForm, paymentReasonCode);

        return null;
    }
    
    /**
     * Determines if the current user has full edit permissions on the document, which would allow them to repopulate the payee
     * @param document the document to check for full edit permissions on
     * @return true if full edit is allowed on the document, false otherwise
     */
    protected boolean hasFullEdit(DisbursementVoucherDocument document) {
        final Person user = GlobalVariables.getUserSession().getPerson();
        final TransactionalDocumentPresentationController documentPresentationController = (TransactionalDocumentPresentationController)getDocumentHelperService().getDocumentPresentationController(document);
        final TransactionalDocumentAuthorizer documentAuthorizer = (TransactionalDocumentAuthorizer)getDocumentHelperService().getDocumentAuthorizer(document);
        Set<String> documentActions =  documentPresentationController.getDocumentActions(document);
        documentActions = documentAuthorizer.getDocumentActions(document, user, documentActions);

        if (getDataDictionaryService().getDataDictionary().getDocumentEntry(document.getClass().getName()).getUsePessimisticLocking()) {
            documentActions = getPessimisticLockService().getDocumentActions(document, user, documentActions);
        }
        
        Set<String> editModes = documentPresentationController.getEditModes(document);
        editModes = documentAuthorizer.getEditModes(document, user, editModes);
        
        return documentActions.contains(KRADConstants.KUALI_ACTION_CAN_EDIT) && editModes.contains("fullEntry");
    }

    /**
     * Hook into performLookup to switch the payee lookup based on the payee type selected.
     */
    public ActionForward performLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument document = (DisbursementVoucherDocument) dvForm.getDocument();

        return super.performLookup(mapping, form, request, response);
    }

    /**
     * render the vendor address lookup results if there are multiple addresses for the selected vendor
     */
    protected ActionForward renderVendorAddressSelection(ActionMapping mapping, HttpServletRequest request, DisbursementVoucherForm dvForm) {
        Properties props = new Properties();

        props.put(KRADConstants.SUPPRESS_ACTIONS, Boolean.toString(true));
        props.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, VendorAddress.class.getName());
        props.put(KRADConstants.LOOKUP_ANCHOR, KRADConstants.ANCHOR_TOP_OF_FORM);
        props.put(KRADConstants.LOOKED_UP_COLLECTION_NAME, KFSPropertyConstants.VENDOR_ADDRESSES);

        String conversionPatttern = "{0}" + KFSConstants.FIELD_CONVERSION_PAIR_SEPERATOR + "{0}";
        StringBuilder filedConversion = new StringBuilder();
        filedConversion.append(MessageFormat.format(conversionPatttern, KFSPropertyConstants.VENDOR_ADDRESS_GENERATED_ID)).append(KFSConstants.FIELD_CONVERSIONS_SEPERATOR);
        filedConversion.append(MessageFormat.format(conversionPatttern, KFSPropertyConstants.VENDOR_HEADER_GENERATED_ID)).append(KFSConstants.FIELD_CONVERSIONS_SEPERATOR);
        filedConversion.append(MessageFormat.format(conversionPatttern, KFSPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID));
        props.put(KRADConstants.CONVERSION_FIELDS_PARAMETER, filedConversion);

        props.put(KFSPropertyConstants.VENDOR_HEADER_GENERATED_ID, dvForm.getVendorHeaderGeneratedIdentifier());
        props.put(KFSPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, dvForm.getVendorDetailAssignedIdentifier());
        props.put(KFSPropertyConstants.ACTIVE, KFSConstants.ACTIVE_INDICATOR);

        props.put(KRADConstants.RETURN_LOCATION_PARAMETER, this.getReturnLocation(request, mapping));
        props.put(KRADConstants.BACK_LOCATION, this.getReturnLocation(request, mapping));

        props.put(KRADConstants.LOOKUP_AUTO_SEARCH, "Yes");
        props.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);

        props.put(KRADConstants.DOC_FORM_KEY, GlobalVariables.getUserSession().addObjectWithGeneratedKey(dvForm));
        props.put(KRADConstants.DOC_NUM, dvForm.getDocument().getDocumentNumber());

        // TODO: how should this forward be handled
        String url = UrlFactory.parameterizeUrl(getApplicationBaseUrl() + "/kr/" + KRADConstants.LOOKUP_ACTION, props);

        dvForm.registerEditableProperty("methodToCall");

        return new ActionForward(url, true);
    }

    /**
     * setup the payee as an employee with the given id number
     */
    protected void setupPayeeAsEmployee(DisbursementVoucherForm dvForm, String payeeIdNumber) {
        Person person = (Person) SpringContext.getBean(PersonService.class).getPersonByEmployeeId(payeeIdNumber);
        if (person != null) {
            ((DisbursementVoucherDocument) dvForm.getDocument()).templateEmployee(person);
            dvForm.setTempPayeeIdNumber(payeeIdNumber);
            dvForm.setOldPayeeType(DisbursementVoucherConstants.DV_PAYEE_TYPE_EMPLOYEE);

        }
        else {
            LOG.error("Exception while attempting to retrieve universal user by universal user id " + payeeIdNumber);
        }
    }

    /**
     * setup the payee as a vendor with the given id number and address id
     */
    protected void setupPayeeAsVendor(DisbursementVoucherForm dvForm, String payeeIdNumber, String payeeAddressIdentifier) {
        VendorDetail vendorDetail = new VendorDetail();
        vendorDetail.setVendorNumber(payeeIdNumber);
        vendorDetail = (VendorDetail) SpringContext.getBean(BusinessObjectService.class).retrieve(vendorDetail);

        VendorAddress vendorAddress = new VendorAddress();
        if (StringUtils.isNotBlank(payeeAddressIdentifier)) {
            try {
                vendorAddress.setVendorAddressGeneratedIdentifier(new Integer(payeeAddressIdentifier));
                vendorAddress = (VendorAddress) SpringContext.getBean(BusinessObjectService.class).retrieve(vendorAddress);
                dvForm.setTempPayeeIdNumber(payeeIdNumber);
                dvForm.setOldPayeeType(DisbursementVoucherConstants.DV_PAYEE_TYPE_VENDOR);

            }
            catch (Exception e) {
                LOG.error("Exception while attempting to retrieve vendor address for vendor address id " + payeeAddressIdentifier + ": " + e);
            }
        }

        ((DisbursementVoucherDocument) dvForm.getDocument()).templateVendor(vendorDetail, vendorAddress);
    }

    /**
     * add warning message based on the given reason code
     */
    protected void addPaymentCodeWarningMessage(DisbursementVoucherForm dvForm, String paymentReasonCode) {
        // clear up the warning message and tab state carried from previous screen
        for (String tabKey : TabByReasonCode.getAllTabKeys()) {
            dvForm.getTabStates().remove(tabKey);
        }

        for (String propertyKey : TabByReasonCode.getAllDocumentPropertyKeys()) {
            GlobalVariables.getMessageMap().removeAllWarningMessagesForProperty(propertyKey);
        }

        String reasonCodeProperty = KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.DV_PAYEE_DETAIL + "." + KFSPropertyConstants.DISB_VCHR_PAYMENT_REASON_CODE;
        GlobalVariables.getMessageMap().removeAllWarningMessagesForProperty(reasonCodeProperty);

        // add warning message and reset tab state as open if any
        TabByReasonCode tab = TabByReasonCode.getTabByReasonCode(paymentReasonCode);
        if (tab != null) {
            dvForm.getTabStates().put(tab.tabKey, "OPEN");
            GlobalVariables.getMessageMap().putWarning(reasonCodeProperty, tab.messageKey);
            GlobalVariables.getMessageMap().putWarning(tab.getDocumentPropertyKey(), tab.messageKey);
        }
    }
    
    

}
