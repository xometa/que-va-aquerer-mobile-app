package com.grupo5.quevaquerer.props;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

public class MyAxisValueFormatter implements ValueFormatter {
    DecimalFormat df;

    public MyAxisValueFormatter() {
        this.df = new DecimalFormat("0.00");
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return "$ " + df.format(value);
    }
}
