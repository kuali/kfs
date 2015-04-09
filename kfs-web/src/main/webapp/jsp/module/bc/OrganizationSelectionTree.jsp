<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<script language="JavaScript" type="text/javascript" src="scripts/module/bc/organizationSelectionTree.js"></script>

<c:set var="pointOfViewOrgAttributes" value="${DataDictionary.BudgetConstructionOrganizationReports.attributes}" />
<c:set var="pullupOrgAttributes" value="${DataDictionary.BudgetConstructionPullup.attributes}" />
<c:set var="organizationAttributes" value="${DataDictionary.Organization.attributes}" />

<kul:page showDocumentInfo="false"
	htmlFormAction="budgetOrganizationSelectionTree" renderMultipart="true"
	docTitle="Organization Selection"
    transactionalDocument="false" showTabButtons="true">
    
    <!--[if IE]> 
      <style>
        #workarea div.tab-container {
          width:100%;
        }
      </style> 
    <![endif]-->
    	    
    <kul:errors keyMatch="pointOfViewOrg" errorTitle="Errors found in Organization Selection:" />
    <c:forEach items="${KualiForm.messages}" var="message">
	   ${message}
	</c:forEach>

	<br/><br/>	

	<kul:tabTop tabTitle="${KualiForm.operatingModeTitle}" defaultOpen="true" tabErrorKey="orgSel,selectionSubTreeOrgs">
	<div class="tab-container" align=center>
		<bc:budgetConstructionOrgSelection />
	</div>
	</kul:tabTop>
	
	<c:if test="${!empty KualiForm.selectionSubTreeOrgs}">		
		<c:if test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.REPORTS}">
			<bc:budgetConstructionOrgSelectionReport />
		</c:if>
		
	    <c:if test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.ACCOUNT}">
			<bc:budgetConstructionOrgSelectionAccount />
		</c:if>
		
	    <c:if test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.SALSET}">
			<bc:budgetConstructionOrgSelectionSalset />
		</c:if>
		
		<c:if test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.PULLUP or KualiForm.operatingMode == BCConstants.OrgSelOpMode.PUSHDOWN}">    
			<bc:budgetConstructionOrgSelectionPushOrPull />     
		</c:if>       
    </c:if>

	<kul:panelFooter/>
	
    <div id="globalbuttons" class="globalbuttons">
        <c:if test="${!empty KualiForm.selectionSubTreeOrgs && KualiForm.operatingMode == BCConstants.OrgSelOpMode.PULLUP}">
             <html:image property="methodToCall.performPullUp" src="${ConfigProperties.externalizable.images.url}buttonsmall_pullup.gif" title="Perform Pullup" alt="Perform Pullup" styleClass="globalbuttons" />
        </c:if>
        
        <c:if test="${!empty KualiForm.selectionSubTreeOrgs && KualiForm.operatingMode == BCConstants.OrgSelOpMode.PUSHDOWN}">
             <html:image property="methodToCall.performPushDown" src="${ConfigProperties.externalizable.images.url}buttonsmall_pushdown.gif" title="Perform Pushdown" alt="Perform Pushdown" styleClass="globalbuttons" />
        </c:if>
        
        <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif" styleClass="globalbuttons" property="methodToCall.returnToCaller" title="close" alt="close"/>
    </div>
</kul:page>
