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

<c:set var="specialAttributes" value="${DataDictionary.SpecialCircumstances.attributes}" />
<c:set var="specialQuestionAttributes" value="${DataDictionary.SpecialCircumstancesQuestion.attributes}" />
<c:set var="documentAttributes" value="${DataDictionary.TravelAuthorizationDocument.attributes}" />
<c:set var="isTA" value="${KualiForm.isTravelAuthorizationDoc}" />
<c:set var="isTR" value="${KualiForm.docTypeName == TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT}" />
<c:set var="isLimitEditable" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] && KualiForm.editingMode['expenseLimitEntry']}" />

<kul:tab tabTitle="Special Circumstances" defaultOpen="${KualiForm.document.specialCircumstancesDefaultOpen}" tabErrorKey="${TemKeyConstants.TRVL_AUTH_SPECIAL_CIRCUMSTANCES_ERRORS}">
    <div class="tab-container" align=center > 
		<h3>Special Circumstances</h3>
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Special Circumstances">
          <tr>
            <td>&nbsp;</td> 
            <td>If there is an expense limit imposed by department or grant or some other budgetary restrictions on this trip, please enter the expense limit here $<kul:htmlControlAttribute
				attributeEntry="${documentAttributes.expenseLimit}"
				property="document.expenseLimit"
				readOnly="${!isLimitEditable}" /></td>
          </tr>
          <c:if test="${(isTA || isTR) && KualiForm.document.mealsWithoutLodging}">
          <tr>
            <td>&nbsp;</td> 
            <td>Justification for meals without lodging <br /> <kul:htmlControlAttribute
				attributeEntry="${documentAttributes.mealWithoutLodgingReason}"
				property="document.mealWithoutLodgingReason"
				readOnly="${!fullEntryMode}" /></td>
          </tr>
          </c:if>          
          <c:forEach items="${KualiForm.document.specialCircumstances}" varStatus="specialIndex" >
          <tr>
          <td class="datacell">
          	<c:if test="${!KualiForm.document.specialCircumstances[specialIndex.count - 1].question.free}">
          	<kul:htmlControlAttribute
				attributeEntry="${specialAttributes.response}"
				property="document.specialCircumstances[${specialIndex.count - 1}].response"
				readOnly="${!fullEntryMode}" />
			</c:if>
		  </td>
          <td><kul:htmlControlAttribute
				attributeEntry="${specialQuestionAttributes.text}"
                				property="document.specialCircumstances[${specialIndex.count - 1}].question.text" 
                                readOnly="true" />
                <c:if test="${KualiForm.document.specialCircumstances[specialIndex.count - 1].question.free}">
                    <br/>
                  <kul:htmlControlAttribute
                        attributeEntry="${specialAttributes.text}"
                        property="document.specialCircumstances[${specialIndex.count - 1}].text"
                        readOnly="${!fullEntryMode}" />
                </c:if>
                </td>
           </tr>
          </c:forEach>
        </table>
    </div>
</kul:tab>
