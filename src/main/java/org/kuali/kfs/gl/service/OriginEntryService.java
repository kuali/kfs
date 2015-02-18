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
package org.kuali.kfs.gl.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.businessobject.LedgerEntryHolder;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.PosterOutputSummaryEntry;

/**
 * An interface of methods to interact with Origin Entries
 */
public interface OriginEntryService {
    
    public void createEntry(OriginEntryFull originEntry, PrintStream ps);

    /**
     * writes out a list of origin entries to an output stream.
     * 
     * @param entries an Iterator of entries to save as text
     * @param bw the output stream to write origin entries to
     */
    public void flatFile(Iterator<OriginEntryFull> entries, BufferedOutputStream bw);

    /**
     * get the summarized information of the entries that belong to the entry groups with the given group id list
     * 
     * @param groupIdList the origin entry groups
     * @return a set of summarized information of the entries within the specified group
     */
    public LedgerEntryHolder getSummaryByGroupId(Collection groupIdList);
    
    /**
     * get the summarized information of poster input entries that belong to the entry groups with the given group id list
     * 
     * @param groupIdList the origin entry groups
     * @return a map of summarized information of poster input entries within the specified groups
     */
    public Map<String, PosterOutputSummaryEntry> getPosterOutputSummaryByGroupId(Collection groupIdList);

    public Integer getGroupCount(String groupId);
    
    public Map getEntriesByBufferedReader(BufferedReader inputBufferedReader, List<OriginEntryFull> originEntryList);
    
    public  Map getEntriesByGroupIdWithPath(String fileNameWithPath, List<OriginEntryFull> originEntryList);
}
