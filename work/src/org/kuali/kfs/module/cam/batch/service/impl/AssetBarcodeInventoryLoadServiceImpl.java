/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.module.cam.batch.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.batch.service.AssetBarcodeInventoryLoadService;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.BarcodeInventoryErrorDetail;
import org.kuali.kfs.module.cam.document.BarcodeInventoryErrorDocument;
import org.kuali.kfs.module.cam.document.validation.event.ValidateBarcodeInventoryEvent;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.validation.event.DocumentSystemSaveEvent;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This is the default implementation of the AssetBarcodeInventoryLoadService interface. Handles loading, parsing, and storing of
 * incoming barcode inventory files.
 */
public class AssetBarcodeInventoryLoadServiceImpl implements AssetBarcodeInventoryLoadService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetBarcodeInventoryLoadServiceImpl.class);

    private static final int MAX_NUMBER_OF_COLUMNS = 9;
    private static final String DOCUMENT_EXPLANATION = "BARCODE ERROR INVENTORY";

    private BusinessObjectService businessObjectService;
    private WorkflowDocumentService workflowDocumentService;
    private DataDictionaryService dataDictionaryService;
    private KualiRuleService kualiRuleService;
    private DocumentService documentService;

    /**
     * @see org.kuali.module.cams.service.AssetBarcodeInventoryLoadService#isFileFormatValid(java.io.File)
     */
    public boolean isFileFormatValid(File file) {
        LOG.debug("isFileFormatValid(File file) - start");
        String fileName = file.getName();

        BufferedReader input = null;

        // Getting the length of each field that needs to be validated
        Integer campusTagNumberMaxLength = dataDictionaryService.getAttributeMaxLength(Asset.class, CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER);
        Integer inventoryScannedCodeMaxLength = new Integer(1);
        Integer InventoryDateMaxLength = dataDictionaryService.getAttributeMaxLength(BarcodeInventoryErrorDetail.class, CamsPropertyConstants.BarcodeInventory.INVENTORY_DATE);
        Integer campusCodeMaxLength = dataDictionaryService.getAttributeMaxLength(Asset.class, CamsPropertyConstants.Asset.CAMPUS_CODE);
        Integer buildingCodeMaxLength = dataDictionaryService.getAttributeMaxLength(Asset.class, CamsPropertyConstants.Asset.BUILDING_CODE);
        Integer buildingRoomNumberMaxLength = dataDictionaryService.getAttributeMaxLength(Asset.class, CamsPropertyConstants.Asset.BUILDING_ROOM_NUMBER);
        Integer buildingSubRoomNumberMaxLength = dataDictionaryService.getAttributeMaxLength(Asset.class, CamsPropertyConstants.Asset.BUILDING_SUB_ROOM_NUMBER);
        Integer conditionCodeMaxLength = dataDictionaryService.getAttributeMaxLength(Asset.class, CamsPropertyConstants.Asset.CONDITION_CODE);

        // Getting the label of each field from data dictionary.
        String campusTagNumberLabel = dataDictionaryService.getAttributeLabel(Asset.class, CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER);
        String inventoryScannedCodeLabel = dataDictionaryService.getAttributeLabel(Asset.class, CamsPropertyConstants.BarcodeInventory.UPLOAD_SCAN_INDICATOR);
        String InventoryDateLabel = dataDictionaryService.getAttributeLabel(BarcodeInventoryErrorDetail.class, CamsPropertyConstants.BarcodeInventory.INVENTORY_DATE);
        String campusCodeLabel = dataDictionaryService.getAttributeLabel(Asset.class, CamsPropertyConstants.Asset.CAMPUS_CODE);
        String buildingCodeLabel = dataDictionaryService.getAttributeLabel(Asset.class, CamsPropertyConstants.Asset.BUILDING_CODE);
        String buildingRoomNumberLabel = dataDictionaryService.getAttributeLabel(Asset.class, CamsPropertyConstants.Asset.BUILDING_ROOM_NUMBER);
        String buildingSubRoomNumberLabel = dataDictionaryService.getAttributeLabel(Asset.class, CamsPropertyConstants.Asset.BUILDING_SUB_ROOM_NUMBER);
        String conditionCodeLabel = dataDictionaryService.getAttributeLabel(Asset.class, CamsPropertyConstants.Asset.CONDITION_CODE);

        try {
            int recordCount = 0;
            String errorMsg = "";
            String errorMessage = "";
            boolean proceed = true;
            String lengthError = "exceeds maximum length";

            input = new BufferedReader(new FileReader(file));
            String line = null;


            while ((line = input.readLine()) != null) {
                recordCount++;
                errorMsg = "";
                line = StringUtils.remove(line, "\"");

                String[] column = org.springframework.util.StringUtils.delimitedListToStringArray(line, ",");

                if (MAX_NUMBER_OF_COLUMNS < column.length) {
                    // Error more columns that allowed. put it in the constants class.
                    errorMsg += "  Barcode inventory file has record(s) with more than " + MAX_NUMBER_OF_COLUMNS + " columns\n";
                    proceed = false;
                }
                else if (MAX_NUMBER_OF_COLUMNS > column.length) {
                    errorMsg += "  Barcode inventory file has record(s) with less than " + MAX_NUMBER_OF_COLUMNS + " columns\n";
                    proceed = false;
                }
                else {

                    // Validating length of each field
                    if (column[0].length() > campusTagNumberMaxLength.intValue()) {
                        errorMsg += ", " + campusTagNumberLabel;
                    }

                    if (column[1].length() > inventoryScannedCodeMaxLength.intValue()) {
                        errorMsg += ", " + inventoryScannedCodeLabel;
                    }

                    if (column[2].length() > InventoryDateMaxLength.intValue()) {
                        errorMsg += ", " + InventoryDateLabel;
                    }

                    if (column[3].length() > campusCodeMaxLength.intValue()) {
                        errorMsg += ", " + campusCodeLabel;
                    }
                    if (column[4].length() > buildingCodeMaxLength.intValue()) {
                        errorMsg += ", " + buildingCodeLabel;
                    }
                    if (column[5].length() > buildingRoomNumberMaxLength.intValue()) {
                        errorMsg += ", " + buildingRoomNumberLabel;
                    }
                    if (column[6].length() > buildingSubRoomNumberMaxLength.intValue()) {
                        errorMsg += ", " + buildingSubRoomNumberLabel;
                    }
                    if (column[7].length() > conditionCodeMaxLength.intValue()) {
                        errorMsg += ", " + conditionCodeLabel;
                    }

                    if (!StringUtils.isBlank(errorMsg)) {
                        errorMsg += " " + lengthError;
                    }

                    // Validating other than the length of the fields
                    if (!column[1].equals(CamsConstants.BarCodeInventory.BCI_SCANED_INTO_DEVICE) && !column[1].equals(CamsConstants.BarCodeInventory.BCI_MANUALLY_KEYED_CODE)) {
                        errorMsg += ", " + inventoryScannedCodeLabel + " is invalid";
                    }
                }
                if (!StringUtils.isBlank(errorMsg)) {
                    errorMsg = "Error on record number " + recordCount + ": " + errorMsg.substring(2) + "\n";
                    GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_CUSTOM, errorMsg);
                    errorMessage += errorMsg;
                    LOG.error(errorMsg);
                }
                if (!proceed)
                    break;
            }
            if (!StringUtils.isBlank(errorMessage)) {
                return false;
            }

            return true;
        }
        catch (FileNotFoundException e1) {
            LOG.error("file to parse not found " + fileName, e1);
            throw new RuntimeException("Cannot find the file requested to be parsed " + fileName + " " + e1.getMessage(), e1);
        }
        catch (Exception e) {
            LOG.error("Error running file validation - File: " + fileName, e);
            throw new IllegalArgumentException("Error running file validation - File: " + fileName);
        }
        finally {
            LOG.debug("isFileFormatValid(File file) - end");
            try {
                if (input != null) {
                    input.close();
                }
            }
            catch (IOException ex) {
                LOG.error("isFileFormatValid() error closing file.", ex);
            }
        }

    }

    /**
     * @see org.kuali.module.cams.service.AssetBarCodeInventoryLoadService#processFile(java.io.File)
     */
    public boolean processFile(File file) {
        LOG.debug("processFile(File file) - start");

        BufferedReader input = null;
        String fileName = file.getName();

        String day;
        String month;
        String year;
        String hours;
        String minutes;
        String seconds;
        boolean isValid = true;

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss", Locale.US);

        BarcodeInventoryErrorDetail barcodeInventoryErrorDetail;
        List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetails = new ArrayList<BarcodeInventoryErrorDetail>();

        try {
            Long ln = new Long(1);
            input = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = input.readLine()) != null) {
                line = StringUtils.remove(line, "\"");
                String[] lineStrings = org.springframework.util.StringUtils.delimitedListToStringArray(line, ",");

                // Parsing date so it can be validated.
                lineStrings[2] = StringUtils.rightPad(lineStrings[2].trim(), 14, "0");

                day = lineStrings[2].substring(0, 2);
                month = lineStrings[2].substring(2, 4);
                year = lineStrings[2].substring(4, 8);
                hours = lineStrings[2].substring(8, 10);
                minutes = lineStrings[2].substring(10, 12);
                seconds = lineStrings[2].substring(12);

                String stringDate = month + "/" + day + "/" + year + " " + hours + ":" + minutes + ":" + seconds;
                Timestamp timestamp = null;

                // If date has invalid format set its value to null
                try {
                    timestamp = new Timestamp(formatter.parse(stringDate).getTime());
                }
                catch (Exception e) {
                }

                // Its set to null because for some reason java parses "00000000000000" as 0002-11-30
                if (lineStrings[2].equals(StringUtils.repeat("0", 14))) {
                    timestamp = null;
                }

                barcodeInventoryErrorDetail = new BarcodeInventoryErrorDetail();
                barcodeInventoryErrorDetail.setUploadRowNumber(ln);
                barcodeInventoryErrorDetail.setAssetTagNumber(lineStrings[0].trim());
                barcodeInventoryErrorDetail.setUploadScanIndicator(lineStrings[1].equals(CamsConstants.BarCodeInventory.BCI_SCANED_INTO_DEVICE));
                barcodeInventoryErrorDetail.setUploadScanTimestamp(timestamp);
                barcodeInventoryErrorDetail.setCampusCode(lineStrings[3].trim());
                barcodeInventoryErrorDetail.setBuildingCode(lineStrings[4].trim());
                barcodeInventoryErrorDetail.setBuildingRoomNumber(lineStrings[5].trim());
                barcodeInventoryErrorDetail.setBuildingSubRoomNumber(lineStrings[6].trim());
                barcodeInventoryErrorDetail.setAssetConditionCode(lineStrings[7].trim());
                // barcodeInventoryErrorDetail.setRowSelected(true);

                barcodeInventoryErrorDetails.add(barcodeInventoryErrorDetail);
                ln++;
            }

            BarcodeInventoryErrorDocument document = createInvalidBarcodeInventoryDocument(barcodeInventoryErrorDetails);
            processBarcodeInventory(document);

            // Removing *.done files that are created automatically by the framework.
            this.removeDoneFile(file);

            return true;
        }
        catch (FileNotFoundException e1) {
            LOG.error("file to parse not found " + fileName, e1);
            throw new RuntimeException("Cannot find the file requested to be parsed " + fileName + " " + e1.getMessage(), e1);
        }
        catch (Exception ex) {
            LOG.error("Error reading file", ex);
            throw new IllegalArgumentException("Error reading file: " + ex.getMessage());
        }
        finally {
            LOG.debug("processFile(File file) - End");

            try {
                if (input != null) {
                    input.close();
                }
            }
            catch (IOException ex) {
                LOG.error("loadFlatFile() error closing file.", ex);
            }
        }
    }

    /**
     * This method removes the *.done files. If not deleted, then the program will display the name of the file in a puldown menu
     * with a label of ready for process.
     * 
     * @param file
     */
    private void removeDoneFile(File file) {
        String filePath = file.getAbsolutePath();
        File doneFile = new File(StringUtils.substringBeforeLast(filePath, ".") + ".done");

        if (doneFile.exists()) {
            doneFile.delete();
        }
    }

    /**
     * This method invokes the rules in order to validate each records of the barcode file and invokes the method that updates the
     * asset table with the records that passes the rules validation
     * 
     * @param barcodeInventoryErrorDetails
     */
    public void processBarcodeInventory(BarcodeInventoryErrorDocument barcodeInventoryErrorDocument) throws Exception {
        Long lineNumber = new Long(0);

        // apply rules for the new cash control detail
        kualiRuleService.applyRules(new ValidateBarcodeInventoryEvent("", barcodeInventoryErrorDocument));

        List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetails = barcodeInventoryErrorDocument.getBarcodeInventoryErrorDetail();
        List<BarcodeInventoryErrorDetail> tmpBarcodeInventoryErrorDetails = new ArrayList<BarcodeInventoryErrorDetail>();

        for (BarcodeInventoryErrorDetail barcodeInventoryErrorDetail : barcodeInventoryErrorDetails) {
            // if no error found, then update asset table.
            if (!barcodeInventoryErrorDetail.getErrorCorrectionStatusCode().equals(CamsConstants.BarcodeInventoryError.STATUS_CODE_ERROR)) {
                this.updateAssetInformation(barcodeInventoryErrorDetail);
            }
            else {
                lineNumber++;
                // Assigning the row number to each invalid BCIE record
                barcodeInventoryErrorDetail.setUploadRowNumber(lineNumber);

                // Storing in temp collection the invalid BCIE records.
                tmpBarcodeInventoryErrorDetails.add(barcodeInventoryErrorDetail);
            }
        }

        // Storing the invalid barcode inventory records.
        if (!tmpBarcodeInventoryErrorDetails.isEmpty()) {
            barcodeInventoryErrorDocument.setBarcodeInventoryErrorDetail(tmpBarcodeInventoryErrorDetails);
            saveInvalidBarcodeInventoryDocument(barcodeInventoryErrorDocument);
        }

    }

    /**
     * This method updates the asset information particularly the building code, bulding room, building subrool, campus code, and
     * condition code
     * 
     * @param barcodeInventoryErrorDetail
     */
    public void updateAssetInformation(BarcodeInventoryErrorDetail barcodeInventoryErrorDetail) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER, barcodeInventoryErrorDetail.getAssetTagNumber());
        Asset asset = ((List<Asset>) businessObjectService.findMatching(Asset.class, fieldValues)).get(0);

        /*
         * LOG.info("*********BEFORE *******************"); LOG.info("Asset:"+asset.getCapitalAssetNumber());
         * LOG.info("BuildingCode:"+asset.getBuildingCode()); LOG.info("Room:"+asset.getBuildingRoomNumber());
         * LOG.info("SubRoom:"+asset.getBuildingSubRoomNumber()); LOG.info("Condition Code:"+asset.getConditionCode());
         * LOG.info("************************************");
         */


        asset.setBuildingCode(barcodeInventoryErrorDetail.getBuildingCode());
        asset.setBuildingRoomNumber(barcodeInventoryErrorDetail.getBuildingRoomNumber());
        asset.setBuildingSubRoomNumber(barcodeInventoryErrorDetail.getBuildingSubRoomNumber());
        asset.setCampusCode(barcodeInventoryErrorDetail.getCampusCode());
        asset.setConditionCode(barcodeInventoryErrorDetail.getAssetConditionCode());

        // Updating asset information
        businessObjectService.save(asset);

        /*
         * asset = ((List<Asset>)businessObjectService.findMatching(Asset.class, fieldValues)).get(0); LOG.info("********* After *
         * *****************"); LOG.info("Asset:"+asset.getCapitalAssetNumber()); LOG.info("BuildingCode:"+asset.getBuildingCode());
         * LOG.info("Room:"+asset.getBuildingRoomNumber()); LOG.info("SubRoom:"+asset.getBuildingSubRoomNumber());
         * LOG.info("Condition Code:"+asset.getConditionCode()); LOG.info("************************************");
         */
    }

    /**
     * This method creates a transaction document with the invalid barcode inventory records
     * 
     * @param barcodeInventoryErrorDetails
     * @return BarcodeInventoryErrorDocument
     */
    private BarcodeInventoryErrorDocument createInvalidBarcodeInventoryDocument(List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetails) throws WorkflowException {
        BarcodeInventoryErrorDocument document = (BarcodeInventoryErrorDocument) documentService.getNewDocument(BarcodeInventoryErrorDocument.class);
        
        document.getDocumentHeader().setExplanation(DOCUMENT_EXPLANATION);
        document.getDocumentHeader().setFinancialDocumentTotalAmount(new KualiDecimal(0));
        document.getDocumentHeader().setDocumentDescription(DOCUMENT_EXPLANATION);
        document.setUploaderUniversalIdentifier(GlobalVariables.getUserSession().getFinancialSystemUser().getPersonUniversalIdentifier());
        document.setBarcodeInventoryErrorDetail(barcodeInventoryErrorDetails);
        
        return document;
    }


    /**
     * saves the barcode inventory document
     * 
     * @param document
     */
    private void saveInvalidBarcodeInventoryDocument(BarcodeInventoryErrorDocument document) {
        try {
            // The errors are being deleted because, when the document services finds any error then, changes are not saved.
            GlobalVariables.clear();

            // Saving....
            documentService.saveDocument(document, DocumentSystemSaveEvent.class);
        }
        catch (Exception e) {
            LOG.error("Error persisting document # " + document.getDocumentHeader().getDocumentNumber() + " " + e.getMessage(), e);
            throw new RuntimeException("Error persisting document # " + document.getDocumentHeader().getDocumentNumber() + " " + e.getMessage());
        }
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    public void setKualiRuleService(KualiRuleService ruleService) {
        this.kualiRuleService = ruleService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}