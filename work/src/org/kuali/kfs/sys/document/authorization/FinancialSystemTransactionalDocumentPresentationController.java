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
package org.kuali.kfs.sys.document.authorization;

import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentPresentationController;

/**
 * The methods needed to answer questions about presentation viewing based on internal document state for FinancialSystemTransactionalDocuments
 */
public interface FinancialSystemTransactionalDocumentPresentationController extends TransactionalDocumentPresentationController {

    /**
     * Determines if the given document can be error corrected, based on internal document state
     * @param document the document to error correct
     * @return true if the document can be error corrected, false otherwise
     */
    public abstract boolean canErrorCorrect(FinancialSystemTransactionalDocument document);
}
