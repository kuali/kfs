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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.web.struts.action.KualiAction;

public abstract class EndowmentReportBaseAction extends KualiAction {
    
    protected final char KEMID_SEPERATOR = '&';
    protected final char OTHER_CRITERIA_SEPERATOR = ',';
    
    public final String ERROR_REPORT_KEMID_WITH_OTHER_CRITERIA = "The use of the KEMID as a selection criterion cannot be used in combination with any orther selection criteria.";
    public final String ERROR_REPORT_ENDING_DATE_NOT_GREATER_THAN_BEGINNING_DATE = "The ending date must be greater than the beginning date.";
    public final String ERROR_BOTH_BEGINNING_AND_ENDING_DATE_REQUIRED = "Both Beginning Date and Ending Date are required.";
    
    /**
     * Parses the string value, which can include wild cards or separators
     * 
     * @param valueString
     * @param separater
     * @return
     */
    public List<String> parseValueString(String valueString, char separater) {        
        
        List<String> values = null;        
        if (StringUtils.isNotBlank(valueString)) {
            values = Arrays.asList(StringUtils.split(valueString.trim().toUpperCase(), separater));
        }        
        return values;
    }

}
