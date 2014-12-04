/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
