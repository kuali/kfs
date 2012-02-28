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
package org.kuali.kfs.module.purap.pdf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderContractLanguage;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.exception.PurapConfigurationException;
import org.kuali.kfs.module.purap.service.ImageService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.vnd.businessobject.CampusParameter;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * Contains the parameters needed for creating a purchase order pdf document.
 */
public class PurchaseOrderTransmitParameters implements PurchaseOrderParameters  {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderTransmitParameters.class);

    private String imageTempLocation;
    private String key;
    private String logoImage;
    private String directorSignatureImage;
    private String contractManagerSignatureImage;
    private CampusParameter campusParameter;
    private String statusInquiryUrl;
    private String contractLanguage;
    private String contractManagerCampusCode;
    private boolean useImage;

  //   common parameters for pdf and fax
    private String pdfFileLocation;
    private String pdfFileName;

    // parameters for sending fax
    String recipientFaxNumber;
    String vendorName;
    String faxDescription;

    public PurchaseOrderTransmitParameters() {

    }

    @Override
    public void setPurchaseOrderPdfParameters(PurchaseOrderDocument po) {
          setPurchaseOrderPdfParameters(po, null);
     }

     @Override
    public void setPurchaseOrderPdfParameters(PurchaseOrderDocument po, PurchaseOrderVendorQuote povq) {


         this.key = povq == null ? po.getPurapDocumentIdentifier().toString() : po.getPurapDocumentIdentifier().toString() + povq.getPurchaseOrderVendorQuoteIdentifier().toString(); // key can be any string; chose to use the PO number.
         String campusCode = po.getDeliveryCampusCode().toLowerCase();
         boolean useImage = true;
         if (SpringContext.getBean(ParameterService.class).parameterExists(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_IMAGES_AVAILABLE_INDICATOR)) {
             useImage = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_IMAGES_AVAILABLE_INDICATOR);
         }
         // We'll get the imageTempLocation and the actual images only if the useImage is true. If useImage is false, we'll leave the
         // images as blank space
         if (useImage) {
             if (getImageTempLocation() == null) {
                 throw new PurapConfigurationException("IMAGE_TEMP_PATH is missing");
             }

             // Get images
             if ((this.logoImage = SpringContext.getBean(ImageService.class).getLogo(key, campusCode, imageTempLocation)) == null) {
                 throw new PurapConfigurationException("logoImage is null.");
             }
             if ((this.directorSignatureImage = SpringContext.getBean(ImageService.class).getPurchasingDirectorImage(key, campusCode, imageTempLocation)) == null) {
                 throw new PurapConfigurationException("directorSignatureImage is null.");
             }
             if ((this.contractManagerSignatureImage = SpringContext.getBean(ImageService.class).getContractManagerImage(key, po.getContractManagerCode(), imageTempLocation)) == null) {
                 throw new PurapConfigurationException("contractManagerSignatureImage is null.");
             }
         }

         Map<String, Object> criteria = new HashMap<String, Object>();
         criteria.put(KFSPropertyConstants.CAMPUS_CODE, po.getDeliveryCampusCode());
         this.campusParameter = ((List<CampusParameter>) SpringContext.getBean(BusinessObjectService.class).findMatching(CampusParameter.class, criteria)).get(0);

         if (getStatusInquiryUrl() == null) {
             LOG.debug("generatePurchaseOrderPdf() ended");
             throw new PurapConfigurationException("Application Setting INVOICE_STATUS_INQUIRY_URL is missing.");
         }

         StringBuffer contractLanguage = new StringBuffer();
         criteria.put(KFSPropertyConstants.ACTIVE, true);
         List<PurchaseOrderContractLanguage> contractLanguageList = (List<PurchaseOrderContractLanguage>) (SpringContext.getBean(BusinessObjectService.class).findMatching(PurchaseOrderContractLanguage.class, criteria));
         if (!contractLanguageList.isEmpty()) {
             int lineNumber = 1;
             for (PurchaseOrderContractLanguage row : contractLanguageList) {
                 if (row.getCampusCode().equals(po.getDeliveryCampusCode())) {
                     contractLanguage.append(lineNumber + " " + row.getPurchaseOrderContractLanguageDescription() + "\n");
                     ++lineNumber;
                 }
             }
         }

         this.contractLanguage = contractLanguage.toString();

         if (getPdfFileLocation() == null) {
             LOG.debug("savePurchaseOrderPdf() ended");
             throw new PurapConfigurationException("Application Setting PDF_DIRECTORY is missing.");
         }

         String environment = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.ENVIRONMENT_KEY);

          this.pdfFileName = povq != null ? "PURAP_PO_" + po.getPurapDocumentIdentifier().toString() + "_Quote" + povq.getPurchaseOrderVendorQuoteIdentifier().toString()+ "_"  +  environment + "_" + System.currentTimeMillis() + ".pdf" :
                               "PURAP_PO_" + po.getPurapDocumentIdentifier().toString() + "_" + environment + "_" + System.currentTimeMillis() + ".pdf";


          this.contractManagerCampusCode = po.getContractManager().getContractManagerPerson()!= null ? po.getContractManager().getContractManagerPerson().getCampusCode() : "";
          this.contractLanguage = contractLanguage.toString();

     }


     @Override
    public void setPurchaseOrderFaxParameters(PurchaseOrderDocument po, PurchaseOrderVendorQuote povq) {
         // get parameters to send fax

         if (getPdfFileLocation() == null) {
             LOG.debug("savePurchaseOrderPdf() ended");
             throw new PurapConfigurationException("Application Setting PDF_DIRECTORY is missing.");
         }


         this.pdfFileName = povq != null ? "PURAP_PO_" + po.getPurapDocumentIdentifier().toString() + "_Quote" + povq.getPurchaseOrderVendorQuoteIdentifier().toString()+ "_"  + System.currentTimeMillis() + ".pdf" :
                              "PURAP_PO_" + po.getPurapDocumentIdentifier().toString() + "_" + System.currentTimeMillis() + ".pdf";


          this.faxDescription =  povq != null ? "PO: " + po.getPurapDocumentIdentifier() + " Quote ID: " + povq.getPurchaseOrderVendorQuoteIdentifier():
                                                  "PO: " + po.getPurapDocumentIdentifier() + " Cntrct Mgr: " + po.getContractManager().getContractManagerCode();

          String productionEnvironmentCode = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.PROD_ENVIRONMENT_CODE_KEY);
          String environmentCode           = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.ENVIRONMENT_KEY);

          if (!StringUtils.equals(productionEnvironmentCode, environmentCode)) {
              this.faxDescription = environmentCode + " TEST - " + this.faxDescription;
          }

          this.vendorName = povq != null ? povq.getVendorName() : po.getVendorName();





     }



     @Override
    public void setPurchaseOrderPdfAndFaxParameters(PurchaseOrderDocument po) {
          setPurchaseOrderPdfAndFaxParameters(po, null);
     }

     @Override
    public void setPurchaseOrderPdfAndFaxParameters(PurchaseOrderDocument po, PurchaseOrderVendorQuote povq) {

         this.key = povq == null ? po.getPurapDocumentIdentifier().toString() : po.getPurapDocumentIdentifier().toString() + povq.getPurchaseOrderVendorQuoteIdentifier().toString(); // key can be any string; chose to use the PO number.
         String campusCode = po.getDeliveryCampusCode().toLowerCase();
        // String imageTempLocation = "";
        // String logoImage = "";
        // String directorSignatureImage = "";
       //  String contractManagerSignatureImage = "";
         boolean useImage = true;
         if (SpringContext.getBean(ParameterService.class).parameterExists(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_IMAGES_AVAILABLE_INDICATOR)) {
             useImage = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_IMAGES_AVAILABLE_INDICATOR);
         }
         // We'll get the imageTempLocation and the actual images only if the useImage is true. If useImage is false, we'll leave the
         // images as blank space
         if (useImage) {
             if (getImageTempLocation() == null) {
                 throw new PurapConfigurationException("IMAGE_TEMP_PATH is missing");
             }

             // Get images
             if ((this.logoImage = SpringContext.getBean(ImageService.class).getLogo(key, campusCode, imageTempLocation)) == null) {
                 throw new PurapConfigurationException("logoImage is null.");
             }
             if ((this.directorSignatureImage = SpringContext.getBean(ImageService.class).getPurchasingDirectorImage(key, campusCode, imageTempLocation)) == null) {
                 throw new PurapConfigurationException("directorSignatureImage is null.");
             }
             if ((this.contractManagerSignatureImage = SpringContext.getBean(ImageService.class).getContractManagerImage(key, po.getContractManagerCode(), imageTempLocation)) == null) {
                 throw new PurapConfigurationException("contractManagerSignatureImage is null.");
             }
         }

         Map<String, Object> criteria = new HashMap<String, Object>();
         criteria.put(KFSPropertyConstants.CAMPUS_CODE, po.getDeliveryCampusCode());
         this.campusParameter = ((List<CampusParameter>) SpringContext.getBean(BusinessObjectService.class).findMatching(CampusParameter.class, criteria)).get(0);

         if (getStatusInquiryUrl() == null) {
             LOG.debug("generatePurchaseOrderPdf() ended");
             throw new PurapConfigurationException("Application Setting INVOICE_STATUS_INQUIRY_URL is missing.");
         }

         StringBuffer contractLanguage = new StringBuffer();
         criteria.put(KFSPropertyConstants.ACTIVE, true);
         List<PurchaseOrderContractLanguage> contractLanguageList = (List<PurchaseOrderContractLanguage>) (SpringContext.getBean(BusinessObjectService.class).findMatching(PurchaseOrderContractLanguage.class, criteria));
         if (!contractLanguageList.isEmpty()) {
             int lineNumber = 1;
             for (PurchaseOrderContractLanguage row : contractLanguageList) {
                 if (row.getCampusCode().equals(po.getDeliveryCampusCode())) {
                     contractLanguage.append(lineNumber + " " + row.getPurchaseOrderContractLanguageDescription() + "\n");
                     ++lineNumber;
                 }
             }
         }

         this.contractLanguage = contractLanguage.toString();

         if (getPdfFileLocation() == null) {
             LOG.debug("savePurchaseOrderPdf() ended");
             throw new PurapConfigurationException("Application Setting PDF_DIRECTORY is missing.");
         }

          this.pdfFileName = povq != null ? "PURAP_PO_" + po.getPurapDocumentIdentifier().toString() + "_Quote" + povq.getPurchaseOrderVendorQuoteIdentifier().toString()+ "_"  + System.currentTimeMillis() + ".pdf" :
                             "PURAP_PO_" + po.getPurapDocumentIdentifier().toString() + "_" + System.currentTimeMillis() + ".pdf";


          this.contractManagerCampusCode = po.getContractManager().getContractManagerPerson()!= null ? po.getContractManager().getContractManagerPerson().getCampusCode() : "";

         // get parameters to send fax

          this.faxDescription =  povq != null ? "PO: " + po.getPurapDocumentIdentifier() + " Quote ID: " + povq.getPurchaseOrderVendorQuoteIdentifier():
                                                  "PO: " + po.getPurapDocumentIdentifier() + " Cntrct Mgr: " + po.getContractManager().getContractManagerCode();

          String productionEnvironmentCode = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.PROD_ENVIRONMENT_CODE_KEY);
          String environmentCode           = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.ENVIRONMENT_KEY);

          if (!StringUtils.equals(productionEnvironmentCode, environmentCode)) {
              this.faxDescription = environmentCode + " TEST - " + this.faxDescription;
          }

          this.vendorName = povq != null ? povq.getVendorName() : po.getVendorName();
          this.recipientFaxNumber = povq == null ? po.getVendorFaxNumber(): povq.getVendorFaxNumber();


     }



    public String getContractManagerCampusCode() {
        return contractManagerCampusCode;
    }

    public void setContractManagerCampusCode(String contractManagerCampusCode) {
        this.contractManagerCampusCode = contractManagerCampusCode;
    }

    public String getPdfFileName() {
        return pdfFileName;
    }

    public void setPdfFileName(String pdfFileName) {
        this.pdfFileName = pdfFileName;
    }

    public String getPdfFileLocation() {
        if (pdfFileLocation == null) {
            pdfFileLocation = SpringContext.getBean(ParameterService.class).getParameterValueAsString(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PDF_DIRECTORY);
        }
        return pdfFileLocation;
    }

    public void setPdfFileLocation(String pdfFileLocation) {
        this.pdfFileLocation = pdfFileLocation;
    }

    public CampusParameter getCampusParameter() {
        return campusParameter;
    }

    public void setCampusParameter(CampusParameter campusParameter) {
        this.campusParameter = campusParameter;
    }

    public String getContractLanguage() {
        return contractLanguage;
    }

    public void setContractLanguage(String contractLanguage) {
        this.contractLanguage = contractLanguage;
    }

    public String getContractManagerSignatureImage() {
        return contractManagerSignatureImage;
    }

    public void setContractManagerSignatureImage(String contractManagerSignatureImage) {
        this.contractManagerSignatureImage = contractManagerSignatureImage;
    }

    public String getDirectorSignatureImage() {
        return directorSignatureImage;
    }

    public void setDirectorSignatureImage(String directorSignatureImage) {
        this.directorSignatureImage = directorSignatureImage;
    }

    public String getImageTempLocation() {
        if (imageTempLocation == null) {
            imageTempLocation = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.TEMP_DIRECTORY_KEY) + "/";
        }
        return imageTempLocation;
    }

    public void setImageTempLocation(String imageTempLocation) {
        this.imageTempLocation = imageTempLocation;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(String logoImage) {
        this.logoImage = logoImage;
    }

    public String getStatusInquiryUrl() {
        if (statusInquiryUrl == null) {
            statusInquiryUrl = SpringContext.getBean(ParameterService.class).getParameterValueAsString(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.STATUS_INQUIRY_URL);
        }
        return statusInquiryUrl;
    }

    public void setStatusInquiryUrl(String statusInquiryUrl) {
        this.statusInquiryUrl = statusInquiryUrl;
    }

    public boolean isUseImage() {
        return useImage;
    }

    public void setUseImage(boolean useImage) {
        this.useImage = useImage;
    }

    public String getRecipientFaxNumber() {
      return recipientFaxNumber;
    }

    public void setRecipientFaxNumber(String recipientFaxNumber) {
        this.recipientFaxNumber = recipientFaxNumber;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getFaxDescription() {
        return faxDescription;
    }

    public void setFaxDescription(String faxDescription) {
        this.faxDescription = faxDescription;
    }




}
