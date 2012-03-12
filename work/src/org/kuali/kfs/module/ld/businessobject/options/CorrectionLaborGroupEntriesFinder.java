/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.businessobject.options;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.kuali.kfs.gl.web.util.OriginEntryFileComparator;
import org.kuali.kfs.module.ld.service.LaborOriginEntryGroupService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * Returns list of Labor origin entry filenames
 */
public class CorrectionLaborGroupEntriesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyValue> getKeyValues() {
        List<KeyValue> activeLabels = new ArrayList<KeyValue>();

        LaborOriginEntryGroupService originEntryGroupService = SpringContext.getBean(LaborOriginEntryGroupService.class);
        ;
           
        File[] fileList = originEntryGroupService.getAllFileInBatchDirectory();

        List<File> sortedFileList = Arrays.asList(fileList);
        Collections.sort(sortedFileList, new OriginEntryFileComparator());

        for (File file : sortedFileList) {
            String fileName = file.getName();

            // build display file name with date and size
            Date date = new Date(file.lastModified());
            String timeInfo = "(" + SpringContext.getBean(DateTimeService.class).toDateTimeString(date) + ")";
            String sizeInfo = "(" + (new Long(file.length())).toString() + ")";

            activeLabels.add(new ConcreteKeyValue(fileName, timeInfo + " " + fileName + " " + sizeInfo));
        }

        return activeLabels;
    }
}
