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

import static org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
import static org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters.PARAM_DTL_TYPE;
import static org.kuali.kfs.module.tem.TemConstants.TravelParameters.DOCUMENT_DTL_TYPE;
import static org.kuali.kfs.module.tem.TemConstants.TravelParameters.TRAVEL_DOCUMENTATION_LOCATION_CODE;
import static org.kuali.kfs.module.tem.TemKeyConstants.MESSAGE_DV_IN_ACTION_LIST;
import static org.kuali.kfs.module.tem.TemPropertyConstants.TRVL_IDENTIFIER_PROPERTY;
import static org.kuali.kfs.module.tem.util.BufferedLogger.debug;
import static org.kuali.kfs.module.tem.util.BufferedLogger.error;
import static org.kuali.kfs.module.tem.util.BufferedLogger.info;
import static org.kuali.rice.kns.util.GlobalVariables.getMessageList;

import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.TravelExpenseTypeCode;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.gl.service.EncumbranceService;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoice;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerAddress;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoiceDetail;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.integration.ar.AccountsReceivableOrganizationOptions;
import org.kuali.kfs.integration.ar.AccountsReceivableSystemInformation;
import org.kuali.kfs.integration.ar.AccountsRecievableCustomerInvoiceRecurrenceDetails;
import org.kuali.kfs.integration.ar.AccountsRecievableDocumentHeader;
import org.kuali.kfs.module.purap.util.PurApObjectUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters;
import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TemTravelExpenseTypeCode;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.dataaccess.TravelDocumentDao;
import org.kuali.kfs.module.tem.document.TravelAuthorizationAmendmentDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService;
import org.kuali.kfs.module.tem.document.service.TravelAuthorizationService;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.kfs.sys.document.validation.event.AddAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedRouteDocumentEvent;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.dao.DocumentDao;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KeyValuesService;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;

public class TravelAuthorizationServiceImpl implements TravelAuthorizationService {
    private BusinessObjectService businessObjectService;
    private AccountsReceivableModuleService accountsReceivableModuleService;
    private ParameterService parameterService;
    private DocumentService documentService;
    private TravelDocumentService travelDocumentService;
    private DocumentDao documentDao;
    private DateTimeService dateTimeService;
    private KualiRuleService kualiRuleService;
    private WorkflowDocumentService workflowDocumentService;
    private UniversityDateService universityDateService;
    private TravelDocumentDao travelDocumentDao;
    private AccountingDocumentRelationshipService accountingDocumentRelationshipService;
    private TravelerService travelerService;
    private TemProfileService temProfileService;
    
    private List<PropertyChangeListener> propertyChangeListeners;

    private final int currentYear = Calendar.getInstance().get(Calendar.YEAR);

    @Override
    public void createCustomerInvoice(TravelAuthorizationDocument travelAuthorizationDocument) {

        boolean enableInvoice = getParameterService().getIndicatorParameter(PARAM_NAMESPACE, PARAM_DTL_TYPE, TravelAuthorizationParameters.ENABLE_AR_INV_FOR_TRAVL_ADVANCE_IND);
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

    private void createCustomerInvoiceFromAdvances(TravelAuthorizationDocument travelAuthorizationDocument, List<TravelAdvance> advances, KualiDecimal amount) {

        int numDaysDue = Integer.parseInt(getParameterService().getParameterValue(PARAM_NAMESPACE, PARAM_DTL_TYPE, TravelAuthorizationParameters.NUMBER_OF_DAYS_DUE));
        String invoiceItemCode = getParameterService().getParameterValue(PARAM_NAMESPACE, PARAM_DTL_TYPE, TravelAuthorizationParameters.TRAVEL_ADVANCE_INVOICE_ITEM_CODE);
        String processingOrgCode = getParameterService().getParameterValue(PARAM_NAMESPACE, PARAM_DTL_TYPE, TravelAuthorizationParameters.TRAVEL_ADVANCE_BILLING_ORG_CODE);
        String processingChartCode = getParameterService().getParameterValue(PARAM_NAMESPACE, PARAM_DTL_TYPE, TravelAuthorizationParameters.TRAVEL_ADVANCE_BILLING_CHART_CODE);

        // store this so we can reset after we're finished
        UserSession originalUser = GlobalVariables.getUserSession();

        UserSession tempUser = new UserSession(KFSConstants.SYSTEM_USER);

        // setting to initiator
        GlobalVariables.setUserSession(tempUser);

        // need to refactor this so the customer id is stored on the doc, not in travel advances
        Calendar cal = Calendar.getInstance();
        String customerNumber = travelAuthorizationDocument.getTraveler().getCustomerNumber();
        String orgInvoiceNumber = travelAuthorizationDocument.getTravelDocumentIdentifier();
        java.util.Date billingDate = getDateTimeService().getCurrentDate();
        cal.setTime(travelAuthorizationDocument.getTripEnd());
        cal.add(Calendar.DATE, numDaysDue);
        java.util.Date dueDate = cal.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        AccountsReceivableCustomerInvoice customerInvoiceDocument = getAccountsReceivableModuleService().createCustomerInvoiceDocument();
        info("Created customer invoice document ", customerInvoiceDocument.getDocumentNumber());
        
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
        	customerBillToAddress = getAccountsReceivableModuleService().createCustomerAddress();
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
            getAccountsReceivableModuleService().saveCustomer(customer);
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
            info("Saving customer invoice document ", customerInvoiceDocument.getDocumentNumber());
            // getDocumentService().saveDocument(customerInvoiceDocument);
            for (TravelAdvance adv : advances) {
                if (StringUtils.isEmpty(adv.getArInvoiceDocNumber())) {
                    AccountsReceivableCustomerInvoiceDetail detail = createInvoiceDetailFromAdvance(adv, customerInvoiceDocument.getDocumentNumber(), invoiceItemCode, processingOrgCode, processingChartCode);
                    addInvoiceDetailToDocument(detail, customerInvoiceDocument);
                }
            }
            info("Saving customer invoice document after adding acctg lines ", customerInvoiceDocument.getDocumentNumber());
            getAccountsReceivableModuleService().saveCustomerInvoiceDocument(customerInvoiceDocument);
            
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
        	KualiWorkflowDocument originalWorkflowDocument = customerInvoiceDocument.getDocumentHeader().getWorkflowDocument();
            try {
                // original initiator may not have permission to blanket approve the INV
                GlobalVariables.setUserSession(new UserSession(KFSConstants.SYSTEM_USER));
                
                KualiWorkflowDocument newWorkflowDocument = getWorkflowDocumentService().createWorkflowDocument(Long.valueOf(customerInvoiceDocument.getDocumentNumber()), GlobalVariables.getUserSession().getPerson());
                newWorkflowDocument.setTitle(originalWorkflowDocument.getTitle());
                
                customerInvoiceDocument.getDocumentHeader().setWorkflowDocument(newWorkflowDocument);
            
                getAccountsReceivableModuleService().blanketApproveCustomerInvoiceDocument(customerInvoiceDocument);
        	}
            finally {
                GlobalVariables.setUserSession(originalUser);
                customerInvoiceDocument.getDocumentHeader().setWorkflowDocument(originalWorkflowDocument);
            }
            info("Submitted customer invoice document ", customerInvoiceDocument.getDocumentNumber(), " for ", customerNumber, " - ", sdf.format(billingDate) + "\n\n");

        }
        catch (WorkflowException e) {
            throw new RuntimeException("Customer Invoice Document routing failed.");
        }
        finally {
            // reset user session
            GlobalVariables.setUserSession(originalUser);
        }

    }
    
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

    protected void setupDefaultValuesForNewCustomerInvoiceDocument(AccountsReceivableCustomerInvoice document, String chartOfAccountsCode, String organizationCode) {

        // setupBasicDefaultValuesForCustomerInvoiceDocument(document);
        document.setBillByChartOfAccountCode(chartOfAccountsCode);
        document.setBilledByOrganizationCode(organizationCode);
        document.setOpenInvoiceIndicator(true);

        // set up the default values for the AR DOC Header

        AccountsRecievableDocumentHeader accountsReceivableDocumentHeader = getAccountsReceivableModuleService().createAccountsReceivableDocumentHeader();

        // we try to get the processing org directly, which we'll get if the initiating user is an AR Processor
        // Remove once we switch our parameters
        AccountsReceivableSystemInformation processingOrg = getAccountsReceivableModuleService().getSystemInformationByProcessingChartOrgAndFiscalYear(chartOfAccountsCode, organizationCode, getUniversityDateService().getCurrentFiscalYear());
        if (processingOrg != null) {
            accountsReceivableDocumentHeader.setProcessingChartOfAccountCode(processingOrg.getProcessingChartOfAccountCode());
            accountsReceivableDocumentHeader.setProcessingOrganizationCode(processingOrg.getProcessingOrganizationCode());
            // return accountsReceivableDocumentHeader;
        }

        // next we try to get the processing org through the initiating user's billing org, if that exists
        AccountsReceivableOrganizationOptions orgOptions = getAccountsReceivableModuleService().getOrgOptionsIfExists(chartOfAccountsCode, organizationCode);
        if (orgOptions != null) {
            accountsReceivableDocumentHeader.setProcessingChartOfAccountCode(orgOptions.getProcessingChartOfAccountCode());
            accountsReceivableDocumentHeader.setProcessingOrganizationCode(orgOptions.getProcessingOrganizationCode());
            // return accountsReceivableDocumentHeader;
        }

        accountsReceivableDocumentHeader.setDocumentNumber(document.getDocumentNumber());
        document.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);

        // set up the primary key for AR_INV_RCURRNC_DTL_T
        AccountsRecievableCustomerInvoiceRecurrenceDetails recurrenceDetails = getAccountsReceivableModuleService().createCustomerInvoiceRecurrenceDetails();
        recurrenceDetails.setInvoiceNumber(document.getDocumentNumber());
        // recurrenceDetails.setCustomerNumber(document.getCustomer().getCustomerNumber());
        document.setCustomerInvoiceRecurrenceDetails(recurrenceDetails);

        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("chartOfAccountsCode", document.getBillByChartOfAccountCode());
        criteria.put("organizationCode", document.getBilledByOrganizationCode());
        AccountsReceivableOrganizationOptions organizationOptions = getAccountsReceivableModuleService().getOrganizationOptionsByPrimaryKey(criteria); 

        if (ObjectUtils.isNotNull(organizationOptions)) {
            document.setPrintInvoiceIndicator(organizationOptions.getPrintInvoiceIndicator());
            document.setInvoiceTermsText(organizationOptions.getOrganizationPaymentTermsText());
        }

        // If document is using receivable option, set receivable accounting line for customer invoice document
        if (getAccountsReceivableModuleService().isUsingReceivableFAU()) {
            getAccountsReceivableModuleService().setReceivableAccountingLineForCustomerInvoiceDocument(document);
        }
    }

    protected AccountsReceivableCustomerInvoiceDetail createInvoiceDetailFromAdvance(TravelAdvance advance, String documentNumber, String invoiceItemCode, String processingOrgCode, String processingChartCode) {
        AccountsReceivableCustomerInvoiceDetail customerInvoiceDetail = getAccountsReceivableModuleService().getCustomerInvoiceDetailFromCustomerInvoiceItemCode(invoiceItemCode, processingChartCode, processingOrgCode);
        
        customerInvoiceDetail.setDocumentNumber(documentNumber);
        customerInvoiceDetail.setInvoiceItemUnitPrice(advance.getTravelAdvanceRequested());
        customerInvoiceDetail.setInvoiceItemQuantity(new BigDecimal(1));

        customerInvoiceDetail.updateAmountBasedOnQuantityAndUnitPrice();
        String accountsReceivableObjectCode = getAccountsReceivableModuleService().getAccountsReceivableObjectCodeBasedOnReceivableParameter(customerInvoiceDetail);
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
        getAccountsReceivableModuleService().recalculateCustomerInvoiceDetail(customerInvoiceDocument, detail);
        
        // run rules
        boolean rulePassed = true;
        // check any business rules
        rulePassed &= getRuleService().applyRules(new AddAccountingLineEvent(KFSConstants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME, (Document)customerInvoiceDocument, (AccountingLine)detail));

        debug("running rules on new source line : ", rulePassed);
        // add accountingLine
        detail.refreshNonUpdateableReferences();
        getAccountsReceivableModuleService().prepareCustomerInvoiceDetailForAdd(detail, customerInvoiceDocument);
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
        return getBusinessObjectService().findMatching(TravelAuthorizationDocument.class, criteria);
    }
    
    public void addListenersTo(final TravelAuthorizationDocument authorization) {
        authorization.setPropertyChangeListeners(getPropertyChangeListeners());
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
        return getBusinessObjectService().findMatching(TravelAuthorizationAmendmentDocument.class, criteria);
    }

    @Override
    public void createDVARDocument(TravelAuthorizationDocument travelAuthorizationDocument) {
        boolean enableDVAR = getParameterService().getIndicatorParameter(PARAM_NAMESPACE, PARAM_DTL_TYPE, TravelAuthorizationParameters.ENABLE_DV_FOR_TRAVEL_ADVANCE_IND);
        if (enableDVAR) {
            KualiDecimal amount = KualiDecimal.ZERO;
            for (TravelAdvance adv : travelAuthorizationDocument.getTravelAdvances()) {
                if (StringUtils.isBlank(adv.getArInvoiceDocNumber()) && adv.getTravelAdvanceRequested().isGreaterThan(KualiDecimal.ZERO)) {
                    amount = amount.add(adv.getTravelAdvanceRequested());
                }
            }
            if (amount.isGreaterThan(KualiDecimal.ZERO)) {
                createDVAR(travelAuthorizationDocument);
            }
        }
    }

    /**
     * Method for creating the {@link DisbursementVoucherDocument} for Accounts Receiveable. Used when advances are > 0.
     * 
     * @param travelAuthorizationDocument
     * @param advances
     * @param amount
     */
    protected void createDVAR(TravelAuthorizationDocument travelAuthorizationDocument) {
        // change current user to be the submitter of the original doc
        String currentUser = GlobalVariables.getUserSession().getPrincipalName();
        String principalName = SpringContext.getBean(PersonService.class).getPerson(travelAuthorizationDocument.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId()).getPrincipalName();
        GlobalVariables.setUserSession(new UserSession(principalName));

        DisbursementVoucherDocument disbursementVoucherDocument = null;
        try {
            disbursementVoucherDocument = (DisbursementVoucherDocument) documentService.getNewDocument(DisbursementVoucherDocument.class);
        }
        catch (Exception e) {
            error("Error creating new disbursement voucher document: ", e.getMessage());
            throw new RuntimeException("Error creating new disbursement voucher document: " + e.getMessage(), e);
        }

        populateDisbursementVoucherFields(disbursementVoucherDocument, travelAuthorizationDocument);

        final Map<String, TypedArrayList> oldErrors = new LinkedHashMap<String, TypedArrayList>();
        oldErrors.putAll(GlobalVariables.getMessageMap().getErrorMessages());

        // always save DV
        try {
            disbursementVoucherDocument.prepareForSave();

            businessObjectService.save(disbursementVoucherDocument);
            
            // add relationship
            String relationDescription = "TA - DV";
            accountingDocumentRelationshipService.save(new AccountingDocumentRelationship(travelAuthorizationDocument.getDocumentNumber(), disbursementVoucherDocument.getDocumentNumber(), relationDescription));
            
            getMessageList().add(MESSAGE_DV_IN_ACTION_LIST, disbursementVoucherDocument.getDocumentNumber());

            final Note taDvNote = getDocumentService().createNoteFromDocument(disbursementVoucherDocument, 
                    "system generated note by TA document # " 
                    + travelAuthorizationDocument.getTravelDocumentIdentifier());
            getDocumentService().addNoteToDocument(disbursementVoucherDocument, taDvNote);

            boolean rulePassed = getRuleService().applyRules(new AttributedRouteDocumentEvent("", disbursementVoucherDocument));
            
            if (rulePassed && !(TemConstants.DisbursementVoucherPaymentMethods.WIRE_TRANSFER_PAYMENT_METHOD_CODE.equals(disbursementVoucherDocument.getDisbVchrPaymentMethodCode())
                  || TemConstants.DisbursementVoucherPaymentMethods.FOREIGN_DRAFT_PAYMENT_METHOD_CODE.equals(disbursementVoucherDocument.getDisbVchrPaymentMethodCode()))) {
            	
            	KualiWorkflowDocument originalWorkflowDocument = disbursementVoucherDocument.getDocumentHeader().getWorkflowDocument();
                
            	try {
                    // original initiator may not have permission to blanket approve the DV
                    GlobalVariables.setUserSession(new UserSession(KFSConstants.SYSTEM_USER));
                    
                    KualiWorkflowDocument newWorkflowDocument = getWorkflowDocumentService().createWorkflowDocument(Long.valueOf(disbursementVoucherDocument.getDocumentNumber()), GlobalVariables.getUserSession().getPerson());
                    newWorkflowDocument.setTitle(originalWorkflowDocument.getTitle());
                    
                    disbursementVoucherDocument.getDocumentHeader().setWorkflowDocument(newWorkflowDocument);
                
                    String annotation = "Blanket Approved by system in relation to Travel Auth Document: " + travelAuthorizationDocument.getDocumentNumber();
                    //getWorkflowDocumentService().blanketApprove(disbursementVoucherDocument.getDocumentHeader().getWorkflowDocument(), annotation, null);
                    getDocumentService().blanketApproveDocument(disbursementVoucherDocument, annotation, null);
            	}
                finally {
                    GlobalVariables.setUserSession(new UserSession(currentUser));
                    disbursementVoucherDocument.getDocumentHeader().setWorkflowDocument(originalWorkflowDocument);
                }
            	
                final String noteText = String.format("DV Document %s was system generated and blanket approved", disbursementVoucherDocument.getDocumentNumber());                
                final Note noteToAdd = getDocumentService().createNoteFromDocument(travelAuthorizationDocument, noteText);
                getDocumentService().addNoteToDocument(travelAuthorizationDocument, noteToAdd);
            }
            
            if (!rulePassed || TemConstants.DisbursementVoucherPaymentMethods.WIRE_TRANSFER_PAYMENT_METHOD_CODE.equals(disbursementVoucherDocument.getDisbVchrPaymentMethodCode())
                || TemConstants.DisbursementVoucherPaymentMethods.FOREIGN_DRAFT_PAYMENT_METHOD_CODE.equals(disbursementVoucherDocument.getDisbVchrPaymentMethodCode())) {
                businessObjectService.save(disbursementVoucherDocument);
                String annotation = "Saved by system in relation to Travel Auth Document: " + travelAuthorizationDocument.getDocumentNumber();
                getWorkflowDocumentService().save(disbursementVoucherDocument.getDocumentHeader().getWorkflowDocument(), annotation);
                
                final String noteText = String.format("DV Document %s is saved in the initiator's action list to process travel advance", disbursementVoucherDocument.getDocumentNumber());                
                final Note noteToAdd = getDocumentService().createNoteFromDocument(travelAuthorizationDocument, noteText);
                getDocumentService().addNoteToDocument(travelAuthorizationDocument, noteToAdd);
                getTravelDocumentService().addAdHocFYIRecipient(disbursementVoucherDocument, travelAuthorizationDocument.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
            }
        }
        catch (Exception ex1) {
            // if we can't save DV, need to stop processing
            error("cannot save DV ", disbursementVoucherDocument.getDocumentNumber(), ex1);
            throw new RuntimeException("cannot save DV " + disbursementVoucherDocument.getDocumentNumber(), ex1);
        }
        GlobalVariables.getMessageMap().clearErrorMessages();
        GlobalVariables.getMessageMap().getErrorMessages().putAll(oldErrors);
        GlobalVariables.setUserSession(new UserSession(currentUser));
    }

    /**
     * Populates the given disbursement voucher document from the given payment application document based on predetermined rules
     * 
     * @param disbursementVoucherDocument - disbursement voucher document instance to populate
     * @param paymentApplicationDocument - payment application document instance to pull values from
     */
    protected void populateDisbursementVoucherFields(DisbursementVoucherDocument disbursementVoucherDocument, TravelAuthorizationDocument travelAuthorizationDocument) {
        disbursementVoucherDocument.getDocumentHeader().setDocumentDescription("Generated for TA doc: " + travelAuthorizationDocument.getDocumentNumber());
        disbursementVoucherDocument.getDocumentHeader().setOrganizationDocumentNumber(travelAuthorizationDocument.getTravelDocumentIdentifier());
        
        disbursementVoucherDocument.setRefundIndicator(true);
        disbursementVoucherDocument.getDvPayeeDetail().setDocumentNumber(disbursementVoucherDocument.getDocumentNumber());
        
        try {
            disbursementVoucherDocument.getDocumentHeader().getWorkflowDocument().setTitle("Disbursement Voucher - " + travelAuthorizationDocument.getDocumentHeader().getDocumentDescription());
        }
        catch (WorkflowException ex) {
            error("cannot set title for DV " + disbursementVoucherDocument.getDocumentNumber(), ex);
            throw new RuntimeException("Error setting DV title: " + disbursementVoucherDocument.getDocumentNumber(), ex);
        }

        // init document
        disbursementVoucherDocument.initiateDocument();
        Person initiator = SpringContext.getBean(PersonService.class).getPerson(travelAuthorizationDocument.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
        if (initiator == null) {
            throw new RuntimeException("Initiator could not be found in KIM!");
        }
        
        disbursementVoucherDocument.setDisbVchrContactPersonName(initiator.getPrincipalName());
        disbursementVoucherDocument.setDisbVchrContactPhoneNumber(initiator.getPhoneNumber());

        // This type needs to be Customer "C", do not change otherwise we will change the configuration
        disbursementVoucherDocument.getDvPayeeDetail().setDisbursementVoucherPayeeTypeCode(DisbursementVoucherConstants.DV_PAYEE_TYPE_CUSTOMER); 
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeIdNumber(travelAuthorizationDocument.getTraveler().getCustomerNumber());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeePersonName(travelAuthorizationDocument.getTraveler().getFirstName() + " " + travelAuthorizationDocument.getTraveler().getLastName());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrAlienPaymentCode(false);

        // disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrVendorAddressIdNumber(travelAuthorizationDocument.getTraveler().get.getCustomerAddressIdentifier().toString());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeLine1Addr(travelAuthorizationDocument.getTraveler().getStreetAddressLine1());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeLine2Addr(travelAuthorizationDocument.getTraveler().getStreetAddressLine2());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeCityName(travelAuthorizationDocument.getTraveler().getCityName());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeStateCode(travelAuthorizationDocument.getTraveler().getStateCode());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeZipCode(travelAuthorizationDocument.getTraveler().getZipCode());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeCountryCode(travelAuthorizationDocument.getTraveler().getCountryCode());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeEmployeeCode(travelerService.isEmployee(travelAuthorizationDocument.getTraveler()));

        // set defaults
        String paymentReasonCode = parameterService.getParameterValue(PARAM_NAMESPACE, PARAM_DTL_TYPE, TemConstants.TravelAuthorizationParameters.TRAVEL_ADVANCE_DV_PAYMENT_REASON_CODE);
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPaymentReasonCode(paymentReasonCode);
        disbursementVoucherDocument.setDisbVchrPaymentMethodCode(travelAuthorizationDocument.getTravelAdvances().get(0).getPaymentMethod());
        
        String advancePaymentChartCode = parameterService.getParameterValue(PARAM_NAMESPACE, PARAM_DTL_TYPE, TemConstants.TravelAuthorizationParameters.TRAVEL_ADVANCE_PAYMENT_CHART_CODE);
        String advancePaymentAccountNumber = parameterService.getParameterValue(PARAM_NAMESPACE, PARAM_DTL_TYPE, TemConstants.TravelAuthorizationParameters.TRAVEL_ADVANCE_PAYMENT_ACCOUNT_NBR);
        String advancePaymentObjectCode = parameterService.getParameterValue(PARAM_NAMESPACE, PARAM_DTL_TYPE, TemConstants.TravelAuthorizationParameters.TRAVEL_ADVANCE_PAYMENT_OBJECT_CODE);

        // set accounting
        KualiDecimal totalAmount = KualiDecimal.ZERO;
        for (TravelAdvance advance : travelAuthorizationDocument.getTravelAdvances()) {
            SourceAccountingLine accountingLine = new SourceAccountingLine();

            if (StringUtils.isNotBlank(advancePaymentChartCode)) {
                accountingLine.setChartOfAccountsCode(advancePaymentChartCode);
            }
            else {
                accountingLine.setChartOfAccountsCode(advance.getAcct().getChartOfAccountsCode());
            }

            if (StringUtils.isNotBlank(advancePaymentAccountNumber)) {
                accountingLine.setAccountNumber(advancePaymentAccountNumber);
            }
            else {
                accountingLine.setAccountNumber(advance.getAccountNumber());
            }

            if (StringUtils.isNotBlank(advancePaymentObjectCode)) {
                accountingLine.setFinancialObjectCode(advancePaymentObjectCode);
            }
            else {
                if (StringUtils.isNotBlank(advance.getFinancialObjectCode())) {
                    accountingLine.setFinancialObjectCode(advance.getFinancialObjectCode());
                }
            }

            if (StringUtils.isNotBlank(advance.getFinancialSubObjectCode())) {
                accountingLine.setFinancialSubObjectCode(advance.getFinancialSubObjectCode());
            }

            if (StringUtils.isNotBlank(advance.getSubAccountNumber())) {
                accountingLine.setSubAccountNumber(advance.getSubAccountNumber());
            }

            accountingLine.setAmount(advance.getTravelAdvanceRequested());
            accountingLine.setPostingYear(disbursementVoucherDocument.getPostingYear());
            accountingLine.setDocumentNumber(disbursementVoucherDocument.getDocumentNumber());

            disbursementVoucherDocument.addSourceAccountingLine(accountingLine);

            totalAmount = totalAmount.add(advance.getTravelAdvanceRequested());

            if (advance.getDueDate() != null) {
                disbursementVoucherDocument.setDisbursementVoucherDueDate(advance.getDueDate());
            }
        }

        disbursementVoucherDocument.setDisbursementVoucherDocumentationLocationCode(parameterService.getParameterValue(PARAM_NAMESPACE, DOCUMENT_DTL_TYPE, TRAVEL_DOCUMENTATION_LOCATION_CODE));
        disbursementVoucherDocument.setDisbVchrCheckStubText("Travel Advance for " + travelAuthorizationDocument.getTravelDocumentIdentifier() + " " + travelAuthorizationDocument.getTraveler().getLastName() + " - " + travelAuthorizationDocument.getPrimaryDestinationName() + " - " + travelAuthorizationDocument.getTripBegin());
        disbursementVoucherDocument.setDisbVchrCheckTotalAmount(totalAmount);
    }

    /**
     * This method creates a new travel auth document from a source document
     * 
     * @param sourceDocument
     * @param docType
     * @return new Travel Authorization Document
     * @throws WorkflowException
     */
    protected TravelAuthorizationDocument createTravelAuthorizationDocumentFromSourceDocument(TravelDocument sourceDocument, String docType) throws WorkflowException {
        if (ObjectUtils.isNull(sourceDocument)) {
            String errorMsg = "Attempting to create new Travel Authorization of type '" + docType + "' from source TA doc that is null";
            error(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        TravelAuthorizationDocument newTravelAuthChangeDocument = (TravelAuthorizationDocument) getDocumentService().getNewDocument(docType);

        Set classesToExclude = new HashSet();
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
                TravelAuthorizationDocument doc = (TravelAuthorizationDocument) getDocumentService().getByDocumentHeaderId(documentNumber);
                if (ObjectUtils.isNotNull(doc)) {
                    KualiWorkflowDocument workflowDocument = doc.getDocumentHeader().getWorkflowDocument();
                    doc.refreshReferenceObject(KFSPropertyConstants.DOCUMENT_HEADER);
                    doc.getDocumentHeader().setWorkflowDocument(workflowDocument);
                }
                return doc;
            }
            catch (WorkflowException e) {
                String errorMessage = "Error getting travel authorization document from document service";
                error("getTravelAuthorizationByDocumentNumber() ", errorMessage, e);
                throw new RuntimeException(errorMessage, e);
            }
        }
        return null;
    }

    /**
     * This method checks to see if the travel expense type code is a prepaid expense
     * 
     * @param travelExpenseTypeCodeCode
     */
    public boolean checkNonReimbursable(String travelExpenseTypeCodeId) {
        boolean nonReimbursable = false;
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("travelExpenseTypeCodeId", travelExpenseTypeCodeId);
        List<TravelExpenseTypeCode> boList = (List<TravelExpenseTypeCode>) SpringContext.getBean(KeyValuesService.class).findMatching(TemTravelExpenseTypeCode.class, fieldValues);
        for (TravelExpenseTypeCode typeCode : boList) {
            nonReimbursable = typeCode.isPrepaidExpense();
        }
        return nonReimbursable;
    }
   
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    protected BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Gets the accountsReceivableModuleService attribute.
     * 
     * @return Returns the accountsReceivableModuleService.
     */
    protected AccountsReceivableModuleService getAccountsReceivableModuleService() {
        if (accountsReceivableModuleService == null) {
            this.accountsReceivableModuleService = SpringContext.getBean(AccountsReceivableModuleService.class);
        }
        
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
     * Sets the propertyChangeListener attribute value.
     * 
     * @param propertyChangeListener The propertyChangeListener to set.
     */
    public void setPropertyChangeListeners(final List<PropertyChangeListener> propertyChangeListeners) {
        this.propertyChangeListeners = propertyChangeListeners;
    }
    
    /**
     * Gets the propertyChangeListeners attribute.
     * 
     * @return Returns the propertyChangeListenerDetailId.
     */
    public List<PropertyChangeListener> getPropertyChangeListeners() {
        return this.propertyChangeListeners;
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

    /**
     * Gets the workflowDocumentService attribute.
     * 
     * @return Returns the workflowDocumentService.
     */
    public WorkflowDocumentService getWorkflowDocumentService() {
        return workflowDocumentService;
    }

    /**
     * Sets the workflowDocumentService attribute value.
     * 
     * @param workflowDocumentService The workflowDocumentService to set.
     */
    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    /**
     * Gets the dateTimeService attribute.
     * 
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
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
     * Gets the universityDateService attribute.
     * 
     * @return Returns the universityDateService.
     */
    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    /**
     * Sets the universityDateService attribute value.
     * 
     * @param universityDateService The universityDateService to set.
     */
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    @Override
    public void setTravelDocumentDao(final TravelDocumentDao travelDocumentDao) {
        this.travelDocumentDao = travelDocumentDao;
    }

    protected TravelDocumentDao getTravelDocumentDao() {
        return travelDocumentDao;
    }

    /**
     * Sets the kualiRulesService attribute.
     * 
     * @return Returns the kualiRuleService.
     */
    public void setRuleService(final KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }

    /**
     * Gets the kualiRulesService attribute.
     * 
     * @return Returns the kualiRuleseService.
     */
    protected KualiRuleService getRuleService() {
        return kualiRuleService;
    }

    /**
     * Sets the documentDao attribute.
     * 
     * @return Returns the documentDao.
     */
    public void setDocumentDao(final DocumentDao documentDao) {
        this.documentDao = documentDao;
    }

    /**
     * Gets the documentDao attribute.
     * 
     * @return Returns the documentDao.
     */
    protected DocumentDao getDocumentDao() {
        return documentDao;
    }

    public AccountingDocumentRelationshipService getAccountingDocumentRelationshipService() {
        return accountingDocumentRelationshipService;
    }

    public void setAccountingDocumentRelationshipService(AccountingDocumentRelationshipService accountingDocumentRelationshipService) {
        this.accountingDocumentRelationshipService = accountingDocumentRelationshipService;
    }

    public TravelerService getTravelerService() {
        return travelerService;
    }

    public void setTravelerService(TravelerService travelerService) {
        this.travelerService = travelerService;
    }
    
    public EncumbranceService getEncumbranceService() {
        return SpringContext.getBean(EncumbranceService.class);
    }

	/**
	 * Gets the temProfileService attribute. 
	 * @return Returns the temProfileService.
	 */
	public TemProfileService getTemProfileService() {
		return temProfileService;
	}

	/**
	 * Sets the temProfileService attribute value.
	 * @param temProfileService The temProfileService to set.
	 */
	public void setTemProfileService(TemProfileService temProfileService) {
		this.temProfileService = temProfileService;
	}   
	
    public DocumentHelperService getDocumentHelperService() {
        return SpringContext.getBean(DocumentHelperService.class);
    }
}
