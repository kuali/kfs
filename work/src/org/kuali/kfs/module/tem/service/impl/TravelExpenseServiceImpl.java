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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.TravelExpenseTypeCode;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemConstants.AgencyStagingDataErrorCodes;
import org.kuali.kfs.module.tem.TemConstants.CreditCardStagingDataErrorCodes;
import org.kuali.kfs.module.tem.TemConstants.ExpenseImportTypes;
import org.kuali.kfs.module.tem.TemConstants.ExpenseTypeMetaCategory;
import org.kuali.kfs.module.tem.TemConstants.ReconciledCodes;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.businessobject.CreditCardAgency;
import org.kuali.kfs.module.tem.businessobject.CreditCardStagingData;
import org.kuali.kfs.module.tem.businessobject.ExpenseType;
import org.kuali.kfs.module.tem.businessobject.ExpenseTypeObjectCode;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.OtherExpense;
import org.kuali.kfs.module.tem.businessobject.TemExpense;
import org.kuali.kfs.module.tem.dataaccess.ExpenseTypeObjectCodeDao;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.web.struts.TravelFormBase;
import org.kuali.kfs.module.tem.service.TemExpenseService;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for handling travel expenses
 *
 * @see org.kuali.kfs.module.tem.document.validation.impl.AgencyStagingDataValidation
 */
@Transactional
public class TravelExpenseServiceImpl implements TravelExpenseService {
    org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TravelExpenseServiceImpl.class);

    protected BusinessObjectService businessObjectService;
    protected DateTimeService dateTimeService;
    protected ExpenseTypeObjectCodeDao expenseTypeObjectCodeDao;
    protected DocumentHelperService documentHelperService;

    @Override
    public ExpenseTypeObjectCode getExpenseType(String expense, String documentType, String tripType, String travelerType) {
        if (StringUtils.isBlank(expense)) {
            throw new IllegalArgumentException("Expense Type Code cannot be blank");
        }

        final Set<String> parentDocumentTypes = getParentDocumentTypeNames(documentType);
        List<ExpenseTypeObjectCode> expenseTypeObjectCodes = getExpenseTypeObjectCodeDao().findMatchingExpenseTypeObjectCodes(expense, parentDocumentTypes, tripType, travelerType);
        if (expenseTypeObjectCodes == null || expenseTypeObjectCodes.isEmpty()) {
            return null;
        }
        Collections.sort(expenseTypeObjectCodes, getExpenseTypeObjectCodeComparator());
        final ExpenseTypeObjectCode chosenExpenseTypeObjectCode = expenseTypeObjectCodes.get(0);
        if (LOG.isDebugEnabled()) {
            LOG.debug("I choose you, "+chosenExpenseTypeObjectCode.toString());
        }
        return chosenExpenseTypeObjectCode;
    }

    /**
     * @return the comparator that will sort expense type object codes, such that the top one will be selected as the "most" correct
     */
    protected Comparator<ExpenseTypeObjectCode> getExpenseTypeObjectCodeComparator() {
        return new ExpenseTypeObjectCodeComparatorByHierarchyLogic();
    }

    /**
     * This wise child knows how to sort ExpenseTypeObjectCode records to find the one which should be most associated with a set of given conditions.  Basically, it sorts with more specific records being
     * higher ranked than less specific records.  It checks expense type code; then document type level (TAA is more specific than TRV, so should be higher); then traveler type (something specific ranks higher than ALL); and finally trip type (again, something specific ranks higher than ALL)
     */
    protected class ExpenseTypeObjectCodeComparatorByHierarchyLogic implements Comparator<ExpenseTypeObjectCode> {
        @Override
        public int compare(ExpenseTypeObjectCode dora, ExpenseTypeObjectCode nora) {
            if (!StringUtils.equals(dora.getExpenseTypeCode(), nora.getExpenseTypeCode())) {
                return dora.getExpenseTypeCode().compareTo(nora.getExpenseTypeCode());
            }
            if (!StringUtils.equals(dora.getDocumentTypeName(), nora.getDocumentTypeName())) {
                final int doraDocLevel = getLevelForDocumentType(dora.getDocumentTypeName());
                final int noraDocLevel = getLevelForDocumentType(nora.getDocumentTypeName());
                final int delta = doraDocLevel - noraDocLevel;
                return delta;
            }
            if (!StringUtils.equals(dora.getTravelerTypeCode(), nora.getTravelerTypeCode())) {
                if (TemConstants.ALL_EXPENSE_TYPE_OBJECT_CODE_TRAVELER_TYPE.equals(dora.getTravelerTypeCode())) {
                    return 1; // advantage: nora
                }
                if (TemConstants.ALL_EXPENSE_TYPE_OBJECT_CODE_TRAVELER_TYPE.equals(nora.getTravelerTypeCode())) {
                    return -1; // advantage: dora
                }
                // we're still here?  That's...weird (we shouldn't have both EMP and NON records).  Let's have trip type figure it out for us
            }
            if (!StringUtils.equals(dora.getTripTypeCode(), nora.getTripTypeCode())) {
                if (TemConstants.ALL_EXPENSE_TYPE_OBJECT_CODE_TRIP_TYPE.equals(dora.getTripTypeCode())) {
                    return 1; // advantage: nora
                }
                if (TemConstants.ALL_EXPENSE_TYPE_OBJECT_CODE_TRAVELER_TYPE.equals(nora.getTripTypeCode())) {
                    return -1; // advantage: dora
                }
                if (StringUtils.isBlank(dora.getTripTypeCode())) {
                    return 1; // advantage: nora.  Though how did we get here?
                }
                return dora.getTripTypeCode().compareTo(nora.getTripTypeCode()); // another place we shouldn't ever really get to
            }
            return 0;  // odd that we got here, but whichever
        }

        /**
         * Returns the branch level of the given document type
         * @param documentType the document type to find a branch level for
         * @return the branch level, with TT being high and doc types which descend from TT having lower levels
         */
        protected int getLevelForDocumentType(String documentType) {
            if (TemConstants.TravelDocTypes.TEM_TRANSACTIONAL_DOCUMENT.equals(documentType)) {
                return 3;
            } else if (TemConstants.TravelDocTypes.TRAVEL_TRANSACTIONAL_DOCUMENT.equals(documentType) || TemConstants.TravelDocTypes.TRAVEL_ENTERTAINMENT_DOCUMENT.equals(documentType) || TemConstants.TravelDocTypes.TRAVEL_RELOCATION_DOCUMENT.equals(documentType)) {
                return 2;
            } else if (TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT.equals(documentType) || TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT.equals(documentType)) {
                return 1;
            }
            return 0; // should be TAA and TAC only here
        }
    }

    @Override
    public ExpenseTypeObjectCode getExpenseTypeObjectCode(String travelExpenseCode, String documentNumber) {
        final TravelDocument document = retrieveTravelDocument(documentNumber);
        if (document != null) {
            final String documentType = document.getFinancialDocumentTypeCode();
            final String tripType = document.getTripTypeCode();
            final String travelerType = document.getTraveler().getTravelerTypeCode();
            return getExpenseType(travelExpenseCode, documentType, tripType, travelerType);
        }
        return null;
    }

    /**
     * Attempts to retrieve the TravelDocument in operation - first from the form, then by looking up via document service
     * @param documentNumber the document number of the document to attempt retrieval of
     * @return the retrieved document, or null if retrieval was unsucessful
     */
    protected TravelDocument retrieveTravelDocument(String documentNumber) {
        // first, we'll look in the form
        KualiForm form = KNSGlobalVariables.getKualiForm();
        if (form instanceof TravelFormBase) {
            final TravelDocument document = ((TravelFormBase)form).getTravelDocument();
            if (!StringUtils.isBlank(document.getDocumentNumber()) && document.getDocumentNumber().equals(documentNumber)) {
                return document;
            }
        }
        // still here?  Let's look it up
        try {
            final TravelDocument document = (TravelDocument)getDocumentService().getByDocumentHeaderIdSessionless(documentNumber);
            return document;
        }
        catch (WorkflowException we) {
            throw new RuntimeException("Could not retrieve document "+documentNumber, we);
        }
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.service.TravelExpenseService#getExpenseTypesForDocument(java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    @Override
    public List<ExpenseType> getExpenseTypesForDocument(String documentTypeName, String tripType, String travelerType, boolean groupOnly) {
        final Set<String> documentTypesForSearch = getParentDocumentTypeNames(documentTypeName);
        final List<ExpenseTypeObjectCode> expenseTypeObjectCodes = getExpenseTypeObjectCodeDao().findMatchingExpenseTypesObjectCodes(documentTypesForSearch, tripType, travelerType);
        Set<String> expenseTypeCodes = new HashSet<String>();
        List<ExpenseType> expenseTypes = new ArrayList<ExpenseType>();

        for (ExpenseTypeObjectCode expenseTypeObjectCode : expenseTypeObjectCodes) {
            if (!expenseTypeCodes.contains(expenseTypeObjectCode.getExpenseTypeCode())) {
                if (!groupOnly || (groupOnly && expenseTypeObjectCode.getExpenseType().isGroupTravel())) {
                    expenseTypeObjectCode.refreshReferenceObject(TemPropertyConstants.EXPENSE_TYPE);
                    expenseTypes.add(expenseTypeObjectCode.getExpenseType());
                    expenseTypeCodes.add(expenseTypeObjectCode.getExpenseTypeCode());
                }
            }
        }
        return expenseTypes;
    }

    /**
     * Comparator which compares expense types by their code
     */
    protected class ExpenseTypeComparatorByCode implements Comparator<ExpenseType> {
        @Override
        public int compare(ExpenseType lava, ExpenseType kusha) {
            if (StringUtils.equals(lava.getCode(), kusha.getCode())) {
                return 0;
            } else if (lava.getCode() == null && !StringUtils.isBlank(kusha.getCode())) {
                return 1;
            } else {
                return lava.getCode().compareTo(kusha.getCode());
            }
        }

    }

    /**
     * Retrieves the parent document type names - up to "TT" - for the document type
     * @param documentTypeName the document type to find the ancestry of
     * @return the document type names, including TT and the given document type
     */
    protected Set<String> getParentDocumentTypeNames(String documentTypeName) {
        // hard code for now until we can build the actual branch
        Set<String> docTypes = new HashSet<String>();
        docTypes.add(TemConstants.TravelDocTypes.TEM_TRANSACTIONAL_DOCUMENT);
        if (TemConstants.TravelDocTypes.getAuthorizationDocTypes().contains(documentTypeName)) {
            docTypes.add(TemConstants.TravelDocTypes.TRAVEL_TRANSACTIONAL_DOCUMENT);
            docTypes.add(TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT);
        } else if (TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT.equals(documentTypeName)) {
            docTypes.add(TemConstants.TravelDocTypes.TRAVEL_TRANSACTIONAL_DOCUMENT);
            docTypes.add(TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT);
        } else {
            docTypes.add(documentTypeName);
        }
        return docTypes;
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
        final ExpenseType expenseType = getDefaultExpenseTypeForCategory(agency.getExpenseTypeCategory());
        if (expenseType != null) {
            expense.setTravelExpenseType(expenseType.getCode());
        }
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
    public HistoricalTravelExpense createHistoricalTravelExpense(AgencyStagingData agency, CreditCardStagingData creditCard, ExpenseTypeObjectCode travelExpenseType) {
        HistoricalTravelExpense expense = createHistoricalTravelExpense(agency);
        expense.setLocation(creditCard.getLocation());
        expense.setCreditCardStagingDataId(creditCard.getId());
        expense.setReconciled(ReconciledCodes.AUTO_RECONCILED);
        expense.setTravelExpenseTypeString(travelExpenseType.getExpenseType().getName());
        return expense;
    }

    @Override
    public List<AgencyStagingData> retrieveValidAgencyData() {
        Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put(TemPropertyConstants.AGENCY_ERROR_CODE, AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);
        criteria.put(TemPropertyConstants.ACTIVE_IND, TemConstants.YES);
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
     * This method should only be called when adding an actual expense or detail
     * @see org.kuali.kfs.module.tem.service.TravelExpenseService#updateTaxabilityOfActualExpense(org.kuali.kfs.module.tem.businessobject.ActualExpense, org.kuali.kfs.module.tem.document.TravelDocument, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public void updateTaxabilityOfActualExpense(ActualExpense actualExpense, TravelDocument document, Person currentUser) {
        if (!ObjectUtils.isNull(actualExpense.getExpenseTypeObjectCode())) {  // don't have an expense type object code? then why are we bothering?
            // 1. if the given user cannot change the taxability then we should actually look at it
            Map<String, String> permissionDetails = new HashMap<String, String>();
            permissionDetails.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, document.getDocumentTypeName());
            permissionDetails.put(KimConstants.AttributeConstants.EDIT_MODE, TemConstants.EditModes.ACTUAL_EXPENSE_TAXABLE_MODE);
            final boolean canEditTaxable = getDocumentHelperService().getDocumentAuthorizer(document).isAuthorizedByTemplate(document, KRADConstants.KNS_NAMESPACE, KimConstants.PermissionTemplateNames.USE_TRANSACTIONAL_DOCUMENT, currentUser.getPrincipalId(), permissionDetails, Collections.<String, String>emptyMap());
            if (!canEditTaxable) {
                actualExpense.setTaxable(actualExpense.getExpenseTypeObjectCode().isTaxable());
            }
        }
    }

    /**
     * Does BusinessObjectService lookup - pretty simple
     * @see org.kuali.kfs.module.tem.service.TravelExpenseService#getDefaultExpenseTypeForCategory(org.kuali.kfs.module.tem.TemConstants.ExpenseTypeMetaCategory)
     */
    @Override
    public ExpenseType getDefaultExpenseTypeForCategory(ExpenseTypeMetaCategory category) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(TemPropertyConstants.EXPENSE_TYPE_META_CATEGORY_CODE, category.getCode());
        fieldValues.put(TemPropertyConstants.CATEGORY_DEFAULT, Boolean.TRUE);
        fieldValues.put(KFSPropertyConstants.ACTIVE, Boolean.TRUE);

        Collection<ExpenseType> expenseTypes = getBusinessObjectService().findMatching(ExpenseType.class, fieldValues);
        if (expenseTypes == null || expenseTypes.isEmpty()) {
            return null;
        }
        Iterator<ExpenseType> expenseTypesIterator = expenseTypes.iterator();
        ExpenseType returnValue = expenseTypesIterator.next();
        while (expenseTypesIterator.hasNext()) {
            expenseTypesIterator.next(); // exhaust result set - which already *should* be exhausted but just in case
        }
        return returnValue;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TravelExpenseService#getExpenseServiceByType(org.kuali.kfs.module.tem.TemConstants.ExpenseType)
     */
    @Override
    public TemExpenseService getExpenseServiceByType(TemConstants.ExpenseType expenseType){
        return (TemExpenseService) SpringContext.getBean(TemExpense.class, expenseType.service);
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

    public ExpenseTypeObjectCodeDao getExpenseTypeObjectCodeDao() {
        return expenseTypeObjectCodeDao;
    }

    public void setExpenseTypeObjectCodeDao(ExpenseTypeObjectCodeDao expenseTypeObjectCodeDao) {
        this.expenseTypeObjectCodeDao = expenseTypeObjectCodeDao;
    }

    public DocumentHelperService getDocumentHelperService() {
        return documentHelperService;
    }

    public void setDocumentHelperService(DocumentHelperService documentHelperService) {
        this.documentHelperService = documentHelperService;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TravelExpenseService#isTravelExpenseExceedReceiptRequirementThreshold(org.kuali.kfs.module.tem.businessobject.OtherExpense)
     */
    @Override
    public boolean isTravelExpenseExceedReceiptRequirementThreshold(OtherExpense expense) {
        boolean isExceed = false;

        final ExpenseTypeObjectCode expenseTypeCode = expense.getExpenseTypeObjectCode();

        if (expenseTypeCode.isReceiptRequired()) {
          //check for the threshold amount
            if (expenseTypeCode.getReceiptRequirementThreshold() != null){
                KualiDecimal threshold = expenseTypeCode.getReceiptRequirementThreshold();
                isExceed = threshold.isLessThan(expense.getExpenseAmount());
            }else{
                isExceed = true;
            }
        }
        return isExceed;
    }

}
