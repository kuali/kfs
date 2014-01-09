/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.identity;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

@ConfigureContext
public class TravelerServiceTest extends KualiTestBase {

    public static Logger LOG = Logger.getLogger(TravelerServiceTest.class);

    private TravelerService travelerService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        travelerService = SpringContext.getBean(TravelerService.class);
    }

    public final void testIsParentOrg() {

        // BA ACPR reports to BA ACAC
        // BA ADAF reports to BL DBFA

        LOG.debug("testing no qualifications");
        assertFalse(travelerService.isParentOrg(null, null, "BA", "ACPR", true));

        LOG.debug("testing no roles");
        assertFalse(travelerService.isParentOrg("BA", "ACPR", null, null, true));

        LOG.debug("testing no organization code role");
        assertTrue(travelerService.isParentOrg("BA", "ACPR", "BA", null, true));

        LOG.debug("testing no org code role with chart code child of role chart code");
        assertTrue(travelerService.isParentOrg("BA", "ADAF", "BL", null, true));

        LOG.debug("testing chart and org codes the same as the rold");
        assertTrue(travelerService.isParentOrg("BA", "ACPR", "BA", "ACPR", true));

        LOG.debug("testing the chart and org codes are the children of the role");
        assertTrue(travelerService.isParentOrg("BA", "ADAF", "BL", "DBFA", true));

    }
}

