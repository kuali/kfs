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

<%@ attribute name="editingMode" required="true" description="used to decide if items may be edited" type="java.util.Map"%>
<%@ attribute name="showSource" required="true" %>
<%@ attribute name="showTarget" required="true" %>
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:tab tabTitle="Security Details" defaultOpen="false" tabErrorKey="${KFSConstants.ITEM_LINE_ERRORS}">


 <c:set var="securityTransactionAttributes" value="${DataDictionary.EndowmentTransactionSecurity.attributes}" />



 <div class="tab-container" align=center>
	<h3>Security Details</h3>
	<table cellpadding="0" cellspacing="0" class="datatable" summary="Security Details">
	<c:if  test="${showSource}">  
		<tr>
            <kul:htmlAttributeHeaderCell attributeEntry="${securityTransactionAttributes.securityId}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${securityTransactionAttributes.security.securityClassCode}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${securityTransactionAttributes.security.classCode.securityEndowmentTransactionCode}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${securityTransactionAttributes.security.classCode.endowmentTransactionCode.name}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${securityTransactionAttributes.registrationCode}"/>
            
            
		</tr>
      
        <tr>
            <td class="infoline"><kul:htmlControlAttribute attributeEntry="${securityTransactionAttributes.securityId}" property="fromTransactionSecurity.securityId" /></td>
            <td class="infoline"><kul:htmlControlAttribute attributeEntry="${securityTransactionAttributes.registrationCode}" property="fromTransactionSecurity.registrationCode" /></td>
        </tr>
    </c:if>
    
    <c:if  test="${showTarget}">  
		<tr>
            <kul:htmlAttributeHeaderCell attributeEntry="${securityTransactionAttributes.securityId}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${securityTransactionAttributes.registrationCode}"/>
		</tr>
      
        <tr>
            <td class="infoline"><kul:htmlControlAttribute attributeEntry="${securityTransactionAttributes.securityId}" property="toTransactionSecurity.securityId" /></td>
            <td class="infoline"><kul:htmlControlAttribute attributeEntry="${securityTransactionAttributes.registrationCode}" property="toTransactionSecurity.registrationCode" /></td>
        </tr>
    </c:if>

	</table>
</div>