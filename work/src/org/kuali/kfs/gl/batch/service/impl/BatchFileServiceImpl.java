/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.gl.batch.service.impl;

import java.util.Date;

import org.kuali.kfs.gl.batch.service.BatchFileService;

/**
 * A general use implementation of BatchFileServiceImpl
 */
public class BatchFileServiceImpl implements BatchFileService {

    public String addTimeInfoToFilename(String fileName, Date timeInfo){
        
        
        String timeString = timeInfo.toString();
        String year = timeString.substring(timeString.length() - 4, timeString.length());
        String month = timeString.substring(4, 7);
        String day = timeString.substring(8, 10);
        String hour = timeString.substring(11, 13);
        String min = timeString.substring(14, 16);
        String sec = timeString.substring(17, 19);

        return fileName + "." + year + "-" + month + "-" + day + "." + hour + "-" + min + "-" + sec;
        
    }
}
