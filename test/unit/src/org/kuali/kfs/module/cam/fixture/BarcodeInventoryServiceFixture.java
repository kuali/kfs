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
package org.kuali.kfs.module.cam.fixture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobalDetail;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;
import org.kuali.kfs.sys.dataaccess.impl.UnitTestSqlDaoOjb;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.TransactionalServiceUtils;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.springframework.transaction.annotation.Transactional;

import com.mysql.jdbc.log.Log;
import org.kuali.kfs.module.cam.businessobject.BarcodeInventoryErrorDetail;

//@Transactional

public enum BarcodeInventoryServiceFixture{

    DATA();
    private BusinessObjectService businessObjectService;    
    private int testDataPos;
    private static Properties properties;
    static {
        String propertiesFileName = "org/kuali/kfs/module/cam/document/service/barcode_inventory_service.properties";
        properties = new Properties();
        try {
            properties.load(ClassLoader.getSystemResourceAsStream(propertiesFileName));
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
    }

    static String TEST_RECORD="testRecord";
    static String BCIE="bcie";
    static String FIELD_NAMES="fieldNames";
    static String NUM_OF_REC="numOfRecords";
    static String DELIMINATOR="deliminator";
    
    private BarcodeInventoryServiceFixture() {  
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);        
    }

    @SuppressWarnings("deprecation")
    public List<BarcodeInventoryErrorDetail> getBarcodeInventoryDetail() {
        Integer numOfRecords = new Integer(properties.getProperty(BCIE+"."+NUM_OF_REC));                        
        List<BarcodeInventoryErrorDetail> details = new ArrayList<BarcodeInventoryErrorDetail>();
                
        String deliminator = properties.getProperty(DELIMINATOR);
        String fieldNames = properties.getProperty(BCIE+"."+FIELD_NAMES);

        for(int i=1;i<=numOfRecords.intValue();i++) {
            String propertyKey = BCIE+"."+TEST_RECORD + i;
            details.add(CamsFixture.DATA_POPULATOR.buildTestDataObject(BarcodeInventoryErrorDetail.class, properties, propertyKey, fieldNames, deliminator));
        }
        return details;
    }
}