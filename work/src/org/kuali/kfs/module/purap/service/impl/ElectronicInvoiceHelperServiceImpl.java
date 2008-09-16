/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.batch.ElectronicInvoiceInputFileType;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoice;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceDetailRequestHeader;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceDetailRequestSummary;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceItem;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoad;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoadSummary;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceOrder;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectReason;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectReasonType;
import org.kuali.kfs.module.purap.businessobject.ItemType;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestAccount;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.dataaccess.ElectronicInvoicingDao;
import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.ReceivingDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.AccountsPayableService;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.module.purap.document.validation.event.CalculateAccountsPayableEvent;
import org.kuali.kfs.module.purap.document.validation.event.PaymentRequestForEInvoiceEvent;
import org.kuali.kfs.module.purap.exception.CxmlParseException;
import org.kuali.kfs.module.purap.exception.PaymentRequestInitializationValidationErrors;
import org.kuali.kfs.module.purap.exception.PurError;
import org.kuali.kfs.module.purap.exception.PaymentRequestInitializationValidationErrors.PREQCreationFailure;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceMappingService;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceMatchingService;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceHelperService;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceService;
import org.kuali.kfs.module.purap.util.ExpiredOrClosedAccountEntry;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.XMLParseException;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.dto.UserIdDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.user.UserService;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.bo.AdHocRoutePerson;
import org.kuali.rice.kns.bo.AdHocRouteRecipient;
import org.kuali.rice.kns.bo.Attachment;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.exception.UserNotFoundException;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.service.MailService;
import org.kuali.rice.kns.service.NoteService;
import org.kuali.rice.kns.service.UniversalUserService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.springframework.dao.support.DaoSupport;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

@Transactional
public class ElectronicInvoiceHelperServiceImpl implements ElectronicInvoiceHelperService {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceHelperServiceImpl.class);

    private static final String UNKNOWN_DUNS_IDENTIFIER = "Unknown";

    private StringBuffer emailTextErrorList = new StringBuffer();
    
    private Map<String, Map> itemTypeMappingsCache = new WeakHashMap<String, Map>();  
    private Map<String,ItemType> kualiItemTypes = new HashMap<String, ItemType>();

    private ElectronicInvoiceInputFileType electronicInvoiceInputFileType;
    private MailService mailService;
    private ElectronicInvoiceMatchingService matchingService; 
    private ElectronicInvoicingDao electronicInvoicingDao;
    private BatchInputFileService batchInputFileService;
    private VendorService vendorService;
    private PurchaseOrderService purchaseOrderService;
    private PaymentRequestService paymentRequestService;
    
    public boolean loadElectronicInvoices() {

        /**
         * TODO: HAVE TO REMOVE THIS TEMP CODE
         */
        try {
            GlobalVariables.setUserSession(new UserSession("abolding"));
        }
        catch (UserNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        catch (WorkflowException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        LOG.debug("loadElectronicInvoices() started");

        /**
         * FIXME: Add parameters for each dir name In EPIC, accept and reject dir names are coming from sys param and invoice dir
         * are coming from cmd line args
         */
        String baseDirName = getBaseDir();
        String rejectDirName = getRejectDir();
        String acceptDirName = getAcceptDir();
        String sourceDirName = getSourceDir();

        Boolean moveFiles = false;

        if (LOG.isInfoEnabled()){
            LOG.info("Invoice Base Directory - " + electronicInvoiceInputFileType.getDirectoryPath());
            LOG.info("Invoice Source Directory - " + sourceDirName);
            LOG.info("Invoice Accept Directory - " + acceptDirName);
            LOG.info("Invoice Reject Directory - " + rejectDirName);
            LOG.info("Is moving files allowed - " + moveFiles);
        }

        if (StringUtils.isBlank(rejectDirName)) {
            throw new RuntimeException("Reject directory name should not be empty");
        }

        if (StringUtils.isBlank(acceptDirName)) {
            throw new RuntimeException("Accept directory name should not be empty");
        }

        if (StringUtils.isBlank(sourceDirName)) {
            throw new RuntimeException("Source directory name should not be empty");
        }

        File baseDir = new File(baseDirName);
        File[] filesToBeProcessed = baseDir.listFiles(new FileFilter() {
                                                            public boolean accept(File file) {
                                                                return (!file.isDirectory() && file.getName().endsWith(".xml"));
                                                            }
                                                        });

        String emailFileName = "c:\\test.txt";
        (new File(emailFileName)).delete();
        
        if (!baseDir.exists() ||  
             filesToBeProcessed == null || 
             filesToBeProcessed.length == 0) {

            StringBuffer mailText = new StringBuffer();
            
            mailText.append("\n\n");
            mailText.append(PurapConstants.ElectronicInvoice.NO_FILES_PROCESSED_EMAIL_MESSAGE);
            mailText.append("\n\n");
            
            sendSummary(mailText, emailFileName);
            return false;
        }

        try {
            FileUtils.forceMkdir(new File(acceptDirName));
            FileUtils.forceMkdir(new File(rejectDirName));
            FileUtils.forceMkdir(new File(sourceDirName));
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        /**
         * TODO : Is it required to clean up the accept and reject dir here.  
         */
        
        /**
         * FIXME : Introduce param here
         */
        boolean hasRejectedFile = false;
        ElectronicInvoiceLoad eInvoiceLoad = new ElectronicInvoiceLoad();

        if (LOG.isInfoEnabled()){
            LOG.info(filesToBeProcessed.length + " file(s) available for processing");
        }

        for (int i = 0; i < filesToBeProcessed.length; i++) {

            LOG.info("Processing " + filesToBeProcessed[i].getName() + "....");

            boolean movedToSourceDir = addNamespaceDefinition(eInvoiceLoad, filesToBeProcessed[i]);
            
            if (!movedToSourceDir){
                if (moveFiles) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(filesToBeProcessed[i].getName() + " has been marked to move to " + rejectDirName);
                    }
                    eInvoiceLoad.insertRejectFileToMove(filesToBeProcessed[i], rejectDirName);
                }
                hasRejectedFile = true;
                continue;
            }
            
            File fileInSourceDir = new File(getSourceDir() + filesToBeProcessed[i].getName());
            
            boolean isRejected = processElectronicInvoice(eInvoiceLoad, fileInSourceDir);

            if (isRejected) {
                if (LOG.isInfoEnabled()){
                    LOG.info(filesToBeProcessed[i].getName() + " has been rejected");
                }
                if (moveFiles) {
                    
                    if (LOG.isDebugEnabled()){
                        LOG.debug(filesToBeProcessed[i].getName() + " has been marked to move to " + rejectDirName);
                    }
                    eInvoiceLoad.insertRejectFileToMove(filesToBeProcessed[i], rejectDirName);
                }
                hasRejectedFile = true;
            } else {
                if (LOG.isInfoEnabled()){
                    LOG.info(filesToBeProcessed[i].getName() + " has been accepted");
                }
                if (moveFiles) {
                    if (!moveFile(filesToBeProcessed[i], acceptDirName)) {
                        String errorMessage = filesToBeProcessed[i].getName() + " unable to move";
                        LOG.error("loadElectronicInvoices() " + errorMessage);
                        throw new PurError(errorMessage);
                    }
                }
            }
        }

         StringBuffer summaryText = saveLoadSummary(eInvoiceLoad);

         StringBuffer finalText = new StringBuffer();
         finalText.append(summaryText);
         finalText.append("\n");
         finalText.append(emailTextErrorList);
         sendSummary(finalText,emailFileName);

        LOG.debug("Processing completed");

        return hasRejectedFile;

    }
    
    private boolean addNamespaceDefinition(ElectronicInvoiceLoad eInvoiceLoad, 
                                           File invoiceFile) {
        
        boolean result = true;
        
        File moveHere = new File(getSourceDir() + invoiceFile.getName());
        
        if (LOG.isDebugEnabled()){
            LOG.debug("Adding namespace definition to " + invoiceFile.getName());
        }
        
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setValidating(false); // It's not needed to validate here
        builderFactory.setIgnoringElementContentWhitespace(true); 
        
        DocumentBuilder builder = null;
        try {
          builder = builderFactory.newDocumentBuilder();  // Create the parser
        } catch(ParserConfigurationException e) {
            LOG.error("Error getting document builder - " + e.getMessage());
            throw new RuntimeException(e);
        }
        
        Document xmlDoc = null;
        
        try {
            xmlDoc = builder.parse(invoiceFile);
        } catch(Exception e) {
            if (LOG.isDebugEnabled()){
                LOG.debug("Error parsing the file " + e.getMessage());
            }
            rejectElectronicInvoiceFile(eInvoiceLoad, UNKNOWN_DUNS_IDENTIFIER, invoiceFile, e.getMessage(),PurapConstants.ElectronicInvoice.FILE_FORMAT_INVALID);
            return false;
        }
        
        Node node = xmlDoc.getDocumentElement();
        Element element = (Element)node;

        String xmlnsValue = element.getAttribute("xmlns");
        String xmlnsXsiValue = element.getAttribute("xmlns:xsi");
        
        /**
         * It's not needed to add the namespace if it's already there. 
         */
        if (StringUtils.equals(xmlnsValue, "http://www.kuali.org/modules/purap/electronicInvoice") && 
            StringUtils.equals(xmlnsXsiValue, "http://www.w3.org/2001/XMLSchema-instance")){
            if (LOG.isDebugEnabled()){
                LOG.debug("xmlns and xmlns:xsi attributes already exists in the invoice xml");
            }
            try {
                FileUtils.copyFile(invoiceFile, moveHere);
                FileUtils.forceDelete(invoiceFile);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        
        element.setAttribute("xmlns", "http://www.kuali.org/modules/purap/electronicInvoice");
        element.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        
        OutputFormat outputFormat = new OutputFormat(xmlDoc );
        outputFormat.setIndenting(true);
        outputFormat.setLineWidth(0);
        outputFormat.setOmitDocumentType(true);
        outputFormat.setEncoding("UTF-8");
        
        FileOutputStream outputStream;
        
        /**
         * If a file with the same name exists in source dir, delete it. Otherwise, it's not 
         * possible for the serializer to write the file.
         */
        if (moveHere.exists()){
            if (LOG.isDebugEnabled()){
                LOG.debug("Deleting existing " + invoiceFile.getName() + " from the source dir");
                try {
                    FileUtils.forceDelete(moveHere);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        
        try {
            
            outputStream = new FileOutputStream(new File(getSourceDir() + invoiceFile.getName()));
            XMLSerializer serializer = new XMLSerializer( outputStream,outputFormat );
            serializer.asDOMSerializer();
            serializer.serialize( xmlDoc.getDocumentElement() );
            outputStream.flush();
            outputStream.close();
        }
        catch (FileNotFoundException e) {
           /**
            * Shallow. This never happen
            */
        }catch (IOException e) {
            rejectElectronicInvoiceFile(eInvoiceLoad, UNKNOWN_DUNS_IDENTIFIER, invoiceFile, e.getMessage(),PurapConstants.ElectronicInvoice.ERROR_ADDING_SCHEMA);
            return false;
        }
        
        try {
            FileUtils.forceDelete(invoiceFile);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        if (LOG.isDebugEnabled()){
            LOG.debug("Successfully added the namespace");
        }

        return true;
    }
    
    /**
     * This method processes a single electronic invoice file
     * 
     * @param eil the load summary to be modified
     * @param invoiceFile the file to be loaded
     * @param emailFilename the path and name of the e-mail file to be sent
     * @return boolean where true means there has been some type of reject
     */
    private boolean processElectronicInvoice(ElectronicInvoiceLoad eInvoiceLoad, 
                                             File invoiceFile) {

        ElectronicInvoice eInvoice = null;

        if (LOG.isDebugEnabled()){
            LOG.debug("Processing " + invoiceFile.getName());
        }
        
        try {
            eInvoice = loadElectronicInvoice(invoiceFile);
        }catch (CxmlParseException e) {
            e.printStackTrace();
            rejectElectronicInvoiceFile(eInvoiceLoad, UNKNOWN_DUNS_IDENTIFIER, invoiceFile, e.getMessage(),PurapConstants.ElectronicInvoice.FILE_FORMAT_INVALID);
            return true;
        }

        eInvoice.setFileName(invoiceFile.getName());
        
        boolean isCompleteFailure = checkForCompleteFailure(eInvoiceLoad,eInvoice,invoiceFile); 
        
        if (isCompleteFailure){
            return true;
        }

        setVendorDUNSNumber(eInvoice);
        setVendorDetails(eInvoice);
        
        Map itemTypeMappings = getItemTypeMappings(eInvoice.getVendorHeaderID(),eInvoice.getVendorDetailID());
        Map kualiItemTypes = getKualiItemTypes();
        
        if (LOG.isDebugEnabled()){
            if (itemTypeMappings != null && itemTypeMappings.size() > 0){
                LOG.debug("Item mappings found");
            }
        }
        
        boolean validateHeader = true;
        
        for (ElectronicInvoiceOrder order : eInvoice.getInvoiceDetailOrders()) {

            String poID = order.getOrderReferenceOrderID();
            PurchaseOrderDocument po = null;
            
            if (NumberUtils.isDigits(StringUtils.defaultString(poID))){
                po = purchaseOrderService.getCurrentPurchaseOrder(new Integer(poID));    
                if (po != null){
                    order.setInvoicePurchaseOrderID(poID);
                    order.setPurchaseOrderID(po.getPurapDocumentIdentifier());
                    order.setPurchaseOrderCampusCode(po.getDeliveryCampus().getCampusCode());
                    
                    if (LOG.isDebugEnabled()){
                        LOG.debug("PO matching Document found");
                    }
                }
            }
            
            ElectronicInvoiceOrderHolder orderHolder = new ElectronicInvoiceOrderHolder(eInvoice,order,po,itemTypeMappings,kualiItemTypes,validateHeader);
            matchingService.doMatchingProcess(orderHolder);
            
            if (orderHolder.isInvoiceRejected()){
                
                ElectronicInvoiceRejectDocument rejectDocument = createAndSaveRejectDocument(eInvoice, order);
                
                ElectronicInvoiceLoadSummary loadSummary = getOrCreateLoadSummary(eInvoiceLoad, eInvoice.getDunsNumber());
                loadSummary.addFailedInvoiceOrder(rejectDocument.getTotalAmount(),eInvoice);
                eInvoiceLoad.insertInvoiceLoadSummary(loadSummary);
                
            }else{
                
                PaymentRequestDocument preqDoc  = createPaymentRequest(orderHolder);
                
                if (orderHolder.isInvoiceRejected()){
                    ElectronicInvoiceRejectDocument rejectDocument = createAndSaveRejectDocument(eInvoice, order);
                    
                    ElectronicInvoiceLoadSummary loadSummary = getOrCreateLoadSummary(eInvoiceLoad, eInvoice.getDunsNumber());
                    loadSummary.addFailedInvoiceOrder(rejectDocument.getTotalAmount(),eInvoice);
                    eInvoiceLoad.insertInvoiceLoadSummary(loadSummary);
                }else{
                    ElectronicInvoiceLoadSummary loadSummary = getOrCreateLoadSummary(eInvoiceLoad, eInvoice.getDunsNumber());
                    loadSummary.addSuccessfulInvoiceOrder(preqDoc.getTotalDollarAmount(),eInvoice);
                    eInvoiceLoad.insertInvoiceLoadSummary(loadSummary);
                }
                
            }
            
            validateHeader = false;
        }
        
        return eInvoice.isFileRejected();
    }
    
    private void setVendorDUNSNumber(ElectronicInvoice eInvoice) {
        
        String dunsNumber = null;
        
        if (StringUtils.equals(eInvoice.getCxmlHeader().getFromDomain(),"DUNS")) {
            dunsNumber = eInvoice.getCxmlHeader().getFromIdentity();
        }else if (StringUtils.equals(eInvoice.getCxmlHeader().getSenderDomain(),"DUNS")) {
            dunsNumber = eInvoice.getCxmlHeader().getSenderIdentity();
        }
        
        if (StringUtils.isNotEmpty((dunsNumber))) {
            if (LOG.isDebugEnabled()){
                LOG.debug("Setting Vendor DUNS number - " + dunsNumber);
            }
            eInvoice.setDunsNumber(dunsNumber);
        }
        
    }
    
    private void setVendorDetails(ElectronicInvoice eInvoice){
        
        if (StringUtils.isNotEmpty(eInvoice.getDunsNumber())){
            
            VendorDetail vendorDetail = vendorService.getVendorByDunsNumber(eInvoice.getDunsNumber());
            
            if (vendorDetail != null) {
                if (LOG.isDebugEnabled()){
                    LOG.debug("Vendor match found - " + vendorDetail.getVendorNumber());
                }
                eInvoice.setVendorHeaderID(vendorDetail.getVendorHeaderGeneratedIdentifier());
                eInvoice.setVendorDetailID(vendorDetail.getVendorDetailAssignedIdentifier());
                eInvoice.setVendorName(vendorDetail.getVendorName());
            }else{
                eInvoice.setVendorHeaderID(null);
                eInvoice.setVendorDetailID(null);
                eInvoice.setVendorName(null);
            }
        }
    }
    
    private Map getItemTypeMappings(Integer vendorHeaderId,
                                    Integer vendorDetailId) {
        
        Map itemTypeMappings = null;
        
        if (vendorHeaderId != null && vendorDetailId != null) {
            
            String vendorNumber = getVendorNumber(vendorHeaderId,vendorDetailId);
            itemTypeMappings = itemTypeMappingsCache.get(vendorNumber);
            
            if (itemTypeMappings == null){
                itemTypeMappings = electronicInvoicingDao.getItemMappingMap(vendorHeaderId,vendorDetailId);
                itemTypeMappingsCache.put(vendorNumber,itemTypeMappings);
            }
        }
        /*else {
            
            itemTypeMappings = itemTypeMappingsCache.get(UNKNOWN_DUNS_IDENTIFIER);
            
            if (itemTypeMappings == null){
                itemTypeMappings = mappingService.getDefaultItemMappingMap();
                itemTypeMappingsCache.put(UNKNOWN_DUNS_IDENTIFIER,itemTypeMappings);
            }
        }*/

        return itemTypeMappings;
    }
    
    private String getVendorNumber(Integer vendorHeaderId,
                                   Integer vendorDetailId ){
        
        if (vendorHeaderId != null && vendorDetailId != null) {
            VendorDetail forVendorNo = new VendorDetail();
            forVendorNo.setVendorHeaderGeneratedIdentifier(vendorHeaderId);
            forVendorNo.setVendorDetailAssignedIdentifier(vendorDetailId);
            return forVendorNo.getVendorName();
        }else{
            return null;
        }
    }
    
    private Map<String, ItemType> getKualiItemTypes(){
        
        if (kualiItemTypes == null){
            
            Collection<ItemType> collection = SpringContext.getBean(BusinessObjectService.class).findAll(ItemType.class);
            Map kualiItemTypes = new HashMap<String, ItemType>();
            
            if (collection == null || collection.size() == 0){
                throw new RuntimeException("Kauli Item types not available");
            }else{
                if (collection != null){
                    ItemType[] itemTypes = new ItemType[collection.size()];
                    collection.toArray(itemTypes);
                    for (int i = 0; i < itemTypes.length; i++) {
                        kualiItemTypes.put(itemTypes[i].getItemTypeCode(),itemTypes[i]);
                    }
                }
            }
        }
        
        return kualiItemTypes;
    }
    
    private boolean checkForCompleteFailure(ElectronicInvoiceLoad electronicInvoiceLoad, 
                                            ElectronicInvoice electronicInvoice,
                                            File invoiceFile){
        
        if (LOG.isDebugEnabled()){
            LOG.debug("Checking for complete failure...");
        }
        
        if (electronicInvoice.getInvoiceDetailRequestHeader().isHeaderInvoiceIndicator()) {
            rejectElectronicInvoiceFile(electronicInvoiceLoad, UNKNOWN_DUNS_IDENTIFIER, invoiceFile,PurapConstants.ElectronicInvoice.HEADER_INVOICE_IND_ON);
            return true;
        }
        
        if (electronicInvoice.getInvoiceDetailOrders().size() < 1) {
            rejectElectronicInvoiceFile(electronicInvoiceLoad, UNKNOWN_DUNS_IDENTIFIER, invoiceFile,PurapConstants.ElectronicInvoice.INVOICE_ORDERS_NOT_FOUND);
            return true;
        }
        
        //IN EPIC, it says - Future Release - Enter valid location for Customer Number from E-Invoice
        //mappingService.getInvoiceCustomerNumber() doesnt have any implementation
//        electronicInvoice.setCustomerNumber(mappingService.getInvoiceCustomerNumber(electronicInvoice));
        
        for (Iterator orderIter = electronicInvoice.getInvoiceDetailOrders().iterator(); orderIter.hasNext();) {
          ElectronicInvoiceOrder invoiceOrder = (ElectronicInvoiceOrder) orderIter.next();
          for (Iterator itemIter = invoiceOrder.getInvoiceItems().iterator(); itemIter.hasNext();) {
            ElectronicInvoiceItem invoiceItem = (ElectronicInvoiceItem) itemIter.next();
            if (invoiceItem != null) {
              invoiceItem.setCatalogNumber(invoiceItem.getReferenceItemIDSupplierPartID());
            }
          }
        }
        
        if (LOG.isDebugEnabled()){
            LOG.debug("No Complete failure");
        }
        
        return false;
        
    }
    
    private ElectronicInvoiceRejectReasonType getRejectReasonType(String rejectReasonTypeCode){
        return matchingService.getElectronicInvoiceRejectReasonType(rejectReasonTypeCode);
    }
    
    private void rejectElectronicInvoiceFile(ElectronicInvoiceLoad eInvoiceLoad, 
                                             String fileDunsNumber, 
                                             File filename, 
                                             String rejectReasonTypeCode) {

        rejectElectronicInvoiceFile(eInvoiceLoad,fileDunsNumber,filename,null,rejectReasonTypeCode);
    }
    
    private void rejectElectronicInvoiceFile(ElectronicInvoiceLoad eInvoiceLoad, 
                                             String fileDunsNumber, 
                                             File invoiceFile, 
                                             String extraDescription,
                                             String rejectReasonTypeCode) {
        if (LOG.isDebugEnabled()){
            LOG.debug("Rejecting the entire invoice file - " + invoiceFile.getName());
        }
        
        ElectronicInvoiceLoadSummary eInvoiceLoadSummary;

        if (eInvoiceLoad.getInvoiceLoadSummaries().containsKey(fileDunsNumber)) {
            eInvoiceLoadSummary = (ElectronicInvoiceLoadSummary) eInvoiceLoad.getInvoiceLoadSummaries().get(fileDunsNumber);
        }else {
            eInvoiceLoadSummary = new ElectronicInvoiceLoadSummary(fileDunsNumber);
        }

        eInvoiceLoadSummary.addFailedInvoiceOrder();
        eInvoiceLoad.insertInvoiceLoadSummary(eInvoiceLoadSummary);
        
        ElectronicInvoiceRejectDocument eInvoiceRejectDocument = null;
        try {
            eInvoiceRejectDocument = (ElectronicInvoiceRejectDocument) KNSServiceLocator.getDocumentService().getNewDocument("ElectronicInvoiceRejectDocument");
            
            eInvoiceRejectDocument.setInvoiceProcessDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
            eInvoiceRejectDocument.setVendorDunsNumber(fileDunsNumber);
            
            if (invoiceFile != null){
                eInvoiceRejectDocument.setInvoiceFileName(invoiceFile.getName());
            }
            
            List<ElectronicInvoiceRejectReason> list = new ArrayList<ElectronicInvoiceRejectReason>(1);
            
            String message = "Complete failure document has been created for the Invoice with Filename '" + invoiceFile.getName() + "' due to this error:\n";
            emailTextErrorList.append(message);
            
            ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(rejectReasonTypeCode,extraDescription, invoiceFile.getName());
            list.add(rejectReason);
            
            emailTextErrorList.append("    - " + rejectReason.getInvoiceRejectReasonDescription());
            emailTextErrorList.append("\n\n");
            
            eInvoiceRejectDocument.setInvoiceRejectReasons(list);
            eInvoiceRejectDocument.getDocumentHeader().setDocumentDescription("Complete failure");
            
            String noteText = "Complete failure - " + rejectReason.getInvoiceRejectReasonDescription();
            
            Note attachmentNote = attachInvoiceXMLWithRejectDoc(eInvoiceRejectDocument,invoiceFile,noteText);
            eInvoiceRejectDocument.addNote(attachmentNote);
            
            KNSServiceLocator.getDocumentService().routeDocument(eInvoiceRejectDocument,null,null);
        }catch (WorkflowException e) {
            throw new RuntimeException(e);
        }

        eInvoiceLoad.addInvoiceReject(eInvoiceRejectDocument);
        
        if (LOG.isDebugEnabled()){
            LOG.debug("Complete failure document has been created (DocNo:" + eInvoiceRejectDocument.getDocumentNumber() + ")");
            LOG.debug("rejectElectronicInvoiceFile() ended");
        }
    }
    
    private Note attachInvoiceXMLWithRejectDoc(ElectronicInvoiceRejectDocument eInvoiceRejectDocument,
                                               File attachmentFile,
                                               String noteText){
        
        Note note;
        try {
            note = SpringContext.getBean(DocumentService.class).createNoteFromDocument(eInvoiceRejectDocument, noteText);
        }catch (Exception e1) {
            throw new RuntimeException(e1);
        }
        
        String attachmentType = null;
        BufferedInputStream fileStream = null;
        try {
            fileStream = new BufferedInputStream(new FileInputStream(attachmentFile));
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        Attachment attachment = null;
        try {
            attachment = KNSServiceLocator.getAttachmentService().createAttachment(eInvoiceRejectDocument, attachmentFile.getName(), "text/xml", (int)attachmentFile.length(), fileStream, attachmentType);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        note.setAttachment(attachment);
        attachment.setNote(note);
        
        return note;
    }
    
    public ElectronicInvoiceRejectDocument createAndSaveRejectDocument(ElectronicInvoice eInvoice,
                                                                       ElectronicInvoiceOrder electronicInvoiceOrder) {

        if (LOG.isDebugEnabled()){
            LOG.debug("Creating reject document [DUNS=" + eInvoice.getDunsNumber() + ",POID=" + electronicInvoiceOrder.getInvoicePurchaseOrderID() + "]");
        }

        ElectronicInvoiceRejectDocument eInvoiceRejectDocument;
        
        try {

            eInvoiceRejectDocument = (ElectronicInvoiceRejectDocument) KNSServiceLocator.getDocumentService().getNewDocument("ElectronicInvoiceRejectDocument");

            eInvoiceRejectDocument.setFileLevelData(eInvoice);
            eInvoiceRejectDocument.setInvoiceOrderLevelData(eInvoice, electronicInvoiceOrder);

            String noteText = "Partial failure";

            Note note = attachInvoiceXMLWithRejectDoc(eInvoiceRejectDocument, getInvoiceFile(eInvoice.getFileName()), noteText);
            
            eInvoiceRejectDocument.addNote(note);
            eInvoiceRejectDocument.getDocumentHeader().setDocumentDescription(noteText);

            
            KNSServiceLocator.getDocumentService().routeDocument(eInvoiceRejectDocument,"Routed by electronic invoice batch job",null);
//            KNSServiceLocator.getDocumentService().saveDocument(eInvoiceRejectDocument);
            
        }catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
        
        if (LOG.isDebugEnabled()){
            LOG.debug("Reject document has been created (DocNo=" + eInvoiceRejectDocument.getDocumentNumber() + ")");
        }
        
        emailTextErrorList.append("An Invoice from file '" + eInvoice.getFileName() + "' has been rejected due to the following errors:\n");
        for (Iterator iter = eInvoiceRejectDocument.getInvoiceRejectReasons().iterator(); iter.hasNext();) {
          ElectronicInvoiceRejectReason reason = (ElectronicInvoiceRejectReason) iter.next();
          emailTextErrorList.append("    - " + reason.getInvoiceRejectReasonDescription() + "\n");
        }
        emailTextErrorList.append("\n\n");
        
        return eInvoiceRejectDocument;
    }
    
    public ElectronicInvoiceLoadSummary getOrCreateLoadSummary(ElectronicInvoiceLoad eInvoiceLoad,
                                                               String fileDunsNumber){
        ElectronicInvoiceLoadSummary eInvoiceLoadSummary;
        
        if (eInvoiceLoad.getInvoiceLoadSummaries().containsKey(fileDunsNumber)) {
            eInvoiceLoadSummary = (ElectronicInvoiceLoadSummary) eInvoiceLoad.getInvoiceLoadSummaries().get(fileDunsNumber);
        }
        else {
            eInvoiceLoadSummary = new ElectronicInvoiceLoadSummary(fileDunsNumber);
        }
        
        return eInvoiceLoadSummary;
        
    }
    
    public ElectronicInvoice loadElectronicInvoice(String filename)
    throws CxmlParseException {
        
      File invoiceFile = new File(filename);
      return loadElectronicInvoice(invoiceFile);
      
    }
    
    public ElectronicInvoice loadElectronicInvoice(File invoiceFile)
    throws CxmlParseException {
      
      if (LOG.isDebugEnabled()){
          LOG.debug("loadElectronicInvoice() started");
      }
      
      BufferedInputStream fileStream = null;
      try {
          fileStream = new BufferedInputStream(new FileInputStream(invoiceFile));
      }catch (FileNotFoundException e) {
          /**
           * This never happen since we're getting this file name from the existing file
           */
          throw new RuntimeException(invoiceFile.getAbsolutePath() + " not available");
      }

      ElectronicInvoice electronicInvoice = null;
      
      try {
          
          byte[] fileByteContent = IOUtils.toByteArray(fileStream);
          electronicInvoice = (ElectronicInvoice) batchInputFileService.parse(electronicInvoiceInputFileType, fileByteContent);
          
      }catch (IOException e) {
          throw new CxmlParseException(e.getMessage());
      }catch (XMLParseException e) {
          throw new CxmlParseException(e.getMessage());
      }finally{
          try {
              fileStream.close();
          }catch (IOException e1) {
              e1.printStackTrace();
          }
      }
      
      if (LOG.isDebugEnabled()){
          LOG.debug("loadElectronicInvoice() ended");
      }
      
      return electronicInvoice;
      
    }
    
    private StringBuffer saveLoadSummary(ElectronicInvoiceLoad eInvoiceLoad) {

        Map savedLoadSummariesMap = new HashMap();
        Integer currentLoadSummaryId = null;
        StringBuffer summaryMessage = new StringBuffer();
        
        for (Iterator iter = eInvoiceLoad.getInvoiceLoadSummaries().keySet().iterator(); iter.hasNext();) {
            
            String dunsNumber = (String) iter.next();
            ElectronicInvoiceLoadSummary eInvoiceLoadSummary = (ElectronicInvoiceLoadSummary) eInvoiceLoad.getInvoiceLoadSummaries().get(dunsNumber);
            
            if (currentLoadSummaryId != null) {
                eInvoiceLoadSummary.setInvoiceLoadSummaryIdentifier(currentLoadSummaryId);
            }
            
            if ((!(UNKNOWN_DUNS_IDENTIFIER.equals(dunsNumber))) || 
                    ((UNKNOWN_DUNS_IDENTIFIER.equals(dunsNumber)) && 
                    !(eInvoiceLoadSummary.getIsEmpty().booleanValue()))) {
                
                LOG.info("runLoadSave() Saving Load Summary for DUNS '" + dunsNumber + "'");
                
                ElectronicInvoiceLoadSummary currentLoadSummary = saveElectronicInvoiceLoadSummary(eInvoiceLoadSummary);
                
                summaryMessage.append("DUNS Number - " + eInvoiceLoadSummary.getVendorDescriptor() + ":\n");
                summaryMessage.append("     " + eInvoiceLoadSummary.getInvoiceLoadSuccessCount() + " successfully processed invoices for a total of $ " + eInvoiceLoadSummary.getInvoiceLoadSuccessAmount().doubleValue() + "\n");
                summaryMessage.append("     " + eInvoiceLoadSummary.getInvoiceLoadFailCount() + " rejected invoices for an approximate total of $ " + eInvoiceLoadSummary.getInvoiceLoadFailAmount().doubleValue() + "\n");
                summaryMessage.append("\n\n");
                currentLoadSummaryId = currentLoadSummary.getInvoiceLoadSummaryIdentifier();
                savedLoadSummariesMap.put(currentLoadSummary.getVendorDunsNumber(), eInvoiceLoadSummary);
                
            } else {
                LOG.info("runLoadSave() Not saving Load Summary for DUNS '" + dunsNumber + "' because empty indicator is '" + eInvoiceLoadSummary.getIsEmpty().booleanValue() + "'");
            }
        }
        
        summaryMessage.append("\n\n");
        
        for (Iterator rejectIter = eInvoiceLoad.getElectronicInvoiceRejects().iterator(); rejectIter.hasNext();) {
            
            ElectronicInvoiceRejectDocument eInvoiceRejectDocument = (ElectronicInvoiceRejectDocument) rejectIter.next();
            LOG.info("runLoadSave() Saving Invoice Reject for DUNS '" + eInvoiceRejectDocument.getVendorDunsNumber() + "'");
            
            if (savedLoadSummariesMap.containsKey(eInvoiceRejectDocument.getVendorDunsNumber())) {
                eInvoiceRejectDocument.setInvoiceLoadSummary((ElectronicInvoiceLoadSummary) savedLoadSummariesMap.get(eInvoiceRejectDocument.getVendorDunsNumber()));
            }
            else {
                eInvoiceRejectDocument.setInvoiceLoadSummary((ElectronicInvoiceLoadSummary) savedLoadSummariesMap.get(UNKNOWN_DUNS_IDENTIFIER));
            }
            
//            saveElectronicInvoiceReject(eInvoiceRejectDocument);
            try {
                
                /*AdHocRouteRecipient adHocRouteRecipient = new AdHocRoutePerson();
                adHocRouteRecipient.setActionRequested(actionRequest);
                adHocRouteRecipient.setId(personUserId);*/
                
                
//                List<AdHocRouteRecipient> adHocRoutingRecipients = new ArrayList<AdHocRouteRecipient>();
//                AdHocRouteRecipient recipient = new AdHocRouteRecipient()
//                GlobalVariables.setUserSession(new UserSession("abolding"));
                SpringContext.getBean(DocumentService.class).routeDocument(eInvoiceRejectDocument, "Routed by electronic invoice batch job", null);
//                SpringContext.getBean(DocumentService.class).saveDocument(eInvoiceRejectDocument);
            }
            catch (WorkflowException e) {
                e.printStackTrace();
            }
        }
        
        moveFileList(eInvoiceLoad.getRejectFilesToMove());
        
        return summaryMessage;
    }
    
    private void sendSummary(StringBuffer message, String filename) {

        LOG.debug("writeToEmailFileEnd() started");

        // write valid data to e-mail file
        // this is used for files not saved to reject DB
        BufferedWriter bw = null;

        try {
            bw = new BufferedWriter(new FileWriter(filename, true));
            bw.write(message.toString());
            bw.newLine();
            bw.flush();
        }
        catch (IOException ioe) {
            LOG.error("writeToEmailFileEnd() Error writing to Email File", ioe);
        }
        finally {
            if (bw != null) {
                try {
                    bw.close();
                }
                catch (IOException ioe2) {
                }
            }
        }
        
        
        /*MailMessage mailMessage = new MailMessage();
        mailMessage.addToAddress("vpremchandran@deltacollege.edu");
        mailMessage.setSubject("E-Invoice Load Results");
        mailMessage.setMessage(message.toString());
        mailMessage.setFromAddress("mpvenkat@gmail.com");
        
        try {
            mailService.sendMessage(mailMessage);
        }catch (InvalidAddressException e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
        }*/
        
    }
    
    public boolean doMatchingProcess(ElectronicInvoiceRejectDocument rejectDocument){
        
        Map itemTypeMappings = getItemTypeMappings(rejectDocument.getVendorHeaderGeneratedIdentifier(),
                                                   rejectDocument.getVendorDetailAssignedIdentifier());
        
        Map kualiItemTypes = getKualiItemTypes();
        
        ElectronicInvoiceOrderHolder rejectDocHolder = new ElectronicInvoiceOrderHolder(rejectDocument,itemTypeMappings,kualiItemTypes);
        matchingService.doMatchingProcess(rejectDocHolder);
        
        return rejectDocHolder.isInvoiceRejected();
    }
    
    public boolean createPaymentRequest(ElectronicInvoiceRejectDocument rejectDocument){
     
        if (rejectDocument.getInvoiceRejectReasons().size() > 0){
            throw new RuntimeException("Not possible to create payment request since the reject document contains " + rejectDocument.getInvoiceRejectReasons().size() + " rejects");
        }
        
        Map itemTypeMappings = getItemTypeMappings(rejectDocument.getVendorHeaderGeneratedIdentifier(),
                                                   rejectDocument.getVendorDetailAssignedIdentifier());

        Map kualiItemTypes = getKualiItemTypes();

        ElectronicInvoiceOrderHolder rejectDocHolder = new ElectronicInvoiceOrderHolder(rejectDocument,itemTypeMappings,kualiItemTypes);
        createPaymentRequest(rejectDocHolder);
        
        return rejectDocHolder.isInvoiceRejected();
        
    }
    
    private PaymentRequestDocument createPaymentRequest(ElectronicInvoiceOrderHolder orderHolder){
        
        if (LOG.isDebugEnabled()){
            LOG.debug("Creating Payment Request document");
        }
        
        GlobalVariables.getMessageList().clear();
        
        validateInvoiceOrderValidForPREQCreation(orderHolder);
        
        if (LOG.isDebugEnabled()){
            if (orderHolder.isInvoiceRejected()){
                LOG.debug("Not possible to convert reject document into payment request");
            }else{
                LOG.debug("Payment request document creation validation succeeded");
            }
        }
        
        if (orderHolder.isInvoiceRejected()){
            return null;
        }
        
        PaymentRequestDocument preqDoc = null;
        try {
            preqDoc = (PaymentRequestDocument) KNSServiceLocator.getDocumentService().getNewDocument("PaymentRequestDocument");
        }
        catch (WorkflowException e) {
            String extraDescription = "Error=" + e.getMessage();
            ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.PREQ_WORKLOW_EXCEPTION,          
                                                                                            extraDescription, 
                                                                                            orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            LOG.error("Error creating Payment request document - " + e.getMessage());
            return null;
        }
        
        PurchaseOrderDocument poDoc = orderHolder.getPurchaseOrderDocument();
        
        preqDoc.getDocumentHeader().setDocumentDescription(generatePREQDocumentDescription(poDoc));
        
        preqDoc.setStatusCode(PurapConstants.PaymentRequestStatuses.IN_PROCESS);
        preqDoc.setInvoiceDate(orderHolder.getInvoiceDate());
        preqDoc.setInvoiceNumber(orderHolder.getInvoiceNumber());
        preqDoc.setVendorInvoiceAmount(new KualiDecimal(orderHolder.getInvoiceNetAmount()));
        preqDoc.setAccountsPayableProcessorIdentifier("E-Invoice");
        preqDoc.setVendorCustomerNumber(orderHolder.getCustomerNumber());
        preqDoc.setCreatedByElectronicInvoice(true);
        
        RequisitionDocument reqDoc = SpringContext.getBean(RequisitionService.class).getRequisitionById(poDoc.getRequisitionIdentifier());
        String reqDocInitiator = reqDoc.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId();
        try {
            UserIdDTO userDTO = new NetworkIdDTO(reqDocInitiator);
            WorkflowUser wfUser = SpringContext.getBean(UserService.class).getWorkflowUser(userDTO);
            UniversalUser user = SpringContext.getBean(UniversalUserService.class).getUniversalUserByAuthenticationUserId(wfUser.getAuthenticationUserId().getAuthenticationId());
            preqDoc.setProcessingCampusCode(user.getCampusCode());
        }catch(Exception e){
            e.printStackTrace();
        }
        
        HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList = SpringContext.getBean(AccountsPayableService.class).expiredOrClosedAccountsList(poDoc);
        if (expiredOrClosedAccountList == null){
            expiredOrClosedAccountList = new HashMap();
        }
        
        if (LOG.isDebugEnabled()){
             LOG.debug(expiredOrClosedAccountList.size() + " accounts has been found as Expired or Closed");
        }
        
        preqDoc.populatePaymentRequestFromPurchaseOrder(orderHolder.getPurchaseOrderDocument(),expiredOrClosedAccountList);
        
        populateItemDetails(preqDoc,orderHolder);
        
        SpringContext.getBean(KualiRuleService.class).applyRules(new CalculateAccountsPayableEvent(preqDoc));
        SpringContext.getBean(PaymentRequestService.class).calculatePaymentRequest(preqDoc,true);
        
        processItemsForDiscount(preqDoc,orderHolder);
        
        if (orderHolder.isInvoiceRejected()){
            return null;
        }
        
        SpringContext.getBean(PaymentRequestService.class).calculatePaymentRequest(preqDoc,false);
        
        SpringContext.getBean(KualiRuleService.class).applyRules(new PaymentRequestForEInvoiceEvent(preqDoc));
        
        if(GlobalVariables.getErrorMap().size() > 0){
//            StringBuffer errors = new StringBuffer(); 
//            for (int i = 0; i < GlobalVariables.getErrorMap().size(); i++) {
//                errors.append(GlobalVariables.getErrorMap().getPropertiesWithErrors().get(i));
//            }
            ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.PREQ_ROUTING_VALIDATION_ERROR, GlobalVariables.getErrorMap().toString(), orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            return null;
        }
        
        /**
         * Is it ok to proceed with the warnings????
         */
        if(GlobalVariables.getMessageList().size() > 0){
            if (LOG.isDebugEnabled()){
                LOG.debug("Payment request contains " + GlobalVariables.getMessageList().size() + " warning message(s)");
                for (int i = 0; i < GlobalVariables.getMessageList().size(); i++) {
                    LOG.debug("Warning " + i + "  - " +GlobalVariables.getMessageList().get(i));
                }
            }
        }
        
        boolean isFakeElectronicInvoicing = false; // USE PARAM 
        
        if (LOG.isDebugEnabled()){
            LOG.debug("Fake electronic invoicing flag is set to " + isFakeElectronicInvoicing);
        }
        
        if (isFakeElectronicInvoicing){
            if (LOG.isInfoEnabled()){
                LOG.info("------------------------------------------------------------------------------");
                LOG.info("PAYMENT REQUEST NOT SAVED DUE TO FAKE ELECTRONIC INVOICING FLAG IS SET TO TRUE");
                LOG.info("PAYMENT REQUEST FOR PO NUMBER '" + orderHolder.getInvoicePurchaseOrderID() + "' WOULD HAVE SAVED");
                LOG.info("------------------------------------------------------------------------------");
            }
            return preqDoc;
        }

        addBillToAndShipToNotes(preqDoc,orderHolder);
        
        try {
            KNSServiceLocator.getDocumentService().routeDocument(preqDoc, "Routed by electronic invoice batch job", null);
        }
        catch (WorkflowException e) {
            e.printStackTrace();
            ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.PREQ_ROUTING_FAILURE, e.getMessage(), orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            return null;
        }catch(ValidationException e){
            ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.PREQ_ROUTING_VALIDATION_ERROR, e.getMessage(), orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            try {
                KNSServiceLocator.getDocumentService().cancelDocument(preqDoc, "Cancelled by electronic invoice batch job because of the validation error " + e.getMessage());
            }
            catch (WorkflowException e1) {
            }
            return null;
        }
        
        return preqDoc;
    }
    
    private void addBillToAndShipToNotes(PaymentRequestDocument preqDoc, 
                                         ElectronicInvoiceOrderHolder orderHolder){
        
        String shipToAddress = orderHolder.getInvoiceShipToAddressAsString();
        String billToAddress = orderHolder.getInvoiceBillToAddressAsString();
        
        try {
            Note noteObj = SpringContext.getBean(DocumentService.class).createNoteFromDocument(preqDoc, shipToAddress);
            preqDoc.addNote(noteObj);
         
            noteObj = SpringContext.getBean(DocumentService.class).createNoteFromDocument(preqDoc, billToAddress);
            preqDoc.addNote(noteObj);
        }catch (Exception e) {
             LOG.error("Error creating shipTo/BillTo notes - " + e.getMessage());
        }
    }
    
    private void processItemsForDiscount(PaymentRequestDocument preqDocument, 
                                         ElectronicInvoiceOrderHolder orderHolder){
        
        if (LOG.isDebugEnabled()){
            LOG.debug("Processing payment request items for discount");
        }
        
        if (!orderHolder.isItemTypeAvailableInItemMapping(ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_DISCOUNT)){
            if (LOG.isDebugEnabled()){
                LOG.debug("Skipping discount processing since there is no mapping of discount type for this vendor");
            }
            return;
        }
        
        if (orderHolder.getInvoiceDiscountAmount() == null ||
            orderHolder.getInvoiceDiscountAmount() == BigDecimal.ZERO){
            if (LOG.isDebugEnabled()){
                LOG.debug("Skipping discount processing since there is no discount amount found in the invoice file");
            }
            return;
        }
        
        KualiDecimal discountValueToUse = new KualiDecimal(orderHolder.getInvoiceDiscountAmount().negate());
        List<PaymentRequestItem> preqItems = preqDocument.getItems();
        
        boolean alreadyProcessedInvoiceDiscount = false;
        boolean hasKualiPaymentTermsDiscountItem = false;
        
        //if e-invoice amount is negative... it is a penalty and we must pay extra 
        for (int i = 0; i < preqItems.size(); i++) {
            
            PaymentRequestItem preqItem = preqItems.get(i);
            
            hasKualiPaymentTermsDiscountItem = hasKualiPaymentTermsDiscountItem || (StringUtils.equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE,preqItem.getItemTypeCode()));
            
            /**
             * TODO: Relook into this code
             */
            if (isItemValidForUpdation(preqItem.getItemTypeCode(),ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_DISCOUNT,orderHolder)){
                
                preqItem.setProcessedOnElectronicInvoice(true);
                alreadyProcessedInvoiceDiscount = true;
                
                if (StringUtils.equals(preqItem.getItemTypeCode(),PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE)){
                    //Item is kuali payment terms discount item... must perform calculation
                    // if discount item exists on PREQ and discount dollar amount exists... use greater amount
                    if (LOG.isDebugEnabled()){
                        LOG.debug("Discount Check - E-Invoice matches PREQ item type '" + preqItem.getItemTypeCode() + "'... now checking for amount");
                    }
                    
                    KualiDecimal preqExtendedPrice = preqItem.getExtendedPrice() == null ? KualiDecimal.ZERO : preqItem.getExtendedPrice();
                    if ( (discountValueToUse.compareTo(preqExtendedPrice)) < 0 ) {
                        if (LOG.isDebugEnabled()){
                            LOG.info("Discount Check - Using E-Invoice amount (" + discountValueToUse + ") as it is more discount than current payment terms amount " + preqExtendedPrice);
                        }
                        preqItem.setItemUnitPrice(discountValueToUse.bigDecimalValue());
                        preqItem.setExtendedPrice(discountValueToUse);
                      }
                }else {
                    /**
                     * FIXME : I dont think this block will gets executed
                     */  
                    // item is not epic payment terms discount item... just add value
                    // if discount item exists on PREQ and discount dollar amount exists... use greater amount
                    if (LOG.isDebugEnabled()){
                        LOG.info("Discount Check - E-Invoice matches PREQ item type '" + preqItem.getItemTypeCode() + "'");
                        LOG.info("Discount Check - Using E-Invoice amount (" + discountValueToUse + ") as it is greater than payment terms amount");
                    }
                    preqItem.addToUnitPrice(discountValueToUse.bigDecimalValue());
                    preqItem.addToExtendedPrice(discountValueToUse);
                  }
                }
         }
        
        /*
         *   If we have not already processed the discount amount then the mapping is pointed
         *   to an item that is not in the PREQ item list
         *   
         *   FYI - FILE DISCOUNT AMOUNT CURRENTLY HARD CODED TO GO INTO PAYMENT TERMS DISCOUNT ITEM ONLY... ALL OTHERS WILL FAIL
         */
        
        /**
         * TODO: Relook into this code
         */
        if (!alreadyProcessedInvoiceDiscount) {
            String itemTypeRequired = PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE;
            // if we already have a PMT TERMS DISC item but the e-invoice discount wasn't processed... error out
            // if the item mapping for e-invoice discount item is not PMT TERMS DISC item and we haven't processed it... error out
            
            if (hasKualiPaymentTermsDiscountItem || 
                !orderHolder.isItemTypeAvailableInItemMapping(ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_DISCOUNT)) {
                //E-Invoice Matching Succeeded but PREQ creation failed due to E-Invoice discount processing on incorrect item (contact development team)
                ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.PREQ_DISCOUNT_ERROR, null, orderHolder.getFileName());
                orderHolder.addInvoiceOrderRejectReason(rejectReason);
                return;
            }
            else {
                PaymentRequestItem newItem = new PaymentRequestItem();
                newItem.setItemUnitPrice(discountValueToUse.bigDecimalValue());
                newItem.setItemTypeCode(PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE);
                newItem.setExtendedPrice(discountValueToUse);
                newItem.setProcessedOnElectronicInvoice(true);
                preqDocument.addItem(newItem);
            }
        }
        
        if (LOG.isDebugEnabled()){
            LOG.debug("Completed processing payment request items for discount");
        }
        
    }
    
    private void populateItemDetails(PaymentRequestDocument preqDocument, 
                                     ElectronicInvoiceOrderHolder orderHolder){
        
        if (LOG.isDebugEnabled()){
            LOG.debug("Populating invoice order items into the payment request document");
        }
        
        List<PurApItem> preqItems = preqDocument.getItems();
        
        boolean hasShippingItem = false;
        
        for (int i = 0; i < preqItems.size(); i++) {
            
            PaymentRequestItem preqItem = (PaymentRequestItem)preqItems.get(i);
            
            if (isItemValidForUpdation(preqItem.getItemTypeCode(),PurapConstants.ItemTypeCodes.ITEM_TYPE_SHIPPING_CODE,orderHolder)){
//                hasShippingItem = true;
//                processShippingItem(preqItem, orderHolder);
            }else if (isItemValidForUpdation(preqItem.getItemTypeCode(),PurapConstants.ItemTypeCodes.ITEM_TYPE_SHIP_AND_HAND_CODE,orderHolder)){
                processSpecialHandlingItem(preqItem, orderHolder);
            }else if (isItemValidForUpdation(preqItem.getItemTypeCode(),PurapConstants.ItemTypeCodes.ITEM_TYPE_ITEM_CODE,orderHolder)){
                processAboveTheLineItem(preqItem, orderHolder);
            }
        }
        
//        if (!hasShippingItem && orderHolder.isItemTypeAvailableInItemMapping(PurapConstants.ItemTypeCodes.ITEM_TYPE_SHIPPING_CODE)){
//            LOG.debug("Creating new Shipping item since it's not available in the existing items");
//            PaymentRequestItem newItem = new PaymentRequestItem();
//            newItem.setProcessedOnElectronicInvoice(true);
//            newItem.setItemTypeCode(PurapConstants.ItemTypeCodes.ITEM_TYPE_SHIPPING_CODE);
//            processShippingItem(newItem, orderHolder);
//            preqDocument.addItem(newItem);
//        }
        
        if (LOG.isDebugEnabled()){
            LOG.debug("Successfully populated the invoice order items");
        }
        
    }
    
    private void processAboveTheLineItem(PaymentRequestItem purapItem,
                                         ElectronicInvoiceOrderHolder orderHolder){

        if (LOG.isDebugEnabled()){
            LOG.debug("Processing above the line item");
        }
        
        ElectronicInvoiceItemHolder itemHolder = orderHolder.getItemByLineNumber(purapItem.getItemLineNumber().intValue());
        if (itemHolder == null){
            //I'm sure this never happen since we are validating all the line numbers during the matching process
            throw new RuntimeException("ItemHolder not found for the item line# " + purapItem.getItemLineNumber());
        }
        
        purapItem.setItemUnitPrice(itemHolder.getInvoiceItemUnitPrice());
        purapItem.setItemQuantity(new KualiDecimal(itemHolder.getInvoiceItemQuantity()));
        
        if (itemHolder.getSubTotalAmount() != null && 
            itemHolder.getSubTotalAmount().compareTo(KualiDecimal.ZERO) > 0){

            purapItem.setExtendedPrice(itemHolder.getSubTotalAmount());
            
        }else{
            
            if (purapItem.getItemQuantity() != null) {
                if (LOG.isDebugEnabled()){
                    LOG.info("Item number " + purapItem.getItemLineNumber() + " needs calculation of extended " +
                             "price from quantity " + purapItem.getItemQuantity() + " and unit cost " + purapItem.getItemUnitPrice());
                }
                purapItem.setExtendedPrice(purapItem.getItemQuantity().multiply(new KualiDecimal(purapItem.getItemUnitPrice())));
              } else {
                  if (LOG.isDebugEnabled()){
                      LOG.info("Item number " + purapItem.getItemLineNumber() + " has no quantity so extended price " +
                               "equals unit price of " + purapItem.getItemUnitPrice());
                  }
                  purapItem.setExtendedPrice(new KualiDecimal(purapItem.getItemUnitPrice()));
              }
        }
        
        purapItem.setProcessedOnElectronicInvoice(true);
        
    }
    
    private void processSpecialHandlingItem(PaymentRequestItem purapItem,
                                            ElectronicInvoiceOrderHolder orderHolder){
        
        if (LOG.isDebugEnabled()){
            LOG.debug("Processing special handling item");
        }

        purapItem.addToUnitPrice(orderHolder.getInvoiceSpecialHandlingAmount());
        purapItem.addToExtendedPrice(new KualiDecimal(orderHolder.getInvoiceSpecialHandlingAmount()));
        
        String invoiceSpecialHandlingDescription = orderHolder.getInvoiceSpecialHandlingDescription();
        
        if (StringUtils.isNotEmpty(invoiceSpecialHandlingDescription)) {
            if (StringUtils.isEmpty(purapItem.getItemDescription())) {
                purapItem.setItemDescription(invoiceSpecialHandlingDescription);
            }
            else {
                purapItem.setItemDescription(purapItem.getItemDescription() + " - " + invoiceSpecialHandlingDescription);
            }
        }
        
        purapItem.setProcessedOnElectronicInvoice(true);
        
    }
    
    private void processShippingItem(PaymentRequestItem preqItem,
                                     ElectronicInvoiceOrderHolder orderHolder){
        
        LOG.info("Processing Shipping Item");
        
        preqItem.addToUnitPrice(orderHolder.getInvoiceShippingAmount());
        preqItem.addToExtendedPrice(new KualiDecimal(orderHolder.getInvoiceShippingAmount()));
        
        if (StringUtils.isNotEmpty(orderHolder.getInvoiceShippingDescription())) {
            if (StringUtils.isEmpty(preqItem.getItemDescription())) {
                preqItem.setItemDescription(orderHolder.getInvoiceShippingDescription());
            } else {
                preqItem.setItemDescription(preqItem.getItemDescription() + " - " + orderHolder.getInvoiceShippingDescription());
            }
        }
    }
    
    /*private void processDepositItem(PaymentRequestItem preqItem,
                                    ElectronicInvoiceOrderHolder orderHolder){

        LOG.info("Processing Deposit Item");
        
        preqItem.addToUnitPrice(orderHolder.getInvoiceShippingAmount());
        preqItem.addToExtendedPrice(new KualiDecimal(orderHolder.getInvoiceShippingAmount()));
        
        if (StringUtils.isNotEmpty(orderHolder.getInvoiceShippingDescription())) {
            if (StringUtils.isEmpty(preqItem.getItemDescription())) {
                preqItem.setItemDescription(orderHolder.getInvoiceShippingDescription());
            } else {
                preqItem.setItemDescription(preqItem.getItemDescription() + " - " + orderHolder.getInvoiceShippingDescription());
            }
        }
        
    }
    
    private void processDueItem(PaymentRequestItem preqItem,
                                ElectronicInvoiceOrderHolder orderHolder){

    }*/
    
    private void setItemDefaultDescription(PaymentRequestItem preqItem,
                                           String itemTypeCode){
        
        //This should be moved to purapconstants and should have proper field list
        String[] ITEM_TYPES_REQUIRES_DESCRIPTION = {PurapConstants.ItemTypeCodes.ITEM_TYPE_ITEM_CODE,
                                                    PurapConstants.ItemTypeCodes.ITEM_TYPE_MISC_CODE};
        
        if (StringUtils.isEmpty(preqItem.getItemDescription())){
            if (ArrayUtils.contains(ITEM_TYPES_REQUIRES_DESCRIPTION, itemTypeCode)){
                preqItem.setItemDescription(PurapConstants.ElectronicInvoice.DEFAULT_BELOW_LINE_ITEM_DESCRIPTION);
            }
        }
    }
    
    private boolean isItemValidForUpdation(String itemTypeCode,
                                           String invoiceItemTypeCode,
                                           ElectronicInvoiceOrderHolder orderHolder){
        
        return orderHolder.isItemTypeAvailableInItemMapping(invoiceItemTypeCode) && invoiceItemTypeCode.equals(itemTypeCode);
    }
     
    
    private String generatePREQDocumentDescription(PurchaseOrderDocument poDocument) {
        
        String description = "PO: " + poDocument.getPurapDocumentIdentifier() + " Vendor: " + poDocument.getVendorName() + " Electronic Invoice";
        int noteTextMaxLength = SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(DocumentHeader.class, KNSPropertyConstants.DOCUMENT_DESCRIPTION).intValue();
        
        if (noteTextMaxLength < description.length()) {
            description = description.substring(0, noteTextMaxLength);
        }
        
        return description;
        
    }
    
    /**
     * This validates an electronic invoice and makes sure it can be turned into a Payment Request
     * 
     */
    public void validateInvoiceOrderValidForPREQCreation(ElectronicInvoiceOrderHolder orderHolder){
        
        if (LOG.isDebugEnabled()){
            LOG.debug("Validiting ElectronicInvocie Order to make sure that it can be turned into a Payment Request document");
        }
        
        PurchaseOrderDocument poDoc = orderHolder.getPurchaseOrderDocument();
        
        if ( poDoc == null){
            throw new RuntimeException("PurchaseOrder not available");
        }
            
        if (!poDoc.getStatusCode().equals(PurchaseOrderStatuses.OPEN)) {
            orderHolder.addInvoiceOrderRejectReason(matchingService.createRejectReason(PurapConstants.ElectronicInvoice.PO_NOT_OPEN,null,orderHolder.getFileName()));
            return;
        }
        
        /**
         * FIXME: Is it needed to move it to the matching process?
         */
        if (!orderHolder.isInvoiceNumberAcceptIndicatorEnabled()){
            List preqs = paymentRequestService.getPaymentRequestsByVendorNumberInvoiceNumber(poDoc.getVendorHeaderGeneratedIdentifier(), 
                                                                                             poDoc.getVendorDetailAssignedIdentifier(), 
                                                                                             orderHolder.getInvoiceNumber());
            
            if (preqs != null && preqs.size() > 0){
                orderHolder.addInvoiceOrderRejectReason(matchingService.createRejectReason(PurapConstants.ElectronicInvoice.INVOICE_ORDER_DUPLICATE,null,orderHolder.getFileName()));
                return;
            }
        }
        
        if (orderHolder.getInvoiceDate() == null){
            orderHolder.addInvoiceOrderRejectReason(matchingService.createRejectReason(PurapConstants.ElectronicInvoice.INVOICE_DATE_INVALID,null,orderHolder.getFileName()));
            return;
        }else if (orderHolder.getInvoiceDate().after(new java.util.Date())) {
            ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.INVOICE_DATE_GREATER,null,orderHolder.getFileName()); 
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            return;
        }
        
    }
    
    public String getSourceDir(){
        return electronicInvoiceInputFileType.getDirectoryPath() + File.separator + "source" + File.separator;
    }
    
    public String getBaseDir(){
        return electronicInvoiceInputFileType.getDirectoryPath() + File.separator;
    }
    
    public String getRejectDir(){
        return electronicInvoiceInputFileType.getDirectoryPath() + File.separator + "reject" + File.separator;
    }
    
    public String getAcceptDir(){
        return electronicInvoiceInputFileType.getDirectoryPath() + File.separator + "accept" + File.separator;
    }
    
    public File getInvoiceFile(String fileName){
        return new File(electronicInvoiceInputFileType.getDirectoryPath() + File.separator + "source" + File.separator + fileName);
    }
    
    private void moveFileList(Map filesToMove) {
        for (Iterator iter = filesToMove.keySet().iterator(); iter.hasNext();) {
            File fileToMove = (File) iter.next();

            boolean success = this.moveFile(fileToMove, (String) filesToMove.get(fileToMove));
            if (!success) {
                String errorMessage = "File with name '" + fileToMove.getName() + "' could not be moved";
                throw new PurError(errorMessage);
            }
        }
    }

    private boolean moveFile(File fileForMove, String location) {
        File moveDir = new File(location);
        boolean success = fileForMove.renameTo(new File(moveDir, fileForMove.getName()));
        if (success) {
            LOG.info("moveFile() Succeeded in moving file '" + fileForMove.getName() + "' to '" + moveDir.getAbsolutePath() + "'");
        }else {
            LOG.error("moveFile() Failed moving file '" + fileForMove.getName() + "' to '" + moveDir.getAbsolutePath() + "'");
        }
        return success;
    }
    
    public ElectronicInvoiceLoadSummary saveElectronicInvoiceLoadSummary(ElectronicInvoiceLoadSummary eils) {
        electronicInvoicingDao.saveElectronicInvoiceLoadSummary(eils);
        return eils;
    }
    
//    public Map getItemMappingMap(Integer vendorHeaderId, Integer vendorDetailId) {
//        return electronicInvoicingDao.getItemMappingMap(vendorHeaderId,vendorDetailId);
//    }
    
//    public void setElectronicInvoiceMappingService(ElectronicInvoiceMappingService mappingService) {
//        this.mappingService = mappingService;
//    }

    public void setElectronicInvoiceInputFileType(ElectronicInvoiceInputFileType electronicInvoiceInputFileType) {
        this.electronicInvoiceInputFileType = electronicInvoiceInputFileType;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void setElectronicInvoicingDao(ElectronicInvoicingDao electronicInvoicingDao) {
        this.electronicInvoicingDao = electronicInvoicingDao;
    }
    
    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }

    public void setElectronicInvoiceMatchingService(ElectronicInvoiceMatchingService matchingService) {
        this.matchingService = matchingService;
    }

    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    public void setPaymentRequestService(PaymentRequestService paymentRequestService) {
        this.paymentRequestService = paymentRequestService;
    }
}
