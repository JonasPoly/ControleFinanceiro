package com.polystitch.controlefinanceiro.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object PdfGenerator {

    data class TransacaoItem(
        val descricao: String,
        val valor: Double,
        val tipo: String,
        val data: Long
    )

    data class CategoriaGastoItem(
        val nomeCategoria: String,
        val valorTotal: Double
    )

    fun gerarRelatorioFinanceiro(
        context: Context,
        mesAnoNome: String,
        receitaTotal: Double,
        despesaTotal: Double,
        saldoTotal: Double,
        transacoes: List<TransacaoItem>,
        categoriasGastos: List<CategoriaGastoItem>
    ) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Formato A4 (595x842 px)
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 12f
        }
        val titlePaint = Paint().apply {
            color = android.graphics.Color.parseColor("#1E293B")
            textSize = 20f
            isFakeBoldText = true
        }
        val subtitlePaint = Paint().apply {
            color = android.graphics.Color.parseColor("#475569")
            textSize = 14f
            isFakeBoldText = true
        }
        val linePaint = Paint().apply {
            color = android.graphics.Color.parseColor("#CBD5E1")
            strokeWidth = 1f
        }

        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        var yPos = 50f

        // Cabeçalho
        canvas.drawText("Relatório Financeiro", 50f, yPos, titlePaint)
        yPos += 25f
        canvas.drawText("Referência: $mesAnoNome", 50f, yPos, subtitlePaint)
        yPos += 15f
        canvas.drawLine(50f, yPos, 545f, yPos, linePaint)
        yPos += 25f

        // Resumo do Mês
        canvas.drawText("Resumo do Período", 50f, yPos, subtitlePaint)
        yPos += 20f
        paint.color = android.graphics.Color.parseColor("#059669") // Verde
        canvas.drawText("Total de Entradas: ${currencyFormat.format(receitaTotal)}", 50f, yPos, paint)
        yPos += 16f
        paint.color = android.graphics.Color.parseColor("#DC2626") // Vermelho
        canvas.drawText("Total de Saídas: ${currencyFormat.format(despesaTotal)}", 50f, yPos, paint)
        yPos += 16f
        paint.color = android.graphics.Color.parseColor("#2563EB") // Azul
        canvas.drawText("Saldo Líquido: ${currencyFormat.format(saldoTotal)}", 50f, yPos, paint)
        yPos += 25f

        paint.color = android.graphics.Color.BLACK
        canvas.drawLine(50f, yPos, 545f, yPos, linePaint)
        yPos += 25f

        // Seção: Gastos por Categoria (Análise Gráfica)
        canvas.drawText("Análise de Gastos por Categoria", 50f, yPos, subtitlePaint)
        yPos += 20f

        if (categoriasGastos.isEmpty()) {
            paint.color = android.graphics.Color.parseColor("#64748B")
            canvas.drawText("Nenhuma categoria registrada neste período.", 50f, yPos, paint)
            yPos += 25f
        } else {
            paint.isFakeBoldText = true
            canvas.drawText("Categoria", 50f, yPos, paint)
            canvas.drawText("Total Gasto", 400f, yPos, paint)
            paint.isFakeBoldText = false
            yPos += 8f
            canvas.drawLine(50f, yPos, 545f, yPos, linePaint)
            yPos += 18f

            for (cat in categoriasGastos) {
                if (yPos > 780f) break
                canvas.drawText(cat.nomeCategoria.take(35), 50f, yPos, paint)
                canvas.drawText(currencyFormat.format(cat.valorTotal), 400f, yPos, paint)
                yPos += 18f
            }
            yPos += 10f
        }

        canvas.drawLine(50f, yPos, 545f, yPos, linePaint)
        yPos += 25f

        // Tabela de Transações
        canvas.drawText("Histórico de Transações", 50f, yPos, subtitlePaint)
        yPos += 20f

        // Cabeçalho da Tabela
        paint.isFakeBoldText = true
        canvas.drawText("Data", 50f, yPos, paint)
        canvas.drawText("Descrição", 130f, yPos, paint)
        canvas.drawText("Tipo", 380f, yPos, paint)
        canvas.drawText("Valor", 470f, yPos, paint)
        paint.isFakeBoldText = false
        yPos += 8f
        canvas.drawLine(50f, yPos, 545f, yPos, linePaint)
        yPos += 18f

        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.systemDefault())

        for (tx in transacoes) {
            if (yPos > 780f) {
                break // Limite simples para caber na página A4
            }
            val dataStr = dateFormatter.format(Instant.ofEpochMilli(tx.data))
            val valorStr = currencyFormat.format(tx.valor)
            val tipoStr = when (tx.tipo) {
                "RECEITA" -> "Entrada"
                "RETIRADA_RECEITA" -> "Estorno"
                else -> "Saída"
            }

            canvas.drawText(dataStr, 50f, yPos, paint)
            canvas.drawText(tx.descricao.take(35), 130f, yPos, paint)
            canvas.drawText(tipoStr, 380f, yPos, paint)
            canvas.drawText(valorStr, 470f, yPos, paint)

            yPos += 18f
        }

        pdfDocument.finishPage(page)

        // Salvando o arquivo na pasta de Downloads pública do Android
        val fileName = "Relatorio_Financeiro_${System.currentTimeMillis()}.pdf"
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)

        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(context, "PDF salvo em Downloads: $fileName", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Erro ao salvar PDF: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            pdfDocument.close()
        }
    }
}