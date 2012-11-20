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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.DisburseType;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.document.TEMReimbursementDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService;
import org.kuali.kfs.module.tem.document.service.TravelDisbursementService;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.web.bean.AccountingLineDistributionKey;
import org.kuali.kfs.module.tem.service.AccountingDistributionService;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
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
    protected AccountingDistributionService accountingDistributionService;
    protected ParameterService parameterService;
    protected WorkflowDocumentService workflowDocumentService;
    protected KualiRuleService kualiRuleService;
    protected TravelDocumentService travelDocumentService;
    
    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDisbursementService#populateReimbursableDisbursementVoucherFields(org.kuali.kfs.fp.document.DisbursementVoucherDocument, org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public void populateReimbursableDisbursementVoucherFields(DisbursementVoucherDocument disbursementVoucherDocument, TravelDocument document){
        
        disbursementVoucherDocument.setDisbVchrCheckStubText(StringUtils.defaultString(document.getDocumentTitle()));              
        disbursementVoucherDocument.getDocumentHeader().setDocumentDescription("Generated for " + travelDocumentService.getDocumentType(document) +" doc: " + StringUtils.defaultString(document.getDocumentTitle(), document.getTravelDocumentIdentifier()));
        travelDocumentService.trimFinancialSystemDocumentHeader(disbursementVoucherDocument.getDocumentHeader());
        disbursementVoucherDocument.getDvPayeeDetail().setDocumentNumber(disbursementVoucherDocument.getDocumentNumber());
        disbursementVoucherDocument.getDocumentHeader().setOrganizationDocumentNumber(document.getTravelDocumentIdentifier());
        
        //due date is set to the next day
        setDVDocNextDueDay(disbursementVoucherDocument);
        
        try {
            disbursementVoucherDocument.getDocumentHeader().getWorkflowDocument().setTitle("Disbursement Voucher - " + document.getDocumentHeader().getDocumentDescription());
        }
        catch (WorkflowException ex) {
            LOG.error("cannot set title for DV " + disbursementVoucherDocument.getDocumentNumber(), ex);
            throw new RuntimeException("Error setting DV title: " + disbursementVoucherDocument.getDocumentNumber(), ex);
        }
        disbursementVoucherDocument.initiateDocument();
        Person initiator = SpringContext.getBean(PersonService.class).getPerson(document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
        
        disbursementVoucherDocument.setDisbVchrContactPersonName(initiator.getPrincipalName());
        disbursementVoucherDocument.setDisbVchrContactPhoneNumber(initiator.getPhoneNumber());

        //if traveler has is an employee data, default to use DV Payee Employee type and employee Id, otherwise use Customer type and customer number
        if (travelerService.isEmployee(document.getTraveler())){
            disbursementVoucherDocument.getDvPayeeDetail().setDisbursementVoucherPayeeTypeCode(DisbursementVoucherConstants.DV_PAYEE_TYPE_EMPLOYEE); 
            document.setProfileId(document.getTemProfileId());
            disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeIdNumber(document.getTemProfile().getEmployeeId());
        }else{
            disbursementVoucherDocument.getDvPayeeDetail().setDisbursementVoucherPayeeTypeCode(DisbursementVoucherConstants.DV_PAYEE_TYPE_CUSTOMER); 
            disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeIdNumber(document.getTraveler().getCustomerNumber());
        }
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

        //TA's advance payment is done with custom distribution
        if (document.hasCustomDVDistribution()){
            // disbursement voucher distribution will be set by the document in the caller function
        }else {
            // set accounting
            KualiDecimal totalAmount = KualiDecimal.ZERO;
            for (SourceAccountingLine line : document.getReimbursableSourceAccountingLines()) {
//                SourceAccountingLine accountingLine = new SourceAccountingLine();
//                  
//                accountingLine.setChartOfAccountsCode(line.getChartOfAccountsCode());
//                accountingLine.setAccountNumber(line.getAccountNumber());
//                accountingLine.setFinancialObjectCode(line.getFinancialObjectCode());              
//                accountingLine.setFinancialSubObjectCode(line.getFinancialSubObjectCode());
//                accountingLine.setSubAccountNumber(line.getSubAccountNumber());
//                accountingLine.setAmount(line.getAmount());
//                accountingLine.setPostingYear(disbursementVoucherDocument.getPostingYear());
//                accountingLine.setDocumentNumber(disbursementVoucherDocument.getDocumentNumber());
//    
//                disbursementVoucherDocument.addSourceAccountingLine(accountingLine);
                totalAmount = totalAmount.add(line.getAmount());
            }
            
            //All of reimbursement will be paid to DV through the single DV clearing account
            SourceAccountingLine accountingLine = getTravelClearingGLPESourceDetail();
            accountingLine.setPostingYear(disbursementVoucherDocument.getPostingYear());
            accountingLine.setDocumentNumber(disbursementVoucherDocument.getDocumentNumber());
            accountingLine.setAmount(totalAmount);
            disbursementVoucherDocument.addSourceAccountingLine(accountingLine);
            
            //change the DV's total to the total of accounting lines
            disbursementVoucherDocument.setDisbVchrCheckTotalAmount(totalAmount);
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDisbursementService#getTravelClearingGLPESourceDetail()
     */
    @Override
    public SourceAccountingLine getTravelClearingGLPESourceDetail() {
        
        SourceAccountingLine sourceDetail = new SourceAccountingLine();
        
        final String dvClearingChart = parameterService.getParameterValue(TemParameterConstants.TEM_REIMBURSEMENT.class, TravelReimbursementParameters.TRAVEL_DV_CLEARING_CHART_CODE);
        final String dvClearingAccount = parameterService.getParameterValue(TemParameterConstants.TEM_REIMBURSEMENT.class, TravelReimbursementParameters.TRAVEL_DV_CLEARING_ACCOUNT_NBR);
        final String dvClearingObject = parameterService.getParameterValue(TemParameterConstants.TEM_REIMBURSEMENT.class, TravelReimbursementParameters.TRAVEL_DV_CLEARING_OBJECT_CODE);

        sourceDetail.setChartOfAccountsCode(dvClearingChart);
        sourceDetail.setAccountNumber(dvClearingAccount);
        sourceDetail.setFinancialObjectCode(dvClearingObject);
        
        return sourceDetail;
    }
    
    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDisbursementService#populateImportedCorpCardDisbursementVoucherFields(org.kuali.kfs.fp.document.DisbursementVoucherDocument, org.kuali.kfs.module.tem.document.TravelDocument, java.lang.String)
     */
    @Override
    public void populateImportedCorpCardDisbursementVoucherFields(DisbursementVoucherDocument disbursementVoucherDocument, TravelDocument document, String cardAgencyType){

        Person principal = SpringContext.getBean(PersonService.class).getPerson(document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
        
        disbursementVoucherDocument.initiateDocument();

        String vendorNumber = "";
        for (HistoricalTravelExpense historicalTravelExpense : document.getHistoricalTravelExpenses()){
            if (historicalTravelExpense.isCreditCardTravelExpense()){
                if (historicalTravelExpense.getCreditCardAgency().getTravelCardTypeCode().equals(cardAgencyType)){
                    vendorNumber = historicalTravelExpense.getCreditCardAgency().getVendorNumber();
                    break;
                }
            }
        }
        setDVDocNextDueDay(disbursementVoucherDocument);
        
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
     * Set due date of the DV doc to the next day
     * 
     * @param disbursementVoucherDocument
     */
    private void setDVDocNextDueDay(DisbursementVoucherDocument disbursementVoucherDocument){
        Calendar calendar = dateTimeService.getCurrentCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        disbursementVoucherDocument.setDisbursementVoucherDueDate(new java.sql.Date(calendar.getTimeInMillis()));
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDisbursementService#createAndApproveDisbursementVoucherDocument(org.kuali.kfs.module.tem.TemConstants.DisburseType, org.kuali.kfs.module.tem.document.TravelDocument, java.lang.String)
     */
    @Override
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

            if (!(TemConstants.DisbursementVoucherPaymentMethods.WIRE_TRANSFER_PAYMENT_METHOD_CODE.equals(disbursementVoucherDocument.getDisbVchrPaymentMethodCode())
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
        GlobalVariables.setUserSession(new UserSession(currentUser));
        
        return disbursementVoucherDocument;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDisbursementService#createAndApproveDisbursementVoucherDocument(org.kuali.kfs.module.tem.TemConstants.DisburseType, org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public DisbursementVoucherDocument createAndApproveDisbursementVoucherDocument(DisburseType type, TravelDocument document){
        return  createAndApproveDisbursementVoucherDocument(type, document, null);
    }
    
    /**
     * Since there is no difference between TEMReimbursement and TravelAdvance type disbursement voucher,
     * use the same implementation.
     * 
     * @param document
     */
    private void processTravelDocumentDV(TravelDocument document){
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
     * @see org.kuali.kfs.module.tem.document.service.TravelDisbursementService#processTEMReimbursementDV(org.kuali.kfs.module.tem.document.TEMReimbursementDocument)
     */
    @Override
    public void processTEMReimbursementDV(TEMReimbursementDocument document){
        processTravelDocumentDV(document);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDisbursementService#processTEMReimbursementDV(org.kuali.kfs.module.tem.document.TEMReimbursementDocument)
     */
    @Override
    public void processTravelAdvanceDV(TravelAuthorizationDocument document){
        processTravelDocumentDV(document);
    }
    
    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDisbursementService#saveErrorDisbursementVoucher(org.kuali.kfs.fp.document.DisbursementVoucherDocument, org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public void saveErrorDisbursementVoucher(DisbursementVoucherDocument disbursementVoucherDocument, TravelDocument travelDocument) throws Exception {
        businessObjectService.save(disbursementVoucherDocument);
        
        String annotation = "Saved by system in relation to Travel Document: " + travelDocument.getDocumentNumber();
        workflowDocumentService.save(disbursementVoucherDocument.getDocumentHeader().getWorkflowDocument(), annotation);
        
        final String noteText = String.format("DV Document %s is saved in the initiator's action list", disbursementVoucherDocument.getDocumentNumber());                
        final Note noteToAdd = documentService.createNoteFromDocument(travelDocument, noteText);
        documentService.addNoteToDocument(travelDocument, noteToAdd);
        
        travelDocumentService.addAdHocFYIRecipient(disbursementVoucherDocument, travelDocument.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
    }
    
    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelDisbursementService#redistributeDisbursementAccountingLine(org.kuali.kfs.fp.document.DisbursementVoucherDocument, java.util.List)
     */
    @Override
    public void redistributeDisbursementAccountingLine(DisbursementVoucherDocument disbursementVoucherDocument, List<SourceAccountingLine> sourceAccountingLines) {
        Map<AccountingLineDistributionKey, KualiDecimal> distributionPercentMap = accountingDistributionService.calculateAccountingLineDistributionPercent(sourceAccountingLines);
        
        //** the DV's DisbVchrCheckTotalAmount MUST be set already
        KualiDecimal dvTotal = disbursementVoucherDocument.getDisbVchrCheckTotalAmount();
        KualiDecimal remainder = dvTotal;
        
        AccountingLineDistributionKey key;
        //traverse through the DV's source accounting lines and look for the percentage from the map to redistribute by the percent
        int index = 0;
        
        for (SourceAccountingLine line : (List<SourceAccountingLine>)disbursementVoucherDocument.getSourceAccountingLines()){
            key = new AccountingLineDistributionKey(line);
            
            //when we reach the end of the list, do not apply the amount through percentage, but use the remainders directly
            if(disbursementVoucherDocument.getSourceAccountingLines().size() == ++index){ 
                line.setAmount(remainder);
            }else{
                line.setAmount(dvTotal.multiply(distributionPercentMap.get(key)));
                remainder = remainder.subtract(line.getAmount());
            }
        }
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
