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
import static org.kuali.kfs.module.tem.TemConstants.REMAINING_DISTRIBUTION_ATTRIBUTE;
import static org.kuali.kfs.module.tem.TemConstants.SHOW_ACCOUNT_DISTRIBUTION_ATTRIBUTE;
import static org.kuali.kfs.module.tem.TemConstants.SHOW_ADVANCES_ATTRIBUTE;
import static org.kuali.kfs.module.tem.TemConstants.SHOW_ENCUMBRANCE_ATTRIBUTE;
import static org.kuali.kfs.module.tem.TemConstants.SHOW_REPORTS_ATTRIBUTE;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.DISPLAY_ACCOUNTING_DISTRIBUTION_TAB_IND;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.DISPLAY_ADVANCES_IN_REIMBURSEMENT_TOTAL_IND;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.DISPLAY_ENCUMBRANCE_IND;
import static org.kuali.kfs.sys.KFSConstants.ReportGeneration.PDF_FILE_EXTENSION;
import static org.kuali.kfs.sys.KFSConstants.ReportGeneration.PDF_MIME_TYPE;
import static org.kuali.kfs.sys.KFSPropertyConstants.DOCUMENT_NUMBER;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.AccountingDistribution;
import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.SpecialCircumstances;
import org.kuali.kfs.module.tem.document.TravelAuthorizationCloseDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelDocumentBase;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.authorization.TravelReimbursementAuthorizer;
import org.kuali.kfs.module.tem.document.service.TravelAuthorizationService;
import org.kuali.kfs.module.tem.document.service.TravelReimbursementService;
import org.kuali.kfs.module.tem.document.web.bean.TravelReimbursementMvcWrapperBean;
import org.kuali.kfs.module.tem.pdf.Coversheet;
import org.kuali.kfs.module.tem.report.ExpenseSummaryReport;
import org.kuali.kfs.module.tem.report.NonEmployeeCertificationReport;
import org.kuali.kfs.module.tem.report.SummaryByDayReport;
import org.kuali.kfs.module.tem.report.service.ExpenseSummaryReportService;
import org.kuali.kfs.module.tem.report.service.NonEmployeeCertificationReportService;
import org.kuali.kfs.module.tem.report.service.SummaryByDayReportService;
import org.kuali.kfs.module.tem.report.util.BarcodeHelper;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/***
 * Action methods for the {@link TravelReimbursementDocument}
 *
 */
public class TravelReimbursementAction extends TravelActionBase {

    public static Logger LOG = Logger.getLogger(TravelReimbursementAction.class);

    /**
     * Refreshes all collections upon load
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#loadDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);
        final TravelReimbursementForm reimbForm = (TravelReimbursementForm) kualiDocumentFormBase;
        final TravelReimbursementDocument document = reimbForm.getTravelReimbursementDocument();

        refreshCollectionsFor(document);
        initializeAssignAccounts(reimbForm);
        reimbForm.setDistribution(getAccountingDistributionService().buildDistributionFrom(document));
    }


    protected void refreshCollectionsFor(final TravelReimbursementDocument reimbursement) {
        if (!reimbursement.getDocumentHeader().getWorkflowDocument().isInitiated()) {
            LOG.debug("Refreshing objects in reimbursement");
            reimbursement.refreshReferenceObject(TemPropertyConstants.PER_DIEM_EXPENSES);
            reimbursement.refreshReferenceObject(TemPropertyConstants.TRAVELER);
            reimbursement.refreshReferenceObject(TemPropertyConstants.TRIP_TYPE);
            reimbursement.refreshReferenceObject(TemPropertyConstants.ACTUAL_EXPENSES);
            reimbursement.refreshReferenceObject(TemPropertyConstants.PRIMARY_DESTINATION);
            reimbursement.refreshReferenceObject(TemPropertyConstants.SPECIAL_CIRCUMSTANCES);
        }
    }

    /**
     * This method sets all the boolean properties on the form to determine what buttons can be displayed depending on what is going
     * on
     */
    protected void setButtonPermissions(TravelReimbursementForm form) {
        canSave(form);

        setCanCertify(form);
        setCanCalculate(form);
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
            reqForm.getDocumentActions().put(KRADConstants.KUALI_ACTION_CAN_SAVE,true);
        }
        else{
            reqForm.getDocumentActions().remove(KRADConstants.KUALI_ACTION_CAN_SAVE);
        }
    }

    /**
     * Determines whether or not someone can certify a travel document
     *
     * @param authForm
     */
    protected void setCanCertify(TravelReimbursementForm form) {
        final TravelReimbursementAuthorizer authorizer = getDocumentAuthorizer(form);
        //certify
        form.setCanCertify(authorizer.canCertify(form.getTravelReimbursementDocument(), GlobalVariables.getUserSession().getPerson()));
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

    protected void initializePerDiem(final TravelReimbursementDocument reimbursement, final TravelAuthorizationDocument authorization) {
        for (final PerDiemExpense estimate : authorization.getPerDiemExpenses()) {
            final PerDiemExpense mileage = getTravelDocumentService().copyPerDiemExpense(estimate);
            mileage.setDocumentNumber(reimbursement.getDocumentNumber());

            if (!StringUtils.isBlank(mileage.getMileageRateExpenseTypeCode())) {
                LOG.debug("Adding mileage for estimate with date "+ estimate.getMileageDate());
                reimbursement.getPerDiemExpenses().add(mileage);
            }
        }
    }

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

        return super.refresh(mapping, form, request, response);
    }


    @Override
    protected void performRequesterRefresh(TravelDocument document, TravelFormBase travelForm, HttpServletRequest request) {
        final String travelerTypeCode = request.getParameter("document.traveler.travelerTypeCode");
        if (StringUtils.isNotEmpty(travelerTypeCode)) {
            document.getTraveler().setTravelerTypeCode(travelerTypeCode);
        }
        document.getTraveler().refreshReferenceObject(TemPropertyConstants.TRAVELER_TYPE);

        ((TravelReimbursementDocument)document).updatePayeeTypeForReimbursable();
        updateAccountsWithNewProfile(travelForm, document.getTemProfile());
    }


    protected Integer getPerDiemActionLineNumber(final HttpServletRequest request) {
        for (final String parameterKey : ((Map<String,String>) request.getParameterMap()).keySet()) {
            if (StringUtils.containsIgnoreCase(parameterKey, TemPropertyConstants.PER_DIEM_EXPENSES)) {
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

        if (!StringUtils.isBlank(travelForm.getTravelDocumentIdentifier())) {
            LOG.debug("Creating reimbursement for document number "+ travelForm.getTravelDocumentIdentifier());
            document.setTravelDocumentIdentifier(travelForm.getTravelDocumentIdentifier());

            TravelDocument rootDocument = getTravelDocumentService().findRootForTravelReimbursement(document.getTravelDocumentIdentifier());
            if (ObjectUtils.isNull(rootDocument)) {
                String errorMsg = "Retrieved null TravelDocument when searching by travelDocumentIdentifier: "+ document.getTravelDocumentIdentifier()
                                    + " Cannot create a new document";
                LOG.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            LOG.debug("Setting traveler with id "+ rootDocument.getTravelerDetailId());
            document.setTravelerDetailId(rootDocument.getTravelerDetailId());
            document.refreshReferenceObject(TemPropertyConstants.TRAVELER);
            LOG.debug("Traveler is "+ document.getTraveler()+ " with customer number "+ document.getTraveler().getCustomerNumber());

            if (document.getTraveler().getPrincipalId() != null) {
                document.getTraveler().setPrincipalName(getPersonService().getPerson(document.getTraveler().getPrincipalId()).getPrincipalName());
            }
            document.updatePayeeTypeForReimbursable();

            document.setPrimaryDestinationId(rootDocument.getPrimaryDestinationId());
            document.setPrimaryDestination(rootDocument.getPrimaryDestination());
            document.setTripDescription(rootDocument.getTripDescription());
            document.setTripType(rootDocument.getTripType());
            document.setTripTypeCode(rootDocument.getTripTypeCode());
            document.setPrimaryDestination(rootDocument.getPrimaryDestination());
            document.setTripBegin(rootDocument.getTripBegin());
            document.setTripEnd(rootDocument.getTripEnd());
            document.setPrimaryDestinationName(rootDocument.getPrimaryDestinationName());
            document.setPrimaryDestinationCounty(rootDocument.getPrimaryDestinationCounty());
            document.setPrimaryDestinationCountryState(rootDocument.getPrimaryDestinationCountryState());
            document.setGroupTravelers(getTravelDocumentService().copyGroupTravelers(rootDocument.getGroupTravelers(), document.getDocumentNumber()));
            document.setDelinquentTRException(rootDocument.getDelinquentTRException());
            document.setBlanketTravel(rootDocument.getBlanketTravel());
            document.setMealWithoutLodgingReason(rootDocument.getMealWithoutLodgingReason());
            document.configureTraveler(rootDocument.getTemProfileId(), rootDocument.getTraveler());
            document.setExpenseLimit(rootDocument.getExpenseLimit());
            document.setPerDiemAdjustment(rootDocument.getPerDiemAdjustment());
            document.getDocumentHeader().setOrganizationDocumentNumber(rootDocument.getDocumentHeader().getOrganizationDocumentNumber());

            if (document.getPrimaryDestinationId() != null && document.getPrimaryDestinationId().intValue() == TemConstants.CUSTOM_PRIMARY_DESTINATION_ID){
                document.getPrimaryDestination().setPrimaryDestinationName(document.getPrimaryDestinationName());
                document.getPrimaryDestination().setCounty(document.getPrimaryDestinationCounty());
                document.getPrimaryDestination().getRegion().setRegionName(document.getPrimaryDestinationCountryState());
                document.setPrimaryDestinationIndicator(true);
            }

            //copy special circumstances from root document
            for (SpecialCircumstances rootSpecialCircumstances : rootDocument.getSpecialCircumstances() ) {
                for (SpecialCircumstances circumstances : document.getSpecialCircumstances()) {
                    if(circumstances.getQuestionId().equals(rootSpecialCircumstances.getQuestionId())) {
                        circumstances.setText(rootSpecialCircumstances.getText());
                    }
                }
            }



            //KUALITEM-404 : Copying the accounting lines from the TA to the TR upon TR creation.
            //document.setSourceAccountingLines(rootDocument.getSourceAccountingLines());
            //document.setTargetAccountingLines(rootDocument.getTargetAccountingLines());

            //only initialize per diem and copy expenses for a TR created from a TA
            if (rootDocument instanceof TravelAuthorizationDocument) {

                if (isCopyPerDiemAndExpenses(document)) {
                    initializePerDiem(document, (TravelAuthorizationDocument)rootDocument);

                    document.setActualExpenses((List<ActualExpense>) getTravelDocumentService().copyActualExpenses(rootDocument.getActualExpenses(), document.getDocumentNumber()));
                    // add new detail for the copied actualExpenses
                    if (document.getActualExpenses() != null && !document.getActualExpenses().isEmpty()) {
                        for (int i = 0; i < document.getActualExpenses().size(); i++) {
                            travelForm.getNewActualExpenseLines().add(new ActualExpense());
                        }
                    }
                }
            }

            final AccountingDocumentRelationship relationship = buildRelationshipToProgenitorDocument(rootDocument, document);
            getBusinessObjectService().save(relationship);

            // we're not the progenitor so let's force a refresh of notes
            final List<Note> notes = getNoteService().getByRemoteObjectId(rootDocument.getNoteTarget().getObjectId());
            document.setNotes(notes);

        } else {
            // we have no parent document; blank out the trip begin and end dates
            document.setTripBegin(null);
            document.setTripEnd(null);
            document.setTripProgenitor(true); // this is the trip progenitor
        }
        // do the distribution
        travelForm.setDistribution(getAccountingDistributionService().buildDistributionFrom(travelForm.getTravelDocument()));
        initializeAssignAccounts(travelForm);
    }

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ActionForward retval                 = super.execute(mapping, form, request, response);
        final TravelReimbursementForm reimbForm    = (TravelReimbursementForm) form;
        final TravelReimbursementDocument document = ((TravelReimbursementForm) form).getTravelReimbursementDocument();
        final String travelIdentifier = document.getTravelDocumentIdentifier();

        // should we refresh the trip type, upon which so much depends?  let's check and do so if we need to
        if (!StringUtils.isBlank(document.getTripTypeCode())) {
            if (ObjectUtils.isNull(document.getTripType()) || !StringUtils.equals(document.getTripType().getCode(), document.getTripTypeCode())) {
                document.refreshReferenceObject(TemPropertyConstants.TRIP_TYPE);
            }
        } else {
            document.setTripType(null);
        }
        setButtonPermissions(reimbForm);
        LOG.debug("Found "+ document.getActualExpenses().size()+ " other expenses");

        if (reimbForm.getHistory() == null) {
            LOG.debug("Looking up history for TEM document number "+ travelIdentifier);
            final List<Serializable> history = new ArrayList<Serializable>();
            final Collection<TravelReimbursementDocument> docs = getTravelReimbursementService().findByTravelId(travelIdentifier);
            LOG.debug("Got history of size "+ docs.size());
            for (final TravelReimbursementDocument found : docs) {
                LOG.debug("Creating history object for document "+ found);
                LOG.debug("Using header "+ found.getDocumentHeader());
                history.add(new HistoryValueObject(found));
            }
            reimbForm.setHistory(history);
        }

        //Set request variable that will determine whether to show the "Final Reimbursement" checkbox.
        //If TAC exists, no need to create another.
        TravelAuthorizationDocument authorization = getTravelDocumentService().findCurrentTravelAuthorization(document);
        if (authorization instanceof TravelAuthorizationCloseDocument){
            request.setAttribute("isClose", true);
        }

        disablePerDiemExpenes(document);

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

        showAccountDistribution(request, document);

        request.setAttribute(SHOW_REPORTS_ATTRIBUTE, !document.getDocumentHeader().getWorkflowDocument().isInitiated());

        request.setAttribute(CERTIFICATION_STATEMENT_ATTRIBUTE, getCertificationStatement(document));
        request.setAttribute(EMPLOYEE_TEST_ATTRIBUTE, isEmployee(document.getTraveler()));
        request.setAttribute(TemConstants.DELINQUENT_TEST_ATTRIBUTE, document.getDelinquentAction());
        LOG.debug("Found "+ document.getActualExpenses().size()+ " other expenses");

        final boolean showAdvances = getParameterService().getParameterValueAsBoolean(TravelReimbursementDocument.class, DISPLAY_ADVANCES_IN_REIMBURSEMENT_TOTAL_IND);
        request.setAttribute(SHOW_ADVANCES_ATTRIBUTE, showAdvances);

        final boolean showEncumbrance = getParameterService().getParameterValueAsBoolean(TravelReimbursementDocument.class, DISPLAY_ENCUMBRANCE_IND);
        request.setAttribute(SHOW_ENCUMBRANCE_ATTRIBUTE, showEncumbrance);

        if(!getCalculateIgnoreList().contains(reimbForm.getMethodToCall())){
            recalculateTripDetailTotalOnly(mapping, form, request, response);
        }

        getTravelDocumentService().showNoTravelAuthorizationError(document);

        final KualiDecimal paymentTotal = document.getPaymentAmount(); // the grand total is the amount that's actually reimbursable from this trip
        if (paymentTotal != null && !ObjectUtils.isNull(document.getTravelPayment()) && paymentTotal.isGreaterEqual(KualiDecimal.ZERO)) {
            document.getTravelPayment().setCheckTotalAmount(paymentTotal);
        }
        // and update the new source line if possible
        if (reimbForm.getNewSourceLine() != null) {
            final String objectCode = getObjectCodeForNewSourceAccountingLine(reimbForm);
            reimbForm.getNewSourceLine().setFinancialObjectCode(objectCode);
        }

        if (reimbForm.getAccountDistributionsourceAccountingLines() == null || reimbForm.getAccountDistributionsourceAccountingLines().isEmpty()) {
            initializeAssignAccounts(reimbForm);
        }

        return retval;
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
        final String parameterName = (String) request.getAttribute(KRADConstants.METHOD_TO_CALL_ATTRIBUTE);
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
        TravelFormBase travelReqForm = (TravelFormBase) form;
        TravelDocumentBase travelReqDoc = (TravelDocumentBase) travelReqForm.getDocument();

        if (travelReqForm.getDocument() instanceof TravelReimbursementDocument) {
            final boolean showAdvances = getParameterService().getParameterValueAsBoolean(TravelReimbursementDocument.class, DISPLAY_ADVANCES_IN_REIMBURSEMENT_TOTAL_IND);
            request.setAttribute(SHOW_ADVANCES_ATTRIBUTE, showAdvances);
        }

        return recalculateTripDetailTotal(mapping, form, request, response);
    }

    @Override
    public ActionForward approve(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        getTravelDocumentService().showNoTravelAuthorizationError(((TravelReimbursementForm) form).getTravelReimbursementDocument());

        return super.approve(mapping, form, request, response);
    }

    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        addAccountingDocumentRelationship(form);

        return super.save(mapping, form, request, response);
    }

    private void addAccountingDocumentRelationship(ActionForm form) throws WorkflowException {
        TravelReimbursementForm reqForm = (TravelReimbursementForm) form;
        TravelReimbursementDocument trDoc = reqForm.getTravelReimbursementDocument();
        String travelDocumentIdentifier = trDoc.getTravelDocumentIdentifier();

        if (ObjectUtils.isNotNull(travelDocumentIdentifier)) {
            TravelDocument rootDocument = getTravelDocumentService().findRootForTravelReimbursement(travelDocumentIdentifier);

            if (ObjectUtils.isNotNull(rootDocument)) {
            String relationshipDescription = rootDocument.getDocumentTypeName() +" - "+ trDoc.getDocumentTypeName();
            getAccountingDocumentRelationshipService().save(new AccountingDocumentRelationship(rootDocument.getDocumentNumber(), trDoc.getDocumentNumber(), relationshipDescription));
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
        final String parameterName = (String) request.getAttribute(KRADConstants.METHOD_TO_CALL_ATTRIBUTE);
        LOG.debug("Getting selected other expense index from "+ parameterName);
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

        final boolean showAccountDistribution = getParameterService().getParameterValueAsBoolean(TravelReimbursementDocument.class, DISPLAY_ACCOUNTING_DISTRIBUTION_TAB_IND);
        request.setAttribute(SHOW_ACCOUNT_DISTRIBUTION_ATTRIBUTE, showAccountDistribution);

        ActionForward forward = super.route(mapping, form, request, response);

        if (!StringUtils.isBlank(forward.getPath()) && forward.getPath().indexOf(KRADConstants.QUESTION_ACTION) < 0 ) {
            addDateChangedNote(form);

            addAccountingDocumentRelationship(form);

            if (GlobalVariables.getMessageMap().getErrorCount() == 0){
                TravelReimbursementForm travelReimbursementForm = (TravelReimbursementForm) form;
                TravelReimbursementDocument travelReimbursementDocument = travelReimbursementForm.getTravelReimbursementDocument();

                Boolean value = getParameterService().getParameterValueAsBoolean(TravelReimbursementDocument.class, TemConstants.TravelReimbursementParameters.PRETRIP_REIMBURSEMENT_IND, false);

                if (value != null && value.booleanValue()){
                    travelReimbursementDocument.getDocumentHeader().setDocumentDescription(postpendPreTripToDescription(travelReimbursementDocument.getDocumentHeader().getClass(), travelReimbursementDocument.getDocumentHeader().getDocumentDescription()));
                }
            }
        }

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

        if (!StringUtils.isBlank(forward.getPath()) && forward.getPath().indexOf(KRADConstants.QUESTION_ACTION) < 0 ) {
            addDateChangedNote(form);

            addAccountingDocumentRelationship(form);

            if (GlobalVariables.getMessageMap().getErrorCount() == 0){
                TravelReimbursementForm travelReimbursementForm = (TravelReimbursementForm) form;
                TravelReimbursementDocument travelReimbursementDocument = travelReimbursementForm.getTravelReimbursementDocument();

                final boolean preTrip = getParameterService().getParameterValueAsBoolean(TravelReimbursementDocument.class, TemConstants.TravelReimbursementParameters.PRETRIP_REIMBURSEMENT_IND, false);

                if (preTrip){
                    travelReimbursementDocument.getDocumentHeader().setDocumentDescription(postpendPreTripToDescription(travelReimbursementDocument.getDocumentHeader().getClass(), travelReimbursementDocument.getDocumentHeader().getDocumentDescription()));
                }
            }
        }

        return forward;
    }

    /**
     * Adds (Pre-Trip) to the end of the given String (presumably the document description), and then makes sure it will fit within the doc description's max length
     * @param description the description to add (Pre-Trip) to
     * @return the fitted String
     */
    protected String postpendPreTripToDescription(Class<?> documentDescriptionClass, String description) {
        final String postPendedDescription = description + " (Pre-Trip)";
        final int maxLength = getDataDictionaryService().getAttributeMaxLength(documentDescriptionClass, KFSPropertyConstants.DOCUMENT_DESCRIPTION);
        final String fittedDescription = (postPendedDescription.length() > maxLength) ? postPendedDescription.substring(0, maxLength) : postPendedDescription;
        return fittedDescription;
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
            TravelAuthorizationDocument taDoc = getTravelDocumentService().findCurrentTravelAuthorization(travelReqDoc);
            if (ObjectUtils.isNotNull(taDoc)) {
                getTravelReimbursementService().addDateChangedNote(travelReqDoc, taDoc);
            }
        }
    }


    /**
     * Determines the object code for the next source accounting line, based on the distribution for the document
     * @param form the reimbursement form
     * @return the object code to set on the new source accounting line
     */
    protected String getObjectCodeForNewSourceAccountingLine(TravelReimbursementForm form) {
        if (form.getDistribution() != null && !form.getDistribution().isEmpty()) {
            if (form.getDistribution().size() == 1) {
                return form.getDistribution().get(0).getObjectCode();
            } else {
                Set<String> nonUsedDistributionObjectCodes = new HashSet<String>();
                Set<String> usedObjectCodes = getAccountingLineObjectCodes(form);
                for (AccountingDistribution dist : form.getDistribution()) {
                    if (!usedObjectCodes.contains(dist.getObjectCode()) && !dist.getSubTotal().equals(KualiDecimal.ZERO)) {
                        nonUsedDistributionObjectCodes.add(dist.getObjectCode());
                    }
                }
                if (nonUsedDistributionObjectCodes.size() == 1) {
                    // only one left, let's set it; and we can use a for loop to grab the code because...obviously, it will only go once
                    String objectCode = null;
                    for (String objCode : nonUsedDistributionObjectCodes) {
                        objectCode = objCode;
                    }
                    return objectCode;
                }
            }
        }
        return "";
    }

    /**
     * @return a Set of all financial object codes currently used by accounting lines
     */
    protected Set<String> getAccountingLineObjectCodes(TravelReimbursementForm form) {
        Set<String> codes = new HashSet<String>();
        for (AccountingLine line : (List<AccountingLine>)form.getTravelDocument().getSourceAccountingLines()) {
            codes.add(line.getFinancialObjectCode());
        }
        return codes;
    }

    /**
     * Should a new TR created from a TR initialize per diem and copy expenses?
     * Not if there is already a FINAL/PROCESSED TR
     *
     * @param newReimbursementDocument
     * @return
     */
    protected boolean isCopyPerDiemAndExpenses(TravelReimbursementDocument newReimbursementDocument) {

        List<TravelReimbursementDocument> reimbursementDocuments = getTravelDocumentService().findReimbursementDocuments(newReimbursementDocument.getTravelDocumentIdentifier());
        if (!reimbursementDocuments.isEmpty()) {

            for(TravelReimbursementDocument reimbursementDocument : reimbursementDocuments) {
                if (reimbursementDocument.getDocumentHeader().getWorkflowDocument().isFinal() ||
                        reimbursementDocument.getDocumentHeader().getWorkflowDocument().isProcessed()) {

                    //a finalized or processed TR exists- not okay to set up per diem or initialize expenses
                    return false;
                }
            }
        }

        //no TRs exist or there aren't any which have been finalized or processed- okay to set up per diem and initialize expenses
        return true;
    }

    protected TravelReimbursementService getTravelReimbursementService() {
        return SpringContext.getBean(TravelReimbursementService.class);
    }

    protected TravelAuthorizationService getTravelAuthorizationService() {
        return SpringContext.getBean(TravelAuthorizationService.class);
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


