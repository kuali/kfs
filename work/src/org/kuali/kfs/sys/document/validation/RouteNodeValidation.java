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
