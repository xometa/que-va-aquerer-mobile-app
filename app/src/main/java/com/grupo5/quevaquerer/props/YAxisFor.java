package com.grupo5.quevaquerer.props;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import java.text.DecimalFormat;

public class YAxisFor implements YAxisValueFormatter {
    DecimalFormat df;

    public YAxisFor() {
        this.df = new DecimalFormat("0.00");
    }
    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        return "$ " + df.format(value);
    }
}
