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
package org.kuali.kfs.sys.service.impl;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.service.DocumentNumberAwareReportWriterService;
import org.kuali.rice.kns.util.KNSConstants;

public class ScrubberListingReportWriterTextServiceImpl extends ReportWriterTextServiceImpl implements DocumentNumberAwareReportWriterService {
    private String documentNumber;
    
    /**
     * @see org.kuali.kfs.sys.service.impl.ReportWriterTextServiceImpl#destroy()
     */
    @Override
    public void destroy() {
        super.destroy();
        
        documentNumber = KNSConstants.EMPTY_STRING;
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
//        if (StringUtils.isBlank(documentNumber)) {
//            throw new RuntimeException("document number is blank");
//        }
        
        String fullFilePath = filePath + File.separator + this.fileNamePrefix;
        if (StringUtils.isNotBlank(documentNumber)) {
            fullFilePath += documentNumber + "_";
        }
        fullFilePath += dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate()) + fileNameSuffix;
        
        return fullFilePath;
    }
}
