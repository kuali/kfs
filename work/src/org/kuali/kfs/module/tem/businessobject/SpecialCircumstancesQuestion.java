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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Special Circumstances Business Object
 *
 */
@Entity
@Table(name="TEM_SPCL_CRCMSNCS_Q_T")
public class SpecialCircumstancesQuestion extends PersistableBusinessObjectBase implements MutableInactivatable {

    @Id
    @GeneratedValue(generator="TEM_SPCL_CRCMSNCS_Q_ID_SEQ")
    @SequenceGenerator(name="TEM_SPCL_CRCMSNCS_Q_SEQ",sequenceName="TEM_SPCL_CRCMSNCS_Q_ID_SEQ", allocationSize=5)
    @Column(name="ID",nullable=false)
    private Long id;

    @Column(name="TEXT",length=255,nullable=false)
    private String text;

    @Column(name="TXT_IND",nullable=false,length=1)
    private boolean free = Boolean.FALSE;

    @Column(name="ACTIVE_IND",nullable=false,length=1)
    private boolean active = Boolean.TRUE;

    @Column(name="DOCUMENT_TYPE",nullable=false,length=4)
    private String documentType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean hasFreeFormTextField() {
        return isFree();
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("id", id);
        map.put("text", text);
        map.put("free", free);

        return map;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
}
