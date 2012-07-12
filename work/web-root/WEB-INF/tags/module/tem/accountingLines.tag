<%--
 Copyright 2007-2009 The Kuali Foundation
 
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
	<a name="${TEMConstants.SOURCE_ANCHOR }" id="${TEMConstants.SOURCE_ANCHOR }"></a>
	<kul:tab tabTitle="Accounting Lines" defaultOpen="true" tabErrorKey="${KFSConstants.SOURCE_ACCOUNTING_LINE_ERROR_PATTERN}">
		<c:if test="${fullEntryMode }">
			<div class="tab-container" align="left">
		    	<c:if test="${fn:length(KualiForm.document.sourceAccountingLines) > 0}" >
		    		<html:image
						property="methodToCall.resetAccountingLines"
						src="${ConfigProperties.externalizable.images.url}tinybutton-restartaccountinglines.gif"
						alt="Restart Accounting Lines" title="Restart Accounting Lines"
						styleClass="tinybutton" />    		
				</c:if>
			</div>
		</c:if>
		<sys-java:accountingLines>
			<sys-java:accountingLineGroup newLinePropertyName="newSourceLine" collectionPropertyName="document.sourceAccountingLines" collectionItemPropertyName="document.sourceAccountingLine" attributeGroupName="source" />
		</sys-java:accountingLines>
	</kul:tab>