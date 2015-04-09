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
<%@ taglib uri="/WEB-INF/tlds/temfunc.tld" prefix="temfunc"%>

<c:set var="documentAttributes" value="${DataDictionary.TravelReimbursementDocument.attributes}" />
<c:set var="distributionAttributes" value="${DataDictionary.AccountingDistribution.attributes}" />

<c:if test="${accountDistribution}">
<a name="${TEMConstants.SUMMARY_ANCHOR }" id="${TEMConstants.SUMMARY_ANCHOR }"></a>
	<kul:tab tabTitle="Summary by Object Code" defaultOpen="true"
		tabErrorKey="${TemKeyConstants.TRVL_REIMB_ACCOUNTING_DISTRIBUTION_ERRORS}">
		<div class="tab-container" align="center">
			<h3>Accounting Distribution</h3>
			<input type="hidden" id="accountDistributionSize" name="accountDistributionSize" value="<c:out value="${fn:length(KualiForm.distribution)}" />" />
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
												readOnly="false"
												onclick="recalculateDistributionRemainingAmount();"/>
											<input type="hidden" id="distribution[<c:out value="${ctr}"/>].remainingAmount" name="distribution[<c:out value="${ctr}"/>].remainingAmount" value="<c:out value="${KualiForm.distribution[ctr].remainingAmount}"/>" />
											<input type="hidden" id="distribution[<c:out value="${ctr}"/>].subTotal" name="distribution[<c:out value="${ctr}"/>].subTotal" value="<c:out value="${KualiForm.distribution[ctr].subTotal}"/>" />
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
								<c:set var="selectedCount" value="${selectedCount + (dist.selected ? 1 : 0)}" />
								<c:set var="selectedValue" value="${temfunc:add(selectedValue,(dist.selected ? dist.subTotal : 0))}" />
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
										property="methodToCall.selectAllDistributions.line${(fn:length(KualiForm.distribution) == selectedCount) ? TemConstants.UNSELECT_ALL_INDEX : TemConstants.SELECT_ALL_INDEX }"
										src="${ConfigProperties.externalizable.images.url}tinybutton-${(fn:length(KualiForm.distribution) == selectedCount)? 'un' : ''}selectall.gif"
										alt="${(fn:length(KualiForm.distribution) == selectedCount)? 'Uns' : 'S'}elect All Distributions" 
										title="${(fn:length(KualiForm.distribution) == selectedCount)? 'Uns' : 'S'}elect All Distributions"
										styleClass="tinybutton" />
								</c:otherwise>
							</c:choose>							
						</div>
					</th>
					<th class="bord-1-b" align="right">Total Remaining:</th>
					<c:set var="numValue"
						value="${remainingDistribution == null ? tempValue : remainingDistribution}" />
					<c:set var="formattedValue" value="${fn:endsWith(numValue,'.0') ? '0' : ''}" />
					<td class="inline">${numValue}${formattedValue} USD
						<input type="hidden" id="selectedDistributionAmount" value="${KualiForm.selectedDistributionRemainingAmount}" />
						<input type="hidden" id="distributionAmount" value="${KualiForm.fullDistributionSubTotal}" />
					</td>
				</tr>
			</table>
		</div>
	</kul:tab>
</c:if>
