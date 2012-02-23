package org.kuali.kfs.sys.batch.service.impl;



import java.io.File;

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
        AttributeSet permissionDetails = batchFileAdminAuthorizationService.generatePermissionDetails(batchFile, user);
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
