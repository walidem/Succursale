package cgodin.qc.ca

import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cgodin.qc.ca.model.Succursale
import cgodin.qc.ca.viewmodel.SuccursaleViewModel
import cgodin.qc.ca.viewmodel.SuccursaleViewModelFactory
import cgodin.qc.ca.repository.SuccursaleRepository
import cgodin.qc.ca.model.SuccursaleDatabase
import cgodin.qc.ca.network.RetrofitInstance

class FavorisFragment : Fragment() {

    private lateinit var gestureDetector: GestureDetector
    private lateinit var viewModel: SuccursaleViewModel
    private lateinit var recyclerViewFavoris: RecyclerView
    private var isVisible: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Initialize ViewModel
        val db = SuccursaleDatabase.getDatabase(requireContext())
        val repository = SuccursaleRepository(db.succursaleDao(), RetrofitInstance.apiService)
        val factory = SuccursaleViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[SuccursaleViewModel::class.java]

        val view = inflater.inflate(R.layout.fragment_favoris, container, false)
        recyclerViewFavoris = view.findViewById(R.id.recyclerViewFavoris)
        recyclerViewFavoris.layoutManager = LinearLayoutManager(context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gestureDetector = GestureDetector(context, SwipeGestureDetector {
            findNavController().navigate(R.id.action_favorisFragment_to_gestionFragment)
        })

        view.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_UP) {
                v.performClick()
            }
            true
        }

        viewModel.favoris.observe(viewLifecycleOwner) { favoris ->
            if (favoris.isEmpty()) {
                Toast.makeText(context, "Aucune succursale dans les favoris", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_favorisFragment_to_gestionFragment)
            } else {
                recyclerViewFavoris.adapter = SuccursaleAdapter(
                    favoris.toMutableList(),
                    object : SuccursaleAdapter.OnItemClickListener {
                            override fun onItemClick(succursale: Succursale) {
                                // Pas d'action nécessaire
                            }

                            override fun onModifierClick(succursale: Succursale) {
                                // Pas d'action nécessaire
                            }

                            override fun onSupprimerClick(succursale: Succursale) {
                                // Pas d'action nécessaire
                            }

                        override fun onFavorisClick(succursale: Succursale) {
                            val updatedSuccursale = succursale.copy(isFavoris = !succursale.isFavoris)
                            viewModel.updateFavoris(updatedSuccursale)
                        }
                    },
                    viewModel,
                    requireContext(),
                    isVisible
                )
            }
        }
    }
}