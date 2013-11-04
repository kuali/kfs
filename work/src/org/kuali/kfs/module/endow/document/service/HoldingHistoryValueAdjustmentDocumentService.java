/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service;

import java.util.Collection;

import org.kuali.kfs.module.endow.document.HoldingHistoryValueAdjustmentDocument;

/**
 * HoldingHistoryService interface to provide the method to get holding history records
 */
public interface HoldingHistoryValueAdjustmentDocumentService {

    /**
     * gets holding history value adjustment records matching transactionPosted flag
     * 
     * @param transactionPosted
     * @return List<HoldingHistory> List of HoldingHistory records matched on securityId and monthEndId
     */
    public Collection<HoldingHistoryValueAdjustmentDocument> getHoldingHistoryValueAdjustmentDocument(String transactionPosted);
    
    /**
     * saves HoldingHistoryValueAdjustmentDocument record
     * 
     * @param holdingHistoryValueAdjustmentDocument HoldingHistoryValueAdjustmentDocument record to save
     * @return boolean true is successful else false
     */
    public boolean saveHoldingHistory(HoldingHistoryValueAdjustmentDocument holdingHistoryValueAdjustmentDocument);

    /**
     * get HoldingHistoryValueAdjustmentDocument using documentNumber field as primary key
     * @param documentNumber documentNumber to query on
     * @return holdingHistoryValueAdjustmentDocument
     */
    public Collection<HoldingHistoryValueAdjustmentDocument> getHoldingHistoryValueAdjustmentDocumentByDocumentNumber(String documentNumber);
}
