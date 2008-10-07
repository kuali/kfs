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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.sql.SqlDigester;
import org.kuali.kfs.module.cab.sql.SqlFileParser;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.DateUtils;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This abstract test class will provide the SQL inserts required to perform the testing CAB batch extract related services
 */
public abstract class BatchTestBase extends KualiTestBase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        updateLastExtractTime();
        prepareTestDataRecords();
    }

    protected void updateLastExtractTime() {
        updateLastCabExtractTime();
        updateLastPreTagExtractTime();
    }

    private void updateLastCabExtractTime() {
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put("parameterNamespaceCode", CabConstants.Parameters.NAMESPACE);
        primaryKeys.put("parameterDetailTypeCode", CabConstants.Parameters.DETAIL_TYPE_BATCH);
        primaryKeys.put("parameterName", CabConstants.Parameters.LAST_EXTRACT_TIME);
        Parameter lastExtractTime = (Parameter) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Parameter.class, primaryKeys);
        if (ObjectUtils.isNotNull(lastExtractTime)) {
            SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            lastExtractTime.setParameterValue(fmt.format(DateUtils.addDays(new Date(), -1)));
            SpringContext.getBean(BusinessObjectService.class).save(lastExtractTime);
        }
        else {
            fail("Could not find the parameter LAST_EXTRACT_TIME");
        }
    }

    private void updateLastPreTagExtractTime() {
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put("parameterNamespaceCode", CabConstants.Parameters.NAMESPACE);
        primaryKeys.put("parameterDetailTypeCode", CabConstants.Parameters.DETAIL_TYPE_PRE_ASSET_TAGGING_STEP);
        primaryKeys.put("parameterName", CabConstants.Parameters.LAST_EXTRACT_DATE);
        Parameter lastExtractTime = (Parameter) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Parameter.class, primaryKeys);
        if (ObjectUtils.isNotNull(lastExtractTime)) {
            SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
            lastExtractTime.setParameterValue(fmt.format(DateUtils.addDays(new Date(), -1)));
            SpringContext.getBean(BusinessObjectService.class).save(lastExtractTime);
        }
        else {
            fail("Could not find the parameter LAST_EXTRACT_TIME");
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        clearTestDataRecords();
    }

    protected void executeSqls(String sqlFileName, Connection connection) throws SQLException {
        List<SqlDigester> sqls = SqlFileParser.parseSqls(sqlFileName, ";");

        for (SqlDigester sqlStr : sqls) {
            PreparedStatement prepareStatement = connection.prepareStatement(sqlStr.toSql());
            prepareStatement.execute();
            prepareStatement.close();
        }
    }

    protected void clearTestDataRecords() throws SQLException {
        Connection connection = getConnection();
        // clean again
        executeSqls("org/kuali/kfs/module/cab/sql/cleanup.sql", connection);
        connection.close();
    }

    protected void prepareTestDataRecords() throws SQLException {
        Connection connection = getConnection();
        // clean first
        executeSqls("org/kuali/kfs/module/cab/sql/cleanup.sql", connection);
        // reqs
        executeSqls("org/kuali/kfs/module/cab/sql/reqs/en_doc_hdr_t.sql", connection);
        executeSqls("org/kuali/kfs/module/cab/sql/reqs/fp_doc_header_t.sql", connection);
        executeSqls("org/kuali/kfs/module/cab/sql/reqs/fs_doc_header_t.sql", connection);
        executeSqls("org/kuali/kfs/module/cab/sql/reqs/pur_reqs_t.sql", connection);
        executeSqls("org/kuali/kfs/module/cab/sql/reqs/pur_reqs_itm_t.sql", connection);
        executeSqls("org/kuali/kfs/module/cab/sql/reqs/pur_reqs_acct_t.sql", connection);
        // po
        executeSqls("org/kuali/kfs/module/cab/sql/po/en_doc_hdr_t.sql", connection);
        executeSqls("org/kuali/kfs/module/cab/sql/po/fp_doc_header_t.sql", connection);
        executeSqls("org/kuali/kfs/module/cab/sql/po/fs_doc_header_t.sql", connection);
        executeSqls("org/kuali/kfs/module/cab/sql/po/pur_po_t.sql", connection);
        executeSqls("org/kuali/kfs/module/cab/sql/po/pur_po_itm_t.sql", connection);
        executeSqls("org/kuali/kfs/module/cab/sql/po/pur_po_acct_t.sql", connection);

        // preq
        executeSqls("org/kuali/kfs/module/cab/sql/preq/en_doc_hdr_t.sql", connection);
        executeSqls("org/kuali/kfs/module/cab/sql/preq/fp_doc_header_t.sql", connection);
        executeSqls("org/kuali/kfs/module/cab/sql/preq/fs_doc_header_t.sql", connection);
        executeSqls("org/kuali/kfs/module/cab/sql/preq/ap_pmt_rqst_t.sql", connection);
        executeSqls("org/kuali/kfs/module/cab/sql/preq/ap_pmt_rqst_itm_t.sql", connection);
        executeSqls("org/kuali/kfs/module/cab/sql/preq/ap_pmt_rqst_acct_t.sql", connection);
        executeSqls("org/kuali/kfs/module/cab/sql/preq/ap_pmt_rqst_acct_hist_t.sql", connection);
        executeSqls("org/kuali/kfs/module/cab/sql/preq/gl_entry_t.sql", connection);
        // cm
        executeSqls("org/kuali/kfs/module/cab/sql/cm/en_doc_hdr_t.sql", connection);
        executeSqls("org/kuali/kfs/module/cab/sql/cm/fp_doc_header_t.sql", connection);
        executeSqls("org/kuali/kfs/module/cab/sql/cm/fs_doc_header_t.sql", connection);
        executeSqls("org/kuali/kfs/module/cab/sql/cm/ap_crdt_memo_t.sql", connection);
        executeSqls("org/kuali/kfs/module/cab/sql/cm/ap_crdt_memo_itm_t.sql", connection);
        executeSqls("org/kuali/kfs/module/cab/sql/cm/ap_crdt_memo_acct_t.sql", connection);
        executeSqls("org/kuali/kfs/module/cab/sql/cm/ap_crdt_memo_acct_hist_t.sql", connection);
        executeSqls("org/kuali/kfs/module/cab/sql/cm/gl_entry_t.sql", connection);

        executeSqls("org/kuali/kfs/module/cab/sql/ar/en_doc_hdr_t.sql", connection);
        executeSqls("org/kuali/kfs/module/cab/sql/ar/fp_doc_header_t.sql", connection);
        executeSqls("org/kuali/kfs/module/cab/sql/ar/gl_entry_t.sql", connection);
        connection.close();
    }

    private Connection getConnection() throws SQLException {
        DataSource dataSource = SpringContext.getBean(DataSource.class);
        Connection connection = dataSource.getConnection();
        return connection;
    }
}
