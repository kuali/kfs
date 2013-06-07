/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsDetail;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;

/**
 * This class tests the ReferralToCollectionsDocument class.
 */
@ConfigureContext(session = khuntley)
public class ReferralToCollectionsDocumentTest extends KualiTestBase {

    private ReferralToCollectionsDocument referralToCollectionsDocument;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        referralToCollectionsDocument = new ReferralToCollectionsDocument();

        List<ReferralToCollectionsDetail> rcDetails = new ArrayList<ReferralToCollectionsDetail>();
        ReferralToCollectionsDetail rcDetail = new ReferralToCollectionsDetail();
        rcDetail.setInvoiceNumber("4295");
        rcDetails.add(rcDetail);
        rcDetail = new ReferralToCollectionsDetail();
        rcDetail.setInvoiceNumber("4275");
        rcDetails.add(rcDetail);
        referralToCollectionsDocument.setReferralToCollectionsDetails(rcDetails);
    }

    /**
     * This method tests deleteReferralToCollectionsDocumentDetail() method of ReferralToCollectionsDocument.
     */
    public void testDeleteReferralToCollectionsDocumentDetail() {
        referralToCollectionsDocument.deleteReferralToCollectionsDetail(1);
        for (ReferralToCollectionsDetail rcDetail : referralToCollectionsDocument.getReferralToCollectionsDetails()) {
            assertNotSame("4275", rcDetail.getInvoiceNumber());
        }
    }

    /**
     * This method tests getReferralToCollectionsDocumentDetail() method of ReferralToCollectionsDocument.
     */
    public void testGetReferralToCollectionsDocumentDetail() {
        ReferralToCollectionsDetail rcDetail = referralToCollectionsDocument.getReferralToCollectionsDetail(1);
        assertEquals("4275", rcDetail.getInvoiceNumber());
    }
}
