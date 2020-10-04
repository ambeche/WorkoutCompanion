package com.example.workoutcompanion.activities.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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

class StepsBarChartFragment : Fragment() {
    private lateinit var appViewModel: WorkoutCompanionViewModel

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
        appViewModel.userSteps.observe(viewLifecycleOwner, {steps ->
            val barEntries = ArrayList<BarEntry>()
            val labels = ArrayList<String>()

            steps.forEachIndexed { index, step ->
                barEntries.add(BarEntry(step.value, index.toFloat()))
                labels.add(step.date.dropLast(6))

            }
            Log.d("label", labels.toString())
            val barDataSet = BarDataSet(barEntries, getString(R.string.steps_label))
            barDataSet.apply {
                setDrawValues(true)
                barBorderWidth = 24f
                valueTextSize = 12f
                barShadowColor = getColor(R.color.colorSecondaryDark)
                barBorderColor = getColor(R.color.colorPrimaryLight)

            }
            if (barEntries.isNotEmpty() && labels.isNotEmpty()){
                fragLayout.vStepsBarChart.apply{
                    xAxis.apply {
                        //setCenterAxisLabels(true)
                        setDrawValueAboveBar(true)
                        isGranularityEnabled = true
                        axisLeft.isEnabled = false
                        axisRight.isEnabled = false
                        setDrawGridLines(false)
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
                        valueFormatter = IndexAxisValueFormatter(labels)
                    }
                    contentDescription = getString(R.string.daily_step_counts)

                    description.apply {
                        text = getString(R.string.daily_step_counts)
                        textSize = 16f
                        //setPosition(800f,1060f)
                    }
                    //animateXY(1000,1000)
                    setFitBars(true)

                    data = BarData(barDataSet)
                    invalidate()
                }
            }
        })

        return fragLayout
    }

    companion object {
        @JvmStatic
        fun newInstance() = StepsBarChartFragment()
    }
}