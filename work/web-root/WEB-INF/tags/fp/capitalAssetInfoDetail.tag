<%--
 Copyright 2005-2009 The Kuali Foundation
 
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

<%@ tag description="render the given field in the capital asset info object"%>

<%@ attribute name="capitalAssetInfoDetails" required="true" type="java.lang.Object"
	description="The capital asset info object containing the data being displayed"%>
<%@ attribute name="capitalAssetInfoDetailsName" required="true" description="The name of the capital asset info object"%>	
<%@ attribute name="readOnly" required="false" description="Whether the capital asset information should be read only" %>	
<%@ attribute name="capitalAssetInfoIndex" required="true" description="Gives the capital asset information index" %>	
	
<c:set var="attributes" value="${DataDictionary.CapitalAssetInformationDetail.attributes}" />		
<c:set var="dataCellCssClass" value="datacell"/>

<c:if test="${not empty capitalAssetInfoDetails}">
	<table style="border-top: 1px solid rgb(153, 153, 153); width: 90%;" cellpadding="0" cellspacing="0" class="datatable" summary="Capital Asset Information Details">  
	   <tr>  
	   		<kul:htmlAttributeHeaderCell literalLabel=""/>
	   	    <kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetTagNumber}" labelFor="${capitalAssetInfoDetailsName}.capitalAssetTagNumber"/>
			<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetSerialNumber}" labelFor="${capitalAssetInfoDetailsName}.capitalAssetSerialNumber"/>                      
		    <kul:htmlAttributeHeaderCell attributeEntry="${attributes.campusCode}" labelFor="${capitalAssetInfoDetailsName}.campusCode"/>
			<kul:htmlAttributeHeaderCell attributeEntry="${attributes.buildingCode}" labelFor="${capitalAssetInfoDetailsName}.buildingCode"/>
			<kul:htmlAttributeHeaderCell attributeEntry="${attributes.buildingRoomNumber}" labelFor="${capitalAssetInfoDetailsName}.buildingRoomNumber"/>
			<kul:htmlAttributeHeaderCell attributeEntry="${attributes.buildingSubRoomNumber}" labelFor="${capitalAssetInfoDetailsName}.buildingSubRoomNumber"/>
			<c:if test="${!readOnly}">
				<kul:htmlAttributeHeaderCell literalLabel="Action"/>
			</c:if>
	   </tr>
	   
   	   <c:forEach items="${capitalAssetInfoDetails}" var="detailLine" varStatus="status">
	   <tr>
	   		<c:set var="lineNumber" value="${status.index + 1}"/>
			<kul:htmlAttributeHeaderCell literalLabel="${lineNumber}"/>	
	   		
			<fp:dataCell dataCellCssClass="${dataCellCssClass}"
				businessObjectFormName="${capitalAssetInfoDetailsName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
				field="capitalAssetTagNumber" lookup="false" inquiry="false"/>	
				   
			<fp:dataCell dataCellCssClass="${dataCellCssClass}"
				businessObjectFormName="${capitalAssetInfoDetailsName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
				field="capitalAssetSerialNumber" lookup="false" inquiry="false"/>
				
			<fp:dataCell dataCellCssClass="${dataCellCssClass}"
				businessObjectFormName="${capitalAssetInfoDetailsName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
				field="campusCode" lookup="false" inquiry="true"
				boClassSimpleName="Campus" boPackageName="org.kuali.rice.kns.bo"
				lookupOrInquiryKeys="campusCode"
				businessObjectValuesMap="${capitalAssetInfoDetail.valuesMap}"/>	
			
			<fp:dataCell dataCellCssClass="${dataCellCssClass}"
				businessObjectFormName="${capitalAssetInfoDetailsName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
				field="buildingCode" lookup="true" inquiry="true"
				boClassSimpleName="Building" boPackageName="org.kuali.kfs.sys.businessobject"
				lookupOrInquiryKeys="campusCode,buildingCode"
				businessObjectValuesMap="${capitalAssetInfoDetail.valuesMap}"/>
			
			<fp:dataCell dataCellCssClass="${dataCellCssClass}"
				businessObjectFormName="${capitalAssetInfoDetailsName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
				field="buildingRoomNumber" lookup="true" inquiry="true"
				boClassSimpleName="Room" boPackageName="org.kuali.kfs.sys.businessobject"
				lookupOrInquiryKeys="campusCode,buildingCode,buildingRoomNumber"
				businessObjectValuesMap="${capitalAssetInfoDetail.valuesMap}"/>	
			
			<fp:dataCell dataCellCssClass="${dataCellCssClass}"
				businessObjectFormName="${capitalAssetInfoDetailsName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
				field="buildingSubRoomNumber" lookup="false" inquiry="false"/>
			
			<c:if test="${!readOnly}">
				<td class="infoline">  
					<div style="text-align: center;">			 
						 <html:image property="methodToCall.deleteCapitalAssetInfoDetailLine.line${capitalAssetInfoIndex}.Anchor" 
							src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" 
							title="delete the capital Asset Information Detail line ${lineNumber}"
							alt="delete the capital Asset Information Detail line ${lineNumber}" styleClass="tinybutton" />
					</div>
				</td>
			</c:if>																									 
	   </tr>
	   </c:forEach>
	</table>
</c:if>
