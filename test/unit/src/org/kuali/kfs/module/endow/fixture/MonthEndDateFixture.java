/*
 * Copyright 2010 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.endow.fixture;

import java.sql.Date;

import org.kuali.kfs.module.endow.businessobject.MonthEndDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum MonthEndDateFixture {

    MONTH_END_DATE_TEST_RECORD(new KualiInteger(0), // monthEndDateId
            Date.valueOf("2010-07-07")// monthEndDate
    );

    public KualiInteger monthEndDateId;
    public Date monthEndDate;

    private MonthEndDateFixture(KualiInteger monthEndDateId, Date monthEndDate) {
        this.monthEndDateId = monthEndDateId;
        this.monthEndDate = monthEndDate;
    }

    /**
     * This method creates a MonthEndDate record and saves it to the DB table.
     * 
     * @return a monthEndDate
     */
    public MonthEndDate createMonthEndDate() {
        MonthEndDate monthEndDate = new MonthEndDate();
        monthEndDate.setMonthEndDateId(this.monthEndDateId);
        monthEndDate.setMonthEndDate(this.monthEndDate);

        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(monthEndDate);

        return monthEndDate;
    }
}
