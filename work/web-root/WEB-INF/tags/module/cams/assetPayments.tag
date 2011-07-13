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
