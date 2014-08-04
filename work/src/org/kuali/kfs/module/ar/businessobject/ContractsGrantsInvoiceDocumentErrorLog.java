/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.ar.businessobject;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.integration.cg.ContractsAndGrantsAward;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Defines a Contracts & Grants Billing Invoice Document Error Log entry.
 */
public class ContractsGrantsInvoiceDocumentErrorLog extends PersistableBusinessObjectBase {

    private Long proposalNumber;
    private Long errorLogIdentifier;
    private String accounts;
    private Date awardBeginningDate;
    private Date awardEndingDate;
    private BigDecimal awardTotalAmount;
    private BigDecimal cumulativeExpensesAmount;
    private Timestamp errorDate;
    private String creationProcessTypeCode;
    private String batchForReport;
    private String primaryFundManagerPrincipalId;
    private String primaryFundManagerName;

    private List<ContractsGrantsInvoiceDocumentErrorMessage> errorMessages;
    private ContractsAndGrantsAward award;
    private Person awardPrimaryFundManager;

    public ContractsGrantsInvoiceDocumentErrorLog() {
        errorMessages = new ArrayList<ContractsGrantsInvoiceDocumentErrorMessage>();
    }

    public Long getProposalNumber() {
        return proposalNumber;
    }

    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    public Long getErrorLogIdentifier() {
        return errorLogIdentifier;
    }

    public void setErrorLogIdentifier(Long errorLogIdentifier) {
        this.errorLogIdentifier = errorLogIdentifier;
    }

    public String getAccounts() {
        return accounts;
    }

    public void setAccounts(String accounts) {
        this.accounts = accounts;
    }

    public Date getAwardBeginningDate() {
        return awardBeginningDate;
    }

    public void setAwardBeginningDate(Date awardBeginningDate) {
        this.awardBeginningDate = awardBeginningDate;
    }

    public Date getAwardEndingDate() {
        return awardEndingDate;
    }

    public void setAwardEndingDate(Date awardEndingDate) {
        this.awardEndingDate = awardEndingDate;
    }

    public BigDecimal getAwardTotalAmount() {
        return awardTotalAmount;
    }

    public void setAwardTotalAmount(BigDecimal awardTotalAmount) {
        this.awardTotalAmount = awardTotalAmount;
    }

    public BigDecimal getCumulativeExpensesAmount() {
        return cumulativeExpensesAmount;
    }

    public void setCumulativeExpensesAmount(BigDecimal cumulativeExpensesAmount) {
        this.cumulativeExpensesAmount = cumulativeExpensesAmount;
    }

    public Timestamp getErrorDate() {
        return errorDate;
    }

    public void setErrorDate(Timestamp errorDate) {
        this.errorDate = errorDate;
    }

    public String getPrimaryFundManagerPrincipalName() {
        return getAwardPrimaryFundManager().getPrincipalName();
    }

    public String getPrimaryFundManagerPrincipalId() {
        return primaryFundManagerPrincipalId;
    }

    public void setPrimaryFundManagerPrincipalId(String primaryFundManagerPrincipalId) {
        this.primaryFundManagerPrincipalId = primaryFundManagerPrincipalId;
    }

    public String getPrimaryFundManagerName() {
        return primaryFundManagerName;
    }

    public void setPrimaryFundManagerName(String primaryFundManagerName) {
        this.primaryFundManagerName = primaryFundManagerName;
    }

    public String getCreationProcessTypeCode() {
        return creationProcessTypeCode;
    }

    public void setCreationProcessTypeCode(String creationProcessTypeCode) {
        this.creationProcessTypeCode = creationProcessTypeCode;
    }

    public String getCreationProcessTypeName() {
        return ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.getName(creationProcessTypeCode);
    }

    public List<ContractsGrantsInvoiceDocumentErrorMessage> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<ContractsGrantsInvoiceDocumentErrorMessage> errorMesssages) {
        this.errorMessages = errorMesssages;
    }

    public ContractsAndGrantsAward getAward() {
        return award;
    }

    public void setAward(ContractsAndGrantsAward award) {
        this.award = award;
    }

    public Person getAwardPrimaryFundManager() {
        awardPrimaryFundManager = SpringContext.getBean(PersonService.class).updatePersonIfNecessary(primaryFundManagerPrincipalId, awardPrimaryFundManager);
        return awardPrimaryFundManager;
    }

    public void setAwardPrimaryFundManager(Person awardPrimaryFundManager) {
        this.awardPrimaryFundManager = awardPrimaryFundManager;
    }

}