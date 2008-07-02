function selectAllCheckboxes(formObj) {
	var checkbox=null;
	var masterCheckbox = formObj.selectAllCheckbox;	
	var nElements= formObj.rowCheckbox.length;
	if (nElements > 0) {
		for (var x= 0; x < nElements; x++)  {
			checkbox = formObj.rowCheckbox[x];
			checkbox.checked = masterCheckbox.checked;
		}
	} else {
		checkbox = formObj.rowCheckbox;
		checkbox.checked = masterCheckbox.checked;
	}			
}