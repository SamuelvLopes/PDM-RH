package com.incompanyapp.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.incompanyapp.db.fb.FBDatabase
import com.incompanyapp.ui.calculateDistance

class MainViewModel : ViewModel(), FBDatabase.Listener {
    private val _companies = mutableStateMapOf<String, Company>()
    val companies : List<Company>
        get() = _companies.values.toList()

    private var _selectedCompany = mutableStateOf<Company?>(null)
    var selectedCompany: Company?
        get() = _selectedCompany.value
        set(tmp) { _selectedCompany = mutableStateOf(tmp?.copy()) }

    private var _currentClock = mutableStateOf<Clock?>(null)
    var currentClock: Clock?
        get() = _currentClock.value
        set(tmp) { _currentClock = mutableStateOf(tmp?.copy()) }

    val availableCompanies = getCompany().toMutableStateList()

    /**
     * - Criar uma variavel para armazenar as empresas do usuario logado (DONE)
     * - Usar as empresas debaixo para checar o codigo ao adicionar a empresa
     * - Criar na tela botao de adicionar empresa (DONE)
     * - Passar como parametro o FBdb e o outro igual na pratica (DONE)
     * - Salvar as empresas por usuario (DONE)
     * - Dentro da empresa seria salvo os horarios ( da forma hierarquica)
     * - Usuario -> Empresa -> Horario
     * - Verificar pela localizaçao da empresa se o usuario esta a 100 metros para liberar o botao de log de hora (DONE)
     *
     */

    private val _user = mutableStateOf(User("", ""))
    val user : User
        get() = _user.value

    private var _loggedIn = mutableStateOf(false)
    val loggedIn : Boolean
        get() = _loggedIn.value
    private val listener = FirebaseAuth.AuthStateListener {
            firebaseAuth ->
        _loggedIn.value = firebaseAuth.currentUser != null
    }
    init {
        listener.onAuthStateChanged(Firebase.auth)
        Firebase.auth.addAuthStateListener(listener)
    }
    override fun onCleared() {
        Firebase.auth.removeAuthStateListener(listener)
    }

    override fun onUserLoaded(user: User) {
        _user.value = user
    }
    override fun onCompanyAdded(company: Company) {
        _companies[company.name] = company
    }
    override fun onCompanyRemoved(company: Company) {
        _companies.remove(company.name)
    }
    override fun onCompanyUpdated(company: Company) {
        if (_selectedCompany.value?.name == company.name) {
            _selectedCompany.value = company.copy()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClockUpdated(clock: Clock) {
        if (_currentClock.value?.date == clock.date) {
            _currentClock.value = clock.copy()
        }
    }

//    override fun onClockAdded(companyName: String, clock: Clock) {
//        // Handle the addition of a clock, e.g., update the selected company's clocks list
//        _companies[companyName]?.let { company ->
//            // Update the company’s clock list (make sure you have a mutable structure)
//            val updatedClocks = company.clocks?.toMutableList()
//             if (updatedClocks!!.isNotEmpty() && updatedClocks.filter { it.date == clock.date }.size == 1) {
//                updatedClocks.removeAt(updatedClocks.indexOf(updatedClocks.find { it.date == clock.date }))
//            }
//            updatedClocks.add(clock)
//            _companies[companyName] = company.copy(clocks = updatedClocks)
//
//            // If this is the selected company, update the selected company as well
//            if (_selectedCompany.value?.name == companyName) {
//                _selectedCompany.value = company.copy(clocks = updatedClocks)
//            }
//        }
//    }
//
//    override fun onClockRemoved(companyName: String, clock: Clock) {
//        // Handle the removal of a clock, e.g., update the selected company's clocks list
//        _companies[companyName]?.let { company ->
//            // Update the company’s clock list (make sure you have a mutable structure)
//            val updatedClocks = company.clocks?.toMutableList()
//            updatedClocks?.remove(clock) // Assuming clock has overridden equals method
//            _companies[companyName] = company.copy(clocks = updatedClocks)
//
//            // If this is the selected company, update the selected company as well
//            if (_selectedCompany.value?.name == companyName) {
//                _selectedCompany.value = company.copy(clocks = updatedClocks)
//            }
//        }
//    }

    fun isWithinDistance(userLocation: LatLng): Boolean {
        return selectedCompany?.let { company ->
            val distance = calculateDistance(userLocation, company.location)
            distance <= 100 // Check if within 100 meters
        } ?: false
    }
}

private fun getCompany() = listOf(
    Company(name = "CESAR", code = "1", location = LatLng(-8.058102710673973, -34.87223689325401)),
    Company(name = "Liferay", code = "2", location = LatLng(-8.064763855259473, -34.87389842375884)),
    Company(name = "Projeto CIn/Motorola", code = "3", location = LatLng(-8.05528478542925, -34.95132330499805)),
    Company(name = "SERPRO", code = "4", location = LatLng(-8.034591151068645, -34.909191362026455)),
    Company(name = "Accenture", code = "5", location = LatLng(-8.058976911515272, -34.86970396202903)),
    Company(name = "Thoughtworks", code = "6", location = LatLng(-8.065035739374215, -34.89637836064772))
)