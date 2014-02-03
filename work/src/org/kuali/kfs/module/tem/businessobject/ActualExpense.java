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
import java.math.BigDecimal;
import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
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
    private Integer miles;
    private BigDecimal mileageOtherRate = BigDecimal.ZERO;
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
     * Get the value of mileageRate
     *
     * @return the value of mileageRate
     */
    public MileageRate getMileageRate(){
        return SpringContext.getBean(TravelDocumentService.class).getMileageRate(getExpenseTypeCode(), getExpenseDate());
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
    public void setMileageOtherRate(final BigDecimal mileageOtherRate) {
        this.mileageOtherRate = mileageOtherRate;
    }

    /**
     * Get the value of mileageOtherRate
     *
     * @return the value of mileageOtherRate
     */
    @Override
    @Column(name="MILEAGE_OTHR_RT",precision=19,scale=2,nullable=true)
    public BigDecimal getMileageOtherRate() {
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
        setAirfareIndicator(isAirfare());
        setMileageIndicator(isMileage());
        setRentalCarIndicator(isRentalCar());
        setLodgingIndicator(isLodging());
        setLodgingAllowanceIndicator(isLodgingAllowance());
    }

    public boolean isHostedBreakfast() {
        return isHostedMeal() && isBreakfast();
    }

    public boolean isHostedLunch() {
        return isHostedMeal() && isLunch();
    }

    public boolean isHostedDinner() {
        return isHostedMeal() && isDinner();
    }

    public boolean isBreakfast() {
        return isInMetaCategory(TemConstants.ExpenseTypeMetaCategory.BREAKFAST);
    }

    public boolean isLunch() {
        return isInMetaCategory(TemConstants.ExpenseTypeMetaCategory.LUNCH);
    }

    public boolean isDinner() {
        return isInMetaCategory(TemConstants.ExpenseTypeMetaCategory.DINNER);
    }

    public boolean isAirfare(){
        return isInMetaCategory(TemConstants.ExpenseTypeMetaCategory.AIRFARE);
    }

    public boolean isMileage(){
        return isInMetaCategory(TemConstants.ExpenseTypeMetaCategory.MILEAGE);
    }

    @Override
    public boolean isRentalCar(){
        return isInMetaCategory(TemConstants.ExpenseTypeMetaCategory.RENTAL_CAR);
    }

    public boolean isLodging(){
        return isInMetaCategory(TemConstants.ExpenseTypeMetaCategory.LODGING);
    }

    public boolean isLodgingAllowance(){
        return isInMetaCategory(TemConstants.ExpenseTypeMetaCategory.LODGING_ALLOWANCE);
    }

    public boolean isIncidental(){
        return isInMetaCategory(TemConstants.ExpenseTypeMetaCategory.INCIDENTALS);
    }

    /**
     * Determines if this expense is part of the given metacategory
     * @param category the expense type meta category to test membership in
     * @return true if the expense is in the expense type meta category, false otherwise
     */
    protected boolean isInMetaCategory(TemConstants.ExpenseTypeMetaCategory category) {
        if (ObjectUtils.isNull(getExpenseType()) && !StringUtils.isBlank(getExpenseTypeCode())) {
            refreshReferenceObject(TemPropertyConstants.EXPENSE_TYPE);
        }

        return ( !ObjectUtils.isNull(getExpenseType()) && category.getCode().equals(getExpenseType().getExpenseTypeMetaCategoryCode()));
    }

    public boolean isHostedMeal(){
        if (ObjectUtils.isNull(getExpenseType()) && !StringUtils.isBlank(getExpenseTypeCode())) {
            refreshReferenceObject(TemPropertyConstants.EXPENSE_TYPE);
        }
        return !ObjectUtils.isNull(getExpenseType()) && getExpenseType().isHosted();
    }

    @Transient
    public KualiDecimal getMileageTotal(){
        KualiDecimal total = KualiDecimal.ZERO;

        if(ObjectUtils.isNotNull(this.miles) && this.miles != 0){
            if (ObjectUtils.isNotNull(mileageOtherRate) && mileageOtherRate != BigDecimal.ZERO) {
                total= new KualiDecimal(new BigDecimal(miles).multiply(this.mileageOtherRate)); // mileageOtherRate takes precedence
            }
            else {
                try{
                    final MileageRate mileageRate = this.getMileageRate();
                    total = new KualiDecimal(new BigDecimal(miles).multiply(getMileageRate().getRate()));
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
