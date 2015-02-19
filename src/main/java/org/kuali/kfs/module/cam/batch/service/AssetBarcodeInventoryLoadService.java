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
