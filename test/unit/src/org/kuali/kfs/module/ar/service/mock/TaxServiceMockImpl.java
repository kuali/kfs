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
package org.kuali.kfs.module.ar.service.mock;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.businessobject.TaxDetail;
import org.kuali.kfs.sys.service.TaxService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * 
 * Mocks the TaxServiceImpl with the minimally needed configuration.
 */
public class TaxServiceMockImpl implements TaxService {

    public static final String TAXABLE_POSTAL_CD = "85705";
    
    public List<TaxDetail> getUseTaxDetails(Date dateOfTransaction, String postalCode, KualiDecimal amount) {
        return new ArrayList<TaxDetail>();
    }
    
    public KualiDecimal getTotalSalesTaxAmount(Date dateOfTransaction, String postalCode, KualiDecimal amount) {
        return new KualiDecimal(0);
    }
    
    public KualiDecimal getPretaxAmount(Date dateOfTransaction, String postalCode, KualiDecimal amountWithTax) {
        return new KualiDecimal(0);
    }
    
    public List<TaxDetail> getSalesTaxDetails(Date dateOfTransaction, String postalCode, KualiDecimal amount) {
        List<TaxDetail> salesTaxDetails = new ArrayList<TaxDetail>();
        
        //  only apply the fake tax to one postal code, the rest get nothing
        if (!TAXABLE_POSTAL_CD.equalsIgnoreCase(postalCode)) {
            return salesTaxDetails;
        }
        
        //  fake state tax
        salesTaxDetails.add(getFakeStateTax(amount));
        salesTaxDetails.add(getFakeCountyTax(amount));
        salesTaxDetails.add(getFakeCityTax(amount));
        return salesTaxDetails;
    }

    private TaxDetail getFakeStateTax(KualiDecimal taxableAmount) {
        TaxDetail taxDetail = new TaxDetail();
        taxDetail.setAccountNumber("1031400");
        taxDetail.setChartOfAccountsCode("BL");
        taxDetail.setFinancialObjectCode("8000");
        taxDetail.setRateCode("ST-AZ");
        taxDetail.setRateName("State-Arizona");
        taxDetail.setTaxRate(new BigDecimal("0.065"));  // 6.5%
        taxDetail.setTypeCode("ST");

        taxDetail.setTaxAmount(taxableAmount.multiply(new KualiDecimal(taxDetail.getTaxRate()), true));

        return taxDetail;
    }

    private TaxDetail getFakeCountyTax(KualiDecimal taxableAmount) {
        TaxDetail taxDetail = new TaxDetail();
        taxDetail.setAccountNumber("1031400");
        taxDetail.setChartOfAccountsCode("BL");
        taxDetail.setFinancialObjectCode("8000");
        taxDetail.setRateCode("CNTY-PIMA");
        taxDetail.setRateName("County-Pima");
        taxDetail.setTaxRate(new BigDecimal("0.0075")); // 0.75%
        taxDetail.setTypeCode("CNTY");

        taxDetail.setTaxAmount(taxableAmount.multiply(new KualiDecimal(taxDetail.getTaxRate()), true));
        
        return taxDetail;
    }

    private TaxDetail getFakeCityTax(KualiDecimal taxableAmount) {
        TaxDetail taxDetail = new TaxDetail();
        taxDetail.setAccountNumber("1031400");
        taxDetail.setChartOfAccountsCode("BL");
        taxDetail.setFinancialObjectCode("8000");
        taxDetail.setRateCode("POST-85705");
        taxDetail.setRateName("PostalCode-85705");
        taxDetail.setTaxRate(new BigDecimal("0.015"));  // 1.5%
        taxDetail.setTypeCode("POST");

        taxDetail.setTaxAmount(taxableAmount.multiply(new KualiDecimal(taxDetail.getTaxRate()), true));

        return taxDetail;
    }

}
