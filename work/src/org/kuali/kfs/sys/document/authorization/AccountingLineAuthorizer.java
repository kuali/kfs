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
import java.util.Set;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.web.AccountingLineViewAction;
import org.kuali.kfs.sys.document.web.AccountingLineViewField;
import org.kuali.rice.kim.bo.Person;

/**
 * Methods used to determine certain permissions associated with an accounting line.
 */
public interface AccountingLineAuthorizer {

    /**
     * Determines which, if any, blocks whose children elements should not in any fashion be rendered
     * 
     * @param accountingDocument the accounting document the line to authorize is owned by
     * @param accountingLine the accounting line that is being authorized against
     * @param newLine whether the line is a new line or not
     * @return a Set of the names of blocks that should not being in any way rendered
     */
    public abstract Set<String> getUnviewableBlocks(AccountingDocument accountingDocument, AccountingLine accountingLine, boolean newLine, Person currentUser);

    /**
     * Determines what actions are available to act upon the given accounting line
     * 
     * @param accountingDocument the accounting document the line to authorize is owned by
     * @param accountingLine the accounting line that is being authorized against
     * @param accountingLinePropertyName the name of the property that represents the accounting line
     * @param lineIndex value, as Integer, of the index of the given accounting line within the group's collection of accounting
     *        lines; if null, then it is assumed that this is a new line
     * @param groupTitle title of the group from the data dictionary
     * @return a List of the Actions that are available for this line
     */
    public abstract List<AccountingLineViewAction> getActions(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLinePropertyName, Integer lineIndex, Person currentUser, String groupTitle);

    /**
     * Determines if new lines should be rendered for the given accounting line group (identified by its property name)
     * 
     * @param accountingDocument the document that has accounting lines being authorized
     * @param accountingGroupProperty the property of this accounting group
     * @return true if new lines should be displayed, false otherwise
     */
    public abstract boolean renderNewLine(AccountingDocument accountingDocument, String accountingGroupProperty, Person currentUser);

    /**
     * Determines if the entire group is rendered as editable, which means that a new line will appear
     * 
     * @param accountingDocument the accounting document which the collection of line are on
     * @param accountingLineCollectionProperty the collection of lines
     * @param currentUser the current user
     * @return true if the entire group is editable, false otherwise
     */
    public abstract boolean isGroupEditable(AccountingDocument accountingDocument, String accountingLineCollectionProperty, Person currentUser);

    /**
     * Determines whether the given field can be modified or not
     * 
     * @param accountingDocument the accounting document the accounting line is on or eventually will be on
     * @param accountingLine the accounting line the field is a member of
     * @param field the field we're testing the modifyability of
     * @param currentUser the user requesting this permission
     * @return true if field can be modified, false otherwise
     */
    public abstract boolean isFieldEditable(AccountingDocument accountingDocument, AccountingLine accountingLine, AccountingLineViewField field, Person currentUser);

    /**
     * determine whether the current user has permission to edit the given field in the given accounting line
     * 
     * @param accountingDocument the given accounting document
     * @param accountingLine the given accounting line in the document
     * @param fieldName the name of a field in the given accounting line
     * @param currentUser the current user
     * @return true if the the current user has permission to edit the given field in the given accounting line; otherwsie, false
     */
    public abstract boolean hasEditPermissionOnField(AccountingDocument accountingDocument, AccountingLine accountingLine, String fieldName, Person currentUser);      
}
