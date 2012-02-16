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
package org.kuali.kfs.module.tem.businessobject;

import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * Emergency Contact
 * 
 */
public abstract class EmergencyContact extends PersistableBusinessObjectBase {

    private Integer id;    
    private String documentNumber;    
    private Integer financialDocumentLineNumber;    
    private boolean primary;   
    private String contactRelationTypeCode;  
    private ContactRelationType contactRelationType;  
    private String contactName;  
    private String phoneNumber;   
    private String emailAddress;
    
    @Id
    @Column(name="id",nullable=false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    @Column(name="FDOC_NBR")
    public String getDocumentNumber() {
        return documentNumber;
    }


    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }
    
    /**
     * Gets the financialDocumentLineNumber attribute.
     * 
     * @return Returns the financialDocumentLineNumber
     */
    @Column(name="FDOC_LINE_NBR")
    public Integer getFinancialDocumentLineNumber() {
        return financialDocumentLineNumber;
    }

    /**
     * Sets the financialDocumentLineNumber attribute.
     * 
     * @param financialDocumentLineNumber The financialDocumentLineNumber to set.
     */
    public void setFinancialDocumentLineNumber(Integer financialDocumentLineNumber) {
        this.financialDocumentLineNumber = financialDocumentLineNumber;
    }

    @Column(name="cont_rel_typ_cd",length=3,nullable=false)
    public String getContactRelationTypeCode() {
        return contactRelationTypeCode;
    }

    public void setContactRelationTypeCode(String contactRelationTypeCode) {
        this.contactRelationTypeCode = contactRelationTypeCode;
    }

    @ManyToOne
    @JoinColumn(name="cont_rel_typ_cd")
    public ContactRelationType getContactRelationType() {
        return contactRelationType;
    }

    public void setContactRelationType(ContactRelationType contactRelationType) {
        this.contactRelationType = contactRelationType;
    }

    @Column(name="cont_nm",length=40,nullable=false)
    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    /**
     * Gets the emailAddress attribute. 
     * @return Returns the emailAddress.
     */
    @Column(name="email_addr",length=50,nullable=true)
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets the emailAddress attribute value.
     * @param emailAddress The emailAddress to set.
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Gets the phoneNumber attribute. 
     * @return Returns the phoneNumber.
     */
    @Column(name="phone_nbr",length=20,nullable=true)
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phoneNumber attribute value.
     * @param phoneNumber The phoneNumber to set.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("id", id);
        map.put("contactName", contactName);
        map.put("contactRelationTypeCode", contactRelationTypeCode);
        
        return map;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }
    
    
}
