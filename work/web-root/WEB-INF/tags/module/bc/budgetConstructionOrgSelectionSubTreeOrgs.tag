<%--
 Copyright 2007-2009 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>
	<c:set var="pullupOrgAttributes" value="${DataDictionary.BudgetConstructionPullup.attributes}" />
	<c:set var="organizationAttributes" value="${DataDictionary.Organization.attributes}" />

    <table class="datatable" border="0" cellpadding="0" cellspacing="0"  summary="">
		<tr>
	        <td class="subhead" colspan="4">Organization Sub-Tree</td>
      	</tr>
      	<tr>
    		<th width="12">Selected</th>
            <th>&nbsp;</th>
            <th>Organization Sub-Tree</th>
            <th>Action</th>
       </tr>

        <%-- pullup selection data lines --%>
        <c:forEach items="${KualiForm.selectionSubTreeOrgs}" var="item" varStatus="status" >

	    <tr>
            <td class="grid" valign="center" rowspan="1" colspan="1">
            <div align="center">
                
                <c:choose>
                   	<c:when test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.PULLUP or KualiForm.operatingMode == BCConstants.OrgSelOpMode.PUSHDOWN}">                   
                      <html:select property="selectionSubTreeOrgs[${status.index}].pullFlag">
                        <html:optionsCollection property="pullFlagKeyLabels" label="value" value="key" />
                      </html:select>
                    </c:when>
                    <c:otherwise>
                       <html:checkbox property="selectionSubTreeOrgs[${status.index}].pullFlag" value="1" />
                    </c:otherwise>
                </c:choose>      
            </div>
            </td>
            <td class="grid" valign="center" rowspan="1" colspan="1">
            <kul:htmlControlAttribute
                property="selectionSubTreeOrgs[${status.index}].chartOfAccountsCode"
                attributeEntry="${pullupOrgAttributes.chartOfAccountsCode}"
                readOnly="true"
                readOnlyBody="true">
                <kul:inquiry
                    boClassName="org.kuali.kfs.coa.businessobject.Chart"
                    keyValues="chartOfAccountsCode=${item.chartOfAccountsCode}"
                    render="${!empty KualiForm.selectionSubTreeOrgs[status.index].chartOfAccountsCode}">
                    ${KualiForm.selectionSubTreeOrgs[status.index].chartOfAccountsCode}
                </kul:inquiry>&nbsp;
            </kul:htmlControlAttribute>
			-
            <kul:htmlControlAttribute
                property="selectionSubTreeOrgs[${status.index}].organizationCode"
                attributeEntry="${pullupOrgAttributes.organizationCode}"
                readOnly="true"
                readOnlyBody="true">
                <kul:inquiry
                    boClassName="org.kuali.kfs.coa.businessobject.Organization"
                    keyValues="chartOfAccountsCode=${item.chartOfAccountsCode}&amp;organizationCode=${item.organizationCode}"
                    render="${!empty KualiForm.selectionSubTreeOrgs[status.index].organizationCode}">
                    ${KualiForm.selectionSubTreeOrgs[status.index].organizationCode}
                </kul:inquiry>&nbsp;
	      	</kul:htmlControlAttribute>
            </td>
            <td class="grid" valign="center" rowspan="1" >
            <kul:htmlControlAttribute
                property="selectionSubTreeOrgs[${status.index}].organization.organizationName"
                attributeEntry="${organizationAttributes.organizationName}"
                readOnly="true"
                readOnlyBody="true">
                ${KualiForm.selectionSubTreeOrgs[status.index].organization.organizationName}&nbsp;
	      	</kul:htmlControlAttribute>
            </td>
            <td class="grid" valign="center" rowspan="1" >
            <div align="center">&nbsp;
            <c:if test="${!item.leaf}">
                <html:image property="methodToCall.navigateDown.line${status.index}.anchorselectionSubTreeOrgsAnchor${status.index}" src="${ConfigProperties.externalizable.images.url}purap-down.gif" title="Drill Down" alt="Drill Down" styleClass="tinybutton" />
            </c:if>
            </div>
            </td>
	    </tr>
        </c:forEach>

        <tr>
        <c:choose>
            <c:when test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.SALSET || KualiForm.operatingMode == BCConstants.OrgSelOpMode.REPORTS || KualiForm.operatingMode == BCConstants.OrgSelOpMode.ACCOUNT}">
      	     <td colspan="4" nowrap class="infoline">
      		   <div align="center">
                    <html:image property="methodToCall.selectAll" src="${ConfigProperties.externalizable.images.url}tinybutton-selectall.gif" title="Select All" alt="Select All" styleClass="tinybutton" />
                    <html:image property="methodToCall.clearAll" src="${ConfigProperties.externalizable.images.url}tinybutton-clearall.gif" title="Clear All" alt="Clear All" styleClass="tinybutton" />
               </div>
            </td>
            </c:when>

            <c:when test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.PULLUP}">
            <td colspan="4" valign="center" class="infoline" nowrap="nowrap">
                <div align="center">
                    <html:image property="methodToCall.selectPullOrgAll" src="${ConfigProperties.externalizable.images.url}tinybutton-setorg.gif" title="Select Org All" alt="Select Org All" styleClass="tinybutton" />
                    <html:image property="methodToCall.selectPullSubOrgAll" src="${ConfigProperties.externalizable.images.url}tinybutton-setsuborg.gif" title="Select SubOrg All" alt="Select Sub Org All" styleClass="tinybutton" />
                    <html:image property="methodToCall.selectPullBothAll" src="${ConfigProperties.externalizable.images.url}tinybutton-setorgsuborg.gif" title="Select Both All" alt="Select Both All" styleClass="tinybutton" />
                    <html:image property="methodToCall.clearAll" src="${ConfigProperties.externalizable.images.url}tinybutton-clearall.gif" title="Clear All" alt="Clear All" styleClass="tinybutton" />
                </div>
            </td>
            </c:when>

            <c:when test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.PUSHDOWN}">
            <td colspan="4" valign="center" class="infoline" nowrap="nowrap">
                <div align="center">
                    <html:image property="methodToCall.selectPushOrgLevAll" src="${ConfigProperties.externalizable.images.url}tinybutton-setorg.gif" title="Select Org Lev All" alt="Select Org Lev All" styleClass="tinybutton" />
                    <html:image property="methodToCall.selectPushMgrLevAll" src="${ConfigProperties.externalizable.images.url}tinybutton-setfiscofflevel.gif" title="Select Mgr Lev All" alt="Select Mgr Lev All" styleClass="tinybutton" />
                    <html:image property="methodToCall.selectPushOrgMgrLevAll" src="${ConfigProperties.externalizable.images.url}tinybutton-setfiscorgofflevel.gif" title="Select Org and Mgr Lev All" alt="Select Org and Mgr Lev All" styleClass="tinybutton" />
                    <html:image property="methodToCall.selectPushLevOneAll" src="${ConfigProperties.externalizable.images.url}tinybutton-setlevelone.gif" title="Select Lev One All" alt="Select Lev One All" styleClass="tinybutton" />
                    <html:image property="methodToCall.selectPushLevZeroAll" src="${ConfigProperties.externalizable.images.url}tinybutton-setlevelzero.gif" title="Select Lev Zero All" alt="Select Lev Zero All" styleClass="tinybutton" />
                    <html:image property="methodToCall.clearAll" src="${ConfigProperties.externalizable.images.url}tinybutton-clearall.gif" title="Clear All" alt="Clear All" styleClass="tinybutton" />
                </div>
            </td>
            </c:when>
            <c:otherwise>
	            <td class="grid" valign="center" rowspan="1" colspan="1">&nbsp;</td>
            </c:otherwise>
	        </c:choose>
		</tr>
		</table>

