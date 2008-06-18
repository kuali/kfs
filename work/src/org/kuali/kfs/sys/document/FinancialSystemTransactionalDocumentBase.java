/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.document;

import org.kuali.core.document.Copyable;
import org.kuali.core.document.SessionDocument;
import org.kuali.rice.KNSServiceLocator;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class is a KFS specific TransactionalDocumentBase class
 */
public class FinancialSystemTransactionalDocumentBase extends FinancialSystemDocument implements FinancialSystemTransactionalDocument {

    /**
     * @see org.kuali.core.document.FinancialSystemTransactionDocument#getAllowsErrorCorrection()
     * Checks if error correction is set to true in data dictionary and the document instance implements
     * Correctable. Furthermore, a document cannot be error corrected twice.
     */
    public boolean getAllowsErrorCorrection() {
        boolean allowErrorCorrection = KNSServiceLocator.getTransactionalDocumentDictionaryService().getAllowsErrorCorrection(this).booleanValue() && this instanceof Correctable;
        allowErrorCorrection = allowErrorCorrection && getDocumentHeader().getCorrectedByDocumentId() == null;

        return allowErrorCorrection;
    }

    /**
     * @see org.kuali.kfs.document.Correctable#toErrorCorrection()
     */
    public void toErrorCorrection() throws WorkflowException, IllegalStateException {
        if (!this.getAllowsErrorCorrection()) {
            throw new IllegalStateException(this.getClass().getName() + " does not support document-level error correction");
        }

        String sourceDocumentHeaderId = getDocumentNumber();
        setNewDocumentHeader();
        getDocumentHeader().setFinancialDocumentInErrorNumber(sourceDocumentHeaderId);
        addCopyErrorDocumentNote("error-correction for document " + sourceDocumentHeaderId);
    }
    
    /*
     * Below methods copied from Rice TransactionalDocumentBase class
     */
    /**
     * Checks if copy is set to true in data dictionary and the document instance implements
     * Copyable.
     * 
     * @see org.kuali.core.document.DocumentBase#getAllowsCopy()
     * @see org.kuali.core.document.TransactionalDocument#getAllowsCopy()
     */
    public boolean getAllowsCopy() {
        return KNSServiceLocator.getTransactionalDocumentDictionaryService().getAllowsCopy(this).booleanValue() && this instanceof Copyable;
    }

    /**
     * This method to check whether the document class implements SessionDocument
     * @see org.kuali.core.document.TransactionalDocument#isSessionDocument()
     * 
     * @return
     */
    public boolean isSessionDocument() {
        return SessionDocument.class.isAssignableFrom(this.getClass());
    }

}
