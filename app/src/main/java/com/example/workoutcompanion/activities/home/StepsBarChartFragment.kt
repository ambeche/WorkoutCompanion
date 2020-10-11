package com.example.workoutcompanion.activities.home
/*
* Daily step counts is visualised as a bar chart using MPAndroidChart library and LiveData
*/

import android.graphics.Color
import android.graphics.Color.rgb
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ComplexColorCompat
import androidx.core.view.marginTop
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.workoutcompanion.R
import com.example.workoutcompanion.model.WorkoutCompanionViewModel
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.fragment_steps_bar_chart.view.*
import org.jetbrains.anko.topPadding
import java.text.DateFormat

class StepsBarChartFragment : Fragment() {
    private lateinit var appViewModel: WorkoutCompanionViewModel
    private val dateFormat: DateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appViewModel = ViewModelProvider(this).get(WorkoutCompanionViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragLayout = inflater.inflate(R.layout.fragment_steps_bar_chart,
            container, false)

        // sets and draw bar chart
        appViewModel.userSteps.observe(viewLifecycleOwner) { steps ->
            // sets UI as observer to liveData
            val barEntries = ArrayList<BarEntry>()
            val labels = ArrayList<String>()

            steps.sortedByDescending { dateFormat.parse(it.date)}
                .reversed().forEachIndexed { index, step ->
                    barEntries.add(BarEntry(index.toFloat(), step.value ))
                    labels.add(step.date.dropLast(6))
                }

            Log.d("labels", labels.toString())
            Log.d("entries", barEntries.toString())
            val barDataSet = BarDataSet(barEntries, getString(R.string.steps_label))
            barDataSet.apply {
                setDrawValues(true)
                valueTextSize = 15f
                setColors(
                    rgb(5, 197, 221),
                    rgb(76, 175, 80),
                    rgb(224, 64, 251),
                    rgb(244, 81, 30)
                )
            }
            if (barEntries.isNotEmpty() && labels.isNotEmpty()){
                fragLayout.vStepsBarChart.apply{
                    xAxis.apply {
                        setDrawValueAboveBar(true)
                        isGranularityEnabled = true
                        granularity = 1f
                        axisLineColor = Color.TRANSPARENT
                        axisLeft.isEnabled = false
                        axisRight.isEnabled = false
                        setDrawGridLines(false)
                        position = XAxis.XAxisPosition.TOP_INSIDE
                        valueFormatter = IndexAxisValueFormatter(labels)
                        xAxis.textSize = 14f
                    }
                    axisLeft.spaceTop = 15f
                    contentDescription = getString(R.string.daily_step_counts)

                    description.apply {
                        text = getString(R.string.daily_step_counts)
                        textSize = 16f
                        setPosition(700f, 1065f)
                    }
                    //animateXY(1000,1000)
                    setFitBars(true)
                    legend.isEnabled = false
                    data = BarData(barDataSet)
                    invalidate()
                }
            }
        }

        return fragLayout
    }

    companion object {
        @JvmStatic
        fun newInstance() = StepsBarChartFragment()
    }
}