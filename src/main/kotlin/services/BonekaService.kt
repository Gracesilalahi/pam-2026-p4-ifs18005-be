package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.data.DollRequest
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IDollRepository
import java.io.File
import java.util.*

class DollService(private val dollRepository: IDollRepository) {

    // Mengambil semua data boneka
    suspend fun getAllDolls(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""

        val dolls = dollRepository.getDolls(search)

        val response = DataResponse(
            "success",
            "Berhasil mengambil daftar koleksi boneka",
            mapOf(Pair("dolls", dolls))
        )
        call.respond(response)
    }

    // Mengambil data boneka berdasarkan id
    suspend fun getDollById(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID boneka tidak boleh kosong!")

        val doll = dollRepository.getDollById(id) ?: throw AppException(404, "Data boneka tidak ditemukan!")

        val response = DataResponse(
            "success",
            "Berhasil mengambil data boneka",
            mapOf(Pair("doll", doll))
        )
        call.respond(response)
    }

    // Ambil data request multipart
    private suspend fun getDollRequest(call: ApplicationCall): DollRequest {
        val dollReq = DollRequest()

        val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)
        multipartData.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "nama" -> dollReq.nama = part.value.trim()
                        "deskripsi" -> dollReq.deskripsi = part.value
                        "merk" -> dollReq.merk = part.value
                        "material" -> dollReq.material = part.value
                    }
                }

                is PartData.FileItem -> {
                    val ext = part.originalFileName
                        ?.substringAfterLast('.', "")
                        ?.let { if (it.isNotEmpty()) ".$it" else "" }
                        ?: ""

                    val fileName = UUID.randomUUID().toString() + ext
                    val filePath = "uploads/dolls/$fileName"

                    val file = File(filePath)
                    file.parentFile.mkdirs()

                    part.provider().copyAndClose(file.writeChannel())
                    dollReq.pathGambar = filePath
                }
                else -> {}
            }
            part.dispose()
        }
        return dollReq
    }

    // Validasi data input
    private fun validateDollRequest(dollReq: DollRequest){
        val validatorHelper = ValidatorHelper(dollReq.toMap())
        validatorHelper.required("nama", "Nama boneka wajib diisi")
        validatorHelper.required("deskripsi", "Deskripsi wajib diisi")
        validatorHelper.required("merk", "Merk boneka wajib diisi")
        validatorHelper.required("material", "Material boneka wajib diisi")
        validatorHelper.required("pathGambar", "Foto boneka wajib diunggah")
        validatorHelper.validate()

        val file = File(dollReq.pathGambar)
        if (!file.exists()) {
            throw AppException(400, "Gambar boneka gagal diproses!")
        }
    }

    // Menambahkan data boneka baru
    suspend fun createDoll(call: ApplicationCall) {
        val dollReq = getDollRequest(call)
        validateDollRequest(dollReq)

        // Cek duplikasi nama
        val existDoll = dollRepository.getDollByName(dollReq.nama)
        if(existDoll != null){
            val tmpFile = File(dollReq.pathGambar)
            if(tmpFile.exists()) tmpFile.delete()
            throw AppException(409, "Boneka dengan nama ini sudah ada di koleksi!")
        }

        val dollId = dollRepository.addDoll(dollReq.toEntity())

        val response = DataResponse(
            "success",
            "Berhasil menambahkan boneka ke koleksi",
            mapOf(Pair("dollId", dollId))
        )
        call.respond(response)
    }

    // Mengubah data boneka
    suspend fun updateDoll(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID boneka tidak boleh kosong!")

        val oldDoll = dollRepository.getDollById(id) ?: throw AppException(404, "Data boneka tidak tersedia!")

        val dollReq = getDollRequest(call)

        // Jika tidak upload gambar baru, gunakan gambar lama
        if(dollReq.pathGambar.isEmpty()){
            dollReq.pathGambar = oldDoll.pathGambar
        }

        validateDollRequest(dollReq)

        // Validasi nama jika diubah
        if(dollReq.nama != oldDoll.nama){
            val existDoll = dollRepository.getDollByName(dollReq.nama)
            if(existDoll != null){
                val tmpFile = File(dollReq.pathGambar)
                if(tmpFile.exists()) tmpFile.delete()
                throw AppException(409, "Nama boneka tersebut sudah digunakan!")
            }
        }

        // Hapus file lama jika ada upload baru
        if(dollReq.pathGambar != oldDoll.pathGambar){
            val oldFile = File(oldDoll.pathGambar)
            if(oldFile.exists()) oldFile.delete()
        }

        val isUpdated = dollRepository.updateDoll(id, dollReq.toEntity())
        if (!isUpdated) throw AppException(400, "Gagal memperbarui data boneka!")

        val response = DataResponse("success", "Berhasil memperbarui data boneka", null)
        call.respond(response)
    }

    // Menghapus data boneka
    suspend fun deleteDoll(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID boneka tidak boleh kosong!")

        val doll = dollRepository.getDollById(id) ?: throw AppException(404, "Data boneka tidak ditemukan!")

        val imageFile = File(doll.pathGambar)

        val isDeleted = dollRepository.removeDoll(id)
        if (!isDeleted) throw AppException(400, "Gagal menghapus data dari database!")

        if (imageFile.exists()) imageFile.delete()

        val response = DataResponse("success", "Berhasil menghapus boneka dari koleksi", null)
        call.respond(response)
    }

    // Mengambil file gambar boneka
    suspend fun getDollImage(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: return call.respond(HttpStatusCode.BadRequest)

        val doll = dollRepository.getDollById(id)
            ?: return call.respond(HttpStatusCode.NotFound)

        val file = File(doll.pathGambar)

        if (!file.exists()) return call.respond(HttpStatusCode.NotFound)

        call.respondFile(file)
    }
}