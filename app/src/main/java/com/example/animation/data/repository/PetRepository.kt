package com.example.animation.data.repository

import com.example.animation.data.model.PetData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PetRepository {
    private val database = Firebase.database("https://mascota-virtual-a5572-default-rtdb.firebaseio.com/").reference.child("pets")

    fun savePet(name: String, onComplete: (String?) -> Unit) {
        val petId = database.push().key ?: return onComplete(null)
        val newPet = PetData(id = petId, name = name)
        database.child(petId).setValue(newPet)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onComplete(petId) else onComplete(null)
            }
    }

    fun observePet(petId: String, onResult: (PetData?) -> Unit) {
        database.child(petId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pet = snapshot.getValue(PetData::class.java)
                onResult(pet)
            }
            override fun onCancelled(error: DatabaseError) {
                onResult(null)
            }
        })
    }

    fun getPet(petId: String, onResult: (PetData?) -> Unit) {
        database.child(petId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pet = snapshot.getValue(PetData::class.java)
                onResult(pet)
            }
            override fun onCancelled(error: DatabaseError) {
                onResult(null)
            }
        })
    }

    fun updatePet(petId: String, updates: Map<String, Any>, onComplete: (Boolean) -> Unit = {}) {
        android.util.Log.d("PetRepo", "Updating pet $petId with $updates")
        database.child(petId).updateChildren(updates)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    android.util.Log.e("PetRepo", "Update failed: ${task.exception?.message}")
                } else {
                    android.util.Log.d("PetRepo", "Update successful")
                }
                onComplete(task.isSuccessful)
            }
    }

    fun addXp(petData: PetData, amount: Int) {
        val newXp = petData.xp + amount
        val xpNeeded = 100 + (petData.level * 50)
        
        val updates = mutableMapOf<String, Any>()
        if (newXp >= xpNeeded) {
            updates["level"] = petData.level + 1
            updates["xp"] = newXp - xpNeeded
            // Bonus por subir de nivel: Curar un poco de salud
            updates["health"] = (petData.health + 20).coerceAtMost(100)
        } else {
            updates["xp"] = newXp
        }
        
        updatePet(petData.id, updates)
    }
}
