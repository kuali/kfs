/*
 * Created on Aug 2, 2004
 *
 */
package org.kuali.module.pdp.form.paymentsearch;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.kuali.module.pdp.bo.BatchSearch;
import org.kuali.module.pdp.utilities.DateHandler;
import org.kuali.module.pdp.utilities.GeneralUtilities;


/**
 * @author delyea
 *
 */
public class BatchSearchForm extends ActionForm {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchSearchForm.class);
  
  private List indivSearchResults;
  private String paymentSearchType;
  private List batchSearchResults;
  
  private String batchId;             // Integer
  private String paymentCount;        // Integer       
  private String paymentTotalAmount;  // BigDecimal
  private String beginDate;           // Date
  private String endDate;             // Date
  private String chartCode;
  private String orgCode;
  private String subUnitCode;


  /**
   * @param advancedSearch
   */
  public BatchSearchForm() {
    this.paymentSearchType = "B";
  }
  
  public BatchSearch getBatchSearch() {
    BatchSearch pds = new BatchSearch();
    
    pds.setBatchId(GeneralUtilities.convertStringToInteger(this.getBatchId()));
    pds.setChartCode(this.getChartCode());
    pds.setOrgCode(this.getOrgCode());
    pds.setSubUnitCode(this.getSubUnitCode());
    pds.setPaymentCount(GeneralUtilities.convertStringToInteger(this.getPaymentCount()));
    pds.setPaymentTotalAmount(GeneralUtilities.convertStringToBigDecimal(this.getPaymentTotalAmount()));
    pds.setBeginDate(GeneralUtilities.convertStringToDate(this.getBeginDate()));
    pds.setEndDate(GeneralUtilities.convertStringToDate(this.getEndDate()));

    // BOTH
    pds.setChartCode(this.getChartCode());
    pds.setOrgCode(this.getOrgCode());
    pds.setSubUnitCode(this.getSubUnitCode());

    
    return pds;
  }
  
  public void clearForm() {
    this.indivSearchResults = null;
    
    this.setBatchId("");  
    this.setChartCode("");
    this.setOrgCode("");
    this.setSubUnitCode("");
    this.setPaymentCount("");               
    this.setPaymentTotalAmount("");  
    this.setBeginDate("");           
    this.setEndDate("");             
    this.batchSearchResults = null;
  }
  
  public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
    LOG.debug("Entered validate().");
    //create instance of ActionErrors to send errors to user
    ActionErrors actionErrors = new ActionErrors();
    String buttonPressed = GeneralUtilities.whichButtonWasPressed(request);
    
    if (buttonPressed.startsWith("btnSearch")) {
      if ((GeneralUtilities.isStringEmpty(this.batchId)) &&
          (GeneralUtilities.isStringEmpty(this.chartCode)) &&
          (GeneralUtilities.isStringEmpty(this.orgCode)) &&
          (GeneralUtilities.isStringEmpty(this.subUnitCode)) &&
          (GeneralUtilities.isStringEmpty(this.paymentCount)) &&
          (GeneralUtilities.isStringEmpty(this.paymentTotalAmount)) &&
          (GeneralUtilities.isStringEmpty(this.beginDate)) &&
          (GeneralUtilities.isStringEmpty(this.endDate))) {
        actionErrors.add("errors", new ActionMessage("batchSearchForm.batchcriteria.noneEntered"));
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
        if ((!(GeneralUtilities.isStringEmpty(this.batchId))) &&
            (!(GeneralUtilities.isStringAllNumbers(this.batchId)))) {
          actionErrors.add("errors", new ActionMessage("batchSearchForm.batchId.invalid"));
        }
        if ((!(GeneralUtilities.isStringEmpty(this.paymentCount))) &&
            (!(GeneralUtilities.isStringAllNumbers(this.paymentCount)))) {
          actionErrors.add("errors", new ActionMessage("batchSearchForm.paymentCount.invalid"));
        }
        if ((!(GeneralUtilities.isStringEmpty(this.paymentTotalAmount))) &&
            (!(GeneralUtilities.isStringAllNumbersOrASingleCharacter(this.paymentTotalAmount,'.')))) {
          actionErrors.add("errors", new ActionMessage("batchSearchForm.paymentTotalAmount.invalid"));
        }
        if (!(GeneralUtilities.isStringEmpty(this.beginDate))){
          actionErrors = DateHandler.validDate(actionErrors,"errors",this.beginDate);
        }
        if (!(GeneralUtilities.isStringEmpty(this.endDate))){
          actionErrors = DateHandler.validDate(actionErrors,"errors",this.endDate);
        }
        
//        Defining Search Criteria needed for searching batches
        if ((GeneralUtilities.isStringEmpty(this.batchId)) &&
            (GeneralUtilities.isStringEmpty(this.paymentCount)) &&
            (GeneralUtilities.isStringEmpty(this.paymentTotalAmount))) {
//          If batchId, paymentCount, and paymentTotalAmount are empty then at least one date is required  
          if ((GeneralUtilities.isStringEmpty(this.beginDate)) &&
              (GeneralUtilities.isStringEmpty(this.endDate))) {
            actionErrors.add("errors", new ActionMessage("batchSearchForm.batchcriteria.noDate"));
          } else if (((!(GeneralUtilities.isStringEmpty(this.beginDate))) ||
                     (!(GeneralUtilities.isStringEmpty(this.endDate)))) &&
                    ((GeneralUtilities.isStringEmpty(this.beginDate)) ||
                     (GeneralUtilities.isStringEmpty(this.endDate)))) {
//            If we have one (but not both) dates the user must enter either the chartCode, orgCode, or subUnitCode
            if ((GeneralUtilities.isStringEmpty(this.chartCode)) &&
                (GeneralUtilities.isStringEmpty(this.orgCode)) &&
                (GeneralUtilities.isStringEmpty(this.subUnitCode))) {
              actionErrors.add("errors", new ActionMessage("batchSearchForm.batchcriteria.sourcemissing"));
            }
          }
        }
      }
      
      // Individual Search Variables in Session
      request.getSession().removeAttribute("indivSearchResults");
      request.getSession().removeAttribute("PaymentDetailSearchFormSession");
      
      // Batch Search Variables in Session
      request.getSession().removeAttribute("batchSearchResults");
      request.getSession().removeAttribute("batchIndivSearchResults");
      request.getSession().removeAttribute("BatchDetail");
      request.getSession().removeAttribute("BatchSearchFormSession");

    }

    LOG.debug("Exiting validate()  There were " + actionErrors.size() + " ActionMessages found.");
    return actionErrors;
  }
  
  /**
   * @return Returns the paymentSearchType.
   */
  public String getPaymentSearchType() {
    return paymentSearchType;
  }
  /**
   * @param paymentSearchType The paymentSearchType to set.
   */
  public void setPaymentSearchType(String paymentSearchType) {
    this.paymentSearchType = paymentSearchType;
  }
  
  /**
   * @return Returns the batchSearchResults.
   */
  public List getBatchSearchResults() {
    return batchSearchResults;
  }
  /**
   * @return Returns the indivSearchResults.
   */
  public List getIndivSearchResults() {
    return indivSearchResults;
  }
  /**
   * @param batchSearchResults The batchSearchResults to set.
   */
  public void setBatchSearchResults(List batchSearchResults) {
    this.batchSearchResults = batchSearchResults;
  }
  /**
   * @param indivSearchResults The indivSearchResults to set.
   */
  public void setIndivSearchResults(List indivSearchResults) {
    this.indivSearchResults = indivSearchResults;
  }

  /**
   * @return Returns the batchId.
   */
  public String getBatchId() {
    return batchId;
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
   * @return Returns the paymentCount.
   */
  public String getPaymentCount() {
    return paymentCount;
  }
  /**
   * @return Returns the paymentTotalAmount.
   */
  public String getPaymentTotalAmount() {
    return paymentTotalAmount;
  }
  /**
   * @return Returns the subUnitCode.
   */
  public String getSubUnitCode() {
    return subUnitCode;
  }
  /**
   * @param batchId The batchId to set.
   */
  public void setBatchId(String batchId) {
    this.batchId = batchId;
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
   * @param paymentCount The paymentCount to set.
   */
  public void setPaymentCount(String paymentCount) {
    this.paymentCount = paymentCount;
  }
  /**
   * @param paymentTotalAmount The paymentTotalAmount to set.
   */
  public void setPaymentTotalAmount(String paymentTotalAmount) {
    this.paymentTotalAmount = paymentTotalAmount;
  }
  /**
   * @param subUnitCode The subUnitCode to set.
   */
  public void setSubUnitCode(String subUnitCode) {
    this.subUnitCode = subUnitCode;
  }
  /**
   * @return Returns the beginDate.
   */
  public String getBeginDate() {
    return beginDate;
  }
  /**
   * @return Returns the endDate.
   */
  public String getEndDate() {
    return endDate;
  }
  /**
   * @param beginDate The beginDate to set.
   */
  public void setBeginDate(String beginDate) {
    this.beginDate = beginDate;
  }
  /**
   * @param endDate The endDate to set.
   */
  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }
}
