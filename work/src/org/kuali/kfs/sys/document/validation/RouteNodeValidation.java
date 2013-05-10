/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kew.api.WorkflowDocument;

/**
 * An abstract class that creates an easy way to do routeNode validations.  Basically,
 * extenders set a validRouteNodeNames - a list of all valid route nodes to perform the validation.
 */
public abstract class RouteNodeValidation extends GenericValidation {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RouteNodeValidation.class);

    protected List<String> validRouteNodeNames;

    @Override
    public boolean stageValidation(AttributedDocumentEvent event) {
        boolean valid = true;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Staging validation for: "+getClass().getName()+" for event "+event.getClass().getName());
        }
        populateParametersFromEvent(event);

        Collection<String> currentRouteLevels = new ArrayList<String>();
        try {
            WorkflowDocument workflowDoc = event.getDocument().getDocumentHeader().getWorkflowDocument();
            currentRouteLevels = workflowDoc.getNodeNames();
            for(String nodeName : validRouteNodeNames) {
                if (currentRouteLevels.contains(nodeName) && workflowDoc.isApprovalRequested()) {
                    return validate(event);
                }
            }

        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return valid;


    }





    public void setValidRouteNodeNames(List<String> validRouteNodeNames) {
        this.validRouteNodeNames = validRouteNodeNames;
    }

    /**
     * @return list of valid route node names
     */
    public List<String> getValidRouteNodeNames() {
        return validRouteNodeNames;
    }





}
