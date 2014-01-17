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

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Maintainable instance for the travel agency audit maintenance document
 *
 */
public class AgencyStagingDataMaintainable extends FinancialSystemMaintainable {
    private static final Logger LOG = Logger.getLogger(AgencyStagingDataMaintainable.class);
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
        super.doRouteStatusChange(documentHeader);
        if (documentHeader.getWorkflowDocument().isProcessed()){
            AgencyStagingData agencyStaging  = (AgencyStagingData) getBusinessObject();

            //get the updated AgencyStagingData from DB
            AgencyStagingData updateAgencyStaging = getBusinessObjectService().findBySinglePrimaryKey(AgencyStagingData.class, agencyStaging.getId());
            updateCreditCardAgency(updateAgencyStaging);
            //after fixing the agency audit record, attempt to move agency data to historical table
            AgencyDataImportService importService = SpringContext.getBean(AgencyDataImportService.class);
            boolean result = importService.processAgencyStagingExpense(updateAgencyStaging, new GeneralLedgerPendingEntrySequenceHelper());
            LOG.info("Agency Data Id: "+ updateAgencyStaging.getId() + (result ? " was":" was not") +" processed.");

            //save the agency staging record after it is processed and moved to history
            //getBusinessObjectService().save(updateAgencyStaging);
        }
    }

    /**
     *
     * @param agencyStaging
     */
    private void updateCreditCardAgency(AgencyStagingData agencyStaging){
        //update the agency name base on code if provided
        CreditCardAgencyService creditCardAgencyService = SpringContext.getBean(CreditCardAgencyService.class);
        CreditCardAgency agency = creditCardAgencyService.getCreditCardAgencyByCode(agencyStaging.getCreditCardOrAgencyCode());
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
           ExpenseImportByTripService expenseImportByTripService =   SpringContext.getBean(ExpenseImportByTripService.class);
           errorMessages = expenseImportByTripService.validateAgencyData(agency);
        }
        else if (TemConstants.ExpenseImportTypes.IMPORT_BY_TRAVELLER.equals(agency.getImportBy())) {
            ExpenseImportByTravelerService expenseImportByTravelerService =  SpringContext.getBean(ExpenseImportByTravelerService.class);
            errorMessages = expenseImportByTravelerService.validateAgencyData(agency);
        }

       MessageMap messageMap = GlobalVariables.getMessageMap();
       for(ErrorMessage message : errorMessages ){
           messageMap.putError(KFSConstants.GLOBAL_ERRORS, message.getErrorKey(), message.getMessageParameters());
       }

        updateCreditCardAgency((AgencyStagingData)document.getNewMaintainableObject().getBusinessObject());

        super.processAfterEdit(document, parameters);
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#populateBusinessObject(java.util.Map, org.kuali.rice.kns.document.MaintenanceDocument, java.lang.String)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Map populateBusinessObject(Map<String, String> fieldValues, MaintenanceDocument maintenanceDocument, String methodToCall) {
         return super.populateBusinessObject(fieldValues, maintenanceDocument, methodToCall);
    }

	/**
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#saveBusinessObject()
	 */
	@Override
	public void saveBusinessObject() {
	    AgencyStagingData agencyStaging = (AgencyStagingData) getBusinessObject();

	    if (agencyStaging.isActive()) {
    	    //since it is fixed and submitted, changing the status to OK
    	    agencyStaging.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);

	        agencyStaging.setProcessingTimestamp(getDateTimeService().getCurrentTimestamp());

	        if (ObjectUtils.isNull(agencyStaging.getCreationTimestamp())) {
	            agencyStaging.setCreationTimestamp(getDateTimeService().getCurrentTimestamp());
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

	public DateTimeService getDateTimeService(){
	    return SpringContext.getBean(DateTimeService.class);
	}

}
