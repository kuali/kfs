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
	<a name="${TEMConstants.DISTRIBUTION_ANCHOR }" id="${TEMConstants.DISTRIBUTION_ANCHOR }"></a>
	<kul:tab tabTitle="Assign Accounts" defaultOpen="${fullEntryMode }" tabErrorKey="${TemKeyConstants.TRVL_ACCOUNT_DIST}">
	<c:choose>
		<c:when test="${KualiForm.hasSelectedDistributionRemainingAmount}">
			<sys-java:accountingLines>
		        <sys-java:accountingLineGroup newLinePropertyName="accountDistributionnewSourceLine" collectionPropertyName="accountDistributionsourceAccountingLines" collectionItemPropertyName="accountDistributionsourceAccountingLines" attributeGroupName="accountDistribution" />
		    </sys-java:accountingLines>
		    <div class="tab-container" align="left">
		    	<c:choose>
		    		<c:when test="${fn:length(KualiForm.accountDistributionsourceAccountingLines) > 0}" >
		    			<html:image
							property="methodToCall.distributeAccountingLines"
							src="${ConfigProperties.externalizable.images.url}tinybutton-assignaccounts.gif"
							alt="Assign Accounts" title="Assign Accounts"
							styleClass="tinybutton" />
		    		</c:when>
		    		<c:otherwise>
		    			<img src="${ConfigProperties.externalizable.images.url}tinybutton-assignaccounts1.gif" alt="Assign Accounts Disabled" title="Assign Accounts Disabled" />
		    		</c:otherwise>
		    	</c:choose>
			    
				<hr />
			</div>
		</c:when>
		<c:otherwise>
			<div class="tab-container" align="left">
				${TemConstants.ASSIGN_ACCOUNTS_DISABLED_MESSAGE }
			</div>
		</c:otherwise>
	</c:choose>
	
	</kul:tab>