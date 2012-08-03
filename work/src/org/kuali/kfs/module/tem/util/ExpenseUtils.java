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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TEMProfileProperties;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.businessobject.TEMExpense;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TemTravelExpenseTypeCode;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectService;
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
            historicalTravelExpense.refreshReferenceObject(TemPropertyConstants.CREDIT_CARD_AGENCY);
            historicalTravelExpense.refreshReferenceObject(TemPropertyConstants.AGENCY_STAGING_DATA);
            historicalTravelExpense.refreshReferenceObject(TemPropertyConstants.CREDIT_CARD_STAGING_DATA);
            historicalTravelExpense.getCreditCardAgency().refreshReferenceObject(TemPropertyConstants.TRAVEL_CARD_TYPE);
            
            historicalTravelExpense.setDocumentNumber(travelDocument.getDocumentNumber());            
           
            ImportedExpense importedExpense = new ImportedExpense();
            if (historicalTravelExpense.isAgencyTravelExpense()){
                importedExpense.setCardType(TemConstants.TRAVEL_TYPE_CTS);
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
