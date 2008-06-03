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
package org.kuali.module.cams.gl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.GeneralLedgerPendingEntrySource;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.module.financial.service.UniversityDateService;

public abstract class CamsGeneralLedgerPendingEntrySourceBase implements GeneralLedgerPendingEntrySource {

    private List<GeneralLedgerPendingEntry> pendingEntries = new ArrayList<GeneralLedgerPendingEntry>();
    private DocumentHeader documentHeader;
    private List<GeneralLedgerPendingEntrySourceDetail> postables = new ArrayList<GeneralLedgerPendingEntrySourceDetail>();
    public final static String CAMS_GENERAL_LEDGER_POSTING_HELPER_BEAN_ID = "camsGeneralLedgerPendingEntryGenerationProcess";


    public CamsGeneralLedgerPendingEntrySourceBase(DocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }

    public void addPendingEntry(GeneralLedgerPendingEntry entry) {
        this.pendingEntries.add(entry);
    }

    public void clearAnyGeneralLedgerPendingEntries() {
        this.pendingEntries.clear();

    }

    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        // over ride if needed
    }

    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        return true;
    }

    public DocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    public KualiDecimal getGeneralLedgerPendingEntryAmountForDetail(GeneralLedgerPendingEntrySourceDetail postable) {
        return postable.getAmount();
    }

    public List<GeneralLedgerPendingEntrySourceDetail> getGeneralLedgerPendingEntrySourceDetails() {
        return this.postables;
    }

    public Integer getPostingYear() {
        return SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
    }

    public List<GeneralLedgerPendingEntry> getPendingEntries() {
        return pendingEntries;
    }

    public void setPendingEntries(List<GeneralLedgerPendingEntry> pendingEntries) {
        this.pendingEntries = pendingEntries;
    }

    public List<GeneralLedgerPendingEntrySourceDetail> getPostables() {
        return postables;
    }

    public void setPostables(List<GeneralLedgerPendingEntrySourceDetail> postables) {
        this.postables = postables;
    }

    public void setDocumentHeader(DocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }

    public void handleRouteStatusChange(List<GeneralLedgerPendingEntry> glPendingEntries) {
        if (glPendingEntries == null || glPendingEntries.isEmpty()) {
            return;
        }
        if (documentHeader.getWorkflowDocument().stateIsProcessed()) {
            for (GeneralLedgerPendingEntry glpe : glPendingEntries) {
                glpe.setFinancialDocumentApprovedCode(KFSConstants.DocumentStatusCodes.APPROVED);
            }
            SpringContext.getBean(BusinessObjectService.class).save(glPendingEntries);
        }
        else if (getDocumentHeader().getWorkflowDocument().stateIsCanceled() || documentHeader.getWorkflowDocument().stateIsDisapproved()) {
            removeGeneralLedgerPendingEntries(documentHeader.getDocumentNumber());
        }
    }


    /**
     * This method calls the service to remove all of the GLPE's associated with this document
     */
    private void removeGeneralLedgerPendingEntries(String docNumber) {
        GeneralLedgerPendingEntryService glpeService = SpringContext.getBean(GeneralLedgerPendingEntryService.class);
        glpeService.delete(docNumber);
    }

    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
        SpringContext.getBean(GeneralLedgerPendingEntryService.class).populateExplicitGeneralLedgerPendingEntry(this, postable, sequenceHelper, explicitEntry);
        customizeExplicitGeneralLedgerPendingEntry(postable, explicitEntry);
        addPendingEntry(explicitEntry);
        return true;
    }

}
