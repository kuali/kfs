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
package org.kuali.kfs.sys.document.validation.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.MassImportLineBase;
import org.kuali.kfs.sys.document.MassImportDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * A validation to have the data dictionary perform its validations upon a business object
 */
public class MassImportLineRequiredValidation extends GenericValidation {
    private static Logger LOG = Logger.getLogger(MassImportLineRequiredValidation.class);
    /**
     * Validates imported line exists in the import document</strong>
     *
     * @see org.kuali.kfs.sys.document.validation.GenericValidation#validate(java.lang.Object[])
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;

        MassImportDocument massImportDocument = (MassImportDocument)event.getDocument();
        List<MassImportLineBase> importedLines = massImportDocument.getImportDetailCollection();

        if (importedLines == null || importedLines.isEmpty()) {
            GlobalVariables.getMessageMap().putError(massImportDocument.getFullErrorPathPrefix(), RiceKeyConstants.ERROR_REQUIRED, KFSConstants.IMPORT_LINE);
            valid = false;
        }

        return valid;
    }
}
