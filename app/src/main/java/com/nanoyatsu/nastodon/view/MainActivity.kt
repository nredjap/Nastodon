package com.nanoyatsu.nastodon.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.databinding.ActivityMainBinding
import com.nanoyatsu.nastodon.view.fragment.TimelineFragment
import com.nanoyatsu.nastodon.viewModel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    TimelineFragment.EventListener {

    lateinit var viewModel: MainViewModel
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this@MainActivity).get(MainViewModel::class.java) // todo bindingの中に入れる
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this@MainActivity, R.layout.activity_main)

        // 上部ToolBar
        setSupportActionBar(toolbar)

        // FloatingButton todo 関数化・処理分割
        floating_edit.setOnClickListener {
            val intent = Intent(this@MainActivity, TootEditActivity::class.java)
            startActivity(intent)
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        // 下部タブ
        setTabButton(supportFragmentManager)
        // 左部メニュー(Navigation Drawer)
        setNavigationDrawer(nav_view)

        // 初期化あるいは再構成
        restoreView(viewModel)
    }

    private fun restoreView(vm: MainViewModel) {
        navigation.findViewById<View>(vm.selectedTabId).callOnClick()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val timelineTabs =
        arrayOf(R.id.navigation_timeline, R.id.navigation_notice, R.id.navigation_global_timeline)
            .zip(TimelineFragment.GetMethod.values())

    private fun setTabButton(fm: FragmentManager) {
        fun fragmentTransition(selected: Pair<Int, TimelineFragment.GetMethod>, showing: TimelineFragment?) {
            viewModel.selectedTabId = selected.first
            fm.beginTransaction().also { trans ->
                fm.fragments.forEach { trans.hide(it) }

                if (showing == null)
                    trans.add(R.id.content_main, TimelineFragment.newInstance(selected.second), selected.second.name)
                else
                    trans.show(showing)
                trans.commit()
            }
        }

        val selectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            val selected = timelineTabs.find { it.first == item.itemId }
                ?: return@OnNavigationItemSelectedListener false
            val showing = fm.findFragmentByTag(selected.second.name) as? TimelineFragment

            if (showing != null && viewModel.selectedTabId == selected.first)
                showing.focusTop()
            else
                fragmentTransition(selected, showing)
            true
        }

        navigation.setOnNavigationItemSelectedListener(selectedListener)
    }

    private fun setNavigationDrawer(view: NavigationView) {
        view.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this@MainActivity, AuthActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_tools -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun progressStart() {
        progress_view.visibility = View.VISIBLE
    }

    override fun progressEnd() {
        progress_view.visibility = View.GONE
    }
}
