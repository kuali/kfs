/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.endow.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This EndowmentCorpusValues class provides the relevant values for monitoring the Endowment.
 */
public class EndowmentCorpusValues extends PersistableBusinessObjectBase {

    private String kemid;
    private KualiDecimal endowmentCorpus;
    private KualiDecimal currentPrincipalMarketValue;
    private KualiDecimal priorFYEndCorpusValue;
    private KualiDecimal priorFYEndPrincipalMarketValue;

    private KEMID kemidObjRef;

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(EndowPropertyConstants.KEMID, this.kemid);
        return m;
    }


    /**
     * Gets the currentPrincipalMarketValue.
     * 
     * @return currentPrincipalMarketValue
     */
    public KualiDecimal getCurrentPrincipalMarketValue() {
        return currentPrincipalMarketValue;
    }


    /**
     * Sets the currentPrincipalMarketValue.
     * 
     * @param currentPrincipalMarketValue
     */
    public void setCurrentPrincipalMarketValue(KualiDecimal currentPrincipalMarketValue) {
        this.currentPrincipalMarketValue = currentPrincipalMarketValue;
    }


    /**
     * Gets the endowmentCorpus.
     * 
     * @return endowmentCorpus
     */
    public KualiDecimal getEndowmentCorpus() {
        return endowmentCorpus;
    }


    /**
     * Sets the endowmentCorpus.
     * 
     * @param endowmentCorpus
     */
    public void setEndowmentCorpus(KualiDecimal endowmentCorpus) {
        this.endowmentCorpus = endowmentCorpus;
    }


    /**
     * Gets the kemid.
     * 
     * @return kemid
     */
    public String getKemid() {
        return kemid;
    }


    /**
     * Sets the kemid.
     * 
     * @param kemid
     */
    public void setKemid(String kemid) {
        this.kemid = kemid;
    }


    /**
     * Gets the priorFYEndCorpusValue.
     * 
     * @return priorFYEndCorpusValue
     */
    public KualiDecimal getPriorFYEndCorpusValue() {
        return priorFYEndCorpusValue;
    }


    /**
     * Sets the priorFYEndCorpusValue.
     * 
     * @param priorFYEndCorpusValue
     */
    public void setPriorFYEndCorpusValue(KualiDecimal priorFYEndCorpusValue) {
        this.priorFYEndCorpusValue = priorFYEndCorpusValue;
    }


    /**
     * Gets the priorFYEndCorpusValue.
     * 
     * @return priorFYEndCorpusValue
     */
    public KualiDecimal getPriorFYEndPrincipalMarketValue() {
        return priorFYEndPrincipalMarketValue;
    }


    /**
     * Sets the priorFYEndCorpusValue.
     * 
     * @param priorFYEndPrincipalMarketValue
     */
    public void setPriorFYEndPrincipalMarketValue(KualiDecimal priorFYEndPrincipalMarketValue) {
        this.priorFYEndPrincipalMarketValue = priorFYEndPrincipalMarketValue;
    }


    /**
     * Gets the kemidObjRef.
     * 
     * @return kemidObjRef
     */
    public KEMID getKemidObjRef() {
        return kemidObjRef;
    }


    /**
     * Sets the kemidObjRef.
     * 
     * @param kemidObjRef
     */
    public void setKemidObjRef(KEMID kemidObjRef) {
        this.kemidObjRef = kemidObjRef;
    }


}
