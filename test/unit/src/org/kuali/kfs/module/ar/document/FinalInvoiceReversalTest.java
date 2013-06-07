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
package org.kuali.kfs.module.ar.document;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAwardAccount;
import org.kuali.kfs.module.ar.businessobject.FinalInvoiceReversalEntry;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.document.validation.impl.FinalInvoiceReversalValidation;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.KualiModuleService;

/**
 * This class tests the final invoice reversal process
 */
@ConfigureContext(session = khuntley)
public class FinalInvoiceReversalTest extends CGInvoiceDocumentSetupTest {

    protected void setUp() throws Exception {
        super.setUp();
        document.getInvoiceGeneralDetail().setFinalBill(true);
        document.doWhenFinalInvoice();
    }

    public void testFinalInvoiceDocumentValidation() throws WorkflowException {
        FinalInvoiceReversalDocument firDocument = (FinalInvoiceReversalDocument) documentService.getNewDocument(FinalInvoiceReversalDocument.class);
        FinalInvoiceReversalEntry entry = new FinalInvoiceReversalEntry();
        entry.setInvoiceDocumentNumber(document.getDocumentNumber());
        firDocument.addInvoiceEntry(entry);
        documentService.saveDocument(firDocument);
        assertTrue(FinalInvoiceReversalValidation.validateDocument(firDocument));
    }

    public void testInvoiceReversal() throws WorkflowException {
        FinalInvoiceReversalEntry entry = new FinalInvoiceReversalEntry();
        entry.setInvoiceDocumentNumber(document.getDocumentNumber());
        FinalInvoiceReversalDocument firDocument = (FinalInvoiceReversalDocument) documentService.getNewDocument(FinalInvoiceReversalDocument.class);
        firDocument.addInvoiceEntry(entry);
        documentService.saveDocument(firDocument);
        Iterator iterator = document.getAccountDetails().iterator();
        while (iterator.hasNext()) {
            InvoiceAccountDetail id = (InvoiceAccountDetail) iterator.next();
            Map<String, Object> mapKey = new HashMap<String, Object>();
            mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, id.getAccountNumber());
            mapKey.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, id.getChartOfAccountsCode());
            mapKey.put(KFSPropertyConstants.PROPOSAL_NUMBER, document.getProposalNumber());
            ContractsAndGrantsCGBAwardAccount awardAccount = (ContractsAndGrantsCGBAwardAccount) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsCGBAwardAccount.class).getExternalizableBusinessObject(ContractsAndGrantsCGBAwardAccount.class, mapKey);
            assertTrue(awardAccount.isFinalBilled());
        }

        firDocument.updateContractsGrantsInvoiceDocument();

        document = (ContractsGrantsInvoiceDocument) documentService.getByDocumentHeaderId(document.getDocumentNumber());
        assertFalse(document.getInvoiceGeneralDetail().isFinalBill());
        iterator = document.getAccountDetails().iterator();
        while (iterator.hasNext()) {
            InvoiceAccountDetail id = (InvoiceAccountDetail) iterator.next();
            Map<String, Object> mapKey = new HashMap<String, Object>();
            mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, id.getAccountNumber());
            mapKey.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, id.getChartOfAccountsCode());
            mapKey.put(KFSPropertyConstants.PROPOSAL_NUMBER, document.getProposalNumber());
            ContractsAndGrantsCGBAwardAccount awardAccount = (ContractsAndGrantsCGBAwardAccount) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsCGBAwardAccount.class).getExternalizableBusinessObject(ContractsAndGrantsCGBAwardAccount.class, mapKey);
            assertFalse(awardAccount.isFinalBilled());
        }
    }
}
