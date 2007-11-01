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
package org.kuali.workflow.attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.RuleExtension;
import edu.iu.uis.eden.routetemplate.RuleExtensionValue;

public class KualiResearchRiskAttribute implements WorkflowAttribute {

    private final static String PROPOSAL_RESEARCH_RISKS = "proposalResearchRisks";
    private boolean required = false;
    private String researchRiskTypeCode;

    public String getDocContent() {
        // TODO Auto-generated method stub
        /*
         * <newMaintainableObject class="org.kuali.module.cg.maintenance.AwardMaintainableImpl"> <businessObject
         * class="org.kuali.module.cg.bo.Award"> <proposalNumber>81159</proposalNumber> <proposal> <proposalResearchRisks
         * class="org.apache.ojb.broker.util.collections.ManageableArrayList" serialization="custom"> <list> <default> <size>6</size>
         * </default> <int>10</int> <org.kuali.module.cg.bo.ProposalResearchRisk> <researchRiskTypeCode>H</researchRiskTypeCode>
         * <proposalNumber>81159</proposalNumber> <isActive>false</isActive> <proposal reference="../../../.."/>
         * <researchRiskType> <researchRiskTypeCode>H</researchRiskTypeCode> <dataObjectMaintenanceCodeActiveIndicator>true</dataObjectMaintenanceCodeActiveIndicator>
         * <researchRiskTypeDescription>Human Subjects</researchRiskTypeDescription>
         */
        return null;
    }

    public List<Row> getRoutingDataRows() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<RuleExtensionValue> getRuleExtensionValues() {
        List extensions = new ArrayList();
        extensions.add(new RuleExtensionValue(PROPOSAL_RESEARCH_RISKS, this.researchRiskTypeCode));
        return extensions;
    }

    public List<Row> getRuleRows() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isMatch(DocumentContent arg0, List<RuleExtension> arg1) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isRequired() {
        return this.required;
    }

    public void setRequired(boolean required) {
        this.required = required;

    }

    public List validateRoutingData(Map arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public List validateRuleData(Map arg0) {
        // TODO Auto-generated method stub
        return null;
    }

}
