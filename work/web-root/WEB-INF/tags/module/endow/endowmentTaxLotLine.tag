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

<c:set var="holdingTaxLotAttributes" value="${DataDictionary.HoldingTaxLot.attributes}"/>

<kul:tab tabTitle="Tax Lot Lines" defaultOpen="true" tabErrorKey="${EndowConstants.TAX_LOT_LINE_DOCUMENT_ERRORS}" tabItemCount="${KualiForm.document.taxLotLinesNumber}">
	<div class="tab-container" align=center>
			<h3>Tax Lot Lines</h3>
		<table cellpadding="0" cellspacing="0" summary="Tax Lot Lines">
		<c:if test="${isSource}">	
			<tr>
	            <td colspan="5" class="tab-subhead" style="border-right: none;" align="left">
	        	    FROM
	            </td>   
	        </tr>	
			<tr>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${documentAttributes.transactionHoldingLotNumber}"
					useShortLabel="false"
					/>

				<kul:htmlAttributeHeaderCell
					attributeEntry="${holdingTaxLotAttributes.lotNumber}"
					useShortLabel="false"
					hideRequiredAsterisk="true"
					/>

				<kul:htmlAttributeHeaderCell
					attributeEntry="${documentAttributes.lotUnits}"
					useShortLabel="false"
					/>

				<kul:htmlAttributeHeaderCell
					attributeEntry="${documentAttributes.lotHoldingCost}"
					useShortLabel="false"
					/>

				<kul:htmlAttributeHeaderCell
					attributeEntry="${holdingTaxLotAttributes.acquiredDate}"
					useShortLabel="false"
					/>
			</tr>
			<logic:iterate id="taxLotLinesCollection" name="KualiForm" property="document.sourceTransactionLines" indexId="outerctr">
				<logic:iterate id="item" name="taxLotLinesCollection" property="taxLotLines" indexId="ctr">
		            <tr>
		                <td class="datacell">${outerctr + 1}</td>
		                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${holdingTaxLotAttributes.lotNumber}" property="document.sourceTransactionLines[${outerctr}].taxLotLines[${ctr}].transactionHoldingLotNumber" readOnly="${readOnly}"/></td>
		                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${documentAttributes.lotUnits}" property="document.sourceTransactionLines[${outerctr}].taxLotLines[${ctr}].lotUnits" readOnly="${readOnly}"/></td>
		                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${documentAttributes.lotHoldingCost}" property="document.sourceTransactionLines[${outerctr}].taxLotLines[${ctr}].lotHoldingCost" readOnly="${readOnly}"/></td>
		                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${holdingTaxLotAttributes.acquiredDate}" property="document.sourceTransactionLines[${outerctr}].taxLotLines[${ctr}].lotAcquiredDate" readOnly="${readOnly}"/></td>
		            </tr>
		        </logic:iterate>
		    </logic:iterate>
		</c:if>
		
		<c:if test="${isTarget}">	
			<tr>
	            <td colspan="5" class="tab-subhead" style="border-right: none;" align="left">
	        	    TO
	            </td>   
	        </tr>	
			<tr>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${documentAttributes.transactionHoldingLotNumber}"
					useShortLabel="false"
					/>

				<kul:htmlAttributeHeaderCell
					attributeEntry="${holdingTaxLotAttributes.lotNumber}"
					useShortLabel="false"
					hideRequiredAsterisk="true"
					/>

				<kul:htmlAttributeHeaderCell
					attributeEntry="${documentAttributes.lotUnits}"
					useShortLabel="false"
					/>

				<kul:htmlAttributeHeaderCell
					attributeEntry="${documentAttributes.lotHoldingCost}"
					useShortLabel="false"
					/>

				<kul:htmlAttributeHeaderCell
					attributeEntry="${holdingTaxLotAttributes.acquiredDate}"
					useShortLabel="false"
					/>
			</tr>	
			<logic:iterate id="taxLotLinesCollection" name="KualiForm" property="document.targetTransactionLines" indexId="outerctr">
				<logic:iterate id="item" name="taxLotLinesCollection" property="taxLotLines" indexId="ctr">
		            <tr>
		                <td class="datacell">${outerctr + 1}</td>
		                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${holdingTaxLotAttributes.lotNumber}" property="document.targetTransactionLines[${outerctr}].taxLotLines[${ctr}].transactionHoldingLotNumber" readOnly="${readOnly}"/></td>
		                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${documentAttributes.lotUnits}" property="document.targetTransactionLines[${outerctr}].taxLotLines[${ctr}].lotUnits" readOnly="${readOnly}"/></td>
		                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${documentAttributes.lotHoldingCost}" property="document.targetTransactionLines[${outerctr}].taxLotLines[${ctr}].lotHoldingCost" readOnly="${readOnly}"/></td>
		                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${holdingTaxLotAttributes.acquiredDate}" property="document.targetTransactionLines[${outerctr}].taxLotLines[${ctr}].lotAcquiredDate" readOnly="${readOnly}"/></td>
		            </tr>
		        </logic:iterate>
		    </logic:iterate>
		</c:if>
		
		</table>
	</div>
</kul:tab>


