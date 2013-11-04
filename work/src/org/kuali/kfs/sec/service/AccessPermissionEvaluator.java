/*
 * Copyright 2009 The Kuali Foundation.
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
