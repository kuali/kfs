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
package org.kuali.kfs.module.cab.batch.service;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.cab.batch.PreAssetTaggingStep;
import org.kuali.kfs.module.cab.businessobject.Pretag;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.ProxyUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;

public class PreAssetTaggingStepTest extends BatchTestBase {
    private PreAssetTaggingStep preAssetTaggingStep;
    private DateTimeService dateTimeService;

    @Override
    @ConfigureContext(session = UserNameFixture.khuntley, shouldCommitTransactions = false)
    protected void setUp() throws Exception {
        super.setUp();
        preAssetTaggingStep = (PreAssetTaggingStep) ProxyUtils.getTargetIfProxied( SpringContext.getBean(PreAssetTaggingStep.class) );
        dateTimeService = SpringContext.getBean(DateTimeService.class);
    }

    public void testExecute() throws Exception {
        java.sql.Date currentSqlDate = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        preAssetTaggingStep.execute("testPreAssetTaggingExtractStep", dateTimeService.getCurrentDate());
        Collection<Pretag> match = findByPO("21");
        assertEquals(0, match.size());

        match = findByPO("22");
        assertEquals(2, match.size());

        match = findByPO("23");
        assertEquals(2, match.size());

        // assert the extract date value
        SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
        assertEquals(fmt.format(currentSqlDate), findPretagExtractDateParam().getValue());
    }

    private Collection<Pretag> findByPO(String poNumber) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("purchaseOrderNumber", poNumber);
        Collection<Pretag> match = SpringContext.getBean(BusinessObjectService.class).findMatching(Pretag.class, fieldValues);
        assertNotNull(match);
        return match;
    }
}
