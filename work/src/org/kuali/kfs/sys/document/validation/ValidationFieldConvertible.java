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
