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
package org.kuali.kfs.module.ar.batch.report;

import org.apache.commons.lang.StringUtils;

public class CustomerLoadBatchError {

    private String customerName;
    private String propertyName;
    private Class<?> propertyClass;
    private String value;
    private String description;
    
    /**
     * 
     * Default constructor with all internal fields defaulting to empty-string or null.
     */
    public CustomerLoadBatchError() {
        this.customerName = "";
        this.propertyName = "";
        this.propertyClass = null;
        this.value = "";
        this.description = "";
    }
    
    /**
     * 
     * Constructor to initialize with just the customerName.
     * @param customerName The customer name of the document being imported.
     */
    public CustomerLoadBatchError(String customerName) {
        this.customerName = customerName;
    }
    
    /**
     * 
     * Constructs a CustomerLoadBatchError.java.
     * @param customerName The customer name of the document being imported.
     * @param propertyName The name of the property on the Customer BO.
     * @param propertyClass The class of the property named by propertyName.
     * @param value The original value of the field from the batch import document.
     * @param description The description of the error that occurred.
     */
    public CustomerLoadBatchError(String customerName, String propertyName, Class<?> propertyClass, String value, String description) {
        this.customerName = customerName;
        this.propertyName = propertyName;
        this.propertyClass = propertyClass;
        this.value = value;
        this.description = description;
    }

    public String toString() {
        return "[" + customerName + "] " + 
                ("class java.lang.Object".equals(propertyClass.toString()) || propertyClass == null ? "" : "(" + propertyClass.toString() + ") ") + 
                (StringUtils.isBlank(propertyName) ? "" : getPropertyNameLastElement() + ": ") + 
                (StringUtils.isBlank(value) || "N/A".equalsIgnoreCase(value) ? "" : "'" + value + "' - ") +  
                description;
    }
    
    public String getPropertyNameLastElement() {
        if (StringUtils.isBlank(propertyName)) return propertyName;
        
        String[] propertyNameElements = propertyName.split("\\.");
        if (propertyNameElements.length <= 0) {
            return propertyName;
        }
        else {
            return propertyNameElements[propertyNameElements.length - 1];
        }
        
    }
    
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Class<?> getPropertyClass() {
        return propertyClass;
    }

    public void setPropertyClass(Class<?> propertyClass) {
        this.propertyClass = propertyClass;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}
