/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.web.struts;

import java.sql.Date;

import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;

public class TrialBalanceForm extends EndowmentReportBaseForm {

    protected String asOfDate;
    
    public TrialBalanceForm() {
        KEMService kemService = SpringContext.getBean(KEMService.class);
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        Date currentDate = kemService.getCurrentDate();
        asOfDate = dateTimeService.toDateString(currentDate);
    }
    
    /**
     * Clears all the fields
     */
    public void clear() {
        super.clear();
    }

    public String getAsOfDate() {
        return asOfDate;
    }

    public void setAsOfDate(String asOfDate) {
        //this.asOfDate = asOfDate;
    }
    
}
