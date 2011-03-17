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

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "awardAccountDTO", propOrder = {
    "awardId",
    "errorMessage",
    "federalSponsor",
    "grantNumber",
    "institutionalproposalId",
    "projectDirector",
    "proposalFederalPassThroughAgencyNumber",
    "proposalNumber",
    "sponsorCode",
    "sponsorName"
})
public class AwardAccountDTO implements ContractsAndGrantsAwardAccount, Serializable {

    private long awardId;
    private String errorMessage;
    private boolean federalSponsor;
    private String grantNumber;
    private long institutionalproposalId;
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

}
