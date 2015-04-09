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
<c:set var="assetAttributes" value="${DataDictionary.Asset.attributes}" />
<c:set var="requiredCapitalAssetNumber" value="* Asset Number" />
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
<c:set var="tabKey" value="${kfunc:generateTabKey(subTabTitle)}"/>

<kul:tab tabTitle="Assets" defaultOpen="true" tabErrorKey="capitalAssetNumber*,commonErrorSection">
	<div class="tab-container" id="assets" align="center">		
		<table class="datatable" width="100%" border="0" cellpadding="0" cellspacing="0" border="1">
			<tr>
				<td colspan="3" class="subhead">
			    	<span class="subhead-left">Assets</span>
			  	</td>
			</tr>			
			<c:if test="${!readOnly}">
			    <tr>
				    <th width="10%">${requiredCapitalAssetNumber }</th>
					<td class="infoline" valign="top" width="84%">															   
						<kul:htmlControlAttribute attributeEntry="${assetAttributes.capitalAssetNumber}" property="capitalAssetNumber"/>				
						<kul:multipleValueLookup boClassName="org.kuali.kfs.module.cam.businessobject.Asset" lookedUpCollectionName="assetPaymentAssetDetail"/>
						&nbsp;&nbsp;&nbsp;&nbsp;
					    <html:image property="methodToCall.insertAssetPaymentAssetDetail" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" styleClass="tinybutton" alt="Add an asset" title="Add an asset"/>
					</td>
			    </tr>
			</c:if>
			<tr>
				<td colspan="7" style="padding: 0px;border-bottom-style:none;border-left-style:none;border-right-style:none;border-top-style:none;">
					<cams:assetPaymentsAssetInformation/>
				</td>
			</tr>
		</table>
    </div>    
</kul:tab>
