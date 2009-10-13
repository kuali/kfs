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
