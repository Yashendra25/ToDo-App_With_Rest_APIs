package com.yashendra.todoapp.Fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yashendra.todoapp.R
import com.yashendra.todoapp.Utills.Utills
import com.yashendra.todoapp.activities.LoginActivity
import com.yashendra.todoapp.adapters.TodoListAdapter
import com.yashendra.todoapp.interfaces.AdapterItemClickListener
import com.yashendra.todoapp.model.TodoModel


class HomeFragment : Fragment(), AdapterItemClickListener {
    lateinit var floatingActionButton: FloatingActionButton
    lateinit var recyclerView: RecyclerView
    lateinit var empty_tv: TextView
    lateinit var progressBar: ProgressBar
    lateinit var arrayList: ArrayList<TodoModel>
    lateinit var todoListAdapter: TodoListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        floatingActionButton = view.findViewById(R.id.addtask_btn)
        floatingActionButton.setOnClickListener {
            showAlertDialog()
        }

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
        Utills.getRequest(requireContext(), "https://fantastic-cyan-katydid.cyclic.app/api/todo/", {
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
            todoListAdapter = TodoListAdapter(activity, arrayList, this)
            recyclerView.adapter = todoListAdapter
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

    private fun showAlertDialog() {
        val alertLayout = layoutInflater.inflate(R.layout.custom_dialog_layout, null)
        val title = alertLayout.findViewById<EditText>(R.id.title_edit_text)
        val description = alertLayout.findViewById<EditText>(R.id.description_edit_text)
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(alertLayout)
            .setTitle("Add Task")
            .setPositiveButton("Add") { dialogInterface, i ->

            }
            .setNegativeButton("Cancel") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            .create()


        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(resources.getColor(R.color.teal_200))
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(resources.getColor(R.color.black))

            //set on positive button click listener
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val titleText = title.text.toString()
                val descriptionText = description.text.toString()
                //add task to our api using function
                if (!TextUtils.isEmpty(titleText) && !TextUtils.isEmpty(descriptionText)) {
                    addTaskToAPI(titleText, descriptionText)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please fill all the fields",
                        Toast.LENGTH_SHORT
                    ).show()
                }


                alertDialog.dismiss()
            }

        }
        alertDialog.show()

    }

    private fun addTaskToAPI(titleText: String, descriptionText: String) {
        val url: String = "https://fantastic-cyan-katydid.cyclic.app/api/todo/"
        val body = HashMap<String, String>()
        body["title"] = titleText
        body["description"] = descriptionText

        Utills.postRequest(
            requireContext(),
            url,
            body,
            onSuccess = {
                // Handle successful response, if needed
                Toast.makeText(requireContext(), "Task Added $titleText", Toast.LENGTH_SHORT).show()
                getTasks()
            },
            onError = { errorMessage ->
                // Handle error
                Toast.makeText(requireContext(), "Error: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onItemClicked(position: Int) {
        //showing snack
        Utills.showSnackBar(requireView(), "$position Clicked")

    }

    override fun onItemLongClicked(position: Int) {
        Utills.showSnackBar(requireView(), "$position Clicked")
    }

    override fun onItemDoneClicked(position: Int) {
        showFinishedTaskDialog(arrayList[position].id, position)
    }



    override fun onItemDeleteClicked(position: Int) {
        showDeleteDialog(arrayList[position].id,position)
    }



    override fun onItemEditClicked(position: Int) {
        Utills.showSnackBar(requireView(), arrayList[position].title)
        showUpdateDialog(
            arrayList[position].id,
            arrayList[position].title,
            arrayList[position].description
        )
    }

    private fun showUpdateDialog(id: String, title: String, description: String) {
        val alertLayout = layoutInflater.inflate(R.layout.custom_dialog_layout, null)
        val titleEditText = alertLayout.findViewById<EditText>(R.id.title_edit_text)
        val descriptionEditText = alertLayout.findViewById<EditText>(R.id.description_edit_text)
        titleEditText.setText(title)
        descriptionEditText.setText(description)
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(alertLayout)
            .setTitle("Update Task")
            .setPositiveButton("Update") { dialogInterface, i ->

            }
            .setNegativeButton("Cancel") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            .create()

        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(resources.getColor(R.color.teal_200))
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(resources.getColor(R.color.black))

            //set on positive button click listener
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val titleText = titleEditText.text.toString()
                val descriptionText = descriptionEditText.text.toString()
                //add task to our api using function
                if (!TextUtils.isEmpty(titleText) && !TextUtils.isEmpty(descriptionText)) {
                   Utills.updateTaskToAPI(requireContext(),id,titleText,descriptionText,onSuccess = {
                       getTasks()
                   },onError = {
                       Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
                   })
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please fill all the fields",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                alertDialog.dismiss()
            }


        }
        alertDialog.show()
    }

    private fun showDeleteDialog(id: String, position: Int) {
            val url:String= "https://fantastic-cyan-katydid.cyclic.app/api/todo/$id"
            val alertDialog = AlertDialog.Builder(requireContext())
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Delete") { dialogInterface, i ->
                    Utills.deleteTaskFromAPI(requireContext(),url,onSuccess = {
                        arrayList.removeAt(position)
                        todoListAdapter.notifyDataSetChanged()
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

    private fun showFinishedTaskDialog(id: String, position: Int) {
        //create an alert dialog
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Task Finished")
            .setMessage("Are you sure you want to finish this task?")
            .setPositiveButton("Finish") { dialogInterface, i ->
                //call finish task api
                Utills.finishTask(requireContext(),id,onSuccess = {
                    arrayList.removeAt(position)
                    todoListAdapter.notifyDataSetChanged()
                    Utills.showSnackBar(requireView(), it)
                },onError = {
                    Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
                })
            }
            .setNegativeButton("Cancel") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            .create()
        alertDialog.show()


    }

}