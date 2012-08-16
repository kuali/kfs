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
import org.kuali.kfs.gl.service.impl.StringHelper;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.batch.service.AssetBarcodeInventoryLoadService;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetLocation;
import org.kuali.kfs.module.cam.businessobject.BarcodeInventoryErrorDetail;
import org.kuali.kfs.module.cam.document.BarcodeInventoryErrorDocument;
import org.kuali.kfs.module.cam.document.validation.event.ValidateBarcodeInventoryEvent;
import org.kuali.kfs.module.cam.document.web.struts.AssetBarCodeInventoryInputFileForm;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.document.WorkflowDocumentService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.AdHocRoutePerson;
import org.kuali.rice.krad.bo.AdHocRouteRecipient;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Implementation of the AssetBarcodeInventoryLoadService interface. Handles loading, parsing, and storing of incoming barcode
 * inventory files.
 */
public class AssetBarcodeInventoryLoadServiceImpl implements AssetBarcodeInventoryLoadService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetBarcodeInventoryLoadServiceImpl.class);

    public static final String MESSAGE_NO_DOCUMENT_CREATED = "NO barcode inventory error document was created.";
    public static final String DOCUMENTS_MSG = "The following barcode inventory error document were created";
    public static final String TOTAL_RECORDS_UPLOADED_MSG = "Total records uploaded";
    public static final String TOTAL_RECORDS_IN_ERROR_MSG = "Total records in error";

    protected static final int MAX_NUMBER_OF_COLUMNS = 8;
    protected static final String DOCUMENT_EXPLANATION = "BARCODE ERROR INVENTORY";

    private BusinessObjectService businessObjectService;
    private WorkflowDocumentService workflowDocumentService;
    private DataDictionaryService dataDictionaryService;
    private KualiRuleService kualiRuleService;
    private DocumentService documentService;
    private ParameterService parameterService;
    private DateTimeService dateTimeService;

    /**
     * Determines whether or not the BCIE document has all its records corrected or deleted
     * 
     * @param document
     * @return boolean
     */
    public boolean isFullyProcessed(Document document) {
        BarcodeInventoryErrorDocument barcodeInventoryErrorDocument = (BarcodeInventoryErrorDocument)document;
        boolean result = true;
        List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetails = barcodeInventoryErrorDocument.getBarcodeInventoryErrorDetail();
        BarcodeInventoryErrorDetail barcodeInventoryErrorDetail;

        for (BarcodeInventoryErrorDetail detail : barcodeInventoryErrorDetails) {
            if (detail.getErrorCorrectionStatusCode().equals(CamsConstants.BarCodeInventoryError.STATUS_CODE_ERROR)) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * @see org.kuali.kfs.module.cam.batch.service.AssetBarcodeInventoryLoadService#isCurrentUserInitiator(org.kuali.rice.krad.document.Document)
     */
    public boolean isCurrentUserInitiator(Document document) {
        if (document != null) {
            return GlobalVariables.getUserSession().getPerson().getPrincipalId().equalsIgnoreCase(document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
        }
        return false;
    }

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

                    // validate date 
                    if(!validateDate(column[2])) {
                        errorMsg += ", " + InventoryDateLabel + " is invalid";
                    }



                }
                if (!StringUtils.isBlank(errorMsg)) {
                    errorMsg = "Error on record number " + recordCount + ": " + errorMsg.substring(2) + "\n";
                    GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_CUSTOM, errorMsg);
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
    public boolean processFile(File file, AssetBarCodeInventoryInputFileForm form) {
        LOG.debug("processFile(File file) - start");
        
        // Removing *.done files that are created automatically by the framework.
        this.removeDoneFile(file);

        BufferedReader input = null;
        String fileName = file.getName();

        String day;
        String month;
        String year;
        String hours;
        String minutes;
        String seconds;
        boolean isValid = true;

        SimpleDateFormat formatter = new SimpleDateFormat(CamsConstants.DateFormats.MONTH_DAY_YEAR + " " + CamsConstants.DateFormats.MILITARY_TIME, Locale.US);
        formatter.setLenient(false);
        
        BarcodeInventoryErrorDetail barcodeInventoryErrorDetail;
        List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetails = new ArrayList<BarcodeInventoryErrorDetail>();

        List<BarcodeInventoryErrorDocument> barcodeInventoryErrorDocuments = new ArrayList<BarcodeInventoryErrorDocument>();
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
                barcodeInventoryErrorDetail.setCampusCode(lineStrings[3].trim().toUpperCase());
                barcodeInventoryErrorDetail.setBuildingCode(lineStrings[4].trim().toUpperCase());
                barcodeInventoryErrorDetail.setBuildingRoomNumber(lineStrings[5].trim().toUpperCase());
                barcodeInventoryErrorDetail.setBuildingSubRoomNumber(lineStrings[6].trim().toUpperCase());
                barcodeInventoryErrorDetail.setAssetConditionCode(lineStrings[7].trim().toUpperCase());
                barcodeInventoryErrorDetail.setErrorCorrectionStatusCode(CamsConstants.BarCodeInventoryError.STATUS_CODE_ERROR);
                barcodeInventoryErrorDetail.setCorrectorUniversalIdentifier(GlobalVariables.getUserSession().getPerson().getPrincipalId());

                barcodeInventoryErrorDetails.add(barcodeInventoryErrorDetail);
                ln++;
            }
            processBarcodeInventory(barcodeInventoryErrorDetails, form);

            return true;
        }
        catch (FileNotFoundException e1) {
            LOG.error("file to parse not found " + fileName, e1);
            throw new RuntimeException("Cannot find the file requested to be parsed " + fileName + " " + e1.getMessage(), e1);
        }
        catch (Exception ex) {
            LOG.error("Error reading file", ex);
            throw new IllegalArgumentException("Error reading file: " + ex.getMessage(), ex);
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
    protected void removeDoneFile(File file) {
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
    protected void processBarcodeInventory(List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetails, AssetBarCodeInventoryInputFileForm form) throws Exception {
        Long lineNumber = new Long(0);
        boolean docCreated = false;
        int errorRecCount = 0;
        int totalRecCount = 0;

        BarcodeInventoryErrorDocument barcodeInventoryErrorDocument = createInvalidBarcodeInventoryDocument(barcodeInventoryErrorDetails, form.getUploadDescription());
        // apply rules for the new cash control detail
        kualiRuleService.applyRules(new ValidateBarcodeInventoryEvent("", barcodeInventoryErrorDocument, true));

        List<BarcodeInventoryErrorDetail> tmpBarcodeInventoryErrorDetails = new ArrayList<BarcodeInventoryErrorDetail>();

        for (BarcodeInventoryErrorDetail barcodeInventoryErrorDetail : barcodeInventoryErrorDetails) {
            totalRecCount++;
            // if no error found, then update asset table.
            if (!barcodeInventoryErrorDetail.getErrorCorrectionStatusCode().equals(CamsConstants.BarCodeInventoryError.STATUS_CODE_ERROR)) {
                this.updateAssetInformation(barcodeInventoryErrorDetail,true);
            }
            else {
                errorRecCount++;
                lineNumber++;
                // Assigning the row number to each invalid BCIE record
                barcodeInventoryErrorDetail.setUploadRowNumber(lineNumber);

                // Storing in temp collection the invalid BCIE records.
                tmpBarcodeInventoryErrorDetails.add(barcodeInventoryErrorDetail);
            }
        }
        // *********************************************************************
        // Storing the invalid barcode inventory records.
        // *********************************************************************
        String documentsCreated = "";
        if (!tmpBarcodeInventoryErrorDetails.isEmpty()) {
            documentsCreated = this.createBarcodeInventoryErrorDocuments(tmpBarcodeInventoryErrorDetails, barcodeInventoryErrorDocument, form);
            docCreated = true;
        }

        if (!docCreated) {
            form.getMessages().add(MESSAGE_NO_DOCUMENT_CREATED);
        }
        else {
            // Adding the list of documents that were created in the message list
            form.getMessages().add(DOCUMENTS_MSG + ": " + documentsCreated.substring(2));
        }
        form.getMessages().add(TOTAL_RECORDS_UPLOADED_MSG + ": " + StringUtils.rightPad(Integer.toString(totalRecCount), 5, " "));
        form.getMessages().add(TOTAL_RECORDS_IN_ERROR_MSG + ": " + StringUtils.rightPad(Integer.toString(errorRecCount), 5, " "));
    }


    /**
     * This method...
     * 
     * @param bcies
     * @param barcodeInventoryErrorDocument
     */
    protected String createBarcodeInventoryErrorDocuments(List<BarcodeInventoryErrorDetail> bcies, BarcodeInventoryErrorDocument barcodeInventoryErrorDocument, AssetBarCodeInventoryInputFileForm form) {
        List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetails = new ArrayList<BarcodeInventoryErrorDetail>();
        boolean isFirstDocument = true;
        int ln = 0;
        int bcieCount = 0;
        String documentsCreated = "";
        int maxNumberRecordsPerDocument = 300;

        try {
            if (parameterService.parameterExists(BarcodeInventoryErrorDocument.class, CamsConstants.Parameters.MAX_NUMBER_OF_RECORDS_PER_DOCUMENT)) {
                maxNumberRecordsPerDocument = new Integer(parameterService.getParameterValueAsString(BarcodeInventoryErrorDocument.class, CamsConstants.Parameters.MAX_NUMBER_OF_RECORDS_PER_DOCUMENT)).intValue();
            }

            while (true) {
                if ((ln > maxNumberRecordsPerDocument) || (bcieCount >= bcies.size())) {
                    // This if was added in order to not waste the document already created and not create a new one.
                    if (!isFirstDocument) {
                        barcodeInventoryErrorDocument = createInvalidBarcodeInventoryDocument(barcodeInventoryErrorDetails, form.getUploadDescription());
                    }
                    documentsCreated += ", " + barcodeInventoryErrorDocument.getDocumentNumber();

                    barcodeInventoryErrorDocument.setBarcodeInventoryErrorDetail(barcodeInventoryErrorDetails);
                    saveInvalidBarcodeInventoryDocument(barcodeInventoryErrorDocument);

                    barcodeInventoryErrorDetails = new ArrayList<BarcodeInventoryErrorDetail>();

                    if (bcieCount >= bcies.size())
                        break;

                    ln = 0;
                    isFirstDocument = false;
                }

                BarcodeInventoryErrorDetail barcodeInventoryErrorDetail =bcies.get(bcieCount);
                barcodeInventoryErrorDetail.setUploadRowNumber(Long.valueOf(ln+1));
                barcodeInventoryErrorDetails.add(barcodeInventoryErrorDetail);

                ln++;
                bcieCount++;
            }
        }
        catch (Exception e) {
            LOG.error("Error creating BCIE documents", e);
            throw new IllegalArgumentException("Error creating BCIE documents: " + e.getMessage(), e);
        }
        return documentsCreated;
    }


    /**
     * This method updates the asset information particularly the building code, bulding room, building subrool, campus code, and
     * condition code
     * 
     * @param barcodeInventoryErrorDetail
     */
    public void updateAssetInformation(BarcodeInventoryErrorDetail barcodeInventoryErrorDetail, boolean updateWithDateAssetWasScanned) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER, barcodeInventoryErrorDetail.getAssetTagNumber());
        Asset asset = ((List<Asset>) businessObjectService.findMatching(Asset.class, fieldValues)).get(0);

        asset.setInventoryScannedCode((barcodeInventoryErrorDetail.isUploadScanIndicator() ? CamsConstants.BarCodeInventory.BCI_SCANED_INTO_DEVICE : CamsConstants.BarCodeInventory.BCI_MANUALLY_KEYED_CODE));
        asset.setBuildingCode(barcodeInventoryErrorDetail.getBuildingCode());
        asset.setBuildingRoomNumber(barcodeInventoryErrorDetail.getBuildingRoomNumber());
        asset.setBuildingSubRoomNumber(barcodeInventoryErrorDetail.getBuildingSubRoomNumber());
        asset.setCampusCode(barcodeInventoryErrorDetail.getCampusCode());
        asset.setConditionCode(barcodeInventoryErrorDetail.getAssetConditionCode());        

        // set building code and room number to null if they are empty string, to avoid FK violation exception
        if (StringUtils.isEmpty(asset.getBuildingCode())) {
            asset.setBuildingCode(null);
            asset.setBuilding(null);
        }
        if (StringUtils.isEmpty(asset.getBuildingRoomNumber())) {
            asset.setBuildingRoomNumber(null);
            asset.setBuildingRoom(null);
        }        
        
        if (updateWithDateAssetWasScanned) {
            asset.setLastInventoryDate(barcodeInventoryErrorDetail.getUploadScanTimestamp());
        } else {
            asset.setLastInventoryDate(new Timestamp(dateTimeService.getCurrentSqlDate().getTime()));
        }
        
        // Purposefully deleting off-campus locations when loading locations via barcode scanning.
        List<AssetLocation> assetLocations = asset.getAssetLocations();
        for (AssetLocation assetLocation : assetLocations) {
            if(CamsConstants.AssetLocationTypeCode.OFF_CAMPUS.equals(assetLocation.getAssetLocationTypeCode())) {
                assetLocations.remove(assetLocation);
                break;
            }
        }

        // Updating asset information
        businessObjectService.save(asset);
    }

    /**
     * This method creates a transaction document with the invalid barcode inventory records
     * 
     * @param barcodeInventoryErrorDetails
     * @return BarcodeInventoryErrorDocument
     */
    protected BarcodeInventoryErrorDocument createInvalidBarcodeInventoryDocument(List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetails, String uploadDescription) throws WorkflowException {
        BarcodeInventoryErrorDocument document = (BarcodeInventoryErrorDocument) documentService.getNewDocument(BarcodeInventoryErrorDocument.class);

        document.getDocumentHeader().setExplanation(DOCUMENT_EXPLANATION);
        document.getFinancialSystemDocumentHeader().setFinancialDocumentTotalAmount(KualiDecimal.ZERO);
        document.getDocumentHeader().setDocumentDescription(uploadDescription);
        document.setUploaderUniversalIdentifier(GlobalVariables.getUserSession().getPerson().getPrincipalId());
        document.setBarcodeInventoryErrorDetail(barcodeInventoryErrorDetails);

        return document;
    }


    /**
     * saves the barcode inventory document
     * 
     * @param document
     */
    protected void saveInvalidBarcodeInventoryDocument(BarcodeInventoryErrorDocument document) {
        try {
            // The errors are being deleted because, when the document services finds any error then, changes are not saved.
            GlobalVariables.clear();

            // no adhoc recipient need to add when submit doc. doc will route to the doc uploader, i.e. initiator automtically.
            List<AdHocRouteRecipient> adHocRouteRecipients = new ArrayList<AdHocRouteRecipient>();
            documentService.routeDocument(document, "Routed Update Barcode Inventory Document", adHocRouteRecipients);
        }
        catch (Exception e) {
            LOG.error("Error persisting document # " + document.getDocumentHeader().getDocumentNumber() + " " + e.getMessage(), e);
            throw new RuntimeException("Error persisting document # " + document.getDocumentHeader().getDocumentNumber() + " " + e.getMessage(), e);
        }
    }

    /**
     * This method builds a recipient for Approval.
     * 
     * @param userId
     * @return
     */
    protected AdHocRouteRecipient buildApprovePersonRecipient(String userId) {
        AdHocRouteRecipient adHocRouteRecipient = new AdHocRoutePerson();
        adHocRouteRecipient.setActionRequested(KewApiConstants.ACTION_REQUEST_APPROVE_REQ);
        adHocRouteRecipient.setId(userId);
        return adHocRouteRecipient;
    }

    private boolean validateDate(String date) {
        // Parsing date so it can be validated.
        boolean valid = true;
        if(StringHelper.isEmpty(date)) {
            valid = false;
        }
        else {
            SimpleDateFormat formatter = new SimpleDateFormat(CamsConstants.DateFormats.MONTH_DAY_YEAR + " " + CamsConstants.DateFormats.STANDARD_TIME, Locale.US);
            date = StringUtils.rightPad(date.trim(), 14, "0");
            String day = date.substring(0, 2);
            String month = date.substring(2, 4);
            String year = date.substring(4, 8);
            String hours = date.substring(8, 10);
            String minutes = date.substring(10, 12);
            String seconds = date.substring(12);

            String stringDate = month + "/" + day + "/" + year + " " + hours + ":" + minutes + ":" + seconds;
            Timestamp timestamp = null;

            // If date has invalid format set its value to null
            try {
                timestamp = new Timestamp(formatter.parse(stringDate).getTime());
            }
            catch (Exception e) {
                valid = false;
            }
            
        }
        
        return valid;
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

        public ParameterService getParameterService() {
            return parameterService;
        }

        public void setParameterService(ParameterService parameterService) {
            this.parameterService = parameterService;
        }

        public void setDateTimeService(DateTimeService dateTimeService) {
            this.dateTimeService = dateTimeService;
        }
    }
