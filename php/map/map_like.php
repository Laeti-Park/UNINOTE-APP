<?php

header("Content-type:application/json");

require_once 'example_con.php';

$userID = $_POST['userID'];
$mapID = $_POST['mapID'];

$sql = "SELECT * FROM `maplikey` WHERE `userID` = '$userID' AND `mapID` = '$mapID'";
$result = mysqli_query($con, $sql);

if (mysqli_num_rows($result) <= 0) {
    $error = "ok";
    $sqlLike = "INSERT INTO `maplikey` (`userID`, `mapID`) VALUES ('$userID', '$mapID');
    UPDATE `counts` SET `recommend`=`recommend`+1 WHERE `userID`='$mapID'";
    if (mysqli_multi_query($con, $sqlLike)) {
        do {
            // store first result set
            if ($result = mysqli_store_result($con)) {
                mysqli_free_result($result);
            }
        } while (mysqli_more_results($con) && mysqli_next_result($con));
    }
    echo json_encode(array("error" => "$error"));
} else {
    $error = "failed";
    echo json_encode(array("error" => "$error", "userID" => $row["userID"], "mapID" => $row["mapID"]));
}
