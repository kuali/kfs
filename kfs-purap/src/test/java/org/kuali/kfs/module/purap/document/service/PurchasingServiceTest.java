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
package org.kuali.kfs.module.purap.document.service;

import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentFixture;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentWithCapitalAssetItemsFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

@ConfigureContext(session = khuntley)
public class PurchasingServiceTest extends KualiTestBase {

    public void testSetupCapitalAssetSystem() {
        RequisitionDocument requisition = RequisitionDocumentWithCapitalAssetItemsFixture.REQ_VALID_ONE_NEW_CAPITAL_ASSET_ITEM.createRequisitionDocument();
        SpringContext.getBean(PurchasingService.class).setupCapitalAssetSystem(requisition);
        assertTrue(requisition.getPurchasingCapitalAssetSystems().size() == 1);
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = false)
    public final void testDefaultUseTaxIndicatorValue(){

        RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_TAX.createRequisitionDocument();
        requisitionDocument.refreshReferenceObject("vendorDetail");

        requisitionDocument.getVendorDetail().setDefaultAddressStateCode(requisitionDocument.getVendorStateCode());
        boolean defaultUseTaxIndicatorValue = SpringContext.getBean(PurchasingService.class).getDefaultUseTaxIndicatorValue(requisitionDocument);
        assertFalse(defaultUseTaxIndicatorValue);
    }

}
