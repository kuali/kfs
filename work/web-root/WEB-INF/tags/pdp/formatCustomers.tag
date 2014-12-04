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

<%@ attribute name="customerProfileAttributes" required="true"
	type="java.util.Map"
	description="The DataDictionary entry containing attributes for disbursement number range."%>
	
<%@ attribute name="dummyAttributes" required="true"
	type="java.util.Map"
	description="The DataDictionary entry containing attributes for disbursement number range."%>	

<kul:tab tabTitle="Customers" defaultOpen="true" tabErrorKey="customer*">
	<div id="formatCustomer" class="tab-container" align=center>
		<table cellpadding="0" cellspacing="0" class="datatable"
			summary="Customers">
			<tr>
				<td colspan="3" class="subhead">
					Customers
				</td>
			</tr>
			<tr>
			
			 <tr>
            <logic:iterate id="customer" name="KualiForm" property="customers" indexId="ctr">
	                    <tr>
	                      <td class="${dataCell}">
                            <kul:htmlControlAttribute property="customer[${ctr}].selectedForFormat" attributeEntry="${dummyAttributes.genericBoolean}" />
	                      </td>
	                      <td class="${dataCell}"><kul:htmlControlAttribute attributeEntry="${customerProfileAttributes.customerShortName}" property="customer[${ctr}].customerShortName" readOnly="true" /></td>
	                      <td class="${dataCell}"><kul:htmlControlAttribute attributeEntry="${customerProfileAttributes.customerDescription}" property="customer[${ctr}].customerDescription" readOnly="true" /></td>
	                    </tr>
	         </logic:iterate>
          </tr>

		</table>

		
	</div>
</kul:tab>
    
