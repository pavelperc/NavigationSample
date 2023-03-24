package com.example.navigationsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openTab(0)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_my_books -> openTab(0)
                R.id.item_showcase -> openTab(1)
                R.id.item_profile -> openTab(2)
            }
            false
        }
    }

    private fun openTab(position: Int) {
        val fragment = supportFragmentManager.findFragmentByTag(getTabTag(position)) as? TabFragment
        if (fragment != null) {
            supportFragmentManager.commit {
                replace(R.id.container, fragment, getTabTag(position))
                addToBackStack(getTabTag(position))
            }
            selectTabIcon(position)
            return
        }

        val newFragment = when (position) {
            0 -> TabFragment.newInstance(MyBooksFragment.TAG)
            1 -> TabFragment.newInstance(ShowcaseFragment.TAG)
            2 -> TabFragment.newInstance(ProfileFragment.TAG)
            else -> error("unknown position $position")
        }
        supportFragmentManager.commit {
            replace(R.id.container, newFragment, getTabTag(position))
            addToBackStack(getTabTag(position))
        }
        selectTabIcon(position)
    }

    private fun selectTabIcon(position: Int) {
        bottomNavigationView.menu.getItem(position).isChecked = true
    }

    override fun onBackPressed() {
        val currentFragment = getCurrentTabFragment()
        if (currentFragment.childFragmentManager.backStackEntryCount > 1) {
            currentFragment.childFragmentManager.popBackStack()
            return
        }
        if (supportFragmentManager.backStackEntryCount > 1) {
            val previousEntry = supportFragmentManager
                .getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 2)
            val previousPosition = getPositionFromTag(previousEntry.name.orEmpty())
            supportFragmentManager.popBackStack()
            selectTabIcon(previousPosition)
            return
        }
        finish()
    }

    private fun getCurrentTabFragment() =
        supportFragmentManager.findFragmentById(R.id.container) as TabFragment

    private fun getPositionFromTag(tag: String) = tag.split("_")[1].toInt()

    private fun getTabTag(position: Int) = "tab_$position"
}