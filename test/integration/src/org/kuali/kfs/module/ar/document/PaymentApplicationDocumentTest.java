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
package org.kuali.module.ar.document;

import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import org.kuali.core.document.Document;
import org.kuali.core.service.DocumentService;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.test.ConfigureContext;

@ConfigureContext(session = KHUNTLEY)
public class PaymentApplicationDocumentTest extends KualiTestBase {

    public void testCreatePaymentApplicationDocument() throws Exception {
        DocumentService service = SpringContext.getBean(DocumentService.class);
        PaymentApplicationDocument document = (PaymentApplicationDocument) service.getNewDocument(PaymentApplicationDocument.class);
        document.getDocumentHeader().setFinancialDocumentDescription("Testing");
        service.saveDocument(document);
    }
    
}
