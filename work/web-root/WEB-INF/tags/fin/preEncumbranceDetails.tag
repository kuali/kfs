<%@ taglib prefix="kul" tagdir="/WEB-INF/tags" %>
<kul:tab
    tabTitle="Pre Encumbrance Details"
    defaultOpen="true"
    tabErrorKey="${Constants.EDIT_PRE_ENCUMBRANCE_ERRORS}"
    >
    <div class="tab-container" align=center>
    <div class="h2-container">
<h2>Pre Encumbrance Details</h2>
</div>
        <table cellpadding=0 class="datatable"
               summary="view/edit pre-encumbrance specific fields">
            <tbody>
                
                <tr>
                    <th width="35%" class="bord-l-b"><div
                        align="right">${ConfigProperties.label.document.preEncumbrance.reversalDate}: </div>
                    </th>
                    <td class="datacell-nowrap"><kul:dateInputNoAttributeEntry
                        property="document.reversalDate" maxLength="10" size="10"/></td>
                </tr>
            </tbody>
        </table>
    </div>
</kul:tab>
