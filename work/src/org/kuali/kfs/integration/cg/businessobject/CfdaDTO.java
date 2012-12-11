/*
 * Copyright 2007-2009 The Kuali Foundation
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

package org.kuali.kfs.integration.cg.businessobject;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.kuali.kfs.integration.cg.ContractsAndGrantsCfda;
import org.kuali.rice.krad.bo.BusinessObjectBase;


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
public class CfdaDTO extends BusinessObjectBase implements ContractsAndGrantsCfda, Serializable {

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
    @Override
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
    @Override
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
    @Override
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
