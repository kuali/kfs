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
package org.kuali.kfs.module.cab.batch.service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.fixture.CreditMemoAccountFixture;
import org.kuali.kfs.module.cab.fixture.CreditMemoAccountRevisionFixture;
import org.kuali.kfs.module.cab.fixture.CreditMemoDocumentFixture;
import org.kuali.kfs.module.cab.fixture.CreditMemoItemFixture;
import org.kuali.kfs.module.cab.fixture.DocumentRouteHeaderValueFixture;
import org.kuali.kfs.module.cab.fixture.EntryFixture;
import org.kuali.kfs.module.cab.fixture.FinancialSystemDocumentHeaderFixture;
import org.kuali.kfs.module.cab.fixture.PaymentRequestAccountFixture;
import org.kuali.kfs.module.cab.fixture.PaymentRequestAccountRevisionFixture;
import org.kuali.kfs.module.cab.fixture.PaymentRequestDocumentFixture;
import org.kuali.kfs.module.cab.fixture.PaymentRequestItemFixture;
import org.kuali.kfs.module.cab.fixture.PurchaseOrderAccountFixture;
import org.kuali.kfs.module.cab.fixture.PurchaseOrderCapitalAssetItemFixture;
import org.kuali.kfs.module.cab.fixture.PurchaseOrderCapitalAssetLocationFixture;
import org.kuali.kfs.module.cab.fixture.PurchaseOrderCapitalAssetSystemFixture;
import org.kuali.kfs.module.cab.fixture.PurchaseOrderDocumentFixture;
import org.kuali.kfs.module.cab.fixture.PurchaseOrderItemFixture;
import org.kuali.kfs.module.cab.fixture.RequisitionAccountFixture;
import org.kuali.kfs.module.cab.fixture.RequisitionDocumentFixture;
import org.kuali.kfs.module.cab.fixture.RequisitionItemFixture;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.DateUtils;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This abstract test class will provide the SQL inserts required to perform the testing CAB batch extract related services
 */
public abstract class BatchTestBase extends KualiTestBase {
    private static final String SQL_PACKAGE = "org/kuali/kfs/module/cab/sql/";
    private DateTimeService dateTimeService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dateTimeService = SpringContext.getBean(DateTimeService.class);
        updateLastExtractTime();
        prepareTestDataRecords();
    }

    protected void updateLastExtractTime() {
        updateLastCabExtractTime();
        updateLastPreTagExtractTime();
    }

    private void updateLastCabExtractTime() {
        Parameter lastExtractTime = findCabExtractTimeParam();
        if (ObjectUtils.isNotNull(lastExtractTime)) {
            SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            lastExtractTime.setParameterValue(fmt.format(DateUtils.addDays(dateTimeService.getCurrentDate(), -1)));
            SpringContext.getBean(BusinessObjectService.class).save(lastExtractTime);
        }
        else {
            fail("Could not find the parameter LAST_EXTRACT_TIME");
        }
    }

    protected Parameter findCabExtractTimeParam() {
        Map<String, String> primaryKeys = new LinkedHashMap<String, String>();
        primaryKeys.put(CabPropertyConstants.Parameter.PARAMETER_NAMESPACE_CODE, CabConstants.Parameters.NAMESPACE);
        primaryKeys.put(CabPropertyConstants.Parameter.PARAMETER_DETAIL_TYPE_CODE, CabConstants.Parameters.DETAIL_TYPE_BATCH);
        primaryKeys.put(CabPropertyConstants.Parameter.PARAMETER_NAME, CabConstants.Parameters.LAST_EXTRACT_TIME);
        Parameter lastExtractTime = (Parameter) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Parameter.class, primaryKeys);
        return lastExtractTime;
    }

    private void updateLastPreTagExtractTime() {
        Parameter lastExtractTime = findPretagExtractDateParam();
        if (ObjectUtils.isNotNull(lastExtractTime)) {
            SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
            lastExtractTime.setParameterValue(fmt.format(DateUtils.addDays(dateTimeService.getCurrentDate(), -1)));
            SpringContext.getBean(BusinessObjectService.class).save(lastExtractTime);
        }
        else {
            fail("Could not find the parameter LAST_EXTRACT_TIME");
        }
    }

    protected Parameter findPretagExtractDateParam() {
        Map<String, String> primaryKeys = new LinkedHashMap<String, String>();
        primaryKeys.put(CabPropertyConstants.Parameter.PARAMETER_NAMESPACE_CODE, CabConstants.Parameters.NAMESPACE);
        primaryKeys.put(CabPropertyConstants.Parameter.PARAMETER_DETAIL_TYPE_CODE, CabConstants.Parameters.DETAIL_TYPE_PRE_ASSET_TAGGING_STEP);
        primaryKeys.put(CabPropertyConstants.Parameter.PARAMETER_NAME, CabConstants.Parameters.LAST_EXTRACT_DATE);
        Parameter lastExtractTime = (Parameter) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Parameter.class, primaryKeys);
        return lastExtractTime;
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    protected void prepareTestDataRecords() throws SQLException {
        // clean first
        DocumentRouteHeaderValueFixture.setUpData();
        FinancialSystemDocumentHeaderFixture.setUpData();
        RequisitionDocumentFixture.setUpData();
        RequisitionItemFixture.setUpData();
        RequisitionAccountFixture.setUpData();
        PurchaseOrderDocumentFixture.setUpData();
        PurchaseOrderItemFixture.setUpData();
        PurchaseOrderAccountFixture.setUpData();
        PurchaseOrderCapitalAssetSystemFixture.setUpData();
        PurchaseOrderCapitalAssetItemFixture.setUpData();
        PurchaseOrderCapitalAssetLocationFixture.setUpData();
        PaymentRequestDocumentFixture.setUpData();
        PaymentRequestItemFixture.setUpData();
        PaymentRequestAccountFixture.setUpData();
        PaymentRequestAccountRevisionFixture.setUpData();
        CreditMemoDocumentFixture.setUpData();
        CreditMemoItemFixture.setUpData();
        CreditMemoAccountFixture.setUpData();
        CreditMemoAccountRevisionFixture.setUpData();
        EntryFixture.setUpData();
    }
}
