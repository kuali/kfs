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
            <th class="grid" colspan="7" align="left">
                <br>
                <html:hidden property="operatingModeTitle" value="${KualiForm.operatingModeTitle}" />
                ${KualiForm.operatingModeTitle}
                <br><br>
		    </th>
        </tr>
	    <tr>
            <%--point of view header --%>
            <th class="grid" colspan="2" rowspan="2">&nbsp;</th>
		    <th class="grid" align="left" colspan="4">
		        Current Point Of View Organization
		    </th>
		    <th class="grid" align="center" colspan="1">
			    Action
		    </th>
	    </tr>
	    <tr>
            <%-- point of view data line --%>
            <%-- first cell in row above spans two rows 
                                 property="pointOfViewOrg.selectionKeyCode"
            <bc:pbglLineDataCell dataCellCssClass="grid"
                accountingLine="pointOfViewOrg"
                field="organizationCode"
                attributes="${pointOfViewOrgAttributes}" inquiry="false"
                readOnly="true"
                displayHidden="false"
                colSpan="1" />
                     onchange="submitForm()"
            --%>
            <td class="grid" valign="center" rowspan="1" colspan="1">
                <span class="nowrap">
                <html:hidden property="previousPointOfViewKeyCode" value="${KualiForm.currentPointOfViewKeyCode}" />
                <kul:htmlControlAttribute
                     property="currentPointOfViewKeyCode"
                     attributeEntry="${pointOfViewOrgAttributes.selectionKeyCode}"
                     onchange="refreshPointOfView(this.form)"
                     readOnly="false"
                     styleClass="grid" />
                <html:image property="methodToCall.performBuildPointOfView" src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_refresh.gif" alt="refresh" title="refresh" styleClass="tinybutton"/>
                </span>
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
            <td class="grid" valign="center" rowspan="1" colspan="1">
            <kul:htmlControlAttribute
                property="pointOfViewOrg.organization.organizationName"
                attributeEntry="${organizationAttributes.organizationName}"
                readOnly="true"/>&nbsp;
            </td>
            <td class="grid" valign="center" rowspan="1" colspan="1">
                <html:image property="methodToCall.performBuildPointOfView" src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_refresh.gif" alt="refresh" title="refresh" styleClass="tinybutton"/>
            </td>
	    </tr>
    </table>

    <div id="globalbuttons" class="globalbuttons">
        <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif" styleClass="globalbuttons" property="methodToCall.returnToCaller" title="close" alt="close"/>
    </div>

</kul:page>
