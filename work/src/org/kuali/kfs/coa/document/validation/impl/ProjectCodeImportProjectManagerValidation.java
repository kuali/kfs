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
package org.kuali.kfs.coa.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ProjectCodeImportDetail;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.MassImportLineBase;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * GenericValidation to validate project manager information
 */
public class ProjectCodeImportProjectManagerValidation extends GenericValidation {
    private MassImportLineBase importedLineForValidation;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        return validateProjectManager();
    }


    private boolean validateProjectManager() {
        boolean valid = true;
        ProjectCodeImportDetail projectCodeImportDetail = (ProjectCodeImportDetail) importedLineForValidation;
        String errorPrefix = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSPropertyConstants.ProjectCodeImport.PROJECT_CODE_IMPORT_DETAILS + "[" + importedLineForValidation.getSequenceNumber() == null ? String.valueOf(0) : String.valueOf(importedLineForValidation.getSequenceNumber().intValue() - 1) + "].";

        if (StringUtils.isBlank(projectCodeImportDetail.getProjectManagerUniversalId())) {
            GlobalVariables.getMessageMap().putError(errorPrefix + KFSPropertyConstants.ProjectCodeImport.PROJECT_MANAGER_PRINCIPAL_NAME, KFSKeyConstants.ERROR_PROJECTCODEIMPORT_PROJECTMANAGER_INVALID, new String[] { importedLineForValidation.getSequenceNumber() == null ? "" : importedLineForValidation.getSequenceNumber().toString(), projectCodeImportDetail.getProjectManagerPrincipalName()});
            valid = false;
        }
        return valid;
    }


    /**
     * Gets the importedLineForValidation attribute.
     *
     * @return Returns the importedLineForValidation.
     */
    public MassImportLineBase getImportedLineForValidation() {
        return importedLineForValidation;
    }

    /**
     * Sets the importedLineForValidation attribute value.
     *
     * @param importedLineForValidation The importedLineForValidation to set.
     */
    public void setImportedLineForValidation(MassImportLineBase importedLineForValidation) {
        this.importedLineForValidation = importedLineForValidation;
    }


}
