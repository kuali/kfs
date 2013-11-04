/*
 * Copyright 2009 The Kuali Foundation.
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
import java.util.LinkedHashMap;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This KemidSpecialInstruction class provides the various special instructions coded by the organization that apply to the KEMID.
 */
public class KemidSpecialInstruction extends PersistableBusinessObjectBase {

    private String kemid;
    private KualiInteger instructionSeq;
    private String agreementSpecialInstructionCode;
    private String comments;
    private Date instructionStartDate;
    private Date instructionEndDate;

    private KEMID kemidObjRef;
    private AgreementSpecialInstruction agreementSpecialInstruction;

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(EndowPropertyConstants.KEMID, this.kemid);
        m.put(EndowPropertyConstants.KEMID_SPEC_INSTR_SEQ, String.valueOf(instructionSeq));
        m.put(EndowPropertyConstants.KEMID_SPEC_INSTR_CD, String.valueOf(agreementSpecialInstructionCode));
        return m;
    }

    /**
     * Gets the agreementSpecialInstruction.
     * 
     * @return agreementSpecialInstruction
     */
    public AgreementSpecialInstruction getAgreementSpecialInstruction() {
        return agreementSpecialInstruction;
    }

    /**
     * Sets the agreementSpecialInstruction.
     * 
     * @param agreementSpecialInstruction
     */
    public void setAgreementSpecialInstruction(AgreementSpecialInstruction agreementSpecialInstruction) {
        this.agreementSpecialInstruction = agreementSpecialInstruction;
    }

    /**
     * Gets the agreementSpecialInstructionCode.
     * 
     * @return agreementSpecialInstructionCode
     */
    public String getAgreementSpecialInstructionCode() {
        return agreementSpecialInstructionCode;
    }

    /**
     * Sets the agreementSpecialInstructionCode.
     * 
     * @param agreementSpecialInstructionCode
     */
    public void setAgreementSpecialInstructionCode(String agreementSpecialInstructionCode) {
        this.agreementSpecialInstructionCode = agreementSpecialInstructionCode;
    }

    /**
     * Gets the comments.
     * 
     * @return comments
     */
    public String getComments() {
        return comments;
    }

    /**
     * Sets the comments.
     * 
     * @param comments
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * Gets the instructionEndDate.
     * 
     * @return instructionEndDate
     */
    public Date getInstructionEndDate() {
        return instructionEndDate;
    }

    /**
     * Sets the instructionEndDate.
     * 
     * @param instructionEndDate
     */
    public void setInstructionEndDate(Date instructionEndDate) {
        this.instructionEndDate = instructionEndDate;
    }

    /**
     * Gets the instructionSeq.
     * 
     * @return instructionSeq
     */
    public KualiInteger getInstructionSeq() {
        return instructionSeq;
    }

    /**
     * Sets the instructionSeq.
     * 
     * @param instructionSeq
     */
    public void setInstructionSeq(KualiInteger instructionSeq) {
        this.instructionSeq = instructionSeq;
    }

    /**
     * Gets the instructionStartDate.
     * 
     * @return instructionStartDate
     */
    public Date getInstructionStartDate() {
        return instructionStartDate;
    }

    /**
     * Sets the instructionStartDate.
     * 
     * @param instructionStartDate
     */
    public void setInstructionStartDate(Date instructionStartDate) {
        this.instructionStartDate = instructionStartDate;
    }

    /**
     * Gets the kemid.
     * 
     * @return kemid
     */
    public String getKemid() {
        return kemid;
    }

    /**
     * Sets the kemid.
     * 
     * @param kemid
     */
    public void setKemid(String kemid) {
        this.kemid = kemid;
    }

    /**
     * Gets the kemidObjRef.
     * 
     * @return kemidObjRef
     */
    public KEMID getKemidObjRef() {
        return kemidObjRef;
    }

    /**
     * Sets the kemidObjRef.
     * 
     * @param kemidObjRef
     */
    public void setKemidObjRef(KEMID kemidObjRef) {
        this.kemidObjRef = kemidObjRef;
    }

}
