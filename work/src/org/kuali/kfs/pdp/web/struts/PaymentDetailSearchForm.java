/*
 * Created on Aug 2, 2004
 *
 */
package org.kuali.module.pdp.form.paymentsearch;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.kuali.module.pdp.bo.PaymentDetailSearch;
import org.kuali.module.pdp.utilities.DateHandler;
import org.kuali.module.pdp.utilities.GeneralUtilities;


/**
 * @author delyea
 *
 */
public class PaymentDetailSearchForm extends ActionForm {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentDetailSearchForm.class);
  
  private Boolean advancedSearch;
  private String custPaymentDocNbr;
  private String invoiceNbr;         
  private String purchaseOrderNbr;
  private String payeeName;
  private String payeeId;              
  private String payeeIdTypeCd;
  private String disbursementTypeCode;
  private String paymentStatusCode;
  private String netPaymentAmount;
  private String beginDisbursementDate;
  private String endDisbursementDate;
  private String beginPaymentDate;
  private String endPaymentDate;
  private String disbursementNbr;
  private String oldDisbursementNbr; // for cancel/reissue disbNbrs showing correctly in list
  private String pymtAttachment;
  private String pymtSpecialHandling;
  private String processImmediate;
  private String requisitionNbr;
  private String iuIdForCustomer;
  private String processId;
  private String paymentId;
  private String chartCode;
  private String orgCode;
  private String subUnitCode;

  /**
   * @param advancedSearch
   */
  public PaymentDetailSearchForm() {
    this.advancedSearch = new Boolean(false);
  }
  
  public PaymentDetailSearch getPaymentDetailSearch() {
    PaymentDetailSearch pds = new PaymentDetailSearch();
    
    pds.setCustPaymentDocNbr(this.getCustPaymentDocNbr());
    pds.setDisbursementNbr(GeneralUtilities.convertStringToInteger(this.getDisbursementNbr()));
    pds.setDisbursementTypeCode(this.getDisbursementTypeCode());
    pds.setInvoiceNbr(this.getInvoiceNbr());
    pds.setNetPaymentAmount(GeneralUtilities.convertStringToBigDecimal(this.getNetPaymentAmount()));
    pds.setPayeeId(this.getPayeeId());
    pds.setPayeeIdTypeCd(this.getPayeeIdTypeCd());
    pds.setPayeeName(this.getPayeeName());
    pds.setPaymentStatusCode(this.getPaymentStatusCode());
    pds.setPurchaseOrderNbr(this.getPurchaseOrderNbr());
    pds.setPymtAttachment(this.getPymtAttachment());
    pds.setPymtSpecialHandling(this.getPymtSpecialHandling());
    pds.setProcessImmediate(this.getProcessImmediate());
    pds.setBeginDisbursementDate(GeneralUtilities.convertStringToDate(this.getBeginDisbursementDate()));
    pds.setEndDisbursementDate(GeneralUtilities.convertStringToDate(this.getEndDisbursementDate()));
    pds.setBeginPaymentDate(GeneralUtilities.convertStringToDate(this.getBeginPaymentDate()));
    pds.setEndPaymentDate(GeneralUtilities.convertStringToDate(this.getEndPaymentDate()));
    pds.setRequisitionNbr(this.getRequisitionNbr());
    pds.setIuIdForCustomer(this.getIuIdForCustomer());
    pds.setProcessId(GeneralUtilities.convertStringToInteger(this.getProcessId()));
    pds.setPaymentId(GeneralUtilities.convertStringToInteger(this.getPaymentId()));
    pds.setChartCode(this.getChartCode());
    pds.setOrgCode(this.getOrgCode());
    pds.setSubUnitCode(this.getSubUnitCode());
    
    return pds;
  }
  
  public void clearForm() {
    this.setCustPaymentDocNbr("");
    this.disbursementNbr = "";
//    this.setDisbursementNbr("");
    this.setDisbursementTypeCode("");
    this.setInvoiceNbr("");
    this.setNetPaymentAmount("");
    this.setPayeeId("");
    this.setPayeeIdTypeCd("");
    this.setPayeeName("");
    this.setPaymentStatusCode("");
    this.setPurchaseOrderNbr("");
    this.setPymtAttachment("");
    this.setPymtSpecialHandling("");
    this.setProcessImmediate("");
    this.setBeginDisbursementDate("");
    this.setEndDisbursementDate("");
    this.setBeginPaymentDate("");
    this.setEndPaymentDate("");
    this.advancedSearch = new Boolean(false);
    this.setRequisitionNbr("");
    this.setIuIdForCustomer("");
    this.setProcessId("");
    this.setPaymentId("");
    this.setChartCode("");
    this.setOrgCode("");
    this.setSubUnitCode("");
  }
  
  public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
    LOG.debug("Entered validate().");
    //create instance of ActionErrors to send errors to user
    ActionErrors actionErrors = new ActionErrors();
    String buttonPressed = GeneralUtilities.whichButtonWasPressed(request);
    
    if (buttonPressed.startsWith("btnSearch")) {
        if ((GeneralUtilities.isStringEmpty(this.custPaymentDocNbr)) &&
            (GeneralUtilities.isStringEmpty(this.invoiceNbr)) &&
            (GeneralUtilities.isStringEmpty(this.purchaseOrderNbr)) &&
            (GeneralUtilities.isStringEmpty(this.processId)) &&
            (GeneralUtilities.isStringEmpty(this.paymentId)) &&
            (GeneralUtilities.isStringEmpty(this.payeeName)) && 
            (GeneralUtilities.isStringEmpty(this.payeeId)) &&
            (GeneralUtilities.isStringEmpty(this.payeeIdTypeCd)) &&
            (GeneralUtilities.isStringEmpty(this.disbursementTypeCode)) &&
            (GeneralUtilities.isStringEmpty(this.paymentStatusCode)) &&
            (GeneralUtilities.isStringEmpty(this.netPaymentAmount)) &&
            (GeneralUtilities.isStringEmpty(this.beginDisbursementDate)) &&
            (GeneralUtilities.isStringEmpty(this.endDisbursementDate)) &&
            (GeneralUtilities.isStringEmpty(this.beginPaymentDate)) &&
            (GeneralUtilities.isStringEmpty(this.endPaymentDate)) &&
            (GeneralUtilities.isStringEmpty(this.disbursementNbr)) &&
            (GeneralUtilities.isStringEmpty(this.chartCode)) &&
            (GeneralUtilities.isStringEmpty(this.orgCode)) &&
            (GeneralUtilities.isStringEmpty(this.subUnitCode)) &&
            (GeneralUtilities.isStringEmpty(this.requisitionNbr)) &&
            (GeneralUtilities.isStringEmpty(this.iuIdForCustomer)) &&
            (GeneralUtilities.isStringEmpty(this.pymtAttachment)) &&
            (GeneralUtilities.isStringEmpty(this.processImmediate)) &&
            (GeneralUtilities.isStringEmpty(this.pymtSpecialHandling))) {
          
          actionErrors.add("errors", new ActionMessage("paymentDetailSearchForm.criteria.noneEntered"));
        } else {
          
          if (!(GeneralUtilities.isStringEmpty(this.chartCode))) { 
            this.chartCode = this.chartCode.toUpperCase();
          }
          if (!(GeneralUtilities.isStringEmpty(this.orgCode))) { 
            this.orgCode = this.orgCode.toUpperCase();
          }
          if (!(GeneralUtilities.isStringEmpty(this.subUnitCode))) { 
            this.subUnitCode = this.subUnitCode.toUpperCase();
          }
          if (!(GeneralUtilities.isStringEmpty(this.beginPaymentDate))){
            actionErrors = DateHandler.validDate(actionErrors,"errors",this.beginPaymentDate);
          }
          if (!(GeneralUtilities.isStringEmpty(this.endPaymentDate))){
            actionErrors = DateHandler.validDate(actionErrors,"errors",this.endPaymentDate);
          }
          if (!(GeneralUtilities.isStringEmpty(this.beginDisbursementDate))){
            actionErrors = DateHandler.validDate(actionErrors,"errors",this.beginDisbursementDate);
          }
          if (!(GeneralUtilities.isStringEmpty(this.endDisbursementDate))){
            actionErrors = DateHandler.validDate(actionErrors,"errors",this.endDisbursementDate);
          }
          if ((!(GeneralUtilities.isStringEmpty(this.disbursementNbr))) && 
              (!(GeneralUtilities.isStringAllNumbers(this.disbursementNbr)))){
            actionErrors.add("errors", new ActionMessage("paymentDetailSearchForm.disbursementNbr.invalid"));
          }
          if ((!(GeneralUtilities.isStringEmpty(this.processId))) && 
              (!(GeneralUtilities.isStringAllNumbers(this.processId)))) {
            actionErrors.add("errors", new ActionMessage("paymentDetailSearchForm.processId.invalid"));
          }
          if ((!(GeneralUtilities.isStringEmpty(this.paymentId))) && 
              (!(GeneralUtilities.isStringAllNumbers(this.paymentId)))) {
            actionErrors.add("errors", new ActionMessage("paymentDetailSearchForm.paymentId.invalid"));
          }
          if ((!(GeneralUtilities.isStringEmpty(this.netPaymentAmount))) && 
              (!(GeneralUtilities.isStringAllNumbersOrASingleCharacter(this.netPaymentAmount,'.')))) {
            actionErrors.add("errors", new ActionMessage("paymentDetailSearchForm.netPaymentAmount.invalid"));
          }
          if ((!(GeneralUtilities.isStringEmpty(this.payeeId))) && 
              (GeneralUtilities.isStringEmpty(this.payeeIdTypeCd))) {
            actionErrors.add("errors", new ActionMessage("paymentDetailSearchForm.payeeIdTypeCd.nullWithPayeeId"));
          }
          if ((GeneralUtilities.isStringEmpty(this.payeeId)) && 
              (!(GeneralUtilities.isStringEmpty(this.payeeIdTypeCd)))) {
            actionErrors.add("errors", new ActionMessage("paymentDetailSearchForm.payeeId.nullWithPayeeIdTypeCd"));
          }
          
          if (!(GeneralUtilities.isStringEmpty(this.custPaymentDocNbr))) {
            if (this.custPaymentDocNbr.length() < 2) {
              actionErrors.add("errors", new ActionMessage("paymentDetailSearchForm.custPaymentDocNbr.lessThan2Chars"));
            }
          }
          if (!(GeneralUtilities.isStringEmpty(this.invoiceNbr))) {
            if (this.invoiceNbr.length() < 2) {
              actionErrors.add("errors", new ActionMessage("paymentDetailSearchForm.invoiceNbr.lessThan2Chars"));
            }
          }
          if (!(GeneralUtilities.isStringEmpty(this.requisitionNbr))) {
            if (this.requisitionNbr.length() < 2) {
              actionErrors.add("errors", new ActionMessage("paymentDetailSearchForm.requisitionNbr.lessThan2Chars"));
            }
          }
          if (!(GeneralUtilities.isStringEmpty(this.purchaseOrderNbr))) {
            if (this.purchaseOrderNbr.length() < 2) {
              actionErrors.add("errors", new ActionMessage("paymentDetailSearchForm.purchaseOrderNbr.lessThan2Chars"));
            }
          }
          if (!(GeneralUtilities.isStringEmpty(this.payeeName))) {
            if (this.payeeName.length() < 2) {
              actionErrors.add("errors", new ActionMessage("paymentDetailSearchForm.payeeName.lessThan2Chars"));
            }
          }
        }
      
      if (actionErrors.size() > 0) {
        // Individual Search Variables in Session
        request.getSession().removeAttribute("indivSearchResults");
        request.getSession().removeAttribute("PaymentDetailSearchFormSession");
        
        // Batch Search Variables in Session
        request.getSession().removeAttribute("batchSearchResults");
        request.getSession().removeAttribute("batchIndivSearchResults");
        request.getSession().removeAttribute("BatchDetail");
        request.getSession().removeAttribute("BatchSearchFormSession");

      }
    }

    LOG.debug("Exiting validate()  There were " + actionErrors.size() + " ActionMessages found.");
    return actionErrors;
  }
  
  /**
   * @return Returns the custPaymentDocNbr.
   */
  public String getCustPaymentDocNbr() {
    return custPaymentDocNbr;
  }
  /**
   * @return Returns the disbursementNbr.
   */
  public String getDisbursementNbr() {
    return disbursementNbr;
  }
  /**
   * @return Returns the invoiceNbr.
   */
  public String getInvoiceNbr() {
    return invoiceNbr;
  }
  /**
   * @return Returns the netPaymentAmount.
   */
  public String getNetPaymentAmount() {
    return netPaymentAmount;
  }
  /**
   * @return Returns the payeeId.
   */
  public String getPayeeId() {
    return payeeId;
  }
  /**
   * @return Returns the payeeIdTypeCd.
   */
  public String getPayeeIdTypeCd() {
    return payeeIdTypeCd;
  }
  /**
   * @return Returns the payeeName.
   */
  public String getPayeeName() {
    return payeeName;
  }
  /**
   * @return Returns the purchaseOrderNbr.
   */
  public String getPurchaseOrderNbr() {
    return purchaseOrderNbr;
  }
  /**
   * @return Returns the pymtAttachment.
   */
  public String getPymtAttachment() {
    return pymtAttachment;
  }
  /**
   * @param custPaymentDocNbr The custPaymentDocNbr to set.
   */
  public void setCustPaymentDocNbr(String custPaymentDocNbr) {
    this.custPaymentDocNbr = custPaymentDocNbr;
  }
  /**
   * @param disbursementNbr The disbursementNbr to set.
   */
  public void setDisbursementNbr(String disbursementNbr) {
    this.disbursementNbr = disbursementNbr;
    if (!(GeneralUtilities.isStringEmpty(disbursementNbr))){
      setOldDisbursementNbr(disbursementNbr);
    }
  }
  /**
   * @param invoiceNbr The invoiceNbr to set.
   */
  public void setInvoiceNbr(String invoiceNbr) {
    this.invoiceNbr = invoiceNbr;
  }
  /**
   * @param netPaymentAmount The netPaymentAmount to set.
   */
  public void setNetPaymentAmount(String netPaymentAmount) {
    this.netPaymentAmount = netPaymentAmount;
  }
  /**
   * @param payeeId The payeeId to set.
   */
  public void setPayeeId(String payeeId) {
    this.payeeId = payeeId;
  }
  /**
   * @param payeeIdTypeCd The payeeIdTypeCd to set.
   */
  public void setPayeeIdTypeCd(String payeeIdTypeCd) {
    this.payeeIdTypeCd = payeeIdTypeCd;
  }
  /**
   * @param payeeName The payeeName to set.
   */
  public void setPayeeName(String payeeName) {
    this.payeeName = payeeName;
  }
  /**
   * @param purchaseOrderNbr The purchaseOrderNbr to set.
   */
  public void setPurchaseOrderNbr(String purchaseOrderNbr) {
    this.purchaseOrderNbr = purchaseOrderNbr;
  }
  /**
   * @param pymtAttachment The pymtAttachment to set.
   */
  public void setPymtAttachment(String pymtAttachment) {
    this.pymtAttachment = pymtAttachment;
  }
  /**
   * @return Returns the disbursementTypeCode.
   */
  public String getDisbursementTypeCode() {
    return disbursementTypeCode;
  }
  /**
   * @return Returns the paymentStatusCode.
   */
  public String getPaymentStatusCode() {
    return paymentStatusCode;
  }
  /**
   * @param disbursementTypeCode The disbursementTypeCode to set.
   */
  public void setDisbursementTypeCode(String disbursementTypeCode) {
    this.disbursementTypeCode = disbursementTypeCode;
  }
  /**
   * @param paymentStatusCode The paymentStatusCode to set.
   */
  public void setPaymentStatusCode(String paymentStatusCode) {
    this.paymentStatusCode = paymentStatusCode;
  }
  /**
   * @return Returns the advancedSearch.
   */
  public Boolean getAdvancedSearch() {
    return advancedSearch;
  }
  /**
   * @param advancedSearch The advancedSearch to set.
   */
  public void setAdvancedSearch(Boolean advancedSearch) {
    this.advancedSearch = advancedSearch;
  }
  /**
   * @param advancedSearch The advancedSearch to set.
   */
  public void changeAdvancedSearch() {
    if (advancedSearch.booleanValue()) {
      this.advancedSearch = new Boolean(false);
    } else {
      this.advancedSearch = new Boolean(true);
    }
  }
  /**
   * @return Returns the chartCode.
   */
  public String getChartCode() {
    return chartCode;
  }
  /**
   * @return Returns the orgCode.
   */
  public String getOrgCode() {
    return orgCode;
  }
  /**
   * @return Returns the subUnitCode.
   */
  public String getSubUnitCode() {
    return subUnitCode;
  }
  /**
   * @param chartCode The chartCode to set.
   */
  public void setChartCode(String chartCode) {
    this.chartCode = chartCode;
  }
  /**
   * @param orgCode The orgCode to set.
   */
  public void setOrgCode(String orgCode) {
    this.orgCode = orgCode;
  }
  /**
   * @param subUnitCode The subUnitCode to set.
   */
  public void setSubUnitCode(String subUnitCode) {
    this.subUnitCode = subUnitCode;
  }
  /**
   * @return Returns the beginDisbursementDate.
   */
  public String getBeginDisbursementDate() {
    return beginDisbursementDate;
  }
  /**
   * @return Returns the beginPaymentDate.
   */
  public String getBeginPaymentDate() {
    return beginPaymentDate;
  }
  /**
   * @return Returns the endDisbursementDate.
   */
  public String getEndDisbursementDate() {
    return endDisbursementDate;
  }
  /**
   * @return Returns the endPaymentDate.
   */
  public String getEndPaymentDate() {
    return endPaymentDate;
  }
  /**
   * @param beginDisbursementDate The beginDisbursementDate to set.
   */
  public void setBeginDisbursementDate(String beginDisbursementDate) {
    this.beginDisbursementDate = beginDisbursementDate;
  }
  /**
   * @param beginPaymentDate The beginPaymentDate to set.
   */
  public void setBeginPaymentDate(String beginPaymentDate) {
    this.beginPaymentDate = beginPaymentDate;
  }
  /**
   * @param endDisbursementDate The endDisbursementDate to set.
   */
  public void setEndDisbursementDate(String endDisbursementDate) {
    this.endDisbursementDate = endDisbursementDate;
  }
  /**
   * @param endPaymentDate The endPaymentDate to set.
   */
  public void setEndPaymentDate(String endPaymentDate) {
    this.endPaymentDate = endPaymentDate;
  }
  /**
   * @return Returns the iuIdForCustomer.
   */
  public String getIuIdForCustomer() {
    return iuIdForCustomer;
  }
  /**
   * @return Returns the requisitionNbr.
   */
  public String getRequisitionNbr() {
    return requisitionNbr;
  }
  /**
   * @param iuIdForCustomer The iuIdForCustomer to set.
   */
  public void setIuIdForCustomer(String iuIdForCustomer) {
    this.iuIdForCustomer = iuIdForCustomer;
  }
  /**
   * @param requisitionNbr The requisitionNbr to set.
   */
  public void setRequisitionNbr(String requisitionNbr) {
    this.requisitionNbr = requisitionNbr;
  }
  /**
   * @return Returns the processId.
   */
  public String getProcessId() {
    return processId;
  }
  /**
   * @param procId The processId to set.
   */
  public void setProcessId(String processId) {
    this.processId = processId;
  }
  /**
   * @return Returns the pymtSpecialHandling.
   */
  public String getPymtSpecialHandling() {
    return pymtSpecialHandling;
  }
  /**
   * @param pymtSpecialHandling The pymtSpecialHandling to set.
   */
  public void setPymtSpecialHandling(String pymtSpecialHandling) {
    this.pymtSpecialHandling = pymtSpecialHandling;
  }
  /**
   * @return Returns the paymentId.
   */
  public String getPaymentId() {
    return paymentId;
  }
  /**
   * @param paymentId The paymentId to set.
   */
  public void setPaymentId(String paymentId) {
    this.paymentId = paymentId;
  }
  public String getProcessImmediate() {
    return processImmediate;
  }
  public void setProcessImmediate(String processImmediate) {
    this.processImmediate = processImmediate;
  }
  public String getOldDisbursementNbr() {
    return oldDisbursementNbr;
  }
  public void setOldDisbursementNbr(String oldDisbursementNbr) {
    this.oldDisbursementNbr = oldDisbursementNbr;
  }
}
