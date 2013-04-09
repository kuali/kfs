/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.purap.businessobject.PurApItemUseTax;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentFixture;
import org.kuali.kfs.module.purap.fixture.TaxFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.TaxRegion;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.coreservice.impl.parameter.ParameterBo;
import org.kuali.rice.krad.service.BusinessObjectService;

@ConfigureContext(session = khuntley)
public class PurapServiceTest extends KualiTestBase {
    private static final Logger LOG = Logger.getLogger(PurapServiceTest.class);

    private Date currentDate;
    private Date compareDate;
    private int dayOffset = 60;
    private DateTimeService dateTimeService;
    private BusinessObjectService businessObjectService;
    private ParameterService parameterService;
    private String NAMESPACE = "KFS-PURAP";

    private static final String[] TAXABLE_PARM_NMs = { "TAXABLE_DELIVERY_STATES", "TAXABLE_FUND_GROUPS_FOR_NON_TAXABLE_STATES", "TAXABLE_FUND_GROUPS_FOR_TAXABLE_STATES", "TAXABLE_OBJECT_CONSOLIDATIONS_FOR_NON_TAXABLE_STATES", "TAXABLE_OBJECT_CONSOLIDATIONS_FOR_TAXABLE_STATES", "TAXABLE_OBJECT_LEVELS_FOR_NON_TAXABLE_STATES", "TAXABLE_OBJECT_LEVELS_FOR_TAXABLE_STATES", "TAXABLE_SUB_FUND_GROUPS_FOR_NON_TAXABLE_STATES", "TAXABLE_SUB_FUND_GROUPS_FOR_TAXABLE_STATES" };

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        currentDate = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        Calendar calendar = SpringContext.getBean(DateTimeService.class).getCurrentCalendar();
        calendar.add(Calendar.DATE, dayOffset);
        compareDate = new Date(calendar.getTimeInMillis());
        dateTimeService = SpringContext.getBean(DateTimeService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        parameterService = SpringContext.getBean(ParameterService.class);

        enableParameterConstraints(true);
    }

    @Override
    protected void tearDown() throws Exception {
        currentDate = null;
        compareDate = null;
        super.tearDown();
    }

    public void testIsDateMoreThanANumberOfDaysAway_ManyFewerDays() {
        int daysAway = dayOffset - 5;
        assertTrue(SpringContext.getBean(PurapService.class).isDateMoreThanANumberOfDaysAway(compareDate, daysAway));
    }

    public void testIsDateMoreThanANumberOfDaysAway_OneLessDays() {
        int daysAway = dayOffset - 1;
        assertTrue(SpringContext.getBean(PurapService.class).isDateMoreThanANumberOfDaysAway(compareDate, daysAway));
    }

    public void testIsDateMoreThanANumberOfDaysAway_SameNumberOfDays() {
        int daysAway = dayOffset;
        assertFalse(SpringContext.getBean(PurapService.class).isDateMoreThanANumberOfDaysAway(compareDate, daysAway));
    }

    public void testIsDateMoreThanANumberOfDaysAway_OneMoreDays() {
        int daysAway = dayOffset + 1;
        assertFalse(SpringContext.getBean(PurapService.class).isDateMoreThanANumberOfDaysAway(compareDate, daysAway));
    }

    public void testIsDateMoreThanANumberOfDaysAway_ManyMoreDays() {
        int daysAway = dayOffset + 5;
        assertFalse(SpringContext.getBean(PurapService.class).isDateMoreThanANumberOfDaysAway(compareDate, daysAway));
    }

    public void testSalesTaxHappyPath() {
        try {
            TaxRegion taxRegionPostalCode = TaxFixture.TaxRegionFixture.TAX_REGION_NO_USE_TAX.createTaxRegion(new TaxFixture.TaxRegionRateFixture[] { TaxFixture.TaxRegionRateFixture.TAX_REGION_RATE_05 }, new TaxFixture.TaxRegionPostalCodeFixture[] { TaxFixture.TaxRegionPostalCodeFixture.PO_46202, TaxFixture.TaxRegionPostalCodeFixture.PO_46202_SHORT }, null);
            businessObjectService.save(taxRegionPostalCode);
        } catch (Exception e) {
            // in case the database has the missing data added back in, I do not want this fix to break the test. This will allow the the test
            // to recover if the data mysteriously gets back to the previous state
            try {
                TaxRegion taxRegionPostalCode = TaxFixture.TaxRegionFixture.TAX_REGION_NO_USE_TAX.createTaxRegion(new TaxFixture.TaxRegionRateFixture[] { TaxFixture.TaxRegionRateFixture.TAX_REGION_RATE_05 }, new TaxFixture.TaxRegionPostalCodeFixture[] { TaxFixture.TaxRegionPostalCodeFixture.PO_46202 }, null);
                businessObjectService.save(taxRegionPostalCode);
            } catch (Exception e2) {
                // consume
            }
        }
        RequisitionDocument reqDoc = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocumentForTax(TaxFixture.TaxTestCaseFixture.SalesTaxHappyPathTest);
        SpringContext.getBean(PurapService.class).calculateTax(reqDoc);

        assertEquals(new KualiDecimal("0.05"), reqDoc.getItem(0).getItemTaxAmount());
        assertEquals(new KualiDecimal("1.05"), reqDoc.getItem(0).getTotalAmount());
    }

    public void testUseTaxHappyPath() {
        TaxRegion taxRegionState = TaxFixture.TaxRegionFixture.TAX_REGION_WITH_USE_TAX.createTaxRegion(new TaxFixture.TaxRegionRateFixture[] { TaxFixture.TaxRegionRateFixture.TAX_REGION_RATE_07 }, null, new TaxFixture.TaxRegionStateFixture[] { TaxFixture.TaxRegionStateFixture.IN });
        businessObjectService.save(taxRegionState);

        RequisitionDocument reqDoc = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocumentForTax(TaxFixture.TaxTestCaseFixture.UseTaxHappyPathTest);

        SpringContext.getBean(PurapService.class).calculateTax(reqDoc);

        assertNotNull(reqDoc.getItem(0).getUseTaxItems());
        assertEquals(1, reqDoc.getItem(0).getUseTaxItems().size());

        PurApItemUseTax itemUseTax = reqDoc.getItem(0).getUseTaxItems().get(0);
        assertEquals(itemUseTax.getAccountNumber(), "6044900");
        assertEquals(itemUseTax.getChartOfAccountsCode(), "BA");
        assertEquals(itemUseTax.getFinancialObjectCode(), "1500");
        assertEquals(itemUseTax.getRateCode(), "USETAX");
        assertEquals(itemUseTax.getTaxAmount(), new KualiDecimal("0.07"));
    }

    public void testSalesTaxWithItemTypeNotTaxable() {

        TaxRegion taxRegionPostalCode = TaxFixture.TaxRegionFixture.TAX_REGION_NO_USE_TAX.createTaxRegion(new TaxFixture.TaxRegionRateFixture[] { TaxFixture.TaxRegionRateFixture.TAX_REGION_RATE_05 }, new TaxFixture.TaxRegionPostalCodeFixture[] { TaxFixture.TaxRegionPostalCodeFixture.PO_46202 }, null);
        businessObjectService.save(taxRegionPostalCode);

        RequisitionDocument reqDoc = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocumentForTax(TaxFixture.TaxTestCaseFixture.SalesTaxItemTypeNotTaxableTest);
        SpringContext.getBean(PurapService.class).calculateTax(reqDoc);

        assertNull(reqDoc.getItem(0).getItemTaxAmount());
        assertEquals(reqDoc.getItem(0).getTotalAmount(), new KualiDecimal("1.00"));
    }

    public void testSalesTaxWithItemTaxFieldNull() {

        TaxRegion taxRegionPostalCode = TaxFixture.TaxRegionFixture.TAX_REGION_NO_USE_TAX.createTaxRegion(new TaxFixture.TaxRegionRateFixture[] { TaxFixture.TaxRegionRateFixture.TAX_REGION_RATE_05 }, new TaxFixture.TaxRegionPostalCodeFixture[] { TaxFixture.TaxRegionPostalCodeFixture.PO_46202 }, null);
        businessObjectService.save(taxRegionPostalCode);

        RequisitionDocument reqDoc = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocumentForTax(TaxFixture.TaxTestCaseFixture.SalesTaxItemTaxFieldNullTest);
        SpringContext.getBean(PurapService.class).calculateTax(reqDoc);

        assertNotNull(reqDoc.getItem(0).getItemTaxAmount());
        assertEquals(new KualiDecimal("100.00"), reqDoc.getItem(0).getItemTaxAmount());
        assertEquals(new KualiDecimal("101.00"), reqDoc.getItem(0).getTotalAmount());

    }

    public void testSalesTaxWithCommodityCodeNull() throws Exception {
        enableParameterConstraints(false);
        TaxRegion taxRegionPostalCode = TaxFixture.TaxRegionFixture.TAX_REGION_NO_USE_TAX.createTaxRegion(new TaxFixture.TaxRegionRateFixture[] { TaxFixture.TaxRegionRateFixture.TAX_REGION_RATE_05 }, new TaxFixture.TaxRegionPostalCodeFixture[] { TaxFixture.TaxRegionPostalCodeFixture.PO_46202 }, null);
        businessObjectService.save(taxRegionPostalCode);

        RequisitionDocument reqDoc = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocumentForTax(TaxFixture.TaxTestCaseFixture.SalesTaxCommodityCodeNullTest);
        SpringContext.getBean(PurapService.class).calculateTax(reqDoc);

        assertNull(reqDoc.getItem(0).getItemTaxAmount());
        assertEquals(new KualiDecimal("1.00"), reqDoc.getItem(0).getTotalAmount());
        enableParameterConstraints(true);
    }

    public void testSalesTaxWithDeliveryStateNotTaxable() {
        try {
            TaxRegion taxRegionPostalCode = TaxFixture.TaxRegionFixture.TAX_REGION_NO_USE_TAX.createTaxRegion(new TaxFixture.TaxRegionRateFixture[] { TaxFixture.TaxRegionRateFixture.TAX_REGION_RATE_05 }, new TaxFixture.TaxRegionPostalCodeFixture[] { TaxFixture.TaxRegionPostalCodeFixture.PO_46202, TaxFixture.TaxRegionPostalCodeFixture.PO_46202_SHORT }, null);
            businessObjectService.save(taxRegionPostalCode);
        } catch (Exception e) {
            // in case the database has the missing data added back in, I do not want this fix to break the test. This will allow the the test
            // to recover if the data mysteriously gets back to the previous state
            try {
                TaxRegion taxRegionPostalCode = TaxFixture.TaxRegionFixture.TAX_REGION_NO_USE_TAX.createTaxRegion(new TaxFixture.TaxRegionRateFixture[] { TaxFixture.TaxRegionRateFixture.TAX_REGION_RATE_05 }, new TaxFixture.TaxRegionPostalCodeFixture[] { TaxFixture.TaxRegionPostalCodeFixture.PO_46202 }, null);
                businessObjectService.save(taxRegionPostalCode);
            } catch (Exception e2) {
                // consume
            }
        }
        boolean isExists = SpringContext.getBean(ParameterService.class).parameterExists(KfsParameterConstants.PURCHASING_DOCUMENT.class, "TAXABLE_DELIVERY_STATES");

        RequisitionDocument reqDoc = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocumentForTax(TaxFixture.TaxTestCaseFixture.SalesTaxDeliveryStateExemptTest);
        SpringContext.getBean(PurapService.class).calculateTax(reqDoc);

        assertEquals(new KualiDecimal("0.05"), reqDoc.getItem(0).getItemTaxAmount());
        assertEquals(new KualiDecimal("1.05"), reqDoc.getItem(0).getTotalAmount());
    }

    public void testSalesTaxDeliveryStateExemptWithNonTaxableFund() {
        TaxRegion taxRegionPostalCode = TaxFixture.TaxRegionFixture.TAX_REGION_NO_USE_TAX.createTaxRegion(new TaxFixture.TaxRegionRateFixture[] { TaxFixture.TaxRegionRateFixture.TAX_REGION_RATE_05 }, new TaxFixture.TaxRegionPostalCodeFixture[] { TaxFixture.TaxRegionPostalCodeFixture.PO_46202 }, null);
        businessObjectService.save(taxRegionPostalCode);

        boolean isExists = SpringContext.getBean(ParameterService.class).parameterExists(KfsParameterConstants.PURCHASING_DOCUMENT.class, "TAXABLE_DELIVERY_STATES");

        RequisitionDocument reqDoc = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocumentForTax(TaxFixture.TaxTestCaseFixture.SalesTaxDeliveryStateExemptWithNonTaxableFundTest);
        SpringContext.getBean(PurapService.class).calculateTax(reqDoc);

        assertNull(reqDoc.getItem(0).getItemTaxAmount());
        assertEquals(new KualiDecimal("1.00"), reqDoc.getItem(0).getTotalAmount());
    }

    public void testSalesTaxWithAccountNotTaxable() {
        TaxRegion taxRegionPostalCode = TaxFixture.TaxRegionFixture.TAX_REGION_NO_USE_TAX.createTaxRegion(new TaxFixture.TaxRegionRateFixture[] { TaxFixture.TaxRegionRateFixture.TAX_REGION_RATE_05 }, new TaxFixture.TaxRegionPostalCodeFixture[] { TaxFixture.TaxRegionPostalCodeFixture.PO_46202 }, null);
        businessObjectService.save(taxRegionPostalCode);

        RequisitionDocument reqDoc = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocumentForTax(TaxFixture.TaxTestCaseFixture.SalesTaxAccountNotTaxableTest);
        SpringContext.getBean(PurapService.class).calculateTax(reqDoc);

        assertNull(reqDoc.getItem(0).getItemTaxAmount());
        assertEquals(new KualiDecimal("1.00"), reqDoc.getItem(0).getTotalAmount());
    }

    public void testSalesTaxWithObjectCodeNotTaxable() {
        TaxRegion taxRegionPostalCode = TaxFixture.TaxRegionFixture.TAX_REGION_NO_USE_TAX.createTaxRegion(new TaxFixture.TaxRegionRateFixture[] { TaxFixture.TaxRegionRateFixture.TAX_REGION_RATE_05 }, new TaxFixture.TaxRegionPostalCodeFixture[] { TaxFixture.TaxRegionPostalCodeFixture.PO_46202 }, null);
        businessObjectService.save(taxRegionPostalCode);

        RequisitionDocument reqDoc = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocumentForTax(TaxFixture.TaxTestCaseFixture.SalesTaxObjectCodeNotTaxableTest);
        SpringContext.getBean(PurapService.class).calculateTax(reqDoc);

        assertNull(reqDoc.getItem(0).getItemTaxAmount());
        assertEquals(reqDoc.getItem(0).getTotalAmount(), new KualiDecimal("1.00"));
    }

    public void testUseTaxItemTypeNotTaxable() {
        TaxRegion taxRegionPostalCode = TaxFixture.TaxRegionFixture.TAX_REGION_NO_USE_TAX.createTaxRegion(new TaxFixture.TaxRegionRateFixture[] { TaxFixture.TaxRegionRateFixture.TAX_REGION_RATE_05 }, new TaxFixture.TaxRegionPostalCodeFixture[] { TaxFixture.TaxRegionPostalCodeFixture.PO_46202 }, null);
        businessObjectService.save(taxRegionPostalCode);

        RequisitionDocument reqDoc = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocumentForTax(TaxFixture.TaxTestCaseFixture.UseTaxItemTypeNotTaxableTest);
        SpringContext.getBean(PurapService.class).calculateTax(reqDoc);

        assertEquals(reqDoc.getItem(0).getUseTaxItems().size(), 0);
    }

    public void testSalesTaxWithSalesTaxParamDisabled() {

        TaxRegion taxRegionPostalCode = TaxFixture.TaxRegionFixture.TAX_REGION_NO_USE_TAX.createTaxRegion(new TaxFixture.TaxRegionRateFixture[] { TaxFixture.TaxRegionRateFixture.TAX_REGION_RATE_05 }, new TaxFixture.TaxRegionPostalCodeFixture[] { TaxFixture.TaxRegionPostalCodeFixture.PO_46202 }, null);
        businessObjectService.save(taxRegionPostalCode);

        RequisitionDocument reqDoc = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocumentForTax(TaxFixture.TaxTestCaseFixture.SalesTaxParamDisabledTest);
        SpringContext.getBean(PurapService.class).calculateTax(reqDoc);

        assertNull(reqDoc.getItem(0).getItemTaxAmount());
        assertEquals(reqDoc.getItem(0).getTotalAmount(), new KualiDecimal("1.00"));
    }

    /**
     * This method ensures that the database entries upon which the test was designed have good values. This
     * is safe because whatever changes are made for this test are rolled back before the next test.
     * @param enabled true to set the constraint indicator to Allow; false is Disallow.
     */
    private void enableParameterConstraints(boolean enabled) {
        // apparently, the database values are not what the test writer originally expected. Force the issue for test (it will be rolled back anyway)
        for (String taxableParmName : TAXABLE_PARM_NMs) {
            Map<String, String> fieldValues = new HashMap<String, String>();
            fieldValues.put("namespaceCode", NAMESPACE);
            fieldValues.put("componentCode", "Document");
            fieldValues.put("applicationId", KFSConstants.APPLICATION_NAMESPACE_CODE);
            fieldValues.put("name", taxableParmName);

            ParameterBo parameter = businessObjectService.findByPrimaryKey(ParameterBo.class, fieldValues);
            if (enabled) {
                parameter.setEvaluationOperatorCode("A");
            } else {
                parameter.setEvaluationOperatorCode("D");
            }
            businessObjectService.save(parameter);
        }
    }
}
