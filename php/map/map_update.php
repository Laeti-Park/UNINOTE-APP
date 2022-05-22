<?php

header("Content-type:application/json");

require_once 'example_con.php';

$userID = 'qqqq';
$publicMap = 0;
$password = '131';

$publicUpdate = "UPDATE `publicmap` SET `publicmap`=$publicMap, `password`='$password' WHERE `userID`='$userID'";
$result = mysqli_query($con, $publicUpdate);
