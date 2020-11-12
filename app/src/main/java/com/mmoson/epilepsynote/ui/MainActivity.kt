package com.mmoson.epilepsynote.ui

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import com.google.android.material.navigation.NavigationView
import com.mmoson.epilepsynote.R
import com.mmoson9.epilepsynote.mvp.interactor.SharedPrefsInteractor
import com.mmoson9.epilepsynote.mvp.interactor.impl.FirestoreInteractor
import com.mmoson9.epilepsynote.mvp.interactor.impl.FirestoreInteractorImpl
import com.mmoson9.epilepsynote.mvp.interactor.impl.SharedPrefsInteractorImpl
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), AppBarConfiguration.OnNavigateUpListener {

    lateinit var navController: NavController
    lateinit var thisView: View
    lateinit var context: Context
    lateinit var toolbar: Toolbar
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        thisView = findViewById(R.id.nav_host_frag)
        context = applicationContext
        toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val permission1 = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        ActivityCompat.requestPermissions(this, permission1, 1)

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_frag) as NavHostFragment? ?: return
        navController = host.navController

        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupNavigation(navController)
        setupActionBar(navController, appBarConfiguration)
        bottom_nav.setOnNavigationItemSelectedListener { item ->
            onNavDestinationSelected(item, Navigation.findNavController(this, R.id.nav_host_frag))
        }
        toolbar.setNavigationOnClickListener {
            findNavController(R.id.nav_host_frag).navigateUp(appBarConfiguration)
        }
    }

    private fun setupActionBar(
        navController: NavController,
        appBarConfig: AppBarConfiguration
    ) {
        setupActionBarWithNavController(navController, appBarConfig)
    }

    private fun setupNavigation(navController: NavController) {
        val sideNavView = findViewById<NavigationView>(R.id.nav_view)
        sideNavView?.setupWithNavController(navController)
        val drawerLayout: DrawerLayout? = findViewById(R.id.drawer_layout)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_main,
                R.id.nav_list,
                R.id.nav_cal,
                R.id.nav_export,
                R.id.nav_settings
            ),
            drawerLayout
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val retValue = super.onCreateOptionsMenu(menu)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        if (navigationView == null) {
            menuInflater.inflate(R.menu.activity_main_drawer, menu)
            return true
        }
        return retValue
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_frag).navigateUp()
    }


}
