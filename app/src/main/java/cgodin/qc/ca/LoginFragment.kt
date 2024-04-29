package cgodin.qc.ca

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cgodin.qc.ca.model.SuccursaleDatabase
import cgodin.qc.ca.network.RetrofitInstance
import cgodin.qc.ca.repository.AuthRepository
import cgodin.qc.ca.repository.SuccursaleRepository
import cgodin.qc.ca.viewmodel.AuthViewModel
import cgodin.qc.ca.viewmodel.AuthViewModelFactory
import cgodin.qc.ca.viewmodel.SuccursaleViewModel
import cgodin.qc.ca.viewmodel.SuccursaleViewModelFactory

class LoginFragment : Fragment() {
    private lateinit var authViewModel: AuthViewModel
    private lateinit var succursaleViewModel: SuccursaleViewModel

    // Declare identifiant and motDePasse
    private var identifiant: String = ""
    private var motDePasse: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // Création de l'instance de AuthRepository
        val apiService = RetrofitInstance.apiService
        val authRepository = AuthRepository(apiService)

        // Initialisation de AuthViewModel avec la Factory
        val factory = AuthViewModelFactory(authRepository)
        authViewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        // Initialiser succursaleViewModel
        val succursaleRepository = SuccursaleRepository(SuccursaleDatabase.getDatabase(requireContext()).succursaleDao(), apiService)
        succursaleViewModel = ViewModelProvider(this, SuccursaleViewModelFactory(succursaleRepository))[SuccursaleViewModel::class.java]

        val identifiantEditText = view.findViewById<EditText>(R.id.editTextIdentifiant)
        val motDePasseEditText = view.findViewById<EditText>(R.id.editTextMotDePasse)
        val checkboxSauvegarder = view.findViewById<CheckBox>(R.id.checkboxSauvegarder)
        val buttonConnexion = view.findViewById<Button>(R.id.buttonConnexion)

        // Charger les identifiants si sauvegardés
        val sharedPreferences = requireActivity().getSharedPreferences("MesPreferences", Context.MODE_PRIVATE)
        val savedIdentifiant = sharedPreferences.getString("identifiant", "")
        val savedMotDePasse = sharedPreferences.getString("motDePasse", "")
        if (!savedIdentifiant.isNullOrEmpty() && !savedMotDePasse.isNullOrEmpty()) {
            identifiantEditText.setText(savedIdentifiant)
            motDePasseEditText.setText(savedMotDePasse)
            checkboxSauvegarder.isChecked = true
        }

        buttonConnexion.setOnClickListener {
            identifiant = identifiantEditText.text.toString()
            motDePasse = motDePasseEditText.text.toString()

            if (identifiant.isNotBlank() && motDePasse.isNotBlank()) {
                authViewModel.loginUser(identifiant, motDePasse)

                if (checkboxSauvegarder.isChecked) {
                    saveCredentialsInSharedPreferences(sharedPreferences, identifiant, motDePasse, true)
                }
            } else {
                Toast.makeText(context, "Veuillez entrer l'identifiant et le mot de passe", Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.token.observe(viewLifecycleOwner) { receivedToken ->
            if (!receivedToken.isNullOrEmpty()) {
                val aut = generateAut(identifiant, motDePasse)
                with(sharedPreferences.edit()) {
                    putString("autToken", aut)
                    if (checkboxSauvegarder.isChecked) {
                        putString("identifiant", identifiant)
                        putString("motDePasse", motDePasse)
                    }
                    apply()
                }

                findNavController().navigate(R.id.action_loginFragment_to_gestionFragment)
            }
        }

        authViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            if (!errorMsg.isNullOrEmpty()) {
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            }
        }

        val textLienInscription = view.findViewById<TextView>(R.id.textLienConnexion)
        textLienInscription.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_inscriptionFragment)
        }

        return view
    }

    private fun generateAut(identifiant: String, motDePasse: String): String {
        val lastFiveDigits = motDePasse.takeLast(5)
        return identifiant + lastFiveDigits
    }

    private fun saveCredentialsInSharedPreferences(sharedPreferences: SharedPreferences, identifiant: String, motDePasse: String, seSouvenir: Boolean) {
        with(sharedPreferences.edit()) {
            putString("identifiant", identifiant)
            putString("motDePasse", motDePasse)
            putString("autToken", generateAut(identifiant, motDePasse))
            putBoolean("seSouvenirDeMoi", seSouvenir)
            apply()
        }
    }
}
