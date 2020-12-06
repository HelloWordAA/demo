/*验证是否登录，由于ajax发送非同源请求会发生跨域问题，用jsonp*/
$(function(){
    $.ajax({
        url:"http://localhost:8084/sso/islogin",
        success:function(data){
            /*当data不为null时，即已经登录，通过jq将html语句拼接进去，实现动态效果*/
            if(data){
                $("#pid").html(JSON.parse(data).nickname+"您好，欢迎来到<b><a>ShopCZ商城</a><a href='http://localhost:8084/sso/logout'>注销</a></b>");
            }else{
                $("#pid").html("[<a href='javascript:login();'>登录</a>]\n" +
                    "[<a href=\"http://localhost:8084/sso/toregister\">注册</a>]");
            }
        },
        dataType:"jsonp",
        jsonpCallback:"islogin"
    })

});

function login(){
    //location.href  相当于当前的页面，相当于替换之前的页面，，可以实现登录之后返回原页面
    var returnUrl = location.href;
    //浏览器会自动解码，所以编码一次，否则乱码
    returnUrl = encodeURI(returnUrl);
    //相当于将当前页面替换为新的页面
    location.href="http://localhost:8084/sso/tologin?returnUrl="+returnUrl;
}