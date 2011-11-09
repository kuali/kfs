/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.cam.utils;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.cam.businessobject.BarcodeInventoryErrorDetail;
import org.kuali.kfs.module.cam.document.BarcodeInventoryErrorDocument;
import org.kuali.kfs.module.cam.fixture.BarcodeInventoryErrorDetailPredicateFixture;
import org.kuali.kfs.module.cam.util.BarcodeInventoryErrorDetailPredicate;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.fixture.UserNameFixture;

@ConfigureContext(session = UserNameFixture.kfs)
public class BarcodeInventoryErrorDetailPredicateTest extends KualiTestBase {
    private static Logger LOG = Logger.getLogger(BarcodeInventoryErrorDetailPredicateTest.class);

    /**
     *     
     * test the UpdateAssetInformation
     */
    public void testUpdateAssetInformation() {
        BarcodeInventoryErrorDetail barcodeInventoryErrorDetail;
        BarcodeInventoryErrorDetail barcodeInventoryErrorExpectedDetail;

        BarcodeInventoryErrorDocument barcodeInventoryErrorDocument = BarcodeInventoryErrorDetailPredicateFixture.DATA.getBarcodeInventoryErrorDocument();
        List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetails=BarcodeInventoryErrorDetailPredicateFixture.DATA.getBarcodeInventoryDetail();
        List<BarcodeInventoryErrorDetail> barcodeInventoryErrorExpectedDetails=BarcodeInventoryErrorDetailPredicateFixture.DATA.getExpectedResults();

        BarcodeInventoryErrorDetailPredicate predicatedClosure = new BarcodeInventoryErrorDetailPredicate(barcodeInventoryErrorDocument);

        // searches and replaces
        CollectionUtils.forAllDo(barcodeInventoryErrorDetails, predicatedClosure);

        for(int row=0;row < barcodeInventoryErrorDetails.size();row++) {
            barcodeInventoryErrorDetail = barcodeInventoryErrorDetails.get(row);
            barcodeInventoryErrorExpectedDetail = barcodeInventoryErrorExpectedDetails.get(row);

            assertTrue("Replacement failed.",                    
                    barcodeInventoryErrorDetail.getCampusCode().equals(barcodeInventoryErrorExpectedDetail.getCampusCode()) &&  
                    barcodeInventoryErrorDetail.getBuildingCode().equals(barcodeInventoryErrorExpectedDetail.getBuildingCode()) &&
                    barcodeInventoryErrorDetail.getBuildingRoomNumber().equals(barcodeInventoryErrorExpectedDetail.getBuildingRoomNumber()) && 
                    barcodeInventoryErrorDetail.getBuildingSubRoomNumber().equals(barcodeInventoryErrorExpectedDetail.getBuildingSubRoomNumber()) &&
                    barcodeInventoryErrorDetail.getAssetConditionCode().equals(barcodeInventoryErrorExpectedDetail.getAssetConditionCode()));
        }
    }
}
