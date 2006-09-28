/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.purap.document;

import java.util.LinkedHashMap;

import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.document.DocumentHeader;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.bo.RequisitionStatus;
import org.kuali.module.purap.bo.RequisitionStatusHistory;

/**
 * @author PURAP (kualidev@oncourse.iu.edu)
 */
public class RequisitionDocument extends PurchasingDocumentBase {

	private Integer requisitionIdentifier;
	private String requisitionStatusCode;
	private String requisitionOrganizationReference1Text;
	private String requisitionOrganizationReference2Text;
	private String requisitionOrganizationReference3Text;
	private String alternate1VendorName;
	private String alternate2VendorName;
	private String alternate3VendorName;
	private String alternate4VendorName;
	private String alternate5VendorName;
	private KualiDecimal organizationAutomaticPurchaseOrderLimit;

    private RequisitionStatusHistory requisition;
	private RequisitionStatus requisitionStatus;

	/**
	 * Default constructor.
	 */
	public RequisitionDocument() {

	}

    /**
     * Perform logic needed to initiate Requisition Document
     */
    public void initiateDocument() {
        //TODO add code
        KualiUser currentUser = GlobalVariables.getUserSession().getKualiUser();

//        r.setSource((RequisitionSource) referenceService.getCode("RequisitionSource", EpicConstants.REQ_SOURCE_STANDARD_ORDER));
//        r.setStatus((RequisitionStatus) referenceService.getCode("RequisitionStatus", EpicConstants.REQ_STAT_IN_PROCESS));
//

// TODO get fiscal year from dateservice        
//        UniversityDate ud = coaService.getFiscalPeriodForToday();
//        r.setPurchaseOrderEncumbranceFiscalYear(ud.getUniversityFiscalYear());
//        r.setFinancialChartOfAccountsCode(u.getOrganization().getChart().getCode());
//        r.setOrganizationCode(u.getOrganization().getCode());
//        r.setDeliveryCampus(vendorService.getCampusByCode(u.getCampusCd()));

        // TODO wait to code this until we have the new table created
        // get the APO limit and the alternate reference titles (if set)
//        updateOrganizationAndAPOLimit(r);// this must be done after the chart/org has been set on the req (do not move this line)

//        BillingAddress billingAddress = billingAddressService.getByCampusCode(u.getCampusCd());
//        if (billingAddress != null) {
//            r.setBillingName(billingAddress.getName());
//            r.setBillingLine1Address(billingAddress.getLine1Address());
//            r.setBillingLine2Address(billingAddress.getLine2Address());
//            r.setBillingCityName(billingAddress.getCityName());
//            r.setBillingStateCode(billingAddress.getStateCode());
//            r.setBillingPostalCode(billingAddress.getPostalCode());
//            r.setBillingCountryCode(billingAddress.getCountryCode());
//            r.setBillingPhoneNumber(billingAddress.getPhoneNumber());
//        }

        // default cost source to estimate and transmission method to fax
//        r.setPurchaseOrderCostSourceCode(EpicConstants.PO_COST_SRC_ESTIMATE);
//        r.setPurchaseOrderTransmissionMethodCode(EpicConstants.PO_TRANSMISSION_METHOD_FAX);
//        this.updateDropDownObjects(r);

// TODO  WAIT ON ITEM LOGIC  (CHRIS AND DAVID SHOULD FIX THIS HERE)
//        // add new item for freight
//        addNewBelowLineItem(r, EpicConstants.ITEM_TYPE_FREIGHT_CODE);
//
//        // add new item for s&h
//        addNewBelowLineItem(r, EpicConstants.ITEM_TYPE_SHIP_AND_HAND_CODE);
//
//        // add new item for full order discount
//        addNewBelowLineItem(r, EpicConstants.ITEM_TYPE_ORDER_DISCOUNT_CODE);
//
//        // add new item for trade in
//        addNewBelowLineItem(r, EpicConstants.ITEM_TYPE_TRADE_IN_CODE);

//        Integer numRows = applicationSettingService.getInt("DEFAULT_ITEM_ROWS");
//        for (int i = 0; i < numRows.intValue(); i++) {
//            Integer nextLineNum = r.getNextItemLineNumber();
//            ItemType iT = getItemType();
//            addNewItem(r, iT, nextLineNum);
//        }

    }

    /**
     * Perform logic needed to copy Requisition Document
     */
    public void copyDocument() {
        
//      //set req status to 'INPR'
//      newReq.setStatus((RequisitionStatus) referenceService.getCode("RequisitionStatus", EpicConstants.REQ_STAT_IN_PROCESS));
//
//      //set fields from the user
//      newReq.setFinancialChartOfAccountsCode(user.getOrganization().getChart().getCode());
//      newReq.setOrganizationCode(user.getOrganization().getCode());
//      newReq.setLastUpdateUser(user);
//      newReq.setLastUpdateTimestamp(new Timestamp((new java.util.Date()).getTime()));
//
//      if (req.getDeliveryCampus() != null) {
//        BillingAddress billingAddress = billingAddressService.getByCampusCode(req.getDeliveryCampus().getCampusCd());
//        if (billingAddress != null) {
//          newReq.setBillingName(billingAddress.getName());
//          newReq.setBillingLine1Address(billingAddress.getLine1Address());
//          newReq.setBillingLine2Address(billingAddress.getLine2Address());
//          newReq.setBillingCityName(billingAddress.getCityName());
//          newReq.setBillingStateCode(billingAddress.getStateCode());
//          newReq.setBillingPostalCode(billingAddress.getPostalCode());
//          newReq.setBillingCountryCode(billingAddress.getCountryCode());
//          newReq.setBillingPhoneNumber(billingAddress.getPhoneNumber());
//        }
//      }
//
//      UniversityDate ud = coaService.getFiscalPeriodForToday();
//      newReq.setPurchaseOrderEncumbranceFiscalYear(ud.getUniversityFiscalYear());
//
//      boolean activeVendor = true;
//      boolean activeContract = true;
//
//      Date today = new Date(java.lang.System.currentTimeMillis());
//      VendorContract vendorContract = vendorService.getVendorContractById(req.getVendorContractGeneratedId());
//      if (!(vendorContract != null && 
//          today.after(vendorContract.getBeginDate()) && 
//          today.before(vendorContract.getEndDate()))) {
//        activeContract = false;
//      }
//
//      VendorDetail vendorDetail = vendorService.getVendorDetail(req.getVendorHeaderGeneratedId(), 
//          req.getVendorDetailAssignedId());
//      if (!(vendorDetail != null && EpicConstants.VENDOR_STAT_TYPE_ACTIVE.equals(vendorDetail.getStatusCode()))) {
//        activeVendor = false;
//      }
//
//      //B2B - only copy if contract and vendor are both active (throw separate errors to print to screen)
//      if (EpicConstants.REQ_SOURCE_B2B.equals(req.getSource().getCode())) {
//        if (!activeContract) {
//          LOG.debug("copy() B2B contract has expired; don't allow copy.");
//          throw new PurError("Requisition # " + req.getId() + " uses an expired contract and cannot be copied.");
//        }
//        if (!activeVendor) {
//          LOG.debug("copy() B2B vendor is inactive; don't allow copy.");
//          throw new PurError("Requisition # " + req.getId() + " uses an inactive vendor and cannot be copied.");
//        }
//      }

//DO THIS OPPOSITE...IF INACTIVE, CLEAR OUT IDS
//      if (activeVendor) {
//        newReq.setVendorHeaderGeneratedId(req.getVendorHeaderGeneratedId());
//        newReq.setVendorDetailAssignedId(req.getVendorDetailAssignedId());
//        if (activeContract) {
//          newReq.setVendorContract(req.getVendorContract());
//        }
//      }

      //these fields should not be set in this method; force to be null
//      newReq.setVendorNoteText(null);
//      newReq.setContractManager(null);
//      newReq.setInstitutionContactName(null);
//      newReq.setInstitutionContactPhoneNumber(null);
//      newReq.setInstitutionContactEmailAddress(null);
//      newReq.setAutomaticPurchaseOrderLimit(null);
//      newReq.setIsAPO(null);
//      newReq.setStatusHistoryList(null);

//TODO DAVID AND CHRIS SHOULD FIX THIS
      //Trade In and Discount Items are only available for B2B. If the Requisition
      //doesn't currently contain trade in and discount, we should add them in 
      //here (KULAPP 1715)
//      if (! EpicConstants.REQ_SOURCE_B2B.equals(req.getSource().getCode())) {
//        boolean containsFullOrderDiscount = req.containsFullOrderDiscount();
//        //If the po has not contained full order discount item, create and
//        //add the full order discount item to it.
//        if (! containsFullOrderDiscount) {
//          ItemType iT = (ItemType)(referenceService.getCode("ItemType",EpicConstants.ITEM_TYPE_ORDER_DISCOUNT_CODE));
//          RequisitionItem discountItem = new RequisitionItem(iT, EpicConstants.ITEM_TYPE_ORDER_DISCOUNT_LINE_NUMBER, req);
//          newReq.getItems().add(discountItem);
//        }
//          
//        boolean containsTradeIn = req.containsTradeIn();
//        //If the po has not contained trade in item, create and
//        //add the trade in item to it.
//        if (! containsTradeIn) {
//          ItemType iT = (ItemType)(referenceService.getCode("ItemType",EpicConstants.ITEM_TYPE_TRADE_IN_CODE));
//          RequisitionItem tradeInItem = new RequisitionItem(iT, EpicConstants.ITEM_TYPE_TRADE_IN_LINE_NUMBER, req);
//          newReq.getItems().add(tradeInItem);
//        }
//      }

      // get the contacts, supplier diversity list and APO limit 
//      setupRequisition(newReq);
      
        
    }
    
    /**
     * Gets the requisitionIdentifier attribute.
     * 
     * @return - Returns the requisitionIdentifier
     * 
     */
	public Integer getRequisitionIdentifier() { 
		return requisitionIdentifier;
	}

	/**
	 * Sets the requisitionIdentifier attribute.
	 * 
	 * @param - requisitionIdentifier The requisitionIdentifier to set.
	 * 
	 */
	public void setRequisitionIdentifier(Integer requisitionIdentifier) {
		this.requisitionIdentifier = requisitionIdentifier;
	}


	/**
	 * Gets the requisitionStatusCode attribute.
	 * 
	 * @return - Returns the requisitionStatusCode
	 * 
	 */
	public String getRequisitionStatusCode() { 
		return requisitionStatusCode;
	}

	/**
	 * Sets the requisitionStatusCode attribute.
	 * 
	 * @param - requisitionStatusCode The requisitionStatusCode to set.
	 * 
	 */
	public void setRequisitionStatusCode(String requisitionStatusCode) {
		this.requisitionStatusCode = requisitionStatusCode;
	}



	/**
	 * Gets the requisitionOrganizationReference1Text attribute.
	 * 
	 * @return - Returns the requisitionOrganizationReference1Text
	 * 
	 */
	public String getRequisitionOrganizationReference1Text() { 
		return requisitionOrganizationReference1Text;
	}

	/**
	 * Sets the requisitionOrganizationReference1Text attribute.
	 * 
	 * @param - requisitionOrganizationReference1Text The requisitionOrganizationReference1Text to set.
	 * 
	 */
	public void setRequisitionOrganizationReference1Text(String requisitionOrganizationReference1Text) {
		this.requisitionOrganizationReference1Text = requisitionOrganizationReference1Text;
	}


	/**
	 * Gets the requisitionOrganizationReference2Text attribute.
	 * 
	 * @return - Returns the requisitionOrganizationReference2Text
	 * 
	 */
	public String getRequisitionOrganizationReference2Text() { 
		return requisitionOrganizationReference2Text;
	}

	/**
	 * Sets the requisitionOrganizationReference2Text attribute.
	 * 
	 * @param - requisitionOrganizationReference2Text The requisitionOrganizationReference2Text to set.
	 * 
	 */
	public void setRequisitionOrganizationReference2Text(String requisitionOrganizationReference2Text) {
		this.requisitionOrganizationReference2Text = requisitionOrganizationReference2Text;
	}


	/**
	 * Gets the requisitionOrganizationReference3Text attribute.
	 * 
	 * @return - Returns the requisitionOrganizationReference3Text
	 * 
	 */
	public String getRequisitionOrganizationReference3Text() { 
		return requisitionOrganizationReference3Text;
	}

	/**
	 * Sets the requisitionOrganizationReference3Text attribute.
	 * 
	 * @param - requisitionOrganizationReference3Text The requisitionOrganizationReference3Text to set.
	 * 
	 */
	public void setRequisitionOrganizationReference3Text(String requisitionOrganizationReference3Text) {
		this.requisitionOrganizationReference3Text = requisitionOrganizationReference3Text;
	}



	/**
	 * Gets the alternate1VendorName attribute.
	 * 
	 * @return - Returns the alternate1VendorName
	 * 
	 */
	public String getAlternate1VendorName() { 
		return alternate1VendorName;
	}

	/**
	 * Sets the alternate1VendorName attribute.
	 * 
	 * @param - alternate1VendorName The alternate1VendorName to set.
	 * 
	 */
	public void setAlternate1VendorName(String alternate1VendorName) {
		this.alternate1VendorName = alternate1VendorName;
	}


	/**
	 * Gets the alternate2VendorName attribute.
	 * 
	 * @return - Returns the alternate2VendorName
	 * 
	 */
	public String getAlternate2VendorName() { 
		return alternate2VendorName;
	}

	/**
	 * Sets the alternate2VendorName attribute.
	 * 
	 * @param - alternate2VendorName The alternate2VendorName to set.
	 * 
	 */
	public void setAlternate2VendorName(String alternate2VendorName) {
		this.alternate2VendorName = alternate2VendorName;
	}


	/**
	 * Gets the alternate3VendorName attribute.
	 * 
	 * @return - Returns the alternate3VendorName
	 * 
	 */
	public String getAlternate3VendorName() { 
		return alternate3VendorName;
	}

	/**
	 * Sets the alternate3VendorName attribute.
	 * 
	 * @param - alternate3VendorName The alternate3VendorName to set.
	 * 
	 */
	public void setAlternate3VendorName(String alternate3VendorName) {
		this.alternate3VendorName = alternate3VendorName;
	}


	/**
	 * Gets the alternate4VendorName attribute.
	 * 
	 * @return - Returns the alternate4VendorName
	 * 
	 */
	public String getAlternate4VendorName() { 
		return alternate4VendorName;
	}

	/**
	 * Sets the alternate4VendorName attribute.
	 * 
	 * @param - alternate4VendorName The alternate4VendorName to set.
	 * 
	 */
	public void setAlternate4VendorName(String alternate4VendorName) {
		this.alternate4VendorName = alternate4VendorName;
	}


	/**
	 * Gets the alternate5VendorName attribute.
	 * 
	 * @return - Returns the alternate5VendorName
	 * 
	 */
	public String getAlternate5VendorName() { 
		return alternate5VendorName;
	}

	/**
	 * Sets the alternate5VendorName attribute.
	 * 
	 * @param - alternate5VendorName The alternate5VendorName to set.
	 * 
	 */
	public void setAlternate5VendorName(String alternate5VendorName) {
		this.alternate5VendorName = alternate5VendorName;
	}


	/**
	 * Gets the organizationAutomaticPurchaseOrderLimit attribute.
	 * 
	 * @return - Returns the organizationAutomaticPurchaseOrderLimit
	 * 
	 */
	public KualiDecimal getOrganizationAutomaticPurchaseOrderLimit() { 
		return organizationAutomaticPurchaseOrderLimit;
	}

	/**
	 * Sets the organizationAutomaticPurchaseOrderLimit attribute.
	 * 
	 * @param - organizationAutomaticPurchaseOrderLimit The organizationAutomaticPurchaseOrderLimit to set.
	 * 
	 */
	public void setOrganizationAutomaticPurchaseOrderLimit(KualiDecimal organizationAutomaticPurchaseOrderLimit) {
		this.organizationAutomaticPurchaseOrderLimit = organizationAutomaticPurchaseOrderLimit;
	}



	/**
	 * Gets the requisition attribute.
	 * 
	 * @return - Returns the requisition
	 * 
	 */
	public RequisitionStatusHistory getRequisition() { 
		return requisition;
	}

	/**
	 * Sets the requisition attribute.
	 * 
	 * @param - requisition The requisition to set.
	 * @deprecated
	 */
	public void setRequisition(RequisitionStatusHistory requisition) {
		this.requisition = requisition;
	}

	/**
	 * Gets the documentHeader attribute.
	 * 
	 * @return - Returns the documentHeader
	 * 
	 */
	public DocumentHeader getDocumentHeader() { 
		return documentHeader;
	}

	/**
	 * Sets the documentHeader attribute.
	 * 
	 * @param - documentHeader The documentHeader to set.
	 * @deprecated
	 */
	public void setDocumentHeader(DocumentHeader documentHeader) {
		this.documentHeader = documentHeader;
	}


	/**
	 * Gets the requisitionStatus attribute.
	 * 
	 * @return - Returns the requisitionStatus
	 * 
	 */
	public RequisitionStatus getRequisitionStatus() { 
		return requisitionStatus;
	}

	/**
	 * Sets the requisitionStatus attribute.
	 * 
	 * @param - requisitionStatus The requisitionStatus to set.
	 * @deprecated
	 */
	public void setRequisitionStatus(RequisitionStatus requisitionStatus) {
		this.requisitionStatus = requisitionStatus;
	}

	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.requisitionIdentifier != null) {
            m.put("requisitionIdentifier", this.requisitionIdentifier.toString());
        }
	    return m;
    }
}
