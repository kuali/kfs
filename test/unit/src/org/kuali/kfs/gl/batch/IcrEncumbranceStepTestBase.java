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
package org.kuali.kfs.gl.batch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.IcrEncumbranceService;
import org.kuali.kfs.gl.batch.service.impl.IcrEncumbranceServiceImpl;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.ProxyUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UniversityDateServiceFixture;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;


/**
 * This class manages the setUp and tearDown of services that multiple children use
 */
public abstract class IcrEncumbranceStepTestBase extends KualiTestBase {

    // Services
    protected ConfigurationService configurationService;
    protected IcrEncumbranceService icrEncumbranceService;
    protected DateTimeService dateTimeService;

    // Exposed for IcrEncumbranceFileRenameStepTest
    protected List<String> fileNames;
    protected String batchFileDirectoryName;




    /**
     * This method sets up services needed by children classes.
     *
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.configurationService = SpringContext.getBean(ConfigurationService.class);
        this.dateTimeService = SpringContext.getBean(DateTimeService.class);
        this.icrEncumbranceService = SpringContext.getBean(IcrEncumbranceService.class);

        // If we have time, see if it is possible to inject the date service via Spring config (spring-gl-test.xml),
        // the crux is injecting the fixture's resulting createUniversityDateService() object through configuration.
        icrEncumbranceService = (IcrEncumbranceServiceImpl) ProxyUtils.getTargetIfProxied(icrEncumbranceService);
        ((IcrEncumbranceServiceImpl)icrEncumbranceService).setUniversityDateService(UniversityDateServiceFixture.DATE_2009_03_14.createUniversityDateService());

        initFileNames();
    }

    /*
     * Use a combination of the configuration service and constants to
     * create file paths that children classes generate files to.
     */
    private void initFileNames() {
        batchFileDirectoryName = configurationService.getPropertyValueAsString("staging.directory") + File.separator +"gl/originEntry";

        fileNames = new ArrayList<String>();
        fileNames.add(batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.ICR_ENCUMBRANCE_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION);
        fileNames.add(batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.ICR_ENCUMBRANCE_POSTER_INPUT_FILE  + GeneralLedgerConstants.BatchFileSystem.EXTENSION);
        fileNames.add(batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.ICR_ENCUMBRANCE_POSTER_ERROR_OUTPUT_FILE  + GeneralLedgerConstants.BatchFileSystem.EXTENSION);
    }


    /**
     * This method ensures any generated ICR Encumbrance files are deleted.
     *
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    public void tearDown() throws Exception {
        super.tearDown();

        for(String fileName : fileNames){
            File file = new File(fileName);
            FileUtils.deleteQuietly(file);
        }
    }


    /**
     * This method corresponds to the Step#execute() method, this is
     * the entry point for real work being done.
     */
    public abstract void testExecute();
}
