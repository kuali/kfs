/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.workflow.module.kra.attribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.xpath.XPath;

import org.apache.commons.lang.StringUtils;
import org.kuali.workflow.KualiWorkflowUtils;

import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routetemplate.AbstractRoleAttribute;
import edu.iu.uis.eden.routetemplate.ResolvedQualifiedRole;
import edu.iu.uis.eden.routetemplate.Role;
import edu.iu.uis.eden.user.WorkflowUserId;

public class ResearchProjectDirectorRoleAttribute extends AbstractRoleAttribute {
    
    private static final String PROJECT_DIRECTOR_ROLE_KEY = "PROJECT_DIRECTOR";
    private static final String PROJECT_DIRECTOR_ROLE_LABEL = "Project Director";
    private static final Role ROLE = new Role(ResearchProjectDirectorRoleAttribute.class, PROJECT_DIRECTOR_ROLE_KEY, PROJECT_DIRECTOR_ROLE_LABEL);
    
    private static final List ROLES;
    static {
        ArrayList list = new ArrayList(0);
        list.add(ROLE);
        ROLES = Collections.unmodifiableList(list);
    }

    private static final List QUALIFIED_ROLE_NAMES;
    static {
        ArrayList list = new ArrayList(0);
        list.add(PROJECT_DIRECTOR_ROLE_KEY);
        QUALIFIED_ROLE_NAMES = Collections.unmodifiableList(list);
    }
    
    public List getRoleNames() {
        return ROLES;
    }

    public List getQualifiedRoleNames(String roleName, DocumentContent documentContent) throws EdenUserNotFoundException {
        // deferring all "logic" to the resolve stage
        return QUALIFIED_ROLE_NAMES;
    }
    
    public ResolvedQualifiedRole resolveQualifiedRole(RouteContext routeContext, String roleName, String qualifiedRole) throws EdenUserNotFoundException {
        if (!PROJECT_DIRECTOR_ROLE_KEY.equals(roleName)) {
            throw new IllegalArgumentException("resolveQualifiedRole was called with a role name other than '" + PROJECT_DIRECTOR_ROLE_KEY + "'");
        }
        
        List members = new ArrayList();
        try {
            DocumentContent docContent = routeContext.getDocumentContent();
            DocumentRouteHeaderValue document = routeContext.getDocument();
            XPath xpath = KualiWorkflowUtils.getXPath(docContent.getDocument());
            String xpathExp = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(
                    KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append("projectDirector").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
            String projectDirector = xpath.evaluate(xpathExp, docContent.getDocument());
            if (!StringUtils.isBlank(projectDirector)) {
                members.add(new WorkflowUserId(projectDirector));
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        return new ResolvedQualifiedRole(PROJECT_DIRECTOR_ROLE_KEY, members);
    }
}
