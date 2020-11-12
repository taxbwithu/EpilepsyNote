package com.mmoson.epilepsynote.ui

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mmoson.epilepsynote.R
import com.mmoson9.epilepsynote.mvp.presenter.impl.SettingsPresenter
import com.mmoson9.epilepsynote.mvp.presenter.impl.SettingsPresenterImpl
import com.mmoson9.epilepsynote.mvp.view.SettingsView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_settings.*
import org.jetbrains.anko.toast


class SettingsFragment : Fragment(), SettingsView {

    internal lateinit var context: Context
    private var presenter: SettingsPresenter = SettingsPresenterImpl()
    lateinit var rootView: View
    lateinit var uID: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false)
        super.onCreate(savedInstanceState)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = view.context
        val mainActivity = activity as AppCompatActivity
        mainActivity.bottom_nav.visibility = View.GONE
        presenter.attachView(this)
        presenter.currentSettings(context)
        presenter.checkUserID(context)
        seizure_serie.setInputType(InputType.TYPE_CLASS_NUMBER)
        seizure_serie_number.setInputType(InputType.TYPE_CLASS_NUMBER)

        reset_data.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Usuń wszystkie dane")
            builder.setMessage("Utraconych danych nie będzie można odzyskać.")
            builder.setPositiveButton("TAK") { _, _ ->
                presenter.deleteUser(uID)
            }
            builder.setNeutralButton("Anuluj") { _, _ ->
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        save_set.setOnClickListener {
            var seizureSerie = seizure_serie.text.toString()
            var serieNumber = seizure_serie_number.text.toString()
            if (seizureSerie.isBlank() || seizureSerie == "0") {
                seizureSerie = "1"
                seizure_serie.setText(seizureSerie)
            }
            if (serieNumber.isBlank() || serieNumber == "0" || serieNumber == "1") {
                serieNumber = "2"
                seizure_serie_number.setText(serieNumber)
            }
            presenter.changeSettings(seizureSerie, serieNumber)
        }
    }

    override fun settingsChanged() {
        context.toast("Ustawienia zostały zmienione")
    }

    override fun displaySettings(seizureSerie: String, serieNumber: String) {
        seizure_serie.setText(seizureSerie)
        seizure_serie_number.setText(serieNumber)
    }

    override fun setUserID(userID: String) {
        if (userID.isEmpty()) reset_data.visibility = View.GONE
        else reset_data.visibility = View.VISIBLE
        this.uID = userID
    }

    override fun userDeleted() {
        this.uID = ""
        reset_data.visibility = View.GONE
    }
}