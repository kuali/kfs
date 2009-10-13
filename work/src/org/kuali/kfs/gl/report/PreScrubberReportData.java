/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.gl.report;

import java.util.Set;

public class PreScrubberReportData {
    protected int inputRecords;
    protected int outputRecords;
    protected Set<String> accountsWithNoCharts;
    protected Set<String> accountsWithMultipleCharts;
    
    public PreScrubberReportData(int inputRecords, int outputRecords, Set<String> accountsWithNoCharts, Set<String> accountsWithMultipleCharts) {
        this.inputRecords = inputRecords;
        this.outputRecords = outputRecords;
        this.accountsWithMultipleCharts = accountsWithMultipleCharts;
        this.accountsWithNoCharts = accountsWithNoCharts;
    }
    
    public int getInputRecords() {
        return inputRecords;
    }
    public void setInputRecords(int inputRecords) {
        this.inputRecords = inputRecords;
    }
    public int getOutputRecords() {
        return outputRecords;
    }
    public void setOutputRecords(int outputRecords) {
        this.outputRecords = outputRecords;
    }
    public Set<String> getAccountsWithNoCharts() {
        return accountsWithNoCharts;
    }
    public void setAccountsWithNoCharts(Set<String> accountsWithNoCharts) {
        this.accountsWithNoCharts = accountsWithNoCharts;
    }
    public Set<String> getAccountsWithMultipleCharts() {
        return accountsWithMultipleCharts;
    }
    public void setAccountsWithMultipleCharts(Set<String> accountsWithMultipleCharts) {
        this.accountsWithMultipleCharts = accountsWithMultipleCharts;
    }
}
