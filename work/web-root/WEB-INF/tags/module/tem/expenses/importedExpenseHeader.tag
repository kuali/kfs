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
<%@ attribute name="lineNumber" required="false" description="Line number for the record." %>
<%@ attribute name="isCTS" required="true" description="determines rowspan." %>

<c:set var="importedExpenseAttributes" value="${DataDictionary.ImportedExpense.attributes}" />
			<tr>
				<c:choose>
					<c:when test="${lineNumber == null }">
						<th>&nbsp;</th>
					</c:when>
					<c:otherwise>
						<th scope="row" class="infoline" rowspan="${isCTS?3:4}">
							<div align="center"> 
								${lineNumber}
							</div>
						</th>
					</c:otherwise>
				</c:choose>
	            <th>
	            	<div align="left">
	            		<kul:htmlAttributeLabel attributeEntry="${importedExpenseAttributes.cardType}" noColon="true" />
	            	</div>
				</th>
				<th>
					<div align="left">
						<kul:htmlAttributeLabel attributeEntry="${importedExpenseAttributes.expenseDate}" noColon="true" />
					</div>
				</th>
				<th>
					<div align="left">
						<kul:htmlAttributeLabel attributeEntry="${importedExpenseAttributes.travelExpenseTypeCodeId}" noColon="true" />
					</div>
				</th>
				<th>
					<div align="left">
						<kul:htmlAttributeLabel attributeEntry="${importedExpenseAttributes.travelCompanyCodeName}" noColon="true" />
					</div>
				</th>
				<th>
					<div align="left">
						<kul:htmlAttributeLabel attributeEntry="${importedExpenseAttributes.expenseAmount}" noColon="true" />
					</div>
				</th>
				<th>
					<div align="left">
						<kul:htmlAttributeLabel attributeEntry="${importedExpenseAttributes.currencyRate}" noColon="true" />
					</div>
				</th>
				<th>
					<div align="left">$US</div>
				</th>
				<th>
					<div align="left">
						<kul:htmlAttributeLabel attributeEntry="${importedExpenseAttributes.nonReimbursable}" noColon="true" />
					</div>
				</th>
				<th>
					<div align="left">
						<kul:htmlAttributeLabel attributeEntry="${importedExpenseAttributes.receiptRequired}" noColon="true" />
					</div>
				</th>
				<th>
					<div align="left">
						<kul:htmlAttributeLabel attributeEntry="${importedExpenseAttributes.taxable}" noColon="true" />
					</div>
				</th>
	            <th>
	            	<div align="center">Actions</div>
	            </th>
			</tr>