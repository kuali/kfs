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

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class EndowmentRecurringCashTransferKEMIDTarget extends PersistableBusinessObjectBase {

    private String transferNumber;
    private String targetSequenceNumber;
    private String sourceKemid;
    private String targetKemid;
    private String targetEtranCode;
    private String targetLineDescription;
    private String targetIncomeOrPrincipal;
    private KualiDecimal targetAmount;
    private KualiDecimal targetPercent;
    private String targetUseEtranCode;
    private boolean active;
    
    private EndowmentRecurringCashTransfer endowmentRecurringCashTransfer;
    private KEMID targetKemidObj;
    private EndowmentTransactionCode targetEtranCodeObj;
    private IncomePrincipalIndicator incomePrincipalIndicator;
    private EndowmentTransactionCode targetUseEtranCodeObj;
    
    /**
     * Default constructor.
     */
    public EndowmentRecurringCashTransferKEMIDTarget() {
        //setTargetSequenceNumber(Integer.toString(getEndowmentRecurringCashTransfer().getKemidTarget().size() + 1));
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

    public String getTargetKemid() {
        return targetKemid;
    }

    public void setTargetKemid(String targetKemid) {
        this.targetKemid = targetKemid;
    }

    public String getTargetEtranCode() {
        return targetEtranCode;
    }

    public void setTargetEtranCode(String targetEtranCode) {
        this.targetEtranCode = targetEtranCode;
    }

    public String getTargetLineDescription() {
        return targetLineDescription;
    }

    public void setTargetLineDescription(String targetLineDescription) {
        this.targetLineDescription = targetLineDescription;
    }

    public String getTargetIncomeOrPrincipal() {
        return targetIncomeOrPrincipal;
    }

    public void setTargetIncomeOrPrincipal(String targetIncomeOrPrincipal) {
        this.targetIncomeOrPrincipal = targetIncomeOrPrincipal;
    }

    public KualiDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(KualiDecimal targetAmount) {
        this.targetAmount = targetAmount;
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

    public KEMID getTargetKemidObj() {
        return targetKemidObj;
    }

    public void setTargetKemidObj(KEMID targetKemidObj) {
        this.targetKemidObj = targetKemidObj;
    }

    public EndowmentTransactionCode getTargetEtranCodeObj() {
        return targetEtranCodeObj;
    }

    public void setTargetEtranCodeObj(EndowmentTransactionCode targetEtranCodeObj) {
        this.targetEtranCodeObj = targetEtranCodeObj;
    }

    public IncomePrincipalIndicator getIncomePrincipalIndicator() {
        return incomePrincipalIndicator;
    }

    public void setIncomePrincipalIndicator(IncomePrincipalIndicator incomePrincipalIndicator) {
        this.incomePrincipalIndicator = incomePrincipalIndicator;
    }

    public EndowmentTransactionCode getTargetUseEtranCodeObj() {
        return targetUseEtranCodeObj;
    }

    public void setTargetUseEtranCodeObj(EndowmentTransactionCode targetUseEtranCodeObj) {
        this.targetUseEtranCodeObj = targetUseEtranCodeObj;
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
}
