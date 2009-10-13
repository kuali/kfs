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
