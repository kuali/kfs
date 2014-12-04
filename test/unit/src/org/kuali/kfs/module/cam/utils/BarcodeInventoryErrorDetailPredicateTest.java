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
