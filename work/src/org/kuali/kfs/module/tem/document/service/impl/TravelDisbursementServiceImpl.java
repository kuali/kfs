/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.service.impl;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.DisburseType;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.document.TEMReimbursementDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService;
import org.kuali.kfs.module.tem.document.service.TravelDisbursementService;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AttributedRouteDocumentEvent;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.TypedArrayList;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;

/**
 * Travel Disbursement Voucher Service
 */
public class TravelDisbursementServiceImpl implements TravelDisbursementService{
    
    protected static Logger LOG = Logger.getLogger(TravelDisbursementServiceImpl.class);
    
    protected DocumentService documentService;
    protected BusinessObjectService businessObjectService;
    protected DateTimeService dateTimeService;
    protected TravelerService travelerService;
    protected AccountingDocumentRelationshipService accountingDocumentRelationshipService;
    protected ParameterService parameterService;
    protected WorkflowDocumentService workflowDocumentService;
    protected KualiRuleService kualiRuleService;
    protected TravelDocumentService travelDocumentService;
    
    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDisbursementService#populateReimbursableDisbursementVoucherFields(org.kuali.kfs.fp.document.DisbursementVoucherDocument, org.kuali.kfs.module.tem.document.TravelDocument)
     */
    public void populateReimbursableDisbursementVoucherFields(DisbursementVoucherDocument disbursementVoucherDocument, TravelDocument document){
        disbursementVoucherDocument.setRefundIndicator(true);
        disbursementVoucherDocument.getDvPayeeDetail().setDocumentNumber(disbursementVoucherDocument.getDocumentNumber());
        disbursementVoucherDocument.getDocumentHeader().setOrganizationDocumentNumber(document.getTravelDocumentIdentifier());
        
        //due date is set to the next day
        Calendar calendar = dateTimeService.getCurrentCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, 1);        
        disbursementVoucherDocument.setDisbursementVoucherDueDate(new java.sql.Date(calendar.getTimeInMillis()));
        
        try {
            disbursementVoucherDocument.getDocumentHeader().getWorkflowDocument().setTitle("Disbursement Voucher - " + document.getDocumentHeader().getDocumentDescription());
        }
        catch (WorkflowException ex) {
            LOG.error("cannot set title for DV " + disbursementVoucherDocument.getDocumentNumber(), ex);
            throw new RuntimeException("Error setting DV title: " + disbursementVoucherDocument.getDocumentNumber(), ex);
        }
        disbursementVoucherDocument.initiateDocument();
        Person initiator = SpringContext.getBean(PersonService.class).getPerson(document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
        if (initiator == null) {
            throw new RuntimeException("Initiator could not be found in KIM!");
        }
        
        disbursementVoucherDocument.setDisbVchrContactPersonName(initiator.getPrincipalName());
        disbursementVoucherDocument.setDisbVchrContactPhoneNumber(initiator.getPhoneNumber());

        // This type needs to be Customer "C", do not change otherwise we will change the configuration
        disbursementVoucherDocument.getDvPayeeDetail().setDisbursementVoucherPayeeTypeCode(DisbursementVoucherConstants.DV_PAYEE_TYPE_CUSTOMER); 
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeIdNumber(document.getTraveler().getPrincipalId());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeePersonName(document.getTraveler().getFirstName() + " " + document.getTraveler().getLastName());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrAlienPaymentCode(false);

       // disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrVendorAddressIdNumber(document.getTraveler().getCustomerAddressIdentifier().toString());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeLine1Addr(document.getTraveler().getStreetAddressLine1());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeLine2Addr(document.getTraveler().getStreetAddressLine2());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeCityName(document.getTraveler().getCityName());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeStateCode(document.getTraveler().getStateCode());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeZipCode(document.getTraveler().getZipCode());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeCountryCode(document.getTraveler().getCountryCode());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeEmployeeCode(travelerService.isEmployee(document.getTraveler()));

        disbursementVoucherDocument.setDisbVchrPaymentMethodCode(TemConstants.DisbursementVoucherPaymentMethods.CHECK_ACH_PAYMENT_METHOD_CODE);
        
        //Copied from TA's impl - may be usable when we refactor that code
//        String advancePaymentChartCode = parameterService.getParameterValue(TemParameterConstants.TEM_AUTHORIZATION.class, TemConstants.TravelAuthorizationParameters.TRAVEL_ADVANCE_PAYMENT_CHART_CODE);
//        String advancePaymentAccountNumber = parameterService.getParameterValue(TemParameterConstants.TEM_AUTHORIZATION.class, TemConstants.TravelAuthorizationParameters.TRAVEL_ADVANCE_PAYMENT_ACCOUNT_NBR);
//        String advancePaymentObjectCode = parameterService.getParameterValue(TemParameterConstants.TEM_AUTHORIZATION.class, TemConstants.TravelAuthorizationParameters.TRAVEL_ADVANCE_PAYMENT_OBJECT_CODE);

        // set accounting
        KualiDecimal totalAmount = KualiDecimal.ZERO;
        for (SourceAccountingLine line : document.getReimbursableSourceAccountingLines()) {
            SourceAccountingLine accountingLine = new SourceAccountingLine();
              
            accountingLine.setChartOfAccountsCode(line.getChartOfAccountsCode());
            accountingLine.setAccountNumber(line.getAccountNumber());
            accountingLine.setFinancialObjectCode(line.getFinancialObjectCode());              
            accountingLine.setFinancialSubObjectCode(line.getFinancialSubObjectCode());
            accountingLine.setSubAccountNumber(line.getSubAccountNumber());
            accountingLine.setAmount(line.getAmount());
            accountingLine.setPostingYear(disbursementVoucherDocument.getPostingYear());
            accountingLine.setDocumentNumber(disbursementVoucherDocument.getDocumentNumber());

            disbursementVoucherDocument.addSourceAccountingLine(accountingLine);
            totalAmount = totalAmount.add(line.getAmount());
        }
        //change the DV's total to the total of accounting lines
        disbursementVoucherDocument.setDisbVchrCheckTotalAmount(totalAmount);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDisbursementService#populateImportedCorpCardDisbursementVoucherFields(org.kuali.kfs.fp.document.DisbursementVoucherDocument, org.kuali.kfs.module.tem.document.TravelDocument, java.lang.String)
     */
    public void populateImportedCorpCardDisbursementVoucherFields(DisbursementVoucherDocument disbursementVoucherDocument, TravelDocument document, String cardAgencyType){

        Person principal = SpringContext.getBean(PersonService.class).getPerson(document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
        
        disbursementVoucherDocument.initiateDocument();

        String vendorNumber = "";
        for (HistoricalTravelExpense historicalTravelExpense : document.getHistoricalTravelExpenses()){
            if (historicalTravelExpense.isCreditCardTravelExpense()){
                if (historicalTravelExpense.getCreditCardAgency().getCreditCardOrAgencyCode().equals(cardAgencyType)){
                    vendorNumber = historicalTravelExpense.getCreditCardAgency().getVendorNumber();
                    break;
                }
            }
        }
        
        Calendar calendar = dateTimeService.getCurrentCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        disbursementVoucherDocument.setDisbursementVoucherDueDate(new java.sql.Date(calendar.getTimeInMillis()));
        
        disbursementVoucherDocument.getDvPayeeDetail().setDisbursementVoucherPayeeTypeCode(DisbursementVoucherConstants.DV_PAYEE_TYPE_VENDOR);
        disbursementVoucherDocument.getDocumentHeader().setDocumentDescription(document.getDocumentHeader().getDocumentDescription());
        disbursementVoucherDocument.getDocumentHeader().setOrganizationDocumentNumber(document.getTravelDocumentIdentifier());
        
        disbursementVoucherDocument.getDvPayeeDetail().setDocumentNumber(disbursementVoucherDocument.getDocumentNumber());  

        VendorService vendorService = SpringContext.getBean(VendorService.class);
        VendorDetail vendor = vendorService.getVendorDetail(vendorNumber);            
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeIdNumber(vendor.getVendorNumber());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeePersonName(vendor.getVendorName());
        
        VendorAddress defaultAddress = vendorService.getVendorDefaultAddress(vendor.getVendorAddresses(), vendor.getVendorHeader().getVendorType().getAddressType().getVendorAddressTypeCode(), "");
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeLine1Addr(defaultAddress.getVendorLine1Address());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeLine2Addr(defaultAddress.getVendorLine2Address());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeCityName(defaultAddress.getVendorCityName());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeStateCode(defaultAddress.getVendorStateCode());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeZipCode(defaultAddress.getVendorZipCode());
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeCountryCode(defaultAddress.getVendorCountryCode());
        
        disbursementVoucherDocument.setDisbVchrCheckStubText(document.getDocumentHeader().getDocumentDescription());
        
        document.setProfileId(document.getTemProfileId());
        
        if (document.getTemProfile().getTravelerType().getCode().equals(TemConstants.EMP_TRAVELER_TYP_CD)){ 
            disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeEmployeeCode(true); 
        }
        
        KualiDecimal totalAmount = KualiDecimal.ZERO;
        for(TemSourceAccountingLine line: (List<TemSourceAccountingLine>)document.getSourceAccountingLines()){
            if (line.getCardType().equals(cardAgencyType)){
                SourceAccountingLine accountingLine = new SourceAccountingLine();
                
                accountingLine.setChartOfAccountsCode(line.getChartOfAccountsCode());
                accountingLine.setAccountNumber(line.getAccountNumber());
                accountingLine.setFinancialObjectCode(line.getFinancialObjectCode());
                accountingLine.setFinancialSubObjectCode(line.getFinancialSubObjectCode());
                accountingLine.setSubAccountNumber(line.getSubAccountNumber());
                accountingLine.setAmount(line.getAmount());
                accountingLine.setPostingYear(disbursementVoucherDocument.getPostingYear());
                accountingLine.setDocumentNumber(disbursementVoucherDocument.getDocumentNumber());

                //accountingLine.setOrganizationReferenceId("");
                
                disbursementVoucherDocument.addSourceAccountingLine(accountingLine); 
                totalAmount = totalAmount.add(line.getAmount());
            }
        }
      //change the DV's total to the total of accounting lines
        disbursementVoucherDocument.setDisbVchrCheckTotalAmount(totalAmount);
        
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPaymentReasonCode(parameterService.getParameterValue(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.CORP_CARD_BANK_PAYMENT_REASON_CODE));
        disbursementVoucherDocument.setDisbVchrContactPersonName(principal.getPrincipalName());
        disbursementVoucherDocument.setDisbVchrContactPhoneNumber(principal.getPhoneNumber());
        disbursementVoucherDocument.setDisbVchrPaymentMethodCode(TemConstants.DisbursementVoucherPaymentMethods.CHECK_ACH_PAYMENT_METHOD_CODE);
        disbursementVoucherDocument.setDisbursementVoucherDocumentationLocationCode(parameterService.getParameterValue(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.TRAVEL_DOCUMENTATION_LOCATION_CODE));

    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDisbursementService#createAndApproveDisbursementVoucherDocument(org.kuali.kfs.module.tem.TemConstants.DisburseType, org.kuali.kfs.module.tem.document.TravelDocument, java.lang.String)
     */
    public DisbursementVoucherDocument createAndApproveDisbursementVoucherDocument(DisburseType type, TravelDocument document, String cardAgencyType){
        String currentUser = GlobalVariables.getUserSession().getPrincipalName();
        
        String principalName = SpringContext.getBean(PersonService.class).getPerson(document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId()).getPrincipalName();
        GlobalVariables.setUserSession(new UserSession(principalName));

        DisbursementVoucherDocument disbursementVoucherDocument = null;
        
        //1. create and populate the DV data and details
        try {
            disbursementVoucherDocument = (DisbursementVoucherDocument) documentService.getNewDocument(DisbursementVoucherDocument.class);
            //update the DV's DocumentHeader's workflow title to be that of the travel document 
            disbursementVoucherDocument.getDocumentHeader().getWorkflowDocument().setTitle(document.getDocumentHeader().getDocumentDescription());
            
            switch (type){
                case corpCard:
                    populateImportedCorpCardDisbursementVoucherFields(disbursementVoucherDocument, document, cardAgencyType);
                    break;
                case reimbursable:
                    document.populateDisbursementVoucherFields(disbursementVoucherDocument);
                    break;
            }
            
        }
        catch (WorkflowException wfe) {
            LOG.error("Error creating new disbursement voucher document: " + wfe.getMessage());
            throw new RuntimeException("Error creating new disbursement voucher document: " + wfe.getMessage(), wfe);
        }
        
        final Map<String, TypedArrayList> oldErrors = new LinkedHashMap<String, TypedArrayList>();
        oldErrors.putAll(GlobalVariables.getMessageMap().getErrorMessages());

        //2. attempt to first save DV doc
        try {
            documentService.saveDocument(disbursementVoucherDocument);
        }
        catch(Exception e){
            // if we can't save DV, need to stop processing
            String errorMessage = String.format("Unable to save DV %s generated by %s TEM Document #: %s",  disbursementVoucherDocument.getDocumentNumber(), 
                    travelDocumentService.getDocumentType(document), document.getDocumentNumber());
            LOG.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);           
        }
        
        //3. blanket approve the doc and add notes to the travel doc
        try {
            String note = String.format("System generated note by %s Document # %s TEM Doc #: %s",  travelDocumentService.getDocumentType(document), 
                    document.getDocumentNumber(), document.getTravelDocumentIdentifier());
            Note dvNote = documentService.createNoteFromDocument(disbursementVoucherDocument, note);
            documentService.addNoteToDocument(disbursementVoucherDocument, dvNote);

            boolean rulePassed = kualiRuleService.applyRules(new AttributedRouteDocumentEvent("", disbursementVoucherDocument));

            if (rulePassed && !(TemConstants.DisbursementVoucherPaymentMethods.WIRE_TRANSFER_PAYMENT_METHOD_CODE.equals(disbursementVoucherDocument.getDisbVchrPaymentMethodCode())
                    || TemConstants.DisbursementVoucherPaymentMethods.FOREIGN_DRAFT_PAYMENT_METHOD_CODE.equals(disbursementVoucherDocument.getDisbVchrPaymentMethodCode()))) {
                
                KualiWorkflowDocument originalWorkflowDocument = disbursementVoucherDocument.getDocumentHeader().getWorkflowDocument();
                
                try {
                    // original initiator may not have permission to blanket approve the DV
                    GlobalVariables.setUserSession(new UserSession(KFSConstants.SYSTEM_USER));
                    
                    KualiWorkflowDocument newWorkflowDocument = workflowDocumentService.createWorkflowDocument(Long.valueOf(disbursementVoucherDocument.getDocumentNumber()), GlobalVariables.getUserSession().getPerson());
                    newWorkflowDocument.setTitle(originalWorkflowDocument.getTitle());
                    
                    disbursementVoucherDocument.getDocumentHeader().setWorkflowDocument(newWorkflowDocument);
                
                    String annotation= String.format("Blanket Approved by system for %s Document: %s",  travelDocumentService.getDocumentType(document), document.getDocumentNumber());
                    documentService.blanketApproveDocument(disbursementVoucherDocument, annotation, null);
                    
                    final String noteText = String.format("DV Document %s was system generated and blanket approved", disbursementVoucherDocument.getDocumentNumber());                
                    final Note noteToAdd = documentService.createNoteFromDocument(document, noteText);
                    documentService.addNoteToDocument(document, noteToAdd);
                }
                 catch (WorkflowException wfe) {
                     LOG.error(wfe.getMessage(), wfe);
                     saveErrorDisbursementVoucher(disbursementVoucherDocument, document);
                 }
                finally {
                    GlobalVariables.setUserSession(new UserSession(currentUser));
                    disbursementVoucherDocument.getDocumentHeader().setWorkflowDocument(originalWorkflowDocument);
                }
            }
            else {
                LOG.info("Business rule does not pass for DV Doc " + disbursementVoucherDocument.getDocumentNumber() + ".  Save DV to action list of " + currentUser);
                saveErrorDisbursementVoucher(disbursementVoucherDocument, document);
            }
        }
        catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
        
        //done with everything, reset errors and put back the old errors
        GlobalVariables.getMessageMap().clearErrorMessages();
        GlobalVariables.getMessageMap().getErrorMessages().putAll(oldErrors);
        
        return disbursementVoucherDocument;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDisbursementService#createAndApproveDisbursementVoucherDocument(org.kuali.kfs.module.tem.TemConstants.DisburseType, org.kuali.kfs.module.tem.document.TravelDocument)
     */
    public DisbursementVoucherDocument createAndApproveDisbursementVoucherDocument(DisburseType type, TravelDocument document){
        return  createAndApproveDisbursementVoucherDocument(type, document, null);
    }
    
    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDisbursementService#processTEMReimbursementDV(org.kuali.kfs.module.tem.document.TEMReimbursementDocument)
     */
    public void processTEMReimbursementDV(TEMReimbursementDocument document){
        String relationDescription = document.getDocumentHeader().getWorkflowDocument().getDocumentType() + " - DV";
        try {
            DisbursementVoucherDocument disbursementVoucherDocument = createAndApproveDisbursementVoucherDocument(DisburseType.reimbursable, document);
            accountingDocumentRelationshipService.save(new AccountingDocumentRelationship(document.getDocumentNumber(), disbursementVoucherDocument.getDocumentNumber(), relationDescription));
            GlobalVariables.getMessageList().add(TemKeyConstants.MESSAGE_DV_IN_ACTION_LIST, disbursementVoucherDocument.getDocumentNumber());
        }
        catch (Exception ex) {
            LOG.error("Could not spawn " + relationDescription + " for reimbursement:" + ex.getMessage(), ex);
        }
    }
    
    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDisbursementService#saveErrorDisbursementVoucher(org.kuali.kfs.fp.document.DisbursementVoucherDocument, org.kuali.kfs.module.tem.document.TravelDocument)
     */
    public void saveErrorDisbursementVoucher(DisbursementVoucherDocument disbursementVoucherDocument, TravelDocument travelDocument) throws Exception {
        businessObjectService.save(disbursementVoucherDocument);
        
        String annotation = "Saved by system in relation to Travel Document: " + travelDocument.getDocumentNumber();
        workflowDocumentService.save(disbursementVoucherDocument.getDocumentHeader().getWorkflowDocument(), annotation);
        
        final String noteText = String.format("DV Document %s is saved in the initiator's action list", disbursementVoucherDocument.getDocumentNumber());                
        final Note noteToAdd = documentService.createNoteFromDocument(travelDocument, noteText);
        documentService.addNoteToDocument(travelDocument, noteToAdd);
        
        travelDocumentService.addAdHocFYIRecipient(disbursementVoucherDocument, travelDocument.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setTravelerService(TravelerService travelerService) {
        this.travelerService = travelerService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    public void setKualiRuleService(KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }

    public void setTravelDocumentService(TravelDocumentService travelDocumentService) {
        this.travelDocumentService = travelDocumentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setAccountingDocumentRelationshipService(AccountingDocumentRelationshipService accountingDocumentRelationshipService) {
        this.accountingDocumentRelationshipService = accountingDocumentRelationshipService;
    }
    

}
