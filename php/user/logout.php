<?php
header("Content-type:application/json");
require_once 'example_con.php';
//$con = mysqli_connect("hjk709914.cafe24.com", "hjk709914", "tiger123*", "hjk709914");

$userID = $_POST['userID'];

$sqlIsLogout = "UPDATE `user` SET `isLogin` = '0' WHERE `user`.`userID` = '$userID'";
$resultLogout = mysqli_query($con, $sqlIsLogout);