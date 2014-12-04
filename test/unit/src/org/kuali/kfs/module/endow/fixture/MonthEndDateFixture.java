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
