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
package org.kuali.kfs.module.cam.document.gl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.service.BusinessObjectService;

public abstract class CamsGeneralLedgerPendingEntrySourceBase implements GeneralLedgerPendingEntrySource {

    private List<GeneralLedgerPendingEntry> pendingEntries = new ArrayList<GeneralLedgerPendingEntry>();
    private FinancialSystemDocumentHeader documentHeader;
    private List<GeneralLedgerPendingEntrySourceDetail> postables = new ArrayList<GeneralLedgerPendingEntrySourceDetail>();


    public CamsGeneralLedgerPendingEntrySourceBase(FinancialSystemDocumentHeader documentHeader) {
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

    @Override
    public DocumentHeader getDocumentHeader() {
        return documentHeader;
    }
    
    @Override
    public FinancialSystemDocumentHeader getFinancialSystemDocumentHeader() {
        // TODO Auto-generated method stub
        return null;
    }

    public KualiDecimal getGeneralLedgerPendingEntryAmountForDetail(GeneralLedgerPendingEntrySourceDetail postable) {
        return postable.getAmount().abs();
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

    public void setDocumentHeader(FinancialSystemDocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }

    public void doRouteStatusChange(List<GeneralLedgerPendingEntry> glPendingEntries) {
        if (glPendingEntries == null || glPendingEntries.isEmpty()) {
            return;
        }
        if (documentHeader.getWorkflowDocument().isProcessed()) {
            for (GeneralLedgerPendingEntry glpe : glPendingEntries) {
                glpe.setFinancialDocumentApprovedCode(KFSConstants.DocumentStatusCodes.APPROVED);
            }
            SpringContext.getBean(BusinessObjectService.class).save(glPendingEntries);
        }
        else if (getDocumentHeader().getWorkflowDocument().isCanceled() || documentHeader.getWorkflowDocument().isDisapproved()) {
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
