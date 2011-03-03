
package org.kuali.kfs.module.external.kc.businessobject;

import java.io.Serializable;
import java.util.LinkedHashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBudgetCategory;
import org.kuali.rice.kns.bo.BusinessObjectBase;


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

    public void prepareForWorkflow() {}
    
    public void refresh() {}

    protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String,String> m = new LinkedHashMap<String,String>();
        m.put("BudgetCategoryDTO", this.budgetCategoryCode);
        return m;
    }

}
