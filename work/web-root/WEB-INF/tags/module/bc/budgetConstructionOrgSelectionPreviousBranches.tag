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
