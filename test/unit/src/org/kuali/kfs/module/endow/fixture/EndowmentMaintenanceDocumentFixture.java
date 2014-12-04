/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
