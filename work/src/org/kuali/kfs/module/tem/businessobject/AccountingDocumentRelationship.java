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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

@Entity
@Table(name = "TEM_ACCT_DOC_REL_T")
public class AccountingDocumentRelationship extends PersistableBusinessObjectBase implements MutableInactivatable {
    public static final String ID = "ID";
    public static final String DOC_NBR = "DOC_NBR";
    public static final String REL_DOC_NBR = "REL_DOC_NBR";
    public static final String PRNCPL_ID = "PRNCPL_ID";

    private Integer id;
    private String documentNumber;
    private String relDocumentNumber;
    private String principalId; // initiated by
    private String description;
    private boolean active = true;

    public AccountingDocumentRelationship() {
    }

    public AccountingDocumentRelationship(Integer id, String documentNumber, String relDocumentNumber, String principalId, String description, boolean active) {
        this.id = id;
        this.documentNumber = documentNumber;
        this.relDocumentNumber = relDocumentNumber;
        this.principalId = principalId;
        this.description = description;
        this.active = active;
    }

    public AccountingDocumentRelationship(String documentNumber, String relDocumentNumber, String description) {
        this(null, documentNumber, relDocumentNumber, null, description, true);
    }

    public AccountingDocumentRelationship(String documentNumber, String relDocumentNumber) {
        this(documentNumber, relDocumentNumber, null);
    }

    @Id
    @GeneratedValue(generator = "TEM_ACCT_DOC_REL_ID_SEQ")
    @SequenceGenerator(name = "TEM_ACCT_DOC_REL_ID_SEQ", sequenceName = "TEM_ACCT_DOC_REL_ID_SEQ", allocationSize = 5)
    @Column(name = ID, length = 19, nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = DOC_NBR, length = 40, nullable = false)
    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    @Column(name = REL_DOC_NBR, length = 40, nullable = false)
    public String getRelDocumentNumber() {
        return relDocumentNumber;
    }

    public void setRelDocumentNumber(String relDocumentNumber) {
        this.relDocumentNumber = relDocumentNumber;
    }

    @Column(name = PRNCPL_ID, length = 40, nullable = true)
    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    @Override
    @Column(name = "ACTV_IND", nullable = false, length = 1)
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @SuppressWarnings("rawtypes")
    public LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("id", id);
        map.put("documentNumber", documentNumber);
        map.put("relDocumentNumber", relDocumentNumber);
        map.put("principalId", principalId);
        map.put("description", description);
        map.put("active", active);

        return map;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
