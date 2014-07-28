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

import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.businessobject.ErrorCertification;
import org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * The unit tests for methods in the ErrorCertificationValidation class using a Salary Expense Transfer document.
 *
 * @see org.kuali.kfs.module.ld.document.validation.impl.ErrorCertificationValidation
 */
@ConfigureContext
public class ErrorCertificationValidationTest extends KualiTestBase {
    private SalaryExpenseTransferDocument stDocument;
    private ErrorCertificationValidation errorCertificationTabValidation;
    private MyAttributedDocumentEvent event;
    private ErrorCertification errorCertification;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        stDocument = new SalaryExpenseTransferDocument();
        event = new MyAttributedDocumentEvent(stDocument);
        errorCertificationTabValidation = new ErrorCertificationValidation();
        errorCertification = new ErrorCertification();
        errorCertification.setDocumentNumber("1");
        stDocument.setErrorCertification(errorCertification);
    }

    public void testPartiallyFullErrorCertification() {
        setUpErrorCertification(false);

        errorCertificationTabValidation.validate(event);

        boolean hasError = GlobalVariables.getMessageMap().doesPropertyHaveError(LaborKeyConstants.ErrorCertification.ERROR_ERROR_CERT_KEY);
        assertTrue("Error Certification Tab isn't required, but should be.", hasError);
    }

    public void testFullErrorCertification() {
        setUpErrorCertification(true);

        errorCertificationTabValidation.validate(event);

        boolean hasError = GlobalVariables.getMessageMap().doesPropertyHaveError(LaborKeyConstants.ErrorCertification.ERROR_ERROR_CERT_KEY);
        assertFalse("Unexpected requirement of Error Certification Tab.", hasError);
    }

    protected void setUpErrorCertification(boolean isCompleted) {
        if (isCompleted) {
            errorCertification.setErrorCorrectionReason("test reason");
            errorCertification.setErrorDescription("test desc");
            errorCertification.setExpenditureDescription("test description");
            errorCertification.setExpenditureProjectBenefit("test benefit");
        }
        else {
            errorCertification.setErrorCorrectionReason("test reason");
            errorCertification.setErrorDescription("test desc");
            errorCertification.setExpenditureDescription("");
            errorCertification.setExpenditureProjectBenefit("");
        }
    }

    static class MyAttributedDocumentEvent extends AttributedDocumentEventBase {
        public MyAttributedDocumentEvent(SalaryExpenseTransferDocument d) {
            super("", "", d);
        }
    }
}
