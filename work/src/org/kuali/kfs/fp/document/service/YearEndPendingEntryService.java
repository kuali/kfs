/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.fp.document.service;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.rice.krad.document.TransactionalDocument;

/**
 * Methods needed to update pending entries on FP year end documents
 */
public interface YearEndPendingEntryService {
    /**
     * @return the previous fiscal year used with all GLPE
     */
    public abstract Integer getPreviousFiscalYear();
    
    /**
     * @return the accounting period code used with all GLPE
     */
    public abstract String getFinalAccountingPeriod();
    
    /**
     * populates a <code>GeneralLedgerPendingEntry</code> populated with common year end document data into the explicit general
     * ledger pending entry. currently is the following:
     * <ol>
     * <li>fiscal period code = final accounting period code
     * <li>fiscal year= previous fiscal year
     * </ol>
     * 
     * @param transactionalDocument
     * @param accountingLine
     * @param explicitEntry
     */
    public abstract void customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry);
    
    /**
     * Populates an offset GeneralLedgerPendingEntry such that it uses the Offset Definition from the previous year
     * @param transactionalDocument the transactional document with general ledger pending entries on it
     * @param accountingLine the general ledger pending entry source which generated the explicit and offset entry
     * @param explicitEntry the explicit entry requiring this offset entry
     * @param offsetEntry the offset entry which is being customized
     * @return whether the offset could be successfully customized for year end or not
     */
    public abstract boolean customizeOffsetGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument, GeneralLedgerPendingEntrySourceDetail accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry);
    
    


}
