/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ec.service;

import org.kuali.kfs.module.ec.businessobject.EffortCertificationDocumentBuild;
import org.kuali.kfs.module.ec.document.EffortCertificationDocument;

/**
 * To define the services related to the effort certification document
 * 
 * @see org.kuali.kfs.module.ec.document.EffortCertificationDocument
 */
public interface EffortCertificationDocumentService {

    /**
     * process the approved effort certification document
     * 
     * @param effortCertificationDocument the approved effort certification document
     */
    public void processApprovedEffortCertificationDocument(EffortCertificationDocument effortCertificationDocument);

    /**
     * create an effort certification document from the given document build record, and route it for approval
     * 
     * @param effortCertificationDocumentBuild the given effort certification document build
     */
    public boolean createAndRouteEffortCertificationDocument(EffortCertificationDocumentBuild effortCertificationDocumentBuild);

    /**
     * populate the given effort certification document with the given effort certification document build
     * 
     * @param effortCertificationDocument the given effort certification document
     * @param effortCertificationDocumentBuild the given effort certification document build
     * 
     * @return true if the given document has been populated sucessfully; otherwise, false
     */
    public boolean populateEffortCertificationDocument(EffortCertificationDocument effortCertificationDocument, EffortCertificationDocumentBuild effortCertificationDocumentBuild);

    /**
     * generate salary expense transfer document from the given effort certification document
     * 
     * @param effortCertificationDocument the given effort certification document
     * 
     * @return true if the generation is complete successfully; otherwise, return false
     */
    public boolean generateSalaryExpenseTransferDocument(EffortCertificationDocument effortCertificationDocument);
    
    /**
     * delete the detail lines associated with the given effort certification document if they have been persisted
     * 
     * @param effortCertificationDocument the given effort certification document
     */
    public void removeEffortCertificationDetailLines(EffortCertificationDocument effortCertificationDocument);

    /**
     * add the adHoc route recipient into the given document when the effort on any detail line has been changed.
     * 
     * @param effortCertificationDocument the given effort certification document
     */
    public void addRouteLooping(EffortCertificationDocument effortCertificationDocument);    
}
