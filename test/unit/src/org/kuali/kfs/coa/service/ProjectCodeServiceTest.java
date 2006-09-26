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

import org.kuali.module.chart.bo.ProjectCode;
import org.kuali.module.chart.dao.ProjectCodeDao;
import org.kuali.module.chart.service.impl.ProjectCodeServiceImpl;
import org.kuali.test.KualiTestBase;

/**
 * This class tests the ProjectCode service.
 * 
 * @author Kuali Nervous System Team ()
 */
public class ProjectCodeServiceTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProjectCodeServiceTest.class);

    private FakeProjectCodeDao projectCodeDao;
    private ProjectCodeServiceImpl projectCodeService;

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
        ProjectCode retrieved = projectCodeService.getByPrimaryId("XXX");
        assertNotNull("Didn't retrieve", retrieved);
        assertEquals("Code is wrong", "PROJ", retrieved.getCode());
        assertEquals("Name is wrong", "PROJ Name", retrieved.getName());

        projectCodeDao.retrieved = null;
        retrieved = projectCodeService.getByPrimaryId("XXX");
        assertNull("Didn't retrieve", retrieved);
    }

    public void testSave() throws Exception {
        ProjectCode pc = new ProjectCode();
        pc.setCode("PROJ");
        pc.setName("PROJ Name");

        projectCodeService.save(pc);
        assertNotNull("Didn't save", projectCodeDao.saved);
        assertEquals("Code is wrong", "PROJ", projectCodeDao.saved.getCode());
        assertEquals("Name is wrong", "PROJ Name", projectCodeDao.saved.getName());
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
