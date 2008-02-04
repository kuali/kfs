/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.effort.service;

import org.kuali.module.effort.bo.EffortCertificationDocumentBuild;
import org.kuali.module.effort.document.EffortCertificationDocument;

/**
 * To define the services related to the effort certification document
 * 
 * @see org.kuali.module.effort.document.EffortCertificationDocument
 */
public interface EffortCertificationDocumentService {
    
    /**
     * create an effort certification document from the given document build record
     * 
     * @param documentBuild the given effort certification document build
     */
    public boolean createEffortCertificationDocument(EffortCertificationDocumentBuild effortCertificationDocumentBuild);

    /**
     * generate salary expense transfer document from the given effort certification document
     * 
     * @param effortCertificationDocument the given effort certification document
     * 
     * @return true if the generation is complete successfully; otherwise, return false
     */
    public boolean generateSalaryExpenseTransferDocument(EffortCertificationDocument effortCertificationDocument);

}
