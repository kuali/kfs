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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.module.cg.bo.Award;

import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.plugin.attributes.RoleAttribute;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.ResolvedQualifiedRole;
import edu.iu.uis.eden.routetemplate.xmlrouting.XPathHelper;
import org.kuali.kfs.util.SpringServiceLocator;


public class KualiCGAttribute implements RoleAttribute, WorkflowAttribute{
    private boolean required;
    private static XPath xpath;
    private static BusinessObjectService businessObjectService;
    
    public KualiCGAttribute(){
        xpath = XPathHelper.newXPath();
        if (businessObjectService == null) businessObjectService = SpringServiceLocator.getBusinessObjectService();
    }
    public List getRoutingDataRows() {
        return new ArrayList();
    }
    public boolean isRequired() {
        return required;
    }
    public List validateRuleData(Map paramMap) {
        return Collections.EMPTY_LIST;
    }
    public List getRuleExtensionValues() {
        return Collections.EMPTY_LIST;
    }
    public void setRequired(boolean required) {
        this.required = required;
    }
    public List getRuleRows() {
        return Collections.EMPTY_LIST;
    }
    public List validateRoutingData(Map paramMap) {
        return Collections.EMPTY_LIST;
    }
    public boolean isMatch(DocumentContent documentContent, List rules) {
        try{
            //Select the class attribute of the document element to determine the doc type
            String docType = (String) xpath.evaluate("//document/attribute::class", documentContent.getDocument(), XPathConstants.STRING);
        
            if (Class.forName("org.kuali.kfs.document.AccountingDocumentBase").isAssignableFrom(Class.forName(docType))){
                return true;
            }else{
                return false;
            }
        }catch (XPathException xpe){
            throw new RuntimeException ("XPath failed "+xpe, xpe);
        }catch (NullPointerException npe){
            throw new RuntimeException (npe);
        }catch (ClassNotFoundException cnfe){
            return false; //Some docs, like maint docs, don't have a document element.
        }   
    }
    public String getDocContent() {
        return "";
    }
    public List getQualifiedRoleNames(String roleName, DocumentContent docContent) throws EdenUserNotFoundException {
        return new ArrayList();
    }
    public ResolvedQualifiedRole resolveQualifiedRole(RouteContext context, String roleName, String qualifiedRole) throws EdenUserNotFoundException {
        return new ResolvedQualifiedRole();
    }
    public List getRoleNames() {
        return new ArrayList();
    }
    private List routingWorkgroupNames (String chart, String account){
        Map queryMap = new HashMap();
        queryMap.put("awardAccounts.chartOfAccountsCode", chart);
        queryMap.put("awardAccounts.accountNumber", account);
        List <String> workgroupList  = new ArrayList();
        try{
            for (Object award : businessObjectService.findMatching(Class.forName("org.kuali.module.cg.bo.Award"), queryMap)){
                workgroupList.add(((Award)award).getWorkgroupName());
            }
        }catch (ClassNotFoundException cnfe){
            throw new RuntimeException ("Award or AwardAccount BO went away!!!", cnfe);
        }
        return workgroupList;
    }
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
