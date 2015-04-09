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
