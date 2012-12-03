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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Emergency Contact
 */
@Entity
@Table(name = "TEM_EM_CONT_T")
public class TravelerDetailEmergencyContact extends EmergencyContact {
    private TravelerDetail traveler;
    private Integer travelerDetailId;
    private String documentNumber;
    private Integer financialDocumentLineNumber;

    public TravelerDetailEmergencyContact() {
    }

    public TravelerDetailEmergencyContact(TemProfileEmergencyContact emergencyContact) {
        this.setPrimary(emergencyContact.isPrimary());
        this.setContactRelationTypeCode(emergencyContact.getContactRelationTypeCode());
        this.setContactRelationType(emergencyContact.getContactRelationType());
        this.setContactName(emergencyContact.getContactName());
        this.setPhoneNumber(emergencyContact.getPhoneNumber());
        this.setEmailAddress(emergencyContact.getEmailAddress());
    }

    public Integer getTravelerDetailId() {
        return travelerDetailId;
    }

    public void setTravelerDetailId(Integer travelerDetailId) {
        this.travelerDetailId = travelerDetailId;
    }

    public TravelerDetail getTraveler() {
        return traveler;
    }

    public void setTraveler(TravelerDetail traveler) {
        this.traveler = traveler;
    }

    @Column(name="FDOC_NBR")
    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    @Column(name="FDOC_LINE_NBR")
    public Integer getFinancialDocumentLineNumber() {
        return financialDocumentLineNumber;
    }

    public void setFinancialDocumentLineNumber(Integer financialDocumentLineNumber) {
        this.financialDocumentLineNumber = financialDocumentLineNumber;
    }
}
