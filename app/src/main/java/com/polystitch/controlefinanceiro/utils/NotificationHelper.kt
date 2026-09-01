package com.polystitch.controlefinanceiro.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object NotificationHelper {
    private const val CANAL_ID = "controle_financeiro_canal_geral"
    private const val CANAL_NOME = "Alertas Financeiros"
    private const val CANAL_DESC = "Notificações de despesas fixas e cartões"

    fun criarCanalNotificacao(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importancia = NotificationManager.IMPORTANCE_HIGH
            val canal = NotificationChannel(CANAL_ID, CANAL_NOME, importancia).apply {
                description = CANAL_DESC
            }
            val gerenciadorNotificacao = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            gerenciadorNotificacao.createNotificationChannel(canal)
        }
    }

    @SuppressLint("MissingPermission")
    fun dispararNotificacaoLocal(context: Context, titulo: String, mensagem: String, idNotificacao: Int = 1) {
        criarCanalNotificacao(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val construtor = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(mensagem)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    notify(idNotificacao, construtor.build())
                }
            } else {
                notify(idNotificacao, construtor.build())
            }
        }
    }

    fun agendarNotificacao(
        context: Context,
        tag: String,
        titulo: String,
        mensagem: String,
        delayEmMillis: Long,
        idNotificacao: Int
    ) {
        if (delayEmMillis <= 0) return

        val dadosTrabalho = Data.Builder()
            .putString("NOTIFICATION_TITLE", titulo)
            .putString("NOTIFICATION_MESSAGE", mensagem)
            .putInt("NOTIFICATION_ID", idNotificacao)
            .build()

        val requisicaoTrabalho = OneTimeWorkRequestBuilder<FinanceNotificationWorker>()
            .setInitialDelay(delayEmMillis, TimeUnit.MILLISECONDS)
            .addTag(tag)
            .setInputData(dadosTrabalho)
            .build()

        WorkManager.getInstance(context).enqueue(requisicaoTrabalho)
    }
}