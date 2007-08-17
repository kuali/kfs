<%--
 Copyright 2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<script language="JavaScript" type="text/javascript" src="scripts/budget/organizationSelectionTree.js"></script>

<c:set var="pointOfViewOrgAttributes" value="${DataDictionary.BudgetConstructionOrganizationReports.attributes}" />
<c:set var="pullupOrgAttributes" value="${DataDictionary.BudgetConstructionPullup.attributes}" />
<c:set var="organizationAttributes" value="${DataDictionary.Org.attributes}" />

<kul:page showDocumentInfo="false"
	htmlFormAction="budgetOrganizationSelectionTree" renderMultipart="true"
	docTitle="Organization Selection"
    transactionalDocument="false">

<%--	<kul:hiddenDocumentFields /> --%>
	<html-el:hidden name="KualiForm" property="returnAnchor" />
	<html-el:hidden name="KualiForm" property="returnFormKey" />
	<html-el:hidden name="KualiForm" property="operatingMode" />
	
    <kul:errors errorTitle="Errors found in Organization Selection:" />
    <kul:messages/>

    <table align="center" cellpadding="0" cellspacing="0" class="datatable-100">
        <tr>
            <th class="grid" colspan="6" align="left">
                <br>
                <html:hidden property="operatingModeTitle" value="${KualiForm.operatingModeTitle}" />
                ${KualiForm.operatingModeTitle}
                <br><br>
		    </th>
        </tr>
	    <tr>
            <%--point of view header --%>
            <th class="grid" colspan="1" rowspan="1">&nbsp;</th>
		    <th class="grid" align="left" colspan="5"><br>Current Point Of View Organization</th>
	    </tr>
	    <tr>
            <%-- point of view data line --%>
            <%-- first cell in row above spans two rows
                             <span class="nowrap">
                </span>
            <div align="right">
            </div>
            --%>
            <td class="grid" valign="center" rowspan="1" colspan="1">
            <div align="right">
                <html:hidden property="previousPointOfViewKeyCode" value="${KualiForm.currentPointOfViewKeyCode}" />
                <kul:htmlControlAttribute
                     property="currentPointOfViewKeyCode"
                     attributeEntry="${pointOfViewOrgAttributes.selectionKeyCode}"
                     onchange="refreshPointOfView(this.form)"
                     readOnly="false"
                     styleClass="grid" />
                <html:image property="methodToCall.performBuildPointOfView" src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_refresh.gif" alt="refresh" title="refresh" styleClass="tinybutton"/>
            </div>
            </td>
            <td class="grid" valign="center" rowspan="1" colspan="1">
            <kul:htmlControlAttribute
                property="pointOfViewOrg.chartOfAccountsCode"
                attributeEntry="${pointOfViewOrgAttributes.chartOfAccountsCode}"
                readOnly="true"
                readOnlyBody="true">
                <kul:inquiry
                    boClassName="org.kuali.module.chart.bo.Chart"
                    keyValues="chartOfAccountsCode=${pointOfViewOrg.chartOfAccountsCode}"
                    render="${!empty KualiForm.pointOfViewOrg.chartOfAccountsCode}">
                	<html:hidden write="true" property="pointOfViewOrg.chartOfAccountsCode" />
                </kul:inquiry>&nbsp;
            </kul:htmlControlAttribute>
            </td>
            <td class="grid" valign="center" rowspan="1" colspan="1">
            <kul:htmlControlAttribute
                property="pointOfViewOrg.organizationCode"
                attributeEntry="${pointOfViewOrgAttributes.organizationCode}"
                readOnly="true"
                readOnlyBody="true">
                <kul:inquiry
                    boClassName="org.kuali.module.chart.bo.Org"
                    keyValues="chartOfAccountsCode=${KualiForm.pointOfViewOrg.chartOfAccountsCode}&amp;organizationCode=${KualiForm.pointOfViewOrg.organizationCode}"
                    render="${!empty KualiForm.pointOfViewOrg.organizationCode}">
                	<html:hidden write="true" property="pointOfViewOrg.organizationCode" />
                </kul:inquiry>&nbsp;
	      	</kul:htmlControlAttribute>
            </td>
            <td class="grid" valign="center" rowspan="1" colspan="3">
            <kul:htmlControlAttribute
                property="pointOfViewOrg.organization.organizationName"
                attributeEntry="${organizationAttributes.organizationName}"
                readOnly="true"/>&nbsp;
            </td>
	    </tr>

        <c:if test="${!empty KualiForm.previousBranchOrgs}">

        <%-- previous branches header --%>
	    <tr>
            <th class="grid" colspan="1" rowspan="1">&nbsp;</th>
		    <th class="grid" align="left" colspan="5"><br>Previous Branches</th>
	    </tr>

        <%-- previous branches --%>
        <c:forEach items="${KualiForm.previousBranchOrgs}" var="item" varStatus="status" >
	    <tr>
            <td class="grid" valign="center" rowspan="1" colspan="1">
            <div align="center">
               	<html:hidden write="false" property="previousBranchOrgs[${status.index}].reportsToChartOfAccountsCode" />
               	<html:hidden write="false" property="previousBranchOrgs[${status.index}].reportsToOrganizationCode" />
                <html:image property="methodToCall.navigateUp.line${status.index}.anchorpreviousBranchOrgsAnchor${status.index}" src="${ConfigProperties.externalizable.images.url}purap-up.gif" title="Return Previous" alt="Return Previous" styleClass="tinybutton" />
            </div>
            </td>
            <td class="grid" valign="center" rowspan="1" colspan="1">
            <kul:htmlControlAttribute
                property="previousBranchOrgs[${status.index}].chartOfAccountsCode"
                attributeEntry="${pullupOrgAttributes.chartOfAccountsCode}"
                readOnly="true"
                readOnlyBody="true">
                <kul:inquiry
                    boClassName="org.kuali.module.chart.bo.Chart"
                    keyValues="chartOfAccountsCode=${item.chartOfAccountsCode}"
                    render="${!empty KualiForm.previousBranchOrgs[0].chartOfAccountsCode}">
                	<html:hidden write="true" property="previousBranchOrgs[${status.index}].chartOfAccountsCode" />
                </kul:inquiry>&nbsp;
            </kul:htmlControlAttribute>
            </td>
            <td class="grid" valign="center" rowspan="1" colspan="1">
            <kul:htmlControlAttribute
                property="previousBranchOrgs[${status.index}].organizationCode"
                attributeEntry="${pullupOrgAttributes.organizationCode}"
                readOnly="true"
                readOnlyBody="true">
                <kul:inquiry
                    boClassName="org.kuali.module.chart.bo.Org"
                    keyValues="chartOfAccountsCode=${item.chartOfAccountsCode}&amp;organizationCode=${item.organizationCode}"
                    render="${!empty KualiForm.previousBranchOrgs[0].organizationCode}">
                	<html:hidden write="true" property="previousBranchOrgs[${status.index}].organizationCode" />
                </kul:inquiry>&nbsp;
	      	</kul:htmlControlAttribute>
            </td>
            <td class="grid" valign="center" rowspan="1" colspan="2">
            <kul:htmlControlAttribute
                property="previousBranchOrgs[${status.index}].organization.organizationName"
                attributeEntry="${organizationAttributes.organizationName}"
                readOnly="true"/>&nbsp;
            </td>
            <td class="grid" valign="center" rowspan="1" colspan="1">&nbsp;</td>
	    </tr>

        </c:forEach>
        </c:if>


        <c:if test="${!empty KualiForm.selectionSubTreeOrgs}">

        <%--pullup selection header --%>
	    <tr>
            <th class="grid" colspan="1" rowspan="1">&nbsp;</th>
		    <th class="grid" align="left" colspan="4"><br>Organization Selection Sub-Tree</th>
		    <th class="grid" align="center" colspan="1"><br>Action</th>
	    </tr>

        <%--pullup selection data lines 
            <kul:htmlControlAttribute
                property="KualiForm.selectionSubTreeOrgs.(${status.index}).chartOfAccountsCode"
                attributeEntry="${pullupOrgAttributes.chartOfAccountsCode}"
                readOnly="true"
                readOnlyBody="true">
                <kul:inquiry
                    boClassName="org.kuali.module.chart.bo.Chart"
                    keyValues="chartOfAccountsCode="KualiForm.selectionSubTreeOrgs.(0).chartOfAccountsCode}"
                    render="${!empty KualiForm.selectionSubTreeOrgs.(0).chartOfAccountsCode}">
                	<html:hidden write="true" property="selectionSubTreeOrgs.(${status.index}).chartOfAccountsCode" />
                </kul:inquiry>&nbsp;
            </kul:htmlControlAttribute>
        --%>
        <c:forEach items="${KualiForm.selectionSubTreeOrgs}" var="item" varStatus="status" >

	    <tr>
            <td class="grid" valign="center" rowspan="1" colspan="1">
                <span class="nowrap">&nbsp;
               	<html:hidden write="false" property="selectionSubTreeOrgs[${status.index}].reportsToChartOfAccountsCode" />
               	<html:hidden write="false" property="selectionSubTreeOrgs[${status.index}].reportsToOrganizationCode" />
                </span>
            </td>
            <td class="grid" valign="center" rowspan="1" colspan="1">
            <kul:htmlControlAttribute
                property="selectionSubTreeOrgs[${status.index}].chartOfAccountsCode"
                attributeEntry="${pullupOrgAttributes.chartOfAccountsCode}"
                readOnly="true"
                readOnlyBody="true">
                <kul:inquiry
                    boClassName="org.kuali.module.chart.bo.Chart"
                    keyValues="chartOfAccountsCode=${item.chartOfAccountsCode}"
                    render="${!empty KualiForm.selectionSubTreeOrgs[0].chartOfAccountsCode}">
                	<html:hidden write="true" property="selectionSubTreeOrgs[${status.index}].chartOfAccountsCode" />
                </kul:inquiry>&nbsp;
            </kul:htmlControlAttribute>
            </td>
            <td class="grid" valign="center" rowspan="1" colspan="1">
            <kul:htmlControlAttribute
                property="selectionSubTreeOrgs[${status.index}].organizationCode"
                attributeEntry="${pullupOrgAttributes.organizationCode}"
                readOnly="true"
                readOnlyBody="true">
                <kul:inquiry
                    boClassName="org.kuali.module.chart.bo.Org"
                    keyValues="chartOfAccountsCode=${item.chartOfAccountsCode}&amp;organizationCode=${item.organizationCode}"
                    render="${!empty KualiForm.selectionSubTreeOrgs[0].organizationCode}">
                	<html:hidden write="true" property="selectionSubTreeOrgs[${status.index}].organizationCode" />
                </kul:inquiry>&nbsp;
	      	</kul:htmlControlAttribute>
            </td>
            <td class="grid" valign="center" rowspan="1" colspan="2">
            <kul:htmlControlAttribute
                property="selectionSubTreeOrgs[${status.index}].organization.organizationName"
                attributeEntry="${organizationAttributes.organizationName}"
                readOnly="true"/>&nbsp;
            </td>
            <td class="grid" valign="center" rowspan="1" colspan="1">
            <div align="center">
                <html:image property="methodToCall.navigateDown.line${status.index}.anchorselectionSubTreeOrgsAnchor${status.index}" src="${ConfigProperties.externalizable.images.url}purap-down.gif" title="Drill Down" alt="Drill Down" styleClass="tinybutton" />
            </div>
            </td>
	    </tr>

        </c:forEach>
        </c:if>

    </table>

    <div id="globalbuttons" class="globalbuttons">
        <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif" styleClass="globalbuttons" property="methodToCall.returnToCaller" title="close" alt="close"/>
    </div>

</kul:page>
