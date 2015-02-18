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
