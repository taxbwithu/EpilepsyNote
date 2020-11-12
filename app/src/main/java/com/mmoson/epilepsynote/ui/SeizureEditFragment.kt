package com.mmoson.epilepsynote.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.mmoson.epilepsynote.R
import com.mmoson9.epilepsynote.mvp.interactor.impl.Date
import com.mmoson9.epilepsynote.mvp.presenter.impl.SeizureEditPresenter
import com.mmoson9.epilepsynote.mvp.presenter.impl.SeizureEditPresenterImpl
import com.mmoson9.epilepsynote.mvp.view.SeizureEditView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add.date_picker
import kotlinx.android.synthetic.main.fragment_add.time_picker
import kotlinx.android.synthetic.main.fragment_edit.*
import org.jetbrains.anko.toast

class SeizureEditFragment : Fragment(), SeizureEditView {

    internal lateinit var context: Context
    private var presenter: SeizureEditPresenter = SeizureEditPresenterImpl()
    lateinit var rootView: View
    var userID = ""
    lateinit var pickedDate: Date
    lateinit var mHour: String
    lateinit var mMinute: String
    var epSize: String = "Mały"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_edit, container, false)
        super.onCreate(savedInstanceState)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = view.context
        val mainActivity = activity as AppCompatActivity
        mainActivity.bottom_nav.visibility = View.GONE
        presenter.attachView(this)
        arguments?.let {
            val safeArgs = SeizureEditFragmentArgs.fromBundle(it)
            presenter.getData(safeArgs.seizureID)
        }
        presenter.checkUserID(context)
        btn_edit.setOnClickListener {
            if (time_picker.text != "..."){
                btn_delete?.isClickable = false
                btn_edit?.isClickable = false
                val descEdit = description_edit?.getText().toString()
                Log.d("TAG", "description" + descEdit)
                presenter.updateData(userID, epSize, descEdit)
            }
            else {
                context.toast("Ładowanie zdarzenia")
            }
        }
        btn_delete.setOnClickListener {
            if (time_picker.text != "..."){
                btn_delete?.isClickable = false
                btn_edit?.isClickable = false
                presenter.deleteData(userID)}
            else {
                context.toast("Ładowanie zdarzenia")
            }
        }
    }


    override fun dateSetter(pickedDate: Date) {
        this.pickedDate = pickedDate
        date_picker?.text = pickedDate.printAll()
    }

    override fun timeSetter(hour: String, minutes: String) {
        mHour = hour
        mMinute = minutes
        val setTime = mHour + ":" + mMinute
        time_picker?.text = setTime
    }

    override fun pickerListeners(epSize: String, description: String?) {
        this.epSize = epSize
        if (epSize == radio_small_e.text) radio_small_e?.isChecked = true
        else if (epSize == radio_medium_e.text) radio_medium_e?.isChecked = true
        else if (epSize == radio_big_e.text) radio_big_e?.isChecked = true
        date_picker?.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                context,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    presenter.setDate(dayOfMonth, monthOfYear, year)
                }, pickedDate.year.toInt(), pickedDate.month.toInt() - 1, pickedDate.day.toInt()
            )
            datePickerDialog.show()
        }
        time_picker?.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                context,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    presenter.setTime(hourOfDay, minute)
                }, mHour.toInt(), mMinute.toInt(), true
            )
            timePickerDialog.show()
        }
        radio_group_e?.setOnCheckedChangeListener { group, checkedId ->
            val rb: RadioButton? = view?.findViewById(checkedId)
            this.epSize = rb?.text.toString()
        }
        description_edit?.setText(description)
    }

    override fun userIDSetter(userID: String) {
        this.userID = userID
    }

    override fun documentUpdated() {
        context.toast("Zaktualizowano Zdarzenie")
        btn_delete?.isClickable = true
        btn_edit?.isClickable = true
    }

    override fun documentDeleted() {
        context.toast("Atak usunięty")
        Navigation.findNavController(rootView).popBackStack()
    }
}
