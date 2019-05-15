package com.example.maanjo.expense_mgmt.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.maanjo.expense_mgmt.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class ChartViewer extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        BarChart chart = findViewById(R.id.barchart);

        ArrayList<BarEntry> NoOfEmp = new ArrayList<BarEntry>();

        NoOfEmp.add(new BarEntry(945f, 0));
        NoOfEmp.add(new BarEntry(1040f, 1));
        NoOfEmp.add(new BarEntry(1133f, 2));
        NoOfEmp.add(new BarEntry(1240f, 3));
        NoOfEmp.add(new BarEntry(1369f, 4));
        NoOfEmp.add(new BarEntry(1487f, 5));
        NoOfEmp.add(new BarEntry(1501f, 6));
        NoOfEmp.add(new BarEntry(1645f, 7));
        NoOfEmp.add(new BarEntry(1578f, 8));
        NoOfEmp.add(new BarEntry(1695f, 9));

        ArrayList<String> year = new ArrayList<String>();

        year.add("2008");
        year.add("2009");
        year.add("2010");
        year.add("2011");
        year.add("2012");
        year.add("2013");
        year.add("2014");
        year.add("2015");
        year.add("2016");
        year.add("2017");

        BarDataSet bardataset = new BarDataSet(NoOfEmp, "No Of Employee");
        chart.animateY(5000);
        BarData data = new BarData(bardataset);
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        chart.setData(data);

        PieChart pieChart = findViewById(R.id.piechart);
        ArrayList NoOfEmp2 = new ArrayList();

        ArrayList<Entry> NoOfEmpP = new ArrayList();

        NoOfEmpP.add(new Entry(945f, 0));
        NoOfEmpP.add(new Entry(1040f, 1));
        NoOfEmpP.add(new Entry(1133f, 2));
        NoOfEmpP.add(new Entry(1240f, 3));
        NoOfEmpP.add(new Entry(1369f, 4));
        NoOfEmpP.add(new Entry(1487f, 5));
        NoOfEmpP.add(new Entry(1501f, 6));
        NoOfEmpP.add(new Entry(1645f, 7));
        NoOfEmpP.add(new Entry(1578f, 8));
        NoOfEmpP.add(new Entry(1695f, 9));

        PieDataSet dataSet = new PieDataSet(NoOfEmp2, "Number Of Employees");

        PieData data2 = new PieData(dataSet);
        pieChart.setData(data2);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.animateXY(5000, 5000);
    }


}
