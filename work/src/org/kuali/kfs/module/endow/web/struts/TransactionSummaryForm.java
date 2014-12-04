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
