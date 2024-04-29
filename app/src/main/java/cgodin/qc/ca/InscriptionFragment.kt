package cgodin.qc.ca

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cgodin.qc.ca.network.RetrofitInstance
import cgodin.qc.ca.repository.InscriptionRepository
import cgodin.qc.ca.viewmodel.InscriptionViewModel
import cgodin.qc.ca.viewmodel.InscriptionViewModelFactory

class InscriptionFragment : Fragment() {
    private lateinit var viewModel: InscriptionViewModel
    private lateinit var editTextMatricule: EditText
    private lateinit var editTextMotDePasse: EditText
    private lateinit var editTextNom: EditText
    private lateinit var editTextPrenom: EditText
    private lateinit var buttonInscription: Button
    private lateinit var textLienConnexion: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inscription, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialisation des vues
        editTextMatricule = view.findViewById(R.id.editTextMatricule)
        editTextMotDePasse = view.findViewById(R.id.editTextMotDePasse)
        editTextNom = view.findViewById(R.id.editTextNom)
        editTextPrenom = view.findViewById(R.id.editTextPrenom)
        buttonInscription = view.findViewById(R.id.buttonInscription)
        textLienConnexion = view.findViewById(R.id.textLienConnexion)

        // Création de l'instance de InscriptionRepository
        val apiService =
            RetrofitInstance.apiService
        val inscriptionRepository = InscriptionRepository(apiService)

        // Initialisation de InscriptionViewModel avec la Factory
        val factory = InscriptionViewModelFactory(inscriptionRepository)
        viewModel = ViewModelProvider(this, factory).get(InscriptionViewModel::class.java)

        // Observer les réponses et erreurs
        viewModel.inscriptionResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                // Gérer la réponse réussie
                findNavController().navigate(R.id.action_inscriptionFragment_to_loginFragment)
            } else {
                // Ce cas est traité dans errorMessage
            }
        }


        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
        }

        // Gérer l'inscription
        buttonInscription.setOnClickListener {
            val mat = editTextMatricule.text.toString()
            val motDePasse = editTextMotDePasse.text.toString()
            val nom = editTextNom.text.toString()
            val prenom = editTextPrenom.text.toString()

            if (validatePassword(motDePasse)) {
                if (mat.isNotBlank() && motDePasse.isNotBlank() && nom.isNotBlank() && prenom.isNotBlank()) {
                    viewModel.inscrire(mat, motDePasse, nom, prenom)
                } else {
                    Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Le mot de passe doit se terminer par 5 chiffres.", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.inscriptionResponse.observe(viewLifecycleOwner) { response ->
            if (response != null && findNavController().currentDestination?.id == R.id.inscriptionFragment) {
                // Gérer la réponse réussie
                findNavController().navigate(R.id.action_inscriptionFragment_to_loginFragment)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
        }

        // Naviguer vers la page de connexion
        textLienConnexion.setOnClickListener {
            findNavController().navigate(R.id.action_inscriptionFragment_to_loginFragment)
        }
    }

    private fun validatePassword(password: String): Boolean {
        return password.matches(Regex(".*\\d{5}$"))
    }
}