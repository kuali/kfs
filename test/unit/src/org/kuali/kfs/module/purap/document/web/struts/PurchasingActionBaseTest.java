/*
 * Copyright 2007-2008 The Kuali Foundation
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
