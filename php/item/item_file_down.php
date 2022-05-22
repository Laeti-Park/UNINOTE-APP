<?php
header("Content-Type: text/html; charset=UTF-8");
require_once 'example_con.php';

$userID = $_POST['userID'];
$itemID = $_POST['itemID'];
$file = $_POST["fileName"];

$filePath = $_SERVER['DOCUMENT_ROOT']."/$file";
$fileSize = filesize($filePath);
$pathParts = pathinfo($filePath);
$fileName = $pathParts['basename'];
$extension = $pathParts['extension'];

if (file_exists($filePath)) {
    header("Pragma: public");
    header("Expires: 0");
    header("Content-Type: application/octet-stream");
    header("Content-Disposition: attachment; fileName='$fileName'");
    header("Content-Transfer-Encoding: binary");
    header("Content-Length: $fileSize");
    header("Pragma:no-cache");
    header("Expires:0");

    //    rename($filePath, "../uninote/Bye.jpg");

    echo json_encode(array("part" => $pathParts));

    ob_clean();
    flush();
    readfile($filePath);
}
