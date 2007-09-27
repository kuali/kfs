package org.kuali.module.purap.service.impl;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.bo.CampusParameter;
import org.kuali.module.purap.bo.PurchaseOrderContractLanguage;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.PurchaseOrderVendorQuote;
import org.kuali.module.purap.dao.ImageDao;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.exceptions.PurError;
import org.kuali.module.purap.exceptions.PurapConfigurationException;
import org.kuali.module.purap.pdf.PurchaseOrderPdf;
import org.kuali.module.purap.pdf.PurchaseOrderPdfParameters;
import org.kuali.module.purap.pdf.PurchaseOrderQuotePdf;
import org.kuali.module.purap.pdf.PurchaseOrderQuoteRequestsPdf;
import org.kuali.module.purap.service.PrintService;
import org.kuali.module.vendor.bo.ContractManager;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PrintServiceImpl implements PrintService {
    private static Log LOG = LogFactory.getLog(PrintServiceImpl.class);
    private static final boolean TRANSMISSION_IS_RETRANSMIT = true;
    private static final boolean TRANSMISSION_IS_NOT_RETRANSMIT = !TRANSMISSION_IS_RETRANSMIT;
    private ImageDao imageDao;
    //private PurchaseOrderVendorStipulationsService purchaseOrderVendorStipulationsService;
    //private VendorService vendorService;
    //private ReferenceService referenceService;
    //private PurchaseOrderContractLanguageDao purchaseOrderContractLanguageDao;
    //private UserService userService;
    private KualiConfigurationService kualiConfigurationService;
    private BusinessObjectService businessObjectService;
   
    /**
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * @param imageDao The imageDao to set
     */
    public void setImageDao(ImageDao imageDao) {
        this.imageDao = imageDao;
    }

    /**
     * @param businessObjectService The businessObjectService to set
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Create the Purchase Order Quote Requests List Pdf document and send it back
     * to the Action so that it can be dealt with.
     * 
     * @param po                         PurchaseOrder that holds the Quote
     * @param byteArrayOutputStream      ByteArrayOutputStream that the action is using
     * @return Collection of ServiceError objects
     */
    public Collection generatePurchaseOrderQuoteRequestsListPdf(PurchaseOrderDocument po, ByteArrayOutputStream byteArrayOutputStream) {
    LOG.debug("generatePurchaseOrderQuoteRequestsListPdf() started");
    Collection errors = new ArrayList();

    PurchaseOrderQuoteRequestsPdf poQuoteRequestsPdf = new PurchaseOrderQuoteRequestsPdf();

    try {
        PurchaseOrderPdfParameters pdfParameters = getPurchaseOrderQuoteRequestsListPdfParameters(po);
        String deliveryCampusName = pdfParameters.getCampusParameter().getCampus().getCampusName();
        poQuoteRequestsPdf.generatePOQuoteRequestsListPdf(po, byteArrayOutputStream);
        if (pdfParameters.isUseImage()) {
            imageDao.removeImages(po.getPurapDocumentIdentifier().toString(), pdfParameters.getImageTempLocation()); // Removes temporary images; only need to call once.
        }
    } catch (PurError pe) {
        LOG.error("Caught exception ", pe);
        errors.add(pe.getMessage());
    } catch (PurapConfigurationException pce) {
        LOG.error("Caught exception ", pce);
        errors.add(pce.getMessage());
    } catch (Exception e) {
        LOG.error("Caught exception ", e);
        errors.add(e.getMessage());
    }
    
    LOG.debug("generatePurchaseOrderQuoteRequestsListPdf() ended");
    return errors;
  }
  
  /**
   * Create the Purchase Order Quote Requests List Pdf document and save it
   * so that it can be faxed in a later process.
   * 
   * @param po        PurchaseOrderDocument that holds the Quote
   * @return Collection of ServiceError objects
   */
  public Collection savePurchaseOrderQuoteRequestsListPdf(PurchaseOrderDocument po) {
      /* TODO: Uncomment this method when we find out what to do for Kuali
    LOG.debug("savePurchaseOrderQuoteRequestsListPdf() started");
    LOG.debug("savePurchaseOrderQuoteRequestsListPdf() started");

    String pdfQuotesRequestsListFilename = "PURAP_PO_"+po.getId().toString()+"_Quotes_Requests_List_"+System.currentTimeMillis()+".pdf";
    PurchaseOrderQuoteRequestsPdf poQuoteRequestsPdf = new PurchaseOrderQuoteRequestsPdf();
    Collection pdfErrorsIn = new ArrayList();
    Collection pdfErrorsOut = new ArrayList();

    String pdfFileLocation = applicationSettingService.getString("PDF_DIRECTORY");
    if (pdfFileLocation == null) {
      LOG.debug("savePurchaseOrderQuoteRequestsListPdf() ended");
      throw new ConfigurationException("Application Setting PDF_DIRECTORY is missing.");
    }

    try {
      pdfErrorsIn = poQuoteRequestsPdf.savePOQuoteRequestsListPdf(po, pdfFileLocation, pdfQuotesRequestsListFilename);
    } catch (PurError e) {
      ServiceError se = new ServiceError("errors","error.blank");
      se.addParameter(e.getMessage());
      pdfErrorsIn.add(se);
    } finally {
      try {
        poQuoteRequestsPdf.deletePdf(pdfFileLocation, pdfQuotesRequestsListFilename);
      } catch (Throwable e) {
        LOG.error("savePurchaseOrderQuoteRequestsListPdf() Error deleting Quote Requests PDF" + pdfQuotesRequestsListFilename + " - Exception was " + e.getMessage(), e);
        ServiceError se = new ServiceError("errors","error.blank");
        se.addParameter("Your PO Quote Requests PDF was saved successfully but an error occurred. Please report this problem to Purchasing");
        pdfErrorsIn.add(se);
      }
    }
    
    if (!pdfErrorsIn.isEmpty()) {
      for (Iterator iter = pdfErrorsIn.iterator(); iter.hasNext();) {
        String errorMessage = (String) iter.next();
        ServiceError se = new ServiceError("pdf.error", errorMessage);
        pdfErrorsOut.add(se);
      }
    }
    LOG.debug("savePurchaseOrderQuoteRequestsListPdf() ended");
    return pdfErrorsOut;
    */
      return null;
  }

  
    private PurchaseOrderPdfParameters getPurchaseOrderQuotePdfParameters(PurchaseOrderDocument po) {
        String key = po.getPurapDocumentIdentifier().toString(); // key can be any string; chose to use the PO number.
        String campusCode = po.getDeliveryCampusCode().toLowerCase();
        String imageTempLocation = "";
        String logoImage = "";
        boolean useImage = true;
        if ( kualiConfigurationService.parameterExists(KFSConstants.PURAP_NAMESPACE,  KFSConstants.Components.DOCUMENT, PurapConstants.PDF_IMAGES_AVAILABLE_INDICATOR) ) {
            useImage = kualiConfigurationService.getIndicatorParameter(KFSConstants.PURAP_NAMESPACE,  KFSConstants.Components.DOCUMENT, PurapConstants.PDF_IMAGES_AVAILABLE_INDICATOR);
        }
        //We'll get the imageTempLocation and the actual images only if the useImage is true. If useImage is false, we'll leave the images as blank space
        if (useImage) {
            imageTempLocation = kualiConfigurationService.getParameterValue(KFSConstants.PURAP_NAMESPACE, KFSConstants.Components.DOCUMENT, PurapConstants.IMAGE_TEMP_PATH);
        if ( imageTempLocation == null ) {
            LOG.debug("generatePurchaseOrderQuotePdf() ended");
            throw new PurapConfigurationException("Application Setting IMAGE_TEMP_PATH is missing");      
        }
        // Get logo image.
        logoImage = imageDao.getLogo(key, campusCode, imageTempLocation); 
        }
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.CAMPUS_CODE, po.getDeliveryCampusCode());
        CampusParameter campusParameter = (CampusParameter)((List) businessObjectService.findMatching(CampusParameter.class, criteria)).get(0);

        // Get the contract manager's campus
        criteria.clear();
        ContractManager contractManager = po.getContractManager();
        String contractManagerCampusCode = "N/A";
        if (contractManager != null  && contractManager.getContractManagerUserIdentifier() != null) {
            criteria.put("personUserIdentifier", contractManager.getContractManagerUserIdentifier());
            UniversalUser contractManagerUser = (UniversalUser) ((List) businessObjectService.findMatching(UniversalUser.class, criteria)).get(0);
            contractManagerCampusCode = contractManagerUser.getCampusCode();
        }
        else {
            
        }
 
        String pdfFileLocation = kualiConfigurationService.getParameterValue(KFSConstants.PURAP_NAMESPACE, KFSConstants.Components.DOCUMENT, PurapConstants.PDF_DIRECTORY);
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
    
    private PurchaseOrderPdfParameters getPurchaseOrderQuoteRequestsListPdfParameters(PurchaseOrderDocument po) {
        String key = po.getPurapDocumentIdentifier().toString(); // key can be any string; chose to use the PO number.
        String campusCode = po.getDeliveryCampusCode().toLowerCase();
        String imageTempLocation = "";
        String logoImage = "";
        boolean useImage = true;
        if ( kualiConfigurationService.parameterExists(KFSConstants.PURAP_NAMESPACE,  KFSConstants.Components.DOCUMENT, PurapConstants.PDF_IMAGES_AVAILABLE_INDICATOR) ) {
            useImage = kualiConfigurationService.getIndicatorParameter(KFSConstants.PURAP_NAMESPACE,  KFSConstants.Components.DOCUMENT, PurapConstants.PDF_IMAGES_AVAILABLE_INDICATOR);
        }
        //We'll get the imageTempLocation and the actual images only if the useImage is true. If useImage is false, we'll leave the images as blank space
        if (useImage) {
            imageTempLocation = kualiConfigurationService.getParameterValue(KFSConstants.PURAP_NAMESPACE, KFSConstants.Components.DOCUMENT, PurapConstants.IMAGE_TEMP_PATH);
        if ( imageTempLocation == null ) {
            LOG.debug("generatePurchaseOrderQuotePdf() ended");
            throw new PurapConfigurationException("Application Setting IMAGE_TEMP_PATH is missing");      
        }
        // Get logo image.
        logoImage = imageDao.getLogo(key, campusCode, imageTempLocation); 
        }
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.CAMPUS_CODE, po.getDeliveryCampusCode());
        CampusParameter campusParameter = (CampusParameter)((List) businessObjectService.findMatching(CampusParameter.class, criteria)).get(0);

        // Get the contract manager's campus
        criteria.clear();
        ContractManager contractManager = po.getContractManager();
        criteria.put("personUserIdentifier", contractManager.getContractManagerUserIdentifier());
        String contractManagerCampusCode = "";
        if (contractManager.getContractManagerUserIdentifier() != null) {
            UniversalUser contractManagerUser = (UniversalUser) ((List) businessObjectService.findMatching(UniversalUser.class, criteria)).get(0);
            contractManagerCampusCode = contractManagerUser.getCampusCode();
        }
        
        String pdfFileLocation = kualiConfigurationService.getParameterValue(KFSConstants.PURAP_NAMESPACE, KFSConstants.Components.DOCUMENT, PurapConstants.PDF_DIRECTORY);
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
 
    /*****************************************************************
  
    /**
     * Create the Purchase Order Quote Pdf document and send it back
     * to the Action so that it can be dealt with.
     * 
     * @param po                         PurchaseOrderDocument that holds the Quote
     * @param povq                       PurchaseOrderVendorQuote that is being transmitted to
     * @param byteArrayOutputStream      ByteArrayOutputStream that the action is using
     * @return Collection of ServiceError objects
     */
    public Collection generatePurchaseOrderQuotePdf(PurchaseOrderDocument po, PurchaseOrderVendorQuote povq, 
        ByteArrayOutputStream byteArrayOutputStream, String environment) {
        LOG.debug("generatePurchaseOrderQuotePdf() started");

        PurchaseOrderQuotePdf poQuotePdf = new PurchaseOrderQuotePdf();
        Collection errors = new ArrayList();

        try {
            PurchaseOrderPdfParameters pdfParameters = getPurchaseOrderQuotePdfParameters(po);
            String deliveryCampusName = pdfParameters.getCampusParameter().getCampus().getCampusName();
            poQuotePdf.generatePOQuotePDF(po, povq, deliveryCampusName, pdfParameters.getContractManagerCampusCode(), 
                pdfParameters.getLogoImage(), byteArrayOutputStream, environment);
            if (pdfParameters.isUseImage()) {
            imageDao.removeImages(po.getPurapDocumentIdentifier().toString(), pdfParameters.getImageTempLocation()); // Removes temporary images; only need to call once.
            }
        } catch (PurError pe) {
            LOG.error("Caught exception ", pe);
            errors.add(pe.getMessage());
        } catch (PurapConfigurationException pce) {
            LOG.error("Caught exception ", pce);
            errors.add(pce.getMessage());
        } catch (Exception e) {
            LOG.error("Caught exception ", e);
            errors.add(e.getMessage());
        }

        LOG.debug("generatePurchaseOrderQuotePdf() ended");
        return errors;
    }
   
    /**
     * Create the Purchase Order Quote Pdf document and save it
     * so that it can be faxed in a later process.
     * 
     * @param po        PurchaseOrderDocument that holds the Quote
     * @param povq      PurchaseOrderVendorQuote that is being transmitted to
     * @return Collection of ServiceError objects
     */
    public Collection savePurchaseOrderQuotePdf(PurchaseOrderDocument po, PurchaseOrderVendorQuote povq,
        String environment) {
        LOG.debug("savePurchaseOrderQuotePdf() started");

        String pdfQuoteFilename = "PURAP_PO_"+po.getPurapDocumentIdentifier().toString()+"_Quote_"+povq.getPurchaseOrderVendorQuoteIdentifier().toString()+"_"+System.currentTimeMillis()+".pdf";
        PurchaseOrderQuotePdf poQuotePdf = new PurchaseOrderQuotePdf();
        Collection errors = new ArrayList();

        PurchaseOrderPdfParameters pdfParameters = null;
        try {
            pdfParameters = getPurchaseOrderQuotePdfParameters(po);
            String deliveryCampusName = pdfParameters.getCampusParameter().getCampus().getCampusName();
            poQuotePdf.savePOQuotePDF(po, povq, pdfParameters.getPdfFileLocation(), pdfQuoteFilename, deliveryCampusName, 
                pdfParameters.getContractManagerCampusCode(), pdfParameters.getLogoImage(), environment);
            if (pdfParameters.isUseImage()) {
            imageDao.removeImages(po.getPurapDocumentIdentifier().toString(), pdfParameters.getImageTempLocation()); // Removes temporary images; only need to call once.
            }
        } catch (PurError e) {
            LOG.error("Caught exception ", e);
            errors.add(e.getMessage());
        } catch (PurapConfigurationException pce) {
            LOG.error("Caught exception ", pce);
            errors.add(pce.getMessage());
        } finally {
            try {
                poQuotePdf.deletePdf(pdfParameters.getPdfFileLocation(), pdfQuoteFilename);
            } catch (Throwable e) {
                LOG.error("savePurchaseOrderQuotePdf() Error deleting Quote PDF" + pdfQuoteFilename + " - Exception was " + e.getMessage(), e);
                errors.add(e.getMessage());
            }
        }
     
        LOG.debug("savePurchaseOrderQuotePdf() ended");
        return errors;
    }
      
    private PurchaseOrderPdfParameters getPurchaseOrderPdfParameters(PurchaseOrderDocument po) {
        String key = po.getPurapDocumentIdentifier().toString(); // key can be any string; chose to use the PO number.
        String campusCode = po.getDeliveryCampusCode().toLowerCase();
        String imageTempLocation = "";
        String logoImage = "";
        String directorSignatureImage = "";
        String contractManagerSignatureImage = "";
        boolean useImage = true;
        if ( kualiConfigurationService.parameterExists(KFSConstants.PURAP_NAMESPACE, KFSConstants.Components.DOCUMENT,  PurapConstants.PDF_IMAGES_AVAILABLE_INDICATOR) ) {
            useImage = kualiConfigurationService.getIndicatorParameter(KFSConstants.PURAP_NAMESPACE,  KFSConstants.Components.DOCUMENT, PurapConstants.PDF_IMAGES_AVAILABLE_INDICATOR);
        }
        //We'll get the imageTempLocation and the actual images only if the useImage is true. If useImage is false, we'll leave the images as blank space
        if (useImage) {
            imageTempLocation = kualiConfigurationService.getParameterValue(KFSConstants.PURAP_NAMESPACE, KFSConstants.Components.DOCUMENT, PurapConstants.IMAGE_TEMP_PATH);
        if ( imageTempLocation == null ) {
            throw new PurapConfigurationException("IMAGE_TEMP_PATH is missing");      
        }

        // Get images
            if ( (logoImage = imageDao.getLogo(key, campusCode, imageTempLocation)) == null ) {
            throw new PurapConfigurationException("logoImage is null.");
        }
        if ((directorSignatureImage = imageDao.getPurchasingDirectorImage(key, campusCode, imageTempLocation)) == null ) {
            throw new PurapConfigurationException("directorSignatureImage is null.");
        }
        if ( (contractManagerSignatureImage = imageDao.getContractManagerImage(key, po.getContractManagerCode(), imageTempLocation)) == null ) {
            throw new PurapConfigurationException("contractManagerSignatureImage is null.");
        }
        }

        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.CAMPUS_CODE, po.getDeliveryCampusCode());
        CampusParameter campusParameter = (CampusParameter)((List) businessObjectService.findMatching(CampusParameter.class, criteria)).get(0);

        String statusInquiryUrl = kualiConfigurationService.getParameterValue(KFSConstants.PURAP_NAMESPACE, KFSConstants.Components.DOCUMENT, PurapConstants.STATUS_INQUIRY_URL);
        if ( statusInquiryUrl == null ) {
            LOG.debug("generatePurchaseOrderPdf() ended");
            throw new PurapConfigurationException("Application Setting INVOICE_STATUS_INQUIRY_URL is missing.");
        }
         
        StringBuffer contractLanguage = new StringBuffer();
        criteria.put(KFSPropertyConstants.ACTIVE, true);
        List<PurchaseOrderContractLanguage> contractLanguageList = (List<PurchaseOrderContractLanguage>) (businessObjectService.findMatching(PurchaseOrderContractLanguage.class, criteria));
        if (!contractLanguageList.isEmpty()) {
            int lineNumber = 1;
            for (PurchaseOrderContractLanguage row: contractLanguageList) {
                if (row.getCampusCode().equals(po.getDeliveryCampusCode())) {
                    contractLanguage.append(lineNumber+" "+ row.getPurchaseOrderContractLanguageDescription()+"\n");
                    ++lineNumber;
                }
            }
        }
        
        String pdfFileLocation = kualiConfigurationService.getParameterValue(KFSConstants.PURAP_NAMESPACE, KFSConstants.Components.DOCUMENT, PurapConstants.PDF_DIRECTORY);
        if (pdfFileLocation == null) {
            LOG.debug("savePurchaseOrderPdf() ended");
            throw new PurapConfigurationException("Application Setting PDF_DIRECTORY is missing.");
        }
        
        String pdfFileName = "PURAP_PO_"+po.getPurapDocumentIdentifier().toString()+"_" + System.currentTimeMillis() + ".pdf";

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
     * Create the Purchase Order Pdf document and send it back
     * to the Action so that it can be dealt with.
     * 
     * @param po                         PurchaseOrderDocument that holds the Quote
     * @param byteArrayOutputStream      ByteArrayOutputStream that the action is using
     * @return Collection of ServiceError objects
     */
    private Collection generatePurchaseOrderPdf(PurchaseOrderDocument po, ByteArrayOutputStream byteArrayOutputStream, 
        boolean isRetransmit, String environment, List<PurchaseOrderItem> retransmitItems) {
        LOG.debug("generatePurchaseOrderPdf() started");

        PurchaseOrderPdf poPdf = new PurchaseOrderPdf();
        Collection errors = new ArrayList();
        try {
            PurchaseOrderPdfParameters pdfParameters = getPurchaseOrderPdfParameters(po);
            poPdf.generatePdf(po, pdfParameters, byteArrayOutputStream, isRetransmit, environment, retransmitItems);
            if (pdfParameters.isUseImage()) {
            imageDao.removeImages(po.getPurapDocumentIdentifier().toString(), pdfParameters.getImageTempLocation()); // Removes the temporary images; only need to call once.
            }
        } catch (PurError e) {
            LOG.error("Caught exception ", e);
            errors.add(e.getMessage());
        } catch (PurapConfigurationException pce) {
            LOG.error("Caught exception ", pce);
            errors.add(pce.getMessage());
        }

        LOG.debug("generatePurchaseOrderPdf() ended");
        return errors;
    }
    
    /**
     * @see org.kuali.module.purap.service.PrintService#generatePurchaseOrderPdf(org.kuali.module.purap.document.PurchaseOrderDocument, java.io.ByteArrayOutputStream, java.lang.String)
     */
    public Collection generatePurchaseOrderPdf(PurchaseOrderDocument po, ByteArrayOutputStream byteArrayOutputStream, String environment, List<PurchaseOrderItem> retransmitItems) {
        return generatePurchaseOrderPdf(po, byteArrayOutputStream, TRANSMISSION_IS_NOT_RETRANSMIT, environment, retransmitItems);
    }

    /**
     * @see org.kuali.module.purap.service.PrintService#generatePurchaseOrderPdfForRetransmission(org.kuali.module.purap.document.PurchaseOrderDocument, java.io.ByteArrayOutputStream, java.lang.String)
     */
    public Collection generatePurchaseOrderPdfForRetransmission(PurchaseOrderDocument po, ByteArrayOutputStream byteArrayOutputStream, String environment, List<PurchaseOrderItem> retransmitItems) {
        return generatePurchaseOrderPdf(po, byteArrayOutputStream, TRANSMISSION_IS_RETRANSMIT, environment, retransmitItems);
    }

    /**
     * Create the Purchase Order Pdf document and save it
     * so that it can be faxed in a later process.
     * 
     * @param po        PurchaseOrderDocument that holds the Quote
     * @return Collection of ServiceError objects
     */
    private Collection savePurchaseOrderPdf(PurchaseOrderDocument po, boolean isRetransmit, String environment) {
        LOG.debug("savePurchaseOrderPdf() started");

        PurchaseOrderPdf poPdf = new PurchaseOrderPdf();
        Collection errors = new ArrayList();

        PurchaseOrderPdfParameters pdfParameters = null;
        
        try {
            pdfParameters = getPurchaseOrderPdfParameters(po);
            poPdf.savePdf(po, pdfParameters, isRetransmit, environment);
            if (pdfParameters.isUseImage()) {
            imageDao.removeImages(po.getPurapDocumentIdentifier().toString(), pdfParameters.getImageTempLocation()); // Removes the temporary images; only need to call once.
            }
        } catch (PurError e) {
            LOG.error("Caught exception ", e);
            errors.add(e.getMessage());
        } catch (PurapConfigurationException pce) {
            LOG.error("Caught exception ", pce);
            errors.add(pce.getMessage());
        } finally {
            try {
                poPdf.deletePdf(pdfParameters.getPdfFileLocation(), pdfParameters.getPdfFileName());
            } catch (Throwable e) {
                LOG.error("savePurchaseOrderPdf() Error deleting PDF" + pdfParameters.getPdfFileName() + " - Exception was " + e.getMessage(), e);
                errors.add("Error while deleting the pdf after savePurchaseOrderPdf" + e.getMessage());
            }
        }


        LOG.debug("savePurchaseOrderPdf() ended");
        return errors;
    }

    /**
     * @see org.kuali.module.purap.service.PrintService#savePurchaseOrderPdf(org.kuali.module.purap.document.PurchaseOrderDocument, java.lang.String)
     */
    public Collection savePurchaseOrderPdf(PurchaseOrderDocument po, String environment) {
        return savePurchaseOrderPdf(po, TRANSMISSION_IS_NOT_RETRANSMIT, environment);
    }
    
    /**
     * @see org.kuali.module.purap.service.PrintService#savePurchaseOrderPdfForRetransmission(org.kuali.module.purap.document.PurchaseOrderDocument, java.lang.String)
     */
    public Collection savePurchaseOrderPdfForRetransmission(PurchaseOrderDocument po, String environment) {
        return savePurchaseOrderPdf(po, TRANSMISSION_IS_RETRANSMIT, environment);
    }

}
