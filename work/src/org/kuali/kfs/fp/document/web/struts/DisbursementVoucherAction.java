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

import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.user.KualiGroup;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.util.WebUtils;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.module.financial.bo.DisbursementVoucherNonEmployeeExpense;
import org.kuali.module.financial.bo.DisbursementVoucherPreConferenceRegistrant;
import org.kuali.module.financial.bo.Payee;
import org.kuali.module.financial.bo.WireCharge;
import org.kuali.module.financial.document.DisbursementVoucherDocument;
import org.kuali.module.financial.service.DisbursementVoucherCoverSheetService;
import org.kuali.module.financial.service.DisbursementVoucherTaxService;
import org.kuali.module.financial.service.impl.DisbursementVoucherCoverSheetServiceImpl;
import org.kuali.module.financial.web.struts.form.DisbursementVoucherForm;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class handles Actions for the DisbursementVoucher.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class DisbursementVoucherAction extends KualiTransactionalDocumentActionBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherAction.class);

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
        if (Constants.KUALI_LOOKUPABLE_IMPL.equals(dvForm.getRefreshCaller()) && request.getParameter("document.dvPayeeDetail.disbVchrPayeeIdNumber") != null && document.getDvPayeeDetail().isPayee()) {
            String payeeIdNumber = ((DisbursementVoucherDocument) dvForm.getDocument()).getDvPayeeDetail().getDisbVchrPayeeIdNumber();
            Payee refreshPayee = new Payee();
            refreshPayee.setPayeeIdNumber(payeeIdNumber);
            refreshPayee = (Payee) SpringServiceLocator.getBusinessObjectService().retrieve(refreshPayee);
            ((DisbursementVoucherDocument) dvForm.getDocument()).templatePayee(refreshPayee);
        }
        /* refresh from employee lookup */
        else if (Constants.KUALI_LOOKUPABLE_IMPL.equals(dvForm.getRefreshCaller()) && request.getParameter("document.dvPayeeDetail.disbVchrPayeeIdNumber") != null && document.getDvPayeeDetail().isEmployee()) {
            String emplUuid = ((DisbursementVoucherDocument) dvForm.getDocument()).getDvPayeeDetail().getDisbVchrPayeeIdNumber();
            UniversalUser employee = new UniversalUser();
            employee.setPersonUniversalIdentifier(emplUuid);
            employee = (UniversalUser) SpringServiceLocator.getBusinessObjectService().retrieve(employee);
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
        String directory = getServlet().getServletConfig().getServletContext().getRealPath(DisbursementVoucherCoverSheetServiceImpl.DV_COVERSHEET_TEMPLATE_RELATIVE_DIR);

        DisbursementVoucherDocument document = (DisbursementVoucherDocument) SpringServiceLocator.getDocumentService().getByDocumentHeaderId(request.getParameter(PropertyConstants.FINANCIAL_DOCUMENT_NUMBER));

        // set worflow document back into form to prevent document authorizer "invalid (null)
        // document.documentHeader.workflowDocument" since we are bypassing form submit and just linking directly to the action
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        dvForm.getDocument().getDocumentHeader().setWorkflowDocument(document.getDocumentHeader().getWorkflowDocument());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DisbursementVoucherCoverSheetService coverSheetService = SpringServiceLocator.getDisbursementVoucherCoverSheetService();

        coverSheetService.generateDisbursementVoucherCoverSheet(directory, DisbursementVoucherCoverSheetServiceImpl.DV_COVERSHEET_TEMPLATE_NM, document, baos);
        String fileName = document.getFinancialDocumentNumber() + "_cover_sheet.pdf";
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
            KualiDecimal perDiemAmount = SpringServiceLocator.getDisbursementVoucherTravelService().calculatePerDiemAmount(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDisbVchrPerdiemRate());

            dvDocument.getDvNonEmployeeTravel().setDisbVchrPerdiemCalculatedAmt(perDiemAmount);
            dvDocument.getDvNonEmployeeTravel().setDisbVchrPerdiemActualAmount(perDiemAmount);
        }
        catch (RuntimeException e) {
            LOG.error("Error in calculating travel per diem: " + e.getMessage());
            GlobalVariables.getErrorMap().putError("DVNonEmployeeTravelErrors", KeyConstants.ERROR_CUSTOM, e.getMessage());
        }

        return mapping.findForward(Constants.MAPPING_BASIC);

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
            GlobalVariables.getErrorMap().putError(Constants.GENERAL_NONEMPLOYEE_TAB_ERRORS, KeyConstants.ERROR_REQUIRED, "Total Mileage");
        }

        if (dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp() == null) {
            LOG.error("Travel Start Date must be given");
            GlobalVariables.getErrorMap().putError(Constants.GENERAL_NONEMPLOYEE_TAB_ERRORS, KeyConstants.ERROR_REQUIRED, "Travel Start Date");
        }

        if (GlobalVariables.getErrorMap().isEmpty()) {
            // call service to calculate mileage amount
            KualiDecimal mileageAmount = SpringServiceLocator.getDisbursementVoucherTravelService().calculateMileageAmount(dvDocument.getDvNonEmployeeTravel().getDvPersonalCarMileageAmount(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp());

            dvDocument.getDvNonEmployeeTravel().setDisbVchrMileageCalculatedAmt(mileageAmount);
            dvDocument.getDvNonEmployeeTravel().setDisbVchrPersonalCarAmount(mileageAmount);
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
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
        GlobalVariables.getErrorMap().addToErrorPath(PropertyConstants.NEW_NONEMPLOYEE_EXPENSE_LINE);
        SpringServiceLocator.getDictionaryValidationService().validateBusinessObject(newExpenseLine);
        GlobalVariables.getErrorMap().removeFromErrorPath(PropertyConstants.NEW_NONEMPLOYEE_EXPENSE_LINE);

        if (GlobalVariables.getErrorMap().isEmpty()) {
            dvDocument.getDvNonEmployeeTravel().addDvNonEmployeeExpenseLine(newExpenseLine);
            dvForm.setNewNonEmployeeExpenseLine(new DisbursementVoucherNonEmployeeExpense());
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
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
        GlobalVariables.getErrorMap().addToErrorPath(PropertyConstants.NEW_PREPAID_EXPENSE_LINE);
        SpringServiceLocator.getDictionaryValidationService().validateBusinessObject(newExpenseLine);
        GlobalVariables.getErrorMap().removeFromErrorPath(PropertyConstants.NEW_PREPAID_EXPENSE_LINE);

        if (GlobalVariables.getErrorMap().isEmpty()) {
            dvDocument.getDvNonEmployeeTravel().addDvPrePaidEmployeeExpenseLine(newExpenseLine);
            dvForm.setNewPrePaidNonEmployeeExpenseLine(new DisbursementVoucherNonEmployeeExpense());
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
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

        return mapping.findForward(Constants.MAPPING_BASIC);
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

        return mapping.findForward(Constants.MAPPING_BASIC);
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
        GlobalVariables.getErrorMap().addToErrorPath(PropertyConstants.NEW_PRECONF_REGISTRANT_LINE);
        SpringServiceLocator.getDictionaryValidationService().validateBusinessObject(newRegistrantLine);
        GlobalVariables.getErrorMap().removeFromErrorPath(PropertyConstants.NEW_PRECONF_REGISTRANT_LINE);

        if (GlobalVariables.getErrorMap().isEmpty()) {
            dvDocument.addDvPrePaidRegistrantLine(newRegistrantLine);
            dvForm.setNewPreConferenceRegistrantLine(new DisbursementVoucherPreConferenceRegistrant());
        }

        return mapping.findForward(Constants.MAPPING_BASIC);

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

        return mapping.findForward(Constants.MAPPING_BASIC);

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
        if (!GlobalVariables.getUserSession().getKualiUser().isMember(new KualiGroup(KualiGroup.KUALI_DV_TAX_GROUP))) {
            LOG.info("User requested generateNonResidentAlienTaxLines who is not in the kuali tax group.");
            GlobalVariables.getErrorMap().putError(Constants.DV_NRATAX_TAB_ERRORS, KeyConstants.ERROR_DV_NRA_PERMISSIONS_GENERATE);
            return mapping.findForward(Constants.MAPPING_BASIC);
        }

        DisbursementVoucherTaxService taxService = SpringServiceLocator.getDisbursementVoucherTaxService();

        /* call service to generate new tax lines */
        GlobalVariables.getErrorMap().addToErrorPath("document");
        taxService.processNonResidentAlienTax(document);
        GlobalVariables.getErrorMap().removeFromErrorPath("document");

        return mapping.findForward(Constants.MAPPING_BASIC);
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
        if (!GlobalVariables.getUserSession().getKualiUser().isMember(new KualiGroup(KualiGroup.KUALI_DV_TAX_GROUP))) {
            LOG.info("User requested generateNonResidentAlienTaxLines who is not in the kuali tax group.");
            GlobalVariables.getErrorMap().putError(Constants.DV_NRATAX_TAB_ERRORS, KeyConstants.ERROR_DV_NRA_PERMISSIONS_GENERATE);
            return mapping.findForward(Constants.MAPPING_BASIC);
        }

        DisbursementVoucherTaxService taxService = SpringServiceLocator.getDisbursementVoucherTaxService();

        /* call service to clear previous lines */
        taxService.clearNRATaxLines(document);

        return mapping.findForward(Constants.MAPPING_BASIC);
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

        String fullParameter = (String) request.getAttribute(Constants.METHOD_TO_CALL_ATTRIBUTE);
        String boClassName = StringUtils.substringBetween(fullParameter, Constants.METHOD_TO_CALL_BOPARM_LEFT_DEL, Constants.METHOD_TO_CALL_BOPARM_RIGHT_DEL);

        if ("org.kuali.module.financial.bo.Payee".equals(boClassName) && document.getDvPayeeDetail().isEmployee()) {
            String conversionFields = StringUtils.substringBetween(fullParameter, Constants.METHOD_TO_CALL_PARM1_LEFT_DEL, Constants.METHOD_TO_CALL_PARM1_RIGHT_DEL);

            fullParameter = StringUtils.replace(fullParameter, boClassName, "org.kuali.core.bo.user.UniversalUser");
            fullParameter = StringUtils.replace(fullParameter, conversionFields, "personUniversalIdentifier:document.dvPayeeDetail.disbVchrPayeeIdNumber");
            request.setAttribute(Constants.METHOD_TO_CALL_ATTRIBUTE, fullParameter);
        }

        return super.performLookup(mapping, form, request, response);
    }

    /*
     * Builds the wire charge message for the current fiscal year.
     */
    private String retrieveWireChargeMessage() {
        String message = SpringServiceLocator.getKualiConfigurationService().getPropertyString(KeyConstants.MESSAGE_DV_WIRE_CHARGE);
        WireCharge wireCharge = new WireCharge();
        wireCharge.setUniversityFiscalYear(SpringServiceLocator.getDateTimeService().getCurrentFiscalYear());

        wireCharge = (WireCharge) SpringServiceLocator.getBusinessObjectService().retrieve(wireCharge);
        Object[] args = { wireCharge.getDomesticChargeAmt(), wireCharge.getForeignChargeAmt() };

        return MessageFormat.format(message, args);
    }

}