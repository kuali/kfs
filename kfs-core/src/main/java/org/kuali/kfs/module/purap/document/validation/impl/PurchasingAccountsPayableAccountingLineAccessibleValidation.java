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

