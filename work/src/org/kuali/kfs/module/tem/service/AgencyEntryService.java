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
