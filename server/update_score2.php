<?php
 
/*
 * update score2 for user based on their email.
 */
 
// array for JSON response
$response = array();
$mysqli = new mysqli("localhost", "parkinapp", "parkinapppassword", "parkinapp"); 
// check for required fields
if (isset($_GET['email']) && isset($_GET['score2']) && isset($_GET['average_game2'])) {
    $email = $_GET['email'];
    $score2 = $_GET['score2'];
    $average_game2 = $_GET['average_game2'];
 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // mysql update row with matched pid
    $result = $mysqli->query("UPDATE userlogin 
    SET score2 = GREATEST(score2, '$score2'), average_game2 = '$average_game2', play_time2 = play_time2 + 1 
    WHERE email = '$email'");
 
    // check if row inserted or not
    if ($result) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "Score for Game 2 successfully updated.";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
 
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>
