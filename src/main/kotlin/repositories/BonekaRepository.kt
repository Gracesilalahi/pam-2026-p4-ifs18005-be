package org.delcom.repositories

import org.delcom.dao.DollDAO
import org.delcom.entities.Doll
import org.delcom.helpers.daoToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.DollTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.UUID

class DollRepository : IDollRepository {
    override suspend fun getDolls(search: String): List<Doll> = suspendTransaction {
        if (search.isBlank()) {
            DollDAO.all()
                .orderBy(DollTable.createdAt to SortOrder.DESC)
                .limit(20)
                .map(::daoToModel)
        } else {
            val keyword = "%${search.lowercase()}%"

            DollDAO
                .find {
                    DollTable.nama.lowerCase() like keyword
                }
                .orderBy(DollTable.nama to SortOrder.ASC)
                .limit(20)
                .map(::daoToModel)
        }
    }

    override suspend fun getDollById(id: String): Doll? = suspendTransaction {
        DollDAO
            .find { (DollTable.id eq UUID.fromString(id)) }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun getDollByName(name: String): Doll? = suspendTransaction {
        DollDAO
            .find { (DollTable.nama eq name) }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun addDoll(doll: Doll): String = suspendTransaction {
        val dollDAO = DollDAO.new {
            nama = doll.nama
            pathGambar = doll.pathGambar
            deskripsi = doll.deskripsi
            merk = doll.merk
            material = doll.material
            createdAt = doll.createdAt
            updatedAt = doll.updatedAt
        }

        dollDAO.id.value.toString()
    }

    override suspend fun updateDoll(id: String, newDoll: Doll): Boolean = suspendTransaction {
        val dollDAO = DollDAO
            .find { DollTable.id eq UUID.fromString(id) }
            .limit(1)
            .firstOrNull()

        if (dollDAO != null) {
            dollDAO.nama = newDoll.nama
            dollDAO.pathGambar = newDoll.pathGambar
            dollDAO.deskripsi = newDoll.deskripsi
            dollDAO.merk = newDoll.merk
            dollDAO.material = newDoll.material
            dollDAO.updatedAt = newDoll.updatedAt
            true
        } else {
            false
        }
    }

    override suspend fun removeDoll(id: String): Boolean = suspendTransaction {
        val rowsDeleted = DollTable.deleteWhere {
            DollTable.id eq UUID.fromString(id)
        }
        rowsDeleted == 1
    }
}