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
