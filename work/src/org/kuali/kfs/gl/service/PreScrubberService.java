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
package org.kuali.kfs.gl.service;

import java.io.IOException;
import java.util.Iterator;

import org.kuali.kfs.gl.report.PreScrubberReportData;

public interface PreScrubberService {
    public PreScrubberReportData preprocessOriginEntries(Iterator<String> inputOriginEntries, String outputFileName) throws IOException;
    
    /**
     * Returns whether chart of accounts codes that are spaces should be derived from the account number.  This assumes that a particular account
     * number may be associated with at most one chart of accounts code.
     */
    public boolean deriveChartOfAccountsCodeIfSpaces();
}
