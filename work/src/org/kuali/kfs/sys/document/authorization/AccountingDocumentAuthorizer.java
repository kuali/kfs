/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.kfs.document.authorization;

import java.util.List;
import java.util.Map;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.document.authorization.TransactionalDocumentAuthorizer;
import org.kuali.module.chart.bo.ChartUser;

/**
 * Extension to TransactionalDocumentAuthorizer interface which adds financial-document-specific methods.
 */
public interface AccountingDocumentAuthorizer extends TransactionalDocumentAuthorizer {
    /**
     * @param document
     * @param user
     * @return Map with field names as keys that are allowed to be edited in special edit modes.
     */
    public Map getAccountingLineEditableFields(Document document, UniversalUser user);

    /**
     * Variant version of getEditMode which uses passed-in sourceAccountingLines and targetAccountingLines instead of getting them
     * out of the given Document, since the Document may (when this gets called from KualiDocumentActionBase) contain invalid
     * accountingLines whose invalidity will prevent the editMode from being calculated correctly.
     * 
     * @param document
     * @param user
     * @param sourceAccountingLines
     * @param targetAccountingLines
     * @return Map with keys AuthorizationConstants.EditMode value (String) which indicates what operations the user is currently
     *         allowed to take on that document.
     */
    public Map getEditMode(Document document, UniversalUser user, List sourceAccountingLines, List targetAccountingLines);


    /**
     * Initially wanted to use a Set, but JSTL doesn't seem to allow me to navigate Sets as easily as Maps. Initially used Account
     * objects as keys, but the Accounts of AccountingLines are sometimes left unpopulated when you reach the JSP, so I had to add a
     * method to Account and to AccountingLine which would generate a well-formatted String from the primitive account-related keys
     * in an Account or AccountingLine; that well-formatted String is now used as the key in this Map.
     * 
     * @param document
     * @param user
     * @return Map of Account objects, indexed by accountKey (return value of account.buildAccountKey), which the given user should
     *         be allowed to edit
     */
    public Map getEditableAccounts(TransactionalDocument document, ChartUser user);
}
