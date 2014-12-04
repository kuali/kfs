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

package org.kuali.kfs.module.external.kc.businessobject;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.kuali.kfs.integration.cg.ContractsAndGrantsCfda;


/**
 * <p>Java class for cfdaDTO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cfdaDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="active" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="awardId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cfdaMaintenanceTypeId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cfdaNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cfdaProgramTitleName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cfdaDTO", propOrder = {
    "active",
    "awardId",
    "cfdaMaintenanceTypeId",
    "cfdaNumber",
    "cfdaProgramTitleName"
})
public class CfdaDTO implements ContractsAndGrantsCfda, Serializable {

    protected boolean active;
    protected String awardId;
    protected String cfdaMaintenanceTypeId;
    protected String cfdaNumber;
    protected String cfdaProgramTitleName;

    /**
     * Gets the value of the active property.
     * 
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the value of the active property.
     * 
     */
    public void setActive(boolean value) {
        this.active = value;
    }

    /**
     * Gets the value of the awardId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAwardId() {
        return awardId;
    }

    /**
     * Sets the value of the awardId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAwardId(String value) {
        this.awardId = value;
    }

    /**
     * Gets the value of the cfdaMaintenanceTypeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCfdaMaintenanceTypeId() {
        return cfdaMaintenanceTypeId;
    }

    /**
     * Sets the value of the cfdaMaintenanceTypeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCfdaMaintenanceTypeId(String value) {
        this.cfdaMaintenanceTypeId = value;
    }

    /**
     * Gets the value of the cfdaNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCfdaNumber() {
        return cfdaNumber;
    }

    /**
     * Sets the value of the cfdaNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCfdaNumber(String value) {
        this.cfdaNumber = value;
    }

    /**
     * Gets the value of the cfdaProgramTitleName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCfdaProgramTitleName() {
        return cfdaProgramTitleName;
    }

    /**
     * Sets the value of the cfdaProgramTitleName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCfdaProgramTitleName(String value) {
        this.cfdaProgramTitleName = value;
    }

    @Override
    public void refresh() {}

}
