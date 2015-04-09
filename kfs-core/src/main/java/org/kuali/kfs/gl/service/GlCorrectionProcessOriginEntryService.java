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

import java.util.List;

import org.kuali.kfs.gl.businessobject.OriginEntryFull;

/**
 * Implementations of this interface are used to store a list of origin entries to be used by the GLCP. These persisted entries are
 * not stored permanently, but are stored for a period of time during the page views of a GLCP document. This is similar to a HTTP
 * session in that origin entries can be stored, but data can be cleared out after a specific lifetime.
 */
public interface GlCorrectionProcessOriginEntryService {


    /**
     * Retrieves the origin entries stored under the given sequence number
     * 
     * @param glcpSearchResuiltsSequenceNumber a sequence number
     * @return a list of origin entries, or null if no results are currently not in the system.
     * @throws Exception thrown if something goes wrong
     */
    public List<OriginEntryFull> retrieveAllEntries(String glcpSearchResuiltsSequenceNumber) throws Exception;

    /**
     * Persists the origin entries under a given sequence number. If entries are persisted again under the same sequence number,
     * then they will be overridden.
     * 
     * @param glcpSearchResuiltsSequenceNumber a sequence number
     * @param allEntries a list of origin entries
     * @throws Exception thrown if anything goes wrong
     */
    public void persistAllEntries(String glcpSearchResuiltsSequenceNumber, List<OriginEntryFull> allEntries) throws Exception;
}
