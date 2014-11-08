$(function(){ 
//ȡ��div�� 
var $search = $('#search'); 
//ȡ�������JQuery���� 
var $searchInput = $search.find('#search-text'); 
//�ر�������ṩ���������Զ���� 
$searchInput.attr('autocomplete','off'); 
//�����Զ���ɵ������б�������ʾ���������ص�����,������������ť�ĺ��棬����ʾ��ʱ���ٵ���λ�� 
var $autocomplete = $('<div class="autocomplete"></div>') 
.hide() 
.insertAfter('#submit'); 
//��������б�����ݲ������������б��� 
var clear = function(){ 
$autocomplete.empty().hide(); 
}; 
//ע���¼����������ʧȥ�����ʱ����������б����� 
$searchInput.blur(function(){ 
setTimeout(clear,500); 
}); 
//�����б��и�������Ŀ������������ʾ�����б����ʱ���ƶ������߼��̵����¼��ͻ��ƶ���������Ŀ����ٶ��������� 
var selectedItem = null; 
//timeout��ID 
var timeoutid = null; 
//����������ĸ������� 
var setSelectedItem = function(item){ 
//������������ 
selectedItem = item ; 
//�����¼���ѭ����ʾ�ģ�С��0���ó�����ֵ���������ֵ���ó�0 
if(selectedItem < 0){ 
selectedItem = $autocomplete.find('li').length - 1; 
} 
else if(selectedItem > $autocomplete.find('li').length-1 ) { 
selectedItem = 0; 
} 
//�����Ƴ������б���ĸ���������Ȼ���ٸ�����ǰ�����ı��� 
$autocomplete.find('li').removeClass('highlight') 
.eq(selectedItem).addClass('highlight'); 
}; 
var ajax_request = function(){ 
//ajax�����ͨ�� 
$.ajax({ 
'url':'/GeekSearch/auto_server.jsp', //�������ĵ�ַ 
'data':{'search-text':$searchInput.val()}, //���� 
'dataType':'json', //������������ 
'type':'POST', //�������� 
'success':function(data){ 
if(data.length) { 
//����data����ӵ��Զ������ 
$.each(data, function(index,term) { 
//����li��ǩ,��ӵ������б��� 
$('<li></li>').text(term).appendTo($autocomplete) 
.addClass('clickable') 
.hover(function(){ 
//�����б�ÿһ����¼�������ƽ�ȥ�Ĳ��� 
$(this).siblings().removeClass('highlight'); 
$(this).addClass('highlight'); 
selectedItem = index; 
},function(){ 
//�����б�ÿһ����¼�������뿪�Ĳ��� 
$(this).removeClass('highlight'); 
//������뿪ʱ������-1��������� 
selectedItem = -1; 
}) 
.click(function(){ 
//��굥�������б����һ��Ļ����ͽ���һ���ֵ��ӵ�������� 
$searchInput.val(term); 
//��ղ����������б� 
$autocomplete.empty().hide(); 
}); 
});//�¼�ע����� 
//���������б��λ�ã�Ȼ����ʾ�����б� 
var ypos = $searchInput.position().top; 
var xpos = $searchInput.position().left; 
$autocomplete.css('width',$searchInput.css('width')); 
$autocomplete.css({'position':'relative','left':xpos + "px",'top':ypos +"px"}); 
setSelectedItem(0); 
//��ʾ�����б� 
$autocomplete.show(); 
} 
} 
}); 
}; 
//�����������¼�ע�� 
$searchInput 
.keyup(function(event) { 
//��ĸ���֣��˸񣬿ո� 
if(event.keyCode > 40 || event.keyCode == 8 || event.keyCode ==32) { 
//����ɾ�������б��е���Ϣ 
$autocomplete.empty().hide(); 
clearTimeout(timeoutid); 
timeoutid = setTimeout(ajax_request,100); 
} 
else if(event.keyCode == 38){ 
//�� 
//selectedItem = -1 ��������뿪 
if(selectedItem == -1){ 
setSelectedItem($autocomplete.find('li').length-1); 
} 
else { 
//������1 
setSelectedItem(selectedItem - 1); 
} 
event.preventDefault(); 
} 
else if(event.keyCode == 40) { 
//�� 
//selectedItem = -1 ��������뿪 
if(selectedItem == -1){ 
setSelectedItem(0); 
} 
else { 
//������1 
setSelectedItem(selectedItem + 1); 
} 
event.preventDefault(); 
} 
}) 
.keypress(function(event){ 
//enter�� 
if(event.keyCode == 13) { 
//�б�Ϊ�ջ�������뿪���µ�ǰû������ֵ 
if($autocomplete.find('li').length == 0 || selectedItem == -1) { 
return; 
} 
$searchInput.val($autocomplete.find('li').eq(selectedItem).text()); 
$autocomplete.empty().hide(); 
event.preventDefault(); 
} 
}) 
.keydown(function(event){ 
//esc�� 
if(event.keyCode == 27 ) { 
$autocomplete.empty().hide(); 
event.preventDefault(); 
} 
}); 
//ע�ᴰ�ڴ�С�ı���¼������µ��������б��λ�� 
$(window).resize(function() { 
var ypos = $searchInput.position().top; 
var xpos = $searchInput.position().left; 
$autocomplete.css('width',$searchInput.css('width')); 
$autocomplete.css({'position':'relative','left':xpos + "px",'top':ypos +"px"}); 
}); 
}); 