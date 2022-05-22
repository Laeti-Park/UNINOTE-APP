<?php
header("Content-type:application/json");

require_once 'example_con.php';

$userID = $_POST['userID'];

$sqluser = "DELETE FROM user WHERE `user`.`userID` = '$userID'";
$sqlmaplikey = "DELETE FROM maplikey WHERE `maplikey`.`userID` = '$userID'";
$sqlcounts = "DELETE FROM counts WHERE `counts`.`userID` = '$userID'";
$sqlpublicmap = "DELETE FROM publicmap WHERE `publicmap`.`userID` = '$userID'";
$sqlbbs = "UPDATE `bbs` SET `bbsAvailable` = '0' WHERE `bbs`.`userID` = '$userID'";
$sqlnotice = "UPDATE `notice` SET `noticeAvailable` = '0' WHERE `notice`.`userID` = '$userID'";
$sqlstudyboard = "UPDATE `studyboard` SET `studyboardAvailable` = '0' WHERE `studyboard`.`userID` = '$userID'";

$result1 = mysqli_query($con, $sqluser);
$result2 = mysqli_query($con, $sqlmaplikey);
$result3 = mysqli_query($con, $sqlcounts);
$result4 = mysqli_query($con, $sqlpublicmap);
$result5 = mysqli_query($con, $sqlbbs);
$result6 = mysqli_query($con, $sqlnotice);
$result7 = mysqli_query($con, $sqlstudyboard);
