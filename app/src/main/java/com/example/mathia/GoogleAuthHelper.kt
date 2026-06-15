package com.example.mathia

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

sealed class GoogleAuthResult {
    data class Success(val account: GoogleSignInAccount) : GoogleAuthResult()
    data class Error(val message: String, val code: Int) : GoogleAuthResult()
}

class GoogleAuthHelper(private val context: Context) {

    private val googleSignInClient: GoogleSignInClient

    init {
        // Safe check for R.string.default_web_client_id
        val webClientId = try {
            val id = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
            if (id != 0) context.getString(id) else "your_default_web_client_id_placeholder"
        } catch (e: Exception) {
            "your_default_web_client_id_placeholder"
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .requestProfile()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun getSignInIntent(): Intent {
        googleSignInClient.signOut()
        return googleSignInClient.signInIntent
    }

    fun parseResult(data: Intent?): GoogleAuthResult {
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
        return try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                GoogleAuthResult.Success(account)
            } else {
                GoogleAuthResult.Error("No se pudo obtener la cuenta de Google.", -1)
            }
        } catch (e: ApiException) {
            e.printStackTrace()
            val userFriendlyMsg = when (e.statusCode) {
                10 -> "Error de Configuración (Desarrollador - 10):\nEl Web Client ID es inválido o no has registrado las firmas SHA-1/SHA-256 en la consola de Firebase."
                7 -> "Error de Red:\nNo hay conexión a internet para autenticar con Google."
                12501 -> "Operación cancelada por el usuario."
                12500 -> "Error en el perfil de Google Sign-In (12500)."
                12502 -> "Inicio de sesión de Google ya en progreso."
                else -> "Error de Google Sign-In (${e.statusCode}): ${e.localizedMessage ?: e.message}"
            }
            GoogleAuthResult.Error(userFriendlyMsg, e.statusCode)
        } catch (e: Exception) {
            GoogleAuthResult.Error("Error inesperado en Google Sign-In: ${e.message}", -2)
        }
    }
}
