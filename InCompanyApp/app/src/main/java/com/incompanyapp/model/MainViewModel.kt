package com.incompanyapp.model

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.incompanyapp.db.fb.FBDatabase

class MainViewModel : ViewModel(), FBDatabase.Listener {
    private val _companies = emptyList<Company>().toMutableStateList()
    val companies : List<Company>
        get() = _companies

    var selectedCompany = mutableStateOf<Company?>(null)

    val availableCompanies = getCompany().toMutableStateList()

    /**
     * - Criar uma variavel para armazenar as empresas do usuario logado (DONE)
     * - Usar as empresas debaixo para checar o codigo ao adicionar a empresa
     * - Criar na tela botao de adicionar empresa
     * - Passar como parametro o FBdb e o outro igual na pratica
     * - Salvar as empresas por usuario
     * - Dentro da empresa seria salvo os horarios ( da forma hierarquica)
     * - Usuario -> Empresa -> Horario
     * - Verificar pela localizaçao da empresa se o usuario esta a 100 metros para liberar o botao de log de hora
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
        _companies.add(company)
    }
    override fun onCompanyRemoved(company: Company) {
        _companies.remove(company)
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