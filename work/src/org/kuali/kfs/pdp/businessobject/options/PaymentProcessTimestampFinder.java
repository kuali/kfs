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
package org.kuali.kfs.pdp.businessobject.options;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.valuefinder.ValueFinder;

public class PaymentProcessTimestampFinder implements ValueFinder {


    /**
     * @see org.kuali.rice.krad.valuefinder.ValueFinder#getValue()
     */
    public String getValue() {
        // we create a search criteria to get te payment processes in tha last 4 months
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        GregorianCalendar calendarFourMonthsAgo = new GregorianCalendar();
        calendarFourMonthsAgo.setTime(new Date());
        calendarFourMonthsAgo.add(Calendar.MONTH, -4);

        // one day was added to the current date becuase the ".." wildcard on date will give us the results with a date greater or
        // equal with the beginning date and less than the end date.
        GregorianCalendar calendarNow = new GregorianCalendar();
        calendarNow.setTime(new Date());
        calendarNow.add(Calendar.DATE, +1);
        String now = dateTimeService.toDateString(new Timestamp(calendarNow.getTimeInMillis()));
        String fourMonthsAgo = dateTimeService.toDateString(new Timestamp(calendarFourMonthsAgo.getTimeInMillis()));

        String processTimestampValue = fourMonthsAgo + ".." + now;
        return processTimestampValue;
    }

}
