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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

@Entity
@Table(name = "TEM_ACCT_DOC_REL_T")
public class AccountingDocumentRelationship extends PersistableBusinessObjectBase implements Inactivateable {
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

    @Override
    public LinkedHashMap toStringMapper() {
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
