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

package org.kuali.kfs.fp.businessobject;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

/**
 * This transient business object represents the Capital Accounting Lines
 * business object that is used by the FP documents.
 */
public class CapitalAccountingLines extends TransientBusinessObjectBase {
    protected String documentNumber;
    protected Integer sequenceNumber; // relative to the grouping of accounting lines
    protected String lineType; //tells where source or target line
    protected String chartOfAccountsCode;
    protected String accountNumber;
    protected String financialObjectCode;
    protected String subAccountNumber;
    protected String financialSubObjectCode;
    protected String projectCode;
    protected String organizationReferenceId;
    protected String financialDocumentLineDescription;
    protected KualiDecimal amount;
    protected boolean selectLine;
    protected String distributionAmountCode;
    protected boolean amountDistributed;

    //need to show the percentage of the accounts.
    protected KualiDecimal accountLinePercent;

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getLineType() {
        return lineType;
    }

    public void setLineType(String lineType) {
        this.lineType = lineType;
    }


    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }


    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    public String getAccountNumber() {
        return accountNumber;
    }


    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    public String getFinancialObjectCode() {
        return financialObjectCode;
    }


    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }


    public String getSubAccountNumber() {
        return subAccountNumber;
    }


    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }


    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }


    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }


    public String getProjectCode() {
        return projectCode;
    }


    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getOrganizationReferenceId() {
        return organizationReferenceId;
    }

    public void setOrganizationReferenceId(String organizationReferenceId) {
        this.organizationReferenceId = organizationReferenceId;
    }


    public String getFinancialDocumentLineDescription() {
        return financialDocumentLineDescription;
    }


    public void setFinancialDocumentLineDescription(String financialDocumentLineDescription) {
        this.financialDocumentLineDescription = financialDocumentLineDescription;
    }

    public KualiDecimal getAmount() {
        return amount;
    }

    public void setAmount(KualiDecimal amount) {
        this.amount = amount;
    }

    public boolean isSelectLine() {
        return selectLine;
    }

    public void setSelectLine(boolean selectLine) {
        this.selectLine = selectLine;
    }

    public String getDistributionAmountCode() {
        return distributionAmountCode;
    }

    public void setDistributionAmountCode(String distributionAmountCode) {
        this.distributionAmountCode = distributionAmountCode;
    }

    public boolean isAmountDistributed() {
        return amountDistributed;
    }

    public void setAmountDistributed(boolean amountDistributed) {
        this.amountDistributed = amountDistributed;
    }

    public KualiDecimal getAccountLinePercent() {
        return accountLinePercent;
    }

    public void setAccountLinePercent(KualiDecimal accountLinePercent) {
        this.accountLinePercent = accountLinePercent;
    }
}
