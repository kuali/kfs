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

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class EndowmentRecurringCashTransfer extends PersistableBusinessObjectBase implements MutableInactivatable {
    
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EndowmentRecurringCashTransfer.class);
    
    private String transferNumber;
    private String sourceKemid;
    private String transactionType;
    private String sourceEtranCode;
    private String sourceLineDescription;
    private String sourceIncomeOrPrincipal;
    private String frequencyCode;
    private Date nextProcessDate;
    private Date lastProcessDate;
    private boolean active;
    private Integer targetKemidNextSeqNumber;
    private Integer targetGlNextSeqNumber;
    
    private KEMID kemidObj;
    private EndowmentTransactionCode etranCodeObj;
    private IncomePrincipalIndicator incomePrincipalIndicator;
    private FrequencyCode frequencyCodeObj;
    
    private List<EndowmentRecurringCashTransferKEMIDTarget> kemidTarget;
    private List<EndowmentRecurringCashTransferGLTarget> glTarget;
    
    /**
     * Default constructor.
     */
    public EndowmentRecurringCashTransfer() {
        super();
        kemidTarget = new ArrayList<EndowmentRecurringCashTransferKEMIDTarget>();
        glTarget = new ArrayList<EndowmentRecurringCashTransferGLTarget>();
        
//      setTransferNumber(NextTransferNumberFinder.getLongValue().toString());
    }
    
    public String getTransferNumber() {
        return transferNumber;
    }

    public void setTransferNumber(String transferNumber) {
        this.transferNumber = transferNumber;
    }

    public String getSourceKemid() {
        return sourceKemid;
    }

    public void setSourceKemid(String sourceKemid) {
        this.sourceKemid = sourceKemid;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getSourceEtranCode() {
        return sourceEtranCode;
    }

    public void setSourceEtranCode(String sourceEtranCode) {
        this.sourceEtranCode = sourceEtranCode;
    }

    public String getSourceLineDescription() {
        return sourceLineDescription;
    }

    public void setSourceLineDescription(String sourceLineDescription) {
        this.sourceLineDescription = sourceLineDescription;
    }

    public String getSourceIncomeOrPrincipal() {
        return sourceIncomeOrPrincipal;
    }

    public void setSourceIncomeOrPrincipal(String sourceIncomeOrPrincipal) {
        this.sourceIncomeOrPrincipal = sourceIncomeOrPrincipal;
    }

    public String getFrequencyCode() {
        return frequencyCode;
    }

    public void setFrequencyCode(String frequencyCode) {
        this.frequencyCode = frequencyCode;
    }

    public Date getNextProcessDate() {
        return nextProcessDate;
    }

    public void setNextProcessDate(Date nextProcessDate) {
        this.nextProcessDate = nextProcessDate;
    }

    public Date getLastProcessDate() {
        return lastProcessDate;
    }

    public void setLastProcessDate(Date lastProcessDate) {
        this.lastProcessDate = lastProcessDate;
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

    public FrequencyCode getFrequencyCodeObj() {
        return frequencyCodeObj;
    }

    public void setFrequencyCodeObj(FrequencyCode frequencyCodeObj) {
        this.frequencyCodeObj = frequencyCodeObj;
    }
    
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("transferNumber", this.transferNumber);
        return m;
    }
    
    public List<EndowmentRecurringCashTransferKEMIDTarget> getKemidTarget() {
        return kemidTarget;
    }

    public void setKemidTarget(List<EndowmentRecurringCashTransferKEMIDTarget> kemidTarget) {
        this.kemidTarget = kemidTarget;
    }

    public List<EndowmentRecurringCashTransferGLTarget> getGlTarget() {
        return glTarget;
    }

    public void setGlTarget(List<EndowmentRecurringCashTransferGLTarget> glTarget) {
        this.glTarget = glTarget;
    }

    public Integer getTargetKemidNextSeqNumber() {
        return targetKemidNextSeqNumber;
    }

    public void setTargetKemidNextSeqNumber(Integer targetKemidNextSeqNumber) {
        this.targetKemidNextSeqNumber = targetKemidNextSeqNumber;
    }
    
    public Integer incrementTargetKemidNextSeqNumber() {
        if (this.targetKemidNextSeqNumber == null) {
            this.targetKemidNextSeqNumber = 0;
        }
        this.targetKemidNextSeqNumber += 1;
        return targetKemidNextSeqNumber;
    }

    public Integer getTargetGlNextSeqNumber() {
        return targetGlNextSeqNumber;
    }

    public void setTargetGlNextSeqNumber(Integer targetGlNextSeqNumber) {
        this.targetGlNextSeqNumber = targetGlNextSeqNumber;
    }
    
    public Integer incrementTargetGlNextSeqNumber() {
        if (this.targetGlNextSeqNumber == null) {
            this.targetGlNextSeqNumber = 0;
        }
        this.targetGlNextSeqNumber += 1;
        return targetGlNextSeqNumber;
    }
}
