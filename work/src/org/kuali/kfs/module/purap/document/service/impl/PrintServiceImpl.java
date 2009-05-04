/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.service.impl;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderContractLanguage;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote;
import org.kuali.kfs.module.purap.dataaccess.ImageDao;
import org.kuali.kfs.module.purap.document.BulkReceivingDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PrintService;
import org.kuali.kfs.module.purap.exception.PurError;
import org.kuali.kfs.module.purap.exception.PurapConfigurationException;
import org.kuali.kfs.module.purap.pdf.BulkReceivingPdf;
import org.kuali.kfs.module.purap.pdf.PurchaseOrderPdf;
import org.kuali.kfs.module.purap.pdf.PurchaseOrderPdfParameters;
import org.kuali.kfs.module.purap.pdf.PurchaseOrderQuotePdf;
import org.kuali.kfs.module.purap.pdf.PurchaseOrderQuoteRequestsPdf;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.vnd.businessobject.CampusParameter;
import org.kuali.kfs.vnd.businessobject.ContractManager;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.ParameterService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PrintServiceImpl implements PrintService {
    private static Log LOG = LogFactory.getLog(PrintServiceImpl.class);
    private static final boolean TRANSMISSION_IS_RETRANSMIT = true;
    private static final boolean TRANSMISSION_IS_NOT_RETRANSMIT = !TRANSMISSION_IS_RETRANSMIT;

    private ImageDao imageDao;
    private ParameterService parameterService;
    private BusinessObjectService businessObjectService;
    private KualiConfigurationService kualiConfigurationService;
    
    
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setImageDao(ImageDao imageDao) {
        this.imageDao = imageDao;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PrintService#generatePurchaseOrderQuoteRequestsListPdf(org.kuali.kfs.module.purap.document.PurchaseOrderDocument, java.io.ByteArrayOutputStream)
     */
    public Collection generatePurchaseOrderQuoteRequestsListPdf(PurchaseOrderDocument po, ByteArrayOutputStream byteArrayOutputStream) {
        LOG.debug("generatePurchaseOrderQuoteRequestsListPdf() started");
        Collection errors = new ArrayList();

        PurchaseOrderQuoteRequestsPdf poQuoteRequestsPdf = new PurchaseOrderQuoteRequestsPdf();

        try {
            PurchaseOrderPdfParameters pdfParameters = getPurchaseOrderQuoteRequestsListPdfParameters(po);
            String deliveryCampusName = pdfParameters.getCampusParameter().getCampus().getCampusName();
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
    private PurchaseOrderPdfParameters getPurchaseOrderQuotePdfParameters(PurchaseOrderDocument po) {
        String key = po.getPurapDocumentIdentifier().toString(); // key can be any string; chose to use the PO number.
        String campusCode = po.getDeliveryCampusCode().toLowerCase();
        String imageTempLocation = "";
        String logoImage = "";
        boolean useImage = true;
        if (parameterService.parameterExists(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_IMAGES_AVAILABLE_INDICATOR)) {
            useImage = parameterService.getIndicatorParameter(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_IMAGES_AVAILABLE_INDICATOR);
        }
        // We'll get the imageTempLocation and the actual images only if the useImage is true. If useImage is false, we'll leave the
        // images as blank space
        if (useImage) {
            imageTempLocation = kualiConfigurationService.getPropertyString(KFSConstants.TEMP_DIRECTORY_KEY) + "/";

            if (imageTempLocation == null) {
                LOG.debug("generatePurchaseOrderQuotePdf() ended");
                throw new PurapConfigurationException("Application Setting IMAGE_TEMP_PATH is missing");
            }
            // Get logo image.
            logoImage = imageDao.getLogo(key, campusCode, imageTempLocation);
        }
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.CAMPUS_CODE, po.getDeliveryCampusCode());
        CampusParameter campusParameter = (CampusParameter) ((List) businessObjectService.findMatching(CampusParameter.class, criteria)).get(0);

        // Get the contract manager's campus
        ContractManager contractManager = po.getContractManager();
        String contractManagerCampusCode = "N/A";
        if (contractManager != null && contractManager.getContractManagerUserIdentifier() != null) {
            Person contractManagerUser = KIMServiceLocator.getPersonService().getPerson(contractManager.getContractManagerUserIdentifier());
            contractManagerCampusCode = contractManagerUser.getCampusCode();
        }

        String pdfFileLocation = parameterService.getParameterValue(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_DIRECTORY);
        if (pdfFileLocation == null) {
            LOG.debug("savePurchaseOrderPdf() ended");
            throw new PurapConfigurationException("Application Setting PDF_DIRECTORY is missing.");
        }

        PurchaseOrderPdfParameters pdfParameters = new PurchaseOrderPdfParameters();
        pdfParameters.setImageTempLocation(imageTempLocation);
        pdfParameters.setKey(key);
        pdfParameters.setLogoImage(logoImage);
        pdfParameters.setCampusParameter(campusParameter);
        pdfParameters.setContractManagerCampusCode(contractManagerCampusCode);
        pdfParameters.setPdfFileLocation(pdfFileLocation);
        pdfParameters.setUseImage(useImage);
        return pdfParameters;
    }

    /**
     * Returns the PurchaseOrderPdfParameters given the PurchaseOrderDocument.
     * 
     * @param po  The PurchaseOrderDocument object to be used to obtain the PurchaseOrderPdfParameters.
     * @return    The PurchaseOrderPdfParameters given the PurchaseOrderDocument.
     */
    private PurchaseOrderPdfParameters getPurchaseOrderQuoteRequestsListPdfParameters(PurchaseOrderDocument po) {
        String key = po.getPurapDocumentIdentifier().toString(); // key can be any string; chose to use the PO number.
        String campusCode = po.getDeliveryCampusCode().toLowerCase();
        String imageTempLocation = "";
        String logoImage = "";
        boolean useImage = true;
        if (parameterService.parameterExists(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_IMAGES_AVAILABLE_INDICATOR)) {
            useImage = parameterService.getIndicatorParameter(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_IMAGES_AVAILABLE_INDICATOR);
        }
        // We'll get the imageTempLocation and the actual images only if the useImage is true. If useImage is false, we'll leave the
        // images as blank space
        if (useImage) {
            imageTempLocation = kualiConfigurationService.getPropertyString(KFSConstants.TEMP_DIRECTORY_KEY) + "/";

            if (imageTempLocation == null) {
                LOG.debug("generatePurchaseOrderQuotePdf() ended");
                throw new PurapConfigurationException("Application Setting IMAGE_TEMP_PATH is missing");
            }
            // Get logo image.
            logoImage = imageDao.getLogo(key, campusCode, imageTempLocation);
        }
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.CAMPUS_CODE, po.getDeliveryCampusCode());
        CampusParameter campusParameter = (CampusParameter) ((List) businessObjectService.findMatching(CampusParameter.class, criteria)).get(0);

        // Get the contract manager's campus
        ContractManager contractManager = po.getContractManager();
        String contractManagerCampusCode = "";
        if (contractManager.getContractManagerUserIdentifier() != null) {            
            Person contractManagerUser = KIMServiceLocator.getPersonService().getPerson(contractManager.getContractManagerUserIdentifier());
            contractManagerCampusCode = contractManagerUser.getCampusCode();
        }

        String pdfFileLocation = parameterService.getParameterValue(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_DIRECTORY);
        if (pdfFileLocation == null) {
            LOG.debug("savePurchaseOrderPdf() ended");
            throw new PurapConfigurationException("Application Setting PDF_DIRECTORY is missing.");
        }

        PurchaseOrderPdfParameters pdfParameters = new PurchaseOrderPdfParameters();
        pdfParameters.setImageTempLocation(imageTempLocation);
        pdfParameters.setKey(key);
        pdfParameters.setLogoImage(logoImage);
        pdfParameters.setCampusParameter(campusParameter);
        pdfParameters.setContractManagerCampusCode(contractManagerCampusCode);
        pdfParameters.setPdfFileLocation(pdfFileLocation);
        pdfParameters.setUseImage(useImage);
        return pdfParameters;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.PrintService#generatePurchaseOrderQuotePdf(org.kuali.kfs.module.purap.document.PurchaseOrderDocument, org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote, java.io.ByteArrayOutputStream, java.lang.String)
     */
    public Collection generatePurchaseOrderQuotePdf(PurchaseOrderDocument po, PurchaseOrderVendorQuote povq, ByteArrayOutputStream byteArrayOutputStream, String environment) {
        LOG.debug("generatePurchaseOrderQuotePdf() started");

        PurchaseOrderQuotePdf poQuotePdf = new PurchaseOrderQuotePdf();
        Collection errors = new ArrayList();

        try {
            PurchaseOrderPdfParameters pdfParameters = getPurchaseOrderQuotePdfParameters(po);
            String deliveryCampusName = pdfParameters.getCampusParameter().getCampus().getCampusName();
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
        catch (Exception e) {
            LOG.error("Caught exception ", e);
            errors.add(e.getMessage());
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

        PurchaseOrderPdfParameters pdfParameters = null;
        try {
            pdfParameters = getPurchaseOrderQuotePdfParameters(po);
            String deliveryCampusName = pdfParameters.getCampusParameter().getCampus().getCampusName();
            poQuotePdf.savePOQuotePDF(po, povq, pdfParameters.getPdfFileLocation(), pdfQuoteFilename, deliveryCampusName, pdfParameters.getContractManagerCampusCode(), pdfParameters.getLogoImage(), environment);
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
    private PurchaseOrderPdfParameters getPurchaseOrderPdfParameters(PurchaseOrderDocument po) {
        String key = po.getPurapDocumentIdentifier().toString(); // key can be any string; chose to use the PO number.
        String campusCode = po.getDeliveryCampusCode().toLowerCase();
        String imageTempLocation = "";
        String logoImage = "";
        String directorSignatureImage = "";
        String contractManagerSignatureImage = "";
        boolean useImage = true;
        if (parameterService.parameterExists(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_IMAGES_AVAILABLE_INDICATOR)) {
            useImage = parameterService.getIndicatorParameter(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_IMAGES_AVAILABLE_INDICATOR);
        }
        // We'll get the imageTempLocation and the actual images only if the useImage is true. If useImage is false, we'll leave the
        // images as blank space
        if (useImage) {
            imageTempLocation = kualiConfigurationService.getPropertyString(KFSConstants.TEMP_DIRECTORY_KEY) + "/";

            if (imageTempLocation == null) {
                throw new PurapConfigurationException("IMAGE_TEMP_PATH is missing");
            }

            // Get images
            if ((logoImage = imageDao.getLogo(key, campusCode, imageTempLocation)) == null) {
                throw new PurapConfigurationException("logoImage is null.");
            }
            if ((directorSignatureImage = imageDao.getPurchasingDirectorImage(key, campusCode, imageTempLocation)) == null) {
                throw new PurapConfigurationException("directorSignatureImage is null.");
            }
            if ((contractManagerSignatureImage = imageDao.getContractManagerImage(key, po.getContractManagerCode(), imageTempLocation)) == null) {
                throw new PurapConfigurationException("contractManagerSignatureImage is null.");
            }
        }

        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.CAMPUS_CODE, po.getDeliveryCampusCode());
        CampusParameter campusParameter = (CampusParameter) ((List) businessObjectService.findMatching(CampusParameter.class, criteria)).get(0);

        String statusInquiryUrl = parameterService.getParameterValue(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.STATUS_INQUIRY_URL);
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

        String pdfFileLocation = parameterService.getParameterValue(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_DIRECTORY);
        if (pdfFileLocation == null) {
            LOG.debug("savePurchaseOrderPdf() ended");
            throw new PurapConfigurationException("Application Setting PDF_DIRECTORY is missing.");
        }

        String pdfFileName = "PURAP_PO_" + po.getPurapDocumentIdentifier().toString() + "_" + System.currentTimeMillis() + ".pdf";

        PurchaseOrderPdfParameters pdfParameters = new PurchaseOrderPdfParameters();
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
    }

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
    private Collection generatePurchaseOrderPdf(PurchaseOrderDocument po, ByteArrayOutputStream byteArrayOutputStream, boolean isRetransmit, String environment, List<PurchaseOrderItem> retransmitItems) {
        LOG.debug("generatePurchaseOrderPdf() started");

        PurchaseOrderPdf poPdf = new PurchaseOrderPdf();
        Collection errors = new ArrayList();
        try {
            PurchaseOrderPdfParameters pdfParameters = getPurchaseOrderPdfParameters(po);
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
    private Collection savePurchaseOrderPdf(PurchaseOrderDocument po, boolean isRetransmit, String environment) {
        LOG.debug("savePurchaseOrderPdf() started");

        PurchaseOrderPdf poPdf = new PurchaseOrderPdf();
        Collection errors = new ArrayList();

        PurchaseOrderPdfParameters pdfParameters = null;

        try {
            pdfParameters = getPurchaseOrderPdfParameters(po);
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
        
        String environment = kualiConfigurationService.getPropertyString(KFSConstants.ENVIRONMENT_KEY);
        
        boolean useImage = true;
        if (parameterService.parameterExists(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_IMAGES_AVAILABLE_INDICATOR)) {
            useImage = parameterService.getIndicatorParameter(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_IMAGES_AVAILABLE_INDICATOR);
        }

        if (useImage) {
            imageTempLocation = kualiConfigurationService.getPropertyString(KFSConstants.TEMP_DIRECTORY_KEY) + "/";

            if (imageTempLocation == null) {
                throw new PurapConfigurationException("IMAGE_TEMP_PATH is missing");
            }

            // Get images
            logoImage = imageDao.getLogo(key, campusCode, imageTempLocation);
            
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
    
}

