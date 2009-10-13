<%--
 Copyright 2005-2009 The Kuali Foundation
 
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

<%@ attribute name="editingMode" required="true" description="used to decide editability of overview fields" type="java.util.Map"%>
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
<kul:tab
    tabTitle="Pre-Encumbrance Details"
    defaultOpen="true"
    tabErrorKey="${KFSConstants.EDIT_PRE_ENCUMBRANCE_ERRORS}"
    >
    <div class="tab-container" align=center>
<h3>Pre-Encumbrance Details</h3>
        <table cellpadding=0 class="datatable"
               summary="view/edit pre-encumbrance specific fields">
            <tbody>
                
                <tr>
                    <kul:htmlAttributeHeaderCell
                        attributeEntry="${DataDictionary.PreEncumbranceDocument.attributes.reversalDate}"
                        horizontal="true"
                        width="35%"
                        />
                    <td class="datacell-nowrap"><kul:htmlControlAttribute
                        attributeEntry="${DataDictionary.PreEncumbranceDocument.attributes.reversalDate}"
                        datePicker="true"
                        property="document.reversalDate"
                        readOnly="${readOnly}"
                        readOnlyAlternateDisplay="${KualiForm.formattedReversalDate}"
                        /></td>
                </tr>
            </tbody>
        </table>
    </div>
</kul:tab>
