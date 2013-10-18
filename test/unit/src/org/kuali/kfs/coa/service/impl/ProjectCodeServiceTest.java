/*
 * Copyright 2005 The Kuali Foundation
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
package org.kuali.kfs.coa.service.impl;

import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.dataaccess.ProjectCodeDao;
import org.kuali.kfs.coa.service.impl.ProjectCodeServiceImpl;
import org.kuali.kfs.sys.context.KualiTestBase;

/**
 * This class tests the ProjectCode service.
 */
public class ProjectCodeServiceTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProjectCodeServiceTest.class);

    private FakeProjectCodeDao projectCodeDao;
    private ProjectCodeServiceImpl projectCodeService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        projectCodeDao = new FakeProjectCodeDao();
        projectCodeService = new ProjectCodeServiceImpl();
        projectCodeService.setProjectCodeDao(projectCodeDao);
    }

    public void testGetByPrimaryId() throws Exception {
        ProjectCode pc = new ProjectCode();
        pc.setCode("PROJ");
        pc.setName("PROJ Name");

        projectCodeDao.retrieved = pc;
        ProjectCode retrieved = projectCodeService.getByPrimaryId("XXX");
        assertNotNull("Didn't retrieve", retrieved);
        assertEquals("Code is wrong", "PROJ", retrieved.getCode());
        assertEquals("Name is wrong", "PROJ Name", retrieved.getName());

        projectCodeDao.retrieved = null;
        retrieved = projectCodeService.getByPrimaryId("XXX");
        assertNull("Didn't retrieve", retrieved);
    }

    public void testGetByName() throws Exception {
        ProjectCode pc = new ProjectCode();
        pc.setCode("PROJ");
        pc.setName("PROJ Name");

        projectCodeDao.retrieved = pc;
        ProjectCode retrieved = projectCodeService.getByPrimaryId("BOB");
        assertNotNull("Didn't retrieve", retrieved);
        assertEquals("Code is wrong", "PROJ", retrieved.getCode());
        assertEquals("Name is wrong", "PROJ Name", retrieved.getName());

        projectCodeDao.retrieved = null;
        retrieved = projectCodeService.getByPrimaryId("XXX");
        assertNull("Didn't retrieve", retrieved);
    }

    class FakeProjectCodeDao implements ProjectCodeDao {
        public ProjectCode retrieved = null;
        public ProjectCode saved = null;

        public ProjectCode getByName(String name) {
            return retrieved;
        }

        public ProjectCode getByPrimaryId(String projectCode) {
            return retrieved;
        }

        public void save(ProjectCode projectCode) {
            saved = projectCode;
        }
    }
}
