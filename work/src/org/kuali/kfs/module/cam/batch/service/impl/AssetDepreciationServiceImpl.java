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
import static org.kuali.kfs.KFSConstants.BLANK_SPACE;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.service.HomeOriginationService;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.cams.batch.AssetDepreciationStep;
import org.kuali.module.cams.bo.DepreciableAssets;
import org.kuali.module.cams.bo.DepreciationTransaction;
import org.kuali.module.cams.dao.DepreciableAssetsDao;
import org.kuali.module.cams.service.AssetDepreciationService;
import org.kuali.module.cams.service.ReportService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.service.SufficientFundsService;
import org.kuali.kfs.KFSConstants;
import org.springframework.transaction.annotation.Transactional;

import sun.util.calendar.BaseCalendar.Date;

@Transactional
public class AssetDepreciationServiceImpl implements AssetDepreciationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetDepreciationServiceImpl.class);
    private ParameterService            parameterService;
    private ReportService               reportService;
    private DepreciableAssetsDao        depreciableAssetsDao;    
    private KualiConfigurationService   kualiConfigurationService;
    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    private Integer fiscalYear;
    private Integer fiscalMonth;

    private int paymentsElibigleForDepreciationProcessed=0;

    private KualiDecimal debits  = new KualiDecimal(0);
    private KualiDecimal credits = new KualiDecimal(0);

    private List<String[]> reportLog = new ArrayList<String[]>();
    private String errorMsg="";
    
    public static final String NO_ELIGIBLE_FOR_DEPRECIATION_ASSETS_FOUND ="There are not assets eligible for depreciation.";
    public static final String ERROR_WHEN_CALCULATING_BASE_AMOUNT        ="An error occurred when calculating assets base amount";        
    public static final String ERROR_WHEN_CALCULATING_DEPRECIATION       ="An error occurred when calculating assets depreciation";
    public static final String ERROR_WHEN_GENERATING_TRANSACTIONS        ="An error occurred when generating transactions";
    public static final String ERROR_WHEN_UPDATING_GL_PENDING_ENTRY_TABLE="An error occurred when updating general pending entry table";
        
    public void runDepreciation(){
        boolean error=false;
        try { 
            UniversityDate universityDate = SpringContext.getBean(UniversityDateService.class).getCurrentUniversityDate();
            this.fiscalYear = universityDate.getUniversityFiscalYear();
            this.fiscalMonth= new Integer(universityDate.getUniversityFiscalAccountingPeriod());            

            //this.fiscalYear = new Integer(2007);
            //this.fiscalMonth= new Integer(10);

            LOG.info("*****Year:"+this.fiscalYear);
            LOG.info("*****month:"+this.fiscalMonth);

            depreciableAssetsDao.initDepreciation(this.fiscalYear,this.fiscalMonth);

            Collection<DepreciableAssets> depreciableAssetsCollection = depreciableAssetsDao.getListOfDepreciableAssets();

            List<DepreciableAssets> data = new ArrayList<DepreciableAssets>();

            if (depreciableAssetsCollection != null && !depreciableAssetsCollection.isEmpty()) {
                data = this.calculateDepreciation(depreciableAssetsCollection);             
                Map<Object,DepreciationTransaction> depreciationTransactions=this.generateDepreciationTransaction(data);
                depreciableAssetsDao.updateAssetPayments(data);
                processGeneralLedgerPendingEntry(depreciationTransactions);                
            } else {
                throw new RuntimeException(NO_ELIGIBLE_FOR_DEPRECIATION_ASSETS_FOUND); 
            }

        } catch (Exception e) {
            error=true;
            this.errorMsg = "Depreciation process ran unsucessfuly.\nReason:"+e.getMessage();
            throw new RuntimeException(errorMsg);             
        } finally {
            if (!error)
                depreciableAssetsDao.checkSum(false);
            
            this.reportLog.addAll(depreciableAssetsDao.getReportLine());
            reportService.generateDepreciationReport(reportLog,errorMsg);       
        }
        // After the process is complete, we need to run and get the second section of the report. 
    }    

    /**
     * 
     * This method calculates the base amount adding the PrimaryDepreciationBaseAmount field of each asset   
     * @param depreciableAssetsCollection collection of assets and assets payments that are eligible for depreciation 
     * @return Map with the asset# and the base amount
     */
    private Map<Long,KualiDecimal> getBaseAmountOfAssets(Collection<DepreciableAssets> depreciableAssetsCollection) {
        Map <Long,KualiDecimal> assetsBaseAmount = new HashMap<Long,KualiDecimal>();
        Long         assetNumber    = new Long(0);
        KualiDecimal baseAmount     = new KualiDecimal(0);

        try {
            int i=0;
            for( DepreciableAssets depreciableAssets : depreciableAssetsCollection){
                i++;
                if (assetNumber.compareTo(new Long(0))== 0)
                    assetNumber = depreciableAssets.getCapitalAssetNumber();

                if (depreciableAssets.getCapitalAssetNumber().compareTo(assetNumber) == 0) {
                    baseAmount = baseAmount.add(depreciableAssets.getPrimaryDepreciationBaseAmount());
                }
                if ( (depreciableAssets.getCapitalAssetNumber().compareTo(assetNumber) < 0) || (i == depreciableAssetsCollection.size())) {
                    assetsBaseAmount.put(assetNumber, baseAmount);
                    baseAmount = new KualiDecimal(0);
                }            
                assetNumber = depreciableAssets.getCapitalAssetNumber();
            }

        } catch(Exception e) {
            throw new RuntimeException( ERROR_WHEN_CALCULATING_BASE_AMOUNT+" :"+e.getMessage());             
        }
        return assetsBaseAmount;
    }

    /**
     * 
     * This method ...
     * @param depreciableAssetsCollection
     */
    private List<DepreciableAssets> calculateDepreciation(Collection<DepreciableAssets> depreciableAssetsCollection) {
        LOG.debug("calculateDepreciation() - start");
        List<DepreciableAssets> assetsInDepreciation = new ArrayList<DepreciableAssets>(); 
        List<String> organizationPlantFundObjectSubType  = new ArrayList<String>();        
        List<String> campusPlantFundObjectSubType        = new ArrayList<String>();

        Double monthsElapsed;
        Double assetLifeInMonths;

        KualiDecimal depreciationPercentage=new KualiDecimal(0);

        KualiDecimal baseAmount;
        KualiDecimal acummulatedDepreciationAmount;

        //**********************************************************************************
        //   this needs to be modify because needs to receive a date from the batch job.
        //**********************************************************************************
        Calendar depreciationDate = Calendar.getInstance(); 
        //**********************************************************************************


        Calendar asssetServiceDate= Calendar.getInstance();

        try {
            if (parameterService.parameterExists(ParameterConstants.CAPITAL_ASSETS_BATCH.class,CamsConstants.Parameters.NON_DEPRECIABLE_ORGANIZATON_PLANT_FUND_SUB_OBJECT_TYPES)){
                organizationPlantFundObjectSubType = parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSETS_BATCH.class,CamsConstants.Parameters.NON_DEPRECIABLE_ORGANIZATON_PLANT_FUND_SUB_OBJECT_TYPES);
            }

            if (parameterService.parameterExists(ParameterConstants.CAPITAL_ASSETS_BATCH.class,CamsConstants.Parameters.NON_DEPRECIABLE_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPES)){
                campusPlantFundObjectSubType= parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSETS_BATCH.class,CamsConstants.Parameters.NON_DEPRECIABLE_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPES);
            }    

            Map<Long,KualiDecimal> assetBaseAmounts = this.getBaseAmountOfAssets(depreciableAssetsCollection);  

            for (DepreciableAssets d : depreciableAssetsCollection) {
                d.setTransactionType(KFSConstants.GL_DEBIT_CODE);

                asssetServiceDate.setTime(d.getCapitalAssetInServiceDate());

                acummulatedDepreciationAmount  = new KualiDecimal(0);
                baseAmount          = assetBaseAmounts.get(d.getCapitalAssetNumber()); 
                assetLifeInMonths   = new Double(d.getDepreciableLifeLimit() * 12);

                long diffDays = ((depreciationDate.getTime().getTime() - asssetServiceDate.getTime().getTime())/(1000*60*60*24));        
                monthsElapsed = new Double((diffDays / 30) + 1);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");             
                LOG.info("%:"+monthsElapsed.doubleValue()/assetLifeInMonths.doubleValue()+"****life in Months:"+assetLifeInMonths + " - Month Elapsed:"+monthsElapsed+" Service Date:"+sdf.format(d.getCapitalAssetInServiceDate())+" d.getPrimaryDepreciationBaseAmount():"+d.getPrimaryDepreciationBaseAmount()+" Accum Depre:"+d.getAccumulatedPrimaryDepreciationAmount());

                //**************************
                // DELETE THESE LINES!!!!
                //************************
                //monthsElapsed = new Double(98);
                //d.setPrimaryDepreciationMethodCode("SV");
                //********************************************
                if (monthsElapsed.compareTo(assetLifeInMonths) >= 0) {
                    if (d.getPrimaryDepreciationMethodCode().equals(CamsConstants.DEPRECIATION_METHOD_STRAIGHT_LINE_CODE)) {
                        //LOG.info("***SV-1");                    
                        acummulatedDepreciationAmount = d.getPrimaryDepreciationBaseAmount();                    
                    } else if (d.getPrimaryDepreciationMethodCode().equals(CamsConstants.DEPRECIATION_METHOD_SALVAGE_VALUE_CODE)) {
                        //LOG.info("***SL-2");                    
                        acummulatedDepreciationAmount = d.getPrimaryDepreciationBaseAmount().subtract( (d.getPrimaryDepreciationBaseAmount().divide(baseAmount)).multiply(d.getSalvageAmount()));                    
                    }
                } else {
                    if (d.getPrimaryDepreciationMethodCode().equals(CamsConstants.DEPRECIATION_METHOD_STRAIGHT_LINE_CODE)){
                        //LOG.info("***SL-1");
                        acummulatedDepreciationAmount = new KualiDecimal( (monthsElapsed.doubleValue()/assetLifeInMonths.doubleValue()) * d.getPrimaryDepreciationBaseAmount().doubleValue());
                    } else if (d.getPrimaryDepreciationMethodCode().equals(CamsConstants.DEPRECIATION_METHOD_SALVAGE_VALUE_CODE)) {
                        acummulatedDepreciationAmount = new KualiDecimal( 
                                (monthsElapsed.doubleValue()/assetLifeInMonths.doubleValue()) * 
                                ( d.getPrimaryDepreciationBaseAmount().subtract( ( d.getPrimaryDepreciationBaseAmount().divide(baseAmount)).multiply(d.getSalvageAmount()))).doubleValue());
                        //LOG.info("***SL-2");
                    }
                }
                //LOG.info("**** Accum Depreciation Amount:"+acummulatedDepreciationAmount);
                d.setAccumulatedDepreciation(acummulatedDepreciationAmount);

                d.setTransactionAmount(acummulatedDepreciationAmount.subtract(d.getAccumulatedPrimaryDepreciationAmount()));

                if (d.getTransactionAmount().compareTo(new KualiDecimal(0)) == -1){
                    d.setTransactionType(KFSConstants.GL_CREDIT_CODE);
                    d.setTransactionAmount(d.getTransactionAmount().abs());
                    this.credits.add(d.getTransactionAmount());
                } else {
                    this.debits.add(d.getTransactionAmount());
                }

                //plant accounts
                if (organizationPlantFundObjectSubType.contains(d.getFinancialObjectSubTypeCode())) {
                    d.setPlantAccount(d.getOrganizationPlantAccountNumber());                
                    d.setPlantCOA    (d.getOrganizationPlantChartCode());                
                } else if (campusPlantFundObjectSubType.contains(d.getFinancialObjectSubTypeCode())) {
                    d.setPlantAccount(d.getCampusPlantAccountNumber());
                    d.setPlantCOA    (d.getCampusPlantChartCode());               
                }

                //LOG.info("**** XXXXDepreciation:"+d.toString());            
                assetsInDepreciation.add(d);
                this.paymentsElibigleForDepreciationProcessed++;
            } // end for
        } catch(Exception e) {
            throw new RuntimeException( ERROR_WHEN_CALCULATING_DEPRECIATION+" :"+e.getMessage());             
        }
        return assetsInDepreciation;
    }

    /**
     * 
     * This method generates the depreciation transactions for earch depreciated asset
     * @param assetsInDepreciation
     * @return
     */
    private Map<Object,DepreciationTransaction> generateDepreciationTransaction(List<DepreciableAssets> assetsInDepreciation) {
        LOG.debug("generateDepreciationTransaction() - start");        
        Map <Object,DepreciationTransaction> depreciationTransactionSummary = new HashMap<Object,DepreciationTransaction>();
        DepreciationTransaction depreciationTransaction = new DepreciationTransaction();

        try {
            for (DepreciableAssets d : assetsInDepreciation) {
                depreciationTransaction = new DepreciationTransaction();

                for (int x=1;x<=2;x++) {
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

                    depreciationTransaction.setTransactionAmount             (d.getTransactionAmount());
                    depreciationTransaction.setTransactionLedgerEntryDescription("Batch Depreciation Asset " + d.getCapitalAssetNumber());

                    String sKey = depreciationTransaction.getKey();

                    if (depreciationTransactionSummary.containsKey(sKey)) {
                        depreciationTransaction =depreciationTransactionSummary.get(sKey);
                        depreciationTransaction.setTransactionAmount(depreciationTransaction.getTransactionAmount().add(d.getTransactionAmount()));
                    }
                    depreciationTransactionSummary.put(sKey, (DepreciationTransaction)depreciationTransaction.clone());

                    if (x == 1) {
                        if (d.getTransactionType().equals(KFSConstants.GL_CREDIT_CODE))
                            d.setTransactionType(KFSConstants.GL_DEBIT_CODE);
                        else
                            d.setTransactionType(KFSConstants.GL_CREDIT_CODE);
                    } // end if

                } // end for           
            }   
            for(DepreciationTransaction d:depreciationTransactionSummary.values()) {
                LOG.info("*****CAMS Transaction:"+d.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException( ERROR_WHEN_GENERATING_TRANSACTIONS+" :"+e.getLocalizedMessage());
        }
        return depreciationTransactionSummary;        
    }

    /**
     * 
     * This method...
     * @param trans
     */
    private void processGeneralLedgerPendingEntry(Map<Object,DepreciationTransaction> trans) {
        LOG.debug("populateExplicitGeneralLedgerPendingEntry(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper, GeneralLedgerPendingEntry) - start");

        try {
            Timestamp transactionTimestamp = new Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());

            GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper();        
            for(Object key : trans.keySet()){    
                DepreciationTransaction t = trans.get(key);

                GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();

                explicitEntry.setFinancialSystemOriginationCode(SpringContext.getBean(HomeOriginationService.class).getHomeOrigination().getFinSystemHomeOriginationCode());
                //LOG.debug("Document Number:"+explicitEntry.getaccountingLine.getDocumentNumber();
                explicitEntry.setDocumentNumber("1000");
                explicitEntry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));

                sequenceHelper.increment();

                explicitEntry.setChartOfAccountsCode     (t.getChartOfAccountsCode());
                explicitEntry.setAccountNumber           (t.getAccountNumber());
                explicitEntry.setSubAccountNumber        (t.getSubAccountNumber());
                explicitEntry.setFinancialObjectCode     (t.getFinancialObjectCode());
                explicitEntry.setFinancialSubObjectCode  (t.getFinancialSubObjectCode());

                explicitEntry.setFinancialBalanceTypeCode         (BALANCE_TYPE_ACTUAL); // this is the default that most documents use        
                explicitEntry.setFinancialObjectTypeCode          (t.getFinancialObjectTypeCode());
                explicitEntry.setUniversityFiscalYear             (this.fiscalYear);
                explicitEntry.setUniversityFiscalPeriodCode       (StringUtils.leftPad(this.fiscalMonth.toString().trim(),2,"0")); 
                explicitEntry.setTransactionLedgerEntryDescription(t.getTransactionLedgerEntryDescription());
                explicitEntry.setTransactionLedgerEntryAmount     (t.getTransactionAmount());
                explicitEntry.setTransactionDebitCreditCode       (t.getTransactionType());
                explicitEntry.setTransactionDate                  (new java.sql.Date(transactionTimestamp.getTime()));
                explicitEntry.setFinancialDocumentTypeCode        ("DEPR");
                //SpringContext.getBean(DocumentTypeService.class).getDocumentTypeCodeByClass(accountingDocument.getClass()));
                explicitEntry.setFinancialDocumentApprovedCode    (GENERAL_LEDGER_PENDING_ENTRY_CODE.YES);
                explicitEntry.setVersionNumber                    (new Long(1));

                //explicitEntry.setTransactionEntryProcessedTs(new java.sql.Date(transactionTimestamp.getTime()));
                //generalLedgerPendingEntryService.save(explicitEntry);
            }
        }catch (Exception e) {
            throw new RuntimeException( ERROR_WHEN_UPDATING_GL_PENDING_ENTRY_TABLE+" :"+e.getMessage());
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