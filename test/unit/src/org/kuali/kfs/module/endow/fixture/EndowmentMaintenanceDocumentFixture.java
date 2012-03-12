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
package org.kuali.kfs.module.endow.fixture;

import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintenanceDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;

public enum EndowmentMaintenanceDocumentFixture {
    // Endowment Maintenance Document Fixture
    ENDOWMENT_MAINTENANCE_DOCUMENT_REQUIRED_FIELDS_RECORD("ABCD-TEST", // documentNumber
            "ABCD-TEST Description", // documentDescription
            "1234567890", // objectId
            new Long(1) // versionNumber
    );

    public final String documentNumber;
    public final String documentDescription;
    public final String objectId;
    public final Long versionNumber;

    private EndowmentMaintenanceDocumentFixture(String documentNumber, String documentDescription, String objectId, Long versionNumber) {
        this.documentNumber = documentNumber;
        this.documentDescription = documentDescription;
        this.objectId = objectId;
        this.versionNumber = versionNumber;
    }

    /**
     * This method creates a Endowment Maintenance Document Base record
     * 
     * @return endowmentTransactionDocument
     */
    public FinancialSystemMaintenanceDocument createEndowmentMaintenanceDocument() {
        FinancialSystemMaintenanceDocument financialSystemMaintenanceDocument = null;

        DocumentService documentService = SpringContext.getBean(DocumentService.class);

        try {
            financialSystemMaintenanceDocument =  DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), FinancialSystemMaintenanceDocument.class);
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Document creation failed.");
        }

        financialSystemMaintenanceDocument.getDocumentHeader().setDocumentNumber(this.documentNumber);
        financialSystemMaintenanceDocument.getDocumentHeader().setDocumentDescription(this.documentDescription);
        financialSystemMaintenanceDocument.setVersionNumber(this.versionNumber);
        financialSystemMaintenanceDocument.setObjectId(this.objectId);
        return financialSystemMaintenanceDocument;
    }
}
