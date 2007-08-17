/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.workflow.module.purap.attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;

import edu.iu.uis.eden.Id;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.routetemplate.ResolvedQualifiedRole;
import edu.iu.uis.eden.routetemplate.Role;
import edu.iu.uis.eden.routetemplate.UnqualifiedRoleAttribute;
import edu.iu.uis.eden.user.AuthenticationUserId;

/**
 * TODO delyea - documentation
 * 
 */
public class PurApSourceDocumentRouteUserRoleAttribute extends UnqualifiedRoleAttribute {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurApSourceDocumentRouteUserRoleAttribute.class);    
    
    private static final String SOURCE_DOC_ROUTED_BY_USER_ROLE_KEY = "SOURCE_DOC_ROUTED_BY_USER";
    private static final String SOURCE_DOC_ROUTED_BY_USER_ROLE_LABEL = "User who Routed Source Document";

    private static final Role ROLE = new Role(PurApSourceDocumentRouteUserRoleAttribute.class, SOURCE_DOC_ROUTED_BY_USER_ROLE_KEY, SOURCE_DOC_ROUTED_BY_USER_ROLE_LABEL);
    private static final List<Role> ROLES;
    static {
        ArrayList<Role> roles = new ArrayList<Role>(1);
        roles.add(ROLE);
        ROLES = Collections.unmodifiableList(roles);
    }

    /**
     * @see edu.iu.uis.eden.routetemplate.UnqualifiedRoleAttribute#getRoleNames()
     */
    @Override
    public List<Role> getRoleNames() {
        return ROLES;
    }
    
    private void assertDocumentNotNull(PurchasingAccountsPayableDocument document) {
        if (ObjectUtils.isNull(document)) {
            String errorMessage = "Document with doc id '" + document.getDocumentNumber() + "' and class '" + document.getClass() + "' does not exist in system";
            LOG.error("resolveRole() " + errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    /**
     * TODO delyea - documentation
     * 
     * @param routeContext the RouteContext
     * @param roleName the role name
     * @return a ResolvedQualifiedRole
     */
    @Override
    public ResolvedQualifiedRole resolveRole(RouteContext routeContext, String roleName) throws EdenUserNotFoundException {
        String documentNumber = null;
        try {
            documentNumber = routeContext.getDocument().getRouteHeaderId().toString();
            PurchasingAccountsPayableDocument document = (PurchasingAccountsPayableDocument)SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(documentNumber);
            assertDocumentNotNull(document);
            document.refreshNonUpdateableReferences();
            PurchasingAccountsPayableDocument sourceDocument = document.getPurApSourceDocumentIfPossible();
            // method getSourceDocumentIfPossible() could return null but for using this instance we should get something back
            assertDocumentNotNull(sourceDocument);
            // return the user who routed the source document
            DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
            String label = "User Who Routed " + document.getPurApSourceDocumentLabelIfPossible() + " " + sourceDocument.getPurapDocumentIdentifier();
            return new ResolvedQualifiedRole(label, Arrays.asList(new Id[] {new AuthenticationUserId(sourceDocument.getDocumentHeader().getWorkflowDocument().getRoutedByUserNetworkId())}));
        }
        catch (WorkflowException e) {
            String errorMessage = "Workflow problem while trying to get document using doc id '" + documentNumber + "'";
            LOG.error("resolveRole() " + errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }
}
