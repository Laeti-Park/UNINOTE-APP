<?php

header("Content-type:application/json");

require_once 'example_con.php';

$userID = $_POST['userID'];

$sql = "SELECT * FROM `publicmap` WHERE `userID` = '$userID'";
$result = mysqli_query($con, $sql);
$row = mysqli_fetch_array($result);

if($row["userID"] == $userID) {
    $error = "ok";
    echo json_encode(array("error" => "$error", "userID" => $row["userID"], "publicMap" => $row["publicmap"],"mapPassword" => $row["password"]));
} else {
    $error = "failed";
    $sqlInsert = "INSERT INTO `publicmap` (`userID`, `publicmap`, `password`) VALUES ('$userID', 1, '')";
    $resultInsert = mysqli_query($con, $sqlInsert);

    echo json_encode(array("error"=>"$error"));
}