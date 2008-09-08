/*
 * Copyright 2007 The Kuali Foundation.
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
/*
 * Created on Jul 9, 2004
 *
 */
package org.kuali.kfs.pdp.businessobject;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerAware;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.kfs.sys.businessobject.TimestampedBusinessObjectBase;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.exception.UserNotFoundException;
import org.kuali.rice.kns.service.UniversalUserService;

/**
 * 
 */
public class DocumentType extends TimestampedBusinessObjectBase {

    private Integer id; // DOC_TYP_ID
    private String fsOriginCode; // FS_ORIGIN_CD
    private String fdocTypeCode; // FDOC_TYP_CD
    private TransactionType transactionType;
    private DisbursementType disbursementType;

    public DocumentType() {
        super();
    }

    /**
     * @hibernate.id column="DOC_TYP_ID" generator-class="sequence"
     * @hibernate.generator-param name="sequence" value="PDP.PDP_DOC_TYP_ID_SEQ"
     * @return Returns the documentTypeId.
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param documentTypeId The documentTypeId to set.
     */
    public void setId(Integer documentTypeId) {
        this.id = documentTypeId;
    }

    /**
     * @return
     * @hibernate.property column="FDOC_TYP_CD" length="4"
     */
    public String getFdocTypeCode() {
        return fdocTypeCode;
    }

    /**
     * @return
     * @hibernate.property class="edu.iu.uis.pdp.bo.DisbursementType" column="DISB_TYP_CD" not-null="true"
     */
    public DisbursementType getDisbursementType() {
        return disbursementType;
    }

    /**
     * @return
     * @hibernate.property column="FS_ORIGIN_CD" length="2"
     */
    public String getFsOriginCode() {
        return fsOriginCode;
    }

    /**
     * @return
     * @hibernate.many-to-one class="edu.iu.uis.pdp.bo.TransactionType" column="TRN_TYP_CD" not-null="true"
     */
    public TransactionType getTransactionType() {
        return transactionType;
    }

    /**
     * @param string
     */
    public void setDisbursementType(DisbursementType disbursementType) {
        this.disbursementType = disbursementType;
    }

    /**
     * @param string
     */
    public void setFdocTypeCode(String string) {
        fdocTypeCode = string;
    }

    /**
     * @param string
     */
    public void setFsOriginCode(String string) {
        fsOriginCode = string;
    }

    /**
     * @param string
     */
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof DocumentType)) {
            return false;
        }
        DocumentType o = (DocumentType) obj;
        return new EqualsBuilder().append(id, o.getId()).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(83, 47).append(id).toHashCode();
    }

    public String toString() {
        return new ToStringBuilder(this).append("id", this.id).toString();
    }

}
