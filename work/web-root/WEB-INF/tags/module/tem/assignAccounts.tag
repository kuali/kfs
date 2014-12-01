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
