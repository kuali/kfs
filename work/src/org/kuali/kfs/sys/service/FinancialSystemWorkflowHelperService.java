/*
 * Copyright 2012 The Kuali Foundation.
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
