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
package org.kuali.kfs.module.purap.document.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderContractLanguage;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote;
import org.kuali.kfs.module.purap.dataaccess.ImageDao;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.FaxService;
import org.kuali.kfs.module.purap.exception.FaxServerUnavailableError;
import org.kuali.kfs.module.purap.exception.FaxSubmissionError;
import org.kuali.kfs.module.purap.exception.PurError;
import org.kuali.kfs.module.purap.exception.PurapConfigurationException;
import org.kuali.kfs.module.purap.pdf.PurchaseOrderPdf;
import org.kuali.kfs.module.purap.pdf.PurchaseOrderPdfParameters;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.vnd.businessobject.CampusParameter;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kns.bo.Campus;
import org.kuali.rice.kns.bo.Country;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.CountryService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class FaxServiceImpl implements FaxService {

    private static final Logger LOG = Logger.getLogger(BulkReceivingServiceImpl.class);

    private KualiConfigurationService kualiConfigurationService;
    private ParameterService parameterService;
    private VendorService vendorService;
    private BusinessObjectService businessObjectService;
    private CountryService countryService;
    private ImageDao imageDao;

    /**
     * Create the Purchase Order Pdf document and send it via fax to the recipient in the PO
     * 
     * @param po PurchaseOrder that holds the Quote
     * @param isRetransmit if passed true then PO is being retransmitted
     * @return Collection of ServiceError objects
     */
    public void faxPurchaseOrderPdf(PurchaseOrderDocument po, boolean isRetransmit) {
        LOG.debug("faxPurchaseOrderPdf(po,reTransmit) started");
        String pdfFileLocation = parameterService.getParameterValue(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_DIRECTORY);
        if (pdfFileLocation == null) {
            throw new RuntimeException("Application Setting PDF_DIRECTORY is missing.");
        }

        String imageTempLocation = kualiConfigurationService.getPropertyString(KFSConstants.TEMP_DIRECTORY_KEY) + "/";
        if (imageTempLocation == null) {
            throw new RuntimeException("Application Setting TEMP_DIRECTORY_KEY is missing.");
        }

        LOG.debug("faxPurchaseOrderPdf() ended");
        this.faxPurchaseOrderPdf(po, pdfFileLocation, imageTempLocation, isRetransmit);
    }

    /**
     * Create the Purchase Order Pdf document and send it via fax to the recipient in the PO
     * 
     * @param po PurchaseOrder that holds the Quote
     * @param isRetransmit if passed true then PO is being retransmitted
     * @return Collection of ServiceError objects
     */
    public void faxPurchaseOrderPdf(PurchaseOrderDocument po, String pdfFileLocation, String imageTempLocation, boolean isRetransmit) {
        LOG.debug("faxPurchaseOrderPdf() started with locations");

        PurchaseOrderPdfParameters pdfParameters = getPurchaseOrderPdfParameters(po);

        String environment = kualiConfigurationService.getPropertyString(KFSConstants.ENVIRONMENT_KEY);

        String pdfFilename = System.currentTimeMillis() + "_" + environment + "_" + po.getPurapDocumentIdentifier().toString() + ".pdf";

        // Get images
        String key = po.getPurapDocumentIdentifier().toString(); // key can be any string; chose to use the PO number.
        String campusCode = po.getDeliveryCampusCode().toLowerCase();
        LOG.debug("Getting images. key is " + key + ". campusCode is " + campusCode);
        String logoImage;
        if ((logoImage = imageDao.getLogo(key, campusCode, imageTempLocation)) == null) {
            GlobalVariables.getErrorMap().putError("errors", "pdf.error", "logoImage is null.");
            LOG.debug("faxPurchaseOrderPdf() ended");
        }
        String directorSignatureImage;
        if ((directorSignatureImage = imageDao.getPurchasingDirectorImage(key, campusCode, imageTempLocation)) == null) {
            GlobalVariables.getErrorMap().putError("errors", "pdf.error", "directorSignatureImage is null." );
            LOG.debug("faxPurchaseOrderPdf() ended");
        }
        String contractManagerSignatureImage;
        if ((contractManagerSignatureImage = imageDao.getContractManagerImage(key, po.getContractManager().getContractManagerCode(), imageTempLocation)) == null) {
            GlobalVariables.getErrorMap().putError("errors", "pdf.error", "contractManagerSignatureImage is null.");
            LOG.debug("faxPurchaseOrderPdf() ended");
        }

        Campus deliveryCampus = pdfParameters.getCampusParameter().getCampus();

        if (deliveryCampus == null) {
            GlobalVariables.getErrorMap().putError("errors", "pdf.error", "delivery campus is null.");
            LOG.debug("faxPurchaseOrderPdf() ended");
        }
        String campusName = deliveryCampus.getCampusName();
        if (campusName == null) {
            LOG.debug("faxPurchaseOrderPdf() ended");
            throw new RuntimeException("Campus Information is missing - campusName: " + campusName);
        }
        String statusInquiryUrl = parameterService.getParameterValue(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.STATUS_INQUIRY_URL);
        if (statusInquiryUrl == null) {
            LOG.debug("faxPurchaseOrderPdf() ended");
            throw new RuntimeException("Application Setting STATUS_INQUIRY_URL is missing.");
        }

        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.CAMPUS_CODE, po.getDeliveryCampusCode());
        CampusParameter campusParameter = (CampusParameter) ((List) businessObjectService.findMatching(CampusParameter.class, criteria)).get(0);

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

        // Get the vendor's country name.
        if (po.getVendorCountryCode() != null) {
            Country vendorCountry = countryService.getByPrimaryId(po.getVendorCountryCode());
            if (vendorCountry != null) {
                po.setVendorCountryCode(vendorCountry.getPostalCountryCode());
            }
            else {
                po.setVendorCountryCode("N/A");
            }
        }
        else {
            po.setVendorCountryCode("N/A");
        }

        PurchaseOrderPdf poPdf = null;
        try {
            poPdf = new PurchaseOrderPdf();
            poPdf.savePdf(po, pdfParameters, isRetransmit, environment);
        }
        catch (PurError e) {
            GlobalVariables.getErrorMap().putError("errors", "error.blank");
                                                                                                  // only need to call once.
            LOG.debug("faxPurchaseOrderPdf() ended");
        }
        catch (Throwable e) {
            LOG.error("faxPurchaseOrderPdf() Faxing Failed on PDF creation - Exception was " + e.getMessage(), e);
            GlobalVariables.getErrorMap().putError("errors", "error.blank", "Faxing Error.  Unable to save pdf file. Please Contact Purchasing");
                                                                                                  // only need to call once.
            LOG.debug("faxPurchaseOrderPdf() ended");
        }

        LOG.info("faxPurchaseOrderPdf() PO: " + po.getPurapDocumentIdentifier() + " with vendor fax number: " + po.getVendorFaxNumber() + " and contract manager ID/Name: " + po.getContractManager().getContractManagerUserIdentifier() + " - " + po.getContractManager().getContractManagerName() + " and long distance code: " + po.getContractManager().getContractManagerPhoneNumber());
        String faxDescription = "PO: " + po.getPurapDocumentIdentifier() + " Cntrct Mgr: " + po.getContractManager().getContractManagerUserIdentifier();
        if (!kualiConfigurationService.isProductionEnvironment()) {
            faxDescription = environment + " TEST - " + faxDescription;
        }
        String[] files = new String[1];
        files[0] = pdfFilename;

        try {
            this.faxPDF(files, pdfFileLocation, po.getVendorFaxNumber(), po.getVendorName(), faxDescription);
        }
        catch (FaxSubmissionError e) {
            GlobalVariables.getErrorMap().putError("errors", "error.blank");
        }
        catch (FaxServerUnavailableError e) {
            GlobalVariables.getErrorMap().putError("errors", "error.blank");
        }
        catch (PurError e) {
            GlobalVariables.getErrorMap().putError("errors", "error.blank");
        }
        catch (Throwable e) {
            LOG.error("faxPurchaseOrderPdf() Faxing Failed Exception was " + e.getMessage(), e);
            GlobalVariables.getErrorMap().putError("errors", "error.blank", "Faxing Error.  Please Contact Purchasing");
        }
        finally {
            try {
                if (poPdf != null) {
                    poPdf.deletePdf(pdfFileLocation, pdfFilename);
                }
                else {
                    // ignore - PDF can't be deleted if PDF doesn't exist
                }
            }
            catch (Throwable e) {
                LOG.error("faxPurchaseOrderPdf() Error deleting PDF" + pdfFilename + " - Exception was " + e.getMessage(), e);
                GlobalVariables.getErrorMap().putError("errors", "error.blank","Your fax was sent successfully but an error occurred that is unrelated to faxing. Please report this problem to Purchasing");
            }
        }

        LOG.debug("faxPurchaseOrderPdf() ended");
    }

    public void faxPurchaseOrderQuotePdf(PurchaseOrderDocument po, PurchaseOrderVendorQuote povq) {
        // TODO Auto-generated method stub
    }

    /**
     * Returns the PurchaseOrderPdfParameters given the PurchaseOrderDocument.
     * 
     * @param po The PurchaseOrderDocument object to be used to obtain the PurchaseOrderPdfParameters.
     * @return The PurchaseOrderPdfParameters given the PurchaseOrderDocument.
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
     * Here is where the PDF is actually faxed, needs to be implemented at each institution
     */
    private void faxPDF(String[] files, String pdfFileLocation, String recipientFaxNumber, String vendorName, String faxDescription) {
        LOG.info("faxPDF() NEEDS TO BE IMPLEMENTED!");
        throw new RuntimeException("faxPDF() NEEDS TO BE IMPLEMENTED!");
    }
    
    public KualiConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public VendorService getVendorService() {
        return vendorService;
    }

    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public CountryService getCountryService() {
        return countryService;
    }

    public void setCountryService(CountryService countryService) {
        this.countryService = countryService;
    }

    public ImageDao getImageDao() {
        return imageDao;
    }

    public void setImageDao(ImageDao imageDao) {
        this.imageDao = imageDao;
    }

}
