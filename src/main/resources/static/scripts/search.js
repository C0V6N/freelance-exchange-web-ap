

$(document).ready(function(){
    $("#search").on('input', function(){
        var query = $(this).val();
        $.ajax({
            url: '/search?term='+query,
            type: 'GET',
            success:function(data){
                var len = data.length;
                var count = 0;
                $(".orders_list").empty();
                for(var i = 0; i < len; i++){

                    var div = "<div class='order'>" +
                        "<div class='order_description'> " +
                        "<a href='orders/order-" + data[i][0] + "'><h3>" + data[i][1] + "</h3></a>" +
                        "<p class='order_short_description'>" + data[i][2] +"</p>" +
                        "<p>" + data[i][7] + " > " + data[i][8] + "</p>" +
                        "</div>" +
                        "<div class='order_action'>" + "<p class='order_action_time'>До " + data[i][6] + "</p>" +
                        "<div class='order_action_price'><p>" + data[i][3] + " ₽</p><p>за заказ</p></div>" +
                        "<p class='order_action_responses'><i class='fa fa-reply'></i> " + data[i][9] + "</p>" +
                        "<a href='orders/order-" + data[i][0] + "' class='order_action_button'>Откликнуться</a>" +
                        "</div>" +
                        "</div>";

                    $(".orders_list").append(div);
                    count++;
                }
                $(".orders_count").text(count);
            }
        });
    });
});

