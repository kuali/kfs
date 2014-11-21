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
