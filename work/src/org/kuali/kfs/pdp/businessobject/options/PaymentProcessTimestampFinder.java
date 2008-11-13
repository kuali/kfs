/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.pdp.businessobject.options;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.valueFinder.ValueFinder;
import org.kuali.rice.kns.service.DateTimeService;

public class PaymentProcessTimestampFinder implements ValueFinder {


    /**
     * @see org.kuali.rice.kns.lookup.valueFinder.ValueFinder#getValue()
     */
    public String getValue() {
        // we create a search criteria to get te payment processes in tha last 4 months
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -4);

        String now = dateTimeService.toDateString(dateTimeService.getCurrentTimestamp());
        String fourMonthsAgo = dateTimeService.toDateString(new Timestamp(calendar.getTimeInMillis()));

        String processTimestampValue = fourMonthsAgo + ".." + now;
        return processTimestampValue;
    }

}
