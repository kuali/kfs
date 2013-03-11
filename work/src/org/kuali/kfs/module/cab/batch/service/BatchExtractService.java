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

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.cab.batch.ExtractProcessLog;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLineBase;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderAccount;

/**
 * Declares the service methods used by CAB batch program
 */
public interface BatchExtractService {

    /**
     * This method is the entry point into the Batch Extract.
     *
     * @param processLog
     */
    public void performExtract(ExtractProcessLog processLog);


    /**
     * Allocate additional charges during batch.
     *
     * @param purApDocuments
     */
    void allocateAdditionalCharges(HashSet<PurchasingAccountsPayableDocument> purApDocuments);

    /**
     * Returns the list of CAB eligible GL entries, filter parameters are pre-configured
     *
     * @param process log
     * @return Eligible GL Entries meeting batch parameters configured under parameter group KFS-CAB:Batch
     */
    Collection<Entry> findElgibleGLEntries(ExtractProcessLog processLog);


    /**
     * Saves financial transaction lines which dont have Purchase Order number associated with it
     *
     * @param fpLines Financial transaction lines
     * @param processLog Process Log
     */
    void saveFPLines(List<Entry> fpLines, ExtractProcessLog processLog);

    /**
     * Saved purchasing line transactions, this method implementation internally uses
     * {@link org.kuali.kfs.gl.batch.service.ReconciliationService} to QA the data before saving
     *
     * @param poLines Eligible GL Lines
     * @param processLog Process Log
     */
    HashSet<PurchasingAccountsPayableDocument> savePOLines(List<Entry> poLines, ExtractProcessLog processLog);

    /**
     * Separates out transaction lines associated with purchase order from the rest
     *
     * @param fpLines Non-purchasing lines
     * @param purapLines Purchasing lines
     * @param elgibleGLEntries Full list of eligible GL entries
     */
    void separatePOLines(List<Entry> fpLines, List<Entry> purapLines, Collection<Entry> elgibleGLEntries);

    /**
     * Updates the last extract time stamp system parameter, usually done when a batch process is finished successfully.
     *
     * @param time Last extract start time
     */
    void updateLastExtractTime(Timestamp time);


    /**
     * This method collects account line history using batch parameters
     *
     * @return Collection Purchasing Accounts Payable Account Line History
     */
    Collection<PurApAccountingLineBase> findPurapAccountRevisions();


    /**
     * Implementation will retrieve all eligible Purchase Order account lines from a Purchase order that matches criteria required
     * by pre-asset tagging, using these account lines, batch process can identify the eligible purchase order line items to be
     * saved for pre-tagging screen
     *
     * @return Pre-taggable PO Account lines
     */
    Collection<PurchaseOrderAccount> findPreTaggablePOAccounts();

    /**
     * Implementation will identify eligible purchase oder line items eligible for pre-tagging screen
     *
     * @param preTaggablePOAccounts List of pre-taggable account lines
     */
    void savePreTagLines(Collection<PurchaseOrderAccount> preTaggablePOAccounts);

    /**
     * Updates the last extract date parameter for Pre Asset Tagging Step
     *
     * @param date Date value
     */
    void updateLastExtractDate(Date date);
}
