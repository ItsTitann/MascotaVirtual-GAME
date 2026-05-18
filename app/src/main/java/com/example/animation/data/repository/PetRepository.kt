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
        var currentXp = petData.xp + amount
        var currentLevel = petData.level
        var currentHealth = petData.health
        
        // Loop para permitir subir varios niveles si la XP es mucha
        var xpNeeded = 100 + (currentLevel * 50)
        var levelUpOccurred = false
        
        while (currentXp >= xpNeeded) {
            currentXp -= xpNeeded
            currentLevel++
            currentHealth = (currentHealth + 20).coerceAtMost(100)
            xpNeeded = 100 + (currentLevel * 50)
            levelUpOccurred = true
        }
        
        val updates = mutableMapOf<String, Any>()
        updates["xp"] = currentXp
        updates["level"] = currentLevel
        if (levelUpOccurred) {
            updates["health"] = currentHealth
        }
        
        updatePet(petData.id, updates)
    }
}
