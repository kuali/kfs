/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.labor.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.labor.bo.ExpenseTransferAccountingLine;
import org.kuali.module.labor.bo.PendingLedgerEntry;
import org.kuali.module.labor.service.LaborLedgerPendingEntryService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class LaborLedgerPendingEntryServiceImpl implements LaborLedgerPendingEntryService {

    private BusinessObjectService businessObjectService;

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public boolean hasPendingLaborLedgerEntry(Account account) {
        Map fieldValues = new HashMap();
        fieldValues.put("chartOfAccountsCode", account.getChartOfAccountsCode());
        fieldValues.put("accountNumber", account.getAccountNumber());

        return businessObjectService.countMatching(PendingLedgerEntry.class, fieldValues) > 0;
    }

    public boolean hasPendingLaborLedgerEntry(String emplid) {

        Map fieldValues = new HashMap();
        fieldValues.put("emplid", emplid);
        PendingLedgerEntry pendingEntry = new PendingLedgerEntry();
        Collection<PendingLedgerEntry> pendingEntries = businessObjectService.findMatching(PendingLedgerEntry.class, fieldValues);

        // When the financial Document Approved Code equals 'X' it means the pending labor ledger transaction has been processed
        for (PendingLedgerEntry pendingLedgerEntry : pendingEntries)
            if ((pendingLedgerEntry.getFinancialDocumentApprovedCode() == null) || (!pendingLedgerEntry.getFinancialDocumentApprovedCode().trim().equals("X")))
                return true;
        return false;
    }

    public boolean clearCancelledPendingLaborLedgerEntries() {

        // Insert an entry into fp_cancelled_docs_mt by copying columns from fp_doc_header_t 
        
   /*     SELECT  USERENV('SESSIONID'), fs_origin_cd, fdoc_nbr
        FROM fp_doc_header_t 
        WHERE fdoc_status_cd = 'C';
*/

        // Delete from gl_pending_entry_t
        
/*        DELETE  gl_pending_entry_t
        WHERE ROWID IN
        (SELECT p.ROWID
        FROM gl_pending_entry_t p, fp_cancelled_docs_mt c
        WHERE (p.fs_origin_cd = c.fs_origin_cd 
        AND p.fdoc_nbr = c.fdoc_nbr) 
        AND c.SESID = USERENV('SESSIONID'));
*/    
       // Delete from ld_pnd_ldgr_entr_t
    
/*      DELETE  ld_pnd_ldgr_entr_t
        WHERE ROWID IN
        (SELECT p.ROWID
        FROM ld_pnd_ldgr_entr_t p, fp_cancelled_docs_mt c
        WHERE (p.fs_origin_cd = c.fs_origin_cd 
        AND p.fdoc_nbr = c.fdoc_nbr) 
        AND c.SESID = USERENV('SESSIONID'));
*/
    //DELETE  from fp_cancelled_docs_mt

     //   WHERE SESID = USERENV('SESSIONID');


        
        
        
        return true;
    }
}
