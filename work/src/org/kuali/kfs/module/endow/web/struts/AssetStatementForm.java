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

public class AssetStatementForm extends EndowmentReportBaseForm {
    
    protected String monthEndDate;
    protected String reportOption;
    protected String printFileOption;
    
    /**
     * Clears all the fields
     */
    public void clear() {
        super.clear();
        this.monthEndDate = null;
        this.reportOption = "B";
        this.printFileOption = "Y";
    }

    public String getMonthEndDate() {
        return monthEndDate;
    }

    public void setMonthEndDate(String monthEndDate) {
        this.monthEndDate = monthEndDate;
    }

    public String getReportOption() {
        return reportOption;
    }

    public void setReportOption(String reportOption) {
        this.reportOption = reportOption;
    }

    public String getPrintFileOption() {
        return printFileOption;
    }

    public void setPrintFileOption(String printFileOption) {
        this.printFileOption = printFileOption;
    }

}
