/*
 * Copyright 2011 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
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

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;

import org.kuali.kfs.module.tem.report.annotations.ColumnHeader;
import org.kuali.kfs.module.tem.report.annotations.Crosstab;
import org.kuali.kfs.module.tem.report.annotations.DetailSection;
import org.kuali.kfs.module.tem.report.annotations.JasperReport;
import org.kuali.kfs.module.tem.report.annotations.Measure;
import org.kuali.kfs.module.tem.report.annotations.ReportStyle;
import org.kuali.kfs.module.tem.report.annotations.RowHeader;
import org.kuali.kfs.module.tem.report.annotations.SubReport;
import org.kuali.kfs.module.tem.report.annotations.TitleStyle;
import org.kuali.kfs.sys.report.ReportInfoHolder;
import org.kuali.rice.kns.util.KualiDecimal;

@JasperReport
@ReportStyle("standard")
@TitleStyle("standard")
public class NonEmployeeCertificationReport extends ReportInfoHolder{
    private String documentId;
    private String eventId;
    private String eventName;
    private String eventType;
    private String traveler;
    private String approvingDepartment;
    private Date beginDate;
    private Date endDate;
    private KualiDecimal totalExpense;
    private String destination;
    
    private String purpose;
    private String tripId;
    private String institution;
    private BufferedImage barcodeImage;
    private String certificationDescription;
    List<NonEmployeeCertificationReport.Detail> expenseDetails;
    
    @Crosstab
    @DetailSection
    @SubReport
    private JRDataSource actualExpenses;
    
    
    public NonEmployeeCertificationReport(){
        setReportTemplateClassPath(TEMPLATE_CLASSPATH);
        setReportFileName("NonEmployeeCertification" + PDF_FILE_EXTENSION);
        setReportTitle("Non-Employee Certification #");
        setReportTemplateName("NonEmployeeCertification");
        
        //default destination to blank
        setDestination("");
    }
    
    /**
     * 
     * This method...
     * @param argDocumentId
     */
    public void setDocumentId(String argDocumentId){
        documentId = argDocumentId;
    }
    
    /**
     * 
     * This method...
     * @return
     */
    public String getDocumentId(){
        return documentId;
    }
    
    /**
     * 
     * This method...
     * @param argEventId
     */
    public void setEventId(String argEventId){
        eventId = argEventId;
    }
    
    /**
     * 
     * This method...
     * @return
     */
    public String getEventId(){
        return eventId;
    }
    
    /**
     * 
     * This method...
     * @param argEventName
     */
    public void setEventName(String argEventName){
        eventName = argEventName;
    }
    
    /**
     * 
     * This method...
     * @return
     */
    public String getEventName(){
        return eventName;
    }
    
    /**
     * 
     * This method...
     * @param argEventType
     */
    public void setEventType(String argEventType){
        eventType = argEventType;
    }
    
    /**
     * 
     * This method...
     * @return
     */
    public String getEventType(){
        return eventType;
    }
    
    /**
     * 
     * This method...
     * @param argTraveler
     */
    public void setTraveler(String argTraveler){
        traveler = argTraveler;
    }
    
    /**
     * 
     * This method...
     * @return
     */
    public String getTraveler(){
        return traveler;
    }
    
    /**
     * 
     * This method...
     * @param argApprovingDepartment
     */
    public void setApprovingDepartment(String argApprovingDepartment){
        approvingDepartment = argApprovingDepartment;
    }
    
    /**
     * 
     * This method...
     * @return
     */
    public String getApprovingDepartment(){
        return approvingDepartment;
    }
    
    /**
     * 
     * This method...
     * @param argBeginDate
     */
    public void setBeginDate(Date argBeginDate){
        beginDate = argBeginDate;
    }
    
    /**
     * 
     * This method...
     * @return
     */
    public Date getBeginDate(){
        return beginDate;
    }
    
    /**
     * 
     * This method...
     * @param argEndDate
     */
    public void setEndDate(Date argEndDate){
        endDate = argEndDate;
    }
    
    /**
     * 
     * This method...
     * @return
     */
    public Date getEndDate(){
        return endDate;
    }
    
    /**
     * 
     * This method...
     * @param argTotalExpense
     */
    public void setTotalExpense(KualiDecimal argTotalExpense){
        totalExpense = argTotalExpense;
    }
    
    /**
     * 
     * This method...
     * @return
     */
    public KualiDecimal getTotalExpense(){
        return totalExpense;
    }
    
    /**
     * 
     * This method...
     * @param argDestination
     */
    public void setDestination(String argDestination){
        destination = argDestination;
    }
    
    /**
     * 
     * This method...
     * @return
     */
    public String getDestination(){
        return destination;
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
     * Gets the value of Other
     *
     * @return the value of Other
     */
    public JRDataSource getActualExpenses() {
        return this.actualExpenses;
    }
    
    /**
     * Sets the value of Other
     *
     * @param argOther Value to assign to this.Other
     */
    public void setActualExpenses(final Collection<Detail> argOther) {
        this.actualExpenses = convertReportData(argOther);
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

	/**
	 * Gets the barcodeImage attribute. 
	 * @return Returns the barcodeImage.
	 */
	public BufferedImage getBarcodeImage() {
		return barcodeImage;
	}

	/**
	 * Sets the barcodeImage attribute value.
	 * @param barcodeImage The barcodeImage to set.
	 */
	public void setBarcodeImage(BufferedImage barcodeImage) {
		this.barcodeImage = barcodeImage;
	}

	/**
	 * Gets the certificationDescription attribute. 
	 * @return Returns the certificationDescription.
	 */
	public String getCertificationDescription() {
		return certificationDescription;
	}

	/**
	 * Sets the certificationDescription attribute value.
	 * @param certificationDescription The certificationDescription to set.
	 */
	public void setCertificationDescription(String certificationDescription) {
		this.certificationDescription = certificationDescription;
	}

	/**
	 * Gets the expenseDetails attribute. 
	 * @return Returns the expenseDetails.
	 */
	public List<NonEmployeeCertificationReport.Detail> getExpenseDetails() {
		return expenseDetails;
	}

	/**
	 * Sets the expenseDetails attribute value.
	 * @param expenseDetails The expenseDetails to set.
	 */
	public void setExpenseDetails(List<NonEmployeeCertificationReport.Detail> expenseDetails) {
		this.expenseDetails = expenseDetails;
	}

}
