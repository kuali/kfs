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
package org.kuali.kfs.module.tem.report;

import static org.kuali.kfs.module.tem.TemConstants.Report.TEMPLATE_CLASSPATH;
import static org.kuali.kfs.sys.KFSConstants.ReportGeneration.PDF_FILE_EXTENSION;
import static org.springframework.ui.jasperreports.JasperReportsUtils.convertReportData;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import net.sf.jasperreports.engine.JRDataSource;

import org.kuali.kfs.module.tem.report.annotations.ColumnHeader;
import org.kuali.kfs.module.tem.report.annotations.Crosstab;
import org.kuali.kfs.module.tem.report.annotations.JasperReport;
import org.kuali.kfs.module.tem.report.annotations.Measure;
import org.kuali.kfs.module.tem.report.annotations.ReportStyle;
import org.kuali.kfs.module.tem.report.annotations.RowHeader;
import org.kuali.kfs.module.tem.report.annotations.SubReport;
import org.kuali.kfs.module.tem.report.annotations.Summary;
import org.kuali.kfs.module.tem.report.annotations.TitleStyle;
import org.kuali.kfs.sys.report.ReportInfoHolder;
import org.kuali.rice.core.api.util.type.KualiDecimal;

@JasperReport
@ReportStyle("standard")
@TitleStyle("standard")
public class SummaryByDayReport extends ReportInfoHolder {
    private Date beginDate;
    private Date endDate;
    private String purpose;
    private String tripId;
    private String institution;

    @Crosstab
    @Summary
    private JRDataSource summary;

    @Crosstab
    @SubReport
    private JRDataSource transportation;

    @Crosstab
    @SubReport
    private JRDataSource lodging;

    @Crosstab
    @SubReport
    private JRDataSource meals;

    @Crosstab
    @SubReport
    private JRDataSource otherExpenses;

    @Crosstab
    @SubReport
    private JRDataSource weeklyTotal;

    public SummaryByDayReport() {
        setReportTemplateClassPath(TEMPLATE_CLASSPATH);
        setReportFileName("ExpenseSummary" + PDF_FILE_EXTENSION);
        setSubReports(new HashMap<String, String>());
    }


    /**
     * Gets the value of beginDate
     *
     * @return the value of beginDate
     */
    public Date getBeginDate() {
        return this.beginDate;
    }

    /**
     * Sets the value of beginDate
     *
     * @param argBeginDate Value to assign to this.beginDate
     */
    public void setBeginDate(final Date argBeginDate) {
        this.beginDate = argBeginDate;
    }

    /**
     * Gets the value of endDate
     *
     * @return the value of endDate
     */
    public Date getEndDate() {
        return this.endDate;
    }

    /**
     * Sets the value of endDate
     *
     * @param argEndDate Value to assign to this.endDate
     */
    public void setEndDate(final Date argEndDate) {
        this.endDate = argEndDate;
    }

    /**
     * Gets the value of TripId
     *
     * @return the value of TripId
     */
    public String getTripId() {
        return this.tripId;
    }

    /**
     * Sets the value of TripId
     *
     * @param argTripId Value to assign to this.TripId
     */
    public void setTripId(final String argTripId) {
        this.tripId = argTripId;
    }

    /**
     * Gets the value of Purpose
     *
     * @return the value of Purpose
     */
    public String getPurpose() {
        return this.purpose;
    }

    /**
     * Sets the value of Purpose
     *
     * @param argPurpose Value to assign to this.Purpose
     */
    public void setPurpose(final String argPurpose) {
        this.purpose = argPurpose;
    }

    /**
     * Gets the value of Institution
     *
     * @return the value of Institution
     */
    public String getInstitution() {
        return this.institution;
    }

    /**
     * Sets the value of Institution
     *
     * @param argInstitution Value to assign to this.Institution
     */
    public void setInstitution(final String argInstitution) {
        this.institution = argInstitution;
    }


    /**
     * Gets the value of Summary
     *
     * @return the value of Summary
     */
    public JRDataSource getSummary() {
        return this.summary;
    }

    /**
     * Sets the value of Summary
     *
     * @param argSummary Value to assign to this.Summary
     */
    public void setSummary(final Collection<Detail> argSummary) {
        this.summary = convertReportData(argSummary);
    }

    /**
     * Gets the value of Lodging
     *
     * @return the value of Lodging
     */
    public JRDataSource getLodging() {
        return this.lodging;
    }

    /**
     * Sets the value of Lodging
     *
     * @param argLodging Value to assign to this.Lodging
     */
    public void setLodging(final Collection<Detail> argLodging) {
        this.lodging = convertReportData(argLodging);
    }

    /**
     * Gets the value of Transportation
     *
     * @return the value of Transportation
     */
    public JRDataSource getTransportation() {
        return this.transportation;
    }

    /**
     * Sets the value of Transportation
     *
     * @param argTransportation Value to assign to this.Transportation
     */
    public void setTransportation(final Collection<Detail> argTransportation) {
        this.transportation = convertReportData(argTransportation);
    }

    /**
     * Gets the value of Meals
     *
     * @return the value of Meals
     */
    public JRDataSource getMeals() {
        return this.meals;
    }

    /**
     * Sets the value of Meals
     *
     * @param argMeals Value to assign to this.Meals
     */
    public void setMeals(final Collection<Detail> argMeals) {
        this.meals = convertReportData(argMeals);
    }

    /**
     * Gets the value of Other
     *
     * @return the value of Other
     */
    public JRDataSource getOtherExpenses() {
        return this.otherExpenses;
    }

    /**
     * Sets the value of Other
     *
     * @param argOther Value to assign to this.Other
     */
    public void setOtherExpenses(final Collection<Detail> argOther) {
        this.otherExpenses = convertReportData(argOther);
    }

    public JRDataSource getWeeklyTotal() {
        return weeklyTotal;
    }

    public void setWeeklyTotal(final Collection<Detail> argWeeklyTotal) {
        this.weeklyTotal = convertReportData(argWeeklyTotal);
    }

    public static class Detail {
        @ColumnHeader
        private String name;

        @RowHeader
        private String date;

        @Measure
        private BigDecimal amount;

        public Detail(final String name, final KualiDecimal amount, final String date) {
            this.name = name;
            if (amount != null) {
                this.amount = amount.bigDecimalValue();
            }
            else {
                this.amount = KualiDecimal.ZERO.bigDecimalValue();
            }
            this.date = date;
        }

        /**
         * Gets the value of Name
         *
         * @return the value of Name
         */
        public String getName() {
            return this.name;
        }

        /**
         * Sets the value of Name
         *
         * @param argName Value to assign to this.Name
         */
        public void setName(final String argName) {
            this.name = argName;
        }

        /**
         * Gets the value of Amount
         *
         * @return the value of Amount
         */
        public BigDecimal getAmount() {
            return this.amount;
        }

        /**
         * Sets the value of Amount
         *
         * @param argAmount Value to assign to this.Amount
         */
        public void setAmount(final BigDecimal argAmount) {
            this.amount = argAmount;
        }

        /**
         * Gets the value of Date
         *
         * @return the value of Date
         */
        public String getDate() {
            return this.date;
        }

        /**
         * Sets the value of Date
         *
         * @param argDate Value to assign to this.Date
         */
        public void setDate(final String argDate) {
            this.date = argDate;
        }
    }
}