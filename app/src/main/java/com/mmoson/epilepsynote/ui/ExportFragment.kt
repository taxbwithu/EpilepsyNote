package com.mmoson.epilepsynote.ui

import android.Manifest
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
import com.itextpdf.text.pdf.draw.VerticalPositionMark
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.mmoson.epilepsynote.R
import com.mmoson9.epilepsynote.mvp.interactor.impl.Date
import com.mmoson9.epilepsynote.mvp.interactor.impl.Seizure
import com.mmoson9.epilepsynote.mvp.presenter.impl.ExportPresenter
import com.mmoson9.epilepsynote.mvp.presenter.impl.ExportPresenterImpl
import com.mmoson9.epilepsynote.mvp.view.ExportView
import com.mmoson9.epilepsynote.utils.Common
import com.mmoson9.epilepsynote.utils.ExportToPDF
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_export.*
import kotlinx.android.synthetic.main.fragment_export.date_end
import kotlinx.android.synthetic.main.fragment_export.date_start
import kotlinx.android.synthetic.main.fragment_list.*
import org.jetbrains.anko.toast
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class ExportFragment : Fragment(), ExportView, ExportToPDF.onPdfRequests {

    internal lateinit var context: Context
    lateinit var rootView: View
    private var presenter: ExportPresenter = ExportPresenterImpl()
    var exportPDF = ExportToPDF()
    lateinit var path : String
    lateinit var startDate: Date
    lateinit var endDate: Date
    var seizStats = IntArray(5)
    var includeStatistics : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_export, container, false)
        super.onCreate(savedInstanceState)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = view.context
        presenter.attachView(this)
        path = Common.getAppPath(context)
        exportPDF.pathSet(path)
        presenter.setDate(50, 0, 0, true)
        presenter.checkUserID(context)
        val mainActivity = activity as AppCompatActivity
        mainActivity.bottom_nav.visibility = View.GONE
        includeStatistics = checkbox_stats.isChecked
        checkbox_stats.setOnClickListener {
            includeStatistics = !includeStatistics
        }

        date_start.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                context,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    presenter.setDate(dayOfMonth, monthOfYear, year, true)
                }, startDate.year.toInt(), startDate.month.toInt() - 1, startDate.day.toInt()
            )
            datePickerDialog.show()
        }
        date_end.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                context,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    presenter.setDate(dayOfMonth, monthOfYear, year, false)
                }, endDate.year.toInt(), endDate.month.toInt() - 1, endDate.day.toInt()
            )
            datePickerDialog.show()
        }

        show_all_exp_btn.setOnClickListener {
            date_start.text = "---"
            date_end.text = "---"
            presenter.showAllRecords()
        }

        open_pdf.setOnClickListener {
            if(exportPDF.pdfExists()) exportPDF.openPDF(context, this)
            else context.toast("Plik nie istnieje.")
        }

        Dexter.withActivity(mainActivity)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object:PermissionListener{
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    create_pdf.setOnClickListener {
                        presenter.exportData(startDate, endDate)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    context.toast("Brak dostêpu do plików.")
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    context.toast("Brak dostêpu do plików.")
                }

            })
            .check()
    }

    override fun createPDFFile(seizures: ArrayList<Seizure>) {
        exportPDF.createPDFFile(seizures, context, startDate, endDate, includeStatistics, seizStats)
    }

    override fun dateSetter(startDate: Date, endDate: Date, init: Boolean) {
        this.startDate = startDate
        this.endDate = endDate
        if (init) {
            date_start.text = startDate.printAll()
            date_end.text = "---"
        } else {
            date_start.text = startDate.printAll()
            date_end.text = endDate.printAll()
        }
    }

    override fun statsSetter(
        allSeizures: Int,
        smallSeizures: Int,
        mediumSeizures: Int,
        bigSeizures: Int,
        allSeries: Int
    ) {
        this.seizStats[0] = allSeizures
        this.seizStats[1] = smallSeizures
        this.seizStats[2] = mediumSeizures
        this.seizStats[3] = bigSeizures
        this.seizStats[4] = allSeries
    }

    override fun noUserID() {
        context.toast("Baza danych jest pusta.")
    }

    override fun onPdfNotFound() {
        context.toast("Nie znaleziono pliku")
    }
}