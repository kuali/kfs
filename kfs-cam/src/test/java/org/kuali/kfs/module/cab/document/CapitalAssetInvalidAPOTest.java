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
package org.kuali.kfs.module.cab.document;

import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapRuleConstants;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentFixture;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentWithCommodityCodeFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.core.api.config.property.ConfigurationService;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

/**
 * This class is used to create and test populated Requisition Documents
 * that are not eligible to become APO because of capital asset rules.
 *
 * The other criteria for APO ineligibility are tested in NegativeAPOTest
 * in the PurAp module.
 * 
 * @see org.kuali.kfs.module.purap.document.NegativeAPOTest
 */
@ConfigureContext(session = khuntley)
public class CapitalAssetInvalidAPOTest extends KualiTestBase {

    private RequisitionService reqService;
    private ConfigurationService kualiConfigurationService;

    private RequisitionDocument requisitionDocument = null;

    protected void setUp() throws Exception {
        super.setUp();
        if (null == reqService) {
            reqService = SpringContext.getBean(RequisitionService.class);
        }
        if (null == kualiConfigurationService) {
            kualiConfigurationService = SpringContext.getBean(ConfigurationService.class);
        }
    }

    protected void tearDown() throws Exception {
        requisitionDocument = null;
        super.tearDown();
    }

    // Requisition has failed capital asset rules.
    public void testInvalidAPOCapitalAssetFailure() throws Exception {
        RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_APO_INVALID_FAILS_CAPITAL_ASSET_RULE.createRequisitionDocument();
        assertFalse(reqService.isAutomaticPurchaseOrderAllowed(requisitionDocument));
        if (requisitionDocument.getNotes() != null && requisitionDocument.getNotes().size() > 0) {
            String reason = kualiConfigurationService.getPropertyValueAsString(PurapKeyConstants.NON_APO_REQUISITION_ACCT_LINE_CAPITAL_OBJ_LEVEL);
            assertTrue(requisitionDocument.getNote(0).getNoteText().indexOf(reason) >=0);
        }
    }
    
}

