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
