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
    private static final String SQL_PACKAGE = "org/kuali/kfs/module/cab/sql/";

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
        Parameter lastExtractTime = findCabExtractTimeParam();
        if (ObjectUtils.isNotNull(lastExtractTime)) {
            SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            lastExtractTime.setParameterValue(fmt.format(DateUtils.addDays(new Date(), -1)));
            SpringContext.getBean(BusinessObjectService.class).save(lastExtractTime);
        }
        else {
            fail("Could not find the parameter LAST_EXTRACT_TIME");
        }
    }

    protected Parameter findCabExtractTimeParam() {
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put("parameterNamespaceCode", CabConstants.Parameters.NAMESPACE);
        primaryKeys.put("parameterDetailTypeCode", CabConstants.Parameters.DETAIL_TYPE_BATCH);
        primaryKeys.put("parameterName", CabConstants.Parameters.LAST_EXTRACT_TIME);
        Parameter lastExtractTime = (Parameter) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Parameter.class, primaryKeys);
        return lastExtractTime;
    }

    private void updateLastPreTagExtractTime() {
        Parameter lastExtractTime = findPretagExtractDateParam();
        if (ObjectUtils.isNotNull(lastExtractTime)) {
            SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
            lastExtractTime.setParameterValue(fmt.format(DateUtils.addDays(new Date(), -1)));
            SpringContext.getBean(BusinessObjectService.class).save(lastExtractTime);
        }
        else {
            fail("Could not find the parameter LAST_EXTRACT_TIME");
        }
    }

    protected Parameter findPretagExtractDateParam() {
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put("parameterNamespaceCode", CabConstants.Parameters.NAMESPACE);
        primaryKeys.put("parameterDetailTypeCode", CabConstants.Parameters.DETAIL_TYPE_PRE_ASSET_TAGGING_STEP);
        primaryKeys.put("parameterName", CabConstants.Parameters.LAST_EXTRACT_DATE);
        Parameter lastExtractTime = (Parameter) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Parameter.class, primaryKeys);
        return lastExtractTime;
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        clearTestDataRecords();
    }

    protected void executeSqls(String sqlFileName, Connection connection) throws SQLException {
        List<SqlDigester> sqls = SqlFileParser.parseSqls(SQL_PACKAGE + sqlFileName, ";");

        for (SqlDigester sqlStr : sqls) {
            PreparedStatement prepareStatement = connection.prepareStatement(sqlStr.toSql());
            prepareStatement.execute();
            prepareStatement.close();
        }
    }

    protected void clearTestDataRecords() throws SQLException {
        Connection connection = getConnection();
        // clean again
        executeSqls("cleanup.sql", connection);
        connection.close();
    }

    protected void prepareTestDataRecords() throws SQLException {
        Connection connection = getConnection();
        // clean first
        executeSqls("cleanup.sql", connection);
        // reqs
        executeSqls("reqs/krew_doc_hdr_t.sql", connection);
        executeSqls("reqs/krns_doc_hdr_t.sql", connection);
        executeSqls("reqs/fs_doc_header_t.sql", connection);
        executeSqls("reqs/pur_reqs_t.sql", connection);
        executeSqls("reqs/pur_reqs_itm_t.sql", connection);
        executeSqls("reqs/pur_reqs_acct_t.sql", connection);
        // po
        executeSqls("po/krew_doc_hdr_t.sql", connection);
        executeSqls("po/krns_doc_hdr_t.sql", connection);
        executeSqls("po/fs_doc_header_t.sql", connection);
        executeSqls("po/pur_po_t.sql", connection);
        executeSqls("po/pur_po_itm_t.sql", connection);
        executeSqls("po/pur_po_acct_t.sql", connection);
        executeSqls("po/pur_po_cptl_ast_sys_t.sql", connection);
        executeSqls("po/pur_po_cptl_ast_itm_t.sql", connection);
        executeSqls("po/pur_po_cptl_ast_itm_ast_t.sql", connection);
        executeSqls("po/pur_po_cptl_ast_loc_t.sql", connection);
        // preq
        executeSqls("preq/krew_doc_hdr_t.sql", connection);
        executeSqls("preq/krns_doc_hdr_t.sql", connection);
        executeSqls("preq/fs_doc_header_t.sql", connection);
        executeSqls("preq/ap_pmt_rqst_t.sql", connection);
        executeSqls("preq/ap_pmt_rqst_itm_t.sql", connection);
        executeSqls("preq/ap_pmt_rqst_acct_t.sql", connection);
        executeSqls("preq/ap_pmt_rqst_acct_chg_t.sql", connection);
        executeSqls("preq/gl_entry_t.sql", connection);
        // cm
        executeSqls("cm/krew_doc_hdr_t.sql", connection);
        executeSqls("cm/krns_doc_hdr_t.sql", connection);
        executeSqls("cm/fs_doc_header_t.sql", connection);
        executeSqls("cm/ap_crdt_memo_t.sql", connection);
        executeSqls("cm/ap_crdt_memo_itm_t.sql", connection);
        executeSqls("cm/ap_crdt_memo_acct_t.sql", connection);
        executeSqls("cm/ap_crdt_memo_acct_chg_t.sql", connection);
        executeSqls("cm/gl_entry_t.sql", connection);
        // FP invoice doc
        executeSqls("ar/krew_doc_hdr_t.sql", connection);
        executeSqls("ar/krns_doc_hdr_t.sql", connection);
        executeSqls("ar/gl_entry_t.sql", connection);
        connection.close();
    }

    protected Connection getConnection() throws SQLException {
        DataSource dataSource = SpringContext.getBean(DataSource.class);
        Connection connection = dataSource.getConnection();
        return connection;
    }
}
