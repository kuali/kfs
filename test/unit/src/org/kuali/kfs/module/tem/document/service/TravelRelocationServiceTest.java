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
import org.junit.After;
import org.junit.Before;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.document.TravelRelocationDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.DocumentService;

@ConfigureContext(session = khuntley)
public class TravelRelocationServiceTest extends KualiTestBase{
    private TravelRelocationDocument relo = null;
    private TravelerDetail traveler = null;
    private TravelRelocationService reloService;
    private DocumentService documentService;

    private static final Logger LOG = Logger.getLogger(TravelRelocationServiceTest.class);

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

     // setup services
        reloService = SpringContext.getBean(TravelRelocationService.class);
        documentService = SpringContext.getBean(DocumentService.class);

        relo = DocumentTestUtils.createDocument(documentService, TravelRelocationDocument.class);
        documentService.prepareWorkflowDocument(relo);

        // setup traveler
        traveler = new TravelerDetail() {
            @Override
            public void refreshReferenceObject(String referenceObjectName) {
                // do nothing
            }
        };
        traveler.setTravelerTypeCode(TemConstants.EMP_TRAVELER_TYP_CD);
        traveler.setCustomer(new Customer());
        relo.setTraveler(traveler);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        reloService = null;
        super.tearDown();
    }
}
