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
