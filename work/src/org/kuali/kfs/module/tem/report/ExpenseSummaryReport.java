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
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import net.sf.jasperreports.engine.JRDataSource;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.report.annotations.DetailSection;
import org.kuali.kfs.module.tem.report.annotations.Group;
import org.kuali.kfs.module.tem.report.annotations.JasperReport;
import org.kuali.kfs.module.tem.report.annotations.ReportStyle;
import org.kuali.kfs.module.tem.report.annotations.SubReport;
import org.kuali.kfs.module.tem.report.annotations.Summary;
import org.kuali.kfs.module.tem.report.annotations.TitleStyle;
import org.kuali.kfs.sys.report.ReportInfoHolder;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Represents an Expense Summary Report
 *
 */
@JasperReport
@ReportStyle("standard")
@TitleStyle("standard")
public class ExpenseSummaryReport extends ReportInfoHolder implements DetailedReport {
    
    public static Logger LOG = Logger.getLogger(ExpenseSummaryReport.class);
    
    private String traveler;
    private String initiator;
    private String authorizationInitiator;
    private Date beginDate;
    private Date endDate;
    private String locations;
    private String purpose;
    private String institution;

    @Group("expenseType")
    @DetailSection
    private JRDataSource data;
    
    @SubReport
    @Summary
    private JRDataSource summary;

    private String tripId;

    public ExpenseSummaryReport() {
        setReportTemplateClassPath(TEMPLATE_CLASSPATH);
        setReportFileName("ExpenseSummary" + PDF_FILE_EXTENSION);
        setReportTitle("Overview Summary of Expenses");
    }

    /**
     * Gets the value of traveler
     *
     * @return the value of traveler
     */
    public String getTraveler() {
        return this.traveler;
    }
    
    /**
     * Sets the value of traveler
     *
     * @param argTraveler Value to assign to this.traveler
     */
    public void setTraveler(final String argTraveler) {
        this.traveler = argTraveler;
    }

    /**
     * Gets the value of initiator
     *
     * @return the value of initiator
     */
    public String getInitiator() {
        return this.initiator;
    }
    
    /**
     * Sets the value of initiator
     *
     * @param argInitiator Value to assign to this.initiator
     */
    public void setInitiator(final String argInitiator) {
        this.initiator = argInitiator;
    }

    /**
     * Gets the value of authorizationInitiator
     *
     * @return the value of authorizationInitiator
     */
    public String getAuthorizationInitiator() {
        return this.authorizationInitiator;
    }
    
    /**
     * Sets the value of authorizationInitiator
     *
     * @param argAuthorizationInitiator Value to assign to this.authorizationInitiator
     */
    public void setAuthorizationInitiator(final String argAuthorizationInitiator) {
        this.authorizationInitiator = argAuthorizationInitiator;
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
     * Gets the value of locations
     *
     * @return the value of locations
     */
    public String getLocations() {
        return this.locations;
    }
    
    /**
     * Sets the value of locations
     *
     * @param argLocations Value to assign to this.locations
     */
    public void setLocations(final String argLocations) {
        this.locations = argLocations;
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
     * Gets the value of Data
     *
     * @return the value of Data
     */
    @Override
    public JRDataSource getData() {
        return this.data;
    }
    
    /**
     * Sets the value of Data
     *
     * @param argData Value to assign to this.expenses
     */
    public void setData(final Collection<Detail> argData) {
        LOG.debug("Report created with data " + argData.size());
        this.data = convertReportData(argData);
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

    public static class Detail {
        private String name = "";
        private String expenseType = "";
        private String date = "";
        private BigDecimal amount = new BigDecimal(0);

        public Detail(final String name, final String expenseType, final KualiDecimal amount, final Date date) {
            this.name = name;
            if (amount != null) {
                this.amount = amount.bigDecimalValue();
            }
            else {
                this.amount = KualiDecimal.ZERO.bigDecimalValue();
            }
 
            if (date != null) {
                final String expenseDate = new SimpleDateFormat("MM/dd").format(date);
                this.date = expenseDate;
            }
            this.expenseType = expenseType;
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
         * Gets the value of ExpenseType
         *
         * @return the value of ExpenseType
         */
        public String getExpenseType() {
            return this.expenseType;
        }
        
        /**
         * Sets the value of ExpenseType
         *
         * @param argExpenseType Value to assign to this.ExpenseType
         */
        public void setExpenseType(final String argExpenseType) {
            this.expenseType = argExpenseType;
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