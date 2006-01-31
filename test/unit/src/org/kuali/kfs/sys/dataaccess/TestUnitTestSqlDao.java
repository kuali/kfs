/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.dao.ojb;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.gl.dao.UnitTestSqlDao;
import org.kuali.test.KualiTestBaseWithSpring;

public class TestUnitTestSqlDao extends KualiTestBaseWithSpring {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TestUnitTestSqlDao.class);

  private UnitTestSqlDao unitTestSqlDao;

  public TestUnitTestSqlDao() {
    super();
  }

  protected void setUp() throws Exception {
    super.setUp();

    unitTestSqlDao = (UnitTestSqlDao)SpringServiceLocator.getBeanFactory().getBean("glUnitTestSqlDao");
  }

  public void testSelect() throws Exception {
    LOG.debug("testSelect() started");

    List results = unitTestSqlDao.sqlSelect("select 1 from dual");

    assertNotNull("List shouldn't be null",results);
    assertEquals("Should return 1 result",1,results.size());
    
    Iterator i = results.iterator();
    if ( i.hasNext() ) {
      Map m = (Map)i.next();
      assertEquals("Map should have 1 field",1,m.size());
      BigDecimal value = (BigDecimal)m.get("1");
      assertEquals("Field should equal 1",1.00,value.doubleValue(),0.01);
    }
  }

  public void testAllSql() throws Exception {
    LOG.debug("testAllSql() started");

    // Delete from a table (just in case the code is already there)
    unitTestSqlDao.sqlCommand("delete from SH_STATE_T where POSTAL_STATE_CD = 'JJ'");

    // Insert into the table
    int rows = unitTestSqlDao.sqlCommand("insert into SH_STATE_T (POSTAL_STATE_CD,POSTAL_STATE_NM) values ('JJ','JJSTATE')");
    assertEquals("Should have inserted 1 row",1,rows);

    List results = unitTestSqlDao.sqlSelect("select POSTAL_STATE_CD,POSTAL_STATE_NM from SH_STATE_T where POSTAL_STATE_CD = 'JJ'");
    assertNotNull("List shouldn't be null",results);
    assertEquals("Should return 1 result",1,results.size());
    Iterator i = results.iterator();
    Map row = (Map)i.next();

    assertEquals("State code should be JJ","JJ",(String)row.get("POSTAL_STATE_CD"));
    assertEquals("State name should be JJSTATE","JJSTATE",(String)row.get("POSTAL_STATE_NM"));

    rows = unitTestSqlDao.sqlCommand("update SH_STATE_T set POSTAL_STATE_NM = 'JJXX' where POSTAL_STATE_CD = 'JJ'");
    assertEquals("Should have updated 1 row",1,rows);

    results = unitTestSqlDao.sqlSelect("select POSTAL_STATE_CD,POSTAL_STATE_NM from SH_STATE_T where POSTAL_STATE_CD = 'JJ'");
    assertNotNull("List shouldn't be null",results);
    assertEquals("Should return 1 result",1,results.size());
    i = results.iterator();
    row = (Map)i.next();

    assertEquals("State code should be JJ","JJ",(String)row.get("POSTAL_STATE_CD"));
    assertEquals("State name should be JJXX","JJXX",(String)row.get("POSTAL_STATE_NM"));

    rows = unitTestSqlDao.sqlCommand("delete from SH_STATE_T where POSTAL_STATE_CD = 'JJ'");
    assertEquals("Should have deleted 1 row",1,rows);
  }
}
