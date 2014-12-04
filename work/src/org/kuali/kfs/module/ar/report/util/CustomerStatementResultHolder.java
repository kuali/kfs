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
package org.kuali.kfs.module.ar.report.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.api.util.type.KualiDecimal;

public class CustomerStatementResultHolder {
    
    private String customerNumber;
    private File file;
    private KualiDecimal currentBilledAmount;
    private List<String> invoiceNumbers;

    public CustomerStatementResultHolder() {
        customerNumber = null;
        file = null;
        currentBilledAmount = KualiDecimal.ZERO;
        invoiceNumbers = new ArrayList<String>();
    }

    /**
     * @return the currentBilledAmount
     */
    public KualiDecimal getCurrentBilledAmount() {
        return currentBilledAmount;
    }

    /**
     * @param currentBilledAmount the currentBilledAmount to set
     */
    public void setCurrentBilledAmount(KualiDecimal currentBilledAmount) {
        this.currentBilledAmount = currentBilledAmount;
    }

    /**
     * @return the invoiceNumbers
     */
    public List<String> getInvoiceNumbers() {
        return invoiceNumbers;
    }

    /**
     * @param invoiceNumbers the invoiceNumbers to set
     */
    public void setInvoiceNumbers(List<String> invoiceNumbers) {
        this.invoiceNumbers = invoiceNumbers;
    }

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * @return the customerNumber
     */
    public String getCustomerNumber() {
        return customerNumber;
    }

    /**
     * @param customerNumber the customerNumber to set
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }    
    
}
