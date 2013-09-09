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

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

@Entity
@Table(name = "TEM_PER_DIEM_EXP_T")
public class PerDiemExpense extends PersistableBusinessObjectBase {

    @Id
    @GeneratedValue(generator = "TEM_PER_DIEM_EXP_ID_SEQ")
    @SequenceGenerator(name = "TEM_PER_DIEM_EXP_ID_SEQ", sequenceName = "TEM_PER_DIEM_EXP_ID_SEQ", allocationSize = 5)
    @Column(name = "ID", nullable = false)
    private Integer id;
    private String documentNumber;
    private String countryState;
    private String county;
    private String primaryDestination;
    //boolean flag if it gets disabled due to duplicate
    private Boolean breakfast = Boolean.TRUE;
    private Boolean lunch = Boolean.TRUE;
    private Boolean dinner = Boolean.TRUE;
    private Boolean personal = Boolean.FALSE;
    private Boolean incidentalsWithMealsOnly;

    private KualiDecimal breakfastValue = KualiDecimal.ZERO;
    private KualiDecimal lunchValue = KualiDecimal.ZERO;
    private KualiDecimal dinnerValue = KualiDecimal.ZERO;
    private KualiDecimal incidentalsValue = KualiDecimal.ZERO;

    @Column(name = "per_diem_id")
    private Integer perDiemId;

    @ManyToOne
    @JoinColumn(name = "per_diem_id")
    private PerDiem perDiem;

    private Integer miles = new Integer(0);
    private Timestamp mileageDate;
    private MileageRate mileageRate;
    private Integer mileageRateId;

    private String accommodationTypeCode;
    private String accommodationName;
    private String accommodationPhoneNum;
    private String accommodationAddress;
    private AccommodationType accommodationType;

    private KualiDecimal lodging = KualiDecimal.ZERO;
    private boolean prorated = false;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "FDOC_NBR", length = 14, nullable = false)
    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    @Column(name = "COUNTRY", length = 100, nullable = false)
    public String getCountryState() {
        return countryState;
    }

    public void setCountryState(String countryState) {
        this.countryState = countryState;
    }

    @Column(name = "COUNTY_CD", length = 100, nullable = false)
    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    @Column(name = "PRI_DEST", length = 100, nullable = false)
    public String getPrimaryDestination() {
        return primaryDestination;
    }

    public void setPrimaryDestination(String primaryDestination) {
        this.primaryDestination = primaryDestination;
    }

    @Column(name = "BREAKFAST_IND", nullable = false, length = 1)
    public Boolean getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(Boolean breakfast) {
        this.breakfast = breakfast;
    }

    @Column(name = "LUNCH_IND", nullable = false, length = 1)
    public Boolean getLunch() {
        return lunch;
    }

    public void setLunch(Boolean lunch) {
        this.lunch = lunch;
    }

    @Column(name = "DINNER_IND", nullable = false, length = 1)
    public Boolean getDinner() {
        return dinner;
    }

    public void setDinner(Boolean dinner) {
        this.dinner = dinner;
    }

    /**
     * Gets the perDiemId attribute.
     *
     * @return Returns the perDiemId.
     */
    public Integer getPerDiemId() {
        return perDiemId;
    }

    /**
     * Sets the perDiemId attribute value.
     *
     * @param perDiemId The perDiemId to set.
     */
    public void setPerDiemId(Integer perDiemId) {
        this.perDiemId = perDiemId;
    }

    /**
     * Gets the perDiem attribute.
     *
     * @return Returns the perDiem.
     */
    public PerDiem getPerDiem() {
        return perDiem;
    }

    /**
     * Sets the perDiem attribute value.
     *
     * @param perDiem The perDiem to set.
     */
    public void setPerDiem(PerDiem perDiem) {
        this.perDiem = perDiem;

        setupCustomPerDiem();
    }

    /**
     * This method gets the accommodation type code associated with this day
     *
     * @return accoomodation type code
     */
    @Column(name = "ACCOM_TYP_CD", length = 4)
    public String getAccommodationTypeCode() {
        return accommodationTypeCode;
    }

    /**
     * This method sets the accommodation type code for this day
     *
     * @param accommodationTypeCode
     */
    public void setAccommodationTypeCode(String accommodationTypeCode) {
        this.accommodationTypeCode = accommodationTypeCode;
    }

    /**
     * This method returns the current accommodation Type for this TripDetailEstimate
     *
     * @return accommodation Type
     */
    @ManyToOne
    @JoinColumn(name = "ACCOM_TYP_CD")
    public AccommodationType getAccommodationType() {
        return accommodationType;
    }

    /**
     * This method is only used for when the object is initially populated from OJB
     *
     * @param accommodationType
     */
    public void setAccommodationType(AccommodationType accommodationType) {
        this.accommodationType = accommodationType;
    }

    /**
     * This method returns the name of the accommodation for this day
     *
     * @return accommodation name
     */
    @Column(name = "ACCOM_NM")
    public String getAccommodationName() {
        return accommodationName;
    }

    /**
     * This method sets the accommodation name for this day
     *
     * @param accommodationName
     */
    public void setAccommodationName(String accommodationName) {
        this.accommodationName = accommodationName;
    }

    /**
     * This method returns the accommodation's phone
     *
     * @return the phone number for the accommodation
     */
    @Column(name = "ACCOM_PH")
    public String getAccommodationPhoneNum() {
        return accommodationPhoneNum;
    }

    /**
     * This method sets the accommodation phone number
     *
     * @param accommodationPhoneNum
     */
    public void setAccommodationPhoneNum(String accommodationPhoneNum) {
        this.accommodationPhoneNum = accommodationPhoneNum;
    }

    /**
     * This method returns the accommodation's address
     *
     * @return accommodation's address
     */
    @Column(name = "ACCOM_ADDRESS")
    public String getAccommodationAddress() {
        return accommodationAddress;
    }

    /**
     * This method sets the accommodation's address
     *
     * @param accommodationAddress
     */
    public void setAccommodationAddress(String accommodationAddress) {
        this.accommodationAddress = accommodationAddress;
    }

    /**
     * This method gets the lodging cost for this day. This method does not take into account personal expenses. Use getLodgingTotal() for that.
     *
     * @return lodging cost
     */
    @Column(name = "LODGING", precision = 19, scale = 2)
    public KualiDecimal getLodging() {
        if (ObjectUtils.isNotNull(lodging) && this.lodging.isGreaterThan(KualiDecimal.ZERO)) {
            return lodging;
        }

        return KualiDecimal.ZERO;
    }

    /**
     * @return the amount of lodging, without logic that may force it back to zero
     */
    public KualiDecimal getUnfilteredLodging() {
        return lodging;
    }

    /**
     * Matching setter for getUnfilteredLoding, functionally equivalent to setLodging
     * @param lodging
     */
    public void setUnfilteredLodging(KualiDecimal lodging) {
        this.lodging = lodging;
    }

    /**
     * This method sets the lodging cost for this day
     *
     * @param lodging
     */
    public void setLodging(KualiDecimal lodging) {
        this.lodging = lodging;
    }

    @Column(name = "MILES", nullable = false)
    public Integer getMiles() {
        if (ObjectUtils.isNotNull(miles) && miles > 0) {
            return miles;
        }

        return 0;
    }

    public void setMiles(Integer miles) {
        this.miles = miles;
    }

    public Integer getUnfilteredMiles() {
        return miles;
    }

    public void setUnfilteredMiles(Integer miles) {
        this.miles = miles;
    }

    /**
     * Gets the breakfastValue attribute.
     *
     * @return Returns the breakfastValue.
     */
    public KualiDecimal getBreakfastValue() {
        if (ObjectUtils.isNotNull(breakfastValue) && this.breakfastValue.isGreaterThan(KualiDecimal.ZERO)) {
            return breakfastValue;
        }

        return KualiDecimal.ZERO;
    }

    /**
     * Sets the breakfastValue attribute value.
     *
     * @param breakfastValue The breakfastValue to set.
     */
    public void setBreakfastValue(KualiDecimal breakfastValue) {
        this.breakfastValue = breakfastValue;
    }

    public KualiDecimal getUnfilteredBreakfastValue() {
        return this.breakfastValue;
    }

    public void setUnfilteredBreakfastValue(KualiDecimal breakfastValue) {
        this.breakfastValue = breakfastValue;
    }

    /**
     * Gets the lunchValue attribute.
     *
     * @return Returns the lunchValue.
     */
    public KualiDecimal getLunchValue() {
        if (ObjectUtils.isNotNull(lunchValue) && this.lunchValue.isGreaterThan(KualiDecimal.ZERO)) {
            return lunchValue;
        }

        return KualiDecimal.ZERO;
    }

    /**
     * Sets the lunchValue attribute value.
     *
     * @param lunchValue The lunchValue to set.
     */
    public void setLunchValue(KualiDecimal lunchValue) {
        this.lunchValue = lunchValue;
    }

    public KualiDecimal getUnfilteredLunchValue() {
        return this.lunchValue;
    }

    public void setUnfilteredLunchValue(KualiDecimal lunchValue) {
        this.lunchValue = lunchValue;
    }

    /**
     * Gets the dinnerValue attribute.
     *
     * @return Returns the dinnerValue.
     */
    public KualiDecimal getDinnerValue() {
        if (ObjectUtils.isNotNull(dinnerValue) && this.dinnerValue.isGreaterThan(KualiDecimal.ZERO)) {
            return dinnerValue;
        }

        return KualiDecimal.ZERO;
    }

    /**
     * Sets the dinnerValue attribute value.
     *
     * @param dinnerValue The dinnerValue to set.
     */
    public void setDinnerValue(KualiDecimal dinnerValue) {
        this.dinnerValue = dinnerValue;
    }

    public KualiDecimal getUnfilteredDinnerValue() {
        return this.dinnerValue;
    }

    public void setUnfilteredDinnerValue(KualiDecimal dinnerValue) {
        this.dinnerValue = dinnerValue;
    }

    @ManyToOne
    @JoinColumn(name = "MILEAGE_RT_ID")
    public MileageRate getMileageRate() {
        return mileageRate;
    }

    public void setMileageRate(MileageRate mileageRate) {
        this.mileageRate = mileageRate;
    }

    @Column(name = "MILEAGE_RT_ID", precision = 19, scale = 2, nullable = false)
    public Integer getMileageRateId() {
        return mileageRateId;
    }

    public void setMileageRateId(Integer mileageRateId) {
        this.mileageRateId = mileageRateId;
    }

    @Column(name = "MILEAGE_TOT", precision = 19, scale = 2, nullable = false)
    public KualiDecimal getMileageTotal() {
        KualiDecimal total = KualiDecimal.ZERO;
        if (!personal) {
            if (ObjectUtils.isNotNull(this.mileageRateId) && ObjectUtils.isNotNull(this.miles) && this.miles > 0) {
                this.refreshReferenceObject("mileageRate");
                total = new KualiDecimal(miles).multiply(mileageRate.getRate());
            }
        }

        return total;
    }

    public KualiDecimal getDailyTotal() {
        KualiDecimal total = KualiDecimal.ZERO;
        if (!personal) {
            total = total.add(this.getMileageTotal());
            total = total.add(this.getLodging());
            total = total.add(getMealsAndIncidentals());
        }

        return total;
    }

    /**
     *
     * This method gets the mealsTotal with
     * @return
     */
    public KualiDecimal getMealsTotal() {
        KualiDecimal total = KualiDecimal.ZERO;
        if (!personal) {
            if (breakfast) {
                total = total.add(this.getBreakfastValue());
            }
            if (lunch) {
                total = total.add(this.getLunchValue());
            }
            if (dinner) {
                total = total.add(this.getDinnerValue());
            }
        }
        return total;
    }

    /**
     *
     * This method gets the Lodging Total if it is not a personal expense.
     * @return
     */
    public KualiDecimal getLodgingTotal() {
        if (!personal) {
            return this.getLodging();
        }
        return KualiDecimal.ZERO;
    }

    /**
     * Retrieve the Mileage date of per diem
     *
     * CLEANUP: mileage date should be the per diem expense date
     *
     * @return mileage date
     */
    @Column(name = "MLG_DT")
    public Timestamp getMileageDate() {
        return mileageDate;
    }

    public KualiDecimal getMealsAndIncidentals() {
        KualiDecimal total = KualiDecimal.ZERO;
        if (!personal) {
            total = total.add(getMealsTotal());
            total = total.add(getIncidentalsValue());
        }
        return total;
    }


    public static KualiDecimal calculateMealsAndIncidentalsProrated(KualiDecimal total, Integer perDiemPercent) {
        KualiDecimal percent = new KualiDecimal(perDiemPercent).divide(new KualiDecimal(100));
        total = total.multiply(percent);
        return total;
    }

    /**
     *
     * This method split mealsAndIncidentals into breakfast, lunch, dinner and incidentals (overriding the existing breakfast, lunch, dinner and incidentals values).
     * @param mealsAndIncidentals
     */
    public void setMealsAndIncidentals(KualiDecimal mealsAndIncidentals) {
        KualiDecimal meal = mealsAndIncidentals.divide(new KualiDecimal(4));
        setBreakfastValue(meal);
        setLunchValue(meal);
        setDinnerValue(meal);
        setIncidentalsValue(mealsAndIncidentals.subtract(getMealsTotal()));
    }

    /**
     * This method returns whether or not this is a personal expense or not (true for personal expense, false otherwise)
     *
     * @return true for personal expense, false otherwise
     */
    @Column(name = "PERSONAL", nullable = false, length = 1)
    public Boolean getPersonal() {
        return personal;
    }

    /**
     * This method sets true if this is a personal expense
     *
     * @param true for personal expense
     */
    public void setPersonal(Boolean personal) {
        this.personal = personal;
    }

    /**
     * Assign the Mileage date of per diem
     *
     * @param mileageDate as Date
     */
    public void setMileageDate(final Timestamp mileageDate) {
        this.mileageDate = mileageDate;
    }

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("id", id);
        map.put("countryState", this.countryState);
        map.put("county", this.county);
        map.put("primaryDestination", this.primaryDestination);

        return map;
    }

    public KualiDecimal getDefaultMealsAndIncidentals() {
        KualiDecimal total = KualiDecimal.ZERO;
        refreshPerDiem();
        if (!personal && this.perDiem != null) {
            if (breakfast) {
                total = total.add(this.perDiem.getBreakfast());
            }
            if (lunch) {
                total = total.add(this.perDiem.getLunch());
            }
            if (dinner) {
                total = total.add(this.perDiem.getDinner());
            }

            // finally add in the incidentals
            if (getIncludeIncidentals()) {
                total = total.add(this.perDiem.getIncidentals());
            }
        }
        return total;
    }

    public void refreshPerDiem() {
        if (!isCustomPerDiem()) {
            this.refreshReferenceObject("perDiem");
        }

        setupCustomPerDiem();
    }

    /**
     * This method generates a custom perDiem if perDiem is null
     */
    private void setupCustomPerDiem() {
        if (this.perDiem == null) {
            this.perDiem = new PerDiem();
            this.perDiem.setId(TemConstants.CUSTOM_PER_DIEM_ID);
        }
    }

    public boolean isCustomPerDiem() {
        return this.perDiem == null || this.perDiem.getId() == null ? true : this.perDiem.getId() == TemConstants.CUSTOM_PER_DIEM_ID;
    }

    /**
     * This method checks for incidentals inclusion to the mealsAndIncidentals calculation.
     *
     * @return
     */
    public Boolean getIncludeIncidentals() {
        return !(!breakfast && !lunch && !dinner && getIncidentalsWithMealsOnly());
    }

    public Boolean getIncidentalsWithMealsOnly() {
        if (incidentalsWithMealsOnly == null) {
            String incidentalsWithMealsOnlyParam = SpringContext.getBean(ParameterService.class).getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.INCIDENTALS_WITH_MEALS_IND);

            //incidentalsWithMealsOnly = incidentalsWithMealsOnlyParam != null && incidentalsWithMealsOnlyParam.equals(KFSConstants.DocumentTypeAttributes.INDICATOR_ATTRIBUTE_TRUE_VALUE);
            incidentalsWithMealsOnly = incidentalsWithMealsOnlyParam != null && incidentalsWithMealsOnlyParam.equals(KFSConstants.ParameterValues.YES);
        }

        return incidentalsWithMealsOnly;
    }

    public void setIncidentalsWithMealsOnly(Boolean incidentalsWithMealsOnly) {
        this.incidentalsWithMealsOnly = incidentalsWithMealsOnly;
    }

    /**
     * This method returns incidentalsValue affected by INCIDENTALS_WITH_MEALS_IND parameter
     *
     * @return
     */
    public KualiDecimal getIncidentalsValue() {
        if (getIncludeIncidentals()) {
            if (ObjectUtils.isNotNull(incidentalsValue) && this.incidentalsValue.isGreaterThan(KualiDecimal.ZERO)) {
                return incidentalsValue;
            }
        }

        return KualiDecimal.ZERO;
    }

    public void setIncidentalsValue(KualiDecimal incidentalsValue) {
        this.incidentalsValue = incidentalsValue;
    }

    public KualiDecimal getUnfilteredIncidentalsValue() {
        return this.incidentalsValue;
    }

    public void setUnfilteredIncidentalsValue(KualiDecimal incidentalsValue) {
        this.incidentalsValue = incidentalsValue;
    }

    public boolean isProrated() {
        if(isCustomPerDiem()){
            return false;
        }

        return prorated;
    }

    public void setProrated(boolean prorated) {
        this.prorated = prorated;
    }

    /**
     * Gets the countryStateText attribute.
     *
     * @return Returns the countryStateText
     */

    public String getCountryStateText() {
        return countryState;
    }

    /**
     * Sets the countryStateText attribute.
     *
     * @param countryStateText The countryStateText to set.
     */
    public void setCountryStateText(String countryState) {
        this.countryState = countryState;
    }


}
