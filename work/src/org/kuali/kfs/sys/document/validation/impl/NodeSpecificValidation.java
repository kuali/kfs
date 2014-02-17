/*
 * Copyright 2013 The Kuali Foundation.
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
