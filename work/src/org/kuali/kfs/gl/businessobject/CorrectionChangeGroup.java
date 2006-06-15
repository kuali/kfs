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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CorrectionChangeGroup extends BusinessObjectBase implements Comparable {

    private String financialDocumentNumber;
    private Integer correctionChangeGroupLineNumber;
    private Integer correctionCriteriaNextLineNumber;
    private Integer correctionChangeNextLineNumber;
    private List correctionCriteria;
    private List correctionChange;

    /**
     * Default constructor.
     */
    public CorrectionChangeGroup() {
        super();
        correctionCriteria = new ArrayList();
        correctionChange = new ArrayList();
        correctionCriteriaNextLineNumber = new Integer(0);
        correctionChangeNextLineNumber = new Integer(0);
    }

    /**
     * Gets the financialDocumentNumber attribute.
     * 
     * @return - Returns the financialDocumentNumber
     * 
     */
    public String getFinancialDocumentNumber() {
        return financialDocumentNumber;
    }

    /**
     * Sets the financialDocumentNumber attribute.
     * 
     * @param financialDocumentNumber The financialDocumentNumber to set.
     * 
     */
    public void setFinancialDocumentNumber(String financialDocumentNumber) {
        this.financialDocumentNumber = financialDocumentNumber;
    }


    /**
     * Gets the correctionChangeGroupLineNumber attribute.
     * 
     * @return - Returns the correctionChangeGroupLineNumber
     * 
     */
    public Integer getCorrectionChangeGroupLineNumber() {
        return correctionChangeGroupLineNumber;
    }

    /**
     * Sets the correctionChangeGroupLineNumber attribute.
     * 
     * @param correctionChangeGroupLineNumber The correctionChangeGroupLineNumber to set.
     * 
     */
    public void setCorrectionChangeGroupLineNumber(Integer correctionChangeGroupLineNumber) {
        this.correctionChangeGroupLineNumber = correctionChangeGroupLineNumber;
    }


    /**
     * Gets the correctionCriteriaNextLineNumber attribute.
     * 
     * @return - Returns the correctionCriteriaNextLineNumber
     * 
     */
    public Integer getCorrectionCriteriaNextLineNumber() {
        return correctionCriteriaNextLineNumber;
    }

    /**
     * Sets the correctionCriteriaNextLineNumber attribute.
     * 
     * @param correctionCriteriaNextLineNumber The correctionCriteriaNextLineNumber to set.
     * 
     */
    public void setCorrectionCriteriaNextLineNumber(Integer correctionCriteriaNextLineNumber) {
        this.correctionCriteriaNextLineNumber = correctionCriteriaNextLineNumber;
    }


    /**
     * Gets the correctionChangeNextLineNumber attribute.
     * 
     * @return - Returns the correctionChangeNextLineNumber
     * 
     */
    public Integer getCorrectionChangeNextLineNumber() {
        return correctionChangeNextLineNumber;
    }

    /**
     * Sets the correctionChangeNextLineNumber attribute.
     * 
     * @param correctionChangeNextLineNumber The correctionChangeNextLineNumber to set.
     * 
     */
    public void setCorrectionChangeNextLineNumber(Integer correctionChangeNextLineNumber) {
        this.correctionChangeNextLineNumber = correctionChangeNextLineNumber;
    }


    /**
     * Gets the correctionCriteria list.
     * 
     * @return - Returns the correctionCriteria list
     * 
     */
    public List getCorrectionCriteria() {
        return correctionCriteria;
    }

    /**
     * Sets the correctionCriteria list.
     * 
     * @param correctionCriteria The correctionCriteria list to set.
     * 
     */
    public void setCorrectionCriteria(List correctionCriteria) {
        this.correctionCriteria = correctionCriteria;
    }

    /**
     * Gets the correctionChange attribute.
     * 
     * @return Returns the correctionChange.
     */
    public List getCorrectionChange() {
        return correctionChange;
    }

    /**
     * Sets the correctionChange attribute value.
     * 
     * @param correctionChange The correctionChange to set.
     */
    public void setCorrectionChange(List correctionChange) {
        this.correctionChange = correctionChange;
    }

    /**
     * 
     * @param specification
     */
    public void addReplacementSpecification(CorrectionChange specification) {
        specification.setCorrectionChangeGroupLineNumber(getCorrectionChangeGroupLineNumber());
        specification.setFinancialDocumentNumber(getFinancialDocumentNumber());

        specification.setCorrectionChangeLineNumber(getCorrectionChangeNextLineNumber());
        this.correctionChangeNextLineNumber = new Integer(getCorrectionChangeNextLineNumber().intValue() + 1);

        this.correctionChange.add(specification);
    }

    /**
     * 
     * @param specificationNumber
     * @return
     */
    public CorrectionChange getReplacementSpecification(Integer specificationNumber) {
        CorrectionChange selected = null;
        for (Iterator i = getCorrectionChange().iterator(); i.hasNext();) {
            CorrectionChange specification = (CorrectionChange) i.next();
            if (specificationNumber.equals(specification.getCorrectionChangeLineNumber())) {
                selected = specification;
                break;
            }
        }
        return selected;
    }

    /**
     * 
     * @param searchCriterion
     */
    public void addSearchCriterion(CorrectionCriteria searchCriterion) {
        searchCriterion.setCorrectionChangeGroupLineNumber(getCorrectionChangeGroupLineNumber());
        searchCriterion.setFinancialDocumentNumber(getFinancialDocumentNumber());

        searchCriterion.setCorrectionCriteriaLineNumber(getCorrectionCriteriaNextLineNumber());
        this.correctionCriteriaNextLineNumber = new Integer(getCorrectionCriteriaNextLineNumber().intValue() + 1);

        this.correctionCriteria.add(searchCriterion);
    }

    /**
     * 
     * @param criterionNumber
     * @return
     */
    public CorrectionCriteria getSearchCriterion(Integer criterionNumber) {
        CorrectionCriteria selected = null;
        for (Iterator i = getCorrectionCriteria().iterator(); i.hasNext();) {
            CorrectionCriteria criterion = (CorrectionCriteria) i.next();
            if (criterionNumber.equals(criterion.getCorrectionCriteriaLineNumber())) {
                selected = criterion;
                break;
            }
        }
        return selected;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        CorrectionChangeGroup other = (CorrectionChangeGroup) o;

        int c = getCorrectionChangeGroupLineNumber().compareTo(other.getCorrectionChangeGroupLineNumber());

        if (0 != c) {
            return c;
        }

        return getFinancialDocumentNumber().compareTo(other.getFinancialDocumentNumber());
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
