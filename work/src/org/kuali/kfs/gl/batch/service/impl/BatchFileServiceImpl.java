/*
 * Copyright 2006-2009 The Kuali Foundation
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
package org.kuali.kfs.gl.batch.service.impl;

import java.util.Date;

import org.kuali.kfs.gl.batch.service.BatchFileService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;

/**
 * A general use implementation of BatchFileServiceImpl
 */
public class BatchFileServiceImpl implements BatchFileService {

    public String addTimeInfoToFilename(String fileName, Date timeInfo){
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        String formattedDate = dateTimeService.toDateTimeStringForFilename(timeInfo);
        return fileName + "." + formattedDate;
        
    }
}
