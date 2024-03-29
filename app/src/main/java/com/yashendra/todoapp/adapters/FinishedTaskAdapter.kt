package com.yashendra.todoapp.adapters

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.yashendra.todoapp.R
import com.yashendra.todoapp.Utills.Utills
import com.yashendra.todoapp.interfaces.AdapterItemClickListener
import com.yashendra.todoapp.model.TodoModel

class FinishedTaskAdapter(var activity: FragmentActivity?, var arrayList: ArrayList<TodoModel>, val itemClickListener: AdapterItemClickListener) : RecyclerView.Adapter<FinishedTaskAdapter.ViewHolder>(){
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val accordiantitle: CardView =itemView.findViewById(R.id.accordian_title)
        val title: TextView =itemView.findViewById(R.id.tasktitle)
        val description: TextView =itemView.findViewById(R.id.task_description)
        val accordianbody: RelativeLayout =itemView.findViewById(R.id.accordian_body)
        val arrow: ImageView =itemView.findViewById(R.id.arrow)
        val delete: ImageView =itemView.findViewById(R.id.deletbtn)






    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= View.inflate(parent.context, R.layout.finished_task_item,null)
        val myview=ViewHolder(view)
        myview.accordiantitle.setCardBackgroundColor(Color.parseColor(Utills.getRandomColor()))
        myview.arrow.setOnClickListener {
            if (myview.accordianbody.visibility== View.GONE){
                myview.accordianbody.visibility= View.VISIBLE
                myview.arrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            }else{
                myview.accordianbody.visibility= View.GONE
                myview.arrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            }
        }

        return myview
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text=arrayList[position].title
        holder.description.text=arrayList[position].description

        holder.delete.setOnClickListener {
            itemClickListener.onItemDeleteClicked(position)
        }

    }


    override fun getItemCount(): Int {
        return arrayList.size
    }

}