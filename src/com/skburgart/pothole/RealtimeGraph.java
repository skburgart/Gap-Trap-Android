package com.skburgart.pothole;

import android.content.Context;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;

public class RealtimeGraph {

    // Log tag
    private static final String TAG = "RealtimeGraph";

    private static GraphView graphView;
    private static GraphViewSeries accelerometerSeries;
    private static int x = 0;

    private static final String TITLE = "G-Force Monitor";
    private static final float DIP = 16.0f;

    public RealtimeGraph(Context c) {

        float scale = c.getResources().getDisplayMetrics().density;
        accelerometerSeries = new GraphViewSeries(new GraphViewData[] {});
        graphView = new LineGraphView(c, TITLE);
        graphView.addSeries(accelerometerSeries);
        graphView.setManualYAxisBounds(4, 0);
        graphView.getGraphViewStyle().setNumVerticalLabels(5);
        graphView.getGraphViewStyle().setNumHorizontalLabels(1);
        graphView.getGraphViewStyle().setTextSize(scale * DIP);
        graphView.getGraphViewStyle().setVerticalLabelsWidth(50);
    }

    public void update(double y) {

        Log.v(TAG, "Updating graph");
        accelerometerSeries.appendData(new GraphViewData(x++, y), false, 50);
        graphView.redrawAll();
    }

    public void reset() {

        Log.v(TAG, "Resetting graph");
        accelerometerSeries.resetData(new GraphViewData[] {});
        x = 0;
    }

    public GraphView getView() {

        return graphView;
    }
}
