/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.test;

import org.kuali.core.bo.user.KualiUser;
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
    public static DocumentNote createDocumentNote(String documentHeaderId, KualiUser documentNoteAuthor, String documentNoteText) {
        java.util.Date now = new java.util.Date();
        DocumentNote documentNote = new DocumentNote();
        documentNote.setFinancialDocumentNumber(documentHeaderId);
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
