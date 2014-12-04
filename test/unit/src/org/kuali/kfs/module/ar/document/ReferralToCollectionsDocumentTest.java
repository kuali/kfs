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
