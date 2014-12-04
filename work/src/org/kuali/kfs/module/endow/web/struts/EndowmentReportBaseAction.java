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
