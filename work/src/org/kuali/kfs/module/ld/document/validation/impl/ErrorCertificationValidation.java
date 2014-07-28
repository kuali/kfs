/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ld.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.businessobject.ErrorCertification;
import org.kuali.kfs.module.ld.document.ErrorCertifiable;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validation for error certification tab to check if it's incomplete
 */
public class ErrorCertificationValidation extends GenericValidation {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ErrorCertificationValidation.class);

    /**
     * Validates the Error Certification tab.
     *
     * @param event
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        int numBlankFields = errorCertificationBlankFieldsCheck((ErrorCertifiable) event.getDocument());
        if (LOG.isDebugEnabled()) {
            LOG.debug("numBlankFields: " + numBlankFields);
        }

        // if Error Certification tab is partially filled out
        if ((numBlankFields > 0) && (numBlankFields < LaborConstants.ErrorCertification.NUM_ERROR_CERT_FIELDS)) {
            GlobalVariables.getMessageMap().putErrorForSectionId(LaborKeyConstants.ErrorCertification.ERROR_ERROR_CERT_KEY, LaborKeyConstants.ErrorCertification.ERROR_ERROR_CERT_FIELDS_REQ);
            return false;
        }

        return true;
    }

    /**
     * This method goes through all the Error Certification fields looking for blank fields.
     *
     * @param document
     * @return blankFieldCount the number of blank fields in the tab
     */
    public int errorCertificationBlankFieldsCheck(ErrorCertifiable document) {
        int blankFieldCount = 0;

        ErrorCertification ecTab = document.getErrorCertification();

        if (StringUtils.isBlank(ecTab.getExpenditureDescription())) {
            blankFieldCount++;
        }

        if (StringUtils.isBlank(ecTab.getExpenditureProjectBenefit())) {
            blankFieldCount++;
        }

        if (StringUtils.isBlank(ecTab.getErrorDescription())) {
            blankFieldCount++;
        }

        if (StringUtils.isBlank(ecTab.getErrorCorrectionReason())) {
            blankFieldCount++;
        }

        return blankFieldCount;
    }
}
