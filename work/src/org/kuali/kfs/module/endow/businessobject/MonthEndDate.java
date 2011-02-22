/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiInteger;

public class MonthEndDate extends PersistableBusinessObjectBase {
    private KualiInteger monthEndDateId;
    private Date monthEndDate;

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(EndowPropertyConstants.MONTH_END_DATE_ID, this.monthEndDateId);
        return m;
    }

    /**
     * Gets the monthEndDate.
     * 
     * @return monthEndDate
     */
    public Date getMonthEndDate() {
        return monthEndDate;
    }

    /**
     * Sets the monthEndDate.
     * 
     * @param monthEndDate
     */
    public void setMonthEndDate(Date monthEndDate) {
        this.monthEndDate = monthEndDate;
    }

    /**
     * Gets the monthEndDateId.
     * 
     * @return monthEndDateId
     */
    public KualiInteger getMonthEndDateId() {
        return monthEndDateId;
    }

    /**
     * Sets the monthEndDateId.
     * 
     * @param monthEndDateId
     */
    public void setMonthEndDateId(KualiInteger monthEndDateId) {
        this.monthEndDateId = monthEndDateId;
    }
    
    public Date getBeginningDate() {
        return monthEndDate;
    }

    public Date getEndingDate() {
        return monthEndDate;
    }
}
