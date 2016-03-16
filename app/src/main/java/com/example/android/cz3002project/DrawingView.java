package com.example.android.cz3002project;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import android.graphics.Bitmap;
import android.graphics.Path;
import android.view.MotionEvent;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.*;

/**
 * This class is used to handle the drawing on Shape Tracing Game
 */
public class DrawingView extends View {

    // 0 <= score <= 100
    public int score;

    //shape path
    private Path shapePath;
    private Paint shapePaint;
    private float shapeLineWidth;
    private int shapeColor = 0xFF000000;

    private Path innerShape;
    private Path outerShape;

    //drawing path
    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private int paintColor = 0xFF660000;

    // difference path
    private Path outerDifferencePath;
    private Path innerDifferencePath;

    // difference paint
    private Paint differencePaint;
    // difference color
    private int differenceColor = 0xFFFF0000;


    ArrayList<Point> randomPoints;
    private boolean lastShape;

    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    String email;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    // url to access the database with PHP
    private static String url_update_score2 = "http://10.27.44.239/update_score2.php";
    private static String url_read_user = "http://10.27.44.239/read_user.php";

    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);

        setupShapePath();
        setupDrawingPath();
        setupDifferencePaths();

        randomPoints = new ArrayList<Point>();
        score = 0;
        lastShape = false;

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        email = preferences.getString("Email", "");

    }

    private void setupShapePath() {
        shapePath = new Path();

        shapePaint = new Paint();
        shapePaint.setColor(shapeColor);
        shapePaint.setAntiAlias(true);
        shapePaint.setStyle(Paint.Style.FILL);

        shapeLineWidth = 100;

        innerShape = new Path();
        outerShape = new Path();
    }

    private void setupDrawingPath(){
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    private void setupDifferencePaths() {
        outerDifferencePath = new Path();
        innerDifferencePath = new Path();

        differencePaint = new Paint();
        differencePaint.setColor(differenceColor);
        differencePaint.setAntiAlias(true);
        differencePaint.setStyle(Paint.Style.FILL);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //view given size
        super.onSizeChanged(w, h, oldw, oldh);

//        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//        drawCanvas = new Canvas(canvasBitmap);

        float centerX = w / 2;
        float centerY = h / 2;
        float radius = w / 4;

        shapePath = circlePath(centerX, centerY, radius, shapeLineWidth, innerShape, outerShape);

//        drawCanvas.drawPath(shapePath, shapePaint);

        setupRandomPoints(10000, w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawPath(shapePath, shapePaint);

        canvas.drawPath(drawPath, drawPaint);

//        canvas.drawPath(outerDifferencePath, differencePaint);
//
//        Paint innerDifferencePaint = new Paint();
//        innerDifferencePaint.setColor(0xFF00FF00);
//        innerDifferencePaint.setAntiAlias(true);
//        innerDifferencePaint.setStyle(Paint.Style.FILL);
//        canvas.drawPath(innerDifferencePath, innerDifferencePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //detect user touch
        float touchX = event.getX();
        float touchY = event.getY();

        Log.d("TOUCH", "touch x:" + touchX + "y:" + touchY);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.reset();
                outerDifferencePath.reset();
                innerDifferencePath.reset();
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:

//                drawPath.close();


                outerDifferencePath.op(drawPath, outerShape, Path.Op.DIFFERENCE);
                innerDifferencePath.op(innerShape, drawPath, Path.Op.DIFFERENCE);
                double outerDifferenceArea = monteCarloArea(outerDifferencePath);
                Log.d("MONTECARLOAREA", "outerDifferenceArea=" + outerDifferenceArea);
                double innerDifferenceArea = monteCarloArea(innerDifferencePath);
                Log.d("MONTECARLOAREA", "innerDifferenceArea=" + innerDifferenceArea);
                double innerShapeArea = monteCarloArea(innerShape);
                Log.d("MONTECARLOAREA", "innerShapeArea=" + innerShapeArea);

                double differenceToInnerShapeRatio = (outerDifferenceArea + innerDifferenceArea) / innerShapeArea;
                Log.d("MONTECARLOAREA", "differenceToInnerShapeRatio=" + differenceToInnerShapeRatio);
                double normalizedRatio = min(differenceToInnerShapeRatio, 1.0);
                Log.d("MONTECARLOAREA", "normalizedRatio=" + normalizedRatio);

                int subscore = (int)((1.0 - normalizedRatio) * 100.0);

                Log.d("SCORE", "subscore=" + score);

                if (lastShape) {
                    score = (score + subscore) / 2;
                    new UpdateScore2().execute();
                    ((Activity)getContext()).finish();

                } else {
                    score = subscore;

                    shapePath.reset();
                    innerShape.reset();
                    outerShape.reset();

                    drawPath.reset();

                    int viewWidth = getWidth();
                    int viewHeight = getHeight();
                    float centerX = viewWidth / 2;
                    float centerY = viewHeight / 2;
                    float triangleOuterSide = (float)(viewWidth / 1.5);

                    shapePath = triangleFramePath(centerX, centerY, triangleOuterSide, shapeLineWidth, innerShape, outerShape);
                    lastShape = true;
                }

                com.example.android.cz3002project.DrawingGameActivity.scoreTextView.setText("Score: "+score);

                break;
            default:
                return false;
        }


        invalidate();
        return true;
    }

    private Path circlePath(float centerX, float centerY, float radius, float lineWidth, Path innerCircle, Path outerCircle) {
        Path path = new Path();
        float halfLineWidth = lineWidth / 2;

        outerCircle.addCircle(centerX, centerY, radius + halfLineWidth, Path.Direction.CW);

        innerCircle.addCircle(centerX, centerY, radius - halfLineWidth, Path.Direction.CW);

        path.op(outerCircle, innerCircle, Path.Op.DIFFERENCE);

        return path;
    }

    private Path triangleFramePath(float centerX, float centerY, float outerSide, float lineWidth, Path innerTriangle, Path outerTriangle) {
        Path path = new Path();
        float innerSide = outerSide - (2 * lineWidth * (float)sqrt(3));

        trianglePath(innerTriangle, centerX, centerY, innerSide);

        trianglePath(outerTriangle, centerX, centerY, outerSide);

        path.op(outerTriangle, innerTriangle, Path.Op.DIFFERENCE);

        return path;
    }

    private Path trianglePath(Path path, float centerX, float centerY, float side) {
        path.moveTo(centerX, centerY - (side / (float) sqrt(3)));
        path.lineTo(centerX + (side / 2), centerY + (side / ((float) sqrt(3) * 2)));
        path.lineTo(centerX - (side/2), centerY + (side / ((float)sqrt(3) * 2)));
        path.lineTo(centerX, centerY - (side / (float) sqrt(3)));
        path.close();

        return path;
    }

    private double monteCarloArea(Path path) {

        RectF clipRectF = new RectF();
        path.computeBounds(clipRectF, true);
        Rect clipRect = new Rect();
        clipRectF.round(clipRect);
        Region clip = new Region(clipRect);

        Region region = new Region();
        region.setPath(path, clip);
        Rect regionBounds = new Rect();
        region.getBounds(regionBounds);


        double pointsInsideRegionCount = 0;

        for (Point point : randomPoints) {
            if (region.contains(point.x, point.y)) {
                pointsInsideRegionCount++;
            }
        }

        double area = (pointsInsideRegionCount / randomPoints.size()) * 1000.0;

        return area;
    }

    private void setupRandomPoints(int count, int viewWidth, int viewHeight) {
        Random random = new Random();

        for (int i=0; i<count; i++) {
            int randX = random.nextInt(viewWidth);
            int randY = random.nextInt(viewHeight);
            randomPoints.add(new Point(randX, randY));
        }
    }

    /**
     * Async class to handle database
     */
    class UpdateScore2 extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Updating Score for Game 2...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Updating the score for Shape Tracing game
         * */
        protected String doInBackground(String... args) {

            // Retrieving the previous data for this game

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));

            Double average_game2 = 0.0, play_time2 = 0.0, total = 0.0;
            JSONObject prevData = jsonParser.makeHttpRequest(url_read_user, "GET", params);

            try {
                int success = prevData.getInt("success");

                if (success == 1) {
                    JSONArray user = prevData.getJSONArray("user");
                    JSONObject jo = user.getJSONObject(0);

                    average_game2 = Double.parseDouble(jo.getString("average_game2"));
                    play_time2 = Double.parseDouble(jo.getString("play_time2"));
                    total = average_game2 * play_time2 + score;
                    average_game2 = total / (play_time2 + 1); // update average score for this game
                } else {
                    // connection failed
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Sending Data to database
            params.add(new BasicNameValuePair("score2", ((Integer)score).toString()));
            params.add(new BasicNameValuePair("average_game2", (average_game2.toString())));

            // getting JSON Object to check the status
            JSONObject json = jsonParser.makeHttpRequest(url_update_score2,
                    "GET", params);

            // check log cat fro response
            //Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt("success");

                if (success == 1) {
                    // successfully created product
                    //Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
                    //startActivity(i);
                    Log.e("UPDATE SCORE2 PROCESS", "SUCCESS");
                    // closing this screen
                    // finish();
                } else {
                    // failed to create product
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }

}
