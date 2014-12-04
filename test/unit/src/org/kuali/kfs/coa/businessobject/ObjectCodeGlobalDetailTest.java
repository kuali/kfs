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

