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
<%@ taglib uri="/WEB-INF/tlds/temfunc.tld" prefix="temfunc"%>

<c:set var="documentAttributes" value="${DataDictionary.TravelReimbursementDocument.attributes}" />
<c:set var="distributionAttributes" value="${DataDictionary.AccountingDistribution.attributes}" />

<c:if test="${accountDistribution}">
<a name="${TEMConstants.SUMMARY_ANCHOR }" id="${TEMConstants.SUMMARY_ANCHOR }"></a>
	<kul:tab tabTitle="Summary by Object Code" defaultOpen="true"
		tabErrorKey="${TemKeyConstants.TRVL_REIMB_ACCOUNTING_DISTRIBUTION_ERRORS}">
		<div class="tab-container" align="center">
			<h3>Accounting Distribution</h3>
			<table class="datatable" summary="Summary by Object Code"
				cellpadding="0">
				<tr>
					<th class="bord-1-b">&nbsp;</th>
					<th class="bord-1-b"><kul:htmlAttributeLabel attributeEntry="${distributionAttributes.objectCode}" noColon="true" /></th>
					<th class="bord-1-b"><kul:htmlAttributeLabel attributeEntry="${distributionAttributes.cardType}" noColon="true" /></th>
					<th class="bord-1-b"><kul:htmlAttributeLabel attributeEntry="${distributionAttributes.objectCodeName}" noColon="true" /></th>
					<th class="bord-1-b"><kul:htmlAttributeLabel attributeEntry="${distributionAttributes.subTotal}" noColon="true" /></th>
					<th class="bord-1-b"><kul:htmlAttributeLabel attributeEntry="${distributionAttributes.remainingAmount}" noColon="true" /></th>
				</tr>
				<c:set var="tempValue" value="${0.00}" />
				<c:set var="selectedCount" value="${0}" />
				<c:set var="selectedValue" value="${0.00}" />
				<c:choose>
					<c:when test="${fn:length(KualiForm.distribution) > 0}">
						<logic:iterate indexId="ctr" name="KualiForm" property="distribution" id="dist">
							<tr>
								<td class="bord-1-b" align="center">
									<c:choose>
										<c:when test="${(dist.remainingAmount > 0 && fullEntryMode && !dist.disabled)}">
											<kul:htmlControlAttribute
												attributeEntry="${distributionAttributes.selected}"
												property="distribution[${ctr}].selected"
												readOnly="false" />
										</c:when>
										<c:otherwise>
											&nbsp;
										</c:otherwise>
									</c:choose>									
								</td>
								<td class="bord-1-b">
									<kul:htmlControlAttribute
										attributeEntry="${distributionAttributes.objectCode}"
										property="distribution[${ctr}].objectCode"
										readOnly="true" />
								</td>
								<td class="bord-1-b">
									<kul:htmlControlAttribute
										attributeEntry="${distributionAttributes.cardType}"
										property="distribution[${ctr}].cardType"
										readOnly="true" />
								</td>
								<td class="bord-1-b">
									<kul:htmlControlAttribute
										attributeEntry="${distributionAttributes.objectCodeName}"
										property="distribution[${ctr}].objectCodeName"
										readOnly="true" />
								</td>
								<td class="bord-1-b">
									<kul:htmlControlAttribute
										attributeEntry="${distributionAttributes.subTotal}"
										property="distribution[${ctr}].subTotal"
										readOnly="true" />
								</td>
								<td class="bord-1-b">
									<kul:htmlControlAttribute
										attributeEntry="${distributionAttributes.remainingAmount}"
										property="distribution[${ctr}].remainingAmount"
										readOnly="true" />
								</td>
								<c:set var="tempValue" value="${temfunc:add(dist.remainingAmount,tempValue)}" />
								<!-- c:set var="tempValue" value="${temfunc:add((dist.disabled?0:dist.remainingAmount),tempValue)}" / -->
								<c:set var="selectedCount" value="${selectedCount + (dist.selected?1:0)}" />
								<c:set var="selectedValue" value="${temfunc:add(selectedValue,(dist.selected?dist.subTotal:0))}" />
							</tr>
						</logic:iterate>
					</c:when>
					<c:otherwise>
						<td class="bord-1-b" colspan="6" align="left">No Expenses to show.</th>
					</c:otherwise>
				</c:choose>
				
				<tr>
					<th colspan="4">
						<div align="left">
							<c:choose>
								<c:when test="${tempValue == 0 || selectedCount == 0}">
									<img src="<c:out value="${ConfigProperties.externalizable.images.url}" />tinybutton-setdist1.gif" />
								</c:when>
								<c:otherwise>
									<html:image
										property="methodToCall.selectAllDistributions.line${(fn:length(KualiForm.distribution) == selectedCount)?TemConstants.UNSELECT_ALL_INDEX:TemConstants.SELECT_ALL_INDEX }"
										src="${ConfigProperties.externalizable.images.url}tinybutton-${(fn:length(KualiForm.distribution) == selectedCount)?'un':''}selectall.gif"
										alt="${(fn:length(KualiForm.distribution) == selectedCount)?'Uns':'S'}elect All Distributions" 
										title="${(fn:length(KualiForm.distribution) == selectedCount)?'Uns':'S'}elect All Distributions"
										styleClass="tinybutton" />
									<html:image
										property="methodToCall.distribute"
										src="${ConfigProperties.externalizable.images.url}tinybutton-setdist.gif"
										alt="Setup Distribution" title="Setup Distribution"
										styleClass="tinybutton" />
								</c:otherwise>
							</c:choose>							
						</div>
					</th>
					<th class="bord-1-b" align="right">Total Remaining:</th>
					<c:set var="numValue"
						value="${remainingDistribution == null ? tempValue : remainingDistribution}" />
					<c:set var="formattedValue" value="${fn:endsWith(numValue,'.0')?'0':''}" />
					<td class="inline">${numValue}${formattedValue} USD
						<input type="hidden" id="selectedDistributionAmount" value="${KualiForm.selectedDistributionRemainingAmount}" />
						<input type="hidden" id="distributionAmount" value="${KualiForm.fullDistributionSubTotal}" />
					</td>
				</tr>
			</table>
		</div>
	</kul:tab>
</c:if>
