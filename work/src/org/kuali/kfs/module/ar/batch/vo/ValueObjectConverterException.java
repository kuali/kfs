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
package org.kuali.kfs.module.ar.batch.vo;

public class ValueObjectConverterException extends RuntimeException {

    private String failedProperty;
    
    /**
     * 
     * Constructs a ValueObjectConverterException.java.  These are exceptions that 
     * occur when trying to convert a VO (Value Object) into a proper BO 
     * (Business Object).  The exception includes the property that failed, in 
     * addition to the message.
     * 
     * @param failedProperty The name of the VO property that failed to convert.
     * @param message The detailed message about why the property failed to convert.
     */
    public ValueObjectConverterException(String failedProperty, String message) {
        super("Failed to convert property [" + failedProperty + "]: " + message);
        this.failedProperty = failedProperty;
    }

    /**
     * 
     * @return Returns the failedProperty field.
     */
    public String getFailedProperty() {
        return failedProperty;
    }
    
}
