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
package org.kuali.kfs.module.cam.document.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.document.EquipmentLoanOrReturnDocument;
import org.kuali.kfs.module.cam.document.service.impl.EquipmentLoanOrReturnServiceImpl;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.kns.service.impl.BusinessObjectServiceImpl;
import org.kuali.rice.kns.util.DateUtils;

public class EquipmentLoanOrReturnServiceTest extends KualiTestBase {
    private static final int THREE_DAYS_LATER = 3;
    private static final int TWO_DAYS_LATER = 2;
    private EquipmentLoanOrReturnServiceImpl equipmentLoanOrReturnService;
    private Asset asset;

    private static EquipmentLoanOrReturnDocument createEquipmentLoanDoc(String docNum, int loanDaysToadd, String docStatus) {
        EquipmentLoanOrReturnDocument doc = new EquipmentLoanOrReturnDocument() {
            @Override
            public void refreshReferenceObject(String referenceObjectName) {
            }
        };
        doc.setLoanDate(new java.sql.Date((DateUtils.addDays(new Date(), loanDaysToadd)).getTime()));
        FinancialSystemDocumentHeader header = new FinancialSystemDocumentHeader();
        header.setFinancialDocumentStatusCode(docStatus);
        doc.setDocumentHeader(header);
        doc.setDocumentNumber(docNum);
        return doc;
    }

    @Override
    @ConfigureContext(session = UserNameFixture.khuntley, shouldCommitTransactions = false)
    protected void setUp() throws Exception {
        super.setUp();
        equipmentLoanOrReturnService = new EquipmentLoanOrReturnServiceImpl();
        equipmentLoanOrReturnService.setBusinessObjectService(new BusinessObjectServiceImpl() {
            @Override
            public Collection findMatching(Class clazz, Map fieldValues) {
                List<EquipmentLoanOrReturnDocument> docs = new ArrayList<EquipmentLoanOrReturnDocument>();
                docs.add(createEquipmentLoanDoc("1", 3, KFSConstants.DocumentStatusCodes.APPROVED));
                docs.add(createEquipmentLoanDoc("2", 5, KFSConstants.DocumentStatusCodes.APPROVED));
                docs.add(createEquipmentLoanDoc("3", 5, KFSConstants.DocumentStatusCodes.DISAPPROVED));
                return docs;
            }
        });
        this.asset = new Asset();
        this.asset.setExpectedReturnDate(new java.sql.Date(new Date().getTime()));
    }

    public void testSetEquipmentLoanInfo() throws Exception {
        equipmentLoanOrReturnService.setEquipmentLoanInfo(asset);
        assertNotNull(asset.getLoanOrReturnInfo());
        assertEquals(DateUtils.clearTimeFields(DateUtils.addDays(new Date(), 5)), DateUtils.clearTimeFields(asset.getLoanOrReturnInfo().getLoanDate()));
    }

}
