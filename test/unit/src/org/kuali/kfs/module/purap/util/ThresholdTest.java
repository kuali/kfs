/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.util;

import static org.kuali.kfs.sys.fixture.UserNameFixture.parke;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.kfs.sys.document.workflow.WorkflowTestUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.krad.service.DocumentService;

@ConfigureContext(session = parke, shouldCommitTransactions=false)

public class ThresholdTest extends KualiTestBase {

    private static Logger LOG = Logger.getLogger(ThresholdTest.class);

    private UnitTestSqlDao unitTestSqlDao;

    public ThresholdTest() {
        super();
    }

    @Override
    protected void setUp()
    throws Exception {
        super.setUp();
        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);
    }

    private void insertThresholdRecord(Map<ThresholdField,Object> field2Values,
                                       KualiDecimal thresholdAmount){

        unitTestSqlDao.sqlCommand("delete from PUR_THRSHLD_T");

        String objId = java.util.UUID.randomUUID().toString();

        StringBuffer sqlPart1 = new StringBuffer();
        StringBuffer sqlPart2 = new StringBuffer();
        sqlPart1.append("insert into PUR_THRSHLD_T(PUR_THRSHLD_ID,OBJ_ID,VER_NBR,DOBJ_MAINT_CD_ACTV_IND,PUR_THRSHLD_AMT");
        sqlPart2.append(" values(1,'" + objId + "',1,'Y'," + thresholdAmount.floatValue());

        for (ThresholdField field : (List<ThresholdField>)ThresholdField.getEnumList()) {
            if (field2Values.get(field) != null){
                sqlPart1.append("," + getDBColumnName(field));
                if (field2Values.get(field) instanceof Number){
                    sqlPart2.append("," + ((Number)field2Values.get(field)).floatValue());
                }else{
                    sqlPart2.append(",'" + field2Values.get(field) + "'");
                }
            }
        }

        sqlPart1.append(")");
        sqlPart2.append(")");
        sqlPart1.append(sqlPart2);

        unitTestSqlDao.sqlCommand(sqlPart1.toString());
    }

    public final void testReceivingRequiredFlagWithChart()
    throws Exception {
        PurchaseOrderDocument poDocument = buildSimplePODocument();
        HashMap map = new HashMap();
        map.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getChartOfAccountsCode());
        checkReceivingFlagInPO(poDocument,map,true,ThresholdHelper.CHART);
    }

    public final void testReceivingRequiredFlagWithChartNegTest()
    throws Exception {
        PurchaseOrderDocument poDocument = buildSimplePODocument();
        HashMap map = new HashMap();
        map.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getChartOfAccountsCode());
        checkReceivingFlagInPO(poDocument,map,false,ThresholdHelper.CHART);
    }

    public final void testReceivingRequiredFlagWithFund()
    throws Exception {
        PurchaseOrderDocument poDocument = buildSimplePODocument();
        String accTypeCode = poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getAccountTypeCode();
        HashMap map = new HashMap();
        map.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getChartOfAccountsCode());
        map.put(ThresholdField.ACCOUNT_TYPE_CODE,accTypeCode);
        checkReceivingFlagInPO(poDocument,map,true,ThresholdHelper.CHART_AND_ACCOUNTTYPE);
    }

    public final void testReceivingRequiredFlagWithFundNegativeTest()
    throws Exception {
        PurchaseOrderDocument poDocument = buildSimplePODocument();
        String accTypeCode = poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getAccountTypeCode();
        HashMap map = new HashMap();
        map.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getChartOfAccountsCode());
        map.put(ThresholdField.ACCOUNT_TYPE_CODE,accTypeCode);
        checkReceivingFlagInPO(poDocument,map,false,ThresholdHelper.CHART_AND_ACCOUNTTYPE);
    }

    public final void testReceivingRequiredFlagWithSubFund()
    throws Exception {
        PurchaseOrderDocument poDocument = buildSimplePODocument();
        String subFundGroupCode = poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getSubFundGroupCode();
        HashMap map = new HashMap();
        map.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getChartOfAccountsCode());
        map.put(ThresholdField.SUBFUND_GROUP_CODE,subFundGroupCode);
        checkReceivingFlagInPO(poDocument,map,true,ThresholdHelper.CHART_AND_SUBFUND);
    }

    public final void testReceivingRequiredFlagWithSubFundNegativeTest()
    throws Exception {
        PurchaseOrderDocument poDocument = buildSimplePODocument();
        String subFundGroupCode = poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getSubFundGroupCode();
        HashMap map = new HashMap();
        map.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getChartOfAccountsCode());
        map.put(ThresholdField.SUBFUND_GROUP_CODE,subFundGroupCode);
        checkReceivingFlagInPO(poDocument,map,false,ThresholdHelper.CHART_AND_SUBFUND);
    }

    public final void testReceivingRequiredFlagWithObjectCode()
    throws Exception {
        PurchaseOrderDocument poDocument = buildSimplePODocument();
        String objectCode = poDocument.getItem(0).getSourceAccountingLines().get(0).getFinancialObjectCode();
        HashMap map = new HashMap();
        map.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getChartOfAccountsCode());
        map.put(ThresholdField.FINANCIAL_OBJECT_CODE,objectCode);
        checkReceivingFlagInPO(poDocument,map,true,ThresholdHelper.CHART_AND_OBJECTCODE);
    }

    public final void testReceivingRequiredFlagWithObjectCodeNegativeTest()
    throws Exception {
        PurchaseOrderDocument poDocument = buildSimplePODocument();
        String objectCode = poDocument.getItem(0).getSourceAccountingLines().get(0).getFinancialObjectCode();
        HashMap map = new HashMap();
        map.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getChartOfAccountsCode());
        map.put(ThresholdField.FINANCIAL_OBJECT_CODE,objectCode);
        checkReceivingFlagInPO(poDocument,map,false,ThresholdHelper.CHART_AND_OBJECTCODE);
    }

    public final void testReceivingRequiredFlagWithOrgCode()
    throws Exception {
        PurchaseOrderDocument poDocument = buildSimplePODocument();
        String orgCode = poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getOrganizationCode();
        HashMap map = new HashMap();
        map.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getChartOfAccountsCode());
        map.put(ThresholdField.ORGANIZATION_CODE,orgCode);
        checkReceivingFlagInPO(poDocument,map,true,ThresholdHelper.CHART_AND_ORGANIZATIONCODE);
    }

    public final void testReceivingRequiredFlagWithOrgCodeNegativeTest()
    throws Exception {
        PurchaseOrderDocument poDocument = buildSimplePODocument();
        String orgCode = poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getOrganizationCode();
        HashMap map = new HashMap();
        map.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getChartOfAccountsCode());
        map.put(ThresholdField.ORGANIZATION_CODE,orgCode);
        checkReceivingFlagInPO(poDocument,map,false,ThresholdHelper.CHART_AND_ORGANIZATIONCODE);
    }

    public final void testReceivingRequiredFlagWithVendor()
    throws Exception {
        PurchaseOrderDocument poDocument = buildSimplePODocument();
        Integer vendorGeneratedId = poDocument.getVendorHeaderGeneratedIdentifier();
        Integer vendorAssignedId = poDocument.getVendorDetailAssignedIdentifier();
        HashMap map = new HashMap();
        map.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getChartOfAccountsCode());
        map.put(ThresholdField.VENDOR_HEADER_GENERATED_ID,vendorGeneratedId);
        map.put(ThresholdField.VENDOR_DETAIL_ASSIGNED_ID,vendorAssignedId);
        checkReceivingFlagInPO(poDocument,map,true,ThresholdHelper.CHART_AND_VENDOR);
    }

    public final void testReceivingRequiredFlagWithVendorNegativeTest()
    throws Exception {
        PurchaseOrderDocument poDocument = buildSimplePODocument();
        Integer vendorGeneratedId = poDocument.getVendorHeaderGeneratedIdentifier();
        Integer vendorAssignedId = poDocument.getVendorDetailAssignedIdentifier();
        HashMap map = new HashMap();
        map.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getChartOfAccountsCode());
        map.put(ThresholdField.VENDOR_HEADER_GENERATED_ID,vendorGeneratedId);
        map.put(ThresholdField.VENDOR_DETAIL_ASSIGNED_ID,vendorAssignedId);
        checkReceivingFlagInPO(poDocument,map,false,ThresholdHelper.CHART_AND_VENDOR);
    }

    public final void testReceivingRequiredFlagWithCommodityCode()
    throws Exception {
        PurchaseOrderDocument poDocument = buildSimplePODocument();
        String commodityCode = ((PurchaseOrderItem)poDocument.getItem(0)).getPurchasingCommodityCode();
        HashMap map = new HashMap();
        map.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getChartOfAccountsCode());
        map.put(ThresholdField.COMMODITY_CODE,commodityCode);
        checkReceivingFlagInPO(poDocument,map,true,ThresholdHelper.CHART_AND_COMMODITYCODE);
    }

    public final void testReceivingRequiredFlagWithCommodityCodeNegativeTest()
    throws Exception {
        PurchaseOrderDocument poDocument = buildSimplePODocument();
        String commodityCode = ((PurchaseOrderItem)poDocument.getItem(0)).getPurchasingCommodityCode();
        HashMap map = new HashMap();
        map.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getChartOfAccountsCode());
        map.put(ThresholdField.COMMODITY_CODE,commodityCode);
        checkReceivingFlagInPO(poDocument,map,false,ThresholdHelper.CHART_AND_COMMODITYCODE);
    }

    private void checkReceivingFlagInPO(PurchaseOrderDocument poDocument,
                                        Map thresholdCriteriaFieldValues,
                                        boolean isPositiveTest,
                                        ThresholdCriteria thresholdCriteria)
    throws Exception {

//        if (isPositiveTest){
//            insertThresholdRecord(thresholdCriteriaFieldValues,poDocument.getItem(0).getExtendedPrice().subtract(new KualiDecimal(1)));
//        }else{
//            insertThresholdRecord(thresholdCriteriaFieldValues,poDocument.getItem(0).getExtendedPrice().add(new KualiDecimal(1)));
//        }
//
//        PurchaseOrderDocument result = routePO(poDocument);
//        assertEquals("Receiving Flag test[" + thresholdCriteriaFieldValues + ",isPositiveTest=" + isPositiveTest + "]",
//                     isPositiveTest,
//                     result.isReceivingDocumentRequiredIndicator());
//        /**
//         * This check is needed here irrespective of checking the receiving required flag in PO.
//         * We should make sure that the flag has been set for the correct threshold criteria.
//         * For eg, consider the CHART_AND_COMMODITYCODE criteria, here it could be possible for the flag
//         * to set if the chart criteria is satisfied instead of chart and commoditycode one.
//         */
//        ThresholdHelper thresholdHelper = new ThresholdHelper(poDocument);
//        assertEquals(isPositiveTest,
//                     thresholdHelper.isReceivingDocumentRequired(thresholdCriteria));
    }

    private PurchaseOrderDocument routePO(PurchaseOrderDocument poDocument)
    throws Exception {
      String docId = poDocument.getDocumentNumber();
      DocumentService documentService = SpringContext.getBean(DocumentService.class);
      poDocument.prepareForSave();
      assertFalse(DocumentStatus.ENROUTE.equals(poDocument.getDocumentHeader().getWorkflowDocument().getStatus()));
      AccountingDocumentTestUtils.routeDocument(poDocument, "saving copy source document", null, documentService);
      WorkflowTestUtils.waitForDocumentApproval(poDocument.getDocumentNumber());

      PurchaseOrderDocument result = (PurchaseOrderDocument) documentService.getByDocumentHeaderId(docId);
      return result;
    }

    public PurchaseOrderDocument buildSimplePODocument() {
        PurchaseOrderDocument poDocument = PurchaseOrderDocumentFixture.PO_FOR_THRESHOLD_CHECK.createPurchaseOrderDocument();
        assertEquals("PO should have only one item for Threshold test",1,poDocument.getItems().size());
        assertEquals("PO Item should have only one account for Threshold test",1,poDocument.getItem(0).getSourceAccountingLines().size());
        return poDocument;
    }

    private String getDBColumnName(ThresholdField field){
        if (field == ThresholdField.CHART_OF_ACCOUNTS_CODE){
            return "FIN_COA_CD";
        }else if (field == ThresholdField.ACCOUNT_TYPE_CODE){
            return "ACCT_TYP_CD";
        }else if (field == ThresholdField.SUBFUND_GROUP_CODE){
            return "SUB_FUND_GRP_CD";
        }else if (field == ThresholdField.FINANCIAL_OBJECT_CODE){
            return "FIN_OBJECT_CD";
        }else if (field == ThresholdField.ORGANIZATION_CODE){
            return "ORG_CD";
        }else if (field == ThresholdField.VENDOR_DETAIL_ASSIGNED_ID){
            return "VNDR_DTL_ASND_ID";
        }else if (field == ThresholdField.VENDOR_HEADER_GENERATED_ID){
            return "VNDR_HDR_GNRTD_ID";
        }else if (field == ThresholdField.COMMODITY_CODE){
            return "PUR_COMM_CD";
        }
        return null;
    }
}

