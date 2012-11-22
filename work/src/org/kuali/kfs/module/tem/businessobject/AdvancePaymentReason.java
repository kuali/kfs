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
import javax.persistence.Id;
import javax.persistence.Table;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

@Entity
@Table(name="TEM_ADV_PMNT_RSN_T")
public class AdvancePaymentReason extends PersistableBusinessObjectBase {

    private String code;
    private String description;
    private Boolean active = Boolean.TRUE;


    @Id
    @Column(name="CODE",length=2, nullable=false)
    public String getCode() {
        return code;
    }

    /**
     * Sets the code attribute value.
     * @param code The code to set.
     */
    public void setCode(String code) {
        this.code = code;
    }

    @Column(name="DESCRIPTION",length=100,nullable=true)
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description attribute value.
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name="ACTV_IND",nullable=false,length=1)
    public boolean isActive() {
        return active;
    }


    public void setActive(boolean active) {
        this.active = active;
    }

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        return null;
    }

}
