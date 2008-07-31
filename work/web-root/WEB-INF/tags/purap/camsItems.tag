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

<kul:tab tabTitle="Cams" defaultOpen="false" tabErrorKey="${PurapConstants.ITEM_TAB_ERRORS}">
	<div class="tab-container" align=center>
		
    <table cellpadding="0" cellspacing="0" class="datatable" summary="Items Section">
	<tr>
		<td colspan="2" class="subhead">System Selection</td>
	</tr>

	<c:if test="${empty document.purchasingCapitalAssetItems}">		    
	<tr>
 		<th align=right valign=middle class="bord-l-b">
			<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.capitalAssetSystemTypeCode}" /></div>
		</th>
        <td align=left valign=middle class="datacell">
			<kul:htmlControlAttribute attributeEntry="${documentAttributes.capitalAssetSystemTypeCode}" property="document.capitalAssetSystemTypeCode" readOnly="${false}"/>
		</td>
	</tr>
	<tr>
		<th align=right valign=middle class="bord-l-b">
			<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.capitalAssetSystemStateCode}" /></div>
		</th>
        <td align=left valign=middle class="datacell">
			&nbsp;
		</td>
	</tr>
	<tr>
		<th colspan="2" align=center valign=middle class="bord-l-b">
			<html:image property="methodToCall.selectSystem" src="${ConfigProperties.kr.externalizable.images.url}select_system.gif" alt="select system" styleClass="tinybutton"/>
       </th>
	</tr>
	</c:if>

	<c:if test="${false}">
	<tr>
		<td class="tab-subhead" style="border-right: none;">
    		
		</td>
	</tr>
	</c:if>

	</table>

	</div>
</kul:tab>
