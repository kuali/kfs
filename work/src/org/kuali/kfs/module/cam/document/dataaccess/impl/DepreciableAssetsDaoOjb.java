/*
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
package org.kuali.module.cams.dao.ojb;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.service.OptionsService;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetObjectCode;
import org.kuali.module.cams.bo.AssetPayment;
import org.kuali.module.cams.bo.AssetRetirementGlobalDetail;
import org.kuali.module.cams.dao.DepreciableAssetsDao;
import org.kuali.module.cams.document.AssetTransferDocument;
import org.kuali.module.chart.bo.Account;

public class DepreciableAssetsDaoOjb extends PlatformAwareDaoBaseOjb implements DepreciableAssetsDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DepreciableAssetsDaoOjb.class);
    private KualiConfigurationService kualiConfigurationService;
    private ParameterService parameterService;
    private DateTimeService dateTimeService;
    private OptionsService optionsService;
    private BusinessObjectService businessObjectService;

    private Criteria assetCriteria = new Criteria();
    private String errorMsg = new String();

    private final static String PAYMENT_TO_ASSET_REFERENCE_DESCRIPTOR = "asset.";
    private final static String PAYMENT_TO_OBJECT_REFERENCE_DESCRIPTOR = "financialObject.";
    private final static String ASSET_TO_ASSET_TYPE_REFERENCE_DESCRIPTOR = "asset.capitalAssetType.";
    private final static String[] REPORT_GROUP = { "*** BEFORE RUNNING DEPRECIATION PROCESS ****", "*** AFTER RUNNING DEPRECIATION PROCESS ****" };

    /**
     * 
     * @see org.kuali.module.cams.dao.CamsDepreciableAssetsDao#getListOfDepreciableAssets()
     */
    public Collection<AssetPayment> getListOfDepreciableAssets(Integer fiscalYear, Integer fiscalMonth, Calendar depreciationDate) {
        LOG.debug("getListOfDepreciableAssets() -  started");

        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Retreving eligible asset payments.");

        QueryByCriteria q = QueryFactory.newQuery(AssetPayment.class, this.getDepreciationCriteria(fiscalYear, fiscalMonth, depreciationDate, false));
        q.addOrderByAscending(CamsPropertyConstants.AssetPayment.CAPITAL_ASSET_NUMBER);
        q.addOrderByAscending(CamsPropertyConstants.AssetPayment.ORIGINATION_CODE);
        q.addOrderByAscending(CamsPropertyConstants.AssetPayment.ACCOUNT_NUMBER);
        q.addOrderByAscending(CamsPropertyConstants.AssetPayment.SUB_ACCOUNT_NUMBER);
        q.addOrderByAscending(CamsPropertyConstants.AssetPayment.OBJECT_CODE);
        q.addOrderByAscending(CamsPropertyConstants.AssetPayment.SUB_OBJECT_CODE);
        q.addOrderByAscending(CamsPropertyConstants.AssetPayment.OBJECT_TYPE_CODE);
        q.addOrderByAscending(CamsPropertyConstants.AssetPayment.PROJECT_CODE);
        Collection<AssetPayment> iter = getPersistenceBrokerTemplate().getCollectionByQuery(q);

        return iter;
    }


    /**
     * 
     * @see org.kuali.module.cams.dao.CamsDepreciableAssetsDao#updateAssetPayments(java.util.List)
     */
    public void initializeAssetPayment(Integer fiscalMonth) {
        LOG.debug("initializeAssetPayments() -  started");

        Criteria criteria = new Criteria();
        Collection<AssetPayment> assetPayments;

        // If we are in the first month of the fiscal year, then add the previous year depreciation amounts into previous year
        // depreciation field.
        if (fiscalMonth == 1) {
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Fiscal month = 1. Therefore, initializing each month with zeros.");

            criteria.addNotNull(CamsPropertyConstants.AssetPayment.CAPITAL_ASSET_NUMBER);
            QueryByCriteria q = QueryFactory.newQuery(AssetPayment.class, criteria);
            assetPayments = getPersistenceBrokerTemplate().getCollectionByQuery(q);

            for (AssetPayment assetPayment : assetPayments) {
                if (assetPayment != null) {
                    assetPayment.setPreviousYearPrimaryDepreciationAmount(assetPayment.getPeriod1Depreciation1Amount().add(assetPayment.getPeriod2Depreciation1Amount()).add(assetPayment.getPeriod3Depreciation1Amount()).add(assetPayment.getPeriod4Depreciation1Amount()).add(assetPayment.getPeriod5Depreciation1Amount()).add(assetPayment.getPeriod6Depreciation1Amount()).add(assetPayment.getPeriod7Depreciation1Amount()).add(assetPayment.getPeriod8Depreciation1Amount()).add(assetPayment.getPeriod9Depreciation1Amount()).add(assetPayment.getPeriod10Depreciation1Amount()).add(assetPayment.getPeriod11Depreciation1Amount()).add(assetPayment.getPeriod12Depreciation1Amount()));

                    assetPayment.setPeriod1Depreciation1Amount(new KualiDecimal(0));
                    assetPayment.setPeriod2Depreciation1Amount(new KualiDecimal(0));
                    assetPayment.setPeriod3Depreciation1Amount(new KualiDecimal(0));
                    assetPayment.setPeriod4Depreciation1Amount(new KualiDecimal(0));
                    assetPayment.setPeriod5Depreciation1Amount(new KualiDecimal(0));
                    assetPayment.setPeriod6Depreciation1Amount(new KualiDecimal(0));
                    assetPayment.setPeriod7Depreciation1Amount(new KualiDecimal(0));
                    assetPayment.setPeriod8Depreciation1Amount(new KualiDecimal(0));
                    assetPayment.setPeriod9Depreciation1Amount(new KualiDecimal(0));
                    assetPayment.setPeriod10Depreciation1Amount(new KualiDecimal(0));
                    assetPayment.setPeriod11Depreciation1Amount(new KualiDecimal(0));
                    assetPayment.setPeriod12Depreciation1Amount(new KualiDecimal(0));

                    LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Initializing asset payment - Asset:" + assetPayment.getCapitalAssetNumber() + " - Sequence#:" + assetPayment.getPaymentSequenceNumber());
                    getPersistenceBrokerTemplate().store(assetPayment);
                    LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Initializing asset payment - Asset:" + assetPayment.getCapitalAssetNumber() + " - Sequence#:" + assetPayment.getPaymentSequenceNumber() + " was successfully saved.");
                }
            }
        }
        LOG.debug("initializeAssetPayments() -  ended");
    }

    /**
     * 
     * @see org.kuali.module.cams.dao.CamsDepreciableAssetsDao#updateAssetPayments(java.util.List)
     */
    public void updateAssetPayments(String capitalAssetNumber, String paymentSequenceNumber, KualiDecimal transactionAmount, KualiDecimal accumulatedDepreciationAmount, Integer fiscalMonth) {
        LOG.debug("updateAssetPayments() -  started");

        HashMap<String, Object> keys = new HashMap<String, Object>();
        keys.put(CamsPropertyConstants.AssetPayment.CAPITAL_ASSET_NUMBER, capitalAssetNumber);
        keys.put(CamsPropertyConstants.AssetPayment.PAYMENT_SEQ_NUMBER, paymentSequenceNumber);

        AssetPayment assetPayment = (AssetPayment) businessObjectService.findByPrimaryKey(AssetPayment.class, keys);

        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Updating depreciation amount - Asset:" + assetPayment.getCapitalAssetNumber() + " - Sequence#:" + assetPayment.getPaymentSequenceNumber());

        assetPayment.setAccumulatedPrimaryDepreciationAmount(accumulatedDepreciationAmount);

        /*
         * try { String setterMethodName="setPeriod"+fiscalMonth+"Depreciation1Amount"; LOG.debug("Invoking method
         * :"+setterMethodName+" in class AssetPayment."); Method method = AssetPayment.class.getMethod(setterMethodName, new
         * Class[] { KualiDecimal.class }); method.invoke(assetPayment, new Object[] {transactionAmount}); } catch (Exception re) {
         * LOG.info("***Reflection error:"+re.getMessage()); throw re; }
         */

        if (fiscalMonth == 1)
            assetPayment.setPeriod1Depreciation1Amount(transactionAmount);
        else if (fiscalMonth == 2)
            assetPayment.setPeriod2Depreciation1Amount(transactionAmount);
        else if (fiscalMonth == 3)
            assetPayment.setPeriod3Depreciation1Amount(transactionAmount);
        else if (fiscalMonth == 4)
            assetPayment.setPeriod4Depreciation1Amount(transactionAmount);
        else if (fiscalMonth == 5)
            assetPayment.setPeriod5Depreciation1Amount(transactionAmount);
        else if (fiscalMonth == 6)
            assetPayment.setPeriod6Depreciation1Amount(transactionAmount);
        else if (fiscalMonth == 7)
            assetPayment.setPeriod7Depreciation1Amount(transactionAmount);
        else if (fiscalMonth == 8)
            assetPayment.setPeriod8Depreciation1Amount(transactionAmount);
        else if (fiscalMonth == 9)
            assetPayment.setPeriod9Depreciation1Amount(transactionAmount);
        else if (fiscalMonth == 10)
            assetPayment.setPeriod10Depreciation1Amount(transactionAmount);
        else if (fiscalMonth == 11)
            assetPayment.setPeriod11Depreciation1Amount(transactionAmount);
        else if (fiscalMonth == 12)
            assetPayment.setPeriod12Depreciation1Amount(transactionAmount);

        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Saving depreciation amount - Asset:" + assetPayment.getCapitalAssetNumber() + " - Sequence#:" + assetPayment.getPaymentSequenceNumber());
        businessObjectService.save(assetPayment);
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Depreciation amount - Asset:" + assetPayment.getCapitalAssetNumber() + " - Sequence#:" + assetPayment.getPaymentSequenceNumber() + " was saved successfully.");

        LOG.debug("updateAssetPayments() -  ended");
    }


    /**
     * 
     * This method generates a sub query that will retrieve all the assets with pending transfers and pending retriments
     * 
     * @return
     */
    private List<String> getAssetsWithPendingAssetDocuments() {
        LOG.debug("getAssetsWithPendingAssetDocuments() -  started");
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Creating subqueries for assets with pending transfers and retirement documents.");

        Object[] fieldValue;
        
        List<String> notPendingDocStatuses = new ArrayList<String>();
        notPendingDocStatuses.add(CamsConstants.NotPendingDocumentStatuses.APPROVED);
        notPendingDocStatuses.add(CamsConstants.NotPendingDocumentStatuses.CANCELED);

        List<String> capitalAssetNumbers = new ArrayList<String>();

        Criteria criteria = new Criteria();

        // Retired assets sub query
        ReportQueryByCriteria arSubQuery;
        criteria.addNotIn(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_STATUS_CODE, notPendingDocStatuses);
        ReportQueryByCriteria q = QueryFactory.newReportQuery(AssetRetirementGlobalDetail.class, criteria);
        q.setAttributes(new String[] { CamsPropertyConstants.AssetRetirementGlobalDetail.CAPITAL_ASSET_NUMBER });

        Iterator<Object> i= getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
        while (i.hasNext()) {
            fieldValue = (Object[]) i.next();
            if (fieldValue[0] != null) {
                capitalAssetNumbers.add(convertCountValueToString(fieldValue[0]));
            }
        }
        // transferred assets sub query

        criteria = new Criteria();
        criteria.addNotIn(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_STATUS_CODE, notPendingDocStatuses);

        q = QueryFactory.newReportQuery(AssetTransferDocument.class, criteria);
        q.setAttributes(new String[] { CamsPropertyConstants.AssetTransferDocument.CAPITAL_ASSET_NUMBER });

        i= getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
        
        while (i.hasNext()) {
            fieldValue = (Object[]) i.next();
            if (fieldValue[0] != null) {
                capitalAssetNumbers.add(convertCountValueToString(fieldValue[0]));             
            }
        }
        
        LOG.debug("getAssetsWithPendingAssetDocuments() -  Ended");
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Finished done creating subqueries for assets with pending transfers and retirement documents.");
        return capitalAssetNumbers;
    }


    /**
     * 
     * This method returns the number of assets with pending transfers or retirements
     * 
     * @return Object
     */
    private Object getNumberOfAssetsBeingRetiredAndTransferred() {
        LOG.debug("getNumberOfAssetsBeingRetiredAndTransferred() -  Started");
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting the number of assets being retired or transferred.");

        Object result=null;
        Long lCount = new Long(0);
        BigDecimal bdCount = new BigDecimal(0);
        
        List<String> notPendingDocStatuses = new ArrayList<String>();
        notPendingDocStatuses.add(CamsConstants.NotPendingDocumentStatuses.APPROVED);
        notPendingDocStatuses.add(CamsConstants.NotPendingDocumentStatuses.CANCELED);

        List<String> excludedAssets = new ArrayList<String>();

        //Criteria arCriteria = new Criteria();
        Criteria criteria = new Criteria();
        criteria.addNotIn(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_STATUS_CODE, notPendingDocStatuses);

        // Retired assets sub query
        ReportQueryByCriteria q = QueryFactory.newReportQuery(AssetTransferDocument.class, criteria);
        q.setAttributes(new String[] { "count(distinct " + CamsPropertyConstants.AssetTransferDocument.CAPITAL_ASSET_NUMBER + ")" });
        Iterator<Object> i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
        
        Object[] data1 = (Object[]) i.next();
        if (data1[0] instanceof BigDecimal)
            bdCount=bdCount.add((BigDecimal)data1[0]);
        else
            lCount=lCount+(Long)data1[0];
            
        // transferred assets sub query
        q = QueryFactory.newReportQuery(AssetTransferDocument.class, criteria);
        q.setAttributes(new String[] { "count(distinct " + CamsPropertyConstants.AssetTransferDocument.CAPITAL_ASSET_NUMBER + ")" });
        i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);

        Object[] data2 = (Object[]) i.next();
        if (data2[0] instanceof BigDecimal) {
            bdCount=bdCount.add((BigDecimal)data2[0]);
        } else {
            lCount=lCount+(Long)data2[0];
        }
     
        if (bdCount.compareTo(new BigDecimal(0)) != 0)
            result=(Object)bdCount;
        
        if (lCount.compareTo(new Long(0)) != 0)
            result=(Object)lCount;
        
        LOG.debug("getNumberOfAssetsBeingRetiredAndTransferred() -  Ended");
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Finished getting the number of assets being retired or transferred.");        
        return result;
    }


    /**
     * 
     * This method creates the criteria that the program uses in order to retrieve the asset payments eligible for depreciation
     * 
     * @param fiscalYear
     * @param fiscalMonth
     * @param depreciationDate
     * @param federallyOwnedCriteria true=> Only include federally owned assets in criteria, false=>Exclude federally owned assets
     *        from criteria
     * @return
     */
    private Criteria getDepreciationCriteria(Integer fiscalYear, Integer fiscalMonth, Calendar depreciationDate, boolean federallyOwnedCriteria) {
        LOG.debug("createDepreciationCriteria() -  started");

        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Creating criteria for asset payments - Include federally owned? " + federallyOwnedCriteria);

        List<String> assetTransferCode = new ArrayList<String>();
        List<String> depreciationMethodList = new ArrayList<String>();
        List<String> notAcceptedAssetStatus = new ArrayList<String>();

        assetTransferCode.add(CamsConstants.TRANSFER_PAYMENT_CODE_N);
        assetTransferCode.add(" ");

        depreciationMethodList.add(CamsConstants.DEPRECIATION_METHOD_SALVAGE_VALUE_CODE);
        depreciationMethodList.add(CamsConstants.DEPRECIATION_METHOD_STRAIGHT_LINE_CODE);

        List<String> federallyOwnedObjectSubTypes = getFederallyOwnedObjectSubTypes();

        if (parameterService.parameterExists(ParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_NON_CAPITAL_ASSETS_STATUS_CODES)) {
            notAcceptedAssetStatus = parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_NON_CAPITAL_ASSETS_STATUS_CODES);
        }

        Criteria criteria = new Criteria();
        Criteria criteriaB = new Criteria();
        Criteria criteriaC = new Criteria();

        criteria.addEqualTo(CamsPropertyConstants.AssetPayment.ORIGINATION_CODE, CamsConstants.Depreciation.DEPRECIATION_ORIGINATION_CODE);

        // Begin ************
        criteriaB = new Criteria();
        criteriaB.addNotNull(CamsPropertyConstants.AssetPayment.PRIMARY_DEPRECIATION_BASE_AMOUNT);

        criteriaC = new Criteria();
        criteriaC.addNotEqualTo(CamsPropertyConstants.AssetPayment.PRIMARY_DEPRECIATION_BASE_AMOUNT, 0);

        criteriaB.addOrCriteria(criteriaC);
        criteria.addAndCriteria(criteriaB);
        // End ***************

        // Begin *******************************************************************
        criteriaB = new Criteria();
        criteriaB.addIn(CamsPropertyConstants.AssetPayment.TRANSFER_PAYMENT_CODE, assetTransferCode);

        criteriaC = new Criteria();
        criteriaC.addIsNull(CamsPropertyConstants.AssetPayment.TRANSFER_PAYMENT_CODE);

        criteriaB.addOrCriteria(criteriaC);
        criteria.addAndCriteria(criteriaB);
        // End *********************************************************************

        // Begin **********
        criteria.addIn(PAYMENT_TO_ASSET_REFERENCE_DESCRIPTOR + CamsPropertyConstants.Asset.PRIMARY_DEPRECIATION_METHOD, depreciationMethodList);
        // End **********

        Calendar DateOf1900 = Calendar.getInstance();
        DateOf1900.set(1900, 0, 1);

        criteria.addNotNull(PAYMENT_TO_ASSET_REFERENCE_DESCRIPTOR + CamsPropertyConstants.Asset.ASSET_DEPRECIATION_DATE);
        criteria.addLessOrEqualThan(PAYMENT_TO_ASSET_REFERENCE_DESCRIPTOR + CamsPropertyConstants.Asset.ASSET_DEPRECIATION_DATE, new java.sql.Date(depreciationDate.getTimeInMillis()));
        criteria.addGreaterThan(PAYMENT_TO_ASSET_REFERENCE_DESCRIPTOR + CamsPropertyConstants.Asset.ASSET_DEPRECIATION_DATE, new java.sql.Date(DateOf1900.getTimeInMillis()));

        // Begin *******************************************************************
        criteriaB = new Criteria();
        criteriaB.addGreaterThan(PAYMENT_TO_ASSET_REFERENCE_DESCRIPTOR + CamsPropertyConstants.Asset.ASSET_RETIREMENT_FISCAL_YEAR, fiscalYear);

        criteriaC = new Criteria();
        criteriaC.addIsNull(PAYMENT_TO_ASSET_REFERENCE_DESCRIPTOR + CamsPropertyConstants.Asset.ASSET_RETIREMENT_FISCAL_YEAR);

        Criteria criteriaD = new Criteria();
        criteriaD.addNotNull(PAYMENT_TO_ASSET_REFERENCE_DESCRIPTOR + CamsPropertyConstants.Asset.ASSET_RETIREMENT_FISCAL_MONTH);

        criteriaB.addOrCriteria(criteriaC);
        criteriaB.addOrCriteria(criteriaD);

        criteriaC = new Criteria();
        criteriaC.addEqualTo(PAYMENT_TO_ASSET_REFERENCE_DESCRIPTOR + CamsPropertyConstants.Asset.ASSET_RETIREMENT_FISCAL_YEAR, fiscalYear);
        criteriaC.addGreaterThan(PAYMENT_TO_ASSET_REFERENCE_DESCRIPTOR + CamsPropertyConstants.Asset.ASSET_RETIREMENT_FISCAL_MONTH, fiscalMonth);

        criteriaB.addOrCriteria(criteriaC);
        criteria.addAndCriteria(criteriaB);
        // End *********************************************************************

        if (!notAcceptedAssetStatus.isEmpty())
            criteria.addNotIn(PAYMENT_TO_ASSET_REFERENCE_DESCRIPTOR + CamsPropertyConstants.Asset.ASSET_INVENTORY_STATUS, notAcceptedAssetStatus);

        criteria.addGreaterThan(ASSET_TO_ASSET_TYPE_REFERENCE_DESCRIPTOR + CamsPropertyConstants.AssetType.ASSET_DEPRECIATION_LIFE_LIMIT, 0);

        // Excluding federally owned assets.
        if (!federallyOwnedObjectSubTypes.isEmpty()) {
            if (federallyOwnedCriteria) {
                criteria.addIn(PAYMENT_TO_OBJECT_REFERENCE_DESCRIPTOR + KFSPropertyConstants.FINANCIAL_OBJECT_SUB_TYPE_CODE, federallyOwnedObjectSubTypes);
            }
            else {
                criteria.addNotIn(PAYMENT_TO_OBJECT_REFERENCE_DESCRIPTOR + KFSPropertyConstants.FINANCIAL_OBJECT_SUB_TYPE_CODE, federallyOwnedObjectSubTypes);
            }
        }
        // DELETE THESE LINES ******************************************
        // criteria.addEqualTo("capitalAssetNumber", "389220");
        // criteria.addEqualTo("capitalAssetNumber", "393098");
        // ************************************************************

        // Getting a list of assets being transferred or retired which documents are pending of approval.
        List<String> assetsWithPendingDocs = this.getAssetsWithPendingAssetDocuments();
        if (!assetsWithPendingDocs.isEmpty()) {
            criteria.addNotIn(CamsPropertyConstants.AssetPayment.CAPITAL_ASSET_NUMBER, assetsWithPendingDocs);
        }

        LOG.debug("createDepreciationCriteria() -  ended");
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Finished creating criteria for asset payments - Include federally owned? " + federallyOwnedCriteria);
        return criteria;
    }

    /**
     * 
     * @see org.kuali.module.cams.dao.DepreciableAssetsDao#generateStatistics(boolean, java.lang.String, java.lang.Integer,
     *      java.lang.Integer, java.util.Calendar)
     */
    public List<String[]> generateStatistics(boolean beforeDepreciationReport, String documentNumber, Integer fiscalYear, Integer fiscalMonth, Calendar depreciationDate) {
        LOG.debug("generateStatistics() -  started");
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "generating statistics for report - " + (beforeDepreciationReport ? "Before part." : "After part"));

        List<String[]> reportLine = new ArrayList<String[]>();
        boolean processAlreadyRan = false;

        NumberFormat usdFormat = NumberFormat.getCurrencyInstance(Locale.US);
        KualiDecimal amount = new KualiDecimal(0);
        String[] columns = new String[2];


        columns[1] = "******************";
        if (beforeDepreciationReport)
            columns[0] = REPORT_GROUP[0];
        else
            columns[0] = REPORT_GROUP[1];

        reportLine.add(columns.clone());

        if (beforeDepreciationReport) {
            columns[0] = "Depreciation Run Date";
            columns[1] = (dateTimeService.toDateString(depreciationDate.getTime()));
            reportLine.add(columns.clone());


            columns[0] = "Fiscal Year";
            columns[1] = (fiscalYear.toString());
            reportLine.add(columns.clone());

            columns[0] = "Fiscal Month";
            columns[1] = (fiscalMonth.toString());
            reportLine.add(columns.clone());

            columns[0] = "Number of assets fully depreciated";
            columns[1] = getFullyDepreciatedAssetCount().toString();
            reportLine.add(columns.clone());
        }

        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting DocumentHeader row count.");
        Criteria criteria = new Criteria();
        ReportQueryByCriteria q = QueryFactory.newReportQuery(DocumentHeader.class, new Criteria());
        q.setAttributes(new String[] { "count(*)" });
        Iterator<Object> i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);

        Object[] data = (Object[]) i.next();
        columns[0] = "Document header table - record count";
        columns[1] = (convertCountValueToString(data[0]));
        reportLine.add(columns.clone());

        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting general ledger pending entry row count.");
        q = QueryFactory.newReportQuery(GeneralLedgerPendingEntry.class, new Criteria());
        q.setAttributes(new String[] { "count(*)" });
        i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
        data = (Object[]) i.next();
        columns[0] = "General ledger pending entry table - record count";
        columns[1] = (convertCountValueToString(data[0]));
        reportLine.add(columns.clone());

        if (beforeDepreciationReport) {
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting assets row count.");
            q = QueryFactory.newReportQuery(Asset.class, new Criteria());
            q.setAttributes(new String[] { "count(*)" });
            i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
            data = (Object[]) i.next();
            columns[0] = "Asset table - record count";
            columns[1] = (convertCountValueToString(data[0]));
            reportLine.add(columns.clone());
        }

        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting assets payment row count, depreciation base amount, accumulated depreciation amount, and every months depreciation amount.");
        q = QueryFactory.newReportQuery(AssetPayment.class, new Criteria());
        q.setAttributes(new String[] { "count(*)", "SUM(" + CamsPropertyConstants.AssetPayment.PRIMARY_DEPRECIATION_BASE_AMOUNT + ")", "SUM(" + CamsPropertyConstants.AssetPayment.ACCUMULATED_DEPRECIATION_AMOUNT + ")", "SUM(" + CamsPropertyConstants.AssetPayment.PREVIOUS_YEAR_DEPRECIATION_AMOUNT + ")", "SUM(" + CamsPropertyConstants.AssetPayment.PERIOD_1_DEPRECIATION_AMOUNT + ")", "SUM(" + CamsPropertyConstants.AssetPayment.PERIOD_2_DEPRECIATION_AMOUNT + ")", "SUM(" + CamsPropertyConstants.AssetPayment.PERIOD_3_DEPRECIATION_AMOUNT + ")", "SUM(" + CamsPropertyConstants.AssetPayment.PERIOD_4_DEPRECIATION_AMOUNT + ")", "SUM(" + CamsPropertyConstants.AssetPayment.PERIOD_5_DEPRECIATION_AMOUNT + ")", "SUM(" + CamsPropertyConstants.AssetPayment.PERIOD_6_DEPRECIATION_AMOUNT + ")", "SUM(" + CamsPropertyConstants.AssetPayment.PERIOD_7_DEPRECIATION_AMOUNT + ")", "SUM(" + CamsPropertyConstants.AssetPayment.PERIOD_8_DEPRECIATION_AMOUNT + ")",
                "SUM(" + CamsPropertyConstants.AssetPayment.PERIOD_9_DEPRECIATION_AMOUNT + ")", "SUM(" + CamsPropertyConstants.AssetPayment.PERIOD_10_DEPRECIATION_AMOUNT + ")", "SUM(" + CamsPropertyConstants.AssetPayment.PERIOD_11_DEPRECIATION_AMOUNT + ")", "SUM(" + CamsPropertyConstants.AssetPayment.PERIOD_12_DEPRECIATION_AMOUNT + ")" });

        i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
        data = new Object[16];
        if (i.hasNext()) {
            data = (Object[]) i.next();
        }
        else {
            for (int c = 0; c < 16; c++)
                data[c] = new KualiDecimal(0);
        }

        if (beforeDepreciationReport) {
            columns[0] = "Asset payment table - record count";
            columns[1] = (convertCountValueToString(data[0]));
            reportLine.add(columns.clone());
        }

        columns[0] = "Depreciation base amount";
        columns[1] = (usdFormat.format((KualiDecimal) data[1]));
        reportLine.add(columns.clone());

        columns[0] = "Current year - accumulated depreciation";
        columns[1] = (usdFormat.format((KualiDecimal) data[2]));
        reportLine.add(columns.clone());

        columns[0] = "Previous year - accumulated depreciation";
        columns[1] = (usdFormat.format((KualiDecimal) data[3]));
        reportLine.add(columns.clone());

        /*
         * Here I'm getting the column of total depreciation for the current fiscal month. The idea here is to prevent the process
         * from running a second time for the same fiscal month. 3 + fiscalMonth (variable) => current fiscal month depreciation
         * column in the array. So if the current fiscal month depreciation column > 0 then depreciation was already ran. Therefore,
         * it should be stop but, not until part of the pdf report List is populated so it can be written.
         */
        processAlreadyRan = false;
        if (((KualiDecimal) data[3 + fiscalMonth]).compareTo(new KualiDecimal(0)) != 0)
            processAlreadyRan = true;
        // *******************************************************************************************************************************

        // Adding monthly depreciation amounts
        KualiDecimal yearToDateDepreciationAmt = new KualiDecimal(0);

        int fiscalStartMonth = Integer.parseInt(optionsService.getCurrentYearOptions().getUniversityFiscalYearStartMo());
        boolean isJanuaryTheFirstFiscalMonth = (fiscalStartMonth == 1);
        int col = 4;
        int currentMonth = fiscalStartMonth - 1;
        for (int monthCounter = 1; monthCounter <= 12; monthCounter++, currentMonth++) {
            columns[0] = CamsConstants.MONTHS[currentMonth] + " depreciation amount";
            columns[1] = (usdFormat.format((KualiDecimal) data[col]));
            reportLine.add(columns.clone());

            yearToDateDepreciationAmt = yearToDateDepreciationAmt.add((KualiDecimal) data[col]);

            col++;

            if (!isJanuaryTheFirstFiscalMonth) {
                if (currentMonth == 11)
                    currentMonth = -1;
            }
        }

        columns[0] = "Year to date depreciation amount";
        columns[1] = (usdFormat.format(yearToDateDepreciationAmt));
        reportLine.add(columns.clone());

        if (beforeDepreciationReport) {
            Object federallyOwnedAssetPaymentObjectCount = getFederallyOwnedAssetPaymentCount(fiscalYear, fiscalMonth, depreciationDate);
            Object retiredAndTransferredAssetObjectCount = getNumberOfAssetsBeingRetiredAndTransferred();

            Integer federallyOwnedAssetPaymentCount;
            Integer retiredAndTransferredAssetCount;
            Integer eligibleAssetPaymentCount;

            columns[0] = "Object code table - record count";
            columns[1] = (convertCountValueToString(this.getAssetObjectCodesCount(fiscalYear)));
            reportLine.add(columns.clone());

            columns[0] = "Plant fund account table - record count";
            columns[1] = (convertCountValueToString(this.getCOAsCount()));
            reportLine.add(columns.clone());

            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting asset payment row count , depreciation base amount, accumulated depreciation amount, and every months depreciation amount.");
            q = QueryFactory.newReportQuery(AssetPayment.class, this.getDepreciationCriteria(fiscalYear, fiscalMonth, depreciationDate, false));
            q.setAttributes(new String[] { "count(distinct " + CamsPropertyConstants.AssetPayment.CAPITAL_ASSET_NUMBER + ")", "count(*)" });
            i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
            
            data = (Object[]) i.next();
            
            if (data[1] instanceof BigDecimal) {
                eligibleAssetPaymentCount = new Integer(((BigDecimal) data[1]).toString());
                federallyOwnedAssetPaymentCount = new Integer(((BigDecimal) federallyOwnedAssetPaymentObjectCount).toString());
                retiredAndTransferredAssetCount = new Integer(((BigDecimal) retiredAndTransferredAssetObjectCount).toString());
            }
            else {
                eligibleAssetPaymentCount = new Integer(((Long) data[1]).toString());
                federallyOwnedAssetPaymentCount = new Integer(((Long) federallyOwnedAssetPaymentObjectCount).toString());
                retiredAndTransferredAssetCount = new Integer(((Long) retiredAndTransferredAssetObjectCount).toString());
            }

            Integer totalAssetPayments = (eligibleAssetPaymentCount + retiredAndTransferredAssetCount + federallyOwnedAssetPaymentCount);

            columns[0] = "Asset payments eligible for depreciation";
            columns[1] = totalAssetPayments.toString();
            reportLine.add(columns.clone());

            columns[0] = "Number of assets with pending AR or AT documents";
            columns[1] = retiredAndTransferredAssetCount.toString();
            reportLine.add(columns.clone());

            totalAssetPayments = (eligibleAssetPaymentCount + federallyOwnedAssetPaymentCount);
            columns[0] = "Asset payments eligible for depreciation - After excluding AR and AT";
            columns[1] = totalAssetPayments.toString();
            reportLine.add(columns.clone());

            columns[0] = "Asset payments ineligible for depreciation (Federally owned assets)";
            columns[1] = (convertCountValueToString(getFederallyOwnedAssetPaymentCount(fiscalYear, fiscalMonth, depreciationDate)));
            reportLine.add(columns.clone());

            // payments eligible after deleting pending AR and AT documents.!!
            columns[0] = "Asset payments eligible for depreciation - After excluding federally owned assets";
            columns[1] = eligibleAssetPaymentCount.toString();
            reportLine.add(columns.clone());

            columns[0] = "Assets eligible for depreciation";
            columns[1] = (convertCountValueToString(data[0]));
            reportLine.add(columns.clone());
        }


        if (!beforeDepreciationReport) {
            // Generating a list of depreciation expense object codes.
            List<String> depreExpObjCodes = this.getExpenseObjectCodes(fiscalYear);

            // Generating a list of accumulated depreciation object codes.
            List<String> accumulatedDepreciationObjCodes = this.getAccumulatedDepreciationObjectCodes(fiscalYear);

            KualiDecimal debits = new KualiDecimal(0);
            KualiDecimal credits = new KualiDecimal(0);

            // Document Number created
            columns[0] = "Document Number";
            columns[1] = documentNumber;
            reportLine.add(columns.clone());

            // Expense Debit
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "calculating the debit amount for expense object codes.");
            criteria = new Criteria();
            criteria.addIn(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, depreExpObjCodes);
            criteria.addEqualTo(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE, KFSConstants.GL_DEBIT_CODE);
            criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);

            q = QueryFactory.newReportQuery(GeneralLedgerPendingEntry.class, criteria);
            q.setAttributes(new String[] { "SUM(" + KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")" });
            i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
            data = (Object[]) i.next();

            amount = (data[0] == null ? new KualiDecimal(0) : (KualiDecimal) data[0]);
            columns[0] = "Debit - Depreciation Expense object codes: " + depreExpObjCodes.toString();
            columns[1] = (usdFormat.format(amount));
            reportLine.add(columns.clone());
            debits = debits.add(amount);

            // Accumulated Depreciation credit
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "calculating the credit amount for accumulated depreciation object codes.");
            criteria = new Criteria();
            criteria.addIn(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, accumulatedDepreciationObjCodes);
            criteria.addEqualTo(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE, KFSConstants.GL_CREDIT_CODE);
            criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);

            q = QueryFactory.newReportQuery(GeneralLedgerPendingEntry.class, criteria);
            q.setAttributes(new String[] { "SUM(" + KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")" });
            i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
            data = (Object[]) i.next();
            amount = (data[0] == null ? new KualiDecimal(0) : (KualiDecimal) data[0]);
            columns[0] = "Credit - Accumulated depreciation object codes: " + accumulatedDepreciationObjCodes.toString();
            columns[1] = (usdFormat.format(amount));
            reportLine.add(columns.clone());
            credits = credits.add(amount);
            // ***********************************************************************************************

            // Accumulated Depreciation debit
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "calculating the debit amount for accumulated depreciation object codes.");
            criteria = new Criteria();
            criteria.addIn(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, accumulatedDepreciationObjCodes);
            criteria.addEqualTo(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE, KFSConstants.GL_DEBIT_CODE);
            criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);

            q = QueryFactory.newReportQuery(GeneralLedgerPendingEntry.class, criteria);
            q.setAttributes(new String[] { "SUM(" + KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")" });
            i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
            data = (Object[]) i.next();
            amount = (data[0] == null ? new KualiDecimal(0) : (KualiDecimal) data[0]);
            columns[0] = "Debit - Accumulated depreciation object codes:" + accumulatedDepreciationObjCodes.toString();
            columns[1] = (usdFormat.format(amount));
            reportLine.add(columns.clone());
            debits = debits.add(amount);

            // Expense credit
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "calculating the credit amount for expense object codes.");
            criteria = new Criteria();
            criteria.addIn(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, depreExpObjCodes);
            criteria.addEqualTo(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE, KFSConstants.GL_CREDIT_CODE);
            criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);

            q = QueryFactory.newReportQuery(GeneralLedgerPendingEntry.class, criteria);
            q.setAttributes(new String[] { "SUM(" + KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")" });
            i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
            data = (Object[]) i.next();
            amount = (data[0] == null ? new KualiDecimal(0) : (KualiDecimal) data[0]);
            columns[0] = "Credit - Depreciation Expense object codes:" + depreExpObjCodes.toString();
            columns[1] = (usdFormat.format(amount));
            reportLine.add(columns.clone());
            credits = credits.add(amount);


            columns[0] = "Total Debits";
            columns[1] = usdFormat.format(debits);
            reportLine.add(columns.clone());

            columns[0] = "Total Credits";
            columns[1] = usdFormat.format(credits);
            reportLine.add(columns.clone());

            columns[0] = "Total Debits - Total Credits";
            columns[1] = usdFormat.format(debits.subtract(credits));
            reportLine.add(columns.clone());
        }
        LOG.debug("generateStatistics() -  ended");

        if (processAlreadyRan && beforeDepreciationReport) {
            throw new IllegalStateException(kualiConfigurationService.getPropertyString(CamsKeyConstants.Depreciation.DEPRECIATION_ALREADY_RAN_MSG));
        }
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Finished generating statistics for report - " + (beforeDepreciationReport ? "Before part." : "After part"));
        return reportLine;
    }

    /**
     * 
     * This method counts the number of assets fully depreciated comparing the depreciation base amount with the accumulated
     * depreciation amount
     * 
     * @return
     */
    private Integer getFullyDepreciatedAssetCount() {
        LOG.debug("DepreciableAssetsDaoOjb.getFullyDepreciatedAssetCount() -  started");

        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting the number of assets fully depreciated.");


        List<String> assetTransferCode = new ArrayList<String>();
        assetTransferCode.add(CamsConstants.TRANSFER_PAYMENT_CODE_N);
        assetTransferCode.add(" ");

        Criteria criteria = new Criteria();
        criteria.addIn(CamsPropertyConstants.AssetPayment.TRANSFER_PAYMENT_CODE, assetTransferCode);

        ReportQueryByCriteria q = QueryFactory.newReportQuery(AssetPayment.class, criteria);
        q.setAttributes(new String[] { PAYMENT_TO_ASSET_REFERENCE_DESCRIPTOR + CamsPropertyConstants.Asset.SALVAGE_AMOUNT, "SUM(" + CamsPropertyConstants.AssetPayment.PRIMARY_DEPRECIATION_BASE_AMOUNT + ")", "SUM(" + CamsPropertyConstants.AssetPayment.ACCUMULATED_DEPRECIATION_AMOUNT + ")" });
        q.addGroupBy(CamsPropertyConstants.AssetPayment.CAPITAL_ASSET_NUMBER);
        q.addGroupBy(PAYMENT_TO_ASSET_REFERENCE_DESCRIPTOR + CamsPropertyConstants.Asset.SALVAGE_AMOUNT);
        Iterator<Object> i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);

        Object[] fieldValue;
        
        Integer fullyDepreciatedCounter = 0;
        while (i.hasNext()) {
            fieldValue = (Object[]) i.next();

            KualiDecimal salvageAmount = new KualiDecimal(0);
            if (fieldValue[0] != null)
                salvageAmount = (KualiDecimal)fieldValue[0];
                        
            KualiDecimal baseAmount  = new KualiDecimal(0);
            if (fieldValue[1] != null)
                baseAmount = (KualiDecimal)fieldValue[1];
            
            KualiDecimal accumulatedDepreciation = new KualiDecimal(0);
            if (fieldValue[2] != null)
                accumulatedDepreciation = (KualiDecimal)fieldValue[2];
                
            if (baseAmount.subtract(salvageAmount).compareTo(accumulatedDepreciation) == 0) {
                fullyDepreciatedCounter++;
            }
        }
        LOG.debug("DepreciableAssetsDaoOjb.getFullyDepreciatedAssetCount() -  ended");
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Finished getting the number of assets fully depreciated.");
        return fullyDepreciatedCounter;
    }

    /**
     * 
     * This method the number of federally owned asset payments
     * 
     * @param fiscalYear
     * @param fiscalMonth
     * @param depreciationDate
     * 
     * @return Object with the # of federally owned assets
     */
    private Object getFederallyOwnedAssetPaymentCount(Integer fiscalYear, Integer fiscalMonth, Calendar depreciationDate) {
        LOG.debug("DepreciableAssetsDaoOjb.getFederallyOwnedAssetPaymentCount() -  started");

        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting the number of federally owned asset payments.");

        String sNumber="0";
        Object[] data=null;
        List<String> federallyOwnedObjectSubTypes = getFederallyOwnedObjectSubTypes();
        if (!federallyOwnedObjectSubTypes.isEmpty()) {
            ReportQueryByCriteria q = QueryFactory.newReportQuery(AssetPayment.class, this.getDepreciationCriteria(fiscalYear, fiscalMonth, depreciationDate, true));
            q.setAttributes(new String[] { "count(*)" });
            Iterator<Object> i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
            if (i.hasNext()) {
                data = (Object[]) i.next();
                sNumber = this.convertCountValueToString(data[0]);
            }
        }

        LOG.debug("DepreciableAssetsDaoOjb.getFederallyOwnedAssetPaymentCount() -  ended");
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Finished getting the number of federally owned asset payments.");
        if (data[0] instanceof Long)
            return new Long(sNumber);
        else 
            return new BigDecimal(sNumber);
    }


    /**
     * 
     * This method returns the number of records found resulting from a join of the organization table and the account table
     * 
     * @param fiscalYear
     * @return
     */
    private Object getCOAsCount() {
        LOG.debug("DepreciableAssetsDaoOjb.getCOAsCount() -  started");
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting the number of campus plant fund accounts.");

        Criteria criteria = new Criteria();
        Object[] data;
        ReportQueryByCriteria q = QueryFactory.newReportQuery(Account.class, criteria);
        q.setAttributes(new String[] { "count(" + KFSPropertyConstants.ORGANIZATION + "." + KFSPropertyConstants.CAMPUS_PLANT_ACCOUNT_NUMBER + ")" });
        Iterator<Object> i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
        if (!i.hasNext()) {
            data = new Object[1];
            data[0] = new BigDecimal(0);
        }
        else {
            data = (Object[]) i.next();
        }

        LOG.debug("DepreciableAssetsDaoOjb.getCOAsCount() -  ended");
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Finised getting the number of campus plant fund accounts.");
        return data[0];
    }

    /**
     * 
     * This method the value of the system parameter NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES
     * 
     * @return
     */
    private List<String> getFederallyOwnedObjectSubTypes() {
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "getting the federally owned object subtype codes.");

        List<String> federallyOwnedObjectSubTypes = new ArrayList<String>();
        if (parameterService.parameterExists(ParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES)) {
            federallyOwnedObjectSubTypes = parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES);
        }
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Finished getting the federally owned object subtype codes which are:" + federallyOwnedObjectSubTypes.toString());
        return federallyOwnedObjectSubTypes;
    }


    /**
     * 
     * @see org.kuali.module.cams.dao.DepreciableAssetsDao#getAssetObjectCodes(java.lang.Integer)
     */
    public Collection<AssetObjectCode> getAssetObjectCodes(Integer fiscalYear) {
        LOG.debug("DepreciableAssetsDAoOjb.getAssetObjectCodes() -  started");
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting asset object codes.");

        Collection<AssetObjectCode> assetObjectCodesCollection;
        HashMap<String, Object> fields = new HashMap<String, Object>();
        fields.put(CamsPropertyConstants.AssetObject.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        assetObjectCodesCollection = (Collection<AssetObjectCode>) businessObjectService.findMatching(AssetObjectCode.class, fields);

        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Finished getting asset object codes - which are:" + assetObjectCodesCollection.toString());
        LOG.debug("DepreciableAssetsDAoOjb.getAssetObjectCodes() -  ended");
        return assetObjectCodesCollection;
    }

    /**
     * 
     * This method gets a list of Expense object codes from the asset object code table for a particular fiscal year
     * 
     * @param fiscalYear
     * @return a List<String>
     */
    private List<String> getExpenseObjectCodes(Integer fiscalYear) {
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
     * 
     * This method gets a list of Accumulated Depreciation Object Codes from the asset object code table for a particular fiscal
     * year.
     * 
     * @param fiscalYear
     * @return List<String>
     */
    private List<String> getAccumulatedDepreciationObjectCodes(Integer fiscalYear) {
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
     * 
     * This method counts the number of assets that exist in both chart of accounts object code table and capital asset object code
     * tables
     * 
     * @param fiscalYear
     * @return number of object codes found
     */
    private Object getAssetObjectCodesCount(Integer fiscalYear) {
        LOG.debug("DepreciableAssetsDAoOjb.getAssetObjectCodesCount() -  started");
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting asset object code count.");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(CamsPropertyConstants.AssetObject.UNIVERSITY_FISCAL_YEAR, fiscalYear);

        ReportQueryByCriteria q = QueryFactory.newReportQuery(AssetObjectCode.class, criteria);
        q.setAttributes(new String[] { "count(" + KFSPropertyConstants.OBJECT_CODE + "." + KFSPropertyConstants.FINANCIAL_OBJECT_CODE + ")" });
        Iterator<Object> i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
        Object[] data = (Object[]) i.next();

        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Finisned getting asset object code count which is: " + data[0]);
        LOG.debug("DepreciableAssetsDAoOjb.getAssetObjectCodesCount() -  ended");
        return data[0];
    }

    /**
     * 
     * This method converts a variable of type object to BigDecimal or a Long type in order to return a string
     * 
     * @param fieldValue
     * @return String
     */
    private String convertCountValueToString(Object fieldValue) {
        if (fieldValue == null)
            fieldValue = new BigDecimal(0);

        if (fieldValue instanceof BigDecimal) {
            return ((BigDecimal) fieldValue).toString();
        }
        else {
            return ((Long) fieldValue).toString();
        }
    }

    public void setKualiConfigurationService(KualiConfigurationService kcs) {
        kualiConfigurationService = kcs;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setOptionsService(OptionsService optionService) {
        this.optionsService = optionService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

} // end of class

/*
 * A.cptlast_nbr = P.cptlast_nbr // foreign key AND A.cptlast_origin_cd = P.cptlast_origin_cd // ??
 * 
 * AND P.cptlast_origin_cd = '01' // done AND ( P.ast_depr1_base_amt != 0.00 OR P.ast_depr1_base_amt IS NOT NULL ) //done AND (
 * P.ast_trnfr_pmt_cd IN ( 'N', ' ') OR P.ast_trnfr_pmt_cd IS NULL) //done AND ( ( A.ast_depr_mthd1_cd = 'SL'OR A.ast_depr_mthd1_cd
 * IS NULL ) OR ( A.ast_depr_mthd1_cd = 'SV' ) ) //Done
 * 
 * AND A.cptl_ast_int_srvc_dt IS NOT NULL // done AND A.cptl_ast_crt_dt != '01/01/1900' // done AND A.cptl_ast_crt_dt <= v_depr_date //
 * done
 * 
 * AND ( ( A.ast_retir_fscl_yr > v_fiscal_yr ) OR ( A.ast_retir_fscl_yr = v_fiscal_yr AND TO_NUMBER( A.ast_retir_prd_cd ) >
 * v_fiscal_prd ) OR A.ast_retir_fscl_yr IS NULL OR A.ast_retir_prd_cd IS NULL )
 * 
 * AND A.ast_invn_stat_cd NOT IN ( 'N', 'O' ) // done AND A.cptlast_typ_cd = T.cptlast_typ_cd //done AND T.cptlast_deprlf_lmt > 0
 * //done
 * 
 * and p.fin_object_cd not in ( -- Exclusing all the federally-owned object sub type codes. Select ca_object_code_t From
 * ca_object_code_t o, cm_cptlast_obj_t a Where o.univ_fiscal_yr = a.univ_fiscal_yr and a.univ_fiscal_yr = v_fiscal_year And
 * o.fin_coa_cd = a.fin_coa_cd And o.fin_obj_sub_typ_cd = a.fin_obj_sub_typ_cd And fin_obj_sub_typ_cd IN ('CO', 'CP', 'UO', 'AM',
 * 'AF', 'LA', 'BY' ) ) and
 * 
 * **** Excluding retired and transferd assets ************* P.cptlast_nbr + cptlast_origin_cd not in ( Select c.cptlast_nbr +
 * c.cptlast_origin_cd FROM fp_doc_header_t, H cm_cptlast_hdr_t T WHERE H.fs_origin_cd = C.fs_origin_cd and H.fdoc_nbr = C.fdoc_nbr
 * and H.fdoc_typ_cd IN ('AR', 'AT') AND H.fdoc_status_cd NOT IN ('A', 'C') GROUP BY P.cptlast_nbr, P.cptlast_origin_cd )
 */