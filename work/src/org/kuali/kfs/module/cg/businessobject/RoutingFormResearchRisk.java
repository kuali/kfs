/*
 * Copyright 2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/businessobject/RoutingFormResearchRisk.java,v $
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

package org.kuali.module.kra.routingform.bo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.module.kra.budget.bo.BudgetThirdPartyCostShare;
import org.kuali.PropertyConstants;

/**
 * 
 */
public class RoutingFormResearchRisk extends BusinessObjectBase {

	private String documentNumber;
	private String researchRiskTypeCode;
	private String researchRiskDescription;
	private String dataPresentIndicator;

    private ResearchRiskType researchRiskType;
    private RoutingFormResearchRiskStudy newResearchRiskStudy;
    
    private List<RoutingFormResearchRiskStudy> researchRiskStudies;
    
	/**
	 * Default constructor.
	 */
	public RoutingFormResearchRisk() {
        newResearchRiskStudy = new RoutingFormResearchRiskStudy();
	    researchRiskStudies = new ArrayList<RoutingFormResearchRiskStudy>();
	}
    
    public RoutingFormResearchRisk(String documentNumber, ResearchRiskType researchRiskType) {
        super();
        this.documentNumber = documentNumber;
        this.researchRiskTypeCode = researchRiskType.getResearchRiskTypeCode();
        this.researchRiskType = researchRiskType;
    }

	/**
	 * Gets the documentNumber attribute.
	 * 
	 * @return Returns the documentNumber
	 * 
	 */
	public String getDocumentNumber() { 
		return documentNumber;
	}

	/**
	 * Sets the documentNumber attribute.
	 * 
	 * @param documentNumber The documentNumber to set.
	 * 
	 */
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}


	/**
	 * Gets the researchRiskTypeCode attribute.
	 * 
	 * @return Returns the researchRiskTypeCode
	 * 
	 */
	public String getResearchRiskTypeCode() { 
		return researchRiskTypeCode;
	}

	/**
	 * Sets the researchRiskTypeCode attribute.
	 * 
	 * @param researchRiskTypeCode The researchRiskTypeCode to set.
	 * 
	 */
	public void setResearchRiskTypeCode(String researchRiskTypeCode) {
		this.researchRiskTypeCode = researchRiskTypeCode;
	}


	/**
	 * Gets the researchRiskDescription attribute.
	 * 
	 * @return Returns the researchRiskDescription
	 * 
	 */
	public String getResearchRiskDescription() { 
		return researchRiskDescription;
	}

	/**
	 * Sets the researchRiskDescription attribute.
	 * 
	 * @param researchRiskDescription The researchRiskDescription to set.
	 * 
	 */
	public void setResearchRiskDescription(String researchRiskDescription) {
		this.researchRiskDescription = researchRiskDescription;
	}


	/**
	 * Gets the dataPresentIndicator attribute.
	 * 
	 * @return Returns the dataPresentIndicator
	 * 
	 */
	public String getDataPresentIndicator() { 
		return dataPresentIndicator;
	}

	/**
	 * Sets the dataPresentIndicator attribute.
	 * 
	 * @param dataPresentIndicator The dataPresentIndicator to set.
	 * 
	 */
	public void setDataPresentIndicator(String dataPresentIndicator) {
		this.dataPresentIndicator = dataPresentIndicator;
	}

    /**
     * Gets the researchRiskType attribute. 
     * @return Returns the researchRiskType.
     */
    public ResearchRiskType getResearchRiskType() {
        return researchRiskType;
    }

    /**
     * Sets the researchRiskType attribute value.
     * @param researchRiskType The researchRiskType to set.
     * @deprecated
     */
    public void setResearchRiskType(ResearchRiskType researchRiskType) {
        this.researchRiskType = researchRiskType;
    }  
    
    /**
     * Gets the researchRiskStudies attribute.
     * 
     * @return Returns the researchRiskStudies.
     */
	public List<RoutingFormResearchRiskStudy> getResearchRiskStudies() {
        return researchRiskStudies;
    }
    
    public RoutingFormResearchRiskStudy getResearchRiskStudy(int index) {
        while (getResearchRiskStudies().size() <= index) {
            getResearchRiskStudies().add(new RoutingFormResearchRiskStudy());
        }
        return (RoutingFormResearchRiskStudy) getResearchRiskStudies().get(index);
    }
    
    public void addNewResearchRiskStudyToList() {
        // set the sequence number
        if (this.researchRiskStudies.isEmpty()) {
            this.newResearchRiskStudy.setRoutingFormResearchRiskStudySequenceNumber(new Integer(1));
        } else {
            this.newResearchRiskStudy.setRoutingFormResearchRiskStudySequenceNumber(
                    this.researchRiskStudies.get(this.researchRiskStudies.size() - 1).getRoutingFormResearchRiskStudySequenceNumber() + 1);
        }
        this.researchRiskStudies.add(this.newResearchRiskStudy);
        this.newResearchRiskStudy = new RoutingFormResearchRiskStudy();
    }

    /**
     * Sets the researchRiskStudies attribute.
     * 
     * @param researchRiskStudies The researchRiskStudies to set.
     */
    public void setResearchRiskStudies(List<RoutingFormResearchRiskStudy> researchRiskStudies) {
        this.researchRiskStudies = researchRiskStudies;
    }
    
    public RoutingFormResearchRiskStudy getNewResearchRiskStudy() {
        return newResearchRiskStudy;
    }

    public void setNewResearchRiskStudy(RoutingFormResearchRiskStudy newResearchRiskStudy) {
        this.newResearchRiskStudy = newResearchRiskStudy;
    }
    
    /**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put(PropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put("researchRiskTypeCode", this.researchRiskTypeCode);
	    return m;
    }
}
