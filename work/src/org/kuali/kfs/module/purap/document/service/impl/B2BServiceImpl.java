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
package org.kuali.kfs.module.purap.document.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.gl.businessobject.UniversityDate;
import org.kuali.kfs.module.ar.document.service.CustomerAddressService;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.businessobject.B2BInformation;
import org.kuali.kfs.module.purap.businessobject.B2BShoppingCartItem;
import org.kuali.kfs.module.purap.businessobject.BillingAddress;
import org.kuali.kfs.module.purap.businessobject.ItemType;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderTransmissionMethod;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionSource;
import org.kuali.kfs.module.purap.businessobject.RequisitionStatus;
import org.kuali.kfs.module.purap.dataaccess.B2BDao;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.B2BService;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.module.purap.exception.B2BRemoteError;
import org.kuali.kfs.module.purap.exception.CxmlParseError;
import org.kuali.kfs.module.purap.exception.MissingContractIdError;
import org.kuali.kfs.module.purap.exception.ServiceError;
import org.kuali.kfs.module.purap.util.cxml.B2BShoppingCartParser;
import org.kuali.kfs.module.purap.util.cxml.PunchOutSetupCxml;
import org.kuali.kfs.module.purap.util.cxml.PunchOutSetupResponse;
import org.kuali.kfs.module.purap.util.cxml.PurchaseOrderCxml;
import org.kuali.kfs.module.purap.util.cxml.PurchaseOrderResponse;
import org.kuali.kfs.pdp.service.ReferenceService;
import org.kuali.kfs.sys.businessobject.FinancialSystemUser;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.businessobject.ContractManager;
import org.kuali.kfs.vnd.businessobject.PurchaseOrderCostSource;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorContract;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.businessobject.VendorPhoneNumber;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.exception.UserNotFoundException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.PersistenceService;
import org.kuali.rice.kns.service.UniversalUserService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.springframework.beans.factory.BeanFactory;


public class B2BServiceImpl implements B2BService {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(B2BServiceImpl.class);
  private static org.apache.log4j.Logger SERVICELOG = org.apache.log4j.Logger.getLogger("epic.service." + B2BServiceImpl.class.getName());
  
  private B2BDao b2bDao;
  private BeanFactory beanFactory;
  private ReferenceService referenceService;
  private RequisitionService requisitionService;
  private ParameterService parameterService;
  private VendorService vendorService;
  private UniversalUserService universalUserService;
  private ChartService chartService;
  private CustomerAddressService billingAddressService;
  
  public void setChartService(ChartService c) {
    chartService = c;
  }
  public void setBillingAddressService(CustomerAddressService billingAddressService) {
    this.billingAddressService = billingAddressService;
  }
  public void setReferenceService(ReferenceService r) {
    referenceService = r;
  }
  public void setUniversalUserService(UniversalUserService u) {
    universalUserService = u;
  }
  public void setVendorService(VendorService vendorService) {
    this.vendorService = vendorService;
  }
  public void setRequisitionService(RequisitionService r) {
    requisitionService = r;
  }
  public void setBeanFactory(BeanFactory bf) {
    beanFactory = bf;
  }
  public void setParameterService(ParameterService parameterService) {
      this.parameterService = parameterService;
  }
  public void setB2bDao(B2BDao sqd) {
    b2bDao = sqd;
  }

  public B2BServiceImpl() {
    super();
  }

  private B2BInformation getB2bShoppingConfigurationInformation() {
    LOG.debug("getB2bShoppingConfigurationInformation() started");
    SERVICELOG.debug("getB2bShoppingConfigurationInformation() started");
    B2BInformation b2b = new B2BInformation();    
    
    LOG.debug("getB2bShoppingConfigurationInformation() url = " + parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.B2BParameters.PUNCHOUT_URL));
    
    //application settings
    b2b.setPunchoutURL(parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.B2BParameters.PUNCHOUT_URL));
    b2b.setPunchbackURL(parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.B2BParameters.PUNCHBACK_URL));
    b2b.setEnvironment(parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.B2BParameters.ENVIRONMENT));
    b2b.setUserAgent(parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.B2BParameters.USER_AGENT));
    b2b.setPassword(parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.B2BParameters.PASSWORD));
    SERVICELOG.debug("getB2bShoppingConfigurationInformation() ended");
    return b2b;
  }

  /**
   * Get URL to punch out to by sending a request xml and reading their response
   * 
   * @param User ID punching out
   * @return URL to punch out to
   */
  public String getPunchOutUrl(UniversalUser user) {
    LOG.debug("getPunchOutUrl() started");
    SERVICELOG.debug("getPunchOutUrl() started");
    B2BInformation b2b = getB2bShoppingConfigurationInformation();

    PunchOutSetupCxml cxml = new PunchOutSetupCxml(user,b2b);

    String response = b2bDao.sendPunchOutRequest(cxml.getPunchOutSetupRequestMessage(),b2b.getPunchoutURL());

    PunchOutSetupResponse posr = new PunchOutSetupResponse(response);
    SERVICELOG.debug("getPunchOutUrl() ended");
    return posr.getPunchOutUrl();
  }

  /**
   * Create requisition(s) from cxml and return
   * list for display
   * 
   * @param cxml cXml string from sciquest
   * @param user User doing the requisitioning
   * @return List of requisitions
   */
  public List createRequisitionsFromCxml(B2BShoppingCartParser message, UniversalUser user) {
    LOG.debug("createRequisitionsFromCxml() started");
    SERVICELOG.debug("createRequisitionsFromCxml() started");
    //for returning requisitions
    ArrayList requisitions = new ArrayList();

    //get items
    List items = message.getItems();

    //get vendor(s)
    List vendors = getAllVendors(items);

    //TODO: setup retrieval of reference codes with new getAll interface
    RequisitionSource b2bSource = null;//(RequisitionSource) referenceService.getCode("RequisitionSource", PurapConstants.RequisitionSources.B2B);
    RequisitionStatus reqStatus = null;//(RequisitionStatus) referenceService.getCode("RequisitionStatus", PurapConstants.RequisitionStatuses.IN_PROCESS);
    PurchaseOrderTransmissionMethod electricTransmissionMethod = null;//(PurchaseOrderTransmissionMethod) referenceService.getCode("PurchaseOrderTransmissionMethod",PurapConstants.POTransmissionMethods.ELECTRONIC);

    UniversityDate ud = SpringContext.getBean(UniversityDateService.class).getCurrentUniversityDate();

    //create requisition(s) one per vendor
    for (Iterator iter = vendors.iterator(); iter.hasNext();) {
      VendorDetail vendor = (VendorDetail)iter.next();

      // create requisition
      RequisitionDocument req = new RequisitionDocument();

      //set contract
      //TODO: Note sure how to get the B2B vendor contract
      VendorContract contract = null; //vendorService.getVendorB2BContract(vendor, user);
      if (contract != null) {
        req.setVendorContract(contract);
        if (contract.getPurchaseOrderCostSourceCode() != null) {
          req.setPurchaseOrderCostSourceCode(contract.getPurchaseOrderCostSourceCode());
        } else {
          //per jira kulapp 1614, if cost source is null, we should set it by default to "Estimate" 
          //TODO: setup retrieval of reference codes with new getAll interface
          PurchaseOrderCostSource defaultCostSource = null; //(PurchaseOrderCostSource)referenceService.getCode("PurchaseOrderCostSource", PurapConstants.POCostSources.ESTIMATE);
          req.setPurchaseOrderCostSourceCode(defaultCostSource.getPurchaseOrderCostSourceCode());
        }
      } else {
        throw new MissingContractIdError("Contract ID is missing for vendor " + vendor.getVendorName());
      }

      String dunsNumber = vendor.getVendorDunsNumber();

      //get items for this vendor
      List itemsForVendor = getAllVendorItems(items, dunsNumber, user.getPersonUserIdentifier());
      String supplierId = getExternalSupplierId(items, dunsNumber);
   
      FinancialSystemUser fUser = SpringContext.getBean(FinancialSystemUserService.class).convertUniversalUserToFinancialSystemUser(user);
      
      // We are storing this before it is finished.  We'll just get the chart/org
      // from the user for now.      
      req.setChartOfAccountsCode(fUser.getChartOfAccountsCode());
      req.setOrganizationCode(fUser.getOrganizationCode());

      //set things that need to be set
      //TODO: Note sure which field this is
      //req.setPurchaseOrderEncumbranceFiscalYear(ud.getUniversityFiscalYear());
      req.setVendorHeaderGeneratedIdentifier(vendor.getVendorHeaderGeneratedIdentifier());
      req.setVendorDetailAssignedIdentifier(vendor.getVendorDetailAssignedIdentifier());
      req.setVendorName(vendor.getVendorName());
      req.setVendorRestrictedIndicator(vendor.getVendorRestrictedIndicator());
      req.setItems(itemsForVendor);      
      //TODO: Find this field
      //req.setSource(b2bSource);
      req.setStatus(reqStatus);
      req.setPurchaseOrderTransmissionMethodCode(electricTransmissionMethod.getPurchaseOrderTransmissionMethodCode());
      //TODO: Can't find this field
      //req.setExternalOrganizationB2BSupplierId(supplierId);

      //set address      
      String userCampus = GlobalVariables.getUserSession().getFinancialSystemUser().getCampusCode();
      VendorAddress vendorAddress = SpringContext.getBean(VendorService.class).getVendorDefaultAddress(vendor.getVendorHeaderGeneratedIdentifier(), vendor.getVendorDetailAssignedIdentifier(), VendorConstants.AddressTypes.PURCHASE_ORDER, userCampus);
      if (vendorAddress != null) {
          req.setVendorLine1Address(vendorAddress.getVendorLine1Address());
          req.setVendorLine2Address(vendorAddress.getVendorLine2Address());        
          req.setVendorCityName(vendorAddress.getVendorCityName());
          req.setVendorStateCode(vendorAddress.getVendorStateCode());
          req.setVendorPostalCode(vendorAddress.getVendorZipCode());
          req.setVendorCountryCode(vendorAddress.getVendorCountryCode());          
          req.setVendorAddressGeneratedIdentifier(vendorAddress.getVendorAddressGeneratedIdentifier());
      }
      else {
          req.setVendorLine1Address(vendor.getDefaultAddressLine1());
          req.setVendorLine2Address(vendor.getDefaultAddressLine2());        
          req.setVendorCityName(vendor.getDefaultAddressCity());
          req.setVendorStateCode(vendor.getDefaultAddressStateCode());
          req.setVendorPostalCode(vendor.getDefaultAddressPostalCode());
          req.setVendorCountryCode(vendor.getDefaultAddressCountryCode());
          req.setVendorAddressGeneratedIdentifier(vendor.getVendorHeaderGeneratedIdentifier());
      }
               
      //set fax number
      if (vendorAddress.getVendorFaxNumber() != null) {
        req.setVendorFaxNumber(vendorAddress.getVendorFaxNumber());
      }
      

      //set delivery campus - billing code
      req.setDeliveryCampusCode(user.getCampusCode());

      BillingAddress billingAddress = new BillingAddress();
      billingAddress.setBillingCampusCode(req.getDeliveryCampusCode());
      Map keys = SpringContext.getBean(PersistenceService.class).getPrimaryKeyFieldValues(billingAddress);
      billingAddress = (BillingAddress) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BillingAddress.class, keys);
      
      if (billingAddress != null) {
        req.setBillingName(billingAddress.getBillingName());
        req.setBillingLine1Address(billingAddress.getBillingLine1Address());
        req.setBillingLine2Address(billingAddress.getBillingLine2Address());
        req.setBillingCityName(billingAddress.getBillingCityName());
        req.setBillingStateCode(billingAddress.getBillingStateCode());
        req.setBillingPostalCode(billingAddress.getBillingPostalCode());
        req.setBillingCountryCode(billingAddress.getBillingCountryCode());
        req.setBillingPhoneNumber(billingAddress.getBillingPhoneNumber());
      }
      
      //TODO: What is the default phone number?
      VendorPhoneNumber phone = null; //vendorService.getVendorPhoneNumber(vendor);
      if (phone != null) {
        req.setVendorPhoneNumber(phone.getVendorPhoneNumber());
      }

      // Call Routing to get the Document header
      //TODO: Do we need to setup the document header like this anymore?
      //String docName = parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, "EDEN_DOCUMENT_REQ");
      //DocumentHeader dh = routingService.createDocumentHeader(docName,null,req.getTotalCost(),user);
      //req.setDocumentHeader(dh);

      //save requisition to database
      requisitionService.saveDocumentWithoutValidation(req);

      //add requisition to List
      requisitions.add(req);
    }
    SERVICELOG.debug("createRequisitionsFromCxml() ended");
    return requisitions;
  }

  /**
   * Send purchase order to SciQuest 
   */
  public Collection sendPurchaseOrder(PurchaseOrderDocument purchaseOrder, UniversalUser user) {
    LOG.debug("sendPurchaseOrder(PurchaseOrder purchaseOrder, UniversalUser user) started.");
    SERVICELOG.debug("sendPurchaseOrder() started");
    // ***** Get what we need to create the cXML.    
    PurchaseOrderCxml poCxml = new PurchaseOrderCxml(purchaseOrder);
    
    // ***** Important design note:
    //  We need the contract manager's name, phone number, and e-mail address.
    //  B2B orders that don’t qualify to become APO's will have contract
    //  managers on the PO, and the ones that DO become APO's will not.
    //  We decided to always get the contract manager from the B2B contract associated 
    //  with the order, and for B2B orders to ignore the contract manager field on the PO.
    //  We pull the name and phone number from the contract manager table and get the
    //  e-mail address from the EDS.
    ContractManager contractManager = purchaseOrder.getVendorContract().getContractManager();
    String contractManagerEmail = this.getContractManagerEmail(contractManager);

    String vendorDuns = purchaseOrder.getVendorDetail().getVendorDunsNumber();

    RequisitionDocument r = requisitionService.getRequisitionById(purchaseOrder.getRequisitionIdentifier());
    KualiWorkflowDocument reqWorkflowDoc = r.getDocumentHeader().getWorkflowDocument();
    UniversalUser initiator = null;
    try{
        universalUserService.getUniversalUser(reqWorkflowDoc.getInitiatorNetworkId());
    } catch (UserNotFoundException e) {
        LOG.error("getContractManagerEmail(): caught UserNotFoundException, returning null.");
        SERVICELOG.debug("getContractManagerEmail() ended");
        return null;
    }

    String password = parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.B2BParameters.PO_PASSWORD);
    String punchoutUrl = parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.B2BParameters.PO_URL);
    LOG.debug("sendPurchaseOrder(): punchoutUrl is " + punchoutUrl);

    Collection errors;

    errors = poCxml.verifyCxmlPOData(initiator, password, 
        contractManager, contractManagerEmail, vendorDuns);
    if (!errors.isEmpty()) {
      SERVICELOG.debug("sendPurchaseOrder() ended");
      return errors;
    } else {
      errors = new ArrayList();
    }

    try {
      
      // check for vendor specific code
      //TODO: See about setting this zero items variable another way
        
      boolean includeZeroItems = true;
/*      String beanId = "purVendorSpecific" + vendorDuns;
      LOG.debug("sendPurchaseOrder() beanId = " + beanId);
      if ( beanFactory.containsBean(beanId) ) {
        VendorSpecificService vendorService = (VendorSpecificService)beanFactory.getBean(beanId);
        LOG.debug("getAllVendorItems() Using specific service for " + vendorService.getVendorName());
        includeZeroItems = vendorService.includeZeroItems();
      } else {
        LOG.debug("getAllVendorItems() No vendor specific service");
      }*/

      LOG.debug("sendPurchaseOrder() Generating cxml");
      String cxml = poCxml.getCxml(initiator, password, 
          contractManager, contractManagerEmail, vendorDuns, includeZeroItems);

      LOG.debug("sendPurchaseOrder() Sending cxml");
      String responseCxml = b2bDao.sendPunchOutRequest(cxml, punchoutUrl);

      LOG.info("sendPurchaseOrder(): Response cXML for po number " + purchaseOrder.getPurapDocumentIdentifier() + ":" + responseCxml);

      PurchaseOrderResponse poResponse = new PurchaseOrderResponse(responseCxml);
      String statusText = poResponse.getStatusText();
      LOG.debug("sendPurchaseOrder(): statusText is " + statusText);
      if ( (statusText == null) || ( ! "success".equals(statusText.trim().toLowerCase()) ) ) {
        LOG.error("sendPurchaseOrder(): PO cXML for po number " + purchaseOrder.getPurapDocumentIdentifier() + " failed sending to SciQuest: " + statusText);
        ServiceError se = new ServiceError("cxml.response.error", statusText);
        errors.add(se);
        // Find error messages other than the status.
        List errorMessages = poResponse.getPOResponseErrorMessages();
        if ( errorMessages == null || errorMessages.isEmpty()) {
          // Not all of the cXML responses have error messages other than the status text error.
          LOG.debug("sendPurchaseOrder() Unable to find errors in response other than status, but not all responses have other errors.");
        } else {
            for (Iterator iter = errorMessages.iterator(); iter.hasNext();) {
              String errorMessage = (String) iter.next();
              
              if (errorMessage == null) {
                LOG.error("sendPurchaseOrder(): errorMessage not found.");
                SERVICELOG.debug("sendPurchaseOrder() ended");
                return null;
              }
              LOG.error("sendPurchaseOrder(): SciQuest error message for po number " + purchaseOrder.getPurapDocumentIdentifier() + ": " + errorMessage);
              se = new ServiceError("cxml.response.error", errorMessage);
              errors.add(se);
            }
        }
      }
    } catch (B2BRemoteError sqre) {
      LOG.error("sendPurchaseOrder() Error sendng", sqre);
      errors.add(new ServiceError("cxml.response.error","Unable to talk to SciQuest"));
    } catch (CxmlParseError e) {
      LOG.error("sendPurchaseOrder() Error Parsing", e);
      errors.add(new ServiceError("cxml.response.error","Unable to read response"));
    } catch (Throwable e) {
      LOG.error("sendPurchaseOrder() Unknown Error", e);
      errors.add(new ServiceError("cxml.response.error","Unknown exception: " + e.getMessage()));      
    }
    SERVICELOG.debug("sendPurchaseOrder() ended");
    return errors;
  }

  /**
   * Get all the vendors in a single shopping cart
   *
   * @param items Items in the shopping cart
   * @return List of VendorDetails for each vendor in the shopping cart
   */
  private List getAllVendors(List items) {
    LOG.debug("getAllVendors() started");

    Set vendorDuns = new HashSet();
    for (Iterator iter = items.iterator(); iter.hasNext();) {
      B2BShoppingCartItem item = (B2BShoppingCartItem)iter.next();

      vendorDuns.add(item.getSupplier("DUNS"));
    }

    ArrayList vendors = new ArrayList();
    for (Iterator iter = vendorDuns.iterator(); iter.hasNext();) {
      String duns = (String)iter.next();      
      //TODO: get vendor by duns number
      VendorDetail vd = null; //vendorService.getVendorByDunsNumber(duns);
      if ( vd == null ) {
        LOG.error("getAllVendors() Invalid DUNS number from shopping cart: " + duns);
        throw new IllegalArgumentException("Invalid DUNS number from shopping cart: " + duns);
      }
      vendors.add(vd);
    }
    SERVICELOG.debug("getAllVendors() ended");
    return vendors;
  }

  /**
   * Get all the items for a specific vendor
   *
   * @param items List of all items
   * @param vendorDuns Vendor DUNS
   * @param updateUserId last update user id
   * @return list of RequisitionItems for a specific DUNS
   */
  private List getAllVendorItems(List items, String vendorDuns,String updateUserId) {
    LOG.debug("getAllVendorItems() started");
    SERVICELOG.debug("getAllVendorItems() started");
    // First get all the ShoppingCartItems for this vendor in a list
    List scItems = new ArrayList();
    for (Iterator iter = items.iterator(); iter.hasNext();) {
      B2BShoppingCartItem item = (B2BShoppingCartItem)iter.next();

      if ( vendorDuns.equals(item.getSupplier("DUNS")) ) {
        scItems.add(item);
      }
      
    }

    //TODO: setup retrieval of reference codes with new getAll interface
    ItemType itemType = null; //(ItemType) referenceService.getCode("ItemType", PurapConstants.ItemTypeCodes.ITEM_TYPE_ITEM_CODE);

    // Handle vendor specific code
    //TODO: seems like this just sorts the list, may need to incorporate this.
/*    String beanId = "purVendorSpecific" + vendorDuns;
    if ( beanFactory.containsBean(beanId) ) {
      VendorSpecificService vendorService = (VendorSpecificService)beanFactory.getBean(beanId);

      LOG.debug("getAllVendorItems() Using specific service for " + vendorService.getVendorName());
      scItems = vendorService.sortList(scItems);
    } else {
      LOG.debug("getAllVendorItems() No vendor specific service");
    }*/

    // Now convert them to Requisition items
    int itemLine = 1;
    List vendorItems = new ArrayList();
    for (Iterator iter = scItems.iterator(); iter.hasNext();) {
      B2BShoppingCartItem item = (B2BShoppingCartItem)iter.next();
      RequisitionItem reqItem = createRequisitionItem(item, new Integer(itemLine));
      reqItem.setItemTypeCode(itemType.getItemTypeCode());
      reqItem.setItemRestrictedIndicator(false);      
      itemLine = itemLine + 1;

      vendorItems.add(reqItem);
    }
    SERVICELOG.debug("getAllVendorItems() ended");
    return vendorItems;
  }


  //These are helper classes for extracting information from the cxml message
  private RequisitionItem createRequisitionItem(B2BShoppingCartItem item, Integer itemLine) {
    SERVICELOG.debug("createRequisitionItem() started");
    //TODO: This wants to create an item with a line number already
    //RequisitionItem reqItem = new RequisitionItem(itemLine);
    RequisitionItem reqItem = new RequisitionItem();
    reqItem.setItemUnitPrice(new BigDecimal(item.getUnitPrice()));
    reqItem.setItemQuantity(new KualiDecimal(item.getQuantity()));
    reqItem.setItemCatalogNumber(item.getSupplierPartId());
    reqItem.setItemAuxiliaryPartIdentifier(item.getSupplierPartAuxiliaryId());
    reqItem.setItemDescription(item.getDescription());
    reqItem.setItemUnitOfMeasureCode(item.getUnitOfMeasure());
    //reqItem.setRequisitionLineId(item.getExtrinsic("RequisitionLineID"));
    reqItem.setExternalOrganizationB2bProductTypeName(item.getClassification("Product Source"));
    reqItem.setExternalOrganizationB2bProductReferenceNumber(item.getExtrinsic("SystemProductID"));
    reqItem.setItemTypeCode("TEST");

    //nulls
    //reqItem.setItemCapitalAssetNoteText(null);
    //TODO: This doesn't seem to exist
    //reqItem.setItemCapitalAssetNumbers(null);
    //reqItem.setCapitalAssetTransactionTypeCode(null);
    reqItem.setItemTypeCode(null);
    SERVICELOG.debug("createRequisitionItem() ended");
    //return
    return reqItem;
  }
  
  /**
   * This method looks up the Contract Manager's email from the EDS.
  */
  public String getContractManagerEmail(ContractManager cm) {
    LOG.debug("getContractManagerEmail(): entered method.");
    SERVICELOG.debug("getContractManagerEmail() started");
    try {
      UniversalUser contractManager = universalUserService.getUniversalUser(cm.getContractManagerUserIdentifier());
      
      String contractManagerEmail = contractManager.getPersonEmailAddress();
      if (contractManagerEmail == null) {
        SERVICELOG.debug("getContractManagerEmail() ended");
        return null;
      }
      SERVICELOG.debug("getContractManagerEmail() ended");
      return contractManagerEmail;
    } catch (UserNotFoundException e) {
      LOG.error("getContractManagerEmail(): caught UserNotFoundException, returning null.");
      SERVICELOG.debug("getContractManagerEmail() ended");
      return null;
    }
  }
  
  private String getExternalSupplierId(List items, String vendorDuns){
      LOG.debug("getExternalSupplierId() ");
      SERVICELOG.debug("getExternalSupplierId() started");
      String id = null;
      List scItems = new ArrayList();
      for (Iterator iter = items.iterator(); iter.hasNext();) {
        B2BShoppingCartItem item = (B2BShoppingCartItem)iter.next();

        if ( vendorDuns.equals(item.getSupplier("DUNS")) ) {
          id = item.getSupplier("SystemSupplierID");
        }
        
      }
      SERVICELOG.debug("getExternalSupplierId() ended");
      return id;
  }

}
