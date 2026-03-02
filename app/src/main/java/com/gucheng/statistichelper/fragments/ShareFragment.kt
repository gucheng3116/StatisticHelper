package com.gucheng.statistichelper.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.gucheng.statistichelper.AccountApplication
import com.gucheng.statistichelper.R
import com.gucheng.statistichelper.vm.ShareViewModel
import com.gucheng.statistichelper.vm.ShareViewModelFactory
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ShareFragment : Fragment() {
    private val colors = ArrayList<Int>()
    private val viewModel: ShareViewModel by viewModels {
        ShareViewModelFactory(
            (requireActivity().application as AccountApplication).itemRepository,
            (requireActivity().application as AccountApplication).dailyReportRepository
        )
    }

    private lateinit var positiveChart: PieChart
    private lateinit var negativeChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.activity_trend, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setTitle(R.string.share_details)
        initColors()
        initPieChart(view)
        setPositiveData()
        setNegativeData()
    }

    private fun initPieChart(rootView: View) {
        positiveChart = rootView.findViewById(R.id.positive_chart)
        positiveChart.setUsePercentValues(true)
        positiveChart.description.isEnabled = false

        negativeChart = rootView.findViewById(R.id.negetive_chart)
        negativeChart.setUsePercentValues(true)
        negativeChart.description.isEnabled = false
    }

    private fun setPositiveData() {
        val scope = CoroutineScope(Job())
        scope.launch {
            val items = viewModel.getPositiveItems()
            val entries = ArrayList<PieEntry>()
            var sum = 0.0
            for (item in items) {
                sum += item.amount ?: 0.0
            }
            for (item in items) {
                val value: Float = (item.amount ?: 0.0 / sum).toFloat()
                entries.add(PieEntry(value, item.typeName, null))
            }
            setData(positiveChart, entries, "资产分布 (%)")
        }
    }

    private fun setNegativeData() {
        val scope = CoroutineScope(Job())
        scope.launch {
            val items = viewModel.getNegativeItems()
            val entries = ArrayList<PieEntry>()
            var sum = 0.0
            for (item in items) {
                sum += item.amount ?: 0.0
            }
            for (item in items) {
                val value: Float = (item.amount ?: 0.0 / sum).toFloat()
                entries.add(PieEntry(value, item.typeName, null))
            }
            setData(negativeChart, entries, "负债分布 (%)")
        }
    }

    private fun initColors() {
        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
    }

    private fun setData(chart: PieChart, entries: List<PieEntry>, label: String) {
        val dataSet = PieDataSet(entries, label)
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0F, 40F)
        dataSet.selectionShift = 5f
        dataSet.colors = colors

        val data = PieData(dataSet)
        data.setValueFormatter(DefaultValueFormatter(2))
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)

        chart.data = data
        chart.highlightValues(null)
        chart.invalidate()
    }
}
