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

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoice;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters;
import org.kuali.kfs.module.tem.batch.TaxableRamificationNotificationStep;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.document.TaxableRamificationDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.service.TaxableRamificationDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * implement the service calls and operations on tax ramification document
 */
public class TaxableRamificationDocumentServiceImpl implements TaxableRamificationDocumentService {
    public static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TaxableRamificationDocumentServiceImpl.class);

    private DocumentService documentService;
    private ParameterService parameterService;
    private BusinessObjectService businessObjectService;
    private TravelDocumentService travelDocumentService;
    private AccountsReceivableModuleService accountsReceivableModuleService;

    /**
     * @see org.kuali.kfs.module.tem.service.TaxableRamificationDocumentService#getAllQualifiedOutstandingTravelAdvance()
     */
    @Override
    public List<TravelAdvance> getAllQualifiedOutstandingTravelAdvance() {
        List<TravelAdvance> qualifiedOutstandingTravelAdvance = new ArrayList<TravelAdvance>();

        List<String> customerTypeCodes = this.getTravelerCustomerTypes();
        Integer customerInvoiceAge = this.getNotificationOnDays();

        Date lastTaxableRamificationNotificationDate = this.getLastTaxableRamificationNotificationDate();
        Map<String, KualiDecimal> invoiceOpenAmountMap = this.getAccountsReceivableModuleService().getCustomerInvoiceOpenAmount(customerTypeCodes, customerInvoiceAge, lastTaxableRamificationNotificationDate);
        Set<String> arInvoiceDocNumbers = invoiceOpenAmountMap.keySet();

        if (ObjectUtils.isNotNull(arInvoiceDocNumbers) && !arInvoiceDocNumbers.isEmpty()) {
            qualifiedOutstandingTravelAdvance = this.getTravelDocumentService().getOutstandingTravelAdvanceByInvoice(arInvoiceDocNumbers);
        }

        return qualifiedOutstandingTravelAdvance;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TaxRamificationDocumentService#blanketApproveTaxRamificationDocument(org.kuali.kfs.module.tem.document.TaxRamificationDocument)
     */
    @Override
    @Transactional
    public void blanketApproveRamificationDocument(TaxableRamificationDocument taxRamificationDocument) {
        try {
            this.addAdHocRoutePersons(taxRamificationDocument);

            this.getDocumentService().blanketApproveDocument(taxRamificationDocument, KFSConstants.EMPTY_STRING, taxRamificationDocument.getAdHocRoutePersons());
        }
        catch (WorkflowException we) {
            LOG.error("Failed to blanket approve the given tax ramification document. ", we);
            throw new RuntimeException(we);
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TaxRamificationDocumentService#createAndBlanketApproveTaxRamificationDocument(org.kuali.kfs.module.tem.businessobject.TravelAdvance)
     */
    @Override
    @Transactional
    public TaxableRamificationDocument createAndBlanketApproveRamificationDocument(TravelAdvance travelAdvance) {
        if (this.hasTaxableRamification(travelAdvance)) {
            throw new RuntimeException("There exists a tax ramification document created from the given travel advance. " + travelAdvance);
        }

        TaxableRamificationDocument taxRamificationDocument = this.createRamificationDocument(travelAdvance);

        if (ObjectUtils.isNotNull(taxRamificationDocument)) {
            this.blanketApproveRamificationDocument(taxRamificationDocument);
        }

        return taxRamificationDocument;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TaxRamificationDocumentService#hasTaxRamification(org.kuali.kfs.module.tem.businessobject.TravelAdvance)
     */
    @Override
    public boolean hasTaxableRamification(TravelAdvance travelAdvance) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(TemPropertyConstants.TRAVEL_ADVANCE_ID, travelAdvance.getId());

        int count = this.getBusinessObjectService().countMatching(TaxableRamificationDocument.class, fieldValues);

        return count > 0;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TaxRamificationDocumentService#createTaxRamificationDocument(org.kuali.kfs.module.tem.businessobject.TravelAdvance)
     */
    @Override
    public TaxableRamificationDocument createRamificationDocument(TravelAdvance travelAdvance) {
        try {
            TaxableRamificationDocument taxRamificationDocument = (TaxableRamificationDocument) this.getDocumentService().getNewDocument(TaxableRamificationDocument.class);

            this.populateTaxRamificationDocument(taxRamificationDocument, travelAdvance);

            return taxRamificationDocument;
        }
        catch (WorkflowException we) {
            LOG.error(we);
            throw new RuntimeException(we);
        }
    }

    /**
     * add adhoc recipients to the given taxable ramification document
     */
    protected void addAdHocRoutePersons(TaxableRamificationDocument taxableRamificationDocument) {
        Set<String> adHocRecipientPrincipalIds = this.getAdhocRecipientsPrincipalIds(taxableRamificationDocument);
        for (String principalId : adHocRecipientPrincipalIds) {
            this.getTravelDocumentService().addAdHocFYIRecipient(taxableRamificationDocument, principalId);
        }
    }

    /**
     * collect all adhoc recipients of the given tax ramification document. They may be the traveler and fiscal officer
     */
    protected Set<String> getAdhocRecipientsPrincipalIds(TaxableRamificationDocument taxRamificationDocument) {
        Set<String> adHocRecipients = new HashSet<String>();

        if (this.willSendNotificationToFiscalOfficer()) {
            Account account = taxRamificationDocument.getTravelAdvance().getAcct();
            String fiscalOfficerPrincipalId = account.getAccountFiscalOfficerUser().getPrincipalId();

            if (StringUtils.isNotEmpty(fiscalOfficerPrincipalId)) {
                adHocRecipients.add(fiscalOfficerPrincipalId);
            }
        }

        TravelerDetail traveler = taxRamificationDocument.getTravelerDetail();
        String travelerPrincipalId = traveler.getPrincipalId();
        if (StringUtils.isNotEmpty(travelerPrincipalId)) {
            adHocRecipients.add(travelerPrincipalId);
        }

        return adHocRecipients;
    }

    /**
     * populate the given tax ramification document with the information provided by the given travel advance
     */
    protected void populateTaxRamificationDocument(TaxableRamificationDocument taxRamificationDocument, TravelAdvance travelAdvance) {
        taxRamificationDocument.setArInvoiceDocNumber(travelAdvance.getArInvoiceDocNumber());

        taxRamificationDocument.setTravelAdvanceId(travelAdvance.getId());
        taxRamificationDocument.setTravelAdvance(travelAdvance);

        TravelAuthorizationDocument travelAuthorizationDocument = this.getTravelAuthorizationDocument(travelAdvance);
        String travelDocumentIdentifier = travelAuthorizationDocument.getTravelDocumentIdentifier();
        taxRamificationDocument.setTravelDocumentIdentifier(travelDocumentIdentifier);

        TravelerDetail travelerDetail = travelAuthorizationDocument.getTraveler();
        this.refreshTraverler(travelerDetail);
        taxRamificationDocument.setTravelerDetailId(travelerDetail.getId());
        taxRamificationDocument.setTravelerDetail(travelerDetail);

        AccountsReceivableCustomerInvoice customerInvoice = this.getOpenCustomerInvoice(travelAdvance);
        taxRamificationDocument.setOpenAmount(customerInvoice.getOpenAmount());
        taxRamificationDocument.setInvoiceAmount(customerInvoice.getTotalDollarAmount());
        taxRamificationDocument.setDueDate(customerInvoice.getInvoiceDueDate());

        String taxRamificationNotice = this.getNotificationText();
        taxRamificationDocument.setTaxableRamificationNotice(taxRamificationNotice);

        taxRamificationDocument.getDocumentHeader().setOrganizationDocumentNumber(String.valueOf(travelDocumentIdentifier));

        String travelerPrincipalName = StringUtils.upperCase(travelerDetail.getPrincipalName());
        String description = this.getNotificationSubject() + travelerPrincipalName;
        taxRamificationDocument.getDocumentHeader().setDocumentDescription(StringUtils.left(description, KFSConstants.getMaxLengthOfDocumentDescription()));
    }

    /**
     * refresh the given traveler's information
     */
    protected void refreshTraverler(TravelerDetail travelerDetail) {
        if (ObjectUtils.isNull(travelerDetail)) {
            return;
        }

        String principalName = travelerDetail.getPrincipalName();
        if (StringUtils.isNotBlank(principalName)) {
            return;
        }

        String principalId = travelerDetail.getPrincipalId();
        Person person = SpringContext.getBean(PersonService.class).getPerson(principalId);

        if (ObjectUtils.isNotNull(person)) {
            travelerDetail.setPrincipalName(person.getPrincipalName());
        }
    }
    
    /**
     * get the last taxable ramification notification date
     */
    protected Date getLastTaxableRamificationNotificationDate() {
        return this.getTravelDocumentService().findLatestTaxableRamificationNotificationDate();
    }

    /**
     * get the travel authorization document associated with the given travel advance
     */
    protected TravelAuthorizationDocument getTravelAuthorizationDocument(TravelAdvance travelAdvance) {
        String travelAuthorizationDocumentNumber = travelAdvance.getDocumentNumber();

        return this.getBusinessObjectService().findBySinglePrimaryKey(TravelAuthorizationDocument.class, travelAuthorizationDocumentNumber);
    }

    /**
     * get the open customer invoice amount associated with the given travel advance
     */
    protected AccountsReceivableCustomerInvoice getOpenCustomerInvoice(TravelAdvance travelAdvance) {
        String customerInvoiceDocumentNumber = travelAdvance.getArInvoiceDocNumber();

        return this.getAccountsReceivableModuleService().getOpenCustomerInvoice(customerInvoiceDocumentNumber);
    }

    /**
     * get the notification text from an application parameter
     */
    protected String getNotificationText() {
        return this.getParameterService().getParameterValue(TaxableRamificationNotificationStep.class, TemConstants.TaxRamificationParameter.TAX_RAMIFICATION_NOTIFICATION_TEXT_PARAM_NAME);
    }

    /**
     * get the indicator from an application parameter, which instructs whether to send notification to Fiscal Officer
     */
    protected boolean willSendNotificationToFiscalOfficer() {
        return this.getParameterService().getIndicatorParameter(TaxableRamificationNotificationStep.class, TemConstants.TaxRamificationParameter.SEND_TAX_NOTIFICATION_TO_FISCAL_OFFICER_IND_PARAM_NAME);
    }

    /**
     * get the notification subject
     */
    protected String getNotificationSubject() {
        return "Notice for ";
    }

    /**
     * get the notification text from an application parameter
     */
    protected List<String> getTravelerCustomerTypes() {
        return this.getParameterService().getParameterValues(TravelAuthorizationDocument.class, TravelAuthorizationParameters.TRAVELER_AR_CUSTOMER_TYPE);
    }

    /**
     * get the timing of when the aging email notification should be sent
     */
    protected Integer getNotificationOnDays() {
        String daysAsString = this.getParameterService().getParameterValue(TaxableRamificationNotificationStep.class, TemConstants.TaxRamificationParameter.TAX_RAMIFICATION_NOTIFICATION_DAYS_PARAM_NAME);
        if (!StringUtils.isNumeric(daysAsString)) {
            return TemConstants.DEFAULT_NOTIFICATION_DAYS;
        }

        return Integer.parseInt(daysAsString);
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
     * Sets the documentService attribute value.
     * 
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Gets the parameterService attribute.
     * 
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
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
     * Gets the businessObjectService attribute.
     * 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the accountsReceivableModuleService attribute.
     * 
     * @return Returns the accountsReceivableModuleService.
     */
    public AccountsReceivableModuleService getAccountsReceivableModuleService() {
        return accountsReceivableModuleService;
    }

    /**
     * Sets the accountsReceivableModuleService attribute value.
     * 
     * @param accountsReceivableModuleService The accountsReceivableModuleService to set.
     */
    public void setAccountsReceivableModuleService(AccountsReceivableModuleService accountsReceivableModuleService) {
        this.accountsReceivableModuleService = accountsReceivableModuleService;
    }

    /**
     * Gets the travelDocumentService attribute.
     * 
     * @return Returns the travelDocumentService.
     */
    public TravelDocumentService getTravelDocumentService() {
        return travelDocumentService;
    }

    /**
     * Sets the travelDocumentService attribute value.
     * 
     * @param travelDocumentService The travelDocumentService to set.
     */
    public void setTravelDocumentService(TravelDocumentService travelDocumentService) {
        this.travelDocumentService = travelDocumentService;
    }
}
