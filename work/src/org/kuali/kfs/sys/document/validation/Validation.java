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
