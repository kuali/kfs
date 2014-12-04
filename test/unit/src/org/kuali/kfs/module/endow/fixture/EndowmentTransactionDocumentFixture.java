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

import org.kuali.kfs.module.endow.document.EndowmentTransactionalDocumentBase;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;

public enum EndowmentTransactionDocumentFixture {
    // Endowment Transaction Document Fixture
    ENDOWMENT_TRANSACTIONAL_DOCUMENT_REQUIRED_FIELDS_RECORD("ABCD-TEST", // documentNumber
            "C", // transactionSubTypeCode
            "M", // transactionSourceTypeCode
            false, // transactionPosted
            "1234567890", // objectId
            new Long(1) // versionNumber
    ), ENDOWMENT_TRANSACTIONAL_DOCUMENT_ASSET_INCREASE("EAI-TEST", // documentNumber
            "C", // transactionSubTypeCode
            "M", // transactionSourceTypeCode
            false, // transactionPosted
            "1234567890", // objectId
            new Long(1) // versionNumber
    ), ENDOWMENT_TRANSACTIONAL_DOCUMENT_ASSET_DECREASE("EAD-TEST", // documentNumber
            "C", // transactionSubTypeCode
            "M", // transactionSourceTypeCode
            false, // transactionPosted
            "1234567890", // objectId
            new Long(1) // versionNumber
    ), ENDOWMENT_TRANSACTIONAL_DOCUMENT_CASH_INCREASE("ECI-TEST", // documentNumber
            "C", // transactionSubTypeCode
            "M", // transactionSourceTypeCode
            false, // transactionPosted
            "1234567890", // objectId
            new Long(1) // versionNumber
    ), ENDOWMENT_TRANSACTIONAL_DOCUMENT_CASH_DECREASE("ECDD-TEST", // documentNumber
            "C", // transactionSubTypeCode
            "M", // transactionSourceTypeCode
            false, // transactionPosted
            "1234567890", // objectId
            new Long(1) // versionNumber
    ), ENDOWMENT_TRANSACTIONAL_DOCUMENT_CASH_TRANSFER("ECT-TEST", // documentNumber
            "C", // transactionSubTypeCode
            "M", // transactionSourceTypeCode
            false, // transactionPosted
            "1234567890", // objectId
            new Long(1) // versionNumber
    ), ENDOWMENT_TRANSACTIONAL_DOCUMENT_SECURITY_TRANSFER("EST-TEST", // documentNumber
            "N", // transactionSubTypeCode
            "M", // transactionSourceTypeCode
            false, // transactionPosted
            "1234567890", // objectId
            new Long(1) // versionNumber
    ), ENDOWMENT_TRANSACTIONAL_DOCUMENT_ENDOWMENT_TO_GL_TRANSFER("EGLT-TEST", // documentNumber
            "C", // transactionSubTypeCode
            "M", // transactionSourceTypeCode
            false, // transactionPosted
            "1234567890", // objectId
            new Long(1) // versionNumber
    ), ENDOWMENT_TRANSACTIONAL_DOCUMENT_GL_TO_ENDOWMENT_TRANSFER("GLET-TEST", // documentNumber
            "C", // transactionSubTypeCode
            "M", // transactionSourceTypeCode
            false, // transactionPosted
            "1234567890", // objectId
            new Long(1) // versionNumber
    ),
    ENDOWMENT_TRANSACTIONAL_DOCUMENT_UNIT_SHARE_ADJ("EUSA-TEST", // documentNumber
            "N", // transactionSubTypeCode
            "M", // transactionSourceTypeCode
            false, // transactionPosted
            "1234567890", // objectId
            new Long(1) // versionNumber
    );

    public final String documentNumber;
    public final String transactionSubTypeCode;
    public final String transactionSourceTypeCode;
    public final boolean transactionPosted;
    public final String objectId;
    public final Long versionNumber;

    private EndowmentTransactionDocumentFixture(String documentNumber, String transactionSubTypeCode, String transactionSourceTypeCode, boolean transactionPosted, String objectId, Long versionNumber) {
        this.documentNumber = documentNumber;
        this.transactionSubTypeCode = transactionSubTypeCode;
        this.transactionSourceTypeCode = transactionSourceTypeCode;
        this.transactionPosted = transactionPosted;
        this.objectId = objectId;
        this.versionNumber = versionNumber;
    }

    /**
     * This method creates a Endowment Transaction Document Base record
     * 
     * @return endowmentTransactionDocument
     */
    public EndowmentTransactionalDocumentBase createEndowmentTransactionDocument(Class clazz) {
        EndowmentTransactionalDocumentBase endowmentTransactionDocument = null;

        DocumentService documentService = SpringContext.getBean(DocumentService.class);

        try {
            endowmentTransactionDocument = (EndowmentTransactionalDocumentBase) DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), clazz);
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Document creation failed.");
        }

        endowmentTransactionDocument.getDocumentHeader().setDocumentNumber(this.documentNumber);
        endowmentTransactionDocument.setTransactionSubTypeCode(this.transactionSubTypeCode);
        endowmentTransactionDocument.setTransactionSourceTypeCode(this.transactionSourceTypeCode);
        endowmentTransactionDocument.setTransactionPosted(this.transactionPosted);
        endowmentTransactionDocument.setObjectId(this.objectId);
        endowmentTransactionDocument.setVersionNumber(this.versionNumber);
        endowmentTransactionDocument.refreshNonUpdateableReferences();

        return endowmentTransactionDocument;
    }

    /**
     * This method creates a Endowment Transaction Document Base record
     * 
     * @return endowmentTransactionDocument
     */
    public EndowmentTransactionalDocumentBase createEndowmentTransactionDocument(String documentNumber, String transactionSubTypeCode, String transactionSourceTypeCode, boolean transactionPosted, String objectId, Long versionNumber) {
        EndowmentTransactionalDocumentBase endowmentTransactionDocument = null;

        DocumentService documentService = SpringContext.getBean(DocumentService.class);

        try {
            endowmentTransactionDocument = (EndowmentTransactionalDocumentBase) DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), EndowmentTransactionalDocumentBase.class);
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Document creation failed.");
        }

        endowmentTransactionDocument.getDocumentHeader().setDocumentNumber(this.documentNumber);
        endowmentTransactionDocument.setTransactionSubTypeCode(transactionSubTypeCode);
        endowmentTransactionDocument.setTransactionSourceTypeCode(transactionSourceTypeCode);
        endowmentTransactionDocument.setTransactionPosted(transactionPosted);
        endowmentTransactionDocument.setObjectId(objectId);
        endowmentTransactionDocument.setVersionNumber(versionNumber);
        endowmentTransactionDocument.refreshNonUpdateableReferences();

        return endowmentTransactionDocument;
    }
}
