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
