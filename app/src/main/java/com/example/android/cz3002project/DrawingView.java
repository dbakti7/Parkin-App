package com.example.android.cz3002project;

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



/**
 * TODO: document your custom view class.
 */
public class DrawingView extends View {

    // 0 <= score <= 100
    public int score = 0;

    private Path shapePath;
    private Paint shapePaint;
    private float shapeLineWidth;
    private int shapeColor = 0xFF000000;
    private Path innerCircle;
    private Path outerCircle;



    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xFF660000;

    // difference path
    private Path outerDifferencePath;
    private Path innerDifferencePath;

    // difference paint
    private Paint differencePaint;
    // difference color
    private int differenceColor = 0xFFFF0000;


    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;

    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    String email;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private static String url_update_score2 = "http://10.27.44.239/update_score2.php";
    private static String url_read_user = "http://10.27.44.239/read_user.php";

    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);

        setupShapePath();
        setupDrawingPath();
        setupDifferencePaths();

        randomPoints = new ArrayList<Point>();

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

        innerCircle = new Path();
        outerCircle = new Path();
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

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);

        float centerX = w / 2;
        float centerY = h / 2;
        float radius = w / 4;


        shapePath = circlePath(centerX, centerY, radius, shapeLineWidth, innerCircle, outerCircle);

        drawCanvas.drawPath(shapePath, shapePaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //draw view
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);

        //canvas.drawPath(outerDifferencePath, differencePaint);
        //canvas.drawPath(innerDifferencePath, differencePaint);

//        for (Point point : randomPoints) {
//            Rect tinyRect = new Rect(point.x, point.y, point.x + 1, point.y + 1);
//            Paint paint = new Paint();
//            paint.setColor(0xFF000000);
//            paint.setAntiAlias(true);
//            paint.setStrokeWidth(1);
//            paint.setStyle(Paint.Style.STROKE);
//            paint.setStrokeJoin(Paint.Join.ROUND);
//            paint.setStrokeCap(Paint.Cap.ROUND);
//
//            canvas.drawRect(tinyRect, paint);
//        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //detect user touch
        float touchX = event.getX();
        float touchY = event.getY();

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
//                drawCanvas.drawPath(drawPath, drawPaint);

//                drawPath.close();

                outerDifferencePath.op(drawPath, outerCircle, Path.Op.DIFFERENCE);
                innerDifferencePath.op(innerCircle, drawPath, Path.Op.DIFFERENCE);

                RectF clipRectF = new RectF();
                outerDifferencePath.computeBounds(clipRectF, true);
                Rect clipRect = new Rect();
                clipRectF.roundOut(clipRect);
                Region clip = new Region(clipRect);

                RectF clipRectF2 = new RectF();
                innerDifferencePath.computeBounds(clipRectF2, true);
                Rect clipRect2 = new Rect();
                clipRectF2.roundOut(clipRect2);
                Region clip2 = new Region(clipRect2);

                Region outerRegion = new Region();
                outerRegion.setPath(outerDifferencePath, clip);
                Rect outerRegionBounds = new Rect();
                outerRegion.getBounds(outerRegionBounds);
                Log.d("REGION", "outer region bounds bottom:" + outerRegionBounds.bottom + " left:" + outerRegionBounds.left + " right: " + outerRegionBounds.right + " top: " + outerRegionBounds.top);

                Region innerRegion = new Region();
                innerRegion.setPath(innerDifferencePath, clip2);
                Rect innerRegionBounds = new Rect();
                innerRegion.getBounds(innerRegionBounds);
                Log.d("REGION", "inner region bounds bottom:" + innerRegionBounds.bottom + " left:" + innerRegionBounds.left + " right: " + innerRegionBounds.right + " top: " + innerRegionBounds.top);


                float outerDifferenceArea = monteCarloArea(outerRegion);
                Log.d("MONTECARLOAREA", "outerArea=" + outerDifferenceArea);

                float innerDifferenceArea = monteCarloArea(innerRegion);
                Log.d("MONTECARLOAREA", "innerArea=" + innerDifferenceArea);

//                drawPath.reset();

                int uncorrectedScore = (int) (100 - Math.ceil(outerDifferenceArea) - Math.ceil(innerDifferenceArea));
                score = (uncorrectedScore >= 0) ? uncorrectedScore : 0;
                Log.d("SCORE", "score=" + score);
                if (CheckNetworkConnection.checknetwork(this.getContext()))
                    new UpdateScore2().execute();
                else
                    Toast.makeText(this.getContext(), "No Internet Connection to upload your score!", Toast.LENGTH_LONG).show();
                com.example.android.cz3002project.DrawingGameActivity.scoreTextView.setText("Score: "+score);
                break;
            default:
                return false;
        }
        Log.d("TOUCH", "touch x:" + touchX + "y:" + touchY);

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

    ArrayList<Point> randomPoints;

    private float monteCarloArea(Region region) {

        int viewWidth = getWidth();
        int viewHeight = getHeight();

        float testPointsCount = 1000;
        float pointsInsideRegionCount = 0;

        Random random = new Random();

        for (int i=0; i<testPointsCount; i++) {
            int randX = random.nextInt(viewWidth);

            int randY = random.nextInt(viewHeight);
            randomPoints.add(new Point(randX, randY));

            if (region.contains(randX, randY)) {
                pointsInsideRegionCount++;
            }
        }


        float area = (pointsInsideRegionCount / testPointsCount) * 1000;



        return area;
    }


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
         * Creating product
         * */
        protected String doInBackground(String... args) {

            // Retrieving Data
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
                    Log.e("Average game2", average_game2.toString());
                    play_time2 = Double.parseDouble(jo.getString("play_time2"));
                    total = average_game2 * play_time2 + score;
                    Log.e("Total", total.toString());
                    average_game2 = total / (play_time2 + 1);

                    // closing this screen
                } else {
                    // failed to create product
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Sending Data
            params.add(new BasicNameValuePair("score2", ((Integer)score).toString()));
            params.add(new BasicNameValuePair("average_game2", (average_game2.toString())));
            // getting JSON Object
            // Note that create product url accepts POST method
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
//                    finish();
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
