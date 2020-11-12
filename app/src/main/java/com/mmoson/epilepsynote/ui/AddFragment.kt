package com.mmoson.epilepsynote.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.mmoson.epilepsynote.R
import com.mmoson9.epilepsynote.mvp.interactor.impl.Date
import com.mmoson9.epilepsynote.mvp.presenter.impl.AddPresenter
import com.mmoson9.epilepsynote.mvp.presenter.impl.AddPresenterImpl
import com.mmoson9.epilepsynote.mvp.view.AddView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add.*
import org.jetbrains.anko.toast

class AddFragment : Fragment(), AddView {

    internal lateinit var context: Context
    private var presenter: AddPresenter = AddPresenterImpl()
    lateinit var rootView: View
    var userID = ""
    lateinit var pickedDate: Date
    lateinit var mHour: String
    lateinit var mMinute: String
    var epSize: String = "Mały"
    var countSet: String = "1"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_add, container, false)
        super.onCreate(savedInstanceState)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = view.context
        val mainActivity = activity as AppCompatActivity
        mainActivity.bottom_nav.visibility = View.GONE
        presenter.attachView(this)
        presenter.setDate(50, 0, 0)
        presenter.setTime(30, 0)
        presenter.checkUserID(context)
        count_set.keyListener = null
        dec_set.visibility = View.INVISIBLE
        dec_set.isClickable = false
        radio_small.isChecked = true

        date_picker.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                context,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    presenter.setDate(dayOfMonth, monthOfYear, year)
                }, pickedDate.year.toInt(), pickedDate.month.toInt() - 1, pickedDate.day.toInt()
            )
            datePickerDialog.show()
        }
        time_picker.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                context,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    presenter.setTime(hourOfDay, minute)
                }, mHour.toInt(), mMinute.toInt(), true
            )
            timePickerDialog.show()
        }
        radio_group.setOnCheckedChangeListener { group, checkedId ->
            val rb: RadioButton = view.findViewById(checkedId)
            epSize = rb.text.toString()
        }
        dec_set.setOnClickListener {
            countSet = (countSet.toInt() - 1).toString()
            count_set.setText(countSet)
            if (countSet == "1") {
                dec_set.visibility = View.INVISIBLE
                dec_set.isClickable = false
            }
        }
        inc_set.setOnClickListener {
            countSet = (countSet.toInt() + 1).toString()
            count_set.setText(countSet)
            dec_set.visibility = View.VISIBLE
            dec_set.isClickable = true
        }
        add_att.setOnClickListener {
            val descr = description_add.getText().toString()
            presenter.addData(
                userID,
                epSize,
                descr,
                countSet
            )
        }
    }


    override fun dateSetter(pickedDate: Date) {
        this.pickedDate = pickedDate
        date_picker.text = pickedDate.printAll()
    }

    override fun timeSetter(hour: String, minutes: String) {
        mHour = hour
        mMinute = minutes
        val setTime = mHour + ":" + mMinute
        time_picker.text = setTime
    }

    override fun userIDSetter(userID: String) {
        this.userID = userID
    }

    override fun documentAdded() {
        context.toast("Dodano Zdarzenie")
        Navigation.findNavController(rootView).popBackStack()
    }

    override fun documentNotAdded(){
        context.toast("Połącz się z internetem i spróbuj ponownie")
        Navigation.findNavController(rootView).popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}