<?php
header("Content-type:application/json");

require_once 'example_con.php';

$sql = "SELECT * FROM `notice`";
$result = mysqli_query($con, $sql);
$rowNum = mysqli_num_rows($result);

$arr = array();
$j = 0;
for($i = (int)$rowNum; $i > 0; $i = $i - 1){
    $row = mysqli_fetch_array($result, MYSQLI_ASSOC);
    $arr[$j]= $row;
    $j++;
}
$jsonData = json_encode($arr);

echo "$jsonData";

