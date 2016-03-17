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
package edu.arizona.kfs.coa.businessobject.defaultvalue;


import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.valuefinder.ValueFinder;
import org.kuali.rice.core.api.datetime.DateTimeService;

public class ActiveIndicatorValuesFinder implements ValueFinder {

    public String getValue() {
      
        return "Yes";
    }

}