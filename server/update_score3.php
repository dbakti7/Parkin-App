<?php
 
/*
 * update score3 for user based on their email.
 */
 
// array for JSON response
$response = array();
$mysqli = new mysqli("localhost", "parkinapp", "parkinapppassword", "parkinapp"); 
// check for required fields
if (isset($_GET['email']) && isset($_GET['score3']) && isset($_GET['average_game3'])) {
    $email = $_GET['email'];
    $score3 = $_GET['score3'];
    $average_game3 = $_GET['average_game3'];
 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // mysql update row with matched pid
    $result = $mysqli->query("UPDATE userlogin 
    SET score3 = GREATEST(score3, '$score3'), average_game3 = '$average_game3', play_time3 = play_time3 + 1 
    WHERE email = '$email'");
 
    // check if row inserted or not
    if ($result) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "Score for Game 3 successfully updated.";
 
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
