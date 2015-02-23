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

<c:set var="groupAttributes" value="${DataDictionary.GroupBo.attributes}" />
<c:set var="groupTypeAttributes" value="${DataDictionary.KimTypeBo.attributes}" />

<kul:tab tabTitle="Overview" defaultOpen="true" transparentBackground="${inquiry}" tabErrorKey="document.group*,document.active">
<div class="tab-container" align="center">
	<table cellpadding="0" cellspacing="0" summary=""> 
	 	<tr>
			<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${groupAttributes.id}"  /></div></th>
	 		<td><kul:htmlControlAttribute property="document.groupId" attributeEntry="${groupAttributes.id}" readOnly="true" /></td>
    		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${groupTypeAttributes.name}"  /></div></th>
	 		<td>
	 		    <kul:htmlControlAttribute property="document.groupTypeName" attributeEntry="${groupTypeAttributes.name}" readOnly="true" />
				<c:if test="${!readOnly}">
	            	<c:set var="bo" value="${KualiForm.kimTypeClass}"/>
	               	<kul:lookup boClassName="${bo}" 
	               	   fieldConversions=""  />
	           	</c:if>
	 		</td>
	 		<html:hidden property="document.groupTypeId" />
	 	</tr>
	 	<tr>
    		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${groupAttributes.namespaceCode}"  /></div></th>
	 		<td>
	 		    <kul:htmlControlAttribute property="document.groupNamespace" attributeEntry="${groupAttributes.namespaceCode}" readOnly="${(readOnly || editingDocument)}" onchange="namespaceChanged( this.form );" />
	 		    <c:if test="${!inquiry && !readOnly && !editingDocument}">
	 		        <noscript>
	 		            <html:image tabindex="32768" property="methodToCall.changeNamespace" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-refresh.gif" styleClass="tinybutton" title="Click to refresh the page after changing the namespace." alt="Click to refresh the page after changing the namespace." />
	 		        </noscript>
	 		    </c:if>
	 		</td>
    		<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${groupAttributes.name}"  /></div></th>
	 		<td><kul:htmlControlAttribute property="document.groupName" attributeEntry="${groupAttributes.name}" readOnly="${(readOnly || editingDocument)}" /></td>
	 	</tr>
	 	<tr>
			<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${groupAttributes.active}"  /></div></th>
	 		<td><kul:htmlControlAttribute property="document.active" attributeEntry="${groupAttributes.active}" readOnly="${readOnly}" /></td>
	 		
			<th><div align="right"><kul:htmlAttributeLabel attributeEntry="${groupAttributes.description}"  /></div></th>
		    <td><kul:htmlControlAttribute property="document.groupDescription" attributeEntry="${groupAttributes.description}" readOnly="${readOnly}" /></td>
		    
	 	</tr>
	</table> 
</div>
</kul:tab>

