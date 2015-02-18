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
package org.kuali.kfs.sys.service;

import org.kuali.rice.kew.api.WorkflowDocument;

/**
 * Interface to handle "meta-questions" about the workflow side of documents.
 * Used to provide common complex implementations for calls not supported by the
 * core workflow engine APIs.
 */
public interface FinancialSystemWorkflowHelperService {

    /**
     * Check whether the given workflow document presently has an adhoc approval
     * request for the given person.
     *
     * @param workflowDocument
     * @param principalId
     * @return
     */
    boolean isAdhocApprovalRequestedForPrincipal( WorkflowDocument workflowDocument, String principalId );

    String getApplicationDocumentStatus(String documentNumber);
}
