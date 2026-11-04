package com.pdm.cartaovisita

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/**
 * Unica tela do aplicativo.
 *
 * A Activity so guarda a preferencia de tema e entrega os dados do perfil
 * para a interface; toda a apresentacao fica em CartaoScreen.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Na primeira abertura o aplicativo acompanha o tema do sistema.
            val escuroNoSistema = isSystemInDarkTheme()

            // rememberSaveable preserva a escolha do usuario ao girar a tela.
            var temaEscuro by rememberSaveable { mutableStateOf(escuroNoSistema) }

            CartaoVisitaTheme(escuro = temaEscuro) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CartaoScreen(
                        perfil = PERFIL_PADRAO,
                        temaEscuro = temaEscuro,
                        onAlternarTema = { temaEscuro = it }
                    )
                }
            }
        }
    }
}
