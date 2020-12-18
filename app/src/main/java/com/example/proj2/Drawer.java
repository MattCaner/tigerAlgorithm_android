package com.example.proj2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class Drawer extends View{

    private int paintColor;
    private Paint paint;
    private List<Point> points;
    private List<Integer> clusterIds;
    private boolean showingClusters;
    private int [] clusterColors;

    public Drawer(Context context, AttributeSet attrs) {
        super(context,attrs);
        this.showingClusters = false;
        this.paintColor = Color.GRAY;
        this.paint = new Paint();
        this.paint.setColor(paintColor);
        this.paint.setAntiAlias(true);
        this.paint.setStrokeWidth(8);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeJoin(Paint.Join.ROUND);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.points = new ArrayList<Point>();
        clusterColors = new int [10];
        clusterColors[0] = Color.RED;
        clusterColors[1] = Color.BLUE;
        clusterColors[2] = Color.GREEN;
        clusterColors[3] = Color.YELLOW;
        clusterColors[4] = Color.DKGRAY;
        clusterColors[5] = Color.BLACK;
        clusterColors[6] = Color.YELLOW;
        clusterColors[7] = Color.MAGENTA;
        clusterColors[8] = Color.CYAN;
        clusterColors[9] = Color.LTGRAY;

    }

    @Override
    protected void onDraw(Canvas canvas){
        if(!showingClusters){
            this.paintColor = Color.GRAY;
            for(Point p : points){
                canvas.drawCircle(p.x, p.y, 4, paint);
            }
        } else{
            for(int i = 0; i<points.size(); i++){
                if(clusterIds.get(i)==-1) this.paint.setColor(Color.GRAY);
                else this.paint.setColor(clusterColors[clusterIds.get(i)]);
                canvas.drawCircle(points.get(i).x, points.get(i).y, 4, paint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!showingClusters){
            float touchX = event.getX();
            float touchY = event.getY();
            points.add(new Point(Math.round(touchX), Math.round(touchY)));
            // indicate view should be redrawn
            postInvalidate();
        }
        return true;
    }

    public void clearPoints(){
        points.clear();
        showingClusters = false;
        paint.setColor(Color.GRAY);
        postInvalidate();
    }

    public ArrayList<Vec2> getPoints(){
        ArrayList<Vec2> result = new ArrayList<Vec2>();
        for(Point p : points){
            result.add(new Vec2(p.x,p.y));
        }
        return result;
    }

    public void loadClustering(ArrayList<Integer> clusters){
        clusterIds = clusters;
        showingClusters = true;
        postInvalidate();
    }
}
