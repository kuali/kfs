<%--
 Copyright 2006-2008 The Kuali Foundation
 
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
    
