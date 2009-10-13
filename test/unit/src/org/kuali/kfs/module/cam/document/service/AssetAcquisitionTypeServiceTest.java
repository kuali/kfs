/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.cam.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.module.cam.businessobject.AssetAcquisitionType;
import org.kuali.kfs.module.cam.fixture.AssetAcquisitionTypeFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;

public class AssetAcquisitionTypeServiceTest extends KualiTestBase {

    private AssetAcquisitionTypeService assetAcquisitionTypeService;

    @Override
    @ConfigureContext(session = UserNameFixture.khuntley, shouldCommitTransactions = false)
    protected void setUp() throws Exception {
        super.setUp();
        assetAcquisitionTypeService = SpringContext.getBean(AssetAcquisitionTypeService.class);
    }


    /**
     * Test hasIncomeAssetObjectCode
     * 
     * @throws Exception
     */
    @ConfigureContext(session = khuntley, shouldCommitTransactions = false)
    public void testHasIncomeAssetObjectCode_Success() throws Exception {
        // set up the data
        AssetAcquisitionType assetAcquisitionType = AssetAcquisitionTypeFixture.WITH_INCOME_ASSET_OBJECT_CODE.newAssetAcquisitionType();
        assertTrue(assetAcquisitionTypeService.hasIncomeAssetObjectCode(assetAcquisitionType.getAcquisitionTypeCode()));
        
        assetAcquisitionType = AssetAcquisitionTypeFixture.WITHOUT_INCOME_ASSET_OBJECT_CODE.newAssetAcquisitionType();
        assertFalse(assetAcquisitionTypeService.hasIncomeAssetObjectCode(assetAcquisitionType.getAcquisitionTypeCode()));
    }
}

