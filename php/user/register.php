<?php
header("Content-type:application/json");

require_once 'example_con.php';

$createID = $_POST['createID'];
$createPassword = $_POST['createPassword'];
$createName = $_POST['createName'];
$createEmail = $_POST['createEmail'];

$sqlSelect = "SELECT * FROM `user` WHERE `userID` LIKE '$createID'";
$resultSelect = mysqli_query($con, $sqlSelect);
$row = mysqli_fetch_array($resultSelect);

if ($row["userID"] == $createID) {
    $error = "failed";
    echo json_encode(array("error" => $error));
} else {
    $error = "ok";
    $sqlInsert = "INSERT INTO `user` (`userID`, `userPassword`, `userName`, `userEmail`, `isLogin`) VALUES ('$createID', '$createPassword', '$createName', '$createEmail', 0);
    INSERT INTO `counts` (`userID`, `hits`, `recommend`) VALUES ('$createID', 0, 0)";

    if (mysqli_multi_query($con, $sqlInsert)) {
        do {
            // store first result set
            if ($result = mysqli_store_result($con)) {
                mysqli_free_result($result);
            }
        } while (mysqli_more_results($con) && mysqli_next_result($con));
    }

    echo json_encode(array("error" => "$error"));
}
mysqli_close($con);
