package com.mmoson9.epilepsynote.utils

import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
import com.itextpdf.text.pdf.draw.VerticalPositionMark
import com.mmoson9.epilepsynote.mvp.interactor.impl.Date
import com.mmoson9.epilepsynote.mvp.interactor.impl.Seizure
import org.jetbrains.anko.toast
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class ExportToPDF {

    interface onPdfRequests{
        fun onPdfNotFound()
    }

    val fileName : String = "/PDFExport.pdf"
    lateinit var context : Context
    lateinit var path : String

    fun createPDFFile(seizures: ArrayList<Seizure>, context : Context, startDate : Date, endDate : Date, includeStatistics : Boolean, seizStats : IntArray){
        this.context = context
        if(File(path+fileName).exists()){
            File(path+fileName).delete()
        }
        try{
            val document = Document()
            PdfWriter.getInstance(document, FileOutputStream(path+fileName))

            document.open()

            document.pageSize = PageSize.A4
            document.addCreationDate()
            document.addAuthor("EpilepsyNote")

            val titleFontSize = 25.0f
            val headingFontSize = 20.0f
            val valueFontSize = 15.0f
            val font = BaseFont.createFont("assets/fonts/times.ttf", "ISO-8859-2", BaseFont.CACHED)

            val titleFont = Font(font, titleFontSize, Font.NORMAL, BaseColor.BLACK)
            val headingFont = Font(font, headingFontSize, Font.NORMAL, BaseColor.BLUE)
            val valueFont = Font(font, valueFontSize, Font.NORMAL, BaseColor.BLACK)
            val boldValueFont = Font(font, valueFontSize, Font.BOLD, BaseColor.BLACK)

            var title = "EpilepsyNote " + startDate.printPart() + "-" + endDate.printPart()
            Log.d("TAG", startDate.printPart() + "-" + endDate.printPart())
            addItem(document, title, Element.ALIGN_CENTER, titleFont)
            var sizeSet = arrayListOf("Ma³e", "Srednie", "Du¿e")
            if(includeStatistics){
                addItem(document, "Wszystkie ataki padaczkowe:", Element.ALIGN_LEFT, headingFont)
                addItem(document, seizStats[0].toString(), Element.ALIGN_LEFT, valueFont)
                addLeftMidRight(document, sizeSet[0], sizeSet[1], sizeSet[2], headingFont)
                addLeftMidRight(document, seizStats[1].toString(), seizStats[2].toString(), seizStats[3].toString(), valueFont)
                addItem(document, "Wszystkie serie ataków:", Element.ALIGN_LEFT, headingFont)
                addItem(document, seizStats[4].toString(), Element.ALIGN_LEFT, valueFont)
            }

            if (seizures.isNotEmpty()){
                for (seiz in seizures){
                    addLineSeparator(document)
                    addItem(document, seiz.date, Element.ALIGN_LEFT, boldValueFont)
                    addItem(document, seiz.size, Element.ALIGN_LEFT, valueFont)
                    addItem(document, seiz.description!!, Element.ALIGN_LEFT, valueFont)
                }
            }

            document.close()

            addToMediaScanner()

            context.toast("Sukces")

        }catch (e: Exception){
            Log.e("EpilepsyNote", ""+e.message)
        }
    }

    fun addItem(document : Document, text : String, align: Int, style : Font){
        val chunk = Chunk(text, style)
        val p = Paragraph(chunk)
        p.alignment = align
        document.add(p)
    }

    fun addLeftMidRight(document : Document, left : String, mid : String, right : String, style : Font){
        val chunkTextLeft = Chunk(left, style)
        val chunkTextMid = Chunk(mid, style)
        val chunkTextRight = Chunk(right, style)
        val p = Paragraph(chunkTextLeft)
        p.add(Chunk(VerticalPositionMark()))
        p.add(chunkTextMid)
        p.add(Chunk(VerticalPositionMark()))
        p.add(chunkTextRight)
        document.add(p)
    }

    fun addLineSeparator(document: Document){
        val lineSeparator = LineSeparator()
        lineSeparator.lineColor = BaseColor(0,0,0,69)
        addLineSpace(document)
        document.add(Chunk(lineSeparator))
        addLineSpace(document)
    }

    fun addLineSpace(document: Document){
        document.add(Paragraph(""))
    }

    fun openPDF(context : Context, onPdfRequests: onPdfRequests) {
        val file = File(path, fileName)
        if (pdfExists()){
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".fileprovider", file)
            intent.setDataAndType(uri, "application/pdf")
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
        }
        else{
            onPdfRequests.onPdfNotFound()
        }
    }

    fun pdfExists() : Boolean{
        val file = File(path , fileName)
        if (file.exists()) return true
        return false
    }

    fun pathSet(path : String){
        this.path = path
    }

    fun addToMediaScanner(){
        val toBeScanned = ArrayList<String>()
        toBeScanned.add(path+ "/")
        val array = arrayOfNulls<String>(toBeScanned.size)
        toBeScanned.toArray(array)
        Log.d("TAG", "test" + array[0])
        MediaScannerConnection.scanFile(context, array, null, object: MediaScannerConnection.OnScanCompletedListener{
            override fun onScanCompleted(path: String?, uri: Uri?) {
                Log.d("TAG", "File added to MediaScanner")
            }
        })
    }
}