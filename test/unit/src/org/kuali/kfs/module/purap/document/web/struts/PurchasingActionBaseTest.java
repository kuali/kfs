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
package org.kuali.kfs.module.purap.document.web.struts;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.krad.util.KRADConstants;

@ConfigureContext(session = khuntley)
public class PurchasingActionBaseTest extends KualiTestBase {


    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testPurchaseOrderRequiresCalculateSalesTaxDisabled() {
        PurchasingActionBase purchasingActionBase = new PurchasingActionBase();
        PurchasingFormBase purForm = setupPurchasingFormBase(false);
        TestUtils.setSystemParameter(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_SALES_TAX_IND,"N");
        assertTrue(purchasingActionBase.requiresCalculate(purForm));
    }

    public void testPurchaseOrderRequiresCalculateSalesTaxEnabled() {
        PurchasingActionBase purchasingActionBase = new PurchasingActionBase();
        PurchasingFormBase purForm = setupPurchasingFormBase(false);
        TestUtils.setSystemParameter(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_SALES_TAX_IND,"Y");
        assertTrue(purchasingActionBase.requiresCalculate(purForm));
    }

    public void testRequisitionRequiresCalculateSalesTaxDisabled() {
        PurchasingActionBase purchasingActionBase = new PurchasingActionBase();
        PurchasingFormBase purForm = setupPurchasingFormBase(true);
        TestUtils.setSystemParameter(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_SALES_TAX_IND,"N");
        assertTrue(purchasingActionBase.requiresCalculate(purForm));
    }

    public void testRequisitionRequiresCalculateSalesTaxEnabled() {
        PurchasingActionBase purchasingActionBase = new PurchasingActionBase();
        PurchasingFormBase purForm = setupPurchasingFormBase(true);
        TestUtils.setSystemParameter(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_SALES_TAX_IND,"Y");
        assertTrue(purchasingActionBase.requiresCalculate(purForm));
    }

    private PurchasingFormBase setupPurchasingFormBase(boolean isRequisition) {
        PurchasingFormBase purForm;
        if (isRequisition) {
            purForm = new RequisitionForm();
        } else {
            purForm = new PurchaseOrderForm();
        }

        Map<String, String> documentActions = new HashMap<String, String>();
        documentActions.put(KRADConstants.KUALI_ACTION_CAN_EDIT, KRADConstants.KUALI_DEFAULT_TRUE_VALUE);
        purForm.setDocumentActions(documentActions);
        purForm.setCalculated(false);

        if (isRequisition) {
            RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_APO_VALID.createRequisitionDocument();
            purForm.setDocument(requisitionDocument);
        }

        return purForm;
    }

}
