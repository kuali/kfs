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
