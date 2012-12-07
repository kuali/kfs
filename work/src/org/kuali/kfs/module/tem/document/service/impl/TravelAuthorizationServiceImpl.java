/*
 * Copyright 2010 The Kuali Foundation
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
package org.kuali.kfs.module.tem.document.service.impl;

import static org.kuali.kfs.module.tem.TemKeyConstants.TA_MESSAGE_CLOSE_DOCUMENT_TEXT;
import static org.kuali.kfs.module.tem.TemPropertyConstants.TRVL_IDENTIFIER_PROPERTY;

import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerAddress;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoice;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoiceDetail;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoiceRecurrenceDetails;
import org.kuali.kfs.integration.ar.AccountsReceivableDocumentHeader;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.integration.ar.AccountsReceivableOrganizationOptions;
import org.kuali.kfs.integration.ar.AccountsReceivableSystemInformation;
import org.kuali.kfs.module.purap.util.PurApObjectUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TemTravelExpenseTypeCode;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.document.TravelAuthorizationAmendmentDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationCloseDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService;
import org.kuali.kfs.module.tem.document.service.TravelAuthorizationService;
import org.kuali.kfs.module.tem.document.service.TravelDisbursementService;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.module.tem.util.MessageUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.kfs.sys.document.validation.event.AddAccountingLineEvent;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.dao.DocumentDao;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KeyValuesService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;

public class TravelAuthorizationServiceImpl implements TravelAuthorizationService {

    protected static Logger LOG = Logger.getLogger(TravelAuthorizationServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private AccountsReceivableModuleService accountsReceivableModuleService;
    private ParameterService parameterService;
    private DocumentService documentService;
    private TravelDisbursementService travelDisbursementService;
    private DateTimeService dateTimeService;
    private KualiRuleService kualiRuleService;
    private WorkflowDocumentService workflowDocumentService;
    private UniversityDateService universityDateService;
    private AccountingDocumentRelationshipService accountingDocumentRelationshipService;
    private TemProfileService temProfileService;
    private TravelDocumentService travelDocumentService;
    private DocumentDao documentDao;

    private List<PropertyChangeListener> propertyChangeListeners;

    private final int currentYear = Calendar.getInstance().get(Calendar.YEAR);

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelAuthorizationService#createCustomerInvoice(org.kuali.kfs.module.tem.document.TravelAuthorizationDocument)
     */
    @Override
    public void createCustomerInvoice(TravelAuthorizationDocument travelAuthorizationDocument) {

        boolean enableInvoice = parameterService.getParameterValueAsBoolean(TemParameterConstants.TEM_AUTHORIZATION.class, TravelAuthorizationParameters.ENABLE_AR_INV_FOR_TRAVL_ADVANCE_IND);
        if (enableInvoice) {
            KualiDecimal amount = KualiDecimal.ZERO;
            List<TravelAdvance> advances = new ArrayList<TravelAdvance>();
            for (TravelAdvance adv : travelAuthorizationDocument.getTravelAdvances()) {
                if (StringUtils.isBlank(adv.getArInvoiceDocNumber()) && adv.getTravelAdvanceRequested().isGreaterThan(KualiDecimal.ZERO)) {
                    advances.add(adv);
                    amount = amount.add(adv.getTravelAdvanceRequested());
                }
            }
            if (amount.isGreaterThan(KualiDecimal.ZERO)) {
                createCustomerInvoiceFromAdvances(travelAuthorizationDocument, advances, amount);
            }
        }
    }

    /**
     * Create customer invoice from advance
     *
     * @param travelAuthorizationDocument
     * @param advances
     * @param amount
     */
    private void createCustomerInvoiceFromAdvances(TravelAuthorizationDocument travelAuthorizationDocument, List<TravelAdvance> advances, KualiDecimal amount) {

        int numDaysDue = Integer.parseInt(parameterService.getParameterValueAsString(TemParameterConstants.TEM_AUTHORIZATION.class, TravelAuthorizationParameters.NUMBER_OF_DAYS_DUE));
        String invoiceItemCode = parameterService.getParameterValueAsString(TemParameterConstants.TEM_AUTHORIZATION.class, TravelAuthorizationParameters.TRAVEL_ADVANCE_INVOICE_ITEM_CODE);
        String processingOrgCode = parameterService.getParameterValueAsString(TemParameterConstants.TEM_AUTHORIZATION.class, TravelAuthorizationParameters.TRAVEL_ADVANCE_BILLING_ORG_CODE);
        String processingChartCode = parameterService.getParameterValueAsString(TemParameterConstants.TEM_AUTHORIZATION.class, TravelAuthorizationParameters.TRAVEL_ADVANCE_BILLING_CHART_CODE);

        // store this so we can reset after we're finished
        UserSession originalUser = GlobalVariables.getUserSession();

        UserSession tempUser = new UserSession(KFSConstants.SYSTEM_USER);

        // setting to initiator
        GlobalVariables.setUserSession(tempUser);

        // need to refactor this so the customer id is stored on the doc, not in travel advances
        Calendar cal = Calendar.getInstance();
        String customerNumber = travelAuthorizationDocument.getTraveler().getCustomerNumber();
        String orgInvoiceNumber = travelAuthorizationDocument.getTravelDocumentIdentifier();
        java.util.Date billingDate = dateTimeService.getCurrentDate();
        cal.setTime(travelAuthorizationDocument.getTripEnd());
        cal.add(Calendar.DATE, numDaysDue);
        java.util.Date dueDate = cal.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        AccountsReceivableCustomerInvoice customerInvoiceDocument = accountsReceivableModuleService.createCustomerInvoiceDocument();
        LOG.info("Created customer invoice document " + customerInvoiceDocument.getDocumentNumber());

        setupDefaultValuesForNewCustomerInvoiceDocument(customerInvoiceDocument, processingChartCode, processingOrgCode);

        customerInvoiceDocument.getDocumentHeader().setOrganizationDocumentNumber(travelAuthorizationDocument.getTravelDocumentIdentifier() + "");
        customerInvoiceDocument.getDocumentHeader().setDocumentDescription("Travel Advance - " + travelAuthorizationDocument.getTravelDocumentIdentifier() + " - " + travelAuthorizationDocument.getTraveler().getFirstName() + " " + travelAuthorizationDocument.getTraveler().getLastName());
        // KUALITEM-490 fix. I would have thought that perhaps the CustomerInvoiceDocument would have provided a max length for the
        // CustomerInvoiceDocument-invoiceHeaderText
        if (customerInvoiceDocument.getDocumentHeader().getDocumentDescription().length() >= 40) {
            String truncatedDocumentDescription = customerInvoiceDocument.getDocumentHeader().getDocumentDescription().substring(0, 39);
            customerInvoiceDocument.getDocumentHeader().setDocumentDescription(truncatedDocumentDescription);
        }
        customerInvoiceDocument.getAccountsReceivableDocumentHeader().setCustomerNumber(customerNumber);
        customerInvoiceDocument.setBillingDate(new java.sql.Date(billingDate.getTime()));
        customerInvoiceDocument.setInvoiceDueDate(new java.sql.Date(dueDate.getTime()));
        customerInvoiceDocument.setOrganizationInvoiceNumber(orgInvoiceNumber.toString());
        customerInvoiceDocument.setPaymentChartOfAccountsCode(processingChartCode);
        customerInvoiceDocument.setPaymentOrganizationReferenceIdentifier(processingOrgCode);

        //Make sure the address from the TA is a customer address for the Invoice that is getting created
        AccountsReceivableCustomerAddress customerBillToAddress = null;
        TravelerDetail traveler = travelAuthorizationDocument.getTraveler();
        TEMProfile profile = travelAuthorizationDocument.getTemProfile();
        if (profile == null){
        	//Get the TEM Profile associated with this TA
        	profile = temProfileService.findTemProfileById(travelAuthorizationDocument.getTemProfileId());
        }
        AccountsReceivableCustomer customer = profile.getCustomer();

        //Compare the address from the TA to the addresses for this customer to see if it already exists
        for(AccountsReceivableCustomerAddress address: customer.getAccountsReceivableCustomerAddresses()) {
    		if (!compareAddress(address, traveler)) {
    			//Address found
            	customerBillToAddress = address;
            	break;
            }
    	}

        if (customerBillToAddress == null){
        	//This address from the TA was not found as a customer address so create a new one for this customer
        	customerBillToAddress = accountsReceivableModuleService.createCustomerAddress();
        	customerBillToAddress.setCustomerAddressTypeCodeAsAlternate();

        	//Customer's name as the customer address name
        	String tempName = profile.getFirstName() + " " + (StringUtils.isEmpty(profile.getMiddleName()) ? "" : profile.getMiddleName() + " ") + profile.getLastName();
            if (tempName.length() > 40){
                tempName = profile.getFirstName() + " " + profile.getLastName();
                while (tempName.length() > 40){
                    tempName = tempName.substring(0, tempName.length()-1);
                }
            }

            //Set all the fields for the new address
        	customerBillToAddress.setCustomerAddressName(tempName);
        	customer.setCustomerAddressChangeDate(dateTimeService.getCurrentSqlDate());
            customerBillToAddress.setCustomerLine1StreetAddress(StringUtils.isNotEmpty(traveler.getStreetAddressLine1()) ? traveler.getStreetAddressLine1().toUpperCase() : "");
            customerBillToAddress.setCustomerLine2StreetAddress(StringUtils.isNotEmpty(traveler.getStreetAddressLine2()) ? traveler.getStreetAddressLine2().toUpperCase() : "");
            customerBillToAddress.setCustomerCityName(StringUtils.isNotEmpty(traveler.getCityName()) ? traveler.getCityName().toUpperCase() : "");
            customerBillToAddress.setCustomerStateCode(StringUtils.isNotEmpty(traveler.getStateCode()) ? traveler.getStateCode().toUpperCase() : "");
            customerBillToAddress.setCustomerZipCode(traveler.getZipCode());
            customerBillToAddress.setCustomerCountryCode(StringUtils.isNotEmpty(traveler.getCountryCode()) ? traveler.getCountryCode().toUpperCase() : "");
            customerBillToAddress.setCustomerEmailAddress(StringUtils.isNotEmpty(traveler.getEmailAddress()) ? traveler.getEmailAddress().toUpperCase() : "");

            //Add the new address to the customer and save
            List<AccountsReceivableCustomerAddress> customerAddresses = customer.getAccountsReceivableCustomerAddresses();
            customerAddresses.add(customerBillToAddress);
            customer.setAccountsReceivableCustomerAddresses(customerAddresses);
            accountsReceivableModuleService.saveCustomer(customer);
        }

        customerBillToAddress.refresh();

        customerInvoiceDocument.setCustomerBillToAddress(customerBillToAddress);
        customerInvoiceDocument.setCustomerBillToAddressIdentifier(customerBillToAddress.getCustomerAddressIdentifier());
        customerInvoiceDocument.setBillingAddressTypeCodeAsPrimary();
        customerInvoiceDocument.setBillingAddressName(customerBillToAddress.getAccountsReceivableCustomer().getCustomerName());
        customerInvoiceDocument.setBillingLine1StreetAddress(customerBillToAddress.getCustomerLine1StreetAddress());
        customerInvoiceDocument.setBillingLine2StreetAddress(customerBillToAddress.getCustomerLine2StreetAddress());
        customerInvoiceDocument.setBillingCityName(customerBillToAddress.getCustomerCityName());
        customerInvoiceDocument.setBillingStateCode(customerBillToAddress.getCustomerStateCode());
        customerInvoiceDocument.setBillingZipCode(customerBillToAddress.getCustomerZipCode());
        customerInvoiceDocument.setBillingCountryCode(customerBillToAddress.getCustomerCountryCode());
        customerInvoiceDocument.setBillingAddressInternationalProvinceName(customerBillToAddress.getCustomerAddressInternationalProvinceName());
        customerInvoiceDocument.setBillingInternationalMailCode(customerBillToAddress.getCustomerInternationalMailCode());
        customerInvoiceDocument.setBillingEmailAddress(customerBillToAddress.getCustomerEmailAddress());

        try {
            LOG.info("Saving customer invoice document " + customerInvoiceDocument.getDocumentNumber());
            // getDocumentService().saveDocument(customerInvoiceDocument);
            for (TravelAdvance adv : advances) {
                if (StringUtils.isEmpty(adv.getArInvoiceDocNumber())) {
                    AccountsReceivableCustomerInvoiceDetail detail = createInvoiceDetailFromAdvance(adv, customerInvoiceDocument.getDocumentNumber(), invoiceItemCode, processingOrgCode, processingChartCode);
                    addInvoiceDetailToDocument(detail, customerInvoiceDocument);
                }
            }
            LOG.info("Saving customer invoice document after adding acctg lines " + customerInvoiceDocument.getDocumentNumber());
            accountsReceivableModuleService.saveCustomerInvoiceDocument(customerInvoiceDocument);

            // add relationship
            String relationDescription = "TA - Customer Invoice";
            accountingDocumentRelationshipService.save(new AccountingDocumentRelationship(travelAuthorizationDocument.getDocumentNumber(), customerInvoiceDocument.getDocumentNumber(), relationDescription));

            //update AR Invoice Doc number to the travel advances
            for (TravelAdvance adv : advances) {
                if (StringUtils.isEmpty(adv.getArInvoiceDocNumber())) {
                    adv.setArInvoiceDocNumber(customerInvoiceDocument.getDocumentNumber());
                    adv.setArCustomerId(customerNumber);
                }
            }

            // route
        	WorkflowDocument originalWorkflowDocument = customerInvoiceDocument.getDocumentHeader().getWorkflowDocument();
            try {
                // original initiator may not have permission to blanket approve the INV
                GlobalVariables.setUserSession(new UserSession(KFSConstants.SYSTEM_USER));

                WorkflowDocument newWorkflowDocument = workflowDocumentService.loadWorkflowDocument(customerInvoiceDocument.getDocumentNumber(), GlobalVariables.getUserSession().getPerson());
                newWorkflowDocument.setTitle(originalWorkflowDocument.getTitle());

                customerInvoiceDocument.getDocumentHeader().setWorkflowDocument(newWorkflowDocument);

                accountsReceivableModuleService.blanketApproveCustomerInvoiceDocument(customerInvoiceDocument);
        	}
            finally {
                GlobalVariables.setUserSession(originalUser);
                customerInvoiceDocument.getDocumentHeader().setWorkflowDocument(originalWorkflowDocument);
            }
            LOG.info("Submitted customer invoice document "+ customerInvoiceDocument.getDocumentNumber() + " for " + customerNumber + " - " + sdf.format(billingDate) + "\n\n");

        }
        catch (WorkflowException e) {
            throw new RuntimeException("Customer Invoice Document routing failed.");
        }
        finally {
            // reset user session
            GlobalVariables.setUserSession(originalUser);
        }

    }

    /**
     *
     * @param customerAddress
     * @param traveler
     * @return
     */
    protected boolean compareAddress(AccountsReceivableCustomerAddress customerAddress, TravelerDetail traveler) {
    	if(!StringUtils.equalsIgnoreCase(customerAddress.getCustomerLine1StreetAddress(), traveler.getStreetAddressLine1())) {
    		return true;
    	}
    	if(!StringUtils.equalsIgnoreCase(customerAddress.getCustomerLine2StreetAddress(), traveler.getStreetAddressLine2())) {
    		return true;
    	}
    	if(!StringUtils.equalsIgnoreCase(customerAddress.getCustomerCityName(), traveler.getCityName())) {
    		return true;
    	}
    	if(!StringUtils.equalsIgnoreCase(customerAddress.getCustomerStateCode(), traveler.getStateCode())) {
    		return true;
    	}
    	if(!StringUtils.equalsIgnoreCase(customerAddress.getCustomerZipCode(), traveler.getZipCode())) {
    		return true;
    	}
    	if(!StringUtils.equalsIgnoreCase(customerAddress.getCustomerCountryCode(), traveler.getCountryCode())) {
    		return true;
    	}

        return false;
    }

    /**
     *
     * @param document
     * @param chartOfAccountsCode
     * @param organizationCode
     */
    protected void setupDefaultValuesForNewCustomerInvoiceDocument(AccountsReceivableCustomerInvoice document, String chartOfAccountsCode, String organizationCode) {

        // setupBasicDefaultValuesForCustomerInvoiceDocument(document);
        document.setBillByChartOfAccountCode(chartOfAccountsCode);
        document.setBilledByOrganizationCode(organizationCode);
        document.setOpenInvoiceIndicator(true);

        // set up the default values for the AR DOC Header

        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = accountsReceivableModuleService.createAccountsReceivableDocumentHeader();

        // we try to get the processing org directly, which we'll get if the initiating user is an AR Processor
        // Remove once we switch our parameters
        AccountsReceivableSystemInformation processingOrg = accountsReceivableModuleService.getSystemInformationByProcessingChartOrgAndFiscalYear(chartOfAccountsCode, organizationCode, universityDateService.getCurrentFiscalYear());
        if (processingOrg != null) {
            accountsReceivableDocumentHeader.setProcessingChartOfAccountCode(processingOrg.getProcessingChartOfAccountCode());
            accountsReceivableDocumentHeader.setProcessingOrganizationCode(processingOrg.getProcessingOrganizationCode());
            // return accountsReceivableDocumentHeader;
        }

        // next we try to get the processing org through the initiating user's billing org, if that exists
        AccountsReceivableOrganizationOptions orgOptions = accountsReceivableModuleService.getOrgOptionsIfExists(chartOfAccountsCode, organizationCode);
        if (orgOptions != null) {
            accountsReceivableDocumentHeader.setProcessingChartOfAccountCode(orgOptions.getProcessingChartOfAccountCode());
            accountsReceivableDocumentHeader.setProcessingOrganizationCode(orgOptions.getProcessingOrganizationCode());
            // return accountsReceivableDocumentHeader;
        }

        accountsReceivableDocumentHeader.setDocumentNumber(document.getDocumentNumber());
        document.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);

        // set up the primary key for AR_INV_RCURRNC_DTL_T
        AccountsReceivableCustomerInvoiceRecurrenceDetails recurrenceDetails = accountsReceivableModuleService.createCustomerInvoiceRecurrenceDetails();
        recurrenceDetails.setInvoiceNumber(document.getDocumentNumber());
        // recurrenceDetails.setCustomerNumber(document.getCustomer().getCustomerNumber());
        document.setCustomerInvoiceRecurrenceDetails(recurrenceDetails);

        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("chartOfAccountsCode", document.getBillByChartOfAccountCode());
        criteria.put("organizationCode", document.getBilledByOrganizationCode());
        AccountsReceivableOrganizationOptions organizationOptions = accountsReceivableModuleService.getOrganizationOptionsByPrimaryKey(criteria);

        if (ObjectUtils.isNotNull(organizationOptions)) {
            document.setPrintInvoiceIndicator(organizationOptions.getPrintInvoiceIndicator());
            document.setInvoiceTermsText(organizationOptions.getOrganizationPaymentTermsText());
        }

        // If document is using receivable option, set receivable accounting line for customer invoice document
        if (accountsReceivableModuleService.isUsingReceivableFAU()) {
            accountsReceivableModuleService.setReceivableAccountingLineForCustomerInvoiceDocument(document);
        }
    }

    protected AccountsReceivableCustomerInvoiceDetail createInvoiceDetailFromAdvance(TravelAdvance advance, String documentNumber, String invoiceItemCode, String processingOrgCode, String processingChartCode) {
        AccountsReceivableCustomerInvoiceDetail customerInvoiceDetail = accountsReceivableModuleService.getCustomerInvoiceDetailFromCustomerInvoiceItemCode(invoiceItemCode, processingChartCode, processingOrgCode);

        customerInvoiceDetail.setDocumentNumber(documentNumber);
        customerInvoiceDetail.setInvoiceItemUnitPrice(advance.getTravelAdvanceRequested());
        customerInvoiceDetail.setInvoiceItemQuantity(new BigDecimal(1));

        customerInvoiceDetail.updateAmountBasedOnQuantityAndUnitPrice();
        String accountsReceivableObjectCode = accountsReceivableModuleService.getAccountsReceivableObjectCodeBasedOnReceivableParameter(customerInvoiceDetail);
        customerInvoiceDetail.setAccountsReceivableObjectCode(accountsReceivableObjectCode);
        return customerInvoiceDetail;
    }


    /**
     * This method takes a detail object, runs business rules and adds it to the source accounting lines of the invoice
     *
     * @param detail
     * @param customerInvoiceDocument
     */
    protected void addInvoiceDetailToDocument(AccountsReceivableCustomerInvoiceDetail detail, AccountsReceivableCustomerInvoice customerInvoiceDocument) {
        accountsReceivableModuleService.recalculateCustomerInvoiceDetail(customerInvoiceDocument, detail);

        // run rules
        boolean rulePassed = true;
        // check any business rules
        rulePassed &= kualiRuleService.applyRules(new AddAccountingLineEvent(KFSConstants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME, (Document)customerInvoiceDocument, (AccountingLine)detail));

        LOG.debug("running rules on new source line : " + rulePassed);
        // add accountingLine
        detail.refreshNonUpdateableReferences();
        accountsReceivableModuleService.prepareCustomerInvoiceDetailForAdd(detail, customerInvoiceDocument);
        customerInvoiceDocument.addSourceAccountingLine((SourceAccountingLine) detail);
        if (customerInvoiceDocument instanceof AmountTotaling) {
            ((FinancialSystemDocumentHeader) customerInvoiceDocument.getDocumentHeader()).setFinancialDocumentTotalAmount(((AmountTotaling) customerInvoiceDocument).getTotalDollarAmount());
        }
    }

    /**
     * Locate all {@link TravelAuthorizationDocument} instances with the same <code>travelDocumentIdentifier</code>
     *
     * @param travelDocumentIdentifier to locate {@link TravelAuthorizationDocument} instances
     * @return {@link Collection} of {@link TravelAuthorizationDocument} instances
     */
    @Override
    public Collection<TravelAuthorizationDocument> find(final String travelDocumentIdentifier) {
        final Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(TRVL_IDENTIFIER_PROPERTY, travelDocumentIdentifier);
        return businessObjectService.findMatching(TravelAuthorizationDocument.class, criteria);
    }

    public void addListenersTo(final TravelAuthorizationDocument authorization) {
        authorization.setPropertyChangeListeners(propertyChangeListeners);
    }

    /**
     * Locate all {@link TravelAuthorizationAmendmentDocument} instances with the same <code>travelDocumentIdentifier</code>
     *
     * @param travelDocumentIdentifier to locate {@link TravelAuthorizationAmendmentDocument} instances
     * @return {@link Collection} of {@link TravelAuthorizationAmendmentDocument} instances
     */
    @Override
    public Collection<TravelAuthorizationAmendmentDocument> findAmendment(Integer travelDocumentIdentifier) {
        final Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(TRVL_IDENTIFIER_PROPERTY, travelDocumentIdentifier);
        return businessObjectService.findMatching(TravelAuthorizationAmendmentDocument.class, criteria);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelAuthorizationService#createTravelAdvanceDVDocument(org.kuali.kfs.module.tem.document.TravelAuthorizationDocument)
     */
    @Override
    public void createTravelAdvanceDVDocument(TravelAuthorizationDocument travelAuthorizationDocument) {
        boolean enableDVAR = parameterService.getParameterValueAsBoolean(TemParameterConstants.TEM_AUTHORIZATION.class, TravelAuthorizationParameters.ENABLE_DV_FOR_TRAVEL_ADVANCE_IND);
        if (enableDVAR) {
            KualiDecimal amount = KualiDecimal.ZERO;
            for (TravelAdvance adv : travelAuthorizationDocument.getTravelAdvances()) {
                if (StringUtils.isBlank(adv.getArInvoiceDocNumber()) && adv.getTravelAdvanceRequested().isGreaterThan(KualiDecimal.ZERO)) {
                    amount = amount.add(adv.getTravelAdvanceRequested());
                }
            }
            if (amount.isGreaterThan(KualiDecimal.ZERO)) {
                travelDisbursementService.processTravelAdvanceDV(travelAuthorizationDocument);
            }
        }
    }

    /**
     * This method creates a new travel auth document from a source document
     *
     * @param sourceDocument
     * @param docType
     * @return new Travel Authorization Document
     * @throws WorkflowException
     */
    @SuppressWarnings("rawtypes")
    protected TravelAuthorizationDocument createTravelAuthorizationDocumentFromSourceDocument(TravelDocument sourceDocument, String docType) throws WorkflowException {
        if (ObjectUtils.isNull(sourceDocument)) {
            String errorMsg = "Attempting to create new Travel Authorization of type '" + docType + "' from source TA doc that is null";
            LOG.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        TravelAuthorizationDocument newTravelAuthChangeDocument = (TravelAuthorizationDocument) documentService.getNewDocument(docType);

        Set<Class> classesToExclude = new HashSet<Class>();
        Class sourceObjectClass = FinancialSystemTransactionalDocumentBase.class;
        classesToExclude.add(sourceObjectClass);
        while (sourceObjectClass.getSuperclass() != null) {
            sourceObjectClass = sourceObjectClass.getSuperclass();
            classesToExclude.add(sourceObjectClass);
        }
        PurApObjectUtils.populateFromBaseWithSuper(sourceDocument, newTravelAuthChangeDocument, TemConstants.uncopyableFieldsForTravelAuthorization(), classesToExclude);
        newTravelAuthChangeDocument.getDocumentHeader().setDocumentDescription(sourceDocument.getDocumentHeader().getDocumentDescription());
        newTravelAuthChangeDocument.getDocumentHeader().setOrganizationDocumentNumber(sourceDocument.getDocumentHeader().getOrganizationDocumentNumber());
        newTravelAuthChangeDocument.getDocumentHeader().setExplanation(sourceDocument.getDocumentHeader().getExplanation());

        newTravelAuthChangeDocument.refreshNonUpdateableReferences();

        return newTravelAuthChangeDocument;
    }


    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelAuthorizationService#getTravelAuthorizationBy(java.lang.String)
     */
    @Override
    public TravelAuthorizationDocument getTravelAuthorizationBy(String documentNumber) {
        if (ObjectUtils.isNotNull(documentNumber)) {
            try {
                TravelAuthorizationDocument doc = (TravelAuthorizationDocument) documentService.getByDocumentHeaderId(documentNumber);
                if (ObjectUtils.isNotNull(doc)) {
                    WorkflowDocument workflowDocument = doc.getDocumentHeader().getWorkflowDocument();
                    doc.refreshReferenceObject(KFSPropertyConstants.DOCUMENT_HEADER);
                    doc.getDocumentHeader().setWorkflowDocument(workflowDocument);
                }
                return doc;
            }
            catch (WorkflowException e) {
                String errorMessage = "Error getting travel authorization document from document service";
                LOG.error("getTravelAuthorizationByDocumentNumber() " + errorMessage, e);
                throw new RuntimeException(errorMessage, e);
            }
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelAuthorizationService#closeAuthorization(org.kuali.kfs.module.tem.document.TravelAuthorizationDocument, java.lang.String, java.lang.String)
     */
    @Override
    public TravelAuthorizationCloseDocument closeAuthorization(TravelAuthorizationDocument authorization, String annotation, String initiatorPrincipalName) {
        TravelAuthorizationCloseDocument authorizationClose = null;
        try {
            String user = GlobalVariables.getUserSession().getPerson().getLastName() + ", " + GlobalVariables.getUserSession().getPerson().getFirstName();
            String note = MessageUtils.getMessage(TA_MESSAGE_CLOSE_DOCUMENT_TEXT, user);

            final Note newNote = documentService.createNoteFromDocument(authorization, note);
            authorization.addNote(newNote);
            authorization.updateAppDocStatus(TravelAuthorizationStatusCodeKeys.RETIRED_VERSION);
            documentDao.save(authorization);

            // setting to initiator principal name
            GlobalVariables.setUserSession(new UserSession(initiatorPrincipalName));
            authorizationClose = authorization.toCopyTAC();
            final Note newNoteTAC = documentService.createNoteFromDocument(authorizationClose, note);
            authorizationClose.addNote(newNoteTAC);

            // add relationship
            String relationDescription = authorization.getDocumentTypeName() + " - " + authorizationClose.getDocumentTypeName();
            accountingDocumentRelationshipService.save(new AccountingDocumentRelationship(authorization.getDocumentNumber(), authorizationClose.getDocumentNumber(), relationDescription));

            // switching to KR user to route
            GlobalVariables.setUserSession(new UserSession(KRADConstants.SYSTEM_USER));
            authorizationClose.setAppDocStatus(TravelAuthorizationStatusCodeKeys.CLOSED);
            documentService.routeDocument(authorizationClose, annotation, null);
        }
        catch (Exception e) {
            LOG.error("Could not create TAC or route it with travel id " + authorization.getTravelDocumentIdentifier());
            LOG.error(e.getMessage(), e);
        }
        return authorizationClose;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelAuthorizationService#findEnrouteOrApprovedTravelReimbursement(org.kuali.kfs.module.tem.document.TravelAuthorizationDocument)
     */
    @Override
    public TravelReimbursementDocument findEnrouteOrProcessedTravelReimbursement(TravelAuthorizationDocument authorization) {

        TravelReimbursementDocument reimbursement = null;
        List<TravelReimbursementDocument> reimbursementDocumentList = travelDocumentService.findReimbursementDocuments(authorization.getTravelDocumentIdentifier());

        //look for enroute TR document - return the first document if any is found
        for (TravelReimbursementDocument document : reimbursementDocumentList){
            WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
            if (workflowDocument.isEnroute() || workflowDocument.isFinal() || workflowDocument.isProcessed()){
                reimbursement = document;
            }
        }
        return reimbursement;
    }
    /**
     * This method checks to see if the travel expense type code is a prepaid expense
     *
     * @param travelExpenseTypeCodeCode
     */
    public boolean checkNonReimbursable(String travelExpenseTypeCodeId) {
        boolean nonReimbursable = false;
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put("travelExpenseTypeCodeId", travelExpenseTypeCodeId);
        Collection<TemTravelExpenseTypeCode> keyValueList = SpringContext.getBean(KeyValuesService.class).findMatching(TemTravelExpenseTypeCode.class, fieldValues);
        //should only return 1
        for (TemTravelExpenseTypeCode typeCode : keyValueList) {
            nonReimbursable = typeCode.isPrepaidExpense();
        }
        return nonReimbursable;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setAccountsReceivableModuleService(AccountsReceivableModuleService accountsReceivableModuleService) {
        this.accountsReceivableModuleService = accountsReceivableModuleService;
    }

    public void setPropertyChangeListeners(final List<PropertyChangeListener> propertyChangeListeners) {
        this.propertyChangeListeners = propertyChangeListeners;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setTravelDocumentService(TravelDocumentService travelDocumentService) {
        this.travelDocumentService = travelDocumentService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public void setRuleService(final KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }

    public void setAccountingDocumentRelationshipService(AccountingDocumentRelationshipService accountingDocumentRelationshipService) {
        this.accountingDocumentRelationshipService = accountingDocumentRelationshipService;
    }

	public void setTemProfileService(TemProfileService temProfileService) {
		this.temProfileService = temProfileService;
	}

    public void setTravelDisbursementService(TravelDisbursementService travelDisbursementService) {
        this.travelDisbursementService = travelDisbursementService;
    }

    public void setDocumentDao(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }

}
