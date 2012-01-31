/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.fixture;

import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.ObjectUtils;
import org.mortbay.log.Log;

/**
 * Fixture class for ContractsGrantsInvoiceDocument
 */
public enum ContractsGrantsInvoiceDocumentFixture {
    CG_INV_DOC1(new Long(111)), CG_INV_DOC2(new Long(1234));

    private Long proposalNumber;

    private ContractsGrantsInvoiceDocumentFixture(Long proposalNumber) {

        this.proposalNumber = proposalNumber;

    }

    private void setValues(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        contractsGrantsInvoiceDocument.setProposalNumber(this.proposalNumber);

    }


    public ContractsGrantsInvoiceDocument createContractsGrantsInvoiceDocument(DocumentService documentService) {
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = null;
        try {
            contractsGrantsInvoiceDocument = (ContractsGrantsInvoiceDocument) documentService.getNewDocument("CGIN");
        }
        catch (WorkflowException e) {
            Log.debug(e.toString());
        }

        if (ObjectUtils.isNotNull(contractsGrantsInvoiceDocument)) {
            setValues(contractsGrantsInvoiceDocument);
        }
        return contractsGrantsInvoiceDocument;
    }

}
