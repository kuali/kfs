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
