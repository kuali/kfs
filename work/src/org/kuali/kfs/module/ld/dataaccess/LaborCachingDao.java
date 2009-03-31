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
package org.kuali.kfs.module.ld.dataaccess;

import org.kuali.kfs.gl.businessobject.OriginEntry;
import org.kuali.kfs.gl.dataaccess.CachingDao;
import org.kuali.kfs.module.ld.businessobject.LaborObject;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.businessobject.LedgerEntry;

public interface LaborCachingDao extends CachingDao {

    public void insertLedgerEntry(LedgerEntry ledgerEntry);
    public LaborObject getLaborObject(OriginEntry originEntry);   
    public int getMaxLaborSequenceNumber(LedgerEntry t);
    public LedgerBalance getLedgerBalance(LedgerBalance ledgerBalance);
    public void insertLedgerBalance(LedgerBalance ledgerBalance);
    public void updateLedgerBalance(LedgerBalance ledgerBalance);
    public void init();
}
