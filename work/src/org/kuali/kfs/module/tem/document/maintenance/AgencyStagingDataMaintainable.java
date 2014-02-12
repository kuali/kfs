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
package org.kuali.kfs.module.tem.document.maintenance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.AgencyStagingDataErrorCodes;
import org.kuali.kfs.module.tem.TemConstants.ExpenseImportTypes;
import org.kuali.kfs.module.tem.batch.service.AgencyDataImportService;
import org.kuali.kfs.module.tem.batch.service.ExpenseImportByTravelerService;
import org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.businessobject.CreditCardAgency;
import org.kuali.kfs.module.tem.businessobject.TripAccountingInformation;
import org.kuali.kfs.module.tem.service.CreditCardAgencyService;
import org.kuali.kfs.module.tem.util.MessageUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.kfs.sys.report.BusinessObjectReportHelper;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.MessageMap;

/**
 * Maintainable instance for the travel agency audit maintenance document
 *
 */
public class AgencyStagingDataMaintainable extends FinancialSystemMaintainable {
    static final Logger LOG = Logger.getLogger(AgencyStagingDataMaintainable.class);

    private volatile static AgencyDataImportService agencyDataImportService;
    private volatile static ExpenseImportByTravelerService expenseImportByTravelerService;
    private volatile static ExpenseImportByTripService expenseImportByTripService;
    private volatile static CreditCardAgencyService creditCardAgencyService;
    private volatile static DocumentService documentService;
    private volatile static DataDictionaryService dataDictionaryService;
    private volatile static IdentityService identityService;
    private volatile static NoteService noteService;

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterNew(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
     */
    @Override
    public void processAfterNew(MaintenanceDocument document, Map<String,String[]> parameters) {
        super.processAfterNew(document, parameters);
        AgencyStagingData agencyData = (AgencyStagingData) getBusinessObject();
        agencyData.setManualCreated(true);

        //default the import type (probably by trip)
        agencyData.setImportBy(ExpenseImportTypes.IMPORT_BY_TRIP);

        agencyData.setCreationTimestamp(getDateTimeService().getCurrentTimestamp());
        agencyData.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterCopy(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
     */
    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String,String[]> parameters) {
        super.processAfterCopy(document, parameters);
        AgencyStagingData agencyData = (AgencyStagingData) getBusinessObject();
        agencyData.setManualCreated(true);
        agencyData.setCreationTimestamp(getDateTimeService().getCurrentTimestamp());

        AgencyStagingDataMaintainable oldMaintainable = (AgencyStagingDataMaintainable)document.getOldMaintainableObject();
        //this is not new, so it must be for copy - we will set the Copied From Id
        agencyData.setCopiedFromId(((AgencyStagingData)oldMaintainable.getBusinessObject()).getId());

        //set TripAccountingInformation primary key and foreign key to null so the maintainance document can handle setting the appropriate key values
        if (!agencyData.getTripAccountingInformation().isEmpty()) {
            for(TripAccountingInformation account : agencyData.getTripAccountingInformation()) {
                account.setId(null);
                account.setAgencyStagingDataId(null);
            }
        }
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#doRouteStatusChange(org.kuali.rice.kns.bo.DocumentHeader)
     */
    @Override
    public void doRouteStatusChange(DocumentHeader documentHeader) {
        if (documentHeader.getWorkflowDocument().isProcessed()){
            AgencyStagingData agencyStaging  = (AgencyStagingData) getBusinessObject();

            updateCreditCardAgency(agencyStaging);

            //All validations have passed at this point. Set error code to 'OK'.
            agencyStaging.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);

            //after fixing the agency audit record, attempt to move agency data to historical table
            List<ErrorMessage> errors = getAgencyDataImportService().processAgencyStagingExpense(agencyStaging, getGeneralLedgerPendingEntrySequenceHelper());
            LOG.info("Agency Data Id: "+ agencyStaging.getId() + (errors.isEmpty() ? " was":" was not") +" processed.");

            //add a Note if there were errors reconciling or distributing the record
            if (!errors.isEmpty()) {
                try {
                    MaintenanceDocument document = (MaintenanceDocument) getDocumentService().getByDocumentHeaderId(documentHeader.getDocumentNumber());
                    addNoteAfterProcessingAgencyStagingExpense(document, errors);
                }
                catch (WorkflowException exception) {
                    LOG.error("Unable to add Note to Document Id: "+ documentHeader.getDocumentNumber(), exception);
                    LOG.error(getMessageAsString(errors));
                }
            }

            // nota bene: agency staging data object does NOT need to be saved here as the maint doc will save it itself once processing completes

        }
        super.doRouteStatusChange(documentHeader);
    }

    protected GeneralLedgerPendingEntrySequenceHelper getGeneralLedgerPendingEntrySequenceHelper() {
        Collection<GeneralLedgerPendingEntry> glpes = getAgencyDataImportService().getGeneralLedgerPendingEntriesForDocumentNumber((AgencyStagingData) getBusinessObject());

        int maxGLPESequenceValue = 0;

        for (GeneralLedgerPendingEntry glpe : glpes) {
            if (glpe.getTransactionLedgerEntrySequenceNumber().intValue() > maxGLPESequenceValue) {
                maxGLPESequenceValue = glpe.getTransactionLedgerEntrySequenceNumber().intValue();
            }
        }

        maxGLPESequenceValue++;

        return new GeneralLedgerPendingEntrySequenceHelper(maxGLPESequenceValue);
    }

    /**
     *
     * @param agencyStaging
     */
    protected void updateCreditCardAgency(AgencyStagingData agencyStaging){
        //update the agency name base on code if provided
        CreditCardAgency agency = getCreditCardAgencyService().getCreditCardAgencyByCode(agencyStaging.getCreditCardOrAgencyCode());
        if (agency != null){
            agencyStaging.setCreditCardAgency(agency);
        }
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterEdit(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
     */
    @Override
    public void processAfterPost(MaintenanceDocument document, Map<String, String[]> parameters) {
        updateCreditCardAgency((AgencyStagingData)document.getNewMaintainableObject().getBusinessObject());
        super.processAfterPost(document, parameters);
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterEdit(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
     */
    @Override
    public void processAfterEdit(MaintenanceDocument document, Map<String, String[]> parameters) {
        List<ErrorMessage>  errorMessages = null;
        AgencyStagingData  agency = (AgencyStagingData)document.getNewMaintainableObject().getBusinessObject();

        if(TemConstants.ExpenseImportTypes.IMPORT_BY_TRIP.equals(agency.getImportBy())) {
           errorMessages = getExpenseImportByTripService().validateAgencyData(agency);
        }
        else if (TemConstants.ExpenseImportTypes.IMPORT_BY_TRAVELLER.equals(agency.getImportBy())) {
            errorMessages = getExpenseImportByTravelerService().validateAgencyData(agency);
        }

        if (errorMessages.isEmpty()) {
            agency.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);
        }

        MessageMap messageMap = GlobalVariables.getMessageMap();
        for(ErrorMessage message : errorMessages ){
            messageMap.putError(KFSConstants.GLOBAL_ERRORS, message.getErrorKey(), message.getMessageParameters());
        }

        updateCreditCardAgency((AgencyStagingData)document.getNewMaintainableObject().getBusinessObject());

        super.processAfterEdit(document, parameters);
    }

	/**
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#saveBusinessObject()
	 */
	@Override
	public void saveBusinessObject() {
	    AgencyStagingData agencyStaging = (AgencyStagingData) getBusinessObject();

	    if (agencyStaging.isActive()) {
    	    //since it is fixed and submitted, changing the status to OK unless it has already been set to HIS
	        if (!agencyStaging.getErrorCode().equals(AgencyStagingDataErrorCodes.AGENCY_MOVED_TO_HISTORICAL)) {
	            agencyStaging.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);
	        }
        }

        super.saveBusinessObject();
	}

	/**
	 *
	 * This method trims the descriptionText to 40 characters.
	 * @param descriptionText
	 * @return
	 */
	protected String trimDescription(String descriptionText) {
        return StringUtils.substring(descriptionText, 0, 39);
	}

	protected void addNoteAfterProcessingAgencyStagingExpense(MaintenanceDocument document, List<ErrorMessage> errors) {

        Principal kfsSystemUser = getIdentityService().getPrincipalByPrincipalName(KFSConstants.SYSTEM_USER);
	    String errorText = getMessageAsString(errors);

	    if (!StringUtils.isEmpty(errorText)) {
	        //check maxLength on a Note and truncate if necessary
    	    Integer maxLength = getDataDictionaryService().getAttributeMaxLength(Note.class, KRADConstants.NOTE_TEXT_PROPERTY_NAME);
    	    if (errorText.length() > maxLength) {
    	        LOG.warn("Adding a truncated error text to Note due to space limitations. Original text:");
    	        LOG.warn(errorText);
    	        errorText = errorText.substring(0,maxLength);
    	    }

            final Note newNote = getDocumentService().createNoteFromDocument(document, errorText);
            newNote.setAuthorUniversalIdentifier(kfsSystemUser.getPrincipalId());
            document.addNote(newNote);
            getNoteService().save(newNote);
	    }
	}

    protected String getMessageAsString(List<ErrorMessage> errorMessages){

        List<String> messageList = new ArrayList<String>();

        for (ErrorMessage error : errorMessages){
            messageList.add(MessageUtils.getErrorMessage(error));
        }

        StrBuilder builder = new StrBuilder();
        builder.appendWithSeparators(messageList, BusinessObjectReportHelper.LINE_BREAK);
        return  builder.toString();
    }

	public DateTimeService getDateTimeService(){
	    return SpringContext.getBean(DateTimeService.class);
	}

	public AgencyDataImportService getAgencyDataImportService() {
	    if (agencyDataImportService == null) {
	        agencyDataImportService = SpringContext.getBean(AgencyDataImportService.class);
	    }
	    return agencyDataImportService;
	}

	public ExpenseImportByTravelerService getExpenseImportByTravelerService() {
	    if (expenseImportByTravelerService == null) {
	        expenseImportByTravelerService = SpringContext.getBean(ExpenseImportByTravelerService.class);
	    }
	    return expenseImportByTravelerService;
	}

	public ExpenseImportByTripService getExpenseImportByTripService() {
	    if (expenseImportByTripService == null) {
	        expenseImportByTripService = SpringContext.getBean(ExpenseImportByTripService.class);
	    }
	    return expenseImportByTripService;
	}

	public CreditCardAgencyService getCreditCardAgencyService() {
	    if (creditCardAgencyService == null) {
	        creditCardAgencyService = SpringContext.getBean(CreditCardAgencyService.class);
	    }
	    return creditCardAgencyService;
	}

    public DocumentService getDocumentService() {
        if (documentService == null) {
            documentService = SpringContext.getBean(DocumentService.class);
        }
        return documentService;
    }

	@Override
    public DataDictionaryService getDataDictionaryService() {
        if (dataDictionaryService == null) {
            dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        }
        return dataDictionaryService;
	}

	public IdentityService getIdentityService() {
        if (identityService == null) {
            identityService = SpringContext.getBean(IdentityService.class);
        }
        return identityService;
	}

	public NoteService getNoteService() {
        if (noteService == null) {
            noteService = SpringContext.getBean(NoteService.class);
        }
        return noteService;
	}

}
