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
package org.kuali.kfs.module.endow.web.struts;

public class TransactionSummaryForm extends EndowmentReportBaseForm {

    protected String beginningDate;
    protected String endingDate;
    protected String reportOption;
    protected String listKemidsInHeader;
    protected String summaryTotalsOnly;
    
    /**
     * Clears all the fields
     */
    public void clear() {
        super.clear();
        this.beginningDate = null;
        this.endingDate = null;
        this.reportOption = "B";
        this.listKemidsInHeader = "N";
        this.summaryTotalsOnly = "N";
    }

    public String getBeginningDate() {
        return beginningDate;
    }

    public void setBeginningDate(String beginningDate) {
        this.beginningDate = beginningDate;
    }

    public String getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(String endingDate) {
        this.endingDate = endingDate;
    }

    public String getReportOption() {
        return reportOption;
    }

    public void setReportOption(String reportOption) {
        this.reportOption = reportOption;
    }

    public String getListKemidsInHeader() {
        return listKemidsInHeader;
    }

    public void setListKemidsInHeader(String listKemidsInHeader) {
        this.listKemidsInHeader = listKemidsInHeader;
    }

    public String getSummaryTotalsOnly() {
        return summaryTotalsOnly;
    }

    public void setSummaryTotalsOnly(String summaryTotalsOnly) {
        this.summaryTotalsOnly = summaryTotalsOnly;
    }
}
