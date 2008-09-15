/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.businessobject.TaxDetail;
import org.kuali.kfs.sys.businessobject.TaxRegion;
import org.kuali.kfs.sys.service.TaxRegionService;
import org.kuali.kfs.sys.service.TaxService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TaxServiceImpl implements TaxService {
    
    private TaxRegionService taxRegionService;

    /**
     * @see org.kuali.kfs.sys.service.TaxService#getSalesTaxDetails(java.lang.String, java.lang.String, org.kuali.rice.kns.util.KualiDecimal)
     */
    public List<TaxDetail> getSalesTaxDetails(Date dateOfTransaction, String postalCode, KualiDecimal amount) {
        List<TaxDetail> salesTaxDetails = new ArrayList<TaxDetail>();

        for( TaxRegion taxRegion : taxRegionService.getSalesTaxRegions(postalCode)){
            salesTaxDetails.add(populateTaxDetail( taxRegion, dateOfTransaction, amount));
        }
        
        return salesTaxDetails;
    }
    
    /**
     * @see org.kuali.kfs.sys.service.TaxService#getUseTaxDetails(java.lang.String, java.lang.String, org.kuali.rice.kns.util.KualiDecimal)
     */
    public List<TaxDetail> getUseTaxDetails(Date dateOfTransaction, String postalCode, KualiDecimal amount) {
        List<TaxDetail> useTaxDetails = new ArrayList<TaxDetail>();
        
        for( TaxRegion taxRegion : taxRegionService.getUseTaxRegions(postalCode)){
            useTaxDetails.add(populateTaxDetail( taxRegion, dateOfTransaction, amount ));
        }
        
        return useTaxDetails;
    }    

    /**
     * @see org.kuali.kfs.sys.service.TaxService#getTotalSalesTaxAmount(java.lang.String, java.lang.String, org.kuali.rice.kns.util.KualiDecimal)
     */
    public KualiDecimal getTotalSalesTaxAmount(Date dateOfTransaction, String postalCode, String stateCode, String countryCode, KualiDecimal amount) {
        KualiDecimal totalSalesTaxAmount = KualiDecimal.ZERO;
        
        for( TaxDetail taxDetail : getSalesTaxDetails( dateOfTransaction, postalCode, amount )){
            totalSalesTaxAmount = totalSalesTaxAmount.add(taxDetail.getTaxAmount());
        }
        
        return totalSalesTaxAmount;
    }
    
    /**
     * This method returns a populated Tax Detail BO based on the Tax Region BO and amount
     * @param taxRegion
     * @param amount
     * @return
     */
    protected TaxDetail populateTaxDetail( TaxRegion taxRegion, Date dateOfTransaction, KualiDecimal amount ){
        TaxDetail taxDetail = new TaxDetail();
        taxDetail.setAccountNumber(taxRegion.getAccountNumber());
        taxDetail.setChartOfAccountsCode(taxRegion.getChartOfAccountsCode());
        taxDetail.setFinancialObjectCode(taxRegion.getFinancialObjectCode());
        taxDetail.setRateCode( taxRegion.getTaxRegionCode() );
        taxDetail.setRateName( taxRegion.getTaxRegionName() );
        taxDetail.setTaxRate( taxRegion.getEffectiveTaxRegionRate( dateOfTransaction ).getTaxRate() );
        taxDetail.setTaxAmount( amount.multiply( new KualiDecimal( taxDetail.getTaxRate() ) ) );
        
        return taxDetail;
    }

    public TaxRegionService getTaxRegionService() {
        return taxRegionService;
    }

    public void setTaxRegionService(TaxRegionService taxRegionService) {
        this.taxRegionService = taxRegionService;
    }
}
