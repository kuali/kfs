/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.kuali.kfs.module.purap.PurapConstants;

public class PurApDateFormatUtils {

    /**
     * 
     * This method retrieves a SimpleDataFormat by format Name.
     * @param formatName
     * @return
     */
    public static final SimpleDateFormat getSimpleDateFormat(String formatName) {
        return new SimpleDateFormat(getFormattingString(formatName), Locale.US);
    }
    
    public static final String getFormattingString(String formatName) {
        //TODO these will be moved to parameters shortly, its done this way now to centralize 
        //     all the formats in one place within PURAP.
        //KualiConfigurationService configService = SpringContext.getBean(KualiConfigurationService.class);
        //return configService.getParameterValue("KFS-PURAP", "All", formatName);
        
        if (PurapConstants.NamedDateFormats.CXML_DATE_FORMAT.equalsIgnoreCase(formatName)) {
            return "0000-00-00";
        }
        if (PurapConstants.NamedDateFormats.CXML_SIMPLE_DATE_FORMAT.equalsIgnoreCase(formatName)) {
            return "yyyy-MM-dd";
        }
        if (PurapConstants.NamedDateFormats.CXML_SIMPLE_TIME_FORMAT.equalsIgnoreCase(formatName)) {
            return "HH:mm:ss.sss";
        }
        if (PurapConstants.NamedDateFormats.KUALI_DATE_FORMAT.equalsIgnoreCase(formatName)) {
            return "00/00/0000";
        }
        if (PurapConstants.NamedDateFormats.KUALI_SIMPLE_DATE_FORMAT.equalsIgnoreCase(formatName)) {
            return "MM/dd/yyyy";
        }
        if (PurapConstants.NamedDateFormats.KUALI_SIMPLE_DATE_FORMAT_2.equalsIgnoreCase(formatName)) {
            return "MM-dd-yyyy";
        }
        throw new UnsupportedOperationException("The formatName passed in [" + formatName + "] is not one of the recognized formats.");
    }
    
}
