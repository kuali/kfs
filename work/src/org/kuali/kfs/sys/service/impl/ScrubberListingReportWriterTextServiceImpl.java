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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.service.DocumentNumberAwareReportWriterService;
import org.kuali.rice.krad.util.KRADConstants;

public class ScrubberListingReportWriterTextServiceImpl extends ReportWriterTextServiceImpl implements DocumentNumberAwareReportWriterService {
    private String documentNumber;
    
    /**
     * @see org.kuali.kfs.sys.service.impl.ReportWriterTextServiceImpl#destroy()
     */
    @Override
    public void destroy() {
        super.destroy();
        
        documentNumber = KRADConstants.EMPTY_STRING;
    }

    /**
     * @see org.kuali.kfs.sys.service.impl.ReportWriterTextServiceImpl#initialize()
     */
    @Override
    public void initialize() {
        super.initialize();
    }

    /**
     * @see org.kuali.kfs.sys.service.DocumentNumberAwareReportWriterService#setDocumentNumber(java.lang.String)
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    @Override
    protected String generateFullFilePath() {
        String fullFilePath = filePath + File.separator;
        if (StringUtils.isNotBlank(documentNumber) && isAggregationModeOn()) {
            fullFilePath += documentNumber + "_";
        }
        fullFilePath += this.fileNamePrefix;
        if (StringUtils.isNotBlank(documentNumber) && !isAggregationModeOn()) {
            fullFilePath += documentNumber + "_";
        }
        if (!isAggregationModeOn()) {
            fullFilePath += dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate());
        }
        fullFilePath += fileNameSuffix;
        
        return fullFilePath;
    }
}
