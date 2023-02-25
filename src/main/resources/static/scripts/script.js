$(document).ready(function(){

    $("#category").change(function(){
        var catId = $(this).val();
        $.ajax({
            url: '/services?categoryId='+catId,
            type: 'GET',
            success:function(response){
                var len = response.length;

                $("#service").empty();
                for( var i = 0; i<len; i++){
                    var service = response[i];
                    $("#service").append("<option value='"+response[i][0]+"'>"+response[i][1]+"</option>");
                }
            }
        });
    });
});