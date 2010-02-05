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
package org.kuali.kfs.sys.document.validation.impl;

import java.util.List;

import org.kuali.kfs.sys.document.validation.Validation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;


/**
 * This validation represents a hiearchy of validations
 */
public class CompositeValidation implements Validation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CompositeValidation.class);
    
    protected List<Validation> subValidations;
    protected boolean shouldQuitOnFail = false;

    /**
     * Validates each sub-validation in turn, returning the cumulative effect of each
     * @see org.kuali.kfs.rules.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        if (subValidations == null) {
            throw new IllegalStateException("A composite validation must have children validations");
        }
        
        boolean result = true;
        boolean currResult;
        
        for (Validation validation: subValidations) {
            currResult = validation.stageValidation(event);
            result &= currResult;
            
            if(!currResult) {
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug(validation.getClass() + " failed");
                }
            }
            
            if (!currResult && validation.shouldQuitOnFail()) {
                break;
            }
        }
        
        return result;
    }

    /**
     * Just call validate!
     * @see org.kuali.kfs.sys.document.validation.Validation#stageValidation(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean stageValidation(AttributedDocumentEvent event) {
        return validate(event);
    }

    /**
     * Returns the List of validations to test
     * @return a List of validations to test
     */
    public List<Validation> getValidations() {
        return this.subValidations;
    }
    
    /**
     * Sets the List of validations to test
     * @param validations a List of validations to test against
     */
    public void setValidations(List<Validation> validations) {
        this.subValidations = validations;
    }

    /**
     * Gets the shouldQuitOnFail attribute. 
     * @return Returns the shouldQuitOnFail.
     */
    public boolean shouldQuitOnFail() {
        return shouldQuitOnFail;
    }

    /**
     * Sets the shouldQuitOnFail attribute value.
     * @param shouldQuitOnFail The shouldQuitOnFail to set.
     */
    public void setQuitOnFail(boolean shouldQuitOnFail) {
        this.shouldQuitOnFail = shouldQuitOnFail;
    }
}
