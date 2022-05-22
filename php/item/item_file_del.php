<?php

header("Content-type:application/json");

require_once 'example_con.php';

$userID = $_POST['userID'];
$itemID = $_POST['itemID'];
$fileRealName = $_POST["fileRealName"];

$dir = $_SERVER['DOCUMENT_ROOT']."//upload/" . urlencode(preg_replace("/[\"\']/i", "", $userID)) . "//" . urlencode(preg_replace("/[\"\']/i", "", $itemID));
$filePath = "./".$fileRealName;

if (unlink("/$dir/$filePath")) {
  $error = "success";
  $sql = "DELETE FROM `file` WHERE `userID`='$userID' AND `itemID`='$itemID' AND `fileRealName`='$fileRealName'";
  $result = mysqli_query($con, $sql);
  echo json_encode(array("error" => "$error"));
} else {
  $error = "error";
  echo json_encode(array("error" => "$error"));
}
