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
package org.kuali.module.chart.service;

import java.util.Collections;
import java.util.List;

import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.dao.OrganizationDao;
import org.kuali.module.chart.service.impl.OrganizationServiceImpl;
import org.kuali.test.KualiTestBase;

/**
 * This class tests the Organization service.
 * 
 * @author Kuali Nervous System Team ()
 */
public class OrganizationServiceTest extends KualiTestBase {
    private OrganizationServiceImpl organizationService;
    private FakeOrganizationDao organizationDao;

    protected void setUp() throws Exception {
        super.setUp();

        organizationDao = new FakeOrganizationDao();
        organizationService = new OrganizationServiceImpl();
        organizationService.setOrganizationDao(organizationDao);
    }

    public void testSave() throws Exception {
        Org o = new Org();
        o.setChartOfAccountsCode("XX");
        o.setOrganizationCode("ZZZZ");
        o.setOrganizationName("Sleep Org");

        organizationService.save(o);
        assertNotNull("Didn't save", organizationDao.saved);
        assertEquals("Wrong chart", "XX", organizationDao.saved.getChartOfAccountsCode());
        assertEquals("Wrong code", "ZZZZ", organizationDao.saved.getOrganizationCode());
        assertEquals("Wrong name", "Sleep Org", organizationDao.saved.getOrganizationName());
    }

    public void testGetByPrimaryId() throws Exception {
        Org o = new Org();
        o.setChartOfAccountsCode("XX");
        o.setOrganizationCode("ZZZZ");
        o.setOrganizationName("Sleep Org");

        organizationDao.retrieved = o;
        Org retrieved = organizationService.getByPrimaryId("X", "Y");
        assertNotNull("Didn't save", retrieved);
        assertEquals("Wrong chart", "XX", retrieved.getChartOfAccountsCode());
        assertEquals("Wrong code", "ZZZZ", retrieved.getOrganizationCode());
        assertEquals("Wrong name", "Sleep Org", retrieved.getOrganizationName());

        organizationDao.retrieved = null;
        retrieved = organizationService.getByPrimaryId("X", "Y");
        assertNull("Retrieved org that shouldn't have existed", retrieved);
    }

    class FakeOrganizationDao implements OrganizationDao {
        public Org saved;
        public Org retrieved;

        public Org getByPrimaryId(String chartOfAccountsCode, String organizationCode) {
            return retrieved;
        }

        public void save(Org organization) {
            saved = organization;
        }

        public List getActiveAccountsByOrg(String chartOfAccountsCode, String organizationCode) {
            return Collections.EMPTY_LIST;
        }

        public List getActiveChildOrgs(String chartOfAccountsCode, String organizationCode) {
            return Collections.EMPTY_LIST;
        }

    }
}
