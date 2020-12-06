$(function(){
    $.ajax({
        url:"http://localhost:8085/cart/list",
        success:function(data){
            if(data){
                //设置购物车的商品数量
                $("#cart_number_id").html(data.length);
                //设置购物车的列表
                var html = "<ul style='width: 100%;'>";
                for(var i = 0; i < data.length; i++){
                    //获得购物车的商品信息
                    var cart = data[i];
                    html += "<li style='margin-bottom: 15px; width: 200px'>";

                    //处理图片路径
                    var url = cart.goods.gimage.split("|")[0];
                    html += "<img style='width: 50px; height: 30px' src='http://192.168.6.128/" + url + "'/>";
                    html += cart.goods.gname;
                    // html += cart.goods.gprice;
                    // html += "小计：" + cart.allprice;
                    html += "</li>";
                }
                html += "</ul>";
                $("#cart_id").html(html);
            }
        },
        dataType:"jsonp",
        jsonpCallback:"cartlist"
    })

})