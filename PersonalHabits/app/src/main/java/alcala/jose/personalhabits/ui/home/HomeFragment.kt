package alcala.jose.personalhabits.ui.home

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
import android.content.Intent
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.Observer
import java.util.ArrayList

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var habitAdapter: HabitAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        homeViewModel.habitsLiveData.observe(viewLifecycleOwner, Observer { habits ->
            habitAdapter = HabitAdapter(requireContext(), habits as ArrayList<Habito?>?)
            recyclerView.adapter = habitAdapter
        })

        homeViewModel.fetchHabits()

        val addHabitButton: ImageButton = binding.addHabitButton
        addHabitButton.setOnClickListener {
            val intent = Intent(activity, AddHabito::class.java)
            startActivity(intent)
        }

        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        fun updateProgress(completed: Int, total: Int) {
            if (total == 0) {
                progressBar.progress = 0
                return
            }
            val percentage = (completed.toFloat() / total * 100).toInt()
            progressBar.progress = percentage
        }

        updateProgress(5,10)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}