package cgodin.qc.ca

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cgodin.qc.ca.model.Succursale
import cgodin.qc.ca.model.SuccursaleDatabase
import cgodin.qc.ca.network.RetrofitInstance
import cgodin.qc.ca.repository.SuccursaleRepository
import cgodin.qc.ca.viewmodel.SuccursaleViewModel
import cgodin.qc.ca.viewmodel.SuccursaleViewModelFactory

class GestionFragment : Fragment(), SuccursaleAdapter.OnItemClickListener {

    private lateinit var viewModel: SuccursaleViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var textViewListeVide: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var isVisible: Boolean = true
    private lateinit var adapter: SuccursaleAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val db = Room.databaseBuilder(
            requireContext().applicationContext,
            SuccursaleDatabase::class.java, "SuccursaleDatabase"
        ).build()
        val succursaleDao = db.succursaleDao()
        val apiService = RetrofitInstance.apiService
        val succursaleRepository = SuccursaleRepository(succursaleDao, apiService)
        val factory = SuccursaleViewModelFactory(succursaleRepository)
        viewModel = ViewModelProvider(this, factory)[SuccursaleViewModel::class.java]

        val view = inflater.inflate(R.layout.fragment_gestion, container, false)

        textViewListeVide = view.findViewById(R.id.textViewListeVide)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = SuccursaleAdapter(
            mutableListOf(),
            this,
            viewModel,
            requireContext(),
            isVisible
        )
        recyclerView.adapter = adapter

        viewModel.succursales.observe(viewLifecycleOwner) { succursales ->
            if (succursales.isEmpty()) {
                textViewListeVide.visibility = View.VISIBLE
                adapter.updateData(succursales.toMutableList())
            } else {
                textViewListeVide.visibility = View.GONE
                adapter.updateData(succursales.toMutableList())
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            swipeRefreshLayout.isRefreshing = isLoading
        }

        viewModel.allSuccursalesDeleted.observe(viewLifecycleOwner) { allDeleted ->
            if (allDeleted) {
                val autToken = getAuthToken()
                viewModel.refreshData(autToken)
                viewModel.allSuccursalesDeleted.value = false
            }
        }

        swipeRefreshLayout.setOnRefreshListener {
            val autToken = getAuthToken()
            viewModel.refreshData(autToken)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpActionButtons(view)

        // Refresh data on view creation
        refreshSuccursaleList()

        swipeRefreshLayout.setOnRefreshListener {
            refreshSuccursaleList()
        }
    }

    private fun refreshSuccursaleList() {
        val autToken = getAuthToken()
        viewModel.refreshData(autToken)
        viewModel.succursales.observe(viewLifecycleOwner) { succursales ->
            adapter.updateData(succursales.toMutableList())
            textViewListeVide.visibility = if (succursales.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onItemClick(succursale: Succursale) {
        // Pas d'action nécessaire
    }

    override fun onModifierClick(succursale: Succursale) {
        val action = succursale.Budget?.let {
            GestionFragmentDirections.actionFragmentGestionToFragmentModifier(
                succursale.Ville,
                it
            )
        }
        if (action != null) {
            findNavController().navigate(action)
        }
    }

    override fun onSupprimerClick(succursale: Succursale) {
        afficherDialogueSuppression(succursale)
    }

    override fun onFavorisClick(succursale: Succursale) {
        val updatedSuccursale = succursale.copy(isFavoris = !succursale.isFavoris)
        viewModel.updateFavoris(updatedSuccursale)
    }


    private fun getAuthToken(): String {
        val sharedPreferences = requireActivity().getSharedPreferences("MesPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("autToken", "") ?: ""
    }

    private fun setUpActionButtons(view: View) {
        val ajouterVilleButton = view.findViewById<ImageButton>(R.id.ajouterVilleButton)
        ajouterVilleButton.setOnClickListener {
            findNavController().navigate(R.id.action_gestionFragment_to_ajoutFragment)
        }

        val deconnexionButton = view.findViewById<ImageView>(R.id.deconnexionButton)
        deconnexionButton.setOnClickListener {
            afficherDialogueDeconnexion()
        }

        val supprimerToutButton = view.findViewById<ImageButton>(R.id.supprimerTout)
        supprimerToutButton.setOnClickListener {
            afficherPremiereBoiteDialogue()
        }

        val afficherFavorisButton = view.findViewById<ImageButton>(R.id.afficherFavorisButton)
        afficherFavorisButton.setOnClickListener {
            viewModel.favoris.observe(viewLifecycleOwner) { favoris ->
                if (favoris.isEmpty()) {
                    Toast.makeText(context, "Vous n'avez aucune succursale dans vos listes de favoris", Toast.LENGTH_SHORT).show()
                } else {
                    findNavController().navigate(R.id.action_gestionFragment_to_favorisFragment)
                }
            }
        }

        val afficherBudgetButton = view.findViewById<ImageButton>(R.id.afficherBudgetButton)
        afficherBudgetButton.setOnClickListener {
            findNavController().navigate(R.id.action_gestionFragment_to_budgetFragment)
        }
    }

    private fun afficherDialogueSuppression(succursale: Succursale) {
        AlertDialog.Builder(context)
            .setTitle("Supprimer Succursale")
            .setMessage("Voulez-vous vraiment supprimer la succursale suivante : ${succursale.Ville} ?")
            .setPositiveButton("Oui") { _, _ ->
                val autToken = getAuthToken()
                viewModel.supprimerSuccursale(autToken, succursale.Ville)
            }
            .setNegativeButton("Non", null)
            .show()
    }

    private fun afficherDialogueDeconnexion() {
        AlertDialog.Builder(context)
            .setTitle("Se déconnecter")
            .setMessage("Voulez-vous vous déconnecter ?")
            .setPositiveButton("Oui") { _, _ ->
                naviguerVersEcranConnexion()
            }
            .setNegativeButton("Non", null)
            .show()
    }

    private fun naviguerVersEcranConnexion() {
        supprimerTokenAuthentification()
        findNavController().navigate(R.id.action_gestionFragment_to_loginFragment)
    }

    private fun supprimerTokenAuthentification() {
        val sharedPreferences = requireActivity().getSharedPreferences("MesPreferences", Context.MODE_PRIVATE)
        val seSouvenirDeMoi = sharedPreferences.getBoolean("seSouvenirDeMoi", false)

        with(sharedPreferences.edit()) {
            remove("autToken")
            if (!seSouvenirDeMoi) {
                remove("identifiant")
                remove("motDePasse")
            }
            apply()
        }
    }

    private fun afficherPremiereBoiteDialogue() {
        AlertDialog.Builder(context)
            .setTitle("Confirmation")
            .setMessage("Voulez-vous vraiment supprimer toutes les succursales?")
            .setPositiveButton("Oui") { _, _ ->
                afficherSecondeBoiteDialogue()
            }
            .setNegativeButton("Non", null)
            .show()
    }

    private fun afficherSecondeBoiteDialogue() {
        AlertDialog.Builder(context)
            .setTitle("Confirmation finale")
            .setMessage("Êtes-vous sûr de vouloir supprimer définitivement toutes les succursales ?")
            .setPositiveButton("Oui") { _, _ ->
                val autToken = getAuthToken()
                viewModel.supprimerToutesLesSuccursales(autToken)
            }
            .setNegativeButton("Non", null)
            .show()
    }
}