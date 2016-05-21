package com.shamik.easymoney.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Created by Shamik on 5/4/2016.
 */
public class EasyMoneyWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget_easymoney);
        Intent quickaddIntent = new Intent(context, MainActivity.class);
        Intent openIntent = new Intent(context, MainActivity.class);
        quickaddIntent.setAction(MainActivity.QUICKADD_CLICKED);
        PendingIntent pendingQuickaddIntent
                = PendingIntent.getActivity(context, 0, quickaddIntent, 0);
        PendingIntent pendingOpenIntent
                = PendingIntent.getActivity(context, 0, openIntent, 0);
        // when quickadd is clicked, open main activity with intent to add transaction
        remoteViews.setOnClickPendingIntent(R.id.widget_quickadd_button, pendingQuickaddIntent);
        // when icon is clicked, open main activity as normal
        remoteViews.setOnClickPendingIntent(R.id.widget_open_button, pendingOpenIntent);

        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }
}
