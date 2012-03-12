/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.cg.businessobject.defaultvalue;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.web.format.DateFormatter;
import org.kuali.rice.krad.valuefinder.ValueFinder;

/**
 * Gets the current date from the DateTimeService.
 */
public class TodayValueFinder implements ValueFinder {

    /**
     * Gets the current date from the DateTimeService.
     * 
     * @see org.kuali.rice.krad.valuefinder.ValueFinder#getValue()
     */
    public String getValue() {
        DateTimeService dts = SpringContext.getBean(DateTimeService.class);
        DateFormatter df = new DateFormatter();
        return df.format(dts.getCurrentSqlDateMidnight()).toString();
    }

}
