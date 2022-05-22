<?php

header("Content-type:application/json");

require_once 'example_con.php';

$userID = $_POST['userID'];
$itemID = $_POST['itemID'];
$data = $_POST["media"];

$dir = $_SERVER['DOCUMENT_ROOT']."//upload/" . urlencode(preg_replace("/[\"\']/i", "", $userID)) . "//" . urlencode(preg_replace("/[\"\']/i", "", $itemID));
if (!is_dir($dir)) {
  mkdir($dir, 0777, true);
}
$filePath = "./";
$oldFile = basename($_FILES['media']['name']);

if ($oldFile) {
  $fileExt = substr(strrchr($oldFile, "."), 1);
  $oldName = substr($oldFile, 0, strlen($oldFile) - strlen($fileExt) - 1);

  $fileCnt = 0;
  $ret = "$oldName.$fileExt";
  while (file_exists("/".$dir ."/". $filePath . $ret)) {
    $fileCnt++;
    $ret = $oldName ."". $fileCnt .".". $fileExt;
  }
  $baseName = $ret;
}
$filePath = $filePath . $baseName;

if (move_uploaded_file($_FILES['media']['tmp_name'], "/$dir/$filePath")) {
  $error = "success";

  $sqlInsert = "INSERT INTO `file`(`fileName`, `fileRealName`, `userID`, `itemID`) VALUES ('$oldFile','$baseName',$userID, $itemID)";
  $resultInsert = mysqli_query($con, $sqlInsert);
  echo $error;
} else {
  $error = "error";
  echo $error;
}