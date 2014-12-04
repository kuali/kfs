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
