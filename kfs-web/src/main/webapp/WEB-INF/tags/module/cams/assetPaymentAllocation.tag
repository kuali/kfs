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
<c:set var="assetAllocationAttributes" value="${DataDictionary.AssetPaymentAllocationType.attributes}" />
<c:set var="viewOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<%-- Tab to allow selection of the method to distribute asset payments. --%>
<kul:tab tabTitle="Asset Allocation" defaultOpen="true">
	<div class="tab-container" id="allocation" align="left">	
		<table class="datatable" width="100%" border="0" cellpadding="0" cellspacing="0" border="1">
			<tr>
				<td colspan="3" class="subhead">
			    	<span class="subhead-left">Asset Allocation</span>
			  	</td>
			</tr>
		    <tr>
			    <th width="10%">Asset Allocation</th>
			    
			    <c:if test="${KualiForm.document.allocationFromFPDocuments == true}" >
			    	<c:set var="viewOnly" value="true"/>	
			    </c:if>
			     
				<td class="infoline" valign="top" width="84%">														
					<kul:htmlControlAttribute attributeEntry="${assetAllocationAttributes.allocationCode}" property="allocationCode" readOnly="${viewOnly}"/>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<c:if test="${!viewOnly}">
				    	<html:image property="methodToCall.selectAllocationType" src="${ConfigProperties.externalizable.images.url}tinybutton-select.gif" styleClass="tinybutton" alt="Select Allocation" title="Select Allocation"/>
				    </c:if>
				</td>
		    </tr>
		</table>
    </div>    
</kul:tab>
