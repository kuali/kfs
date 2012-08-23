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
<%@ attribute name="lineNumber"   required="false" description="Line number for the record." %>
<%@ attribute name="expense"      required="false" description="The expense to create the form for." %>
<%@ attribute name="detailObject" required="true"  description="The actual object" type="org.kuali.kfs.module.tem.businessobject.ActualExpense"%>

<c:set var="otherExpenseAttributes" value="${DataDictionary.ActualExpense.attributes}" />
<jsp:useBean id="paramMap" class="java.util.HashMap" />
<c:set target="${paramMap}" property="tripType" value="${KualiForm.document.tripTypeCode}" />
<c:set target="${paramMap}" property="travelerType" value="${KualiForm.document.traveler.travelerTypeCode}" />
<c:set target="${paramMap}" property="documentType" value="${KualiForm.docTypeName}" />

		<tr>
			<c:if test="${lineNumber == null}">
				<th scope="row" class="infoline" rowspan="2">
					<div align="right">add:</div>
				</th>
			</c:if>
			<td valign="top" class="infoline">
				<kul:htmlControlAttribute
					attributeEntry="${otherExpenseAttributes.expenseDate}"
					property="${expense}.expenseDate"
					readOnly="${lineNumber != null || !fullEntryMode}" />
			</td>
			<td valign="top" class="infoline">
				<c:choose>
					<c:when test="${lineNumber == null }">
						<c:set target="${paramMap}" property="groupTravelCount" value="${fn:length(KualiForm.document.groupTravelers)}" />
						<html:select property="${expense}.travelCompanyCodeCode" 
							styleId="${expense}.travelCompanyCodeCode"
							onchange="checkDirectBilled('${expense}');disableExpenseAmount(this)">
							<c:forEach items="${temfunc:getOptionList('org.kuali.kfs.module.tem.businessobject.options.TravelExpenseTypeValuesFinder', paramMap)}" var="option">						
								<c:set var="isSelected" value="${detailObject.travelCompanyCodeCode == option.key}" />
								<%-- Populate the value that was previously selected before error occurred --%>
								<option value="${option.key}" ${isSelected?'selected=true':'' }>${option.label}</option>
							</c:forEach>
						</html:select>
						<c:forEach items="${ErrorPropertyList}" var="key">
							<c:if test="${key == 'newActualExpenseLine.travelCompanyCodeCode'}">
								<kul:fieldShowErrorIcon />
							</c:if>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<c:out value="${detailObject.travelExpenseTypeCode.name}" />
					</c:otherwise>
				</c:choose>	
			</td>
			<td valign="top" class="infoline">
				<c:choose>
					<c:when test="${lineNumber == null }">
						<kul:htmlControlAttribute attributeEntry="${otherExpenseAttributes.travelCompanyCodeName}" property="${expense}.travelCompanyCodeName"/>
                		<kul:lookup boClassName="org.kuali.kfs.fp.businessobject.TravelCompanyCode" 
                			fieldConversions="name:${expense}.travelCompanyCodeName,code:${expense}.travelCompanyCodeCode" 
                			fieldLabel="${otherExpenseAttributes.travelCompanyCodeName.label}" 
                			lookupParameters="${expense}.travelCompanyCodeCode:code,${expense}.travelCompanyCodeName:name" 
                			readOnlyFields="travelExpenseTypeCode.prepaidExpense"/>
					</c:when>
					<c:otherwise>
						<c:out value="${detailObject.travelCompanyCodeName}" />&nbsp;
					</c:otherwise>
				</c:choose>
			</td>
			<td valign="top" nowrap class="infoline">
				<div align="center" id="div_${expense}.expenseAmount"><kul:htmlControlAttribute
					attributeEntry="${otherExpenseAttributes.expenseAmount}"
					property="${expense}.expenseAmount" readOnly="${!fullEntryMode || detailObject.travelCompanyCodeCode == TemConstants.MILEAGE_EXPENSE}" />
				</div>
			</td>
			<td valign="top" nowrap class="infoline">
				<div align="center" id="div_${expense}.currencyRate">
					<kul:htmlControlAttribute
						attributeEntry="${otherExpenseAttributes.currencyRate}"
						property="${expense}.currencyRate"
						readOnly="${lineNumber != null || !fullEntryMode}" />
					<br/>
					<c:if test="${lineNumber == null}" >
						<a href="${currencyUrl}">Rate Conversion Site</a>
					</c:if>
	            </div>
            </td>
            <td valign="top" nowrap class="infoline">
				<div align="center">
					<kul:htmlControlAttribute
						attributeEntry="${otherExpenseAttributes.nonReimbursable}"
						property="${expense}.nonReimbursable"
						readOnly="${!fullEntryMode}" />
            	</div>
            </td>
            <td valign="top" nowrap class="infoline">
				<div align="center">
					<kul:htmlControlAttribute
						attributeEntry="${otherExpenseAttributes.taxable}"
						property="${expense}.taxable"
						readOnly="${documentAttributes.taxSelectable || !fullEntryMode || lineNumber !=null }" />
            	</div>
            </td>
            <td valign="top" nowrap class="infoline">
            	<div align="center">
            		<c:if test="${! empty detailObject.travelExpenseTypeCode}" >
					<kul:htmlControlAttribute
						attributeEntry="${otherExpenseAttributes.travelExpenseTypeCode.receiptRequired}"
						property="${expense}.travelExpenseTypeCode.receiptRequired" readOnly="true" />
					</c:if>
				</div>
            </td>
			
			<td class="infoline" valign="top">
				<div align="center">
					<c:if test="${detailObject.travelExpenseTypeCode.receiptRequired}">
						<kul:htmlControlAttribute
							attributeEntry="${otherExpenseAttributes.missingReceipt}"
							property="${expense}.missingReceipt"
							readOnly="${!fullEntryMode}" />
					</c:if>
					<c:if test="${!detailObject.travelExpenseTypeCode.receiptRequired}">
						N/A
					</c:if>
				</div>
				</td>
			<td valign="top" nowrap class="infoline">
				<div align="center" id="div_${expense}.convertedAmount">
					<kul:htmlControlAttribute
						attributeEntry="${otherExpenseAttributes.convertedAmount}"
						property="${expense}.convertedAmount" readOnly="true" />
				</div>
			</td>
			<td class="infoline" rowspan="2">
				<c:set var="tabindex" value="${KualiForm.currentTabIndex}"/>
				<c:set var="dummyIncrementVar" value="${kfunc:incrementTabIndex(KualiForm, tabKey)}" />
				<div align="center">
					<c:choose>
						<c:when test="${fullEntryMode}">
							<c:choose>
								<c:when test="${lineNumber == null }">
									<html:image
										src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif"
										styleClass="tinybutton" 
										tabindex="${ tabindex}" 
										property="methodToCall.addActualExpenseLine.line${lineNumber-1}"
										alt="Add Actual Expense Line" title="Add Actual Expense Line" />
								</c:when>
								<c:otherwise>
									<html:image
										src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif"
										styleClass="tinybutton"
										property="methodToCall.deleteActualExpenseLine.line${lineNumber-1}"
										alt="Delete Actual Expense Line"
										title="Delete Actual Expense Line" />
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							&nbsp;
						</c:otherwise>
					</c:choose>
				</div>
			</td>
		</tr>
		<tr>
			<th>
				<div align="left">
					<kul:htmlAttributeLabel attributeEntry="${otherExpenseAttributes.description}" />
				</div>
			</th>
			<td valign="top" class="infoline" colspan="9">
				<kul:htmlControlAttribute
					attributeEntry="${otherExpenseAttributes.description}"
					property="${expense}.description" readOnly="${!fullEntryMode}" />
			</td>
		</tr>
