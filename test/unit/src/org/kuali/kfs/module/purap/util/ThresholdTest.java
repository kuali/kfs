/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.util;

import static org.kuali.kfs.sys.fixture.UserNameFixture.PARKE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.Guid;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.ConfigureContext;
import org.kuali.kfs.module.purap.PurapRuleConstants;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderDocumentFixture;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.kfs.sys.document.workflow.WorkflowTestUtils;

@ConfigureContext(session = PARKE, shouldCommitTransactions=false)
public class ThresholdTest extends KualiTestBase {
    
    private static Logger LOG = Logger.getLogger(ThresholdTest.class);
    
    private UnitTestSqlDao unitTestSqlDao;

    public ThresholdTest() {
        super();
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);
    }
    
    private void insertThresholdRecord(Map<ThresholdField,Object> field2Values,
                                       KualiDecimal thresholdAmount){
        
        unitTestSqlDao.sqlCommand("delete from PUR_THRSHLD_T");
        
        String objId = new Guid().toString();
        
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
        checkReceivingFlagInPO(poDocument,map,true);
    }
    
    public final void testReceivingRequiredFlagWithChartNegTest()
    throws Exception {
        PurchaseOrderDocument poDocument = buildSimplePODocument();
        HashMap map = new HashMap();
        map.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getChartOfAccountsCode());
        checkReceivingFlagInPO(poDocument,map,false);
    }
    
    public final void testReceivingRequiredFlagWithFund()
    throws Exception {
        PurchaseOrderDocument poDocument = buildSimplePODocument();
        String accTypeCode = poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getAccountTypeCode();
        HashMap map = new HashMap();
        map.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getChartOfAccountsCode());
        map.put(ThresholdField.ACCOUNT_TYPE_CODE,accTypeCode);
        checkReceivingFlagInPO(poDocument,map,true);
    }
    
    public final void testReceivingRequiredFlagWithFundNegativeTest()
    throws Exception {
        PurchaseOrderDocument poDocument = buildSimplePODocument();
        String accTypeCode = poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getAccountTypeCode();
        HashMap map = new HashMap();
        map.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getChartOfAccountsCode());
        map.put(ThresholdField.ACCOUNT_TYPE_CODE,accTypeCode);
        checkReceivingFlagInPO(poDocument,map,false);
    }
    
    public final void testReceivingRequiredFlagWithSubFund()
    throws Exception {
        PurchaseOrderDocument poDocument = buildSimplePODocument();
        String subFundGroupCode = poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getSubFundGroupCode();
        HashMap map = new HashMap();
        map.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getChartOfAccountsCode());
        map.put(ThresholdField.SUBFUND_GROUP_CODE,subFundGroupCode);
        checkReceivingFlagInPO(poDocument,map,true);
    }
    
    public final void testReceivingRequiredFlagWithSubFundNegativeTest()
    throws Exception {
        PurchaseOrderDocument poDocument = buildSimplePODocument();
        String subFundGroupCode = poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getSubFundGroupCode();
        HashMap map = new HashMap();
        map.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getChartOfAccountsCode());
        map.put(ThresholdField.SUBFUND_GROUP_CODE,subFundGroupCode);
        checkReceivingFlagInPO(poDocument,map,false);
    }
    
    public final void testReceivingRequiredFlagWithObjectCode()
    throws Exception {
        PurchaseOrderDocument poDocument = buildSimplePODocument();
        String objectCode = poDocument.getItem(0).getSourceAccountingLines().get(0).getFinancialObjectCode();
        HashMap map = new HashMap();
        map.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getChartOfAccountsCode());
        map.put(ThresholdField.FINANCIAL_OBJECT_CODE,objectCode);
        checkReceivingFlagInPO(poDocument,map,true);
    }
    
    public final void testReceivingRequiredFlagWithObjectCodeNegativeTest()
    throws Exception {
        PurchaseOrderDocument poDocument = buildSimplePODocument();
        String objectCode = poDocument.getItem(0).getSourceAccountingLines().get(0).getFinancialObjectCode();
        HashMap map = new HashMap();
        map.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getChartOfAccountsCode());
        map.put(ThresholdField.FINANCIAL_OBJECT_CODE,objectCode);
        checkReceivingFlagInPO(poDocument,map,false);
    }
    
    public final void testReceivingRequiredFlagWithOrgCode()
    throws Exception {
        PurchaseOrderDocument poDocument = buildSimplePODocument();
        String orgCode = poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getOrganizationCode();
        HashMap map = new HashMap();
        map.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getChartOfAccountsCode());
        map.put(ThresholdField.ORGANIZATION_CODE,orgCode);
        checkReceivingFlagInPO(poDocument,map,true);
    }
    
    public final void testReceivingRequiredFlagWithOrgCodeNegativeTest()
    throws Exception {
        PurchaseOrderDocument poDocument = buildSimplePODocument();
        String orgCode = poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getOrganizationCode();
        HashMap map = new HashMap();
        map.put(ThresholdField.CHART_OF_ACCOUNTS_CODE,poDocument.getItem(0).getSourceAccountingLines().get(0).getAccount().getChartOfAccountsCode());
        map.put(ThresholdField.ORGANIZATION_CODE,orgCode);
        checkReceivingFlagInPO(poDocument,map,false);
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
        checkReceivingFlagInPO(poDocument,map,true);
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
        checkReceivingFlagInPO(poDocument,map,false);
    }
    
    private void checkReceivingFlagInPO(PurchaseOrderDocument poDocument,
                                        Map thresholdCriteria,
                                        boolean isPositiveTest)
    throws Exception {
        if (isPositiveTest){
            insertThresholdRecord(thresholdCriteria,poDocument.getItem(0).getExtendedPrice().subtract(new KualiDecimal(1)));
        }else{
            insertThresholdRecord(thresholdCriteria,poDocument.getItem(0).getExtendedPrice().add(new KualiDecimal(1)));
        }
        
        PurchaseOrderDocument result = routePO(poDocument);
        assertEquals("Receiving Flag test[" + thresholdCriteria + ",isPositiveTest=" + isPositiveTest + "]",isPositiveTest,result.isReceivingDocumentRequiredIndicator());
    }
    
    private PurchaseOrderDocument routePO(PurchaseOrderDocument poDocument) 
    throws Exception {
      /**
       * FIXME: This is a temp fix if PO.ITEMS_REQUIRE_COMMODITY_CODE_IND='Y' since we're not using commodity code in the PO item fixture.
       * Have to add CommodityCode in PO item fixture but facing user authorization issue since PARKE doesnt have permission to create vendormaintdoc
       * for the given CC (Getting exception from POService.updateVendorCommodityCode(PurchaseOrderDocument po))   
       */
      TestUtils.setSystemParameter(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND,"N");
      
      String docId = poDocument.getDocumentNumber();
      DocumentService documentService = SpringContext.getBean(DocumentService.class);
      poDocument.prepareForSave();       
      assertFalse("R".equals(poDocument.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus()));
      AccountingDocumentTestUtils.routeDocument(poDocument, "saving copy source document", null, documentService);
      WorkflowTestUtils.waitForStatusChange(poDocument.getDocumentHeader().getWorkflowDocument(), "F");
      
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
