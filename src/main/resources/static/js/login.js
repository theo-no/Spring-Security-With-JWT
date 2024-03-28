
let userId = $('#userId');
let password = $('#password');
let btn = $('#btn');

$(btn).on('click', function(){
	if($(userId).val()==""){
		$(userId).next('label').addClass('warning');
		setTimeout(function(){
			$('label').removeClass('warning');
		}, 1500);
	}else if($(password).val()==""){
		$(password).next('label').addClass('warning');
		setTimeout(function(){
			$('label').removeClass('warning');
		}, 1500);
	}else{
		//여기서 userId, password 가지고 login post 통신하자
		var loginRequest  = {
		    userId : $(userId).val(),
		    password : $(password).val()
		};
		sendAjaxRequest(
		    "/login",
		    "POST",
		    loginRequest,
		    null,
            function(response){
                alert("로그인 성공");
                console.log(response.responseJSON); //응답 객체 뽑기
                console.log(response.getResponseHeader('Authorization')); //access token 얻기
                localStorage.setItem('access token',response.getResponseHeader('Authorization').split(' ')[1]); //local storage에 access token 저장
                localStorage.setItem('userId', response.responseJSON.userId);
                window.location.href = "/user";
            },
            function(response){
                console.log(response.status);
            }
		);
	}
});


//ajax 호출 공통 함수
function sendAjaxRequest(url, type, requestData, params,
	successCallback, errorCallback){
	var options = {
	    url: url ,
	    type: type,
	    contentType: "application/json",
	    headers: {
	      "Authorization": "Bearer " + localStorage.getItem('access token')
	    },
	    complete: function(response) {
            if (response.status === 200) {
              successCallback(response, response.responseJSON.data);
            } else {
              errorCallback(response);
            }
          }
	  };
    // data가 null이 아닌 경우에만 options에 data 추가
    if (requestData !== null) {
    	options.data = JSON.stringify(requestData);
    }
    // params가 null이 아닌 경우에만 URL의 쿼리 파라미터로 추가
	if (params !== null && typeof params === 'object' && Object.keys(params).length > 0) {
	    var queryParams = new URLSearchParams(params);
	    options.url += '?' + queryParams.toString();
	}
    // Ajax 요청 보내기
    $.ajax(options);
}
