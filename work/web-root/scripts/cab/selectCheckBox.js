	function selectSources(all){
		var elms = document.getElementsByTagName("input");
		for(var i=0; i< elms.length; i++){
			if(elms[i].name.match('src') == 'src' && !elms[i].disabled){
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
	function merge(src,trg){
		var elms = document.getElementsByTagName("input");
		var trgCount = 0;
		var srcCount = 0;
		for(var i=0; i< elms.length; i++){
			if(elms[i].name.match('trg')== 'trg' && elms[i].checked ){
			   trgCount = trgCount+1;
			}else if((elms[i].name.match('src') == 'src' || elms[i].name.match('addl') == 'addl' )  && elms[i].checked){
				srcCount = srcCount+1;
			}
		}
		if(trgCount != 1 || srcCount == 0){
		   alert('Merge action expects one target line and multiple source lines.');
		}else{
		   alert('Selection valid');
		}
	}
	function allocate(src,trg){
		var elms = document.getElementsByTagName("input");
		var trgCount = 0;
		var srcCount = 0;
		for(var i=0; i< elms.length; i++){
			if(elms[i].name.match('trg')== 'trg' && elms[i].checked ){
			   trgCount = trgCount+1;
			}else if((elms[i].name.match('src') == 'src' || elms[i].name.match('addl') == 'addl' )  && elms[i].checked){
				srcCount = srcCount+1;
			}
		}		
		if(srcCount != 1){
		   alert('Allocate action expects one source line and multiple target lines');
		}
		else{
		   alert('Selection valid');
		}
	}