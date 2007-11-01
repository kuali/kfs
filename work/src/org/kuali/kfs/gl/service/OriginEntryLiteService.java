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
     * @param oeg Group selection
     * @param documentNumber Document number selection
     * @param documentTypeCode Document type selection
     * @param originCode Origin Code selection
     * @return iterator to all the entries
     */
    public Iterator<OriginEntryLite> getEntriesByDocument(OriginEntryGroup oeg, String documentNumber, String documentTypeCode, String originCode);

    public void save(OriginEntryLite entry);

    public void delete(OriginEntryLite entry);
}
