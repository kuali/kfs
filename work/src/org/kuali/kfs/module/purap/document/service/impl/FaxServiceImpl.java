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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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
import org.kuali.kfs.module.purap.exception.ServiceError;
import org.kuali.kfs.module.purap.pdf.PurchaseOrderPdf;
import org.kuali.kfs.module.purap.pdf.PurchaseOrderPdfParameters;
import org.kuali.kfs.pdp.exception.ConfigurationError;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Country;
import org.kuali.kfs.sys.service.CountryService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.kfs.vnd.businessobject.CampusParameter;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kns.bo.Campus;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KualiConfigurationService;

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
    public Collection<ServiceError> faxPurchaseOrderPdf(PurchaseOrderDocument po, boolean isRetransmit) {
        LOG.debug("faxPurchaseOrderPdf(po,reTransmit) started");
        String pdfFileLocation = ""; // applicationSettingService.getString("PDF_DIRECTORY");
        if (pdfFileLocation == null) {
            throw new RuntimeException("Application Setting PDF_DIRECTORY is missing.");
        }

        String imageTempLocation = ""; // applicationSettingService.getString("IMAGE_TEMP_PATH");
        if (imageTempLocation == null) {
            throw new RuntimeException("Application Setting IMAGE_TEMP_PATH is missing.");
        }

        LOG.debug("faxPurchaseOrderPdf() ended");
        return this.faxPurchaseOrderPdf(po, pdfFileLocation, imageTempLocation, isRetransmit);
    }

    /**
     * Create the Purchase Order Pdf document and send it via fax to the recipient in the PO
     * 
     * @param po PurchaseOrder that holds the Quote
     * @param isRetransmit if passed true then PO is being retransmitted
     * @return Collection of ServiceError objects
     */
    public Collection<ServiceError> faxPurchaseOrderPdf(PurchaseOrderDocument po, String pdfFileLocation, String imageTempLocation, boolean isRetransmit) {
        LOG.debug("faxPurchaseOrderPdf() started with locations");

        PurchaseOrderPdfParameters pdfParameters = getPurchaseOrderPdfParameters(po);

        Collection<ServiceError> errors = new ArrayList<ServiceError>();
        String environment = kualiConfigurationService.getPropertyString(KFSConstants.ENVIRONMENT_KEY);
        // b2b.setEnvironment(parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class,
        // PurapParameterConstants.B2BParameters.ENVIRONMENT));

        String pdfFilename = System.currentTimeMillis() + "_" + environment + "_" + po.getPurapDocumentIdentifier().toString() + ".pdf";

        // Get images
        String key = po.getPurapDocumentIdentifier().toString(); // key can be any string; chose to use the PO number.
        String campusCode = po.getDeliveryCampusCode().toLowerCase();
        LOG.debug("Getting images. key is " + key + ". campusCode is " + campusCode);
        String logoImage;
        if ((logoImage = imageDao.getLogo(key, campusCode, imageTempLocation)) == null) {
            ServiceError se = new ServiceError("errors", "pdf.error");
            se.addParameter("logoImage is null.");
            errors.add(se);
            LOG.debug("faxPurchaseOrderPdf() ended");
            return errors;
        }
        String directorSignatureImage;
        if ((directorSignatureImage = imageDao.getPurchasingDirectorImage(key, campusCode, imageTempLocation)) == null) {
            ServiceError se = new ServiceError("errors", "pdf.error");
            se.addParameter("directorSignatureImage is null.");
            errors.add(se);
            LOG.debug("faxPurchaseOrderPdf() ended");
            return errors;
        }
        String contractManagerSignatureImage;
        if ((contractManagerSignatureImage = imageDao.getContractManagerImage(key, po.getContractManager().getContractManagerCode(), imageTempLocation)) == null) {
            ServiceError se = new ServiceError("errors", "pdf.error");
            se.addParameter("contractManagerSignatureImage is null.");
            errors.add(se);
            LOG.debug("faxPurchaseOrderPdf() ended");
            return errors;
        }

        Campus deliveryCampus = pdfParameters.getCampusParameter().getCampus();

        // String campusName = pdfParameters. getCampusParameter().getCampus().getCampusName();

        if (deliveryCampus == null) {
            ServiceError se = new ServiceError("errors", "pdf.error");
            se.addParameter("delivery campus is null.");
            errors.add(se);
            LOG.debug("faxPurchaseOrderPdf() ended");
            return errors;
        }
        String campusName = deliveryCampus.getCampusName();
        // String directorName = deliveryCampus.get getCampusPurchasingDirectorName();
        // String directorTitle = deliveryCampus.getCampusPurchasingDirectorTitle();
        String directorName = "";
        String directorTitle = "";
        if ((campusName == null) || (directorName == null) || (directorTitle == null)) {
            LOG.debug("faxPurchaseOrderPdf() ended");
            throw new ConfigurationError("Campus Information is missing - campusName: " + campusName + "  - directorName: " + directorName + "  - directorTitle: " + directorTitle);
        }
        String statusInquiryUrl = parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.STATUS_INQUIRY_URL);
        if (statusInquiryUrl == null) {
            LOG.debug("faxPurchaseOrderPdf() ended");
            throw new ConfigurationError("Application Setting STATUS_INQUIRY_URL is missing.");
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
            // savePdf(po, pdfFileLocation, pdfFilename, statusInquiryUrl, campusName,
            // contractLanguage.toString(), logoImage, directorSignatureImage, directorName,
            // directorTitle, contractManagerSignatureImage, isRetransmit, environment);
        }
        catch (PurError e) {
            ServiceError se = new ServiceError("errors", "error.blank");
            se.addParameter(e.getMessage());
            errors.add(se);
            imageDao.removeImages(po.getPurapDocumentIdentifier().toString(), imageTempLocation); // Removes the temporary images;
                                                                                                  // only need to call once.
            LOG.debug("faxPurchaseOrderPdf() ended");
            return errors;
        }
        catch (Throwable e) {
            LOG.error("faxPurchaseOrderPdf() Faxing Failed on PDF creation - Exception was " + e.getMessage(), e);
            ServiceError se = new ServiceError("errors", "error.blank");
            se.addParameter("Faxing Error.  Unable to save pdf file. Please Contact Purchasing");
            errors.add(se);
            imageDao.removeImages(po.getPurapDocumentIdentifier().toString(), imageTempLocation); // Removes the temporary images;
                                                                                                  // only need to call once.
            LOG.debug("faxPurchaseOrderPdf() ended");
            return errors;
        }

        LOG.info("faxPurchaseOrderPdf() PO: " + po.getPurapDocumentIdentifier() + " with vendor fax number: " + po.getVendorFaxNumber() + " and contract manager ID/Name: " + po.getContractManager().getContractManagerUserIdentifier() + " - " + po.getContractManager().getContractManagerName() + " and long distance code: " + po.getContractManager().getContractManagerPhoneNumber());
        String faxNumber = this.getFaxNumberToUse(po.getVendorFaxNumber());
        String faxDescription = "PO: " + po.getPurapDocumentIdentifier() + " Cntrct Mgr: " + po.getContractManager().getContractManagerUserIdentifier();
        if (!kualiConfigurationService.isProductionEnvironment()) {
            faxDescription = environment + " TEST - " + faxDescription;
        }
        String[] files = new String[1];
        files[0] = pdfFilename;

        try {
            this.faxPDF(files, pdfFileLocation, faxNumber, po.getVendorName(), faxDescription);
        }
        catch (FaxSubmissionError e) {
            ServiceError se = new ServiceError("errors", "error.blank");
            se.addParameter(e.getMessage());
            errors.add(se);
        }
        catch (FaxServerUnavailableError e) {
            ServiceError se = new ServiceError("errors", "error.blank");
            se.addParameter(e.getMessage());
            errors.add(se);
        }
        catch (PurError e) {
            ServiceError se = new ServiceError("errors", "error.blank");
            se.addParameter(e.getMessage());
            errors.add(se);
        }
        catch (Throwable e) {
            LOG.error("faxPurchaseOrderPdf() Faxing Failed Exception was " + e.getMessage(), e);
            ServiceError se = new ServiceError("errors", "error.blank");
            se.addParameter("Faxing Error.  Please Contact Purchasing");
            errors.add(se);
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
                ServiceError se = new ServiceError("errors", "error.blank");
                se.addParameter("Your fax was sent successfully but an error occurred that is unrelated to faxing. Please report this problem to Purchasing");
                errors.add(se);
            }
        }

        imageDao.removeImages(po.getPurapDocumentIdentifier().toString(), imageTempLocation); // Removes the temporary images; only need to call once.
        LOG.debug("faxPurchaseOrderPdf() ended");
        return errors;
    }

    public Collection<ServiceError> faxPurchaseOrderQuotePdf(PurchaseOrderDocument po, PurchaseOrderVendorQuote povq) {
        // TODO Auto-generated method stub
        return null;
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
        if (parameterService.parameterExists(ParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_IMAGES_AVAILABLE_INDICATOR)) {
            useImage = parameterService.getIndicatorParameter(ParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_IMAGES_AVAILABLE_INDICATOR);
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

        String statusInquiryUrl = parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.STATUS_INQUIRY_URL);
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

        String pdfFileLocation = parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_DIRECTORY);
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
     * Here we are calling the FaxPress WebService and giving it a PDF
     * file that we would like to fax.
     */
    private void faxPDF(String[] files, String pdfFileLocation, String recipientFaxNumber, String vendorName, String faxDescription) {
//      LOG.debug("faxPDF() started");
//      
//      String faxUser = applicationSettingService.getString("FAXPRESS_USER_NAME");
//      if (faxUser == null) {
//        LOG.error("faxPDF() Error getting FaxPress user for FaxPress");
//        throw new ConfigurationError("Error getting FaxPress user for FaxPress");
//      }
//      String faxPass = applicationSettingService.getString("FAXPRESS_USER_PASSWORD");
//      if (faxPass == null) {
//        LOG.error("faxPDF() Error getting FaxPress password for FaxPress user " + faxUser);
//        throw new ConfigurationError("Error getting FaxPress password for FaxPress user " + faxUser);
//      }
////      String faxLocation = applicationSettingService.getString("FAXPRESS_WEBSERVICE_URL");
////      if (faxLocation == null) {
////        String error = "Error getting Faxpress location url using application setting name '" + faxLocation + "'";
////        LOG.error("faxPDF()" + error);
////        throw new ConfigurationError(error);
////      }
//      
//      WsQMClientSoapBindingStub binding;
//      try {
//        FaxPressQMLocator locator = new FaxPressQMLocator();
////        if (EnvironmentService.PRODUCTION_ENVIRONMENT.equals(environmentService.getEnvironment())) {
////            locator.setEnvironment(FaxPressQMLocator.ENVIRONMENT_TEST);
////        } else {
////            locator.setEnvironment(FaxPressQMLocator.ENVIRONMENT_PRODUCTION);
////        }
//        binding = (WsQMClientSoapBindingStub)locator.getwsQMClientSoapPort();
//      } catch (ServiceException e) {
//        LOG.error("faxPDF() Error getting port from fax server: " + e.getMessage(),e);
//        throw new FaxServerUnavailableError("Error getting port from fax server",e);
//      }
//      
//      binding.setTimeout(60000);  // One minute timeout
//
//      IntHolder sessionId = new IntHolder();
//
//      try {
//        ShortHolder returnCode = new ShortHolder();
//        binding.QMOpenSession(returnCode,faxUser,faxPass,sessionId);
//        // return code of 0 means FaxPress session opened ok
//        if (returnCode.value != ((short)0)) {
//          LOG.error("faxPDF() QMOpenSession Return Code: " + returnCode.value);
//          throw new FaxServerUnavailableError("Fax Server Unavailable - Error Opening Session with FaxPress.  FaxPress sent return code " + returnCode.value);
//        }
//      } catch (RemoteException e1) {
//        LOG.error("faxPDF() Fax Server Unavailable - Error Opening Session: " + e1.getMessage(),e1);
//        throw new FaxServerUnavailableError("Fax Server Unavailable - Error Opening Session",e1);
//      }
//
//      FaxSubmissionError submissionError =  null;
//      try {
//        ShortHolder returnCode = new ShortHolder();
//        
//        binding._setProperty(Call.ATTACHMENT_ENCAPSULATION_FORMAT,Call.ATTACHMENT_ENCAPSULATION_FORMAT_DIME);
//        File file = new File(pdfFileLocation + "/" + files[0]);
//        DataHandler dh = new DataHandler(new FileDataSource(file));
//        binding.addAttachment(dh);
//
//        // need a new DataHandler for each file
////        dh = new DataHandler(new FileDataSource(pdfFileLocation + "/" + files[0]));
////        binding.addAttachment(dh);
//
//        CpiDestinationParamDefExWs[] destinations = new CpiDestinationParamDefExWs[1];
//
//        destinations[0] = new CpiDestinationParamDefExWs();
//        destinations[0].setCover_page( (short)1 );
//        destinations[0].setBill_back_code("bill_back");
//        destinations[0].setBill_back_matter("bill_matter");
//        destinations[0].setSzCoverPageName("Urgent");
//        destinations[0].setTarget_timdat(new TimeDateDef());
//
//        CpiNamePhoneDefExWs[] phones = new CpiNamePhoneDefExWs[1];
//
//        // Fax information of recipient
//        phones[0] = new CpiNamePhoneDefExWs();
//        phones[0].setName(vendorName);
//        phones[0].setFaxPhone(recipientFaxNumber);
//        phones[0].setName2("");
//        phones[0].setName3("");
//        phones[0].setName4("");
//        phones[0].setName5("");
//        phones[0].setCompany(vendorName);
//        phones[0].setBillBackCode("");
//        phones[0].setVoicePhone("");
//        phones[0].setEmail("");
//
//        CpiFaxCommonParamDefWs param = new CpiFaxCommonParamDefWs();
//        // FaxPress Server Description goes here
//        param.setComment(faxDescription);
//        param.setCoverMsg("Indiana University Fax to " + vendorName);
//        param.setSender("Indiana University Purchasing Department");
//
//        IntHolder failCount = new IntHolder();
//        ArrayOfstringHolder failedFiles = new ArrayOfstringHolder();
//        ArrayOfintHolder jobIds = new ArrayOfintHolder();
//        IntHolder bid = new IntHolder();
//        ShortHolder unknown = new ShortHolder();
//
//        for (int i = 0; i < files.length; i++) {
//         LOG.debug("faxPDF() Faxing file with name " + files[i]);
//        }
//        binding.QMSubmitFax(returnCode, sessionId.value, files, destinations, (short)1, phones,
//            unknown, param, files.length,
//            failCount, failedFiles, jobIds, bid);
//        // return code of 0 means FaxPress closed session ok
//        if (returnCode.value != ((short)0)) {
//          LOG.error("faxPDF() QMSubmitFax Return Code: " + returnCode.value);
//          throw new FaxSubmissionError("Fax Server Error - Error Submitting Fax to FaxPress.  FaxPress sent return code " + returnCode.value);
//        }
//
//        binding.clearAttachments();
//      } catch (RemoteException e3) {
//        LOG.error("faxPDF() Fax Server Unavailable - Error Submitting Fax: " + e3.getMessage(),e3);
//        binding.clearAttachments();
//        submissionError =  new FaxSubmissionError("Fax Server Error - Error Submitting Fax",e3);
//      }
//
//      try {
//        ShortHolder returnCode = new ShortHolder();
//        binding.QMCloseSession(returnCode,sessionId.value);
//        // return code of 0 means FaxPress closed session ok
//        if (returnCode.value != ((short)0)) {
//          LOG.error("faxPDF() QMCloseSession Return Code: " + returnCode.value);
//          throw new FaxServerUnavailableError("Fax Server Unavailable - Error Closing Session with FaxPress.  FaxPress sent return code " + returnCode.value);
//        }
//        if (submissionError != null) {
//          LOG.error("faxPDF() QMCloseSession returned code " + returnCode.value + " but we had a Submission Error: " + submissionError);
//          throw submissionError;
//        }
//      } catch (RemoteException e2) {
//        LOG.error("faxPDF() Fax Server Unavailable - Error Closing Session: " + e2.getMessage(),e2);
//        throw new FaxServerUnavailableError("Fax Server Unavailable - Error Closing Session",e2);
//      }
    }
    
    private String getFaxNumberToUse(String faxNumber) {
        LOG.info("getFaxNumberToUse() started with faxNumber " + faxNumber);

        String fakeFaxNumber = parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.FAX_TEST_PHONE_NUMBER);
        if (!StringUtils.isEmpty(fakeFaxNumber)) {
            LOG.info("getFaxNumberToUse() Using fake fax number: " + fakeFaxNumber);
            faxNumber = fakeFaxNumber;
        }
        return faxNumber;
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
