/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.purap.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

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
        return SpringContext.getBean(ParameterService.class).getParameterValueAsString(PurapConstants.PURAP_NAMESPACE, "All", formatName);
    }
    
}
