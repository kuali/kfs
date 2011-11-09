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
