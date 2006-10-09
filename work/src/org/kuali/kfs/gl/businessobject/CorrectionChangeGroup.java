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

package org.kuali.module.gl.bo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
 */
public class CorrectionChangeGroup extends BusinessObjectBase implements Comparable {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CorrectionChangeGroup.class);

    private String financialDocumentNumber;
    private Integer correctionChangeGroupLineNumber;
    private Integer correctionCriteriaNextLineNumber;
    private Integer correctionChangeNextLineNumber;
    private List correctionCriteria;
    private List correctionChange;

    public CorrectionChangeGroup(String financialDocumentNumber,Integer correctionChangeGroupLineNumber) {
        setCorrectionChangeGroupLineNumber(correctionChangeGroupLineNumber);

        correctionCriteria = new ArrayList();
        correctionChange = new ArrayList();
        correctionCriteriaNextLineNumber = new Integer(0);
        correctionChangeNextLineNumber = new Integer(0);

        setFinancialDocumentNumber(financialDocumentNumber);
    }

    public CorrectionChangeGroup() {
        super();
        correctionCriteria = new ArrayList();
        correctionChange = new ArrayList();
        correctionCriteriaNextLineNumber = new Integer(0);
        correctionChangeNextLineNumber = new Integer(0);
    }

    public void addCorrectionChange(CorrectionChange cc) {
        LOG.debug("addCorrectionChange() started");

        cc.setFinancialDocumentNumber(financialDocumentNumber);
        cc.setCorrectionChangeGroupLineNumber(correctionChangeGroupLineNumber);
        cc.setCorrectionChangeLineNumber(correctionChangeNextLineNumber++);
        correctionChange.add(cc);
    }

    public void addCorrectionCriteria(CorrectionCriteria cc) {
        cc.setFinancialDocumentNumber(financialDocumentNumber);
        cc.setCorrectionChangeGroupLineNumber(correctionChangeGroupLineNumber);
        cc.setCorrectionCriteriaLineNumber(correctionCriteriaNextLineNumber++);
        correctionCriteria.add(cc);
    }

    public void removeCorrectionChangeItem(int changeNumber) {
        for (Iterator iter = correctionChange.iterator(); iter.hasNext();) {
            CorrectionChange element = (CorrectionChange)iter.next();
            if ( changeNumber == element.getCorrectionChangeLineNumber().intValue() ) {
                iter.remove();
            }
        }
    }

    public void removeCorrectionCriteriaItem(int criteriaNumber) {
        for (Iterator iter = correctionCriteria.iterator(); iter.hasNext();) {
            CorrectionCriteria element = (CorrectionCriteria) iter.next();
            if ( criteriaNumber == element.getCorrectionCriteriaLineNumber().intValue() ) {
                iter.remove();
            }
        }
    }

    public CorrectionChange getCorrectionChangeItem(int changeNumber) {
        for (Iterator iter = correctionChange.iterator(); iter.hasNext();) {
            CorrectionChange element = (CorrectionChange)iter.next();
            if ( changeNumber == element.getCorrectionChangeLineNumber().intValue() ) {
                return element;
            }
        }

        CorrectionChange cc = new CorrectionChange(getFinancialDocumentNumber(),correctionChangeGroupLineNumber,changeNumber);
        correctionChange.add(cc);
        
        return cc;
    }

    public CorrectionCriteria getCorrectionCriteriaItem(int criteriaNumber) {
        for (Iterator iter = correctionCriteria.iterator(); iter.hasNext();) {
            CorrectionCriteria element = (CorrectionCriteria) iter.next();
            if ( criteriaNumber == element.getCorrectionCriteriaLineNumber().intValue() ) {
                return element;
            }
        }

        CorrectionCriteria cc = new CorrectionCriteria(getFinancialDocumentNumber(),correctionChangeGroupLineNumber,criteriaNumber);
        correctionCriteria.add(cc);
        return cc;
    }

    public String getFinancialDocumentNumber() {
        return financialDocumentNumber;
    }

    public void setFinancialDocumentNumber(String financialDocumentNumber) {
        this.financialDocumentNumber = financialDocumentNumber;

        for (Iterator iter = correctionCriteria.iterator(); iter.hasNext();) {
            CorrectionCriteria element = (CorrectionCriteria)iter.next();
            element.setFinancialDocumentNumber(financialDocumentNumber);
        }
        for (Iterator iter = correctionChange.iterator(); iter.hasNext();) {
            CorrectionChange element = (CorrectionChange)iter.next();
            element.setFinancialDocumentNumber(financialDocumentNumber);
        }
    }

    public Integer getCorrectionChangeGroupLineNumber() {
        return correctionChangeGroupLineNumber;
    }

    public void setCorrectionChangeGroupLineNumber(Integer correctionChangeGroupLineNumber) {
        this.correctionChangeGroupLineNumber = correctionChangeGroupLineNumber;
    }

    public Integer getCorrectionCriteriaNextLineNumber() {
        return correctionCriteriaNextLineNumber;
    }

    public void setCorrectionCriteriaNextLineNumber(Integer correctionCriteriaNextLineNumber) {
        this.correctionCriteriaNextLineNumber = correctionCriteriaNextLineNumber;
    }

    public Integer getCorrectionChangeNextLineNumber() {
        return correctionChangeNextLineNumber;
    }

    public void setCorrectionChangeNextLineNumber(Integer correctionChangeNextLineNumber) {
        this.correctionChangeNextLineNumber = correctionChangeNextLineNumber;
    }

    public List getCorrectionCriteria() {
        Collections.sort(correctionCriteria);
        return correctionCriteria;
    }

    public void setCorrectionCriteria(List correctionCriteria) {
        this.correctionCriteria = correctionCriteria;
    }

    public List getCorrectionChange() {
        Collections.sort(correctionChange);
        return correctionChange;
    }

    public void setCorrectionChange(List correctionChange) {
        this.correctionChange = correctionChange;
    }

    public int compareTo(Object o) {
        CorrectionChangeGroup other = (CorrectionChangeGroup)o;

        String thisFdocNbr = financialDocumentNumber == null ? "" : financialDocumentNumber;
        String thatFdocNbr = other.financialDocumentNumber == null ? "" : other.financialDocumentNumber;

        int c = thisFdocNbr.compareTo(thatFdocNbr);
        if ( c == 0 ) {
            Integer thisNbr = correctionChangeGroupLineNumber == null ? 0 : correctionChangeGroupLineNumber;
            Integer thatNbr = other.correctionChangeGroupLineNumber == null ? 0 : other.correctionChangeGroupLineNumber;
            return thisNbr.compareTo(thatNbr);
        } else {
            return c;
        }
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialDocumentNumber", this.financialDocumentNumber);
        if (this.correctionChangeGroupLineNumber != null) {
            m.put("correctionChangeGroupLineNumber", this.correctionChangeGroupLineNumber.toString());
        }
        return m;
    }
}
