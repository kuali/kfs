/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.report.service.impl;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.text.StrBuilder;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KemidBenefittingOrganization;
import org.kuali.kfs.module.endow.businessobject.KemidHistoricalCash;
import org.kuali.kfs.module.endow.businessobject.MonthEndDate;
import org.kuali.kfs.module.endow.dataaccess.KemidBenefittingOrganizationDao;
import org.kuali.kfs.module.endow.dataaccess.KemidDao;
import org.kuali.kfs.module.endow.dataaccess.KemidHistoricalCashDao;
import org.kuali.kfs.module.endow.dataaccess.KemidReportGroupDao;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.report.service.EndowmentReportService;
import org.kuali.kfs.module.endow.report.util.EndowmentReportFooterDataHolder;
import org.kuali.kfs.module.endow.report.util.EndowmentReportFooterDataHolder.BenefittingForFooter;
import org.kuali.kfs.module.endow.report.util.EndowmentReportHeaderDataHolder;
import org.kuali.kfs.module.endow.report.util.KemidsWithMultipleBenefittingOrganizationsDataHolder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.api.campus.Campus;
import org.kuali.rice.location.api.campus.CampusService;

public abstract class EndowmentReportServiceImpl implements EndowmentReportService {

    protected final static String ALL_VALUES = "ALL";

    protected BusinessObjectService businessObjectService;
    protected ParameterService parameterService;
    protected DateTimeService dateTimeService;
    protected KEMService kemService;

    protected KemidDao kemidDao;
    protected KemidBenefittingOrganizationDao kemidBenefittingOrganizationDao;
    protected KemidReportGroupDao kemidReportGroupDao;
    protected KemidHistoricalCashDao kemidHistoricalCashDao;

    /**
     *
     * @see org.kuali.kfs.module.endow.report.service.TrialBalanceReportService#getInstitutionName()
     */
    @Override
    public String getInstitutionName() {
        return parameterService.getParameterValueAsString(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, KfsParameterConstants.INSTITUTION_NAME);
    }

    /**
     *
     * @see org.kuali.kfs.module.endow.report.service.TrialBalanceReportService#getReportRequestor()
     */
    @Override
    public String getReportRequestor() {
        return GlobalVariables.getUserSession().getPerson().getPrincipalName();
    }

    /**
     *
     * @see org.kuali.kfs.module.endow.report.service.TrialBalanceReportService#getBenefittingCampuses(java.util.List)
     */
    @Override
    public String getBenefittingCampuses(List<String> campuses) {
        String result = "";
        if (ObjectUtils.isNotNull(campuses) && !campuses.isEmpty()) {
            List<String> campusList = kemidBenefittingOrganizationDao.getCampusCodes(EndowPropertyConstants.CA_ORG_CAMPUS_CD, campuses);
            result = getListWithSeparator(campuses, ALL_VALUES);
        }
        return result;
    }

    /**
     *
     * @see org.kuali.kfs.module.endow.report.service.TrialBalanceReportService#getBenefittingCharts(java.util.List)
     */
    @Override
    public String getBenefittingCharts(List<String> charts) {
        String result = "";
        if (ObjectUtils.isNotNull(charts) && !charts.isEmpty()) {
            List<String> chartList = kemidBenefittingOrganizationDao.getAttributeValues(EndowPropertyConstants.KEMID_BENE_CHRT_CD, charts);
            result = getListWithSeparator(chartList, ALL_VALUES);
        }
        return result;
    }

    /**
     *
     * @see org.kuali.kfs.module.endow.report.service.TrialBalanceReportService#getBenefittingOrganizations(java.util.List)
     */
    @Override
    public String getBenefittingOrganizations(List<String> organizations) {
        String result = "";
        if (ObjectUtils.isNotNull(organizations) && !organizations.isEmpty()) {
            List<String> organizationList = kemidBenefittingOrganizationDao.getAttributeValues(EndowPropertyConstants.KEMID_BENE_ORG_CD, organizations);
            result = getListWithSeparator(organizationList, ALL_VALUES);
        }
        return result;
    }

    /**
     *
     * @see org.kuali.kfs.module.endow.report.service.TrialBalanceReportService#getKemidTypeCodes(java.util.List)
     */
    @Override
    public String getKemidTypeCodes(List<String> kemidTypeCodes) {
        String result = "";
        if (ObjectUtils.isNotNull(kemidTypeCodes) && !kemidTypeCodes.isEmpty()) {
            List<String> typeCodeList = kemidDao.getAttributeValues(EndowPropertyConstants.KEMID_TYPE_CODE, kemidTypeCodes);
            result = getListWithSeparator(typeCodeList, ALL_VALUES);
        }
        return result;
    }

    /**
     *
     * @see org.kuali.kfs.module.endow.report.service.TrialBalanceReportService#getKemidPurposeCodes(java.util.List)
     */
    @Override
    public String getKemidPurposeCodes(List<String> kemidPurposes) {
        String result = "";
        if (ObjectUtils.isNotNull(kemidPurposes) && !kemidPurposes.isEmpty()) {
            List<String> purposeList = kemidDao.getAttributeValues(EndowPropertyConstants.KEMID_PRPS_CD, kemidPurposes);
            result = getListWithSeparator(purposeList, ALL_VALUES);
        }
        return result;
    }

    /**
     *
     * @see org.kuali.kfs.module.endow.report.service.TrialBalanceReportService#getCombineGroupCodes(java.util.List)
     */
    @Override
    public String getCombineGroupCodes(List<String> combineGroupCodes) {
        String result = "";
        if (ObjectUtils.isNotNull(combineGroupCodes) && !combineGroupCodes.isEmpty()) {
            List<String> groupCodeList = kemidReportGroupDao.getAttributeValues(EndowPropertyConstants.KEMID_REPORT_GRP_CD, combineGroupCodes);
            result = getListWithSeparator(groupCodeList, ALL_VALUES);
        }
        return result;
    }

    /**
     *
     * @see org.kuali.kfs.module.endow.report.service.EndowmentReportService#createReportHeaderSheetData(java.util.List, java.util.List, java.util.List, java.util.List, java.util.List, java.util.List, java.util.List, java.lang.String, java.lang.String)
     */
    @Override
    public EndowmentReportHeaderDataHolder createReportHeaderSheetData(
                    List<String> kemidsSelected,
                    List<String> benefittingOrganziationCampuses,
                    List<String> benefittingOrganziationCharts,
                    List<String> benefittingOrganziations,
                    List<String> typeCodes,
                    List<String> purposeCodes,
                    List<String> combineGroupCodes,
                    String reportName,
                    String endowmnetOption,
                    String reportOption) {

        EndowmentReportHeaderDataHolder reportRequestHeaderDataHolder = new EndowmentReportHeaderDataHolder();

        // get request report
        reportRequestHeaderDataHolder.setInstitutionName(getInstitutionName());
        reportRequestHeaderDataHolder.setReportRequested(reportName);
        reportRequestHeaderDataHolder.setRequestedBy(getReportRequestor());
        String endowmentOptionDesc = "";
        if (EndowConstants.EndowmentReport.BOTH.equalsIgnoreCase(endowmnetOption)) {
            endowmentOptionDesc = EndowConstants.EndowmentReport.BOTH_ENDOWMENT_OPTION;
        } else {
            endowmentOptionDesc = EndowConstants.YES.equalsIgnoreCase(endowmnetOption) ? EndowConstants.EndowmentReport.ENDOWMENT : EndowConstants.EndowmentReport.NON_ENDOWED;
        }
        reportRequestHeaderDataHolder.setEndowmentOption(endowmentOptionDesc);
        String reportOptionDesc = "";
        if (reportOption != null) {
            if (EndowConstants.EndowmentReport.BOTH.equalsIgnoreCase(reportOption)) {
                reportOptionDesc = EndowConstants.EndowmentReport.BOTH_REPORT_OPTION;
            } else if (EndowConstants.EndowmentReport.DETAIL.equalsIgnoreCase(reportOption)) {
                reportOptionDesc = EndowConstants.EndowmentReport.DETAIL_REPORT;
            } else if (EndowConstants.EndowmentReport.TOTAL.equalsIgnoreCase(reportOption)) {
                reportOptionDesc = EndowConstants.EndowmentReport.TOTAL_REPORT;
            }
        }
        reportRequestHeaderDataHolder.setReportOption(reportOptionDesc);

        // get criteria
        reportRequestHeaderDataHolder.setBenefittingCampus(getBenefittingCampuses(benefittingOrganziationCampuses));
        reportRequestHeaderDataHolder.setBenefittingChart(getBenefittingCharts(benefittingOrganziationCharts));
        reportRequestHeaderDataHolder.setBenefittingOrganization(getBenefittingOrganizations(benefittingOrganziations));
        reportRequestHeaderDataHolder.setKemidTypeCode(getKemidTypeCodes(typeCodes));
        reportRequestHeaderDataHolder.setKemidPurposeCode(getKemidPurposeCodes(purposeCodes));
        reportRequestHeaderDataHolder.setCombineGroupCode(getCombineGroupCodes(combineGroupCodes));

        // get kemids selected
        reportRequestHeaderDataHolder.setKemidsSelected(kemidsSelected);

        // get kemdis with multiple benefittting organizations
        reportRequestHeaderDataHolder.setKemidsWithMultipleBenefittingOrganizationsDataHolders(getKemidsWithMultipleBenefittingOrganizations(kemidsSelected));

        return reportRequestHeaderDataHolder;
    }

    /**
     * Gets the kemids that meet the given conditions
     *
     * @param benefittingOrganziationCampusCodes
     * @param benefittingOrganziationChartCodes
     * @param benefittingOrganziationCodes
     * @param typeCodes
     * @param purposeCodes
     * @param combineGroupCodes
     * @return
     */
    public List<String> getKemidsByOtherCriteria(List<String> benefittingOrganziationCampusCodes, List<String> benefittingOrganziationChartCodes,
            List<String> benefittingOrganziationCodes, List<String> typeCodes, List<String> purposeCodes, List<String> combineGroupCodes) {

        // 4.1.9 - If any of the non-required criteria are left blank, ignore it as a criterion
        // The criteria should be the AND combination.

        Set<String> kemids = new HashSet<String>();

        if (ObjectUtils.isNotNull(benefittingOrganziationCampusCodes) && !benefittingOrganziationCampusCodes.isEmpty()) {
            retainCommonKemids(kemids, kemidBenefittingOrganizationDao.getKemidsByCampusCode(benefittingOrganziationCampusCodes));
        }
        if (ObjectUtils.isNotNull(benefittingOrganziationChartCodes) && !benefittingOrganziationChartCodes.isEmpty()) {
            retainCommonKemids(kemids, kemidBenefittingOrganizationDao.getKemidsByAttribute(EndowPropertyConstants.KEMID_BENE_CHRT_CD, benefittingOrganziationChartCodes));
        }
        if (ObjectUtils.isNotNull(benefittingOrganziationCodes) && !benefittingOrganziationCodes.isEmpty()) {
            retainCommonKemids(kemids, kemidBenefittingOrganizationDao.getKemidsByAttribute(EndowPropertyConstants.KEMID_BENE_ORG_CD, benefittingOrganziationCodes));
        }
        if (ObjectUtils.isNotNull(typeCodes) && !typeCodes.isEmpty()) {
            retainCommonKemids(kemids, kemidDao.getKemidsByAttribute(EndowPropertyConstants.KEMID_TYPE_CODE, typeCodes));
        }
        if (ObjectUtils.isNotNull(purposeCodes) && !purposeCodes.isEmpty()) {
            retainCommonKemids(kemids, kemidDao.getKemidsByAttribute(EndowPropertyConstants.KEMID_PRPS_CD, purposeCodes));
        }
        if (ObjectUtils.isNotNull(combineGroupCodes) && !combineGroupCodes.isEmpty()) {
            retainCommonKemids(kemids, kemidReportGroupDao.getKemidsByAttribute(EndowPropertyConstants.KEMID_REPORT_GRP_CD, combineGroupCodes));
        }

        return new ArrayList<String>(kemids);
    }

    /**
     * Retains the kemids that are in common.
     *
     * @param kemids
     * @param list
     */
    public void retainCommonKemids(Set<String> kemids, List<String> list) {
        if (list != null && !list.isEmpty()) {
            if (kemids.isEmpty()) {
                kemids.addAll(list);
            } else {
                kemids.retainAll(list);
            }
        }
    }

    /**
     * method to pickup all kemids based on user selection of endowmentOption and closed indicator
     *
     * @param kemids
     * @param endowmentOption
     * @param endingDate
     * @return List<String> newKemids
     */
    @Override
    public List<String> getKemidsBasedOnUserSelection(List<String> kemids, String endowmentOption, String closedIndicator) {
        List<String> newKemids = new ArrayList<String>();

        //get KEMID records for the given kemid and endowmentOption and closedIndicator..
        List<KEMID> kemidRecords = kemidDao.getKemidRecordsByIds(kemids, endowmentOption, closedIndicator);

        for (KEMID kemidRecord : kemidRecords) {
            if (!newKemids.contains(kemidRecord.getKemid())) {
                newKemids.add(kemidRecord.getKemid());
            }
        }

        return newKemids;
    }

    /**
     * method to pickup all kemids from the list of kemids where for each kemid, if
     * there is record in END_HIST_CSH_T table
     *
     * @param kemids
     * @param beginningDate
     * @param endingDate
     * @return List<String> newKemids
     */
    @Override
    public List<String> getKemidsInHistoryCash(List<String> kemids, String beginningDate, String endingDate) {
        List<String> newKemids = new ArrayList<String>();
        List<KemidHistoricalCash> kemidHistoryCash = new ArrayList<KemidHistoricalCash>();

        MonthEndDate beginningMED = getPreviousMonthEndDate(convertStringToDate(beginningDate));
        MonthEndDate endingMED = getMonthEndDate(convertStringToDate(endingDate));

        kemidHistoryCash = kemidHistoricalCashDao.getHistoricalCashRecords(kemids, beginningMED.getMonthEndDateId(), endingMED.getMonthEndDateId());

        for (KemidHistoricalCash historyCash : kemidHistoryCash) {
            if (!newKemids.contains(historyCash.getKemid())) {
                newKemids.add(historyCash.getKemid());
            }
        }

        return newKemids;
    }

    /**
     * Gets a collection of kemids that exist in historical cash
     *
     * @param kemids
     * @param endingDate
     * @return
     */
    public List<String> getKemidsInHistoryCash(List<String> kemids, String endingDate) {

        MonthEndDate endingMED = getMonthEndDate(convertStringToDate(endingDate));
        List<KemidHistoricalCash> kemidHistoryCash = kemidHistoricalCashDao.getHistoricalCashRecords(kemids, endingMED.getMonthEndDateId());
        List<String> newKemids = new ArrayList<String>();
        for (KemidHistoricalCash historyCash : kemidHistoryCash) {
            if (!newKemids.contains(historyCash.getKemid())) {
                newKemids.add(historyCash.getKemid());
            }
        }

        return newKemids;
    }

    /**
     * Gets the previous month end date
     *
     * @param date
     * @return
     */
    protected MonthEndDate getPreviousMonthEndDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        return getMonthEndDate(new java.sql.Date(calendar.getTimeInMillis()));
    }

    /**
     * Convert string to date
     *
     * @param stringDate
     * @return
     */
    protected Date convertStringToDate(String stringDate) {
        Date date = null;
        try {
            date = dateTimeService.convertToSqlDate(stringDate);
        } catch (ParseException e) {
            return null;
        }
        return date;
    }

    /**
     * Convert java.sql.date to string
     *
     * @param date
     * @return
     */
    protected String convertDateToString(Date date) {
        return dateTimeService.toDateString(date);
    }

    /**
     * Gets a month end date object
     *
     * @param date
     * @return
     */
    protected MonthEndDate getMonthEndDate(Date date) {
        Map<String,Object> primaryKeys = new HashMap<String,Object>();
        primaryKeys.put(EndowPropertyConstants.MONTH_END_DATE, date);
        return businessObjectService.findByPrimaryKey(MonthEndDate.class, primaryKeys);
    }

    /**
     * Gets a benefitting organization object
     *
     * @param kemid
     * @return
     */
    protected List<KemidBenefittingOrganization> getKemidBenefittingOrganization(String kemid) {
        Map<String,Object> primaryKeys = new HashMap<String,Object>();
        primaryKeys.put(EndowPropertyConstants.KEMID_BENE_KEMID, kemid);
        return (List<KemidBenefittingOrganization>) businessObjectService.findMatching(KemidBenefittingOrganization.class, primaryKeys);
    }

    /**
     * Gets an organization object
     *
     * @param chartCode
     * @param organizationCode
     * @return
     */
    protected Organization getOrganization(String chartCode, String organizationCode) {
        Map<String,Object> primaryKeys = new HashMap<String,Object>();
        primaryKeys.put(EndowPropertyConstants.CA_ORG_CHRT_CD, chartCode);
        primaryKeys.put(EndowPropertyConstants.CA_ORG_CD, organizationCode);
        return businessObjectService.findByPrimaryKey(Organization.class, primaryKeys);
    }

    /**
     * Creates footer data
     *
     * @param kemidOjb
     * @return
     */
    protected EndowmentReportFooterDataHolder createFooterData(KEMID kemidOjb) {

        List<KemidBenefittingOrganization> benefittingOrganizationList = getKemidBenefittingOrganization(kemidOjb.getKemid());
        EndowmentReportFooterDataHolder footerDataHolder = new EndowmentReportFooterDataHolder();

        footerDataHolder.setReference(kemidOjb.getKemid());
        footerDataHolder.setEstablishedDate(getDateTimeService().toDateString(kemidOjb.getDateEstablished()));
        footerDataHolder.setKemidType(kemidOjb.getType().getName());
        footerDataHolder.setKemidPurpose(kemidOjb.getPurpose().getName());
        footerDataHolder.setReportRunDate(getDateTimeService().toDateString(kemService.getCurrentDate()));

        if (benefittingOrganizationList != null) {
            for (KemidBenefittingOrganization benefittingOrganization : benefittingOrganizationList) {
                BenefittingForFooter benefitting = footerDataHolder.createBenefittingForFooter();
                benefitting.setChartName(benefittingOrganization.getChart().getName());
                benefitting.setBenefittingPercent(benefittingOrganization.getBenefitPrecent().toString());
                Organization organization = getOrganization(benefittingOrganization.getBenefittingChartCode(), benefittingOrganization.getBenefittingOrgCode());
                // some organizations and campuses may not exist
                if (ObjectUtils.isNotNull(organization)) {
                    benefitting.setOrganizationName(organization.getOrganizationName());
                    Campus campus = SpringContext.getBean(CampusService.class).getCampus(organization.getOrganizationPhysicalCampusCode());
                    if (campus != null) {
                        benefitting.setCampusName(campus.getName());
                    } else {
                        benefitting.setCampusName("");
                    }
                } else {
                    benefitting.setOrganizationName("");
                }
            }
        }

        return footerDataHolder;
    }


    /**
     * Concatenate strings with a separator
     *
     * @param stringList
     * @param defaultEmptyValue
     * @return
     */
    protected String getListWithSeparator(List<String> stringList, String defaultEmptyValue) {
        final String SEPARATOR = " ";
        StrBuilder builder = new StrBuilder();
        builder = builder.appendWithSeparators(stringList, SEPARATOR);
        return builder.isEmpty() ?  defaultEmptyValue : builder.toString();
    }

    /**
     *
     * @see org.kuali.kfs.module.endow.report.service.TrialBalanceReportService#getKemidsWithMultipleBenefittingOrganizations(java.util.List)
     */
    @Override
    public List<KemidsWithMultipleBenefittingOrganizationsDataHolder> getKemidsWithMultipleBenefittingOrganizations(List<String> kemids) {
        return kemidBenefittingOrganizationDao.getKemidsWithMultipleBenefittingOrganizations(kemids);
    }

    /**
     * Sets business object service
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets parameter service
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets Kem service
     *
     * @param kemService
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }

    /**
     * Sets date time service
     *
     * @param dateTimeService
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Sets kemid dao
     */
    public void setKemidDao(KemidDao kemidDao) {
        this.kemidDao = kemidDao;
    }

    /**
     * sets benefitting organization dao
     */
    public void setKemidBenefittingOrganizationDao(KemidBenefittingOrganizationDao kemidBenefittingOrganizationDao) {
        this.kemidBenefittingOrganizationDao = kemidBenefittingOrganizationDao;
    }

    /**
     * Sets report group dao
     */
    public void setKemidReportGroupDao(KemidReportGroupDao kemidReportGroupDao) {
        this.kemidReportGroupDao = kemidReportGroupDao;
    }

    /**
     * Gest historical cash dao
     *
     * @return
     */
    public KemidHistoricalCashDao getKemidHistoricalCashDao() {
        return kemidHistoricalCashDao;
    }

    /**
     * sets attribute kemidHistoricalCashDao
     */
    public void setKemidHistoricalCashDao(KemidHistoricalCashDao kemidHistoricalCashDao) {
        this.kemidHistoricalCashDao = kemidHistoricalCashDao;
    }

    /**
     * gets attribute dateTimeService
     * @return dateTimeService
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }
}
