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


<c:set var="documentAttributes" value="${DataDictionary.ContractsGrantsInvoiceOnDemandDocument.attributes}" />

<kul:tab tabTitle="General" defaultOpen="true" tabErrorKey="${KFSConstants.CUSTOMER_INVOICE_DOCUMENT_GENERAL_ERRORS}">
	<div class="tab-container" align=center>
		<table cellpadding="0" cellspacing="0" class="datatable" summary="Invoice Criteria">
			<tr>
				<td colspan="4" class="subhead">Invoice Criteria</td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${documentAttributes.proposalNumber}" labelFor="document.proposalNumber" useShortLabel="false" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.proposalNumber.div">

						<kul:htmlControlAttribute attributeEntry="${documentAttributes.proposalNumber}" property="document.proposalNumber" readOnly="false" />
						<kul:lookup boClassName="org.kuali.kfs.module.cg.businessobject.Award" fieldConversions="proposalNumber:document.proposalNumber"
							lookupParameters="document.proposalNumber:proposalNumber" />
					</div>
				</td>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${documentAttributes.lastBilledDate}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.billedToDate.div">
						<kul:htmlControlAttribute attributeEntry="${documentAttributes.lastBilledDate}" property="document.lastBilledDate" readOnly="false" />
					</div>
				</td>

			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${documentAttributes.awardBeginningDate}" useShortLabel="false" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.awardBeginningDate.div">
						<kul:htmlControlAttribute attributeEntry="${documentAttributes.awardBeginningDate}" property="document.awardBeginningDate" readOnly="false" />
					</div>
				</td>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${documentAttributes.awardEndingDate}" useShortLabel="false" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.awardEndingDate.div">
						<kul:htmlControlAttribute attributeEntry="${documentAttributes.awardEndingDate}" property="document.awardEndingDate" readOnly="false" />
					</div>
				</td>

			</tr>

			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${documentAttributes.awardBillingFrequency}" useShortLabel="false" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.billingFrequency.div">
						<kul:htmlControlAttribute attributeEntry="${documentAttributes.awardBillingFrequency}" property="document.awardBillingFrequency"
							readOnly="false" />
						<kul:lookup boClassName="org.kuali.kfs.module.cg.businessobject.BillingFrequency" fieldConversions="frequency:document.awardBillingFrequency"
							lookupParameters="document.awardBillingFrequency:frequency" />
					</div>
				</td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${documentAttributes.awardTotal}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.awardTotal.div">
						<kul:htmlControlAttribute attributeEntry="${documentAttributes.awardTotal}" property="document.awardTotal" readOnly="false" />

					</div>
				</td>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${documentAttributes.amountRemainingToBill}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 25%;">
					<div id="document.amountRemainingToBill.div">
						<kul:htmlControlAttribute attributeEntry="${documentAttributes.amountRemainingToBill}" property="document.amountRemainingToBill"
							readOnly="false" />
					</div>
				</td>
			</tr>

			<tr>
				<td height="30" colspan=4 class="infoline"><center>
						<html:image property="methodToCall.search" value="search" src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_search.gif"
							styleClass="tinybutton" alt="search" title="search" border="0" />
						<html:image property="methodToCall.clearValues" value="clearValues" src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_clear.gif"
							styleClass="tinybutton" alt="clear" title="clear" border="0" />
					</center></td>
			</tr>
		</table>
	</div>
</kul:tab>
