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
package org.kuali.kfs.module.tem.service.impl;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.fp.businessobject.TravelExpenseTypeCode;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.AgencyStagingDataErrorCodes;
import org.kuali.kfs.module.tem.TemConstants.CreditCardStagingDataErrorCodes;
import org.kuali.kfs.module.tem.TemConstants.ExpenseImportTypes;
import org.kuali.kfs.module.tem.TemConstants.ExpenseType;
import org.kuali.kfs.module.tem.TemConstants.ReconciledCodes;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.businessobject.CreditCardAgency;
import org.kuali.kfs.module.tem.businessobject.CreditCardStagingData;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.OtherExpense;
import org.kuali.kfs.module.tem.businessobject.TEMExpense;
import org.kuali.kfs.module.tem.businessobject.TemTravelExpenseTypeCode;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.web.struts.TravelFormBase;
import org.kuali.kfs.module.tem.service.TEMExpenseService;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Service for handling travel expenses
 *
 * @see org.kuali.kfs.module.tem.document.validation.impl.TravelAgencyAuditValidation
 */
public class TravelExpenseServiceImpl implements TravelExpenseService {

    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;

    @Override
    public TemTravelExpenseTypeCode getExpenseType(String expense, String documentType, String tripType, String travelerType) {
        Map<String,String> criteria = new HashMap<String,String>();
        criteria.put(KFSPropertyConstants.CODE, expense);

        // defaults
        if (tripType == null) {
            tripType = TemConstants.BLANKET_IN_STATE;
        }

        if (travelerType == null) {
            travelerType = TemConstants.EMP_TRAVELER_TYP_CD;
        }

        if (documentType.equals(TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_AMEND_DOCUMENT) || documentType.equals(TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_CLOSE_DOCUMENT)) {
            criteria.put(KFSPropertyConstants.DOCUMENT_TYPE, TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT);
        }
        else{
            criteria.put(KFSPropertyConstants.DOCUMENT_TYPE, documentType);
        }

        criteria.put(TemPropertyConstants.TRIP_TYPE, tripType);
        criteria.put(TemPropertyConstants.TRAVELER_TYPE, travelerType);

        List<TemTravelExpenseTypeCode> expenseType = (List<TemTravelExpenseTypeCode>) getBusinessObjectService().findMatching(TemTravelExpenseTypeCode.class, criteria);
        if (ObjectUtils.isNotNull(expenseType) && !expenseType.isEmpty()) {
            return expenseType.get(0);
        }

        return null;
    }

    @Override
    public Long getExpenseTypeId(String travelExpenseCode, String documentNumber) {
        TravelFormBase form = (TravelFormBase) KNSGlobalVariables.getKualiForm();
        TravelDocument travelDocument = form != null ? form.getTravelDocument() : null;

        if (travelDocument == null && documentNumber != null) {
            try {
                travelDocument = (TravelDocument) getDocumentService().getByDocumentHeaderId(documentNumber);
            }
            catch (WorkflowException ex) {
                ex.printStackTrace();
            }
        }

        if (travelExpenseCode != null && travelDocument != null) {
            if (travelDocument != null && travelDocument.getTraveler() != null) {
                TemTravelExpenseTypeCode temTravelExpenseTypeCode = getExpenseType(travelExpenseCode, travelDocument.getFinancialDocumentTypeCode(), travelDocument.getTripTypeCode(), travelDocument.getTraveler().getTravelerTypeCode());

                return temTravelExpenseTypeCode.getTravelExpenseTypeCodeId();
            }
        }

        return null;
    }

    @Override
    public TemTravelExpenseTypeCode getExpenseType(Long travelExpenseTypeCodeId) {
        Map<String,String> criteria = new HashMap<String,String>();
        criteria.put("travelExpenseTypeCodeId", travelExpenseTypeCodeId.toString());

        List<TemTravelExpenseTypeCode> expenseType = (List<TemTravelExpenseTypeCode>) getBusinessObjectService().findMatching(TemTravelExpenseTypeCode.class, criteria);
        if (ObjectUtils.isNotNull(expenseType) && !expenseType.isEmpty()) {
            return expenseType.get(0);
        }

        return null;
    }

    @Override
    public HistoricalTravelExpense createHistoricalTravelExpense(AgencyStagingData agency) {

        HistoricalTravelExpense expense = new HistoricalTravelExpense();

        try {
            expense.setImportDate(dateTimeService.convertToSqlDate(agency.getProcessingTimestamp()));
        }
        catch (ParseException e) {
            throw new RuntimeException("Unable to convert timestamp to date " + e.getMessage());
        }

        CreditCardAgency ccAgency =agency.getCreditCardAgency();
        expense.setCreditCardAgencyId(ccAgency.getId());
        expense.setCreditCardAgency(ccAgency);
        expense.setCreditCardOrAgencyCode(ccAgency.getCreditCardOrAgencyCode());
        expense.setTravelExpenseType(agency.getExpenseType());
        expense.setTravelCompany(agency.getMerchantName());
        expense.setAmount(agency.getTripExpenseAmount());
        expense.setTransactionPostingDate(agency.getTransactionPostingDate());
        expense.setAgencyStagingDataId(agency.getId());
        expense.setTripId(agency.getTripId());
        expense.setProfileId(agency.getTemProfileId());

        expense.setReconciled(ReconciledCodes.UNRECONCILED);
        expense.setReconciliationDate(dateTimeService.getCurrentSqlDate());

        // Imports by Traveler don't have access to TemTravelExpenseType due to not having a doc type or trip type, so use TravelExpenseTypeCode.
        if (agency.getImportBy().equals(ExpenseImportTypes.IMPORT_BY_TRAVELLER)) {
            TravelExpenseTypeCode travelExpense = getTravelExpenseTypeCode(expense.getTravelExpenseType());
            if (ObjectUtils.isNotNull(travelExpense)) {
                expense.setTravelExpenseTypeString(travelExpense.getName());
            }
        }

        return expense;
    }

    /**
     *
     * This method returns a {@link TravelExpenseTypeCode}. This is needed to convert from the code to the name for imports by traveler.
     * @param expenseType
     * @return
     */
    protected TravelExpenseTypeCode getTravelExpenseTypeCode(String expenseType) {
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put(KFSPropertyConstants.CODE, expenseType);
        return getBusinessObjectService().findByPrimaryKey(TravelExpenseTypeCode.class, primaryKeys);
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TravelExpenseService#createHistoricalTravelExpense(org.kuali.kfs.module.tem.businessobject.CreditCardStagingData)
     */
    @Override
    public HistoricalTravelExpense createHistoricalTravelExpense(CreditCardStagingData creditCard) {
        HistoricalTravelExpense expense = new HistoricalTravelExpense();

        try {
            expense.setImportDate(dateTimeService.convertToSqlDate(creditCard.getProcessingTimestamp()));
        }
        catch (ParseException e) {
            throw new RuntimeException("Unable to convert timestamp to date " + e.getMessage());
        }

        CreditCardAgency ccAgency = creditCard.getCreditCardAgency();
        expense.setCreditCardAgencyId(ccAgency.getId());
        expense.setCreditCardOrAgencyCode(ccAgency.getCreditCardOrAgencyCode());

        expense.setTravelExpenseType(creditCard.getExpenseTypeCode());
        expense.setDescription(creditCard.getExpenseTypeCode());

        expense.setTravelCompany(creditCard.getMerchantName());
        expense.setAmount(creditCard.getTransactionAmount());
        expense.setTransactionPostingDate(creditCard.getTransactionDate());
        expense.setCreditCardStagingDataId(creditCard.getId());
        expense.setProfileId(creditCard.getTemProfileId());
        expense.setLocation(creditCard.getLocation());

        expense.setReconciled(ReconciledCodes.UNRECONCILED);
        expense.setReconciliationDate(dateTimeService.getCurrentSqlDate());

        // Imports by Traveler don't have access to TemTravelExpenseType due to not having a doc type or trip type, so use TravelExpenseTypeCode.
        if (creditCard.getImportBy().equals(ExpenseImportTypes.IMPORT_BY_TRAVELLER)) {
            TravelExpenseTypeCode travelExpense = getTravelExpenseTypeCode(expense.getTravelExpenseType());
            if (ObjectUtils.isNotNull(travelExpense)) {
                expense.setTravelExpenseTypeString(travelExpense.getName());
            }
        }

        return expense;
    }

    @Override
    public HistoricalTravelExpense createHistoricalTravelExpense(AgencyStagingData agency, CreditCardStagingData creditCard, TemTravelExpenseTypeCode travelExpenseType) {
        HistoricalTravelExpense expense = createHistoricalTravelExpense(agency);
        expense.setLocation(creditCard.getLocation());
        expense.setCreditCardStagingDataId(creditCard.getId());
        expense.setReconciled(ReconciledCodes.AUTO_RECONCILED);
        expense.setTravelExpenseTypeString(travelExpenseType.getName());
        return expense;
    }

    @Override
    public List<AgencyStagingData> retrieveValidAgencyData() {
        Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put(TemPropertyConstants.AGENCY_ERROR_CODE, AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);
        List<AgencyStagingData> agencyData = (List<AgencyStagingData>) getBusinessObjectService().findMatching(AgencyStagingData.class, criteria);
        return agencyData;
    }

    @Override
    public List<AgencyStagingData> retrieveValidAgencyDataByImportType(String importBy) {
        Map<String,String> criteria = new HashMap<String,String>(2);
        criteria.put(TemPropertyConstants.AGENCY_ERROR_CODE, AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);
        criteria.put(TemPropertyConstants.IMPORT_BY, importBy);
        List<AgencyStagingData> agencyData = (List<AgencyStagingData>) getBusinessObjectService().findMatching(AgencyStagingData.class, criteria);
        return agencyData;
    }

    @Override
    public List<CreditCardStagingData> retrieveValidCreditCardData() {
        Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put(TemPropertyConstants.AGENCY_ERROR_CODE, CreditCardStagingDataErrorCodes.CREDIT_CARD_NO_ERROR);
        criteria.put(TemPropertyConstants.IMPORT_BY, ExpenseImportTypes.IMPORT_BY_TRAVELLER);
        List<CreditCardStagingData> creditCardData = (List<CreditCardStagingData>) getBusinessObjectService().findMatching(CreditCardStagingData.class, criteria);
        return creditCardData;
    }

    @Override
    public CreditCardStagingData findImportedCreditCardExpense(KualiDecimal amount, String itineraryNumber) {
        Map<String,Object> criteria = new HashMap<String,Object>(3);
        criteria.put(TemPropertyConstants.TRANSACTION_AMOUNT, amount);
        criteria.put(TemPropertyConstants.ITINERARY_NUMBER, itineraryNumber);
        criteria.put(TemPropertyConstants.AGENCY_ERROR_CODE, CreditCardStagingDataErrorCodes.CREDIT_CARD_NO_ERROR);
        List<CreditCardStagingData> creditCardData = (List<CreditCardStagingData>) getBusinessObjectService().findMatching(CreditCardStagingData.class, criteria);
        if (ObjectUtils.isNotNull(creditCardData) && creditCardData.size() > 0) {
            return creditCardData.get(0);
        }
        return null;
    }

    @Override
    public CreditCardStagingData findImportedCreditCardExpense(KualiDecimal amount, String ticketNumber, String serviceFeeNumber) {
        Map<String,Object> criteria = new HashMap<String,Object>(4);
        criteria.put(TemPropertyConstants.TRANSACTION_AMOUNT, amount);
        criteria.put(TemPropertyConstants.TICKET_NUMBER, ticketNumber);
        criteria.put(TemPropertyConstants.SERVICE_FEE_NUMBER, serviceFeeNumber);
        criteria.put(TemPropertyConstants.AGENCY_ERROR_CODE, CreditCardStagingDataErrorCodes.CREDIT_CARD_NO_ERROR);
        List<CreditCardStagingData> creditCardData = (List<CreditCardStagingData>) getBusinessObjectService().findMatching(CreditCardStagingData.class, criteria);
        if (ObjectUtils.isNotNull(creditCardData) && creditCardData.size() > 0) {
            return creditCardData.get(0);
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TravelExpenseService#getExpenseServiceByType(org.kuali.kfs.module.tem.TemConstants.ExpenseType)
     */
    @Override
    public TEMExpenseService getExpenseServiceByType(ExpenseType expenseType){
        return (TEMExpenseService) SpringContext.getBean(TEMExpense.class, expenseType.service);
    }

    /**
     * Gets the businessObjectService attribute.
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the dateTimeService attribute.
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    private DocumentService getDocumentService() {
        return SpringContext.getBean(DocumentService.class);
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TravelExpenseService#isTravelExpenseExceedReceiptRequirementThreshold(org.kuali.kfs.module.tem.businessobject.OtherExpense)
     */
    @Override
    public boolean isTravelExpenseExceedReceiptRequirementThreshold(OtherExpense expense) {
        boolean isExceed = false;

        final TemTravelExpenseTypeCode expenseTypeCode = expense.getTravelExpenseTypeCode();

        if (expenseTypeCode.getReceiptRequired() != null && expenseTypeCode.getReceiptRequired()) {
          //check for the threshold amount
            if (expenseTypeCode.getReceiptRequirementThreshold() != null){
                KualiDecimal threshold = new KualiDecimal(expenseTypeCode.getReceiptRequirementThreshold());
                isExceed = threshold.isLessThan(expense.getExpenseAmount());
            }else{
                isExceed = true;
            }
        }
        return isExceed;
    }
}
