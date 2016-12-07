package com.udacity.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.StockProvider;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by josecostamartins on 12/6/16.
 */

public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = WidgetDataProvider.class.getSimpleName();
    private Context mContext;
    private Intent mIntent;
    private Cursor mCursor;
    private StockProvider mStockProvider;
    final private DecimalFormat dollarFormatWithPlus;
    final private DecimalFormat dollarFormat;
    final private DecimalFormat percentageFormat;

    public WidgetDataProvider(Context context, Intent intent) {
        this.mContext = context;
        this.mIntent = intent;

        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");

    }

    @Override
    public void onCreate() {
        final long identityToken = Binder.clearCallingIdentity();
        mCursor = mContext.getContentResolver().query(Contract.Quote.uri,
                Contract.Quote.QUOTE_COLUMNS,
                null, null, Contract.Quote.COLUMN_SYMBOL);
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDataSetChanged() {
        final long identityToken = Binder.clearCallingIdentity();
        mCursor = mContext.getContentResolver().query(Contract.Quote.uri,
                Contract.Quote.QUOTE_COLUMNS,
                null, null, Contract.Quote.COLUMN_SYMBOL);
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews view = new RemoteViews(mContext.getPackageName(), R.layout.list_item_quote);
        if (mCursor.moveToPosition(position)) {

            view.setTextViewText(R.id.symbol, mCursor.getString(Contract.Quote.POSITION_SYMBOL));
            view.setTextViewText(R.id.price, dollarFormat.format(mCursor.getFloat(Contract.Quote.POSITION_PRICE)));
            view.setTextViewText(R.id.change, dollarFormat.format(mCursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE)));

            float rawAbsoluteChange = mCursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);

            if (rawAbsoluteChange > 0) {
                view.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
            } else {
                view.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
            }
        }
        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
