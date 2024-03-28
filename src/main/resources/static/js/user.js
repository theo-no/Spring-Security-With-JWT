if((/.*/).test(window.location.href)){
    sendAjaxRequest(
        "user/info",
        "GET",
        null,
        null,
        function(response){
            console.log("유저 정보 조회 성공");
            //console.log(response.responseJSON.data); // TODO response.responseJSON.data가 응답된 dto -> 함수 개선 필요
        },
        function(response){
            console.log("유저 정보 조회 실패");
        }
    )

}

//ajax 호출 공통 함수
function sendAjaxRequest(url, type, requestData, params,
	successCallback, errorCallback){
	var options = {
	    url: url ,
	    type: type,
	    contentType: "application/json",
	    headers: {
	      "Authorization": "Bearer " + localStorage.getItem('access token'),
	      "userId": localStorage.getItem('userId')
	    },
	    complete: function(response, xhr) {
            if (response.status === 200) {
                successCallback(response);
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

// 응답에 대해 공통 로직
$(document).ajaxComplete(function(event, xhr, settings) {
    if (xhr.status === 401) { //401 에러 발생
        var errorCode = xhr.getResponseHeader('errorCode');
        switch(errorCode){
            case "Auth-001": // access token 만료
                console.log("access token 만료");
                    sendAjaxRequest(
                       "/reissue",
                       "GET",
                       null,
                       null,
                       function(response){
                           alert("재발급 성공");
                           console.log(response);
                           console.log(response.getResponseHeader('Authorization'));
                           localStorage.setItem('access token',response.getResponseHeader('Authorization').split(' ')[1]); //local storage에 access token 저장
                           resendOriginalRequest(settings);
                       },
                       function(response){
                           console.log(response);
                           console.log(response.getResponseHeader('errorCode'));
                       }
                   )
                   break;
            case "Auth-002": // 유효하지 않은 access token
                break;
            case "Auth-003": // refresh token 만료
                //로그아웃 처리
                alert("refresh token 만료");
                localStorage.removeItem('access token');
                localStorage.removeItem('userId');
                window.location.href = "/login";
                break;
            case "Auth-004": // 유효하지 않은 refresh token
                //로그아웃 처리
                alert("invalid refresh token");
                localStorage.removeItem('access token');
                localStorage.removeItem('userId');
                window.location.href = "/login";
                break;
            case "Auth-005": // refresh token 서버에 없음
                //로그아웃 처리
                alert("refresh token not found");
                localStorage.removeItem('access token');
                localStorage.removeItem('userId');
                window.location.href = "/login";
                break;
            case "Auth-006": // refresh token이 null
                //로그아웃 처리
                alert("refresh token null");
                localStorage.removeItem('access token');
                localStorage.removeItem('userId');
                window.location.href = "/login";
                break;
            case "Auth-007": // userId이 null TODO 나중에는 접근할 수 있는 KEY가 없음으로 변경
                break;
        }
    }
});
// 토큰 재발급 성공 시 원래의 요청을 재요청하는 함수
function resendOriginalRequest(originalSettings) {
  // 재요청 시 이전에 사용된 토큰을 제거
  delete originalSettings.headers.Authorization;

  // 새로운 accessToken을 헤더에 추가
  originalSettings.headers.Authorization = "Bearer " + localStorage.getItem('access token');

  // 원래의 요청을 재요청
  $.ajax(originalSettings);
}
