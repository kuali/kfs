/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject.options;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.kuali.kfs.module.tem.service.AgencyEntryGroupService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.DateTimeService;

public class AgencyCorrectionGroupEntriesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyLabelPair> getKeyValues() {
        List activeLabels = new ArrayList();

        AgencyEntryGroupService agencyEntryGroupService = SpringContext.getBean(AgencyEntryGroupService.class);
        File[] fileList = agencyEntryGroupService.getAllFileInBatchDirectory();

        if(null != fileList) {
            List<File> sortedFileList = Arrays.asList(fileList);
            for (File file : sortedFileList) {
                String fileName = file.getName();

                // build display file name with date and size
                Date date = new Date(file.lastModified());
                String timeInfo = "(" + SpringContext.getBean(DateTimeService.class).toDateTimeString(date) + ")";
                String sizeInfo = "(" + (new Long(file.length())).toString() + ")";

                activeLabels.add(new KeyLabelPair(fileName, timeInfo + " " + fileName + " " + sizeInfo));
            }
        }
        
        //Collections.sort(sortedFileList, new OriginEntryFileComparator());

        

        return activeLabels;
    }

}
