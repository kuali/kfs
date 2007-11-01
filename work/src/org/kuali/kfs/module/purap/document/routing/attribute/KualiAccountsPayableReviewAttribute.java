/*
 * Copyright 2007 The Kuali Foundation.
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

import java.util.List;

import org.kuali.core.service.DocumentService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.document.AccountsPayableDocument;

import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.AbstractWorkflowAttribute;
import edu.iu.uis.eden.routetemplate.RuleExtension;

/**
 * This class... TODO delyea - documentation
 */
public class KualiAccountsPayableReviewAttribute extends AbstractWorkflowAttribute {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiAccountsPayableReviewAttribute.class);

    private AccountsPayableDocument getAccountsPayableDocument(String documentNumber) {
        try {
            AccountsPayableDocument document = (AccountsPayableDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(documentNumber);
            if (ObjectUtils.isNull(document)) {
                String errorMsg = "Error trying to get document using doc id '" + documentNumber + "'";
                LOG.error("getAccountsPayableDocument() " + errorMsg);
                throw new RuntimeException(errorMsg);
            }
            document.refreshNonUpdateableReferences();
            return document;
        }
        catch (WorkflowException e) {
            String errorMsg = "Error trying to get document using doc id '" + documentNumber + "'";
            LOG.error("getAccountsPayableDocument() " + errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#isMatch(edu.iu.uis.eden.routeheader.DocumentContent, java.util.List)
     */
    public boolean isMatch(DocumentContent docContent, List<RuleExtension> ruleExtensions) {
        AccountsPayableDocument document = getAccountsPayableDocument(docContent.getRouteContext().getDocument().getRouteHeaderId().toString());
        return document.requiresAccountsPayableReviewRouting();
    }

}
