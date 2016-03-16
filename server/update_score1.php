<?php
 
/*
 * update score1 for user based on their email.
 */
 
// array for JSON response
$response = array();
$mysqli = new mysqli("localhost", "parkinapp", "parkinapppassword", "parkinapp"); 
// check for required fields
if (isset($_GET['email']) && isset($_GET['score1']) && isset($_GET['average_game1'])) {
    $email = $_GET['email'];
    $score1 = $_GET['score1'];
    $average_game1 = $_GET['average_game1'];
 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // mysql update row with matched pid
    $result = $mysqli->query("UPDATE userlogin 
    SET score1 = GREATEST(score1, '$score1'), average_game1 = '$average_game1', play_time1 = play_time1 + 1 
    WHERE email = '$email'");
 
    // check if row inserted or not
    if ($result) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "Score for Game 1 successfully updated.";
 
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
