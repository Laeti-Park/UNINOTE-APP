<?php
header("Content-type:application/json");
header("content-type: application/x-www-form-urlencoded; charset=utf-8");

require_once 'example_con.php';

$itemID = $_POST['itemID'];
$targetItemID = $_POST['targetItemID'];
$parentItemID = $_POST['parentItemID'];
$itemTop = $_POST['itemTop'];
$itemLeft = $_POST['itemLeft'];
$userID = $_POST['userID'];
$itemContent = $_POST['itemContent'];
$itemCount = $_POST['itemCount'];
$itemWidth = $_POST['itemWidth'];
$itemHeight = $_POST['itemHeight'];
$noteContent = $_POST['noteContent'];
$mode = $_POST['mode'];

function rmdir_all($delete_path)
{
  $dirs = dir($delete_path);
  while (false !== ($entry = $dirs->read())) {
    if (($entry != '.') && ($entry != '..')) {
      if (is_dir($delete_path . '/' . $entry)) {
        rmdir_all($delete_path . '/' . $entry);
      } else {
        @unlink($delete_path . '/' . $entry);
      }
    }
  }
  $dirs->close();
  @rmdir($delete_path);
}

if ($mode == 'insert') {
  $error = "insert";
  $itemSave = "INSERT INTO `item`(`itemID`, `itemTop`, `itemLeft`, `userID`, `itemContent`, `itemCount`, `itemWidth`, `itemHeight`) VALUE ('$itemID', '$itemTop', '$itemLeft', '$userID', '$itemContent', '$itemCount', '$itemWidth', '$itemHeight') ;
    INSERT INTO `note`(`userID`, `itemID`, `noteContent`) VALUES ('$userID','$itemID','$noteContent')";
} else if ($mode == 'update') {
  $error = "update";
  $updateLine = $parentItemID."To".$itemID;
  $itemSave = "UPDATE `item` SET `itemID`='$itemID',`itemContent`='$itemContent',`itemWidth`='$itemWidth',`itemHeight`='$itemHeight' WHERE `userID`='$userID' AND `itemCount`='$itemCount';
    UPDATE `note` SET `itemID`='$itemID', `noteContent`='$noteContent' WHERE `userID`='$userID' AND SUBSTRING_INDEX(SUBSTR(`itemID`,1,LENGTH(`itemID`)-1),'_',-1)=SUBSTRING_INDEX(SUBSTR('$itemID',1,LENGTH('$itemID')-1),'_',-1);
    UPDATE `file` SET `itemID`='$itemID' WHERE `userID`='$userID' AND SUBSTRING_INDEX(SUBSTR(`itemID`,1,LENGTH(`itemID`)-1),'_',-1)=SUBSTRING_INDEX(SUBSTR('$itemID',1,LENGTH('$itemID')-1),'_',-1);
    UPDATE `line` SET `lineID`='$updateLine' WHERE `userID`='$userID' AND SUBSTRING_INDEX(SUBSTR(`lineID`,1,LENGTH(`lineID`)-1),'To',-1)=SUBSTRING_INDEX(SUBSTR('$targetItemID',1,LENGTH('$targetItemID')-1),'To',-1)";

  if ($itemID != $targetItemID && is_dir($_SERVER['DOCUMENT_ROOT'] . "//upload/" . urlencode(preg_replace("/[\"\']/i", "", $userID)) . "//" . urlencode(preg_replace("/[\"\']/i", "", $targetItemID)))) {
    if (is_dir($_SERVER['DOCUMENT_ROOT'] . "//upload/" . urlencode(preg_replace("/[\"\']/i", "", $userID)) . "//" . urlencode(preg_replace("/[\"\']/i", "", $targetItemID)))) {
      rename(
        $_SERVER['DOCUMENT_ROOT'] . "//upload/" . urlencode(preg_replace("/[\"\']/i", "", $userID)) . "//" . urlencode(preg_replace("/[\"\']/i", "", $targetItemID)),
        $_SERVER['DOCUMENT_ROOT'] . "//upload/" . urlencode(preg_replace("/[\"\']/i", "", $userID)) . "//" . urlencode(preg_replace("/[\"\']/i", "", $itemID))
      );
    }
  }
} else if ($mode == 'delete') {
  $error = "delete";
  $itemSave = "DELETE FROM `item` WHERE `userID`='$userID' AND `itemCount`=$itemCount AND `itemID`='$itemID';
    DELETE FROM `note` WHERE `userID`='$userID' AND `itemID`='$itemID';
    DELETE FROM `file` WHERE `userID`='$userID' AND `itemID`='$itemID';
    DELETE FROM `line` WHERE `userID`='$userID' AND `lineID` LIKE '%$itemID'";

  if (is_dir($_SERVER['DOCUMENT_ROOT'] . "//upload/" . urlencode(preg_replace("/[\"\']/i", "", $userID)) . "//" . urlencode(preg_replace("/[\"\']/i", "", $itemID)))) {
    rmdir_all($_SERVER['DOCUMENT_ROOT'] . "//upload/" . urlencode(preg_replace("/[\"\']/i", "", $userID)) . "//" . urlencode(preg_replace("/[\"\']/i", "", $itemID)));
  }
} else {
  $error = "error";
}

if (mysqli_multi_query($con, $itemSave)) {
  do {
    // store first result set
    if ($result = mysqli_store_result($con)) {
      mysqli_free_result($result);
    }
  } while (mysqli_more_results($con) && mysqli_next_result($con));
}

echo json_encode(array("error" => "$error"));
