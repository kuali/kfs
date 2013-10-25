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
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.batch.PerDiemLoadStep;
import org.kuali.kfs.module.tem.batch.businessobject.MealBreakDownStrategy;
import org.kuali.kfs.module.tem.businessobject.AccountingDistribution;
import org.kuali.kfs.module.tem.businessobject.ExpenseTypeObjectCode;
import org.kuali.kfs.module.tem.businessobject.PerDiem;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.TEMExpense;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.module.tem.dataaccess.PerDiemDao;
import org.kuali.kfs.module.tem.dataaccess.TravelDocumentDao;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.web.struts.TravelFormBase;
import org.kuali.kfs.module.tem.service.PerDiemService;
import org.kuali.kfs.module.tem.service.TEMExpenseService;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.module.tem.util.ExpenseUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.api.state.State;
import org.kuali.rice.location.api.state.StateService;

/**
 * implement the service method calls defined in PerDiemService
 */
public class PerDiemServiceImpl extends ExpenseServiceBase implements PerDiemService, TEMExpenseService {

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

    Collection<PerDiem> persistedPerDiems;


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


    @Override
    public void processPerDiem() {
        Date today = dateTimeService.getCurrentSqlDate();
        Collection<PerDiem> perDiems = perDiemDao.findAllPerDiemsOrderedBySeasonAndDest();
        PerDiem lastPerDiem = perDiems.iterator().next();
        List<PerDiem> group = new ArrayList<PerDiem>();
        for (PerDiem pd : perDiems) {
            if (lastPerDiem.getPrimaryDestinationId().equals(pd.getPrimaryDestinationId())) {
                group.add(pd);
            } else {
                //process the group
                processPerDiem(group, today);

                group.clear();
                group.add(pd);
            }
            lastPerDiem = pd;
        }
    }

    protected void processPerDiem(List<PerDiem> perDiems, Date today) {

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        PerDiem foundActivePerDiem = null;
        for (PerDiem perDiem : perDiems) {
            String season = perDiem.getSeasonBeginMonthAndDay()+"/"+Calendar.getInstance().get(Calendar.YEAR);
            java.util.Date seasonDate;
            try {
                seasonDate = sdf.parse(season);

                if (today.after(seasonDate)) {
                    perDiem.setActive(Boolean.TRUE);
                    if (foundActivePerDiem == null) {
                        foundActivePerDiem = perDiem;
                    } else {
                        foundActivePerDiem.setActive(Boolean.FALSE);
                        businessObjectService.save(foundActivePerDiem);
                    }
                } else {
                    perDiem.setActive(Boolean.FALSE);
                }

                businessObjectService.save(perDiem);
            }
            catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
        }
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
     * @see org.kuali.kfs.module.tem.service.PerDiemService#retrievePreviousPerDiem(java.util.List)
     */
    @Override
    public <T extends PerDiem> List<PerDiem> retrievePreviousPerDiem(List<T> perDiemList) {
        List<PerDiem> previousPerDiemList = new ArrayList<PerDiem>();

        for (T perDiem : perDiemList) {
            List<PerDiem> previousPerDiems = this.retrievePreviousPerDiem(perDiem);

            if (ObjectUtils.isNotNull(previousPerDiems) && !previousPerDiems.isEmpty()) {
                previousPerDiemList.addAll(previousPerDiems);
            }
        }

        return previousPerDiemList;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.PerDiemService#retrievePreviousPerDiem(org.kuali.kfs.module.tem.businessobject.PerDiem)
     */
    @Override
    public <T extends PerDiem> List<PerDiem> retrievePreviousPerDiem(T perDiem) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(TemPropertyConstants.PRIMARY_DESTINATION_ID, perDiem.getPrimaryDestinationId());
        fieldValues.put(TemPropertyConstants.SEASON_BEGIN_MONTH_AND_DAY, perDiem.getSeasonBeginMonthAndDay());
        fieldValues.put(KFSPropertyConstants.ACTIVE, Boolean.TRUE);

        return (List<PerDiem>) this.getBusinessObjectService().findMatching(PerDiem.class, fieldValues);
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
            persistedPerDiems = businessObjectService.findAll(PerDiem.class);
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
     * @see org.kuali.kfs.module.tem.service.PerDiemService#getPerDiem(int, java.sql.Timestamp)
     */
    @Override
    public PerDiem getPerDiem(int primaryDestinationId, Timestamp perDiemDate) {
        Map<String,Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(TemPropertyConstants.PRIMARY_DESTINATION_ID, primaryDestinationId);
        fieldValues.put(KFSPropertyConstants.ACTIVE, KFSConstants.ACTIVE_INDICATOR);
        fieldValues.put(TemPropertyConstants.PER_DIEM_LOOKUP_DATE, perDiemDate);
        final PerDiem perDiem = getTravelDocumentDao().findPerDiem(fieldValues);
        return perDiem;
    }

    @Override
    public void processExpense(TravelDocument travelDocument) {
        //do nothing
    }

    @Override
    public void updateExpense(TravelDocument travelDocument) {
        //do nothing
    }

    @Override
    public void calculateDistributionTotals(TravelDocument document, Map<String, AccountingDistribution> distributionMap, List<? extends TEMExpense> expenses) {
        //not used for PerDiem
    }

    @Override
    public List<? extends TEMExpense> getExpenseDetails(TravelDocument document) {
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
