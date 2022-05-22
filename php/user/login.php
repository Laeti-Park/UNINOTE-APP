<?php

header("Content-type:application/json");

require_once 'example_con.php';

$type = $_POST['type'];
$userID = $_POST['ID'];
    

$sql = "SELECT *  FROM `user` WHERE `userID` LIKE '$userID';";

$result = mysqli_query($con, $sql);
$row = mysqli_fetch_array($result, MYSQLI_ASSOC);

if($type == 1){
    $error = "ok";
    $sqlIsLogin = "UPDATE `user` SET `isLogin` = '1' WHERE `user`.`userID` = '$userID'";
    $resultLogin = mysqli_query($con, $sqlIsLogin);
    echo json_encode(array("error" => $error , "userID" => $row["userID"], "userPassword" => $row["userPassword"], "userName" => $row["userName"]));
}else{
    if($row["isLogin"] == 1){
        $error = "error";
        echo json_encode(array("error" => $error));
    } else {
        $error = "ok";
        $sqlIsLogin = "UPDATE `user` SET `isLogin` = '1' WHERE `user`.`userID` = '$userID'";
        $resultLogin = mysqli_query($con, $sqlIsLogin);
        echo json_encode(array("error" => $error , "userID" => $row["userID"], "userPassword" => $row["userPassword"], "userName" => $row["userName"]));
    } 
}

mysqli_close($con);
