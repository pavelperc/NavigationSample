package com.example.navigationsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.example.navigationsample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val repeatedStackPositions = mutableSetOf<Int>()

    lateinit var binding: ActivityMainBinding

    companion object {
        private const val INITIAL_TAB = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = true

        if (savedInstanceState == null) { // handle screen rotation
            openTab(INITIAL_TAB)
            selectTabIcon(INITIAL_TAB)
        }

        savedInstanceState?.getIntegerArrayList("repeatedStackPositions")?.let {
            repeatedStackPositions += it
        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_my_books -> openTab(0)
                R.id.item_showcase -> openTab(1)
                R.id.item_profile -> openTab(2)
            }
            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntegerArrayList("repeatedStackPositions", ArrayList(repeatedStackPositions))
    }

    private fun openTab(position: Int) {
        val fragment = supportFragmentManager
            .findFragmentByTag(getTabTag(position)) as? TabFragment
            ?: createTabFragment(position)

        val tag = getTabTag(position)

        supportFragmentManager.getBackStack().withIndex().drop(1).forEach {
            if (it.value.name == tag) {
                repeatedStackPositions += it.index
            }
        }

        supportFragmentManager.commit {
            replace(R.id.container, fragment, getTabTag(position))
            addToBackStack(getTabTag(position))
        }
    }

    private fun createTabFragment(position: Int): TabFragment {
        return when (position) {
            0 -> TabFragment.newInstance(MyBooksFragment::class.java, MyBooksFragment.TAG)
            1 -> TabFragment.newInstance(ShowcaseFragment::class.java, ShowcaseFragment.TAG)
            2 -> TabFragment.newInstance(ProfileFragment::class.java, ProfileFragment.TAG)
            else -> error("unknown position $position")
        }
    }

    override fun onBackPressed() {
        val currentFragment = getCurrentTabFragment()
        if (currentFragment.childFragmentManager.backStackEntryCount > 1) {
            currentFragment.childFragmentManager.popBackStack()
            return
        }
        if (supportFragmentManager.backStackEntryCount > 1) {

            val previousEntry = supportFragmentManager.getBackStack().dropLast(1).withIndex()
                .last { it.index !in repeatedStackPositions }.value

            val previousPosition = getPositionFromTag(previousEntry.name.orEmpty())

            supportFragmentManager.popBackStack(previousEntry.id, 0)
            selectTabIcon(previousPosition)
            return
        }
        finish()
    }

    private fun selectTabIcon(position: Int) {
        binding.bottomNavigationView.menu.getItem(position).isChecked = true
    }

    private fun getCurrentTabFragment() =
        supportFragmentManager.findFragmentById(R.id.container) as TabFragment

    private fun getPositionFromTag(tag: String) = tag.split("_")[1].toInt()

    private fun getTabTag(position: Int) = "tab_$position"

    private fun FragmentManager.getBackStack() =
        List(backStackEntryCount) { getBackStackEntryAt(it) }
}