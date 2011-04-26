/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.dataaccess;

import org.kuali.kfs.module.ld.businessobject.LaborGeneralLedgerEntry;

/**
 * This is the data access object for labor general ledger entry
 * 
 * @see org.kuali.kfs.module.ld.businessobject.LaborGeneralLedgerEntry
 */
public interface LaborGeneralLedgerEntryDao {

    /**
     * get the max squence number with the given Labor General edger Entry object
     * 
     * @param laborGeneralLedgerEntry the given labor General Ledger Entry
     * @return the max squence number with the infomation in the given Labor General edger Entry object
     */
    Integer getMaxSequenceNumber(LaborGeneralLedgerEntry laborGeneralLedgerEntry);
}
