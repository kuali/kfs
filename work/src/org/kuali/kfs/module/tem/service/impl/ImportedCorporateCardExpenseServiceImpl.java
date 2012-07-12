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

import static org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
import static org.kuali.kfs.module.tem.TemConstants.TravelParameters.DOCUMENT_DTL_TYPE;
import static org.kuali.kfs.module.tem.TemConstants.TravelParameters.TRAVEL_DOCUMENTATION_LOCATION_CODE;
import static org.kuali.kfs.module.tem.util.BufferedLogger.debug;
import static org.kuali.kfs.module.tem.util.BufferedLogger.error;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.businessobject.TEMExpense;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TemTravelExpenseTypeCode;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.web.bean.AccountingDistribution;
import org.kuali.kfs.module.tem.service.TEMExpenseService;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.module.tem.util.ExpenseUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AttributedRouteDocumentEvent;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.vnd.VendorPropertyConstants;
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
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;

public class ImportedCorporateCardExpenseServiceImpl extends ExpenseServiceBase implements TEMExpenseService {

    /**
     * @see org.kuali.kfs.module.tem.service.TEMExpenseService#calculateDistributionTotals(org.kuali.kfs.module.tem.document.TravelDocument, java.util.Map, java.util.List)
     */
    @Override
    public void calculateDistributionTotals(TravelDocument document, Map<String, AccountingDistribution> distributionMap, List<? extends TEMExpense> expenses){
        String defaultChartCode = ExpenseUtils.getDefaultChartCode(document);
        for (ImportedExpense expense : (List<ImportedExpense>) expenses) {
            
            if (expense.getExpenseDetails() != null && expense.getExpenseDetails().size() > 0){
                //update the expense detail's card type (if null) to the expense's card type 
                for (ImportedExpense imported : (List<ImportedExpense>)expense.getExpenseDetails()){
                    if (imported.getExpenseParentId() != null && imported.getCardType() == null){
                        imported.setCardType(expense.getCardType());
                    }
                }
                calculateDistributionTotals(document, distributionMap, expense.getExpenseDetails());
            }
            else {
                if (expense.getCardType() != null 
                        && !expense.getCardType().equals(TemConstants.CARD_TYPE_CTS) 
                        && !expense.getNonReimbursable()){
                    expense.refreshReferenceObject("travelExpenseTypeCode");
                    TemTravelExpenseTypeCode code = SpringContext.getBean(TravelExpenseService.class).getExpenseType(expense.getTravelExpenseTypeCodeCode(), 
                            document.getFinancialDocumentTypeCode(), document.getTripTypeCode(), document.getTraveler().getTravelerTypeCode());
                    
                    expense.setTravelExpenseTypeCode(code);
                    String financialObjectCode = expense.getTravelExpenseTypeCode() != null ? expense.getTravelExpenseTypeCode().getFinancialObjectCode() : null;
                    
                    debug("Refreshed importedExpense with expense type code ", expense.getTravelExpenseTypeCode(),
                            " and financialObjectCode ", financialObjectCode);

                    final ObjectCode objCode = getObjectCodeService().getByPrimaryIdForCurrentYear(defaultChartCode, financialObjectCode);
                    if (objCode != null && code != null && !code.isPrepaidExpense()){
                        AccountingDistribution distribution = null;
                        String key = objCode.getCode() + "-" + expense.getCardType();
                        if (distributionMap.containsKey(key)){
                            distributionMap.get(key).setSubTotal(distributionMap.get(key).getSubTotal().add(expense.getConvertedAmount()));
                            distributionMap.get(key).setRemainingAmount(distributionMap.get(key).getRemainingAmount().add(expense.getConvertedAmount()));
                        }
                        else{
                            distribution = new AccountingDistribution();
                            distribution.setObjectCode(objCode.getCode());
                            distribution.setObjectCodeName(objCode.getName());
                            distribution.setCardType(expense.getCardType());
                            distribution.setRemainingAmount(expense.getConvertedAmount());
                            distribution.setSubTotal(expense.getConvertedAmount());
                            distributionMap.put(key, distribution);
                        }
                    }
                }
            }
        }
    }      

    /**
     * @see org.kuali.kfs.module.tem.service.TEMExpenseService#getExpenseDetails(org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public List<? extends TEMExpense> getExpenseDetails(TravelDocument document) {      
        return document.getImportedExpenses();
    }
    
    /**
     * @see org.kuali.kfs.module.tem.service.impl.ExpenseServiceBase#validateExpenseCalculation(org.kuali.kfs.module.tem.businessobject.TEMExpense)
     */
    @Override
    public boolean validateExpenseCalculation(TEMExpense expense){
        return (expense instanceof ImportedExpense)
                && ((ImportedExpense)expense).getCardType() != null
                && !StringUtils.defaultString(((ImportedExpense)expense).getCardType()).equals(TemConstants.CARD_TYPE_CTS);
    }
    
    /**
     * @see org.kuali.kfs.module.tem.service.impl.ExpenseServiceBase#processExpense(org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public void processExpense(TravelDocument travelDocument) {
        GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper(travelDocument.getGeneralLedgerPendingEntries().size()+1);
        //create glpe's for just corp cards;
        for (TemSourceAccountingLine line : (List<TemSourceAccountingLine>)travelDocument.getSourceAccountingLines()){
            if (!line.getCardType().equals(TemConstants.NOT_APPLICABLE)
                    && !line.getCardType().equals(TemConstants.CARD_TYPE_CTS)){
                ExpenseUtils.generateImportedExpenseGeneralLedgerPendingEntries(travelDocument, line, sequenceHelper, false, travelDocument.getFinancialDocumentTypeCode());
            }
        }       
    }

    /**
     * Spawn DV doc(s) for the imported corporate card expenses
     */
    private void createVendorDisbursementVoucher(TravelDocument travelDocument){
        String currentUser = GlobalVariables.getUserSession().getPrincipalName();
        PersonService<Person> personService = SpringContext.getBean(PersonService.class);
        String principalName = personService.getPerson(travelDocument.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId()).getPrincipalName();
        String principalPhoneNumber = personService.getPerson(travelDocument.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId()).getPhoneNumber();
        
        //build map of the accounting line info and amount
        List<TemSourceAccountingLine> lines = travelDocument.getSourceAccountingLines();
        GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper(travelDocument.getGeneralLedgerPendingEntries().size()+1);
        Map<String,KualiDecimal> accountingLineMap = new HashMap<String, KualiDecimal>();
        for (TemSourceAccountingLine line : lines){
           if (!line.getCardType().equals(TemConstants.CARD_TYPE_CTS)
                   && !line.getCardType().equals(TemConstants.NOT_APPLICABLE)){
               String key = line.getCardType();
               KualiDecimal amount = line.getAmount();
               if (accountingLineMap.containsKey(key)){
                   amount = accountingLineMap.get(key).add(line.getAmount());
               }
               accountingLineMap.put(key, amount);
           }
        }
        
        Iterator<String> it = accountingLineMap.keySet().iterator();
        while (it.hasNext()){
            GlobalVariables.setUserSession(new UserSession(principalName));
            String cardType = it.next();
            DisbursementVoucherDocument disbursementVoucherDocument = null;
            try {
                disbursementVoucherDocument = (DisbursementVoucherDocument) getDocumentService().getNewDocument(DisbursementVoucherDocument.class);
            }
            catch (Exception e) {
                error("Error creating new disbursement voucher document: ", e.getMessage());
                throw new RuntimeException("Error creating new disbursement voucher document: " + e.getMessage(), e);
            }
            
            disbursementVoucherDocument.initiateDocument();

            String vendorNumber = "";
            for (HistoricalTravelExpense historicalTravelExpense : travelDocument.getHistoricalTravelExpenses()){
                if (historicalTravelExpense.getCreditCardStagingDataId() != null){
                    String tempCardType = historicalTravelExpense.getCreditCardAgency().getCreditCardType().getFinancialDocumentCreditCardCompanyName();
                    if (tempCardType.equals(cardType)){
                        vendorNumber = historicalTravelExpense.getCreditCardAgency().getVendorNumber();
                        break;
                    }
                }
                
            }
            String vendorNumberID[] = vendorNumber.split("\\-");
            Map<String,String> fieldValues = new HashMap<String,String>();
            fieldValues.put(VendorPropertyConstants.VENDOR_HEADER_GENERATED_ID, vendorNumberID[0]);
            fieldValues.put(VendorPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, vendorNumberID[1]);
            VendorDetail vendor = (VendorDetail) getBusinessObjectService().findByPrimaryKey(VendorDetail.class, fieldValues);
            
            disbursementVoucherDocument.getDvPayeeDetail().setDisbursementVoucherPayeeTypeCode(DisbursementVoucherConstants.DV_PAYEE_TYPE_VENDOR);
            disbursementVoucherDocument.getDocumentHeader().setDocumentDescription(travelDocument.getDocumentHeader().getDocumentDescription());
            disbursementVoucherDocument.getDocumentHeader().setOrganizationDocumentNumber(travelDocument.getTravelDocumentIdentifier());
            
            disbursementVoucherDocument.getDvPayeeDetail().setDocumentNumber(disbursementVoucherDocument.getDocumentNumber());  
            disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeIdNumber(vendor.getVendorNumber());
            
            disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeePersonName(vendor.getVendorName());
            VendorService vendorService = SpringContext.getBean(VendorService.class);
            VendorAddress defaultAddress = vendorService.getVendorDefaultAddress(vendor.getVendorAddresses(), vendor.getVendorHeader().getVendorType().getAddressType().getVendorAddressTypeCode(), "");
            
            disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeLine1Addr(defaultAddress.getVendorLine1Address());
            disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeLine2Addr(defaultAddress.getVendorLine2Address());
            disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeCityName(defaultAddress.getVendorCityName());
            disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeStateCode(defaultAddress.getVendorStateCode());
            disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeZipCode(defaultAddress.getVendorZipCode());
            disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeCountryCode(defaultAddress.getVendorCountryCode());
            
            disbursementVoucherDocument.setDisbVchrCheckTotalAmount(accountingLineMap.get(cardType)); 
            disbursementVoucherDocument.setDisbVchrCheckStubText(travelDocument.getDocumentHeader().getDocumentDescription());
            
            travelDocument.setProfileId(travelDocument.getTemProfileId());
            
            if (travelDocument.getTemProfile().getTravelerType().getCode().equals(TemConstants.EMP_TRAVELER_TYP_CD)){ 
                disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPayeeEmployeeCode(true); 
            }  
            for(TemSourceAccountingLine line: (List<TemSourceAccountingLine>)travelDocument.getSourceAccountingLines()){
                if (line.getCardType().equals(cardType)){
                    SourceAccountingLine newLine = new SourceAccountingLine();
                    newLine.setAccountNumber(line.getAccountNumber());
                    newLine.setChartOfAccountsCode(line.getChartOfAccountsCode());
                    newLine.setFinancialObjectCode(line.getFinancialObjectCode());
                    newLine.setAmount(line.getAmount());
                    newLine.setOrganizationReferenceId("");
                    disbursementVoucherDocument.addSourceAccountingLine(newLine); 
                }
            }
            String paymentReasonCode = getParameterService().getParameterValue(PARAM_NAMESPACE, DOCUMENT_DTL_TYPE, TRAVEL_DOCUMENTATION_LOCATION_CODE);
            disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPaymentReasonCode(paymentReasonCode);
            disbursementVoucherDocument.setDisbVchrContactPersonName(principalName);
            disbursementVoucherDocument.setDisbVchrContactPhoneNumber(principalPhoneNumber);
            disbursementVoucherDocument.setDisbVchrPaymentMethodCode(TemConstants.DisbursementVoucherPaymentMethods.CHECK_ACH_PAYMENT_METHOD_CODE);
            disbursementVoucherDocument.setDisbursementVoucherDocumentationLocationCode(getParameterService().getParameterValue(PARAM_NAMESPACE, DOCUMENT_DTL_TYPE, TRAVEL_DOCUMENTATION_LOCATION_CODE));
            Calendar calendar = getDateTimeService().getCurrentCalendar();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            disbursementVoucherDocument.setDisbursementVoucherDueDate(new java.sql.Date(calendar.getTimeInMillis()));
                        
            //Attempt to blanket approve the document.
            try {
                try {
                    disbursementVoucherDocument.getDocumentHeader().getWorkflowDocument().setTitle("Disbursement Voucher - Vendor Payment - " + travelDocument.getDocumentHeader().getDocumentDescription());
                }
                catch (WorkflowException ex) {
                    error("cannot set title for DV " + disbursementVoucherDocument.getDocumentNumber(), ex);
                    throw new RuntimeException("Error setting DV title: " + disbursementVoucherDocument.getDocumentNumber(), ex);
                }
                
                disbursementVoucherDocument.prepareForSave();
                
                getBusinessObjectService().save(disbursementVoucherDocument);
                
                String relationDescription = travelDocument.getDocumentHeader().getWorkflowDocument().getDocumentType() + " - DV";
                SpringContext.getBean(AccountingDocumentRelationshipService.class).save(new AccountingDocumentRelationship(travelDocument.getDocumentNumber(), disbursementVoucherDocument.getDocumentNumber(), relationDescription));
                
                Note DvNote = getDocumentService().createNoteFromDocument(disbursementVoucherDocument, 
                        "system generated note by " + travelDocument.getFinancialDocumentTypeCode() + " document # " 
                        + travelDocument.getTravelDocumentIdentifier());
                getDocumentService().addNoteToDocument(disbursementVoucherDocument, DvNote);
                KualiRuleService ruleService = SpringContext.getBean(KualiRuleService.class);
                boolean rulePassed = ruleService.applyRules(new AttributedRouteDocumentEvent("", disbursementVoucherDocument));

                if (rulePassed){
                    KualiWorkflowDocument originalWorkflowDocument = disbursementVoucherDocument.getDocumentHeader().getWorkflowDocument();
                    
                    try {
                        // original initiator may not have permission to blanket approve the DV
                        GlobalVariables.setUserSession(new UserSession(KFSConstants.SYSTEM_USER));
                        
                        KualiWorkflowDocument newWorkflowDocument = getWorkflowDocumentService().createWorkflowDocument(Long.valueOf(disbursementVoucherDocument.getDocumentNumber()), GlobalVariables.getUserSession().getPerson());
                        newWorkflowDocument.setTitle(originalWorkflowDocument.getTitle());
                        
                        disbursementVoucherDocument.getDocumentHeader().setWorkflowDocument(newWorkflowDocument);
                    
                        String annotation = "Blanket Approved by system in relation to Travel Auth Document: " + travelDocument.getDocumentNumber();
                        getWorkflowDocumentService().blanketApprove(disbursementVoucherDocument.getDocumentHeader().getWorkflowDocument(), annotation, null); 
                    }
                    catch(Exception ex1){
                        ex1.printStackTrace();
                        saveDisbursementVoucher(disbursementVoucherDocument,travelDocument);
                    }
                    finally {
                        disbursementVoucherDocument.getDocumentHeader().setWorkflowDocument(originalWorkflowDocument);
                        GlobalVariables.setUserSession(new UserSession(currentUser));
                    }            
                }
                else{
                    saveDisbursementVoucher(disbursementVoucherDocument,travelDocument);
                }
                final String noteText = String.format("DV Document %s was system generated and blanket approved", disbursementVoucherDocument.getDocumentNumber());                
                final Note noteToAdd = getDocumentService().createNoteFromDocument(travelDocument, noteText);
                getDocumentService().addNoteToDocument(travelDocument, noteToAdd);
                
            }
            catch (Exception ex1) {
                // if we can't save DV, need to stop processing
                GlobalVariables.setUserSession(new UserSession(currentUser));
                error("cannot save DV ", disbursementVoucherDocument.getDocumentNumber(), ex1);
                throw new RuntimeException("cannot save DV " + disbursementVoucherDocument.getDocumentNumber(), ex1);
            }
        }
    }
    
    /**
     * @see org.kuali.kfs.module.tem.service.impl.ExpenseServiceBase#updateExpense(org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public void updateExpense(TravelDocument travelDocument) {
        List<HistoricalTravelExpense> historicalTravelExpenses = travelDocument.getHistoricalTravelExpenses();
        for (HistoricalTravelExpense historicalTravelExpense : historicalTravelExpenses){
            if (historicalTravelExpense.getCreditCardStagingDataId() != null){
                long time = (new java.util.Date()).getTime();
                historicalTravelExpense.setReconciliationDate(new Date(time));
                historicalTravelExpense.setReconciled(TemConstants.ReconciledCodes.RECONCILED);
            }
        }
        getBusinessObjectService().save(historicalTravelExpenses);
        boolean spawnDV = getParameterService().getIndicatorParameter(PARAM_NAMESPACE, TemConstants.TravelParameters.DOCUMENT_DTL_TYPE, TemConstants.TravelParameters.ENABLE_CORP_CARD_PAYMENT_DV_IND);
        if (spawnDV){
            createVendorDisbursementVoucher(travelDocument);
        }
    }

    /**
     * 
     * @param disbursementVoucherDocument
     * @param travelDocument
     * @throws Exception
     */
    private void saveDisbursementVoucher(DisbursementVoucherDocument disbursementVoucherDocument, TravelDocument travelDocument) throws Exception{
        String annotation = "Saved by system in relation to Travel Document: " + travelDocument.getDocumentNumber();
        getWorkflowDocumentService().save(disbursementVoucherDocument.getDocumentHeader().getWorkflowDocument(), annotation);
        
        final String noteText = String.format("DV Document %s is saved in the initiator's action list to process travel advance", disbursementVoucherDocument.getDocumentNumber());                
        final Note noteToAdd = getDocumentService().createNoteFromDocument(travelDocument, noteText);
        getDocumentService().addNoteToDocument(travelDocument, noteToAdd);
        getTravelDocumentService().addAdHocFYIRecipient(disbursementVoucherDocument, travelDocument.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
    }
    
    private DocumentService getDocumentService(){
        return SpringContext.getBean(DocumentService.class);
    }
    
    private DateTimeService getDateTimeService() {
        return SpringContext.getBean(DateTimeService.class);
    }
    
    private WorkflowDocumentService getWorkflowDocumentService() {
        return SpringContext.getBean(WorkflowDocumentService.class);
    }
    
    private GeneralLedgerPendingEntryService getGeneralLedgerPendingEntryService(){
        return SpringContext.getBean(GeneralLedgerPendingEntryService.class);
    }

}
