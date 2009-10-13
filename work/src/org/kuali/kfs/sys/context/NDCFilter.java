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
package org.kuali.kfs.sys.context;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

public class NDCFilter extends Filter {
    private String nestedDiagnosticContext;

    public NDCFilter(String nestedDiagnosticContext) {
        super();
        this.nestedDiagnosticContext = nestedDiagnosticContext;
    }

    /**
     * @see org.apache.log4j.spi.Filter#decide(org.apache.log4j.spi.LoggingEvent)
     */
    @Override
    public int decide(LoggingEvent event) {
        if (nestedDiagnosticContext.equals(event.getNDC())) {
            return ACCEPT;
        }
        return DENY;
    }
}
