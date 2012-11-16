/*
 * Copyright 2008 The Kuali Foundation
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
