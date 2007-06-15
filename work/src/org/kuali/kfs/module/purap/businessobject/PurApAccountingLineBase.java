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
package org.kuali.module.purap.bo;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.SourceAccountingLine;

public abstract class PurApAccountingLineBase extends SourceAccountingLine implements PurApAccountingLine {

    protected Integer accountIdentifier;
    private Integer itemIdentifier;
    private BigDecimal accountLinePercent;

    public Integer getAccountIdentifier() {
        return accountIdentifier;
    }

    public void setAccountIdentifier(Integer requisitionAccountIdentifier) {
        this.accountIdentifier = requisitionAccountIdentifier;
    }

    public Integer getItemIdentifier() {
        return itemIdentifier;
    }

    public void setItemIdentifier(Integer requisitionItemIdentifier) {
        this.itemIdentifier = requisitionItemIdentifier;
    }

    public BigDecimal getAccountLinePercent() {
        return accountLinePercent;
    }

    public void setAccountLinePercent(BigDecimal accountLinePercent) {
        this.accountLinePercent = accountLinePercent;
    }

    public boolean isEmpty() {
        return !(StringUtils.isNotEmpty(getAccountNumber()) || 
                 StringUtils.isNotEmpty(getChartOfAccountsCode()) || 
                 StringUtils.isNotEmpty(getFinancialObjectCode()) || 
                 StringUtils.isNotEmpty(getFinancialSubObjectCode()) || 
                 StringUtils.isNotEmpty(getOrganizationReferenceId()) || 
                 StringUtils.isNotEmpty(getProjectCode()) || 
                 StringUtils.isNotEmpty(getSubAccountNumber()) ||
                 ObjectUtils.isNotNull(getAccountLinePercent()));
    }

    public PurApAccountingLine createBlankAmountsCopy() {
        // TODO PURAP - Finish Me
        return null;
    }

    // TODO PURAP - need more fields for comparison or not? - look at org.kuali.kfs.bo.AccountingLineBase#getValuesMap()
    public boolean accountStringsAreEqual(PurApAccountingLine accountingLine) {
        if (accountingLine == null) { return false;}
        return new EqualsBuilder()
          .append(getChartOfAccountsCode(), accountingLine.getChartOfAccountsCode())
          .append(getAccountNumber(),accountingLine.getAccountNumber())
          .append(getSubAccountNumber(),accountingLine.getSubAccountNumber())
          .append(getFinancialObjectCode(),accountingLine.getFinancialObjectCode())
          .append(getFinancialSubObjectCode(),accountingLine.getFinancialSubObjectCode())
          .append(getProjectCode(),accountingLine.getProjectCode())
          .append(getOrganizationReferenceId(),accountingLine.getOrganizationReferenceId())
//          .append(getReferenceOriginCode(),accountingLine.getReferenceOriginCode())
//          .append(getReferenceNumber(),accountingLine.getReferenceNumber())
//          .append(getReferenceTypeCode(),accountingLine.getReferenceTypeCode())
          .isEquals();
    }

    // TODO PURAP - this method needs to copy any account field we need to display 
    //              and its fields should probably match method 'accountStringsAreEqual' above
    public SourceAccountingLine generateSourceAccountingLine() {
        SourceAccountingLine sourceLine = new SourceAccountingLine();
        sourceLine.setChartOfAccountsCode(getChartOfAccountsCode());
        sourceLine.setAccountNumber(getAccountNumber());
        sourceLine.setSubAccountNumber(getSubAccountNumber());
        sourceLine.setFinancialObjectCode(getFinancialObjectCode());
        sourceLine.setFinancialSubObjectCode(getFinancialSubObjectCode());
        sourceLine.setProjectCode(getProjectCode());
        sourceLine.setOrganizationReferenceId(getOrganizationReferenceId());
        sourceLine.setAmount(getAmount());
        return sourceLine;
    }
    
    /**
     * 
     * @see org.kuali.kfs.bo.AccountingLineBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("chart", getChartOfAccountsCode());
        m.put("account", getAccountNumber());
        m.put("objectCode", getFinancialObjectCode());
        m.put("subAccount", getSubAccountNumber());
        m.put("subObjectCode", getFinancialSubObjectCode());
        m.put("projectCode", getProjectCode());
        m.put("orgRefId", getOrganizationReferenceId());

        return m;
    }
}
