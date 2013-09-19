/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Expense
 */
@Entity
@Table(name="TEM_TRVL_EXP_T")
public class ActualExpense extends AbstractExpense implements OtherExpense, ExpenseTypeAware {

    public static Logger LOG = Logger.getLogger(AbstractExpense.class);

    @GeneratedValue(generator="tem_trvl_exp_id_seq")
    @SequenceGenerator(name="tem_trvl_exp_id_seq",sequenceName="tem_trvl_exp_id_seq", allocationSize=5)

    private String airfareSourceCode;
    private String classOfServiceCode;
    private Integer mileageRateId;
    private MileageRate mileageRate;
    private Integer miles;
    private KualiDecimal mileageOtherRate;
    private Boolean rentalCarInsurance = Boolean.FALSE;

    private Boolean airfareIndicator = Boolean.FALSE;
    private Boolean mileageIndicator = Boolean.FALSE;
    private Boolean rentalCarIndicator = Boolean.FALSE;
    private Boolean lodgingIndicator = Boolean.FALSE;
    private Boolean lodgingAllowanceIndicator = Boolean.FALSE;

    private String expenseLineTypeCode = TemConstants.EXPENSE_ACTUAL;

    private ClassOfService classOfService;

    public ActualExpense() {
        // details = new ArrayList<OtherExpenseDetail>();
    }

    public boolean getDefaultTabOpen(){
        return !getExpenseDetails().isEmpty() || getMileageIndicator() || getAirfareIndicator() || getRentalCarIndicator() || (getExpenseTypeObjectCode() != null && getExpenseTypeObjectCode().getExpenseType().isExpenseDetailRequired());
    }

    /**
     * Sets the value of airfareSourceCode
     *
     * @param airfareSourceCode value to assign to this.airfareSourceCode
     */
    @Override
    public void setAirfareSourceCode(final String airfareSourceCode){
        this.airfareSourceCode = airfareSourceCode;
    }

    /**
     * Get the value of airfareSourceCode
     *
     * @return the value of airfareSourceCode
     */
    @Override
    @Column(name="AIRFARE_SRC_CD",nullable=true)
    public String getAirfareSourceCode(){
        return this.airfareSourceCode;
    }

    /**
     * Sets the value of classOfServiceCode
     *
     * @param classOfServiceCode value to assign to this.classOfServiceCode
     */
    @Override
    public void setClassOfServiceCode(final String classOfServiceCode){
        this.classOfServiceCode = classOfServiceCode;
    }

    /**
     * Get the value of classOfServiceCode
     *
     * @return the value of classOfServiceCode
     */
    @Override
    @Column(name="CLASS_SVC_CODE",nullable=true)
    public String getClassOfServiceCode(){
        return this.classOfServiceCode;
    }

    /**
     * Get the value of mileageRateId
     *
     * @return the value of mileageRateId
     */
    @Override
    @Column(name="MILEAGE_RT_ID",length=19,nullable=true)
    public Integer getMileageRateId(){
        return this.mileageRateId;
    }

    /**
     * Sets the value of mileageRateId
     *
     * @param mileageRateId value to assign to this.mileageRateId
     */
    @Override
    public void setMileageRateId(Integer mileageRateId){
        this.mileageRateId = mileageRateId;
    }

    /**
     * Get the value of mileageRate
     *
     * @return the value of mileageRate
     */
    @Override
    @ManyToOne
    @JoinColumn(name="MILEAGE_RT_ID",nullable=false)
    public MileageRate getMileageRate(){
        if (this.mileageRate == null && mileageRateId != null){
            this.mileageRate = SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(MileageRate.class, mileageRateId);
        }
        return this.mileageRate;
    }

    /**
     * Sets the value of mileageRate
     *
     * @param mileageRate value to assign to this.mileageRate
     */
    @Override
    public void setMileageRate(MileageRate mileageRate){
        this.mileageRate = mileageRate;
    }

    /**
     * Get the value of miles
     *
     * @return the value of miles
     */
    @Override
    @Column(name="MILES",length=19,nullable=true)
    public Integer getMiles(){
        return this.miles;
    }

    /**
     * Sets the value of miles
     *
     * @param miles value to assign to this.miles
     */
    @Override
    public void setMiles(Integer miles){
        this.miles = miles;
    }

    /**
     * Sets the value of mileageOtherRate
     *
     * @param mileageOtherRate value to assign to this.mileageOtherRate
     */
    @Override
    public void setMileageOtherRate(final KualiDecimal mileageOtherRate) {
        this.mileageOtherRate = mileageOtherRate;
    }

    /**
     * Get the value of mileageOtherRate
     *
     * @return the value of mileageOtherRate
     */
    @Override
    @Column(name="MILEAGE_OTHR_RT",precision=19,scale=2,nullable=true)
    public KualiDecimal getMileageOtherRate() {
        return this.mileageOtherRate;
    }

    /**
     * Sets the value of rentalCarInsurance
     *
     * @param rentalCarInsurance value to assign to this.rentalCarInsurance
     */
    @Override
    public void setRentalCarInsurance(final Boolean rentalCarInsurance){
        this.rentalCarInsurance = rentalCarInsurance;
    }

    /**
     * Get the value of rentalCarInsurance
     *
     * @return the value of rentalCarInsurance
     */
    @Override
    @Column(name="RENTAL_CAR_INSURANCE",nullable=true, length=1)
    public Boolean getRentalCarInsurance(){
        return this.rentalCarInsurance;
    }

    public void setAirfareIndicator(Boolean airfareIndicator){
        this.airfareIndicator = airfareIndicator;
    }

    public Boolean getAirfareIndicator(){
        return this.airfareIndicator;
    }

    public void setMileageIndicator(Boolean mileageIndicator){
        this.mileageIndicator = mileageIndicator;
    }

    public Boolean getMileageIndicator(){
        return this.mileageIndicator;
    }

    public void setRentalCarIndicator(Boolean rentalCarIndicator){
        this.rentalCarIndicator = rentalCarIndicator;
    }

    public Boolean getRentalCarIndicator(){
        return this.rentalCarIndicator;
    }

    public void setLodgingIndicator(Boolean lodgingIndicator){
        this.lodgingIndicator = lodgingIndicator;
    }

    public Boolean getLodgingIndicator(){
        return this.lodgingIndicator;
    }

    public void setLodgingAllowanceIndicator(Boolean lodgingAllowanceIndicator){
        this.lodgingAllowanceIndicator = lodgingAllowanceIndicator;
    }

    public Boolean getLodgingAllowanceIndicator(){
        return this.lodgingAllowanceIndicator;
    }

    public void enableExpenseTypeSpecificFields(){
        if (getExpenseTypeObjectCode() != null){
            setTaxable(getExpenseTypeObjectCode().isTaxable());
        }
        setAirfareIndicator(isAirfare());
        setMileageIndicator(isMileage());
        setRentalCarIndicator(isRentalCar());
        setLodgingIndicator(isLodging());
        setLodgingAllowanceIndicator(isLodgingAllowance());
    }

    public boolean isHostedBreakfast() {
        return isHostedMeal("BREAKFAST");
    }

    public boolean isHostedLunch() {
        return isHostedMeal("LUNCH");
    }

    public boolean isHostedDinner() {
        return isHostedMeal("DINNER");
    }

    public boolean isAirfare(){
        final String airfareType = getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.AIRFARE_EXPENSE_TYPE);

        return ( !StringUtils.isBlank(getExpenseTypeCode()) && getExpenseTypeCode().equals(airfareType));
    }

    public boolean isMileage(){
        final String mileageType = getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.MILEAGE_EXPENSE_TYPE);
        return getExpenseTypeCode() != null && getExpenseTypeCode().equals(mileageType);
    }

    @Override
    public boolean isRentalCar(){
        final String rentalCarType = getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.RENTAL_CAR_EXPENSE_TYPE);
        return getExpenseTypeCode() != null && getExpenseTypeCode().equals(rentalCarType);
    }

    public boolean isLodging(){
        final String lodgingType = getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.LODGING_EXPENSE_TYPE);
        return getExpenseTypeCode() != null && getExpenseTypeCode().equals(lodgingType);
    }

    public boolean isLodgingAllowance(){
        final String lodgingAllowanceType = getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.LODGING_ALLOWANCE_EXPENSE_TYPE);
        return getExpenseTypeCode() != null && getExpenseTypeCode().equals(lodgingAllowanceType);
    }

    public boolean isIncidental(){
        final Collection<String> incidentalType = getParameterService().getParameterValuesAsString(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.INCIDENTAL_EXPENSE_TYPES);
        return getExpenseTypeCode() != null && incidentalType.contains(getExpenseTypeCode());
    }

    public boolean isHostedMeal(){
        return (isHostedBreakfast() || isHostedDinner() || isHostedLunch());
    }

    protected boolean isHostedMeal(final String meal) {
        final Collection<String> mealTypes = getParameterService().getParameterValuesAsString(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.HOSTED_MEAL_EXPENSE_TYPES);
        for (final String mealType : mealTypes) {
            if (mealType.startsWith(meal)) {
                final String[] typeCodes = mealType.split("=")[1].split(",");
                for (final String typeCode : typeCodes) {
                    if (getExpenseTypeObjectCode() != null && typeCode.equals(getExpenseTypeObjectCode().getExpenseTypeCode())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Transient
    public KualiDecimal getMileageTotal(){
        KualiDecimal total = KualiDecimal.ZERO;

        if(ObjectUtils.isNotNull(this.miles) && this.miles != 0){
            if(ObjectUtils.isNotNull(this.mileageRateId) && this.mileageRateId != 0){
                refreshReferenceObject("mileageRate");
                try{
                    total = new KualiDecimal(miles).multiply(this.mileageRate.getRate());
                }
                catch(Exception ex){
                    //This should never happen
                    LOG.error("Mileage Rate not found." + getClass());
                    LOG.error(ex.getMessage());
                    if (LOG.isDebugEnabled()) {
                        ex.printStackTrace();
                    }
                }
            }
            else if(ObjectUtils.isNotNull(this.mileageOtherRate)){
                total= new KualiDecimal(miles).multiply(this.mileageOtherRate);
            }
            /*if(ObjectUtils.isNotNull(this.mileageOtherRate) && this.travelExpenseTypeCodeCode.equals("O")){
                total= new KualiDecimal(miles).multiply(this.mileageOtherRate);
            }*/
        }

        return total;
    }

    protected ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }


    /**
     * @see org.kuali.kfs.module.tem.businessobject.AbstractExpense#getConvertedAmount()
     */
    @Override
    public KualiDecimal getExpenseAmount() {
        return super.getExpenseAmount();

    }

    /**
     * @see org.kuali.kfs.module.tem.businessobject.AbstractExpense#getConvertedAmount()
     */
    @Override
    public KualiDecimal getConvertedAmount() {
        return super.getConvertedAmount();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public String getSequenceName() {
        Class boClass = getClass();
        String retval = "";
        try {
            boolean rethrow = true;
            Exception e = null;
            while (rethrow) {
                LOG.debug("Looking for id in "+ boClass.getName());
                try {
                    final Field idField = boClass.getDeclaredField("id");
                    final SequenceGenerator sequenceInfo = idField.getAnnotation(SequenceGenerator.class);

                    return sequenceInfo.sequenceName();
                }
                catch (Exception ee) {
                    // ignore and try again
                    LOG.debug("Could not find id in "+ boClass.getName());

                    // At the end. Went all the way up the hierarchy until we got to Object
                    if (Object.class.equals(boClass)) {
                        rethrow = false;
                    }

                    // get the next superclass
                    boClass = boClass.getSuperclass();
                    e = ee;
                }
            }

            if (e != null) {
                throw e;
            }
        }
        catch (Exception e) {
            LOG.error("Could not get the sequence name for business object "+ getClass().getSimpleName());
            LOG.error(e.getMessage());
            if (LOG.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
        return retval;
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("id", getId());
        map.put("documentNumber", getDocumentNumber());
        map.put("expenseDate", getExpenseDate());
        map.put("expenseAmount", getExpenseAmount());

        return map;
    }

    protected SequenceAccessorService getSequenceAccessorService() {
        return SpringContext.getBean(SequenceAccessorService.class);
    }

    @Override
    public String getExpenseLineTypeCode(){
        return expenseLineTypeCode;
    }

    @Override
    public void setExpenseLineTypeCode(String expenseLineTypeCode) {
        this.expenseLineTypeCode = expenseLineTypeCode;
    }

    public ClassOfService getClassOfService() {
        return classOfService;
    }

    public void setClassOfService(ClassOfService classOfService) {
        this.classOfService = classOfService;
    }
}
