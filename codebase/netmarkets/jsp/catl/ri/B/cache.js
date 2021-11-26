if(!window.localStorage){
    alert("浏览器不支持localstorage");
}

function getLocal(id){
	var storage_json=window.localStorage.getItem(id+"_local_B");
	if(storage_json==null){
		storage_json=window.localStorage.setItem(id+"_local_B","[]");
		return "[]";
	}
	return storage_json;
}

function getLocalArray(id,region){
	if(id=="selectmaterial"){
		id="material";
	}
	var storage_json=window.localStorage.getItem(id+"_local_B");
	if(storage_json!=null){
		var json=JSON.parse(storage_json);
		var array=eval('json.'+region);
		return array;
	}else{
		var array=new Array();
		return array;
	}
}

function setLocal(id,values){	
	for(var i=0;i<200;i++){
	var sid=standard_id;
	if(id=="diecut"){
		sid=diecut_standard_id;
	}
	if(id=="selectmaterial"){
		id="material";
	}
	var colarray=sid.split(",");
	 var oldjsonstr=getLocal(id);
	 var oldjson=JSON.parse(oldjsonstr);
	 var json={};
	 if(oldjson.length!=0){
		 json=oldjson;
	 }
	 
	for(var j=0;j<colarray.length;j++){
		var region=colarray[j]+i;
		var input=document.getElementById("input_"+id+"_"+region);
		if(input){
	       if(input.readOnly!=true){
	    	 var eValue=eval('values.'+colarray[j]+i); 
	    	 if(eValue){
	    		 var cell=document.getElementById(id+"_"+region);
	    		 if(cell){
	    			 if(cell.getAttribute("search")==null){
	    				 var array=eval('json.'+region);
	    	    		 if(array==undefined){
	    	    			 array=new Array();
	    	    		 }
	    	    		 if(!isInArray(array,eValue)){
	    	    			 array.push(eValue);
	    		    		 json[region]=array;
	    	    		 }
	    			 }
	    		 }
	    	 }
	    	
	       }
		}
	}
	window.localStorage.setItem(id+"_local_B",JSON.stringify(json));
}
}

function isInArray(arr,value){
    for(var i = 0; i < arr.length; i++){
        if(value === arr[i]){
            return true;
        }
    }
    return false;
}