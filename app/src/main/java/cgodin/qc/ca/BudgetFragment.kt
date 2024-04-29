package cgodin.qc.ca

import android.content.Context
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cgodin.qc.ca.model.SuccursaleDatabase
import cgodin.qc.ca.network.RetrofitInstance
import cgodin.qc.ca.repository.SuccursaleRepository
import cgodin.qc.ca.viewmodel.SuccursaleViewModel
import cgodin.qc.ca.viewmodel.SuccursaleViewModelFactory

class BudgetFragment : Fragment() {
    private lateinit var nomSuccursaleBudget: EditText
    private lateinit var budgetSuccursaleTextView: TextView
    private lateinit var afficherBudgetButton: Button
    private lateinit var viewModel: SuccursaleViewModel
    private lateinit var gestureDetector: GestureDetector

    private var villeRecherchee: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_budget, container, false)

        // Initialisation du ViewModel
        val db = SuccursaleDatabase.getDatabase(requireContext())
        val repository = SuccursaleRepository(db.succursaleDao(), RetrofitInstance.apiService)
        val factory = SuccursaleViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[SuccursaleViewModel::class.java]

        nomSuccursaleBudget = view.findViewById(R.id.nomSuccursaleBudget)
        budgetSuccursaleTextView = view.findViewById(R.id.budgetSuccursaleTextView)
        afficherBudgetButton = view.findViewById(R.id.afficherBudgetButton)

        afficherBudgetButton.setOnClickListener {
            val nomVille = nomSuccursaleBudget.text.toString()
            rechercherEtAfficherBudget(nomVille)
        }
        // Initialisation du GestureDetector
        gestureDetector = GestureDetector(context, SwipeGestureDetector {
            // Action à effectuer lors du swipe left
            findNavController().navigate(R.id.action_budgetFragment_to_gestionFragment)
        })

        // Ajouter un OnTouchListener au view
        view.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_UP) {
                v.performClick()
            }
            true
        }

        return view
    }

    private fun rechercherEtAfficherBudget(nomVille: String) {
        villeRecherchee = nomVille
        val autToken = getAuthToken()
        viewModel.getSuccursaleBudget(autToken, nomVille)
    }

    private fun setupObservers() {
        viewModel.succursaleBudgetResponse.observe(viewLifecycleOwner) { response ->
            response?.succursale?.let {
                val budgetText = it.Budget.toString()
                budgetSuccursaleTextView.text = getString(R.string.format_ville_budget, it.Ville, budgetText)
            } ?: Toast.makeText(context, "La succursale de la ville que vous avez entré n'existe pas", Toast.LENGTH_LONG).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun getAuthToken(): String {
        val sharedPreferences = requireActivity().getSharedPreferences("MesPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("autToken", "") ?: ""
    }
}