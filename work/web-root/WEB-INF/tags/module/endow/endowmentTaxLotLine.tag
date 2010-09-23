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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map" 
			  description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="readOnly" required="true"
	description="If document is in read only mode"%>	
<%@ attribute name="isSource" required="true"
	description="Display all the Taxlot lines associated with the Source Transaction for this Document."%>	
<%@ attribute name="isTarget" required="true"
	description="Display all the Taxlot lines associated with the Target Transaction for this Document."%>
<%@ attribute name="displayHoldingCost" required="true" 
	description="Display the Gain and Loss for the Tax Lot Lines."%>		
<%@ attribute name="displayGainLoss" required="true"
	description="Display the Gain and Loss for the Tax Lot Lines."%>	
<%@ attribute name="showSourceDeleteButton" required="true"
	description="Display the Delete button for the Source Tax Lot Lines."%>
<%@ attribute name="showTargetDeleteButton" required="true"
	description="Display the Delete button for the Target Tax Lot Lines."%>	

<c:set var="holdingTaxLotAttributes" value="${DataDictionary.HoldingTaxLot.attributes}"/>

<kul:tab tabTitle="Tax Lot Lines" defaultOpen="true" tabErrorKey="${EndowConstants.TAX_LOT_LINE_DOCUMENT_ERRORS}" tabItemCount="${KualiForm.document.taxLotLinesNumber}">
	<div class="tab-container" align=center>
			<h3>Tax Lot Lines</h3>
		<table cellpadding="0" cellspacing="0" summary="Tax Lot Lines">
		<c:if test="${isSource}">	
			<tr>
			<c:choose>
			<c:when test="${showSourceDeleteButton and not readOnly}">
			<c:if test="${displayGainLoss}">
				<td colspan="8" class="tab-subhead" style="border-right: none;" align="left">
			</c:if>
			
			<c:if test="${not displayGainLoss}">
				<td colspan="6" class="tab-subhead" style="border-right: none;" align="left">
			</c:if>
			</c:when>
			<c:otherwise>
			<c:if test="${displayGainLoss}">
				<td colspan="7" class="tab-subhead" style="border-right: none;" align="left">
			</c:if>
			
			<c:if test="${not displayGainLoss}">
				<td colspan="5" class="tab-subhead" style="border-right: none;" align="left">
			</c:if>
			</c:otherwise>
			</c:choose>
	        	   ${KualiForm.sourceTaxLotsLabelName}
	            </td>   
	        </tr>	
			<tr>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${documentAttributes.documentLineNumber}"
					useShortLabel="false"
					/>

				<kul:htmlAttributeHeaderCell
					attributeEntry="${documentAttributes.transactionHoldingLotNumber}"
					useShortLabel="false"
					hideRequiredAsterisk="true"
					/>

				<kul:htmlAttributeHeaderCell
					attributeEntry="${documentAttributes.lotUnits}"
					useShortLabel="false"
					/>

				<c:if test="${displayHoldingCost}">
				<kul:htmlAttributeHeaderCell
					attributeEntry="${documentAttributes.lotHoldingCost}"
					useShortLabel="false"
					/>
				</c:if>					
					
				<c:if test="${displayGainLoss}">
					<kul:htmlAttributeHeaderCell
						attributeEntry="${documentAttributes.lotShortTermGainLoss}"
						useShortLabel="false"
					/>
					
					<kul:htmlAttributeHeaderCell
						attributeEntry="${documentAttributes.lotLongTermGainLoss}"
						useShortLabel="false"
					/>
				</c:if>								

				<kul:htmlAttributeHeaderCell
					attributeEntry="${holdingTaxLotAttributes.acquiredDate}"
					useShortLabel="false"
					/>
				<c:if test="${showSourceDeleteButton and not readOnly}" >	
					<kul:htmlAttributeHeaderCell literalLabel="Actions"/>	
				</c:if>					
			</tr>
			<logic:iterate id="taxLotLinesCollection" name="KualiForm" property="document.sourceTransactionLines" indexId="outerctr">
				<logic:iterate id="item" name="taxLotLinesCollection" property="taxLotLines" indexId="ctr">
		            <tr>
		                <td class="datacell">${outerctr + 1}</td>
		                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${documentAttributes.transactionHoldingLotNumber}" property="document.sourceTransactionLines[${outerctr}].taxLotLines[${ctr}].transactionHoldingLotNumber" readOnly="true"/></td>
		                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${documentAttributes.lotUnits}" property="document.sourceTransactionLines[${outerctr}].taxLotLines[${ctr}].lotUnits" readOnly="true"/></td>
		                <c:if test="${displayHoldingCost}">
		                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${documentAttributes.lotHoldingCost}" property="document.sourceTransactionLines[${outerctr}].taxLotLines[${ctr}].lotHoldingCost" readOnly="true"/></td>
		                </c:if>
		                <c:if test="${displayGainLoss}">
		                	<td class="datacell"><kul:htmlControlAttribute attributeEntry="${documentAttributes.lotShortTermGainLoss}" property="document.sourceTransactionLines[${outerctr}].taxLotLines[${ctr}].lotShortTermGainLoss" readOnly="true"/></td>
		               		<td class="datacell"><kul:htmlControlAttribute attributeEntry="${documentAttributes.lotLongTermGainLoss}" property="document.sourceTransactionLines[${outerctr}].taxLotLines[${ctr}].lotLongTermGainLoss" readOnly="true"/></td>
		                </c:if>
		                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${holdingTaxLotAttributes.acquiredDate}" property="document.sourceTransactionLines[${outerctr}].taxLotLines[${ctr}].lotAcquiredDate" readOnly="true"/></td>
		                <c:if test="${showSourceDeleteButton and not readOnly}" >
		                <td class="datacell">
               				<div align="center">
		                		<html:image property="methodToCall.deleteSourceTaxLotLine.line${outerctr}.taxLot${ctr}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" title="Delete Tax Lot ${ctr+1}" alt="Delete Tax Lot  ${ctr+1}" styleClass="tinybutton"/>
		                    </div>
                		</td>
		                </c:if>
		            </tr>
		        </logic:iterate>
		    </logic:iterate>
		</c:if>
		
		<c:if test="${isTarget}">	
			<tr>
			<c:choose>
			<c:when test="${showTargetDeleteButton and not readOnly}">
			<c:if test="${displayGainLoss}">
				<td colspan="8" class="tab-subhead" style="border-right: none;" align="left">
			</c:if>
			
			<c:if test="${not displayGainLoss}">
				<td colspan="6" class="tab-subhead" style="border-right: none;" align="left">
			</c:if>
			</c:when>
			<c:otherwise>
			<c:if test="${displayGainLoss}">
				<td colspan="7" class="tab-subhead" style="border-right: none;" align="left">
			</c:if>
			
			<c:if test="${not displayGainLoss}">
				<td colspan="5" class="tab-subhead" style="border-right: none;" align="left">
			</c:if>
			</c:otherwise>
			</c:choose>
	        	    ${KualiForm.targetTaxLotsLabelName}
	            </td>   
	        </tr>	
			<tr>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${documentAttributes.documentLineNumber}"
					useShortLabel="false"
					/>

				<kul:htmlAttributeHeaderCell
					attributeEntry="${documentAttributes.transactionHoldingLotNumber}"
					useShortLabel="false"
					hideRequiredAsterisk="true"
					/>

				<kul:htmlAttributeHeaderCell
					attributeEntry="${documentAttributes.lotUnits}"
					useShortLabel="false"
					/>
				<c:if test="${displayHoldingCost}">
				<kul:htmlAttributeHeaderCell
					attributeEntry="${documentAttributes.lotHoldingCost}"
					useShortLabel="false"
					/>
				</c:if>
					
				<c:if test="${displayGainLoss}">
					<kul:htmlAttributeHeaderCell
						attributeEntry="${documentAttributes.lotShortTermGainLoss}"
						useShortLabel="false"
					/>
					
					<kul:htmlAttributeHeaderCell
						attributeEntry="${documentAttributes.lotLongTermGainLoss}"
						useShortLabel="false"
					/>
				</c:if>			

				<kul:htmlAttributeHeaderCell
					attributeEntry="${holdingTaxLotAttributes.acquiredDate}"
					useShortLabel="false"
					/>
				<c:if test="${showTargetDeleteButton and not readOnly}" >	
					<kul:htmlAttributeHeaderCell literalLabel="Actions"/>	
				</c:if>
			</tr>	
			<logic:iterate id="taxLotLinesCollection" name="KualiForm" property="document.targetTransactionLines" indexId="outerctr">
				<logic:iterate id="item" name="taxLotLinesCollection" property="taxLotLines" indexId="ctr">
		            <tr>
		                <td class="datacell">${outerctr + 1}</td>
		                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${documentAttributes.transactionHoldingLotNumber}" property="document.targetTransactionLines[${outerctr}].taxLotLines[${ctr}].transactionHoldingLotNumber" readOnly="true"/></td>
		                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${documentAttributes.lotUnits}" property="document.targetTransactionLines[${outerctr}].taxLotLines[${ctr}].lotUnits" readOnly="true"/></td>
		                <c:if test="${displayHoldingCost}">
		                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${documentAttributes.lotHoldingCost}" property="document.targetTransactionLines[${outerctr}].taxLotLines[${ctr}].lotHoldingCost" readOnly="true"/></td>
		                </c:if>
		                <c:if test="${displayGainLoss}">
		                	<td class="datacell"><kul:htmlControlAttribute attributeEntry="${documentAttributes.lotShortTermGainLoss}" property="document.targetTransactionLines[${outerctr}].taxLotLines[${ctr}].lotShortTermGainLoss" readOnly="true"/></td>
		               		<td class="datacell"><kul:htmlControlAttribute attributeEntry="${documentAttributes.lotLongTermGainLoss}" property="document.targetTransactionLines[${outerctr}].taxLotLines[${ctr}].lotLongTermGainLoss" readOnly="true"/></td>
		                </c:if>
		                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${holdingTaxLotAttributes.acquiredDate}" property="document.targetTransactionLines[${outerctr}].taxLotLines[${ctr}].lotAcquiredDate" readOnly="true"/></td>
		                <c:if test="${showTargetDeleteButton and not readOnly}" >
		                    <td class="datacell">
               					<div align="center">
		                			<html:image property="methodToCall.deleteTargetTaxLotLine.line${outerctr}.taxLot${ctr}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" title="Delete Tax Lot ${ctr+1}" alt="Delete Tax Lot  ${ctr+1}" styleClass="tinybutton"/>
		                		</div>
		                	</td>
		                </c:if>
		            </tr>
		        </logic:iterate>
		    </logic:iterate>
		</c:if>
		
		</table>
	</div>
</kul:tab>


