/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.budget.web.struts.form;

import org.kuali.core.web.struts.form.LookupForm;

/**
 * This class...
 */
public class TempListLookupForm extends LookupForm {
    // holds the BC fiscal year that is currently active
    private Integer universityFiscalYear;
    private String personUniversalIdentifier;
    private String reportMode;
    private String currentPointOfViewKeyCode;
    private boolean buildControlList;
    private boolean reportConsolidation;
    // controls automatic initial display of results
    private boolean showInitialResults;
    

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute value.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the personUniversalIdentifier attribute.
     * 
     * @return Returns the personUniversalIdentifier.
     */
    public String getPersonUniversalIdentifier() {
        return personUniversalIdentifier;
    }

    /**
     * Sets the personUniversalIdentifier attribute value.
     * 
     * @param personUniversalIdentifier The personUniversalIdentifier to set.
     */
    public void setPersonUniversalIdentifier(String personUniversalIdentifier) {
        this.personUniversalIdentifier = personUniversalIdentifier;
    }

    /**
     * Gets the showInitialResults attribute.
     * 
     * @return Returns the showInitialResults.
     */
    public boolean isShowInitialResults() {
        return showInitialResults;
    }

    /**
     * Sets the showInitialResults attribute value.
     * 
     * @param showInitialResults The showInitialResults to set.
     */
    public void setShowInitialResults(boolean showInitialResults) {
        this.showInitialResults = showInitialResults;
    }

    public String getReportMode() {
        return reportMode;
    }

    public void setReportMode(String reportMode) {
        this.reportMode = reportMode;
    }

    public String getCurrentPointOfViewKeyCode() {
        return currentPointOfViewKeyCode;
    }

    public void setCurrentPointOfViewKeyCode(String currentPointOfViewKeyCode) {
        this.currentPointOfViewKeyCode = currentPointOfViewKeyCode;
    }

    public boolean isBuildControlList() {
        return buildControlList;
    }

    public void setBuildControlList(boolean buildControlList) {
        this.buildControlList = buildControlList;
    }

    public boolean isReportConsolidation() {
        return reportConsolidation;
    }

    public void setReportConsolidation(boolean reportConsolidation) {
        this.reportConsolidation = reportConsolidation;
    }


}
