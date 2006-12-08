/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/test/infrastructure/src/org/kuali/kfs/sys/DocumentTestUtils.java,v $
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
package org.kuali.test;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.DocumentHeader;
import org.kuali.core.document.DocumentNote;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.financial.bo.InternalBillingItem;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * DocumentTestUtils
 * 
 * 
 */
public class DocumentTestUtils {
    /**
     * @param quantity
     * @param stockDescription
     * @param stockNumber
     * @param unitAmount
     * @param unitOfMeasureCode
     * @return new InternalBillingItem initialized with the given values
     */
    public static InternalBillingItem createBillingItem(Integer quantity, String stockDescription, String stockNumber, Double unitAmount, String unitOfMeasureCode) {
        InternalBillingItem item = new InternalBillingItem();

        item.setItemQuantity(quantity);
        // item.setItemServiceDate( timestamp );
        item.setItemStockDescription(stockDescription);
        item.setItemStockNumber(stockNumber);
        item.setItemUnitAmount(new KualiDecimal(unitAmount.toString()));
        item.setUnitOfMeasureCode(unitOfMeasureCode);

        return item;
    }


    /**
     * @param documentHeaderId
     * @param documentNoteAuthor
     * @param documentNoteText
     * @return new DocumentNote initialized with the given values
     */
    public static DocumentNote createDocumentNote(String documentHeaderId, UniversalUser documentNoteAuthor, String documentNoteText) {
        java.util.Date now = new java.util.Date();
        DocumentNote documentNote = new DocumentNote();
        documentNote.setDocumentNumber(documentHeaderId);
        documentNote.setFinDocumentAuthorUniversalId(documentNoteAuthor.getPersonUniversalIdentifier());
        documentNote.setFinancialDocumentNoteText(documentNoteText);
        documentNote.setFinDocNotePostedDttmStamp(new java.sql.Timestamp(now.getTime()));

        return documentNote;
    }
    public static <D extends Document> D createDocument(DocumentService documentService,Class<D> docmentClass) throws WorkflowException {
        D document = (D)documentService.getNewDocument(docmentClass);
        document.setExplanation("unit test created document");

        DocumentHeader documentHeader = document.getDocumentHeader();
        documentHeader.setFinancialDocumentDescription("unit test created document");

        return document;
    }
}
