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
<%@ attribute name="expense" required="false" description="The expense to create the form for." %>
<%@ attribute name="lineNumber" required="false" description="Line number for the record." %>
<%@ attribute name="detailObject" required="true" description="The actual object" type="org.kuali.kfs.module.tem.businessobject.ImportedExpense"%>
<c:set var="importedExpenseAttributes" value="${DataDictionary.ImportedExpense.attributes}" />
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
				<td class="infoline">
					<div align="center">
					<kul:htmlControlAttribute
						attributeEntry="${importedExpenseAttributes.cardType}"
						property="${expense}.cardType" 
						readOnly="true" />
					</div>
				</td>
				<td class="infoline">
					<div align="center">
					<kul:htmlControlAttribute
						attributeEntry="${importedExpenseAttributes.expenseDate}"
						property="${expense}.expenseDate"
						readOnly="true" />
					</div>
				</td>
				<td valign="top" class="infoline">
					<c:choose>
						<c:when test="${fullEntryMode}">
							<c:set target="${paramMap}" property="groupTravelCount" value="${fn:length(KualiForm.document.groupTravelers)}" />
							<html:select property="${expense}.expenseTypeCode" 
								styleId="${expense}.expenseTypeCode"
								onchange="checkDirectBilled('${expense}');loadExpenseTypeObjectCode(this, '${KualiForm.docTypeName}', '${KualiForm.document.traveler.travelerTypeCode}', '${KualiForm.document.tripTypeCode}')">
								<c:forEach items="${temfunc:getOptionList('org.kuali.kfs.module.tem.businessobject.options.TravelExpenseTypeValuesFinder', paramMap)}" var="option">						
									<c:set var="isSelected" value="${detailObject.expenseTypeCode == option.key}" />
									<%-- Populate the value that was previously selected before error occurred --%>
									<option value="${option.key}" ${isSelected?'selected=true':'' }>${option.value}</option>
								</c:forEach>
							</html:select>
							<c:forEach items="${ErrorPropertyList}" var="key">
								<c:if test="${key == 'newImportedExpenseLine.expenseTypeCode'}">
									<kul:fieldShowErrorIcon />
								</c:if>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<c:out value="${detailObject.expenseTypeObjectCode.expenseType.name}" />
						</c:otherwise>
					</c:choose>	
				</td>
				<td valign="top" class="infoline">
					<kul:htmlControlAttribute 
						attributeEntry="${importedExpenseAttributes.travelCompanyCodeName}" 
						property="${expense}.travelCompanyCodeName" 
						readOnly="${!fullEntryMode}"/>
					<c:if test="${fullEntryMode }">
			            <kul:lookup 
			            	boClassName="org.kuali.kfs.fp.businessobject.TravelCompanyCode" 
			            	fieldConversions="name:${expense}.travelCompanyCodeName,code:${expense}.expenseTypeCode" 
			            	fieldLabel="${importedExpenseAttributes.travelCompanyCodeName.label}" 
			            	lookupParameters="${expense}.expenseTypeCode:code,${expense}.travelCompanyCodeName:name" 
			            	readOnlyFields="expenseTypeObjectCode.expenseType.prepaidExpense"/> 	
			    	</c:if>
				</td>
				<td valign="top" nowrap class="infoline">
					<div align="center">
						<kul:htmlControlAttribute
							attributeEntry="${importedExpenseAttributes.expenseAmount}"
							property="${expense}.expenseAmount" 
							readOnly="true" />
						<c:set var="fullPath" value="document.importedExpenses[${lineNumber-1}].expenseAmount" />
						<c:forEach items="${ErrorPropertyList}" var="key">
							<c:if test="${key == fullPath}">
								<kul:fieldShowErrorIcon />
							</c:if>
						</c:forEach>
					</div>
				</td>
				<td valign="top" nowrap class="infoline">
					<div align="center">
						<kul:htmlControlAttribute
							attributeEntry="${importedExpenseAttributes.currencyRate}"
							property="${expense}.currencyRate"
							readOnly="${!fullEntryMode }" />
						<br/>
		            	<a href="${KualiForm.foreignCurrencyUrl}" target="currency_conversion_window">Rate Conversion Site</a>
		            </div>
				</td>
		        <td valign="top" nowrap class="infoline">
					<div align="center">
						<kul:htmlControlAttribute
							attributeEntry="${importedExpenseAttributes.convertedAmount}"
							property="${expense}.convertedAmount" 
							readOnly="true" />
					</div>
				</td>
		        <td valign="top" nowrap class="infoline">
					<div align="center">
						<kul:htmlControlAttribute
							attributeEntry="${importedExpenseAttributes.nonReimbursable}"
							property="${expense}.nonReimbursable"
							readOnly="${!fullEntryMode || !detailObject.enableNonReimbursable}" />
		            </div>
				</td>
		        <td valign="top" nowrap class="infoline">
					<div align="center">
						<kul:htmlControlAttribute
							attributeEntry="${importedExpenseAttributes.receiptRequired}"
							property="${expense}.receiptRequired"
							readOnly="${!fullEntryMode }" />
		            </div>
		        </td>
		        <td valign="top" nowrap class="infoline">
					<div align="center">
						<kul:htmlControlAttribute
							attributeEntry="${importedExpenseAttributes.taxable}"
							property="${expense}.taxable"
							readOnly="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] || !expenseTaxableMode}" />
		            </div>
				</td>
				<td class="infoline" rowspan="2">
					<c:choose>
						<c:when test="${lineNumber != null}">
							<c:if test="${fullEntryMode}">
								<div align="center">
									<html:image
										src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif"
										styleClass="tinybutton"
										property="methodToCall.deleteImportedExpenseLine.line${(lineNumber-1)}"
										alt="Delete Imported Expense Line"
										title="Delete Imported Expense Line" />
								</div>
							</c:if>
							<c:if test="${!fullEntryMode}">
								&nbsp;
							</c:if>
						</c:when>
						<c:otherwise>
							<c:set var="tabindex" value="${KualiForm.currentTabIndex}"/>
							<c:set var="dummyIncrementVar" value="${kfunc:incrementTabIndex(KualiForm, tabKey)}" />
							<div align="center">
								<html:image src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif"
									styleClass="tinybutton" 
									tabindex="${tabindex}" 
									property="methodToCall.addImportedExpenseLine"
									alt="Add Imported Expense Line" 
									title="Add Imported Expense Line" />
							</div>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<tr>
				<th>
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${importedExpenseAttributes.description}" noColon="true" />
					</div>
				</th>
				<td valign="top" class="infoline" colspan="9">
					<kul:htmlControlAttribute
						attributeEntry="${importedExpenseAttributes.description}"
						property="${expense}.description" 
						readOnly="${!fullEntryMode }" />
				</td>
			</tr>
