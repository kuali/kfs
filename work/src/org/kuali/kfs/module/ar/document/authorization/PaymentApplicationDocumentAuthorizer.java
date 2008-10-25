/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.authorization;

import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentActionFlags;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kew.exception.WorkflowException;

public class PaymentApplicationDocumentAuthorizer extends FinancialSystemTransactionalDocumentAuthorizerBase {
    private static org.apache.log4j.Logger LOG =
        org.apache.log4j.Logger.getLogger(PaymentApplicationDocumentAuthorizer.class);

    @Override
    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, Person user) {
        FinancialSystemTransactionalDocumentActionFlags flags = 
            super.getDocumentActionFlags(document, user);
        PaymentApplicationDocument paymentApplicationDocument = (PaymentApplicationDocument) document;
        PaymentApplicationDocumentService paymentApplicationDocumentService =
            SpringContext.getBean(PaymentApplicationDocumentService.class);
        CashControlDocument cashControlDocument = null;
        try {
            cashControlDocument =
                paymentApplicationDocumentService.getCashControlDocumentForPaymentApplicationDocument(paymentApplicationDocument);
        } catch(WorkflowException workflowException) {
            LOG.error("Couldn't obtain cash control document for payment application document.", workflowException);
        }

        // KULAR-452
        if(null != cashControlDocument) {
            flags.setCanCancel(false);
        }

        return flags;
    }

}

