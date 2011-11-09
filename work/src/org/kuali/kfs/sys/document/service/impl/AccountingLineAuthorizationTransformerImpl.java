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

