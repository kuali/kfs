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
import java.util.List;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.batch.service.HoldingHistoryMarketValuesUpdateService;
import org.kuali.kfs.module.endow.businessobject.EndowmentExceptionReportHeader;
import org.kuali.kfs.module.endow.businessobject.HoldingHistory;
import org.kuali.kfs.module.endow.document.HoldingHistoryValueAdjustmentDocument;
import org.kuali.kfs.module.endow.document.service.HoldingHistoryService;
import org.kuali.kfs.module.endow.document.service.HoldingHistoryValueAdjustmentDocumentService;
import org.kuali.kfs.module.endow.document.service.MonthEndDateService;
import org.kuali.kfs.module.endow.util.KEMCalculationRoundingHelper;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.dto.DocumentSearchCriteriaDTO;
import org.kuali.rice.kew.dto.DocumentSearchResultDTO;
import org.kuali.rice.kew.dto.DocumentSearchResultRowDTO;
import org.kuali.rice.kew.dto.KeyValueDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KualiInteger;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the HoldingHistoryMarketValuesUpdateServices.
 */
@Transactional
public class HoldingHistoryMarketValuesUpdateServiceImpl implements HoldingHistoryMarketValuesUpdateService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(HoldingHistoryMarketValuesUpdateServiceImpl.class);
    public static final String WORKFLOW_DOCUMENT_HEADER_ID_SEARCH_RESULT_KEY = "routeHeaderId";
    public static final String REPORT_LINE_FORMAT = "          %s          %s          %s";
    public static final String REASON_REPORT_LINE_FORMAT = "  Reason: %s";    
    
    private DocumentService documentService;
    private HoldingHistoryService holdingHistoryService;
    private MonthEndDateService monthEndDateService;
    private HoldingHistoryValueAdjustmentDocumentService holdingHistoryValueAdjustmentDocumentService;

    private PersonService<Person> personService;
    private ReportWriterService holdingHistoryMarketValuesExceptionReportWriterService;
    
    /**
     * Constructs a HoldingHistoryMarketValuesUpdateServiceImpl instance
     */
    public HoldingHistoryMarketValuesUpdateServiceImpl() {
        
    }

    /**
     * Gathers all documents that are in ENROUTE status and auto disapproves them.
     * @see org.kuali.kfs.sys.batch.service.autoDisapproveDocumentsInEnrouteStatus#autoDisapproveDocumentsInEnrouteStatus()
     */
    public boolean updateHoldingHistoryMarketValues() {
        boolean success = true ;
        
        LOG.debug("processUpdateHistoryMarketValues() started");
        
        holdingHistoryMarketValuesExceptionReportWriterService.writeNewLines(1);
        
        EndowmentExceptionReportHeader holdingHistoryMarketValueExceptionReportHeader = new EndowmentExceptionReportHeader();
        holdingHistoryMarketValueExceptionReportHeader.setColumnHeading1("Documnet Type");
        holdingHistoryMarketValueExceptionReportHeader.setColumnHeading2("Security Id");
        holdingHistoryMarketValueExceptionReportHeader.setColumnHeading3("KEMID");
        
        holdingHistoryMarketValuesExceptionReportWriterService.writeTableHeader(holdingHistoryMarketValueExceptionReportHeader);
        
     //   holdingHistoryMarketValuesExceptionReportWriterService.writeFormattedMessageLine(REPORT_LINE_FORMAT, "Document Type", "Security ID", "KEMID");
    //    holdingHistoryMarketValuesExceptionReportWriterService.writeFormattedMessageLine(REPORT_LINE_FORMAT, "-------------", "-----------", "-----");
    //    holdingHistoryMarketValuesExceptionReportWriterService.writeNewLines(1);
        
    //    success = processUpdateHoldingHistoryMarketValues();
        
        return success;
    }
    
    /**
     * This method will use documentsearchcriteriaDTO to search for the documents that are in Final status
     * @return true if update is successful else false
     */
    protected boolean processUpdateHoldingHistoryMarketValues() {
        boolean success = true;
        
        Collection<HoldingHistoryValueAdjustmentDocument> holdingHistoryValueAdjustmentDocuments = holdingHistoryValueAdjustmentDocumentService.getHoldingHistoryValueAdjustmentDocument(EndowConstants.HoldingHistoryValueAdjustmentDocument.TRANSACTION_POSTED_NO);

        for (HoldingHistoryValueAdjustmentDocument holdingHistoryValueAdjustmentDocument : holdingHistoryValueAdjustmentDocuments) {
            String documentHeaderId = holdingHistoryValueAdjustmentDocument.getDocumentNumber();

            Document document = findDocumentForMarketValueUpdate(documentHeaderId);
            String documentTypeName = document.getDocumentHeader().getWorkflowDocument().getDocumentType();
            
            if (document != null) {
                if (document.getDocumentHeader().getWorkflowDocument().stateIsFinal()) {
                    // the state of the document is final.. so processing the document.
                    if (checkIfDocumentEligibleForUpdate(document)) {
                        if (updateHoldingHistoryRecords(holdingHistoryValueAdjustmentDocument)) {
                            //update the HoldingHistoryValueAdjustmentDocument's transactionPosted column to Y
                            holdingHistoryValueAdjustmentDocument.setTransactionPosted(true);
                            if (!holdingHistoryValueAdjustmentDocumentService.saveHoldingHistory(holdingHistoryValueAdjustmentDocument)) {
                                holdingHistoryMarketValuesExceptionReportWriterService.writeFormattedMessageLine(REPORT_LINE_FORMAT, documentTypeName, holdingHistoryValueAdjustmentDocument.getSecurityId(), "");
                                holdingHistoryMarketValuesExceptionReportWriterService.writeFormattedMessageLine(REASON_REPORT_LINE_FORMAT, "Unable to set Transaction Posted flag in Holding History Value Adjustment");
                            }
                        }
                     }
                }
                else {
                 //   holdingHistoryMarketValuesExceptionReportWriterService.writeFormattedMessageLine(REPORT_LINE_FORMAT, documentTypeName, holdingHistoryValueAdjustmentDocument.getSecurityId(), "");
                 //   holdingHistoryMarketValuesExceptionReportWriterService.writeFormattedMessageLine(REASON_REPORT_LINE_FORMAT, "Holding History Value Adjustment document status is NOT FINAL. - Skipped updating the Market Values");
                }
            }
            else {
                LOG.error("Document is NULL.  It should never have been null");
                String message = ("Error: Document with id: ").concat(documentHeaderId).concat(" - Document is NULL.  It should never have been null");
                holdingHistoryMarketValuesExceptionReportWriterService.writeFormattedMessageLine(message);                         
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
        boolean documentEligible = false;
        
        String documentTypeName = document.getDocumentHeader().getWorkflowDocument().getDocumentType();
     
        if (!documentTypeName.equalsIgnoreCase(EndowConstants.FeeMethod.ENDOWMENT_HISTORY_VALUE_ADJUSTMENT)) {
            HoldingHistoryValueAdjustmentDocument ehva = (HoldingHistoryValueAdjustmentDocument) document;
            holdingHistoryMarketValuesExceptionReportWriterService.writeFormattedMessageLine(REPORT_LINE_FORMAT, documentTypeName, ehva.getSecurityId(), "");
            holdingHistoryMarketValuesExceptionReportWriterService.writeFormattedMessageLine(REASON_REPORT_LINE_FORMAT, "Document Type: " + documentTypeName + " - only document types EHVA are allowed to be processed.");
            return documentEligible;
        }
        
        HoldingHistoryValueAdjustmentDocument ehva = (HoldingHistoryValueAdjustmentDocument) document;
        if (ehva.isTransactionPosted()) {
            LOG.info("processUpdateHistoryMarketValues Exceptions:  The market values for document: " + document.getDocumentHeader().getDocumentNumber() + " is NOT Updated.");
            holdingHistoryMarketValuesExceptionReportWriterService.writeFormattedMessageLine(REPORT_LINE_FORMAT, ehva.getDocumentTitle(), ehva.getSecurityId(), "");
            holdingHistoryMarketValuesExceptionReportWriterService.writeFormattedMessageLine(REASON_REPORT_LINE_FORMAT, "Not Processed.  Transaction Posted Flag is marked Y already in Holding History Value Adjustment");
        }
        else {
            documentEligible = true;
           }
     
        return documentEligible;
    }
    
    protected boolean updateHoldingHistoryRecords(HoldingHistoryValueAdjustmentDocument ehva) {
        boolean success = true;
        
        KualiInteger monthEndDateId = monthEndDateService.getMonthEndId(ehva.getMonthEndDate().getMonthEndDate());
        
        if (monthEndDateId.equals(new KualiInteger("0"))) {
            holdingHistoryMarketValuesExceptionReportWriterService.writeFormattedMessageLine(REPORT_LINE_FORMAT, ehva.getDocumentTitle(), ehva.getSecurityId(), "");
            holdingHistoryMarketValuesExceptionReportWriterService.writeFormattedMessageLine(REASON_REPORT_LINE_FORMAT, "Unable to get Month End Date for MonthEndDate: " + ehva.getMonthEndDate().getMonthEndDate().toString() + " in the END_ME_DT_T table.");
            return false;    
        }
        
        Collection<HoldingHistory> holdingHistoryRecords = holdingHistoryService.getHoldingHistoryBySecuritIdAndMonthEndId(ehva.getSecurityId(), monthEndDateId);

        for (HoldingHistory holdingHistoryRecord : holdingHistoryRecords) {
            holdingHistoryRecord.setSecurityUnitVal(ehva.getSecurityUnitValue());
            holdingHistoryRecord.setMarketValue(getMarketValue(ehva));
            if (!holdingHistoryService.saveHoldingHistory(holdingHistoryRecord)) {
                holdingHistoryMarketValuesExceptionReportWriterService.writeFormattedMessageLine(REPORT_LINE_FORMAT, ehva.getDocumentTitle(), holdingHistoryRecord.getSecurityId(), holdingHistoryRecord.getKemid());
                holdingHistoryMarketValuesExceptionReportWriterService.writeFormattedMessageLine(REASON_REPORT_LINE_FORMAT, "Unable to save Holding History record.");
            }
        }
        
        return success;
    }
    
    protected BigDecimal getMarketValue(HoldingHistoryValueAdjustmentDocument ehva) {
        BigDecimal marketValue = BigDecimal.ZERO;
        
        BigDecimal tmpValue = KEMCalculationRoundingHelper.multiply(ehva.getSecurityUnitValue(), ehva.getSecurity().getUnitsHeld(), EndowConstants.Scale.SECURITY_MARKET_VALUE);
        
        if (ehva.getSecurity().getClassCode().getClassCodeType().equalsIgnoreCase(EndowConstants.ClassCodeTypes.BOND)) {
            tmpValue = KEMCalculationRoundingHelper.divide(tmpValue, BigDecimal.valueOf(100), EndowConstants.Scale.SECURITY_MARKET_VALUE);
        }
        
        marketValue = marketValue.add(tmpValue);  
        
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
     * Gets the monthEndDateService attribute. 
     * @return Returns the monthEndDateService.
     */
    public MonthEndDateService getMonthEndDateService() {
        return monthEndDateService;
    }

    /**
     * Sets the monthEndDateService attribute value.
     * @param monthEndDateService The monthEndDateService to set.
     */
    public void setMonthEndDateService(MonthEndDateService monthEndDateService) {
        this.monthEndDateService = monthEndDateService;
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
