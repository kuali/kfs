/*
 * Copyright 2006-2008 The Kuali Foundation
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
package org.kuali.kfs.module.cam.batch.service;

import java.io.File;

import org.kuali.kfs.module.cam.businessobject.BarcodeInventoryErrorDetail;
import org.kuali.kfs.module.cam.document.web.struts.AssetBarCodeInventoryInputFileForm;
import org.kuali.rice.krad.document.Document;

/**
 * This service interface defines the methods that a ProcurementCardLoadTransactionsService implementation must provide. Provides
 * methods to load batch files for the procurement card batch job.
 */
public interface AssetBarcodeInventoryLoadService {
    /**
     * Validates and parses the file identified by the given files name. If successful, parsed entries are stored.
     * 
     * @param fileName Name of file to be uploaded and processed.
     * @return True if the file load and store was successful, false otherwise.
     */
    public boolean isFileFormatValid(File file);

    /**
     * This method creates the barcode inventory error document, validates each record, and invokes the method that actually stores
     * the record in the tables
     * 
     * @param file
     * @return
     */
    public boolean processFile(File file, AssetBarCodeInventoryInputFileForm form);


    /**
     * This method updates the asset table
     * 
     * @param barcodeInventoryErrorDetail
     */
    public void updateAssetInformation(BarcodeInventoryErrorDetail barcodeInventoryErrorDetail, boolean updateWithDateAssetWasScanned);

    /**
     * Determines whether or not the BCIE document has all its records corrected or deleted
     * 
     * @param document
     * @return
     */
    boolean isFullyProcessed(Document document);

    /**
     * Determines whether the current user is the document initiator. This method...
     * 
     * @param document
     * @return
     */
    boolean isCurrentUserInitiator(Document document);
}
