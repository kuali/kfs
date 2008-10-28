<%--
 Copyright 2005-2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>

<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<%@ tag description="render the given field in the capital asset info object"%>

<%@ attribute name="capitalAssetInfoDetails" required="true" type="java.lang.Object"
	description="The capital asset info object containing the data being displayed"%>
<%@ attribute name="capitalAssetInfoDetailsName" required="true" description="The name of the capital asset info object"%>	
<%@ attribute name="readOnly" required="false" description="Whether the capital asset information should be read only" %>	
	
<c:set var="attributes" value="${DataDictionary.CapitalAssetInformationDetail.attributes}" />		
<c:set var="dataCellCssClass" value="datacell"/>

<c:if test="${not empty capitalAssetInfoDetails}">
	<table style="border-top: 1px solid rgb(153, 153, 153); width: 95%;" cellpadding="0" cellspacing="0" class="datatable" summary="Capital Asset Information Details">  
	   <tr>  
	   		<kul:htmlAttributeHeaderCell attributeEntry="${attributes.itemLineNumber}" labelFor="${capitalAssetInfoDetailsName}.itemLineNumber"/>
	   	    <kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetTagNumber}" labelFor="${capitalAssetInfoDetailsName}.capitalAssetTagNumber"/>
			<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetSerialNumber}" labelFor="${capitalAssetInfoDetailsName}.capitalAssetSerialNumber"/>                      
		    <kul:htmlAttributeHeaderCell attributeEntry="${attributes.campusCode}" labelFor="${capitalAssetInfoDetailsName}.campusCode"/>
			<kul:htmlAttributeHeaderCell attributeEntry="${attributes.buildingCode}" labelFor="${capitalAssetInfoDetailsName}.buildingCode"/>
			<kul:htmlAttributeHeaderCell attributeEntry="${attributes.buildingRoomNumber}" labelFor="${capitalAssetInfoDetailsName}.buildingRoomNumber"/>
			<kul:htmlAttributeHeaderCell attributeEntry="${attributes.buildingSubRoomNumber}" labelFor="${capitalAssetInfoDetailsName}.buildingSubRoomNumber"/>
			<c:if test="${!readOnly}">
				<kul:htmlAttributeHeaderCell literalLabel="Action"/>
			</c:if
	   </tr>
	   
   	   <c:forEach items="${capitalAssetInfoDetails}" var="detailLine" varStatus="status">
	   <tr>
	   		<fin:dataCell dataCellCssClass="${dataCellCssClass}"
				businessObjectFormName="${capitalAssetInfoDetailsName}[${status.index}]" attributes="${attributes}" readOnly="true"
				field="itemLineNumber" lookup="false" inquiry="false"/>	
	   		
			<fin:dataCell dataCellCssClass="${dataCellCssClass}"
				businessObjectFormName="${capitalAssetInfoDetailsName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
				field="capitalAssetTagNumber" lookup="false" inquiry="false"/>	
				   
			<fin:dataCell dataCellCssClass="${dataCellCssClass}"
				businessObjectFormName="${capitalAssetInfoDetailsName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
				field="capitalAssetSerialNumber" lookup="false" inquiry="false"/>
				
			<fin:dataCell dataCellCssClass="${dataCellCssClass}"
				businessObjectFormName="${capitalAssetInfoDetailsName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
				field="campusCode" lookup="true" inquiry="true"
				boClassSimpleName="Campus" boPackageName="org.kuali.rice.kns.bo"
				lookupOrInquiryKeys="campusCode"
				businessObjectValuesMap="${capitalAssetInfo.valuesMap}"/>	
			
			<fin:dataCell dataCellCssClass="${dataCellCssClass}"
				businessObjectFormName="${capitalAssetInfoDetailsName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
				field="buildingCode" lookup="true" inquiry="true"
				boClassSimpleName="Building" boPackageName="org.kuali.kfs.sys.businessobject"
				lookupOrInquiryKeys="campusCode,buildingCode"
				businessObjectValuesMap="${capitalAssetInfo.valuesMap}"/>
			
			<fin:dataCell dataCellCssClass="${dataCellCssClass}"
				businessObjectFormName="${capitalAssetInfoDetailsName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
				field="buildingRoomNumber" lookup="true" inquiry="true"
				boClassSimpleName="Room" boPackageName="org.kuali.kfs.sys.businessobject"
				lookupOrInquiryKeys="campusCode,buildingCode,buildingRoomNumber"
				businessObjectValuesMap="${capitalAssetInfo.valuesMap}"/>	
			
			<fin:dataCell dataCellCssClass="${dataCellCssClass}"
				businessObjectFormName="${capitalAssetInfoDetailsName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
				field="buildingSubRoomNumber" lookup="false" inquiry="false"
				businessObjectValuesMap="${capitalAssetInfo.valuesMap}"/>
			
			<c:if test="${!readOnly}">
				<td class="${dataCellCssClass}">  
					<div style="text-align: center;">			 
						 <html:image property="methodToCall.deleteCapitalAssetInfoDetail.line${status.index}" 
							src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" 
							title="delete the capital Asset Information Detail line ${status.index}"
							alt="delete the capital Asset Information Detail line ${status.index}" styleClass="tinybutton" />
					</div>
				</td>
			</c:if>																									 
	   </tr>
	   </c:forEach>
	</table>
</c:if>