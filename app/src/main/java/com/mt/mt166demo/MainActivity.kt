package com.mt.mt166demo

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy

class MainActivity : AppCompatActivity() {
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager2? = null
    private var adapter: ViewPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // updated layout

        instance = this
        Application.cr.MtSetLanguage(bIsTrans)

        tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        viewPager = findViewById<ViewPager2>(R.id.viewPager)

        adapter = ViewPagerAdapter(this)
        viewPager!!.setAdapter(adapter)

        // Attach tabs to ViewPager2
        TabLayoutMediator(
            tabLayout!!, viewPager!!,
            TabConfigurationStrategy { tab: TabLayout.Tab?, position: Int ->
                when (position) {
                    0 -> tab!!.text = getString(R.string.base)
                    1 -> tab!!.text = getString(R.string.TypeAB)
                    2 -> tab!!.text = getString(R.string.RFM1)
                    3 -> tab!!.text = getString(R.string.UL)
                }
            }).attach()

        setStatus(false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.connect) {
            val dlg = ConnDialog(this)
            dlg.setCloseListener { isOk: Boolean ->
                if (isOk) {
                    setStatus(true)
                }
            }
            dlg.show()
        } else if (id == R.id.disconnect) {
            try {
                Application.cr.MtDisConnect()
                setStatus(false)
            } catch (e: Exception) {
                Application.showError(e.message, this)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun setStatus(isConnected: Boolean) {
        var title = getString(R.string.app_name)
        if (isConnected) {
            val status = getString(R.string.connected)
            title = title + " [" + status + "]"
        }
        setTitle(title)
    }

    companion object {
        var instance: MainActivity? = null
        private const val bIsTrans = true
    }
}
