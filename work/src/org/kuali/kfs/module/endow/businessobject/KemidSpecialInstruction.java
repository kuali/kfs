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
