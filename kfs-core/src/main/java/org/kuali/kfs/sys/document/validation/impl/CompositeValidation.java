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
