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

import org.kuali.kfs.gl.businessobject.CorrectionCriteria;

/**
 * A DAO interface that 
 */
public interface CorrectionCriteriaDao {

    /**
     * Deletes a correction criterion
     * 
     * @param criterion the criterion to delete
     */
    void delete(CorrectionCriteria criterion);

    /**
     * Returns a list of all the correction criteria associated with the given GLCP document and correction group
     * 
     * @param documentNumber the GLCP document number of correction criteria to find
     * @param correctionGroupLineNumber the correction group of correction criteria to find
     * @return a List of collection criteria
     */
    List findByDocumentNumberAndCorrectionGroupNumber(String documentNumber, Integer correctionGroupLineNumber);
}
