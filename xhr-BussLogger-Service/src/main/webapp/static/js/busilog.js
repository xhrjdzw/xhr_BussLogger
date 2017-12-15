define(function(require, module, exports){
    //./表示根目录
	require('../js/requireConfig.js');
	require('../js/requireConfig_temp.js');
    var html = require('text!../html/busilog.html');
	require('css!/uui/libs/uui/css/u.css');
    require('css!/uui/libs/uui/css/font-awesome.min.css');
    require('css!../css/busilog.css');
	var app, viewModel, basicDatas, computes, events, inputDom, inputlen, searchflag, usersearchflag,localbrowser;
	basicDatas = {
		listData: new u.DataTable({
			meta: {
				"operuser": {
					type: 'string'
				},
				"clientip": {
					type: 'string'
				},
				"logcategory": {
					type: 'string'
				},
				"logdate": {
					type: 'datetime',format:'YYYY-MM-DD hh:mm:ss'
				},
				"logcontent": {
					type: 'string'
				}
			}
		}),
        groupListData: new u.DataTable({
            meta:{
                "userCode" : {
                   type: 'string'
                },
                "userName" : {
                    type: 'string'
                }
            }
        }),
        dateintervalData: new u.DataTable({
	        meta:{
	            "startField" : {
	               type: 'string'
	            },
	            "endField" : {
	                type: 'string'
	            }
	        }
	    })
	};

	computes = {
	}

	events = {
		afterAdd: function(element, index, data) {
			if (element.nodeType === 1) {
				u.compMgr.updateComp(element);
			}
		},
		pageChangeFunc: function(index) {
			var params={},querydata={};
			querydata.queryType='pageNum';//特定页面检索
			querydata.pageNum=index+1;//第几页数据
			var allcondition = conditionFun();
			if(allcondition!="" && searchflag){
				querydata.param=allcondition;//如果有此参数则增加高级检索条件
			}
			querydata.pageSize=$("#pagination select").find("option:selected").text();
			params.querydata=querydata;
			params.pageSize=$("#pagination select").find("option:selected").text();
			params.pageNum=index+1;
			ajaxFunction(params);
		},
		sizeChangeFunc: function(newsize) {
			var params={},querydata={};
			querydata.queryType='pageSizeChange';//特定页面检索
			querydata.pageSize=newsize;//每页数据条数
			var allcondition = conditionFun();
			if(allcondition!="" && searchflag){
				querydata.param=allcondition;//如果有此参数则增加高级检索条件
			}
			params.querydata=querydata;
			params.pageSize=newsize;
			ajaxFunction(params);
		},
		//用户参照对话框检索特定页
		userPageChangeFunc: function(index){
			var userquerydata={};
			userquerydata.pageNum=index+1;//第几页数据
			userquerydata.pageSize=5;
			var condition = $(".user-search-input").val();
			if(condition!="" && usersearchflag){
				userquerydata.search=condition;//如果有此参数则增加高级检索条件
			}
			userAjaxFunction(userquerydata);
		},
		//页面初始化
		loadData:function(){
			searchflag=false;//判断查询按钮是否点击过
			var params={},querydata={};
			querydata.queryType='all';//全检索
			querydata.pageSize=10;//全检索
			params.querydata=querydata;
			params.pageSize=10;
			ajaxFunction(params);
		},
	
       /*页面初始加载*/
	    pageInit:function(){
	    	basicDatas.dateintervalData.createEmptyRow();
	        u.createApp({
	            el:'.busslog',
	            model:viewModel
	        });
	        viewModel.loadData();
	        
	        localbrowser = myBrowser();
	    	//下拉框
	    	document.getElementById('logkindcombo')['u.Combo'].setComboData([{value:'01',name:'业务日志'},{value:'02',name:'安全日志'}]);
	    	
	    	inputDom=document.querySelectorAll('input[class*="searchinput"]');//所有条件输入框
	    	inputlen=inputDom.length;
	    	var searchbtn=document.querySelector('[data-role="searchbtn"]');//检索条件按钮
	    	var clearbtn=document.querySelector('[data-role="clearbtn"]');//清除条件按钮
	    	var toggleBtn = document.querySelector('#condition-toggle');//高级检索按钮
	    	var userrefer=document.querySelector('[data-role="user-refer"]');//用户编码参照
			var userConfirmBtn=document.querySelector('[data-role="userConfirmBtn"]');//用户编码参照确定按钮
			var userSearchSpan=document.querySelector('[data-role="userSearchSpan"]');//用户编码检索按钮
			var returnbtn=document.querySelector('[data-role="returnbtn"]');//返回按钮
	    	var ifuse=false;//是否可用
	    	
	    	//高级检索功能
	    	u.on(toggleBtn, 'click', function(){
	    	  var conditionRow = document.querySelector('#condition-row');
	    	  var toggleIcon = this.querySelector('i');
	    	  if (u.hasClass(conditionRow, 'u-visible')){
	    	      u.removeClass(conditionRow, 'u-visible').addClass(conditionRow, 'u-not-visible');
              u.removeClass(toggleIcon, 'icon-arrow-up').addClass(toggleIcon, 'icon-arrow-down');
	    	       //this.querySelector('span').innerText='高级';
	    	       //清空查询条件
	    	       for(var i=0;i<inputlen;i++){
	    		        if(inputDom[i].value.length>0){
	    		           inputDom[i].value="";
	    		        }   
	    		     }
	    	  }else{
	    	    u.removeClass(conditionRow, 'u-not-visible').addClass(conditionRow, 'u-visible');
            u.removeClass(toggleIcon, 'icon-arrow-down').addClass(toggleIcon, 'icon-arrow-up');
	    	     //this.querySelector('span').innerText='收起';
	    	  }
	    	})

	    	u.on(searchbtn, 'click', function(){//检索按钮
	    		var searchhascondition = domshasvalue();
	    		if(searchhascondition){
	    			searchflag=true;
	    			//ajax请求
	    			var params={},querydata={};
	    			querydata.queryType='search';//高级条件检索
	    			querydata.pageSize=10;
	    			var allcondition = conditionFun();
	    			querydata.param=allcondition;
	    			params.querydata=querydata;
	    			params.pageSize=10;
	    			ajaxFunction(params);
	    		}else{//全检索
	    			var params={},querydata={};
	    			querydata.queryType='all';//高级条件检索
	    			querydata.pageSize=10;
	    			params.querydata=querydata;
	    			params.pageSize=10;
	    			ajaxFunction(params);
	    		}
	    	})
	    	
	    	u.on(clearbtn, 'click', function(){//清除按钮
	    		for(var i=0;i<inputlen;i++){
	    	        if(inputDom[i].value.length>0){
	    	           inputDom[i].value="";
	    	           $(inputDom[i]).parent().attr("title","");//去掉tooltip
	    	        }   
	    	     }
	    	})
	    	
	    	u.on(returnbtn, 'click', function(){//清除按钮
	    		history.back();
	    	})
	    	
	    	
	    	//用户编码参照
			u.on(userrefer, 'click', function(){
	    	     //用户编码信息查询
				var userquerydata={};
				userquerydata.pageNum=1;
				userquerydata.pageSize=5;
				userquerydata.search="";
				userAjaxFunction(userquerydata);
			    usersearchflag=false;
	            $('#addModal').modal({
	    			backdrop: false
	    		});
	            if(localbrowser=="IE"){
	            	$(".user-search-input").focus();
	            }
	    	});
	    	//参照确定按钮
			u.on(userConfirmBtn, 'click', function(){
				if(viewModel.groupListData.rows().length>0){
					var selectrow = viewModel.groupListData.getSimpleData({type:'select',fields:['userCode']});
					if(selectrow.length>0){
						var usercode = selectrow[0].userCode;
						$("#userrefer input").val(usercode);
					}else{
						u.showMessage({msg:"请选取操作人!",position:"center",msgType:"warning"});
						return;
					}
			    }
				$(".user-search-input").val("");
				$('#addModal').modal('hide');
	    		//$(".search-groupbtn-div button").removeClass("disable");
	    	});
			
	    	u.on(userSearchSpan, 'click', function(){//用户编码检索按钮
	    		userConditionSearchFunc();
	    	});
	    	
	    	//时间元素监听
	    	viewModel.dateintervalData.on('valueChange', function(event){

		    	var type = event.field;
		    	var oldValue = event.oldValue;
		    	var newValue = event.newValue;
		    	if(newValue==null && type=="startField"){
		    		$("#start_date").attr("title","");//去掉tooltip
		    	}else if(newValue==null && type=="endField"){
		    		$("#end_date").attr("title","");//去掉tooltip
		    	}
	    	});
			
	    }
	}
	viewModel = u.extend({}, basicDatas, computes, events)
	
	//所有ajax请求
	var ajaxFunction = function(params) {
		$.ajaxSetup({cache:false});
		//分页初始化
		var element = document.getElementById('pagination');
		var comp = new u.pagination({el:element,jumppage:true});
		var localurl = window.busilogbaseUrl+"busilog/queryLogs";
		$.ajax({
			type: "POST",
			data:{
                "querydata":JSON.stringify(params.querydata)
                },
			dataType: "json",
			url: localurl,
			success: function(result) {
				if(!result || result==null){
					return;
				}
				if (result.status!=1 && result.msg) {
					u.alert(result.msg)
					return
				}
				if(result.data.busiLogs.length>0){
					var pageSize=result.pageSize?result.pageSize:params.pageSize;
					var totalpages=Math.ceil(parseInt(result.data.nums)/pageSize);
					var currentPage=params.pageNum?params.pageNum:1;
					viewModel.listData.removeAllRows();
					viewModel.listData.setSimpleData(result.data.busiLogs,{unSelect:true});
                    if(totalpages>1){
                      $(".paginate-box").removeClass("hide");	
                    }else{
                    	$(".paginate-box").addClass("hide");
                    }
					comp.update({totalPages: totalpages,pageSize:pageSize,currentPage:currentPage,totalCount:result.data.nums});
					comp.on('pageChange',viewModel.pageChangeFunc);
					comp.on('sizeChange',viewModel.sizeChangeFunc);
				}else{
					comp.update({totalPages: 0,pageSize:0,currentPage:0,totalCount:0});
					viewModel.listData.removeAllRows();
					viewModel.listData.createEmptyRow().setSimpleData({"logdate":"无数据！"})
				}
			},
			error: function() {}
		})
	}

	var domshasvalue=function(){
	     for(var i=0;i<inputlen;i++){
	        if(inputDom[i].value.length>0){
	            return true;
	        }   
	     }
	     return false;
	}
	
	//拼接高级检索中的检索条件
	var conditionFun=function(){
		var allcondition="{";
		for(var i=0;i<inputlen;i++){
	        if(inputDom[i].value.length>0){
	        	var searchCondition=(inputDom[i].id=="operuser"?"operuser":(inputDom[i].id=="logcategory"?"logcategory":(inputDom[i].id=="start"?"logstartdate":"logenddate")));
	        	allcondition+="\""+searchCondition+"\""+":"+"\""+inputDom[i].value+"\""+",";
	        }   
	     }
		if(allcondition.substring(0,allcondition.length-1)!=""){
			allcondition=allcondition.substring(0,allcondition.length-1)+"}";
		}else{
			allcondition="";
		}
		//return eval("(" + allcondition + ")");
		return allcondition;
	}

	//用户参照对话框条件检索
	var userConditionSearchFunc=function(){
		usersearchflag=true;
		var userquerydata={};
		userquerydata.pageSize=5;
		var condition = $(".user-search-input").val();
		if(condition!=""){
			userquerydata.search=condition;//如果有此参数则增加高级检索条件
		}
		userAjaxFunction(userquerydata);
	}
	
	//所有ajax请求
	var userAjaxFunction = function(userquerydata){
		$.ajaxSetup({cache:false});
		var userpag = document.getElementById('user-pagination');
		var comp = new u.pagination({el:userpag,jumppage:true});
		$.ajax({
            type: 'POST',
            dataType: 'json',
            url: window.busilogbaseUrl+"busilog/queryUsersByPages",
            async: false,
            data: {
                "data":JSON.stringify(userquerydata)
            },
            success: function (res) {
				if(res.status==1)
				{
					var data=res.data;
					if(data.userList.length>0){
						comp.on('pageChange',viewModel.userPageChangeFunc);
						var pageSize=data.pageSize?data.pageSize:userquerydata.pageSize;
						var totalpages=Math.ceil(parseInt(res.data.totalElements)/pageSize);
						var currentPage=userquerydata.pageNum?userquerydata.pageNum:1;
						comp.update({totalPages: totalpages,pageSize:pageSize,currentPage:currentPage,totalCount:res.data.totalElements,showState:false});
						viewModel.groupListData.removeAllRows();
						viewModel.groupListData.setSimpleData(data.userList,{unSelect:true});
					}else{
						comp.update({totalPages: 0,pageSize:0,currentPage:0,totalCount:0});
						viewModel.groupListData.removeAllRows();
						viewModel.groupListData.createEmptyRow().setSimpleData({"userName":"无数据！"});
					}
				}else{
					comp.update({totalPages: 0,pageSize:0,currentPage:0,totalCount:0});
					viewModel.groupListData.removeAllRows();
					u.showMessage({msg:res.msg,position:"center",msgType:"error"});
				}
            }
        })
	}
	
	var myBrowser = function(){
	    var userAgent = navigator.userAgent;
	    var isOpera = userAgent.indexOf("Opera") > -1;
	    if (isOpera) {
	        return "Opera";
	    }else if (userAgent.indexOf("Firefox") > -1) {
	        return "FF";
	    }else if (userAgent.indexOf("Chrome") > -1){
	        return "Chrome";
	    }else if (userAgent.indexOf("Safari") > -1) {
	        return "Safari";
	    }else if (userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera) {
	        return "IE";
	    }; //判断是否IE浏览器
   }
	//日志内容展开详情
	window.logDetailFun=function(td){
		if($(".logdetail-panel").length>0){//如果有未关闭的先关闭
			$("#logtable tr[id='logtr']").remove();
		}
		var tdContent = $(td).text();
		if(tdContent.trim()!=""){
			var tdIndex = $(td).parent().attr("rowindex");
			var panelStr = "<div class='u-panel u-panel-bordered logdetail-panel'><div class='u-panel-body'><p>"+tdContent+"</P></div><div class='u-panel-footer logdetail-panel-footer' style='padding:0px;'><button class='u-button raised logCloseBtn' id='logCloseBtn'>关闭</button></div></div>";
			var tr = "<tr id='logtr'><td colspan=8>"+panelStr+"</td></tr>";
			$("#logtable tr:eq("+tdIndex+")").after(tr);
		}
	}
	window.tootipShow=function(tr,td){
		var tValue = td.currentTarget.innerText;
		td.currentTarget.setAttribute("title", tValue);
	}
	
	$(document).on("click", "#logCloseBtn", function(e) {
		$("#logtable tr[id='logtr']").remove();
	});
    return {
        init:function(content) {
            // 插入内容
            content.innerHTML = html;
            viewModel.pageInit();
        }
    }
})

