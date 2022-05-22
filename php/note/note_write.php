<?php
header("Content-type:application/json");
require_once 'example_con.php';

//type = 0:공지 1:자유게시판 2:공부게시판
$type = (int)$_POST['type'];

$noticeTitle = $_POST['noticeTitle'];
$userID = $_POST['userID'];
$date = $_POST['date'];
$noticeContents = $_POST['noticeContents'];
$avail = 1;


if($type == 0){
    $sqlkeyNotice = "SELECT * FROM `notice`";
    $resultkeyNotice = mysqli_query($con, $sqlkeyNotice);

    $sqlNumNotice = "SELECT MAX(`noticeID`) AS numNotice FROM `notice`";
    $renumKeyNotice = mysqli_query($con, $sqlNumNotice);
    $rowNumNoticeArray = mysqli_fetch_array($renumKeyNotice, MYSQLI_ASSOC);
    $rowNumNotice = $rowNumNoticeArray['numNotice'];
    
    $numNotice = (int)$rowNumNotice+1;
    $sqlInsertNotice = "INSERT INTO `notice` (`noticeID`, `noticeTitle`, `userID`, `noticeDate`, `noticeContent`, `noticeAvailable`) VALUES ('$numNotice', '$noticeTitle', '$userID', '$date', '$noticeContents', '$avail')";
    $resultNotice = mysqli_query($con, $sqlInsertNotice);
    echo json_encode(array("type" => $type, "key" => $numNotice));
}
else if($type == 1){
    $sqlkey = "SELECT * FROM `bbs`";
    $resultkey = mysqli_query($con, $sqlkey);

    $sqlNumbbs = "SELECT MAX(`bbsID`) AS numbbs FROM `bbs`";
    $renumKeybbs = mysqli_query($con, $sqlNumbbs);
    $rowNumBbsArray = mysqli_fetch_array($renumKeybbs, MYSQLI_ASSOC);
    $rowNum = $rowNumBbsArray['numbbs'];

    $num = (int)$rowNum+1;
    $sqlInsert = "INSERT INTO `bbs` (`bbsID`, `bbsTitle`, `userID`, `bbsDate`, `bbsContent`, `bbsAvailable`) VALUES ('$num', '$noticeTitle', '$userID', '$date', '$noticeContents', '$avail')";
    $result = mysqli_query($con, $sqlInsert);
    echo json_encode(array("type" => $type, "key" => $num));
}
else if($type == 2){
    $sqlkeyStudyboard = "SELECT * FROM `studyboard`";
    $resultkeyStudyboard = mysqli_query($con, $sqlkeyStudyboard);

    $sqlNumStudy = "SELECT MAX(`studyboardID`) AS numStudy FROM `studyboard`";
    $renumKeyStudy = mysqli_query($con, $sqlNumStudy);
    $rowNumStudyArray = mysqli_fetch_array($renumKeyStudy, MYSQLI_ASSOC);
    $rowNumStudyboard = $rowNumStudyArray['numStudy'];

    $numStudyboard = (int)$rowNumStudyboard+1;
    $sqlInsertStudyboard = "INSERT INTO `studyboard` (`studyboardID`, `studyboardTitle`, `userID`, `studyboardDate`, `studyboardContent`, `studyboardAvailable`) VALUES ('$numStudyboard', '$noticeTitle', '$userID', '$date', '$noticeContents', '$avail')";
    $resultStudyboard = mysqli_query($con, $sqlInsertStudyboard);
    echo json_encode(array("type" => $type, "key" => $numStudyboard));
}
else{
    echo json_encode(array("error" => "saveFailed"));
}
mysqli_close($con);