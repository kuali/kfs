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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
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
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.UniversityDateDao;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.spring.CacheNoCopy;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;
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
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetDepreciationServiceImpl.class);
    private ParameterService parameterService;
    private AssetService assetService;
    private ReportService reportService;
    private DateTimeService dateTimeService;
    private DepreciableAssetsDao depreciableAssetsDao;
    private KualiConfigurationService kualiConfigurationService;
    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    private BusinessObjectService businessObjectService;
    private OptionsService optionsService;
    private UniversityDateDao universityDateDao;
    private WorkflowDocumentService workflowDocumentService;
    private DataDictionaryService dataDictionaryService;
    private Integer fiscalYear;
    private Integer fiscalMonth;
    private String errorMsg = "";
    private List<String> documentNos = new ArrayList<String>();
    private DepreciationBatchDao depreciationBatchDao;

    /**
     * @see org.kuali.kfs.module.cam.batch.service.AssetDepreciationService#runDepreciation()
     */
    public void runDepreciation() {
        List<String[]> reportLog = new ArrayList<String[]>();
        boolean hasErrors = false;
        Calendar depreciationDate = Calendar.getInstance();
        java.sql.Date depDate = null;
        Calendar currentDate = Calendar.getInstance();
        String depreciationDateParameter = null;
        DateFormat dateFormat = new SimpleDateFormat(CamsConstants.DateFormats.YEAR_MONTH_DAY);

        try {
            LOG.info("*******" + CamsConstants.Depreciation.DEPRECIATION_BATCH + " HAS BEGUN *******");

            /*
             * Getting the system parameter "DEPRECIATION_DATE" When this parameter is used to determine which fiscal month and year
             * is going to be depreciated If blank then the system will take the system date to determine the fiscal period
             */
            if (parameterService.parameterExists(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_RUN_DATE_PARAMETER)) {
                depreciationDateParameter = ((List<String>) parameterService.getParameterValues(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_RUN_DATE_PARAMETER)).get(0).trim();
            }
            else {
                throw new IllegalStateException(kualiConfigurationService.getPropertyString(CamsKeyConstants.Depreciation.DEPRECIATION_DATE_PARAMETER_NOT_FOUND));
            }
            // This validates the system parameter depreciation_date has a valid format of YYYY-MM-DD.
            if (depreciationDateParameter != null && !depreciationDateParameter.trim().equals("")) {
                try {
                    depreciationDate.setTime(dateFormat.parse(depreciationDateParameter));
                }
                catch (ParseException e) {
                    throw new IllegalArgumentException(kualiConfigurationService.getPropertyString(CamsKeyConstants.Depreciation.INVALID_DEPRECIATION_DATE_FORMAT));
                }
            }
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Depreciation run date: " + depreciationDateParameter);

            UniversityDate universityDate = (UniversityDate) businessObjectService.findBySinglePrimaryKey(UniversityDate.class, new java.sql.Date(depreciationDate.getTimeInMillis()));
            if (universityDate == null) {
                throw new IllegalStateException(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_UNIV_DATE_NOT_FOUND));
            }

            this.fiscalYear = universityDate.getUniversityFiscalYear();
            this.fiscalMonth = new Integer(universityDate.getUniversityFiscalAccountingPeriod());
            // If the depreciation date is not = to the system date then, the depreciation process cannot run.
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Fiscal Year = " + this.fiscalYear + " & Fiscal Period=" + this.fiscalMonth);
            String depreciationDateAsString = dateTimeService.toDateString(depreciationDate.getTime());
            int fiscalStartMonth = Integer.parseInt(optionsService.getCurrentYearOptions().getUniversityFiscalYearStartMo());
            // Generating a list of depreciation expense object codes.
            List<String> depreExpObjCodes = this.getExpenseObjectCodes(fiscalYear);

            // Generating a list of accumulated depreciation object codes.
            List<String> accumulatedDepreciationObjCodes = this.getAccumulatedDepreciationObjectCodes(fiscalYear);
            List<String> notAcceptedAssetStatus = new ArrayList<String>();
            if (parameterService.parameterExists(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_NON_CAPITAL_ASSETS_STATUS_CODES)) {
                notAcceptedAssetStatus = parameterService.getParameterValues(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_NON_CAPITAL_ASSETS_STATUS_CODES);
            }
            List<String> federallyOwnedObjectSubTypes = getFederallyOwnedObjectSubTypes();
            reportLog.addAll(depreciableAssetsDao.generateStatistics(true, null, fiscalYear, fiscalMonth, depreciationDate, depreciationDateAsString, fiscalStartMonth, depreExpObjCodes, accumulatedDepreciationObjCodes, notAcceptedAssetStatus, federallyOwnedObjectSubTypes));
            // update if fiscal period is 12
            // Getting last date of fiscal year
            final UniversityDate lastFiscalYearDate = universityDateDao.getLastFiscalYearDate(fiscalYear);
            if (lastFiscalYearDate == null) {
                throw new IllegalStateException(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_UNIV_DATE_NOT_FOUND));
            }
            List<String> movableEquipmentObjectSubTypes = new ArrayList<String>();
            if (parameterService.parameterExists(Asset.class, CamsConstants.Parameters.MOVABLE_EQUIPMENT_OBJECT_SUB_TYPES)) {
                movableEquipmentObjectSubTypes = parameterService.getParameterValues(Asset.class, CamsConstants.Parameters.MOVABLE_EQUIPMENT_OBJECT_SUB_TYPES);
            }
            depreciationBatchDao.updateAssetsCreatedInLastFiscalPeriod(fiscalMonth, fiscalYear, lastFiscalYearDate, movableEquipmentObjectSubTypes);
            // Retrieving eligible asset payment details
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting list of asset payments eligible for depreciation.");
            Collection<AssetPaymentInfo> depreciableAssetsCollection = depreciationBatchDao.getListOfDepreciableAssetPaymentInfo(this.fiscalYear, this.fiscalMonth, depreciationDate, notAcceptedAssetStatus, federallyOwnedObjectSubTypes);
            // if we have assets eligible for depreciation then, calculate depreciation and create glpe's transactions
            if (depreciableAssetsCollection != null && !depreciableAssetsCollection.isEmpty()) {
                SortedMap<String, AssetDepreciationTransaction> depreciationTransactions = this.calculateDepreciation(depreciableAssetsCollection, depreciationDate);
                processGeneralLedgerPendingEntry(depreciationTransactions);
            }
            else {
                throw new IllegalStateException(kualiConfigurationService.getPropertyString(CamsKeyConstants.Depreciation.NO_ELIGIBLE_FOR_DEPRECIATION_ASSETS_FOUND));
            }
        }
        catch (Exception e) {
            LOG.info("Error occurred");
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "**************************************************************************");
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "AN ERROR HAS OCCURRED! - ERROR: " + e.getMessage());
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "**************************************************************************");
            hasErrors = true;
            this.errorMsg = "Depreciation process ran unsucessfuly.\nReason:" + e.getMessage();
        }
        finally {
            if (!hasErrors) {
                String depreciationDateAsString = dateTimeService.toDateString(depreciationDate.getTime());
                int fiscalStartMonth = Integer.parseInt(optionsService.getCurrentYearOptions().getUniversityFiscalYearStartMo());
                // Generating a list of depreciation expense object codes.
                List<String> depreExpObjCodes = this.getExpenseObjectCodes(fiscalYear);

                // Generating a list of accumulated depreciation object codes.
                List<String> accumulatedDepreciationObjCodes = this.getAccumulatedDepreciationObjectCodes(fiscalYear);
                List<String> notAcceptedAssetStatus = new ArrayList<String>();
                if (parameterService.parameterExists(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_NON_CAPITAL_ASSETS_STATUS_CODES)) {
                    notAcceptedAssetStatus = parameterService.getParameterValues(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_NON_CAPITAL_ASSETS_STATUS_CODES);
                }
                List<String> federallyOwnedObjectSubTypes = getFederallyOwnedObjectSubTypes();
                reportLog.addAll(depreciableAssetsDao.generateStatistics(false, this.documentNos, fiscalYear, fiscalMonth, depreciationDate, depreciationDateAsString, fiscalStartMonth, depreExpObjCodes, accumulatedDepreciationObjCodes, notAcceptedAssetStatus, federallyOwnedObjectSubTypes));
            }
            // the report will be generated only when there is an error or when the log has something.
            if (!reportLog.isEmpty() || !errorMsg.trim().equals(""))
                reportService.generateDepreciationReport(reportLog, errorMsg, depreciationDateParameter);

            LOG.info("*******" + CamsConstants.Depreciation.DEPRECIATION_BATCH + " HAS ENDED *******");
        }
    }

    // CSU 6702 BEGIN
    /**
     * @see org.kuali.kfs.module.cam.batch.service.AssetDepreciationService#runYearEndDepreciation(java.lang.Integer)
     */
    public void runYearEndDepreciation(Integer fiscalYearToDepreciate){
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
        notAcceptedAssetStatus.addAll(parameterService.getParameterValues(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_NON_CAPITAL_ASSETS_STATUS_CODES));
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
            parameterService.setParameterForTesting(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_NON_CAPITAL_ASSETS_STATUS_CODES, notAcceptedAssetStatusString);
        }

        try{
            LOG.info("*******" + "YEAR END DEPRECIATION - " + " HAS BEGUN *******");

            //
            // Getting the system parameter "YEARENDDEPR_INCLUDE_RETIRED" When this parameter is used to determine whether
            // to include retired assets in the depreciation
            //
            LOG.info("parameterService.getParameterValue(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, PurapConstants.ParameterConstants.YEARENDDEPR_INCLUDE_RETIRED) = " + parameterService.getParameterValue(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, PurapConstants.ParameterConstants.YEARENDDEPR_INCLUDE_RETIRED));
            if (parameterService.getParameterValue(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, PurapConstants.ParameterConstants.YEARENDDEPR_INCLUDE_RETIRED).equalsIgnoreCase("Y")) {
                includeRetired=true;
            }
            depreciationDate.setTime(java.sql.Date.valueOf(fiscalYearToDepreciate.toString()+getLastDayOfFiscalyear()));
            UniversityDate universityDate = universityDateDao.getByPrimaryKey(depreciationDate.getTime());
            if (universityDate == null) {
                throw new IllegalStateException(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_UNIV_DATE_NOT_FOUND));
            }
            this.fiscalYear = fiscalYearToDepreciate;
            this.fiscalMonth = 12;

            // If the depreciation date is not = to the system date then, the depreciation process cannot run.
            LOG.info("YEAR END DEPRECIATION - " + "Fiscal Year = " + this.fiscalYear + " & Fiscal Period=" + this.fiscalMonth);

            // mjmc this caused arrayIndexOutOfBounds: 16
// TODO            reportLog.addAll(depreciableAssetsDao.generateStatistics(true, null, fiscalYear, fiscalMonth, depreciationDate, true));

            // update if fiscal period is 12
            depreciationBatchDao.updateAssetsCreatedInLastFiscalPeriod(fiscalMonth, fiscalYear, universityDate, new ArrayList<String>());
            // Retrieving eligible asset payment details
            LOG.info("YEAR END DEPRECIATION - Getting list of YEAR END DEPRECIATION asset payments eligible for depreciation.");            
            Collection<AssetPaymentInfo> depreciableAssetsCollection = depreciationBatchDao.getListOfDepreciableAssetPaymentInfoYearEnd(fiscalYear, fiscalMonth, depreciationDate, includeRetired);
            // if we have assets eligible for depreciation then, calculate depreciation and create glpe's transactions
            if (depreciableAssetsCollection != null && !depreciableAssetsCollection.isEmpty()) {
                SortedMap<String, AssetDepreciationTransaction> depreciationTransactions = this.calculateYearEndDepreciation(depreciableAssetsCollection, depreciationDate, fiscalYearToDepreciate);
                processYearEndGeneralLedgerPendingEntry(depreciationTransactions);
            } else {
                throw new IllegalStateException(kualiConfigurationService.getPropertyString(CamsKeyConstants.Depreciation.NO_ELIGIBLE_FOR_DEPRECIATION_ASSETS_FOUND));
            }
        } catch(Exception e) {
            LOG.error("Error occurred", e);
            LOG.info("YEAR END DEPRECIATION - **************************************************************************");
            LOG.info("YEAR END DEPRECIATION - AN ERROR HAS OCCURRED! - ERROR: " + e.getMessage());
            LOG.info("YEAR END DEPRECIATION - **************************************************************************");
            hasErrors = true;
            this.errorMsg = "YEAR END DEPRECIATION -  process ran unsucessfuly.\nReason:" + e.getMessage();
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

            LOG.info("******* YEAR END DEPRECIATION -  HAS ENDED *******");
        }

        // reset param so that retired assets are not depreciated during the rest of the year
        if (statusContainsR){
            notAcceptedAssetStatusString = notAcceptedAssetStatusString+";R";
            LOG.info("notAcceptedAssetStatusString after reset= " + notAcceptedAssetStatusString);
            parameterService.setParameterForTesting(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_NON_CAPITAL_ASSETS_STATUS_CODES, notAcceptedAssetStatusString);
            LOG.info(CamsConstants.Parameters.NON_DEPRECIABLE_NON_CAPITAL_ASSETS_STATUS_CODES+" now = "+ parameterService.getParameterValues(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_NON_CAPITAL_ASSETS_STATUS_CODES));
        }
    }

    /**
     * Get the last month and day of the fiscal year.  Returned in the format '-mm-dd'
     * @return
     */
    private String getLastDayOfFiscalyear() {
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        String date = parameterService.getParameterValue(KfsParameterConstants.CAPITAL_ASSETS_ALL.class, CamsConstants.Parameters.FISCAL_YEAR_END_DAY_AND_MONTH);
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

    protected void processYearEndGeneralLedgerPendingEntry(SortedMap<String, AssetDepreciationTransaction> trans) {
        fiscalMonth = 13;
        processGeneralLedgerPendingEntry(trans);
    }

    // CSU 6702 END
    
    /**
     * This method calculates the depreciation of each asset payment, creates the depreciation transactions that will be stored in
     * the general ledger pending entry table
     * 
     * @param depreciableAssetsCollection asset payments eligible for depreciation
     * @return SortedMap with a list of depreciation transactions
     */
    protected SortedMap<String, AssetDepreciationTransaction> calculateDepreciation(Collection<AssetPaymentInfo> depreciableAssetsCollection, Calendar depreciationDate) {
        LOG.debug("calculateDepreciation() - start");

        Collection<String> organizationPlantFundObjectSubType = new ArrayList<String>();
        Collection<String> campusPlantFundObjectSubType = new ArrayList<String>();
        SortedMap<String, AssetDepreciationTransaction> depreciationTransactionSummary = new TreeMap<String, AssetDepreciationTransaction>();
        double monthsElapsed = 0d;
        double assetLifeInMonths = 0d;
        KualiDecimal accumulatedDepreciationAmount = KualiDecimal.ZERO;
        Calendar assetDepreciationDate = Calendar.getInstance();

        try {
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting the parameters for the plant fund object sub types.");
            // Getting system parameters needed.
            if (parameterService.parameterExists(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_ORGANIZATON_PLANT_FUND_SUB_OBJECT_TYPES)) {
                organizationPlantFundObjectSubType = parameterService.getParameterValues(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_ORGANIZATON_PLANT_FUND_SUB_OBJECT_TYPES);
            }
            if (parameterService.parameterExists(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPES)) {
                campusPlantFundObjectSubType = parameterService.getParameterValues(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPES);
            }
            // Initializing the asset payment table.
            depreciationBatchDao.resetPeriodValuesWhenFirstFiscalPeriod(fiscalMonth);
            LOG.debug("getBaseAmountOfAssets(Collection<AssetPayment> depreciableAssetsCollection) - Started.");
            // Invoking method that will calculate the base amount for each asset payment transactions, which could be more than 1
            // per asset.
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Calculating the base amount for each asset.");
            Map<Long, KualiDecimal> salvageValueAssetDeprAmounts = depreciationBatchDao.getPrimaryDepreciationBaseAmountForSV();
            // Retrieving the object asset codes.
            Map<String, AssetObjectCode> assetObjectCodeMap = buildChartObjectToCapitalizationObjectMap();
            Map<String, ObjectCode> capitalizationObjectCodes = new HashMap<String, ObjectCode>();

            // Reading asset payments
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Reading collection with eligible asset payment details.");
            int counter = 0;
            List<AssetPaymentInfo> saveList = new ArrayList<AssetPaymentInfo>();
            for (AssetPaymentInfo assetPaymentInfo : depreciableAssetsCollection) {
                AssetObjectCode assetObjectCode = assetObjectCodeMap.get(assetPaymentInfo.getChartOfAccountsCode() + "-" + assetPaymentInfo.getFinancialObjectCode());
                if (assetObjectCode == null) {
                    LOG.error(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Asset object code not found for " + fiscalYear + "-" + assetPaymentInfo.getChartOfAccountsCode() + "-" + assetPaymentInfo.getFinancialObjectCode());
                    LOG.error(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Asset payment is not included in depreciation " + assetPaymentInfo.getCapitalAssetNumber() + " - " + assetPaymentInfo.getPaymentSequenceNumber());
                    continue;
                }
                ObjectCode accumulatedDepreciationFinancialObject = getDepreciationObjectCode(capitalizationObjectCodes, assetPaymentInfo, assetObjectCode.getAccumulatedDepreciationFinancialObjectCode());
                ObjectCode depreciationExpenseFinancialObject = getDepreciationObjectCode(capitalizationObjectCodes, assetPaymentInfo, assetObjectCode.getDepreciationExpenseFinancialObjectCode());

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
                if (primaryDepreciationBaseAmount == null)
                    assetPaymentInfo.setPrimaryDepreciationBaseAmount(KualiDecimal.ZERO);

                if (assetPaymentInfo.getAccumulatedPrimaryDepreciationAmount() == null)
                    assetPaymentInfo.setAccumulatedPrimaryDepreciationAmount(KualiDecimal.ZERO);

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
                LOG.info("Asset#: " + assetNumber + " - Payment sequence#:" + assetPaymentInfo.getPaymentSequenceNumber() + " - Asset Depreciation date:" + assetDepreciationDate + " - Life:" + assetLifeInMonths + " - Depreciation base amt:" + primaryDepreciationBaseAmount + " - Accumulated depreciation:" + assetPaymentInfo.getAccumulatedPrimaryDepreciationAmount() + " - Month Elapsed:" + monthsElapsed + " - Calculated accum depreciation:" + accumulatedDepreciationAmount + " - Depreciation amount:" + transactionAmount.toString() + " - Depreciation Method:" + assetPaymentInfo.getPrimaryDepreciationMethodCode());
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
            throw new IllegalStateException(kualiConfigurationService.getPropertyString(CamsKeyConstants.Depreciation.ERROR_WHEN_CALCULATING_DEPRECIATION) + " :" + e.getMessage());
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
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "populateDepreciationTransaction(): populating AssetDepreciationTransaction pojo - Asset#:" + assetPayment.getCapitalAssetNumber());
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
    protected void processGeneralLedgerPendingEntry(SortedMap<String, AssetDepreciationTransaction> trans) {
        LOG.debug("populateExplicitGeneralLedgerPendingEntry(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper, GeneralLedgerPendingEntry) - start");

        String financialSystemDocumentTypeCodeCode;
        try {

            String documentNumber = createNewDepreciationDocument();
            financialSystemDocumentTypeCodeCode = CamsConstants.DocumentTypeName.ASSET_DEPRECIATION;
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Depreciation Document Type Code: " + financialSystemDocumentTypeCodeCode);

            Timestamp transactionTimestamp = new Timestamp(dateTimeService.getCurrentDate().getTime());

            GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper();
            List<GeneralLedgerPendingEntry> saveList = new ArrayList<GeneralLedgerPendingEntry>();
            int counter = 0;

            for (AssetDepreciationTransaction t : trans.values()) {
                if (t.getTransactionAmount().isNonZero()) {
                    counter++;
                    LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Creating GLPE entries for asset:" + t.getCapitalAssetNumber());
                    GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
                    explicitEntry.setFinancialSystemOriginationCode(KFSConstants.ORIGIN_CODE_KUALI);
                    explicitEntry.setDocumentNumber(documentNumber);
                    explicitEntry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));
                    sequenceHelper.increment();
                    explicitEntry.setChartOfAccountsCode(t.getChartOfAccountsCode());
                    explicitEntry.setAccountNumber(t.getAccountNumber());
                    explicitEntry.setSubAccountNumber(t.getSubAccountNumber());
                    explicitEntry.setFinancialObjectCode(t.getFinancialObjectCode());
                    explicitEntry.setFinancialSubObjectCode(t.getFinancialSubObjectCode());
                    explicitEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_ACTUAL);
                    explicitEntry.setFinancialObjectTypeCode(t.getFinancialObjectTypeCode());
                    explicitEntry.setUniversityFiscalYear(this.fiscalYear);
                    explicitEntry.setUniversityFiscalPeriodCode(StringUtils.leftPad(this.fiscalMonth.toString().trim(), 2, "0"));
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
                        documentNumber = createNewDepreciationDocument();
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
            throw new IllegalStateException(kualiConfigurationService.getPropertyString(CamsKeyConstants.Depreciation.ERROR_WHEN_UPDATING_GL_PENDING_ENTRY_TABLE) + " :" + e.getMessage());
        }
        LOG.debug("populateExplicitGeneralLedgerPendingEntry(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper, GeneralLedgerPendingEntry) - end");
    }


    protected String createNewDepreciationDocument() throws WorkflowException {
        KualiWorkflowDocument workflowDocument = workflowDocumentService.createWorkflowDocument(CamsConstants.DocumentTypeName.ASSET_DEPRECIATION, GlobalVariables.getUserSession().getPerson());
        // **************************************************************************************************
        // Create a new document header object
        // **************************************************************************************************
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Creating document header entry.");

        FinancialSystemDocumentHeader documentHeader = new FinancialSystemDocumentHeader();
        documentHeader.setWorkflowDocument(workflowDocument);
        documentHeader.setDocumentNumber(workflowDocument.getRouteHeaderId().toString());
        documentHeader.setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
        documentHeader.setExplanation(CamsConstants.Depreciation.DOCUMENT_DESCRIPTION);
        documentHeader.setDocumentDescription(CamsConstants.Depreciation.DOCUMENT_DESCRIPTION);
        documentHeader.setFinancialDocumentTotalAmount(KualiDecimal.ZERO);

        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Saving document header entry.");
        this.businessObjectService.save(documentHeader);
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Document Header entry was saved successfully.");
        // **************************************************************************************************

        String documentNumber = documentHeader.getDocumentNumber();
        this.documentNos.add(documentNumber);
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Document Number Created: " + documentNumber);
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
    protected ObjectCode getDepreciationObjectCode(Map<String, ObjectCode> capObjectCodesCache, AssetPaymentInfo assetPaymentInfo, String capitalizationFinancialObjectCode) {
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
    protected Map<String, AssetObjectCode> buildChartObjectToCapitalizationObjectMap() {
        Map<String, AssetObjectCode> assetObjectCodeMap = new HashMap<String, AssetObjectCode>();
        Collection<AssetObjectCode> assetObjectCodes = getAssetObjectCodes(fiscalYear);

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

    /**
     * @see org.kuali.kfs.module.cam.document.dataaccess.DepreciableAssetsDao#getAssetObjectCodes(java.lang.Integer)
     */
    @CacheNoCopy
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

    /**
     * This method gets a list of Expense object codes from the asset object code table for a particular fiscal year
     * 
     * @param fiscalYear
     * @return a List<String>
     */
    @CacheNoCopy
    protected List<String> getExpenseObjectCodes(Integer fiscalYear) {
        LOG.debug("DepreciableAssetsDAoOjb.getExpenseObjectCodes() -  started");
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting expense object codes");

        List<String> depreExpObjCodes = new ArrayList<String>();
        Collection<AssetObjectCode> assetObjectCodesCollection = this.getAssetObjectCodes(fiscalYear);

        // Creating a list of depreciation expense object codes.
        for (Iterator<AssetObjectCode> iterator = assetObjectCodesCollection.iterator(); iterator.hasNext();) {
            AssetObjectCode assetObjectCode = iterator.next();

            String objCode = assetObjectCode.getDepreciationExpenseFinancialObjectCode();
            if (objCode != null && !objCode.equals("") && !depreExpObjCodes.contains(objCode)) {
                depreExpObjCodes.add(objCode);
            }
        }
        LOG.debug("DepreciableAssetsDAoOjb.getExpenseObjectCodes() -  ended");
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Finished getting expense object codes which are:" + depreExpObjCodes.toString());
        return depreExpObjCodes;
    }

    /**
     * This method gets a list of Accumulated Depreciation Object Codes from the asset object code table for a particular fiscal
     * year.
     * 
     * @param fiscalYear
     * @return List<String>
     */
    @CacheNoCopy
    protected List<String> getAccumulatedDepreciationObjectCodes(Integer fiscalYear) {
        LOG.debug("DepreciableAssetsDAoOjb.getAccumulatedDepreciationObjectCodes() -  started");
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting accum depreciation object codes");

        List<String> accumulatedDepreciationObjCodes = new ArrayList<String>();
        Collection<AssetObjectCode> assetObjectCodesCollection = this.getAssetObjectCodes(fiscalYear);

        // Creating a list of depreciation expense object codes.
        for (Iterator<AssetObjectCode> iterator = assetObjectCodesCollection.iterator(); iterator.hasNext();) {
            AssetObjectCode assetObjectCode = iterator.next();

            String objCode = assetObjectCode.getAccumulatedDepreciationFinancialObjectCode();
            if (objCode != null && !objCode.equals("") && !accumulatedDepreciationObjCodes.contains(objCode)) {
                accumulatedDepreciationObjCodes.add(objCode);
            }
        }
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Finished getting accum depreciation object codes which are:" + accumulatedDepreciationObjCodes.toString());
        LOG.debug("DepreciableAssetsDAoOjb.getAccumulatedDepreciationObjectCodes() -  ended");
        return accumulatedDepreciationObjCodes;
    }

    /**
     * This method the value of the system parameter NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES
     * 
     * @return
     */
    protected List<String> getFederallyOwnedObjectSubTypes() {
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "getting the federally owned object subtype codes.");

        List<String> federallyOwnedObjectSubTypes = new ArrayList<String>();
        if (parameterService.parameterExists(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES)) {
            federallyOwnedObjectSubTypes = parameterService.getParameterValues(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES);
        }
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Finished getting the federally owned object subtype codes which are:" + federallyOwnedObjectSubTypes.toString());
        return federallyOwnedObjectSubTypes;
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

    public void setKualiConfigurationService(KualiConfigurationService kcs) {
        kualiConfigurationService = kcs;
    }

    public void setGeneralLedgerPendingEntryService(GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setUniversityDateDao(UniversityDateDao universityDateDao) {
        this.universityDateDao = universityDateDao;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
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
    public void setDepreciationBatchDao(DepreciationBatchDao depreciationBatchDao) {
        this.depreciationBatchDao = depreciationBatchDao;
    }

    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }
    
    protected SortedMap<String, AssetDepreciationTransaction> calculateYearEndDepreciation(Collection<AssetPaymentInfo> depreciableAssetsCollection, Calendar depreciationDate, Integer fiscalYearToDepreciate) {
    LOG.info("calculateDepreciation() - start");

    List<String> organizationPlantFundObjectSubType = new ArrayList<String>();
    List<String> campusPlantFundObjectSubType = new ArrayList<String>();
    SortedMap<String, AssetDepreciationTransaction> depreciationTransactionSummary = new TreeMap<String, AssetDepreciationTransaction>();
    double monthsElapsed = 0d;
    double assetLifeInMonths = 0d;
    KualiDecimal accumulatedDepreciationAmount = KualiDecimal.ZERO;
    Calendar assetDepreciationDate = Calendar.getInstance();

    try {
        LOG.info("YEAR END DEPRECIATION - " + "Getting the parameters for the plant fund object sub types.");
        // Getting system parameters needed.
        if (parameterService.parameterExists(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_ORGANIZATON_PLANT_FUND_SUB_OBJECT_TYPES)) {
            organizationPlantFundObjectSubType = parameterService.getParameterValues(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_ORGANIZATON_PLANT_FUND_SUB_OBJECT_TYPES);
        }
        if (parameterService.parameterExists(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPES)) {
            campusPlantFundObjectSubType = parameterService.getParameterValues(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPES);
        }
        // Initializing the asset payment table.
        depreciationBatchDao.resetPeriodValuesWhenFirstFiscalPeriod(fiscalMonth);
        LOG.info("getBaseAmountOfAssets(Collection<AssetPayment> depreciableAssetsCollection) - Started.");
        // Invoking method that will calculate the base amount for each asset payment transactions, which could be more than 1
        // per asset.
        LOG.info("YEAR END DEPRECIATION - " + "Calculating the base amount for each asset.");
        Map<Long, KualiDecimal> salvageValueAssetDeprAmounts = depreciationBatchDao.getPrimaryDepreciationBaseAmountForSV();
        // Retrieving the object asset codes.
        Map<String, AssetObjectCode> assetObjectCodeMap = buildChartObjectToCapitalizationObjectMap();
        Map<String, ObjectCode> capitalizationObjectCodes = new HashMap<String, ObjectCode>();

        // Reading asset payments
        LOG.info("YEAR END DEPRECIATION - " + "Reading collection with eligible asset payment details.");
        int counter = 0;
        List<AssetPaymentInfo> saveList = new ArrayList<AssetPaymentInfo>();
        for (AssetPaymentInfo assetPaymentInfo : depreciableAssetsCollection) {

            boolean asset_is_retired = false;
            boolean asset_is_not_in_last_year_of_life = false;
            Map pKeys = new HashMap<String, Object>();
            // Asset must be valid and capital active 'A','C','S','U'
            Long assetNumber = assetPaymentInfo.getCapitalAssetNumber();
            pKeys.put(CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER, assetNumber);

            Asset asset = (Asset) businessObjectService.findByPrimaryKey(Asset.class, pKeys);
            if (asset != null) {
                asset_is_retired = assetService.isAssetRetired(asset);
                LOG.info("asset#" + assetNumber + "   asset_is_retired = " + asset_is_retired);
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
            ObjectCode accumulatedDepreciationFinancialObject = getDepreciationObjectCode(capitalizationObjectCodes, assetPaymentInfo, assetObjectCode.getAccumulatedDepreciationFinancialObjectCode());
            ObjectCode depreciationExpenseFinancialObject = getDepreciationObjectCode(capitalizationObjectCodes, assetPaymentInfo, assetObjectCode.getDepreciationExpenseFinancialObjectCode());
            String retire_code = parameterService.getParameterValue(org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal.class, CamsConstants.Parameters.DEFAULT_GAIN_LOSS_DISPOSITION_OBJECT_CODE);
            LOG.info("retire_code from system parameter "+ CamsConstants.Parameters.DEFAULT_GAIN_LOSS_DISPOSITION_OBJECT_CODE+" = " + retire_code);
            ObjectCode depreciationYearEndExpenseFinancialObject = getDepreciationObjectCode(capitalizationObjectCodes, assetPaymentInfo, retire_code);

            if (ObjectUtils.isNull(accumulatedDepreciationFinancialObject)) {
                LOG.error("YEAR END DEPRECIATION - " + "Accumulated Depreciation Financial Object Code not found for " + fiscalYear + "-" + assetPaymentInfo.getChartOfAccountsCode() + "-" + assetObjectCode.getAccumulatedDepreciationFinancialObjectCode());
                LOG.error("YEAR END DEPRECIATION - " + "Asset payment is not included in depreciation " + assetPaymentInfo.getCapitalAssetNumber() + " - " + assetPaymentInfo.getPaymentSequenceNumber());
                continue;
            } else {
//                LOG.info("YEAR END DEPRECIATION - " + " AccumulatedDepreciationFinancialObjectCode:" + assetObjectCode.getAccumulatedDepreciationFinancialObjectCode());
//                LOG.info("YEAR END DEPRECIATION - " + "CapitalAssetNumber:" + assetPaymentInfo.getCapitalAssetNumber() + " PaymentSequenceNumber:" + assetPaymentInfo.getPaymentSequenceNumber());
                LOG.info("YEAR END DEPRECIATION - " + "accumulatedDepreciationFinancialObject:" + accumulatedDepreciationFinancialObject.getFinancialObjectCode());
            }

            if (ObjectUtils.isNull(depreciationExpenseFinancialObject)) {
                LOG.error("YEAR END DEPRECIATION - " + "Depreciation Expense Financial Object Code not found for " + fiscalYear + "-" + assetPaymentInfo.getChartOfAccountsCode() + "-" + assetObjectCode.getDepreciationExpenseFinancialObjectCode());
                LOG.error("YEAR END DEPRECIATION - " + "Asset payment is not included in depreciation " + assetPaymentInfo.getCapitalAssetNumber() + " - " + assetPaymentInfo.getPaymentSequenceNumber());
                continue;
            } else {
                LOG.info("YEAR END DEPRECIATION - " + "depreciationExpenseFinancialObject:" + depreciationExpenseFinancialObject.getFinancialObjectCode());
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
            if (primaryDepreciationBaseAmount == null)
                assetPaymentInfo.setPrimaryDepreciationBaseAmount(KualiDecimal.ZERO);

            if (assetPaymentInfo.getAccumulatedPrimaryDepreciationAmount() == null)
                assetPaymentInfo.setAccumulatedPrimaryDepreciationAmount(KualiDecimal.ZERO);

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
                    LOG.info("transactionAmount after being halved = " + transactionAmount);
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
            LOG.info("Asset#: " + assetNumber + " - Payment sequence#:" + assetPaymentInfo.getPaymentSequenceNumber() + " - Asset Depreciation date:" + sdf.format(assetDepreciationDate.getTime()) + " - Life:" + assetLifeInMonths + " - Depreciation base amt:" + primaryDepreciationBaseAmount);
            LOG.info("Accumulated depreciation:" + assetPaymentInfo.getAccumulatedPrimaryDepreciationAmount() + " - Month Elapsed:" + monthsElapsed + " - Calculated accum depreciation:" + accumulatedDepreciationAmount + " - Depreciation amount:" + transactionAmount.toString() + " - Depreciation Method:" + assetPaymentInfo.getPrimaryDepreciationMethodCode());
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
        throw new IllegalStateException(kualiConfigurationService.getPropertyString(CamsKeyConstants.Depreciation.ERROR_WHEN_CALCULATING_DEPRECIATION) + " :" + e.getMessage());
    }
}

}
