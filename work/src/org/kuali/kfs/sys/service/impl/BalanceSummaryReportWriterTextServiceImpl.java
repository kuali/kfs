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
package org.kuali.kfs.sys.service.impl;

import java.io.File;

import org.kuali.kfs.sys.service.FiscalYearAwareReportWriterService;

/**
 * Ensures that balance summary reports have the fiscal year included in the filename.
 */
public class BalanceSummaryReportWriterTextServiceImpl extends ReportWriterTextServiceImpl implements FiscalYearAwareReportWriterService {
    private Integer fiscalYear;
    
    /**
     * @see org.kuali.kfs.sys.service.impl.ReportWriterTextServiceImpl#destroy()
     */
    @Override
    public void destroy() {
        super.destroy();
        
        fiscalYear = null;
    }

    /**
     * @see org.kuali.kfs.sys.service.impl.ReportWriterTextServiceImpl#initialize()
     */
    @Override
    public void initialize() {
        super.initialize();
    }

    /**
     * @see org.kuali.kfs.sys.service.FiscalYearAwareReportWriterService#setFiscalYear(java.lang.Integer)
     */
    public void setFiscalYear(Integer fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    @Override
    protected String generateFullFilePath() {
        if (fiscalYear == null) {
            throw new RuntimeException("fiscal year is blank");
        }
        if (isAggregationModeOn()) {
            return filePath + File.separator + this.fileNamePrefix + fiscalYear.toString() + fileNameSuffix;            
        }
        else {
            return filePath + File.separator + this.fileNamePrefix + fiscalYear.toString() + "_" + dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate()) + fileNameSuffix;
        }
    }
}
