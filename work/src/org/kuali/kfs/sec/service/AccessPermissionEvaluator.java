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
package org.kuali.kfs.sec.service;

import java.util.Map;

import org.kuali.rice.kim.api.identity.Person;

/**
 * AccessPermissionEvaluator classes provide the evaluation of a given security permission. Methods are exposed for setting the user's qualification for a permission and method
 * exposed to pass a value and perform the evaluation
 */
public interface AccessPermissionEvaluator {

    /**
     * Evaluates the given value against the permission definition. How the evaluation is carried out depends on implementation
     * 
     * @param value String value to evaluate
     * @return True if value is allowed based on the permission definition or false if it is not allowed
     */
    public boolean valueIsAllowed(String value);

    /**
     * Setter for the constraint code found on the user's qualification record
     * 
     * @param constraintCode
     */
    public void setConstraintCode(String constraintCode);

    /**
     * Setter for the operator code found on the user's qualification record
     * 
     * @param operatorCode
     */
    public void setOperatorCode(String operatorCode);

    /**
     * Setter for the property value found on the user's qualification record
     * 
     * @param propertyValue
     */
    public void setPropertyValue(String propertyValue);

    /**
     * Setter for the Map that holds values for the other key fields (if any)
     * 
     * @param otherKeyFieldValues Map with field name as the Map key and field value as Map value
     */
    public void setOtherKeyFieldValueMap(Map<String, Object> otherKeyFieldValues);

    /**
     * Setter for the person who the permission is being evaluated for
     * 
     * @param person Person kim business object
     */
    public void setPerson(Person person);

}
