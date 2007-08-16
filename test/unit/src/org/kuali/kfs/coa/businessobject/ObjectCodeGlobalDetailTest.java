/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.chart.bo;

import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DocumentService;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.financial.document.TransferOfFundsDocument;
import org.kuali.test.ConfigureContext;
import org.kuali.test.DocumentTestUtils;

@ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
public class ObjectCodeGlobalDetailTest extends KualiTestBase {
    private AccountingDocument document;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        document = DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        SpringContext.getBean(DocumentService.class).saveDocument(document);
    }

    public void testSave() {

        ObjectCodeGlobalDetail detail = new ObjectCodeGlobalDetail();
        ObjectCodeGlobal doc = new ObjectCodeGlobal();

        doc.setDocumentNumber(document.getDocumentNumber());

        detail.setDocumentNumber(document.getDocumentNumber());
        detail.setUniversityFiscalYear(document.getPostingYear());
        detail.setChartOfAccountsCode("BL");

        List<ObjectCodeGlobalDetail> list = new ArrayList<ObjectCodeGlobalDetail>();
        list.add(detail);
        doc.setObjectCodeGlobalDetails(list);

        SpringContext.getBean(BusinessObjectService.class).save(doc);
    }

}
