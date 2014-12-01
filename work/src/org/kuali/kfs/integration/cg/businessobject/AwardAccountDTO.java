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

import org.kuali.kfs.integration.cg.ContractsAndGrantsAwardAccount;

/**
 * <p>Java class for awardAccountDTO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="awardAccountDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="awardId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="awardTitle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="errorMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="federalSponsor" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="grantNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="institutionalproposalId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="primeSponsorCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="primeSponsorName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="projectDirector" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="proposalFederalPassThroughAgencyNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="proposalNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sponsorCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sponsorName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "awardAccountDTO", propOrder = {
    "awardId",
    "awardTitle",
    "errorMessage",
    "federalSponsor",
    "grantNumber",
    "institutionalproposalId",
    "primeSponsorCode",
    "primeSponsorName",
    "projectDirector",
    "proposalFederalPassThroughAgencyNumber",
    "proposalNumber",
    "sponsorCode",
    "sponsorName"
})

public class AwardAccountDTO implements ContractsAndGrantsAwardAccount, Serializable {

    private long awardId;
    private String awardTitle;
    private String errorMessage;
    private boolean federalSponsor;
    private String grantNumber;
    private long institutionalproposalId;
    private String primeSponsorCode;
    private String primeSponsorName;
    private String projectDirector;
    private String proposalFederalPassThroughAgencyNumber;
    private String proposalNumber;
    private String sponsorCode;
    private String sponsorName;
        
    public long getAwardId() {
        return awardId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean getFederalSponsor() {
        return federalSponsor;
    }

    public String getGrantNumber() {
        return grantNumber;
    }

    public long getInstitutionalproposalId() {
        return institutionalproposalId;
    }

    public String getProjectDirector() {
        return projectDirector;
    }

    public String getProposalFederalPassThroughAgencyNumber() {
        return proposalFederalPassThroughAgencyNumber;
    }

    public String getProposalNumber() {
        return proposalNumber;
    }

    public String getSponsorCode() {
        return sponsorCode;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public void refresh() {
    }

    public void prepareForWorkflow() {
    }

    /**
     * 
     */
    public String getAwardTitle() {
        return awardTitle;
    }

    /**
     * 
     */
    public void setAwardTitle(String awardTitle) {
        this.awardTitle = awardTitle;
    }

    /**
     * 
     */
    public String getPrimeSponsorCode() {
        return primeSponsorCode;
    }

    /**
     * 
     */
    public void setPrimeSponsorCode(String primeSponsorCode) {
        this.primeSponsorCode = primeSponsorCode;
    }

    /**
     * 
     */
    public String getPrimeSponsorName() {
        return primeSponsorName;
    }

    /**
     * 
     */
    public void setPrimeSponsorName(String primeSponsorName) {
        this.primeSponsorName = primeSponsorName;
    }

}
