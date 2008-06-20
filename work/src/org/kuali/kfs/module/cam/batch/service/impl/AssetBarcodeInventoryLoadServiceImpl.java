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
package org.kuali.module.cams.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.FinancialSystemDocumentHeader;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.batch.barcode.BarcodeInventory;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.BarcodeInventoryErrorDetail;
import org.kuali.module.cams.document.BarcodeInventoryErrorDocument;
import org.kuali.module.cams.rule.event.ValidateBarcodeInventoryEvent;
import org.kuali.module.cams.service.AssetBarcodeInventoryLoadService;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * This is the default implementation of the ProcurementCardLoadTransactionsService interface. Handles loading, parsing, and storing
 * of incoming procurement card batch files.
 * 
 * @see org.kuali.module.financial.service.ProcurementCardCreateDocumentService
 */
public class AssetBarcodeInventoryLoadServiceImpl implements AssetBarcodeInventoryLoadService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetBarcodeInventoryLoadServiceImpl.class);

    private static final int MAX_NUMBER_OF_COLUMNS = 9;

    private BusinessObjectService businessObjectService;
    private WorkflowDocumentService workflowDocumentService;
    private DataDictionaryService dataDictionaryService;
    private KualiRuleService kualiRuleService;
    private DocumentService documentService;

    /**
     * 
     * @see org.kuali.module.cams.service.AssetBarcodeInventoryLoadService#isFileFormatValid(java.io.File)
     */
    public boolean isFileFormatValid(File file) {
        LOG.debug("isFileFormatValid(File file) - start");
        String fileName = file.getName();

        BufferedReader input = null;

        Integer campusTagNumberMaxLength = dataDictionaryService.getAttributeMaxLength(Asset.class, CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER);
        Integer inventoryScannedCodeMaxLength = new Integer(1); 
        Integer createDateMaxLength = dataDictionaryService.getAttributeMaxLength(Asset.class, CamsPropertyConstants.Asset.CREATE_DATE);
        Integer campusCodeMaxLength = dataDictionaryService.getAttributeMaxLength(Asset.class, CamsPropertyConstants.Asset.CAMPUS_CODE);
        Integer buildingCodeMaxLength = dataDictionaryService.getAttributeMaxLength(Asset.class, CamsPropertyConstants.Asset.BUILDING_CODE);

        Integer buildingRoomNumberMaxLength = dataDictionaryService.getAttributeMaxLength(Asset.class, CamsPropertyConstants.Asset.BUILDING_ROOM_NUMBER);
        Integer buildingSubRoomNumberMaxLength = dataDictionaryService.getAttributeMaxLength(Asset.class, CamsPropertyConstants.Asset.BUILDING_SUB_ROOM_NUMBER);
        Integer conditionCodeMaxLength = dataDictionaryService.getAttributeMaxLength(Asset.class, CamsPropertyConstants.Asset.CONDITION_CODE);
        Integer inventoryStatusCodeMaxLength = dataDictionaryService.getAttributeMaxLength(Asset.class, CamsPropertyConstants.Asset.INVENTORY_STATUS_CODE);

        try {
            int recordCount = 0;
            String errorMsg = "";

            // errorMsg="Error on record number "+recordCount;
            // input = new BufferedReader(new FileReader(fileName));

            input = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = input.readLine()) != null) {
                recordCount++;
                line = StringUtils.remove(line, "\"");
                //LOG.info("Reading asset barcode inventory record number " + recordCount);

                BarcodeInventory bci = new BarcodeInventory();
                String[] column = org.springframework.util.StringUtils.delimitedListToStringArray(line, ",");

                if (MAX_NUMBER_OF_COLUMNS > column.length) {
                    // Error more columns that allowed. put it in the constants class.
                    errorMsg += "Bar code inventory has record(s) with more than " + MAX_NUMBER_OF_COLUMNS + " columns\n";
                }

                if (column[0].length() > campusTagNumberMaxLength.intValue()) {
                    errorMsg += "Asset tag number length exceeds the field maximum allowed length\n";
                }

                if (column[1].length() > inventoryScannedCodeMaxLength.intValue()) {
                    errorMsg += "Scanned code length exceeds the field maximum allowed length\n";
                } else if (!column[1].equals("1") && !column[1].equals("0")) {
                    errorMsg += "Scanned code is invalid\n";
                }

                if (column[2].length() > createDateMaxLength.intValue()) {
                    errorMsg += "Inventory date length exceeds the field maximum allowed length\n";
                }

                if (column[3].length() > campusCodeMaxLength.intValue()) {
                    errorMsg += "Campus code length exceeds the field maximum allowed length\n";
                }
                if (column[4].length() > buildingCodeMaxLength.intValue()) {
                    errorMsg += "Building code length exceeds the field maximum allowed length\n";
                }
                if (column[5].length() > buildingRoomNumberMaxLength.intValue()) {
                    errorMsg += "Building room number exceeds the field maximum allowed length\n";
                }
                if (column[6].length() > buildingSubRoomNumberMaxLength.intValue()) {
                    errorMsg += "Building sub room number exceeds the field maximum allowed length\n";
                }
                if (column[7].length() > conditionCodeMaxLength.intValue()) {
                    errorMsg += "Condition code exceeds the field maximum allowed length\n";
                }
                
                /*if (column[8].length() > inventoryStatusCodeMaxLength.intValue()) {
                    errorMsg += "Inventory status code exceeds the field maximum allowed length\n";
                }*/

                if (!StringUtils.isBlank(errorMsg)) {
                    LOG.error("Error on record number " + recordCount + " : " + errorMsg);
                    GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, errorMsg);
                    // throw new RuntimeException(errorMsg);
                    return false;
                }
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
     * 
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
        boolean isValid=true;

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
        BarcodeInventoryErrorDetail barcodeInventoryErrorDetail;
        List <BarcodeInventoryErrorDetail> barcodeInventoryErrorDetails = new ArrayList<BarcodeInventoryErrorDetail>();

        try {
            input = new BufferedReader(new FileReader(file));
            Long lineNumber=new Long(1);
            String line = null;
            while ((line = input.readLine()) != null) {
                line = StringUtils.remove(line, "\"");
                String[] lineStrings = org.springframework.util.StringUtils.delimitedListToStringArray(line, ",");
                //LOG.info("*** lineString:"+lineStrings[2]);

                day = lineStrings[2].substring(0, 2);
                month = lineStrings[2].substring(2, 4);
                year = lineStrings[2].substring(4, 8);
                hours = lineStrings[2].substring(8, 10);
                minutes = lineStrings[2].substring(10, 12);
                seconds = lineStrings[2].substring(12);

                String stringDate = month + "/" + day + "/" + year + " " + hours + ":" + minutes + ":" + seconds;
                java.sql.Date sqlDate = new java.sql.Date(formatter.parse(stringDate).getTime());


                barcodeInventoryErrorDetail = new BarcodeInventoryErrorDetail();
                barcodeInventoryErrorDetail.setUploadRowNumber(lineNumber);                
                barcodeInventoryErrorDetail.setAssetTagNumber(lineStrings[0]);

                // 1=>True meaning hand entered. - 0=>False meaning scanned 
                barcodeInventoryErrorDetail.setUploadScanIndicator(lineStrings[1].equals("1"));

                barcodeInventoryErrorDetail.setUploadScanTimestamp(sqlDate);
                barcodeInventoryErrorDetail.setCampusCode(lineStrings[3]);                
                barcodeInventoryErrorDetail.setBuildingCode(lineStrings[4]);
                barcodeInventoryErrorDetail.setBuildingRoomNumber(lineStrings[5]);
                barcodeInventoryErrorDetail.setBuildingSubRoomNumber(lineStrings[6]);
                barcodeInventoryErrorDetail.setAssetConditionCode(lineStrings[7]);
                //barcodeInventoryErrorDetail.setErrorCorrectionStatusCode(lineStrings[8]);

                barcodeInventoryErrorDetails.add(barcodeInventoryErrorDetail);
                lineNumber++;
            }

            BarcodeInventoryErrorDocument document = createInvalidBarcodeInventoryDocument(barcodeInventoryErrorDetails);

            //TransactionalDocument x = (TransactionalDocument)document;
            //String docTypeName = document.getDocumentHeader().getWorkflowDocument().getDocumentType();
            //LOG.info("**** DOC TYPE:"+docTypeName);

            processBarcodeInventory(document);

            // TODO Deleting loaded files
            // removeDoneFiles(processedFiles);
            // LOG.info("Total transactions loaded: " + Integer.toString(pcardTransactions.size()));
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
     * 
     * This method...
     * @param barcodeInventoryErrorDetails
     */
    private void processBarcodeInventory(BarcodeInventoryErrorDocument barcodeInventoryErrorDocument) throws Exception {
        int totalRecordCount=0;
        int invalidRecordCount=0;
        // apply rules for the new cash control detail                
        kualiRuleService.applyRules(new ValidateBarcodeInventoryEvent("", barcodeInventoryErrorDocument));

        List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetails = barcodeInventoryErrorDocument.getBarcodeInventoryErrorDetail();
        BarcodeInventoryErrorDetail barcodeInventoryErrorDetail;

        totalRecordCount = barcodeInventoryErrorDetails.size();

        for (Iterator<BarcodeInventoryErrorDetail> it = barcodeInventoryErrorDetails.iterator();it.hasNext();) { 
            barcodeInventoryErrorDetail = (BarcodeInventoryErrorDetail)it.next();

            /* if not error found then update asset table and delete the element from the collection 
             * so it can be use to later on insert invalid inventory into the barcodeinventoryerror 
             * tables
             */ 
            if (!barcodeInventoryErrorDetail.getErrorCorrectionStatusCode().equals("?")) {
                //this.updateAssetInformation(barcodeInventoryErrorDetail);
                it.remove();
            } else {
                invalidRecordCount++;                
            }
        }             

        /*
         * Deleting any error generated when invoking the rule class, because we only need to display the errors in the
         * barcode inventory error document page
         */         
        GlobalVariables.getErrorMap().clear();

        //Adding the messages that inform the user how many records were uploaded
        GlobalVariables.getErrorMap().putError(CamsConstants.DOCUMENT_PATH+"."+CamsConstants.DOCUMENT_NUMBER_PATH, CamsKeyConstants.BarCodeInventory.MESSAGE_UPLOAD_RECCOUNT,new Integer(totalRecordCount).toString());

        //Adding the message that inform the user how many records are in error
        GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, CamsKeyConstants.BarCodeInventory.MESSAGE_UPLOAD_ERROR_RECCOUNT,new Integer(invalidRecordCount).toString());       

        for(BarcodeInventoryErrorDetail barcodeInventoryErrorDetail2:barcodeInventoryErrorDetails) {            
            LOG.info("*** Data:"+barcodeInventoryErrorDetail2.getAssetTagNumber());            
        }

        //Storing the invalid barcode inventory records. 
        if (!barcodeInventoryErrorDetails.isEmpty()) {
            barcodeInventoryErrorDocument.setBarcodeInventoryErrorDetail(barcodeInventoryErrorDetails);
            saveInvalidBarcodeInventoryDocument(barcodeInventoryErrorDocument);
        }

    }

    /**
     * 
     * This method...
     * @param barcodeInventoryErrorDetail
     */
    private void updateAssetInformation(BarcodeInventoryErrorDetail barcodeInventoryErrorDetail) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER, barcodeInventoryErrorDetail.getAssetTagNumber());
        List<Asset> assets = (List<Asset>)SpringContext.getBean(BusinessObjectService.class).findMatching(Asset.class, fieldValues);

        assets.get(0).setBuildingCode(barcodeInventoryErrorDetail.getBuildingCode());
        assets.get(0).setBuildingRoomNumber(barcodeInventoryErrorDetail.getBuildingRoomNumber());
        assets.get(0).setBuildingSubRoomNumber(barcodeInventoryErrorDetail.getBuildingSubRoomNumber());
        assets.get(0).setCampusCode(barcodeInventoryErrorDetail.getCampusCode());
        assets.get(0).setConditionCode(barcodeInventoryErrorDetail.getAssetConditionCode());

        //Updating asset information
        businessObjectService.save(assets);
    }

    /**
     * 
     * This method...
     * @param barcodeInventoryErrorDetails
     */
    private BarcodeInventoryErrorDocument createInvalidBarcodeInventoryDocument(List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetails) throws Exception {
        String documentTypeCode;
        BarcodeInventoryErrorDocument barcodeInventoryErrorDocument = new BarcodeInventoryErrorDocument();

        try {
            KualiWorkflowDocument workflowDocument = workflowDocumentService.createWorkflowDocument("BarcodeInventoryErrorDocument", GlobalVariables.getUserSession().getUniversalUser());

            //BarcodeInventoryErrorDocument document = (BarcodeInventoryErrorDocument) documentService.getNewDocument(BarcodeInventoryErrorDocument.class);

            // **************************************************************************************************
            // Create a new document header object
            // **************************************************************************************************
            FinancialSystemDocumentHeader documentHeader = new FinancialSystemDocumentHeader();
            documentHeader.setWorkflowDocument(workflowDocument);
            documentHeader.setDocumentNumber(workflowDocument.getRouteHeaderId().toString());
            documentHeader.setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
            // documentHeader.setExplanation(CamsConstants.Depreciation.DOCUMENT_DESCRIPTION);
            // TODO set explanation
            documentHeader.setExplanation("");

            // TODO set description constants
            documentHeader.setDocumentDescription("");
            documentHeader.setFinancialDocumentTotalAmount(new KualiDecimal(0));

            barcodeInventoryErrorDocument.setDocumentHeader(documentHeader);
            barcodeInventoryErrorDocument.setDocumentNumber(documentHeader.getDocumentNumber());
            barcodeInventoryErrorDocument.setUploaderUniversalIdentifier(GlobalVariables.getUserSession().getFinancialSystemUser().getPersonUniversalIdentifier());
            barcodeInventoryErrorDocument.setBarcodeInventoryErrorDetail(barcodeInventoryErrorDetails);

            Properties parameters = new Properties();
            parameters.put(KNSConstants.PARAMETER_DOC_ID, documentHeader.getDocumentNumber());
            parameters.put(KNSConstants.PARAMETER_COMMAND, KNSConstants.METHOD_DISPLAY_DOC_SEARCH_VIEW);

            String url = UrlFactory.parameterizeUrl(
                    SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.WORKFLOW_URL_KEY)
                    + "/" + KNSConstants.DOC_HANDLER_ACTION, parameters);

            // post an error about the locked document
            String[] errorParameters = { url, documentHeader.getDocumentNumber()};
            GlobalVariables.getErrorMap().putError(KNSConstants.GLOBAL_ERRORS, CamsKeyConstants.BarCodeInventory.MESSAGE_DOCUMENT_URL, errorParameters);            
            //GlobalVariables.getMessageList().add("Document "+documentHeader.getDocumentNumber()+" has been succesfully saved.");            
        } catch (Exception e) {
            LOG.error("createInvalidBarcodeInventoryDocument - error:", e);            
            throw e;
        }

        //LOG.debug("end");
        return barcodeInventoryErrorDocument;
    }


    /**
     * 
     * This method...
     * @param document
     */
    private void saveInvalidBarcodeInventoryDocument(BarcodeInventoryErrorDocument document) {
        this.businessObjectService.save(document);        
    }


    /**
     * Clears out associated .done files for the processed data files.
     */
    private void removeDoneFiles(List<String> dataFileNames) {
        for (String dataFileName : dataFileNames) {
            File doneFile = new File(org.apache.commons.lang.StringUtils.substringBeforeLast(dataFileName, ".") + ".done");
            if (doneFile.exists()) {
                doneFile.delete();
            }
        }
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
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