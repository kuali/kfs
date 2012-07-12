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
package org.kuali.kfs.module.tem.util;

import static org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
import static org.kuali.kfs.module.tem.TemConstants.TravelParameters.DOCUMENT_DTL_TYPE;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.DEFAULT_CHART_CODE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TEMProfileProperties;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.businessobject.TEMExpense;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TemTravelExpenseTypeCode;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.web.bean.TravelMvcWrapperBean;
import org.kuali.kfs.module.tem.document.web.struts.TravelFormBase;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.module.tem.service.impl.ImportedCTSExpenseServiceImpl;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KualiDecimal;

public class ExpenseUtils {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExpenseUtils.class);
    public static KualiDecimal getExpenseDetailsTotal(TEMExpense expense){
        KualiDecimal detailTotal = KualiDecimal.ZERO;
        for (TEMExpense tempExpense : expense.getExpenseDetails()){
            detailTotal = detailTotal.add(tempExpense.getExpenseAmount());
        } 
        
        return detailTotal;
    }

    
    public static List<ImportedExpense> convertHistoricalToImportedExpense(List<HistoricalTravelExpense> historicalTravelExpenses, TravelDocument travelDocument){
        List<ImportedExpense> expenses = new ArrayList<ImportedExpense>();
        BusinessObjectService service = SpringContext.getBean(BusinessObjectService.class);
        for (HistoricalTravelExpense historicalTravelExpense : historicalTravelExpenses){
            int i = 0;
            historicalTravelExpense.refreshReferenceObject("creditCardAgency");
            historicalTravelExpense.refreshReferenceObject("agencyStagingData");
            historicalTravelExpense.refreshReferenceObject("creditCardStagingData");
            historicalTravelExpense.getCreditCardAgency().refreshReferenceObject("creditCardType");
            
            historicalTravelExpense.setDocumentNumber(travelDocument.getDocumentNumber());            
           
            ImportedExpense importedExpense = new ImportedExpense();
            if (historicalTravelExpense.isAgencyTravelExpense()){
                importedExpense.setCardType(TemConstants.CARD_TYPE_CTS);
            }
            else{
                importedExpense.setCardType(historicalTravelExpense.getCreditCardAgency().getCreditCardOrAgencyCode());
            }
            
            importedExpense.setNonReimbursable(!historicalTravelExpense.getReimbursable());
            importedExpense.setMissingReceipt(historicalTravelExpense.getMissingReceipt());
            importedExpense.setExpenseDate(historicalTravelExpense.getTransactionPostingDate());
            importedExpense.setCurrencyRate(historicalTravelExpense.getCurrencyRate());
            importedExpense.setConvertedAmount(historicalTravelExpense.getConvertedAmount());            
            importedExpense.setExpenseAmount(historicalTravelExpense.getAmount());
            importedExpense.setTravelCompanyCodeName(historicalTravelExpense.getTravelCompany());
            
            TemTravelExpenseTypeCode travelExpenseTypeCode = SpringContext.getBean(TravelExpenseService.class).getExpenseType(historicalTravelExpense.getTravelExpenseType(), travelDocument.getDocumentTypeName(), travelDocument.getTripTypeCode(), travelDocument.getTraveler().getTravelerTypeCode());
            
            if (travelExpenseTypeCode != null) {
                historicalTravelExpense.setDescription(travelExpenseTypeCode.getName());
                importedExpense.setDescription(historicalTravelExpense.getDescription());
                importedExpense.setTravelExpenseTypeCode(travelExpenseTypeCode);
                importedExpense.setTravelExpenseTypeCodeId(travelExpenseTypeCode.getTravelExpenseTypeCodeId());
                importedExpense.setTravelCompanyCodeCode(historicalTravelExpense.getTravelExpenseType());
            }
            
            importedExpense.setHistoricalTravelExpenseId(historicalTravelExpense.getId());
            
            expenses.add(importedExpense);
            historicalTravelExpense.setAssigned(true);                                  
            historicalTravelExpense.setDocumentType(travelDocument.getFinancialDocumentTypeCode());
        }
        service.save(historicalTravelExpenses);
        return expenses;
    }
    
    public static String getDefaultChartCode(TravelDocument document){
        String defaultChartCode = null;
        if (document.getTemProfile() == null && document.getTemProfileId() != null) {
            Map<String, Object> primaryKeys = new HashMap<String, Object>();
            primaryKeys.put(TEMProfileProperties.PROFILE_ID, document.getTemProfileId().toString());
            TEMProfile profile = (TEMProfile) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(TEMProfile.class, primaryKeys);
            defaultChartCode = profile.getDefaultChartCode();
        }
        else if (document.getTemProfile() != null) {
            defaultChartCode = document.getTemProfile().getDefaultChartCode();
        }
        
        return defaultChartCode;
    }

    public static void assignExpense(Long historicalTravelExpenseId, String tripId, String documentNumber, String documentType, boolean isAssigned){
        BusinessObjectService service = SpringContext.getBean(BusinessObjectService.class);
        HistoricalTravelExpense historicalTravelExpense = service.findBySinglePrimaryKey(HistoricalTravelExpense.class, historicalTravelExpenseId);
        historicalTravelExpense.setAssigned(isAssigned);
        historicalTravelExpense.setTripId(tripId);
        historicalTravelExpense.setDocumentNumber(documentNumber);
        historicalTravelExpense.setDocumentType(documentType);
        service.save(historicalTravelExpense);        
    }
    
    public static boolean generateImportedExpenseGeneralLedgerPendingEntries(TravelDocument travelDocument, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, boolean isCredit, String docType) {
        LOG.debug("processGenerateGeneralLedgerPendingEntries(TravelDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper, boolean) - start");

        /*Map<String,Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE,glpeSourceDetail.getFinancialObjectCode());
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,glpeSourceDetail.getChartOfAccountsCode());
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,((GeneralLedgerPendingEntrySource)travelDocument).getPostingYear());
        BusinessObjectService service = SpringContext.getBean(BusinessObjectService.class);
        ObjectCode objectCode = (ObjectCode) service.findByPrimaryKey(ObjectCode.class, fieldValues);*/
        glpeSourceDetail.getObjectCode().setChartOfAccountsCode(glpeSourceDetail.getChartOfAccountsCode());
        glpeSourceDetail.getObjectCode().setFinancialObjectCode(glpeSourceDetail.getFinancialObjectCode());
        glpeSourceDetail.getObjectCode().setUniversityFiscalYear(glpeSourceDetail.getPostingYear());
        
        //((TemSourceAccountingLine)glpeSourceDetail).setObjectCode(objectCode);
        
        
        ((TemSourceAccountingLine)glpeSourceDetail).getObjectTypeCode();
        // handle the explicit entry
        // create a reference to the explicitEntry to be populated, so we can pass to the offset method later
        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
        processExplicitGeneralLedgerPendingEntry(travelDocument, sequenceHelper, glpeSourceDetail, explicitEntry);
        
        explicitEntry.setFinancialDocumentTypeCode(docType);
        explicitEntry.setTransactionLedgerEntryDescription(TemConstants.TEM_IMPORTED_GLPE_DESC);
        explicitEntry.setOrganizationDocumentNumber(travelDocument.getTravelDocumentIdentifier());
        explicitEntry.setDocumentNumber(travelDocument.getDocumentNumber());
        explicitEntry.setOrganizationReferenceId(travelDocument.getFinancialDocumentTypeCode()+TemConstants.IMPORTED_FLAG);
        if (isCredit){
            explicitEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            explicitEntry.setProjectCode(null);
        }
        else{
            explicitEntry.setProjectCode(travelDocument.getTemProfileId().toString());
            explicitEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
        }
        
        // increment the sequence counter
        sequenceHelper.increment();

        // handle the offset entry
        GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(explicitEntry);
        explicitEntry.setOrganizationReferenceId(travelDocument.getFinancialDocumentTypeCode()+TemConstants.IMPORTED_FLAG);
        boolean success = processOffsetGeneralLedgerPendingEntry(travelDocument, sequenceHelper, glpeSourceDetail, explicitEntry, offsetEntry);

        LOG.debug("processGenerateGeneralLedgerPendingEntries(TravelDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper, boolean) - end");
        return success;
    }
    
    private static void processExplicitGeneralLedgerPendingEntry(TravelDocument travelDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntry explicitEntry) {
        LOG.debug("processExplicitGeneralLedgerPendingEntry(TravelDocument, GeneralLedgerPendingEntrySequenceHelper, AccountingLine, GeneralLedgerPendingEntry) - start");

        SpringContext.getBean(GeneralLedgerPendingEntryService.class).populateExplicitGeneralLedgerPendingEntry(travelDocument, glpeSourceDetail, sequenceHelper, explicitEntry);

        travelDocument.addPendingEntry(explicitEntry);

        LOG.debug("processExplicitGeneralLedgerPendingEntry(AccountingDocument, GeneralLedgerPendingEntrySequenceHelper, AccountingLine, GeneralLedgerPendingEntry) - end");
    }
    
    private static boolean processOffsetGeneralLedgerPendingEntry(TravelDocument travelDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        LOG.debug("processOffsetGeneralLedgerPendingEntry(TravelDocument, GeneralLedgerPendingEntrySequenceHelper, AccountingLine, GeneralLedgerPendingEntry, GeneralLedgerPendingEntry) - start");
        
        // populate the offset entry
        boolean success = SpringContext.getBean(GeneralLedgerPendingEntryService.class).populateOffsetGeneralLedgerPendingEntry(travelDocument.getPostingYear(), explicitEntry, sequenceHelper, offsetEntry);
        travelDocument.addPendingEntry(offsetEntry);
        sequenceHelper.increment();
        LOG.debug("processOffsetGeneralLedgerPendingEntry(TravelDocument, GeneralLedgerPendingEntrySequenceHelper, AccountingLine, GeneralLedgerPendingEntry, GeneralLedgerPendingEntry) - end");
        return success;
    }
    
    public static void calculateMileage(List<ActualExpense> actualExpenses){
        for (ActualExpense actualExpense : actualExpenses){
            if (actualExpense.getTravelCompanyCodeCode() != null && actualExpense.getTravelExpenseTypeCode().getCode().equals(TemConstants.ExpenseTypes.MILEAGE)){
                actualExpense.setCurrencyRate(new KualiDecimal(1));
                KualiDecimal total = KualiDecimal.ZERO;
                for (TEMExpense detail : actualExpense.getExpenseDetails()){
                    ActualExpense detailExpense = (ActualExpense) detail;
                    KualiDecimal mileage = (new KualiDecimal(detailExpense.getMiles())).multiply(detailExpense.getMileageRate().getRate());
                    detailExpense.setExpenseAmount(mileage);
                    detailExpense.setConvertedAmount(mileage);
                    total = total.add(detailExpense.getExpenseAmount());
                    detailExpense.setCurrencyRate(actualExpense.getCurrencyRate());
                    detailExpense.setTravelExpenseTypeCodeId(actualExpense.getTravelExpenseTypeCodeId());
                }
                actualExpense.setExpenseAmount(total);
            }
        }
    }
    
}
