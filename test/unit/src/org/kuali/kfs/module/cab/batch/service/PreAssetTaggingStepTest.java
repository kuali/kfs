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
