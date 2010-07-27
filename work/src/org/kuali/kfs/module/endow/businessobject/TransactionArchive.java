/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Business Object for Holding Tax Lot table
 */
public class TransactionArchive extends PersistableBusinessObjectBase {
    
    // Composite keys:
    private String  documentNumber;
    private Integer lineNumber;
    private String  lineTypeCode;
    
    // Other fields:
    private String description;
    private String typeCode;
    private String subTypeCode;
    private String srcTypeCode;
    private String kemid;
    private String etranCode;
    private String lineDescription;
    private String incomePrincipalIndicatorCode;
    private BigDecimal principalCashAmount;
    private BigDecimal incomeCashAmount;
    private boolean corpusIndicator;
    private BigDecimal corpusAmount;
    private Date postedDate;
    
    // Reference objects:
    protected TransactionArchiveSecurity archiveSecurity;
    protected EndowmentTransactionCode etranObj;
    protected TransactionTypeCode typeCodeObj;
    protected KEMID kemidObj;
    
    // Transient members:
    private transient BigDecimal greaterAmount;
    private transient BigDecimal lessAmount;
    
    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap<String, Object> toStringMapper() {
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put(EndowPropertyConstants.TRANSACTION_ARCHIVE_DOCUMENT_NUMBER, this.documentNumber);
        m.put(EndowPropertyConstants.TRANSACTION_ARCHIVE_LINE_NUMBER, this.lineNumber);
        m.put(EndowPropertyConstants.TRANSACTION_ARCHIVE_LINE_TYPE_CODE, this.lineTypeCode);
        
        return m;
    }

    /**
     * 
     * This method...
     * @return
     */
    public String getKemidResults() {
        String result = "";
        if (ObjectUtils.isNotNull(kemidObj)) {
            result += "[" + kemid + "," + " ,";
            result += kemidObj.getShortTitle() + "]";
        }
        
        return result;
    }
    
    /**
     * 
     * This method...
     * @return
     */
    public String getTransactionTypeResults() {
        String result = "";
        if (ObjectUtils.isNotNull(typeCodeObj)) {
            result += "[" + typeCode + "," + " ,";
            result += typeCodeObj.getDescription() + "]";
        }
        
        return result;
    }
    
    /**
     * 
     * This method...
     * @return
     */
    public String getEtranCodeResults() {
        String result = "";
        if (ObjectUtils.isNotNull(etranObj)) {
            result += "[" + etranCode + "," + " ,";
            result += etranObj.getName() + "]";
        }
        
        return result;
    }
    
    /**
     * 
     * This method...
     * @return
     */
    public String getSecurityResults() {
        String result = "";
        if (ObjectUtils.isNotNull(archiveSecurity)) {
            result += "[" + archiveSecurity.getSecurityId() + "," + " ,";
            result += archiveSecurity.getSecurity().getDescription() + "]";
        }
        
        return result;
    }
    
    /**
     * 
     * This method...
     * @return
     */
    public String getTransactionArchiveViewer() {
        return "View Detail";
    }

    /**
     * Gets the etranObj attribute. 
     * @return Returns the etranObj.
     */
    public EndowmentTransactionCode getEtranObj() {
        return etranObj;
    }

    /**
     * Sets the etranObj attribute value.
     * @param etranObj The etranObj to set.
     */
    public void setEtranObj(EndowmentTransactionCode etranObj) {
        this.etranObj = etranObj;
    }

    /**
     * Gets the typeCodeObj attribute. 
     * @return Returns the typeCodeObj.
     */
    public TransactionTypeCode getTypeCodeObj() {
        return typeCodeObj;
    }

    /**
     * Sets the typeCodeObj attribute value.
     * @param typeCodeObj The typeCodeObj to set.
     */
    public void setTypeCodeObj(TransactionTypeCode typeCodeObj) {
        this.typeCodeObj = typeCodeObj;
    }

    /**
     * Gets the kemidObj attribute. 
     * @return Returns the kemidObj.
     */
    public KEMID getKemidObj() {
        return kemidObj;
    }

    /**
     * Sets the kemidObj attribute value.
     * @param kemidObj The kemidObj to set.
     */
    public void setKemidObj(KEMID kemidObj) {
        this.kemidObj = kemidObj;
    }

    /**
     * Gets the greaterAmount attribute. 
     * @return Returns the greaterAmount.
     */
    public BigDecimal getGreaterAmount() {
        return greaterAmount;
    }

    /**
     * Sets the greaterAmount attribute value.
     * @param greaterAmount The greaterAmount to set.
     */
    public void setGreaterAmount(BigDecimal greaterAmount) {
        this.greaterAmount = greaterAmount;
    }

    /**
     * Gets the lessAmount attribute. 
     * @return Returns the lessAmount.
     */
    public BigDecimal getLessAmount() {
        return lessAmount;
    }

    /**
     * Sets the lessAmount attribute value.
     * @param lessAmount The lessAmount to set.
     */
    public void setLessAmount(BigDecimal lessAmount) {
        this.lessAmount = lessAmount;
    }

    /**
     * Gets the archiveSecurity attribute. 
     * @return Returns the archiveSecurity.
     */
    public TransactionArchiveSecurity getArchiveSecurity() {
        return archiveSecurity;
    }

    /**
     * Sets the archiveSecurity attribute value.
     * @param archiveSecurity The archiveSecurity to set.
     */
    public void setArchiveSecurity(TransactionArchiveSecurity archiveSecurity) {
        this.archiveSecurity = archiveSecurity;
    }

    /**
     * Gets the documentNumber attribute. 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the lineNumber attribute. 
     * @return Returns the lineNumber.
     */
    public Integer getLineNumber() {
        return lineNumber;
    }

    /**
     * Sets the lineNumber attribute value.
     * @param lineNumber The lineNumber to set.
     */
    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * Gets the lineTypeCode attribute. 
     * @return Returns the lineTypeCode.
     */
    public String getLineTypeCode() {
        return lineTypeCode;
    }

    /**
     * Sets the lineTypeCode attribute value.
     * @param lineTypeCode The lineTypeCode to set.
     */
    public void setLineTypeCode(String lineTypeCode) {
        this.lineTypeCode = lineTypeCode;
    }

    /**
     * Gets the description attribute. 
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description attribute value.
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the typeCode attribute. 
     * @return Returns the typeCode.
     */
    public String getTypeCode() {
        return typeCode;
    }

    /**
     * Sets the typeCode attribute value.
     * @param typeCode The typeCode to set.
     */
    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    /**
     * Gets the subTypeCode attribute. 
     * @return Returns the subTypeCode.
     */
    public String getSubTypeCode() {
        return subTypeCode;
    }

    /**
     * Sets the subTypeCode attribute value.
     * @param subTypeCode The subTypeCode to set.
     */
    public void setSubTypeCode(String subTypeCode) {
        this.subTypeCode = subTypeCode;
    }

    /**
     * Gets the srcTypeCode attribute. 
     * @return Returns the srcTypeCode.
     */
    public String getSrcTypeCode() {
        return srcTypeCode;
    }

    /**
     * Sets the srcTypeCode attribute value.
     * @param srcTypeCode The srcTypeCode to set.
     */
    public void setSrcTypeCode(String srcTypeCode) {
        this.srcTypeCode = srcTypeCode;
    }

    /**
     * Gets the kemid attribute. 
     * @return Returns the kemid.
     */
    public String getKemid() {
        return kemid;
    }

    /**
     * Sets the kemid attribute value.
     * @param kemid The kemid to set.
     */
    public void setKemid(String kemid) {
        this.kemid = kemid;
    }

    /**
     * Gets the etranCode attribute. 
     * @return Returns the etranCode.
     */
    public String getEtranCode() {
        return etranCode;
    }

    /**
     * Sets the etranCode attribute value.
     * @param etranCode The etranCode to set.
     */
    public void setEtranCode(String etranCode) {
        this.etranCode = etranCode;
    }

    /**
     * Gets the lineDescription attribute. 
     * @return Returns the lineDescription.
     */
    public String getLineDescription() {
        return lineDescription;
    }

    /**
     * Sets the lineDescription attribute value.
     * @param lineDescription The lineDescription to set.
     */
    public void setLineDescription(String lineDescription) {
        this.lineDescription = lineDescription;
    }

    /**
     * Gets the incomePrincipalIndicatorCode attribute. 
     * @return Returns the incomePrincipalIndicatorCode.
     */
    public String getIncomePrincipalIndicatorCode() {
        return incomePrincipalIndicatorCode;
    }

    /**
     * Sets the incomePrincipalIndicatorCode attribute value.
     * @param incomePrincipalIndicatorCode The incomePrincipalIndicatorCode to set.
     */
    public void setIncomePrincipalIndicatorCode(String incomePrincipalIndicatorCode) {
        this.incomePrincipalIndicatorCode = incomePrincipalIndicatorCode;
    }

    /**
     * Gets the principalCashAmount attribute. 
     * @return Returns the principalCashAmount.
     */
    public BigDecimal getPrincipalCashAmount() {
        return principalCashAmount;
    }

    /**
     * Sets the principalCashAmount attribute value.
     * @param principalCashAmount The principalCashAmount to set.
     */
    public void setPrincipalCashAmount(BigDecimal principalCashAmount) {
        this.principalCashAmount = principalCashAmount;
    }

    /**
     * Gets the incomeCashAmount attribute. 
     * @return Returns the incomeCashAmount.
     */
    public BigDecimal getIncomeCashAmount() {
        return incomeCashAmount;
    }

    /**
     * Sets the incomeCashAmount attribute value.
     * @param incomeCashAmount The incomeCashAmount to set.
     */
    public void setIncomeCashAmount(BigDecimal incomeCashAmount) {
        this.incomeCashAmount = incomeCashAmount;
    }

    /**
     * Gets the corpusIndicator attribute. 
     * @return Returns the corpusIndicator.
     */
    public boolean getCorpusIndicator() {
        return corpusIndicator;
    }

    /**
     * Sets the corpusIndicator attribute value.
     * @param corpusIndicator The corpusIndicator to set.
     */
    public void setCorpusIndicator(boolean corpusIndicator) {
        this.corpusIndicator = corpusIndicator;
    }

    /**
     * Gets the corpusAmount attribute. 
     * @return Returns the corpusAmount.
     */
    public BigDecimal getCorpusAmount() {
        return corpusAmount;
    }

    /**
     * Sets the corpusAmount attribute value.
     * @param corpusAmount The corpusAmount to set.
     */
    public void setCorpusAmount(BigDecimal corpusAmount) {
        this.corpusAmount = corpusAmount;
    }

    /**
     * Gets the postedDate attribute. 
     * @return Returns the postedDate.
     */
    public Date getPostedDate() {
        return postedDate;
    }

    /**
     * Sets the postedDate attribute value.
     * @param postedDate The postedDate to set.
     */
    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }
    
}
