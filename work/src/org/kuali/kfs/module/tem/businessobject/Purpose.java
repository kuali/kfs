/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject;

import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * Relocation Reason
 * 
 */
@Entity
@Table(name="tem_purpose_t")
public class Purpose extends PersistableBusinessObjectBase implements Inactivateable{
    private String documentType;
    private String purposeCode;
    private String purposeName;
    private String purposeDescription;
    private Boolean reviewRequiredIndicator;
    private Boolean active = Boolean.TRUE;
    
    @Column(name="DOCUMENT_TYPE",length=4,nullable=false)
    public String getDocumentType() {
        return documentType;
    }
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
    @Column(name="PURPOSE_CODE",length=4,nullable=false)
    public String getPurposeCode() {
        return purposeCode;
    }
    public void setPurposeCode(String purposeCode) {
        this.purposeCode = purposeCode;
    }
    @Column(name="PURPOSE_NAME",length=40,nullable=true)
    public String getPurposeName() {
        return purposeName;
    }
    public void setPurposeName(String purposeName) {
        this.purposeName = purposeName;
    }
    @Column(name="PURPOSE_DESCRIPTION",length=100,nullable=true)
    public String getPurposeDescription() {
        return purposeDescription;
    }
    public void setPurposeDescription(String purposeDescription) {
        this.purposeDescription = purposeDescription;
    }
    @Column(name="REVIEW_REQUIRED_IND",length=1,nullable=true)
    public Boolean getReviewRequiredIndicator() {
        return reviewRequiredIndicator;
    }
    public void setReviewRequiredIndicator(Boolean reviewRequiredIndicator) {
        this.reviewRequiredIndicator = reviewRequiredIndicator;
    }
    @Column(name="REVIEW_REQUIRED_IND",length=1,nullable=true)
    public Boolean isReviewRequiredIndicator(){
        return getReviewRequiredIndicator();
    }
    @Override
    @Column(name="ROW_ACTV_IND",length=1, nullable=false)
    public boolean isActive() {
        return this.active;
    }
    @Column(name="ROW_ACTV_IND",length=1, nullable=false)
    public boolean getActive(){
        return this.active;
    }
    @Override
    public void setActive(boolean active){
        this.active = active;
    }
    @Override
    protected LinkedHashMap toStringMapper() {
        // TODO Auto-generated method stub
        return null;
    }
   
   
}
