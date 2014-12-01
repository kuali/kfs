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
package org.kuali.kfs.integration.cg.businessobject;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBudgetCategory;
import org.kuali.rice.krad.bo.BusinessObjectBase;


/**
 * <p>Java class for budgetCategoryDTO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="budgetCategoryDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="authorPersonName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="budgetCategoryCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="budgetCategoryTypeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="budgetCategoryTypeDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "budgetCategoryDTO", propOrder = {
    "authorPersonName",
    "budgetCategoryCode",
    "budgetCategoryTypeCode",
    "budgetCategoryTypeDescription",
    "description"
})
public class BudgetCategoryDTO extends BusinessObjectBase implements ContractsAndGrantsBudgetCategory, Serializable {

    protected String authorPersonName;
    protected String budgetCategoryCode;
    protected String budgetCategoryTypeCode;
    protected String budgetCategoryTypeDescription;
    protected String description;

    /**
     * Gets the value of the authorPersonName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthorPersonName() {
        return authorPersonName;
    }

    /**
     * Sets the value of the authorPersonName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthorPersonName(String value) {
        this.authorPersonName = value;
    }

    /**
     * Gets the value of the budgetCategoryCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBudgetCategoryCode() {
        return budgetCategoryCode;
    }

    /**
     * Sets the value of the budgetCategoryCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBudgetCategoryCode(String value) {
        this.budgetCategoryCode = value;
    }

    /**
     * Gets the value of the budgetCategoryTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBudgetCategoryTypeCode() {
        return budgetCategoryTypeCode;
    }

    /**
     * Sets the value of the budgetCategoryTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBudgetCategoryTypeCode(String value) {
        this.budgetCategoryTypeCode = value;
    }

    /**
     * Gets the value of the budgetCategoryTypeDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBudgetCategoryTypeDescription() {
        return budgetCategoryTypeDescription;
    }

    /**
     * Sets the value of the budgetCategoryTypeDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBudgetCategoryTypeDescription(String value) {
        this.budgetCategoryTypeDescription = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    public void refresh() {}

    
    
        
        
        
    

}
