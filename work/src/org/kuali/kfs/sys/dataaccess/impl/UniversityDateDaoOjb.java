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

import java.sql.Date;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.dao.UniversityDateDao;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * @author jsissom
 *
 */
public class UniversityDateDaoOjb extends PersistenceBrokerDaoSupport implements UniversityDateDao {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UniversityDateDaoOjb.class);

  public UniversityDateDaoOjb() {
    super();
  }
  
  public UniversityDate getByPrimaryKey(Date date) {
    LOG.debug("getByPrimaryKey() started");

    Criteria crit = new Criteria();
    crit.addEqualTo("universityDate",date);

    QueryByCriteria qbc = QueryFactory.newQuery(UniversityDate.class,crit);

    return (UniversityDate)getPersistenceBrokerTemplate().getObjectByQuery(qbc);
  }
  
  public UniversityDate getByPrimaryKey(java.util.Date date) {
      return getByPrimaryKey(convertDate(date));
  }
  
  private java.sql.Date convertDate(java.util.Date date) {
      return new Date(date.getTime());
  }
  
  public UniversityDate getLastFiscalYearDate(Integer fiscalYear) {
      ReportQueryByCriteria subQuery;
      Criteria subCrit = new Criteria();
      Criteria crit = new Criteria();

      subCrit.addEqualTo("universityFiscalYear", fiscalYear);
      subQuery = QueryFactory.newReportQuery(UniversityDate.class, subCrit);
      subQuery.setAttributes(new String[] { "max(univ_dt)" });

      crit.addGreaterOrEqualThan("universityDate", subQuery);
      
      QueryByCriteria qbc = QueryFactory.newQuery(UniversityDate.class,crit);

      return (UniversityDate)getPersistenceBrokerTemplate().getObjectByQuery(qbc);
  }
  
  public UniversityDate getFirstFiscalYearDate(Integer fiscalYear) {
      ReportQueryByCriteria subQuery;
      Criteria subCrit = new Criteria();
      Criteria crit = new Criteria();

      subCrit.addEqualTo("universityFiscalYear", fiscalYear);
      subQuery = QueryFactory.newReportQuery(UniversityDate.class, subCrit);
      subQuery.setAttributes(new String[] { "min(univ_dt)" });

      crit.addGreaterOrEqualThan("universityDate", subQuery);
      
      QueryByCriteria qbc = QueryFactory.newQuery(UniversityDate.class,crit);

      return (UniversityDate)getPersistenceBrokerTemplate().getObjectByQuery(qbc);
  }
  
}
