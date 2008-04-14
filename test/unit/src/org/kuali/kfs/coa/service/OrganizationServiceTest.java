/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.chart.service;

import java.util.Collections;
import java.util.List;

import org.kuali.kfs.context.KualiTestBase;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.dao.OrganizationDao;
import org.kuali.module.chart.service.impl.OrganizationServiceImpl;

/**
 * This class tests the Organization service.
 */
public class OrganizationServiceTest extends KualiTestBase {
    private OrganizationServiceImpl organizationService;
    private FakeOrganizationDao organizationDao;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        organizationDao = new FakeOrganizationDao();
        organizationService = new OrganizationServiceImpl();
        organizationService.setOrganizationDao(organizationDao);
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

        public List<Org> getActiveOrgsByType(String organizationTypeCode) {
            return Collections.EMPTY_LIST;
        }

        public String[] getRootOrganizationCode(String rootChart, String selfReportsORgType) {
            String[] returnValues = { null, null };
            return returnValues;
        }

    }
}
