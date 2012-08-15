/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.math.BigDecimal;
import java.util.List;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.businessobject.TaxDetail;
import org.kuali.kfs.sys.businessobject.TaxRegion;
import org.kuali.kfs.sys.businessobject.TaxRegionPostalCode;
import org.kuali.kfs.sys.businessobject.TaxRegionRate;
import org.kuali.kfs.sys.businessobject.TaxRegionState;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class tests the Tax Service
 */
@ConfigureContext(session = khuntley)
public class TaxServiceTest extends KualiTestBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TaxServiceTest.class);

    protected TaxService taxService;
    protected DateTimeService dateTimeService;
    protected BusinessObjectService businessObjectService;

    protected final static String DATE_OF_TRANSACTION = "01/02/2008";
    protected final static KualiDecimal AMOUNT = new KualiDecimal(100);
    
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        taxService = SpringContext.getBean(TaxService.class);
        dateTimeService = SpringContext.getBean(DateTimeService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
    }

    /**
     * This method tests that the sales tax details get set correctly
     * 
     * @throws Exception
     */
    public void testGetSalesTaxDetails() throws Exception {

        TaxRegion taxRegion = TaxRegionFixture.TAX_REGION_NO_USE_TAX.createTaxRegion(new TaxRegionRateFixture[] { TaxRegionRateFixture.TAX_REGION_RATE_05 }, new TaxRegionPostalCodeFixture[] { TaxRegionPostalCodeFixture.PO_46113 }, null);

        businessObjectService.save(taxRegion);

        List<TaxDetail> taxDetails = taxService.getSalesTaxDetails(dateTimeService.convertToSqlDate(DATE_OF_TRANSACTION), TaxRegionPostalCodeFixture.PO_46113.postalCode, AMOUNT);
        // there should only be one tax detail
        assertTrue(taxDetails.size() == 1);

        // verify that tax detail was set correctly
        TaxDetail taxDetail = taxDetails.get(0);
        assertTrue(TaxRegionFixture.TAX_REGION_NO_USE_TAX.taxRegionCode.equals(taxDetail.getRateCode()));
        assertTrue(TaxRegionFixture.TAX_REGION_NO_USE_TAX.taxRegionName.equals(taxDetail.getRateName()));
        assertTrue(TaxRegionFixture.TAX_REGION_NO_USE_TAX.chartOfAccountsCode.equals(taxDetail.getChartOfAccountsCode()));
        assertTrue(TaxRegionFixture.TAX_REGION_NO_USE_TAX.accountNumber.equals(taxDetail.getAccountNumber()));
        assertTrue(TaxRegionFixture.TAX_REGION_NO_USE_TAX.financialObjectCode.equals(taxDetail.getFinancialObjectCode()));
        assertTrue(TaxRegionFixture.TAX_REGION_NO_USE_TAX.taxRegionTypeCode.equals(taxDetail.getTypeCode()));
        assertTrue(TaxRegionRateFixture.TAX_REGION_RATE_05.taxRate.equals(taxDetail.getTaxRate()));
        assertTrue(taxDetail.getTaxAmount().equals(AMOUNT.multiply(new KualiDecimal(TaxRegionRateFixture.TAX_REGION_RATE_05.taxRate))));
    }

    /**
     * This method tests that only tax details are included that are tied to tax regions with the use tax indicator set to true
     * 
     * @throws Exception
     */
    public void testGetUseTaxDetails() throws Exception {

        TaxRegion taxRegionNoUseTax = TaxRegionFixture.TAX_REGION_NO_USE_TAX.createTaxRegion(new TaxRegionRateFixture[] { TaxRegionRateFixture.TAX_REGION_RATE_05 }, new TaxRegionPostalCodeFixture[] { TaxRegionPostalCodeFixture.PO_46113 }, null);
        TaxRegion taxRegionUseTax = TaxRegionFixture.TAX_REGION_WITH_USE_TAX.createTaxRegion(new TaxRegionRateFixture[] { TaxRegionRateFixture.TAX_REGION_RATE_05 }, new TaxRegionPostalCodeFixture[] { TaxRegionPostalCodeFixture.PO_46113 }, null);

        businessObjectService.save(taxRegionNoUseTax);
        businessObjectService.save(taxRegionUseTax);
        List<TaxDetail> taxDetails = taxService.getUseTaxDetails(dateTimeService.convertToSqlDate(DATE_OF_TRANSACTION), TaxRegionPostalCodeFixture.PO_46113.postalCode, AMOUNT);

        // there should only be one use tax detail, even though there are 2 tax regions tied to the same postal code
        assertTrue(taxDetails.size() == 1);

        // verify that use tax detail was set correctly
        TaxDetail taxDetail = taxDetails.get(0);
        assertTrue(TaxRegionFixture.TAX_REGION_WITH_USE_TAX.taxRegionCode.equals(taxDetail.getRateCode()));
        assertTrue(TaxRegionFixture.TAX_REGION_WITH_USE_TAX.taxRegionName.equals(taxDetail.getRateName()));
        assertTrue(TaxRegionFixture.TAX_REGION_WITH_USE_TAX.chartOfAccountsCode.equals(taxDetail.getChartOfAccountsCode()));
        assertTrue(TaxRegionFixture.TAX_REGION_WITH_USE_TAX.accountNumber.equals(taxDetail.getAccountNumber()));
        assertTrue(TaxRegionFixture.TAX_REGION_WITH_USE_TAX.financialObjectCode.equals(taxDetail.getFinancialObjectCode()));
        assertTrue(TaxRegionFixture.TAX_REGION_WITH_USE_TAX.taxRegionTypeCode.equals(taxDetail.getTypeCode()));
        assertTrue(TaxRegionRateFixture.TAX_REGION_RATE_05.taxRate.equals(taxDetail.getTaxRate()));
        assertTrue(taxDetail.getTaxAmount().equals(AMOUNT.multiply(new KualiDecimal(TaxRegionRateFixture.TAX_REGION_RATE_05.taxRate))));            
    }

    /**
     * This method tests that the total sales tax amount is correct.
     */
    public void testGetTotalSalesTaxAmount()  throws Exception {

        TaxRegion taxRegionPostalCode = TaxRegionFixture.TAX_REGION_NO_USE_TAX.createTaxRegion(new TaxRegionRateFixture[] { TaxRegionRateFixture.TAX_REGION_RATE_05 }, new TaxRegionPostalCodeFixture[] { TaxRegionPostalCodeFixture.PO_46113 }, null);
        TaxRegion taxRegionState = TaxRegionFixture.TAX_REGION_WITH_USE_TAX.createTaxRegion(new TaxRegionRateFixture[] { TaxRegionRateFixture.TAX_REGION_RATE_07 }, null, new TaxRegionStateFixture[] { TaxRegionStateFixture.IN });
        
        businessObjectService.save(taxRegionPostalCode);
        businessObjectService.save(taxRegionState);

        //total sales tax amount
        KualiDecimal totalSalesTaxAmount = taxService.getTotalSalesTaxAmount(dateTimeService.convertToSqlDate(DATE_OF_TRANSACTION), TaxRegionPostalCodeFixture.PO_46113.postalCode, AMOUNT);
        
        BigDecimal totalTaxRate =  TaxRegionRateFixture.TAX_REGION_RATE_05.taxRate.add(TaxRegionRateFixture.TAX_REGION_RATE_07.taxRate);
        assertTrue(AMOUNT.multiply(new KualiDecimal(totalTaxRate)).equals(totalSalesTaxAmount));
    }

    /**
     * This class is a fixture for creating tax regions
     */
    private enum TaxRegionFixture {

        TAX_REGION_NO_USE_TAX("NOUSETAX", "NOUSETAX", "POST", "BL", "1031400", "1500", false, true),
        TAX_REGION_WITH_USE_TAX("USETAX", "USETAX", "ST", "BA", "6044900", "5387", true, true), ;

        public String taxRegionCode;
        public String taxRegionName;
        public String taxRegionTypeCode;
        public String chartOfAccountsCode;
        public String accountNumber;
        public String financialObjectCode;
        public boolean taxRegionUseTaxIndicator;
        public boolean active;

        private TaxRegionFixture(String taxRegionCode, String taxRegionName, String taxRegionTypeCode, String chartOfAccountsCode, String accountNumber, String financialObjectCode, boolean taxRegionUseTaxIndicator, boolean active) {
            this.taxRegionCode = taxRegionCode;
            this.taxRegionName = taxRegionName;
            this.taxRegionTypeCode = taxRegionTypeCode;
            this.chartOfAccountsCode = chartOfAccountsCode;
            this.accountNumber = accountNumber;
            this.financialObjectCode = financialObjectCode;
            this.taxRegionUseTaxIndicator = taxRegionUseTaxIndicator;
            this.active = active;
        }


        public TaxRegion createTaxRegion(TaxRegionRateFixture[] taxRegionRateFixtures, TaxRegionPostalCodeFixture[] taxRegionPostalCodeFixtures, TaxRegionStateFixture[] taxRegionStateFixtures) throws Exception {
            TaxRegion taxRegion = new TaxRegion();
            taxRegion.setTaxRegionCode(this.taxRegionCode);
            taxRegion.setTaxRegionName(this.taxRegionName);
            taxRegion.setTaxRegionTypeCode(this.taxRegionTypeCode);
            taxRegion.setChartOfAccountsCode(this.chartOfAccountsCode);
            taxRegion.setAccountNumber(this.accountNumber);
            taxRegion.setFinancialObjectCode(this.financialObjectCode);
            taxRegion.setTaxRegionUseTaxIndicator(this.taxRegionUseTaxIndicator);
            taxRegion.setActive(this.active);

            if (taxRegionRateFixtures != null) {
                for (TaxRegionRateFixture taxRegionRateFixture : taxRegionRateFixtures) {
                    taxRegionRateFixture.addTo(taxRegion);
                }
            }

            if (taxRegionPostalCodeFixtures != null) {
                for (TaxRegionPostalCodeFixture taxRegionPostalCodeFixture : taxRegionPostalCodeFixtures) {
                    taxRegionPostalCodeFixture.addTo(taxRegion);
                }
            }
            
            if (taxRegionStateFixtures != null) {
                for (TaxRegionStateFixture taxRegionStateFixture : taxRegionStateFixtures) {
                    taxRegionStateFixture.addTo(taxRegion);
                }
            }            

            return taxRegion;
        }
    }

    /**
     * This class is a fixture for creating tax region rates
     */
    private enum TaxRegionRateFixture {

        TAX_REGION_RATE_05(DATE_OF_TRANSACTION, new BigDecimal("0.05")),
        TAX_REGION_RATE_07(DATE_OF_TRANSACTION, new BigDecimal("0.07")), ;

        public String effectiveDate;
        public BigDecimal taxRate;

        private TaxRegionRateFixture(String effectiveDate, BigDecimal taxRate) {
            this.effectiveDate = effectiveDate;
            this.taxRate = taxRate;
        }

        public TaxRegionRate createTaxRegionRate() throws Exception {
            TaxRegionRate taxRegionRate = new TaxRegionRate();
            taxRegionRate.setTaxRate(this.taxRate);
            taxRegionRate.setEffectiveDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate(this.effectiveDate));

            return taxRegionRate;
        }

        public void addTo(TaxRegion taxRegion) throws Exception {
            TaxRegionRate taxRegionRate = createTaxRegionRate();
            taxRegionRate.setTaxRegionCode(taxRegion.getTaxRegionCode());
            taxRegion.getTaxRegionRates().add(taxRegionRate);
        }
    }

    private enum TaxRegionPostalCodeFixture {

        PO_46113("46113", "US", true), ;

        public String postalCode;
        public String countryCode;
        public boolean active;

        private TaxRegionPostalCodeFixture(String postalCode, String countryCode, boolean active) {
            this.postalCode = postalCode;
            this.countryCode = countryCode;
            this.active = active;
        }

        public TaxRegionPostalCode createTaxRegionPostalCode() {
            TaxRegionPostalCode taxRegionPostalCode = new TaxRegionPostalCode();
            taxRegionPostalCode.setPostalCode(this.postalCode);
            taxRegionPostalCode.setPostalCountryCode(this.countryCode);
            taxRegionPostalCode.setActive(this.active);
            return taxRegionPostalCode;
        }

        public void addTo(TaxRegion taxRegion) {
            TaxRegionPostalCode taxRegionPostalCode = this.createTaxRegionPostalCode();
            taxRegionPostalCode.setTaxRegionCode(taxRegion.getTaxRegionCode());
            taxRegion.getTaxRegionPostalCodes().add(taxRegionPostalCode);
        }
    }

    private enum TaxRegionStateFixture {

        IN("IN", "US", true), ;

        public String stateCode;
        public String countryCode;
        public boolean active;

        private TaxRegionStateFixture(String stateCode, String countryCode, boolean active) {
            this.stateCode = stateCode;
            this.countryCode = countryCode;
            this.active = active;
        }

        public TaxRegionState createTaxRegionState() {
            TaxRegionState taxRegionState = new TaxRegionState();
            taxRegionState.setStateCode(this.stateCode);
            taxRegionState.setPostalCountryCode(this.countryCode);
            taxRegionState.setActive(this.active);
            return taxRegionState;
        }

        public void addTo(TaxRegion taxRegion) {
            TaxRegionState taxRegionState = this.createTaxRegionState();
            taxRegionState.setTaxRegionCode(taxRegion.getTaxRegionCode());
            taxRegion.getTaxRegionStates().add(taxRegionState);
        }
    }
}

