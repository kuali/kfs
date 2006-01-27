/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.financial.bo;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.TypedArrayList;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class DisbursementVoucherNonEmployeeTravel extends BusinessObjectBase {

    private String financialDocumentNumber;
    private String disbVchrTravelFromCityName;
    private String disbVchrTravelFromStateCode;
    private String dvTravelFromCountryName;
    private String disbVchrTravelToCityName;
    private String disbVchrTravelToStateCode;
    private String disbVchrTravelToCountryName;
    private Timestamp dvPerdiemStartDttmStamp;
    private Timestamp dvPerdiemEndDttmStamp;
    private KualiDecimal disbVchrPerdiemCalculatedAmt;
    private KualiDecimal disbVchrPerdiemActualAmount;
    private String dvPerdiemChangeReasonText;
    private String disbVchrServicePerformedDesc;
    private String dvServicePerformedLocName;
    private String dvServiceRegularEmprName;
    private String disbVchrAutoFromCityName;
    private String disbVchrAutoFromStateCode;
    private String disbVchrAutoToCityName;
    private String disbVchrAutoToStateCode;
    private boolean disbVchrAutoRoundTripCode;
    private Integer dvPersonalCarMileageAmount;
    private KualiDecimal disbVchrPersonalCarRate;
    private KualiDecimal disbVchrPersonalCarAmount;
    private String disbVchrExceptionCode;
    private Integer financialDocumentNextLineNbr;
    private String disbVchrNonEmpTravelerName;
    private KualiDecimal disbVchrPerdiemRate;
    private String disbVchrPerdiemCategoryName;
    private KualiDecimal disbVchrMileageCalculatedAmt;
    private KualiDecimal totalTravelAmount;

    private Date disbVchrTravelStartDate;
    private String disbVchrTravelStartTime;
    private boolean disbVchrTravelStartPM;
    private Date disbVchrTravelEndDate;
    private String disbVchrTravelEndTime;
    private boolean disbVchrTravelEndPM;


    private List dvNonEmployeeExpenses;

    /**
     * Default no-arg constructor.
     */
    public DisbursementVoucherNonEmployeeTravel() {
        dvNonEmployeeExpenses = new TypedArrayList(DisbursementVoucherNonEmployeeExpense.class);
        financialDocumentNextLineNbr = new Integer(1);
    }

    /**
     * Gets the financialDocumentNumber attribute.
     * 
     * @return - Returns the financialDocumentNumber
     *  
     */
    public String getFinancialDocumentNumber() {
        return financialDocumentNumber;
    }


    /**
     * Sets the financialDocumentNumber attribute.
     * 
     * @param - financialDocumentNumber The financialDocumentNumber to set.
     *  
     */
    public void setFinancialDocumentNumber(String financialDocumentNumber) {
        this.financialDocumentNumber = financialDocumentNumber;
    }

    /**
     * Gets the disbVchrTravelFromCityName attribute.
     * 
     * @return - Returns the disbVchrTravelFromCityName
     *  
     */
    public String getDisbVchrTravelFromCityName() {
        return disbVchrTravelFromCityName;
    }


    /**
     * Sets the disbVchrTravelFromCityName attribute.
     * 
     * @param - disbVchrTravelFromCityName The disbVchrTravelFromCityName to set.
     *  
     */
    public void setDisbVchrTravelFromCityName(String disbVchrTravelFromCityName) {
        this.disbVchrTravelFromCityName = disbVchrTravelFromCityName;
    }

    /**
     * Gets the disbVchrTravelFromStateCode attribute.
     * 
     * @return - Returns the disbVchrTravelFromStateCode
     *  
     */
    public String getDisbVchrTravelFromStateCode() {
        return disbVchrTravelFromStateCode;
    }


    /**
     * Sets the disbVchrTravelFromStateCode attribute.
     * 
     * @param - disbVchrTravelFromStateCode The disbVchrTravelFromStateCode to set.
     *  
     */
    public void setDisbVchrTravelFromStateCode(String disbVchrTravelFromStateCode) {
        this.disbVchrTravelFromStateCode = disbVchrTravelFromStateCode;
    }

    /**
     * Gets the dvTravelFromCountryName attribute.
     * 
     * @return - Returns the dvTravelFromCountryName
     *  
     */
    public String getDvTravelFromCountryName() {
        return dvTravelFromCountryName;
    }


    /**
     * Sets the dvTravelFromCountryName attribute.
     * 
     * @param - dvTravelFromCountryName The dvTravelFromCountryName to set.
     *  
     */
    public void setDvTravelFromCountryName(String dvTravelFromCountryName) {
        this.dvTravelFromCountryName = dvTravelFromCountryName;
    }

    /**
     * Gets the disbVchrTravelToCityName attribute.
     * 
     * @return - Returns the disbVchrTravelToCityName
     *  
     */
    public String getDisbVchrTravelToCityName() {
        return disbVchrTravelToCityName;
    }


    /**
     * Sets the disbVchrTravelToCityName attribute.
     * 
     * @param - disbVchrTravelToCityName The disbVchrTravelToCityName to set.
     *  
     */
    public void setDisbVchrTravelToCityName(String disbVchrTravelToCityName) {
        this.disbVchrTravelToCityName = disbVchrTravelToCityName;
    }

    /**
     * Gets the disbVchrTravelToStateCode attribute.
     * 
     * @return - Returns the disbVchrTravelToStateCode
     *  
     */
    public String getDisbVchrTravelToStateCode() {
        return disbVchrTravelToStateCode;
    }


    /**
     * Sets the disbVchrTravelToStateCode attribute.
     * 
     * @param - disbVchrTravelToStateCode The disbVchrTravelToStateCode to set.
     *  
     */
    public void setDisbVchrTravelToStateCode(String disbVchrTravelToStateCode) {
        this.disbVchrTravelToStateCode = disbVchrTravelToStateCode;
    }

    /**
     * Gets the disbVchrTravelToCountryName attribute.
     * 
     * @return - Returns the disbVchrTravelToCountryName
     *  
     */
    public String getDisbVchrTravelToCountryName() {
        return disbVchrTravelToCountryName;
    }


    /**
     * Sets the disbVchrTravelToCountryName attribute.
     * 
     * @param - disbVchrTravelToCountryName The disbVchrTravelToCountryName to set.
     *  
     */
    public void setDisbVchrTravelToCountryName(String disbVchrTravelToCountryName) {
        this.disbVchrTravelToCountryName = disbVchrTravelToCountryName;
    }

    /**
     * Gets the dvPerdiemStartDttmStamp attribute.
     * 
     * @return - Returns the dvPerdiemStartDttmStamp
     *  
     */
    public Timestamp getDvPerdiemStartDttmStamp() {
        return dvPerdiemStartDttmStamp;
    }


    /**
     * Sets the dvPerdiemStartDttmStamp attribute.
     * 
     * @param - dvPerdiemStartDttmStamp The dvPerdiemStartDttmStamp to set.
     *  
     */
    public void setDvPerdiemStartDttmStamp(Timestamp dvPerdiemStartDttmStamp) {
        this.dvPerdiemStartDttmStamp = dvPerdiemStartDttmStamp;
    }

    /**
     * Gets the dvPerdiemEndDttmStamp attribute.
     * 
     * @return - Returns the dvPerdiemEndDttmStamp
     *  
     */
    public Timestamp getDvPerdiemEndDttmStamp() {
        return dvPerdiemEndDttmStamp;
    }


    /**
     * Sets the dvPerdiemEndDttmStamp attribute.
     * 
     * @param - dvPerdiemEndDttmStamp The dvPerdiemEndDttmStamp to set.
     *  
     */
    public void setDvPerdiemEndDttmStamp(Timestamp dvPerdiemEndDttmStamp) {
        this.dvPerdiemEndDttmStamp = dvPerdiemEndDttmStamp;
    }

    /**
     * Gets the disbVchrPerdiemCalculatedAmt attribute.
     * 
     * @return - Returns the disbVchrPerdiemCalculatedAmt
     *  
     */
    public KualiDecimal getDisbVchrPerdiemCalculatedAmt() {
        return disbVchrPerdiemCalculatedAmt;
    }


    /**
     * Sets the disbVchrPerdiemCalculatedAmt attribute.
     * 
     * @param - disbVchrPerdiemCalculatedAmt The disbVchrPerdiemCalculatedAmt to set.
     *  
     */
    public void setDisbVchrPerdiemCalculatedAmt(KualiDecimal disbVchrPerdiemCalculatedAmt) {
        this.disbVchrPerdiemCalculatedAmt = disbVchrPerdiemCalculatedAmt;
    }

    /**
     * Gets the disbVchrPerdiemActualAmount attribute.
     * 
     * @return - Returns the disbVchrPerdiemActualAmount
     *  
     */
    public KualiDecimal getDisbVchrPerdiemActualAmount() {
        return disbVchrPerdiemActualAmount;
    }


    /**
     * Sets the disbVchrPerdiemActualAmount attribute.
     * 
     * @param - disbVchrPerdiemActualAmount The disbVchrPerdiemActualAmount to set.
     *  
     */
    public void setDisbVchrPerdiemActualAmount(KualiDecimal disbVchrPerdiemActualAmount) {
        this.disbVchrPerdiemActualAmount = disbVchrPerdiemActualAmount;
    }

    /**
     * Gets the dvPerdiemChangeReasonText attribute.
     * 
     * @return - Returns the dvPerdiemChangeReasonText
     *  
     */
    public String getDvPerdiemChangeReasonText() {
        return dvPerdiemChangeReasonText;
    }


    /**
     * Sets the dvPerdiemChangeReasonText attribute.
     * 
     * @param - dvPerdiemChangeReasonText The dvPerdiemChangeReasonText to set.
     *  
     */
    public void setDvPerdiemChangeReasonText(String dvPerdiemChangeReasonText) {
        this.dvPerdiemChangeReasonText = dvPerdiemChangeReasonText;
    }

    /**
     * Gets the disbVchrServicePerformedDesc attribute.
     * 
     * @return - Returns the disbVchrServicePerformedDesc
     *  
     */
    public String getDisbVchrServicePerformedDesc() {
        return disbVchrServicePerformedDesc;
    }


    /**
     * Sets the disbVchrServicePerformedDesc attribute.
     * 
     * @param - disbVchrServicePerformedDesc The disbVchrServicePerformedDesc to set.
     *  
     */
    public void setDisbVchrServicePerformedDesc(String disbVchrServicePerformedDesc) {
        this.disbVchrServicePerformedDesc = disbVchrServicePerformedDesc;
    }

    /**
     * Gets the dvServicePerformedLocName attribute.
     * 
     * @return - Returns the dvServicePerformedLocName
     *  
     */
    public String getDvServicePerformedLocName() {
        return dvServicePerformedLocName;
    }


    /**
     * Sets the dvServicePerformedLocName attribute.
     * 
     * @param - dvServicePerformedLocName The dvServicePerformedLocName to set.
     *  
     */
    public void setDvServicePerformedLocName(String dvServicePerformedLocName) {
        this.dvServicePerformedLocName = dvServicePerformedLocName;
    }

    /**
     * Gets the dvServiceRegularEmprName attribute.
     * 
     * @return - Returns the dvServiceRegularEmprName
     *  
     */
    public String getDvServiceRegularEmprName() {
        return dvServiceRegularEmprName;
    }


    /**
     * Sets the dvServiceRegularEmprName attribute.
     * 
     * @param - dvServiceRegularEmprName The dvServiceRegularEmprName to set.
     *  
     */
    public void setDvServiceRegularEmprName(String dvServiceRegularEmprName) {
        this.dvServiceRegularEmprName = dvServiceRegularEmprName;
    }

    /**
     * Gets the disbVchrAutoFromCityName attribute.
     * 
     * @return - Returns the disbVchrAutoFromCityName
     *  
     */
    public String getDisbVchrAutoFromCityName() {
        return disbVchrAutoFromCityName;
    }


    /**
     * Sets the disbVchrAutoFromCityName attribute.
     * 
     * @param - disbVchrAutoFromCityName The disbVchrAutoFromCityName to set.
     *  
     */
    public void setDisbVchrAutoFromCityName(String disbVchrAutoFromCityName) {
        this.disbVchrAutoFromCityName = disbVchrAutoFromCityName;
    }

    /**
     * Gets the disbVchrAutoFromStateCode attribute.
     * 
     * @return - Returns the disbVchrAutoFromStateCode
     *  
     */
    public String getDisbVchrAutoFromStateCode() {
        return disbVchrAutoFromStateCode;
    }


    /**
     * Sets the disbVchrAutoFromStateCode attribute.
     * 
     * @param - disbVchrAutoFromStateCode The disbVchrAutoFromStateCode to set.
     *  
     */
    public void setDisbVchrAutoFromStateCode(String disbVchrAutoFromStateCode) {
        this.disbVchrAutoFromStateCode = disbVchrAutoFromStateCode;
    }

    /**
     * Gets the disbVchrAutoToCityName attribute.
     * 
     * @return - Returns the disbVchrAutoToCityName
     *  
     */
    public String getDisbVchrAutoToCityName() {
        return disbVchrAutoToCityName;
    }


    /**
     * Sets the disbVchrAutoToCityName attribute.
     * 
     * @param - disbVchrAutoToCityName The disbVchrAutoToCityName to set.
     *  
     */
    public void setDisbVchrAutoToCityName(String disbVchrAutoToCityName) {
        this.disbVchrAutoToCityName = disbVchrAutoToCityName;
    }

    /**
     * Gets the disbVchrAutoToStateCode attribute.
     * 
     * @return - Returns the disbVchrAutoToStateCode
     *  
     */
    public String getDisbVchrAutoToStateCode() {
        return disbVchrAutoToStateCode;
    }


    /**
     * Sets the disbVchrAutoToStateCode attribute.
     * 
     * @param - disbVchrAutoToStateCode The disbVchrAutoToStateCode to set.
     *  
     */
    public void setDisbVchrAutoToStateCode(String disbVchrAutoToStateCode) {
        this.disbVchrAutoToStateCode = disbVchrAutoToStateCode;
    }

    /**
     * Gets the disbVchrAutoRoundTripCode attribute.
     * 
     * @return - Returns the disbVchrAutoRoundTripCode
     *  
     */
    public boolean getDisbVchrAutoRoundTripCode() {
        return disbVchrAutoRoundTripCode;
    }


    /**
     * Sets the disbVchrAutoRoundTripCode attribute.
     * 
     * @param - disbVchrAutoRoundTripCode The disbVchrAutoRoundTripCode to set.
     *  
     */
    public void setDisbVchrAutoRoundTripCode(boolean disbVchrAutoRoundTripCode) {
        this.disbVchrAutoRoundTripCode = disbVchrAutoRoundTripCode;
    }

    /**
     * Gets the dvPersonalCarMileageAmount attribute.
     * 
     * @return - Returns the dvPersonalCarMileageAmount
     *  
     */
    public Integer getDvPersonalCarMileageAmount() {
        return dvPersonalCarMileageAmount;
    }


    /**
     * Sets the dvPersonalCarMileageAmount attribute.
     * 
     * @param - dvPersonalCarMileageAmount The dvPersonalCarMileageAmount to set.
     *  
     */
    public void setDvPersonalCarMileageAmount(Integer dvPersonalCarMileageAmount) {
        this.dvPersonalCarMileageAmount = dvPersonalCarMileageAmount;
    }

    /**
     * Gets the disbVchrPersonalCarRate attribute.
     * 
     * @return - Returns the disbVchrPersonalCarRate
     *  
     */
    public KualiDecimal getDisbVchrPersonalCarRate() {
        return disbVchrPersonalCarRate;
    }


    /**
     * Sets the disbVchrPersonalCarRate attribute.
     * 
     * @param - disbVchrPersonalCarRate The disbVchrPersonalCarRate to set.
     *  
     */
    public void setDisbVchrPersonalCarRate(KualiDecimal disbVchrPersonalCarRate) {
        this.disbVchrPersonalCarRate = disbVchrPersonalCarRate;
    }

    /**
     * Gets the disbVchrPersonalCarAmount attribute.
     * 
     * @return - Returns the disbVchrPersonalCarAmount
     *  
     */
    public KualiDecimal getDisbVchrPersonalCarAmount() {
        return disbVchrPersonalCarAmount;
    }


    /**
     * Sets the disbVchrPersonalCarAmount attribute.
     * 
     * @param - disbVchrPersonalCarAmount The disbVchrPersonalCarAmount to set.
     *  
     */
    public void setDisbVchrPersonalCarAmount(KualiDecimal disbVchrPersonalCarAmount) {
        this.disbVchrPersonalCarAmount = disbVchrPersonalCarAmount;
    }

    /**
     * Gets the disbVchrExceptionCode attribute.
     * 
     * @return - Returns the disbVchrExceptionCode
     *  
     */
    public String getDisbVchrExceptionCode() {
        return disbVchrExceptionCode;
    }


    /**
     * Sets the disbVchrExceptionCode attribute.
     * 
     * @param - disbVchrExceptionCode The disbVchrExceptionCode to set.
     *  
     */
    public void setDisbVchrExceptionCode(String disbVchrExceptionCode) {
        this.disbVchrExceptionCode = disbVchrExceptionCode;
    }

    /**
     * Gets the financialDocumentNextLineNbr attribute.
     * 
     * @return - Returns the financialDocumentNextLineNbr
     *  
     */
    public Integer getFinancialDocumentNextLineNbr() {
        return financialDocumentNextLineNbr;
    }


    /**
     * Sets the financialDocumentNextLineNbr attribute.
     * 
     * @param - financialDocumentNextLineNbr The financialDocumentNextLineNbr to set.
     *  
     */
    public void setFinancialDocumentNextLineNbr(Integer financialDocumentNextLineNbr) {
        this.financialDocumentNextLineNbr = financialDocumentNextLineNbr;
    }

    /**
     * Gets the disbVchrNonEmpTravelerName attribute.
     * 
     * @return - Returns the disbVchrNonEmpTravelerName
     *  
     */
    public String getDisbVchrNonEmpTravelerName() {
        return disbVchrNonEmpTravelerName;
    }


    /**
     * Sets the disbVchrNonEmpTravelerName attribute.
     * 
     * @param - disbVchrNonEmpTravelerName The disbVchrNonEmpTravelerName to set.
     *  
     */
    public void setDisbVchrNonEmpTravelerName(String disbVchrNonEmpTravelerName) {
        this.disbVchrNonEmpTravelerName = disbVchrNonEmpTravelerName;
    }

    /**
     * Gets the disbVchrPerdiemRate attribute.
     * 
     * @return - Returns the disbVchrPerdiemRate
     *  
     */
    public KualiDecimal getDisbVchrPerdiemRate() {
        return disbVchrPerdiemRate;
    }


    /**
     * Sets the disbVchrPerdiemRate attribute.
     * 
     * @param - disbVchrPerdiemRate The disbVchrPerdiemRate to set.
     *  
     */
    public void setDisbVchrPerdiemRate(KualiDecimal disbVchrPerdiemRate) {
        this.disbVchrPerdiemRate = disbVchrPerdiemRate;
    }

    /**
     * Gets the disbVchrPerdiemCategoryName attribute.
     * 
     * @return - Returns the disbVchrPerdiemCategoryName
     *  
     */
    public String getDisbVchrPerdiemCategoryName() {
        return disbVchrPerdiemCategoryName;
    }


    /**
     * Sets the disbVchrPerdiemCategoryName attribute.
     * 
     * @param - disbVchrPerdiemCategoryName The disbVchrPerdiemCategoryName to set.
     *  
     */
    public void setDisbVchrPerdiemCategoryName(String disbVchrPerdiemCategoryName) {
        this.disbVchrPerdiemCategoryName = disbVchrPerdiemCategoryName;
    }

    /**
     * Gets the disbVchrMileageCalculatedAmt attribute.
     * 
     * @return - Returns the disbVchrMileageCalculatedAmt
     *  
     */
    public KualiDecimal getDisbVchrMileageCalculatedAmt() {
        return disbVchrMileageCalculatedAmt;
    }


    /**
     * Sets the disbVchrMileageCalculatedAmt attribute.
     * 
     * @param - disbVchrMileageCalculatedAmt The disbVchrMileageCalculatedAmt to set.
     *  
     */
    public void setDisbVchrMileageCalculatedAmt(KualiDecimal disbVchrMileageCalculatedAmt) {
        this.disbVchrMileageCalculatedAmt = disbVchrMileageCalculatedAmt;
    }

    /**
     * Gets the dvNonEmployeeExpenses attribute.
     * 
     * @return Returns the dvNonEmployeeExpenses.
     */
    public List getDvNonEmployeeExpenses() {
        return dvNonEmployeeExpenses;
    }

    /**
     * Sets the dvNonEmployeeExpenses attribute value.
     * 
     * @param dvNonEmployeeExpenses The dvNonEmployeeExpenses to set.
     */
    public void setDvNonEmployeeExpenses(List dvNonEmployeeExpenses) {
        this.dvNonEmployeeExpenses = dvNonEmployeeExpenses;
    }

    /**
     * Adds a dv non employee expense line
     * @param line
     */
    public void addDvNonEmployeeExpenseLine(DisbursementVoucherNonEmployeeExpense line) {
        line.setFinancialDocumentLineNumber(getFinancialDocumentNextLineNbr());
        this.dvNonEmployeeExpenses.add(line);
        this.financialDocumentNextLineNbr = new Integer(getFinancialDocumentNextLineNbr().intValue() + 1);
    }

    /**
     * @return Returns the disbVchrTravelEndDate.
     */
    public Date getDisbVchrTravelEndDate() {
        return disbVchrTravelEndDate;
    }

    /**
     * @param disbVchrTravelEndDate The disbVchrTravelEndDate to set.
     */
    public void setDisbVchrTravelEndDate(Date disbVchrTravelEndDate) {
        this.disbVchrTravelEndDate = disbVchrTravelEndDate;
    }

    /**
     * @return Returns the disbVchrTravelEndTime.
     */
    public String getDisbVchrTravelEndTime() {
        String endTime = "";

        if (dvPerdiemEndDttmStamp != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dvPerdiemEndDttmStamp);
            endTime = (new Integer(cal.get(Calendar.HOUR))).toString() + ":";
            endTime += (new Integer(cal.get(Calendar.MINUTE))).toString();
        }
        return endTime;
    }

    /**
     * @param disbVchrTravelEndTime The disbVchrTravelEndTime to set.
     */
    public void setDisbVchrTravelEndTime(String disbVchrTravelEndTime) {
        this.disbVchrTravelEndTime = disbVchrTravelEndTime;
    }

    /**
     * @return Returns the disbVchrTravelStartDate.
     */
    public Date getDisbVchrTravelStartDate() {
        return disbVchrTravelStartDate;
    }

    /**
     * @param disbVchrTravelStartDate The disbVchrTravelStartDate to set.
     */
    public void setDisbVchrTravelStartDate(Date disbVchrTravelStartDate) {
        this.disbVchrTravelStartDate = disbVchrTravelStartDate;
    }

    /**
     * @return Returns the disbVchrTravelStartTime.
     */
    public String getDisbVchrTravelStartTime() {
        String startTime = "";

        if (dvPerdiemStartDttmStamp != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dvPerdiemStartDttmStamp);
            startTime = (new Integer(cal.get(Calendar.HOUR))).toString() + ":";
            startTime += (new Integer(cal.get(Calendar.MINUTE))).toString();
        }
        return startTime;
    }

    /**
     * @param disbVchrTravelStartTime The disbVchrTravelStartTime to set.
     */
    public void setDisbVchrTravelStartTime(String disbVchrTravelStartTime) {
        this.disbVchrTravelStartTime = disbVchrTravelStartTime;
    }


    /**
     * @return Returns the disbVchrTravelEndPM.
     */
    public boolean isDisbVchrTravelEndPM() {
        if (dvPerdiemEndDttmStamp != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dvPerdiemEndDttmStamp);
            if (Calendar.PM == cal.get(Calendar.AM_PM)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param disbVchrTravelEndPM The disbVchrTravelEndPM to set.
     */
    public void setDisbVchrTravelEndPM(boolean disbVchrTravelEndPM) {
        this.disbVchrTravelEndPM = disbVchrTravelEndPM;
    }

    /**
     * @return Returns the disbVchrTravelStartPM.
     */
    public boolean isDisbVchrTravelStartPM() {
        if (dvPerdiemStartDttmStamp != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dvPerdiemStartDttmStamp);
            if (Calendar.PM == cal.get(Calendar.AM_PM)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param disbVchrTravelStartPM The disbVchrTravelStartPM to set.
     */
    public void setDisbVchrTravelStartPM(boolean disbVchrTravelStartPM) {
        this.disbVchrTravelStartPM = disbVchrTravelStartPM;
    }

    /**
     * Calculates the total travel amount.
     */
    public KualiDecimal getTotalTravelAmount() {
        KualiDecimal travelAmount = new KualiDecimal(0);

        // get non paid expenses first
        if (dvNonEmployeeExpenses != null) {
            for (Iterator iter = dvNonEmployeeExpenses.iterator(); iter.hasNext();) {
                DisbursementVoucherNonEmployeeExpense element = (DisbursementVoucherNonEmployeeExpense) iter.next();
                // don't add prepaid expenses to travel total
                travelAmount = travelAmount.add(element.getDisbVchrExpenseAmount());
            }
        }
        // add in per diem amount
        if (disbVchrPerdiemActualAmount != null) {
            travelAmount = travelAmount.add(disbVchrPerdiemActualAmount);
        }
        // add in personnal car amount
        if (disbVchrPersonalCarAmount != null) {
            travelAmount = travelAmount.add(disbVchrPersonalCarAmount);
        }

        return travelAmount;
    }

    /**
     * @param totalTravelAmount The totalTravelAmount to set.
     */
    public void setTotalTravelAmount(KualiDecimal totalTravelAmount) {
        this.totalTravelAmount = totalTravelAmount;
    }

    /**
     * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialDocumentNumber", this.financialDocumentNumber);
        return m;
    }
}