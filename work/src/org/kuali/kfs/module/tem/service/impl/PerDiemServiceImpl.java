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

import static org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.LODGING_OBJECT_CODE;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.PARAM_DTL_TYPE;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.PER_DIEM_OBJECT_CODE;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.module.tem.TemConstants.PerDiemParameter;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.batch.PerDiemLoadStep;
import org.kuali.kfs.module.tem.batch.businessobject.MealBreakDownStrategy;
import org.kuali.kfs.module.tem.businessobject.MileageRateObjCode;
import org.kuali.kfs.module.tem.businessobject.PerDiem;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.TEMExpense;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.web.bean.AccountingDistribution;
import org.kuali.kfs.module.tem.service.PerDiemService;
import org.kuali.kfs.module.tem.service.TEMExpenseService;
import org.kuali.kfs.module.tem.util.ExpenseUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.bo.State;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.StateService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * implement the service method calls defined in PerDiemService
 */
public class PerDiemServiceImpl extends ExpenseServiceBase implements PerDiemService, TEMExpenseService {
    
    private static Logger LOG = Logger.getLogger(PerDiemServiceImpl.class);

    private DateTimeService dateTimeService;
    private ParameterService parameterService;
    private BusinessObjectService businessObjectService;
    private StateService stateService;
    private Map<String, MealBreakDownStrategy> mealBreakDownStrategies;
    private String allStateCodes;

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
    @Override
    public <T extends PerDiem> List<T> retrieveActivePerDiem() {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.ACTIVE, Boolean.TRUE);

        return (List<T>) this.getBusinessObjectService().findMatching(PerDiem.class, fieldValues);
    }

    /**
     * @see org.kuali.kfs.module.tem.service.PerDiemService#deactivateAndSavePerDiem()
     */
    @Override
    @Transactional
    public void deactivateAndSavePerDiem() {
        List<PerDiem> perDiemList = this.retrieveActivePerDiem();

        if (ObjectUtils.isNotNull(perDiemList) && !perDiemList.isEmpty()) {
            this.deactivatePerDiemBySeasonEndDate(perDiemList);

            this.getBusinessObjectService().save(perDiemList);
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.service.PerDiemService#deactivatePerDiemBySeasonEndDate(java.util.List)
     */
    @Override
    public <T extends PerDiem> void deactivatePerDiemBySeasonEndDate(List<T> perDiemList) {
        Date date = dateTimeService.getCurrentSqlDate();

        this.deactivatePerDiemBySeasonEndDate(perDiemList, date);
    }

    /**
     * @see org.kuali.kfs.module.tem.service.PerDiemService#deactivatePerDiemBySeasonEndDate(java.util.List, java.sql.Date)
     */
    @Override
    public <T extends PerDiem> void deactivatePerDiemBySeasonEndDate(List<T> perDiemList, Date date) {
        for (PerDiem perDiem : perDiemList) {
            this.deactivatePerDiemSeasonEndDate(perDiem, date);
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.service.PerDiemService#deactivatePerDiemBySeasonEndDate(org.kuali.kfs.module.tem.businessobject.PerDiem,
     *      java.sql.Date)
     */
    @Override
    public <T extends PerDiem> void deactivatePerDiemSeasonEndDate(T perDiem, Date date) {
        /*Date seasonEndDate = perDiem.getSeasonEndDate();

        if (ObjectUtils.isNotNull(seasonEndDate) && seasonEndDate.before(date)) {
            //perDiem.setActive(false);
        }*/
    }

    /**
     * @see org.kuali.kfs.module.tem.service.PerDiemService#deactivatePerDiemByEffectiveDate(java.util.List, java.sql.Date)
     */
    @Override
    public <T extends PerDiem> void deactivatePerDiemByEffectiveDate(List<T> perDiemList, Date date) {
        for (PerDiem perDiem : perDiemList) {
            this.deactivatePerDiemByEffectiveDate(perDiem, date);
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.service.PerDiemService#deactivatePerByDiemEffectiveDate(org.kuali.kfs.module.tem.businessobject.PerDiem,
     *      java.sql.Date)
     */
    @Override
    public <T extends PerDiem> void deactivatePerDiemByEffectiveDate(T perDiem, Date date) {
        Date effectiveDate = perDiem.getEffectiveFromDate();

        if (ObjectUtils.isNotNull(effectiveDate) && effectiveDate.before(date)) {
            perDiem.setActive(false);
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
        String countryState = perDiem.getCountryState();
        String institutionState = this.getInstitutionState();
        
        String tripTypeCode = this.getInternationalTripTypeCode();
        if(StringUtils.equals(countryState, institutionState)) {
        	tripTypeCode = this.getInStateTripTypeCode();
        } else if(getAllStateCodes().contains( ";" + countryState.toUpperCase() +  ";")) {
        	tripTypeCode = this.getOutStateTripTypeCode();
        }
        
        perDiem.setTripTypeCode(tripTypeCode);
    }

    /**
     * get institution state code defined as an application parameter
     * 
     * @return institution state code
     */
    protected String getInstitutionState() {
        String institutionState = this.getParameterService().getParameterValue(PerDiemLoadStep.class, PerDiemParameter.INSTITUTION_STATE_PARAM_NAME);

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
        fieldValues.put(TemPropertyConstants.COUNTRY_STATE, perDiem.getCountryState());
        fieldValues.put(TemPropertyConstants.COUNTY, perDiem.getCounty());
        fieldValues.put(TemPropertyConstants.PRIMARY_DESTINATION, perDiem.getPrimaryDestination());
        fieldValues.put(TemPropertyConstants.SEASON_BEGIN_MONTH_AND_DAY, perDiem.getSeasonBeginMonthAndDay());
        fieldValues.put(TemPropertyConstants.TRIP_TYPE, perDiem.getTripTypeCode());
        fieldValues.put(KFSPropertyConstants.ACTIVE, Boolean.TRUE);

        return (List) this.getBusinessObjectService().findMatching(PerDiem.class, fieldValues);
    }

    /**
     * @see org.kuali.kfs.module.tem.service.PerDiemService#retrieveExpireDeactivatePreviousPerDiem(java.util.List, boolean)
     */
    @Override
    public <T extends PerDiem> List<PerDiem> retrieveExpireDeactivatePreviousPerDiem(List<T> perDiemList, boolean isDeactivate) {
        List<PerDiem> previousPerDiemList = new ArrayList<PerDiem>();

        for (T perDiem : perDiemList) {
            List<PerDiem> previousPerDiems = this.retrieveExpireDeactivatePreviousPerDiem(perDiem, isDeactivate);

            if (ObjectUtils.isNotNull(previousPerDiems) && !previousPerDiems.isEmpty()) {
                previousPerDiemList.addAll(previousPerDiems);
            }
        }

        return previousPerDiemList;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.PerDiemService#retrieveExpireDeactivatePreviousPerDiem(org.kuali.kfs.module.tem.businessobject.PerDiem,
     *      boolean)
     */
    @Override
    public <T extends PerDiem> List<PerDiem> retrieveExpireDeactivatePreviousPerDiem(T perDiem, boolean isDeactivate) {
        List<PerDiem> perviousPerDiems = this.retrievePreviousPerDiem(perDiem);

        Date effectiveDate = perDiem.getEffectiveFromDate();
        String expirationDateAsString = this.getDateTimeService().toDateString(DateUtils.addDays(effectiveDate, -1));

        try {
            Date expirationDate = this.getDateTimeService().convertToSqlDate(expirationDateAsString);
            this.expireOrDeactivate(perviousPerDiems, expirationDate, isDeactivate);
        }
        catch (ParseException ex) {
            throw new RuntimeException("failed to parse the date: " + expirationDateAsString);
        }

        return perviousPerDiems;
    }
    
    /**
     * check whether the given per diem exists in the database
     * 
     * @param perDiem the given per diem
     * @return true if the given per diem exists in the database
     */
    @Override
    public <T extends PerDiem> boolean hasExistingPerDiem(T perDiem) {
    	Map<String, Object> fieldValues = new HashMap<String, Object>();
    	boolean atLeastOneNull = false;
        
        if(StringUtils.isNotEmpty(perDiem.getCountryState())) {
        	fieldValues.put(TemPropertyConstants.COUNTRY_STATE, perDiem.getCountryState());
        } else {
        	atLeastOneNull = true;
        }
        if(StringUtils.isNotEmpty(perDiem.getCounty())) {
        	fieldValues.put(TemPropertyConstants.COUNTY, perDiem.getCounty());
        } else {
        	atLeastOneNull = true;
        }
        if(StringUtils.isNotEmpty(perDiem.getPrimaryDestination())) {
        	fieldValues.put(TemPropertyConstants.PRIMARY_DESTINATION, perDiem.getPrimaryDestination());
        } else {
        	atLeastOneNull = true;
        }
        if(StringUtils.isNotEmpty(perDiem.getSeasonBeginMonthAndDay())) {
        	fieldValues.put(TemPropertyConstants.SEASON_BEGIN_MONTH_AND_DAY, perDiem.getSeasonBeginMonthAndDay());
        } else {
        	atLeastOneNull = true;
        }
        if(ObjectUtils.isNotNull(perDiem.getEffectiveFromDate())) {
        	fieldValues.put(TemPropertyConstants.EFFECTIVE_FROM_DATE, perDiem.getEffectiveFromDate());
        } else {
        	atLeastOneNull = true;
        }
        fieldValues.put(TemPropertyConstants.TRIP_TYPE, perDiem.getTripTypeCode());

        if (atLeastOneNull) {
        	List<PerDiem> results = (List<PerDiem>) this.getBusinessObjectService().findMatching(PerDiem.class, fieldValues);
        
        	boolean existing = false;
	    	for(PerDiem result : results) {
	    		existing = false;
	        	if(!fieldValues.containsKey(TemPropertyConstants.COUNTRY_STATE)) {
	        		if(StringUtils.isEmpty(result.getCountryState())) {
	        			existing = true;
	        		} else {
	        			existing = false;
	        		}
	        	}
	        	if(!fieldValues.containsKey(TemPropertyConstants.COUNTY)) {
	        		if(StringUtils.isEmpty(result.getCounty())) {
	        			existing = true;
	        		} else {
	        			existing = false;
	        		}
	            }
	            if(!fieldValues.containsKey(TemPropertyConstants.PRIMARY_DESTINATION)) {
	            	if(StringUtils.isEmpty(result.getPrimaryDestination())) {
	        			existing = true;
	        		} else {
	        			existing = false;
	        		}
	            }
	            if(!fieldValues.containsKey(TemPropertyConstants.SEASON_BEGIN_MONTH_AND_DAY)) {
	            	if(StringUtils.isEmpty(result.getSeasonBeginMonthAndDay())) {
	        			existing = true;
	        		} else {
	        			existing = false;
	        		}
	            }
	            if(!fieldValues.containsKey(TemPropertyConstants.EFFECTIVE_FROM_DATE)) {
	            	if(ObjectUtils.isNull(result.getEffectiveFromDate())) {
	        			existing = true;
	        		} else {
	        			existing = false;
	        		}
	            }
	            if(existing) {
	            	return existing;
	            }
	        }
	
	        return existing;
        } else {
        	int count = this.getBusinessObjectService().countMatching(PerDiem.class, fieldValues);

            return count > 0;
        }
    }

    /**
     * set expiration date of the given per diems
     */
    protected void expireOrDeactivate(List<PerDiem> perDiemList, Date expirationDate, boolean isDeactivate) {
        for (PerDiem perDiem : perDiemList) {
            if (ObjectUtils.isNotNull(expirationDate)) {
                perDiem.setEffectiveToDate(expirationDate);
            }

            if (isDeactivate) {
                perDiem.setActive(false);
            }
        }
    }

    /**
     * get out state trip type code defined as an application parameter
     * 
     * @return out state trip type code
     */
    protected String getOutStateTripTypeCode() {
        String outStateTripTypeCode = this.getParameterService().getParameterValue(PerDiemLoadStep.class, PerDiemParameter.OUT_STATE_TRIP_TYPE_CODE_PARAM_NAME);

        return outStateTripTypeCode;
    }

    /**
     * get in state trip type code defined as an application parameter
     * 
     * @return in state trip type code
     */
    protected String getInStateTripTypeCode() {
        String inStateTripTypeCode = this.getParameterService().getParameterValue(PerDiemLoadStep.class, PerDiemParameter.IN_STATE_TRIP_TYPE_CODE_PARAM_NAME);

        return inStateTripTypeCode;
    }
    
    /**
     * get international trip type code defined as an application parameter
     * 
     * @return international trip type code
     */
    protected String getInternationalTripTypeCode() {
        String internationalTripTypeCode = this.getParameterService().getParameterValue(PerDiemLoadStep.class, PerDiemParameter.INTERNATIONAL_TRIP_TYPE_CODE_PARAM_NAME);

        return internationalTripTypeCode;
    }

    /**
     * Gets the parameterService attribute.
     * 
     * @return Returns the parameterService.
     */
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
			final List<State> codes = getStateService().findAllStates(KFSConstants.COUNTRY_CODE_UNITED_STATES);
	        
	        final StringBuffer sb = new StringBuffer();
	        sb.append(";").append(KFSConstants.COUNTRY_CODE_UNITED_STATES.toUpperCase()).append(";");
	        for (final State state : codes) {
	            if(state.isActive()) {                
	                sb.append(state.getPostalStateCode().toUpperCase()).append( ";");
	            }
	        }
	
	        allStateCodes = sb.toString();
		}		
		
		return allStateCodes;
	}

	public String getObjectCodeFrom(final TravelDocument travelDocument, String paramName) {
	    if (travelDocument instanceof TravelReimbursementDocument){
	        final String parameterValue = getParameterService().getParameterValue(PARAM_NAMESPACE, PARAM_DTL_TYPE, paramName);
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
                String key = perDiemObjCode.getCode() + "-" + document.getExpenseTypeCode();
                
                for(PerDiemExpense expense : document.getPerDiemExpenses()){
                    if (!expense.getPersonal()){
                        if (!distributionMap.containsKey(key)){
                            accountingDistribution.setCardType(document.getExpenseTypeCode());
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
        String key = lodgingObjCode.getCode() + "-" + document.getExpenseTypeCode();

        if (document.getPerDiemExpenses() != null) {           
            for(PerDiemExpense expense : document.getPerDiemExpenses()){
                if (!distributionMap.containsKey(key)){
                    accountingDistribution.setCardType(document.getExpenseTypeCode());
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
                if (expense.getMileageRateId() != null) {
                    String mileageCode = getMileageObjectCodeFrom(document, expense.getMileageRateId());
                    if (document instanceof TravelAuthorizationDocument){
                        if (document.getTripType() != null){
                            mileageCode = document.getTripType().getEncumbranceObjCode();
                        }
                        else{
                            mileageCode = null;
                        }
                    }
                    LOG.debug("Looking up Object Code for chart = "+ defaultChartCode+ " mileageCode = "+ mileageCode);
                    final ObjectCode mileageObjCode = getObjectCodeService().getByPrimaryIdForCurrentYear(defaultChartCode, mileageCode);
                    LOG.debug("Got mileage object code "+ mileageObjCode);
                    String key = mileageObjCode.getCode() + "-" + document.getExpenseTypeCode();
                    
                    if (!distributionMap.containsKey(key)){
                        accountingDistribution.setCardType(document.getExpenseTypeCode());
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
    
    protected String getMileageObjectCodeFrom(final TravelDocument travelDocument, Integer mileageRateId) {
        String objCode = null;
        if (travelDocument instanceof TravelReimbursementDocument){
            final String travelerType = travelDocument.getTraveler().getTravelerTypeCode();
            final String tripType = travelDocument.getTripType().getCode();

            
            Map<String, Object> fields = new HashMap<String, Object>();

            fields.put("travelerTypeCode", travelerType);
            fields.put("tripTypeCode", tripType);
            fields.put("mileageRateId", mileageRateId);

            List<MileageRateObjCode> mileageObjCodes = (List<MileageRateObjCode>) businessObjectService.findMatching(MileageRateObjCode.class, fields);
            for (MileageRateObjCode mileageObjCode : mileageObjCodes) {
                objCode = mileageObjCode.getFinancialObjectCode();
            }
        }
        else{
            if (travelDocument.getTripType() != null){
                objCode = travelDocument.getTripType().getEncumbranceObjCode();
            }
        }
        
        return objCode;
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
//        KualiDecimal total = KualiDecimal.ZERO;
//        
//        for (PerDiemExpense expense : document.getPerDiemExpenses()){
//            if (expense.getPersonal().booleanValue()){
//                total = total.add(expense.getDailyTotal());
//            }
//        }
//        return total;

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

}
