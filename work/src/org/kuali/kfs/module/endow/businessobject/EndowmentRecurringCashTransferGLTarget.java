/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCodeCurrent;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCodeCurrent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class EndowmentRecurringCashTransferGLTarget extends PersistableBusinessObjectBase {

    private String transferNumber;
    private String targetSequenceNumber;
    private String sourceKemid;
    private String targetChartOfAccountsCode;
    private String targetAccountsNumber;
    private String targetFinancialObjectCode;
    private KualiDecimal targetFdocLineAmount;
    private String targetSubAccountNumber;
    private String targetFinancialSubObjectCode;
    private String targetProjectCode;
    private String targetOrgReferenceId; 
    private KualiDecimal targetPercent;
    private String targetUseEtranCode;
    private boolean active;
    
    private EndowmentRecurringCashTransfer endowmentRecurringCashTransfer;
    private Chart chart;
    private Account account;
    private ObjectCodeCurrent objectCode;
    private SubAccount subAccount;
    private SubObjectCodeCurrent subObjectCode;
    private ProjectCode projectCode;
    private EndowmentTransactionCode etranCodeObj;
    
    /**
     * Default constructor.
     */
    public EndowmentRecurringCashTransferGLTarget() {
        //setTargetSequenceNumber(Integer.toString(getEndowmentRecurringCashTransfer().getGlTarget().size() + 1));
    }
    
    public EndowmentRecurringCashTransfer getEndowmentRecurringCashTransfer() {
        return endowmentRecurringCashTransfer;
    }

    public void setEndowmentRecurringCashTransfer(EndowmentRecurringCashTransfer endowmentRecurringCashTransfer) {
        this.endowmentRecurringCashTransfer = endowmentRecurringCashTransfer;
    }

    public String getTransferNumber() {
        return transferNumber;
    }

    public void setTransferNumber(String transferNumber) {
        this.transferNumber = transferNumber;
    }

    public String getTargetSequenceNumber() {
        return targetSequenceNumber;
    }

    public void setTargetSequenceNumber(String targetSequenceNumber) {
        this.targetSequenceNumber = targetSequenceNumber;
    }

    public String getTargetChartOfAccountsCode() {
        return targetChartOfAccountsCode;
    }

    public void setTargetChartOfAccountsCode(String targetChartOfAccountsCode) {
        this.targetChartOfAccountsCode = targetChartOfAccountsCode;
    }

    public String getTargetAccountsNumber() {
        return targetAccountsNumber;
    }

    public void setTargetAccountsNumber(String targetAccountsNumber) {
        this.targetAccountsNumber = targetAccountsNumber;
    }

    public String getTargetFinancialObjectCode() {
        return targetFinancialObjectCode;
    }

    public void setTargetFinancialObjectCode(String targetFinancialObjectCode) {
        this.targetFinancialObjectCode = targetFinancialObjectCode;
    }

    public KualiDecimal getTargetFdocLineAmount() {
        return targetFdocLineAmount;
    }

    public void setTargetFdocLineAmount(KualiDecimal targetFdocLineAmount) {
        this.targetFdocLineAmount = targetFdocLineAmount;
    }

    public String getTargetSubAccountNumber() {
        return targetSubAccountNumber;
    }

    public void setTargetSubAccountNumber(String targetSubAccountNumber) {
        this.targetSubAccountNumber = targetSubAccountNumber;
    }

    public String getTargetFinancialSubObjectCode() {
        return targetFinancialSubObjectCode;
    }

    public void setTargetFinancialSubObjectCode(String targetFinancialSubObjectCode) {
        this.targetFinancialSubObjectCode = targetFinancialSubObjectCode;
    }

    public String getTargetProjectCode() {
        return targetProjectCode;
    }

    public void setTargetProjectCode(String targetProjectCode) {
        this.targetProjectCode = targetProjectCode;
    }

    public KualiDecimal getTargetPercent() {
        return targetPercent;
    }

    public void setTargetPercent(KualiDecimal targetPercent) {
        this.targetPercent = targetPercent;
    }

    public String getTargetUseEtranCode() {
        return targetUseEtranCode;
    }

    public void setTargetUseEtranCode(String targetUseEtranCode) {
        this.targetUseEtranCode = targetUseEtranCode;
    }

    public Chart getChart() {
        return chart;
    }

    public void setChart(Chart chart) {
        this.chart = chart;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public ObjectCodeCurrent getObjectCode() {
        return objectCode;
    }

    public void setObjectCode(ObjectCodeCurrent objectCode) {
        this.objectCode = objectCode;
    }

    public SubAccount getSubAccount() {
        return subAccount;
    }

    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    public SubObjectCodeCurrent getSubObjectCode() {
        return subObjectCode;
    }

    public void setSubObjectCode(SubObjectCodeCurrent subObjectCode) {
        this.subObjectCode = subObjectCode;
    }

    public ProjectCode getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(ProjectCode projectCode) {
        this.projectCode = projectCode;
    }

    public EndowmentTransactionCode getEtranCodeObj() {
        return etranCodeObj;
    }

    public void setEtranCodeObj(EndowmentTransactionCode etranCodeObj) {
        this.etranCodeObj = etranCodeObj;
    }

    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getSourceKemid() {
        return sourceKemid;
    }

    public void setSourceKemid(String sourceKemid) {
        this.sourceKemid = sourceKemid;
    }
    
    public String getTargetOrgReferenceId() {
        return targetOrgReferenceId;
    }

    public void setTargetOrgReferenceId(String targetOrgReferenceId) {
        this.targetOrgReferenceId = targetOrgReferenceId;
    }
}
