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
package org.kuali.kfs.module.purap.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.parke;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.purap.PurchasingAccountsPayableModuleService;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocumentTest;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.DocumentService;

@ConfigureContext(session = parke)
public class PurchasingAccountsPayableModuleServiceTest extends KualiTestBase {
    
    @ConfigureContext(session = parke, shouldCommitTransactions=true)
    public void testAddAssignedAssetNumbers() throws Exception {
        Integer purchaseOrderNumber = null;
        PurchaseOrderDocumentTest documentTest = new PurchaseOrderDocumentTest();

            PurchaseOrderDocument poDocument = documentTest.buildSimpleDocument();
            DocumentService documentService = SpringContext.getBean(DocumentService.class);
            poDocument.prepareForSave();       
            AccountingDocumentTestUtils.saveDocument(poDocument, documentService);
            PurchaseOrderDocument result = (PurchaseOrderDocument) documentService.getByDocumentHeaderId(poDocument.getDocumentNumber());
            purchaseOrderNumber = result.getPurapDocumentIdentifier();

        List<Long> assetNumbers = new ArrayList<Long>();
        assetNumbers.add(new Long("12345"));
        assetNumbers.add(new Long("12346"));
        String authorId = "khuntley";
        StringBuffer noteText = new StringBuffer("Asset Numbers have been created for this document: ");
        for (int i = 0; i<assetNumbers.size(); i++) {
            noteText.append(assetNumbers.get(i).toString());
            if (i < assetNumbers.size() -1) {
                noteText.append(", ");
            }
        }
        SpringContext.getBean(PurchasingAccountsPayableModuleService.class).addAssignedAssetNumbers(purchaseOrderNumber, authorId, noteText.toString());
        PurchaseOrderDocument po = SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(purchaseOrderNumber);
        assertNotNull("PO should not have been null",po);
        List<Note> boNotes = po.getNotes();
        boolean hasNote = false;
        for( Note note : boNotes ) {
            if (note.getNoteText().contains("Asset Numbers have been created for this document:")) {
                hasNote = true;
                break;
            }
        }
        assertTrue("note was missing from PO",hasNote);
    }
    
    @ConfigureContext(session = parke, shouldCommitTransactions=true)
    public void testGetPurchaseOrderInquiryUrl() throws Exception {
        Integer purchaseOrderNumber = null;
        PurchaseOrderDocumentTest documentTest = new PurchaseOrderDocumentTest();
            PurchaseOrderDocument poDocument = documentTest.buildSimpleDocument();
            DocumentService documentService = SpringContext.getBean(DocumentService.class);
            poDocument.prepareForSave();       
            AccountingDocumentTestUtils.saveDocument(poDocument, documentService);
            PurchaseOrderDocument result = (PurchaseOrderDocument) documentService.getByDocumentHeaderId(poDocument.getDocumentNumber());
            purchaseOrderNumber = result.getPurapDocumentIdentifier();
        String url = SpringContext.getBean(PurchasingAccountsPayableModuleService.class).getPurchaseOrderInquiryUrl(purchaseOrderNumber);
        assertFalse("url was empty",StringUtils.isEmpty(url));
    }
}

