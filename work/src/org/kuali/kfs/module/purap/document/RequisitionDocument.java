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

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.document.DocumentHeader;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.bo.BillingAddress;
import org.kuali.module.purap.bo.RequisitionStatus;
import org.kuali.module.purap.bo.RequisitionStatusHistory;
import org.kuali.module.purap.bo.VendorContract;
import org.kuali.module.purap.bo.VendorDetail;
import org.kuali.module.purap.service.VendorService;

import edu.iu.uis.eden.exception.WorkflowException;

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
    private DateTimeService dateTimeService;
    private VendorService vendorService;

	/**
	 * Default constructor.
	 */
	public RequisitionDocument() {

	}

    /**
     * Perform logic needed to initiate Requisition Document
     */
    public void initiateDocument() {

        this.setRequisitionSourceCode(PurapConstants.REQ_SOURCE_STANDARD_ORDER);
        this.setRequisitionStatusCode(PurapConstants.REQ_STAT_IN_PROCESS);
        this.setPurchaseOrderCostSourceCode(PurapConstants.PO_COST_SRC_ESTIMATE);
        this.setPurchaseOrderTransmissionMethodCode(PurapConstants.PO_TRANSMISSION_METHOD_FAX);

        // ripierce: the PostingYear has already been set before we come to this method.

        KualiUser currentUser = GlobalVariables.getUserSession().getKualiUser();
        this.setChartOfAccountsCode(currentUser.getChartOfAccountsCode());
        this.setOrganizationCode(currentUser.getOrganization().getOrganizationCode());
        this.setDeliveryCampusCode(currentUser.getUniversalUser().getCampusCode());

        // TODO wait to code this until we have the new table created
//        Integer contractId = this.getVendorContractGeneratedIdentifier();
//        vendorService.getApoLimitFromContract(contractId, this.getChartOfAccountsCode(), this.getOrganizationCode());
//        updateOrganizationAndAPOLimit(r);// this must be done after the chart/org has been set on the req (do not move this line)
        BillingAddress billingAddress = new BillingAddress();
        billingAddress.setBillingCampusCode(this.getDeliveryCampusCode());
        Map keys = SpringServiceLocator.getPersistenceService().getPrimaryKeyFieldValues(billingAddress);
        billingAddress = (BillingAddress) SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(BillingAddress.class, keys);
        if (ObjectUtils.isNotNull(billingAddress)) {
            this.setBillingName(billingAddress.getBillingName());
            this.setBillingLine1Address(billingAddress.getBillingLine1Address());
            this.setBillingLine2Address(billingAddress.getBillingLine2Address());
            this.setBillingCityName(billingAddress.getBillingCityName());
            this.setBillingStateCode(billingAddress.getBillingStateCode());
            this.setBillingPostalCode(billingAddress.getBillingPostalCode());
            this.setBillingCountryCode(billingAddress.getBillingCountryCode());
            this.setBillingPhoneNumber(billingAddress.getBillingPhoneNumber());
        }

// TODO Needed?      this.updateDropDownObjects(r);

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
     * Convenience method to set vendor detail fields based on a given VendorDetail.
     * 
     * @param vendorDetail
     */
    public void templateVendorDetail(VendorDetail vendorDetail) {
        if (vendorDetail == null) {
            return;
        }

        this.setVendorDetail(vendorDetail);
        this.setVendorNumber(vendorDetail.getVendorHeaderGeneratedIdentifier() + PurapConstants.DASH + vendorDetail.getVendorDetailAssignedIdentifier());
        this.setVendorName(vendorDetail.getVendorName());
    }
    
    /**
     * Convenience method to set vendor contract fields based on a given VendorContract.
     * 
     * @param vendorContract
     */
    public void templateVendorContract(VendorContract vendorContract) {
        if (vendorContract == null) {
            return;
        }

        this.setVendorContract(vendorContract);
        this.setVendorContractName(vendorContract.getVendorContractName());
    }
    
    /**
     * A method in which to do checks as to whether copying of this document should be allowed.
     * Copying is not to be allowed if this is a B2B req. and more than a set number of days
     * have passed since the document's creation.
     * 
     * @return  True if copying of this requisition is allowed.
     * @see     org.kuali.core.document.Document#getAllowsCopy()
     */
    @Override
    public boolean getAllowsCopy() {
        boolean allowsCopy = super.getAllowsCopy();
        if( this.getRequisitionSourceCode().equals( PurapConstants.REQ_SOURCE_B2B ) ) {
            String allowedCopyDays = ( new Integer( PurapConstants.REQ_B2B_ALLOW_COPY_DAYS ) ).toString();
            
            Calendar c = Calendar.getInstance();
            DocumentHeader dh = this.getDocumentHeader();
            KualiWorkflowDocument wd = dh.getWorkflowDocument();
            Timestamp createDate = (Timestamp)wd.getCreateDate();
            c.setTime( createDate );
            c.set(Calendar.HOUR, 12);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            c.set(Calendar.AM_PM, Calendar.AM);
            c.add(Calendar.DATE, Integer.parseInt(allowedCopyDays));
            //The allowed copy date is the document creation date plus a set number of days.
            Timestamp allowedCopyDate = new Timestamp(c.getTime().getTime());
            
            Calendar c2 = Calendar.getInstance();
            c2.setTime(new java.util.Date());
            c2.set(Calendar.HOUR, 11);
            c2.set(Calendar.MINUTE, 59);
            c2.set(Calendar.SECOND, 59);
            c2.set(Calendar.MILLISECOND, 59);
            c2.set(Calendar.AM_PM, Calendar.PM);
            Timestamp testTime = new Timestamp(c2.getTime().getTime());
            
            //Return true if the current time is before the allowed copy date.
            allowsCopy = (testTime.compareTo(allowedCopyDate) <=  0);
        }
        return allowsCopy;
    }

    /**
     * Perform logic needed to copy Requisition Document
     */
    @Override
    public void convertIntoCopy() throws WorkflowException {
      super.convertIntoCopy();
      
      KualiUser currentUser = GlobalVariables.getUserSession().getKualiUser();
      
      //Set req status to INPR.
      this.setRequisitionStatusCode(PurapConstants.REQ_STAT_IN_PROCESS);

      //Set fields from the user.
      this.setChartOfAccountsCode(currentUser.getOrganization().getChartOfAccountsCode());
      this.setOrganizationCode(currentUser.getOrganizationCode());

      this.dateTimeService = SpringServiceLocator.getDateTimeService();
      this.setPostingYear(dateTimeService.getCurrentFiscalYear());

      boolean activeVendor = true;
      boolean activeContract = true;

      Date today = dateTimeService.getCurrentDate();
 
      VendorContract vendorContract = new VendorContract();
      vendorContract.setVendorContractGeneratedIdentifier(this.getVendorContractGeneratedIdentifier());
      Map keys = SpringServiceLocator.getPersistenceService().getPrimaryKeyFieldValues(vendorContract);
      vendorContract = (VendorContract)SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(VendorContract.class, keys);
      if (!(vendorContract != null && 
          today.after(vendorContract.getVendorContractBeginningDate()) && 
          today.before(vendorContract.getVendorContractEndDate()))) {
        activeContract = false;
      }     

      this.vendorService = SpringServiceLocator.getVendorService();
      VendorDetail vendorDetail = vendorService.getVendorDetail(this.getVendorHeaderGeneratedIdentifier(), 
          this.getVendorDetailAssignedIdentifier());
      if(!(vendorDetail != null && vendorDetail.isDataObjectMaintenanceCodeActiveIndicator() )) {
          activeVendor = false;
      }

      //B2B - only copy if contract and vendor are both active (throw separate errors to print to screen)
      if(this.getRequisitionSourceCode().equals(PurapConstants.REQ_SOURCE_B2B)) {
          if( !activeContract ) {
              GlobalVariables.getErrorMap().putError(this.getVendorContractGeneratedIdentifier().toString(),
                      PurapKeyConstants.ERROR_REQ_COPY_EXPIRED_CONTRACT,this.getRequisitionIdentifier().toString());
              return;
          }
          if( !activeVendor ) {
              GlobalVariables.getErrorMap().putError(this.getVendorHeaderGeneratedIdentifier().toString(),
                      PurapKeyConstants.ERROR_REQ_COPY_INACTIVE_VENDOR,this.getRequisitionIdentifier().toString());
              return;
          }
      }
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

      if( !activeVendor ) {
        this.setVendorHeaderGeneratedIdentifier(null);
        this.setVendorDetailAssignedIdentifier(null);
        if( !activeContract ) {
          this.setVendorContract(null);
        }
      }

      //These fields should not be set in this method; force to be null
      this.setVendorNoteText(null);
      //this.setContractManager(null);
      this.setContractManagerCode(null);
      this.setInstitutionContactName(null);
      this.setInstitutionContactPhoneNumber(null);
      this.setInstitutionContactEmailAddress(null);
      //this.setAutomaticPurchaseOrderLimit(null);
      this.setOrganizationAutomaticPurchaseOrderLimit(null);
      //this.setIsAPO(null);
      this.setPurchaseOrderAutomaticIndicator(false);
      //TODO dterret: Make sure that the Status History gets nulled out here after David E. gets done
      //implementing it.
      //this.setStatusHistoryList(null);

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
