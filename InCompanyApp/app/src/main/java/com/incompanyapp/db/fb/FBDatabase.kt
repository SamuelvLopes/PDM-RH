package com.incompanyapp.db.fb

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.incompanyapp.model.Clock
import com.incompanyapp.model.Company
import com.incompanyapp.model.User

class FBDatabase(private val listener: Listener? = null) {
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private var companiesListReg: ListenerRegistration? = null
    interface Listener {
        fun onUserLoaded(user: User)
        fun onCompanyAdded(company: Company)
        fun onCompanyRemoved(company: Company)
        fun onCompanyUpdated(company: Company)
        fun onClockUpdated(clock: Clock)
//        fun onClockAdded(companyName: String, clock: Clock)
//        fun onClockRemoved(companyName: String, clock: Clock)

    }
    init {
        auth.addAuthStateListener { auth ->
            if (auth.currentUser == null) {
                companiesListReg?.remove()
                return@addAuthStateListener
            }
            val refCurrUser = db.collection("users")
                .document(auth.currentUser!!.uid)
            refCurrUser.get().addOnSuccessListener {
                it.toObject(FBUser::class.java)?.let { user ->
                    listener?.onUserLoaded(user.toUser())
                }
            }
            companiesListReg = refCurrUser.collection("companies")
                .addSnapshotListener { snapshots, ex ->
                    if (ex != null) return@addSnapshotListener
                    snapshots?.documentChanges?.forEach { change ->
                        val fbCompany = change.document.toObject(FBCompany::class.java)
                        if (change.type == DocumentChange.Type.ADDED) {
                            listener?.onCompanyAdded(fbCompany.toCompany())
                        } else if (change.type == DocumentChange.Type.REMOVED) {
                            listener?.onCompanyRemoved(fbCompany.toCompany())
                        }
                    }
                }

        }
    }
    fun register(user: User) {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid + "").set(user.toFBUser());
    }

    fun add(company: Company) {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).collection("companies")
            .document(company.name).set(company.toFBCompany())
    }
    fun remove(company: Company) {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).collection("companies")
            .document(company.name).delete()
    }

//    private fun listenToClockChanges(companyRef: DocumentReference) {
//        companyRef.collection("clocks").addSnapshotListener { snapshots, ex ->
//            if (ex != null) return@addSnapshotListener
//            snapshots?.documentChanges?.forEach { change ->
//                val clock = change.document.toObject(Clock::class.java)
//                if (change.type == DocumentChange.Type.ADDED) {
//                    listener?.onClockAdded(companyRef.id, clock)
//                } else if (change.type == DocumentChange.Type.REMOVED) {
//                    listener?.onClockRemoved(companyRef.id, clock)
//                }
//            }
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addClock(companyName: String, clock: Clock) {
        if (auth.currentUser == null) throw RuntimeException("User not logged in!")
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).collection("companies")
            .document(companyName).collection("clocks").document(
                clock.date.dayOfMonth.toString() +
                        clock.date.monthValue.toString() +
                        clock.date.year.toString())
            .set(clock.toFBClock())
    }

    fun removeClock(companyName: String, clockId: String) {
        if (auth.currentUser == null) throw RuntimeException("User not logged in!")
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).collection("companies")
            .document(companyName).collection("clocks").document(clockId).delete()
    }
}
