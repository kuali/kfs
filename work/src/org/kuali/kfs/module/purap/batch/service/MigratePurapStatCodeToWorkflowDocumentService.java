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
package org.kuali.kfs.module.purap.batch.service;

/**
 * This interface defines the batch job that would be run to look at current
 * REQs, POs, PREQs, and VCMs for statusCode and move them to the workflow side.
 * It will do the following:
 * 1. Get the existing PURAP documents and look at statusCode where statusCode exists
 * 2. Using the document number, look for the document in KREW_DOC_HDR_T and update
 * APP_DOC_STAT and APP_DOC_STAT_MDFN_DT columns if data does not exist in these columns.
 * 3. STAT_CD columns in purap documents will be set to null.
 * 4. The documents in KREW_DOC_HDR_EXT_T table will be reindexed.
 */
public interface MigratePurapStatCodeToWorkflowDocumentService {
    /**
     * Migrate STAT_CD from PURAP documents to the corresponding
     * workflow documents column APP_DOC_STAT
     */
    public boolean migratePurapStatCodeToWorkflowDocuments();
}
