<?php

header("Content-type:application/json");

require_once 'example_con.php';

$userID = $_POST['userID'];

$sql = "SELECT * FROM `counts` WHERE `userID` = '$userID'";
$result = mysqli_query($con, $sql);
$row = mysqli_fetch_array($result);

if($row["userID"] == $userID) {

    $error = "ok";
    $sqlUpdate = "UPDATE `counts` SET `hits`=`hits`+1 WHERE `userID`='$userID'";
    $resultUpdate = mysqli_query($con, $sqlUpdate);

    echo json_encode(array("error" => "$error", "userID" => $row["userID"], "hits" => $row["hits"], "recommend" => $row["recommend"]));
} else {
    
    $error = "failed";
    $sqlInsert = "INSERT INTO `counts` (`userID`, `hits`, `recommend`) VALUES ('$userID', 0, 0)";
    $resultInsert = mysqli_query($con, $sqlInsert);

    echo json_encode(array("error" => "$error", "userID" => $row["userID"], "hits" => $row["hits"], "recommend" => $row["recommend"]));
}
mysqli_close($con);