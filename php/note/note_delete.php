<?php
header("Content-type:application/json");
require_once 'example_con.php';

$type = (int)$_POST['type'];
$mapID = $_POST['mapID'];

if($type == 0){
    $sql = "UPDATE `notice` SET `noticeAvailable` = '0' WHERE `notice`.`noticeID` = '$mapID'";
    $result = mysqli_query($con, $sql);
}
else if($type == 1){
    $sql = "UPDATE `bbs` SET `bbsAvailable` = '0' WHERE `bbs`.`bbsID` = '$mapID'";
    $result = mysqli_query($con, $sql);
}
else if($type == 2){
    $sql = "UPDATE `studyboard` SET `studyboardAvailable` = '0' WHERE `studyboard`.`studyboardID` = '$mapID'";
    $result = mysqli_query($con, $sql);
}