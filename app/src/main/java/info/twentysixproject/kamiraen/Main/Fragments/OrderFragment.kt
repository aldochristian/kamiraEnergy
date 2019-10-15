package info.twentysixproject.kamiraen.Main.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.*
import androidx.fragment.app.FragmentStatePagerAdapter
import info.twentysixproject.kamiraen.R
import kotlinx.android.synthetic.main.fragment_order.*

class OrderFragment : Fragment() {

    val TAG:String = "OrderFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager(viewpager)
        val tabs: TabLayout = view.findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewpager)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(toolbar)
            (activity as AppCompatActivity).supportActionBar?.title = "Order"
        }
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(requireActivity().supportFragmentManager)
        adapter.addFragment(OngoingOrderFragment(),"On Going")
        adapter.addFragment(CompletedOrderFragment(), "Completed")
        viewPager.adapter = adapter
    }

    @Suppress("DEPRECATION")
    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }

        override fun getItemPosition(`object`: Any): Int {
            //return super.getItemPosition(`object`)
            return POSITION_NONE
        }
    }

}
