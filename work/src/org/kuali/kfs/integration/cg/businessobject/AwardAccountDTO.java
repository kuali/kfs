/*
 * Copyright 2011 The Kuali Foundation.
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

    @Override
    public long getAwardId() {
        return awardId;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean getFederalSponsor() {
        return federalSponsor;
    }

    @Override
    public String getGrantNumber() {
        return grantNumber;
    }

    @Override
    public long getInstitutionalproposalId() {
        return institutionalproposalId;
    }

    @Override
    public String getProjectDirector() {
        return projectDirector;
    }

    @Override
    public String getProposalFederalPassThroughAgencyNumber() {
        return proposalFederalPassThroughAgencyNumber;
    }

    @Override
    public String getProposalNumber() {
        return proposalNumber;
    }

    @Override
    public String getSponsorCode() {
        return sponsorCode;
    }

    @Override
    public String getSponsorName() {
        return sponsorName;
    }

    @Override
    public void refresh() {
    }

    /**
     *
     */
    @Override
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
    @Override
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
    @Override
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
