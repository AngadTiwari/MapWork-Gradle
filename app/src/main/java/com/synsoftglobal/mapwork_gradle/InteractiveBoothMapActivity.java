package com.synsoftglobal.mapwork_gradle;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("ClickableViewAccessibility")
public class InteractiveBoothMapActivity extends Activity implements
        OnTouchListener {

    private static final String TAG = "Gesture";
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // The 3 states (events) which the user is trying to perform
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    float prevX=0.0f,prevY=0.0f;

    // these PointF objects are used to record the point(s) the user is touching
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    int mapXCords = 106;
    int mapYCords = 85;
    String boothName = "IBM38";

    RelativeLayout relativeLayout;
    AbsoluteLayout main,topView;
    ImageView mapImageview;
    ImageView dotImageview[];
    JSONArray map;

    GestureDetector gestureDetector;
    OnTouchListener gestureListener;

    private ImageButton zoomin,zoomout;

    private static final float MIN_ZOOM = 0.5f;
    private static final float MAX_ZOOM = 3.0f;

    private static final float MIN_TRANSLATE = -350.0f;
    private static final float MAX_TRANSLATE = 200.0f;

    private float scalePlus= 1.0f;
    private float scaleMinus= 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.canvas_image);

        topView=(AbsoluteLayout)findViewById(R.id.topView);
        main=(AbsoluteLayout)findViewById(R.id.main);

        zoomin=(ImageButton)findViewById(R.id.zoomin);
        zoomout=(ImageButton)findViewById(R.id.zoomout);

        String mapJsonString="[{\"FIELD1\":\"IBM38\",\"FIELD2\":106,\"FIELD3\":85},{\"FIELD1\":\"IBM23\",\"FIELD2\":133,\"FIELD3\":79},{\"FIELD1\":\"IBM22\",\"FIELD2\":162,\"FIELD3\":78},{\"FIELD1\":\"IBM21\",\"FIELD2\":188,\"FIELD3\":85},{\"FIELD1\":\"IBM39\",\"FIELD2\":115,\"FIELD3\":120},{\"FIELD1\":\"IBM24\",\"FIELD2\":147,\"FIELD3\":115},{\"FIELD1\":\"IBM25\",\"FIELD2\":177,\"FIELD3\":121},{\"FIELD1\":\"IBM37\",\"FIELD2\":48,\"FIELD3\":158},{\"FIELD1\":\"IBM36\",\"FIELD2\":55,\"FIELD3\":187},{\"FIELD1\":\"IBM35\",\"FIELD2\":67,\"FIELD3\":211},{\"FIELD1\":\"IBM34\",\"FIELD2\":86,\"FIELD3\":231},{\"FIELD1\":\"IBM30\",\"FIELD2\":101,\"FIELD3\":145},{\"FIELD1\":\"IBM31\",\"FIELD2\":104,\"FIELD3\":165},{\"FIELD1\":\"IBM32\",\"FIELD2\":112,\"FIELD3\":183},{\"FIELD1\":\"IBM33\",\"FIELD2\":124,\"FIELD3\":199},{\"FIELD1\":\"IBM26\",\"FIELD2\":190,\"FIELD3\":145},{\"FIELD1\":\"IBM27\",\"FIELD2\":190,\"FIELD3\":167},{\"FIELD1\":\"IBM28\",\"FIELD2\":181,\"FIELD3\":182},{\"FIELD1\":\"IBM29\",\"FIELD2\":171,\"FIELD3\":199},{\"FIELD1\":\"IBM20\",\"FIELD2\":245,\"FIELD3\":158},{\"FIELD1\":\"IBM19\",\"FIELD2\":239,\"FIELD3\":187},{\"FIELD1\":\"IBM18\",\"FIELD2\":225,\"FIELD3\":211},{\"FIELD1\":\"IBM17\",\"FIELD2\":205,\"FIELD3\":232},{\"FIELD1\":\"632\",\"FIELD2\":319,\"FIELD3\":98},{\"FIELD1\":\"633\",\"FIELD2\":306,\"FIELD3\":123},{\"FIELD1\":\"634\",\"FIELD2\":300,\"FIELD3\":151},{\"FIELD1\":\"635\",\"FIELD2\":303,\"FIELD3\":179},{\"FIELD1\":\"636\",\"FIELD2\":312,\"FIELD3\":208},{\"FIELD1\":\"628\",\"FIELD2\":347,\"FIELD3\":170},{\"FIELD1\":\"627\",\"FIELD2\":361,\"FIELD3\":198},{\"FIELD1\":\"541\",\"FIELD2\":410,\"FIELD3\":181},{\"FIELD1\":\"540\",\"FIELD2\":434,\"FIELD3\":182},{\"FIELD1\":\"539\",\"FIELD2\":421,\"FIELD3\":215},{\"FIELD1\":\"535\",\"FIELD2\":488,\"FIELD3\":174},{\"FIELD1\":\"536\",\"FIELD2\":475,\"FIELD3\":196},{\"FIELD1\":\"529\",\"FIELD2\":536,\"FIELD3\":150},{\"FIELD1\":\"528\",\"FIELD2\":535,\"FIELD3\":177},{\"FIELD1\":\"527\",\"FIELD2\":524,\"FIELD3\":207},{\"FIELD1\":\"328\",\"FIELD2\":640,\"FIELD3\":150},{\"FIELD1\":\"329\",\"FIELD2\":641,\"FIELD3\":179},{\"FIELD1\":\"330\",\"FIELD2\":650,\"FIELD3\":208},{\"FIELD1\":\"323\",\"FIELD2\":683,\"FIELD3\":161},{\"FIELD1\":\"322\",\"FIELD2\":689,\"FIELD3\":183},{\"FIELD1\":\"321\",\"FIELD2\":699,\"FIELD3\":197},{\"FIELD1\":\"240\",\"FIELD2\":740,\"FIELD3\":217},{\"FIELD1\":\"238\",\"FIELD2\":740,\"FIELD3\":187},{\"FIELD1\":\"239\",\"FIELD2\":767,\"FIELD3\":217},{\"FIELD1\":\"237\",\"FIELD2\":767,\"FIELD3\":187},{\"FIELD1\":\"235\",\"FIELD2\":819,\"FIELD3\":180},{\"FIELD1\":\"236\",\"FIELD2\":810,\"FIELD3\":198},{\"FIELD1\":\"228\",\"FIELD2\":866,\"FIELD3\":178},{\"FIELD1\":\"227\",\"FIELD2\":855,\"FIELD3\":205},{\"FIELD1\":\"234\",\"FIELD2\":903,\"FIELD3\":205},{\"FIELD1\":\"233\",\"FIELD2\":904,\"FIELD3\":235},{\"FIELD1\":\"232\",\"FIELD2\":935,\"FIELD3\":206},{\"FIELD1\":\"231\",\"FIELD2\":936,\"FIELD3\":235},{\"FIELD1\":\"230\",\"FIELD2\":968,\"FIELD3\":206},{\"FIELD1\":\"229\",\"FIELD2\":967,\"FIELD3\":235},{\"FIELD1\":\"816\",\"FIELD2\":58,\"FIELD3\":325},{\"FIELD1\":\"815\",\"FIELD2\":80,\"FIELD3\":305},{\"FIELD1\":\"814\",\"FIELD2\":118,\"FIELD3\":292},{\"FIELD1\":\"821\",\"FIELD2\":111,\"FIELD3\":332},{\"FIELD1\":\"719\",\"FIELD2\":183,\"FIELD3\":291},{\"FIELD1\":\"718\",\"FIELD2\":214,\"FIELD3\":302},{\"FIELD1\":\"717\",\"FIELD2\":237,\"FIELD3\":321},{\"FIELD1\":\"716\",\"FIELD2\":255,\"FIELD3\":345},{\"FIELD1\":\"715\",\"FIELD2\":264,\"FIELD3\":374},{\"FIELD1\":\"811\",\"FIELD2\":127,\"FIELD3\":355},{\"FIELD1\":\"810\",\"FIELD2\":173,\"FIELD3\":355},{\"FIELD1\":\"813\",\"FIELD2\":135,\"FIELD3\":393},{\"FIELD1\":\"809\",\"FIELD2\":165,\"FIELD3\":393},{\"FIELD1\":\"712\",\"FIELD2\":136,\"FIELD3\":459},{\"FIELD1\":\"711\",\"FIELD2\":165,\"FIELD3\":459},{\"FIELD1\":\"713\",\"FIELD2\":127,\"FIELD3\":495},{\"FIELD1\":\"714\",\"FIELD2\":151,\"FIELD3\":498},{\"FIELD1\":\"710\",\"FIELD2\":174,\"FIELD3\":496},{\"FIELD1\":\"623\",\"FIELD2\":340,\"FIELD3\":299},{\"FIELD1\":\"622\",\"FIELD2\":365,\"FIELD3\":299},{\"FIELD1\":\"621\",\"FIELD2\":351,\"FIELD3\":332},{\"FIELD1\":\"520\",\"FIELD2\":445,\"FIELD3\":289},{\"FIELD1\":\"519\",\"FIELD2\":476,\"FIELD3\":303},{\"FIELD1\":\"518\",\"FIELD2\":498,\"FIELD3\":322},{\"FIELD1\":\"517\",\"FIELD2\":510,\"FIELD3\":349},{\"FIELD1\":\"516\",\"FIELD2\":518,\"FIELD3\":379},{\"FIELD1\":\"515\",\"FIELD2\":515,\"FIELD3\":404},{\"FIELD1\":\"514\",\"FIELD2\":505,\"FIELD3\":431},{\"FIELD1\":\"521\",\"FIELD2\":439,\"FIELD3\":329},{\"FIELD1\":\"522\",\"FIELD2\":454,\"FIELD3\":338},{\"FIELD1\":\"523\",\"FIELD2\":465,\"FIELD3\":354},{\"FIELD1\":\"524\",\"FIELD2\":472,\"FIELD3\":369},{\"FIELD1\":\"525\",\"FIELD2\":474,\"FIELD3\":389},{\"FIELD1\":\"526\",\"FIELD2\":460,\"FIELD3\":422},{\"FIELD1\":\"513\",\"FIELD2\":425,\"FIELD3\":388},{\"FIELD1\":\"512\",\"FIELD2\":425,\"FIELD3\":417},{\"FIELD1\":\"606\",\"FIELD2\":322,\"FIELD3\":407},{\"FIELD1\":\"607\",\"FIELD2\":309,\"FIELD3\":432},{\"FIELD1\":\"608\",\"FIELD2\":301,\"FIELD3\":460},{\"FIELD1\":\"609\",\"FIELD2\":300,\"FIELD3\":487},{\"FIELD1\":\"610\",\"FIELD2\":309,\"FIELD3\":516},{\"FIELD1\":\"611\",\"FIELD2\":325,\"FIELD3\":541},{\"FIELD1\":\"612\",\"FIELD2\":349,\"FIELD3\":560},{\"FIELD1\":\"614\",\"FIELD2\":389,\"FIELD3\":437},{\"FIELD1\":\"613\",\"FIELD2\":390,\"FIELD3\":466},{\"FIELD1\":\"605\",\"FIELD2\":353,\"FIELD3\":437},{\"FIELD1\":\"604\",\"FIELD2\":347,\"FIELD3\":455},{\"FIELD1\":\"603\",\"FIELD2\":345,\"FIELD3\":474},{\"FIELD1\":\"602\",\"FIELD2\":346,\"FIELD3\":494},{\"FIELD1\":\"601\",\"FIELD2\":383,\"FIELD3\":530},{\"FIELD1\":\"506\",\"FIELD2\":425,\"FIELD3\":485},{\"FIELD1\":\"505\",\"FIELD2\":425,\"FIELD3\":514},{\"FIELD1\":\"508\",\"FIELD2\":515,\"FIELD3\":480},{\"FIELD1\":\"509\",\"FIELD2\":494,\"FIELD3\":504},{\"FIELD1\":\"507\",\"FIELD2\":535,\"FIELD3\":504},{\"FIELD1\":\"504\",\"FIELD2\":536,\"FIELD3\":540},{\"FIELD1\":\"502\",\"FIELD2\":563,\"FIELD3\":540},{\"FIELD1\":\"503\",\"FIELD2\":537,\"FIELD3\":569},{\"FIELD1\":\"501\",\"FIELD2\":558,\"FIELD3\":569},{\"FIELD1\":\"410\",\"FIELD2\":594,\"FIELD3\":480},{\"FIELD1\":\"411\",\"FIELD2\":571,\"FIELD3\":504},{\"FIELD1\":\"409\",\"FIELD2\":614,\"FIELD3\":504},{\"FIELD1\":\"408\",\"FIELD2\":624,\"FIELD3\":540},{\"FIELD1\":\"407\",\"FIELD2\":624,\"FIELD3\":570},{\"FIELD1\":\"406\",\"FIELD2\":650,\"FIELD3\":540},{\"FIELD1\":\"405\",\"FIELD2\":650,\"FIELD3\":570},{\"FIELD1\":\"307\",\"FIELD2\":650,\"FIELD3\":503},{\"FIELD1\":\"306\",\"FIELD2\":670,\"FIELD3\":479},{\"FIELD1\":\"305\",\"FIELD2\":692,\"FIELD3\":503},{\"FIELD1\":\"422\",\"FIELD2\":552,\"FIELD3\":295},{\"FIELD1\":\"423\",\"FIELD2\":541,\"FIELD3\":317},{\"FIELD1\":\"424\",\"FIELD2\":535,\"FIELD3\":341},{\"FIELD1\":\"425\",\"FIELD2\":534,\"FIELD3\":368},{\"FIELD1\":\"426\",\"FIELD2\":537,\"FIELD3\":393},{\"FIELD1\":\"416\",\"FIELD2\":543,\"FIELD3\":445},{\"FIELD1\":\"415\",\"FIELD2\":567,\"FIELD3\":456},{\"FIELD1\":\"414\",\"FIELD2\":592,\"FIELD3\":459},{\"FIELD1\":\"413\",\"FIELD2\":619,\"FIELD3\":455},{\"FIELD1\":\"412\",\"FIELD2\":643,\"FIELD3\":447},{\"FIELD1\":\"421\",\"FIELD2\":631,\"FIELD3\":295},{\"FIELD1\":\"420\",\"FIELD2\":645,\"FIELD3\":319},{\"FIELD1\":\"419\",\"FIELD2\":652,\"FIELD3\":344},{\"FIELD1\":\"418\",\"FIELD2\":652,\"FIELD3\":365},{\"FIELD1\":\"417\",\"FIELD2\":646,\"FIELD3\":393},{\"FIELD1\":\"314\",\"FIELD2\":739,\"FIELD3\":287},{\"FIELD1\":\"315\",\"FIELD2\":711,\"FIELD3\":300},{\"FIELD1\":\"316\",\"FIELD2\":688,\"FIELD3\":320},{\"FIELD1\":\"317\",\"FIELD2\":676,\"FIELD3\":347},{\"FIELD1\":\"318\",\"FIELD2\":669,\"FIELD3\":380},{\"FIELD1\":\"319\",\"FIELD2\":670,\"FIELD3\":404},{\"FIELD1\":\"320\",\"FIELD2\":680,\"FIELD3\":430},{\"FIELD1\":\"313\",\"FIELD2\":746,\"FIELD3\":329},{\"FIELD1\":\"312\",\"FIELD2\":732,\"FIELD3\":339},{\"FIELD1\":\"311\",\"FIELD2\":723,\"FIELD3\":352},{\"FIELD1\":\"310\",\"FIELD2\":715,\"FIELD3\":370},{\"FIELD1\":\"309\",\"FIELD2\":714,\"FIELD3\":386},{\"FIELD1\":\"308\",\"FIELD2\":726,\"FIELD3\":423},{\"FIELD1\":\"219\",\"FIELD2\":765,\"FIELD3\":355},{\"FIELD1\":\"218\",\"FIELD2\":765,\"FIELD3\":382},{\"FIELD1\":\"215\",\"FIELD2\":765,\"FIELD3\":457},{\"FIELD1\":\"214\",\"FIELD2\":765,\"FIELD3\":485},{\"FIELD1\":\"207\",\"FIELD2\":804,\"FIELD3\":446},{\"FIELD1\":\"208\",\"FIELD2\":808,\"FIELD3\":469},{\"FIELD1\":\"210\",\"FIELD2\":798,\"FIELD3\":498},{\"FIELD1\":\"212\",\"FIELD2\":782,\"FIELD3\":515},{\"FIELD1\":\"213\",\"FIELD2\":763,\"FIELD3\":525},{\"FIELD1\":\"201\",\"FIELD2\":773,\"FIELD3\":562},{\"FIELD1\":\"202\",\"FIELD2\":800,\"FIELD3\":553},{\"FIELD1\":\"203\",\"FIELD2\":842,\"FIELD3\":507},{\"FIELD1\":\"204\",\"FIELD2\":852,\"FIELD3\":485},{\"FIELD1\":\"205\",\"FIELD2\":850,\"FIELD3\":451},{\"FIELD1\":\"206\",\"FIELD2\":845,\"FIELD3\":425},{\"FIELD1\":\"226\",\"FIELD2\":807,\"FIELD3\":298},{\"FIELD1\":\"225\",\"FIELD2\":830,\"FIELD3\":298},{\"FIELD1\":\"105\",\"FIELD2\":870,\"FIELD3\":298},{\"FIELD1\":\"104\",\"FIELD2\":892,\"FIELD3\":298},{\"FIELD1\":\"102\",\"FIELD2\":931,\"FIELD3\":298},{\"FIELD1\":\"101\",\"FIELD2\":955,\"FIELD3\":298},{\"FIELD1\":\"224\",\"FIELD2\":818,\"FIELD3\":332},{\"FIELD1\":\"106\",\"FIELD2\":882,\"FIELD3\":332},{\"FIELD1\":\"103\",\"FIELD2\":943,\"FIELD3\":332},{\"FIELD1\":\"221\",\"FIELD2\":846,\"FIELD3\":355},{\"FIELD1\":\"220\",\"FIELD2\":847,\"FIELD3\":384},{\"FIELD1\":\"IBM16\",\"FIELD2\":907,\"FIELD3\":360},{\"FIELD1\":\"IBM15\",\"FIELD2\":885,\"FIELD3\":385},{\"FIELD1\":\"IBM14\",\"FIELD2\":885,\"FIELD3\":421},{\"FIELD1\":\"IBM9\",\"FIELD2\":934,\"FIELD3\":396},{\"FIELD1\":\"IBM4\",\"FIELD2\":1000,\"FIELD3\":361},{\"FIELD1\":\"IBM3\",\"FIELD2\":1020,\"FIELD3\":384},{\"FIELD1\":\"IBM5\",\"FIELD2\":978,\"FIELD3\":389},{\"FIELD1\":\"IBM6\",\"FIELD2\":983,\"FIELD3\":422},{\"FIELD1\":\"IBM10\",\"FIELD2\":930,\"FIELD3\":446},{\"FIELD1\":\"IBM11\",\"FIELD2\":935,\"FIELD3\":469},{\"FIELD1\":\"IBM13\",\"FIELD2\":888,\"FIELD3\":473},{\"FIELD1\":\"IBM12\",\"FIELD2\":909,\"FIELD3\":508},{\"FIELD1\":\"IBM7\",\"FIELD2\":983,\"FIELD3\":445},{\"FIELD1\":\"IBM8\",\"FIELD2\":977,\"FIELD3\":470},{\"FIELD1\":\"IBM2\",\"FIELD2\":1021,\"FIELD3\":484},{\"FIELD1\":\"IBM1\",\"FIELD2\":1000,\"FIELD3\":509}]";
        try
        {
            map=new JSONArray(mapJsonString);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        BitmapFactory.Options myOptions = new BitmapFactory.Options();
        myOptions.inDither = true;
        myOptions.inScaled = false;
        myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// important
        myOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.map, myOptions);

        BitmapFactory.Options dotOptions = new BitmapFactory.Options();
        dotOptions.inDither = true;
        dotOptions.inScaled = false;
        dotOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// important
        dotOptions.inPurgeable = true;
        Bitmap dotbitmap = BitmapFactory.decodeResource(getResources(),R.drawable.dot, dotOptions);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);

        Paint paintTmp = new Paint();
        paintTmp.setAntiAlias(true);
        paintTmp.setColor(Color.GREEN);

        Bitmap workingBitmap = Bitmap.createBitmap(bitmap);
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        canvas.drawCircle(mapXCords, mapYCords, 10, paint);

        Bitmap dotworkingBitmap = Bitmap.createBitmap(bitmap);
        Bitmap dotmutableBitmap = dotworkingBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas dotcanvas = new Canvas(dotmutableBitmap);
        dotcanvas.drawCircle(mapXCords, mapYCords, 10, paint);

        main.setLayoutParams(new AbsoluteLayout.LayoutParams(myOptions.outWidth, myOptions.outHeight, 0, 0));

        mapImageview = new ImageView(InteractiveBoothMapActivity.this);
        AbsoluteLayout.LayoutParams maplayoutParams=new AbsoluteLayout.LayoutParams(myOptions.outWidth, myOptions.outHeight, 0, 0);
        mapImageview.setLayoutParams(maplayoutParams);
        mapImageview.setTag(100);
        main.addView(mapImageview);
        mapImageview.setImageBitmap(bitmap);

        try
        {
            dotImageview=new ImageView[map.length()];
            for(int i=0;i<map.length();i++)
            {
                JSONObject tmpJsonObj=map.getJSONObject(i);

                dotImageview[i]=new ImageView(InteractiveBoothMapActivity.this);
                AbsoluteLayout.LayoutParams dotlayoutParams=new AbsoluteLayout.LayoutParams(10, 10,tmpJsonObj.getInt("FIELD2"),tmpJsonObj.getInt("FIELD3"));
                dotImageview[i].setLayoutParams(dotlayoutParams);
                dotImageview[i].setTag(i);
                main.addView(dotImageview[i]);
                dotImageview[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final AlertDialog.Builder dialog = new AlertDialog.Builder(InteractiveBoothMapActivity.this);
                        dialog.setTitle("Detail");
                        dialog.setMessage("Map detail - "+view.getTag().toString());
                        dialog.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        dialog.show();
                    }
                });
                Bitmap dotBitmapTmp = BitmapFactory.decodeResource(getResources(),R.drawable.dot, myOptions);
                Bitmap dotBitmap = Bitmap.createBitmap(dotBitmapTmp);
                Bitmap dotBitmapMutable = dotBitmap.copy(Bitmap.Config.ARGB_8888, true);
                Canvas dotCanvas = new Canvas(dotBitmapMutable);
                if(tmpJsonObj.getInt("FIELD2")==mapXCords && tmpJsonObj.getInt("FIELD3")==mapYCords)
                    dotCanvas.drawRect(0,0, 10,10, paintTmp);
                else
                    dotCanvas.drawRect(0,0, 10,10, paint);
                dotImageview[i].setImageBitmap(dotBitmapMutable);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        final int centerX = getApplicationContext().getResources().getDisplayMetrics().widthPixels/2;
        final int centerY = getApplicationContext().getResources().getDisplayMetrics().heightPixels/2;
        int displX=(centerX-mapXCords);
        int displY=(centerY-mapYCords);

        main.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        main.animate().translationXBy(displX).translationYBy(displY).scaleX(1.2f).scaleY(1.2f).setDuration(1000);

        main.setOnTouchListener(this);
        zoomin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scaleMinus = scaleMinus + 0.1f;
                scalePlus = scalePlus+ 0.1f;
                if(scalePlus<=MAX_ZOOM)
                    main.animate().scaleX(scalePlus).scaleY(scalePlus).setDuration(1);
            }
        });
        zoomout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scaleMinus = scaleMinus - 0.1f;
                scalePlus = scalePlus - 0.1f;
                if(scaleMinus>=MIN_ZOOM)
                    main.animate().scaleX(scaleMinus).scaleY(scaleMinus).setDuration(1);
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final AbsoluteLayout view= (AbsoluteLayout)v;
        float scale=0.0f;

        dumpEvent(event);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: // first finger down only
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG"); // write to LogCat
                mode = DRAG;
                break;

            case MotionEvent.ACTION_UP:

            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 15f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    float translateX = Math.max(MIN_TRANSLATE, Math.min((event.getX() - start.x), MAX_TRANSLATE));
                    float translateY = Math.max(MIN_TRANSLATE, Math.min((event.getY() - start.y), MAX_TRANSLATE));
                    view.animate().translationX(translateX).translationY(translateY).setDuration(1);

                } else if (mode == ZOOM) {
                    // pinch zooming
                    float newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);

                    if (newDist >15f) {
                        matrix.set(savedMatrix);
                        scale = Math.max(MIN_ZOOM, Math.min((newDist / oldDist), MAX_ZOOM));
                        view.animate().scaleX(scale).scaleY(scale).setDuration(1);
                    }
                }
                break;
        }
        return true;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    @SuppressWarnings("deprecation")
    private void dumpEvent(MotionEvent event) {
        String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
                "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);

        if (actionCode == MotionEvent.ACTION_POINTER_DOWN
                || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(
                    action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }

        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }

        sb.append("]");
        Log.d("Touch Event", sb.toString());
    }
}