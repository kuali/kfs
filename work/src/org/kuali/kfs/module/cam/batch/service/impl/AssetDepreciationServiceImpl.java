/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.cams.service.impl;

import static org.kuali.kfs.KFSConstants.BALANCE_TYPE_ACTUAL;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.service.HomeOriginationService;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetDepreciationTransaction;
import org.kuali.module.cams.bo.AssetObjectCode;
import org.kuali.module.cams.bo.AssetPayment;
import org.kuali.module.cams.bo.AssetType;
import org.kuali.module.cams.dao.DepreciableAssetsDao;
import org.kuali.module.cams.document.AssetDepreciationDocument;
import org.kuali.module.cams.service.AssetDepreciationService;
import org.kuali.module.cams.service.ReportService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.dao.UniversityDateDao;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * This class is a service that calculates the depreciation amount for each asset that has a eligible asset payment.
 * <p>
 * When an error occurs running this process, a pdf file will be created with the error message. However, this doesn't mean that
 * this process automatically leaves all the records as they were right before running the program. If the process fails, is
 * suggested to do the following before trying to run the process again:
 * 
 * a.)Delete gl pending entry depreciation entries: DELETE FROM GL_PENDING_ENTRY_T WHERE FDOC_TYP_CD = 'DEPR'
 * 
 * b.)Substract from the accumulated depreciation amount the depreciation calculated for the fiscal month that was ran, and then
 * reset the depreciation amount field for the fiscal month that was ran. ex: Assuming that the fiscal month = 8 then: UPDATE
 * CM_AST_PAYMENT_T SET AST_ACUM_DEPR1_AMT = AST_ACUM_DEPR1_AMT - AST_PRD8_DEPR1_AMT, AST_PRD8_DEPR1_AMT=0
 * 
 */
@Transactional
public class AssetDepreciationServiceImpl implements AssetDepreciationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetDepreciationServiceImpl.class);
    private ParameterService parameterService;
    private ReportService reportService;
    private DateTimeService dateTimeService;
    private DepreciableAssetsDao depreciableAssetsDao;
    private KualiConfigurationService kualiConfigurationService;
    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    private BusinessObjectService businessObjectService;
    private GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper();
    private UniversityDateDao universityDateDao;
    private WorkflowDocumentService workflowDocumentService;
    private HomeOriginationService homeOriginationService;
    private DocumentTypeService documentTypeService;
    private Integer fiscalYear;
    private Integer fiscalMonth;
    private String documentNumber;
    private String errorMsg = "";

    /**
     * 
     * @see org.kuali.module.cams.service.AssetDepreciationService#runDepreciation()
     */
    public void runDepreciation() {
        List<String[]> reportLog = new ArrayList<String[]>();

        boolean error = false;
        UniversityDate universityDate;

        Calendar depreciationDate = Calendar.getInstance();
        Calendar currentDate = Calendar.getInstance();

        String depreciationDateParameter = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            LOG.info("*******" + CamsConstants.Depreciation.DEPRECIATION_BATCH + " HAS BEGUN *******");

            /*
             * Getting the system parameter "DEPRECIATION_DATE" When this parameter is used to determine which fiscal month and year
             * is going to be depreciated If blank then the system will take the system date to determine the fiscal period
             * 
             */

            if (parameterService.parameterExists(ParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_RUN_DATE_PARAMETER)) {
                depreciationDateParameter = ((List<String>) parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_RUN_DATE_PARAMETER)).get(0).trim();
            }
            else {
                throw new IllegalStateException(kualiConfigurationService.getPropertyString(CamsKeyConstants.Depreciation.DEPRECIATION_DATE_PARAMETER_NOT_FOUND));
            }

            // ***************** GET RID OF THESE LINES WHEN DONE TESTING **********
            //depreciationDateParameter = "2008-02-01";
            //currentDate.setTime(dateFormat.parse("2008-02-01"));
            // ********************************************************************

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

            // If the depreciation date is not = to the system date then, the depreciation process cannot run.
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting fiscal month and fiscal year.");

            universityDate = universityDateDao.getByPrimaryKey(depreciationDate.getTime());
            if (universityDate == null) {
                throw new IllegalStateException(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_UNIV_DATE_NOT_FOUND));
            }

            this.fiscalYear = universityDate.getUniversityFiscalYear();
            this.fiscalMonth = new Integer(universityDate.getUniversityFiscalAccountingPeriod());

            reportLog.addAll(depreciableAssetsDao.generateStatistics(true, "", fiscalYear, fiscalMonth, depreciationDate));

            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting a list of asset payments eligible for depreciation.");

            // Retrieving eligible assets
            Collection<AssetPayment> depreciableAssetsCollection = depreciableAssetsDao.getListOfDepreciableAssets(this.fiscalYear, this.fiscalMonth, depreciationDate);

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
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "**************************************************************************");
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "AN ERROR HAS OCCURRED! - ERROR: " + e.getMessage());
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "**************************************************************************");

            error = true;
            this.errorMsg = "Depreciation process ran unsucessfuly.\nReason:" + e.getMessage();
        }
        finally {
            if (!error) {
                reportLog.addAll(depreciableAssetsDao.generateStatistics(false, this.documentNumber, fiscalYear, fiscalMonth, depreciationDate));
            }
            // the report will be generated only when there is an error or when the log has something.
            if (!reportLog.isEmpty() || !errorMsg.trim().equals(""))
                reportService.generateDepreciationReport(reportLog, errorMsg, depreciationDateParameter);

            LOG.info("*******" + CamsConstants.Depreciation.DEPRECIATION_BATCH + " HAS ENDED *******");
        }
    }

    /**
     * 
     * This method calculates the base amount adding the PrimaryDepreciationBaseAmount field of each asset
     * 
     * @param depreciableAssetsCollection collection of assets and assets payments that are eligible for depreciation
     * @return Map with the asset number and the base amount that was calculated
     */
    private Map<Long, KualiDecimal> getBaseAmountOfAssets(Collection<AssetPayment> depreciableAssetsCollection) {
        LOG.debug("getBaseAmountOfAssets(Collection<AssetPayment> depreciableAssetsCollection) - Started.");

        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Calculating the base amount for each asset.");

        Map<Long, KualiDecimal> assetsBaseAmount = new HashMap<Long, KualiDecimal>();
        Long assetNumber = new Long(0);
        KualiDecimal baseAmount = new KualiDecimal(0);

        try {
            int i = 0;
            for (AssetPayment depreciableAssets : depreciableAssetsCollection) {
                i++;
                
                if (ObjectUtils.isNull(depreciableAssets.getPrimaryDepreciationBaseAmount()))
                    depreciableAssets.setPrimaryDepreciationBaseAmount(new KualiDecimal(0));
                
                if (assetNumber.compareTo(new Long(0)) == 0)
                    assetNumber = depreciableAssets.getCapitalAssetNumber();

                if (depreciableAssets.getCapitalAssetNumber().compareTo(assetNumber) == 0) {
                    baseAmount = baseAmount.add(depreciableAssets.getPrimaryDepreciationBaseAmount());
                }

                if ((depreciableAssets.getCapitalAssetNumber().compareTo(assetNumber) < 0) || (i == depreciableAssetsCollection.size())) {
                    assetsBaseAmount.put(assetNumber, baseAmount);
                    baseAmount = new KualiDecimal(0);
                }
                assetNumber = depreciableAssets.getCapitalAssetNumber();
            }
        }
        catch (Exception e) {
            throw new IllegalStateException(kualiConfigurationService.getPropertyString(CamsKeyConstants.Depreciation.ERROR_WHEN_CALCULATING_BASE_AMOUNT) + " :" + e.getMessage());
        }
        LOG.debug("getBaseAmountOfAssets(Collection<AssetPayment> depreciableAssetsCollection) - End.");
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Finished calculating the base amount for each asset.");
        return assetsBaseAmount;
    }

    /**
     * 
     * This method calculates the depreciation of each asset payment, creates the depreciation transactions that will be stored in
     * the general ledger pending entry table
     * 
     * @param depreciableAssetsCollection asset payments eligible for depreciation
     * @return SortedMap with a list of depreciation transactions
     */
    private SortedMap<String, AssetDepreciationTransaction> calculateDepreciation(Collection<AssetPayment> depreciableAssetsCollection, Calendar depreciationDate) {
        LOG.debug("calculateDepreciation() - start");
        
        List<String> organizationPlantFundObjectSubType = new ArrayList<String>();
        List<String> campusPlantFundObjectSubType = new ArrayList<String>();
        AssetDepreciationTransaction depreciationTransaction = new AssetDepreciationTransaction();
        SortedMap<String, AssetDepreciationTransaction> depreciationTransactionSummary = new TreeMap<String, AssetDepreciationTransaction>();

        Double monthsElapsed;
        Double assetLifeInMonths;

        KualiDecimal depreciationPercentage = new KualiDecimal(0);

        KualiDecimal baseAmount;
        KualiDecimal accumulatedDepreciationAmount;

        Calendar assetDepreciationDate = Calendar.getInstance();
        boolean documentCreated = false;

        try {
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH+"Getting the parameters for the plant fund object sub types.");            

            // Getting system parameters needed.
            if (parameterService.parameterExists(ParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_ORGANIZATON_PLANT_FUND_SUB_OBJECT_TYPES)) {
                organizationPlantFundObjectSubType = parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_ORGANIZATON_PLANT_FUND_SUB_OBJECT_TYPES);
            }

            if (parameterService.parameterExists(ParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPES)) {
                campusPlantFundObjectSubType = parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPES);
            }
            
            // Initializing the asset payment table.
            depreciableAssetsDao.initializeAssetPayment(fiscalMonth);

            // Invoking method that will calculate the base amount for each asset payment transactions, which could be more than 1
            // per asset.
            Map<Long, KualiDecimal> assetBaseAmounts = this.getBaseAmountOfAssets(depreciableAssetsCollection);

            // Retrieving the object asset codes.
            Collection<AssetObjectCode> objectCodes = depreciableAssetsDao.getAssetObjectCodes(fiscalYear);

            // Reading asset payments
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH+"Reading collection with eligible asset payments.");                        
            for (AssetPayment assetPayment : depreciableAssetsCollection) {
                LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH+"Processing asset#:"+assetPayment.getCapitalAssetNumber()+" - Seq#:"+assetPayment.getPaymentSequenceNumber());
                
                // Getting the asset
                Asset asset = assetPayment.getAsset();

                // Getting asset type
                AssetType assetType = asset.getCapitalAssetType();
                
                // Getting the account the asset payment is associated with.
                Account account = assetPayment.getAccount();
                
                // Organization code the asset payment account is associated with.
                Org org = account.getOrganization();
                
                // Object code the asset payment is associated with
                ObjectCode financialObject = assetPayment.getFinancialObject();

                String accumulatedDepreciationFinancialObjectCode = "";
                String depreciationExpenseFinancialObjectCode = "";

                // Reading the list of asset object codes in order to get the accumulated and expense object code that are going
                // to be stored in the glpe depreciation transaction.
                LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH+"Getting the accumulated object code and expense object code of each asset payment.");                                        
                for (AssetObjectCode assetObjectCodes : objectCodes) {
                    boolean found = false;
                    List<ObjectCode> objectCodesList = assetObjectCodes.getObjectCode();
                    for (ObjectCode oc : objectCodesList) {
                        if (oc.getFinancialObjectCode().equals(assetPayment.getFinancialObjectCode())) {
                            accumulatedDepreciationFinancialObjectCode = assetObjectCodes.getAccumulatedDepreciationFinancialObjectCode();
                            depreciationExpenseFinancialObjectCode = assetObjectCodes.getDepreciationExpenseFinancialObjectCode();
                            found = true;
                            break;
                        }
                    }
                    if (found)
                        break;
                }

                LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH+"Getting the accumulated object code and expense object code of each asset payment.");                                        
                
                monthsElapsed = new Double(0);
                String transactionType = KFSConstants.GL_DEBIT_CODE;

                assetDepreciationDate.setTime(asset.getDepreciationDate());
                accumulatedDepreciationAmount = new KualiDecimal(0);

                // Getting the asset base amount.
                baseAmount = assetBaseAmounts.get(assetPayment.getCapitalAssetNumber());
                
                // Calculating the life of the asset in months.
                assetLifeInMonths = new Double(assetType.getDepreciableLifeLimit() * 12);

                // Calculating the month elapse of the asset using the depreciation date and the asset service date.
                monthsElapsed = new Double(depreciationDate.get(Calendar.MONTH) - assetDepreciationDate.get(Calendar.MONTH) + (depreciationDate.get(Calendar.YEAR) - assetDepreciationDate.get(Calendar.YEAR)) * 12) + 1;

                // Calculating depreciation accumulated depreciation amount.
                LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH+"Calculating depreciation - Asset:"+assetPayment.getCapitalAssetNumber());
                
                // **************************************************************************************************************
                // CALCULATING ACCUMULATED DEPRECIATION BASED ON FORMULA FOR SINGLE LINE AND SALVAGE VALUE DEPRECIATION METHODS.
                // **************************************************************************************************************                                
                if (ObjectUtils.isNull(assetPayment.getPrimaryDepreciationBaseAmount()))
                    assetPayment.setPrimaryDepreciationBaseAmount(new KualiDecimal(0));
                 
                 if (ObjectUtils.isNull(assetPayment.getAccumulatedPrimaryDepreciationAmount()))
                     assetPayment.setAccumulatedPrimaryDepreciationAmount(new KualiDecimal(0));
                    
                // If the month elapse >= to the life of the asset (in months) then, the accumulated depreciation should be:
                if (monthsElapsed.compareTo(assetLifeInMonths) >= 0) {
                    if (asset.getPrimaryDepreciationMethodCode().equals(CamsConstants.DEPRECIATION_METHOD_STRAIGHT_LINE_CODE))
                        accumulatedDepreciationAmount =  assetPayment.getPrimaryDepreciationBaseAmount(); 
                    else if (asset.getPrimaryDepreciationMethodCode().equals(CamsConstants.DEPRECIATION_METHOD_SALVAGE_VALUE_CODE))
                        accumulatedDepreciationAmount = assetPayment.getPrimaryDepreciationBaseAmount().subtract((assetPayment.getPrimaryDepreciationBaseAmount().divide(baseAmount)).multiply(asset.getSalvageAmount()));                    
                } // If the month elapse < to the life of the asset (in months) then....
                else {
                    if (asset.getPrimaryDepreciationMethodCode().equals(CamsConstants.DEPRECIATION_METHOD_STRAIGHT_LINE_CODE))
                        accumulatedDepreciationAmount = new KualiDecimal((monthsElapsed.doubleValue() / assetLifeInMonths.doubleValue()) * assetPayment.getPrimaryDepreciationBaseAmount().doubleValue());
                    else if (asset.getPrimaryDepreciationMethodCode().equals(CamsConstants.DEPRECIATION_METHOD_SALVAGE_VALUE_CODE))
                        accumulatedDepreciationAmount = new KualiDecimal((monthsElapsed.doubleValue() / assetLifeInMonths.doubleValue()) * (assetPayment.getPrimaryDepreciationBaseAmount().subtract((assetPayment.getPrimaryDepreciationBaseAmount().divide(baseAmount)).multiply(asset.getSalvageAmount()))).doubleValue());
                }

                // Calculating in process fiscal month depreciation amount
                KualiDecimal transactionAmount = accumulatedDepreciationAmount.subtract(assetPayment.getAccumulatedPrimaryDepreciationAmount());


                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");                
                LOG.info("Asset#: "+assetPayment.getCapitalAssetNumber()+
                        " Payment sequence#:"+assetPayment.getPaymentSequenceNumber()+
                        " Depreciation date:"+dateFormat.format(assetDepreciationDate.getTime())+
                        " Life:"+assetLifeInMonths + 
                        " Depreciation base amt:"+assetPayment.getPrimaryDepreciationBaseAmount()+
                        " Accumulated depreciation:"+assetPayment.getAccumulatedPrimaryDepreciationAmount()+
                        " Month Elapsed:"+monthsElapsed+ 
                        " Calculated accum depreciation:"+accumulatedDepreciationAmount+
                        " Depreciation amount:"+transactionAmount.toString());

                if (transactionAmount.compareTo(new KualiDecimal(0)) == -1)
                    transactionType = KFSConstants.GL_CREDIT_CODE;

                String plantAccount = "";
                String plantCOA = "";
                
                // getting the right Plant Fund Chart code & Plant Fund Account
                if (organizationPlantFundObjectSubType.contains(financialObject.getFinancialObjectSubTypeCode())) {
                    plantAccount = org.getOrganizationPlantAccountNumber();
                    plantCOA = org.getOrganizationPlantChartCode();
                }
                else if (campusPlantFundObjectSubType.contains(financialObject.getFinancialObjectSubTypeCode())) {
                    plantAccount = org.getCampusPlantAccountNumber();
                    plantCOA = org.getCampusPlantChartCode();
                }

                // Saving depreciation amount in the asset payment table
                 depreciableAssetsDao.updateAssetPayments(assetPayment.getCapitalAssetNumber().toString(),
                                                          assetPayment.getPaymentSequenceNumber().toString(), transactionAmount, accumulatedDepreciationAmount,
                                                          fiscalMonth);

                 // if the asset has a depreciation amount > than 0 then, create its debits and credits.
                if (!transactionAmount.isZero()) {
                    this.populateDepreciationTransaction(depreciationTransaction, assetPayment, transactionType, transactionAmount, plantCOA, plantAccount, accumulatedDepreciationFinancialObjectCode, depreciationExpenseFinancialObjectCode, financialObject, depreciationTransactionSummary);
                    
                    transactionType = (transactionType.equals(KFSConstants.GL_CREDIT_CODE) ? KFSConstants.GL_DEBIT_CODE : KFSConstants.GL_CREDIT_CODE);
                    
                    this.populateDepreciationTransaction(depreciationTransaction, assetPayment, transactionType, transactionAmount, plantCOA, plantAccount, accumulatedDepreciationFinancialObjectCode, depreciationExpenseFinancialObjectCode, financialObject, depreciationTransactionSummary);
                }
            } // end for

            return depreciationTransactionSummary;
        }
        catch (Exception e) {
            throw new IllegalStateException(kualiConfigurationService.getPropertyString(CamsKeyConstants.Depreciation.ERROR_WHEN_CALCULATING_DEPRECIATION) + " :" + e.getMessage());
        }
    }

    /**
     * 
     * This method stores in a collection of business objects the depreciation transaction that later on will be passed to the
     * processGeneralLedgerPendingEntry method in order to store the records in gl pending entry table
     * 
     * @param depreciationTransaction
     * @param assetPayment asset payment
     * @param transactionType which can be [C]redit or [D]ebit
     * @param transactionAmount depreciation amount
     * @param plantCOA plant fund char of account code
     * @param plantAccount plant fund char of account code
     * @param accumulatedDepreciationFinancialObjectCode accumulated depreciation object code
     * @param depreciationExpenseFinancialObjectCode expense object code
     * @param financialObject char of account object code linked to the payment
     * @param depreciationTransactionSummary
     * @return none
     */
    private void populateDepreciationTransaction(AssetDepreciationTransaction depreciationTransaction, AssetPayment assetPayment, String transactionType, KualiDecimal transactionAmount, String plantCOA, String plantAccount, String accumulatedDepreciationFinancialObjectCode, String depreciationExpenseFinancialObjectCode, ObjectCode financialObject, SortedMap<String, AssetDepreciationTransaction> depreciationTransactionSummary) {
        LOG.debug("populateDepreciationTransaction(AssetDepreciationTransaction depreciationTransaction, AssetPayment assetPayment, String transactionType, KualiDecimal transactionAmount, String plantCOA, String plantAccount, String accumulatedDepreciationFinancialObjectCode, String depreciationExpenseFinancialObjectCode, ObjectCode financialObject, SortedMap<String, AssetDepreciationTransaction> depreciationTransactionSummary) -  started");

        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "populateDepreciationTransaction(): populating AssetDepreciationTransaction pojo - Asset#:" + assetPayment.getCapitalAssetNumber());

        String financialObjectCode;
        if (transactionType.equals(KFSConstants.GL_CREDIT_CODE))
            financialObjectCode = accumulatedDepreciationFinancialObjectCode;
        else
            financialObjectCode = depreciationExpenseFinancialObjectCode;

        depreciationTransaction.setCapitalAssetNumber(assetPayment.getCapitalAssetNumber());
        depreciationTransaction.setChartOfAccountsCode(plantCOA);
        depreciationTransaction.setAccountNumber(plantAccount);
        depreciationTransaction.setSubAccountNumber(assetPayment.getSubAccountNumber());
        depreciationTransaction.setFinancialObjectCode(financialObjectCode);
        depreciationTransaction.setFinancialSubObjectCode(assetPayment.getFinancialSubObjectCode());
        depreciationTransaction.setFinancialObjectTypeCode(financialObject.getFinancialObjectTypeCode());
        depreciationTransaction.setTransactionType(transactionType);
        depreciationTransaction.setProjectCode(assetPayment.getProjectCode());

        depreciationTransaction.setTransactionAmount(transactionAmount);
        depreciationTransaction.setTransactionLedgerEntryDescription(CamsConstants.Depreciation.TRANSACTION_DESCRIPTION + assetPayment.getCapitalAssetNumber());

        String sKey = depreciationTransaction.getKey();

        // Grouping the asset transactions by asset#, accounts, sub account, object, transaction type (C/D), etc. in order to
        // only have one credit and one credit by group.
        if (depreciationTransactionSummary.containsKey(sKey)) {
            depreciationTransaction = depreciationTransactionSummary.get(sKey);
            depreciationTransaction.setTransactionAmount(depreciationTransaction.getTransactionAmount().add(transactionAmount));
        }
        depreciationTransactionSummary.put(sKey, (AssetDepreciationTransaction) depreciationTransaction.clone());
        LOG.debug("populateDepreciationTransaction(AssetDepreciationTransaction depreciationTransaction, AssetPayment assetPayment, String transactionType, KualiDecimal transactionAmount, String plantCOA, String plantAccount, String accumulatedDepreciationFinancialObjectCode, String depreciationExpenseFinancialObjectCode, ObjectCode financialObject, SortedMap<String, AssetDepreciationTransaction> depreciationTransactionSummary) -  ended");
    }

    /**
     * 
     * This method stores the depreciation transactions in the general pending entry table and creates a new documentHeader entry.
     * <p>
     * 
     * @param trans SortedMap with the transactions
     * @return none
     * 
     */
    private void processGeneralLedgerPendingEntry(SortedMap<String, AssetDepreciationTransaction> trans) {
        LOG.debug("populateExplicitGeneralLedgerPendingEntry(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper, GeneralLedgerPendingEntry) - start");

        String documentTypeCode;
        try {
            KualiWorkflowDocument workflowDocument = workflowDocumentService.createWorkflowDocument("AssetDepreciationDocument", GlobalVariables.getUserSession().getUniversalUser());

            // **************************************************************************************************
            // Create a new document header object
            // **************************************************************************************************
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Creating document header entry.");

            DocumentHeader documentHeader = new DocumentHeader();
            documentHeader.setWorkflowDocument(workflowDocument);
            documentHeader.setDocumentNumber(workflowDocument.getRouteHeaderId().toString());
            documentHeader.setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
            documentHeader.setExplanation(CamsConstants.Depreciation.DOCUMENT_DESCRIPTION);
            documentHeader.setFinancialDocumentDescription(CamsConstants.Depreciation.DOCUMENT_DESCRIPTION);
            documentHeader.setFinancialDocumentTotalAmount(new KualiDecimal(0));

            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Saving document header entry.");
            this.businessObjectService.save(documentHeader);
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Document Header entry was saved successfully.");
            // **************************************************************************************************

            this.documentNumber = documentHeader.getDocumentNumber();
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Document Number Created: " + this.documentNumber);

            // Getting depreciation document type code for the transactions
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting document type for depreciation.");
            documentTypeCode = documentTypeService.getDocumentTypeCodeByClass(AssetDepreciationDocument.class);
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Depreciation Document Type Code: " + documentTypeCode);

            Timestamp transactionTimestamp = new Timestamp(dateTimeService.getCurrentDate().getTime());

            GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper();
            for (Object key : trans.keySet()) {
                AssetDepreciationTransaction t = trans.get(key);

                if (t.getTransactionAmount().isNonZero()) {
                    LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Creating GLPE entries for asset:" + t.getCapitalAssetNumber());

                    GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();

                    explicitEntry.setFinancialSystemOriginationCode(homeOriginationService.getHomeOrigination().getFinSystemHomeOriginationCode());
                    explicitEntry.setDocumentNumber(documentHeader.getDocumentNumber());
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
                    explicitEntry.setFinancialDocumentTypeCode(documentTypeCode);
                    explicitEntry.setFinancialDocumentApprovedCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.YES);
                    explicitEntry.setVersionNumber(new Long(1));
                    explicitEntry.setTransactionEntryProcessedTs(new java.sql.Date(transactionTimestamp.getTime()));

                    LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Saving GLPE entries for asset:" + t.getCapitalAssetNumber());
                    this.generalLedgerPendingEntryService.save(explicitEntry);
                    LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "GLPE entries was saved successfully - asset:" + t.getCapitalAssetNumber());
                }
            }
        }
        catch (Exception e) {
            throw new IllegalStateException(kualiConfigurationService.getPropertyString(CamsKeyConstants.Depreciation.ERROR_WHEN_UPDATING_GL_PENDING_ENTRY_TABLE) + " :" + e.getMessage());
        }
        LOG.debug("populateExplicitGeneralLedgerPendingEntry(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper, GeneralLedgerPendingEntry) - end");
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

    public void setHomeOriginationService(HomeOriginationService homeOriginationService) {
        this.homeOriginationService = homeOriginationService;
    }

    public void setDocumentTypeService(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }
}