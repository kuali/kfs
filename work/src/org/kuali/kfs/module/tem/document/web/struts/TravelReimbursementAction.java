/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.web.struts;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.substringBetween;
import static org.kuali.kfs.module.tem.TemConstants.CERTIFICATION_STATEMENT_ATTRIBUTE;
import static org.kuali.kfs.module.tem.TemConstants.COVERSHEET_FILENAME_FORMAT;
import static org.kuali.kfs.module.tem.TemConstants.EMPLOYEE_TEST_ATTRIBUTE;
import static org.kuali.kfs.module.tem.TemConstants.FOREIGN_CURRENCY_URL_ATTRIBUTE;
import static org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
import static org.kuali.kfs.module.tem.TemConstants.REMAINING_DISTRIBUTION_ATTRIBUTE;
import static org.kuali.kfs.module.tem.TemConstants.SHOW_ACCOUNT_DISTRIBUTION_ATTRIBUTE;
import static org.kuali.kfs.module.tem.TemConstants.SHOW_ADVANCES_ATTRIBUTE;
import static org.kuali.kfs.module.tem.TemConstants.SHOW_ENCUMBRANCE_ATTRIBUTE;
import static org.kuali.kfs.module.tem.TemConstants.SHOW_REPORTS_ATTRIBUTE;
import static org.kuali.kfs.module.tem.TemConstants.TEM_PROFILE_LOOKUPABLE;
import static org.kuali.kfs.module.tem.TemConstants.TravelParameters.DOCUMENT_DTL_TYPE;
import static org.kuali.kfs.module.tem.TemConstants.TravelParameters.ENABLE_PER_DIEM_CATEGORIES;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.DISPLAY_ADVANCES_IN_REIMBURSEMENT_TOTAL_IND;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.DISPLAY_ENCUMBRANCE_IND;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.ENABLE_ACCOUNTING_DISTRIBUTION_TAB_IND;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.FOREIGN_CURRENCY_URL;
import static org.kuali.kfs.module.tem.TemPropertyConstants.TRVL_IDENTIFIER_PROPERTY;
import static org.kuali.kfs.module.tem.util.BufferedLogger.debug;
import static org.kuali.kfs.sys.KFSConstants.ReportGeneration.PDF_FILE_EXTENSION;
import static org.kuali.kfs.sys.KFSConstants.ReportGeneration.PDF_MIME_TYPE;
import static org.kuali.kfs.sys.KFSPropertyConstants.DOCUMENT_NUMBER;
import static org.kuali.rice.kns.util.KNSConstants.METHOD_TO_CALL_ATTRIBUTE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.document.TravelAuthorizationCloseDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.authorization.TravelReimbursementAuthorizer;
import org.kuali.kfs.module.tem.document.authorization.TravelReimbursementDocumentPresentationController;
import org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService;
import org.kuali.kfs.module.tem.document.service.TravelAuthorizationService;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.service.TravelReimbursementService;
import org.kuali.kfs.module.tem.document.web.bean.AccountingDistribution;
import org.kuali.kfs.module.tem.document.web.bean.TravelReimbursementMvcWrapperBean;
import org.kuali.kfs.module.tem.pdf.Coversheet;
import org.kuali.kfs.module.tem.report.ExpenseSummaryReport;
import org.kuali.kfs.module.tem.report.NonEmployeeCertificationReport;
import org.kuali.kfs.module.tem.report.SummaryByDayReport;
import org.kuali.kfs.module.tem.report.service.ExpenseSummaryReportService;
import org.kuali.kfs.module.tem.report.service.NonEmployeeCertificationReportService;
import org.kuali.kfs.module.tem.report.service.SummaryByDayReportService;
import org.kuali.kfs.module.tem.report.service.TravelReportService;
import org.kuali.kfs.module.tem.report.util.BarcodeHelper;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ReportGenerationService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;

/***
 * Action methods for the {@link TravelReimbursementDocument}
 *
 * @author Leo Przybylski (leo [at] rsmart.com)
 */
public class TravelReimbursementAction extends TravelActionBase {

    /**
     * method used for doc handler actions. Typically assumes that this is the entry point for the 
     * document when it is first created. A number of things are done hear assuming the document
     * is created at this point.
     */
    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ActionForward retval = super.docHandler(mapping, form, request, response);
        final TravelReimbursementForm reimbForm = (TravelReimbursementForm) form;
        final TravelReimbursementDocument document = reimbForm.getTravelReimbursementDocument();
        
        // Refreshes all the collections if this is not initiation
        refreshCollectionsFor(document);
        
        final String identifierStr = request.getParameter(TRVL_IDENTIFIER_PROPERTY);

        if (identifierStr != null) {
            debug("Creating reimbursement for document number ", identifierStr);
            document.setTravelDocumentIdentifier(identifierStr);
            TravelAuthorizationDocument authorization = (TravelAuthorizationDocument) getTravelDocumentService().findCurrentTravelAuthorization(document);
            
            debug("Setting traveler with id ", authorization.getTravelerDetailId());
            document.setTravelerDetailId(authorization.getTravelerDetailId());
            document.refreshReferenceObject(TemPropertyConstants.TRAVELER);
            debug("Traveler is ", document.getTraveler(), " with customer number ", document.getTraveler().getCustomerNumber());

            if (document.getTraveler().getPrincipalId() != null) {
                document.getTraveler().setPrincipalName(getPersonService().getPerson(document.getTraveler().getPrincipalId()).getPrincipalName());
            }
            
            document.setPrimaryDestinationId(authorization.getPrimaryDestinationId());
            document.setPrimaryDestination(authorization.getPrimaryDestination());
            document.setTripDescription(authorization.getTripDescription());
            document.setTripType(authorization.getTripType());
            document.setTripTypeCode(authorization.getTripTypeCode());
            document.setPrimaryDestination(authorization.getPrimaryDestination());
            document.setTripBegin(authorization.getTripBegin());
            document.setTripEnd(authorization.getTripEnd());
            document.setPrimaryDestinationName(authorization.getPrimaryDestinationName());
            document.setPrimaryDestinationCounty(authorization.getPrimaryDestinationCounty());
            document.setPrimaryDestinationCountryState(authorization.getPrimaryDestinationCountryState());
            document.setGroupTravelers(getTravelDocumentService().copyGroupTravelers(authorization.getGroupTravelers(), document.getDocumentNumber()));
            document.setDelinquentTRException(authorization.getDelinquentTRException());
            document.setMealWithoutLodgingReason(authorization.getMealWithoutLodgingReason());
            document.configureTraveler(authorization.getTemProfileId(), authorization.getTraveler());
            document.setExpenseLimit(authorization.getExpenseLimit());
            document.setPerDiemAdjustment(authorization.getPerDiemAdjustment());
            document.setTravelAdvances(authorization.getTravelAdvances());
            document.getDocumentHeader().setOrganizationDocumentNumber(authorization.getDocumentHeader().getOrganizationDocumentNumber());
            
            if (document.getPrimaryDestinationId() != null && document.getPrimaryDestinationId().intValue() == TemConstants.CUSTOM_PRIMARY_DESTINATION_ID){
                document.getPrimaryDestination().setPrimaryDestinationName(document.getPrimaryDestinationName());
                document.getPrimaryDestination().setCounty(document.getPrimaryDestinationCounty());
                document.getPrimaryDestination().setCountryState(document.getPrimaryDestinationCountryState());
                document.setPrimaryDestinationIndicator(true);
            }
                       
            //KUALITEM-404 : Copying the accounting lines from the TA to the TR upon TR creation.
            //document.setSourceAccountingLines(authorization.getSourceAccountingLines());
            //document.setTargetAccountingLines(authorization.getTargetAccountingLines());
        
            addTravelAdvancesTo(reimbForm, authorization);
            
            initializePerDiem(document, authorization);           
            
            document.setActualExpenses((List<ActualExpense>) getTravelDocumentService().copyActualExpenses(authorization.getActualExpenses(), document.getDocumentNumber()));
            
            // add new detail for the copied actualExpenses            
            if (document.getActualExpenses() != null && !document.getActualExpenses().isEmpty()) {
                for (int i = 0; i < document.getActualExpenses().size(); i++) {
                    reimbForm.getNewActualExpenseLines().add(new ActualExpense());
                }
            }           
            //initializeSpecialCircumstances(document , authorization);
        }else {
            TravelAuthorizationDocument authorization = null;
            try {
                authorization = (TravelAuthorizationDocument) getTravelDocumentService().findCurrentTravelAuthorization(document);
                addTravelAdvancesTo(reimbForm, authorization);
                if (authorization != null) {
                    document.setTravelAdvances(authorization.getTravelAdvances());
                }
            }
            catch (WorkflowException ex) {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }
        }
                 
        final List<String> perDiemCats = getParameterService().getParameterValues(PARAM_NAMESPACE, DOCUMENT_DTL_TYPE, ENABLE_PER_DIEM_CATEGORIES);
        reimbForm.parsePerDiemCategories(perDiemCats);
        
        return retval;
    }

    protected void refreshCollectionsFor(final TravelReimbursementDocument reimbursement) {
        if (!reimbursement.getDocumentHeader().getWorkflowDocument().stateIsInitiated()) {
            debug("Refreshing objects in reimbursement");
            reimbursement.refreshReferenceObject(TemPropertyConstants.PER_DIEM_EXP);
            reimbursement.refreshReferenceObject(TemPropertyConstants.TRAVELER);
            reimbursement.refreshReferenceObject(TemPropertyConstants.TRIP_TYPE);
            reimbursement.refreshReferenceObject(TemPropertyConstants.ACTUAL_EXPENSES);
            reimbursement.refreshReferenceObject(TemPropertyConstants.TRVL_ADV);
            reimbursement.refreshReferenceObject(TemPropertyConstants.PRIMARY_DESTINATION);
            reimbursement.refreshReferenceObject(TemPropertyConstants.SPECIAL_CIRCUMSTANCES);
        }
    }

    /**
     * This method sets all the boolean properties on the form to determine what buttons can be displayed depending on what is going
     * on
     */
    protected void setButtonPermissions(TravelReimbursementForm form) {
        final TravelReimbursementAuthorizer authorizer = getDocumentAuthorizer(form);
        form.setCanCertify(authorizer.canCertify(form.getTravelReimbursementDocument(), GlobalVariables.getUserSession().getPerson()));
        canSave(form);
    }
    
    protected void canSave(TravelReimbursementForm reqForm) { 
        boolean can = !(isFinal(reqForm) || isProcessed(reqForm));       
        if (can) {
            TravelReimbursementAuthorizer documentAuthorizer = getDocumentAuthorizer(reqForm);
            can = documentAuthorizer.canSave(reqForm.getTravelDocument(), GlobalVariables.getUserSession().getPerson());
        }
        
        if (!can){        
            TravelReimbursementDocument tr = (TravelReimbursementDocument) reqForm.getDocument();
    
            boolean isTravelManager = getTravelDocumentService().isTravelManager(GlobalVariables.getUserSession().getPerson());
            boolean isDelinquent = tr.getDelinquentAction() != null;
    
            if (isTravelManager && isDelinquent) {
                can = true;
            }
        }
        
        if (can) {
            reqForm.getDocumentActions().put(KNSConstants.KUALI_ACTION_CAN_SAVE,true);
        }
        else{
            reqForm.getDocumentActions().remove(KNSConstants.KUALI_ACTION_CAN_SAVE);
        }        
    }    

    public ActionForward printCoversheet(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final TravelReimbursementForm reimbForm = (TravelReimbursementForm) form;
        final String documentNumber = request.getParameter(DOCUMENT_NUMBER);
        if(documentNumber != null && !documentNumber.isEmpty()) {           
            reimbForm.setDocument(getTravelReimbursementService().find(documentNumber));
        }
        final TravelReimbursementDocument reimbursement = reimbForm.getTravelReimbursementDocument();
        
        final Coversheet cover = getTravelReimbursementService().generateCoversheetFor(reimbursement);
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        cover.print(stream);
        
        WebUtils.saveMimeOutputStreamAsFile(response, "application/pdf", stream, String.format(COVERSHEET_FILENAME_FORMAT, reimbursement.getDocumentNumber()));
        
        return null;        
    }

    /**
     * Action method for creating a {@link ExpenseSummaryReport} and producing a PDF from it
     */
    public ActionForward viewExpenseSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final TravelReimbursementForm reimbForm = (TravelReimbursementForm) form;
        reimbForm.setDocument(getTravelReimbursementService().find(request.getParameter(DOCUMENT_NUMBER)));
        final TravelReimbursementDocument reimbursement = reimbForm.getTravelReimbursementDocument();
        final ExpenseSummaryReport report = getExpenseSummaryReportService().buildReport(reimbursement);

        final ByteArrayOutputStream baos = getTravelReportService().buildReport(report);
        WebUtils.saveMimeOutputStreamAsFile(response, PDF_MIME_TYPE, baos, "ExpenseSummary" + PDF_FILE_EXTENSION);
        return null;
    }

    /**
     * Action method for creating a {@link SummaryByDayReport} and producing a PDF from it.
     */
    public ActionForward viewSummaryByDay(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final TravelReimbursementForm reimbForm = (TravelReimbursementForm) form;
        reimbForm.setDocument(getTravelReimbursementService().find(request.getParameter(DOCUMENT_NUMBER)));
        final TravelReimbursementDocument reimbursement = reimbForm.getTravelReimbursementDocument();
        final SummaryByDayReport report = getSummaryByDayReportService().buildReport(reimbursement);
        
        final ByteArrayOutputStream baos = getTravelReportService().buildReport(report);
        WebUtils.saveMimeOutputStreamAsFile(response, PDF_MIME_TYPE, baos, "SummaryByDay" + PDF_FILE_EXTENSION);
        return null;
    }
    
    /**
     * Action method for creating a {@link NonEmployeeCertificationReport} and producing a PDF from it.
     */
    public ActionForward viewNonEmployeeForms(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final TravelReimbursementForm reimbForm = (TravelReimbursementForm) form;
        reimbForm.setDocument(getTravelReimbursementService().find(request.getParameter(DOCUMENT_NUMBER)));
        final TravelReimbursementDocument reimbursement = reimbForm.getTravelReimbursementDocument();
        final NonEmployeeCertificationReport report = getNonEmployeeCertificationReportService().buildReport(reimbursement); 
        BarcodeHelper barcode = new BarcodeHelper();
        report.setBarcodeImage(barcode.generateBarcodeImage(reimbursement.getDocumentNumber()));
        File reportFile = getNonEmployeeCertificationReportService().generateReport(report);
        
        StringBuilder fileName = new StringBuilder();
        fileName.append(reimbForm.getDocument().getDocumentNumber());
        fileName.append(KFSConstants.ReportGeneration.PDF_FILE_EXTENSION);
        if (reportFile.length() == 0) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        
        displayPDF(request, response, reportFile, fileName);

        return null;
        
    }

    /**
     * Adds travel advances from a {@link TravelAuthorizationDocument} instance to the {@link TravelReimbursementForm} for
     * viewing.
     *
     * @param form to add travel advances to
     * @param authorization {@link TravelAuthorization} instance to get travel advances from
     */
    protected void addTravelAdvancesTo(final TravelReimbursementForm form, final TravelAuthorizationDocument authorization) {
        if (authorization != null) {
            form.getInvoices().addAll(authorization.getTravelAdvances());
        }
    }

    protected void initializePerDiem(final TravelReimbursementDocument reimbursement, final TravelAuthorizationDocument authorization) {
        for (final PerDiemExpense estimate : authorization.getPerDiemExpenses()) {
            final PerDiemExpense mileage = getTravelDocumentService().copyPerDiemExpense(estimate);
            mileage.setDocumentNumber(reimbursement.getDocumentNumber());

            if (mileage.getMileageRateId() != null) {
                debug("Adding mileage for estimate with date ", estimate.getMileageDate());
                reimbursement.getPerDiemExpenses().add(mileage);
            }
        }
    }
    
//    /**
//     * Adds special circumstances answers from a {@link TravelAuthorizationDocument} instance to the {@link TravelReimbursementDocument} for
//     * viewing.
//     *
//     * @param reimbursement to add special circumstances answers
//     * @param authorization {@link TravelAuthorization} instance to get travel advances from
//     */
//    protected void initializeSpecialCircumstances(final TravelReimbursementDocument reimbursement, final TravelAuthorizationDocument authorization) {
//        for (SpecialCircumstances authSpCircumstances : authorization.getSpecialCircumstances()) {
//            Long authQuestionId = authSpCircumstances.getQuestionId(); 
//            
//            for(SpecialCircumstances reimSpCircumstances : reimbursement.getSpecialCircumstances()) {
//                Long reimQuestionId =  reimSpCircumstances.getQuestionId();
//                
//                if(reimQuestionId != null && reimQuestionId.equals(authQuestionId)){
//                    reimSpCircumstances.setText(authSpCircumstances.getText());
//                }
//            }            
//        }
//    }
    
    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TravelReimbursementForm reimbForm = (TravelReimbursementForm) form;

        ActionForward actionAfterPrimaryDestinationLookup = refreshAfterPrimaryDestinationLookup(mapping, reimbForm, request);
        if (actionAfterPrimaryDestinationLookup != null) {
            return actionAfterPrimaryDestinationLookup;
        }

        refreshAfterTravelerLookup(mapping, reimbForm, request);

        return super.refresh(mapping, form, request, response);
    }
    
    /**
     * 
     * This method is used to refresh the information on the form
     * @param mapping
     * @param reqForm
     * @param request
     * @return
     */
    protected ActionForward refreshAfterTravelerLookup(ActionMapping mapping, TravelReimbursementForm reqForm, HttpServletRequest request) {
        String refreshCaller = reqForm.getRefreshCaller();

        TravelReimbursementDocument document = reqForm.getTravelReimbursementDocument();

        // if a cancel occurred on address lookup we need to reset the payee id and type, rest of fields will still have correct
        // information
        debug("Got refresh caller ", refreshCaller);
        if (refreshCaller == null) {
            return null;
        }

        boolean isTravelerLookupable = TEM_PROFILE_LOOKUPABLE.equals(refreshCaller);
        if (!isTravelerLookupable) {
            return null;
        }

        final String travelerTypeCode = request.getParameter("document.traveler.travelerTypeCode");
        if (StringUtils.isNotEmpty(travelerTypeCode)) {
            document.getTraveler().setTravelerTypeCode(travelerTypeCode);
        }
        document.getTraveler().refreshReferenceObject(TemPropertyConstants.TRAVELER_TYPE);
        return null;
    }

    protected Integer getPerDiemActionLineNumber(final HttpServletRequest request) {
        for (final String parameterKey : ((Map<String,String>) request.getParameterMap()).keySet()) {
            if (StringUtils.containsIgnoreCase(parameterKey, TemPropertyConstants.PER_DIEM_EXP)) {
                return getLineNumberFromParameter(parameterKey);
            }
        }

        return -1;
    }

    protected PerDiemExpense getPerDiemActionLine(final HttpServletRequest request, final TravelReimbursementDocument reimbursement) {
        final int lineNum = getPerDiemActionLineNumber(request);
        if (lineNum < 0) {
            return null;
        }
        return reimbursement.getPerDiemExpenses().get(lineNum);
    }
    
    /**
     * Uses the {@link TravelAuthorizationService} to lookup a {@link TravelAuthorizationDocument} instance via
     * its <code>travelDocumentIdentifier</code>
     * 
     * @param travelDocumentIdentifier to location a {@link TravelAuthorizationDocument} with
     * @return {@link TravelAuthorizationDocument} instance
     */
    protected TravelAuthorizationDocument getTravelAuthorization(final String travelDocumentIdentifier) {
        Collection<TravelAuthorizationDocument> taList = getTravelAuthorizationService().find(travelDocumentIdentifier);
        if (ObjectUtils.isNotNull(taList) && taList.iterator().hasNext()) {
            return taList.iterator().next();
        }
        return null;
    }

    /**
     * Action method for adding an {@link OtherExpenseDetail} instance to the {@link OtherExpenseLine}
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward addOtherExpenseDetailLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final TravelReimbursementForm reimbForm = (TravelReimbursementForm) form;
        final TravelReimbursementMvcWrapperBean mvcWrapper = newMvcDelegate(form);
        reimbForm.getObservable().notifyObservers(new Object[] { mvcWrapper, getSelectedLine(request) });

        KualiDecimal totalRemaining = KualiDecimal.ZERO;
        for (final AccountingDistribution dist : reimbForm.getDistribution()) {
            totalRemaining = totalRemaining.add(dist.getRemainingAmount());                
        }
        
        request.setAttribute(REMAINING_DISTRIBUTION_ATTRIBUTE, totalRemaining);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * This method removes an other travel expense detail from this collection
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return the page to forward back to
     * @throws Exception
     */
    public ActionForward deleteOtherExpenseDetailLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final TravelReimbursementForm reimbForm = (TravelReimbursementForm) form;
        final TravelReimbursementDocument document = (TravelReimbursementDocument) reimbForm.getDocument();
        final TravelReimbursementMvcWrapperBean mvcWrapper = newMvcDelegate(form);
        reimbForm.getObservable().notifyObservers(new Object[] { mvcWrapper,
                                                                 getSelectedOtherExpenseIndex(request, document),
                                                                 getSelectedLine(request) });
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    @Override
    protected Class getMvcWrapperInterface() {
        return TravelReimbursementMvcWrapperBean.class;
    }

    /**
     * Do initialization for a new {@link TravelReimbursementDocument}
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        final TravelReimbursementForm travelForm = (TravelReimbursementForm) kualiDocumentFormBase;
        final TravelReimbursementDocument document = (TravelReimbursementDocument) travelForm.getDocument();
        getTravelReimbursementService().addListenersTo(document);
        document.addContactInformation();
    }

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ActionForward retval                 = super.execute(mapping, form, request, response);
        final TravelReimbursementForm reimbForm    = (TravelReimbursementForm) form;
        final TravelReimbursementDocument document = ((TravelReimbursementForm) form).getTravelReimbursementDocument();
        final String travelIdentifier = document.getTravelDocumentIdentifier();
        
        document.refreshReferenceObject(TemPropertyConstants.TRIP_TYPE);
        setButtonPermissions(reimbForm);
        setContactMasking(reimbForm);
        debug("Found ", document.getActualExpenses().size(), " other expenses");

        if (reimbForm.getHistory() == null) {
            debug("Looking up history for TEM document number ", travelIdentifier);
            final List<Serializable> history = new ArrayList<Serializable>();
            final Collection<TravelReimbursementDocument> docs = getTravelReimbursementService().findByTravelId(travelIdentifier);
            debug("Got history of size ", docs.size());
            for (final TravelReimbursementDocument found : docs) {
                debug("Creating history object for document ", found);
                debug("Using header ", found.getDocumentHeader());
                history.add(new HistoryValueObject(found));
            }
            reimbForm.setHistory(history);
        }
        
        //Set request variable that will determine whether to show the "Final Reimbursement" checkbox.
        //If TAC exists, no need to create another.
        TravelAuthorizationDocument authorization = (TravelAuthorizationDocument) getTravelDocumentService().findCurrentTravelAuthorization(document);
        if (authorization instanceof TravelAuthorizationCloseDocument){
            request.setAttribute("isClose", true);
        }

        for (ActualExpense actualExpense : document.getActualExpenses()){
            getTravelReimbursementService().disableDuplicateExpenses(document, actualExpense);
        }
        
        //Display any messages
        Iterator<String> it = document.getDisabledProperties().keySet().iterator();
        while (it.hasNext()){
            String key = it.next();
            GlobalVariables.getMessageMap().putInfo(key, TemKeyConstants.MESSAGE_GENERIC,document.getDisabledProperties().get(key));
        }
        
        if(ObjectUtils.isNotNull(document.getActualExpenses())){
            document.enableExpenseTypeSpecificFields(document.getActualExpenses());
        }
        
        refreshRelatedDocuments(reimbForm);
        
        if (!reimbForm.getMethodToCall().equalsIgnoreCase("dochandler")) {            
            if (document.getTripType() != null) {
                
                // Setting up distribution                          
                KualiDecimal totalRemaining = KualiDecimal.ZERO;
                for (final AccountingDistribution dist : reimbForm.getDistribution()) {
                    totalRemaining = totalRemaining.add(dist.getRemainingAmount());                
                }
                
                request.setAttribute(REMAINING_DISTRIBUTION_ATTRIBUTE, totalRemaining);
            }
        } 

        final String currencyUrl = getParameterService().getParameterValue(PARAM_NAMESPACE, TravelReimbursementParameters.PARAM_DTL_TYPE, FOREIGN_CURRENCY_URL);
        request.setAttribute(FOREIGN_CURRENCY_URL_ATTRIBUTE, currencyUrl);
        
        showAccountDistribution(request, document);

        request.setAttribute(SHOW_REPORTS_ATTRIBUTE, !document.getDocumentHeader().getWorkflowDocument().stateIsInitiated());

        request.setAttribute(CERTIFICATION_STATEMENT_ATTRIBUTE, getCertificationStatement(document));
        request.setAttribute(EMPLOYEE_TEST_ATTRIBUTE, isEmployee(document.getTraveler()));
        request.setAttribute(TemConstants.DELINQUENT_TEST_ATTRIBUTE, document.getDelinquentAction());
        debug("Found ", document.getActualExpenses().size(), " other expenses");
                
        final boolean showAdvances = getParameterService().getIndicatorParameter(PARAM_NAMESPACE, TravelReimbursementParameters.PARAM_DTL_TYPE, DISPLAY_ADVANCES_IN_REIMBURSEMENT_TOTAL_IND);
        request.setAttribute(SHOW_ADVANCES_ATTRIBUTE, showAdvances);
        
        final boolean showEncumbrance = getParameterService().getIndicatorParameter(PARAM_NAMESPACE, TravelReimbursementParameters.PARAM_DTL_TYPE, DISPLAY_ENCUMBRANCE_IND);
        request.setAttribute(SHOW_ENCUMBRANCE_ATTRIBUTE, showEncumbrance);        
        
        if(!getCalculateIgnoreList().contains(reimbForm.getMethodToCall())){
            recalculateTripDetailTotalOnly(mapping, form, request, response);
        }        
        
        getTravelDocumentService().showNoTravelAuthorizationError(document);
        return retval;
    }

    protected void setContactMasking(TravelReimbursementForm trForm) {
        TravelReimbursementDocument document = trForm.getTravelReimbursementDocument();
        TravelReimbursementDocumentPresentationController documentPresentationController = (TravelReimbursementDocumentPresentationController) getDocumentHelperService().getDocumentPresentationController(document);
        
        trForm.setCanUnmask(trForm.isUserDocumentInitiator() || documentPresentationController.enableForTravelManager(document.getDocumentHeader().getWorkflowDocument()));
    }

    /**
     * The action called when the "Remove Per Diem Table" buttons are clicked upon. This method will clear out the per diem objects
     * from the {@link TravelAuthorizationDocument} instance
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return {@link ActionForward}
     */
    public ActionForward clearPerDiem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final TravelReimbursementDocument reimbursement = ((TravelReimbursementForm) form).getTravelReimbursementDocument();
        reimbursement.setPerDiemExpenses(new ArrayList<PerDiemExpense>());
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    @Override
    public ActionForward clearPerDiemExpenses(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TravelFormBase reqForm = (TravelFormBase) form;
        TravelDocument document = reqForm.getTravelDocument();
        document.setPerDiemExpenses(new ArrayList<PerDiemExpense>());
        getTravelReimbursementService().enableDuplicateExpenses((TravelReimbursementDocument) document, null);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * Parses the method to call attribute to pick off the line number which should have an action performed on it.
     * 
     * @param request
     * @param document the other expense is selected on
     * @return OtherExpense
     */
    protected ActualExpense getSelectedOtherExpense(final HttpServletRequest request, final TravelReimbursementDocument document) {
        ActualExpense retval = null;
        final String parameterName = (String) request.getAttribute(METHOD_TO_CALL_ATTRIBUTE);
        if (isNotBlank(parameterName)) {
            final int lineNumber = Integer.parseInt(substringBetween(parameterName, TemPropertyConstants.ACTUAL_EXPENSES+"[", "]."));
            retval = document.getActualExpenses().get(lineNumber);
        }

        return retval;
    }

    /**
     * This is a utility method used to prepare to and to return to a previous page, making sure that the buttons will be restored
     * in the process.
     * 
     * @param kualiDocumentFormBase The Form, considered as a KualiDocumentFormBase, as it typically is here.
     * @return An actionForward mapping back to the original page.
     */
    protected ActionForward returnToPreviousPage(ActionMapping mapping, KualiDocumentFormBase kualiDocumentFormBase) {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * Recalculates the Expenses Total Tab
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @throws Exception
     */
    public ActionForward recalculate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {        
        return recalculateTripDetailTotal(mapping, form, request, response);
    }

    @Override
    public ActionForward approve(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {        
        getTravelDocumentService().showNoTravelAuthorizationError(((TravelReimbursementForm) form).getTravelReimbursementDocument());
        
        return super.approve(mapping, form, request, response);
    }
    
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        addTATRRelationship(form);              

        return super.save(mapping, form, request, response);
    }

    private void addTATRRelationship(ActionForm form) throws WorkflowException {
        TravelReimbursementForm reqForm = (TravelReimbursementForm) form;
        TravelReimbursementDocument trDoc = reqForm.getTravelReimbursementDocument();
        String docId = trDoc.getTravelDocumentIdentifier();
        if (ObjectUtils.isNotNull(docId)) {
            TravelAuthorizationDocument taDoc = (TravelAuthorizationDocument) getTravelDocumentService().findCurrentTravelAuthorization(trDoc);
            if (ObjectUtils.isNotNull(taDoc)) {
                // add relationship
                String relationDescription = "TA - TR";
                SpringContext.getBean(AccountingDocumentRelationshipService.class).save(new AccountingDocumentRelationship(taDoc.getDocumentNumber(), trDoc.getDocumentNumber(), relationDescription));          
            }
        }
    }    
        
    /**
     * Parses the method to call attribute to pick off the line number which should have an action performed on it.
     * 
     * @param request
     * @param document the other expense is selected on
     * @return index of an OtherExpense
     */
    protected int getSelectedOtherExpenseIndex(final HttpServletRequest request, final TravelReimbursementDocument document) {
        final String parameterName = (String) request.getAttribute(METHOD_TO_CALL_ATTRIBUTE);
        debug("Getting selected other expense index from ", parameterName);
        if (isNotBlank(parameterName)) {
            return Integer.parseInt(substringBetween(parameterName, TemPropertyConstants.ACTUAL_EXPENSES+"[", "]."));
        }
        return -1;
    }    
    
    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#route(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {      
        getTravelDocumentService().showNoTravelAuthorizationError(((TravelReimbursementForm) form).getTravelReimbursementDocument());        

        final boolean showAccountDistribution = getParameterService().getIndicatorParameter(PARAM_NAMESPACE, TravelReimbursementParameters.PARAM_DTL_TYPE, ENABLE_ACCOUNTING_DISTRIBUTION_TAB_IND);
        request.setAttribute(SHOW_ACCOUNT_DISTRIBUTION_ATTRIBUTE, showAccountDistribution);
        
        ActionForward forward = super.route(mapping, form, request, response);
        
        addDateChangedNote(form);

        addTATRRelationship(form);
        
        return forward;
    }
    
    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#blanketApprove(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward blanketApprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {        
        getTravelDocumentService().showNoTravelAuthorizationError(((TravelReimbursementForm) form).getTravelReimbursementDocument());
        
        ActionForward forward = super.blanketApprove(mapping, form, request, response);
        
        addDateChangedNote(form);
        
        addTATRRelationship(form);

        return forward;
    }
    
    /**
     * This method calls addDateChangedNote() if this TR is created from a TA.
     * 
     * @see org.kuali.kfs.module.tem.document.service.TravelReimbursementService#addDateChangedNote(org.kuali.kfs.module.tem.document.TravelReimbursementDocument, 
     * org.kuali.kfs.module.tem.document.TravelAuthorizationDocument)
     * 
     * @param form
     * @throws Exception
     */
    protected void addDateChangedNote(ActionForm form) throws Exception {

        TravelReimbursementForm reqForm = (TravelReimbursementForm) form;
        TravelReimbursementDocument travelReqDoc = reqForm.getTravelReimbursementDocument();
        String docId = travelReqDoc.getTravelDocumentIdentifier();
        if (ObjectUtils.isNotNull(docId)) {
            TravelAuthorizationDocument taDoc = (TravelAuthorizationDocument) getTravelDocumentService().findCurrentTravelAuthorization(travelReqDoc);
            if (ObjectUtils.isNotNull(taDoc)) {
                getTravelReimbursementService().addDateChangedNote(travelReqDoc, taDoc);
            }
        }
    }

    @Override
    protected TravelDocumentService getTravelDocumentService() {
        return SpringContext.getBean(TravelDocumentService.class);
    }

    protected TravelReimbursementService getTravelReimbursementService() {
        return SpringContext.getBean(TravelReimbursementService.class);
    }

    public PersonService getPersonService() {
        return SpringContext.getBean(PersonService.class);
    }

    protected TravelAuthorizationService getTravelAuthorizationService() {
        return SpringContext.getBean(TravelAuthorizationService.class);
    }

    protected KualiRuleService getRulesService() {
        return SpringContext.getBean(KualiRuleService.class);
    }

    protected ReportGenerationService getReportGenerationService() {
        return SpringContext.getBean(ReportGenerationService.class);
    }

    protected TravelReportService getTravelReportService() {
        return SpringContext.getBean(TravelReportService.class);
    }

    protected ExpenseSummaryReportService getExpenseSummaryReportService() {
        return SpringContext.getBean(ExpenseSummaryReportService.class);
    }
    protected SummaryByDayReportService getSummaryByDayReportService() {
        return SpringContext.getBean(SummaryByDayReportService.class);
    }
    protected NonEmployeeCertificationReportService getNonEmployeeCertificationReportService() {
        return SpringContext.getBean(NonEmployeeCertificationReportService.class);
    }
}


