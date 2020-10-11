package com.example.workoutcompanion.activities.home
/*
* Daily calories burned are visualized in a pie chart using MPAndroidChart library
*/

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.workoutcompanion.R
import com.example.workoutcompanion.model.WorkoutCompanionViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.android.synthetic.main.fragment_calories_pie_chart.view.*
import kotlinx.android.synthetic.main.fragment_steps_bar_chart.view.*
import java.text.DateFormat

class CaloriesPieChartFragment : Fragment() {
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
        val fragLayout = inflater.inflate(R.layout.fragment_calories_pie_chart,
            container, false)
        appViewModel.userSteps.observe(viewLifecycleOwner) { steps ->
            val pieEntries = ArrayList<PieEntry>()
            val labels = ArrayList<String>()

            steps.sortedByDescending { dateFormat.parse(it.date)}
                .reversed().forEachIndexed { index, step ->
                    pieEntries.add(PieEntry(step.calories, step.date.dropLast(6)))
                }

            Log.d("labels", labels.toString())
            Log.d("entries", pieEntries.toString())
            val pieDataSet = PieDataSet(pieEntries, getString(R.string.steps_label))
            pieDataSet.apply {
                setDrawValues(true)
                valueTextSize = 15f
                setColors(
                    Color.rgb(5, 197, 221),
                    Color.rgb(76, 175, 80),
                    Color.rgb(245, 124, 0),
                    Color.rgb(244, 81, 30)
                )
            }
            if (pieEntries.isNotEmpty()){
                fragLayout.vPieChart.apply{
                    contentDescription = getString(R.string.daily_cal_burned_walking)
                    description.isEnabled = false
                    //animateXY(1000,1000)
                    legend.isEnabled = false
                    centerText = getString(R.string.target_cal)
                    setCenterTextSize(18f)
                    setCenterTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                    setDrawRoundedSlices(true)
                    rotationAngle = 60f
                    data = PieData(pieDataSet)
                    invalidate()
                }
            }
        }

        return fragLayout
    }

    companion object {
        @JvmStatic
        fun newInstance() = CaloriesPieChartFragment()
    }
}