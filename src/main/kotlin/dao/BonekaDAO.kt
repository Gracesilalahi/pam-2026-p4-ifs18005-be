package org.delcom.dao

import org.delcom.tables.DollTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import java.util.UUID

class DollDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, DollDAO>(DollTable)

    var nama by DollTable.nama
    var pathGambar by DollTable.pathGambar
    var deskripsi by DollTable.deskripsi
    var merk by DollTable.merk
    var material by DollTable.material
    var createdAt by DollTable.createdAt
    var updatedAt by DollTable.updatedAt
}