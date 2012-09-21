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
<%@ attribute name="detail"           required="false" description="The expense detail to create the form for."%>
<%@ attribute name="lineNumber"       required="true"  description="Line number for the record."%>
<%@ attribute name="detailLineNumber" required="false" description="Detail line number"%>
<%@ attribute name="detailObject"     required="true"  description="The actual object" type="org.kuali.kfs.module.tem.businessobject.ActualExpense"%>
<%@ attribute name="parentObject"     required="true"  description="The parent object" type="org.kuali.kfs.module.tem.businessobject.ActualExpense"%>

<c:set var="otherExpenseAttributes" value="${DataDictionary.ActualExpense.attributes}" />
<jsp:useBean id="paramMap" class="java.util.HashMap" />
<c:set target="${paramMap}" property="tripType" value="${KualiForm.document.tripTypeCode}" />
<c:set target="${paramMap}" property="travelerType" value="${KualiForm.document.traveler.travelerTypeCode}" />
<c:set target="${paramMap}" property="documentType" value="${KualiForm.docTypeName}" />
<c:set var="calcColspan" value="6" />

<tr>
	<c:choose>
		<c:when test="${detailLineNumber == null }">
			<th scope="row" class="infoline" rowspan="2">
				<div align="right">add:</div>
			</th>
		</c:when>
		<c:otherwise>
			<th scope="row" class="infoline" rowspan="2">
				<div align="center">${detailLineNumber+1}</div>
			</th>
		</c:otherwise>
	</c:choose>
	<td valign="top" class="infoline"><kul:htmlControlAttribute
			attributeEntry="${otherExpenseAttributes.expenseDate}"
			property="${detail}.expenseDate" readOnly="${!fullEntryMode}" />
	</td>
	<td valign="top" class="infoline">
		<c:choose>
			<c:when test="${!fullEntryMode || parentObject.mileage}">
				<c:out value="${detailObject.travelExpenseTypeCode.name}" />
			</c:when>
			<c:otherwise>
				<c:set target="${paramMap}" property="groupTravelCount" value="${fn:length(KualiForm.document.groupTravelers)}" /> 
				<html:select
					property="${detail}.travelCompanyCodeCode">
					<c:forEach items="${temfunc:getOptionList('org.kuali.kfs.module.tem.businessobject.options.TravelExpenseTypeValuesFinder', paramMap)}" var="option">
						<c:set var="isSelected" value="${detailObject.travelCompanyCodeCode == option.key}" />
						<option value="${option.key}" ${isSelected?'selected=true':'' }>${option.label}</option>
					</c:forEach>
				</html:select>
			</c:otherwise>
		</c:choose>
		<c:set var="strKey" value="${detail}.travelCompanyCodeCode" /> 
		<c:forEach items="${ErrorPropertyList}" var="key">
			<c:if test="${key == strKey}">
				<kul:fieldShowErrorIcon />
			</c:if>
		</c:forEach>
	</td>
	<%-- Show Mileage --%>
	<c:if test="${parentObject.mileageIndicator}">
		<c:set var="calcColspan" value="${calcColspan+2 }" />
		<td valign="top" class="infoline" align="center"><kul:htmlControlAttribute
				attributeEntry="${otherExpenseAttributes.miles}"
				property="${detail}.miles" readOnly="${!fullEntryMode}" onchange="updateMileage(this.id)" /></td>
		<td valign="top" class="infoline" align="center"><c:set
				target="${paramMap}" property="queryDate"
				value="${detailObject.expenseDate}" /> <html:select
				styleId="${detail}.mileageRateId" property="${detail}.mileageRateId"
				disabled="${!fullEntryMode}" onchange="updateMileage('${detail}.miles')">
				<c:forEach
					items="${temfunc:getOptionList('org.kuali.kfs.module.tem.businessobject.options.MileageRateValuesFinder', paramMap)}"
					var="option">
					<c:set var="mileageSelected" value="" />

					<c:if test="${option.key == detailObject.mileageRateId}">
						<c:set var="mileageSelected" value="selected" />
					</c:if>

					<option value="${option.key}"${mileageSelected}>${option.label}</option>
				</c:forEach>
			</html:select> 
			<c:if test="${fn:length(temfunc:getOptionList('org.kuali.kfs.module.tem.businessobject.options.MileageRateValuesFinder', paramMap)) == 0}">
				<div align="left">No Rates Available</div>
			</c:if>
		</td>
	</c:if>
	<td valign="top" nowrap class="infoline">
		<div align="center" id="div_${detail}.expenseAmount">
			<kul:htmlControlAttribute
				attributeEntry="${otherExpenseAttributes.expenseAmount}"
				property="${detail}.expenseAmount"
				readOnly="${!fullEntryMode || parentObject.mileage}" />
		</div></td>
	<td valign="top" class="infoline">
		<div align="center" id="div_${detail}.convertedAmount">
			<kul:htmlControlAttribute
				attributeEntry="${otherExpenseAttributes.convertedAmount}"
				property="${detail}.convertedAmount"
				readOnly="true" />
		</div>
	</td>
	<td valign="top" nowrap class="infoline">
		<div align="center">
			<kul:htmlControlAttribute
				attributeEntry="${otherExpenseAttributes.nonReimbursable}"
				property="${detail}.nonReimbursable" readOnly="${!fullEntryMode || parentObject.nonReimbursable}" />
		</div>
	</td>
	<td valign="top" nowrap class="infoline">
		<div align="center">
			<kul:htmlControlAttribute
				attributeEntry="${otherExpenseAttributes.taxable}"
				property="${detail}.taxable"
				readOnly="${!KualiForm.document.taxSelectable || !fullEntryMode }" />
    	</div>
    </td>
	<td valign="top" class="infoline">
		<div align="center">
			<c:if test="${detailObject.travelExpenseTypeCode.receiptRequired}">
				<kul:htmlControlAttribute
					attributeEntry="${otherExpenseAttributes.missingReceipt}"
					property="${detail}.missingReceipt"
					readOnly="${!fullEntryMode}" />
			</c:if>
			<c:if test="${!detailObject.travelExpenseTypeCode.receiptRequired}">
				N/A
			</c:if>
		</div>
	</td>
	<%-- Show Airfare --%>
	<c:if test="${parentObject.airfareIndicator}">
		<c:set var="calcColspan" value="${calcColspan+2 }" />
		<td valign="top" class="infoline">
			<kul:htmlControlAttribute
				attributeEntry="${otherExpenseAttributes.airfareSourceCode}"
				property="${detail}.airfareSourceCode" readOnly="${!fullEntryMode}" />
		</td>
		<td valign="top" class="infoline">
			<c:choose>
				<c:when test="${fullEntryMode}">
					<c:set target="${paramMap}" property="expenseTypeCode" value="${parentObject.travelExpenseTypeCodeCode}" />
					<html:select property="${detail}.classOfServiceCode" disabled="${!fullEntryMode}">
						<c:forEach items="${temfunc:getOptionList('org.kuali.kfs.module.tem.businessobject.options.ClassOfServiceValuesFinder', paramMap)}" var="option">
							<c:set var="isSelected" value="${detailObject.classOfServiceCode == option.key}" />
							<option value="${option.key}" ${isSelected?'selected=true':'' }>${option.label}</option>
						</c:forEach>
					</html:select>
				</c:when>
				<c:otherwise>
					<c:out value="${detailObject.classOfServiceCode}" />
				</c:otherwise>
			</c:choose>				
		</td>
	</c:if>
	<c:if test="${parentObject.rentalCarIndicator}">
	<c:set var="calcColspan" value="${calcColspan+2 }" />
		<td valign="top" class="infoline">
			<c:choose>
				<c:when test="${fullEntryMode}">
					<c:set target="${paramMap}" property="expenseTypeCode" value="${parentObject.travelExpenseTypeCodeCode}" />
					<html:select property="${detail}.classOfServiceCode" disabled="${!fullEntryMode}">
						<c:forEach items="${temfunc:getOptionList('org.kuali.kfs.module.tem.businessobject.options.ClassOfServiceValuesFinder', paramMap)}" var="option">
							<c:set var="isSelected" value="${detailObject.classOfServiceCode == option.key}" />
							<option value="${option.key}" ${isSelected?'selected=true':'' }>${option.label}</option>
						</c:forEach>
					</html:select>
				</c:when>
				<c:otherwise>
					<c:out value="${detailObject.classOfServiceCode}" />
				</c:otherwise>
			</c:choose>
		</td>
		<td valign="top" class="infoline">
			<div align="center">
				<kul:htmlControlAttribute
					attributeEntry="${otherExpenseAttributes.rentalCarInsurance}"
					property="${detail}.rentalCarInsurance" readOnly="${!fullEntryMode}" />
			</div>		
		</td>
	</c:if>
	<td class="infoline" rowspan="2">
		<c:choose>
			<c:when test="${detailLineNumber != null}">
				<c:if test="${fullEntryMode}">
					<div align="center">
						<html:image
							src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif"
							styleClass="tinybutton"
							property="methodToCall.deleteActualExpenseDetailLine.line${lineNumber}-${detailLineNumber}"
							alt="Delete Actual Expense Line"
							title="Delete Actual Expense Detail Line" />
					</div>
				</c:if>
				<c:if test="${!fullEntryMode}"> &nbsp; </c:if>
			</c:when>
			<c:otherwise>
				<c:set var="tabindex" value="${KualiForm.currentTabIndex}" />
				<c:set var="dummyIncrementVar" value="${kfunc:incrementTabIndex(KualiForm, tabKey)}" />
				<div align="center">
					<html:image
						src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif"
						styleClass="tinybutton" tabindex="${tabindex}"
						property="methodToCall.addActualExpenseDetailLine.line${lineNumber}"
						alt="Add Actual Expense Detail Line"
						title="Add Actual Expense Detail Line" />
				</div>
			</c:otherwise>
		</c:choose>
	</td>
</tr>
<tr>
	<th>
		<div align="left">
			<kul:htmlAttributeLabel attributeEntry="${otherExpenseAttributes.description}" noColon="true" />
		</div>
	</th>
	<td valign="top" class="infoline" colspan="${calcColspan}">
		<kul:htmlControlAttribute attributeEntry="${otherExpenseAttributes.description}" property="${detail}.description" readOnly="${!fullEntryMode }" />
	</td>
</tr>
