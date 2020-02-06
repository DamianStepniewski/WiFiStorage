//@ sourceURL=filelist.js
//EXTRA METHODS
//Disables selection of text
$.fn.extend({ 
	disableSelection : function() { 
		return this.each(function() { 
			this.onselectstart = function() {
				return false;
			}; 
			this.unselectable = "on"; 
			$(this).css('user-select', 'none'); 
			$(this).css('-o-user-select', 'none'); 
			$(this).css('-moz-user-select', 'none'); 
			$(this).css('-khtml-user-select', 'none'); 
			$(this).css('-webkit-user-select', 'none'); 
		});
		return this;
	} 
});
//Centers the object on screen
$.fn.center = function () {
    this.css("position","absolute");
    this.css("top", Math.max(0, (($(window).height() - $(this).outerHeight()) / 2) + $(window).scrollTop()) + "px");
    this.css("left", Math.max(0, (($(window).width() - $(this).outerWidth()) / 2) + $(window).scrollLeft()) + "px");
    return this;
}
//INITIALIZATION
//Array of selected items
var selected = [];
//If ctrl is pressed
var ctrl = false;
//If the mouse is dragged
var isDragging = false;
//Dragging anchor coords array
var anchor = [0,0];
//If the user is dragging, make sure we suppress click events
var suppressClick = false;
//Timer for throttling dragging animation slowdown
var timer = 0, counter = 0;
//Sets the timer
setInterval(function (){
	timer++;
}, 100);

//MAIN
//Centers popup on scroll, even though there should never be any native scrolling on the page, so this shouldn't be needed, but better safe than sorry
$(document).on("scroll", function() {
	$("#popup").center();
});
//Disable selection on wrapper and context menu
$("#wrapper").disableSelection();
$("#contextmenu").disableSelection();
//Hide popup on overlay mouse click
$("#overlay").click(function() {
	hidePopup();
});
//Stop click event propagation on popup so it doesn't also register on overlay
$("#popup").click(function(event) {
	event.stopPropagation();
});
//User starts dragging, set anchor coords, show selection box
$("#contentcolumn").mousedown(function(event) {
	if (event.which == 1) {
		isDragging = true;
		$("#selection").show();
		anchor[0] = event.clientX;
		anchor[1] = event.clientY;
		$("#selection").css("left", anchor[0]-300);
		$("#selection").css("top", anchor[1]);
		$("#selection").width(0);
		$("#selection").height(0);
	}
});
//Disable context menu on content area, create our own menu instead
$("#contentcolumn").on("contextmenu", function(event) {
	event.preventDefault();
	event.stopPropagation();
	buildContextMenu();
});
//Disable context menu on file/dir icons, create our own menu instead
$(".bigicon").on("contextmenu", function(event) {
	event.preventDefault();
	event.stopPropagation();
	if (selected.length < 2)
		$(this).click();
	buildContextMenu();
});
//Hide selection box on mouseup
$(document).mouseup(function() {
	isDragging = false;
	$("#selection").hide();
});
//Check if user is dragging, properly draw the selection box, check if the box overlaps any files and select them if needed
$(document).mousemove(function(event){
	if (isDragging) {
		if (event.clientX != anchor[0] && event.clientY != anchor[1])
			suppressClick = true;
		$("#selection").css("left", anchor[0]-300);
		$("#selection").css("top", anchor[1]);
		var width = Math.abs(event.clientX-anchor[0]);
		var height = Math.abs(event.clientY-anchor[1]);
		if (anchor[0] > event.clientX)
			$("#selection").css("left", event.clientX-300);
		if (anchor[1] > event.clientY)
			$("#selection").css("top", event.clientY);
		$("#selection").width(width);
		$("#selection").height(height);
		
		if (timer > counter)
		$(".bigicon").each(function() {
			var position = $(this).offset();
			var rect = {x:0, y:0, width:0, height:0};
			rect.x = position.left;
			rect.y = position.top;
			rect.width = $(this).width();
			rect.height = $(this).height();
			getMultiFileDetails();
			if (rectOverlap(rect) && !$(this).hasClass("clicked")) {
				$(this).click();
			}
			else
			if (!rectOverlap(rect) && $(this).hasClass("clicked")) {
				$(this).removeClass("clicked");
				var index = selected.indexOf($(this));
				selected.splice(index, 1);
			}
			counter = timer;
		});
	}
});
//Create details for multiple selected files
function getMultiFileDetails() {
	var totalsize = 0;
	for (var i=0; i<selected.length;i++) {
		totalsize += parseInt(selected[i].attr("size"));
	}
	$("#filename").html(selected.length+" items");
	$("#filesize").html(formatSize(totalsize)+" ("+formatBytes(totalsize)+")");
	$("#lastmodified").html("n/a");
	$("#filetype").html("n/a");
}
//On ctrl keydown
$("#contentcolumn").keydown(function(event) {
	if (event.keyCode == 17)
		ctrl = true;
});
//On ctrl keyup
$("#contentcolumn").keyup(function(event) {
	if (event.keyCode == 17)
		ctrl = false;
});
//Deselect everything if clicked on content area, unless we just stopped dragging
$("#contentcolumn").click(function() {
	if (!suppressClick) {
		$(".bigicon").removeClass("clicked");
		selected = [];
		$("#filename").html("-");
		$("#filesize").html("-");
		$("#lastmodified").html("-");
		$("#filetype").html("-");
	}
	$("#contextmenu").fadeOut(100);
	suppressClick = false;
});
//On file/dir click, select only the object clicked, add to selection if ctrl is pressed, remove if it's already selected, build details
$(".bigicon").click(function(event) {
	event.stopPropagation();
	if (!ctrl && !isDragging) {
		$(".bigicon").removeClass("clicked");
		selected = [];
	}
	var index = -1;
	for (var i=0; i<selected.length;i++)
		if (selected[i].attr("path") == $(this).attr("path")) {
			index = i;
			break;
		}
	if (ctrl && index != -1) {
		selected.splice(index,1);
		$(this).removeClass("clicked");
	} else {
		$(this).addClass("clicked");
		selected.push($(this));
	}
	if (selected.length == 1) {
		$("#filename").html($(this).attr("name"));
		$("#filesize").html(formatSize($(this).attr("size"))+" ("+formatBytes($(this).attr("size"))+")");
		$("#lastmodified").html($(this).attr("lastmodified"));
		$("#filetype").html($(this).attr("type"));
	}
   	if (selected.length > 1) {
   		getMultiFileDetails();
   	}
   	$("#contextmenu").fadeOut(100);
}).mousedown(function(event) {
	event.stopPropagation();
});
//Load directory if doubleclicked, download if it's a file
$(".bigicon").dblclick(function() {
	if ($(this).children(".folder").length > 0)
		loadDirectory($(this).attr("path"),ssid);
	else
		window.location = "?do=dlfile&path="+$(this).attr("path")+"&ssid="+ssid;
});
//On context menu delete click, show popup
$("#contextmenu").off("click", "#delete").on("click", "#delete", function() {
	var content = "<b>The following items will be permanently deleted:</b><br />";
	for (var i=0; i<selected.length; i++) 
		content += selected[i].attr("name")+"<br />";
	content += "<div class=\"center pointer\" id=\"delete_confirm\"><b>Confirm</b></div>";
	showPopup(content);
	$("#contextmenu").fadeOut(100);
});
//Confirm file deletion, send POST request with ajax formatted object of file paths
$("#popup").off("click", "#delete_confirm").on("click", "#delete_confirm", function() {
	var paths = [];
	for (var i=0; i<selected.length;i++) {
		var file = {path:selected[i].attr("path")};
		paths.push(file);
	}
	$.post("", {do:"delete", paths:JSON.stringify(paths), ssid:ssid, path:$("#path").html()}, function( data ) {
		var response = JSON.parse(data);
  		if (response["status"] == true) {
			hidePopup();
			cut = [];
			copied = [];
			$("#wrapper").html(response["html"]);
		}
	});
});

$("#contextmenu").off("click", "#rename").on("click", "#rename", function() {
	var content = "<input type=\"text\" id=\"rename_new\" value=\""+selected[0].attr("name")+"\"/><div class=\"error\" id=\"error\">File already exist!</div><div class=\"center pointer\" id=\"rename_confirm\"><b>Confirm</b></div>";
	showPopup(content);
	$("#contextmenu").fadeOut(100);
});

$("#popup").off("click", "#rename_confirm").on("click", "#rename_confirm", function() {
	$("#error").fadeOut(200);
	var newName = $("#rename_new").val();
	var oldName = selected[0].attr("name");
	var filePath = selected[0].attr("path");
	var path = $("#path").html();
	var exists = false;
	$(".bigicon").each(function() {
		if (newName == $(this).attr("name")) {
			exists = true;
			return false
		}
	});
	if (!exists && oldName != newName)
		$.get("", {do:"rename", path:path, ssid:ssid, newName:newName, filePath:filePath}, function( data ) {
			var response = JSON.parse(data);
  			if (response["status"] == true) {
				hidePopup();
				cut = [];
				copied = [];
				$("#wrapper").html(response["html"]);
			}
		});
	else
		$("#error").fadeIn(200);
});

$("#contextmenu").off("click", "#newfolder").on("click", "#newfolder", function() {
	var content = "<input type=\"text\" id=\"newfolder_name\"/><div class=\"error\" id=\"error\">Folder already exist!</div><div class=\"center pointer\" id=\"newfolder_confirm\"><b>Create</b></div>";
	showPopup(content);
	$("#contextmenu").fadeOut(100);
});

$("#popup").off("click", "#newfolder_confirm").on("click", "#newfolder_confirm", function() {
	$("#error").fadeOut(200);
	var name = $("#newfolder_name").val();
	var path = $("#path").html();
	var exists = false;
	$(".bigicon").each(function() {
		if (name == $(this).attr("name")) {
			exists = true;
			return false
		}
	});
	if (!exists)
		$.get("", {do:"newfolder", path:path, ssid:ssid, name:name}, function( data ) {
			var response = JSON.parse(data);
  			if (response["status"] == true) {
				hidePopup();
				$("#wrapper").html(response["html"]);
			}
		});
	else
		$("#error").fadeIn(200);
});

$("#contextmenu").off("click", "#cut").on("click", "#cut", function() {
	$("#contextmenu").fadeOut(100);
	$(".bigicon").removeClass("cut");
	$(".bigicon").removeClass("copied");
	copied = [];
	for (var i=0; i<selected.length; i++)
		selected[i].addClass("cut");
	cut = selected;
});

$("#contextmenu").off("click", "#copy").on("click", "#copy", function() {
	$("#contextmenu").fadeOut(100);
	$(".bigicon").removeClass("copied");
	$(".bigicon").removeClass("cut");
	cut = [];
	for (var i=0; i<selected.length; i++)
		selected[i].addClass("copied");
	copied = selected;
});

$("#contextmenu").off("click", "#paste").on("click", "#paste", function() {
	$("#contextmenu").fadeOut(100);
	var action = "";
	var paths = [];
	if (cut.length > 0) {
		action = "move";
		for (var i=0; i<cut.length;i++) {
			var file = {path:cut[i].attr("path")};
			paths.push(file);
		}
	} else
	if (copied.length > 0) {
		action = "copy";
		for (var i=0; i<copied.length;i++) {
			var file = {path:copied[i].attr("path")};
			paths.push(file);
		}
	}
	$.post("", {do:action, paths:JSON.stringify(paths), ssid:ssid, path:$("#path").html()}, function( data ) {
		var response = JSON.parse(data);
  		if (response["status"] == true) {
			cut = [];
			copied = [];
			$("#wrapper").html(response["html"]);
		}
	});
});

$("#contextmenu").off("change", "#filesToUpload").on("change", "#filesToUpload", function() {
	var target = [];
	var fileCount = $("#filesToUpload").get(0).files.length;
	for (var i=0; i<fileCount; i++) {
		upload.files.push($("#filesToUpload").get(0).files[i]);
		upload.target.push($("#path").html());
		var size = +$("#filesToUpload").get(0).files[i].size;
		$("#uploadmanager").children(0).children(0).append("<div class=\"manageritem\" id=\""+size+"\"><div class=\"manageritem_name\">"+$("#filesToUpload").get(0).files[i].name+"</div><div class=\"manageritem_bar\"><div class=\"manageritem_bar_fill\" id=\"bar_"+size+"\"></div></div><div class=\"manageritem_size\" id=\"size_"+size+"\">Pending</div></div>");
		$("#"+$("#filesToUpload").get(0).files[i].size).fadeIn(500);
	}
	if (upload.files.length == fileCount)
		massUpload();
	$("#contextmenu").fadeOut(100);
	$("#uploadmanager_wrapper").fadeIn(200);
	$("#uploadmanager_title span").html("Uploading "+upload.files.length+(upload.files.length == 1 ? " file" : " files"));
});

$("#uploadmanager_title_minimize").off("click").on("click", function(e) {
	e.stopPropagation();
	if (minimized) {
		$("#uploadmanager_wrapper").animate({height:200, top:$(window).height()-200}, {duration:200, queue:false});
		$("#uploadmanager").animate({height: 165}, {duration:200, queue:false});
	} else {
		$("#uploadmanager_wrapper").animate({height:35, top:$(window).height()-35}, {duration:200, queue:false});
		$("#uploadmanager").animate({height: 0}, {duration:200, queue:false});
	}
	minimized = !minimized;
});

$("#uploadmanager_title_close").click(function() {
	$("#uploadmanager_wrapper").fadeOut(200);
});

$("#back").click(function() {
	loadDirectory($(this).attr("path"),ssid);
});

$(".treeitem").click(function() {
	loadDirectory($(this).attr("path"),ssid);
});

$(".caption").mouseenter(function() {
	$(this).addClass("wordwrap");
}).mouseleave(function() {
	$(this).removeClass("wordwrap");
});

resizeAllTheThings();
checkCutAndCopied();

$(window).resize(function() {
	resizeAllTheThings();
});

$("#leftcolumn,#contentcolumn,#uploadmanager").mCustomScrollbar({
	advanced:{ updateOnContentResize: true, updateOnSelectorChange: true },
	autoDraggerLength:true,
	scrollInertia: 100,
	theme: "minimal-dark"
});

$("#contentcolumn").append("<div id=\"selection\"></div>");

function massUpload() {
	$("#uploadmanager_title span").html("Uploading "+upload.files.length+(upload.files.length == 1 ? " file" : " files"));
	$("#uploadmanager_title_close").hide();
	var data = new FormData();
	data.append(encodeURI(upload.target[0]+"/"+upload.files[0].name), upload.files[0]);
	$.ajax({
		url: "",
		data: data,
		type: "POST",
		contentType:false,
		processData:false,
		xhr: function() {
			var xhr = new window.XMLHttpRequest();
			xhr.upload.addEventListener("progress", function(e) {
				$("#size_"+upload.files[0].size).html(formatSize(e.loaded)+" / "+formatSize(e.total));
				$("#bar_"+upload.files[0].size).css("width",(e.loaded/e.total*260)+"px");
			}, false);
			return xhr;
		},
		success: function() {
			if ($("#path").html() == upload.target[0])
				loadDirectory($("#path").html(),ssid);
			$("#"+upload.files[0].size).animate({height: 0, opacity: 0}, {duration:500, queue:false, complete: function() {
				$(this).remove();
			}});
			upload.files.splice(0,1);
			upload.target.splice(0,1);
			if (upload.files.length > 0)
				massUpload();
			else {
				$("#uploadmanager_title span").html("Uploading complete");
				$("#uploadmanager_title_close").show();
			}
		}
	});
}

function checkCutAndCopied() {
	for (var i=0; i<cut.length; i++)
		$(".bigicon").each(function() {
			if ($(this).attr("path") == cut[i].attr("path")) {
				cut.splice(i,1,$(this));
				$(this).addClass("cut");
				return false;
			}
		});
	for (var i=0; i<copied.length; i++)
		$(".bigicon").each(function() {
			if ($(this).attr("path") == copied[i].attr("path")) {
				copied.splice(i,1,$(this));
				$(this).addClass("copied");
				return false;
			}
		});
}

function valueInRange(value, min, max){
	return ((value >= min) && (value <= max));
}

function rectOverlap(B) {
	var A = {x:0, y:0, width:0, height:0};
	var position = $("#selection").offset();
	A.x = position.left;
	A.y = position.top;
	A.width = $("#selection").width();
	A.height = $("#selection").height();
    var xOverlap = valueInRange(A.x, B.x, B.x + B.width) || valueInRange(B.x, A.x, A.x + A.width);

    var yOverlap = valueInRange(A.y, B.y, B.y + B.height) || valueInRange(B.y, A.y, A.y + A.height);

    return (xOverlap && yOverlap);
}

function resizeAllTheThings() {
	$("#leftcolumn").css("height", $(window).height());
	$("#rightcolumn").css("height", $(window).height());
	$("#rightcolumn").css("left",$(window).width()-300);
	$("#contentcolumn").css("height", $(window).height());
	$("#contentcolumn").css("width", $(window).width()-600);
	$("#uploadmanager_wrapper").css("left", $(window).width()-650);
	$("#uploadmanager_wrapper").css("top", $(window).height()-(minimized ? 35 : 200));
}

function showPopup(content) {
	$("#overlay").fadeIn(200,function(){
		$("#popup").fadeIn(200);
	});
	$("#popup").html(content);
	$("#popup").center();
}

function hidePopup() {
	$("#popup").fadeOut(200,function(){
		$("#overlay").fadeOut(200);
	});
}

function formatFloat(input) {
	var out = Math.round(input * 100);
	return out/100;
}

function formatSize(length) {
	if (length < 1024)
		return length+" B";
	if (length < 1048576)
		return formatFloat(length/1024)+ " KB";
	if (length < 1073741824)
		return formatFloat(length/1048576)+ " MB";
	return formatFloat(length/1073741824)+ " GB";
}

function formatBytes(size) {
	size = String(size);
	var formated = "";
	var j=0;
	for (var i=size.length-1; i>=0;i--) {
		formated += size.charAt(i);
		j++;
		if (j%3 == 0 && i!=0)
			formated += ",";
	}
    size = "";
    for (var i=formated.length-1; i>=0; i--)
    	size +=formated.charAt(i);
    return size+" B";
}

function buildContextMenu() {
	if (selected.length == 0)
		$("#contextmenu").html(((cut.length > 0 || copied.length > 0) ? "<div class=\"contextitem\" id=\"paste\">Paste</div>":"")+"<div class=\"contextitem\" id=\"newfolder\">New folder</div><div class=\"contextitem\" id=\"upload\"><input type=\"file\" multiple id=\"filesToUpload\" />Upload files</div>");
	if (selected.length == 1)
		$("#contextmenu").html("<div class=\"contextitem\" id=\"rename\">Rename</div><div class=\"contextitem\" id=\"cut\">Cut</div><div class=\"contextitem\" id=\"copy\">Copy</div><div class=\"contextitem\" id=\"delete\">Delete</div>");
	if (selected.length > 1)
		$("#contextmenu").html("<div class=\"contextitem\" id=\"cut\">Cut</div><div class=\"contextitem\" id=\"copy\">Copy</div><div class=\"contextitem\" id=\"delete\">Delete</div>");
	$("#contextmenu").css("left", event.pageX);
	$("#contextmenu").css("top", event.pageY);
	$("#contextmenu").fadeIn(100);
}