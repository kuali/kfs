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

    <table cellpadding=0 cellspacing="0"  summary="">
      <tr>
        <td class="subhead" colspan="4">Previous Branches</td>
      </tr>
      <tr>
        <th>&nbsp;</th>
        <th>Chart/Org</th>
        <th>Organization</th>
        <th>&nbsp;</th>
      </tr>
      <%-- previous branches --%>
      <c:forEach items="${KualiForm.previousBranchOrgs}" var="item" varStatus="status" >
      <tr>
        <td>
          <div align="center">
            <html:image property="methodToCall.navigateUp.line${status.index}.anchorpreviousBranchOrgsAnchor${status.index}" src="${ConfigProperties.externalizable.images.url}purap-up.gif" title="Return Previous" alt="Return Previous" styleClass="tinybutton" />
          </div>
        </td>
        <td>
          <kul:htmlControlAttribute property="previousBranchOrgs[${status.index}].chartOfAccountsCode" attributeEntry="${pullupOrgAttributes.chartOfAccountsCode}" readOnly="true" readOnlyBody="true">
            <kul:inquiry  boClassName="org.kuali.kfs.coa.businessobject.Chart" keyValues="chartOfAccountsCode=${item.chartOfAccountsCode}" render="${!empty KualiForm.previousBranchOrgs[0].chartOfAccountsCode}">
            ${KualiForm.previousBranchOrgs[status.index].chartOfAccountsCode}
            </kul:inquiry>&nbsp;
          </kul:htmlControlAttribute>
      		-
          <kul:htmlControlAttribute property="previousBranchOrgs[${status.index}].organizationCode" attributeEntry="${pullupOrgAttributes.organizationCode}" readOnly="true" readOnlyBody="true">
            <kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Organization" keyValues="chartOfAccountsCode=${item.chartOfAccountsCode}&amp;organizationCode=${item.organizationCode}" render="${!empty KualiForm.previousBranchOrgs[0].organizationCode}">
            ${KualiForm.previousBranchOrgs[status.index].organizationCode}
	        </kul:inquiry>&nbsp;
          </kul:htmlControlAttribute>
        </td>
        <td>
          <kul:htmlControlAttribute
            property="previousBranchOrgs[${status.index}].organization.organizationName"
            attributeEntry="${organizationAttributes.organizationName}"
            readOnly="true"
            readOnlyBody="true">
            ${KualiForm.previousBranchOrgs[status.index].organization.organizationName}&nbsp;
          </kul:htmlControlAttribute>
        </td>
        <td>&nbsp;
        </td>
      </tr>
      </c:forEach>
    </table> 
