<?php
 
/*
 * update reminder_period for user based on their email.
 */
 
// array for JSON response
$response = array();
$mysqli = new mysqli("localhost", "parkinapp", "parkinapppassword", "parkinapp"); 
// check for required fields
if (isset($_GET['email']) && isset($_GET['reminder_period'])) {
    $email = $_GET['email'];
    $reminder_period = $_GET['reminder_period'];
 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // mysql update row with matched pid
    $result = $mysqli->query("UPDATE userlogin SET reminder_period = '$reminder_period' WHERE email = '$email'");
 
    // check if row inserted or not
    if ($result) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "Reminder Period successfully updated.";
 
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
