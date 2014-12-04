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
package org.kuali.kfs.sys.dataaccess;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.util.TransactionalServiceUtils;

@ConfigureContext
public class TestUnitTestSqlDao extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TestUnitTestSqlDao.class);

    private UnitTestSqlDao unitTestSqlDao;

    public TestUnitTestSqlDao() {
        super();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);
    }

    public void testSelect() throws Exception {
        LOG.debug("testSelect() started");

        List results = unitTestSqlDao.sqlSelect("select 1 from dual");

        assertNotNull("List shouldn't be null", results);
        assertEquals("Should return 1 result", 1, results.size());

        Iterator i = results.iterator();
        if (i.hasNext()) {
            Map m = (Map) i.next();
            assertEquals("Map should have 1 field", 1, m.size());
            Number value = (Number) m.get("1");
            assertEquals("Field should equal 1", 1.00, value.doubleValue(), 0.01);
        }
        TransactionalServiceUtils.exhaustIterator(i);
    }

    public void testAllSql() throws Exception {
        LOG.debug("testAllSql() started");

        // Delete from a table (just in case the code is already there)
        unitTestSqlDao.sqlCommand("delete from SH_STATE_T where POSTAL_STATE_CD = 'JJ'");

        // Insert into the table
        int rows = unitTestSqlDao.sqlCommand("insert into SH_STATE_T (POSTAL_STATE_CD,POSTAL_STATE_NM, OBJ_ID) values ('JJ','JJSTATE', 'BLAH BLAH BLAH')");
        assertEquals("Should have inserted 1 row", 1, rows);

        List results = unitTestSqlDao.sqlSelect("select POSTAL_STATE_CD,POSTAL_STATE_NM from SH_STATE_T where POSTAL_STATE_CD = 'JJ'");
        assertNotNull("List shouldn't be null", results);
        assertEquals("Should return 1 result", 1, results.size());
        Iterator i = results.iterator();
        Map row = (Map) i.next();

        assertEquals("State code should be JJ", "JJ", (String) row.get("POSTAL_STATE_CD"));
        assertEquals("State name should be JJSTATE", "JJSTATE", (String) row.get("POSTAL_STATE_NM"));

        rows = unitTestSqlDao.sqlCommand("update SH_STATE_T set POSTAL_STATE_NM = 'JJXX' where POSTAL_STATE_CD = 'JJ'");
        assertEquals("Should have updated 1 row", 1, rows);

        results = unitTestSqlDao.sqlSelect("select POSTAL_STATE_CD,POSTAL_STATE_NM from SH_STATE_T where POSTAL_STATE_CD = 'JJ'");
        assertNotNull("List shouldn't be null", results);
        assertEquals("Should return 1 result", 1, results.size());
        i = results.iterator();
        row = (Map) i.next();

        assertEquals("State code should be JJ", "JJ", (String) row.get("POSTAL_STATE_CD"));
        assertEquals("State name should be JJXX", "JJXX", (String) row.get("POSTAL_STATE_NM"));

        rows = unitTestSqlDao.sqlCommand("delete from SH_STATE_T where POSTAL_STATE_CD = 'JJ'");
        assertEquals("Should have deleted 1 row", 1, rows);
    }
}
