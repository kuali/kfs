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

import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.document.Document;

/**
 * A generic validation which will only validate at specified notes
 */
public class NodeSpecificValidation extends NodeAwareValidation {
    protected List<String> validationNodes;

    /**
     * @return the List of node names where this validation should occur
     */
    public List<String> getValidationNodes() {
        return validationNodes;
    }

    /**
     * Sets the list of node names where this validation should occur
     * @param validationNodes a List of node names
     */
    public void setValidationNodes(List<String> validationNodes) {
        this.validationNodes = validationNodes;
    }

    /**
     * Determines that something has been passed into the validationNodes property, or else the injected validation will never run
     * @see org.kuali.kfs.sys.document.validation.impl.NodeAwareValidation#isNodesPropertyValid()
     */
    @Override
    protected boolean isNodesPropertyValid() {
        return getValidationNodes() != null && !getValidationNodes().isEmpty();
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
            if (getValidationNodes() != null && getValidationNodes().contains(PRE_ROUTE)) {
                if (workflowDocument.isInitiated() || workflowDocument.isSaved()) {
                    return true; // special pre-route check
                }
            }

            final Set<String> currentNodes = workflowDocument.getCurrentNodeNames();
            for (String validationNode : getValidationNodes()) {
                if (currentNodes.contains(validationNode)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Currently, there does not seem a way in which this method would be called - stageValidation should be called, not this.  So we'll
     * throw an UnsupportedOperationException until someone calls me up and tells me I'm crazy
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        throw new UnsupportedOperationException("NodeSpecificValidation does not run validate - all validations are pushed to the composed Validation via stageValidation.  However, if you believe you called this method for a valid reason, please e-mail the KFS tech list.");
    }

}
