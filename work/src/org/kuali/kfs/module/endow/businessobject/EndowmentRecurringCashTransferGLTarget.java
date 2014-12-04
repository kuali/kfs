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
