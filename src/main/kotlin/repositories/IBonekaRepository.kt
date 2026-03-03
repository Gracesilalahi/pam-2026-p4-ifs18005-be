package org.delcom.repositories

import org.delcom.entities.Doll

interface IDollRepository {
    suspend fun getDolls(search: String): List<Doll>
    suspend fun getDollById(id: String): Doll?
    suspend fun getDollByName(name: String): Doll?
    suspend fun addDoll(doll: Doll): String
    suspend fun updateDoll(id: String, newDoll: Doll): Boolean
    suspend fun removeDoll(id: String): Boolean
}