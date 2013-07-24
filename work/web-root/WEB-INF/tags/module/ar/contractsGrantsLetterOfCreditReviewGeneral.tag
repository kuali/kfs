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


<c:set var="documentAttributes" value="${DataDictionary.ContractsGrantsLetterOfCreditReviewDocument.attributes}" />
<c:set var="awardAttributes" value="${DataDictionary.Award.attributes}" />

<kul:tab tabTitle="General" defaultOpen="true">
	<div class="tab-container" align=center>
		<h3>General</h3>
		<table cellpadding="0" cellspacing="0" class="datatable" summary="General Section">

			<tr>
				<th align=right valign=middle class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${documentAttributes.letterOfCreditFundGroupCode}" />
					</div>
				</th>
				<td><a
					href="${ConfigProperties.application.url}/kr/inquiry.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cg.businessobject.LetterOfCreditFundGroup&letterOfCreditFundGroupCode=${KualiForm.document.letterOfCreditFundGroupCode}"
					target="_blank"> <kul:htmlControlAttribute attributeEntry="${documentAttributes.letterOfCreditFundGroupCode}"
							property="document.letterOfCreditFundGroupCode" readOnly="true" />
				</a></td>
			</tr>
			<c:if test="${! empty KualiForm.document.letterOfCreditFundCode}">
				<tr>
					<th align=right valign=middle class="bord-l-b">
						<div align="right">
							<kul:htmlAttributeLabel attributeEntry="${documentAttributes.letterOfCreditFundCode}" />
						</div>
					</th>

					<td align=left valign=middle class="datacell" style="width: 50%;"><a
						href="${ConfigProperties.application.url}/kr/inquiry.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cg.businessobject.LetterOfCreditFund&letterOfCreditFundCode=${KualiForm.document.letterOfCreditFundCode}"
						target="_blank"> <kul:htmlControlAttribute attributeEntry="${documentAttributes.letterOfCreditFundCode}"
								property="document.letterOfCreditFundCode" readOnly="true" />
					</a></td>
				</tr>
			</c:if>
		</table>
	</div>
</kul:tab>
