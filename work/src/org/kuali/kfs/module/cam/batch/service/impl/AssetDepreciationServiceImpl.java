/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.cam.batch.service.impl;

import static org.kuali.kfs.sys.KFSConstants.BALANCE_TYPE_ACTUAL;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.mail.MessagingException;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.gl.service.impl.StringHelper;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.batch.AssetDepreciationStep;
import org.kuali.kfs.module.cam.batch.AssetPaymentInfo;
import org.kuali.kfs.module.cam.batch.service.AssetDepreciationService;
import org.kuali.kfs.module.cam.batch.service.ReportService;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetDepreciationConvention;
import org.kuali.kfs.module.cam.businessobject.AssetDepreciationTransaction;
import org.kuali.kfs.module.cam.businessobject.AssetObjectCode;
import org.kuali.kfs.module.cam.businessobject.AssetYearEndDepreciation;
import org.kuali.kfs.module.cam.document.dataaccess.DepreciableAssetsDao;
import org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.mail.MailMessage;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.exception.InvalidAddressException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.MailService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;
import org.quartz.CronExpression;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is a service that calculates the depreciation amount for each asset that has a eligible asset payment.
 * <p>
 * When an error occurs running this process, a pdf file will be created with the error message. However, this doesn't mean that
 * this process automatically leaves all the records as they were right before running the program. If the process fails, is
 * suggested to do the following before trying to run the process again: a.)Delete gl pending entry depreciation entries: DELETE
 * FROM GL_PENDING_ENTRY_T WHERE FDOC_TYP_CD = 'DEPR' b.)Substract from the accumulated depreciation amount the depreciation
 * calculated for the fiscal month that was ran, and then reset the depreciation amount field for the fiscal month that was ran. ex:
 * Assuming that the fiscal month = 8 then: UPDATE CM_AST_PAYMENT_T SET AST_ACUM_DEPR1_AMT = AST_ACUM_DEPR1_AMT -
 * AST_PRD8_DEPR1_AMT, AST_PRD8_DEPR1_AMT=0
 */
@Transactional
public class AssetDepreciationServiceImpl implements AssetDepreciationService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetDepreciationServiceImpl.class);
    protected ParameterService parameterService;
    protected AssetService assetService;
    protected ReportService reportService;
    protected DateTimeService dateTimeService;
    protected DepreciableAssetsDao depreciableAssetsDao;
    protected ConfigurationService kualiConfigurationService;
    protected GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    protected BusinessObjectService businessObjectService;
    protected UniversityDateService universityDateService;
    protected OptionsService optionsService;
    protected DataDictionaryService dataDictionaryService;
    protected DepreciationBatchDao depreciationBatchDao;
    protected String cronExpression;
    protected MailService mailService;
    protected volatile WorkflowDocumentService workflowDocumentService;

    /**
     * @see org.kuali.kfs.module.cam.batch.service.AssetDepreciationService#runDepreciation()
     */
    @Override
    public void runDepreciation() {
        Integer fiscalYear = -1;
        Integer fiscalMonth = -1;
        String errorMsg = "";
        List<String> documentNos = new ArrayList<String>();
        List<String[]> reportLog = new ArrayList<String[]>();
        Collection<AssetObjectCode> assetObjectCodes = new ArrayList<AssetObjectCode>();
        boolean hasErrors = false;
        Calendar depreciationDate = dateTimeService.getCurrentCalendar();
        java.sql.Date depDate = null;
        Calendar currentDate = dateTimeService.getCurrentCalendar();
        String depreciationDateParameter = null;
        DateFormat dateFormat = new SimpleDateFormat(CamsConstants.DateFormats.YEAR_MONTH_DAY);
        boolean executeJob = false;
        String errorMessage = kualiConfigurationService.getPropertyValueAsString(CamsKeyConstants.Depreciation.DEPRECIATION_ALREADY_RAN_MSG);

        try {
            executeJob = runAssetDepreciation();
            if(executeJob)
            {
                LOG.info("*******" + CamsConstants.Depreciation.DEPRECIATION_BATCH + " HAS BEGUN *******");

                /*
                 * Getting the system parameter "DEPRECIATION_DATE" When this parameter is used to determine which fiscal month and year
                 * is going to be depreciated If blank then the system will take the system date to determine the fiscal period
                 */
                if (parameterService.parameterExists(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_RUN_DATE_PARAMETER)) {
                    depreciationDateParameter = parameterService.getParameterValueAsString(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_RUN_DATE_PARAMETER).trim();
                } else {
                    throw new IllegalStateException(kualiConfigurationService.getPropertyValueAsString(CamsKeyConstants.Depreciation.DEPRECIATION_DATE_PARAMETER_NOT_FOUND));
                }
                
                // This validates the system parameter depreciation_date has a valid format of YYYY-MM-DD.
                if ( !StringUtils.isBlank(depreciationDateParameter) ) {
                    try {
                        depreciationDate.setTime(dateFormat.parse(depreciationDateParameter));
                    } catch (ParseException e) {
                        throw new IllegalArgumentException(kualiConfigurationService.getPropertyValueAsString(CamsKeyConstants.Depreciation.INVALID_DEPRECIATION_DATE_FORMAT));
                    }
                }
                if ( LOG.isInfoEnabled() ) {
                    LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Depreciation run date: " + depreciationDateParameter);
                }

                UniversityDate universityDate = businessObjectService.findBySinglePrimaryKey(UniversityDate.class, new java.sql.Date(depreciationDate.getTimeInMillis()));
                if (universityDate == null) {
                    throw new IllegalStateException(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_UNIV_DATE_NOT_FOUND));
                }

                fiscalYear = universityDate.getUniversityFiscalYear();
                fiscalMonth = new Integer(universityDate.getUniversityFiscalAccountingPeriod());
                  assetObjectCodes = getAssetObjectCodes(fiscalYear);
                // If the depreciation date is not = to the system date then, the depreciation process cannot run.
                if ( LOG.isInfoEnabled() ) {
                    LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Fiscal Year = " + fiscalYear + " & Fiscal Period=" + fiscalMonth);
                }
                int fiscalStartMonth = Integer.parseInt(optionsService.getCurrentYearOptions().getUniversityFiscalYearStartMo());
                reportLog.addAll(depreciableAssetsDao.generateStatistics(true, null, fiscalYear, fiscalMonth, depreciationDate,dateTimeService.toDateString(depreciationDate.getTime()), assetObjectCodes,fiscalStartMonth, errorMessage));
                // update if fiscal period is 12
                depreciationBatchDao.updateAssetsCreatedInLastFiscalPeriod(fiscalMonth, fiscalYear);
                // Retrieving eligible asset payment details
                LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting list of asset payments eligible for depreciation.");
                Collection<AssetPaymentInfo> depreciableAssetsCollection = depreciationBatchDao.getListOfDepreciableAssetPaymentInfo(fiscalYear, fiscalMonth, depreciationDate);
                // if we have assets eligible for depreciation then, calculate depreciation and create glpe's transactions
                if (depreciableAssetsCollection != null && !depreciableAssetsCollection.isEmpty()) {
                    SortedMap<String, AssetDepreciationTransaction> depreciationTransactions = this.calculateDepreciation(fiscalYear, fiscalMonth, depreciableAssetsCollection, depreciationDate, assetObjectCodes);
                        processGeneralLedgerPendingEntry(fiscalYear, fiscalMonth, documentNos, depreciationTransactions);
                }
                else {
                    throw new IllegalStateException(kualiConfigurationService.getPropertyValueAsString(CamsKeyConstants.Depreciation.NO_ELIGIBLE_FOR_DEPRECIATION_ASSETS_FOUND));
                }
            }
        } catch (Exception e) {
            LOG.error("Error occurred");
            LOG.error(CamsConstants.Depreciation.DEPRECIATION_BATCH + "**************************************************************************");
            LOG.error(CamsConstants.Depreciation.DEPRECIATION_BATCH + "AN ERROR HAS OCCURRED! - ERROR: " + e.getMessage(),e);
            LOG.error(CamsConstants.Depreciation.DEPRECIATION_BATCH + "**************************************************************************");
            hasErrors = true;
            errorMsg = "Depreciation process ran unsucessfuly.\nReason:" + e.getMessage();
        }
        finally {
            if (!hasErrors && executeJob) {
                int fiscalStartMonth = Integer.parseInt(optionsService.getCurrentYearOptions().getUniversityFiscalYearStartMo());                
                reportLog.addAll(depreciableAssetsDao.generateStatistics(false, documentNos, fiscalYear, fiscalMonth, depreciationDate,dateTimeService.toDateString(depreciationDate.getTime()), assetObjectCodes, fiscalStartMonth, errorMessage));
            }
            // the report will be generated only when there is an error or when the log has something.
            if (!reportLog.isEmpty() || !errorMsg.trim().equals("")) {
                reportService.generateDepreciationReport(reportLog, errorMsg, depreciationDateParameter);
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("*******" + CamsConstants.Depreciation.DEPRECIATION_BATCH + " HAS ENDED *******");
            }
        }
    }

    public Collection<AssetObjectCode> getAssetObjectCodes(Integer fiscalYear) {
        LOG.debug("DepreciableAssetsDAoOjb.getAssetObjectCodes() -  started");
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting asset object codes.");

        Collection<AssetObjectCode> assetObjectCodesCollection;
        HashMap<String, Object> fields = new HashMap<String, Object>();
        fields.put(CamsPropertyConstants.AssetObject.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        fields.put(CamsPropertyConstants.AssetObject.ACTIVE, Boolean.TRUE);
        assetObjectCodesCollection = (Collection<AssetObjectCode>) businessObjectService.findMatching(AssetObjectCode.class, fields);

        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Finished getting asset object codes - which are:" + assetObjectCodesCollection.toString());
        LOG.debug("DepreciableAssetsDAoOjb.getAssetObjectCodes() -  ended");
        return assetObjectCodesCollection;
    }

    // CSU 6702 BEGIN
    /**
     * @see org.kuali.kfs.module.cam.batch.service.AssetDepreciationService#runYearEndDepreciation(java.lang.Integer)
     */
    @Override
    public void runYearEndDepreciation(Integer fiscalYearToDepreciate){
        Integer fiscalYear = -1;
        Integer fiscalMonth = -1;
        List<String> documentNos = new ArrayList<String>();
        String errorMsg = "";
        
        List<String[]> reportLog = new ArrayList<String[]>();
        boolean hasErrors = false;
        boolean includeRetired = false;
        Calendar depreciationDate = Calendar.getInstance();
        AssetYearEndDepreciation assetYearEndDepreciation = null;
        assetService = SpringContext.getBean(AssetService.class);
        List<String> notAcceptedAssetStatus = new ArrayList<String>();
        boolean statusContainsR = false;
        String notAcceptedAssetStatusString = "";
        String depreciationDateParameter = fiscalYearToDepreciate.toString()+getLastDayOfFiscalyear();
        notAcceptedAssetStatus.addAll(parameterService.getParameterValuesAsString(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_NON_CAPITAL_ASSETS_STATUS_CODES));
        LOG.info("notAcceptedAssetStatusString = " + notAcceptedAssetStatusString);
        if (notAcceptedAssetStatus.contains("R")) {
            LOG.info("looks like notAcceptedAssetStatusString contains R");
            statusContainsR = true;
            notAcceptedAssetStatus.remove("R");
            for (int i = 0; i < notAcceptedAssetStatus.size(); i++) {
                String s =  notAcceptedAssetStatus.get(i);
                notAcceptedAssetStatusString = notAcceptedAssetStatusString+s+";";
            }
            if (notAcceptedAssetStatusString.endsWith(";")){
                notAcceptedAssetStatusString = notAcceptedAssetStatusString.substring(0, (notAcceptedAssetStatusString.length()-1));
            }
            Parameter.Builder param = Parameter.Builder.create( parameterService.getParameter(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_NON_CAPITAL_ASSETS_STATUS_CODES) );
            param.setValue(notAcceptedAssetStatusString);
            parameterService.updateParameter(param.build());
        }

        try{
            LOG.info("*******YEAR END DEPRECIATION - HAS BEGUN *******");

            //
            // Getting the system parameter "YEARENDDEPR_INCLUDE_RETIRED" When this parameter is used to determine whether
            // to include retired assets in the depreciation
            //
            if ( LOG.isInfoEnabled() ) {
                LOG.info("parameterService.getParameterValueAsString(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, PurapConstants.ParameterConstants.YEARENDDEPR_INCLUDE_RETIRED) = " + parameterService.getParameterValueAsString(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, PurapConstants.ParameterConstants.INCLUDE_RETIRED_ASSETS_IND));
            }
            includeRetired=parameterService.getParameterValueAsBoolean(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, PurapConstants.ParameterConstants.INCLUDE_RETIRED_ASSETS_IND);
            
            UniversityDate universityDate = businessObjectService.findBySinglePrimaryKey(UniversityDate.class, new java.sql.Date(depreciationDate.getTimeInMillis()));
            if (universityDate == null) {
                throw new IllegalStateException(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_UNIV_DATE_NOT_FOUND));
            }

            fiscalYear = universityDate.getUniversityFiscalYear();
            fiscalMonth = new Integer(universityDate.getUniversityFiscalAccountingPeriod());

            depreciationDate.setTime(java.sql.Date.valueOf(fiscalYearToDepreciate.toString()+getLastDayOfFiscalyear()));
            fiscalYear = fiscalYearToDepreciate;
            fiscalMonth = 12;
            Collection<AssetObjectCode> assetObjectCodes = getAssetObjectCodes(fiscalYear);

            // If the depreciation date is not = to the system date then, the depreciation process cannot run.
            if ( LOG.isInfoEnabled() ) {
                LOG.info("YEAR END DEPRECIATION - " + "Fiscal Year = " + fiscalYear + " & Fiscal Period=" + fiscalMonth);
            }

            // mjmc this caused arrayIndexOutOfBounds: 16
// TODO            reportLog.addAll(depreciableAssetsDao.generateStatistics(true, null, fiscalYear, fiscalMonth, depreciationDate, true));

            // update if fiscal period is 12
            depreciationBatchDao.updateAssetsCreatedInLastFiscalPeriod(fiscalMonth, fiscalYear);
            // Retrieving eligible asset payment details
            LOG.info("YEAR END DEPRECIATION - Getting list of YEAR END DEPRECIATION asset payments eligible for depreciation.");
            Collection<AssetPaymentInfo> depreciableAssetsCollection = depreciationBatchDao.getListOfDepreciableAssetPaymentInfoYearEnd(fiscalYear, fiscalMonth, depreciationDate, includeRetired);
            // if we have assets eligible for depreciation then, calculate depreciation and create glpe's transactions
            if (depreciableAssetsCollection != null && !depreciableAssetsCollection.isEmpty()) {
                SortedMap<String, AssetDepreciationTransaction> depreciationTransactions = this.calculateYearEndDepreciation(depreciableAssetsCollection, depreciationDate, fiscalYearToDepreciate, fiscalYear, fiscalMonth, assetObjectCodes);
                processYearEndGeneralLedgerPendingEntry(fiscalYear, documentNos, depreciationTransactions);
            } else {
                throw new IllegalStateException(kualiConfigurationService.getPropertyValueAsString(CamsKeyConstants.Depreciation.NO_ELIGIBLE_FOR_DEPRECIATION_ASSETS_FOUND));
            }
        } catch(Exception e) {
            LOG.error("YEAR END DEPRECIATION - **************************************************************************");
            LOG.error("YEAR END DEPRECIATION - AN ERROR HAS OCCURRED! - ERROR: " + e.getClass().getName() + " : " + e.getMessage());
            LOG.error("YEAR END DEPRECIATION - **************************************************************************");
            LOG.error(e);
            hasErrors = true;
            errorMsg = "YEAR END DEPRECIATION -  process ran unsucessfuly.\nReason:" + e.getMessage();
        } finally {
            if (!hasErrors) {

                // mjmc java.lang.ArrayIndexOutOfBoundsException: 16
// TODO                reportLog.addAll(depreciableAssetsDao.generateStatistics(false, documentNos, fiscalYear, fiscalMonth, depreciationDate, true));

                if (assetYearEndDepreciation != null) {
                    assetYearEndDepreciation.setRunDate(new java.sql.Date(new java.util.Date().getTime()));
                }
            }

            // the report will be generated only when there is an error or when the log has something.
            if (!reportLog.isEmpty() || !errorMsg.trim().equals("")) {
                // mjmc java.lang.ArrayIndexOutOfBoundsException: 16
                reportService.generateDepreciationReport(reportLog, errorMsg, depreciationDateParameter);
            }

            LOG.info("******* YEAR END DEPRECIATION - HAS ENDED *******");
        }

        // reset param so that retired assets are not depreciated during the rest of the year
        if (statusContainsR){
            notAcceptedAssetStatusString = notAcceptedAssetStatusString+";R";
            if ( LOG.isInfoEnabled() ) {
                LOG.info("notAcceptedAssetStatusString after reset= " + notAcceptedAssetStatusString);
            }
            Parameter.Builder param = Parameter.Builder.create( parameterService.getParameter(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_NON_CAPITAL_ASSETS_STATUS_CODES) );
            param.setValue(notAcceptedAssetStatusString);
            parameterService.updateParameter(param.build());
            if ( LOG.isInfoEnabled() ) {
                LOG.info(CamsConstants.Parameters.NON_DEPRECIABLE_NON_CAPITAL_ASSETS_STATUS_CODES+" now = "+ parameterService.getParameterValueAsString(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_NON_CAPITAL_ASSETS_STATUS_CODES));
            }
        }
    }

    protected boolean runAssetDepreciation() throws ParseException {
        boolean executeJob = false;
        List<String> errorMessages = new ArrayList<String>();
        Date currentDate = convertToDate(dateTimeService.toDateString(dateTimeService.getCurrentDate()));
        Date beginDate = getBlankOutBeginDate(errorMessages);
        Date endDate = getBlankOutEndDate(errorMessages);

        if (hasBlankOutPeriodStarted(beginDate, endDate, errorMessages)) {
                String blankOutPeriodrunDate = parameterService.getParameterValueAsString(AssetDepreciationStep.class, CamsConstants.Parameters.BLANK_OUT_PERIOD_RUN_DATE);
                if(!StringHelper.isNullOrEmpty(blankOutPeriodrunDate)){
                    Date runDate = convertToDate(blankOutPeriodrunDate);

                    if(runDate.compareTo(beginDate)>=0 && runDate.compareTo(endDate)<=0) {
                        if(currentDate.equals(runDate)) {
                            executeJob = true;
                        }
                        else {
                            LOG.info("Today is not BLANK_OUT_PERIOD_RUN_DATE. executeJob not set to true");
                        }

                    }
                    else {
                        String blankOutBegin =  parameterService.getParameterValueAsString(AssetDepreciationStep.class, CamsConstants.Parameters.BLANK_OUT_BEGIN_MMDD);
                        String blankOutEnd =  parameterService.getParameterValueAsString(AssetDepreciationStep.class, CamsConstants.Parameters.BLANK_OUT_END_MMDD);
                        String message =  "BLANK_OUT_PERIOD_RUN_DATE: " + blankOutPeriodrunDate + " is not in the blank out period range." + "Blank out period range is [ " +
                        blankOutBegin + "-" + blankOutEnd + " ] ." ;
                        errorMessages.add(message);
                        LOG.info(message);
                    }
                }
                else {
                    String message = "Parameter BLANK_OUT_PERIOD_RUN_DATE (component: Asset Depreciation Step) is not set" +
                    " Please set the date correctly to run the job.";
                    errorMessages.add(message);
                    LOG.info(message);
                }
        }
        else {
            CronExpression cronExpression = new CronExpression(this.cronExpression);
            Date validTimeAfter = cronExpression.getNextValidTimeAfter(dateTimeService.getCurrentDate());
            String scheduleJobDate = dateTimeService.toString(validTimeAfter, CamsConstants.DateFormats.MONTH_DAY_YEAR);
            if(scheduleJobDate.equals(currentDate)){
                executeJob = true;
            }else {
                LOG.info("Cron condition not met. executeJob not set to true");
            }
        }

        if(!executeJob && !errorMessages.isEmpty()) {
            sendWarningMail(errorMessages);
        }

        return executeJob;
    }

    protected boolean hasBlankOutPeriodStarted(Date beginDate, Date endDate, List<String> errorMessages) throws ParseException {
        Date currentDate = convertToDate(dateTimeService.toDateString(dateTimeService.getCurrentDate()));
        String blankOutBegin =  parameterService.getParameterValueAsString(AssetDepreciationStep.class, CamsConstants.Parameters.BLANK_OUT_BEGIN_MMDD);
        String blankOutEnd =  parameterService.getParameterValueAsString(AssetDepreciationStep.class, CamsConstants.Parameters.BLANK_OUT_END_MMDD);
        if(ObjectUtils.isNotNull(beginDate) && ObjectUtils.isNotNull(endDate)) {
            if(currentDate.compareTo(beginDate)>=0 && currentDate.compareTo(endDate)<=0 ) {
                return true;
            }
        }
        else {
            String message = "Unable to determine blank out period for a given " + blankOutBegin +
            " - " + blankOutEnd + " range .";

            errorMessages.add(message);
            LOG.info(message);
        }

        return false;
    }

    /**
     *
     * This method calculate blank out period end date.
     * @return blank out period end date in MM/dd/yyyy format.
     * @throws ParseException
     */
    private Date getBlankOutEndDate(List<String> errorMessages) throws ParseException {
        String endDate = parameterService.getParameterValueAsString(AssetDepreciationStep.class, CamsConstants.Parameters.BLANK_OUT_END_MMDD);
        if(!StringHelper.isNullOrEmpty(endDate)) {
            int endDay = new Integer(StringUtils.substringAfterLast(endDate, "/")).intValue();
            int endMonth = new Integer(StringUtils.substringBeforeLast(endDate, "/")).intValue()-1  ;
            Calendar blankOutEndcalendar = Calendar.getInstance();
            blankOutEndcalendar.set(blankOutEndcalendar.get(Calendar.YEAR), endMonth , endDay);
            return  convertToDate(dateTimeService.toString(blankOutEndcalendar.getTime(), CamsConstants.DateFormats.MONTH_DAY_YEAR));

        }
        else {

            String message  = "Parameter BLANK_OUT_END_MMDD (component:Asset Depreciation Step) is not set." ;
            errorMessages.add(message);
            LOG.info(message);

        }

        return null;
    }

    /**
     *
     * This method calculate blank out period begin date.
     * @return blank out period begin date in MM/dd/yyyy format.
     * @throws ParseException
     */
    private Date getBlankOutBeginDate(List<String> errorMessages) throws ParseException {
        String beginDate =  parameterService.getParameterValueAsString(AssetDepreciationStep.class, CamsConstants.Parameters.BLANK_OUT_BEGIN_MMDD);

        if(!StringHelper.isNullOrEmpty(beginDate)) {
            int beginDay = new Integer(StringUtils.substringAfterLast(beginDate, "/")).intValue();
            int beginMonth = new Integer(StringUtils.substringBeforeLast(beginDate, "/")).intValue()-1;
            Calendar blankOutBegincalendar = Calendar.getInstance();
            blankOutBegincalendar.set(blankOutBegincalendar.get(Calendar.YEAR),beginMonth , beginDay);
            return convertToDate(dateTimeService.toString(blankOutBegincalendar.getTime(), CamsConstants.DateFormats.MONTH_DAY_YEAR));

        }
        else {
            String message  = "Parameter BLANK_OUT_BEGIN_MMDD (component:Asset Depreciation Step) is not set.";
            errorMessages.add(message);
            LOG.info(message);

        }


       return null;
    }

    private Date convertToDate(String date) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(CamsConstants.DateFormats.MONTH_DAY_YEAR);
        dateFormat.setLenient(false);
        return dateFormat.parse(date);

    }


    /**
     * This method calculates the depreciation of each asset payment, creates the depreciation transactions that will be stored in
     * the general ledger pending entry table
     *
     * @param depreciableAssetsCollection asset payments eligible for depreciation
     * @return SortedMap with a list of depreciation transactions
     */
    protected SortedMap<String, AssetDepreciationTransaction> calculateDepreciation(Integer fiscalYear, Integer fiscalMonth, Collection<AssetPaymentInfo> depreciableAssetsCollection, Calendar depreciationDate, Collection<AssetObjectCode> assetObjectCodes) {
        LOG.debug("calculateDepreciation() - start");

        Collection<String> organizationPlantFundObjectSubType = new ArrayList<String>();
        Collection<String> campusPlantFundObjectSubType = new ArrayList<String>();
        SortedMap<String, AssetDepreciationTransaction> depreciationTransactionSummary = new TreeMap<String, AssetDepreciationTransaction>();
        double monthsElapsed = 0d;
        double assetLifeInMonths = 0d;
        KualiDecimal accumulatedDepreciationAmount = KualiDecimal.ZERO;
        Calendar assetDepreciationDate = Calendar.getInstance();

        try {
            LOG.debug(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting the parameters for the plant fund object sub types.");
            // Getting system parameters needed.
            if (parameterService.parameterExists(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_ORGANIZATON_PLANT_FUND_SUB_OBJECT_TYPES)) {
                organizationPlantFundObjectSubType = new ArrayList<String>( parameterService.getParameterValuesAsString(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_ORGANIZATON_PLANT_FUND_SUB_OBJECT_TYPES) );
            }
            if (parameterService.parameterExists(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPES)) {
                campusPlantFundObjectSubType = new ArrayList<String>( parameterService.getParameterValuesAsString(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPES) );
            }
            // Initializing the asset payment table.
            depreciationBatchDao.resetPeriodValuesWhenFirstFiscalPeriod(fiscalMonth);
            LOG.debug("getBaseAmountOfAssets(Collection<AssetPayment> depreciableAssetsCollection) - Started.");
            // Invoking method that will calculate the base amount for each asset payment transactions, which could be more than 1
            // per asset.
            LOG.debug(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Calculating the base amount for each asset.");
            Map<Long, KualiDecimal> salvageValueAssetDeprAmounts = depreciationBatchDao.getPrimaryDepreciationBaseAmountForSV();
            // Retrieving the object asset codes.
            Map<String, AssetObjectCode> assetObjectCodeMap = buildChartObjectToCapitalizationObjectMap(assetObjectCodes);
            Map<String, ObjectCode> capitalizationObjectCodes = new HashMap<String, ObjectCode>();

            // Reading asset payments
            LOG.debug(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Reading collection with eligible asset payment details.");
            int counter = 0;
            List<AssetPaymentInfo> saveList = new ArrayList<AssetPaymentInfo>();
            for (AssetPaymentInfo assetPaymentInfo : depreciableAssetsCollection) {
                AssetObjectCode assetObjectCode = assetObjectCodeMap.get(assetPaymentInfo.getChartOfAccountsCode() + "-" + assetPaymentInfo.getFinancialObjectCode());
                if (assetObjectCode == null) {
                    LOG.error(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Asset object code not found for " + fiscalYear + "-" + assetPaymentInfo.getChartOfAccountsCode() + "-" + assetPaymentInfo.getFinancialObjectCode());
                    LOG.error(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Asset payment is not included in depreciation " + assetPaymentInfo.getCapitalAssetNumber() + " - " + assetPaymentInfo.getPaymentSequenceNumber());
                    continue;
                }
                ObjectCode accumulatedDepreciationFinancialObject = getDepreciationObjectCode(fiscalYear, capitalizationObjectCodes, assetPaymentInfo, assetObjectCode.getAccumulatedDepreciationFinancialObjectCode());
                ObjectCode depreciationExpenseFinancialObject = getDepreciationObjectCode(fiscalYear, capitalizationObjectCodes, assetPaymentInfo, assetObjectCode.getDepreciationExpenseFinancialObjectCode());

                if (ObjectUtils.isNull(accumulatedDepreciationFinancialObject)) {
                    LOG.error(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Accumulated Depreciation Financial Object Code not found for " + fiscalYear + "-" + assetPaymentInfo.getChartOfAccountsCode() + "-" + assetObjectCode.getAccumulatedDepreciationFinancialObjectCode());
                    LOG.error(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Asset payment is not included in depreciation " + assetPaymentInfo.getCapitalAssetNumber() + " - " + assetPaymentInfo.getPaymentSequenceNumber());
                    continue;
                }

                if (ObjectUtils.isNull(depreciationExpenseFinancialObject)) {
                    LOG.error(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Depreciation Expense Financial Object Code not found for " + fiscalYear + "-" + assetPaymentInfo.getChartOfAccountsCode() + "-" + assetObjectCode.getDepreciationExpenseFinancialObjectCode());
                    LOG.error(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Asset payment is not included in depreciation " + assetPaymentInfo.getCapitalAssetNumber() + " - " + assetPaymentInfo.getPaymentSequenceNumber());
                    continue;
                }
                Long assetNumber = assetPaymentInfo.getCapitalAssetNumber();
                assetDepreciationDate.setTime(assetPaymentInfo.getDepreciationDate());
                accumulatedDepreciationAmount = KualiDecimal.ZERO;
                KualiDecimal deprAmountSum = salvageValueAssetDeprAmounts.get(assetNumber);
                // Calculating the life of the asset in months.
                assetLifeInMonths = assetPaymentInfo.getDepreciableLifeLimit() * 12;
                // Calculating the months elapsed for the asset using the depreciation date and the asset service date.
                monthsElapsed = (depreciationDate.get(Calendar.MONTH) - assetDepreciationDate.get(Calendar.MONTH) + (depreciationDate.get(Calendar.YEAR) - assetDepreciationDate.get(Calendar.YEAR)) * 12) + 1;

                // **************************************************************************************************************
                // CALCULATING ACCUMULATED DEPRECIATION BASED ON FORMULA FOR SINGLE LINE AND SALVAGE VALUE DEPRECIATION METHODS.
                // **************************************************************************************************************
                KualiDecimal primaryDepreciationBaseAmount = assetPaymentInfo.getPrimaryDepreciationBaseAmount();
                if (primaryDepreciationBaseAmount == null) {
                    assetPaymentInfo.setPrimaryDepreciationBaseAmount(KualiDecimal.ZERO);
                }

                if (assetPaymentInfo.getAccumulatedPrimaryDepreciationAmount() == null) {
                    assetPaymentInfo.setAccumulatedPrimaryDepreciationAmount(KualiDecimal.ZERO);
                }

                // If the months elapsed >= to the life of the asset (in months) then, the accumulated depreciation should be:
                if (monthsElapsed >= assetLifeInMonths) {
                    if (CamsConstants.Asset.DEPRECIATION_METHOD_STRAIGHT_LINE_CODE.equals(assetPaymentInfo.getPrimaryDepreciationMethodCode())) {
                        accumulatedDepreciationAmount = primaryDepreciationBaseAmount;
                    }
                    else if (CamsConstants.Asset.DEPRECIATION_METHOD_SALVAGE_VALUE_CODE.equals(assetPaymentInfo.getPrimaryDepreciationMethodCode()) && deprAmountSum != null && deprAmountSum.isNonZero()) {
                        accumulatedDepreciationAmount = primaryDepreciationBaseAmount.subtract((primaryDepreciationBaseAmount.divide(deprAmountSum)).multiply(assetPaymentInfo.getSalvageAmount()));
                    }
                } // If the month elapse < to the life of the asset (in months) then....
                else {
                    if (CamsConstants.Asset.DEPRECIATION_METHOD_STRAIGHT_LINE_CODE.equals(assetPaymentInfo.getPrimaryDepreciationMethodCode())) {
                        accumulatedDepreciationAmount = new KualiDecimal((monthsElapsed / assetLifeInMonths) * primaryDepreciationBaseAmount.doubleValue());
                    }
                    else if (CamsConstants.Asset.DEPRECIATION_METHOD_SALVAGE_VALUE_CODE.equals(assetPaymentInfo.getPrimaryDepreciationMethodCode()) && deprAmountSum != null && deprAmountSum.isNonZero()) {
                        accumulatedDepreciationAmount = new KualiDecimal((monthsElapsed / assetLifeInMonths) * (primaryDepreciationBaseAmount.subtract((primaryDepreciationBaseAmount.divide(deprAmountSum)).multiply(assetPaymentInfo.getSalvageAmount()))).doubleValue());
                    }
                }
                // Calculating in process fiscal month depreciation amount
                KualiDecimal transactionAmount = accumulatedDepreciationAmount.subtract(assetPaymentInfo.getAccumulatedPrimaryDepreciationAmount());

                String transactionType = KFSConstants.GL_DEBIT_CODE;
                if (transactionAmount.isNegative()) {
                    transactionType = KFSConstants.GL_CREDIT_CODE;
                }
                String plantAccount = "";
                String plantCOA = "";

                // getting the right Plant Fund Chart code & Plant Fund Account
                if (organizationPlantFundObjectSubType.contains(assetPaymentInfo.getFinancialObjectSubTypeCode())) {
                    plantAccount = assetPaymentInfo.getOrganizationPlantAccountNumber();
                    plantCOA = assetPaymentInfo.getOrganizationPlantChartCode();
                }
                else if (campusPlantFundObjectSubType.contains(assetPaymentInfo.getFinancialObjectSubTypeCode())) {
                    plantAccount = assetPaymentInfo.getCampusPlantAccountNumber();
                    plantCOA = assetPaymentInfo.getCampusPlantChartCode();
                }
                if (StringUtils.isBlank(plantCOA) || StringUtils.isBlank(plantAccount)) {
                    // skip the payment
                    LOG.error(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Plant COA is " + plantCOA + " and plant account is " + plantAccount + " for Financial Object SubType Code = " + assetPaymentInfo.getFinancialObjectSubTypeCode() + " so Asset payment is not included in depreciation " + assetPaymentInfo.getCapitalAssetNumber() + " - " + assetPaymentInfo.getPaymentSequenceNumber());
                    continue;
                }
                LOG.debug("Asset#: " + assetNumber + " - Payment sequence#:" + assetPaymentInfo.getPaymentSequenceNumber() + " - Asset Depreciation date:" + assetDepreciationDate + " - Life:" + assetLifeInMonths + " - Depreciation base amt:" + primaryDepreciationBaseAmount + " - Accumulated depreciation:" + assetPaymentInfo.getAccumulatedPrimaryDepreciationAmount() + " - Month Elapsed:" + monthsElapsed + " - Calculated accum depreciation:" + accumulatedDepreciationAmount + " - Depreciation amount:" + transactionAmount.toString() + " - Depreciation Method:" + assetPaymentInfo.getPrimaryDepreciationMethodCode());
                assetPaymentInfo.setAccumulatedPrimaryDepreciationAmount(accumulatedDepreciationAmount);
                assetPaymentInfo.setTransactionAmount(transactionAmount);
                counter++;
                saveList.add(assetPaymentInfo);
                // Saving depreciation amount in the asset payment table
                if (counter % 1000 == 0) {
                    getDepreciationBatchDao().updateAssetPayments(saveList, fiscalMonth);
                    saveList.clear();
                }
                // if the asset has a depreciation amount <> 0 then, create its debit and credit entries.
                if (transactionAmount.isNonZero()) {
                    this.populateDepreciationTransaction(assetPaymentInfo, transactionType, plantCOA, plantAccount, depreciationExpenseFinancialObject, depreciationTransactionSummary);
                    transactionType = (transactionType.equals(KFSConstants.GL_DEBIT_CODE) ? KFSConstants.GL_CREDIT_CODE : KFSConstants.GL_DEBIT_CODE);
                    this.populateDepreciationTransaction(assetPaymentInfo, transactionType, plantCOA, plantAccount, accumulatedDepreciationFinancialObject, depreciationTransactionSummary);
                }
            }
            getDepreciationBatchDao().updateAssetPayments(saveList, fiscalMonth);
            saveList.clear();
            return depreciationTransactionSummary;
        }
        catch (Exception e) {
            LOG.error("Error occurred", e);
            throw new IllegalStateException(kualiConfigurationService.getPropertyValueAsString(CamsKeyConstants.Depreciation.ERROR_WHEN_CALCULATING_DEPRECIATION) + " :" + e.getMessage());
        }
    }


    /**
     * This method stores in a collection of business objects the depreciation transaction that later on will be passed to the
     * processGeneralLedgerPendingEntry method in order to store the records in gl pending entry table
     *
     * @param assetPayment asset payment
     * @param transactionType which can be [C]redit or [D]ebit
     * @param plantCOA plant fund char of account code
     * @param plantAccount plant fund char of account code
     * @param financialObject char of account object code linked to the payment
     * @param depreciationTransactionSummary
     * @return none
     */
    protected void populateDepreciationTransaction(AssetPaymentInfo assetPayment, String transactionType, String plantCOA, String plantAccount, ObjectCode deprObjectCode, SortedMap<String, AssetDepreciationTransaction> depreciationTransactionSummary) {
        LOG.debug("populateDepreciationTransaction(AssetDepreciationTransaction depreciationTransaction, AssetPayment assetPayment, String transactionType, KualiDecimal transactionAmount, String plantCOA, String plantAccount, String accumulatedDepreciationFinancialObjectCode, String depreciationExpenseFinancialObjectCode, ObjectCode financialObject, SortedMap<String, AssetDepreciationTransaction> depreciationTransactionSummary) -  started");
        LOG.debug(CamsConstants.Depreciation.DEPRECIATION_BATCH + "populateDepreciationTransaction(): populating AssetDepreciationTransaction pojo - Asset#:" + assetPayment.getCapitalAssetNumber());
        AssetDepreciationTransaction depreciationTransaction = new AssetDepreciationTransaction();
        depreciationTransaction.setCapitalAssetNumber(assetPayment.getCapitalAssetNumber());
        depreciationTransaction.setChartOfAccountsCode(plantCOA);
        depreciationTransaction.setAccountNumber(plantAccount);
        depreciationTransaction.setSubAccountNumber(assetPayment.getSubAccountNumber());
        depreciationTransaction.setFinancialObjectCode(deprObjectCode.getFinancialObjectCode());
        depreciationTransaction.setFinancialSubObjectCode(assetPayment.getFinancialSubObjectCode());
        depreciationTransaction.setFinancialObjectTypeCode(deprObjectCode.getFinancialObjectTypeCode());
        depreciationTransaction.setTransactionType(transactionType);
        depreciationTransaction.setProjectCode(assetPayment.getProjectCode());
        depreciationTransaction.setTransactionAmount(assetPayment.getTransactionAmount());
        depreciationTransaction.setTransactionLedgerEntryDescription(CamsConstants.Depreciation.TRANSACTION_DESCRIPTION + assetPayment.getCapitalAssetNumber());

        String sKey = depreciationTransaction.getKey();

        // Grouping the asset transactions by asset#, accounts, sub account, object, transaction type (C/D), etc. in order to
        // only have one credit and one credit by group.
        if (depreciationTransactionSummary.containsKey(sKey)) {
            depreciationTransaction = depreciationTransactionSummary.get(sKey);
            depreciationTransaction.setTransactionAmount(depreciationTransaction.getTransactionAmount().add(assetPayment.getTransactionAmount()));
        }
        else {
            depreciationTransactionSummary.put(sKey, depreciationTransaction);
        }
        LOG.debug("populateDepreciationTransaction(AssetDepreciationTransaction depreciationTransaction, AssetPayment assetPayment, String transactionType, KualiDecimal transactionAmount, String plantCOA, String plantAccount, String accumulatedDepreciationFinancialObjectCode, String depreciationExpenseFinancialObjectCode, ObjectCode financialObject, SortedMap<String, AssetDepreciationTransaction> depreciationTransactionSummary) -  ended");
    }

    /**
     * This method stores the depreciation transactions in the general pending entry table and creates a new documentHeader entry.
     * <p>
     *
     * @param trans SortedMap with the transactions
     * @return none
     */
    protected void processGeneralLedgerPendingEntry(Integer fiscalYear, Integer fiscalMonth, List<String> documentNos, SortedMap<String, AssetDepreciationTransaction> trans) {
        LOG.debug("populateExplicitGeneralLedgerPendingEntry(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper, GeneralLedgerPendingEntry) - start");

        String financialSystemDocumentTypeCodeCode;
        try {

            String documentNumber = createNewDepreciationDocument(documentNos);
            financialSystemDocumentTypeCodeCode = CamsConstants.DocumentTypeName.ASSET_DEPRECIATION;
            LOG.debug(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Depreciation Document Type Code: " + financialSystemDocumentTypeCodeCode);

            Timestamp transactionTimestamp = new Timestamp(dateTimeService.getCurrentDate().getTime());

            GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper();
            List<GeneralLedgerPendingEntry> saveList = new ArrayList<GeneralLedgerPendingEntry>();
            int counter = 0;

            for (AssetDepreciationTransaction t : trans.values()) {
                if (t.getTransactionAmount().isNonZero()) {
                    counter++;
                    LOG.debug(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Creating GLPE entries for asset:" + t.getCapitalAssetNumber());
                    GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
                    explicitEntry.setFinancialSystemOriginationCode(KFSConstants.ORIGIN_CODE_KUALI);
                    explicitEntry.setDocumentNumber(documentNumber);
                    explicitEntry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));
                    sequenceHelper.increment();
                    explicitEntry.setChartOfAccountsCode(t.getChartOfAccountsCode());
                    explicitEntry.setAccountNumber(t.getAccountNumber());
                    explicitEntry.setSubAccountNumber(null);
                    explicitEntry.setFinancialObjectCode(t.getFinancialObjectCode());
                    explicitEntry.setFinancialSubObjectCode(null);
                    explicitEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_ACTUAL);
                    explicitEntry.setFinancialObjectTypeCode(t.getFinancialObjectTypeCode());
                    explicitEntry.setUniversityFiscalYear(fiscalYear);
                    explicitEntry.setUniversityFiscalPeriodCode(StringUtils.leftPad(fiscalMonth.toString().trim(), 2, "0"));
                    explicitEntry.setTransactionLedgerEntryDescription(t.getTransactionLedgerEntryDescription());
                    explicitEntry.setTransactionLedgerEntryAmount(t.getTransactionAmount().abs());
                    explicitEntry.setTransactionDebitCreditCode(t.getTransactionType());
                    explicitEntry.setTransactionDate(new java.sql.Date(transactionTimestamp.getTime()));
                    explicitEntry.setFinancialDocumentTypeCode(financialSystemDocumentTypeCodeCode);
                    explicitEntry.setFinancialDocumentApprovedCode(KFSConstants.DocumentStatusCodes.APPROVED);
                    explicitEntry.setVersionNumber(new Long(1));
                    explicitEntry.setTransactionEntryProcessedTs(new java.sql.Timestamp(transactionTimestamp.getTime()));
                    // this.generalLedgerPendingEntryService.save(explicitEntry);
                    saveList.add(explicitEntry);
                    if (counter % 1000 == 0) {
                        // save here
                        getDepreciationBatchDao().savePendingGLEntries(saveList);
                        saveList.clear();
                    }
                    if (sequenceHelper.getSequenceCounter() == 99999) {
                        // create new document and sequence is reset
                        documentNumber = createNewDepreciationDocument(documentNos);
                        sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper();
                    }
                }
            }
            // save last list
            getDepreciationBatchDao().savePendingGLEntries(saveList);
            saveList.clear();

        }
        catch (Exception e) {
            LOG.error("Error occurred", e);
            throw new IllegalStateException(kualiConfigurationService.getPropertyValueAsString(CamsKeyConstants.Depreciation.ERROR_WHEN_UPDATING_GL_PENDING_ENTRY_TABLE) + " :" + e.getMessage());
        }
        LOG.debug("populateExplicitGeneralLedgerPendingEntry(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper, GeneralLedgerPendingEntry) - end");
    }


    protected String createNewDepreciationDocument(List<String> documentNos) throws WorkflowException {
        WorkflowDocument workflowDocument = getWorkflowDocumentService().createWorkflowDocument(CamsConstants.DocumentTypeName.ASSET_DEPRECIATION, GlobalVariables.getUserSession().getPerson());
        // **************************************************************************************************
        // Create a new document header object
        // **************************************************************************************************
        LOG.debug(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Creating document header entry.");

        FinancialSystemDocumentHeader documentHeader = new FinancialSystemDocumentHeader();
        documentHeader.setWorkflowDocument(workflowDocument);
        documentHeader.setDocumentNumber(workflowDocument.getDocumentId());
        documentHeader.setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
        documentHeader.setExplanation(CamsConstants.Depreciation.DOCUMENT_DESCRIPTION);
        documentHeader.setDocumentDescription(CamsConstants.Depreciation.DOCUMENT_DESCRIPTION);
        documentHeader.setFinancialDocumentTotalAmount(KualiDecimal.ZERO);

        LOG.debug(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Saving document header entry.");
        this.businessObjectService.save(documentHeader);
        LOG.debug(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Document Header entry was saved successfully.");
        // **************************************************************************************************

        String documentNumber = documentHeader.getDocumentNumber();
        documentNos.add(documentNumber);
        LOG.debug(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Document Number Created: " + documentNumber);
        return documentNumber;
    }


    /**
     * Depreciation object code is returned from cache or from DB
     *
     * @param capitalizationObjectCodes collection cache
     * @param assetPaymentInfo
     * @param capitalizationFinancialObjectCode
     * @return
     */
    protected ObjectCode getDepreciationObjectCode(Integer fiscalYear, Map<String, ObjectCode> capObjectCodesCache, AssetPaymentInfo assetPaymentInfo, String capitalizationFinancialObjectCode) {
        ObjectCode deprObjCode = null;
        String key = assetPaymentInfo.getChartOfAccountsCode() + "-" + capitalizationFinancialObjectCode;
        if ((deprObjCode = capObjectCodesCache.get(key)) == null) {
            deprObjCode = SpringContext.getBean(ObjectCodeService.class).getByPrimaryId(fiscalYear, assetPaymentInfo.getChartOfAccountsCode(), capitalizationFinancialObjectCode);
            if (ObjectUtils.isNotNull(deprObjCode)) {
                capObjectCodesCache.put(key, deprObjCode);
            }
        }
        return deprObjCode;
    }

    /**
     * Builds map between object code to corresponding asset object code
     *
     * @return Map
     */
    protected Map<String, AssetObjectCode> buildChartObjectToCapitalizationObjectMap(Collection<AssetObjectCode> assetObjectCodes) {
        Map<String, AssetObjectCode> assetObjectCodeMap = new HashMap<String, AssetObjectCode>();

        for (AssetObjectCode assetObjectCode : assetObjectCodes) {
            List<ObjectCode> objectCodes = assetObjectCode.getObjectCode();
            for (ObjectCode objectCode : objectCodes) {
                String key = objectCode.getChartOfAccountsCode() + "-" + objectCode.getFinancialObjectCode();
                if (!assetObjectCodeMap.containsKey(key)) {
                    assetObjectCodeMap.put(key, assetObjectCode);
                }
            }
        }
        return assetObjectCodeMap;
    }

    private void sendWarningMail(List<String> errorMessages) {

        LOG.debug("sendEmail() starting");
        MailMessage message = new MailMessage();

        message.setFromAddress(mailService.getBatchMailingList());
        String subject = "Asset Depreciation Job status";
        message.setSubject(subject);
        Collection<String> toAddresses =  parameterService.getParameterValuesAsString(AssetDepreciationStep.class, CamsConstants.Parameters.RUN_DATE_NOTIFICATION_EMAIL_ADDRESSES);
        message.getToAddresses().add(toAddresses);


        StringBuffer sb = new StringBuffer();
        sb.append("Unable to run Depreciation process.Reason:\n");
        for (String msg : errorMessages) {
            sb.append(msg + "\n");
        }

        sb.append("Please set the dates correctly to run the job.");

        message.setMessage(sb.toString());

        try {
            mailService.sendMessage(message);
        }
        catch (MessagingException e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
        }
    }
    
    /**
     * Get the last month and day of the fiscal year.  Returned in the format '-mm-dd'
     * @return
     */
    protected String getLastDayOfFiscalyear() {
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        String date = parameterService.getParameterValueAsString(KfsParameterConstants.CAPITAL_ASSETS_ALL.class, CamsConstants.Parameters.FISCAL_YEAR_END_MONTH_AND_DAY);
        return "-" + date.substring(0,2) + "-" + date.substring(2);
    }

    protected void populateYearEndDepreciationTransaction(AssetPaymentInfo assetPayment, String transactionType, String plantCOA, String plantAccount, ObjectCode deprObjectCode, SortedMap<String, AssetDepreciationTransaction> depreciationTransactionSummary) {
        LOG.info("\npopulateYearEndDepreciationTransaction - Asset#:" + assetPayment.getCapitalAssetNumber() + " amount:"+ assetPayment.getTransactionAmount()+" type:"+ transactionType);
        LOG.info("deprObjectCode.getFinancialObjectCode():" + deprObjectCode.getFinancialObjectCode() + " deprObjectCode.getFinancialObjectTypeCode():"+ deprObjectCode.getFinancialObjectTypeCode());
        AssetDepreciationTransaction depreciationTransaction = new AssetDepreciationTransaction();
        depreciationTransaction.setCapitalAssetNumber(assetPayment.getCapitalAssetNumber());
        depreciationTransaction.setChartOfAccountsCode(plantCOA);
        depreciationTransaction.setAccountNumber(plantAccount);
        depreciationTransaction.setSubAccountNumber(assetPayment.getSubAccountNumber());
        depreciationTransaction.setFinancialObjectCode(deprObjectCode.getFinancialObjectCode());
        depreciationTransaction.setFinancialSubObjectCode(assetPayment.getFinancialSubObjectCode());
        depreciationTransaction.setFinancialObjectTypeCode(deprObjectCode.getFinancialObjectTypeCode());
        depreciationTransaction.setTransactionType(transactionType);
        depreciationTransaction.setProjectCode(assetPayment.getProjectCode());
        depreciationTransaction.setTransactionAmount(assetPayment.getTransactionAmount());
        depreciationTransaction.setTransactionLedgerEntryDescription("Year End Depreciation Asset " + assetPayment.getCapitalAssetNumber());

        String sKey = depreciationTransaction.getKey();

        // Grouping the asset transactions by asset#, accounts, sub account, object, transaction type (C/D), etc. in order to
        // only have one credit and one credit by group.
        if (depreciationTransactionSummary.containsKey(sKey)) {
            LOG.info("depreciationTransactionSummary.containsKey(sKey) where sKey=" + sKey);
            depreciationTransaction = depreciationTransactionSummary.get(sKey);
            depreciationTransaction.setTransactionAmount(depreciationTransaction.getTransactionAmount().add(assetPayment.getTransactionAmount()));
        } else {
            LOG.info("depreciationTransactionSummary DOESNT containsKey(sKey) where sKey=" + sKey);
            depreciationTransactionSummary.put(sKey, depreciationTransaction);
        }
        LOG.info("\n\n");
        //  LOG.info("populateYearEndDepreciationTransaction(AssetDepreciationTransaction depreciationTransaction, AssetPayment assetPayment, String transactionType, KualiDecimal transactionAmount, String plantCOA, String plantAccount, String accumulatedDepreciationFinancialObjectCode, String depreciationExpenseFinancialObjectCode, ObjectCode financialObject, SortedMap<String, AssetDepreciationTransaction> depreciationTransactionSummary) -  ended");
    }
    
    
    protected SortedMap<String, AssetDepreciationTransaction> calculateYearEndDepreciation(Collection<AssetPaymentInfo> depreciableAssetsCollection, Calendar depreciationDate, Integer fiscalYearToDepreciate, Integer fiscalYear, Integer fiscalMonth, Collection<AssetObjectCode> assetObjectCodes) {
        LOG.info("calculateDepreciation() - start");

        SortedMap<String, AssetDepreciationTransaction> depreciationTransactionSummary = new TreeMap<String, AssetDepreciationTransaction>();
        double monthsElapsed = 0d;
        double assetLifeInMonths = 0d;
        KualiDecimal accumulatedDepreciationAmount = KualiDecimal.ZERO;
        Calendar assetDepreciationDate = Calendar.getInstance();

        try {
            LOG.info("YEAR END DEPRECIATION - Getting the parameters for the plant fund object sub types.");
            // Getting system parameters needed.
            Collection<String> organizationPlantFundObjectSubType = parameterService.getParameterValuesAsString(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_ORGANIZATON_PLANT_FUND_SUB_OBJECT_TYPES);
            Collection<String> campusPlantFundObjectSubType = parameterService.getParameterValuesAsString(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPES);
            // Initializing the asset payment table.
            depreciationBatchDao.resetPeriodValuesWhenFirstFiscalPeriod(fiscalMonth);
            LOG.info("getBaseAmountOfAssets(Collection<AssetPayment> depreciableAssetsCollection) - Started.");
            // Invoking method that will calculate the base amount for each asset payment transactions, which could be more than 1
            // per asset.
            LOG.info("YEAR END DEPRECIATION - Calculating the base amount for each asset.");
            Map<Long, KualiDecimal> salvageValueAssetDeprAmounts = depreciationBatchDao.getPrimaryDepreciationBaseAmountForSV();
            // Retrieving the object asset codes.
            Map<String, AssetObjectCode> assetObjectCodeMap = buildChartObjectToCapitalizationObjectMap(assetObjectCodes);
            Map<String, ObjectCode> capitalizationObjectCodes = new HashMap<String, ObjectCode>();

            // Reading asset payments
            LOG.info("YEAR END DEPRECIATION - Reading collection with eligible asset payment details.");
            int counter = 0;
            List<AssetPaymentInfo> saveList = new ArrayList<AssetPaymentInfo>();
            for (AssetPaymentInfo assetPaymentInfo : depreciableAssetsCollection) {

                boolean asset_is_retired = false;
                boolean asset_is_not_in_last_year_of_life = false;
                HashMap<String, Object> pKeys = new HashMap<String, Object>();
                // Asset must be valid and capital active 'A','C','S','U'
                Long assetNumber = assetPaymentInfo.getCapitalAssetNumber();
                pKeys.put(CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER, assetNumber);

                Asset asset = (Asset) businessObjectService.findByPrimaryKey(Asset.class, pKeys);
                if (asset != null) {
                    asset_is_retired = assetService.isAssetRetired(asset);
                    if ( LOG.isInfoEnabled() ) {
                        LOG.info("asset#" + assetNumber + "   asset_is_retired = " + asset_is_retired);
                    }
                }


                AssetObjectCode assetObjectCode = assetObjectCodeMap.get(assetPaymentInfo.getChartOfAccountsCode() + "-" + assetPaymentInfo.getFinancialObjectCode());
                if (assetObjectCode == null) {
                    LOG.error("YEAR END DEPRECIATION - " + "Asset object code not found for " + fiscalYear + "-" + assetPaymentInfo.getChartOfAccountsCode() + "-" + assetPaymentInfo.getFinancialObjectCode());
                    LOG.error("YEAR END DEPRECIATION - " + "Asset payment is not included in depreciation " + assetPaymentInfo.getCapitalAssetNumber() + " - " + assetPaymentInfo.getPaymentSequenceNumber());
                    continue;
                }
                else {
                    LOG.info("YEAR END DEPRECIATION - " + "fiscal year " + fiscalYear + " chartOfAccountsCode:" + assetPaymentInfo.getChartOfAccountsCode() + " FinancialObjectCode:" + assetPaymentInfo.getFinancialObjectCode());
    //                LOG.info("YEAR END DEPRECIATION - " + "CapitalAssetNumber:" + assetPaymentInfo.getCapitalAssetNumber() + " PaymentSequenceNumber:" + assetPaymentInfo.getPaymentSequenceNumber());
                }
                ObjectCode accumulatedDepreciationFinancialObject = getDepreciationObjectCode(fiscalYear, capitalizationObjectCodes, assetPaymentInfo, assetObjectCode.getAccumulatedDepreciationFinancialObjectCode());
                ObjectCode depreciationExpenseFinancialObject = getDepreciationObjectCode(fiscalYear, capitalizationObjectCodes, assetPaymentInfo, assetObjectCode.getDepreciationExpenseFinancialObjectCode());
                String retire_code = parameterService.getParameterValueAsString(org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal.class, CamsConstants.Parameters.DEFAULT_GAIN_LOSS_DISPOSITION_OBJECT_CODE);
                if ( LOG.isInfoEnabled() ) {
                    LOG.info("retire_code from system parameter "+ CamsConstants.Parameters.DEFAULT_GAIN_LOSS_DISPOSITION_OBJECT_CODE+" = " + retire_code);
                }
                ObjectCode depreciationYearEndExpenseFinancialObject = getDepreciationObjectCode(fiscalYear, capitalizationObjectCodes, assetPaymentInfo, retire_code);

                if (ObjectUtils.isNull(accumulatedDepreciationFinancialObject)) {
                    LOG.error("YEAR END DEPRECIATION - " + "Accumulated Depreciation Financial Object Code not found for " + fiscalYear + "-" + assetPaymentInfo.getChartOfAccountsCode() + "-" + assetObjectCode.getAccumulatedDepreciationFinancialObjectCode());
                    LOG.error("YEAR END DEPRECIATION - " + "Asset payment is not included in depreciation " + assetPaymentInfo.getCapitalAssetNumber() + " - " + assetPaymentInfo.getPaymentSequenceNumber());
                    continue;
                } else {
    //                LOG.info("YEAR END DEPRECIATION - " + " AccumulatedDepreciationFinancialObjectCode:" + assetObjectCode.getAccumulatedDepreciationFinancialObjectCode());
    //                LOG.info("YEAR END DEPRECIATION - " + "CapitalAssetNumber:" + assetPaymentInfo.getCapitalAssetNumber() + " PaymentSequenceNumber:" + assetPaymentInfo.getPaymentSequenceNumber());
                    if ( LOG.isInfoEnabled() ) {
                        LOG.info("YEAR END DEPRECIATION - " + "accumulatedDepreciationFinancialObject:" + accumulatedDepreciationFinancialObject.getFinancialObjectCode());
                    }
                }

                if (ObjectUtils.isNull(depreciationExpenseFinancialObject)) {
                    LOG.error("YEAR END DEPRECIATION - " + "Depreciation Expense Financial Object Code not found for " + fiscalYear + "-" + assetPaymentInfo.getChartOfAccountsCode() + "-" + assetObjectCode.getDepreciationExpenseFinancialObjectCode());
                    LOG.error("YEAR END DEPRECIATION - " + "Asset payment is not included in depreciation " + assetPaymentInfo.getCapitalAssetNumber() + " - " + assetPaymentInfo.getPaymentSequenceNumber());
                    continue;
                } else {
                    if ( LOG.isInfoEnabled() ) {
                        LOG.info("YEAR END DEPRECIATION - " + "depreciationExpenseFinancialObject:" + depreciationExpenseFinancialObject.getFinancialObjectCode());
                    }
                }
                assetDepreciationDate.setTime(assetPaymentInfo.getDepreciationDate());
                accumulatedDepreciationAmount = KualiDecimal.ZERO;
                KualiDecimal deprAmountSum = salvageValueAssetDeprAmounts.get(assetNumber);
                // Calculating the life of the asset in months.
                assetLifeInMonths = assetPaymentInfo.getDepreciableLifeLimit() * 12;
                // Calculating the months elapsed for the asset using the depreciation date and the asset service date.
                monthsElapsed = (depreciationDate.get(Calendar.MONTH) - assetDepreciationDate.get(Calendar.MONTH) + (depreciationDate.get(Calendar.YEAR) - assetDepreciationDate.get(Calendar.YEAR)) * 12) + 1;

                if ((assetLifeInMonths - monthsElapsed)>12){
                    asset_is_not_in_last_year_of_life = true;
                }

                // **************************************************************************************************************
                // CALCULATING ACCUMULATED DEPRECIATION BASED ON FORMULA FOR SINGLE LINE AND SALVAGE VALUE DEPRECIATION METHODS.
                // **************************************************************************************************************
                KualiDecimal primaryDepreciationBaseAmount = assetPaymentInfo.getPrimaryDepreciationBaseAmount();
                if (primaryDepreciationBaseAmount == null) {
                    primaryDepreciationBaseAmount = KualiDecimal.ZERO;
                    assetPaymentInfo.setPrimaryDepreciationBaseAmount(KualiDecimal.ZERO);
                }

                if (assetPaymentInfo.getAccumulatedPrimaryDepreciationAmount() == null) {
                    assetPaymentInfo.setAccumulatedPrimaryDepreciationAmount(KualiDecimal.ZERO);
                }

                // If the months elapsed >= to the life of the asset (in months) then, the accumulated depreciation should be:
                if (monthsElapsed >= assetLifeInMonths) {
                    if (CamsConstants.Asset.DEPRECIATION_METHOD_STRAIGHT_LINE_CODE.equals(assetPaymentInfo.getPrimaryDepreciationMethodCode())) {
                        accumulatedDepreciationAmount = primaryDepreciationBaseAmount;
                    } else if (CamsConstants.Asset.DEPRECIATION_METHOD_SALVAGE_VALUE_CODE.equals(assetPaymentInfo.getPrimaryDepreciationMethodCode()) && deprAmountSum != null && deprAmountSum.isNonZero()) {
                        accumulatedDepreciationAmount = primaryDepreciationBaseAmount.subtract((primaryDepreciationBaseAmount.divide(deprAmountSum)).multiply(assetPaymentInfo.getSalvageAmount()));
                    }
                } // If the month elapse < to the life of the asset (in months) then....
                else {
                    if (CamsConstants.Asset.DEPRECIATION_METHOD_STRAIGHT_LINE_CODE.equals(assetPaymentInfo.getPrimaryDepreciationMethodCode())) {
                        accumulatedDepreciationAmount = new KualiDecimal((monthsElapsed / assetLifeInMonths) * primaryDepreciationBaseAmount.doubleValue());
                    } else if (CamsConstants.Asset.DEPRECIATION_METHOD_SALVAGE_VALUE_CODE.equals(assetPaymentInfo.getPrimaryDepreciationMethodCode()) && deprAmountSum != null && deprAmountSum.isNonZero()) {
                        accumulatedDepreciationAmount = new KualiDecimal((monthsElapsed / assetLifeInMonths) * (primaryDepreciationBaseAmount.subtract((primaryDepreciationBaseAmount.divide(deprAmountSum)).multiply(assetPaymentInfo.getSalvageAmount()))).doubleValue());
                    }
                }
                // Calculating in process fiscal month depreciation amount
                KualiDecimal transactionAmount = accumulatedDepreciationAmount.subtract(assetPaymentInfo.getAccumulatedPrimaryDepreciationAmount());

                Map<String, String> primaryKeys = new HashMap<String, String>();
                primaryKeys.put(CamsPropertyConstants.AssetDepreciationConvention.FINANCIAL_OBJECT_SUB_TYPE_CODE, asset.getFinancialObjectSubTypeCode());
                AssetDepreciationConvention depreciationConvention = (AssetDepreciationConvention) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(AssetDepreciationConvention.class, primaryKeys);
                String conventionCode = depreciationConvention.getDepreciationConventionCode();
                if (CamsConstants.DepreciationConvention.HALF_YEAR.equalsIgnoreCase(conventionCode)) {
                     if (asset_is_retired && asset_is_not_in_last_year_of_life) { // and not in last year of life mjmc
                        transactionAmount = transactionAmount.divide(new KualiDecimal(2));
                        if ( LOG.isInfoEnabled() ) {
                            LOG.info("transactionAmount after being halved = " + transactionAmount);
                        }
                    }
                }


                String transactionType = KFSConstants.GL_DEBIT_CODE;
                if (transactionAmount.isNegative()) {
                    transactionType = KFSConstants.GL_CREDIT_CODE;
                }
                String plantAccount = "";
                String plantCOA = "";

                // getting the right Plant Fund Chart code & Plant Fund Account
                if (organizationPlantFundObjectSubType.contains(assetPaymentInfo.getFinancialObjectSubTypeCode())) {
                    plantAccount = assetPaymentInfo.getOrganizationPlantAccountNumber();
                    plantCOA = assetPaymentInfo.getOrganizationPlantChartCode();
                } else if (campusPlantFundObjectSubType.contains(assetPaymentInfo.getFinancialObjectSubTypeCode())) {
                    plantAccount = assetPaymentInfo.getCampusPlantAccountNumber();
                    plantCOA = assetPaymentInfo.getCampusPlantChartCode();
                }
                if (StringUtils.isBlank(plantCOA) || StringUtils.isBlank(plantAccount)) {
                    // skip the payment
                    LOG.error("YEAR END DEPRECIATION - " + "Plant COA is " + plantCOA + " and plant account is " + plantAccount + " for Financial Object SubType Code = " + assetPaymentInfo.getFinancialObjectSubTypeCode() + " so Asset payment is not included in depreciation " + assetPaymentInfo.getCapitalAssetNumber() + " - " + assetPaymentInfo.getPaymentSequenceNumber());
                    continue;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                if ( LOG.isInfoEnabled() ) {
                    LOG.info("Asset#: " + assetNumber + " - Payment sequence#:" + assetPaymentInfo.getPaymentSequenceNumber() + " - Asset Depreciation date:" + sdf.format(assetDepreciationDate.getTime()) + " - Life:" + assetLifeInMonths + " - Depreciation base amt:" + primaryDepreciationBaseAmount);
                    LOG.info("Accumulated depreciation:" + assetPaymentInfo.getAccumulatedPrimaryDepreciationAmount() + " - Month Elapsed:" + monthsElapsed + " - Calculated accum depreciation:" + accumulatedDepreciationAmount + " - Depreciation amount:" + transactionAmount.toString() + " - Depreciation Method:" + assetPaymentInfo.getPrimaryDepreciationMethodCode());
                }
                if (asset_is_retired && asset_is_not_in_last_year_of_life) {
                    assetPaymentInfo.setAccumulatedPrimaryDepreciationAmount(accumulatedDepreciationAmount.subtract(transactionAmount));
                } else {
                    assetPaymentInfo.setAccumulatedPrimaryDepreciationAmount(accumulatedDepreciationAmount);
                }
                assetPaymentInfo.setTransactionAmount(transactionAmount);
                counter++;
                saveList.add(assetPaymentInfo);
                // Saving depreciation amount in the asset payment table
                if (counter % 1000 == 0) {
                    getDepreciationBatchDao().updateAssetPayments(saveList, fiscalMonth);
                    saveList.clear();
                }
                // if the asset has a depreciation amount <> 0 then, create its debit and credit entries.
                if (transactionAmount.isNonZero()) {
                    this.populateYearEndDepreciationTransaction(assetPaymentInfo, transactionType, plantCOA, plantAccount, depreciationExpenseFinancialObject, depreciationTransactionSummary);
                    transactionType = (transactionType.equals(KFSConstants.GL_DEBIT_CODE) ? KFSConstants.GL_CREDIT_CODE : KFSConstants.GL_DEBIT_CODE);
                    this.populateYearEndDepreciationTransaction(assetPaymentInfo, transactionType, plantCOA, plantAccount, accumulatedDepreciationFinancialObject, depreciationTransactionSummary);





                    if (asset_is_retired) {
                        this.populateYearEndDepreciationTransaction(assetPaymentInfo, transactionType, plantCOA, plantAccount, depreciationYearEndExpenseFinancialObject, depreciationTransactionSummary);
                        transactionType = (transactionType.equals(KFSConstants.GL_DEBIT_CODE) ? KFSConstants.GL_CREDIT_CODE : KFSConstants.GL_DEBIT_CODE);
                        this.populateYearEndDepreciationTransaction(assetPaymentInfo, transactionType, plantCOA, plantAccount, accumulatedDepreciationFinancialObject, depreciationTransactionSummary);
                    }
                }
            }
            getDepreciationBatchDao().updateAssetPayments(saveList, fiscalMonth);
            saveList.clear();
            return depreciationTransactionSummary;
        }
        catch (Exception e) {
            LOG.error("Error occurred", e);
            throw new IllegalStateException(kualiConfigurationService.getPropertyValueAsString(CamsKeyConstants.Depreciation.ERROR_WHEN_CALCULATING_DEPRECIATION) + " :" + e.getMessage(), e);
        }
    }

    protected void processYearEndGeneralLedgerPendingEntry(Integer fiscalYear, List<String> documentNos, SortedMap<String, AssetDepreciationTransaction> trans) {
        Integer fiscalMonth = new Integer(13);
        processGeneralLedgerPendingEntry(fiscalYear, fiscalMonth, documentNos, trans);
    }
    
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setDepreciableAssetsDao(DepreciableAssetsDao depreciableAssetsDao) {
        this.depreciableAssetsDao = depreciableAssetsDao;
    }

    public void setCamsReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    public void setConfigurationService(ConfigurationService kcs) {
        kualiConfigurationService = kcs;
    }

    public void setGeneralLedgerPendingEntryService(GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Gets the depreciationBatchDao attribute.
     *
     * @return Returns the depreciationBatchDao.
     */
    public DepreciationBatchDao getDepreciationBatchDao() {
        return depreciationBatchDao;
    }

    /**
     * Sets the depreciationBatchDao attribute value.
     *
     * @param depreciationBatchDao The depreciationBatchDao to set.
     */
    @Override
    public void setDepreciationBatchDao(DepreciationBatchDao depreciationBatchDao) {
        this.depreciationBatchDao = depreciationBatchDao;
    }


    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }


    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public WorkflowDocumentService getWorkflowDocumentService() {
        if (workflowDocumentService == null) {
            workflowDocumentService = KRADServiceLocatorWeb.getWorkflowDocumentService();
        }
        return workflowDocumentService;
    }

    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }
}
