/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This is the generic class which contains all the elements on a typical Endowment Transaction Line.
 */
public abstract class EndowmentTransactionLineBase extends PersistableBusinessObjectBase implements EndowmentTransactionLine {

    private String documentNumber;
    private String transactionLineTypeCode;
    private Integer transactionLineNumber;
    private String kemid;
    private String etranCode;
    private String transactionLineDescription;
    private String transactionIPIndicatorCode;
    private KualiDecimal transactionAmount;
    private boolean corpusIndicator;
    private KualiDecimal transactionUnits;
    private boolean linePosted;
    private BigDecimal unitAdjustmentAmount;

    private KEMID kemidObj;
    private EndowmentTransactionCode etranCodeObj;
    private IncomePrincipalIndicator incomePrincipalIndicator;

    private List<EndowmentTransactionTaxLotLine> taxLotLines;

    /**
     * Constructs a EndowmentTransactionLineBase.java.
     */
    public EndowmentTransactionLineBase() {
        setTransactionAmount(KualiDecimal.ZERO);
        setTransactionUnits(KualiDecimal.ZERO);

        kemidObj = new KEMID();
        etranCodeObj = new EndowmentTransactionCode();
        incomePrincipalIndicator = new IncomePrincipalIndicator();

        taxLotLines = new ArrayList<EndowmentTransactionTaxLotLine>();
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(EndowPropertyConstants.TRANSACTION_LINE_DOCUMENT_NUMBER, this.documentNumber);
        m.put(EndowPropertyConstants.TRANSACTION_LINE_TYPE_CODE, this.transactionLineTypeCode);
        m.put(EndowPropertyConstants.TRANSACTION_LINE_NUMBER, this.transactionLineNumber);
        return m;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#getDocumentNumber()
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#setDocumentNumber(java.lang.String)
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#getTransactionLineTypeCode()
     */
    public String getTransactionLineTypeCode() {
        return transactionLineTypeCode;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#setTransactionLineTypeCode(java.lang.String)
     */
    public void setTransactionLineTypeCode(String transactionLineTypeCode) {
        this.transactionLineTypeCode = transactionLineTypeCode;
    }


    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#getTransactionLineNumber()
     */
    public Integer getTransactionLineNumber() {
        return transactionLineNumber;
    }


    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#setTransactionLineNumber(java.lang.Integer)
     */
    public void setTransactionLineNumber(Integer transactionLineNumber) {
        this.transactionLineNumber = transactionLineNumber;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#getKemid()
     */
    public String getKemid() {
        return kemid;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#setKemid(java.lang.String)
     */
    public void setKemid(String kemid) {
        this.kemid = kemid;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#getEtranCode()
     */
    public String getEtranCode() {
        return etranCode;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#setEtranCode(java.lang.String)
     */
    public void setEtranCode(String etranCode) {
        this.etranCode = etranCode;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#getTransactionLineDescription()
     */
    public String getTransactionLineDescription() {
        return transactionLineDescription;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#setTransactionLineDescription(java.lang.String)
     */
    public void setTransactionLineDescription(String transactionLineDescription) {
        this.transactionLineDescription = transactionLineDescription;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#getTransactionAmount()
     */
    public KualiDecimal getTransactionAmount() {
        return transactionAmount;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#setTransactionAmount(org.kuali.rice.core.api.util.type.KualiDecimal)
     */
    public void setTransactionAmount(KualiDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#getCorpusIndicator()
     */
    public boolean getCorpusIndicator() {
        return corpusIndicator;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#setCorpusIndicator(boolean)
     */
    public void setCorpusIndicator(boolean corpusIndicator) {
        this.corpusIndicator = corpusIndicator;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#getTransactionUnits()
     */
    public KualiDecimal getTransactionUnits() {
        return transactionUnits;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#setTransactionUnits(org.kuali.rice.core.api.util.type.KualiDecimal)
     */
    public void setTransactionUnits(KualiDecimal transactionUnits) {
        this.transactionUnits = transactionUnits;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#isLinePosted()
     */
    public boolean isLinePosted() {
        return linePosted;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#setLinePosted(boolean)
     */
    public void setLinePosted(boolean linePosted) {
        this.linePosted = linePosted;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#getTransactionIPIndicatorCode()
     */
    public String getTransactionIPIndicatorCode() {
        return transactionIPIndicatorCode;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#setTransactionIPIndicatorCode(java.lang.String)
     */
    public void setTransactionIPIndicatorCode(String transactionIPIndicatorCode) {
        this.transactionIPIndicatorCode = transactionIPIndicatorCode;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#getIncomePrincipalIndicator()
     */
    public IncomePrincipalIndicator getIncomePrincipalIndicator() {
        return incomePrincipalIndicator;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#setIncomePrincipalIndicator(org.kuali.kfs.module.endow.businessobject.IncomePrincipalIndicator)
     */
    public void setIncomePrincipalIndicator(IncomePrincipalIndicator incomePrincipalIndicator) {
        this.incomePrincipalIndicator = incomePrincipalIndicator;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#getKemidObj()
     */
    public KEMID getKemidObj() {
        return kemidObj;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#setKemidObj(org.kuali.kfs.module.endow.businessobject.KEMID)
     */
    public void setKemidObj(KEMID kemidObj) {
        this.kemidObj = kemidObj;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#getEtranCodeObj()
     */
    public EndowmentTransactionCode getEtranCodeObj() {
        return etranCodeObj;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#setEtranCodeObj(org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode)
     */
    public void setEtranCodeObj(EndowmentTransactionCode etranCodeObj) {
        this.etranCodeObj = etranCodeObj;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#getTaxLotLines()
     */
    public List<EndowmentTransactionTaxLotLine> getTaxLotLines() {
        return taxLotLines;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#setTaxLotLines(java.util.List)
     */
    public void setTaxLotLines(List<EndowmentTransactionTaxLotLine> taxLotLines) {
        this.taxLotLines = taxLotLines;
    }

    /**
     * Gets unitAdjustmentAmount
     * 
     * @return unitAdjustmentAmount
     */
    public BigDecimal getUnitAdjustmentAmount() {
        return unitAdjustmentAmount;
    }

    /**
     * sets unitAdjustmentAmount
     */
    public void setUnitAdjustmentAmount(BigDecimal unitAdjustmentAmount) {
        this.unitAdjustmentAmount = unitAdjustmentAmount;
    }
}
