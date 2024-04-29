package cgodin.qc.ca

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cgodin.qc.ca.viewmodel.SuccursaleViewModel
import cgodin.qc.ca.viewmodel.SuccursaleViewModelFactory
import cgodin.qc.ca.repository.SuccursaleRepository
import cgodin.qc.ca.network.RetrofitInstance
import cgodin.qc.ca.model.SuccursaleDatabase

class ModifierFragment : Fragment() {
    private lateinit var nomSuccursaleModif: EditText
    private lateinit var budgetSuccursaleModif: EditText
    private lateinit var modifierSuccursaleButton: Button
    private lateinit var viewModel: SuccursaleViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_modifier, container, false)

        val succursaleDao = SuccursaleDatabase.getDatabase(requireContext()).succursaleDao()
        val repository = SuccursaleRepository(succursaleDao, RetrofitInstance.apiService)
        val factory = SuccursaleViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[SuccursaleViewModel::class.java]

        nomSuccursaleModif = view.findViewById(R.id.nomSuccursaleModif)
        budgetSuccursaleModif = view.findViewById(R.id.budgetSuccursaleModif)
        modifierSuccursaleButton = view.findViewById(R.id.modifierSuccursaleButton)

        // Récupérez l'ancien nom de la succursale et le budget actuel
        val ancienneVille = arguments?.getString("nomSuccursale")
        val budgetActuel = arguments?.getString("budgetSuccursale")

        nomSuccursaleModif.setText(ancienneVille)
        budgetSuccursaleModif.setText(budgetActuel)

        modifierSuccursaleButton.setOnClickListener {
            val nouvelleVille = nomSuccursaleModif.text.toString().trim()
            val nouveauBudget = budgetSuccursaleModif.text.toString().trim()
            val autToken = getAuthToken()

            if (nouvelleVille.isNotEmpty() && nouveauBudget.isNotEmpty()) {
                viewModel.modifierSuccursale(autToken, ancienneVille ?: "", nouvelleVille, nouveauBudget)
            } else {
                Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            }
        }

        setupObservers()

        return view
    }

    private fun getAuthToken(): String {
        val sharedPreferences = requireActivity().getSharedPreferences("MesPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("autToken", "") ?: ""
    }

    private fun setupObservers() {
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
        }

        viewModel.successMessage.observe(viewLifecycleOwner) { successMsg ->
            Toast.makeText(context, successMsg, Toast.LENGTH_SHORT).show()
            if (!successMsg.isNullOrEmpty()) {
                findNavController().navigate(R.id.action_modifierFragment_to_gestionFragment)
            }
        }
    }
}