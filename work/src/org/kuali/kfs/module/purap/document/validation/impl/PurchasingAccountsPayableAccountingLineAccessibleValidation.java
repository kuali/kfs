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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.Arrays;
import java.util.List;

import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.impl.AccountingLineAccessibleValidation;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

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
    protected static List getCurrentRouteLevels(KualiWorkflowDocument workflowDocument) {
        try {
            return Arrays.asList(workflowDocument.getNodeNames());
        }
        catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String getGroupName() {
        return "source";
    }

    @Override
    protected String getAccountingLineCollectionProperty() {
        return "items.sourceAccountingLines"; 
    }

}

