/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.endow.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.math.BigDecimal;

import org.kuali.rice.kns.util.KualiInteger;
import org.kuali.kfs.module.endow.businessobject.HoldingHistory;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;
import org.kuali.rice.kns.service.BusinessObjectService;

/**
 * Contains methods that test the HoldingHistoryService.
 */
@ConfigureContext(session = khuntley)
public class HoldingHistoryServiceTest extends KualiTestBase {

    private HoldingHistoryService holdingHistoryService;
    private BusinessObjectService businessObjectService;
    private UnitTestSqlDao unitTestSqlDao;
    
    /**
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        holdingHistoryService = SpringContext.getBean(HoldingHistoryService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);        
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    /**
     * Retrieves the records from history holding table for the given security id and monthend id
     * First use case is to set monthendid = 0 and making sure no records will be retrieved.
     */
    public void testGetHoldingHistoryBySecuritIdAndMonthEndId_NoRecords() {
        String securityId = "123456789";
        KualiInteger monthEndId = new KualiInteger(0);
        
        // there should not be any records that match monthEndId = 0
        Collection<HoldingHistory> holdingHistory = new ArrayList();  
        holdingHistory = holdingHistoryService.getHoldingHistoryBySecuritIdAndMonthEndId(securityId, monthEndId);
        assertTrue("There will not be any records since MonthEndID = 0 does not correspond to any date", holdingHistory.isEmpty());
    }
    
    /**
     * Retrieves the records from history holding table for the given security id and monthend id
     * First use case is to set monthendid = 0 and making sure no records will be retrieved.
     */
    public void testGetHoldingHistoryBySecuritIdAndMonthEndId_RecordsFound() {
        KualiInteger monthEndId = new KualiInteger(0);
        
        //first add into END_ME_DT_T with monthendid = 0 so this record would be unique.
        String sqlForMonthEndId = "INSERT INTO END_ME_DT_T VALUES ('0'," + unitTestSqlDao.getDbPlatform().getStrToDateFunction() + "('07/07/2010 12:34:42 PM'," + unitTestSqlDao.getDbPlatform().getDateFormatString("DD/MM/YYYY HH12:MI:SS PM") + "),'TEST TEST TEST',1)";
        int rowsForMonthEndId = unitTestSqlDao.sqlCommand(sqlForMonthEndId);   
        
        Collection<HoldingHistory> holdingHistory = new ArrayList();         
        //insert into end_hldg_hist_t a dummy record so it can be queries.
        String sqlForHoldingHistory = "INSERT INTO END_HLDG_HIST_T VALUES ('032A017014',0,'094420981','0NI',1,'I',0,0,0,0," + unitTestSqlDao.getDbPlatform().getStrToDateFunction() + "('07/07/2010 12:34:42 PM'," + unitTestSqlDao.getDbPlatform().getDateFormatString("DD/MM/YYYY HH12:MI:SS PM") + "),0,0," + unitTestSqlDao.getDbPlatform().getStrToDateFunction() + "('07/07/2010 12:34:42 PM'," + unitTestSqlDao.getDbPlatform().getDateFormatString("DD/MM/YYYY HH12:MI:SS PM") + "),0,'TEST TEST TEST',1,0,0,0,0)";
        int rows = unitTestSqlDao.sqlCommand(sqlForHoldingHistory);
        assertEquals("Should have inserted 1 row", 1, rows);
        
        String securityId = "094420981";
        holdingHistory = holdingHistoryService.getHoldingHistoryBySecuritIdAndMonthEndId(securityId, monthEndId);
        assertTrue("There should have been atleast one record since we just added it!", !holdingHistory.isEmpty());
    }
}
