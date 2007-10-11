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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPath;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.kuali.workflow.KualiWorkflowUtils;

import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.plugin.attributes.RoleAttribute;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.ResolvedQualifiedRole;
import edu.iu.uis.eden.routetemplate.Role;
import edu.iu.uis.eden.routetemplate.RuleExtension;
import edu.iu.uis.eden.routetemplate.RuleExtensionValue;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.workgroup.GroupId;
import edu.iu.uis.eden.workgroup.GroupNameId;

public class KualiVerificationWorkgroupAttribute implements RoleAttribute, WorkflowAttribute {
   
    private static final String ROLE_STRING_DELIMITER = "~!~!~";
    
    private boolean required = false;
    
    private String verificationWorkgoupName;
        
    public String getVerificationWorkgoupName() {
        return verificationWorkgoupName;
    }

    public void setVerificationWorkgoupName(String verificationWorkgoupName) {
        this.verificationWorkgoupName = verificationWorkgoupName;
    }

    public List getQualifiedRoleNames(String roleName, DocumentContent docContent) throws EdenUserNotFoundException {
        Set verificationWorkgroups = new HashSet();
        XPath xpath = KualiWorkflowUtils.getXPath(docContent.getDocument());
        String docTypeName = docContent.getRouteContext().getDocument().getDocumentType().getName();
        List qualifiedRoleNames = new ArrayList();
        try {
                String verificationWorkgroupName = xpath.evaluate(KualiWorkflowUtils.xstreamSafeXPath(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX  + "cashReceiptHeader/workgroupName"), docContent.getRouteContext().getDocumentContent().getDocContent());
                VerificationWorkgroupRole role = new VerificationWorkgroupRole(roleName);
                role.verificationWorkgroupName = verificationWorkgroupName;
                
                verificationWorkgroups.add(role);
            
                qualifiedRoleNames.add(getQualifiedRoleString(role));
                  
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return qualifiedRoleNames;
    }

    private String getQualifiedRoleString(VerificationWorkgroupRole role) {
        return new StringBuffer(getNullSafeValue(role.roleName)).append(ROLE_STRING_DELIMITER).append(getNullSafeValue(role.verificationWorkgroupName)).toString();
    }
    
    private static String getNullSafeValue(String value) {
        return (value == null ? "" : value);
    }
    public List getRoleNames() {
        List roles = new ArrayList();
        roles.add(new Role(this.getClass(), "VERIFICATION_WORGROUP_FROM_DOCUMENT", "Verification Workgroup from Document"));

        return roles;
    }

    public ResolvedQualifiedRole resolveQualifiedRole(RouteContext context, String roleName, String qualifiedRole) throws EdenUserNotFoundException {
        try {
            List members = new ArrayList();
            String annotation = "";
            VerificationWorkgroupRole role = getUnqualifiedVerificationWorkgroupRole(qualifiedRole);
            annotation = (role.verificationWorkgroupName == null ? "" : "Routing to workgroup named " + role.verificationWorkgroupName );
            GroupId verificationWorkgroupId = new GroupNameId(role.verificationWorkgroupName);
            if (verificationWorkgroupId != null) {
                members.add(verificationWorkgroupId);
            }
            return new ResolvedQualifiedRole(roleName, members, annotation);
        }
        catch (Exception e) {
            throw new RuntimeException("KualiVerificationWorkgroupAttribute encountered exception while attempting to resolve qualified role", e);
        }
    }
    
    private static VerificationWorkgroupRole getUnqualifiedVerificationWorkgroupRole(String qualifiedRole) {
        String[] values = qualifiedRole.split(ROLE_STRING_DELIMITER, -1);
        if (values.length != 2) {
            throw new RuntimeException("Invalid qualifiedRole, expected 2 encoded values: " + qualifiedRole);
        }
        VerificationWorkgroupRole role = new VerificationWorkgroupRole(values[0]);
        role.verificationWorkgroupName = getNullableString(values[1]);
        return role;
    }
    
    private static String getNullableString(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        return value;
    }
    
     
    public String getDocContent() {
        
        return new StringBuffer(KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_PREFIX).append("<verificationWorkgroupName>").append(getVerificationWorkgoupName() ).append("</verificationWorkgroupName>").append(KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_SUFFIX).toString();
    }

    public List<Row> getRoutingDataRows() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<RuleExtensionValue> getRuleExtensionValues() {
        
        return Collections.EMPTY_LIST;
    }

    public List<Row> getRuleRows() {
        return Collections.EMPTY_LIST;
    }

    public boolean isMatch(DocumentContent arg0, List<RuleExtension> arg1) {
        return true;
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
        return Collections.EMPTY_LIST;
    }
    private static class VerificationWorkgroupRole {
        public String roleName;

        public String verificationWorkgroupName;

        public VerificationWorkgroupRole(String roleName) {
            this.roleName = roleName;
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof VerificationWorkgroupRole) {
                return true;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(verificationWorkgroupName).hashCode();
        }

    }

}
