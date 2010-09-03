/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;

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
    private KEMID kemidObj;
    private EndowmentTransactionCode etranCodeObj;
    private IncomePrincipalIndicator incomePrincipalIndicator;
    private EndowmentTransactionCode useEtranCodeObj;
    
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

    public KEMID getKemidObj() {
        return kemidObj;
    }

    public void setKemidObj(KEMID kemidObj) {
        this.kemidObj = kemidObj;
    }

    public EndowmentTransactionCode getEtranCodeObj() {
        return etranCodeObj;
    }

    public void setEtranCodeObj(EndowmentTransactionCode etranCodeObj) {
        this.etranCodeObj = etranCodeObj;
    }

    public IncomePrincipalIndicator getIncomePrincipalIndicator() {
        return incomePrincipalIndicator;
    }

    public void setIncomePrincipalIndicator(IncomePrincipalIndicator incomePrincipalIndicator) {
        this.incomePrincipalIndicator = incomePrincipalIndicator;
    }

    public EndowmentTransactionCode getUseEtranCodeObj() {
        return useEtranCodeObj;
    }

    public void setUseEtranCodeObj(EndowmentTransactionCode useEtranCodeObj) {
        this.useEtranCodeObj = useEtranCodeObj;
    }

    @Override
    protected LinkedHashMap toStringMapper() {
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
