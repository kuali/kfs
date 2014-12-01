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
