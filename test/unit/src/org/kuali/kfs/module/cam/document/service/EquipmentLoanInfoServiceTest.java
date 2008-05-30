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
package org.kuali.module.cams.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.service.impl.BusinessObjectServiceImpl;
import org.kuali.core.util.DateUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.document.EquipmentLoanOrReturnDocument;
import org.kuali.module.cams.service.impl.EquipmentLoanInfoServiceImpl;

public class EquipmentLoanInfoServiceTest extends KualiTestBase {
    private static final int THREE_DAYS_LATER = 3;
    private static final int TWO_DAYS_LATER = 2;
    private EquipmentLoanInfoServiceImpl equipmentLoanInfoService;
    private Asset asset;

    private static EquipmentLoanOrReturnDocument createEquipmentLoanDoc(int loanDaysToadd, String docStatus) {
        EquipmentLoanOrReturnDocument doc = new EquipmentLoanOrReturnDocument() {
            @Override
            public void refreshReferenceObject(String referenceObjectName) {
            }
        };
        doc.setLoanDate(new java.sql.Date((DateUtils.addDays(new Date(), loanDaysToadd)).getTime()));
        DocumentHeader header = new DocumentHeader();
        header.setFinancialDocumentStatusCode(docStatus);
        doc.setDocumentHeader(header);
        return doc;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        equipmentLoanInfoService = new EquipmentLoanInfoServiceImpl();
        equipmentLoanInfoService.setBusinessObjectService(new BusinessObjectServiceImpl() {
            @Override
            public Collection findMatching(Class clazz, Map fieldValues) {
                List<EquipmentLoanOrReturnDocument> docs = new ArrayList<EquipmentLoanOrReturnDocument>();
                docs.add(createEquipmentLoanDoc(3, KFSConstants.DocumentStatusCodes.APPROVED));
                docs.add(createEquipmentLoanDoc(5, KFSConstants.DocumentStatusCodes.APPROVED));
                docs.add(createEquipmentLoanDoc(5, KFSConstants.DocumentStatusCodes.DISAPPROVED));
                return docs;
            }


        });
        this.asset = new Asset();
        this.asset.setExpectedReturnDate(new java.sql.Date(new Date().getTime()));
    }

    public void testSetEquipmentLoanInfo() throws Exception {
        equipmentLoanInfoService.setEquipmentLoanInfo(asset);
        assertNotNull(asset.getLoanOrReturnInfo());
        assertEquals(DateUtils.clearTimeFields(DateUtils.addDays(new Date(), 5)), DateUtils.clearTimeFields(asset.getLoanOrReturnInfo().getLoanDate()));
    }

}
