package com.example.navigationsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var fragments: List<TabFragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        fragments = listOf(
            getOrCreateTab(0),
            getOrCreateTab(1),
            getOrCreateTab(2)
        )

        supportFragmentManager.commit {
            replace(R.id.container, fragments[0], getTabTag(0))
        }

        bottomNavigationView.setOnItemReselectedListener { item ->
            when (item.itemId) {
                R.id.item_my_books -> openTab(0)
                R.id.item_showcase -> openTab(1)
                R.id.item_profile -> openTab(2)
            }
        }
    }

    private fun openTab(position: Int) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragments[position])
            .addToBackStack(null)
            .commit()
    }

    private fun getOrCreateTab(position: Int): TabFragment {
        supportFragmentManager.findFragmentByTag(getTabTag(position)) as? TabFragment

        val fragment = supportFragmentManager.findFragmentByTag(TabFragment.TAG) as? TabFragment
        if (fragment != null) {
            return fragment
        }

        val newFragment = TabFragment()
        supportFragmentManager.commit {
            add(newFragment, getTabTag(position))
            attach(newFragment)
        }
        when (position) {
            0 -> newFragment.setStartNavigation<MyBooksFragment>(MyBooksFragment.TAG)
            1 -> newFragment.setStartNavigation<ShowcaseFragment>(ShowcaseFragment.TAG)
            2 -> newFragment.setStartNavigation<ProfileFragment>(ProfileFragment.TAG)
        }

        return newFragment
    }

    private fun getTabTag(position: Int) = "tab_$position"
}