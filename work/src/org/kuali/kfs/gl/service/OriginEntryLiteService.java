/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.gl.service;

import java.util.Iterator;

import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntryLite;

/**
 * An interface declaring methods that interact with OriginEntryLite objects.  OriginEntryLite objects
 * hold all the base data of OriginEntry, but don't have any references, which means that the O/RM layer
 * can read them much quicker.  Most of the OriginEntryFull methods are covered by OriginEntryService.
 */
public interface OriginEntryLiteService {
    /**
     * Return all the entries in a specific group
     * 
     * @param oeg Group used to select entries
     * @return Iterator to all the entires
     */
    public Iterator<OriginEntryLite> getEntriesByGroup(OriginEntryGroup oeg);

    /**
     * Return all the entries for a specific document in a specific group
     * 
     * @param oeg the origin entry group to find entries in
     * @param documentNumber the document number of origin entries to return
     * @param documentTypeCode the document type code of origin entries to return
     * @param originCode the origination code to return
     * @return iterator to all qualifying entries
     */
    public Iterator<OriginEntryLite> getEntriesByDocument(OriginEntryGroup oeg, String documentNumber, String documentTypeCode, String originCode);

    /**
     * Saves an origin entry lite object to the database
     * 
     * @param entry an entry to save
     */
    public void save(OriginEntryLite entry);

    /**
     * Deletes an origin entry record from the database
     * 
     * @param entry the entry to delete
     */
    public void delete(OriginEntryLite entry);
}
