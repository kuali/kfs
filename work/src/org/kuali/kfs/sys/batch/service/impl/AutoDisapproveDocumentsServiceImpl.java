/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.sys.batch.service.impl;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.joda.time.DateTime;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.batch.AutoDisapproveDocumentsStep;
import org.kuali.kfs.sys.batch.service.AutoDisapproveDocumentsService;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.datadictionary.exception.UnknownDocumentTypeException;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the AutoDisapproveDocumentsService batch job.
 */
@Transactional
public class AutoDisapproveDocumentsServiceImpl implements AutoDisapproveDocumentsService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AutoDisapproveDocumentsServiceImpl.class);
    public static final String WORKFLOW_DOCUMENT_HEADER_ID_SEARCH_RESULT_KEY = "routeHeaderId";

    private DocumentService documentService;
    private DocumentTypeService documentTypeService;

    private DateTimeService dateTimeService;
    private ParameterService parameterService;

    private NoteService noteService;
    private PersonService personService;

    private ReportWriterService autoDisapproveErrorReportWriterService;
    private FinancialSystemDocumentService financialSystemDocumentService;

    /**
     * Constructs a AutoDisapproveDocumentsServiceImpl instance
     */
    public AutoDisapproveDocumentsServiceImpl() {

    }

    /**
     * Gathers all documents that are in ENROUTE status and auto disapproves them.
     * @see org.kuali.kfs.sys.batch.service.autoDisapproveDocumentsInEnrouteStatus#autoDisapproveDocumentsInEnrouteStatus()
     */
    @Override
    public boolean autoDisapproveDocumentsInEnrouteStatus() {
        boolean success = true ;

        if (systemParametersForAutoDisapproveDocumentsJobExist()) {
            if (canAutoDisapproveJobRun()) {
                LOG.debug("autoDisapproveDocumentsInEnrouteStatus() started");

                Person systemUser = getPersonService().getPersonByPrincipalName(KFSConstants.SYSTEM_USER);

                String principalId = systemUser.getPrincipalId();
                String annotationForAutoDisapprovalDocument = getParameterService().getParameterValueAsString(AutoDisapproveDocumentsStep.class, KFSParameterKeyConstants.YearEndAutoDisapprovalConstants.YEAR_END_AUTO_DISAPPROVE_ANNOTATION);

                Date documentCompareDate = getDocumentCompareDateParameter();
                success = processAutoDisapproveDocuments(principalId, annotationForAutoDisapprovalDocument, documentCompareDate);
            }
        }

        return success;
    }

    /**
     * This method checks if the System parameters have been set up for this batch job.
     * @result return true if the system parameters exist, else false
     */
    protected boolean systemParametersForAutoDisapproveDocumentsJobExist() {
        LOG.debug("systemParametersForAutoDisapproveDocumentsJobExist() started.");

        boolean systemParametersExists = true;

        systemParametersExists &= checkIfRunDateParameterExists();
        systemParametersExists &= checkIfParentDocumentTypeParameterExists();
        systemParametersExists &= checkIfDocumentCompareCreateDateParameterExists();
        systemParametersExists &= checkIfDocumentTypesExceptionParameterExists();
        systemParametersExists &= checkIfAnnotationForDisapprovalParameterExists();

        return systemParametersExists;
    }

    /**
     * This method checks for the system parameter for YEAR_END_AUTO_DISAPPROVE_DOCUMENTS_RUN_DATE
     * @param outputErrorFile_ps output error file stream to write any error messages.
     * @return true if YEAR_END_AUTO_DISAPPROVE_DOCUMENTS_RUN_DATE exists else false
     */
    protected boolean checkIfRunDateParameterExists() {
        boolean parameterExists = true;

        // check to make sure the system parameter for run date check has already been setup...
        if (!getParameterService().parameterExists(AutoDisapproveDocumentsStep.class, KFSParameterKeyConstants.YearEndAutoDisapprovalConstants.YEAR_END_AUTO_DISAPPROVE_DOCUMENT_STEP_RUN_DATE)) {
            LOG.warn("YEAR_END_AUTO_DISAPPROVE_DOCUMENT_RUN_DATE System parameter does not exist in the parameters list.  The job can not continue without this parameter");
            autoDisapproveErrorReportWriterService.writeFormattedMessageLine("YEAR_END_AUTO_DISAPPROVE_DOCUMENTS_RUN_DATE System parameter does not exist in the parameters list.  The job can not continue without this parameter");
            return false;
        }

        return parameterExists;
    }

    /**
     * This method checks for the system parameter for YEAR_END_AUTO_DISAPPROVE_PARENT_DOCUMENT_TYPE
     * @param outputErrorFile_ps output error file stream to write any error messages.
     * @return true if YEAR_END_AUTO_DISAPPROVE_PARENT_DOCUMENT_TYPE exists else false
     */
    protected boolean checkIfParentDocumentTypeParameterExists() {
        boolean parameterExists = true;

        // check to make sure the system parameter for Parent Document Type = FP has been setup...
        if (!getParameterService().parameterExists(AutoDisapproveDocumentsStep.class, KFSParameterKeyConstants.YearEndAutoDisapprovalConstants.YEAR_END_AUTO_DISAPPROVE_PARENT_DOCUMENT_TYPE)) {
            LOG.warn("YEAR_END_AUTO_DISAPPROVE_PARENT_DOCUMENT_TYPE System parameter does not exist in the parameters list.  The job can not continue without this parameter");
            autoDisapproveErrorReportWriterService.writeFormattedMessageLine("YEAR_END_AUTO_DISAPPROVE_PARENT_DOCUMENT_TYPE System parameter does not exist in the parameters list.  The job can not continue without this parameter");
            return false;
        }

        return parameterExists;
    }

    /**
     * This method checks for the system parameter for YEAR_END_AUTO_DISAPPROVE_DOCUMENT_CREATE_DATE
     * @param outputErrorFile_ps output error file stream to write any error messages.
     * @return true if YEAR_END_AUTO_DISAPPROVE_DOCUMENT_CREATE_DATE exists else false
     */
    protected boolean checkIfDocumentCompareCreateDateParameterExists() {
        boolean parameterExists = true;

        // check to make sure the system parameter for create date to compare has been setup...
        if (!getParameterService().parameterExists(AutoDisapproveDocumentsStep.class, KFSParameterKeyConstants.YearEndAutoDisapprovalConstants.YEAR_END_AUTO_DISAPPROVE_DOCUMENT_CREATE_DATE)) {
          LOG.warn("YEAR_END_AUTO_DISAPPROVE_DOCUMENT_CREATE_DATE System parameter does not exist in the parameters list.  The job can not continue without this parameter");
          autoDisapproveErrorReportWriterService.writeFormattedMessageLine("YEAR_END_AUTO_DISAPPROVE_DOCUMENT_CREATE_DATE System parameter does not exist in the parameters list.  The job can not continue without this parameter");
          return false;
        }

        return parameterExists;
    }

    /**
     * This method checks for the system parameter for YEAR_END_AUTO_DISAPPROVE_DOCUMENT_TYPES
     * @param outputErrorFile_ps output error file stream to write any error messages.
     * @return true if YEAR_END_AUTO_DISAPPROVE_DOCUMENT_TYPES exists else false
     */
    protected boolean checkIfDocumentTypesExceptionParameterExists() {
        boolean parameterExists = true;

        // check to make sure the system parameter for Document Types that are exceptions has been setup...
        if (!getParameterService().parameterExists(AutoDisapproveDocumentsStep.class, KFSParameterKeyConstants.YearEndAutoDisapprovalConstants.YEAR_END_AUTO_DISAPPROVE_DOCUMENT_TYPES)) {
          LOG.warn("YEAR_END_AUTO_DISAPPROVE_DOCUMENT_TYPES System parameter does not exist in the parameters list.  The job can not continue without this parameter");
          autoDisapproveErrorReportWriterService.writeFormattedMessageLine("YEAR_END_AUTO_DISAPPROVE_DOCUMENT_TYPES System parameter does not exist in the parameters list.  The job can not continue without this parameter");
          return false;
        }

        return parameterExists;
    }

    /**
     * This method checks for the system parameter for YEAR_END_AUTO_DISAPPROVE_ANNOTATION
     * @param outputErrorFile_ps output error file stream to write any error messages.
     * @return true if YEAR_END_AUTO_DISAPPROVE_ANNOTATION exists else false
     */
    protected boolean checkIfAnnotationForDisapprovalParameterExists() {
        boolean parameterExists = true;

        // check to make sure the system parameter for annotation for notes has been setup...
        if (!getParameterService().parameterExists(AutoDisapproveDocumentsStep.class, KFSParameterKeyConstants.YearEndAutoDisapprovalConstants.YEAR_END_AUTO_DISAPPROVE_ANNOTATION)) {
          LOG.warn("YEAR_END_AUTO_DISAPPROVE_ANNOTATION System parameter does not exist in the parameters list.  The job can not continue without this parameter");
          autoDisapproveErrorReportWriterService.writeFormattedMessageLine("YEAR_END_AUTO_DISAPPROVE_ANNOTATION System parameter does not exist in the parameters list.  The job can not continue without this parameter");
          return false;
        }

        return parameterExists;
    }

    /**
     * This method will compare today's date to the system parameter for year end auto disapproval run date
     * @return true if today's date equals to the system parameter run date
     */
    protected boolean canAutoDisapproveJobRun() {
      boolean autoDisapproveCanRun = true;

      // IF trunc(SYSDATE - 14/24) = v_yec_cncl_doc_run_dt THEN...FIS CODE equivalent here...
      String yearEndAutoDisapproveRunDate = getParameterService().getParameterValueAsString(AutoDisapproveDocumentsStep.class, KFSParameterKeyConstants.YearEndAutoDisapprovalConstants.YEAR_END_AUTO_DISAPPROVE_DOCUMENT_STEP_RUN_DATE);

      String today = getDateTimeService().toDateString(getDateTimeService().getCurrentDate());

      if (!yearEndAutoDisapproveRunDate.equals(today)) {
          LOG.warn("YEAR_END_AUTO_DISAPPROVE_DOCUMENTS_RUN_DATE: Automatic disapproval bypassed. The date on which the auto disapproval step should run: " + yearEndAutoDisapproveRunDate + " does not equal to today's date: " + today);
          String message = ("YEAR_END_AUTO_DISAPPROVE_DOCUMENTS_RUN_DATE: Automatic disapproval bypassed. The date on which the auto disapproval step should run: ").concat(yearEndAutoDisapproveRunDate).concat(" does not equal to today's date: ").concat(today);
          autoDisapproveErrorReportWriterService.writeFormattedMessageLine(message);
          autoDisapproveCanRun = false;
      }

      return autoDisapproveCanRun;
    }

    /**
     * This method will use DocumentSearchCriteria to search for the documents that are in enroute status and disapproves them
     *
     * @param principalId The principal id which is KFS-SYS System user to run the process under.
     * @param annotation The annotation to be set as note in the note of the document.
     * @param documentCompareDate The document create date to compare to
     */
    protected boolean processAutoDisapproveDocuments(String principalId, String annotation, Date documentCompareDate) {
        boolean success = true;

        Collection<FinancialSystemDocumentHeader> documentList = this.getFinancialSystemDocumentService().findByWorkflowStatusCode(DocumentStatus.ENROUTE);

        for (FinancialSystemDocumentHeader financialSystemDocumentHeader : documentList) {

            if (checkIfDocumentEligibleForAutoDispproval(financialSystemDocumentHeader)) {
                if (!exceptionsToAutoDisapproveProcess(financialSystemDocumentHeader, documentCompareDate)) {
                    try {
                        autoDisapprovalYearEndDocument(financialSystemDocumentHeader, annotation);
                        LOG.info("The document with header id: " + financialSystemDocumentHeader.getDocumentNumber() + " is automatically disapproved by this job.");
                    }
                    catch (Exception e) {
                        LOG.error("Exception encountered trying to auto disapprove the document " + e.getMessage());
                        String message = ("Exception encountered trying to auto disapprove the document: ").concat(financialSystemDocumentHeader.getDocumentNumber());
                        autoDisapproveErrorReportWriterService.writeFormattedMessageLine(message);
                    }
                }
                else {
                    LOG.info("Year End Auto Disapproval Exceptions:  The document: " + financialSystemDocumentHeader.getDocumentNumber() + " is NOT AUTO DISAPPROVED.");
                }
            }
        }

       return success;
    }

    /**
     * This method will check the document's document type against the parent document type as specified in the system parameter
     * @param document
     * @return true if  document type of the document is a child of the parent document.
     */
    protected boolean checkIfDocumentEligibleForAutoDispproval(DocumentHeader documentHeader) {
        boolean documentEligible = false;

        String yearEndAutoDisapproveParentDocumentType = getParameterService().getParameterValueAsString(AutoDisapproveDocumentsStep.class, KFSParameterKeyConstants.YearEndAutoDisapprovalConstants.YEAR_END_AUTO_DISAPPROVE_PARENT_DOCUMENT_TYPE);

        DocumentType parentDocumentType = getDocumentTypeService().getDocumentTypeByName(yearEndAutoDisapproveParentDocumentType);

        String documentTypeName = documentHeader.getWorkflowDocument().getDocumentTypeName();
        DocumentType childDocumentType = getDocumentTypeService().getDocumentTypeByName(documentTypeName);
        documentEligible = childDocumentType.getParentId().equals(parentDocumentType.getId());
        return documentEligible;
    }

    /**
     * This method finds the date in the system parameters that will be used to compare the create date.
     * It then adds 23 hours, 59 minutes and 59 seconds to the compare date.
     * @return  documentCompareDate returns YEAR_END_AUTO_DISAPPROVE_DOCUMENT_CREATE_DATE from the system parameter
     */
    protected Date getDocumentCompareDateParameter() {
        Date documentCompareDate = null;

        String yearEndAutoDisapproveDocumentDate = getParameterService().getParameterValueAsString(AutoDisapproveDocumentsStep.class, KFSParameterKeyConstants.YearEndAutoDisapprovalConstants.YEAR_END_AUTO_DISAPPROVE_DOCUMENT_CREATE_DATE);

        if (ObjectUtils.isNull(yearEndAutoDisapproveDocumentDate)) {
            LOG.warn("Exception: System Parameter YEAR_END_AUTO_DISAPPROVE_DOCUMENT_CREATE_DATE can not be determined.");
            String message = ("Exception: The value for System Parameter YEAR_END_AUTO_DISAPPROVE_DOCUMENT_CREATE_DATE can not be determined.  The auto disapproval job is stopped.");
            autoDisapproveErrorReportWriterService.writeFormattedMessageLine(message);
            throw new RuntimeException("Exception: AutoDisapprovalStep job stopped because System Parameter YEAR_END_AUTO_DISAPPROVE_DOCUMENT_CREATE_DATE is null");
        }

        try {
            Date compareDate = getDateTimeService().convertToDate(yearEndAutoDisapproveDocumentDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(compareDate);
            calendar.set(Calendar.HOUR, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            documentCompareDate = calendar.getTime();
        }
        catch (ParseException pe) {
            LOG.warn("ParseException: System Parameter YEAR_END_AUTO_DISAPPROVE_DOCUMENT_CREATE_DATE can not be determined.");
            String message = ("ParseException: The value for System Parameter YEAR_END_AUTO_DISAPPROVE_DOCUMENT_CREATE_DATE is invalid.  The auto disapproval job is stopped.");
            autoDisapproveErrorReportWriterService.writeFormattedMessageLine(message);
            throw new RuntimeException("ParseException: AutoDisapprovalStep job stopped because System Parameter YEAR_END_AUTO_DISAPPROVE_DOCUMENT_CREATE_DATE is invalid");
        }

        return documentCompareDate;
    }

    /**
     * This method finds the document for the given document header id
     * @param documentHeaderId
     * @return document The document in the workflow that matches the document header id.
     */
    protected Document findDocumentForAutoDisapproval(String documentHeaderId) {
        Document document = null;

        try {
            document = documentService.getByDocumentHeaderId(documentHeaderId);
        }
        catch (WorkflowException ex) {
            LOG.error("Exception encountered on finding the document: " + documentHeaderId, ex );
        } catch ( UnknownDocumentTypeException ex ) {
            // don't blow up just because a document type is not installed (but don't return it either)
            LOG.error("Exception encountered on finding the document: " + documentHeaderId, ex );
        }

        return document;
    }

    /**
     * This method first checks the document's create date with system parameter date and then
     * checks the document type name to the system parameter values and returns true if the type name exists
     * @param document document to check for its document type,  documentCompareDate the system parameter specified date
     * to compare the current date to this date.
     * @return true if  document's create date is <= documentCompareDate and if document type is not in the
     * system parameter document types that are set to disallow.
     */
    protected boolean exceptionsToAutoDisapproveProcess(DocumentHeader documentHeader, Date documentCompareDate) {
        boolean exceptionToDisapprove = true;
        Date createDate = null;

        String documentNumber =  documentHeader.getDocumentNumber();

        DateTime documentCreateDate = documentHeader.getWorkflowDocument().getDateCreated();
        createDate = documentCreateDate.toDate();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(documentCompareDate);
        String strCompareDate = calendar.getTime().toString();

        calendar.setTime(createDate);
        String strCreateDate = calendar.getTime().toString();

        if (createDate.before(documentCompareDate) || createDate.equals(documentCompareDate)) {
            String documentTypeName = documentHeader.getWorkflowDocument().getDocumentTypeName();

            ParameterEvaluator evaluatorDocumentType = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(AutoDisapproveDocumentsStep.class, KFSParameterKeyConstants.YearEndAutoDisapprovalConstants.YEAR_END_AUTO_DISAPPROVE_DOCUMENT_TYPES, documentTypeName);
            exceptionToDisapprove = !evaluatorDocumentType.evaluationSucceeds();
            if (exceptionToDisapprove) {
                LOG.info("Document Id: " + documentNumber + " - Exception to Auto Disapprove:  Document's type: " + documentTypeName + " is in the System Parameter For Document Types Exception List.");
            }
        }
        else {
            LOG.info("Document Id: " + documentNumber + " - Exception to Auto Disapprove:  Document's create date: " + strCreateDate + " is NOT less than or equal to System Parameter Compare Date: " + strCompareDate);
            exceptionToDisapprove = true;
        }

        return exceptionToDisapprove;
    }

    /** autoDisapprovalYearEndDocument uses DocumentServiceImpl to  mark as disapproved by calling
     *  DocumentServiceImpl's disapproveDocument method.
     *
     *@param document The document that needs to be auto disapproved in this process
     *@param annotationForAutoDisapprovalDocument The annotationForAutoDisapprovalDocument that is set as annotations when canceling the edoc.
     *
     */
    protected void autoDisapprovalYearEndDocument(DocumentHeader documentHeader, String annotationForAutoDisapprovalDocument)  throws Exception {
        Person systemUser = getPersonService().getPersonByPrincipalName(KFSConstants.SYSTEM_USER);

        Note approveNote = noteService.createNote(new Note(), documentHeader, systemUser.getPrincipalId());
        approveNote.setNoteText(annotationForAutoDisapprovalDocument);

        approveNote.setAuthorUniversalIdentifier(systemUser.getPrincipalId());

        approveNote.setNotePostedTimestampToCurrent();

        noteService.save(approveNote);

        Document document = documentService.getByDocumentHeaderId(documentHeader.getDocumentNumber());

        document.addNote(approveNote);

        documentService.superUserDisapproveDocumentWithoutSaving(document, "Disapproval of Outstanding Documents - Year End Cancellation Process");
    }

    /**
     * Sets the documentService attribute value.
     *
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Gets the documentService attribute.
     *
     * @return Returns the documentService.
     */
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * Gets the parameterService attribute.
     *
     * @return Returns the parameterService.
     */
    protected ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     *
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Gets the dateTimeService attribute.
     *
     * @return Returns the dateTimeService.
     */
    protected DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     *
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Gets the NoteService, lazily initializing if necessary
     * @return the NoteService
     */
    protected synchronized NoteService getNoteService() {
        if (this.noteService == null) {
            this.noteService = SpringContext.getBean(NoteService.class);
        }
        return this.noteService;
    }

    /**
     * Sets the noteService attribute value.
     *
     * @param noteService The noteService to set.
     */
    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * @return Returns the personService.
     */
    protected PersonService getPersonService() {
        if(personService==null) {
            personService = SpringContext.getBean(PersonService.class);
        }
        return personService;
    }

    /**
     * Gets the documentTypeService attribute.
     *
     * @return Returns the documentTypeService.
     */
    protected DocumentTypeService getDocumentTypeService() {
        if(documentTypeService==null) {
            documentTypeService = SpringContext.getBean(DocumentTypeService.class);
        }
        return documentTypeService;
    }

    /**
     * Gets the autoDisapproveErrorReportWriterService attribute.
     * @return Returns the autoDisapproveErrorReportWriterService.
     */
    protected ReportWriterService getAutoDisapproveErrorReportWriterService() {
        return autoDisapproveErrorReportWriterService;
    }

    /**
     * Sets the autoDisapproveErrorReportWriterService attribute value.
     * @param autoDisapproveErrorReportWriterService The autoDisapproveErrorReportWriterService to set.
     */
    public void setAutoDisapproveErrorReportWriterService(ReportWriterService autoDisapproveErrorReportWriterService) {
        this.autoDisapproveErrorReportWriterService = autoDisapproveErrorReportWriterService;
    }

    /**
     * Gets the financialSystemDocumentHeaderSerivce attribute
     *
     * @return
     */
    public FinancialSystemDocumentService getFinancialSystemDocumentService() {
        return financialSystemDocumentService;
    }

    /**
     * Spring hook to inject financialSystemDocumentHeaderService
     *
     * @param financialSystemDocumentService
     */
    public void setFinancialSystemDocumentService(FinancialSystemDocumentService financialSystemDocumentService) {
        this.financialSystemDocumentService = financialSystemDocumentService;
    }



}
