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
package org.kuali.kfs.module.tem.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.businessobject.AgencyEntryFull;

public interface AgencyEntryService {
    public void createEntry(AgencyEntryFull agencyEntry, PrintStream ps);

    /**
     * writes out a list of origin entries to an output stream.
     *
     * @param entries an Iterator of entries to save as text
     * @param bw the output stream to write origin entries to
     */
    public void flatFile(Iterator<AgencyEntryFull> entries, BufferedOutputStream bw);

    public Integer getGroupCount(String groupId);

    public Map getEntriesByBufferedReader(BufferedReader inputBufferedReader, List<AgencyEntryFull> agencyEntryList);

    public  Map getEntriesByGroupIdWithPath(String fileNameWithPath, List<AgencyEntryFull> agencyEntryList);

}
