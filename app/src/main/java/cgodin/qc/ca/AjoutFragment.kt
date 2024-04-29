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
import cgodin.qc.ca.model.SuccursaleDatabase
import cgodin.qc.ca.network.RetrofitInstance
import cgodin.qc.ca.repository.SuccursaleRepository
import cgodin.qc.ca.viewmodel.SuccursaleViewModel
import cgodin.qc.ca.viewmodel.SuccursaleViewModelFactory

class AjoutFragment : Fragment() {
    private lateinit var viewModel: SuccursaleViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_ajout, container, false)

        val apiService = RetrofitInstance.apiService
        val succursaleDao = SuccursaleDatabase.getDatabase(requireContext()).succursaleDao()
        val repository = SuccursaleRepository(succursaleDao, apiService)
        val factory = SuccursaleViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory)[SuccursaleViewModel::class.java]

        setupObservers()

        val villeEditText = view.findViewById<EditText>(R.id.nomSuccursaleEditText)
        val budgetEditText = view.findViewById<EditText>(R.id.budgetSuccursaleEditText)
        val addButton = view.findViewById<Button>(R.id.ajouterSuccursaleButton)

        addButton.setOnClickListener {
            val ville = villeEditText.text.toString()
            val budget = budgetEditText.text.toString()
            if (ville.isNotEmpty() && budget.isNotEmpty()) {
                val aut = getAuthToken()
                viewModel.ajouterSuccursale(aut, ville, budget)
            } else {
                Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            }
        }

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
            if (!successMsg.isNullOrEmpty()) {
                Toast.makeText(context, successMsg, Toast.LENGTH_SHORT).show()
                // Vider les champs de texte après un ajout réussi
                view?.findViewById<EditText>(R.id.nomSuccursaleEditText)?.setText("")
                view?.findViewById<EditText>(R.id.budgetSuccursaleEditText)?.setText("")
            }
        }
    }
}