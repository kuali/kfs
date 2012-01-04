<%--
 Copyright 2006-2009 The Kuali Foundation
 
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
