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

import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

@Entity
@Table(name = "TEM_TRANS_MD_DTL_T")
public class TransportationModeDetail extends PersistableBusinessObjectBase {

    private String documentNumber;
    private String transportationModeCode;
    private TransportationMode transportationMode;

    /**
     *
     * This method returns the document number this TransportationModeDetail object is associated with
     * @return document number
     */
    @Column(name="doc_nbr")
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     *
     * This method sets the document number this TransportationModeDetail object will be associated with
     * @param documentNumber
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    @Column(name="TRANS_MODE_CD",length=3, nullable=false)
    public String getTransportationModeCode() {
        return transportationModeCode;
    }


    public void setTransportationModeCode(String transportationModeCode) {
        this.transportationModeCode = transportationModeCode;
    }

    @ManyToOne
    @JoinColumn(name="TRANS_MODE_CD")
    public TransportationMode getTransportationMode() {
        return transportationMode;
    }


    public void setTransportationMode(TransportationMode transportationMode) {
        this.transportationMode = transportationMode;
    }

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put("transportationModeCode", this.transportationModeCode);
        return m;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        TransportationModeDetail detail = (TransportationModeDetail)obj;

        return (this.transportationModeCode.equals(detail.getTransportationModeCode()) && this.documentNumber.equals(detail.getDocumentNumber()));
    }



}
