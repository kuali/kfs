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
package org.kuali.kfs.sys.batch.service.impl;

import java.io.File;
import java.util.Map;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.batch.BatchFile;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Class to test implementation of the BatchFileAdminAuthorizationService
 *
 */
@ConfigureContext(session=UserNameFixture.khuntley)
public class BatchFileAdminAuthorizationServiceTest extends KualiTestBase {
    private BatchFileAdminAuthorizationServiceImpl batchFileAdminAuthorizationService;
    private ConfigurationService kualiConfigurationService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        batchFileAdminAuthorizationService = new BatchFileAdminAuthorizationServiceImpl();
        kualiConfigurationService = SpringContext.getBean(ConfigurationService.class);
    }

    /**
     * The super method sets an OS specific absolute path of the file into the 'filePath' attribute.
     * The IU class overrides this behavior by setting only the relative path and using '/' as the file separator
     */
    public void testGeneratePermissionDetails() {
        BatchFile batchFile = getBatchFile();
        Person user = GlobalVariables.getUserSession().getPerson();

        //the method we're testing
        Map<String, String> permissionDetails = batchFileAdminAuthorizationService.generatePermissionDetails(batchFile, user);
        System.out.println(permissionDetails);

        String permissionFilePath = permissionDetails.get(KfsKimAttributes.FILE_PATH);
        String expectedFilePath = "staging/sys/batchContainer/placeholder.txt";

        assertEquals( "Wrong filePath was returned", expectedFilePath, permissionFilePath);
    }

    private BatchFile getBatchFile() {
        //Any batch file should do
        String batchContainerDir = kualiConfigurationService.getPropertyValueAsString("staging.directory.sys.batchContainer");

        String batchFileName = batchContainerDir + File.separator + "placeholder.txt";
        File file = new File(batchFileName).getAbsoluteFile();

        BatchFile batchFile = new BatchFile();
        batchFile.setFile(file);

        return batchFile;
    }
}
