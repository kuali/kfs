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

/**
 * Even as we make perfectly good classes, the Maw of Spring calls out for Validations to eat and to proxy.  We cannot argue with the beast; this being Java,
 * we may not subsume to the two concepts into one; thus we find ourselves, exhuasted and outraged, with no choice but to slave and feed Spring interfaces 
 * such as this, causing the rotted wyrm to spew out the beans we do crave.  Such wretched souls are we!
 */
public interface ValidationFieldConvertible {
    /**
     * Gets the sourceEventProperty attribute, the property of the event to transfer to the validation
     * @return Returns the sourceEventProperty.
     */
    public abstract String getSourceEventProperty();
    
    /**
     * Gets the targetValidationProperty attribute, the property on the validation to transfer information from the event to
     * @return Returns the targetValidationProperty.
     */
    public abstract String getTargetValidationProperty();
}
