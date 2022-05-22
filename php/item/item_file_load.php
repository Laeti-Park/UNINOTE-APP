<?php
header("Content-type:application/json");

require_once 'example_con.php';

$userID = $_POST['userID'];
$itemID = $_POST['itemID'];

$sql = "SELECT `fileName`, `fileRealName` FROM `file` WHERE `userID`='$userID' AND `itemID`='$itemID'";

$result = mysqli_query($con, $sql);
$rowCnt = mysqli_num_rows($result);

$arr = array();

for ($i = 0; $i < $rowCnt; $i++) {
    $row = mysqli_fetch_array($result, MYSQLI_ASSOC);
    $arr[$i] = $row;
}

$jsonData = json_encode($arr);
echo "$jsonData";