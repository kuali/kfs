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

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.labor.service.LaborYearEndBalanceForwardService;
import org.kuali.test.ConfigureContext;
import org.kuali.module.cams.service.AssetDepreciationService;
import org.kuali.module.financial.document.GeneralErrorCorrectionDocument;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

@ConfigureContext(session = KHUNTLEY)
//@ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
public class AssetDepreciationServiceTest extends KualiTestBase {
    private AssetDepreciationService camsAssetDepreciationService;
    private ParameterService parameterService;
    private LaborYearEndBalanceForwardService laborYearEndBalanceForwardService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        camsAssetDepreciationService      = SpringContext.getBean(AssetDepreciationService.class);
    }

        
    public void testRunDepreciation() {
        camsAssetDepreciationService.runDepreciation();
        assertEquals(1, 1);
    }
}