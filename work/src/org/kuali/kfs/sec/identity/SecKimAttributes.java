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
package org.kuali.kfs.sec.identity;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.identity.KfsKimAttributes;

public class SecKimAttributes extends KfsKimAttributes {
    public static final String CONSTRAINT_CODE = "constraintCode";
    public static final String OPERATOR = "operator";
    public static final String PROPERTY_VALUE = "propertyValue";
    public static final String OVERRIDE_DENY = "overrideDeny";

    protected String constraintCode;
    protected String operator;
    protected String propertyValue;
    protected String overrideDeny;

    /**
     * Gets the constraintCode attribute.
     * 
     * @return Returns the constraintCode.
     */
    public String getConstraintCode() {
        return constraintCode;
    }


    /**
     * Sets the constraintCode attribute value.
     * 
     * @param constraintCode The constraintCode to set.
     */
    public void setConstraintCode(String constraintCode) {
        this.constraintCode = constraintCode;
    }


    /**
     * Gets the operator attribute.
     * 
     * @return Returns the operator.
     */
    public String getOperator() {
        return operator;
    }


    /**
     * Sets the operator attribute value.
     * 
     * @param operator The operator to set.
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }


    /**
     * Gets the propertyValue attribute.
     * 
     * @return Returns the propertyValue.
     */
    public String getPropertyValue() {
        return propertyValue;
    }


    /**
     * Sets the propertyValue attribute value.
     * 
     * @param propertyValue The propertyValue to set.
     */
    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }


    /**
     * Gets the overrideDeny attribute.
     * 
     * @return Returns the overrideDeny.
     */
    public String getOverrideDeny() {
        return overrideDeny;
    }


    /**
     * Sets the overrideDeny attribute value.
     * 
     * @param overrideDeny The overrideDeny to set.
     */
    public void setOverrideDeny(String overrideDeny) {
        this.overrideDeny = overrideDeny;
    }


    @SuppressWarnings("unchecked")
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        return m;
    }
}
