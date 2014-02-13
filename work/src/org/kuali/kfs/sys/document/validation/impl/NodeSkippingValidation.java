/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.validation.impl;

import java.util.List;
import java.util.Set;

import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.document.Document;

/**
 * The opposite of org.kuali.kfs.sys.document.validation.impl.NodeSpecificValidation, this validation will run the child validation
 * on every node <strong>except</strong> those listed as validationSkipNodes
 */
public class NodeSkippingValidation extends NodeAwareValidation {
    protected List<String> validationSkipNodes;

    /**
     * @return a List of nodes where this validation should be skipped
     */
    public List<String> getValidationSkipNodes() {
        return validationSkipNodes;
    }

    /**
     * Sets a List of route node names where this validation should be skipped
     * @param validationSkipNodes the List of node names to skip
     */
    public void setValidationSkipNodes(List<String> validationSkipNodes) {
        this.validationSkipNodes = validationSkipNodes;
    }

    /**
     * There's less harm here if no validation nodes are set, because the validation will just run everywhere - so this is always true
     * @see org.kuali.kfs.sys.document.validation.impl.NodeAwareValidation#isNodesPropertyValid()
     */
    @Override
    protected boolean isNodesPropertyValid() {
        return true;
    }

    /**
     * Determines if this validation should be run at this node
     * @param document the document which is being validated
     * @return true if one of the validation nodes is within the current nodes and the validation should run; false otherwise
     */
    @Override
    protected boolean shouldRunValidation(Document document) {
        if (document == null) {
            throw new IllegalStateException("Attempting to run NodeSpecificValidation but the event didn't have a document associated with it");
        }
        final DocumentHeader documentHeader = document.getDocumentHeader();
        final WorkflowDocument workflowDocument = documentHeader.getWorkflowDocument();

        if (workflowDocument != null) {
            if (getValidationSkipNodes() != null && getValidationSkipNodes().contains(PRE_ROUTE)) {
                if (workflowDocument.isInitiated() || workflowDocument.isSaved()) {
                    return false; // special pre-route check
                }
            }

            final Set<String> currentNodes = workflowDocument.getCurrentNodeNames();
            for (String validationNode : getValidationSkipNodes()) {
                if (currentNodes.contains(validationNode)) {
                    return false;
                }
            }
        }
        return true;
    }
}
