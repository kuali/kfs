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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.sys.document.validation.impl.AccountingLineAccessibleValidation;
import org.kuali.rice.kew.api.WorkflowDocument;

/**
 * A validation that checks whether the given accounting line is accessible to the given user or not
 */
public class PurchasingAccountsPayableAccountingLineAccessibleValidation extends AccountingLineAccessibleValidation {

    /**
     * Determines the route levels for a given document.
     * 
     * @param workflowDocument The workflow document from which the current route levels are to be obtained.
     * @return List The List of current route levels of the given document.
     */
    protected static Set<String> getCurrentRouteLevels(WorkflowDocument workflowDocument) {
        Set<String> currentNodeNames = workflowDocument.getCurrentNodeNames();
        return currentNodeNames;
    }

    @Override
    protected String getGroupName() {
        return "source";
    }

    @Override
    protected String getAccountingLineCollectionProperty() {
        return "items.sourceAccountingLines"; 
    }
    
    /**
     * @return true if a dummy account identifier should be set on the accounting line, false otherwise
     */
    protected boolean needsDummyAccountIdentifier() {
        if (((PurApAccountingLine)getAccountingLineForValidation()).getAccountIdentifier() != null) {
            return false;
        }
        
        final WorkflowDocument workflowDocument = getAccountingDocumentForValidation().getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.isInitiated() || workflowDocument.isSaved()) {
            return false;
        }
        
        return true;
    }
}

