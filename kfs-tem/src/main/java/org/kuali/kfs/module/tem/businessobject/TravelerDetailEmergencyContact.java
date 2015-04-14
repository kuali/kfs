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
