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
package org.kuali.kfs.module.tem.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.service.AccountingDistributionService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.DocumentService;

@ConfigureContext(session = khuntley)
public class AccountingDistributionServiceTest extends KualiTestBase {
    private static final int EXPENSE_AMOUNT = 100;
    private TravelReimbursementDocument tr = null;
    private TravelerDetail traveler = null;

    private AccountingDistributionService adService;
    private DocumentService documentService;

    private static final Logger LOG = Logger.getLogger(AccountingDistributionServiceTest.class);

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        // setup services
        adService = SpringContext.getBean(AccountingDistributionService.class);
        documentService = SpringContext.getBean(DocumentService.class);

        tr = DocumentTestUtils.createDocument(documentService, TravelReimbursementDocument.class);
        documentService.prepareWorkflowDocument(tr);

        // setup traveler
        traveler = new TravelerDetail() {
            @Override
            public void refreshReferenceObject(String referenceObjectName) {
                // do nothing
            }
        };
        traveler.setTravelerTypeCode(TemConstants.EMP_TRAVELER_TYP_CD);
        traveler.setCustomer(new Customer());
        tr.setTraveler(traveler);
    }

    /**
     * This method tests {@link TravelReimbursementService#buildDistributionFrom(TravelReimbursementDocument)}
     */
    @Test
    public void testBuildDistributionFrom() {
        boolean success = false;

        try {
            adService.buildDistributionFrom(tr);
            success = true;
        }
        catch (NullPointerException e) {
            success = false;
            LOG.warn("NPE.", e);
        }

        assertFalse(success);

        TripType tripType = new TripType();
        tripType.setCode("OUT");
        tr.setTripType(tripType);

        try {
            adService.buildDistributionFrom(tr);
            success = true;
        }
        catch (NullPointerException e) {
            success = false;
            LOG.warn("NPE.", e);
        }

        assertTrue(success);
    }
}
