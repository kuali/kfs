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

