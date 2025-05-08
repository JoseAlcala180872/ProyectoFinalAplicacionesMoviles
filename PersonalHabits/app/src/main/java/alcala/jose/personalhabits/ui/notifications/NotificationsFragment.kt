package alcala.jose.personalhabits.ui.notifications

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
import alcala.jose.personalhabits.databinding.FragmentNotificationsBinding
import alcala.jose.personalhabits.ui.HabitAdapter
import alcala.jose.personalhabits.ui.home.HomeViewModel
import android.content.Intent
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private lateinit var notificationsViewModel: NotificationsViewModel
    private lateinit var habitAdapter: HabitAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationsViewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        notificationsViewModel.habitsLiveData.observe(viewLifecycleOwner) { habits ->
            habitAdapter = HabitAdapter(requireContext(), habits as ArrayList<Habito?>?)
            recyclerView.adapter = habitAdapter
        }

        notificationsViewModel.fetchHabits()

        val imageView = view.findViewById<ImageView>(R.id.streakStatusImage)
        val textView = view.findViewById<TextView>(R.id.streakText)

        fun updateProgress(streak: Int) {
            when {
                streak > 10 -> imageView.setImageResource(R.drawable.happyface)
                streak > 5 -> imageView.setImageResource(R.drawable.calmface)
                else -> imageView.setImageResource(R.drawable.sad_face_svgrepo_com)
            }
            textView.text = "Puntos: $streak"
        }

        notificationsViewModel.streak.observe(viewLifecycleOwner) { streakValue ->
            updateProgress(streakValue)
        }

        notificationsViewModel.loadStreak()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
