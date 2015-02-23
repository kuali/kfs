<%--
 Copyright 2009 The Kuali Foundation
 
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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="roleAttributes" value="${DataDictionary.RoleBo.attributes}" />
<c:set var="roleTypeAttributes" value="${DataDictionary.KimTypeBo.attributes}" />

<kul:tab tabTitle="Overview" defaultOpen="true" transparentBackground="${inquiry}" tabErrorKey="document.role*,document.active">

<div class="tab-container" align="center">
	<table cellpadding="0" cellspacing="0" summary=""> 
	 	<tr>
			<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${roleAttributes.id}"  /></div></th>
	 		<td><kul:htmlControlAttribute property="document.roleId" attributeEntry="${roleAttributes.id}" readOnly="true" /></td>
    		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${roleTypeAttributes.name}"  /></div></th>
	 		<td><kul:htmlControlAttribute property="document.roleTypeName" attributeEntry="${roleTypeAttributes.name}" readOnly="true" /></td>
	 		<html:hidden property="document.roleTypeId" />
	 	</tr>
	 	<tr>
    		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${roleAttributes.namespaceCode}"  /></div></th>
	 		<td>
	 			<kul:htmlControlAttribute property="document.roleNamespace" attributeEntry="${roleAttributes.namespaceCode}" readOnly="${readOnly || editingDocument}" onchange="namespaceChanged( this.form );" />
	 			<c:if test="${!inquiry && !readOnly && !editingDocument}">
	 		        <noscript>
	 		   	        <html:image tabindex="32768" property="methodToCall.changeNamespace" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-refresh.gif" styleClass="tinybutton" title="Click to refresh the page after changing the namespace." alt="Click to refresh the page after changing the namespace." />
	 		        </noscript>
	 		    </c:if>
	 		</td>
    		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${roleAttributes.name}"  /></div></th>
	 		<td><kul:htmlControlAttribute property="document.roleName" attributeEntry="${roleAttributes.name}" readOnly="${readOnly}" /></td>
	 	</tr>
	 	<tr>
			<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${roleAttributes.active}"  /></div></th>
	 		<td><kul:htmlControlAttribute property="document.active" attributeEntry="${roleAttributes.active}" readOnly="${readOnly}" /></td>
			<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${roleAttributes.description}"  /></div></th>
	 		<td><kul:htmlControlAttribute property="document.roleDescription" attributeEntry="${roleAttributes.description}" readOnly="${readOnly}" /></td>
	 	</tr>
	</table> 

</div>
</kul:tab>
