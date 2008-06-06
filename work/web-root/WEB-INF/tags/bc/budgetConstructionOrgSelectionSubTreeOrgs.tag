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


<div class="tab-container" align="center"> 
    <div class="tab-container-error">
    </div>
    <table cellpadding=0 cellspacing="0"  summary="">
		<tr>
	        <td class="subhead" colspan="4">Organization Sub-Tree</td>
      	</tr>
      	<tr>
    		<th width="12">Selected</th>
            <th>&nbsp;</th>
            <th>Organization Sub-Tree</th>
            <th width="200"><span class="grid">Action</span></th>
       </tr>

        <%-- pullup selection data lines --%>
        <c:forEach items="${KualiForm.selectionSubTreeOrgs}" var="item" varStatus="status" >

	    <tr>
            <td class="grid" valign="center" rowspan="1" colspan="1">
            <div align="center">
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
			-

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
            <td class="grid" valign="center" rowspan="1" >
            <kul:htmlControlAttribute
                property="selectionSubTreeOrgs[${status.index}].organization.organizationName"
                attributeEntry="${organizationAttributes.organizationName}"
                readOnly="true"/>&nbsp;
            </td>
            <td class="grid" valign="center" rowspan="1" >
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
      	     <td colspan="4" nowrap class="infoline">
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

		
		
		
		
		</table>
		</div>
		
		
         
         
         
         
         
         
         
         
         
         
         
         
         
         


