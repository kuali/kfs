/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl.dataaccess;

import java.util.List;

import org.kuali.kfs.gl.businessobject.CorrectionChange;

/**
 * A DAO interface for CorrectionChange business objects to interact with the databse
 */
public interface CorrectionChangeDao {

    /**
     * Surprisingly, this method deletes a GLCP correction
     * 
     * @param spec the GLCP correction to delete
     */
    void delete(CorrectionChange spec);

    /**
     * Finds CorrectionChanges associated with the given document and group
     * 
     * @param documentHeaderId the document number of a GLCP document
     * @param correctionGroupLineNumber the line number of the group within the GLCP document to find correction chagnes for
     * @return a List of correction changes
     */
    List findByDocumentHeaderIdAndCorrectionGroupNumber(String documentHeaderId, Integer correctionGroupLineNumber);
}
