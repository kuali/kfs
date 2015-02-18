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
package org.kuali.kfs.module.cab.batch.service;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.cab.businessobject.GlAccountLineGroup;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLineBase;

/**
 * This class declares the service method for CAB Reconciliation service. Expected to be used by {@link BatchExtractService}. This
 * service should be not be implemented as singleton.
 */
public interface ReconciliationService {

    /**
     * Returns the list of duplicate entries found after reconciliation
     * 
     * @return Duplicate GL Entries
     */
    List<Entry> getDuplicateEntries();

    /**
     * Returns the list of ignored entries found after reconciliation
     * 
     * @return Ignored GL Entries
     */
    List<Entry> getIgnoredEntries();

    /**
     * Returns the list of account groups that found match to account line history
     * 
     * @return List of valid matched account groups
     */
    Collection<GlAccountLineGroup> getMatchedGroups();

    /**
     * Returns the list of unmatched account line groups
     * 
     * @return List of mismatches
     */
    Collection<GlAccountLineGroup> getMisMatchedGroups();

    /**
     * Returns true is a GL entry is already available in CAB
     * 
     * @param glEntry GL Line entry
     * @return true if matching GL entry found in CAB
     */
    boolean isDuplicateEntry(Entry glEntry);

    /**
     * Main reconciliation service which will apply the formula where PURAP transaction amounts are compared using
     * <li>GL_ENTRY_T = (AP_PMT_RQST_ACCT_CHG_T or AP_CRDT_MEMO_ACCT_CHG_T) </li>
     * 
     * @param glEntries Purap GL Entries
     * @param purapAcctEntries Purap Account Entries
     */
    void reconcile(Collection<Entry> glEntries, Collection<PurApAccountingLineBase> purapAcctEntries);


}
