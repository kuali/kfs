/*
 * Copyright 2008-2009 The Kuali Foundation
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
