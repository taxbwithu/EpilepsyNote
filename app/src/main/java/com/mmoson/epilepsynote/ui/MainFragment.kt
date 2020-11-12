package com.mmoson.epilepsynote.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.mmoson.epilepsynote.R
import com.mmoson9.epilepsynote.mvp.presenter.impl.MainPresenter
import com.mmoson9.epilepsynote.mvp.presenter.impl.MainPresenterImpl
import com.mmoson9.epilepsynote.mvp.view.MainView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(), MainView {

    internal lateinit var context: Context
    private var presenter: MainPresenter = MainPresenterImpl()
    lateinit var rootView: View
    var userID = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_main, container, false)
        super.onCreate(savedInstanceState)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = view.context
        val mainActivity = activity as AppCompatActivity
        mainActivity.bottom_nav.visibility = View.GONE
        presenter.attachView(this)
        presenter.checkUserID(context)
        closeKeyboard()

        btn_fab.setOnClickListener {
            Navigation.findNavController(rootView).navigate(R.id.nav_add)
        }
    }

    override fun userIDSetter(userID: String) {
        this.userID = userID
    }

    fun closeKeyboard() {
        val mgr = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mgr.hideSoftInputFromWindow(rootView.windowToken, 0)
    }

    override fun setLastSeizure(days : Int){
        last_seizure_numb?.text = days.toString() + " dni temu"
    }

    override fun setLastSerie(days: Int) {
        last_series_numb?.text = days.toString() + " dni temu"
    }

    override fun setWeeklySeizures(days: Int) {
        weekly_seizure_numb?.text = days.toString() + " ataków"
    }

    override fun setWeeklySeries(days: Int) {
        weekly_series_numb?.text = days.toString() + " serii"
    }
}