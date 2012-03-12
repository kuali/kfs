/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.service.impl;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote;
import org.kuali.kfs.module.purap.document.BulkReceivingDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PrintService;
import org.kuali.kfs.module.purap.exception.PurError;
import org.kuali.kfs.module.purap.exception.PurapConfigurationException;
import org.kuali.kfs.module.purap.pdf.BulkReceivingPdf;
import org.kuali.kfs.module.purap.pdf.PurchaseOrderParameters;
import org.kuali.kfs.module.purap.pdf.PurchaseOrderPdf;
import org.kuali.kfs.module.purap.pdf.PurchaseOrderQuotePdf;
import org.kuali.kfs.module.purap.pdf.PurchaseOrderQuoteRequestsPdf;
import org.kuali.kfs.module.purap.pdf.PurchaseOrderTransmitParameters;
import org.kuali.kfs.module.purap.service.ImageService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PrintServiceImpl implements PrintService {
    private static Log LOG = LogFactory.getLog(PrintServiceImpl.class);
    protected static final boolean TRANSMISSION_IS_RETRANSMIT = true;
    protected static final boolean TRANSMISSION_IS_NOT_RETRANSMIT = !TRANSMISSION_IS_RETRANSMIT;

    private ImageService imageService;
    private ParameterService parameterService;
    private BusinessObjectService businessObjectService;
    private ConfigurationService kualiConfigurationService;
    private PurchaseOrderParameters purchaseOrderParameters;
    
    
   

    /**
     * @see org.kuali.kfs.module.purap.document.service.PrintService#generatePurchaseOrderQuoteRequestsListPdf(org.kuali.kfs.module.purap.document.PurchaseOrderDocument, java.io.ByteArrayOutputStream)
     */
    public Collection generatePurchaseOrderQuoteRequestsListPdf(PurchaseOrderDocument po, ByteArrayOutputStream byteArrayOutputStream) {
        LOG.debug("generatePurchaseOrderQuoteRequestsListPdf() started");
        Collection errors = new ArrayList();

        PurchaseOrderQuoteRequestsPdf poQuoteRequestsPdf = new PurchaseOrderQuoteRequestsPdf();

        try {
            PurchaseOrderTransmitParameters pdfParameters = getPurchaseOrderQuoteRequestsListPdfParameters(po);
            String deliveryCampusName = pdfParameters.getCampusParameter().getCampus().getName();
            poQuoteRequestsPdf.generatePOQuoteRequestsListPdf(po, byteArrayOutputStream, pdfParameters.getCampusParameter().getPurchasingInstitutionName());

        }
        catch (PurError pe) {
            LOG.error("Caught exception ", pe);
            errors.add(pe.getMessage());
        }
        catch (PurapConfigurationException pce) {
            LOG.error("Caught exception ", pce);
            errors.add(pce.getMessage());
        }
        catch (Exception e) {
            LOG.error("Caught exception ", e);
            errors.add(e.getMessage());
        }

        LOG.debug("generatePurchaseOrderQuoteRequestsListPdf() ended");
        return errors;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PrintService#savePurchaseOrderQuoteRequestsListPdf(org.kuali.kfs.module.purap.document.PurchaseOrderDocument)
     */
    public Collection savePurchaseOrderQuoteRequestsListPdf(PurchaseOrderDocument po) {
        return null;
    }

    /**
     * Returns the PurchaseOrderPdfParameters given the PurchaseOrderDocument.
     * 
     * @param po  The PurchaseOrderDocument object to be used to obtain the PurchaseOrderPdfParameters.
     * @return    The PurchaseOrderPdfParameters given the PurchaseOrderDocument.
     */
    /*protected PurchaseOrderPdfFaxParameters getPurchaseOrderQuotePdfParameters(PurchaseOrderDocument po) {
        String key = po.getPurapDocumentIdentifier().toString(); // key can be any string; chose to use the PO number.
        String campusCode = po.getDeliveryCampusCode().toLowerCase();
        String imageTempLocation = "";
        String logoImage = "";
        boolean useImage = true;
        if (parameterService.parameterExists(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_IMAGES_AVAILABLE_INDICATOR)) {
            useImage = parameterService.getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_IMAGES_AVAILABLE_INDICATOR);
        }
        // We'll get the imageTempLocation and the actual images only if the useImage is true. If useImage is false, we'll leave the
        // images as blank space
        if (useImage) {
            imageTempLocation = kualiConfigurationService.getPropertyValueAsString(KFSConstants.TEMP_DIRECTORY_KEY) + "/";

            if (imageTempLocation == null) {
                LOG.debug("generatePurchaseOrderQuotePdf() ended");
                throw new PurapConfigurationException("Application Setting IMAGE_TEMP_PATH is missing");
            }
            // Get logo image.
            logoImage = imageService.getLogo(key, campusCode, imageTempLocation);
        }
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.CAMPUS_CODE, po.getDeliveryCampusCode());
        CampusParameter campusParameter = (CampusParameter) ((List) businessObjectService.findMatching(CampusParameter.class, criteria)).get(0);

        // Get the contract manager's campus
        ContractManager contractManager = po.getContractManager();
        String contractManagerCampusCode = "N/A";
        if (contractManager != null && contractManager.getContractManagerUserIdentifier() != null) {
            Person contractManagerUser = SpringContext.getBean(PersonService.class).getPerson(contractManager.getContractManagerUserIdentifier());
            contractManagerCampusCode = contractManagerUser.getCampusCode();
        }

        String pdfFileLocation = parameterService.getParameterValueAsString(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_DIRECTORY);
        if (pdfFileLocation == null) {
            LOG.debug("savePurchaseOrderPdf() ended");
            throw new PurapConfigurationException("Application Setting PDF_DIRECTORY is missing.");
        }

        PurchaseOrderPdfFaxParameters pdfParameters = new PurchaseOrderPdfFaxParameters();
        pdfParameters.setImageTempLocation(imageTempLocation);
        pdfParameters.setKey(key);
        pdfParameters.setLogoImage(logoImage);
        pdfParameters.setCampusParameter(campusParameter);
        pdfParameters.setContractManagerCampusCode(contractManagerCampusCode);
        pdfParameters.setPdfFileLocation(pdfFileLocation);
        pdfParameters.setUseImage(useImage);
        return pdfParameters;
    }*/

    /**
     * Returns the PurchaseOrderPdfParameters given the PurchaseOrderDocument.
     * 
     * @param po  The PurchaseOrderDocument object to be used to obtain the PurchaseOrderPdfParameters.
     * @return    The PurchaseOrderPdfParameters given the PurchaseOrderDocument.
     */
    protected PurchaseOrderTransmitParameters getPurchaseOrderQuoteRequestsListPdfParameters(PurchaseOrderDocument po) {
        PurchaseOrderParameters purchaseOrderParameters = getPurchaseOrderParameters();
        purchaseOrderParameters.setPurchaseOrderPdfParameters(po);
        return (PurchaseOrderTransmitParameters)purchaseOrderParameters;
        /*String key = po.getPurapDocumentIdentifier().toString(); // key can be any string; chose to use the PO number.
        String campusCode = po.getDeliveryCampusCode().toLowerCase();
        String imageTempLocation = "";
        String logoImage = "";
        boolean useImage = true;
        if (parameterService.parameterExists(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_IMAGES_AVAILABLE_INDICATOR)) {
            useImage = parameterService.getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_IMAGES_AVAILABLE_INDICATOR);
        }
        // We'll get the imageTempLocation and the actual images only if the useImage is true. If useImage is false, we'll leave the
        // images as blank space
        if (useImage) {
            imageTempLocation = kualiConfigurationService.getPropertyValueAsString(KFSConstants.TEMP_DIRECTORY_KEY) + "/";

            if (imageTempLocation == null) {
                LOG.debug("generatePurchaseOrderQuotePdf() ended");
                throw new PurapConfigurationException("Application Setting IMAGE_TEMP_PATH is missing");
            }
            // Get logo image.
            logoImage = imageService.getLogo(key, campusCode, imageTempLocation);
        }
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.CAMPUS_CODE, po.getDeliveryCampusCode());
        CampusParameter campusParameter = (CampusParameter) ((List) businessObjectService.findMatching(CampusParameter.class, criteria)).get(0);

        // Get the contract manager's campus
        ContractManager contractManager = po.getContractManager();
        String contractManagerCampusCode = "";
        if (contractManager.getContractManagerUserIdentifier() != null) {            
            Person contractManagerUser = SpringContext.getBean(PersonService.class).getPerson(contractManager.getContractManagerUserIdentifier());
            contractManagerCampusCode = contractManagerUser.getCampusCode();
        }

        String pdfFileLocation = parameterService.getParameterValueAsString(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_DIRECTORY);
        if (pdfFileLocation == null) {
            LOG.debug("savePurchaseOrderPdf() ended");
            throw new PurapConfigurationException("Application Setting PDF_DIRECTORY is missing.");
        }

        PurchaseOrderPdfFaxParameters pdfParameters = new PurchaseOrderPdfFaxParameters();
        pdfParameters.setImageTempLocation(imageTempLocation);
        pdfParameters.setKey(key);
        pdfParameters.setLogoImage(logoImage);
        pdfParameters.setCampusParameter(campusParameter);
        pdfParameters.setContractManagerCampusCode(contractManagerCampusCode);
        pdfParameters.setPdfFileLocation(pdfFileLocation);
        pdfParameters.setUseImage(useImage);
        return pdfParameters;*/
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PrintService#generatePurchaseOrderQuotePdf(org.kuali.kfs.module.purap.document.PurchaseOrderDocument, org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote, java.io.ByteArrayOutputStream, java.lang.String)
     */
    public Collection generatePurchaseOrderQuotePdf(PurchaseOrderDocument po, PurchaseOrderVendorQuote povq, ByteArrayOutputStream byteArrayOutputStream, String environment) {
        LOG.debug("generatePurchaseOrderQuotePdf() started");

        PurchaseOrderQuotePdf poQuotePdf = new PurchaseOrderQuotePdf();
        Collection errors = new ArrayList();

        try {
            PurchaseOrderParameters purchaseOrderParameters = getPurchaseOrderParameters();
            purchaseOrderParameters.setPurchaseOrderPdfParameters(po,povq);
            PurchaseOrderTransmitParameters pdfParameters = (PurchaseOrderTransmitParameters)purchaseOrderParameters;
            String deliveryCampusName = pdfParameters.getCampusParameter().getCampus().getName();
            poQuotePdf.generatePOQuotePDF(po, povq, deliveryCampusName, pdfParameters.getContractManagerCampusCode(), pdfParameters.getLogoImage(), byteArrayOutputStream, environment);
        }
        catch (PurError pe) {
            LOG.error("Caught exception ", pe);
            errors.add(pe.getMessage());
        }
        catch (PurapConfigurationException pce) {
            LOG.error("Caught exception ", pce);
            errors.add(pce.getMessage());
        }
        LOG.debug("generatePurchaseOrderQuotePdf() ended");
        return errors;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PrintService#savePurchaseOrderQuotePdf(org.kuali.kfs.module.purap.document.PurchaseOrderDocument, org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote, java.lang.String)
     */
    public Collection savePurchaseOrderQuotePdf(PurchaseOrderDocument po, PurchaseOrderVendorQuote povq, String environment) {
        LOG.debug("savePurchaseOrderQuotePdf() started");

        String pdfQuoteFilename = "PURAP_PO_" + po.getPurapDocumentIdentifier().toString() + "_Quote_" + povq.getPurchaseOrderVendorQuoteIdentifier().toString() + "_" + System.currentTimeMillis() + ".pdf";
        PurchaseOrderQuotePdf poQuotePdf = new PurchaseOrderQuotePdf();
        Collection errors = new ArrayList();

        PurchaseOrderTransmitParameters pdfParameters = null;
        try {
            PurchaseOrderParameters purchaseOrderParameters = getPurchaseOrderParameters();
            purchaseOrderParameters.setPurchaseOrderPdfParameters(po,povq);
            pdfParameters = (PurchaseOrderTransmitParameters)purchaseOrderParameters;
            String deliveryCampusName = pdfParameters.getCampusParameter().getCampus().getName();
            poQuotePdf.savePOQuotePDF(po, povq, pdfParameters, environment);
        }
        catch (PurError e) {
            LOG.error("Caught exception ", e);
            errors.add(e.getMessage());
        }
        catch (PurapConfigurationException pce) {
            LOG.error("Caught exception ", pce);
            errors.add(pce.getMessage());
        }
        finally {
            try {
                poQuotePdf.deletePdf(pdfParameters.getPdfFileLocation(), pdfQuoteFilename);
            }
            catch (Throwable e) {
                LOG.error("savePurchaseOrderQuotePdf() Error deleting Quote PDF" + pdfQuoteFilename + " - Exception was " + e.getMessage(), e);
                errors.add(e.getMessage());
            }
        }

        LOG.debug("savePurchaseOrderQuotePdf() ended");
        return errors;
    }
    
    /**
     * Returns the PurchaseOrderPdfParameters given the PurchaseOrderDocument.
     * 
     * @param po  The PurchaseOrderDocument object to be used to obtain the PurchaseOrderPdfParameters.
     * @return    The PurchaseOrderPdfParameters given the PurchaseOrderDocument.
     */
   /* protected PurchaseOrderPdfFaxParameters getPurchaseOrderPdfParameters(PurchaseOrderDocument po) {
        String key = po.getPurapDocumentIdentifier().toString(); // key can be any string; chose to use the PO number.
        String campusCode = po.getDeliveryCampusCode().toLowerCase();
        String imageTempLocation = "";
        String logoImage = "";
        String directorSignatureImage = "";
        String contractManagerSignatureImage = "";
        boolean useImage = true;
        if (parameterService.parameterExists(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_IMAGES_AVAILABLE_INDICATOR)) {
            useImage = parameterService.getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_IMAGES_AVAILABLE_INDICATOR);
        }
        // We'll get the imageTempLocation and the actual images only if the useImage is true. If useImage is false, we'll leave the
        // images as blank space
        if (useImage) {
            imageTempLocation = kualiConfigurationService.getPropertyValueAsString(KFSConstants.TEMP_DIRECTORY_KEY) + "/";

            if (imageTempLocation == null) {
                throw new PurapConfigurationException("IMAGE_TEMP_PATH is missing");
            }

            // Get images
            if ((logoImage = imageService.getLogo(key, campusCode, imageTempLocation)) == null) {
                throw new PurapConfigurationException("logoImage is null.");
            }
            if ((directorSignatureImage = imageService.getPurchasingDirectorImage(key, campusCode, imageTempLocation)) == null) {
                throw new PurapConfigurationException("directorSignatureImage is null.");
            }
            if ((contractManagerSignatureImage = imageService.getContractManagerImage(key, po.getContractManagerCode(), imageTempLocation)) == null) {
                throw new PurapConfigurationException("contractManagerSignatureImage is null.");
            }
        }

        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.CAMPUS_CODE, po.getDeliveryCampusCode());
        CampusParameter campusParameter = (CampusParameter) ((List) businessObjectService.findMatching(CampusParameter.class, criteria)).get(0);

        String statusInquiryUrl = parameterService.getParameterValueAsString(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.STATUS_INQUIRY_URL);
        if (statusInquiryUrl == null) {
            LOG.debug("generatePurchaseOrderPdf() ended");
            throw new PurapConfigurationException("Application Setting INVOICE_STATUS_INQUIRY_URL is missing.");
        }

        StringBuffer contractLanguage = new StringBuffer();
        criteria.put(KFSPropertyConstants.ACTIVE, true);
        List<PurchaseOrderContractLanguage> contractLanguageList = (List<PurchaseOrderContractLanguage>) (businessObjectService.findMatching(PurchaseOrderContractLanguage.class, criteria));
        if (!contractLanguageList.isEmpty()) {
            int lineNumber = 1;
            for (PurchaseOrderContractLanguage row : contractLanguageList) {
                if (row.getCampusCode().equals(po.getDeliveryCampusCode())) {
                    contractLanguage.append(lineNumber + " " + row.getPurchaseOrderContractLanguageDescription() + "\n");
                    ++lineNumber;
                }
            }
        }

        String pdfFileLocation = parameterService.getParameterValueAsString(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_DIRECTORY);
        if (pdfFileLocation == null) {
            LOG.debug("savePurchaseOrderPdf() ended");
            throw new PurapConfigurationException("Application Setting PDF_DIRECTORY is missing.");
        }

        String pdfFileName = "PURAP_PO_" + po.getPurapDocumentIdentifier().toString() + "_" + System.currentTimeMillis() + ".pdf";

        PurchaseOrderPdfFaxParameters pdfParameters = new PurchaseOrderPdfFaxParameters();
        pdfParameters.setCampusParameter(campusParameter);
        pdfParameters.setContractLanguage(contractLanguage.toString());
        pdfParameters.setContractManagerSignatureImage(contractManagerSignatureImage);
        pdfParameters.setDirectorSignatureImage(directorSignatureImage);
        pdfParameters.setImageTempLocation(imageTempLocation);
        pdfParameters.setKey(key);
        pdfParameters.setLogoImage(logoImage);
        pdfParameters.setStatusInquiryUrl(statusInquiryUrl);
        pdfParameters.setPdfFileLocation(pdfFileLocation);
        pdfParameters.setPdfFileName(pdfFileName);
        pdfParameters.setUseImage(useImage);
        return pdfParameters;
    }*/

    /**
     * Creates purchase order pdf document given the input parameters.
     * 
     * @param po                     The PurchaseOrderDocument.
     * @param byteArrayOutputStream  ByteArrayOutputStream that the action is using, where the pdf will be printed to.
     * @param isRetransmit           boolean true if this is a retransmit purchase order document.
     * @param environment            The current environment used (e.g. DEV if it is a development environment).
     * @param retransmitItems        The items selected by the user to be retransmitted.
     * @return                       Collection of error strings.
     */
     protected Collection generatePurchaseOrderPdf(PurchaseOrderDocument po, ByteArrayOutputStream byteArrayOutputStream, boolean isRetransmit, String environment, List<PurchaseOrderItem> retransmitItems) {
        LOG.debug("generatePurchaseOrderPdf() started");

        PurchaseOrderPdf poPdf = new PurchaseOrderPdf();
        Collection errors = new ArrayList();
        try {
            PurchaseOrderParameters purchaseOrderParameters = getPurchaseOrderParameters();
            purchaseOrderParameters.setPurchaseOrderPdfParameters(po);
            PurchaseOrderTransmitParameters pdfParameters = (PurchaseOrderTransmitParameters)purchaseOrderParameters;
            poPdf.generatePdf(po, pdfParameters, byteArrayOutputStream, isRetransmit, environment, retransmitItems);
        }
        catch (PurError e) {
            LOG.error("Caught exception ", e);
            errors.add(e.getMessage());
        }
        catch (PurapConfigurationException pce) {
            LOG.error("Caught exception ", pce);
            errors.add(pce.getMessage());
        }

        LOG.debug("generatePurchaseOrderPdf() ended");
        return errors;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PrintService#generatePurchaseOrderPdf(org.kuali.kfs.module.purap.document.PurchaseOrderDocument,
     *      java.io.ByteArrayOutputStream, java.lang.String)
     */
    public Collection generatePurchaseOrderPdf(PurchaseOrderDocument po, ByteArrayOutputStream byteArrayOutputStream, String environment, List<PurchaseOrderItem> retransmitItems) {
        return generatePurchaseOrderPdf(po, byteArrayOutputStream, TRANSMISSION_IS_NOT_RETRANSMIT, environment, retransmitItems);
    }

  

    /**
     * @see org.kuali.kfs.module.purap.document.service.PrintService#generatePurchaseOrderPdfForRetransmission(org.kuali.kfs.module.purap.document.PurchaseOrderDocument,
     *      java.io.ByteArrayOutputStream, java.lang.String)
     */
    public Collection generatePurchaseOrderPdfForRetransmission(PurchaseOrderDocument po, ByteArrayOutputStream byteArrayOutputStream, String environment, List<PurchaseOrderItem> retransmitItems) {
        return generatePurchaseOrderPdf(po, byteArrayOutputStream, TRANSMISSION_IS_RETRANSMIT, environment, retransmitItems);
    }


    /**
     * Saves the purchase order pdf document.
     * 
     * @param po            The PurchaseOrderDocument.
     * @param isRetransmit  boolean true if this is a retransmit purchase order document.
     * @param environment   The current environment used (e.g. DEV if it is a development environment).
     * @return              Collection of error strings.
     */
    protected Collection savePurchaseOrderPdf(PurchaseOrderDocument po, boolean isRetransmit, String environment) {
        LOG.debug("savePurchaseOrderPdf() started");

        PurchaseOrderPdf poPdf = new PurchaseOrderPdf();
        Collection errors = new ArrayList();

        PurchaseOrderTransmitParameters pdfParameters = null;

        try {
            PurchaseOrderParameters purchaseOrderParameters = getPurchaseOrderParameters();
            purchaseOrderParameters.setPurchaseOrderPdfParameters(po);
            pdfParameters = (PurchaseOrderTransmitParameters)purchaseOrderParameters;
            poPdf.savePdf(po, pdfParameters, isRetransmit, environment);
        }
        catch (PurError e) {
            LOG.error("Caught exception ", e);
            errors.add(e.getMessage());
        }
        catch (PurapConfigurationException pce) {
            LOG.error("Caught exception ", pce);
            errors.add(pce.getMessage());
        }
        finally {
            try {
                poPdf.deletePdf(pdfParameters.getPdfFileLocation(), pdfParameters.getPdfFileName());
            }
            catch (Throwable e) {
                LOG.error("savePurchaseOrderPdf() Error deleting PDF" + pdfParameters.getPdfFileName() + " - Exception was " + e.getMessage(), e);
                errors.add("Error while deleting the pdf after savePurchaseOrderPdf" + e.getMessage());
            }
        }

        LOG.debug("savePurchaseOrderPdf() ended");
        return errors;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PrintService#savePurchaseOrderPdf(org.kuali.kfs.module.purap.document.PurchaseOrderDocument,
     *      java.lang.String)
     */
    public Collection savePurchaseOrderPdf(PurchaseOrderDocument po, String environment) {
        return savePurchaseOrderPdf(po, TRANSMISSION_IS_NOT_RETRANSMIT, environment);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PrintService#savePurchaseOrderPdfForRetransmission(org.kuali.kfs.module.purap.document.PurchaseOrderDocument,
     *      java.lang.String)
     */
    public Collection savePurchaseOrderPdfForRetransmission(PurchaseOrderDocument po, String environment) {
        return savePurchaseOrderPdf(po, TRANSMISSION_IS_RETRANSMIT, environment);
    }

    public Collection generateBulkReceivingPDF(BulkReceivingDocument blkRecDoc, 
                                               ByteArrayOutputStream baosPDF) {
        
        LOG.debug("generateBulkReceivingPDF() started");

        BulkReceivingPdf recBlkTicketPDF = new BulkReceivingPdf();
        Collection errors = new ArrayList();
        
        String imageTempLocation = StringUtils.EMPTY;
        String logoImage = StringUtils.EMPTY;
        
        String key = blkRecDoc.getDocumentNumber().toString(); // key can be any string; 
        String campusCode = blkRecDoc.getDeliveryCampusCode().toLowerCase();
        
        String environment = kualiConfigurationService.getPropertyValueAsString(KFSConstants.ENVIRONMENT_KEY);
        
        boolean useImage = true;
        if (parameterService.parameterExists(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_IMAGES_AVAILABLE_INDICATOR)) {
            useImage = parameterService.getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_IMAGES_AVAILABLE_INDICATOR);
        }

        if (useImage) {
            imageTempLocation = kualiConfigurationService.getPropertyValueAsString(KFSConstants.TEMP_DIRECTORY_KEY) + "/";

            if (imageTempLocation == null) {
                throw new PurapConfigurationException("IMAGE_TEMP_PATH is missing");
            }

            // Get images
            logoImage = imageService.getLogo(key, campusCode, imageTempLocation);
            
            if (StringUtils.isEmpty(logoImage)) {
                throw new PurapConfigurationException("logoImage is null.");
            }
        }
        
        try {
            recBlkTicketPDF.generatePdf(blkRecDoc,baosPDF,logoImage,environment);
        }catch (PurapConfigurationException pce) {
            LOG.error("Caught exception ", pce);
            errors.add(pce.getMessage());
        }

        LOG.debug("generateBulkReceivingPDF() ended");
        return errors;
    }
    
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setImageService(ImageService imageService) {
        this.imageService = imageService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setPurchaseOrderParameters(PurchaseOrderParameters purchaseOrderParameters) {
        this.purchaseOrderParameters = purchaseOrderParameters;
    }
    
    public PurchaseOrderParameters getPurchaseOrderParameters() {
        return SpringContext.getBean(PurchaseOrderParameters.class);
    }
    
}

