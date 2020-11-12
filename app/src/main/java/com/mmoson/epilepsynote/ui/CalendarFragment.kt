package com.mmoson.epilepsynote.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import com.mmoson.epilepsynote.R
import com.mmoson9.epilepsynote.adapter.SeizureListAdapter
import com.mmoson9.epilepsynote.mvp.interactor.impl.Seizure
import com.mmoson9.epilepsynote.mvp.presenter.impl.CalendarPresenter
import com.mmoson9.epilepsynote.mvp.presenter.impl.CalendarPresenterImpl
import com.mmoson9.epilepsynote.mvp.view.CalendarView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_calendar.*
import org.jetbrains.anko.toast
import java.util.*


class CalendarFragment : Fragment(), CalendarView,
    SeizureListAdapter.onItemButtonClick {

    internal lateinit var context: Context
    lateinit var rootView: View
    var adapter: SeizureListAdapter? = null
    val calendar = Calendar.getInstance()
    private var presenter: CalendarPresenter = CalendarPresenterImpl()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_calendar, container, false)
        super.onCreate(savedInstanceState)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = view.context
        presenter.attachView(this)
        presenter.checkUserID(context)
        val mainActivity = activity as AppCompatActivity
        mainActivity.bottom_nav.visibility = View.VISIBLE
        mainActivity.bottom_nav.menu.findItem(R.id.nav_cal).isChecked = true
        val date = Date()
        setMonth(date)
        compactcalendar_view.setFirstDayOfWeek(Calendar.MONDAY)
        compactcalendar_view.setListener(OnCalendarChanged())
    }

    override fun setAdapter(seizures: ArrayList<Seizure>) {
        Log.d("TAG", "Wybrano item" + seizures)
        recycler_view?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        adapter = SeizureListAdapter(seizures, this)
        recycler_view?.adapter = adapter
    }

    override fun noResults() {
        context.toast("Brak wyników")
    }

    override fun disconnected() {
        context.toast("Sprawdź połączenie z internetem")
    }

    override fun dateSetter(dateEvent: Event) {
        compactcalendar_view.addEvent(dateEvent)
    }

    override fun startEditFragment(seizure: Seizure) {
        val editAction = CalendarFragmentDirections.destinationSeizureedit(seizure.id)
        Navigation.findNavController(rootView).navigate(editAction)
    }

    override fun setEvent(eventTime: Long) {
        val event = Event(Color.WHITE, eventTime)
        compactcalendar_view?.addEvent(event)
    }

    fun setMonth(pickedDate: Date) {
        calendar.time = pickedDate
        val thisMonth: String =
            (calendar.get(Calendar.MONTH) + 1).toString() + "." + calendar.get(Calendar.YEAR).toString()
        month_display.setText(thisMonth)
    }

    inner class OnCalendarChanged : CompactCalendarView.CompactCalendarViewListener {
        override fun onDayClick(dateClicked: Date?) {
            presenter.filterDate(dateClicked)
        }

        override fun onMonthScroll(firstDayOfNewMonth: Date) {
            setMonth(firstDayOfNewMonth)
        }
    }

}