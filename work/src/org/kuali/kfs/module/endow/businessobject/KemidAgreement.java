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
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This KemidAgreement class provides the documentation used to establish a KEMID.
 */
public class KemidAgreement extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String kemid;
    private KualiInteger agreementId;
    private String agreementTypeCode;
    private String agreementStatusCode;
    private boolean useTransactionRestrictionFromAgreement;
    private Date agreementStatusDate;
    private String otherAgreementDocumentation;
    private String donorIntentFromAgreement;
    private String comments;
    private boolean active;

    private KEMID kemidObjRef;
    private AgreementType agreementType;
    private AgreementStatus agreementStatus;

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(EndowPropertyConstants.KEMID, this.kemid);
        m.put(EndowPropertyConstants.KEMID_AGRMNT_ID, String.valueOf(agreementId));
        return m;
    }


    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#isActive()
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#setActive(boolean)
     */
    public void setActive(boolean active) {
        this.active = active;
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
     * Gets the agreementId.
     * 
     * @return agreementId
     */
    public KualiInteger getAgreementId() {
        return agreementId;
    }


    /**
     * Sets the agreementId.
     * 
     * @param agreementId
     */
    public void setAgreementId(KualiInteger agreementId) {
        this.agreementId = agreementId;
    }


    /**
     * Gets the agreementTypeCode.
     * 
     * @return agreementTypeCode
     */
    public String getAgreementTypeCode() {
        return agreementTypeCode;
    }


    /**
     * Sets the agreementTypeCode.
     * 
     * @param agreementTypeCode
     */
    public void setAgreementTypeCode(String agreementTypeCode) {
        this.agreementTypeCode = agreementTypeCode;
    }


    /**
     * Gets the agreementStatusCode.
     * 
     * @return agreementStatusCode
     */
    public String getAgreementStatusCode() {
        return agreementStatusCode;
    }


    /**
     * Sets the agreementStatusCode.
     * 
     * @param agreementStatusCode
     */
    public void setAgreementStatusCode(String agreementStatusCode) {
        this.agreementStatusCode = agreementStatusCode;
    }


    /**
     * Gets the agreementStatusDate.
     * 
     * @return agreementStatusDate
     */
    public Date getAgreementStatusDate() {
        return agreementStatusDate;
    }


    /**
     * Sets the agreementStatusDate.
     * 
     * @param agreementStatusDate
     */
    public void setAgreementStatusDate(Date agreementStatusDate) {
        this.agreementStatusDate = agreementStatusDate;
    }


    /**
     * Gets the otherAgreementDocumentation.
     * 
     * @return otherAgreementDocumentation
     */
    public String getOtherAgreementDocumentation() {
        return otherAgreementDocumentation;
    }


    /**
     * Sets the otherAgreementDocumentation.
     * 
     * @param otherAgreementDocumentation
     */
    public void setOtherAgreementDocumentation(String otherAgreementDocumentation) {
        this.otherAgreementDocumentation = otherAgreementDocumentation;
    }


    /**
     * Gets the donorIntentFromAgreement.
     * 
     * @return donorIntentFromAgreement
     */
    public String getDonorIntentFromAgreement() {
        return donorIntentFromAgreement;
    }


    /**
     * Sets the donorIntentFromAgreement.
     * 
     * @param donorIntentFromAgreement
     */
    public void setDonorIntentFromAgreement(String donorIntentFromAgreement) {
        this.donorIntentFromAgreement = donorIntentFromAgreement;
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


    /**
     * Gets the agreementType.
     * 
     * @return agreementType
     */
    public AgreementType getAgreementType() {
        return agreementType;
    }


    /**
     * Sets the agreementType.
     * 
     * @param agreementType
     */
    public void setAgreementType(AgreementType agreementType) {
        this.agreementType = agreementType;
    }


    /**
     * Gets the agreementStatus.
     * 
     * @return agreementStatus
     */
    public AgreementStatus getAgreementStatus() {
        return agreementStatus;
    }


    /**
     * Sets the agreementStatus.
     * 
     * @param agreementStatus
     */
    public void setAgreementStatus(AgreementStatus agreementStatus) {
        this.agreementStatus = agreementStatus;
    }

    /**
     * Gets the useTransactionRestrictionFromAgreement.
     * 
     * @return useTransactionRestrictionFromAgreement
     */
    public boolean isUseTransactionRestrictionFromAgreement() {
        return useTransactionRestrictionFromAgreement;
    }

    /**
     * Sets the useTransactionRestrictionFromAgreement.
     * 
     * @param useTransactionRestrictionFromAgreement
     */
    public void setUseTransactionRestrictionFromAgreement(boolean useTransactionRestrictionFromAgreement) {
        this.useTransactionRestrictionFromAgreement = useTransactionRestrictionFromAgreement;
    }

}
