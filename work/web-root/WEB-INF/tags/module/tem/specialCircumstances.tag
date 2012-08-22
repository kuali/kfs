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

<c:set var="specialAttributes" value="${DataDictionary.SpecialCircumstances.attributes}" />
<c:set var="specialQuestionAttributes" value="${DataDictionary.SpecialCircumstancesQuestion.attributes}" />
<c:set var="documentAttributes" value="${DataDictionary.TravelAuthorizationDocument.attributes}" />
<c:set var="isTA" value="${KualiForm.docTypeName == TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT}" />
<c:set var="isTR" value="${KualiForm.docTypeName == TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT}" />
<c:set var="isLimitEditable" value="${fullEntryMode || fiscalOfficer}" />

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
