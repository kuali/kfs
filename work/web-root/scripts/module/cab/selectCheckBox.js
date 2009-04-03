	function selectSources(all){
		var elms = document.getElementsByTagName("input");
		for(var i=0; i< elms.length; i++){
			if(elms[i].id !=null  && elms[i].id =='systemCheckbox' && !elms[i].disabled){
				elms[i].checked = all.checked;
			}
		}
	}
	function toggle(src,trg){
		var src = document.getElementById(src);
		var trg = document.getElementById(trg);
		if(src.checked){
			trg.disabled = true;
		}else{
			trg.disabled = false;
		}
	}
