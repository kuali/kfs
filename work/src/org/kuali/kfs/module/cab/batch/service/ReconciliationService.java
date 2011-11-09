/*
 * Copyright 2008 The Kuali Foundation
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
