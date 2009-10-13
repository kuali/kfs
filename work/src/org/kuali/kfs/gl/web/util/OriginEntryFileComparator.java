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
package org.kuali.kfs.gl.web.util;

import java.io.File;
import java.util.Comparator;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * An implementation of Comparator which compares origin entry Files by their name prefix and then by last modified date
 */
public class OriginEntryFileComparator implements Comparator<File> {

    public int compare(File o1, File o2) {
        String fileName1 = o1.getName();
        String fileName2 = o2.getName();

        // remove date from name
        fileName1 = StringUtils.substringBefore(fileName1, ".");
        fileName2 = StringUtils.substringBefore(fileName2, ".");

        int c = fileName1.compareTo(fileName2);
        if (c != 0) {
            return c;
        }

        Date fileDate1 = new Date(o1.lastModified());
        Date fileDate2 = new Date(o2.lastModified());

        return fileDate1.compareTo(fileDate2);
    }

}
