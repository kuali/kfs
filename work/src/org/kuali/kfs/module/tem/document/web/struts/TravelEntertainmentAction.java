/*
 * Copyright 2011 The Kuali Foundation.
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

import static org.kuali.kfs.module.tem.TemConstants.COVERSHEET_FILENAME_FORMAT;
import static org.kuali.kfs.module.tem.TemConstants.SHOW_REPORTS_ATTRIBUTE;
import static org.kuali.kfs.module.tem.TemPropertyConstants.NEW_ATTENDEE_LINE;
import static org.kuali.kfs.module.tem.TemPropertyConstants.TRVL_IDENTIFIER_PROPERTY;
import static org.kuali.kfs.module.tem.util.BufferedLogger.debug;
import static org.kuali.kfs.sys.KFSPropertyConstants.DOCUMENT_NUMBER;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.Attendee;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.businessobject.options.AttendeeTypeValuesFinder;
import org.kuali.kfs.module.tem.document.TravelEntertainmentDocument;
import org.kuali.kfs.module.tem.document.service.TravelEntertainmentDocumentService;
import org.kuali.kfs.module.tem.document.validation.event.AddAttendeeLineEvent;
import org.kuali.kfs.module.tem.document.web.bean.TravelEntertainmentMvcWrapperBean;
import org.kuali.kfs.module.tem.document.web.bean.TravelMvcWrapperBean;
import org.kuali.kfs.module.tem.pdf.Coversheet;
import org.kuali.kfs.module.tem.report.EntertainmentHostCertificationReport;
import org.kuali.kfs.module.tem.report.NonEmployeeCertificationReport;
import org.kuali.kfs.module.tem.report.service.NonEmployeeCertificationReportService;
import org.kuali.kfs.module.tem.report.service.TravelEntertainmentHostCertificationService;
import org.kuali.kfs.module.tem.report.util.BarcodeHelper;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;

/**
 * This class...
 */
public class TravelEntertainmentAction extends TravelActionBase {

    public static final String[] ATTENDEE_ATTRIBUTE_NAMES = { TemPropertyConstants.AttendeeProperties.ATTENDEE_TYPE, TemPropertyConstants.AttendeeProperties.COMPANY, TemPropertyConstants.AttendeeProperties.TITLE,TemPropertyConstants.AttendeeProperties.NAME};
    public static final Integer[] MAX_LENGTH={10,40,40,40};
    
    /**
     * Constructs a TravelEntertainmentDocumentAction.java.
     */
    public TravelEntertainmentAction() {

    }

    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ActionForward retval = super.docHandler(mapping, form, request, response);
        final TravelEntertainmentForm entForm = (TravelEntertainmentForm) form;
        final TravelEntertainmentDocument document = entForm.getEntertainmentDocument();
        
        refreshCollectionsFor(document);

        initializeNewAttendeeLines(entForm.getNewAttendeeLines(), document.getAttendee());
        //initializeTemProfiles(document);
        final String temDocId = request.getParameter(TRVL_IDENTIFIER_PROPERTY);
        if (temDocId != null) {
            populateFromPreviousENTDoc(document, temDocId);
        }
        
        setEntHostCertificationWarning(document);
       
        return retval;
    }

    //TODO: is this really necessary?
    private void initializeTemProfiles(TravelEntertainmentDocument document) {
        if (document.getHostProfile() == null && document.getHostProfileId() != null) {
            document.setHostProfileId(document.getHostProfileId());
        }
        if (document.getTemProfile() == null && document.getTemProfileId() != null) {
            document.setProfileId(document.getTemProfileId());
        }
    }

    private void populateFromPreviousENTDoc(TravelEntertainmentDocument document, String temDocId) {
        try {
            TravelEntertainmentDocument entDocument = (TravelEntertainmentDocument) getDocumentService().getByDocumentHeaderId(temDocId);
            document.setTravelerDetailId(entDocument.getTravelerDetailId());
            document.refreshReferenceObject(TemPropertyConstants.TRAVELER);
            if (document.getAttendee() != null) {
                document.setAttendee(entDocument.getAttendee());
            }
            if (document.getTraveler() != null) {
                document.setTraveler(entDocument.getTraveler());
            }
            document.setHostProfileId(entDocument.getHostProfileId());
            document.setTemProfileId(entDocument.getTemProfileId());
            document.setEventTitle(entDocument.getEventTitle());
            document.setPurposeCode(entDocument.getPurposeCode());
            document.setPaymentMethod(entDocument.getPaymentMethod());
            document.setTripBegin(entDocument.getTripBegin());
            document.setTripEnd(entDocument.getTripEnd());
            document.setSpouseIncluded(entDocument.getSpouseIncluded());
            document.setDescription(entDocument.getDescription());
            document.setHostCertified(entDocument.getHostCertified());
            document.setNonEmployeeCertified(entDocument.getNonEmployeeCertified());
        }
        catch (WorkflowException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
    }

    protected void initializeNewAttendeeLines(final List<Attendee> newAttendeeLines, List<Attendee> attendees) {
        for (Attendee attendee : attendees) {
            if (ObjectUtils.isNull(attendee)) {
                Attendee newAttendeeLine = new Attendee();
                newAttendeeLine.setAttendeeType(attendee.getAttendeeType());
                newAttendeeLine.setCompany(attendee.getCompany());
                newAttendeeLine.setName(attendee.getName());
                newAttendeeLine.setTitle(attendee.getTitle());
                newAttendeeLines.add(newAttendeeLine);
            }
        }
    }

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ActionForward retval = super.execute(mapping, form, request, response);
        final TravelEntertainmentForm entForm = (TravelEntertainmentForm) form;
        final TravelEntertainmentDocument document = (TravelEntertainmentDocument) ((TravelEntertainmentForm) form).getDocument();
        final String travelIdentifier = document.getTravelDocumentIdentifier();

        initializeNames(entForm, document);
        setEntHostCertificationWarning(document);
        
        if(ObjectUtils.isNotNull(document.getActualExpenses())){
            document.enableExpenseTypeSpecificFields(document.getActualExpenses());
        }
        
        if(!getCalculateIgnoreList().contains(entForm.getMethodToCall())){
            recalculateTripDetailTotalOnly(mapping, form, request, response);
        }
        
        refreshRelatedDocuments(entForm);
        
        showAccountDistribution(request, document);
        
        request.setAttribute(SHOW_REPORTS_ATTRIBUTE, !document.getDocumentHeader().getWorkflowDocument().stateIsInitiated());

        entForm.setCanPrintHostCertification(document.canShowHostCertification());
        
        return retval;
    }

    private void initializeNames(final TravelEntertainmentForm entForm, final TravelEntertainmentDocument document) {
        if (document.getHostProfile() != null) {
            document.setHostName(document.getHostProfile().getLastName() + KFSConstants.COMMA + document.getHostProfile().getFirstName() + KFSConstants.BLANK_SPACE +
                    (document.getHostProfile().getMiddleName() == null ? KFSConstants.BLANK_SPACE : document.getHostProfile().getMiddleName()));
        }
        entForm.getNewAttendeeLines().get(0).setName(getTravelerCompleteName(document.getAttendeeDetail()));
    }

    private String getTravelerCompleteName(TravelerDetail travelerdetail) {
        String completeName = KFSConstants.EMPTY_STRING;
        if (validateTravelerDetailName(travelerdetail)) {
            completeName = travelerdetail.getLastName() + KFSConstants.COMMA + travelerdetail.getFirstName() + KFSConstants.BLANK_SPACE + 
                    (travelerdetail.getMiddleName() == null ? KFSConstants.BLANK_SPACE : travelerdetail.getMiddleName());
        }
        return completeName;
    }
    
    private boolean validateTravelerDetailName(TravelerDetail travelerdetail){
        if (travelerdetail == null)
            return false;
        if (travelerdetail.getLastName() == null)
            return false;
        if (travelerdetail.getFirstName() == null)
            return false;
        if (travelerdetail.getLastName().equals(KFSConstants.EMPTY_STRING) && travelerdetail.getFirstName().equals(KFSConstants.EMPTY_STRING))
            return false;

        return true;
    }
    
    private void setEntHostCertificationWarning(final TravelEntertainmentDocument document) {
        if (document.getDocumentHeader().getWorkflowDocument().stateIsInitiated() || document.getDocumentHeader().getWorkflowDocument().stateIsSaved()) {
            boolean entertainmentHostAttached = false;
            List boNotes = document.getBoNotes();
            if (ObjectUtils.isNotNull(boNotes)) {
                for (Object obj : boNotes) {
                    Note note = (Note) obj;
                    if (ObjectUtils.isNotNull(note.getAttachment()) && TemConstants.AttachmentTypeCodes.ATTACHMENT_TYPE_ENT_HOST_CERT.equals(note.getAttachment().getAttachmentTypeCode())) {
                        entertainmentHostAttached = true;
                        break;
                    }
                }
            }
            
            if (document.getHostProfile() != null && document.getTemProfile() != null && !document.getHostProfile().getProfileId().equals(document.getTemProfile().getProfileId())) {
                GlobalVariables.getMessageMap().putWarning(TemPropertyConstants.EntertainmentFields.HOST_NAME, TemKeyConstants.HOST_CERTIFICATION_REQUIRED_IND);
            }            
        }
    }
    
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
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

    protected void refreshCollectionsFor(final TravelEntertainmentDocument entDoc) {       
        if (!entDoc.getDocumentHeader().getWorkflowDocument().stateIsInitiated()) {
            debug("Refreshing objects in entertainment document");
            entDoc.refreshReferenceObject(TemPropertyConstants.TRAVELER);
            entDoc.refreshReferenceObject(TemPropertyConstants.TRIP_TYPE);
            entDoc.refreshReferenceObject(TemPropertyConstants.ACTUAL_EXPENSES);
            entDoc.refreshReferenceObject(TemPropertyConstants.SPECIAL_CIRCUMSTANCES);
        }
    }

    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TravelEntertainmentForm entForm = (TravelEntertainmentForm) form;
        return super.refresh(mapping, form, request, response);
    }

    public ActionForward printCoversheet(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final TravelEntertainmentForm entForm = (TravelEntertainmentForm) form;
        final String documentNumber = request.getParameter(DOCUMENT_NUMBER);
        if (documentNumber != null && !documentNumber.isEmpty()) {
            entForm.setDocument(getEntertainmentDocumentService().find(documentNumber));
        }
        final TravelEntertainmentDocument document = entForm.getEntertainmentDocument();
        final Coversheet cover = getEntertainmentDocumentService().generateCoversheetFor(document);
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        cover.print(stream);

        WebUtils.saveMimeOutputStreamAsFile(response, KFSConstants.ReportGeneration.PDF_MIME_TYPE, stream, String.format(COVERSHEET_FILENAME_FORMAT, document.getDocumentNumber()));

        return null;
    }

    public ActionForward viewNonEmployeeForms(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	final TravelEntertainmentForm entForm = (TravelEntertainmentForm) form;
        entForm.setDocument(getEntertainmentDocumentService().find(request.getParameter(DOCUMENT_NUMBER)));
        final TravelEntertainmentDocument entertainment = entForm.getEntertainmentDocument();
        final NonEmployeeCertificationReport report = getNonEmployeeCertificationReportService().buildReport(entertainment); 
        BarcodeHelper barcode = new BarcodeHelper();
        report.setBarcodeImage(barcode.generateBarcodeImage(entertainment.getDocumentNumber()));
        File reportFile = getNonEmployeeCertificationReportService().generateReport(report);
        
        StringBuilder fileName = new StringBuilder();
        fileName.append(entForm.getDocument().getDocumentNumber());
        fileName.append(KFSConstants.ReportGeneration.PDF_FILE_EXTENSION);
        if (reportFile.length() == 0) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        
        displayPDF(request, response, reportFile, fileName);

        return null;
    }

    public ActionForward viewEntertainmentCertification(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final TravelEntertainmentForm entForm = (TravelEntertainmentForm) form;
        final String documentNumber = request.getParameter(DOCUMENT_NUMBER);
        if (documentNumber != null && !documentNumber.isEmpty()) {
            entForm.setDocument(getEntertainmentDocumentService().find(documentNumber));
        }
        final TravelEntertainmentDocument document = entForm.getEntertainmentDocument();
        entForm.setDocument(getEntertainmentDocumentService().find(request.getParameter(DOCUMENT_NUMBER)));
        final EntertainmentHostCertificationReport report = getEntertainmentHostCertificationService().buildReport(document);
        BarcodeHelper barcode = new BarcodeHelper();
        report.setBarcodeImage(barcode.generateBarcodeImage(document.getDocumentNumber()));
        File reportFile = getEntertainmentHostCertificationService().generateEntertainmentHostCertReport(report);

        StringBuilder fileName = new StringBuilder();
        fileName.append(document.getDocumentNumber());
        fileName.append(KFSConstants.ReportGeneration.PDF_FILE_EXTENSION);
        if (reportFile.length() == 0) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        displayPDF(request, response, reportFile, fileName);

        return null;
    }

    @Override
    public ActionForward createREQSForVendor(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward addAttendeeLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final TravelFormBase travelForm = (TravelFormBase) form;
        final TravelEntertainmentMVCWrapperBean mvcWrapper = newMvcDelegate(form);
        mvcWrapper.setNewAttendeeLine(mvcWrapper.getNewAttendeeLines().get((int) getSelectedLine(request)));
        travelForm.getObservable().notifyObservers(mvcWrapper);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward deleteAttendeeLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final TravelFormBase travelForm = (TravelFormBase) form;
        final TravelMvcWrapperBean mvcWrapper = newMvcDelegate(form);
        travelForm.getObservable().notifyObservers(new Object[] { mvcWrapper, getSelectedLine(request) });
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Import Attendees to the document from a spreadsheet.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward importAttendees(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final TravelEntertainmentForm travelForm           = (TravelFormBase) form;
        final TravelEntertainmentMvcWrapperBean mvcWrapper = newMvcDelegate(form);

        travelForm.getObservable().notifyObservers(new Object[] { mvcWrapper, new String(travelForm.getAttendeesImportFile().getFileData()) });

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    @Override
    public ActionForward downloadBOAttachment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return super.downloadBOAttachment(mapping, form, request, response);
    }

    protected void setContactMasking(TravelEntertainmentForm entForm) {
        entForm.setCanUnmask(entForm.isUserDocumentInitiator());
    }

    protected TravelEntertainmentHostCertificationService getEntertainmentHostCertificationService() {
        return SpringContext.getBean(TravelEntertainmentHostCertificationService.class);
    }

    protected TravelEntertainmentDocumentService getEntertainmentDocumentService() {
        return SpringContext.getBean(TravelEntertainmentDocumentService.class);
    }

    protected NonEmployeeCertificationReportService getNonEmployeeCertificationReportService() {
        return SpringContext.getBean(NonEmployeeCertificationReportService.class);
    }

    @Override
    protected Class<? extends TravelMvcWrapperBean> getMvcWrapperInterface() {
        return TravelEntertainmentMvcWrapperBean.class;
    }

}
