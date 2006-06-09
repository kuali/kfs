<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib prefix="kul" tagdir="/WEB-INF/tags" %>
<%@ attribute name="editingMode" required="true" description="used to decide editability of overview fields" type="java.util.Map"%>
<c:set var="readOnly" value="${empty editingMode['fullEntry']}" />
<kul:tab
    tabTitle="Pre-Encumbrance Details"
    defaultOpen="true"
    tabErrorKey="${Constants.EDIT_PRE_ENCUMBRANCE_ERRORS}"
    >
    <div class="tab-container" align=center>
    <div class="h2-container">
<h2>Pre-Encumbrance Details</h2>
</div>
        <table cellpadding=0 class="datatable"
               summary="view/edit pre-encumbrance specific fields">
            <tbody>
                
                <tr>
                    <kul:htmlAttributeHeaderCell
                        attributeEntry="${DataDictionary.KualiPreEncumbranceDocument.attributes.reversalDate}"
                        horizontal="true"
                        width="35%"
                        />
                    <td class="datacell-nowrap"><kul:htmlControlAttribute
                        attributeEntry="${DataDictionary.KualiPreEncumbranceDocument.attributes.reversalDate}"
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
