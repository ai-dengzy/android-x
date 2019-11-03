package com.example.eletronicengineer.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.eletronicengineer.R
import com.example.eletronicengineer.fragment.my.MyOrderFragment
import com.example.eletronicengineer.utils.FragmentHelper

class MyOrderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_order)
        FragmentHelper.addFragment(this,MyOrderFragment(),R.id.frame_my_order,"")
    }
    fun switchFragment(fragment: Fragment, tag:String) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.replace(R.id.frame_my_order, fragment,tag)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
