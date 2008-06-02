/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.budget.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kuali.module.budget.BCConstants.Report.BuildMode;
import org.kuali.module.budget.bo.BudgetConstructionPullup;

/**
 * Holds information on the current build configuration of the report control list table, and a requested configuration.
 */
public class ReportControlListBuildHelper {
    private BuildState currentState;
    private BuildState requestedState;

    /**
     * Constructs a ReportControlListBuildHelper.java.
     */
    public ReportControlListBuildHelper() {
        super();
    }

    /**
     * Determines whether a build is needed based on the current and requested states
     * 
     * @return true if build is needed, false otherwise
     */
    public boolean isBuildNeeded() {
        boolean buildNeeded = false;

        if (this.getCurrentState() == null && this.getRequestedState() != null) {
            buildNeeded = true;
        }
        else if (this.getRequestedState() == null) {
            buildNeeded = false;
        }
        else if (!this.getCurrentState().equals(this.getRequestedState())) {
            buildNeeded = true;
        }

        return buildNeeded;
    }

    /**
     * Helper method to add a requestState
     * 
     * @param pointOfView - request point of view
     * @param selectedOrganizations - organizations selected for the report
     * @param buildMode - mode in which the control list should be build
     * @see org.kuali.module.budget.BCConstants.Report.BuildMode
     */
    public void addBuildRequest(String pointOfView, Collection<BudgetConstructionPullup> selectedOrganizations, BuildMode buildMode) {
        this.setRequestedState(new BuildState(pointOfView, selectedOrganizations, buildMode));
    }

    /**
     * Called when the control list has been built that satisfieds the requested state. Will set current state to the requested
     * state then empty the requested state.
     */
    public void requestBuildComplete() {
        if (this.getRequestedState() != null) {
            if (this.getCurrentState() == null) {
                this.setCurrentState(new BuildState());
            }

            this.getCurrentState().setPointOfView(this.getRequestedState().getPointOfView());
            this.getCurrentState().setSelectedOrganizations(this.getRequestedState().getSelectedOrganizations());
            this.getCurrentState().setBuildMode(this.getRequestedState().getBuildMode());

            this.setRequestedState(null);
        }
        else {
            throw new RuntimeException("Requested state does not exist. Control list build state has been lost.");
        }
    }

    /**
     * Represents a build configuration for the control list.
     */
    public class BuildState {
        private String pointOfView;
        private Collection<BudgetConstructionPullup> selectedOrganizations;
        private BuildMode buildMode;

        /**
         * Constructs a ReportControlListBuildHelper.java.
         */
        public BuildState() {
        }

        /**
         * Constructs a ReportControlListBuildHelper.java.
         * 
         * @param pointOfView - chart/org point of view string
         * @param selectedOrganizations - organizations selected for reporting
         * @param buildMode - mode for restricting report data
         */
        public BuildState(String pointOfView, Collection<BudgetConstructionPullup> selectedOrganizations, BuildMode buildMode) {
            this.pointOfView = pointOfView;
            this.selectedOrganizations = selectedOrganizations;
            this.buildMode = buildMode;
        }

        /**
         * Gets the buildMode attribute.
         * 
         * @return Returns the buildMode.
         */
        public BuildMode getBuildMode() {
            return buildMode;
        }

        /**
         * Sets the buildMode attribute value.
         * 
         * @param buildMode The buildMode to set.
         */
        public void setBuildMode(BuildMode buildMode) {
            this.buildMode = buildMode;
        }

        /**
         * Gets the pointOfView attribute.
         * 
         * @return Returns the pointOfView.
         */
        public String getPointOfView() {
            return pointOfView;
        }

        /**
         * Sets the pointOfView attribute value.
         * 
         * @param pointOfView The pointOfView to set.
         */
        public void setPointOfView(String pointOfView) {
            this.pointOfView = pointOfView;
        }

        /**
         * Gets the selectedOrganizations attribute.
         * 
         * @return Returns the selectedOrganizations.
         */
        public Collection<BudgetConstructionPullup> getSelectedOrganizations() {
            return selectedOrganizations;
        }

        /**
         * Sets the selectedOrganizations attribute value.
         * 
         * @param selectedOrganizations The selectedOrganizations to set.
         */
        public void setSelectedOrganizations(Collection<BudgetConstructionPullup> selectedOrganizations) {
            this.selectedOrganizations = selectedOrganizations;
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof BuildState) {
                boolean isEqual = true;

                if (obj == null) {
                    isEqual = false;
                }
                else {
                    BuildState compareState = (BuildState) obj;

                    if (!this.getPointOfView().equals(compareState.getPointOfView())) {
                        isEqual = false;
                    }

                    if (!this.compareOrganizations(this.getSelectedOrganizations(), compareState.getSelectedOrganizations())) {
                        isEqual = false;
                    }

                    if (!this.getBuildMode().equals(compareState.getBuildMode())) {
                        isEqual = false;
                    }
                }

                return isEqual;
            }
            else {
                return super.equals(obj);
            }
        }

        /**
         * Compares two collections of BudgetConstructionPullup objects for equality. BudgetConstructionPullup objects are compared
         * by primary key.
         * 
         * @param currentOrgs - current org build
         * @param requestedOrgs - requested org build
         * @return boolean indicating true if the collections are equal, false otherwise
         */
        private boolean compareOrganizations(Collection<BudgetConstructionPullup> currentOrgs, Collection<BudgetConstructionPullup> requestedOrgs) {
            Set<String> currentOrgSet = new HashSet<String>();
            for (BudgetConstructionPullup pullup : currentOrgs) {
                currentOrgSet.add(pullup.getChartOfAccountsCode() + pullup.getOrganizationCode());
            }
            
            for (BudgetConstructionPullup pullup : requestedOrgs) {
                if (!currentOrgSet.contains(pullup.getChartOfAccountsCode() + pullup.getOrganizationCode())) {
                    return false;
                }
                currentOrgSet.remove(pullup.getChartOfAccountsCode() + pullup.getOrganizationCode());
            }
            
            if (!currentOrgSet.isEmpty()) {
                return false;
            }
            
            return true;
        }
    }

    /**
     * Gets the currentState attribute.
     * 
     * @return Returns the currentState.
     */
    public BuildState getCurrentState() {
        return currentState;
    }

    /**
     * Sets the currentState attribute value.
     * 
     * @param currentState The currentState to set.
     */
    public void setCurrentState(BuildState currentState) {
        this.currentState = currentState;
    }

    /**
     * Gets the requestedState attribute.
     * 
     * @return Returns the requestedState.
     */
    public BuildState getRequestedState() {
        return requestedState;
    }

    /**
     * Sets the requestedState attribute value.
     * 
     * @param requestedState The requestedState to set.
     */
    public void setRequestedState(BuildState requestedState) {
        this.requestedState = requestedState;
    }

}
