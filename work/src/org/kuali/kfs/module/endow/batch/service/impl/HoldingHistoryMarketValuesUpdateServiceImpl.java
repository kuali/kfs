/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.module.endow.batch.service.impl;

import java.math.BigDecimal;
import java.util.Collection;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.batch.service.HoldingHistoryMarketValuesUpdateService;
import org.kuali.kfs.module.endow.batch.service.ProcessFeeTransactionsService;
import org.kuali.kfs.module.endow.businessobject.EndowmentExceptionReportHeader;
import org.kuali.kfs.module.endow.businessobject.HoldingHistory;
import org.kuali.kfs.module.endow.document.HoldingHistoryValueAdjustmentDocument;
import org.kuali.kfs.module.endow.document.service.HoldingHistoryService;
import org.kuali.kfs.module.endow.document.service.HoldingHistoryValueAdjustmentDocumentService;
import org.kuali.kfs.module.endow.util.KEMCalculationRoundingHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the ProcessFeeTransactionsService.
 */
@Transactional
public class HoldingHistoryMarketValuesUpdateServiceImpl implements HoldingHistoryMarketValuesUpdateService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(HoldingHistoryMarketValuesUpdateServiceImpl.class);
    public static final String WORKFLOW_DOCUMENT_HEADER_ID_SEARCH_RESULT_KEY = "routeHeaderId";
    
    private DocumentService documentService;
    private HoldingHistoryService holdingHistoryService;
    private HoldingHistoryValueAdjustmentDocumentService holdingHistoryValueAdjustmentDocumentService;

    private PersonService<Person> personService;
    private ReportWriterService holdingHistoryMarketValuesExceptionReportWriterService;
    
    EndowmentExceptionReportHeader holdingHistoryMarketValueExceptionReportHeader;
    EndowmentExceptionReportHeader holdingHistoryMarketValueExceptionRowValues;
    EndowmentExceptionReportHeader holdingHistoryMarketValueExceptionRowReason;    
    
    /**
     * Constructs a HoldingHistoryMarketValuesUpdateServiceImpl instance
     */
    public HoldingHistoryMarketValuesUpdateServiceImpl() {
        holdingHistoryMarketValueExceptionReportHeader = new EndowmentExceptionReportHeader();
        holdingHistoryMarketValueExceptionRowValues = new EndowmentExceptionReportHeader();
        holdingHistoryMarketValueExceptionRowReason = new EndowmentExceptionReportHeader();                
    }

    /**
     * Gathers all documents that are in ENROUTE status and auto disapproves them.
     * @see org.kuali.kfs.sys.batch.service.autoDisapproveDocumentsInEnrouteStatus#autoDisapproveDocumentsInEnrouteStatus()
     */
    public boolean updateHoldingHistoryMarketValues() {
        boolean success = true ;
        
        LOG.debug("processUpdateHistoryMarketValues() started");
        
        //writes the exception report header
        holdingHistoryMarketValuesExceptionReportWriterService.writeNewLines(1);
        holdingHistoryMarketValueExceptionReportHeader.setColumnHeading1("Documnet Type");
        holdingHistoryMarketValueExceptionReportHeader.setColumnHeading2("Security Id");
        holdingHistoryMarketValueExceptionReportHeader.setColumnHeading3("KEMID");
        
        holdingHistoryMarketValuesExceptionReportWriterService.writeTableHeader(holdingHistoryMarketValueExceptionReportHeader);
        
        //update the market values for the holding history value adjustment documents.
        success = processUpdateHoldingHistoryMarketValues();
        
        return success;
    }
    
    /**
     * This method will use documentsearchcriteriaDTO to search for the documents that are in Final status
     * @return true if update is successful else false
     */
    protected boolean processUpdateHoldingHistoryMarketValues() {
        boolean success = true;
        
        Collection<HoldingHistoryValueAdjustmentDocument> holdingHistoryValueAdjustmentDocuments = holdingHistoryValueAdjustmentDocumentService.getHoldingHistoryValueAdjustmentDocument(EndowConstants.HoldingHistoryValueAdjustmentDocument.TRANSACTION_POSTED_NO);
        
        if (holdingHistoryValueAdjustmentDocuments.isEmpty()) {
            setExceptionReportTableRowReason("There are no records in Holding History Value Adjustment Documents table.  The market values updates process is not executed");
            holdingHistoryMarketValuesExceptionReportWriterService.writeTableRow(holdingHistoryMarketValueExceptionRowReason);
            return success;
        }
        
        for (HoldingHistoryValueAdjustmentDocument holdingHistoryValueAdjustmentDocument : holdingHistoryValueAdjustmentDocuments) {
            String documentHeaderId = holdingHistoryValueAdjustmentDocument.getDocumentNumber();

            Document document = findDocumentForMarketValueUpdate(documentHeaderId);
            
            if (document != null) {
                if (document.getDocumentHeader().getWorkflowDocument().stateIsFinal()) {
                    // the state of the document is final.. so processing the document.
                    if (checkIfDocumentEligibleForUpdate(document)) {
                        if (updateHoldingHistoryRecords(holdingHistoryValueAdjustmentDocument)) {
                            //update the HoldingHistoryValueAdjustmentDocument's transactionPosted column to Y
                            holdingHistoryValueAdjustmentDocument.setTransactionPosted(true);
                            if (!holdingHistoryValueAdjustmentDocumentService.saveHoldingHistory(holdingHistoryValueAdjustmentDocument)) {
                                holdingHistoryMarketValueExceptionRowValues.setColumnHeading3("");
                                writeTableRowAndTableReason(holdingHistoryValueAdjustmentDocument, "Unable to set Transaction Posted flag in Holding History Value Adjustment Document.");
                            }
                        }
                     }
                }
                else {
                    holdingHistoryMarketValueExceptionRowValues.setColumnHeading3("");                    
                    writeTableRowAndTableReason(holdingHistoryValueAdjustmentDocument, "Holding History Value Adjustment document status is NOT FINAL. - Skipped updating the Market Values.");
                }
            }
            else {
                LOG.error("Document is NULL.  It should never have been null");
                holdingHistoryMarketValueExceptionRowValues.setColumnHeading3("");                
                writeTableRowAndTableReason(holdingHistoryValueAdjustmentDocument, "Unable to find if the document status is FINAL.  The document does not exist in the workflow.");                
            }
        }
        
        return success;
    }
        
    /**
     * This method will check the document's transaction posted boolean field to see if the document is
     * eligible for market value update process.
     * @param document
     * @return true if transactionPosted is true, else return false
     */
    protected boolean checkIfDocumentEligibleForUpdate(Document document) {
        boolean documentEligible = true;
        
        HoldingHistoryValueAdjustmentDocument ehva = (HoldingHistoryValueAdjustmentDocument) document;

        getExceptionReportTableRowValues(ehva);
        
        String documentTypeName = document.getDocumentHeader().getWorkflowDocument().getDocumentType();
     
        if (!documentTypeName.equalsIgnoreCase(EndowConstants.FeeMethod.ENDOWMENT_HISTORY_VALUE_ADJUSTMENT)) {
            holdingHistoryMarketValueExceptionRowValues.setColumnHeading3("");            
            writeTableRowAndTableReason(ehva, "Document Type = " + documentTypeName + " - only document types EHVA are allowed to be processed by this job.");

            return false;
        }
        
        return documentEligible;
    }
    
    /**
     * writes out the table row values for document type, secuityId, kemId and then writes the reason row and inserts a blank line
     * @param ehva the holding history value adjustment document
     * @param reasonMessage the reason message
     */
    protected void writeTableRowAndTableReason(HoldingHistoryValueAdjustmentDocument ehva, String reasonMessage) {
        getExceptionReportTableRowValues(ehva);
        
        holdingHistoryMarketValuesExceptionReportWriterService.writeTableRow(holdingHistoryMarketValueExceptionRowValues);            
        setExceptionReportTableRowReason(reasonMessage);
        holdingHistoryMarketValuesExceptionReportWriterService.writeTableRow(holdingHistoryMarketValueExceptionRowReason);            
        holdingHistoryMarketValuesExceptionReportWriterService.writeNewLines(1);
    }
    
    /**
     * gets the values for document type, securityId, and kemdId that will be written to the exception report
     * @param ehva
     */
    protected void getExceptionReportTableRowValues(HoldingHistoryValueAdjustmentDocument ehva) {
        String documentTypeName = ehva.getDocumentHeader().getWorkflowDocument().getDocumentType();
        
        holdingHistoryMarketValueExceptionRowValues.setColumnHeading1(documentTypeName);
        holdingHistoryMarketValueExceptionRowValues.setColumnHeading2(ehva.getSecurityId());
        holdingHistoryMarketValueExceptionRowValues.setColumnHeading3(holdingHistoryService.getKemIdFromHoldingHistory(ehva.getSecurityId()));
    }
    
    /**
     * sets the exception message with the passed in value.
     * @param reasonForException The reason that will be set in the exception report
     */
    protected void setExceptionReportTableRowReason(String reasonForException) {
        
        holdingHistoryMarketValueExceptionRowReason.setColumnHeading1("Reason: " + reasonForException);
        holdingHistoryMarketValueExceptionRowReason.setColumnHeading2("");
        holdingHistoryMarketValueExceptionRowReason.setColumnHeading3("");
    }

    /**
     * This method updates the holding history records.
     * The monthEndDateId is retrieved from END_ME_DT_T using the monthEndDate.  The holding history records 
     * are fetched for matching securityId and monthEndDateId.  The market value is updated on each holding history
     * record and then the record will be saved.
     * @param ehva
     * @return true if the market value is updated else false
     */
    protected boolean updateHoldingHistoryRecords(HoldingHistoryValueAdjustmentDocument ehva) {
        boolean success = true;
        
        getExceptionReportTableRowValues(ehva);
        
        Collection<HoldingHistory> holdingHistoryRecords = holdingHistoryService.getHoldingHistoryBySecuritIdAndMonthEndId(ehva.getSecurityId(), ehva.getHoldingMonthEndDate());

        if (holdingHistoryRecords.isEmpty()) {
            holdingHistoryMarketValueExceptionRowValues.setColumnHeading3("");            
            writeTableRowAndTableReason(ehva, "There are no Holding History records to match the Holding History Value Adjustment document's security id and monthEndDateID.  The process is not executed");
            return false;
        }
        // process the holding history records
        for (HoldingHistory holdingHistoryRecord : holdingHistoryRecords) {
            holdingHistoryRecord.setSecurityUnitVal(ehva.getSecurityUnitValue());

            BigDecimal marketValue = getMarketValue(ehva);
            if (ObjectUtils.isNull(marketValue)) {
                return false;
            }
            
            holdingHistoryRecord.setMarketValue(marketValue);
            
            if (!holdingHistoryService.saveHoldingHistory(holdingHistoryRecord)) {
                holdingHistoryMarketValueExceptionRowValues.setColumnHeading3(holdingHistoryRecord.getKemid());
                writeTableRowAndTableReason(ehva, "Unable to update the market value and save Holding History record.");
                return false;
            }
        }
        
        return success;
    }
    
    /**
     * This method calculates the market value.  If the class type code = B (Bonds) then market value = [Units X Unit Value]/100
     * else Units x Unit Value
     * @param ehva
     * @return marketValue The calculated market value
     */
    protected BigDecimal getMarketValue(HoldingHistoryValueAdjustmentDocument ehva) {
        BigDecimal marketValue = BigDecimal.ZERO;
        try {
            BigDecimal tmpValue = KEMCalculationRoundingHelper.multiply(ehva.getSecurityUnitValue(), null, EndowConstants.Scale.SECURITY_MARKET_VALUE);
            
//            BigDecimal tmpValue = KEMCalculationRoundingHelper.multiply(ehva.getSecurityUnitValue(), ehva.getSecurity().getUnitsHeld(), EndowConstants.Scale.SECURITY_MARKET_VALUE);
            
            if (ehva.getSecurity().getClassCode().getClassCodeType().equalsIgnoreCase(EndowConstants.ClassCodeTypes.BOND)) {
                tmpValue = KEMCalculationRoundingHelper.divide(tmpValue, BigDecimal.valueOf(100), EndowConstants.Scale.SECURITY_MARKET_VALUE);
            }
            
            marketValue = marketValue.add(tmpValue);  
        }
        catch (Exception ex) {
            holdingHistoryMarketValueExceptionRowValues.setColumnHeading3("");
            writeTableRowAndTableReason(ehva, "Reason: " + ex.getMessage() + " - Unable to update the market value.");
            return null;
        }
        
        return marketValue;        
    }
    
    /**
     * This method finds the document for the given document header id
     * @param documentHeaderId
     * @return document The document in the workflow that matches the document header id.
     */
    protected Document findDocumentForMarketValueUpdate(String documentHeaderId) {
        Document document = null;
        
        try {
            document = documentService.getByDocumentHeaderId(documentHeaderId);
        }
        catch (WorkflowException wfe) {
            LOG.error("Exception encountered on finding the document: " + documentHeaderId + " - " + wfe.getMessage());
        }
        
        return document;
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
     * Gets the documentService attribute.
     * 
     * @return Returns the documentService.
     */
    public DocumentService getDocumentService() {
        return documentService;
    }   
    
    /**
     * @return Returns the personService.
     */
    protected PersonService<Person> getPersonService() {
        if(personService==null)
            personService = SpringContext.getBean(PersonService.class);
        return personService;
    }
    
    /**
     * Gets the holdingHistoryMarketValuesExceptionReportWriterService attribute. 
     * @return Returns the holdingHistoryMarketValuesExceptionReportWriterService.
     */
    protected ReportWriterService getHoldingHistoryMarketValuesExceptionReportWriterService() {
        return holdingHistoryMarketValuesExceptionReportWriterService;
    }
    
    /**
     * Sets the holdingHistoryMarketValuesExceptionReportWriterService attribute value.
     * @param holdingHistoryMarketValuesExceptionReportWriterService The holdingHistoryMarketValuesExceptionReportWriterService to set.
     */
    public void setHoldingHistoryMarketValuesExceptionReportWriterService(ReportWriterService holdingHistoryMarketValuesExceptionReportWriterService) {
        this.holdingHistoryMarketValuesExceptionReportWriterService = holdingHistoryMarketValuesExceptionReportWriterService;
    }

    /**
     * Gets the holdingHistoryService attribute. 
     * @return Returns the holdingHistoryService.
     */
    public HoldingHistoryService getHoldingHistoryService() {
        return holdingHistoryService;
    }

    /**
     * Sets the holdingHistoryService attribute value.
     * @param holdingHistoryService The holdingHistoryService to set.
     */
    public void setHoldingHistoryService(HoldingHistoryService holdingHistoryService) {
        this.holdingHistoryService = holdingHistoryService;
    }

    /**
     * Gets the holdingHistoryValueAdjustmentDocumentService attribute. 
     * @return Returns the holdingHistoryValueAdjustmentDocumentService.
     */
    public HoldingHistoryValueAdjustmentDocumentService getHoldingHistoryValueAdjustmentDocumentService() {
        return holdingHistoryValueAdjustmentDocumentService;
    }

    /**
     * Sets the holdingHistoryValueAdjustmentDocumentService attribute value.
     * @param holdingHistoryValueAdjustmentDocumentService The holdingHistoryValueAdjustmentDocumentService to set.
     */
    public void setHoldingHistoryValueAdjustmentDocumentService(HoldingHistoryValueAdjustmentDocumentService holdingHistoryValueAdjustmentDocumentService) {
        this.holdingHistoryValueAdjustmentDocumentService = holdingHistoryValueAdjustmentDocumentService;
    }
}
