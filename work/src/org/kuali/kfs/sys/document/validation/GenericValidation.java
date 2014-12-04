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

import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

/**
 * An interface that represents a generic validation.
 */
public abstract class GenericValidation extends ParameterizedValidation implements Validation {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GenericValidation.class);
    private boolean quitOnFail = false;
    
    /**
     * This version of validate actually sets up the parameter list and then calls validate(Object[] parameters)
     * @param event the event that requested this validation
     * @return true if validation succeeded and the process required validation should continue, false otherwise
     */
    public boolean stageValidation(AttributedDocumentEvent event) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Staging validation for: "+getClass().getName()+" for event "+event.getClass().getName());
        }
        populateParametersFromEvent(event);
        return validate(event);
    }
    
    /**
     * Returns whether the validation process should quit on the failure of this validation
     * @return true if the validation process should quit, false otherwise
     */
    public boolean shouldQuitOnFail() {
        return quitOnFail;
    }
    
    /**
     * Sets whether this rule should quit on fail or not
     * @param quitOnFail true if the validation process should end if this rule fails, false otherwise
     */
    public void setQuitOnFail(boolean quitOnFail) {
        this.quitOnFail = quitOnFail;
    }
}
