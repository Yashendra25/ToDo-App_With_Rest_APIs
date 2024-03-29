package com.yashendra.todoapp.Fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yashendra.todoapp.R
import com.yashendra.todoapp.Utills.Utills
import com.yashendra.todoapp.activities.LoginActivity
import com.yashendra.todoapp.adapters.FinishedTaskAdapter
import com.yashendra.todoapp.adapters.TodoListAdapter
import com.yashendra.todoapp.interfaces.AdapterItemClickListener
import com.yashendra.todoapp.model.TodoModel

class FinishedFragment : Fragment() , AdapterItemClickListener {
    lateinit var recyclerView: RecyclerView
    lateinit var empty_tv: TextView
    lateinit var progressBar: ProgressBar
    lateinit var arrayList: ArrayList<TodoModel>
    lateinit var finishedTaskAdapter: FinishedTaskAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_finished, container, false)
        recyclerView = view.findViewById(R.id.recyclerview)
        empty_tv = view.findViewById(R.id.empty_tv)
        progressBar = view.findViewById(R.id.progressBar)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.hasFixedSize()

        getTasks()
        return view
    }
    private fun getTasks() {
        arrayList = ArrayList()
        progressBar.visibility = View.VISIBLE
        Utills.getRequest(requireContext(), "https://fantastic-cyan-katydid.cyclic.app/api/todo/finished", {
            progressBar.visibility = View.GONE
            val jsonArray = it.getJSONArray("data")
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val id = jsonObject.getString("_id")
                val title = jsonObject.getString("title")
                val description = jsonObject.getString("description")
                val todoModel = TodoModel(id, title, description)
                arrayList.add(todoModel)
            }
            finishedTaskAdapter = FinishedTaskAdapter(activity, arrayList, this)
            recyclerView.adapter = finishedTaskAdapter
            if (arrayList.size == 0) {
                empty_tv.visibility = View.VISIBLE
            } else {
                empty_tv.visibility = View.GONE
                Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
//                recyclerView.adapter = TodoAdapter(arrayList)
            }
        }, {
            progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            if (it.contains("Token")) {
                Toast.makeText(requireContext(), "Please Login Again", Toast.LENGTH_SHORT).show()
                val sharedPreferences = context?.getSharedPreferences("TodoPrefs", Context.MODE_PRIVATE)
                if (sharedPreferences != null) {
                    sharedPreferences.edit().clear().apply()
                }
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }
        })

    }

    override fun onItemClicked(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onItemLongClicked(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onItemDoneClicked(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onItemDeleteClicked(position: Int) {
        showDeleteDialog(arrayList[position].id,position)
    }

    override fun onItemEditClicked(position: Int) {
        TODO("Not yet implemented")
    }


    private fun showDeleteDialog(id: String, position: Int) {
        val url:String= "https://fantastic-cyan-katydid.cyclic.app/api/todo/$id"
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Delete") { dialogInterface, i ->
                Utills.deleteTaskFromAPI(requireContext(),url,onSuccess = {
                    arrayList.removeAt(position)
                    finishedTaskAdapter.notifyDataSetChanged()
                },onError = {
                    Toast.makeText(requireContext(), "Error: $i", Toast.LENGTH_SHORT).show()
                })
            }
            .setNegativeButton("Cancel") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            .create()
        alertDialog.show()


    }
}