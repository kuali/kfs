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
package org.kuali.kfs.gl.document.dataaccess;

import java.sql.Date;
import java.util.Collection;

import org.kuali.kfs.gl.document.GeneralLedgerCorrectionProcessDocument;

/**
 * A DAO interface that declares methods...or, in this case, a method...for CorrectionDocuments to interact 
 * with the database.
 */
public interface CorrectionDocumentDao {
    /**
     * Returns a Collection of GLCP documents finalized on the given date
     * 
     * @param date the finalization date of GLCP documents to find
     * @return a Collection of GLCP documents
     */
    public Collection<GeneralLedgerCorrectionProcessDocument> getCorrectionDocumentsFinalizedOn(Date date);
}
