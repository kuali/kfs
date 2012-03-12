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

import java.sql.Date;
import java.util.List;

import org.kuali.kfs.sys.businessobject.TaxDetail;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public interface TaxService {
    
    /**
     * This method returns a list of Tax Detail BO's for Sales Tax
     * 
     * @param dateOfTransaction date to include tax rates from
     * @param postalCode postal code to get tax rates
     * @param amount amount to be taxed
     * @return
     */
    List<TaxDetail> getSalesTaxDetails( Date dateOfTransaction, String postalCode,  KualiDecimal amount );

    /**
     * This method returns a list of Tax Detail BO's for Sales Tax
     * 
     * @param dateOfTransaction date to include tax rates from
     * @param postalCode postal code to get tax rates
     * @param amount amount to be taxed
     * @return
     */
    List<TaxDetail> getUseTaxDetails( Date dateOfTransaction, String postalCode, KualiDecimal amount );

    /**
     * This method returns the total sales tax amount
     * 
     * @param dateOfTransaction date to include tax rates from
     * @param postalCode postal code to get tax rates
     * @param amount amount to be taxed
     * @return
     */
    KualiDecimal getTotalSalesTaxAmount( Date dateOfTransaction, String postalCode, KualiDecimal amount );

    /**
     * This method returns pretaxAmount
     * 
     * @param dateOfTransaction date to include tax rates from
     * @param postalCode postal code to get tax rates
     * @param amount amount to be taxed
     * @return
     */
    KualiDecimal getPretaxAmount(Date dateOfTransaction, String postalCode, KualiDecimal amountWithTax);
}
