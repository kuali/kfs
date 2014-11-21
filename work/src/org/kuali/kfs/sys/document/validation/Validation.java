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
 * An interface for a simple validation
 */
public interface Validation {
    /**
     * This method validates that certain parameters 
     * @param parameters a list of parameters to validate
     * @return true if validation should continue, false otherwise
     */
   public abstract boolean validate(AttributedDocumentEvent event);
    
   /**
    * Returns whether the validation process should quit on the failure of this validation
    * @return true if the validation process should quit, false otherwise
    */
   public abstract boolean shouldQuitOnFail();
   
   /**
    * Stages the execution of a Validation
    * @param event the event the validate
    * @return the boolean result of the staged Validation
    */
   public abstract boolean stageValidation(AttributedDocumentEvent event);
}
