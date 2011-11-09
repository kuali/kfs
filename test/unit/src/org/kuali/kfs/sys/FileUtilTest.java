/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.sys;

import java.io.File;
import java.io.FilenameFilter;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.core.api.config.property.ConfigurationService;

@ConfigureContext
public class FileUtilTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FileUtilTest.class);
    
    protected String stagingDirectory;
    
    protected String[] DATA = new String[] {
            "2007BL1031400-----    ---A2EX05BT  01LP2837509     88888------------------TEST DESCRIPTION                                      619.90D2009-02-05                                                                     0.00     200905000000000010                                                      ",
            "2007BL1031400-----    ---A2EX05BT  01LP2837509     88888------------------TEST DESCRIPTION                                      276.47D2009-02-05                                                                     0.00     200905000000000010                                                      ",
    };
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        ConfigurationService kualiConfigurationService = SpringContext.getBean(ConfigurationService.class); 
        stagingDirectory = kualiConfigurationService.getPropertyValueAsString("staging.directory") + "/";
    }
    
    public void testGetNewestDataFile() {
        final String testFilename = "testGetNewestDataFile";
        final String testFilenameUnmatched = "testUmatchedGetNewestDataFile";
        
        File batchDirectory = new File(this.stagingDirectory);
        
        // Use a filename filter matching testFilename. This shouldn't impact the logic as long as we're consistently testing with this one
        FilenameFilter filenameFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.startsWith(testFilename) &&
                        name.endsWith(GeneralLedgerConstants.BatchFileSystem.EXTENSION));
            }
        };
        
        assertNull("Shouldn't have found any files of name " + testFilename, FileUtil.getNewestFile(batchDirectory, filenameFilter));
        
        try {
            LOG.debug("Create three test files. Sleeping briefly between each to ensure unique timestamps.");
            String filePathA = this.stagingDirectory + File.separator + testFilename + "FileA" + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
            TestUtils.writeFile(filePathA, this.DATA);
            this.addGeneratedFile(filePathA);
            Thread.sleep(1000);
            String filePathB = this.stagingDirectory + File.separator + testFilename + "FileB" + GeneralLedgerConstants.BatchFileSystem.EXTENSION; 
            TestUtils.writeFile(filePathB, this.DATA);
            this.addGeneratedFile(filePathB);
            Thread.sleep(1000);
            String filePathC = this.stagingDirectory + File.separator + testFilename + "FileC" + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
            TestUtils.writeFile(filePathC, this.DATA);
            this.addGeneratedFile(filePathC);
            Thread.sleep(1000);
            String filePathUnmatched = this.stagingDirectory + File.separator + testFilenameUnmatched + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
            TestUtils.writeFile(filePathUnmatched, this.DATA);
            this.addGeneratedFile(filePathUnmatched);
        } catch (InterruptedException e) {
            assertTrue("No reason that this job should have gotten interrupted.", false);
        }
        
        File newestFile = FileUtil.getNewestFile(batchDirectory, filenameFilter);
        assertNotNull("We just created a few files but none was found, filename=" + testFilename, newestFile);
        assertTrue("Was expecting last file created. Not the case, found: " + newestFile.getName(), newestFile.getName().contains("FileC"));
        
        LOG.debug("Cleanup files after ourselves." + testFilename);
        File[] directoryListing = batchDirectory.listFiles(filenameFilter);
        for (int i = 0; i < directoryListing.length; i++) {
            File file = directoryListing[i];
            assertTrue("Delete failed. Shouldn't.", file.delete());
        }
        assertTrue("Delete failed. Shouldn't.", new File(this.stagingDirectory + File.separator + testFilenameUnmatched + GeneralLedgerConstants.BatchFileSystem.EXTENSION).delete());

    }
}
