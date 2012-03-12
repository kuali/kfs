/*
 * Copyright 2011 The Kuali Foundation.
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
