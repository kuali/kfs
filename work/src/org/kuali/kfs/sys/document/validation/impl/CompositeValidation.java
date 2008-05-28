/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.validation;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.rule.event.AttributedDocumentEvent;


/**
 * This validation represents a hiearchy of validations
 */
public class CompositeValidation implements Validation {
    private List<Validation> subValidations;
    private boolean shouldQuitOnFail = false;

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
            if (!currResult && validation.shouldQuitOnFail()) {
                break;
            }
        }
        
        return result;
    }

    /**
     * Just call validate!
     * @see org.kuali.kfs.validation.Validation#stageValidation(org.kuali.kfs.rule.event.AttributedDocumentEvent)
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
