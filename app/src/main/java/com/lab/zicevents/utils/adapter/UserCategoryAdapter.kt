package com.lab.zicevents.utils.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import com.lab.zicevents.data.model.local.UserCategory

/**
 * Custom ArrrayAdapter for UserCategory
 */

class UserCategoryAdapter(context: Context, private val arrayListCategory: ArrayList<UserCategory>)
    : ArrayAdapter<UserCategory>(context, android.R.layout.activity_list_item, arrayListCategory) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val listItem = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item,parent,false)
        val currentCategory = arrayListCategory[position]

        val categoryName: CheckedTextView = listItem.findViewById(android.R.id.text1)
        categoryName.text = currentCategory.category

        return listItem
    }
}