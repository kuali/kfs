/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.purap.fixture;

import java.math.BigDecimal;

import org.kuali.kfs.sys.businessobject.TaxRegion;
import org.kuali.kfs.sys.businessobject.TaxRegionPostalCode;
import org.kuali.kfs.sys.businessobject.TaxRegionRate;
import org.kuali.kfs.sys.businessobject.TaxRegionState;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;

public class TaxFixture {

    public enum TaxTestCaseFixture {

        SalesTaxHappyPathTest(
                    true, //isItemTypeTaxable
                    true, //isItemTaxAmountNull
                    true, //iscommodityCodeNull
                    true, //fundGroupCodeTaxable
                    true, //objectCodeTaxable
                    true, //isDeliveryStateTaxable
                    false //isUseTax
                    ),

        SalesTaxItemTypeNotTaxableTest(
                    false, //isItemTypeTaxable
                    true, //isItemTaxAmountNull
                    true, //iscommodityCodeNull
                    true, //fundGroupCodeTaxable
                    true, //objectCodeTaxable
                    true, //isDeliveryStateTaxable
                    false //isUseTax
                    ),

         SalesTaxItemTaxFieldNullTest(
                    true, //isItemTypeTaxable
                    false, //isItemTaxAmountNull
                    true, //iscommodityCodeNull
                    true, //fundGroupCodeTaxable
                    true, //objectCodeTaxable
                    true, //isDeliveryStateTaxable
                    false //isUseTax
                    ),

          SalesTaxCommodityCodeNullTest(
                    true, //isItemTypeTaxable
                    true, //isItemTaxAmountNull
                    false, //iscommodityCodeNull
                    true, //fundGroupCodeTaxable
                    true, //objectCodeTaxable
                    true, //isDeliveryStateTaxable
                    false //isUseTax
                    ),

          SalesTaxDeliveryStateExemptTest(
                    true, //isItemTypeTaxable
                    true, //isItemTaxAmountNull
                    true, //iscommodityCodeNull
                    true, //fundGroupCodeTaxable
                    true, //objectCodeTaxable
                    false, //isDeliveryStateTaxable
                    false //isUseTax
                    ),

          SalesTaxDeliveryStateExemptWithNonTaxableFundTest(
                    true, //isItemTypeTaxable
                    true, //isItemTaxAmountNull
                    true, //iscommodityCodeNull
                    false, //fundGroupCodeTaxable
                    true, //objectCodeTaxable
                    false, //isDeliveryStateTaxable
                    false //isUseTax
                    ),

           SalesTaxAccountNotTaxableTest(
                    true, //isItemTypeTaxable
                    true, //isItemTaxAmountNull
                    true, //iscommodityCodeNull
                    false, //fundGroupCodeTaxable
                    true, //objectCodeTaxable
                    true, //isDeliveryStateTaxable
                    false //isUseTax
                    ),

           SalesTaxObjectCodeNotTaxableTest(
                    true, //isItemTypeTaxable
                    true, //isItemTaxAmountNull
                    true, //iscommodityCodeNull
                    true, //fundGroupCodeTaxable
                    false, //objectCodeTaxable
                    true, //isDeliveryStateTaxable
                    false //isUseTax
                    ),

            SalesTaxParamDisabledTest(
                    true, //isItemTypeTaxable
                    true, //isItemTaxAmountNull
                    true, //iscommodityCodeNull
                    true, //fundGroupCodeTaxable
                    true, //objectCodeTaxable
                    true, //isDeliveryStateTaxable
                    false, //isUseTax
                    false //isSalesTaxEnabled
                    ),

            UseTaxHappyPathTest(
                    true, //isItemTypeTaxable
                    true, //isItemTaxAmountNull
                    true, //iscommodityCodeNull
                    true, //fundGroupCodeTaxable
                    true, //objectCodeTaxable
                    true, //isDeliveryStateTaxable
                    true //isUseTax
                    ),

            UseTaxItemTypeNotTaxableTest(
                    false, //isItemTypeTaxable
                    true, //isItemTaxAmountNull
                    true, //iscommodityCodeNull
                    true, //fundGroupCodeTaxable
                    true, //objectCodeTaxable
                    true, //isDeliveryStateTaxable
                    true //isUseTax
                    );

        private boolean isSalesTaxEnabled;
        private final  boolean isItemTypeTaxable;
        private final  boolean isItemTaxAmountNull;
        private final  boolean iscommodityCodeNull;
        private final  boolean fundGroupCodeTaxable;
        private final  boolean objectCodeTaxable;
        private final boolean isDeliveryStateTaxable;
        private final  boolean isUseTax;

        TaxTestCaseFixture(boolean isItemTypeTaxable,
                           boolean isItemTaxAmountNull,
                           boolean iscommodityCodeNull,
                           boolean fundGroupCodeTaxable,
                           boolean objectCodeTaxable,
                           boolean isDeliveryStateTaxable,
                           boolean isUseTax) {
           this.isItemTypeTaxable = isItemTypeTaxable;
           this.isItemTaxAmountNull = isItemTaxAmountNull;
           this.iscommodityCodeNull = iscommodityCodeNull;
           this.fundGroupCodeTaxable = fundGroupCodeTaxable;
           this.objectCodeTaxable = objectCodeTaxable;
           this.isDeliveryStateTaxable = isDeliveryStateTaxable;
           this.isUseTax = isUseTax;
           this.isSalesTaxEnabled = true;
        }

        TaxTestCaseFixture(boolean isItemTypeTaxable,
                           boolean isItemTaxAmountNull,
                           boolean iscommodityCodeNull,
                           boolean fundGroupCodeTaxable,
                           boolean objectCodeTaxable,
                           boolean isDeliveryStateTaxable,
                           boolean isUseTax,
                           boolean isSalesTaxEnabled) {
            this.isItemTypeTaxable = isItemTypeTaxable;
            this.isItemTaxAmountNull = isItemTaxAmountNull;
            this.iscommodityCodeNull = iscommodityCodeNull;
            this.fundGroupCodeTaxable = fundGroupCodeTaxable;
            this.objectCodeTaxable = objectCodeTaxable;
            this.isDeliveryStateTaxable = isDeliveryStateTaxable;
            this.isUseTax = isUseTax;
            this.isSalesTaxEnabled = isSalesTaxEnabled;
        }

        public boolean isConsolidationObjectCode() {
            return objectCodeTaxable;
        }

        public boolean isFundGroupCodeTaxable() {
            return fundGroupCodeTaxable;
        }

        public boolean iscommodityCodeNull() {
            return iscommodityCodeNull;
        }

        public boolean isItemTypeTaxable() {
            return isItemTypeTaxable;
        }

        public boolean isUseTax() {
            return isUseTax;
        }

        public boolean isItemTaxAmountNull() {
            return isItemTaxAmountNull;
        }

        public boolean isObjectCodeTaxable() {
            return objectCodeTaxable;
        }

        public boolean isDeliveryStateTaxable() {
            return isDeliveryStateTaxable;
        }

        public boolean isSalesTaxEnabled(){
            return isSalesTaxEnabled;
        }
    }

    /**
     * All the below tax fixtures are copied from TaxServiceTest
     */
    public enum TaxRegionFixture {

        TAX_REGION_NO_USE_TAX("NOUSETAX", "NOUSETAX", "POST", "BA", "6044900", "1500", false, true),
        TAX_REGION_WITH_USE_TAX("USETAX", "USETAX", "ST", "BA", "6044900", "1500", true, true), ;

        public String taxRegionCode;
        public String taxRegionName;
        public String taxRegionTypeCode;
        public String chartOfAccountsCode;
        public String accountNumber;
        public String financialObjectCode;
        public boolean taxRegionUseTaxIndicator;
        public boolean active;

        private TaxRegionFixture(String taxRegionCode,
                                 String taxRegionName,
                                 String taxRegionTypeCode,
                                 String chartOfAccountsCode,
                                 String accountNumber,
                                 String financialObjectCode,
                                 boolean taxRegionUseTaxIndicator,
                                 boolean active) {
            this.taxRegionCode = taxRegionCode;
            this.taxRegionName = taxRegionName;
            this.taxRegionTypeCode = taxRegionTypeCode;
            this.chartOfAccountsCode = chartOfAccountsCode;
            this.accountNumber = accountNumber;
            this.financialObjectCode = financialObjectCode;
            this.taxRegionUseTaxIndicator = taxRegionUseTaxIndicator;
            this.active = active;
        }

        public TaxRegion createTaxRegion(TaxRegionRateFixture[] taxRegionRateFixtures,
                                         TaxRegionPostalCodeFixture[] taxRegionPostalCodeFixtures,
                                         TaxRegionStateFixture[] taxRegionStateFixtures) {

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
    public enum TaxRegionRateFixture {

        TAX_REGION_RATE_05("01/01/2008", new BigDecimal(.05)),
        TAX_REGION_RATE_07("01/01/2008", new BigDecimal(.07)), ;

        public String effectiveDate;
        public BigDecimal taxRate;

        private TaxRegionRateFixture(String effectiveDate, BigDecimal taxRate) {
            this.effectiveDate = effectiveDate;
            this.taxRate = taxRate;
        }

        public TaxRegionRate createTaxRegionRate() {
            TaxRegionRate taxRegionRate = new TaxRegionRate();
            taxRegionRate.setTaxRate(this.taxRate);
            try {
                taxRegionRate.setEffectiveDate(SpringContext.getBean(DateTimeService.class).convertToSqlDate(this.effectiveDate));
            } catch( Exception e ){
            }

            return taxRegionRate;
        }

        public void addTo(TaxRegion taxRegion) {
            TaxRegionRate taxRegionRate = this.createTaxRegionRate();
            taxRegionRate.setTaxRegionCode(taxRegion.getTaxRegionCode());
            taxRegion.getTaxRegionRates().add(taxRegionRate);
        }
    }

    public enum TaxRegionPostalCodeFixture {

        PO_46202("46202-5260", "US", true), PO_46202_SHORT("46202", "US", true), ;

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

    public enum TaxRegionStateFixture {

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
