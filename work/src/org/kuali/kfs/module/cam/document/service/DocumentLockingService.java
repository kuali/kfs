/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.cams.service;

import java.util.List;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.MaintenanceLock;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.codes.BalanceTyp;

/**
 * Provides helper methods for checking locks on transactional documents. We discussed whether this should move
 * into rice but opted against it because except for cams transactional document should not be maintaining
 * business objects.
 */
public interface DocumentLockingService {
    /**
     * This method attempts to find any other active documents that are pending on the same maintenance record.
     * 
     * If any are pending and locked, thereby blocking this document, then the docHeaderId/documentNumber of the blocking
     * locked document is returned.
     * 
     * Otherwise, if nothing is blocking, then null is returned.
     * 
     * @param documentNumber document to test for
     * @param maintenanceLocks locks that the document holds
     * @return A String representing the docHeaderId of any blocking document, or null if none are blocking
     */
    public String getLockingDocumentId(String documentNumber, List<MaintenanceLock> maintenanceLocks);
    
    /**
     * Adds error message to global ErrorMap showing the docHeaderId that is blocking. 
     * @param docHeaderId of blocking document
     */
    public void checkForLockingDocument(String blockingDocId);
    
}
