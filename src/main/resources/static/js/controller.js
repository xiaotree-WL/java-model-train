function search() {
    //通过id获得要添加数据的表格
    var task_name = document.getElementById("task_name");
    alert(task_name);
    var url = "/consumer/admin?taskName="+task_name;
    var request = new XMLHttpRequest();
    request.open("GET", url);
}