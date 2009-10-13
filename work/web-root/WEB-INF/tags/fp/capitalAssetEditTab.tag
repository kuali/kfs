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
<%@ tag description="render the capital edit tag that contains the given capital asset info"%>

<%@ attribute name="readOnly" required="false" description="Whether the capital asset information should be read only" %>

<c:set var="capitalAssetInfo" value="${KualiForm.document.capitalAssetInformation}" />
<c:set var="capitalAssetInfoName" value="document.capitalAssetInformation" />

<c:set var="newCapitalAssetInfo" value="${KualiForm.capitalAssetInformation}" />	
<c:set var="newCapitalAssetInfoName" value="capitalAssetInformation" />

<kul:tab tabTitle="Capital Edit" defaultOpen="false" tabErrorKey="${KFSConstants.EDIT_CAPITAL_ASSET_INFORMATION_ERRORS}" >
     <div class="tab-container" align="center">	 
	 <c:choose>
	 	<c:when test="${not empty capitalAssetInfo}">
	 		<fp:capitalAssetInfo capitalAssetInfo="${capitalAssetInfo}" capitalAssetInfoName="${capitalAssetInfoName}" readOnly="${readOnly}"/>
	 	</c:when>
	 	<c:when test="${not readOnly}">
	 		<fp:capitalAssetInfo capitalAssetInfo="${newCapitalAssetInfo}" capitalAssetInfoName="${newCapitalAssetInfoName}"/>
	 	</c:when>
	 </c:choose>
	 </div>	
</kul:tab>	 
