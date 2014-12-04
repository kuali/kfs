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
package org.kuali.kfs.sys.document.authorization;

import java.util.List;
import java.util.Set;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.web.AccountingLineRenderingContext;
import org.kuali.kfs.sys.document.web.AccountingLineViewAction;
import org.kuali.rice.kim.api.identity.Person;

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
     * @param accountingLineRenderingContext a renderable context wrapping the accounting line that is being authorized against
     * @param accountingLinePropertyName the name of the property that represents the accounting line
     * @param lineIndex value, as Integer, of the index of the given accounting line within the group's collection of accounting
     *        lines; if null, then it is assumed that this is a new line
     * @param groupTitle title of the group from the data dictionary
     * @return a List of the Actions that are available for this line
     */
    public abstract List<AccountingLineViewAction> getActions(AccountingDocument accountingDocument, AccountingLineRenderingContext accountingLineRenderingContext, String accountingLinePropertyName, Integer lineIndex, Person currentUser, String groupTitle);

    /**
     * Determines if new lines should be rendered for the given accounting line group (identified by its property name)
     * 
     * @param accountingDocument the document that has accounting lines being authorized
     * @param accountingGroupProperty the property of this accounting group
     * @return true if new lines should be displayed, false otherwise
     */
    public abstract boolean renderNewLine(AccountingDocument accountingDocument, String accountingGroupProperty);

    /**
     * Determines if any entire group is rendered as editable, which means that a new line will appear
     * 
     * @param accountingDocument the accounting document which the collection of line are on
     * @param accountingLineRenderingContexts the accounting lines of the group, wrapped in AccountingLineRenderingContext implementations
     * @param currentUser the current user
     * @return true if the group can be edited, false otherwise
     */
    public abstract boolean isGroupEditable(AccountingDocument accountingDocument, List<? extends AccountingLineRenderingContext> accountingLineRenderingContexts, Person currentUser);

    /**
     * determine whether the current user has permission to edit the given field in the given accounting line
     * 
     * @param accountingDocument the given accounting document
     * @param accountingLine the given accounting line in the document
     * @param accountingLineCollectionProperty the property of the collection the given accounting line is in
     * @param fieldName the name of a field in the given accounting line
     * @param editableLine whether the parent line of this field is editable
     * @param editablePage whether the parent page of this field is editable
     * @param currentUser the current user
     * @return true if the the current user has permission to edit the given field in the given accounting line; otherwsie, false
     */
    public abstract boolean hasEditPermissionOnField(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, String fieldName, boolean editableLine, boolean editablePage, Person currentUser);
    
    /**
     * determine whether the current user has permission to edit the given accounting line as a whole
     * 
     * @param accountingDocument the given accounting document
     * @param accountingLine the given accounting line in the document
     * @param accountingLineCollectionProperty the property of the group that holds these accounting lines
     * @param currentUser the current user
     * @param pageIsEditable whether the current page is editable by the current user or not
     * @return true if the the current user has permission to edit the given accounting line; otherwsie, false
     */
    public abstract boolean hasEditPermissionOnAccountingLine(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, Person currentUser, boolean pageIsEditable);
}
