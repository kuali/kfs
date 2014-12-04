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
package org.kuali.kfs.module.cam.document.service;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.batch.service.AssetBarcodeInventoryLoadService;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.BarcodeInventoryErrorDetail;
import org.kuali.kfs.module.cam.document.web.struts.AssetBarCodeInventoryInputFileForm;
import org.kuali.kfs.module.cam.fixture.BarcodeInventoryServiceFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.BusinessObjectService;

// @ConfigureContext(session = UserNameFixture.kfs)
@ConfigureContext(session = UserNameFixture.bomiddle)
public class AssetBarcodeInventoryLoadServiceTest extends KualiTestBase {
    private static Logger LOG = Logger.getLogger(AssetBarcodeInventoryLoadServiceTest.class);

    private DateTimeService dateTimeService;
    private AssetBarcodeInventoryLoadService assetBarcodeInventoryLoadService;
    private BusinessObjectService businessObjectService;
    private static final String BARCODE = "barcode";
    private static final String ERROR = "error";
    private static final String VALID = "valid";
    private static final String FORMAT = "format";
    private static final String INVALID_FORMAT_MSG = "Invalid file format";
    private static final String FILE_NOT_PROCESSED_MSG = "File was not processed successfully.";

    /**
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        assetBarcodeInventoryLoadService = SpringContext.getBean(AssetBarcodeInventoryLoadService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        dateTimeService = SpringContext.getBean(DateTimeService.class);
    }

    /**
     * This method...
     */
    public void testIsFileFormatValid() {
        File[] listOfFiles = this.getFiles(false);
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile() && (listOfFiles[i].length() > 0)) {
                LOG.info("*** testIsFileFormatValid() - Running test on file " + listOfFiles[i].getName());

                if ((listOfFiles[i].getName().indexOf(ERROR) != -1) && (listOfFiles[i].getName().indexOf(FORMAT) != -1)) {
                    assertFalse(INVALID_FORMAT_MSG, assetBarcodeInventoryLoadService.isFileFormatValid(listOfFiles[i]));
                }
                else {
                    assertTrue(INVALID_FORMAT_MSG, assetBarcodeInventoryLoadService.isFileFormatValid(listOfFiles[i]));
                }
            }
        }
    }

    /**
     * This method...
     */
    public void testProcessFile() {
        AssetBarCodeInventoryInputFileForm form = new AssetBarCodeInventoryInputFileForm();
        File[] filesToProcess = this.getFiles(true);
        for (File file : filesToProcess) {
            LOG.info("*** testProcessFile() - Running test on file " + file.getName());
            try {
                assetBarcodeInventoryLoadService.processFile(file, form);
            }
            catch (IllegalArgumentException e) {
                Throwable cause = e.getCause();
                Throwable origin = e;
                while (cause != null) {
                    origin = cause;
                    cause = cause.getCause();
                }
                assertTrue(ValidationException.class.isAssignableFrom(origin.getClass()));
            }
        }
    }

    /**
     * This method...
     * 
     * @return
     */
    private File[] getFiles(boolean onlyValid) {
        File[] listOfFiles;
        try {
            Class<AssetBarcodeInventoryLoadServiceTest> THIS_CLASS = AssetBarcodeInventoryLoadServiceTest.class;
            String childPath = THIS_CLASS.getPackage().getName().replace( '.', '/');

            File directory = new File(new File(THIS_CLASS.getProtectionDomain().getCodeSource().getLocation().toURI()), childPath);
            if (!onlyValid) {
                listOfFiles = directory.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.endsWith(CamsConstants.BarCodeInventory.DATA_FILE_EXTENSION) && name.startsWith(BARCODE) && (name.indexOf(ERROR) != -1) || (name.indexOf(VALID) != -1);
                    }
                });
            }
            else {
                listOfFiles = directory.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.endsWith(CamsConstants.BarCodeInventory.DATA_FILE_EXTENSION) && name.startsWith(BARCODE) && (name.indexOf(VALID) != -1);
                    }
                });
            }
        }
        catch (URISyntaxException e) {
            throw new AssertionError(e);
        }
        return listOfFiles;
    }

    /**
     * test the UpdateAssetInformation
     */
    public void testUpdateAssetInformation() {
        BarcodeInventoryErrorDetail barcodeInventoryErrorDetail;
        List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetails = BarcodeInventoryServiceFixture.DATA.getBarcodeInventoryDetail();
        Map<String, String> fieldValues = new HashMap<String, String>();

        for (int row = 0; row < barcodeInventoryErrorDetails.size(); row++) {
            barcodeInventoryErrorDetail = barcodeInventoryErrorDetails.get(row);

            // Saving record
            assetBarcodeInventoryLoadService.updateAssetInformation(barcodeInventoryErrorDetail,false);

            // Confirming data was sucessfully stored.
            fieldValues.put(CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER, barcodeInventoryErrorDetail.getAssetTagNumber());
            Asset asset = ((List<Asset>) businessObjectService.findMatching(Asset.class, fieldValues)).get(0);

            assertTrue("Error on data", asset.getInventoryScannedCode().equals(barcodeInventoryErrorDetail.isUploadScanIndicator() ? CamsConstants.BarCodeInventory.BCI_SCANED_INTO_DEVICE : CamsConstants.BarCodeInventory.BCI_MANUALLY_KEYED_CODE) && asset.getBuildingCode().equals(barcodeInventoryErrorDetail.getBuildingCode()) && asset.getBuildingRoomNumber().equals(barcodeInventoryErrorDetail.getBuildingRoomNumber()) && asset.getBuildingSubRoomNumber().equals(barcodeInventoryErrorDetail.getBuildingSubRoomNumber()) && asset.getCampusCode().equals(barcodeInventoryErrorDetail.getCampusCode()) && asset.getConditionCode().equals(barcodeInventoryErrorDetail.getAssetConditionCode()));
        }
    }
}
