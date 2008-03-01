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
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.service.HomeOriginationService;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.bo.AssetDepreciationTransaction;
import org.kuali.module.cams.bo.DepreciableAssets;
import org.kuali.module.cams.dao.DepreciableAssetsDao;
import org.kuali.module.cams.document.AssetDepreciationDocument;
import org.kuali.module.cams.service.AssetDepreciationService;
import org.kuali.module.cams.service.ReportService;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.dao.UniversityDateDao;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AssetDepreciationServiceImpl implements AssetDepreciationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetDepreciationServiceImpl.class);
    private ParameterService                    parameterService;
    private ReportService                       reportService;
    private DepreciableAssetsDao                depreciableAssetsDao;
    private KualiConfigurationService           kualiConfigurationService;
    private GeneralLedgerPendingEntryService    generalLedgerPendingEntryService;
    
    private Integer fiscalYear;
    private Integer fiscalMonth;
    private String  documentNumber;
    private String  errorMsg = "";

    /**
     * 
     * @see org.kuali.module.cams.service.AssetDepreciationService#runDepreciation()
     */
    public void runDepreciation() {
        List<String[]> reportLog = new ArrayList<String[]>();        
        boolean error = false;
        UniversityDateDao universityDateDao = SpringContext.getBean(UniversityDateDao.class);
        UniversityDate universityDate;
        
        Calendar depreciationDate = Calendar.getInstance();
        Calendar currentDate = Calendar.getInstance();
        
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);

        String sDepreciationDate = null;

        try {
       /*     if (parameterService.parameterExists(ParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_DATE_PARAMETER)) {
                sDepreciationDate = ((List<String>)parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_DATE_PARAMETER)).get(0);
            } else {
                throw new IllegalStateException(kualiConfigurationService.getPropertyString(CamsKeyConstants.Depreciation.DEPRECIATION_DATE_PARAMETER_NOT_FOUND));
            }*/

            //sDepreciationDate = "2008-02-29";
            if (sDepreciationDate != null && !sDepreciationDate.trim().equals("")) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    depreciationDate.setTime(dateFormat.parse(sDepreciationDate));
                } catch (ParseException e) {
                     throw new IllegalArgumentException(kualiConfigurationService.getPropertyString(CamsKeyConstants.Depreciation.INVALID_DEPRECIATION_DATE_FORMAT));
                }
            }
            
            //*************************************************************************************************
            // If the depreciation date is not = to the system date then, the depreciation process cannot run.
            //*************************************************************************************************
            //LOG.info("*** sDepreciation Date:"+sDepreciationDate + " Depreciation Date:"+dateTimeService.toDateString(depreciationDate.getTime()));
            //LOG.info(currentDate.compareTo(depreciationDate));
            
//            if (currentDate.compareTo(depreciationDate) != 0)
//                return;
            
            
            //LOG.info("*** sDepreciation Date:"+sDepreciationDate + " Depreciation Date:"+dateTimeService.toDateString(depreciationDate.getTime()));                          
            
            universityDate = universityDateDao.getByPrimaryKey(depreciationDate.getTime());
            if (universityDate == null) {
                throw new IllegalStateException(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_UNIV_DATE_NOT_FOUND));
            }

            this.fiscalYear  = universityDate.getUniversityFiscalYear();
            this.fiscalMonth = new Integer(universityDate.getUniversityFiscalAccountingPeriod());

            reportLog.addAll(depreciableAssetsDao.checkSum(true, "", fiscalYear, fiscalMonth, depreciationDate));

            Collection<DepreciableAssets> depreciableAssetsCollection = depreciableAssetsDao.getListOfDepreciableAssets(this.fiscalYear, this.fiscalMonth, depreciationDate);
            
            List<DepreciableAssets> data = new ArrayList<DepreciableAssets>();

            if (depreciableAssetsCollection != null && !depreciableAssetsCollection.isEmpty()) {
                data = this.calculateDepreciation(depreciableAssetsCollection, depreciationDate);
                SortedMap<String, AssetDepreciationTransaction> depreciationTransactions = this.generateDepreciationTransaction(data);
                depreciableAssetsDao.updateAssetPayments(data, fiscalYear, fiscalMonth);
                processGeneralLedgerPendingEntry(depreciationTransactions);
            }
            else {
                throw new IllegalStateException(kualiConfigurationService.getPropertyString(CamsKeyConstants.Depreciation.NO_ELIGIBLE_FOR_DEPRECIATION_ASSETS_FOUND));
            }
        }
        catch (Exception e) {
            error = true;
            this.errorMsg = "Depreciation process ran unsucessfuly.\nReason:" + e.getMessage();
        }
        finally {
            if (!error)
                reportLog.addAll(depreciableAssetsDao.checkSum(false, this.documentNumber, fiscalYear, fiscalMonth, depreciationDate));

            reportService.generateDepreciationReport(reportLog, errorMsg, sDepreciationDate);
        }
    }

    /**
     * 
     * This method calculates the base amount adding the PrimaryDepreciationBaseAmount field of each asset
     * 
     * @param depreciableAssetsCollection collection of assets and assets payments that are eligible for depreciation
     * @return Map with the asset# and the base amount
     */
    private Map<Long, KualiDecimal> getBaseAmountOfAssets(Collection<DepreciableAssets> depreciableAssetsCollection) {
        Map<Long, KualiDecimal> assetsBaseAmount = new HashMap<Long, KualiDecimal>();
        Long assetNumber = new Long(0);
        KualiDecimal baseAmount = new KualiDecimal(0);

        try {
            int i = 0;
            for (DepreciableAssets depreciableAssets : depreciableAssetsCollection) {
                i++;
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
        return assetsBaseAmount;
    }

    /**
     * 
     * This method calculates the depreciation of each asset payment and gets the right plant account for each asset payment
     * depreciation
     * 
     * @param depreciableAssetsCollection
     */
    private List<DepreciableAssets> calculateDepreciation(Collection<DepreciableAssets> depreciableAssetsCollection, Calendar depreciationDate) {
        LOG.debug("calculateDepreciation() - start");
        List<DepreciableAssets> assetsInDepreciation    = new ArrayList<DepreciableAssets>();
        List<String> organizationPlantFundObjectSubType = new ArrayList<String>();
        List<String> campusPlantFundObjectSubType       = new ArrayList<String>();

        Double monthsElapsed;
        Double assetLifeInMonths;

        KualiDecimal depreciationPercentage = new KualiDecimal(0);

        KualiDecimal baseAmount;
        KualiDecimal acummulatedDepreciationAmount;

        Calendar assetServiceDate = Calendar.getInstance();

        try {
            if (parameterService.parameterExists(ParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_ORGANIZATON_PLANT_FUND_SUB_OBJECT_TYPES)) {
                organizationPlantFundObjectSubType = parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_ORGANIZATON_PLANT_FUND_SUB_OBJECT_TYPES);
            }

            if (parameterService.parameterExists(ParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPES)) {
                campusPlantFundObjectSubType = parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPES);
            }

            Map<Long, KualiDecimal> assetBaseAmounts = this.getBaseAmountOfAssets(depreciableAssetsCollection);

            for (DepreciableAssets d : depreciableAssetsCollection) {
                monthsElapsed= new Double(0);
                d.setTransactionType(KFSConstants.GL_DEBIT_CODE);

                assetServiceDate.setTime(d.getCapitalAssetInServiceDate());
                acummulatedDepreciationAmount = new KualiDecimal(0);
                baseAmount = assetBaseAmounts.get(d.getCapitalAssetNumber());
                assetLifeInMonths = new Double(d.getDepreciableLifeLimit() * 12);

                                              
                monthsElapsed = new Double(depreciationDate.get( Calendar.MONTH ) - assetServiceDate.get( Calendar.MONTH ) + ( depreciationDate.get( Calendar.YEAR ) - assetServiceDate.get( Calendar.YEAR ) ) * 12) + 1;                
                if (monthsElapsed.compareTo(assetLifeInMonths) >= 0) {
                    if (d.getPrimaryDepreciationMethodCode().equals(CamsConstants.DEPRECIATION_METHOD_STRAIGHT_LINE_CODE))
                        acummulatedDepreciationAmount = d.getPrimaryDepreciationBaseAmount();
                    else if (d.getPrimaryDepreciationMethodCode().equals(CamsConstants.DEPRECIATION_METHOD_SALVAGE_VALUE_CODE))
                        acummulatedDepreciationAmount = d.getPrimaryDepreciationBaseAmount().subtract((d.getPrimaryDepreciationBaseAmount().divide(baseAmount)).multiply(d.getSalvageAmount()));
                }
                else {
                    if (d.getPrimaryDepreciationMethodCode().equals(CamsConstants.DEPRECIATION_METHOD_STRAIGHT_LINE_CODE))
                        acummulatedDepreciationAmount = new KualiDecimal((monthsElapsed.doubleValue() / assetLifeInMonths.doubleValue()) * d.getPrimaryDepreciationBaseAmount().doubleValue());
                    else if (d.getPrimaryDepreciationMethodCode().equals(CamsConstants.DEPRECIATION_METHOD_SALVAGE_VALUE_CODE))
                        acummulatedDepreciationAmount = new KualiDecimal((monthsElapsed.doubleValue() / assetLifeInMonths.doubleValue()) * (d.getPrimaryDepreciationBaseAmount().subtract((d.getPrimaryDepreciationBaseAmount().divide(baseAmount)).multiply(d.getSalvageAmount()))).doubleValue());
                }
                d.setAccumulatedDepreciation(acummulatedDepreciationAmount);
                d.setTransactionAmount      (acummulatedDepreciationAmount.subtract(d.getAccumulatedPrimaryDepreciationAmount()));

                // LOG.info("**** Asset#: "+d.getCapitalAssetNumber()+" life:"+assetLifeInMonths + " base amt: "+d.getPrimaryDepreciationBaseAmount()+" Prim Depr:"+d.getAccumulatedPrimaryDepreciationAmount()+" Month Elapsed:"+monthsElapsed+ " Calculated Accum Depr:"+acummulatedDepreciationAmount+" Depr Amt:"+d.getTransactionAmount());
                
                if (d.getTransactionAmount().compareTo(new KualiDecimal(0)) == -1)
                    d.setTransactionType(KFSConstants.GL_CREDIT_CODE);

                // plant accounts
                if (organizationPlantFundObjectSubType.contains(d.getFinancialObjectSubTypeCode())) {
                    d.setPlantAccount(d.getOrganizationPlantAccountNumber());
                    d.setPlantCOA    (d.getOrganizationPlantChartCode());
                }
                else if (campusPlantFundObjectSubType.contains(d.getFinancialObjectSubTypeCode())) {
                    d.setPlantAccount(d.getCampusPlantAccountNumber());
                    d.setPlantCOA(d.getCampusPlantChartCode());
                }

                if (d.getTransactionAmount().compareTo(new KualiDecimal(0)) != 0 )
                        assetsInDepreciation.add(d);
            } // end for
        }
        catch (Exception e) {                 
            throw new IllegalStateException(kualiConfigurationService.getPropertyString(CamsKeyConstants.Depreciation.ERROR_WHEN_CALCULATING_DEPRECIATION) + " :" + e.getMessage());            
        }
        return assetsInDepreciation;
    }

    /**
     * 
     * This method generates the depreciation transactions for earch depreciated asset
     * 
     * @param assetsInDepreciation
     * @return SortedMap with the transactions that have to be stored in the gl_pending_entry_t table
     */
    private SortedMap<String, AssetDepreciationTransaction> generateDepreciationTransaction(List<DepreciableAssets> assetsInDepreciation) {
        LOG.debug("generateDepreciationTransaction() - start");
        SortedMap<String, AssetDepreciationTransaction> depreciationTransactionSummary = new TreeMap<String, AssetDepreciationTransaction>();
        AssetDepreciationTransaction depreciationTransaction = new AssetDepreciationTransaction();

        try {
            for (DepreciableAssets d : assetsInDepreciation) {
                depreciationTransaction = new AssetDepreciationTransaction();

                for (int x = 1; x <= 2; x++) {
                    if (d.getTransactionType().equals(KFSConstants.GL_CREDIT_CODE))
                        d.setFinancialObjectCode(d.getAccumulatedDepreciationFinancialObjectCode());
                    else
                        d.setFinancialObjectCode(d.getDepreciationExpenseFinancialObjectCode());

                    depreciationTransaction.setCapitalAssetNumber            (d.getCapitalAssetNumber());
                    depreciationTransaction.setFinancialSystemOriginationCode(d.getFinancialSystemOriginationCode());
                    depreciationTransaction.setDocumentNumber                (d.getDocumentNumber());
                    depreciationTransaction.setChartOfAccountsCode           (d.getPlantCOA());
                    depreciationTransaction.setAccountNumber                 (d.getPlantAccount());
                    depreciationTransaction.setSubAccountNumber              (d.getSubAccountNumber());
                    depreciationTransaction.setFinancialObjectCode           (d.getFinancialObjectCode());
                    depreciationTransaction.setFinancialSubObjectCode        (d.getFinancialSubObjectCode());
                    depreciationTransaction.setFinancialObjectTypeCode       (d.getFinancialObjectTypeCode());
                    depreciationTransaction.setTransactionType               (d.getTransactionType());
                    depreciationTransaction.setProjectCode                   (d.getProjectCode());

                    depreciationTransaction.setTransactionAmount                (d.getTransactionAmount());
                    depreciationTransaction.setTransactionLedgerEntryDescription(CamsConstants.Depreciation.TRANSACTION_DESCRIPTION + d.getCapitalAssetNumber());

                    String sKey = depreciationTransaction.getKey();

                    if (depreciationTransactionSummary.containsKey(sKey)) {
                        depreciationTransaction = depreciationTransactionSummary.get(sKey);
                        depreciationTransaction.setTransactionAmount(depreciationTransaction.getTransactionAmount().add(d.getTransactionAmount()));
                    }
                    depreciationTransactionSummary.put(sKey, (AssetDepreciationTransaction) depreciationTransaction.clone());

                    if (x == 1) {
                        if (d.getTransactionType().equals(KFSConstants.GL_CREDIT_CODE))
                            d.setTransactionType(KFSConstants.GL_DEBIT_CODE);
                        else
                            d.setTransactionType(KFSConstants.GL_CREDIT_CODE);
                    } // end if
                } // end for
            }
/*            Iterator i=depreciationTransactionSummary.keySet().iterator();
            while(i.hasNext()){
                String skey=(String)i.next(); 
                LOG.info("**** Key:"+skey+" - Transaction:"+depreciationTransactionSummary.get(skey).toString()); 
            }
*/        
        }
        catch (Exception e) {
            throw new IllegalStateException(kualiConfigurationService.getPropertyString(CamsKeyConstants.Depreciation.ERROR_WHEN_GENERATING_TRANSACTIONS) + " :" + e.getMessage());            
        }
        return depreciationTransactionSummary;
    }

    /**
     * 
     * This method creates a new documentHeader entry and several depreciation transactions in the general ledger pending
     * entry table
     * 
     * @param trans SortedMap with the transactions
     */
    private void processGeneralLedgerPendingEntry(SortedMap<String, AssetDepreciationTransaction> trans) {
        LOG.debug("populateExplicitGeneralLedgerPendingEntry(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper, GeneralLedgerPendingEntry) - start");

        String documentTypeCode;
        try {
            KualiWorkflowDocument workflowDocument = SpringContext.getBean(WorkflowDocumentService.class).createWorkflowDocument("AssetDepreciationDocument", GlobalVariables.getUserSession().getUniversalUser());

            // **************************************************************************************************
            // Create a new document header object
            // **************************************************************************************************
            DocumentHeader documentHeader = new DocumentHeader();
            documentHeader.setWorkflowDocument              (workflowDocument);
            documentHeader.setDocumentNumber                (workflowDocument.getRouteHeaderId().toString());
            documentHeader.setFinancialDocumentStatusCode   (KFSConstants.DocumentStatusCodes.APPROVED);
            documentHeader.setExplanation                   (CamsConstants.Depreciation.DOCUMENT_DESCRIPTION);
            documentHeader.setFinancialDocumentDescription  (CamsConstants.Depreciation.DOCUMENT_DESCRIPTION);
            documentHeader.setFinancialDocumentTotalAmount  (new KualiDecimal(0));
            SpringContext.getBean(BusinessObjectService.class).save(documentHeader);
            // **************************************************************************************************

            /*LOG.info("*****************************");
            LOG.info("*** DOCUMENT #:" + documentHeader.getDocumentNumber());
            LOG.info("*****************************");
*/
            this.documentNumber = documentHeader.getDocumentNumber();

            documentTypeCode = SpringContext.getBean(DocumentTypeService.class).getDocumentTypeCodeByClass(AssetDepreciationDocument.class);

            Timestamp transactionTimestamp = new Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());

            GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper();
            for (Object key : trans.keySet()) {
                AssetDepreciationTransaction t = trans.get(key);

                GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();

                explicitEntry.setFinancialSystemOriginationCode(SpringContext.getBean(HomeOriginationService.class).getHomeOrigination().getFinSystemHomeOriginationCode());
                explicitEntry.setDocumentNumber(documentHeader.getDocumentNumber());
                explicitEntry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));

                sequenceHelper.increment();

                explicitEntry.setChartOfAccountsCode    (t.getChartOfAccountsCode());
                explicitEntry.setAccountNumber          (t.getAccountNumber());
                explicitEntry.setSubAccountNumber       (t.getSubAccountNumber());
                explicitEntry.setFinancialObjectCode    (t.getFinancialObjectCode());
                explicitEntry.setFinancialSubObjectCode (t.getFinancialSubObjectCode());

                explicitEntry.setFinancialBalanceTypeCode         (BALANCE_TYPE_ACTUAL); 
                explicitEntry.setFinancialObjectTypeCode          (t.getFinancialObjectTypeCode());
                explicitEntry.setUniversityFiscalYear             (this.fiscalYear);
                explicitEntry.setUniversityFiscalPeriodCode       (StringUtils.leftPad(this.fiscalMonth.toString().trim(), 2, "0"));
                explicitEntry.setTransactionLedgerEntryDescription(t.getTransactionLedgerEntryDescription());
                explicitEntry.setTransactionLedgerEntryAmount     (t.getTransactionAmount().abs());
                explicitEntry.setTransactionDebitCreditCode       (t.getTransactionType());
                explicitEntry.setTransactionDate                  (new java.sql.Date(transactionTimestamp.getTime()));
                explicitEntry.setFinancialDocumentTypeCode        (documentTypeCode);
                explicitEntry.setFinancialDocumentApprovedCode    (GENERAL_LEDGER_PENDING_ENTRY_CODE.YES);
                explicitEntry.setVersionNumber                    (new Long(1));
                explicitEntry.setTransactionEntryProcessedTs(new java.sql.Date(transactionTimestamp.getTime()));
                
                generalLedgerPendingEntryService.save(explicitEntry);
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
}