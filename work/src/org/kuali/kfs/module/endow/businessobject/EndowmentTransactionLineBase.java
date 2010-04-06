/*
 * Copyright 2010 The Kuali Foundation.
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

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.KualiInteger;

/**
 * This is the generic class which contains all the elements on a typical Endowment Transaction Line.
 */
public abstract class EndowmentTransactionLineBase extends PersistableBusinessObjectBase implements EndowmentTransactionLine {

    private String documentNumber;
    private String transactionLineTypeCode;
    private KualiInteger transactionLineNumber;
    private String kemid;
    private String etranCode;
    private String transactionLineDescription;
    private String transactionIncomePrincipalIndicatorCode;
    private KualiDecimal transactionAmount;
    private boolean corpusIndicator;
    private KualiDecimal transactionUnits;
    private boolean linePosted;

    private KEMID kemidObj;
    private EndowmentTransactionCode etranCodeObj;
    private IncomePrincipalIndicator incomePrincipalIndicator;

    /**
     * Constructs a EndowmentTransactionLineBase.java.
     */
    public EndowmentTransactionLineBase() {
        setTransactionAmount(KualiDecimal.ZERO);
        setTransactionUnits(KualiDecimal.ZERO);

        kemidObj = new KEMID();
        etranCodeObj = new EndowmentTransactionCode();
        incomePrincipalIndicator = new IncomePrincipalIndicator();
    }

    /**
     * Gets the documentNumber.
     * 
     * @return documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber.
     * 
     * @param documentNumber
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
    public KualiInteger getTransactionLineNumber() {
        return transactionLineNumber;
    }


    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#setTransactionLineNumber(org.kuali.rice.kns.util.KualiInteger)
     */
    public void setTransactionLineNumber(KualiInteger transactionLineNumber) {
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
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#setTransactionAmount(org.kuali.rice.kns.util.KualiDecimal)
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
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#setTransactionUnits(org.kuali.rice.kns.util.KualiDecimal)
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
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#getTransactionIncomePrincipalIndicatorCode()
     */
    public String getTransactionIncomePrincipalIndicatorCode() {
        return transactionIncomePrincipalIndicatorCode;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#setTransactionIncomePrincipalIndicatorCode(java.lang.String)
     */
    public void setTransactionIncomePrincipalIndicatorCode(String transactionIncomePrincipalIndicatorCode) {
        this.transactionIncomePrincipalIndicatorCode = transactionIncomePrincipalIndicatorCode;
    }


    @Override
    protected LinkedHashMap toStringMapper() {
        // TODO Auto-generated method stub
        return null;
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
     * This method...
     * @param kemidObj
     */
    public void setKemidObj(KEMID kemidObj) {
        this.kemidObj = kemidObj;
    }

    /**
     * This method...
     * @param etranCodeObj
     */
    public void setEtranCodeObj(EndowmentTransactionCode etranCodeObj) {
        this.etranCodeObj = etranCodeObj;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#getKemidObj()
     */
    public KEMID getKemidObj() {
        return kemidObj;
    }

    /**
     * @see org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine#getEtranCodeObj()
     */
    public EndowmentTransactionCode getEtranCodeObj() {
        return etranCodeObj;
    }


}
