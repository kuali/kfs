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
package org.kuali.kfs.sys.document.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer;
import org.kuali.kfs.sys.document.service.AccountingLineAuthorizationTransformer;
import org.kuali.kfs.sys.document.web.TableJoining;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Like a regular accounting line rendering transformer, though this  
 */
public class AccountingLineAuthorizationTransformerImpl implements AccountingLineAuthorizationTransformer {

    /**
     * Performs transformations to the element rendering tree based on the authorization's reactions to the accounting line
     * @param elements the element rendering tree
     * @param accountingLine the accounting line to be rendered
     * @param document the document that accounting line lives on
     * @param lineAuthorizer the authorizer for the accounting line
     * @param newLine is this line a new line or a line already on a document?
     * 
     */
    public void transformElements(List<TableJoining> elements, AccountingLine accountingLine, AccountingDocument document, AccountingLineAuthorizer lineAuthorizer, boolean newLine, String accountingLinePropertyName) {
        final Person currentUser = GlobalVariables.getUserSession().getPerson();
        removeUnviewableBlocks(elements, lineAuthorizer.getUnviewableBlocks(document, accountingLine, newLine, currentUser));
    }
    
    /**
     * 
     * @param elements the elements of the rendering tree
     * @param unviewableBlocks a Set of the names of blocks that are not viewable
     */
    protected void removeUnviewableBlocks(List<TableJoining> elements, Set<String> unviewableBlocks) {
        if (unviewableBlocks.size() > 0) {
            Set<TableJoining> elementsToRemove = new HashSet<TableJoining>();
            for (TableJoining element : elements) {
                if (unviewableBlocks.contains(element.getName())) {
                    elementsToRemove.add(element);
                } else {
                    element.removeUnviewableBlocks(unviewableBlocks);
                }
            }
            elements.removeAll(elementsToRemove);
        }
    }
}

