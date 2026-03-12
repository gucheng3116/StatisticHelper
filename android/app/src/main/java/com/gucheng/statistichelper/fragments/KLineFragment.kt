package com.gucheng.statistichelper.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.gucheng.statistichelper.AccountApplication
import com.gucheng.statistichelper.R
import com.gucheng.statistichelper.database.entity.DailyReport
import com.gucheng.statistichelper.vm.KLineViewModel
import com.gucheng.statistichelper.vm.KLineViewModelFactory
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class KLineFragment : Fragment() {
    private lateinit var chart: LineChart
    private lateinit var legend: Legend

    private val DAILY = 1
    private val WEEKLY = 2
    private val MONTHLY = 3

    private lateinit var dailyText: TextView
    private lateinit var weeklyText: TextView
    private lateinit var monthlyText: TextView
    private val textGroup = ArrayList<TextView>(3)

    private val kLineViewModel: KLineViewModel by viewModels {
        KLineViewModelFactory(
            (requireActivity().application as AccountApplication).itemRepository,
            (requireActivity().application as AccountApplication).dailyReportRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.activity_kline, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setTitle(R.string.change_trend)
        chart = view.findViewById(R.id.chart1)
        initCharts(view)
    }

    private fun initCharts(rootView: View) {
        Log.d(
            "gucheng",
            "initCharts thread id is " + Thread.currentThread().id + ",name is " + Thread.currentThread().name
        )
        chart = rootView.findViewById(R.id.chart1)
        chart.description.isEnabled = false
        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.isScaleXEnabled = true
        chart.isScaleYEnabled = false

        legend = chart.legend
        legend.form = Legend.LegendForm.LINE
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT

        dailyText = rootView.findViewById(R.id.day)
        weeklyText = rootView.findViewById(R.id.week)
        monthlyText = rootView.findViewById(R.id.month)
        textGroup.clear()
        textGroup.add(dailyText)
        textGroup.add(weeklyText)
        textGroup.add(monthlyText)

        setData(DAILY)
        dailyText.setOnClickListener { setData(DAILY) }
        weeklyText.setOnClickListener { setData(WEEKLY) }
        monthlyText.setOnClickListener { setData(MONTHLY) }
    }

    private fun setSelected(type: Int) {
        val selected = type - 1
        for (i in 0 until textGroup.size) {
            textGroup[i].isSelected = (i == selected)
        }
    }

    private fun setData(type: Int) {
        setSelected(type)
        val values = ArrayList<Entry>()
        val scope = CoroutineScope(Job())
        scope.launch {
            val reports: List<DailyReport> = when (type) {
                DAILY -> kLineViewModel.queryDailyReport()
                WEEKLY -> kLineViewModel.queryWeeklyReport()
                MONTHLY -> kLineViewModel.queryMonthlyReport()
                else -> kLineViewModel.queryDailyReport()
            }

            Log.d("gucheng", "reports size is " + reports.size)
            if (reports.isNotEmpty()) {
                var count = 0f
                for (item in reports) {
                    values.add(
                        Entry(
                            count++,
                            item.total?.toFloat() ?: 0f,
                            resources.getDrawable(R.drawable.star)
                        )
                    )
                }
            }

            val xAxis: XAxis = chart.xAxis
            xAxis.enableGridDashedLine(10f, 10f, 0f)
            xAxis.setLabelCount(minOf(reports.size, 5), false)
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    if (value < 0 || value.toInt() > reports.size - 1) {
                        return ""
                    }
                    return reports[value.toInt()].date?.substring(5, 10) ?: ""
                }
            }

            val set1: LineDataSet = if (chart.data != null && chart.data.dataSetCount > 0) {
                val dataSet = chart.data.getDataSetByIndex(0) as LineDataSet
                dataSet.values = values
                dataSet.notifyDataSetChanged()
                chart.data.notifyDataChanged()
                chart.notifyDataSetChanged()
                dataSet
            } else {
                LineDataSet(values, "总资产").apply { setDrawIcons(false) }
            }

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1)
            chart.data = LineData(dataSets)
            chart.notifyDataSetChanged()
            chart.invalidate()
        }
    }
}
