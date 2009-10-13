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
package org.kuali.kfs.gl.document.web;

import java.util.List;

import org.kuali.kfs.gl.businessobject.OriginEntryFull;


/**
 * This class represents origin entry meta-data for the correction document.
 */
public interface CorrectionDocumentEntryMetadata {

    public List<OriginEntryFull> getAllEntries();

    public boolean getDataLoadedFlag();

    /**
     * Gets the input group ID of the document when it was persisted in the DB
     * 
     * @return the input group ID of the document when it was persisted in the DB
     */
    public String getInputGroupIdFromLastDocumentLoad();

    public boolean isRestrictedFunctionalityMode();

    public boolean getMatchCriteriaOnly();

    public String getEditMethod();
}
