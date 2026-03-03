package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object DollTable : UUIDTable("dolls") {
    val nama = varchar("nama", 100)
    val pathGambar = varchar("path_gambar", 255)
    val deskripsi = text("deskripsi")
    val merk = text("merk")             // Pengganti manfaat
    val material = text("material")     // Pengganti efek_samping
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}