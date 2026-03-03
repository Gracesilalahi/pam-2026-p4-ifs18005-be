package org.delcom.helpers

import kotlinx.coroutines.Dispatchers
import org.delcom.dao.DollDAO
import org.delcom.dao.PlantDAO
import org.delcom.entities.Doll
import org.delcom.entities.Plant
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)

// Konversi DAO ke Model untuk Plant
fun daoToModel(dao: PlantDAO) = Plant(
    dao.id.value.toString(),
    dao.nama,
    dao.pathGambar,
    dao.deskripsi,
    dao.manfaat,
    dao.efekSamping,
    dao.createdAt,
    dao.updatedAt
)

// Konversi DAO ke Model untuk Doll (Versi Boneka)
fun daoToModel(dao: DollDAO) = Doll(
    dao.id.value.toString(),
    dao.nama,
    dao.pathGambar,
    dao.deskripsi,
    dao.merk,
    dao.material,
    dao.createdAt,
    dao.updatedAt
)