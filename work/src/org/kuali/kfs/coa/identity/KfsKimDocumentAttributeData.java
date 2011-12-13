/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.coa.identity;

import java.io.Serializable;

import javax.persistence.Transient;

import org.kuali.rice.kim.api.common.attribute.KimAttribute;

public class KfsKimDocumentAttributeData implements Serializable {

    private static final long serialVersionUID = -1512640359333185819L;
    private String attrDataId;
    private String memberId;
    private String kimTypId;
    private String kimAttrDefnId;
    private String attrVal = "";
    private KimAttribute kimAttribute;
    @Transient
    private String qualifierKey;

    private Boolean unique;
    
    public String getAttrDataId() {
        return attrDataId;
    }

    public void setAttrDataId(String attrDataId) {
        this.attrDataId = attrDataId;
    }

    public String getKimTypId() {
        return kimTypId;
    }

    public void setKimTypId(String kimTypId) {
        this.kimTypId = kimTypId;
    }

    public String getKimAttrDefnId() {
        return kimAttrDefnId;
    }

    public void setKimAttrDefnId(String kimAttrDefnId) {
        this.kimAttrDefnId = kimAttrDefnId;
    }

    public String getAttrVal() {
        return attrVal;
    }

    public void setAttrVal(String attrVal) {
        this.attrVal = attrVal;
    }

    public String getQualifierKey() {
        return this.qualifierKey;
    }

    public void setQualifierKey(String qualifierKey) {
        this.qualifierKey = qualifierKey;
    }

    /**
     * @return the kimAttribute
     */
    public KimAttribute getKimAttribute() {
        return this.kimAttribute;
    }

    /**
     * @param kimAttribute the kimAttribute to set
     */
    public void setKimAttribute(KimAttribute kimAttribute) {
        this.kimAttribute = kimAttribute;
    }

    /**
     * @return the uniqueAndReadOnly
     */
    public Boolean isUnique() {
        return this.unique;
    }

    /**
     * @param uniqueAndReadOnly the uniqueAndReadOnly to set
     */
    public void setUnique(Boolean unique) {
        this.unique = unique;
    }

    /**
     * Gets the memberId attribute. 
     * @return Returns the memberId.
     */
    public String getMemberId() {
        return memberId;
    }

    /**
     * Sets the memberId attribute value.
     * @param memberId The memberId to set.
     */
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

}
