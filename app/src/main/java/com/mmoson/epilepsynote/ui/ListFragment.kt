package com.mmoson.epilepsynote.ui

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mmoson.epilepsynote.R
import com.mmoson9.epilepsynote.adapter.SeizureListAdapter
import com.mmoson9.epilepsynote.mvp.interactor.impl.Date
import com.mmoson9.epilepsynote.mvp.interactor.impl.Seizure
import com.mmoson9.epilepsynote.mvp.presenter.impl.ListPresenter
import com.mmoson9.epilepsynote.mvp.presenter.impl.ListPresenterImpl
import com.mmoson9.epilepsynote.mvp.view.ListView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_list.*
import org.jetbrains.anko.toast

class ListFragment : Fragment(), ListView, SeizureListAdapter.onItemButtonClick {

    internal lateinit var context: Context
    var adapter: SeizureListAdapter? = null
    private var presenter: ListPresenter = ListPresenterImpl()
    lateinit var rootView: View
    lateinit var startDate: Date
    lateinit var endDate: Date


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_list, container, false)
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = view.context
        date_filter.visibility = View.GONE
        val mainActivity = activity as AppCompatActivity
        mainActivity.bottom_nav.visibility = View.VISIBLE
        mainActivity.bottom_nav.menu.findItem(R.id.nav_list).isChecked = true
        presenter.attachView(this)
        presenter.setDate(50, 0, 0, true)
        presenter.checkUserID(context)
        animation?.setAnimation("load.json")
        animation?.playAnimation()

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
        show_all_btn.setOnClickListener {
            date_start.text = "---"
            date_end.text = "---"
            presenter.showAllRecords()
        }
    }

    override fun setAdapter(seizures: ArrayList<Seizure>) {
        animation?.visibility = View.GONE
        animation?.cancelAnimation()
        recycler_view?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        adapter = SeizureListAdapter(seizures, this)
        recycler_view?.adapter = adapter
    }

    override fun noResults() {
        animation?.visibility = View.GONE
        animation?.cancelAnimation()
        context.toast("Brak wyników")
    }

    override fun disconnected() {
        context.toast("Sprawdź połączenie z internetem")
    }

    override fun startEditFragment(seizure: Seizure) {
        val editAction = ListFragmentDirections.destinationSeizureedit(seizure.id)
        Navigation.findNavController(rootView).navigate(editAction)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.filter_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter_icon -> {
                if (date_filter.visibility == View.VISIBLE) {
                    date_filter.visibility = View.GONE
                    item.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_filter))
                    presenter.checkUserID(context)
                } else {
                    date_filter.visibility = View.VISIBLE
                    item.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_check_white))
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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
}