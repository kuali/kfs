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
package org.kuali.kfs.module.ld.service.impl;

import org.kuali.kfs.gl.batch.service.impl.OriginEntryLookupServiceImpl;
import org.kuali.kfs.gl.businessobject.OriginEntry;
import org.kuali.kfs.gl.dataaccess.CachingDao;
import org.kuali.kfs.module.ld.businessobject.LaborObject;
import org.kuali.kfs.module.ld.businessobject.LedgerEntry;
import org.kuali.kfs.module.ld.dataaccess.LaborCachingDao;
import org.kuali.kfs.module.ld.service.LaborOriginEntryLookupService;
import org.kuali.kfs.sys.service.NonTransactional;

@NonTransactional
public class LaborOriginEntryLookupServiceImpl extends OriginEntryLookupServiceImpl implements LaborOriginEntryLookupService{
    
    private LaborCachingDao laborCachingDao;
    
    public void insertLedgerEntry(LedgerEntry ledgerEntry){
        laborCachingDao.insertLedgerEntry(ledgerEntry) ;  
    }
    public LaborObject getLaborObject(OriginEntry originEntry){
        return laborCachingDao.getLaborObject(originEntry);
    }
    //public int getMaxLaborSequenceNumber(LedgerEntry t){
    //  laborCachingDao.getMaxLaborSequenceNumber(t)
    //}
    
    

    public void setLaborCachingDao(LaborCachingDao laborCachingDao) {
        this.laborCachingDao = laborCachingDao;
        
    }

    
    
    
}
