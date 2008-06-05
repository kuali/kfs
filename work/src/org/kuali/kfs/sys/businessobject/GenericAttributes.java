/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.bo;

import java.sql.Date;

import org.kuali.core.bo.AttributeReferenceDummy;

public class GenericAttributes extends AttributeReferenceDummy {

    private String moduleCode;
    private String searchType;
    private String displayType;
    private String documentTotalAmount;
    private String routingAttributeTitle;

    private Integer transactionEntrySequenceId;
    private String universityFiscalAccountingPeriod;
    private Integer genericFiscalYear;
    private Date createDate;
    private String initiatorNetworkId;
    private String maxDollarAmount;
    private String minDollarAmount;
    private String totalDollarAmount;

    public GenericAttributes() {
        super();
    }

    public String getDocumentTotalAmount() {
        return documentTotalAmount;
    }

    public void setDocumentTotalAmount(String documentTotalAmount) {
        this.documentTotalAmount = documentTotalAmount;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String changedModuleCodes) {
        this.moduleCode = changedModuleCodes;
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    public String getRoutingAttributeTitle() {
        return routingAttributeTitle;
    }

    public void setRoutingAttributeTitle(String routingAttributeTitle) {
        this.routingAttributeTitle = routingAttributeTitle;
    }

    public Integer getTransactionEntrySequenceId() {
        return transactionEntrySequenceId;
    }

    public void setTransactionEntrySequenceId(Integer transactionEntrySequenceId) {
        this.transactionEntrySequenceId = transactionEntrySequenceId;
    }

    public String getUniversityFiscalAccountingPeriod() {
        return universityFiscalAccountingPeriod;
    }

    public void setUniversityFiscalAccountingPeriod(String universityFiscalAccountingPeriod) {
        this.universityFiscalAccountingPeriod = universityFiscalAccountingPeriod;
    }

    public Integer getGenericFiscalYear() {
        return genericFiscalYear;
    }

    public void setGenericFiscalYear(Integer genericFiscalYear) {
        this.genericFiscalYear = genericFiscalYear;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getInitiatorNetworkId() {
        return initiatorNetworkId;
    }

    public void setInitiatorNetworkId(String initiatorNetworkId) {
        this.initiatorNetworkId = initiatorNetworkId;
    }

    public String getMaxDollarAmount() {
        return maxDollarAmount;
    }

    public void setMaxDollarAmount(String maxDollarAmount) {
        this.maxDollarAmount = maxDollarAmount;
    }

    public String getMinDollarAmount() {
        return minDollarAmount;
    }

    public void setMinDollarAmount(String minDollarAmount) {
        this.minDollarAmount = minDollarAmount;
    }

    public String getTotalDollarAmount() {
        return totalDollarAmount;
    }

    public void setTotalDollarAmount(String totalDollarAmount) {
        this.totalDollarAmount = totalDollarAmount;
    }

}
