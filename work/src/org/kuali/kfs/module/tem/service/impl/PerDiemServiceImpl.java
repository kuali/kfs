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
package org.kuali.kfs.module.tem.service.impl;

import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.LODGING_OBJECT_CODE;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.PER_DIEM_OBJECT_CODE;

import java.sql.Date;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.PerDiemParameter;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.batch.PerDiemLoadStep;
import org.kuali.kfs.module.tem.batch.businessobject.MealBreakDownStrategy;
import org.kuali.kfs.module.tem.businessobject.AccountingDistribution;
import org.kuali.kfs.module.tem.businessobject.ExpenseTypeObjectCode;
import org.kuali.kfs.module.tem.businessobject.MileageRate;
import org.kuali.kfs.module.tem.businessobject.PerDiem;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.TemExpense;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.module.tem.dataaccess.PerDiemDao;
import org.kuali.kfs.module.tem.dataaccess.TravelDocumentDao;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.web.struts.TravelFormBase;
import org.kuali.kfs.module.tem.service.PerDiemService;
import org.kuali.kfs.module.tem.service.TemExpenseService;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.module.tem.util.ExpenseUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.api.state.State;
import org.kuali.rice.location.api.state.StateService;
import org.springframework.transaction.annotation.Transactional;

/**
 * implement the service method calls defined in PerDiemService
 */
@Transactional
public class PerDiemServiceImpl extends ExpenseServiceBase implements PerDiemService, TemExpenseService {

    private static Logger LOG = Logger.getLogger(PerDiemServiceImpl.class);

    protected DateTimeService dateTimeService;
    protected ParameterService parameterService;
    protected BusinessObjectService businessObjectService;
    protected StateService stateService;
    protected PerDiemDao perDiemDao;
    protected Map<String, MealBreakDownStrategy> mealBreakDownStrategies;
    protected String allStateCodes;
    protected TravelDocumentDao travelDocumentDao;
    protected TravelExpenseService travelExpenseService;

    List<PerDiem> persistedPerDiems;


    /**
     * @see org.kuali.kfs.module.tem.service.PerDiemService#breakDownMealsIncidental(java.util.List)
     */
    @Override
    public <T extends PerDiem> void breakDownMealsIncidental(List<T> perDiemList) {
        for (T perDiem : perDiemList) {
            this.breakDownMealsIncidental(perDiem);
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.service.PerDiemService#breakDownMealsIncidental(org.kuali.kfs.module.tem.businessobject.PerDiem)
     */
    @Override
    public <T extends PerDiem> void breakDownMealsIncidental(T perDiem) {
        String conusIndicator = perDiem.getConusIndicator();
        if (this.getMealBreakDownStrategies().containsKey(conusIndicator)) {
            MealBreakDownStrategy mealBreakDownStrategy = this.getMealBreakDownStrategies().get(conusIndicator);

            mealBreakDownStrategy.breakDown(perDiem);
        }
        else {
            throw new RuntimeException("Fail to meal break down strategy for the CONUS indicator: " + conusIndicator);
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.service.PerDiemService#retrieveActivePerDiem()
     */
    public <T extends PerDiem> List<T> retrieveInactivePerDiem() {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.ACTIVE, Boolean.FALSE);

        return (List<T>) this.getBusinessObjectService().findMatching(PerDiem.class, fieldValues);
    }


    /**
     * @see org.kuali.kfs.module.tem.service.PerDiemService#updateTripType(java.util.List)
     */
    @Override
    public <T extends PerDiem> void updateTripType(List<T> perDiemList) {
        for (T perDiem : perDiemList) {
            this.updateTripType(perDiem);
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.service.PerDiemService#updateTripType(org.kuali.kfs.module.tem.businessobject.PerDiem)
     */
    @Override
    public <T extends PerDiem> void updateTripType(T perDiem) {
        String region = perDiem.getPrimaryDestination().getRegion().getRegionCode();
        String institutionState = this.getInstitutionState();

        String tripTypeCode = this.getInternationalTripTypeCode();
        if(StringUtils.equals(region, institutionState)) {
        	tripTypeCode = this.getInStateTripTypeCode();
        } else if(getAllStateCodes().contains( ";" + region.toUpperCase() +  ";")) {
        	tripTypeCode = this.getOutStateTripTypeCode();
        }

        perDiem.getPrimaryDestination().getRegion().setTripTypeCode(tripTypeCode);
    }

    /**
     * get institution state code defined as an application parameter
     *
     * @return institution state code
     */
    protected String getInstitutionState() {
        String institutionState = this.getParameterService().getParameterValueAsString(PerDiemLoadStep.class, PerDiemParameter.INSTITUTION_STATE_PARAM_NAME);

        return institutionState;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.PerDiemService#retrievePreviousPerDiem(org.kuali.kfs.module.tem.businessobject.PerDiem)
     */
    @Override
    public <T extends PerDiem> List<PerDiem> retrievePreviousPerDiem(T perDiem) {

        return (List<PerDiem>)  perDiemDao.findSimilarPerDiems(perDiem);
    }


    /**
     * check whether the given per diem exists in the database
     *
     * @param perDiem the given per diem
     * @return true if the given per diem exists in the database
     */
    @Override
    public <T extends PerDiem> boolean hasExistingPerDiem(T perDiem) {
        if (ObjectUtils.isNull(persistedPerDiems)) {
            persistedPerDiems = (List<PerDiem>)businessObjectService.findAll(PerDiem.class);
        }


        boolean retval = persistedPerDiems.contains(perDiem);

        return retval;

    }

    /**
     * get out state trip type code defined as an application parameter
     *
     * @return out state trip type code
     */
    protected String getOutStateTripTypeCode() {
        String outStateTripTypeCode = this.getParameterService().getParameterValueAsString(PerDiemLoadStep.class, PerDiemParameter.OUT_STATE_TRIP_TYPE_CODE_PARAM_NAME);

        return outStateTripTypeCode;
    }

    /**
     * get in state trip type code defined as an application parameter
     *
     * @return in state trip type code
     */
    protected String getInStateTripTypeCode() {
        String inStateTripTypeCode = this.getParameterService().getParameterValueAsString(PerDiemLoadStep.class, PerDiemParameter.IN_STATE_TRIP_TYPE_CODE_PARAM_NAME);

        return inStateTripTypeCode;
    }

    /**
     * get international trip type code defined as an application parameter
     *
     * @return international trip type code
     */
    protected String getInternationalTripTypeCode() {
        String internationalTripTypeCode = this.getParameterService().getParameterValueAsString(PerDiemLoadStep.class, PerDiemParameter.INTERNATIONAL_TRIP_TYPE_CODE_PARAM_NAME);

        return internationalTripTypeCode;
    }

    /**
     * Gets the parameterService attribute.
     *
     * @return Returns the parameterService.
     */
    @Override
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     *
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Gets the businessObjectService attribute.
     *
     * @return Returns the businessObjectService.
     */
    @Override
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the dateTimeService attribute.
     *
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     *
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Gets the perDiemDao attribute.
     *
     * @return Returns the perDiemDao
     */

    public PerDiemDao getPerDiemDao() {
        return perDiemDao;
    }

    /**
     * Sets the perDiemDao attribute.
     *
     * @param perDiemDao The perDiemDao to set.
     */
    public void setPerDiemDao(PerDiemDao perDiemDao) {
        this.perDiemDao = perDiemDao;
    }

    /**
     * Gets the mealBreakDownStrategies attribute.
     *
     * @return Returns the mealBreakDownStrategies.
     */
    public Map<String, MealBreakDownStrategy> getMealBreakDownStrategies() {
        return mealBreakDownStrategies;
    }

    /**
     * Sets the mealBreakDownStrategies attribute value.
     *
     * @param mealBreakDownStrategies The mealBreakDownStrategies to set.
     */
    public void setMealBreakDownStrategies(Map<String, MealBreakDownStrategy> mealBreakDownStrategies) {
        this.mealBreakDownStrategies = mealBreakDownStrategies;
    }

	/**
	 * Gets the stateService attribute.
	 * @return Returns the stateService.
	 */
	public StateService getStateService() {
		return stateService;
	}

	/**
	 * Sets the stateService attribute value.
	 * @param stateService The stateService to set.
	 */
	public void setStateService(StateService stateService) {
		this.stateService = stateService;
	}

	/**
	 * Sets the allStateCodes attribute value.
	 * @param allStateCodes The allStateCodes to set.
	 */
	public void setAllStateCodes(String allStateCodes) {
		this.allStateCodes = allStateCodes;
	}

	/**
	 * Gets the allStateCodes attribute.
	 * @return Returns the allStateCodes.
	 */
	public String getAllStateCodes() {

		if(StringUtils.isEmpty(allStateCodes)) {
			final List<State> codes = getStateService().findAllStatesInCountry(KFSConstants.COUNTRY_CODE_UNITED_STATES);

	        final StringBuffer sb = new StringBuffer();
	        sb.append(";").append(KFSConstants.COUNTRY_CODE_UNITED_STATES.toUpperCase()).append(";");
	        for (final State state : codes) {
	            if(state.isActive()) {
	                sb.append(state.getCode().toUpperCase()).append( ";");
	            }
	        }

	        allStateCodes = sb.toString();
		}

		return allStateCodes;
	}

	public String getObjectCodeFrom(final TravelDocument travelDocument, String paramName) {
	    if (travelDocument instanceof TravelReimbursementDocument){
	        final String parameterValue = getParameterService().getParameterValueAsString(TravelReimbursementDocument.class, paramName);
	        String paramSearchStr = "";
	        TravelerDetail traveler = travelDocument.getTraveler();
	        if(traveler != null){
	            paramSearchStr += traveler.getTravelerTypeCode() + "=";
	        }
	        TripType tripType = travelDocument.getTripType();
	        if(tripType != null){
	            paramSearchStr += tripType.getCode() + "=";
	        }

	        final int searchIdx = parameterValue.indexOf(paramSearchStr);

	        if (searchIdx == -1) {
	            return null;
	        }

	        final int endIdx = parameterValue.indexOf(";", searchIdx);
	        if (endIdx == -1) {
	            return parameterValue.substring(searchIdx + paramSearchStr.length());
	        }

	        return parameterValue.substring(searchIdx + paramSearchStr.length(), endIdx);
	    }
	    else{
	        if (travelDocument.getTripType() != null){
	            return travelDocument.getTripType().getEncumbranceObjCode();
	        }
	        return null;
	    }
    }

    @Override
    public Map<String, AccountingDistribution> getAccountingDistribution(TravelDocument document) {
        Map<String, AccountingDistribution> distributionMap = new HashMap<String, AccountingDistribution>();
        if (document.getPerDiemExpenses() != null
                && document.getPerDiemExpenses().size() > 0){
            String defaultChartCode = ExpenseUtils.getDefaultChartCode(document);
            String perDiemCode = getObjectCodeFrom(document, PER_DIEM_OBJECT_CODE);
            if (document instanceof TravelAuthorizationDocument){
                if (document.getTripType() != null){
                    perDiemCode = document.getTripType().getEncumbranceObjCode();
                }
                else{
                    perDiemCode = null;
                }
            }
            LOG.debug("Looking up Object Code for chart = "+ defaultChartCode+ " perDiemObjectCode = "+ perDiemCode);
            final ObjectCode perDiemObjCode = getObjectCodeService().getByPrimaryIdForCurrentYear(defaultChartCode, perDiemCode);
            LOG.debug("Got per diem object code "+ perDiemObjCode);

            // Per Diem
            AccountingDistribution accountingDistribution = new AccountingDistribution();

            if (perDiemObjCode != null) {
                String key = perDiemObjCode.getCode() + "-" + document.getDefaultCardTypeCode();

                for(PerDiemExpense expense : document.getPerDiemExpenses()){
                    if (!expense.getPersonal()){
                        if (!distributionMap.containsKey(key)){
                            accountingDistribution.setCardType(document.getDefaultCardTypeCode());
                            accountingDistribution.setObjectCode(perDiemObjCode.getCode());
                            accountingDistribution.setObjectCodeName(perDiemObjCode.getName());
                            distributionMap.put(key, accountingDistribution);
                        }
                        distributionMap.get(key).setSubTotal(distributionMap.get(key).getSubTotal().add(expense.getMealsAndIncidentals()));
                        distributionMap.get(key).setRemainingAmount(distributionMap.get(key).getRemainingAmount().add(expense.getMealsAndIncidentals()));
                        LOG.debug("Set perdiem distribution subtotal to "+ accountingDistribution.getSubTotal());
                    }
                }

                if (document.getPerDiemAdjustment() != null
                        && document.getPerDiemAdjustment().isPositive()){
                    distributionMap.get(key).setSubTotal(distributionMap.get(key).getSubTotal().subtract(document.getPerDiemAdjustment()));
                    distributionMap.get(key).setRemainingAmount(distributionMap.get(key).getRemainingAmount().subtract(document.getPerDiemAdjustment()));
                }
                distributeLodging(distributionMap, document);
                distributeMileage(distributionMap, document);
            }
            else {
                LOG.error("PerDiemObjCode is null!");
            }
        }

        return distributionMap;
    }

    protected void distributeLodging(Map<String, AccountingDistribution> distributionMap, final TravelDocument document) {
        String defaultChartCode = ExpenseUtils.getDefaultChartCode(document);
        String lodgingCode = getObjectCodeFrom(document, LODGING_OBJECT_CODE);
        if (document instanceof TravelAuthorizationDocument){
            if (document.getTripType() != null){
                lodgingCode = document.getTripType().getEncumbranceObjCode();
            }
            else{
                lodgingCode = null;
            }
        }
        LOG.debug("Looking up Object Code for chart = "+ defaultChartCode+ " lodgingCode = "+ lodgingCode);
        final ObjectCode lodgingObjCode = getObjectCodeService().getByPrimaryIdForCurrentYear(defaultChartCode, lodgingCode);
        LOG.debug("Got lodging object code "+ lodgingObjCode);

        AccountingDistribution accountingDistribution = new AccountingDistribution();
        String key = lodgingObjCode.getCode() + "-" + document.getDefaultCardTypeCode();

        if (document.getPerDiemExpenses() != null) {
            for(PerDiemExpense expense : document.getPerDiemExpenses()){
                if (!distributionMap.containsKey(key)){
                    accountingDistribution.setCardType(document.getDefaultCardTypeCode());
                    accountingDistribution.setObjectCode(lodgingObjCode.getCode());
                    accountingDistribution.setObjectCodeName(lodgingObjCode.getName());
                    distributionMap.put(key, accountingDistribution);
                }
                distributionMap.get(key).setSubTotal(distributionMap.get(key).getSubTotal().add(expense.getLodgingTotal()));
                distributionMap.get(key).setRemainingAmount(distributionMap.get(key).getRemainingAmount().add(expense.getLodgingTotal()));
            }
        }
    }

    protected void distributeMileage(Map<String, AccountingDistribution> distributionMap, TravelDocument document) {
        String defaultChartCode = ExpenseUtils.getDefaultChartCode(document);

        AccountingDistribution accountingDistribution = new AccountingDistribution();

        if (document.getPerDiemExpenses() != null) {
            for(PerDiemExpense expense : document.getPerDiemExpenses()){
                if (!StringUtils.isBlank(expense.getMileageRateExpenseTypeCode())) {
                    String mileageCode = null;
                    if (document instanceof TravelAuthorizationDocument && document.getTripType() != null){
                        mileageCode = document.getTripType().getEncumbranceObjCode();
                    }
                    else{
                        final String travelerTypeCode = (ObjectUtils.isNull(document.getTraveler())) ? null : document.getTraveler().getTravelerTypeCode();
                        final ExpenseTypeObjectCode expenseTypeObjectCode = getTravelExpenseService().getExpenseType(expense.getMileageRateExpenseTypeCode(), document.getDocumentTypeName(), document.getTripTypeCode(), travelerTypeCode);
                        if (expenseTypeObjectCode != null) {
                            mileageCode = expenseTypeObjectCode.getFinancialObjectCode();
                        }
                    }
                    LOG.debug("Looking up Object Code for chart = "+ defaultChartCode+ " mileageCode = "+ mileageCode);
                    final ObjectCode mileageObjCode = getObjectCodeService().getByPrimaryIdForCurrentYear(defaultChartCode, mileageCode);
                    LOG.debug("Got mileage object code "+ mileageObjCode);
                    String key = mileageObjCode.getCode() + "-" + document.getDefaultCardTypeCode();

                    if (!distributionMap.containsKey(key)){
                        accountingDistribution.setCardType(document.getDefaultCardTypeCode());
                        accountingDistribution.setObjectCode(mileageObjCode.getCode());
                        accountingDistribution.setObjectCodeName(mileageObjCode.getName());
                        distributionMap.put(key, accountingDistribution);
                    }
                    distributionMap.get(key).setSubTotal(distributionMap.get(key).getSubTotal().add(expense.getMileageTotal()));
                    distributionMap.get(key).setRemainingAmount(distributionMap.get(key).getRemainingAmount().add(expense.getMileageTotal()));
                }
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.service.impl.ExpenseServiceBase#getAllExpenseTotal(org.kuali.kfs.module.tem.document.TravelDocument, boolean)
     */
    @Override
    public KualiDecimal getAllExpenseTotal(TravelDocument document, boolean includeNonReimbursable) {
        KualiDecimal total = KualiDecimal.ZERO;

        for (PerDiemExpense expense : document.getPerDiemExpenses()){
            if ((expense.getPersonal().booleanValue() && includeNonReimbursable)
                    || !expense.getPersonal().booleanValue()){
                total = total.add(expense.getDailyTotal());
            }
        }

        return total;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.impl.ExpenseServiceBase#getNonReimbursableExpenseTotal(org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public KualiDecimal getNonReimbursableExpenseTotal(TravelDocument document) {
        // This is because for per diem, when the personal checkbox is checked the amount is already excluded from the total.
        return KualiDecimal.ZERO;
    }

    @Override
    public KualiDecimal getMealsAndIncidentalsGrandTotal(TravelDocument travelDocument) {
        KualiDecimal mealsAndIncidentalsTotal = KualiDecimal.ZERO;
        for (PerDiemExpense expense : travelDocument.getPerDiemExpenses()) {
            mealsAndIncidentalsTotal = mealsAndIncidentalsTotal.add(expense.getMealsAndIncidentals());
        }
        return mealsAndIncidentalsTotal;
    }

    @Override
    public KualiDecimal getLodgingGrandTotal(TravelDocument travelDocument) {
        KualiDecimal lodgingTotal = KualiDecimal.ZERO;
        for (PerDiemExpense perDiemExpense : travelDocument.getPerDiemExpenses()) {
            if (!perDiemExpense.getPersonal()) {
                lodgingTotal = lodgingTotal.add(perDiemExpense.getLodgingTotal());
            }
        }
        return lodgingTotal;
    }

    @Override
    public KualiDecimal getMileageTotalGrandTotal(TravelDocument travelDocument) {
        KualiDecimal mileageTotal = KualiDecimal.ZERO;
        for (PerDiemExpense perDiemExpense : travelDocument.getPerDiemExpenses()) {
            mileageTotal = mileageTotal.add(perDiemExpense.getMileageTotal());
        }
        return mileageTotal;
    }

    @Override
    public KualiDecimal getDailyTotalGrandTotal(TravelDocument travelDocument) {
        KualiDecimal dailyTotal = KualiDecimal.ZERO;
        for (PerDiemExpense perDiemExpense : travelDocument.getPerDiemExpenses()) {
            dailyTotal = dailyTotal.add(perDiemExpense.getDailyTotal());
        }
        return dailyTotal;
    }

    @Override
    public Integer getMilesGrandTotal(TravelDocument travelDocument) {
        Integer milesTotal = 0;
        for (PerDiemExpense perDiemExpense : travelDocument.getPerDiemExpenses()) {
            milesTotal = milesTotal + perDiemExpense.getMiles();
        }
        return milesTotal;
    }

    /**
     * Determines if per diem is handling lodging based on KFS-TEM / Document / PER_DIEM_CATEGORIES
     * @see org.kuali.kfs.module.tem.service.PerDiemService#isPerDiemHandlingLodging()
     */
    @Override
    public boolean isPerDiemHandlingLodging() {
        final Collection<String> perDiemCategoryValues = getParameterService().getParameterValuesAsString(TemParameterConstants.TEM_DOCUMENT.class, TemConstants.TravelParameters.PER_DIEM_CATEGORIES);
        for (String perDiemCategoryValue : perDiemCategoryValues) {
            final String[] keyValueSplit = perDiemCategoryValue.split("=");
            if (keyValueSplit.length == 2) {
                if (TemConstants.PerDiemType.lodging.name().equalsIgnoreCase(keyValueSplit[0])) {
                    return KFSConstants.ParameterValues.YES.equalsIgnoreCase(keyValueSplit[1]);
                }
            }
        }
        return false;
    }

    /**
     * Uses travelDocumentDao to look up per diem records (TravelDocumentDao handles the the date wierdnesses of per diem date)
     * @see org.kuali.kfs.module.tem.service.PerDiemService#getPerDiem(int, java.sql.Date, java.sql.Date)
     */
    @Override
    public PerDiem getPerDiem(int primaryDestinationId, java.sql.Timestamp perDiemDate, java.sql.Date effectiveDate) {
        final List<PerDiem> possiblePerDiems = getTravelDocumentDao().findEffectivePerDiems(primaryDestinationId, effectiveDate);
        Date date = KfsDateUtils.clearTimeFields(new Date(perDiemDate.getTime()));

        if (possiblePerDiems.isEmpty()) {
            return null;
        }
        if (possiblePerDiems.size() == 1) {
            return possiblePerDiems.get(0);
        }

        Collections.sort(possiblePerDiems, new PerDiemComparator());
        PerDiem foundPerDiem = null;
        for (PerDiem perDiem : possiblePerDiems) {
            if (isOnOrAfterSeasonBegin(perDiem.getSeasonBeginMonthAndDay(), perDiemDate)) {
                foundPerDiem = perDiem;
            }
        }
        if (foundPerDiem == null) {
            // no found per diem, so let's take the *last* one of the list (because years are circular and so the last of the list represents the beginning of the year; see KFSTP-926 for a discussion of this)
            foundPerDiem = possiblePerDiems.get(possiblePerDiems.size() - 1);
        }

        return foundPerDiem;
    }

    /**
     * Comparator to help us sort per diem records by season begin month/day
     */
    protected class PerDiemComparator implements Comparator<PerDiem> {
        /**
         * next compare method I write will use patty and selma, I promise
         * Sorts the season begin month/days such that earlier dates are chosen before later dates
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(PerDiem viola, PerDiem sebastian) {
            if (StringUtils.isBlank(viola.getSeasonBeginMonthAndDay())) {
                if (StringUtils.isBlank(sebastian.getSeasonBeginMonthAndDay())) {
                    return 0;
                }
                return 1; // sebastian has a value - choose sebastian
            }
            if (StringUtils.isBlank(sebastian.getSeasonBeginMonthAndDay())) {
                return -1; // viola has a value but not sebastian - choose viola
            }

            final String[] violaSeasonBegin = viola.getSeasonBeginMonthAndDay().split("/");
            final String[] sebastianSeasonBegin = sebastian.getSeasonBeginMonthAndDay().split("/");

            final int violaBeginMonth = Integer.parseInt(violaSeasonBegin[0]);
            final int sebastianBeginMonth = Integer.parseInt(sebastianSeasonBegin[0]);
            if (violaBeginMonth != sebastianBeginMonth) {
                return violaBeginMonth - sebastianBeginMonth;
            }

            final int violaBeginDay = Integer.parseInt(violaSeasonBegin[1]);
            final int sebastianBeginDay = Integer.parseInt(sebastianSeasonBegin[1]);
            if (violaBeginDay != sebastianBeginDay) {
                return violaBeginDay - sebastianBeginDay;
            }
            return 0;
        }
    }

    /**
     * Determines if the given date happens on or after the given season begin month/day for the year of the given date
     * @param seasonBegin the season begin month/day to check
     * @param d the date to check if on or after season begin
     * @return true if the given date is on or after the season begin date, false otherwise
     */
    protected boolean isOnOrAfterSeasonBegin(String seasonBegin, java.sql.Timestamp d) {
        if (StringUtils.isBlank(seasonBegin)) {
            return true; // no season begin/end?  Well...then we're after that, I should think
        }

        Calendar dCal = Calendar.getInstance();
        dCal.setTime(d);
        final int year = dCal.get(Calendar.YEAR);

        Calendar seasonBeginCal = getSeasonBeginMonthDayCalendar(seasonBegin, year);

        if (KfsDateUtils.isSameDay(dCal, seasonBeginCal)) { // let's see if they're on the same day, regardless of time
            return true;
        }
        if (dCal.after(seasonBeginCal)) { // now that we know they're not on the same day, time isn't such a big deal
            return true;
        }
        return false;
    }

    /**
     * Given a season begin month/day and a year, returns a Calendar representing the date
     * @param seasonBegin the season begin month/day
     * @param year the year to set for the calendar
     * @return the Calendar from the given date information
     */
    protected Calendar getSeasonBeginMonthDayCalendar(String seasonBegin, int year) {
        final String[] seasonBeginMonthDay = seasonBegin.split("/");
        Calendar seasonBeginCal = Calendar.getInstance();
        seasonBeginCal.set(Calendar.MONTH, Integer.parseInt(seasonBeginMonthDay[0]) - 1);
        seasonBeginCal.set(Calendar.DATE, Integer.parseInt(seasonBeginMonthDay[1]));
        seasonBeginCal.set(Calendar.YEAR, year);
        return seasonBeginCal;
    }


    @Override
    public void processExpense(TravelDocument travelDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        //do nothing
    }

    @Override
    public void updateExpense(TravelDocument travelDocument) {
        //do nothing
    }

    @Override
    public void calculateDistributionTotals(TravelDocument document, Map<String, AccountingDistribution> distributionMap, List<? extends TemExpense> expenses) {
        //not used for PerDiem
    }

    @Override
    public List<? extends TemExpense> getExpenseDetails(TravelDocument document) {
        //not used for PerDiem
        return null;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.PerDiemService#setPerDiemCategories(org.kuali.kfs.module.tem.document.web.struts.TravelFormBase)
     */
    @Override
    public void setPerDiemCategoriesAndBreakdown(TravelFormBase form) {
        Collection<String> perDiemCats = getParameterService().getParameterValuesAsString(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.PER_DIEM_CATEGORIES);
        form.parsePerDiemCategories(perDiemCats);

        //default to TA
        Boolean showPerDiemBreakdown = parameterService.getParameterValueAsBoolean(TravelAuthorizationDocument.class, TravelAuthorizationParameters.PER_DIEM_AMOUNT_EDITABLE_IND);
        if (TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT.equals(form.getDocTypeName())){
            showPerDiemBreakdown = parameterService.getParameterValueAsBoolean(TravelReimbursementDocument.class, TravelReimbursementParameters.PER_DIEM_AMOUNT_EDITABLE_IND);
        }
        form.setShowPerDiemBreakdown(showPerDiemBreakdown);
    }

    /**
     * Determines if:
     * <ul>
     * <li>A current mileage rate for the KFS-TEM / Document / PER_DIEM_MILEAGE_RATE_EXPENSE_TYPE_CODE is available; if it is not, then per diem cannot be created
     * </ul>
     * @param form the form with the document on it, which may help in making such a decision
     */
    @Override
    public boolean isMileageRateAvailableForAllPerDiem(TravelDocument doc) {
        final String defaultPerDiemMileageRate = getDefaultPerDiemMileageRateExpenseType();
        if (StringUtils.isBlank(defaultPerDiemMileageRate)) {
            return false;
        }
        if (StringUtils.isBlank(doc.getTripTypeCode()) || doc.getTripBegin() == null || doc.getTripEnd() == null) {
            return true; // we can't create per diem when trip begin or end are blank anyhow - but we shouldn't get the error
        }
        // now we need to loop through each day from begin date to end date to see if there is a validate mileage rate record for it
        Calendar currDay = Calendar.getInstance();
        currDay.setTime(KfsDateUtils.clearTimeFields(doc.getTripBegin()));
        Calendar lastDay = Calendar.getInstance();
        lastDay.setTime(KfsDateUtils.clearTimeFields(doc.getTripEnd()));

        while (currDay.before(lastDay) || currDay.equals(lastDay)) {
            java.sql.Date effectiveDay = doc.getEffectiveDateForPerDiem(new java.sql.Timestamp(currDay.getTimeInMillis()));
            final MileageRate currDayMileageRate = getMileageRateService().findMileageRateByExpenseTypeCodeAndDate(defaultPerDiemMileageRate, effectiveDay);
            if (currDayMileageRate == null) {
                return false;
            }
            currDay.add(Calendar.DATE, 1);
        }
        // we're good
        return true;
    }

    /**
     * Does the parameter look-up so that validations don't have to
     * @see org.kuali.kfs.module.tem.service.PerDiemService#getDefaultPerDiemMileageRateExpenseType()
     */
    @Override
    public String getDefaultPerDiemMileageRateExpenseType() {
        final String defaultPerDiemMileageRate = getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, TemConstants.TravelParameters.PER_DIEM_MILEAGE_RATE_EXPENSE_TYPE_CODE, KFSConstants.EMPTY_STRING);
        return defaultPerDiemMileageRate;
    }

    public TravelDocumentDao getTravelDocumentDao() {
        return travelDocumentDao;
    }

    public void setTravelDocumentDao(TravelDocumentDao travelDocumentDao) {
        this.travelDocumentDao = travelDocumentDao;
    }

    public TravelExpenseService getTravelExpenseService() {
        return travelExpenseService;
    }

    public void setTravelExpenseService(TravelExpenseService travelExpenseService) {
        this.travelExpenseService = travelExpenseService;
    }
}
