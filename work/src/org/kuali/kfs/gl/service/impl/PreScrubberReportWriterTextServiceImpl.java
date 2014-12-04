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
package org.kuali.kfs.gl.service.impl;

import org.kuali.kfs.gl.service.PreScrubberService;
import org.kuali.kfs.sys.service.DocumentNumberAwareReportWriterService;
import org.kuali.kfs.sys.service.impl.ScrubberListingReportWriterTextServiceImpl;

public class PreScrubberReportWriterTextServiceImpl extends ScrubberListingReportWriterTextServiceImpl implements DocumentNumberAwareReportWriterService {
    protected boolean enabled;
    protected PreScrubberService preScrubberService;
    
    @Override
    public void destroy() {
        enabled = true;
        super.destroy();
    }

    @Override
    public void initialize() {
        if (preScrubberService.deriveChartOfAccountsCodeIfSpaces()) {
            enabled = true;
            super.initialize();
        }
        else {
            enabled = false;
        }
    }

    @Override
    public void pageBreak() {
        if (enabled) {
            super.pageBreak();
        }
    }

    @Override
    public void writeFormattedMessageLine(String format, Object... args) {
        if (enabled) {
            super.writeFormattedMessageLine(format, args);
        }
    }

    public void setPreScrubberService(PreScrubberService preScrubberService) {
        this.preScrubberService = preScrubberService;
    }
}
