/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.coa.businessobject;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.fp.document.TransferOfFundsDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;

@ConfigureContext(session = khuntley, shouldCommitTransactions = true)
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

