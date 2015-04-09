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
