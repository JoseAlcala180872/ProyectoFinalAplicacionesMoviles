package alcala.jose.personalhabits.ui.home

import HomeViewModel
import alcala.jose.personalhabits.AddHabito
import alcala.jose.personalhabits.Dominio.Habito
import alcala.jose.personalhabits.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import alcala.jose.personalhabits.databinding.FragmentHomeBinding
import alcala.jose.personalhabits.ui.HabitAdapter
import android.R.attr.visible
import android.content.Intent
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.Observer
import java.util.ArrayList
import android.util.Log

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var habitAdapter: HabitAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        Log.d("HomeFragment", "ViewModel initialized")

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)



        homeViewModel.habitsLiveData.observe(viewLifecycleOwner, Observer { habits ->
            Log.d("HomeFragment", "Fetched ${habits.size} habits")
            if (habits.size<=0){
                binding.amountStatus.visibility= View.VISIBLE
            } else{
                binding.amountStatus.visibility= View.GONE
            }

            habitAdapter = HabitAdapter(
                requireContext(),
                habits as ArrayList<Habito>,
                true
            ) {
                homeViewModel.fetchHabits()
            }
            recyclerView.adapter = habitAdapter
        })

        homeViewModel.fetchHabits()


        val addHabitButton: ImageButton = binding.addHabitButton
        addHabitButton.setOnClickListener {
            val intent = Intent(activity, AddHabito::class.java)
            startActivity(intent)
        }

        val progressBar = binding.progressBar

        var total = 0
        var completed = 0

        fun updateProgress(completed: Int, total: Int) {
            val percentage = if (total == 0) 0 else (completed.toFloat() / total * 100).toInt()
            Log.d("HomeFragment", "Updating progress bar: completed=$completed, total=$total, percentage=$percentage")
            progressBar?.progress = percentage
        }

        homeViewModel.totalHabitsOfTheDayLiveData.observe(viewLifecycleOwner) { totalCount ->
            Log.d("HomeFragment", "Observed total habits: $totalCount")
            total = totalCount
            updateProgress(completed, total)
        }

        homeViewModel.totalDoneHabitsOfTheDayLiveData.observe(viewLifecycleOwner) { doneCount ->
            Log.d("HomeFragment", "Observed completed habits: $doneCount")
            completed = doneCount
            updateProgress(completed, total)
        }


        // Trigger the counts
        Log.d("HomeFragment", "Requesting habit counts...")
        homeViewModel.getAmountHabitsOfTheDay()
        homeViewModel.getDoneAmountHabitsOfTheDay()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
