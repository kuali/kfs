/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl.service;

/**
 * This interface defines the batch jobs that would be run nightly against the pending general ledger entries. These jobs are
 * performed to ensure the correctness of the entries. As to date, the jobs involve two functions: finding unbalanced approved
 * documents and finding bad approved documents.
 */
public interface NightlyOutService {

    /**
     * Deletes all the records that were copied to the GL.
     */
    public void deleteCopiedPendingLedgerEntries();

    /**
     * Copies the approved pending ledger entries to orign entry table
     */
    public void copyApprovedPendingLedgerEntries();
}
