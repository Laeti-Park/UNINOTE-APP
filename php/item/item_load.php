<?php
header("Content-type:application/json");
header("content-type: application/x-www-form-urlencoded; charset=utf-8");

require_once 'example_con.php';

$userID = $_POST['userID'];

$sql = "SELECT `item`.`itemID`, `item`.`itemContent`, `item`.`itemCount`, `note`.`noteContent` FROM `item`, `note` WHERE `note`.`userID` = '$userID' AND `item`.`userID` = '$userID' AND `note`.`itemID` = `item`.`itemID`";

$result = mysqli_query($con, $sql);
$rowCnt= mysqli_num_rows($result);
 
$arr= array();

for($i=0; $i<$rowCnt; $i++){
    $row= mysqli_fetch_array($result, MYSQLI_ASSOC);
    $arr[$i]= $row;
}

$jsonData=json_encode($arr);
echo "$jsonData";
mysqli_close($con);