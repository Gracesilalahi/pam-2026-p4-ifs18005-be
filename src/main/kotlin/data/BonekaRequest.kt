package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Doll

@Serializable
data class DollRequest(
    var nama: String = "",
    var deskripsi: String = "",
    var merk: String = "",        // Pengganti manfaat
    var material: String = "",    // Pengganti efekSamping
    var pathGambar: String = "",
){
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nama" to nama,
            "deskripsi" to deskripsi,
            "merk" to merk,
            "material" to material,
            "pathGambar" to pathGambar
        )
    }

    fun toEntity(): Doll {
        return Doll(
            nama = nama,
            deskripsi = deskripsi,
            merk = merk,
            material = material,
            pathGambar = pathGambar,
        )
    }
}