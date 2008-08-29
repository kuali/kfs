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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoice;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceDetailRequestHeader;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceItem;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceOrder;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectReason;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectReasonType;
import org.kuali.kfs.module.purap.dataaccess.ElectronicInvoiceItemMappingDao;
import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceLoadService;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceMappingService;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceMatchingService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;

public class ElectronicInvoiceMatchingServiceImpl implements ElectronicInvoiceMatchingService {

    private Logger LOG = Logger.getLogger(ElectronicInvoiceMatchingServiceImpl.class);
    
    private VendorService vendorService;
//    private ElectronicInvoiceMappingService electronicInvoiceMappingService;
    
    private Map<String,ElectronicInvoiceRejectReasonType> rejectReasonTypes;
    private String fileName;
    private Integer purapDocumentIdentifier;
    private List<ElectronicInvoiceRejectReason> rejectReasonList;
    
    public void doMatchingProcess(ElectronicInvoice electronicInvoice) {
        
        rejectReasonList = new ArrayList<ElectronicInvoiceRejectReason>();
        fileName = electronicInvoice.getFileName();
        
        boolean isMatching =  matchElectronicInvoiceToVendor(electronicInvoice) &&
                              isValidInvoiceId(electronicInvoice.getInvoiceDetailRequestHeader().getInvoiceId()) &&
                              isValidInvoiceDate(electronicInvoice.getInvoiceDetailRequestHeader().getInvoiceDate()) &&
                              isInformationOnly(electronicInvoice.getInvoiceDetailRequestHeader().isInformationOnly());
        
        LOG.debug("isMatching...."+isMatching);
        LOG.debug(""+rejectReasonList);
        
        if (!isMatching){
            for (int i = 0; i < rejectReasonList.size(); i++) {
                LOG.debug("rejectReasonList.get(i)...."+rejectReasonList.get(i));
                electronicInvoice.addFileRejectReasonToList(rejectReasonList.get(i));
            }
        }
    }

    public void doMatchingProcess(ElectronicInvoiceRejectDocument rejectDocument) {
        
        rejectReasonList = new ArrayList<ElectronicInvoiceRejectReason>();
        fileName = rejectDocument.getInvoiceFileName();
        purapDocumentIdentifier = rejectDocument.getPurapDocumentIdentifier();
        
        if(!isValidDUNSNumber(rejectDocument.getVendorDunsNumber())){
            GlobalVariables.getErrorMap().putError("fieldName", "Message property", "Prop values");
        }
        
        if (!isValidInvoiceId(rejectDocument.getInvoiceFileNumber())){
            GlobalVariables.getErrorMap().putError("fieldName", "Message property", "Prop values");
        }
        
//        if (!isValidInvoiceDate(rejectDocument.getInvoiceFileDateString())){
//        }
        
        if (!isInformationOnly(rejectDocument.isInvoiceFileInformationOnlyIndicator())){
        }
        
        rejectDocument.setInvoiceRejectReasons(rejectReasonList);
    }

    private boolean matchElectronicInvoiceToVendor(ElectronicInvoice ei) {

        LOG.debug("matchElectronicInvoiceToVendor() started");

        VendorDetail vd = vendorService.getVendorByDunsNumber(ei.getDunsNumber());
        if (vd == null) {
            addRejectReason(PurapConstants.ElectronicInvoice.DUNS_INVALID,ei.getDunsNumber());
            return false;
        }

        LOG.info(" Electronic Invoice DUNS Number '" + ei.getDunsNumber() + "' matches to Kuali Vendor ID '" + vd.getVendorHeaderGeneratedIdentifier() + "-" + vd.getVendorDetailAssignedIdentifier() + "'");

        ei.setVendorHeaderID(vd.getVendorHeaderGeneratedIdentifier());
        ei.setVendorDetailID(vd.getVendorDetailAssignedIdentifier());
        ei.setVendorName(vd.getVendorName());

        LOG.debug("matchElectronicInvoiceToVendor() Vendor " + vd.getVendorHeaderGeneratedIdentifier() + "-" + vd.getVendorDetailAssignedIdentifier());

        return true;
    }
    
    private boolean isValidInvoiceId(String invoiceId){
        if (StringUtils.isBlank(invoiceId)) {
            String errorMessage = "File has no invoice number present";
            addRejectReason(PurapConstants.ElectronicInvoice.INVOICE_ID_INVALID);
            return false;
        }
        return true;
    }
    
    private boolean isValidInvoiceDate(Date invoiceDate) {

        if (invoiceDate == null) {
            String errorMessage = "File has an invalid invoice date (unreadable by Kuali system)";
            addRejectReason(PurapConstants.ElectronicInvoice.INVOICE_DATE_INVALID);
            return false;
        }
        else {
            if (invoiceDate.after(new java.util.Date())) {
                String errorMessage = "File has an invoice date which is greater than the current date";
                addRejectReason(PurapConstants.ElectronicInvoice.INVOICE_DATE_INVALID);
                return false;
            }
        }

        return true;
    }

    private boolean isInformationOnly(boolean isInformationOnly){
        if (!isInformationOnly){
            addRejectReason(PurapConstants.ElectronicInvoice.INFORMATION_ONLY);
            return false;
        }
        return true;
    }
    
    private boolean isValidDUNSNumber(String vendorDUNSNumber) {
        VendorDetail vendorDetail = vendorService.getVendorByDunsNumber(vendorDUNSNumber);
        if (vendorDetail == null) {
          return false;
        }
        return true;
    }
    
    private void addRejectReason(String rejectReasonTypeCode) {
        addRejectReason(rejectReasonTypeCode,StringUtils.EMPTY);
    }
    
    private void addRejectReason(String rejectReasonTypeCode,
                                 String fieldValue) {

        LOG.debug("Adding reject reason - " + rejectReasonTypeCode);
        
        ElectronicInvoiceRejectReasonType rejectReasonType = getElectronicInvoiceRejectReasonType(rejectReasonTypeCode);
        ElectronicInvoiceRejectReason eInvoiceRejectReason = new ElectronicInvoiceRejectReason();

        eInvoiceRejectReason.setPurapDocumentIdentifier(getPurapDocumentIdentifier());
        eInvoiceRejectReason.setInvoiceFileName(getFileName());
        eInvoiceRejectReason.setInvoiceRejectReasonTypeCode(rejectReasonTypeCode);
        /**
         * FIXME: Have to remove this null check once we get all the records in.
         */
        if (rejectReasonType != null){
            String rejectDescription = rejectReasonType.getInvoiceRejectReasonTypeDescription() + "[value=" + StringUtils.defaultString(fieldValue)+ "]";
            eInvoiceRejectReason.setInvoiceRejectReasonDescription(rejectDescription);
            LOG.debug("File Name '" + getFileName() + "' ERROR: " + rejectReasonType.getInvoiceRejectReasonTypeDescription());
        }else{
            LOG.error(rejectReasonTypeCode + " not available in DB");
        }

        rejectReasonList.add(eInvoiceRejectReason);
    }
    
    /*public ElectronicInvoiceRejectReason getRejectReason(String rejectReasonTypeCode){
        
        LOG.debug("getRejectReason() - " + rejectReasonTypeCode);
        
        ElectronicInvoiceRejectReasonType rejectReasonType = getElectronicInvoiceRejectReasonType(rejectReasonTypeCode);
        ElectronicInvoiceRejectReason eInvoiceRejectReason = new ElectronicInvoiceRejectReason();

        eInvoiceRejectReason.setPurapDocumentIdentifier(getPurapDocumentIdentifier());
        eInvoiceRejectReason.setInvoiceFileName(getFileName());
        eInvoiceRejectReason.setInvoiceRejectReasonTypeCode(rejectReasonTypeCode);
        *//**
         * FIXME: Have to remove this null check once we get all the records in.
         *//*
        if (rejectReasonType != null){
            String rejectDescription = rejectReasonType.getInvoiceRejectReasonTypeDescription() + "[value=" + StringUtils.defaultString(fieldValue)+ "]";
            eInvoiceRejectReason.setInvoiceRejectReasonDescription(rejectDescription);
            LOG.debug("File Name '" + getFileName() + "' ERROR: " + rejectReasonType.getInvoiceRejectReasonTypeDescription());
        }else{
            LOG.error(rejectReasonTypeCode + " not available in DB");
        }
        
    }*/
    
    public ElectronicInvoiceRejectReasonType getElectronicInvoiceRejectReasonType(String rejectReasonTypeCode){
        if (rejectReasonTypes == null){
            rejectReasonTypes = getElectronicInvoiceRejectReasonTypes();
        }
        return rejectReasonTypes.get(rejectReasonTypeCode);
    }

    /**
     * FIXME : How to get all the records from the table without using DAO 
     */
    private Map<String, ElectronicInvoiceRejectReasonType> getElectronicInvoiceRejectReasonTypes(){
        
        Collection<ElectronicInvoiceRejectReasonType> collection = SpringContext.getBean(BusinessObjectService.class).findAll(ElectronicInvoiceRejectReasonType.class);
        Map rejectReasonTypesMap = new HashMap<String, ElectronicInvoiceRejectReasonType>();
        
        if (collection != null &&
            collection.size() > 0){
            ElectronicInvoiceRejectReasonType[] rejectReasonTypesArr = new ElectronicInvoiceRejectReasonType[collection.size()];
            collection.toArray(rejectReasonTypesArr);
            for (int i = 0; i < rejectReasonTypesArr.length; i++) {
                rejectReasonTypesMap.put(rejectReasonTypesArr[i].getInvoiceRejectReasonTypeCode(), rejectReasonTypesArr[i]);
            }
        }
        
        return rejectReasonTypesMap;
    }

    
    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    private String getFileName() {
        return fileName;
    }

    private void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private Integer getPurapDocumentIdentifier() {
        return purapDocumentIdentifier;
    }

    private void setPurapDocumentIdentifier(Integer purapDocumentIdentifier) {
        this.purapDocumentIdentifier = purapDocumentIdentifier;
    }

//    public void setElectronicInvoiceMappingService(ElectronicInvoiceMappingService electronicInvoiceMappingService) {
//        this.electronicInvoiceMappingService = electronicInvoiceMappingService;
//    }
}
