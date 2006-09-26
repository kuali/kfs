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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.dao.UniversityDateDao;
import org.kuali.test.KualiTestBase;
import org.springframework.beans.factory.BeanFactory;

/**
 * @author jsissom
 * 
 */
public class TestUniversityDateDao extends KualiTestBase {

    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

    public void testGetByPrimaryKey() throws Exception {
        BeanFactory factory = SpringServiceLocator.getBeanFactory();
        assertNotNull("Factory shouldn't be null", factory);

        UniversityDateDao dao = (UniversityDateDao) factory.getBean("universityDateDao");
        assertNotNull("Dao shouldn't be null", dao);

        Date missing = sdf.parse("01/01/1901");
        UniversityDate notexist = dao.getByPrimaryKey(new java.sql.Date(missing.getTime()));
        assertNull("01/01/1901 shouldn't exist in table", notexist);

        Date notMissing = sdf.parse("08/14/1993");
        UniversityDate exist = dao.getByPrimaryKey(new java.sql.Date(notMissing.getTime()));
        assertNotNull("08/14/1993 should exist in table", exist);
    }

    public void testGetFirstLastFiscalYearDates() throws Exception {
        BeanFactory factory = SpringServiceLocator.getBeanFactory();
        assertNotNull("Factory shouldn't be null", factory);

        UniversityDateDao dao = (UniversityDateDao) factory.getBean("universityDateDao");
        assertNotNull("Dao shouldn't be null", dao);

        UniversityDate firstFiscalYearDate = dao.getFirstFiscalYearDate(new Integer(2007));
        assertEquals("07/01/2006", sdf.format(firstFiscalYearDate.getUniversityDate()));

        UniversityDate lastFiscalYearDate = dao.getLastFiscalYearDate(new Integer(2007));
        assertEquals("06/30/2007", sdf.format(lastFiscalYearDate.getUniversityDate()));
    }
}
