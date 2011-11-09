/*
 * Copyright 2005-2006 The Kuali Foundation
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

package org.kuali.kfs.fp.businessobject;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class is used to represent a non-employee trip for a disbursement voucher .
 */
public class DisbursementVoucherNonEmployeeTravel extends PersistableBusinessObjectBase {

    private String documentNumber;
    private String disbVchrTravelFromCityName;
    private String disbVchrTravelFromStateCode;
    private String dvTravelFromCountryCode;
    private String disbVchrTravelToCityName;
    private String disbVchrTravelToStateCode;
    private String disbVchrTravelToCountryCode;
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
    private boolean disbVchrExceptionCode;
    private Integer financialDocumentNextLineNbr;
    private String disbVchrNonEmpTravelerName;
    private KualiDecimal disbVchrPerdiemRate;
    private String disbVchrPerdiemCategoryName;
    private KualiDecimal disbVchrMileageCalculatedAmt;

    private KualiDecimal totalTravelAmount;

    private List dvNonEmployeeExpenses;
    private List dvPrePaidEmployeeExpenses;

    /**
     * Default no-arg constructor.
     */
    public DisbursementVoucherNonEmployeeTravel() {
        dvNonEmployeeExpenses = new ArrayList<DisbursementVoucherNonEmployeeExpense>();
        dvPrePaidEmployeeExpenses = new ArrayList<DisbursementVoucherNonEmployeeExpense>();
        financialDocumentNextLineNbr = new Integer(1);
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }


    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the disbVchrTravelFromCityName attribute.
     * 
     * @return Returns the disbVchrTravelFromCityName
     */
    public String getDisbVchrTravelFromCityName() {
        return disbVchrTravelFromCityName;
    }


    /**
     * Sets the disbVchrTravelFromCityName attribute.
     * 
     * @param disbVchrTravelFromCityName The disbVchrTravelFromCityName to set.
     */
    public void setDisbVchrTravelFromCityName(String disbVchrTravelFromCityName) {
        this.disbVchrTravelFromCityName = disbVchrTravelFromCityName;
    }

    /**
     * Gets the disbVchrTravelFromStateCode attribute.
     * 
     * @return Returns the disbVchrTravelFromStateCode
     */
    public String getDisbVchrTravelFromStateCode() {
        return disbVchrTravelFromStateCode;
    }


    /**
     * Sets the disbVchrTravelFromStateCode attribute.
     * 
     * @param disbVchrTravelFromStateCode The disbVchrTravelFromStateCode to set.
     */
    public void setDisbVchrTravelFromStateCode(String disbVchrTravelFromStateCode) {
        this.disbVchrTravelFromStateCode = disbVchrTravelFromStateCode;
    }

    /**
     * Gets the dvTravelFromCountryCode attribute.
     * 
     * @return Returns the dvTravelFromCountryCode
     */
    public String getDvTravelFromCountryCode() {
        return dvTravelFromCountryCode;
    }


    /**
     * Sets the dvTravelFromCountryCode attribute.
     * 
     * @param dvTravelFromCountryCode The dvTravelFromCountryCode to set.
     */
    public void setDvTravelFromCountryCode(String dvTravelFromCountryCode) {
        this.dvTravelFromCountryCode = dvTravelFromCountryCode;
    }

    /**
     * Gets the disbVchrTravelToCityName attribute.
     * 
     * @return Returns the disbVchrTravelToCityName
     */
    public String getDisbVchrTravelToCityName() {
        return disbVchrTravelToCityName;
    }


    /**
     * Sets the disbVchrTravelToCityName attribute.
     * 
     * @param disbVchrTravelToCityName The disbVchrTravelToCityName to set.
     */
    public void setDisbVchrTravelToCityName(String disbVchrTravelToCityName) {
        this.disbVchrTravelToCityName = disbVchrTravelToCityName;
    }

    /**
     * Gets the disbVchrTravelToStateCode attribute.
     * 
     * @return Returns the disbVchrTravelToStateCode
     */
    public String getDisbVchrTravelToStateCode() {
        return disbVchrTravelToStateCode;
    }


    /**
     * Sets the disbVchrTravelToStateCode attribute.
     * 
     * @param disbVchrTravelToStateCode The disbVchrTravelToStateCode to set.
     */
    public void setDisbVchrTravelToStateCode(String disbVchrTravelToStateCode) {
        this.disbVchrTravelToStateCode = disbVchrTravelToStateCode;
    }

    /**
     * Gets the disbVchrTravelToCountryCode attribute.
     * 
     * @return Returns the disbVchrTravelToCountryCode
     */
    public String getDisbVchrTravelToCountryCode() {
        return disbVchrTravelToCountryCode;
    }


    /**
     * Sets the disbVchrTravelToCountryCode attribute.
     * 
     * @param disbVchrTravelToCountryCode The disbVchrTravelToCountryCode to set.
     */
    public void setDisbVchrTravelToCountryCode(String disbVchrTravelToCountryCode) {
        this.disbVchrTravelToCountryCode = disbVchrTravelToCountryCode;
    }

    /**
     * Gets the dvPerdiemStartDttmStamp attribute.
     * 
     * @return Returns the dvPerdiemStartDttmStamp
     */
    public Timestamp getDvPerdiemStartDttmStamp() {
        return dvPerdiemStartDttmStamp;
    }


    /**
     * Sets the dvPerdiemStartDttmStamp attribute.
     * 
     * @param dvPerdiemStartDttmStamp The dvPerdiemStartDttmStamp to set.
     */
    public void setDvPerdiemStartDttmStamp(Timestamp dvPerdiemStartDttmStamp) {
        this.dvPerdiemStartDttmStamp = dvPerdiemStartDttmStamp;
    }

    /**
     * Gets the dvPerdiemEndDttmStamp attribute.
     * 
     * @return Returns the dvPerdiemEndDttmStamp
     */
    public Timestamp getDvPerdiemEndDttmStamp() {
        return dvPerdiemEndDttmStamp;
    }


    /**
     * Sets the dvPerdiemEndDttmStamp attribute.
     * 
     * @param dvPerdiemEndDttmStamp The dvPerdiemEndDttmStamp to set.
     */
    public void setDvPerdiemEndDttmStamp(Timestamp dvPerdiemEndDttmStamp) {
        this.dvPerdiemEndDttmStamp = dvPerdiemEndDttmStamp;
    }

    /**
     * Gets the disbVchrPerdiemCalculatedAmt attribute.
     * 
     * @return Returns the disbVchrPerdiemCalculatedAmt
     */
    public KualiDecimal getDisbVchrPerdiemCalculatedAmt() {
        return disbVchrPerdiemCalculatedAmt;
    }


    /**
     * Sets the disbVchrPerdiemCalculatedAmt attribute.
     * 
     * @param disbVchrPerdiemCalculatedAmt The disbVchrPerdiemCalculatedAmt to set.
     */
    public void setDisbVchrPerdiemCalculatedAmt(KualiDecimal disbVchrPerdiemCalculatedAmt) {
        this.disbVchrPerdiemCalculatedAmt = disbVchrPerdiemCalculatedAmt;
    }

    /**
     * Gets the disbVchrPerdiemActualAmount attribute.
     * 
     * @return Returns the disbVchrPerdiemActualAmount
     */
    public KualiDecimal getDisbVchrPerdiemActualAmount() {
        return disbVchrPerdiemActualAmount;
    }


    /**
     * Sets the disbVchrPerdiemActualAmount attribute.
     * 
     * @param disbVchrPerdiemActualAmount The disbVchrPerdiemActualAmount to set.
     */
    public void setDisbVchrPerdiemActualAmount(KualiDecimal disbVchrPerdiemActualAmount) {
        this.disbVchrPerdiemActualAmount = disbVchrPerdiemActualAmount;
    }

    /**
     * Gets the dvPerdiemChangeReasonText attribute.
     * 
     * @return Returns the dvPerdiemChangeReasonText
     */
    public String getDvPerdiemChangeReasonText() {
        return dvPerdiemChangeReasonText;
    }


    /**
     * Sets the dvPerdiemChangeReasonText attribute.
     * 
     * @param dvPerdiemChangeReasonText The dvPerdiemChangeReasonText to set.
     */
    public void setDvPerdiemChangeReasonText(String dvPerdiemChangeReasonText) {
        this.dvPerdiemChangeReasonText = dvPerdiemChangeReasonText;
    }

    /**
     * Gets the disbVchrServicePerformedDesc attribute.
     * 
     * @return Returns the disbVchrServicePerformedDesc
     */
    public String getDisbVchrServicePerformedDesc() {
        return disbVchrServicePerformedDesc;
    }


    /**
     * Sets the disbVchrServicePerformedDesc attribute.
     * 
     * @param disbVchrServicePerformedDesc The disbVchrServicePerformedDesc to set.
     */
    public void setDisbVchrServicePerformedDesc(String disbVchrServicePerformedDesc) {
        this.disbVchrServicePerformedDesc = disbVchrServicePerformedDesc;
    }

    /**
     * Gets the dvServicePerformedLocName attribute.
     * 
     * @return Returns the dvServicePerformedLocName
     */
    public String getDvServicePerformedLocName() {
        return dvServicePerformedLocName;
    }


    /**
     * Sets the dvServicePerformedLocName attribute.
     * 
     * @param dvServicePerformedLocName The dvServicePerformedLocName to set.
     */
    public void setDvServicePerformedLocName(String dvServicePerformedLocName) {
        this.dvServicePerformedLocName = dvServicePerformedLocName;
    }

    /**
     * Gets the dvServiceRegularEmprName attribute.
     * 
     * @return Returns the dvServiceRegularEmprName
     */
    public String getDvServiceRegularEmprName() {
        return dvServiceRegularEmprName;
    }


    /**
     * Sets the dvServiceRegularEmprName attribute.
     * 
     * @param dvServiceRegularEmprName The dvServiceRegularEmprName to set.
     */
    public void setDvServiceRegularEmprName(String dvServiceRegularEmprName) {
        this.dvServiceRegularEmprName = dvServiceRegularEmprName;
    }

    /**
     * Gets the disbVchrAutoFromCityName attribute.
     * 
     * @return Returns the disbVchrAutoFromCityName
     */
    public String getDisbVchrAutoFromCityName() {
        return disbVchrAutoFromCityName;
    }


    /**
     * Sets the disbVchrAutoFromCityName attribute.
     * 
     * @param disbVchrAutoFromCityName The disbVchrAutoFromCityName to set.
     */
    public void setDisbVchrAutoFromCityName(String disbVchrAutoFromCityName) {
        this.disbVchrAutoFromCityName = disbVchrAutoFromCityName;
    }

    /**
     * Gets the disbVchrAutoFromStateCode attribute.
     * 
     * @return Returns the disbVchrAutoFromStateCode
     */
    public String getDisbVchrAutoFromStateCode() {
        return disbVchrAutoFromStateCode;
    }


    /**
     * Sets the disbVchrAutoFromStateCode attribute.
     * 
     * @param disbVchrAutoFromStateCode The disbVchrAutoFromStateCode to set.
     */
    public void setDisbVchrAutoFromStateCode(String disbVchrAutoFromStateCode) {
        this.disbVchrAutoFromStateCode = disbVchrAutoFromStateCode;
    }

    /**
     * Gets the disbVchrAutoToCityName attribute.
     * 
     * @return Returns the disbVchrAutoToCityName
     */
    public String getDisbVchrAutoToCityName() {
        return disbVchrAutoToCityName;
    }


    /**
     * Sets the disbVchrAutoToCityName attribute.
     * 
     * @param disbVchrAutoToCityName The disbVchrAutoToCityName to set.
     */
    public void setDisbVchrAutoToCityName(String disbVchrAutoToCityName) {
        this.disbVchrAutoToCityName = disbVchrAutoToCityName;
    }

    /**
     * Gets the disbVchrAutoToStateCode attribute.
     * 
     * @return Returns the disbVchrAutoToStateCode
     */
    public String getDisbVchrAutoToStateCode() {
        return disbVchrAutoToStateCode;
    }


    /**
     * Sets the disbVchrAutoToStateCode attribute.
     * 
     * @param disbVchrAutoToStateCode The disbVchrAutoToStateCode to set.
     */
    public void setDisbVchrAutoToStateCode(String disbVchrAutoToStateCode) {
        this.disbVchrAutoToStateCode = disbVchrAutoToStateCode;
    }

    /**
     * Gets the disbVchrAutoRoundTripCode attribute.
     * 
     * @return Returns the disbVchrAutoRoundTripCode
     */
    public boolean getDisbVchrAutoRoundTripCode() {
        return disbVchrAutoRoundTripCode;
    }


    /**
     * Sets the disbVchrAutoRoundTripCode attribute.
     * 
     * @param disbVchrAutoRoundTripCode The disbVchrAutoRoundTripCode to set.
     */
    public void setDisbVchrAutoRoundTripCode(boolean disbVchrAutoRoundTripCode) {
        this.disbVchrAutoRoundTripCode = disbVchrAutoRoundTripCode;
    }

    /**
     * Gets the dvPersonalCarMileageAmount attribute.
     * 
     * @return Returns the dvPersonalCarMileageAmount
     */
    public Integer getDvPersonalCarMileageAmount() {
        return dvPersonalCarMileageAmount;
    }


    /**
     * Sets the dvPersonalCarMileageAmount attribute.
     * 
     * @param dvPersonalCarMileageAmount The dvPersonalCarMileageAmount to set.
     */
    public void setDvPersonalCarMileageAmount(Integer dvPersonalCarMileageAmount) {
        this.dvPersonalCarMileageAmount = dvPersonalCarMileageAmount;
    }

    /**
     * Gets the disbVchrPersonalCarRate attribute.
     * 
     * @return Returns the disbVchrPersonalCarRate
     */
    public KualiDecimal getDisbVchrPersonalCarRate() {
        return disbVchrPersonalCarRate;
    }


    /**
     * Sets the disbVchrPersonalCarRate attribute.
     * 
     * @param disbVchrPersonalCarRate The disbVchrPersonalCarRate to set.
     */
    public void setDisbVchrPersonalCarRate(KualiDecimal disbVchrPersonalCarRate) {
        this.disbVchrPersonalCarRate = disbVchrPersonalCarRate;
    }

    /**
     * Gets the disbVchrPersonalCarAmount attribute.
     * 
     * @return Returns the disbVchrPersonalCarAmount
     */
    public KualiDecimal getDisbVchrPersonalCarAmount() {
        return dvPersonalCarMileageAmount == null ? null : disbVchrPersonalCarAmount;
    }


    /**
     * Sets the disbVchrPersonalCarAmount attribute.
     * 
     * @param disbVchrPersonalCarAmount The disbVchrPersonalCarAmount to set.
     */
    public void setDisbVchrPersonalCarAmount(KualiDecimal disbVchrPersonalCarAmount) {
        this.disbVchrPersonalCarAmount = disbVchrPersonalCarAmount;
    }

    /**
     * Gets the disbVchrExceptionCode attribute.
     * 
     * @return Returns the disbVchrExceptionCode
     */
    public boolean getDisbVchrExceptionCode() {
        return disbVchrExceptionCode;
    }


    /**
     * Sets the disbVchrExceptionCode attribute.
     * 
     * @param disbVchrExceptionCode The disbVchrExceptionCode to set.
     */
    public void setDisbVchrExceptionCode(boolean disbVchrExceptionCode) {
        this.disbVchrExceptionCode = disbVchrExceptionCode;
    }

    /**
     * Gets the financialDocumentNextLineNbr attribute.
     * 
     * @return Returns the financialDocumentNextLineNbr
     */
    public Integer getFinancialDocumentNextLineNbr() {
        return financialDocumentNextLineNbr;
    }


    /**
     * Sets the financialDocumentNextLineNbr attribute.
     * 
     * @param financialDocumentNextLineNbr The financialDocumentNextLineNbr to set.
     */
    public void setFinancialDocumentNextLineNbr(Integer financialDocumentNextLineNbr) {
        this.financialDocumentNextLineNbr = financialDocumentNextLineNbr;
    }

    /**
     * Gets the disbVchrNonEmpTravelerName attribute.
     * 
     * @return Returns the disbVchrNonEmpTravelerName
     */
    public String getDisbVchrNonEmpTravelerName() {
        return disbVchrNonEmpTravelerName;
    }


    /**
     * Sets the disbVchrNonEmpTravelerName attribute.
     * 
     * @param disbVchrNonEmpTravelerName The disbVchrNonEmpTravelerName to set.
     */
    public void setDisbVchrNonEmpTravelerName(String disbVchrNonEmpTravelerName) {
        this.disbVchrNonEmpTravelerName = disbVchrNonEmpTravelerName;
    }

    /**
     * Gets the disbVchrPerdiemRate attribute.
     * 
     * @return Returns the disbVchrPerdiemRate
     */
    public KualiDecimal getDisbVchrPerdiemRate() {
        return disbVchrPerdiemRate;
    }


    /**
     * Sets the disbVchrPerdiemRate attribute.
     * 
     * @param disbVchrPerdiemRate The disbVchrPerdiemRate to set.
     */
    public void setDisbVchrPerdiemRate(KualiDecimal disbVchrPerdiemRate) {
        this.disbVchrPerdiemRate = disbVchrPerdiemRate;
    }

    /**
     * Gets the disbVchrPerdiemCategoryName attribute.
     * 
     * @return Returns the disbVchrPerdiemCategoryName
     */
    public String getDisbVchrPerdiemCategoryName() {
        return disbVchrPerdiemCategoryName;
    }


    /**
     * Sets the disbVchrPerdiemCategoryName attribute.
     * 
     * @param disbVchrPerdiemCategoryName The disbVchrPerdiemCategoryName to set.
     */
    public void setDisbVchrPerdiemCategoryName(String disbVchrPerdiemCategoryName) {
        this.disbVchrPerdiemCategoryName = disbVchrPerdiemCategoryName;
    }

    /**
     * Gets the disbVchrMileageCalculatedAmt attribute.
     * 
     * @return Returns the disbVchrMileageCalculatedAmt
     */
    public KualiDecimal getDisbVchrMileageCalculatedAmt() {
        return dvPersonalCarMileageAmount == null ? null : disbVchrMileageCalculatedAmt;
    }


    /**
     * Sets the disbVchrMileageCalculatedAmt attribute.
     * 
     * @param disbVchrMileageCalculatedAmt The disbVchrMileageCalculatedAmt to set.
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
     * @return Returns the dvPrePaidEmployeeExpenses.
     */
    public List getDvPrePaidEmployeeExpenses() {
        return dvPrePaidEmployeeExpenses;
    }

    /**
     * @param dvPrePaidEmployeeExpenses The dvPrePaidEmployeeExpenses to set.
     */
    public void setDvPrePaidEmployeeExpenses(List dvPrePaidEmployeeExpenses) {
        this.dvPrePaidEmployeeExpenses = dvPrePaidEmployeeExpenses;
    }

    /**
     * Adds a dv non employee expense line
     * 
     * @param line
     */
    public void addDvNonEmployeeExpenseLine(DisbursementVoucherNonEmployeeExpense line) {
        line.setFinancialDocumentLineNumber(getFinancialDocumentNextLineNbr());
        this.dvNonEmployeeExpenses.add(line);
        this.financialDocumentNextLineNbr = new Integer(getFinancialDocumentNextLineNbr().intValue() + 1);
    }

    /**
     * Adds a dv pre paid expense line
     * 
     * @param line
     */
    public void addDvPrePaidEmployeeExpenseLine(DisbursementVoucherNonEmployeeExpense line) {
        line.setFinancialDocumentLineNumber(getFinancialDocumentNextLineNbr());
        this.dvPrePaidEmployeeExpenses.add(line);
        this.financialDocumentNextLineNbr = new Integer(getFinancialDocumentNextLineNbr().intValue() + 1);
    }

    /**
     * Returns the per diem start date time as a string representation.
     * 
     * @return
     */
    public String getPerDiemStartDateTime() {
        return SpringContext.getBean(DateTimeService.class).toDateTimeString(dvPerdiemStartDttmStamp);
    }

    /**
     * Sets the per diem start timestamp from the string representation.
     * 
     * @param perDiemStartDateTime
     */
    public void setPerDiemStartDateTime(String perDiemStartDateTime) {
        try {
            this.dvPerdiemStartDttmStamp = SpringContext.getBean(DateTimeService.class).convertToSqlTimestamp(perDiemStartDateTime);
        }
        catch (ParseException e) {
            this.dvPerdiemStartDttmStamp = null;
        }
    }

    /**
     * Returns the per diem end date time as a string representation.
     * 
     * @return String
     */
    public String getPerDiemEndDateTime() {
        return SpringContext.getBean(DateTimeService.class).toDateTimeString(dvPerdiemEndDttmStamp);
    }

    /**
     * Sets the per diem start timestamp from the string representation.
     * 
     * @param perDiemEndDateTime
     */
    public void setPerDiemEndDateTime(String perDiemEndDateTime) {
        try {
            this.dvPerdiemEndDttmStamp = SpringContext.getBean(DateTimeService.class).convertToSqlTimestamp(perDiemEndDateTime);
        }
        catch (ParseException e) {
            this.dvPerdiemEndDttmStamp = null;
        }
    }

    /**
     * Calculates the total pre paid expense amount
     * 
     * @return KualiDecimal
     */
    public KualiDecimal getTotalPrePaidAmount() {
        KualiDecimal totalPrePaidAmount = KualiDecimal.ZERO;
        if (dvPrePaidEmployeeExpenses != null) {
            for (Iterator iter = dvPrePaidEmployeeExpenses.iterator(); iter.hasNext();) {
                DisbursementVoucherNonEmployeeExpense element = (DisbursementVoucherNonEmployeeExpense) iter.next();
                if (ObjectUtils.isNotNull(element.getDisbVchrExpenseAmount())) {
                    totalPrePaidAmount = totalPrePaidAmount.add(element.getDisbVchrExpenseAmount());
                }
            }
        }

        return totalPrePaidAmount;
    }

    /**
     * Calculates the total expense amount
     * 
     * @return KualiDecimal
     */
    public KualiDecimal getTotalExpenseAmount() {
        KualiDecimal totalExpenseAmount = KualiDecimal.ZERO;
        if (dvNonEmployeeExpenses != null) {
            for (Iterator iter = dvNonEmployeeExpenses.iterator(); iter.hasNext();) {
                DisbursementVoucherNonEmployeeExpense element = (DisbursementVoucherNonEmployeeExpense) iter.next();
                if (ObjectUtils.isNotNull(element.getDisbVchrExpenseAmount())) {
                    totalExpenseAmount = totalExpenseAmount.add(element.getDisbVchrExpenseAmount());
                }
            }
        }
        return totalExpenseAmount;
    }

    /**
     * Calculates the total travel amount.
     * 
     * @return KualiDecimal
     */
    public KualiDecimal getTotalTravelAmount() {
        KualiDecimal travelAmount = KualiDecimal.ZERO;

        // get non paid expenses first
        travelAmount = travelAmount.add(getTotalExpenseAmount());

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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }
}
