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
package org.kuali.kfs.sys.document;

import junit.framework.Assert;

import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.document.Copyable;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;


public class MaintenanceDocumentTestUtils extends KualiTestBase {


    public void testPlaceholder() {
        assertTrue("Test case needs to have at least one test.", true);
    }

    public static <T extends MaintenanceDocument> void testGetNewDocument_byDocumentClass(Class<T> documentClass, DocumentService documentService) throws Exception {
        T document = (T) documentService.getNewDocument(documentClass);
        // verify document was created
        assertNotNull(document);
        assertNotNull(document.getDocumentHeader());
        assertNotNull(document.getDocumentHeader().getDocumentNumber());
    }

    public static void testConvertIntoCopy_copyDisallowed(MaintenanceDocument document, DataDictionaryService dataDictionaryService) throws Exception {
        // change the dataDictionary to disallow copying
        DataDictionary d = dataDictionaryService.getDataDictionary();
        Class documentClass = document.getClass();
        boolean originalValue = d.getDocumentEntry(documentClass.getName()).getAllowsCopy();
        try {
            d.getDocumentEntry(documentClass.getName()).setAllowsCopy(false);

            boolean failedAsExpected = false;
            try {
                ((Copyable) document).toCopy();
            }
            catch (IllegalStateException e) {
                failedAsExpected = true;
            }

            assertTrue(failedAsExpected);
        }
        finally {
            d.getDocumentEntry(documentClass.getName()).setAllowsCopy(originalValue);
        }
    }


    /**
     * @ShouldCommitTransactions needed for this test
     * @see ShouldCommitTransactions
     */
    public static void testSaveDocument(MaintenanceDocument document, DocumentService documentService) throws Exception {
        // get document parameter
        document.prepareForSave();

        assertNotNull(document);
        assertNotNull(document.getDocumentHeader());
        assertNotNull(document.getDocumentHeader().getDocumentNumber());

        Maintainable newMaintainable = document.getNewMaintainableObject();
        assertNotNull("New Maintainable should never be null.", newMaintainable);
        PersistableBusinessObject newBo = newMaintainable.getBusinessObject();
        assertNotNull("New BO should never be null.", newBo);

        // save
        saveDocument(document, documentService);

        // retrieve
        MaintenanceDocument result = (MaintenanceDocument) documentService.getByDocumentHeaderId(document.getDocumentNumber());

        // verify
        assertMatch(document, result);
    }

    /**
     * @ShouldCommitTransactions needed for this test
     * @see ShouldCommitTransactions
     */
    public static void testConvertIntoCopy(MaintenanceDocument document, DocumentService documentService) throws Exception {
        // save the original doc, wait for status change
        document.prepareForSave();

        // collect some preCopy data
        String preCopyId = document.getDocumentNumber();
        String preCopyCopiedFromId = document.getDocumentHeader().getDocumentTemplateNumber();
        String preCopyStatus = document.getDocumentHeader().getWorkflowDocument().getStatus().getCode();

        // validate preCopy state
        assertNotNull(preCopyId);
        assertNull(preCopyCopiedFromId);

        // do the copy
        ((Copyable) document).toCopy();
        // compare to preCopy state

        String postCopyId = document.getDocumentNumber();
        assertFalse(postCopyId.equals(preCopyId));
        // verify that docStatus has changed
        String postCopyStatus = document.getDocumentHeader().getWorkflowDocument().getStatus().getCode();
        assertFalse(postCopyStatus.equals(preCopyStatus));

        // copiedFrom should be equal to old id
        String copiedFromId = document.getDocumentHeader().getDocumentTemplateNumber();
        assertEquals(preCopyId, copiedFromId);
    }


    public static void saveDocument(MaintenanceDocument document, DocumentService documentService) throws WorkflowException {
        try {
            documentService.saveDocument(document);
        }
        catch (ValidationException e) {
            // If the business rule evaluation fails then give us more info for debugging this test.
            fail(e.getMessage() + ", " + GlobalVariables.getMessageMap());
        }
    }


    public static <T extends Document> void assertMatch(T document1, T document2) {
        Assert.assertEquals(document1.getDocumentNumber(), document2.getDocumentNumber());
        Assert.assertEquals(document1.getDocumentHeader().getWorkflowDocument().getDocumentTypeName(), document2.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
    }

}
