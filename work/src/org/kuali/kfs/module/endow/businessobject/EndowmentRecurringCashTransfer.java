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
