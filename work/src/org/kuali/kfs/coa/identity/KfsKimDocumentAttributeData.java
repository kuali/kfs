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
