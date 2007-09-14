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
	<html-el:hidden name="KualiForm" property="universityFiscalYear" />
	
    <kul:errors keyMatch="pointOfViewOrg" errorTitle="Errors found in Organization Selection:" />
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
            <%-- first cell in row above spans two rows --%>
            <td class="grid" valign="center" rowspan="1" colspan="1">
            <div align="right">
                <html:hidden property="previousPointOfViewKeyCode" value="${KualiForm.currentPointOfViewKeyCode}" />
                <kul:htmlControlAttribute
                     property="currentPointOfViewKeyCode"
                     attributeEntry="${pointOfViewOrgAttributes.selectionKeyCode}"
                     onchange="refreshPointOfView(this.form)"
                     readOnly="false"
                     styleClass="grid" />
                <noscript>     
                <html:image property="methodToCall.performBuildPointOfView" src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_refresh.gif" alt="refresh" title="refresh" styleClass="tinybutton"/>
                </noscript>     
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
            <th class="grid" colspan="1" rowspan="1"><br>${KualiForm.operatingModePullFlagLabel}</th>
		    <th class="grid" align="left" colspan="4"><br>Organization Selection Sub-Tree</th>
		    <th class="grid" align="center" colspan="1"><br>Action</th>
	    </tr>

        <%-- pullup selection data lines --%>
        <c:forEach items="${KualiForm.selectionSubTreeOrgs}" var="item" varStatus="status" >

	    <tr>
            <td class="grid" valign="center" rowspan="1" colspan="1">
            <div align="right">
               	<html:hidden write="false" property="selectionSubTreeOrgs[${status.index}].reportsToChartOfAccountsCode" />
               	<html:hidden write="false" property="selectionSubTreeOrgs[${status.index}].reportsToOrganizationCode" />
               	<html:hidden write="false" property="selectionSubTreeOrgs[${status.index}].versionNumber" />
               	<html:hidden write="false" property="selectionSubTreeOrgs[${status.index}].personUniversalIdentifier" />
                <html:select property="selectionSubTreeOrgs[${status.index}].pullFlag">
                    <html:optionsCollection property="pullFlagKeyLabels" label="label" value="key" />
                </html:select>
            </div>
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
            <c:if test="${!item.leaf}">
                <html:image property="methodToCall.navigateDown.line${status.index}.anchorselectionSubTreeOrgsAnchor${status.index}" src="${ConfigProperties.externalizable.images.url}purap-down.gif" title="Drill Down" alt="Drill Down" styleClass="tinybutton" />
            </c:if>&nbsp;
            </div>
            </td>
	    </tr>

        </c:forEach>

        <%-- TODO make this choose a tag passing in operating mode --%>
        <tr>

        <c:choose>

            <c:when test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.SALSET || KualiForm.operatingMode == BCConstants.OrgSelOpMode.REPORTS || KualiForm.operatingMode == BCConstants.OrgSelOpMode.ACCOUNT}">
            <td class="grid" valign="center" rowspan="1" colspan="1">
                <div align="center">
                    <html:image property="methodToCall.selectAll" src="${ConfigProperties.externalizable.images.url}tinybutton-select.gif" title="Select All" alt="Select All" styleClass="tinybutton" />
                    <html:image property="methodToCall.clearAll" src="${ConfigProperties.externalizable.images.url}tinybutton-clearlines.gif" title="Clear All" alt="Clear All" styleClass="tinybutton" />
                </div>
            </td>
            </c:when>

            <c:when test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.PULLUP}">
            <td class="grid" valign="center" rowspan="1" colspan="1">
                <div align="center">
                    <html:image property="methodToCall.selectPullOrgAll" src="${ConfigProperties.externalizable.images.url}tinybutton-select.gif" title="Select Org All" alt="Select Org All" styleClass="tinybutton" />
                    <html:image property="methodToCall.selectPullSubOrgAll" src="${ConfigProperties.externalizable.images.url}tinybutton-select.gif" title="Select SubOrg All" alt="Select Sub Org All" styleClass="tinybutton" />
                    <html:image property="methodToCall.selectPullBothAll" src="${ConfigProperties.externalizable.images.url}tinybutton-select.gif" title="Select Both All" alt="Select Both All" styleClass="tinybutton" />
                    <html:image property="methodToCall.clearAll" src="${ConfigProperties.externalizable.images.url}tinybutton-clearlines.gif" title="Clear All" alt="Clear All" styleClass="tinybutton" />
                </div>
            </td>
            </c:when>

            <c:when test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.PUSHDOWN}">
            <td class="grid" valign="center" rowspan="1" colspan="1">
                <div align="center">
                    <html:image property="methodToCall.selectPushOrgLevAll" src="${ConfigProperties.externalizable.images.url}tinybutton-select.gif" title="Select Org Lev All" alt="Select Org Lev All" styleClass="tinybutton" />
                    <html:image property="methodToCall.selectPushMgrLevAll" src="${ConfigProperties.externalizable.images.url}tinybutton-select.gif" title="Select Mgr Lev All" alt="Select Mgr Lev All" styleClass="tinybutton" />
                    <html:image property="methodToCall.selectPushOrgMgrLevAll" src="${ConfigProperties.externalizable.images.url}tinybutton-select.gif" title="Select Org and Mgr Lev All" alt="Select Org and Mgr Lev All" styleClass="tinybutton" />
                    <html:image property="methodToCall.selectPushLevOneAll" src="${ConfigProperties.externalizable.images.url}tinybutton-select.gif" title="Select Lev One All" alt="Select Lev One All" styleClass="tinybutton" />
                    <html:image property="methodToCall.selectPushLevZeroAll" src="${ConfigProperties.externalizable.images.url}tinybutton-select.gif" title="Select Lev Zero All" alt="Select Lev Zero All" styleClass="tinybutton" />
                    <html:image property="methodToCall.clearAll" src="${ConfigProperties.externalizable.images.url}tinybutton-clearlines.gif" title="Clear All" alt="Clear All" styleClass="tinybutton" />
                </div>
            </td>
            </c:when>

            <c:otherwise>
            <td class="grid" valign="center" rowspan="1" colspan="1">&nbsp;</td>
            </c:otherwise>
        </c:choose>

        <td class="grid" valign="center" rowspan="1" colspan="5">
            <kul:errors keyMatch="selectionSubTreeOrgs" errorTitle="Errors found in Organization Selection Control:" />&nbsp;
        </td>

        </tr>


        <%-- TODO make this along with choose code below a tag passing in operating mode --%>
        <%-- display this regardless of mode --%>
        <tr>
            <th class="grid" align="left" colspan="6">
            <br>Selection Operation
            <br><br>
            </th>
        </tr>

        <c:choose>

            <c:when test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.SALSET}">

            <tr>
                <td class="grid" valign="center" rowspan="1" colspan="6">
                <div align="center">
                    <br>
                    <html:image property="methodToCall.performPositionPick" src="${ConfigProperties.externalizable.images.url}tinybutton-posnsalset.gif" title="Position Pick" alt="Position Pick" styleClass="tinybutton" />
                    <html:image property="methodToCall.performIncumbentPick" src="${ConfigProperties.externalizable.images.url}tinybutton-incmbntsalset.gif" title="Incumbent Pick" alt="Incumbent Pick" styleClass="tinybutton" />
                    <br>&nbsp;
                </div>
                </td>
            </tr>

            </c:when>
            <c:when test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.REPORTS}">

            <tr>
                <td class="grid" valign="center" rowspan="1" colspan="6" style="border: none; padding: 0px;">
                    <table align="center" cellpadding="0" cellspacing="0" class="datatable-100" style="border: none;">
                    <%--reports and dump header --%>
                    <tr>
                        <th class="grid" align="center" colspan="4"><br>Reports</th>
                        <th class="grid" align="center" colspan="1"><br>Export</th>
                    </tr>
                    <tr>
                        <td class="grid" valign="center" rowspan="1" colspan="1">
                        <div align="center">
                            <html:image property="methodToCall.performReport" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Acct Sum" alt="Acct Sum" styleClass="tinybutton" />
                        </div>
                        </td>
                        <td class="grid"  valign="center" rowspan="1" colspan="1">
                        <div align="center">
                            <html:image property="methodToCall.performReport" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="SFund Sum" alt="SFund Sum" styleClass="tinybutton" />
                        </div>
                        </td>
                        <td class="grid"  valign="center" rowspan="1" colspan="1">
                        <div align="center">
                            <html:image property="methodToCall.performReport" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Level Sum" alt="Level Sum" styleClass="tinybutton" />
                        </div>
                        </td>
                        <td class="grid"  valign="center" rowspan="1" colspan="1">
                        <div align="center">
                            <html:image property="methodToCall.performReport" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Object Sum" alt="Object Sum" styleClass="tinybutton" />
                        </div>
                        </td>
                        <td class="grid"  valign="center" rowspan="1" colspan="1">
                        <div align="center">
                            <html:image property="methodToCall.performReport" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Account Dump" alt="Account Dump" styleClass="tinybutton" />
                        </div>
                        </td>
                    </tr>
                    <tr>
                        <td class="grid" valign="center" rowspan="1" colspan="1">
                        <div align="center">
                            <html:image property="methodToCall.performReport" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Account Object Detail" alt="Account Object Detail" styleClass="tinybutton" />
                        </div>
                        </td>
                        <td class="grid"  valign="center" rowspan="1" colspan="1">
                        <div align="center">
                            <html:image property="methodToCall.performReport" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Account Funding Detail" alt="Account Funding Detail" styleClass="tinybutton" />
                        </div>
                        </td>
                        <td class="grid"  valign="center" rowspan="1" colspan="1">
                        <div align="center">
                            <html:image property="methodToCall.performReport" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Position Funding" alt="Position Funding" styleClass="tinybutton" />
                        </div>
                        </td>
                        <td class="grid"  valign="center" rowspan="1" colspan="1">
                        <div align="center">
                            <html:image property="methodToCall.performReport" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Monthy Object Sum" alt="Monthly Object Sum" styleClass="tinybutton" />
                        </div>
                        </td>
                        <td class="grid"  valign="center" rowspan="1" colspan="1">
                        <div align="center">
                            <html:image property="methodToCall.performReport" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Funding Dump" alt="Funding Dump" styleClass="tinybutton" />
                        </div>
                        </td>
                    </tr>
                    <tr>
                        <td class="grid" valign="center" rowspan="1" colspan="1">
                        <div align="center">
                            <html:image property="methodToCall.performReport" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Salary List" alt="Salary List" styleClass="tinybutton" />
                        </div>
                        </td>
                        <td class="grid"  valign="center" rowspan="1" colspan="1">
                        <div align="center">
                            <html:image property="methodToCall.performReport" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Salary Statistics" alt="Salary Statistics" styleClass="tinybutton" />
                        </div>
                        </td>
                        <td class="grid"  valign="center" rowspan="1" colspan="1">
                        <div align="center">
                            <html:image property="methodToCall.performReport" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Salary Exceptions" alt="Salary Exceptions" styleClass="tinybutton" />
                        </div>
                        </td>
                        <td class="grid"  valign="center" rowspan="1" colspan="1">
                        <div align="center">
                            <html:image property="methodToCall.performReport" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Salary Exceptions Statistics" alt="Salary Exceptions Statistics" styleClass="tinybutton" />
                        </div>
                        </td>
                        <td class="grid"  valign="center" rowspan="1" colspan="1">
                        <div align="center">
                            <html:image property="methodToCall.performReport" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Monthly Dump" alt="Monthly Dump" styleClass="tinybutton" />
                        </div>
                        </td>
                    </tr>
                    <tr>
                        <td class="grid" valign="center" rowspan="1" colspan="1">
                        <div align="center">
                            <html:image property="methodToCall.performReport" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="List 2PLG" alt="List 2PLG" styleClass="tinybutton" />
                        </div>
                        </td>
                        <td class="grid"  valign="center" rowspan="1" colspan="1">
                        <div align="center">
                            <html:image property="methodToCall.performReport" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Payroll Synchronization Problems" alt="Payroll Synchronization Problems" styleClass="tinybutton" />
                        </div>
                        </td>
                        <td class="grid"  valign="center" rowspan="1" colspan="3">&nbsp;
                        </td>
                    </tr>
                    </table>
                </td>
            </tr>
            </c:when>
            <c:when test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.ACCOUNT}">
            <tr>
                <td class="grid" valign="center" rowspan="1" colspan="6">
                <div align="center">
                    <br>
                    <html:image property="methodToCall.performShowBudgetDocs" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Show Documents" alt="Show Documents" styleClass="tinybutton" />
                    <br>&nbsp;
                </div>
                </td>
            </tr>
            </c:when>
            <c:when test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.PULLUP}">
            <tr>
                <td class="grid" valign="center" rowspan="1" colspan="6">
                <div align="center">
                    <br>
                    <html:image property="methodToCall.performPullUp" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Pull Up" alt="Pull Up" styleClass="tinybutton" />
                    <html:image property="methodToCall.performShowPullUpBudgetDocs" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Show Documents" alt="Show Documents" styleClass="tinybutton" />
                    <br>&nbsp;
                </div>
                </td>
            </tr>
            </c:when>
            <c:when test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.PUSHDOWN}">
            <tr>
                <td class="grid" valign="center" rowspan="1" colspan="6">
                <div align="center">
                    <br>
                    <html:image property="methodToCall.performPushDown" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Push Down" alt="Push Down" styleClass="tinybutton" />
                    <html:image property="methodToCall.performShowPushDownBudgetDocs" src="${ConfigProperties.externalizable.images.url}tinybutton-view.gif" title="Show Documents" alt="Show Documents" styleClass="tinybutton" />
                    <br>&nbsp;
                </div>
                </td>
            </tr>
            </c:when>

            <c:otherwise>
                <td class="grid" valign="center" rowspan="1" colspan="6">&nbsp;</td>
            </c:otherwise>

        </c:choose>

        </c:if>

    </table>

    <div id="globalbuttons" class="globalbuttons">
        <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif" styleClass="globalbuttons" property="methodToCall.returnToCaller" title="close" alt="close"/>
    </div>

</kul:page>
