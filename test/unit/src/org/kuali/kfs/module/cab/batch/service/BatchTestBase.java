/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.cab.batch.service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.module.cab.CabConstants;
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
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This abstract test class will provide the SQL inserts required to perform the testing CAB batch extract related services
 */
public abstract class BatchTestBase extends KualiTestBase {
    protected static final String SQL_PACKAGE = "org/kuali/kfs/module/cab/sql/";
    protected DateTimeService dateTimeService;

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

    protected void updateLastCabExtractTime() {
        Parameter lastExtractTime = findCabExtractTimeParam();
        if (ObjectUtils.isNotNull(lastExtractTime)) {
            SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");


            Parameter.Builder updatedParm = Parameter.Builder.create(lastExtractTime);
            updatedParm.setValue(fmt.format(DateUtils.addDays(dateTimeService.getCurrentDate(), -1)));
            SpringContext.getBean(ParameterService.class).updateParameter(updatedParm.build());
        }
        else {
            fail("Could not find the parameter LAST_EXTRACT_TIME");
        }
    }

    protected Parameter findCabExtractTimeParam() {
        Parameter lastExtractTime = SpringContext.getBean(ParameterService.class).getParameter(CabConstants.Parameters.NAMESPACE, CabConstants.Parameters.DETAIL_TYPE_BATCH, CabConstants.Parameters.LAST_EXTRACT_TIME);
        return lastExtractTime;
    }

    protected void updateLastPreTagExtractTime() {
        Parameter lastExtractTime = findPretagExtractDateParam();
        if ( lastExtractTime != null ) {
            SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");


            Parameter.Builder updatedParm = Parameter.Builder.create(lastExtractTime);
            updatedParm.setValue(fmt.format(DateUtils.addDays(dateTimeService.getCurrentDate(), -1)));
            SpringContext.getBean(ParameterService.class).updateParameter(updatedParm.build());
        }
        else {
            fail("Could not find the parameter LAST_EXTRACT_TIME");
        }
    }

    protected Parameter findPretagExtractDateParam() {

        Parameter lastExtractTime = SpringContext.getBean(ParameterService.class).getParameter(CabConstants.Parameters.NAMESPACE, CabConstants.Parameters.DETAIL_TYPE_PRE_ASSET_TAGGING_STEP, CabConstants.Parameters.LAST_EXTRACT_DATE);
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
