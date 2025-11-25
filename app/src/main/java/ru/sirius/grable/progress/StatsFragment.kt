package ru.sirius.grable.progress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.sirius.grable.R
import ru.sirius.grable.progress.data.StatisticsData
import ru.sirius.grable.progress.data.repository.StatisticsRepository


class StatsFragment : Fragment() {

    private lateinit var spinnerPeriod: Spinner
    private lateinit var tvPeriod: TextView
    private lateinit var rvDays: RecyclerView
    private lateinit var rvStatistics: RecyclerView

    private val statisticsRepository = StatisticsRepository()
    private var currentPeriod = 7

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupSpinner()
        loadStatistics(currentPeriod)
    }

    private fun initViews(view: View) {
        spinnerPeriod = view.findViewById(R.id.spinnerPeriod)
        tvPeriod = view.findViewById(R.id.tvPeriod)
        rvDays = view.findViewById(R.id.rvDays)
        rvStatistics = view.findViewById(R.id.rvStatistics)

        rvDays.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvStatistics.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupSpinner() {
        val periods = arrayOf("7 дней", "30 дней", "90 дней", "Все время")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, periods)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPeriod.adapter = adapter

        spinnerPeriod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentPeriod = when (position) {
                    0 -> 7
                    1 -> 30
                    2 -> 90
                    else -> -1 // Все время
                }
                loadStatistics(currentPeriod)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadStatistics(period: Int) {
        val statisticsData = statisticsRepository.getStatistics(period)
        updateUI(statisticsData)
    }

    private fun updateUI(data: StatisticsData) {
        tvPeriod.text = "Период: ${data.periodTitle}"

        // Обновляем заголовок "Заучивается сейчас"
        val learningNowText = "Заучивается сейчас: ${data.learningNow}"
        requireView().findViewById<TextView>(R.id.tvPeriodTitle).text = learningNowText

        // Настраиваем адаптеры
        val daysAdapter = DaysAdapter(data.days)
        rvDays.adapter = daysAdapter

        val statisticsAdapter = StatisticsAdapter(data.statistics)
        rvStatistics.adapter = statisticsAdapter
    }
}