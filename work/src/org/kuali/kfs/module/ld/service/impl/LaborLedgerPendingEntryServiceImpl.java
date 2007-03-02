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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.labor.bo.PendingLedgerEntry;
import org.kuali.module.labor.dao.LaborLedgerPendingEntryDao;
import org.kuali.module.labor.document.LaborDocument;
import org.kuali.module.labor.rules.event.GenerateLaborLedgerBenefitClearingPendingEntriesEvent;
import org.kuali.module.labor.rules.event.GenerateLaborLedgerPendingEntriesEvent;
import org.kuali.module.labor.service.LaborLedgerPendingEntryService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class LaborLedgerPendingEntryServiceImpl implements LaborLedgerPendingEntryService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborLedgerPendingEntryServiceImpl.class);

    private LaborLedgerPendingEntryDao laborLedgerPendingEntryDao;
    private KualiRuleService kualiRuleService;
    
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

    /**
     * Invokes generateEntries method on the salary expense transfer document.
     * 
     * @param document - document whose pending entries need generated
     * @return whether the business rules succeeded
     */
    public boolean generateLaborLedgerPendingEntries(LaborDocument document) {
        boolean success = true;

        // we must clear them first before creating new ones
        document.getLaborLedgerPendingEntries().clear();

        LOG.info("deleting existing ll pending ledger entries for document " + document.getDocumentNumber());
        delete(document.getDocumentNumber());

        LOG.info("generating ll pending ledger entries for document " + document.getDocumentNumber());
        GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper();        
        AccountingDocument transactionalDocument = (AccountingDocument) document;
        
        //process accounting lines, generate labor ledger pending entries
        List sourceAccountingLines = transactionalDocument.getSourceAccountingLines();
        if (sourceAccountingLines != null) {
            for (Iterator iter = sourceAccountingLines.iterator(); iter.hasNext();) {
                success &= processLaborLedgerPendingEntryForAccountingLine(transactionalDocument, sequenceHelper, iter);
            }
        }

        List targetAccountingLines = transactionalDocument.getTargetAccountingLines();
        if (targetAccountingLines != null) {
            for (Iterator iter = targetAccountingLines.iterator(); iter.hasNext();) {
                success &= processLaborLedgerPendingEntryForAccountingLine(transactionalDocument, sequenceHelper, iter);
            }
        }

        //compare source and target accounting lines, and generate benefit clearing liens as needed
        success &= processGenerateLaborLedgerBenefitClearingEntries(transactionalDocument, sequenceHelper);
        
        // doc specific pending entries generation
        //GenerateLaborLedgerDocumentPendingEntriesEvent event = new GenerateLaborLedgerDocumentPendingEntriesEvent(document, sequenceHelper);
        //success &= kualiRuleService.applyRules(event);
        return success;
    }

    /**
     * This method handles generically taking an accounting line, doing a deep copy on it so that we have a new instance without
     * reference to the original (won't affect the tran doc's acct lines), performing a retrieveNonKeyFields on the line to make
     * sure it's populated properly, and then calling the rule framework driven GLPE generation code.
     * 
     * @param document
     * @param sequenceHelper
     * @param iter
     * @return whether the business rules succeeded
     */
    private boolean processLaborLedgerPendingEntryForAccountingLine(AccountingDocument document, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, Iterator iter) {
        LOG.debug("processLaborLedgerPendingEntryForAccountingLine() started");
        boolean success = true;

        AccountingLine accountingLine = (AccountingLine) iter.next();

        GenerateLaborLedgerPendingEntriesEvent event = new GenerateLaborLedgerPendingEntriesEvent(document, accountingLine, sequenceHelper);
        success &= kualiRuleService.applyRules(event);
        sequenceHelper.increment(); // increment for the next line
        return success;
    }

    private boolean processGenerateLaborLedgerBenefitClearingEntries(AccountingDocument document, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        LOG.debug("processLaborLedgerPendingEntryForAccountingLine() started");
        boolean success = true;

        GenerateLaborLedgerBenefitClearingPendingEntriesEvent event = new GenerateLaborLedgerBenefitClearingPendingEntriesEvent(document, sequenceHelper);
        success &= kualiRuleService.applyRules(event);
        sequenceHelper.increment(); // increment for the next line
        return success;
    }

    public void delete(String documentHeaderId) {
        LOG.debug("delete() started");

        this.laborLedgerPendingEntryDao.delete(documentHeaderId);
    }

    public void setLaborLedgerPendingEntryDao(LaborLedgerPendingEntryDao laborLedgerPendingEntryDao) {
        this.laborLedgerPendingEntryDao = laborLedgerPendingEntryDao;
    }

    public void setKualiRuleService(KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }

}
