package com.polystitch.controlefinanceiro.utils

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class FinanceNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val titulo = inputData.getString("NOTIFICATION_TITLE") ?: "Aviso Financeiro"
        val mensagem = inputData.getString("NOTIFICATION_MESSAGE") ?: "Você possui um compromisso financeiro hoje."
        val idNotificacao = inputData.getInt("NOTIFICATION_ID", 1)

        NotificationHelper.dispararNotificacaoLocal(
            context = applicationContext,
            titulo = titulo,
            mensagem = mensagem,
            idNotificacao = idNotificacao
        )

        return Result.success()
    }
}