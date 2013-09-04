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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<%@ taglib uri="/WEB-INF/tlds/temfunc.tld" prefix="temfunc"%>

<c:set var="documentAttributes" value="${DataDictionary.TravelAuthorizationDocument.attributes}" />
<c:set var="perDiemAttributes" value="${DataDictionary.PerDiem.attributes}" />
<c:set var="perDiemExpensesAttributes" value="${DataDictionary.PerDiemExpense.attributes}" />
<c:set var="mileageRateAttributes" value="${DataDictionary.MileageRate.attributes}" />

<jsp:useBean id="paramMap" class="java.util.HashMap" />

<c:set var="showLodging" value="${KualiForm.showLodging}" />
<c:set var="showMiles" value="${KualiForm.showMileage}" />
<c:set var="showPerDiem" value="${KualiForm.showPerDiem}" />
<c:set var="showPerDiemBreakdown" value="${KualiForm.showPerDiemBreakdown}" />
<c:set var="isTR" value="${KualiForm.docTypeName == TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT}" />
<c:set var="showAllPerDiemCategories" value="${KualiForm.showAllPerDiemCategories}" />
<c:set var="masterMealsAndIncidentalsTotal" value="0.00" />
<c:set var="masterMilesTotal" value="0" />
<c:set var="masterMileageTotal" value="0.00" />
<c:set var="masterLodgingTotal" value="0.00" />
<c:set var="masterDailyTotal" value="0.00" />

<c:if test="${showAllPerDiemCategories}">
	<kul:tab tabTitle="${KualiForm.perDiemLabel}" defaultOpen="true" tabErrorKey="${TemKeyConstants.TRVL_PER_DIEM_EXPENSES}">
		<div class="tab-container" align=center>
			<h3>${KualiForm.perDiemLabel}</h3>
			<table cellpadding="0" class="datatable" summary="Destination, Miles and Meals">
				<c:forEach items="${KualiForm.document.perDiemExpenses}" var="perDiemExpense" varStatus="perDiemIndex">
					<c:set target="${paramMap}" property="queryDate" value="${perDiemExpense.mileageDate}" />
					<c:set var="isCustom" value="${KualiForm.document.perDiemExpenses[perDiemIndex.count- 1].perDiem.id == TemConstants.CUSTOM_PER_DIEM_ID}" />
					<c:set var="colspan" value="${!showPerDiemBreakdown && !isCustom && !isTR ? 10 : 14}" />
					<c:set var="mileageDate" value="${KualiForm.document.perDiemExpenses[perDiemIndex.count - 1].mileageDate}" />
					
					<tr>
						<td colspan="<c:out value="${colspan}"/>" class="tab-subhead">Trip Detail: <fmt:formatDate value="${mileageDate}" type="date" pattern="MM/dd/yyyy"/>
						</td>
					</tr>
					<tr>
			
						<kul:htmlAttributeHeaderCell attributeEntry="${perDiemExpensesAttributes.countryState}" />
						<kul:htmlAttributeHeaderCell attributeEntry="${perDiemExpensesAttributes.county}" />
						<kul:htmlAttributeHeaderCell attributeEntry="${perDiemExpensesAttributes.primaryDestination}" />
						<kul:htmlAttributeHeaderCell attributeEntry="${perDiemExpensesAttributes.personal}" />

						<c:if test="${showPerDiemBreakdown || isCustom || isTR}">
							<kul:htmlAttributeHeaderCell attributeEntry="${perDiemExpensesAttributes.breakfast}" />
							<kul:htmlAttributeHeaderCell attributeEntry="${perDiemExpensesAttributes.lunch}" />
							<kul:htmlAttributeHeaderCell attributeEntry="${perDiemExpensesAttributes.dinner}" />
							<kul:htmlAttributeHeaderCell attributeEntry="${perDiemExpensesAttributes.incidentalsValue}" />
						</c:if>
						
						<c:if test="${showPerDiem}">
							<th colspan="1" rowspan="1">&nbsp;Meals and Incidentals</th>
						</c:if>
						
						<c:if test="${showLodging}">
							<kul:htmlAttributeHeaderCell attributeEntry="${perDiemExpensesAttributes.lodging}" />
						</c:if>
						
						<c:if test="${showMiles}">
							<kul:htmlAttributeHeaderCell attributeEntry="${perDiemExpensesAttributes.miles}" />
							<kul:htmlAttributeHeaderCell attributeEntry="${perDiemExpensesAttributes.mileageRateId}" />
							<th colspan="1" rowspan="1">&nbsp;Mileage Total</th>
						</c:if>

						<th colspan="1" rowspan="1">&nbsp;Daily Total</th>
					</tr>
					<tr>
					<td valign=top class="datacell">
						<c:choose>
						<c:when test="${isCustom}">
							<kul:htmlControlAttribute
								attributeEntry="${perDiemExpensesAttributes.countryStateText}"
								property="document.perDiemExpenses[${perDiemIndex.count - 1}].countryState"
								readOnly="${!isCustom && !fullEntryMode}" />
							
						</c:when>
						<c:otherwise>
							<kul:htmlControlAttribute
								attributeEntry="${perDiemExpensesAttributes.countryState}"
								property="document.perDiemExpenses[${perDiemIndex.count - 1}].countryState"
								readOnly="${!isCustom && !fullEntryMode}" />
						</c:otherwise>
						</c:choose>
						</td>
						<td valign=top class="datacell"><kul:htmlControlAttribute
								attributeEntry="${perDiemExpensesAttributes.county}"
								property="document.perDiemExpenses[${perDiemIndex.count - 1}].county"
								readOnly="${!isCustom && !fullEntryMode}" />
						</td>
						<td valign=top class="datacell"><kul:htmlControlAttribute
								attributeEntry="${perDiemExpensesAttributes.perDiemId}"
								property="document.perDiemExpenses[${perDiemIndex.count - 1}].primaryDestination"
								readOnly="${!isCustom && !fullEntryMode}" /> 
							<c:if test="${fullEntryMode}">
								<kul:lookup
									boClassName="org.kuali.kfs.module.tem.businessobject.PrimaryDestination"
									fieldConversions="id:document.perDiemExpenses[${perDiemIndex.count - 1}].perDiemId"
									readOnlyFields="tripTypeCode"
									lookupParameters="document.perDiemExpenses[${perDiemIndex.count - 1}].countryState:countryState,document.perDiemExpenses[${perDiemIndex.count - 1}].county:county,document.perDiemExpenses[${perDiemIndex.count - 1}].primaryDestination:primaryDestinationName,document.tripTypeCode:tripTypeCode" />
								<c:if test="${!isCustom && enablePrimaryDestination}">
									<br />
									<br />
									<html:image
										property="methodToCall.customPerDiemExpenses.line${perDiemIndex.count - 1}"
										src="${ConfigProperties.externalizable.images.url}tinybutton-destinationnotfound.gif"
										alt="Manually Enter Per Diem Expense"
										title="Manually Enter Per Diem Expense"
										styleClass="tinybutton" />
								</c:if>
							</c:if>
						</td>
						<td valign=top class="datacell"><kul:htmlControlAttribute
								attributeEntry="${perDiemExpensesAttributes.personal}"
								property="document.perDiemExpenses[${perDiemIndex.count - 1}].personal"
								disabled="${!fullEntryMode}"
								onclick="checkPersonal(${perDiemIndex.count - 1});" />
						</td>
						<c:if test="${showPerDiemBreakdown || isCustom || isTR}">
							<c:set var="breakfastDisabled" value="document.perDiemExpenses[${perDiemIndex.count - 1}].breakfast" />
							<c:set var="lunchDisabled" value="document.perDiemExpenses[${perDiemIndex.count - 1}].lunch" />
							<c:set var="dinnerDisabled" value="document.perDiemExpenses[${perDiemIndex.count - 1}].dinner" />						
							<c:choose>
								<c:when test="${showPerDiemBreakdown || isCustom}">
									<td valign=top class="datacell">
										<input type="hidden" id="document.perDiemExpenses[${perDiemIndex.count - 1}].breakfastValue.holder" value="<bean:write name="KualiForm" property="document.perDiemExpenses[${perDiemIndex.count - 1}].breakfastValue" />" />
										<kul:htmlControlAttribute
											attributeEntry="${perDiemExpensesAttributes.breakfastValue}"
											property="document.perDiemExpenses[${perDiemIndex.count - 1}].unfilteredBreakfastValue"
											disabled="${!fullEntryMode || fn:contains(KualiForm.document.disabledProperties, breakfastDisabled)}" />
									</td>
									<td valign=top class="datacell">
										<input type="hidden" id="document.perDiemExpenses[${perDiemIndex.count - 1}].lunchValue.holder" value="<bean:write name="KualiForm" property="document.perDiemExpenses[${perDiemIndex.count - 1}].lunchValue" />" />
										<kul:htmlControlAttribute
											attributeEntry="${perDiemExpensesAttributes.lunchValue}"
											property="document.perDiemExpenses[${perDiemIndex.count - 1}].unfilteredLunchValue"
											disabled="${!fullEntryMode || fn:contains(KualiForm.document.disabledProperties, lunchDisabled)}" />
									</td>
									<td valign=top class="datacell">
										<input type="hidden" id="document.perDiemExpenses[${perDiemIndex.count - 1}].dinnerValue.holder" value="<bean:write name="KualiForm" property="document.perDiemExpenses[${perDiemIndex.count - 1}].dinnerValue" />" />
										<kul:htmlControlAttribute
											attributeEntry="${perDiemExpensesAttributes.dinnerValue}"
											property="document.perDiemExpenses[${perDiemIndex.count - 1}].unfilteredDinnerValue"
											disabled="${!fullEntryMode || fn:contains(KualiForm.document.disabledProperties, dinnerDisabled)}" />
									</td>
									<td valign=top class="datacell">
										<input type="hidden" id="document.perDiemExpenses[${perDiemIndex.count - 1}].incidentalsValue.holder" value="<bean:write name="KualiForm" property="document.perDiemExpenses[${perDiemIndex.count - 1}].incidentalsValue" />" />
										<kul:htmlControlAttribute
											attributeEntry="${perDiemExpensesAttributes.incidentalsValue}"
											property="document.perDiemExpenses[${perDiemIndex.count - 1}].unfilteredIncidentalsValue"
											disabled="${!fullEntryMode || KualiForm.document.perDiemExpenses[perDiemIndex.count - 1].personal}" />
									</td>								
								</c:when>					
								<c:when test="${isTR}">
									<td valign=top class="datacell"><kul:htmlControlAttribute
											attributeEntry="${perDiemExpensesAttributes.breakfast}"
											property="document.perDiemExpenses[${perDiemIndex.count - 1}].breakfast"
											disabled="${!fullEntryMode || fn:contains(KualiForm.document.disabledProperties, breakfastDisabled)}" />
									</td>
									<td valign=top class="datacell"><kul:htmlControlAttribute
											attributeEntry="${perDiemExpensesAttributes.lunch}"
											property="document.perDiemExpenses[${perDiemIndex.count - 1}].lunch"
											disabled="${!fullEntryMode || fn:contains(KualiForm.document.disabledProperties,lunchDisabled)}" />
									</td>
									<td valign=top class="datacell"><kul:htmlControlAttribute
											attributeEntry="${perDiemExpensesAttributes.dinner}"
											property="document.perDiemExpenses[${perDiemIndex.count - 1}].dinner"
											disabled="${!fullEntryMode || fn:contains(KualiForm.document.disabledProperties,dinnerDisabled)}" />
									</td>
									<td valign=top class="datacell"><kul:htmlControlAttribute
											attributeEntry="${perDiemExpensesAttributes.incidentalsValue}"
											property="document.perDiemExpenses[${perDiemIndex.count - 1}].unfilteredIncidentalsValue"
											disabled="true" />
									</td>									
								</c:when>
							</c:choose>
						</c:if>
						<c:if test="${showPerDiem}">
							<td valign=top class="datacell">
								<input type="hidden" id="document.perDiemExpenses[${perDiemIndex.count - 1}].mealsAndIncidentals.holder" value="${KualiForm.document.perDiemExpenseTotals[perDiemIndex.count - 1]['mealsAndIncidentalsTotal']}" />								
								<div id="document.perDiemExpenses[${perDiemIndex.count - 1}].mealsAndIncidentals">${KualiForm.document.perDiemExpenseTotals[perDiemIndex.count-1]['mealsAndIncidentalsTotal']}</div>
							</td>
						</c:if>
						<c:if test="${showLodging}">
							<c:set var="lodgingDisabled" value="document.perDiemExpenses[${perDiemIndex.count- 1}].lodging" />
							<td valign=top class="datacell">
								<input type="hidden" id="document.perDiemExpenses[${perDiemIndex.count - 1}].lodging.holder" value="<bean:write name="KualiForm" property="document.perDiemExpenses[${perDiemIndex.count - 1}].lodging" />" />                                    
								<kul:htmlControlAttribute
									attributeEntry="${perDiemExpensesAttributes.lodging}"
									property="document.perDiemExpenses[${perDiemIndex.count - 1}].unfilteredLodging"
									disabled="${fn:contains(KualiForm.document.disabledProperties, lodgingDisabled)}"
									readOnly="${!fullEntryMode}" />
							</td>
						</c:if>
						<c:if test="${showMiles}">
							<td valign=top class="datacell">
									<input type="hidden" id="document.perDiemExpenses[${perDiemIndex.count - 1}].miles.holder" value="<bean:write name="KualiForm" property="document.perDiemExpenses[${perDiemIndex.count - 1}].miles" />" />
									<kul:htmlControlAttribute
									attributeEntry="${perDiemExpensesAttributes.miles}"
									property="document.perDiemExpenses[${perDiemIndex.count - 1}].unfilteredMiles"
									readOnly="${!fullEntryMode}" />
							</td>
							<td valign=top class="datacell">
								<input type="hidden" id="document.perDiemExpenses[${perDiemIndex.count - 1}].mileageRateId.holder" value="<bean:write name="KualiForm" property="document.perDiemExpenses[${perDiemIndex.count - 1}].mileageRateId" />" />                               
								<html:select
									styleId="document.perDiemExpenses[${perDiemIndex.count - 1}].mileageRateId"
									property="document.perDiemExpenses[${perDiemIndex.count - 1}].mileageRateId"
									disabled="${!fullEntryMode}">
									<c:forEach
										items="${temfunc:getOptionList('org.kuali.kfs.module.tem.businessobject.options.MileageRateValuesFinder', paramMap)}"
										var="option">
										<c:set var="mileageSelected" value="" />

										<c:if
											test="${option.key == KualiForm.document.perDiemExpenses[perDiemIndex.count - 1].mileageRateId}">
											<c:set var="mileageSelected" value="selected" />
										</c:if>

										<option value="${option.key}"${mileageSelected}>${option.value}</option>
									</c:forEach>
								</html:select> 
								<c:if test="${fn:length(temfunc:getOptionList('org.kuali.kfs.module.tem.businessobject.options.MileageRateValuesFinder', paramMap)) == 0}">
									<div align="left">No Rates Available</div>
								</c:if>
							</td>
							<td valign=top class="datacell">
								<input type="hidden" id="document.perDiemExpenses[${perDiemIndex.count - 1}].mileageTotal.holder" value="<bean:write name="KualiForm" property="document.perDiemExpenseTotals[${perDiemIndex.count - 1}].mileageTotal" />" />
								<div id="document.perDiemExpenses[${perDiemIndex.count - 1}].mileageTotal">${KualiForm.document.perDiemExpenseTotals[perDiemIndex.count - 1]['mileageTotal']}</div>
							</td>
						</c:if>
						<td valign=top class="datacell">
							<input type="hidden" id="document.perDiemExpenses[${perDiemIndex.count - 1}].total.holder" value="<bean:write name="KualiForm" property="document.perDiemExpenseTotals[${perDiemIndex.count - 1}].dailyTotal" />" />
							<div id="document.perDiemExpenses[${perDiemIndex.count - 1}].total">${KualiForm.document.perDiemExpenseTotals[perDiemIndex.count - 1]['dailyTotal']}</div>
						</td>
					</tr>
					<c:if test="${KualiForm.document.tripTypeCode == 'INT'}">
						<tr>
							<td colspan="${colspan}">
								<table cellpadding="0" class="datatable" summary="International Accommodation Information">
									<tr>
										<td colspan="6" class="tab-subhead">International Accommodation Information</td>
									</tr>
									<tr>
										<th class="bord-l-b">
											<div align="right">
												<kul:htmlAttributeLabel attributeEntry="${perDiemExpensesAttributes.accommodationTypeCode}" />
											</div>
										</th>
										<td valign=top class="datacell"><kul:htmlControlAttribute
												attributeEntry="${perDiemExpensesAttributes.accommodationTypeCode}"
												property="document.perDiemExpenses[${perDiemIndex.count - 1}].accommodationTypeCode"
												readOnly="${!fullEntryMode}" />
										</td>
										<th class="bord-l-b">
											<div align="right">
												<kul:htmlAttributeLabel attributeEntry="${perDiemExpensesAttributes.accommodationName}" />
											</div>
										</th>
										<td valign=top class="datacell"><kul:htmlControlAttribute
												attributeEntry="${perDiemExpensesAttributes.accommodationName}"
												property="document.perDiemExpenses[${perDiemIndex.count - 1}].accommodationName"
												readOnly="${!fullEntryMode}" />
										</td>
										<th class="bord-l-b" rowspan="2">
											<div align="right">
												<kul:htmlAttributeLabel attributeEntry="${perDiemExpensesAttributes.accommodationAddress}" />
											</div>
										</th>
										<td valign=top class="datacell" rowspan="2"><kul:htmlControlAttribute
												attributeEntry="${perDiemExpensesAttributes.accommodationAddress}"
												property="document.perDiemExpenses[${perDiemIndex.count - 1}].accommodationAddress"
												readOnly="${!fullEntryMode}" />
										</td>
									</tr>
									<tr>
										<td valign=top class="infoline" colspan="2">&nbsp;</td>
										<th class="bord-l-b">
											<div align="right">
												<kul:htmlAttributeLabel attributeEntry="${perDiemExpensesAttributes.accommodationPhoneNum}" />
											</div>
										</th>
										<td valign=top class="datacell"><kul:htmlControlAttribute
												attributeEntry="${perDiemExpensesAttributes.accommodationPhoneNum}"
												property="document.perDiemExpenses[${perDiemIndex.count - 1}].accommodationPhoneNum"
												readOnly="${!fullEntryMode}" />
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</c:if>
					<c:if test="${(fn:length(KualiForm.document.perDiemExpenses) != (perDiemIndex.count)) && fullEntryMode}">
						<tr>
							<td colspan="${colspan}">
								<div align="center">
									<html:image
										property="methodToCall.copyDownPerDiemExpenses.line${perDiemIndex.count - 1}"
										src="${ConfigProperties.externalizable.images.url}tinybutton-copydown.gif"
										alt="Copy Down Per Diem Expense"
										title="Copy Down Per Diem Expense" styleClass="tinybutton" />
								</div>
							</td>
						</tr>
					</c:if>
					<c:if test="${KualiForm.document.perDiemExpenses[perDiemIndex.count - 1].personal}">
						<script>checkPersonal(${perDiemIndex.count - 1});</script>
					</c:if>
				</c:forEach>
			</table>
			<div id="perdiem-buttons" align="center" valign="bottom">
				<c:choose>
					<c:when test="${fullEntryMode}">
						<c:choose>
							<c:when test="${fn:length(KualiForm.document.perDiemExpenses) < 1}">							
								<c:if test="${KualiForm.document.tripType.usePerDiem}">
									<html:image
										src="${ConfigProperties.externalizable.images.url}tinybutton-createperdiem.gif"
										styleClass="tinybutton"
										property="methodToCall.updatePerDiemExpenses"
										alt="Create Per Diem Rows" title="Create Per Diem Rows" />
								</c:if>
								<c:if test="${!KualiForm.document.tripType.usePerDiem}">
									<div>Per Diem entry is not allowed for this Trip Type [${KualiForm.document.tripType.code}].</div>
								</c:if>
							</c:when>
							<c:otherwise>
								<tem:perDiemExpenseTotals/>
								<html:image
									src="${ConfigProperties.externalizable.images.url}tinybutton-updateperdiem.gif"
									styleClass="tinybutton"
									property="methodToCall.updatePerDiemExpenses"
									alt="Create Per Diem Rows" title="Create Per Diem Rows" />
								<html:image
									src="${ConfigProperties.externalizable.images.url}tinybutton-removeperdiem.gif"
									styleClass="tinybutton"
									property="methodToCall.clearPerDiemExpenses"
									alt="Remove Per Diem Rows" title="Remove Per Diem Rows" />
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<tem:perDiemExpenseTotals/>
					</c:otherwise>
				</c:choose>
			</div>

		</div>
	</kul:tab>
</c:if>