<%@ taglib prefix="kul" tagdir="/WEB-INF/tags" %>
<kul:tab
    tabTitle="Pre Encumbrance Details"
    defaultOpen="true"
    tabErrorKey="${Constants.EDIT_PRE_ENCUMBRANCE_ERRORS}"
    >
    <div class="tab-container" align=center>
        <table cellpadding=0 class="datatable"
               summary="view/edit pre-encumbrance specific fields">
            <tbody>
                <tr>
                    <td colspan=2 class="subhead"><span
                        class="subhead-left"> Pre Encumbrance Details</span></td>
                </tr>
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
