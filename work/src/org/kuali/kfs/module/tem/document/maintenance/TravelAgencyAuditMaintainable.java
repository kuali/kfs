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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants.AgencyStagingDataErrorCodes;
import org.kuali.kfs.module.tem.TemConstants.ExpenseImportTypes;
import org.kuali.kfs.module.tem.batch.service.AgencyDataImportService;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.businessobject.CreditCardAgency;
import org.kuali.kfs.module.tem.service.CreditCardAgencyService;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.KRADServiceLocator;

/**
 * Maintainable instance for the travel agency audit maintenance document
 *
 */
public class TravelAgencyAuditMaintainable extends FinancialSystemMaintainable {

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
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterCopy(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
     */
    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String,String[]> parameters) {
        super.processAfterCopy(document, parameters);
        AgencyStagingData agencyData = (AgencyStagingData) getBusinessObject();
        agencyData.setManualCreated(true);

        TravelAgencyAuditMaintainable oldMaintainable = (TravelAgencyAuditMaintainable)document.getOldMaintainableObject();
        //this is not new, so it must be for copy - we will set the Copied From Id
        agencyData.setCopiedFromId(((AgencyStagingData)oldMaintainable.getBusinessObject()).getId());
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#doRouteStatusChange(org.kuali.rice.kns.bo.DocumentHeader)
     */
    @Override
    public void doRouteStatusChange(DocumentHeader documentHeader) {
        super.doRouteStatusChange(documentHeader);
        if (documentHeader.getWorkflowDocument().isFinal()){
            AgencyStagingData agencyStaging  = (AgencyStagingData) getBusinessObject();

            //get the updated AgencyStagingData from DB
            AgencyStagingData updateAgencyStaging = getBusinessObjectService().findBySinglePrimaryKey(AgencyStagingData.class, agencyStaging.getId());
            updateCreditCardAgency(updateAgencyStaging);
            //after fixing the agency audit record, attempt to move agency data to historical table
            AgencyDataImportService importService = SpringContext.getBean(AgencyDataImportService.class);
            importService.processAgencyStagingExpense(updateAgencyStaging, new GeneralLedgerPendingEntrySequenceHelper());

            //save the agency staging record after it is processed and moved to history
            getBusinessObjectService().save(updateAgencyStaging);
        }
    }

    /**
     *
     * @param agencyStaging
     */
    private void updateCreditCardAgency(AgencyStagingData agencyStaging){
        //update the agency name base on code if provided
        CreditCardAgencyService creditCardAgencyService = SpringContext.getBean(CreditCardAgencyService.class);
        CreditCardAgency agency = SpringContext.getBean(CreditCardAgencyService.class).getCreditCardAgencyByCode(agencyStaging.getCreditCardOrAgencyCode());
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
        updateCreditCardAgency((AgencyStagingData)document.getOldMaintainableObject().getBusinessObject());
        updateCreditCardAgency((AgencyStagingData)document.getNewMaintainableObject().getBusinessObject());
        super.processAfterEdit(document, parameters);
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#populateBusinessObject(java.util.Map, org.kuali.rice.kns.document.MaintenanceDocument, java.lang.String)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Map populateBusinessObject(Map<String, String> fieldValues, MaintenanceDocument maintenanceDocument, String methodToCall) {
      //populate maintenanceDocument with notes from the BO
        List<Note> documentNotes = maintenanceDocument.getNotes();
        if (documentNotes == null){
            documentNotes = new ArrayList<Note>();
        }else{
            documentNotes.clear();
        }

        List<Note> boNotes = new ArrayList<Note>();
        if (maintenanceDocument.getOldMaintainableObject().getBusinessObject().getObjectId() != null) {
            boNotes = KRADServiceLocator.getNoteService().getByRemoteObjectId(this.getBusinessObject().getObjectId());
        }
        documentNotes.addAll(boNotes);
        return super.populateBusinessObject(fieldValues, maintenanceDocument, methodToCall);
    }

	/**
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#saveBusinessObject()
	 */
	@Override
	public void saveBusinessObject() {
	    AgencyStagingData agencyStaging = (AgencyStagingData) getBusinessObject();
	    //since it is fixed an submitted, changing the status to OK
	    agencyStaging.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);

	    //if the object is manual created - we should set the system fields
	    if (agencyStaging.getManualCreated()){
	        // processingTimestamp
	        agencyStaging.setProcessingTimestamp(getDateTimeService().getCurrentTimestamp());
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

	public DateTimeService getDateTimeService(){
	    return SpringContext.getBean(DateTimeService.class);
	}

}
