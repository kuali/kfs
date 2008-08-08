<%--
 Copyright 2007 The Kuali Foundation.
 
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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields." %>
<%@ attribute name="itemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="camsItemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="camsSystemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="camsAssetAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="camsLocationAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>

<kul:tab tabTitle="CAMS" defaultOpen="true" tabErrorKey="${PurapConstants.ITEM_TAB_ERRORS}">
	<div class="tab-container" align=center>
		
    <table cellpadding="0" cellspacing="0" class="datatable" summary="System Selection">
	<tr>
		<td colspan="2" class="subhead">System Selection</td>
	</tr>

	<tr>
 		<th width="20%" align=right valign=middle class="bord-l-b">
			<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.capitalAssetSystemTypeCode}" /></div>
		</th>
        <td align=left valign=middle class="datacell">
			<kul:htmlControlAttribute attributeEntry="${documentAttributes.capitalAssetSystemTypeCode}" property="document.capitalAssetSystemTypeCode" extraReadOnlyProperty="document.capitalAssetSystemType.capitalAssetSystemTypeDescription" readOnly="${!empty KualiForm.document.purchasingCapitalAssetSystems}"/>
		</td>
	</tr>
	<tr>
		<th align=right valign=middle class="bord-l-b">
			<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.capitalAssetSystemStateCode}" /></div>
		</th>
        <td align=left valign=middle class="datacell">
			<kul:htmlControlAttribute attributeEntry="${documentAttributes.capitalAssetSystemStateCode}" property="document.capitalAssetSystemStateCode" extraReadOnlyProperty="document.capitalAssetSystemState.capitalAssetSystemStateDescription" readOnly="${!empty KualiForm.document.purchasingCapitalAssetSystems}"/>
		</td>
	</tr>
	<tr>
		<th align=right valign=middle class="bord-l-b">
			Action:
       </th>
	   <td align=left valign=middle class="datacell">
			<c:choose>
			<c:when test="${empty KualiForm.document.purchasingCapitalAssetSystems}">
				<html:image property="methodToCall.selectSystem" src="${ConfigProperties.externalizable.images.url}tinybutton-select.gif" alt="select system" styleClass="tinybutton"/>
			</c:when>
			<c:otherwise>
			<html:image property="methodToCall.changeSystem" src="${ConfigProperties.externalizable.images.url}tinybutton-select.gif" alt="select system" styleClass="tinybutton"/>
			<html:image property="methodToCall.updateCamsView" src="${ConfigProperties.externalizable.images.url}tinybutton-updateview.gif" alt="Update Cams View" styleClass="tinybutton"/>
			</c:otherwise>
			</c:choose>
		</td>
	</tr>

	</table>

	<c:if test="${!empty KualiForm.document.purchasingCapitalAssetItems}">
		<purap:camsItems itemAttributes="${itemAttributes}" camsItemAttributes="${camsItemAttributes}" camsSystemAttributes="${camsSystemAttributes}" camsAssetAttributes="${camsAssetAttributes}" camsLocationAttributes="${camsLocationAttributes}" />
	</c:if>

	</div>
</kul:tab>