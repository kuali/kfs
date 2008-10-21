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
package org.kuali.kfs.sys.document.authorization;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.FinancialSystemUser;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.web.AccountingLineViewAction;

/**
 * Methods used to determine certain permissions associated with an accounting line.
 */
public interface AccountingLineAuthorizer {
    
    /**
     * Determines which, if any, blocks whose children elements should not in any fashion be rendered
     * @param accountingDocument the accounting document the line to authorize is owned by
     * @param accountingLine the accounting line that is being authorized against
     * @param newLine whether the line is a new line or not
     * @return a Set of the names of blocks that should not being in any way rendered
     */
    public abstract Set<String> getUnviewableBlocks(AccountingDocument accountingDocument, AccountingLine accountingLine, boolean newLine, FinancialSystemUser currentUser);
    
    /**
     * Determines which, if any, blocks specified for accounting line rendering should be forced to be rendered as read only
     * @param accountingDocument the accounting document the line to authorize is owned by
     * @param accountingLine the accounting line that is being authorized against
     * @param newLine whether the line is a new line or not
     * @return a Set of the names of blocks where all children fields should be forced to render as read only
     */
    public abstract Set<String> getReadOnlyBlocks(AccountingDocument accountingDocument, AccountingLine accountingLine, boolean newLine, FinancialSystemUser currentUser);
    
    /**
     * Determines what actions are available to act upon the given accounting line
     * @param accountingDocument the accounting document the line to authorize is owned by
     * @param accountingLine the accounting line that is being authorized against
     * @param the name of the property that represents the accounting line
     * @param lineIndex value, as Integer, of the index of the given accounting line within the group's collection of accounting lines; if null, then it is assumed that this is a new line
     * @param editModesForDocument the edit modes on the whole document
     * @param groupTitle title of the group from the data dictionary
     * @return a List of the Actions that are available for this line
     */
    public abstract List<AccountingLineViewAction> getActions(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineProperty, Integer lineIndex, FinancialSystemUser currentUser, Map editModesForDocument, String groupTitle);
    
    /**
     * Determines if new lines should be rendered for the given accounting line group (identified by its property name)
     * @param accountingDocument the document that has accounting lines being authorized
     * @param accountingGroupProperty the property of this accounting group
     * @return true if new lines should be displayed, false otherwise
     */
    public abstract boolean renderNewLine(AccountingDocument accountingDocument, String accountingGroupProperty, FinancialSystemUser currentUser);
    
    /**
     * Determines the "classic" authorization mode for the accounting line, based on the given parameters
     * @param accountingDocument the accounting document holding the accounting line to test editability for
     * @param accountingLine the accounting line editability is being tested for
     * @param newLine is the given accounting line a new accounting line or is it already on the document?
     * @param currentUser the user attempting to edit the given accounting line
     * @param editModesForDocument the edit modes currently existing on the document
     * @return one of the "classic" edit modes - AuthorizationConstants.EditMode.UNVIEWABLE, AuthorizationConstants.EditMode.VIEW_ONLY, or AuthorizationConstants.EditMode.FULL_ENTRY
     */
    public abstract String getEditModeForAccountingLine(AccountingDocument accountingDocument, AccountingLine accountingLine, boolean newLine, FinancialSystemUser currentUser, Map<String, String> editModesForDocument);
    
    /**
     * Determines if the entire group is rendered as read only, which means that no new line will appear
     * @param accountingDocument the accounting document which the collection of line are on
     * @param accountingLineCollectionProperty the collection of lines
     * @param currentUser the current user
     * @param editModesForDocument the current edit modes on the document
     * @return true if the entire group is read only, false otherwise
     */
    public abstract boolean isGroupReadOnly(AccountingDocument accountingDocument, String accountingLineCollectionProperty, FinancialSystemUser currentUser, Map<String, String> editModesForDocument);
    
    /**
     * Determines which, if any, blocks specified for accounting line rendering should be forced to be rendered as editable when the line is readonly
     * @param accountingDocument the accounting document the line to authorize is owned by
     * @param accountingLine the accounting line that is being authorized against
     * @return a Set of the names of blocks where all children fields should be forced to render as editable
     */
    public abstract Set<String> getEditableBlocksInReadOnlyLine(AccountingDocument accountingDocument, AccountingLine accountingLine, FinancialSystemUser currentUser);
}
