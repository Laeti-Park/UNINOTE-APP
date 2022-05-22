<?php
header("Content-type:application/json");

require_once 'example_con.php';

$editType = array();
$type = $_POST['type'];
$key = $_POST['key'];
$title = $_POST['title'];
$contents = $_POST['contents'];



if($type == 0){
    $sql = "UPDATE `notice` SET `noticeTitle` = '$title', `noticeContent` = '$contents' WHERE `notice`.`noticeID` = $key";
}else if($type == 1){
    $sql = "UPDATE `bbs` SET `bbsTitle` = '$title', `bbsContent` = '$contents' WHERE `bbs`.`bbsID` = $key";
}else if($type == 2){
    $sql = "UPDATE `studyboard` SET `studyboardTitle` = '$title', `studyboardContent` = '$contents' WHERE `studyboard`.`studyboardID` = $key";
}


$result = mysqli_query($con, $sql);
$error = $sql;

echo json_encode(array("error" => $error));