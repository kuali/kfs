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

/**
 * 
 */

public class FormatProcess extends TimestampedBusinessObjectBase {

    private String physicalCampusProcessCode; // PHYS_CMP_PROC_CD
    private Timestamp beginFormat; // BEG_FMT_TS
    
    public FormatProcess() {
        super();
    }

    public Timestamp getBeginFormat() {
        return beginFormat;
    }

    public void setBeginFormat(Timestamp beginFormat) {
        this.beginFormat = beginFormat;
    }

    /**
     * @return
     * @hibernate.id column="PHYS_CMP_PROC_CD" length="2" generator-class="assigned"
     */
    public String getPhysicalCampusProcessCode() {
        return physicalCampusProcessCode;
    }

    /**
     * @param string
     */
    public void setPhysicalCampusProcessCode(String string) {
        physicalCampusProcessCode = string;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof FormatProcess)) {
            return false;
        }
        FormatProcess o = (FormatProcess) obj;
        return new EqualsBuilder().append(physicalCampusProcessCode, o.getPhysicalCampusProcessCode()).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(83, 91).append(physicalCampusProcessCode).toHashCode();
    }

    public String toString() {
        return new ToStringBuilder(this).append("physicalCampusProcessCode", this.physicalCampusProcessCode).toString();
    }

}
