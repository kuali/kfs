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

<c:set var="capitalAssetInfoSize" value="${fn:length(KualiForm.document.capitalAssetInformation)}" />	
<c:set var="defaultOpen" value="false"/>

<c:if test="${capitalAssetInfoSize > 0}" >
	<c:set var="defaultOpen" value="true"/>
</c:if>

<kul:tab tabTitle="Create Capital Assets" defaultOpen="${defaultOpen}" tabErrorKey="${KFSConstants.EDIT_CAPITAL_ASSET_INFORMATION_ERRORS}" >
     <div class="tab-container" align="center">
	 <h3>Create Capital Assets</h3>
	 <table class="datatable" style="border-top: 1px solid rgb(153, 153, 153);"  cellpadding="0" cellspacing="0" summary="Capital Asset Information">
	     <c:if test="${capitalAssetInfoSize <= 0}">
			<tr>
				<td class="datacell" height="50" colspan="5"><div align="center">There are currently no Capital Assets entries associated with this Transaction Processing document.</div></td>
			</tr>
		</c:if>
     </table>
     <c:if test="${capitalAssetInfoSize > 0}">	 
	 	<fp:capitalAssetInfo readOnly="${readOnly}"/>
	 </c:if>     
	 </div>	
</kul:tab>	 
